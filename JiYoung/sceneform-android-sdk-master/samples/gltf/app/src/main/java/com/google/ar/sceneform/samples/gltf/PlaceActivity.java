// 거리만큼 배치해주는 페이지

/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.gltf; // Sceneform -> OpenGL 없이 ARCORE 앱 빌드해주는 3D framework

import static java.util.concurrent.TimeUnit.SECONDS;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TranslationController;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PlaceActivity extends AppCompatActivity {

    private static final String TAG = PlaceActivity.class.getSimpleName(); // getSimplename => package 명 포함 X name만 추출
    private static final double MIN_OPENGL_VERSION = 3.0; // Open grapic Library version

    private ArFragment arFragment; // ARCORE 기본 구성 사용
    private Renderable renderable; // sceneform rendering basic class -> rendering 가능한 3D model 생성


    private static class AnimationInstance { // animation을 만들기 위해 사용되는 데이터들 -> Filament 기능
        Animator animator; // updating matrices in filament / updating bone matrices in filament -> gltf animation
        Long startTime;
        float duration;
        int index;

        AnimationInstance(Animator animator, int index, Long startTime) {
            this.animator = animator;
            this.startTime = startTime;
            this.duration = animator.getAnimationDuration(index); // Returns the duration of the specified glTF animation in seconds / index : animation index
            this.index = index;
        }

        public AnimationInstance(com.google.android.filament.gltfio.Animator animator, int index, long startTime) {
        }
    }

    private final Set<AnimationInstance> animators = new ArraySet<>(); // animationInstance 저장하기 위해 ArraySet 생성

    private final List<Color> colors =
            Arrays.asList(
                    new Color(0, 0, 0, 1),
                    new Color(1, 0, 0, 1),
                    new Color(0, 1, 0, 1),
                    new Color(0, 0, 1, 1),
                    new Color(1, 1, 0, 1),
                    new Color(0, 1, 1, 1),
                    new Color(1, 0, 1, 1),
                    new Color(1, 1, 1, 1));
    private int nextColor = 0; // basic color


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) { // requirements check
            return;
        }
        setContentView(R.layout.activity_place); // activity_place.xml


        Intent intent = getIntent();
        Float distance = (Float) intent.getSerializableExtra("distance");
//        int length = (int) intent.getSerializableExtra("length"); // get length, distance


        int length = 30; // 가구 길이 임의로 지정 for testing
        TextView text_distance = (TextView) findViewById(R.id.text_distance);

        text_distance.setText("측정 거리: " + distance + " cm");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (length <= distance){ // 배치모드
            builder.setMessage("배치가 가능합니다");

            builder.setPositiveButton("배치", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                //    Toast.makeText(getApplicationContext(), "배치된 상태 출력", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent pageIntent = new Intent(PlaceActivity.this, DistanceActivity.class); // 거리 재측정 -> DistanceActivity
                    startActivity(pageIntent);
                //    Toast.makeText(getApplicationContext(), "배치 안하고 출력", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment); // ux_fragment -> fragment manager 불러옴 -> ARFragment

            WeakReference<PlaceActivity> weakActivity = new WeakReference<>(this); // Weakreference -> MemoryLeak X

            ModelRenderable.builder() // Sceneform rendering engine
                    .setSource(
                            this,
                            Uri.parse(
                                    "https://raw.githubusercontent.com/justbeaver97/2021-1-CapstoneDesign/master/threejs_tutorial/models/746525_close.glb")) // our .glb model
                    .setIsFilamentGltf(true) // gltf load
                    .build()
                    .thenAccept(
                            modelRenderable -> {
                                PlaceActivity activity = weakActivity.get();
                                if (activity != null) {
                                    activity.renderable = modelRenderable;

                                }
                            })
                    .exceptionally( // exception
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(this, "인테리어 파일을 불러올 수 없습니다.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            });

            arFragment.setOnTapArPlaneListener( // Plane의 white dot tap하면 function 실행 -> hitresult(x,y), plane, motionEvent -> Anchor 생성 가능
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        if (renderable == null) {
                            return;
                        }

                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor(); // plane tap시 hitresult 에서 anchor 생성
                        AnchorNode anchorNode = new AnchorNode(anchor); // 공간에 anchor을 기반으로 자동으로 배치되는 Node(Node : 하나의 오브젝트가 차지하는 영역)
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        // (getScene : 장면 반환 / getArSceneView : 장면 랜더링(arsceneview) 반환) -> parentNode로 set

                        // Create the transformable model and add it to the anchor.
                        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem()); // TransformableNode -> 선택, 변환, 회전, 크기 조정 가능한 Node
                        model.setRenderable(renderable); // set rendering model

                        float wantScale = 0.00007f * distance; // distance만큼 Scale값 조정
                        model.getScaleController().setMaxScale(wantScale);
                        model.getScaleController().setMinScale(wantScale/100 *99);
                        model.getRotationController().setEnabled(false);
                        model.getTranslationController().setEnabled(false); // 회전, 조작 false
                        model.setParent(anchorNode);
                        model.select();


                        // Filament -> android, iOS 등 WebGL을 위한 실시간 Rendering engine
                        FilamentAsset filamentAsset = model.getRenderableInstance().getFilamentAsset(); // filamentAsset = filament에서 사용할 3D 모델(.glb file) 정의
                        if (filamentAsset.getAnimator().getAnimationCount() > 0) { // if animation exists
                            animators.add(new PlaceActivity.AnimationInstance(filamentAsset.getAnimator(), 0, System.nanoTime())); // Array set animators -> add Instance
                        }

                        Color color = colors.get(nextColor); // basic color setting
                        nextColor++; // color index
                        for (int i = 0; i < renderable.getSubmeshCount(); ++i) {
                            Material material = renderable.getMaterial(i);
                            material.setFloat4("baseColorFactor", color);
                        }

                    });

            arFragment
                    .getArSceneView()
                    .getScene()
                    .addOnUpdateListener( // Scene이 update되기 직전 frame 당 한번 호출될 콜백함수
                            frameTime -> {
                                Long time = System.nanoTime();
                                for (PlaceActivity.AnimationInstance animator : animators) { //for문 -> animators
                                    animator.animator.applyAnimation( // 주어진 animator에 rotation, translation and scale to entities
                                            animator.index,
                                            (float) ((time - animator.startTime) / (double) SECONDS.toNanos(1))
                                                    % animator.duration);
                                    animator.animator.updateBoneMatrices(); //Computes root-to-node transforms for all bone nodes, then passes
                                }
                            });



        }else{ // 배치 불가
            builder.setMessage("배치가 불가능합니다. 다른 장소를 선택하세요. (가구 길이: " + length + " cm)");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent pageIntent = new Intent(PlaceActivity.this, DistanceActivity.class);
                    // error -> 앱 중단 문제 , 재 측정 불가
                    startActivity(pageIntent);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }




    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) { // requirements check
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
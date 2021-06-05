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
package com.google.ar.sceneform.samples.gltf;

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

    private static final String TAG = PlaceActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Renderable renderable;


    private static class AnimationInstance {
        Animator animator;
        Long startTime;
        float duration;
        int index;

        AnimationInstance(Animator animator, int index, Long startTime) {
            this.animator = animator;
            this.startTime = startTime;
            this.duration = animator.getAnimationDuration(index);
            this.index = index;
        }

        public AnimationInstance(com.google.android.filament.gltfio.Animator animator, int index, long startTime) {
        }
    }

    private final Set<AnimationInstance> animators = new ArraySet<>();

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
    private int nextColor = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        setContentView(R.layout.activity_place);


        Intent intent = getIntent();
        Float distance = (Float) intent.getSerializableExtra("distance");

        // 가구 길이
//        int length = (int) intent.getSerializableExtra("length");
        int length = 180;
        TextView text_distance = (TextView) findViewById(R.id.text_distance);

        text_distance.setText("측정 거리: " + distance + " cm");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(length <= distance){
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
                    Intent pageIntent = new Intent(PlaceActivity.this, DistanceActivity.class);
                    startActivity(pageIntent);
                //    Toast.makeText(getApplicationContext(), "배치 안하고 출력", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

            WeakReference<PlaceActivity> weakActivity = new WeakReference<>(this);

            ModelRenderable.builder()
                    .setSource(
                            this,
                            Uri.parse(
                                    "https://raw.githubusercontent.com/justbeaver97/2021-1-CapstoneDesign/master/threejs_tutorial/models/746525_close.glb"))
                    .setIsFilamentGltf(true)
                    .build()
                    .thenAccept(
                            modelRenderable -> {
                                PlaceActivity activity = weakActivity.get();
                                if (activity != null) {
                                    activity.renderable = modelRenderable;

                                }
                            })
                    .exceptionally(
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(this, "인테리어 파일을 불러올 수 없습니다.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            });

            arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        if (renderable == null) {
                            return;
                        }

                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());

                        // Create the transformable model and add it to the anchor.
                        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
                        model.setRenderable(renderable);

                        float wantScale = 0.00007f * distance;
                        model.getScaleController().setMaxScale(wantScale);
                        model.getScaleController().setMinScale(wantScale/100 *99);
                        model.getRotationController().setEnabled(false);
                        model.getTranslationController().setEnabled(false);
                        model.setParent(anchorNode);
                        model.select();

                        FilamentAsset filamentAsset = model.getRenderableInstance().getFilamentAsset();
                        if (filamentAsset.getAnimator().getAnimationCount() > 0) {
                            animators.add(new PlaceActivity.AnimationInstance(filamentAsset.getAnimator(), 0, System.nanoTime()));
                        }

                        Color color = colors.get(nextColor);
                        nextColor++;
                        for (int i = 0; i < renderable.getSubmeshCount(); ++i) {
                            Material material = renderable.getMaterial(i);
                            material.setFloat4("baseColorFactor", color);
                        }

                    });

            arFragment
                    .getArSceneView()
                    .getScene()
                    .addOnUpdateListener(
                            frameTime -> {
                                Long time = System.nanoTime();
                                for (PlaceActivity.AnimationInstance animator : animators) {
                                    animator.animator.applyAnimation(
                                            animator.index,
                                            (float) ((time - animator.startTime) / (double) SECONDS.toNanos(1))
                                                    % animator.duration);
                                    animator.animator.updateBoneMatrices();
                                }
                            });



        }else{

            builder.setMessage("배치가 불가능합니다. 다른 장소를 선택하세요. (가구 길이: " + length + " cm)");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent pageIntent = new Intent(PlaceActivity.this, DistanceActivity.class);
                    startActivity(pageIntent);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }




    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
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
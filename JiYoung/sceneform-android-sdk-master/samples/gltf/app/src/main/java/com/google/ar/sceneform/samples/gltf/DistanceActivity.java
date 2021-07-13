// 거리측정 페이지

package com.google.ar.sceneform.samples.gltf;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

public class DistanceActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ArFragment arFragment;
    private Anchor[] currentAnchor = new Anchor[2]; // anchor array
    private AnchorNode[] currentAnchorNode = new AnchorNode[2];
    // anchorNode 배열 선언 (Node : 하나의 object가 차지하는 영역, 즉 anchorNode는 하나의 anchor object가 차지하는 영역)
    private TextView tvDistance; // Distance 보여주는 textview
    ModelRenderable cubeRenderable; // sceneform renderable
    public static int cnt = 0; // count anchor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) { // requirements check
            Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_distance); // activity_distance.xml

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment); // ux_fragment.xml -> fragment manager -> arfragment
        tvDistance = findViewById(R.id.tvDistance); // Distance Textview


        initModel();

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> { // Plane의 white dot tap하면 function 실행
            if (cubeRenderable == null)
                return;


            // Creating Anchor (Anchor : fixed location and orientation in real world -> rendering 3D model in Anchor)
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene()); // anchorNode Parrent -> Scene (plane)

            clearAnchor(); // function -> cnt == 2일경우 initialize

            currentAnchor[cnt] = anchor;
            currentAnchorNode[cnt] = anchorNode; // anchor 생성마다 array 할당

            cnt++; // anchor count ++

            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem()); // 확대, 축소, 변형 Node
            node.setRenderable(cubeRenderable); // set renderable
            node.setParent(anchorNode); // node Parrent -> anchornode
            arFragment.getArSceneView().getScene().addOnUpdateListener(this);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            node.select();
        });

    }

    public boolean checkIsSupportedDeviceOrFinish(final Activity activity) { // requirements checked

        String openGlVersionString =
                ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)))
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

    private void initModel() {
        MaterialFactory.makeTransparentWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {
                            Vector3 vector3 = new Vector3(0.05f, 0.01f, 0.01f);
                            cubeRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material);
                            cubeRenderable.setShadowCaster(false);
                            cubeRenderable.setShadowReceiver(false);
                        });
    }

    private void clearAnchor() { // clear Anchor
        if(cnt == 2){ // tap hitresult가 2개 초과일 수 없음
            for(int i=0; i<2; i++){
                currentAnchor[i] = null;
                if (currentAnchorNode[i] != null) {
                    arFragment.getArSceneView().getScene().removeChild(currentAnchorNode[i]);
                    currentAnchorNode[i].getAnchor().detach();
                    currentAnchorNode[i].setParent(null);
                    currentAnchorNode = null;
                }
            }
            cnt = 0; // cnt, currentAnchor, currentAnchorNode initialize
        }
    }


    @Override
    public void onUpdate(FrameTime frameTime) { // scene이 update 되기 전 frame당 한번씩 호출
        Frame frame = arFragment.getArSceneView().getArFrame();

        Intent intent = getIntent();
        int length = (int) intent.getSerializableExtra("length"); //  get length


        if (currentAnchorNode != null && cnt == 2) {
            Pose objectPose = currentAnchor[0].getPose();
            Pose newPose = currentAnchor[1].getPose(); // get each 2 anchors pose

            float dx = objectPose.tx() - newPose.tx();
            float dy = objectPose.ty() - newPose.ty();
            float dz = objectPose.tz() - newPose.tz(); // current x,y,z pose distance

            ///Compute distance.
            float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz); // distance
            tvDistance.setText("측정된 길이: " + (float)Math.round(distanceMeters*100) + " cm");

            Intent placeIntent = new Intent(DistanceActivity.this, PlaceActivity.class); // Intent -> PlaceActivity
            placeIntent.putExtra("distance", (float)Math.round(distanceMeters*100)); // distance
            placeIntent.putExtra("length", length); // furniture length

            startActivity(placeIntent);

            finish();

        }
    }
}

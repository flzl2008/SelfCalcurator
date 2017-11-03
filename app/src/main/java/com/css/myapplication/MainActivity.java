package com.css.myapplication;

import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public native String convertNativeGray(long matAddrRgba);


    private Mat mRgba;
    public String barcode;

    static {

        System.loadLibrary("opencv_java3");
    }

    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    System.loadLibrary("nativegray");// Load Native module
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase)
                findViewById(R.id.activity_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
                    mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (inputFrame.rgba() != null) {
            mRgba = inputFrame.rgba();
            barcode = convertNativeGray(mRgba.getNativeObjAddr());
            Intent intent2 = getIntent();
            String input= getIntent().getStringExtra("input");
            if (!barcode.equals("")) {
                if(input==null) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    Toast.makeText(MainActivity.this, "제품이 추가되었습니다,", Toast.LENGTH_SHORT).show();
                                   // String barcode = jsonObject.getString("barcode");
                                   // String id = jsonObject.getString("id");
                                    Log.d("item2",barcode);
                                } else {
                                    Toast.makeText(MainActivity.this, "등록되있지 않는 제품입니다.", Toast.LENGTH_SHORT).show();
                                    Log.d("item","없다");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("item","에러");
                            }
                        }
                    };
                    Intent intent = getIntent();
                    String id = intent.getStringExtra("id");//id 받아옴
                    MainActivityRequest mainActivityRequest = new MainActivityRequest(barcode, id, responseListener);
                    Log.i("barcode", barcode);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(mainActivityRequest);
                    intent = new Intent(this, MYCART.class);
                    intent.putExtra("id",id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    startActivity(intent);
                }
                else{
                    intent2.putExtra("barcode",barcode);//id값 메인엑티비티로전달
                    setResult(RESULT_OK, intent2);
                    finish();
                }
            }

        }

        return mRgba;
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.css.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.css.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}


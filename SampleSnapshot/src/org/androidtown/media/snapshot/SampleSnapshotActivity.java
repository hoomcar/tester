package org.androidtown.media.snapshot;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class SampleSnapshotActivity extends Activity {
	ArrayAdapter<CharSequence> adspin;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final CameraSurfaceView cameraView = new CameraSurfaceView(getApplicationContext());
        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.previewFrame);
        previewFrame.addView(cameraView);
        
        /*
        Spinner sp = (Spinner)findViewById(R.id.spinner1);
        sp.setPrompt("카메라 선택");
        
        adspin = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_list_item_checked);
        //adspin.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //adspin.add("33");
        //adspin.add("44");
        sp.setAdapter(adspin);
        
        sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});*/       
        
        Button changeCameraButton0 = (Button) findViewById(R.id.button1);
        Button changeCameraButton1 = (Button) findViewById(R.id.button2);
        Button changeCameraButton2 = (Button) findViewById(R.id.button3);
        Button changeCameraButton4 = (Button) findViewById(R.id.changeCameraButton);
        changeCameraButton0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	cameraView.changeCameraFacing(0);

            	Toast.makeText(getApplicationContext(), "0번 카메라", Toast.LENGTH_SHORT).show();
            }
        });
        changeCameraButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//cameraView.changeCameraFacing(4);
            	int cnum = Camera.getNumberOfCameras();

            	Toast.makeText(getApplicationContext(), cnum+"개", Toast.LENGTH_LONG).show();
            	
            }
        });
        changeCameraButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	cameraView.changeCameraFacing(1);

            	
            }
        });
        changeCameraButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	cameraView.changeCameraFacing(2);
            	            	
            }
        });

    }

    /**
     * Camera SurfaceView
     */
    private class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    	public static final String TAG = "CameraSurfaceView";

        private SurfaceHolder mHolder;
        private Camera camera = null;

        private int currentFacing;

        public CameraSurfaceView(Context context) {
            super(context);

            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
		public void surfaceCreated(SurfaceHolder holder) {
            //camera = Camera.open();
        	openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

            try {
                camera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                Log.e(TAG, "Failed to set camera preview.", e);
            }
        }

        /**
         * Open camera with facing
         *
         * Facing parameter can be CAMERA_FACING_FRONT or CAMERA_FACING_BACK
         * WARNING : This feature is supported after Gingerbread
         *
         * @param facing
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
		private void openCamera(int facing) {
        	Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        	int cameraCount = Camera.getNumberOfCameras();
        	for (int i = 0; i < cameraCount; i++) {
        		Camera.getCameraInfo(i, cameraInfo);
        		if (cameraInfo.facing == facing) {
        			try {
        				camera = Camera.open(i);
        			} catch (RuntimeException e) {
        				Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
        			}

        			currentFacing = facing;
        		}
        	}
        }

        /**
         * Change camera facing
         *
         * @param facing
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
		public void changeCameraFacing(int num) {
        	if (camera != null) {
        		camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
        	}

        	/*if (currentFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        		openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        	} else {
        		openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        	}*/
        	openCamera(num);

        	try {
        		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    camera.setDisplayOrientation(90);
                 } else {
                    Parameters parameters = camera.getParameters();
                    parameters.setRotation(90);
                    camera.setParameters(parameters);
                 }

                camera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                Log.e(TAG, "Failed to set camera preview.", e);
                camera.release();
            }

            camera.startPreview();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        	try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                   camera.setDisplayOrientation(90);
                } else {
                   Parameters parameters = camera.getParameters();
                   parameters.setRotation(90);
                   camera.setParameters(parameters);
                }

                camera.setPreviewDisplay(holder);
             } catch (IOException exception) {
                camera.release();
             }

        	camera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        	camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }

    }

}
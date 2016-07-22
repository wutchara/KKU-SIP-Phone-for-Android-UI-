package com.portsip.sipsample.ui;

import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.sipsample.util.Session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Observable;
import java.util.Observer;

public class VideoCallFragment extends Fragment implements Observer {

	PortSipSdk sdk;
	static boolean useFrontCamera = true;
	final int MENU_SWITCHCAMERA = 0;
	MyApplication myApplication = null;
	Context context = null;

	private LinearLayout mLlRemoteSurface = null;
	private LinearLayout mLlLocalSurface = null;

	// remote renderer
	private SurfaceView remoteSurfaceView = null;
	private SurfaceView localSurfaceView = null;
	private ImageButton imgSwitchCamera = null;
	private OrientationEventListener mListener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView;
		super.onCreateView(inflater, container, savedInstanceState);
		context = getActivity();
		myApplication = (MyApplication) context.getApplicationContext();
		sdk = myApplication.getPortSIPSDK();

		myView = inflater.inflate(R.layout.videoview, null);

		mLlRemoteSurface = (LinearLayout) myView
				.findViewById(R.id.llRemoteView);
		mLlLocalSurface = (LinearLayout) myView.findViewById(R.id.llLocalView);

		imgSwitchCamera = (ImageButton) myView.findViewById(R.id.ibcamera);

		localSurfaceView = myApplication.getLocalSurfaceView();
		remoteSurfaceView = myApplication.getRemoteSurfaceView();

		mLlLocalSurface.addView(localSurfaceView);
		mLlRemoteSurface.addView(remoteSurfaceView);

		initCamera(sdk);// set default camera

        updateVideo();

        

	    mListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {
				try {				
					if ((orientation > 345 || orientation <15)  ||
							(orientation > 75 && orientation <105)   ||
							(orientation > 165 && orientation < 195) ||
							(orientation > 255 && orientation < 285)){

						 int cameraId=useFrontCamera?1:0;
						if (orientation == ORIENTATION_UNKNOWN) return;
					     android.hardware.Camera.CameraInfo info =
					            new android.hardware.Camera.CameraInfo();
					     android.hardware.Camera.getCameraInfo(cameraId, info);
					     orientation = (orientation + 45) / 90 * 90;
					     
					     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					    	 orientation = (info.orientation - orientation + 360) % 360;
					     } else {  // back-facing camera
					    	 orientation = (info.orientation + orientation) % 360;
					     }
					     sdk.setVideoOrientation(orientation);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
    
		
		imgSwitchCamera.setOnClickListener(new CameraSwitchListener());
		return myView;
	}

	@Override
	public void onDestroyView() {
		mLlRemoteSurface.removeView(remoteSurfaceView);
		mLlLocalSurface.removeView(localSurfaceView);
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
        updateVideo();
		final Session cur = myApplication.getCurrentSession();
		if(cur!=null){
			cur.addObserver(this);
			remoteSurfaceView.post(new Runnable() {
				
				@Override
				public void run() {
					cur.setVideosizeChanged();
					cur.notifyObservers();	
				}
			});
			
		}
    	if (mListener != null && mListener.canDetectOrientation()) {
			mListener.enable();
		}
    	
        
	}

	
	@Override
	public void onPause() {		
		
		if (mListener != null && mListener.canDetectOrientation()) {
			mListener.disable();
		}
		
		Session cur = myApplication.getCurrentSession();
		if(cur!=null){
			cur.deleteObserver(this);
		}

		if (cur != null && cur.getSessionState()
				&& cur.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
			sdk.displayLocalVideo(false); // do not display
		}
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void update(Observable observable, Object o) {
		final Session cur = myApplication.getCurrentSession();
		if(remoteSurfaceView!=null&&cur!=null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					float rlWidht = mLlRemoteSurface.getWidth();
					float rlHeight = mLlRemoteSurface.getHeight();
					float height = cur.getVideoHeight();
					float width = cur.getVideoWidth();
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) remoteSurfaceView.getLayoutParams();
                    float rate = rlHeight/height <rlWidht/width?rlHeight/height:rlWidht/width;
					lp.height = (int) (height*rate);
					lp.width = (int) (width*rate);
					lp.gravity= Gravity.CENTER;

					remoteSurfaceView.setLayoutParams(lp);
				}
			});
		}
	}

	class CameraSwitchListener implements OnClickListener {

		public void onClick(View v) {
			switchCamera(sdk);
		}
	}

	private void initCamera(PortSipSdk sipSdk) {
		if (useFrontCamera) {
			sipSdk.setVideoOrientation(270);
			sipSdk.setVideoDeviceId(1);

		} else {
			sipSdk.setVideoOrientation(90);
			sipSdk.setVideoDeviceId(0);
		} // display remote video
	}

	private void switchCamera(PortSipSdk sipSdk) {
		useFrontCamera = !useFrontCamera;
		sdk.displayLocalVideo(false);
		if (useFrontCamera) {
			sipSdk.setVideoDeviceId(1);
			sipSdk.setVideoOrientation(270);
		} else {
			sipSdk.setVideoDeviceId(0);
			sipSdk.setVideoOrientation(90);
		}
		sdk.displayLocalVideo(true);
	}
    public void updateVideo() {
        if (myApplication.isConference()) {
            sdk.setConferenceVideoWindow(remoteSurfaceView);
            return;
        }

        Session cur = myApplication.getCurrentSession();
        if (cur != null
                //&& cur.getSessionState()
                && cur.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID
                && cur.getVideoState()) {

            sdk.displayLocalVideo(true); // display Local video
            sdk.setRemoteVideoWindow(cur.getSessionId(), remoteSurfaceView);
            sdk.sendVideo(cur.getSessionId(), true);
        }
        else{
            sdk.displayLocalVideo(false);
        }
    }
}

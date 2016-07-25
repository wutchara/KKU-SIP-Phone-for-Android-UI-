package com.portsip.p2psample;

import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.p2psample.R;
import com.portsip.p2psample.util.Line;
import com.portsip.p2psample.util.Network;
import com.portsip.p2psample.util.SettingConfig;
import com.portsip.p2psample.util.UserInfo;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class LoginFragment extends Fragment implements OnItemSelectedListener,OnClickListener{
	PortSipSdk mSipSdk;
	TextView mtxStatus;
	P2pApplication myApplication;
	Context context = null;
	String LogPath = null;
	String licenseKey ="PORTSIP_TEST_LICENSE";
	Network mNetwork =null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		context = getActivity();
		myApplication = ((P2pApplication) context.getApplicationContext());
		mSipSdk = myApplication.getPortSIPSDK();
		//this is a simple example.in fact, you need observe network change event
		mNetwork = new Network(context);	
		View rootView = inflater.inflate(R.layout.loginview, null);
		initView(rootView);
		return rootView;
	}

	public void onRegisterSuccess(String reason, int code) {
		undateStatus();
	}

	public void onRegisterFailure(String reason, int code) {
		undateStatus();
	}

	void undateStatus() {
		if (myApplication.isInitialized()) {
			mtxStatus.setText(R.string.ready);
		} else {
			mtxStatus.setText(R.string.unready);
		}
	}

	private void initView(View view) {
		mtxStatus = (TextView) view.findViewById(R.id.txtips);

		
		loadUserInfo(view);
		
		((EditText)view.findViewById(R.id.tvIP)).setText(mNetwork.getLocalIP(false));
		view.findViewById(R.id.btonline).setOnClickListener(this);
		view.findViewById(R.id.btoffline).setOnClickListener(this);

		undateStatus();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btoffline:
			offline();
			break;
		case R.id.btonline:
			online();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		switch (arg0.getId()) {
		case R.id.spSRTP:
			SetSRTPType(arg2);
			break;
		default:
			break;
		}
	}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void SetSRTPType(int index) {

		int SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;

		switch (index) {
		case 0:
			SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_NONE;
			break;

		case 1:
			SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_FORCE;
			break;

		case 2:
			SrtType = PortSipEnumDefine.ENUM_SRTPPOLICY_PREFER;
			break;
		}

		SettingConfig.setSrtpType(context, SrtType, mSipSdk);
	}

	int setUserInfo() {
		int localPort =5060;
		String localIP = mNetwork.getLocalIP(false);// ipv4

		UserInfo info = saveUserInfo(getView());
		
		if (info.isAvailable())// these fields are required
		{
			mSipSdk.CreateCallManager(context.getApplicationContext());// step 1

			int result = mSipSdk.initialize(PortSipEnumDefine.ENUM_TRANSPORT_UDP,
                    PortSipEnumDefine.ENUM_LOG_LEVEL_NONE, null,
					Line.MAX_LINES, "PortSIP VoIP SDK for Android", 
					0,0);// step 2
			if (result != PortSipErrorcode.ECoreErrorNone) {
				return result;
			}

			setPortSipLisenceKey(licenseKey);// step 3

			result = mSipSdk.setUser(info.getUserName(), info.getUserDisplayName(), null, info.getUserPassword(),
					localIP, localPort, null, null, 0,
					null, 0, null, 0);// step 4

			if (result != PortSipErrorcode.ECoreErrorNone) {
				return result;
			}
		} else {
			return -1;
		}

		SettingConfig.setAVArguments(context,mSipSdk);
		return PortSipErrorcode.ECoreErrorNone;
	}

	void loadUserInfo(View view){
				
		UserInfo userInfo = SettingConfig.getUserInfo(context);	
		String item= userInfo.getUserName()==null?"":userInfo.getUserName();
		((EditText) view.findViewById(R.id.etusername)).setText(item);
		
		item= userInfo.getUserPassword()==null?"":userInfo.getUserPassword();
		((EditText) view.findViewById(R.id.etpwd)).setText(item);
		
		
		item= userInfo.getLocalIp()==null?"":userInfo.getLocalIp();
		((EditText) view.findViewById(R.id.tvIP)).setText(item);
		
		
		item= ""+userInfo.getLocalPort();
		((EditText) view.findViewById(R.id.etsipport)).setText(item);
		
		
		item= userInfo.getUserDisplayName()==null?"":userInfo.getUserDisplayName();
		((EditText) view.findViewById(R.id.etdisplayname)).setText(item);

		Spinner spSRTP = (Spinner) view.findViewById(R.id.spSRTP);
		
		
		spSRTP.setAdapter(new ArrayAdapter<String>(context,
				R.layout.viewspinneritem, getResources().getStringArray(
						R.array.srtp)));

		
		spSRTP.setOnItemSelectedListener(this);		
		spSRTP.setSelection(SettingConfig.getSrtpType(context));
		
	}
	
	private UserInfo saveUserInfo(View view){
		
		int port;		
		UserInfo userInfo = new UserInfo();		
		
		String item = ((EditText) view.findViewById(R.id.etusername)).getText().toString();
		userInfo.setUserName(item);
		item = ((EditText) view.findViewById(R.id.etpwd)).getText().toString();
		userInfo.setUserPwd(item);
		item = ((EditText) view.findViewById(R.id.tvIP)).getText().toString();
		userInfo.setLocalIp(item);
		item = ((EditText) view.findViewById(R.id.etsipport)).getText().toString();
		try{
			port = Integer.parseInt(item);
		}catch(NumberFormatException e){
			port = 5060;
		}
		userInfo.setLocalPort(port);
		item = ((EditText) view.findViewById(R.id.etdisplayname)).getText().toString();
		userInfo.setUserDisplayName(item);
		
		SettingConfig.setUserInfo(context, userInfo);
		
		((Spinner) view.findViewById(R.id.spSRTP)).setSelection(0);
		
		return userInfo;
	}

	private int online() {
		int ret = PortSipErrorcode.ECoreNotInitialized;			
		
		ret = setUserInfo();
		if(ret==PortSipErrorcode.ECoreErrorNone){			
			myApplication.setInitilState(true);
			undateStatus();
		}
		return ret;
	}
	

	private void offline() {
		Line[] mLines = myApplication.getLines();
		for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
			if (mLines[i].getRecvCallState()) {
				mSipSdk.rejectCall(mLines[i].getSessionId(), 486);
			} else if (mLines[i].getSessionState()) {
				mSipSdk.hangUp(mLines[i].getSessionId());
			}

			mLines[i].reset();
		}
		myApplication.setInitilState(false);
		undateStatus();

		mSipSdk.DeleteCallManager();
	}

	boolean setPortSipLisenceKey(String lisence) {
		int nSetKeyRet = mSipSdk.setLicenseKey(lisence);
		if (nSetKeyRet == PortSipErrorcode.ECoreTrialVersionLicenseKey) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Prompt").setMessage(R.string.trial_version_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
			return false;
		} else if (nSetKeyRet == PortSipErrorcode.ECoreWrongLicenseKey) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Prompt").setMessage(R.string.wrong_lisence_tips);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
			return false;
		}
		return true;
	}

}

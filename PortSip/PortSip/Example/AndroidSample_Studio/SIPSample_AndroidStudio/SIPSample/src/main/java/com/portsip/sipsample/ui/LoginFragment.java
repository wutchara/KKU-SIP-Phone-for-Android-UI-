package com.portsip.sipsample.ui;

import java.util.Random;

import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.sipsample.util.Line;
import com.portsip.sipsample.util.SettingConfig;
import com.portsip.sipsample.util.UserInfo;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class LoginFragment extends Fragment implements OnItemSelectedListener,OnClickListener{
	PortSipSdk mSipSdk;
	Button mbtnReg, mbtnUnReg;

	TextView mtxStatus;
	String statusString;
	MyApplication myApplication;
	Context context = null;
	String LogPath = null;
	String licenseKey ="PORTSIP_TEST_LICENSE";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		context = getActivity();
		myApplication = ((MyApplication) context.getApplicationContext());
		mSipSdk = myApplication.getPortSIPSDK();

		View rootView = inflater.inflate(R.layout.loginview, null);
		initView(rootView);
		
		return rootView;
	}

	public void onRegisterSuccess(String reason, int code) {
		undateStatus();
	}

	public void onRegisterFailure(String reason, int code) {
		statusString = reason;
		undateStatus();
	}

	void undateStatus() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (myApplication.isOnline()) {
                    mtxStatus.setText(R.string.online);
                    statusString= null;
                } else {
                    if(statusString!=null)
                        mtxStatus.setText(getString(R.string.unregister)+": "+statusString);
                    else {
                        mtxStatus.setText(R.string.unregister);
                    }
                }
            }
        });

	}

	private void initView(View view) {
		mtxStatus = (TextView) view.findViewById(R.id.txtips);

		mbtnReg = (Button) view.findViewById(R.id.btonline);
		mbtnUnReg = (Button) view.findViewById(R.id.btoffline);
		loadUserInfo(view);

		mbtnReg.setOnClickListener(this);

		mbtnUnReg.setOnClickListener(this);

		
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
		case R.id.spTransport:
			SetTransType(arg2);
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

		SettingConfig.setSrtpType(context,SrtType,mSipSdk);
	}

	
	private void SetTransType(int index) {

		int transType = PortSipEnumDefine.ENUM_TRANSPORT_UDP;

		switch (index) {
		case 0:
			transType = PortSipEnumDefine.ENUM_TRANSPORT_UDP;
			break;

		case 1:
			transType = PortSipEnumDefine.ENUM_TRANSPORT_TLS;
			break;
		case 2:
			transType = PortSipEnumDefine.ENUM_TRANSPORT_TCP;
			break;
		
		case 3:
			transType = PortSipEnumDefine.ENUM_TRANSPORT_PERS;
			break;
		}

		SettingConfig.setTransType(context,transType);
	}
	
	
	void loadUserInfo(View view){
				
		UserInfo userInfo = SettingConfig.getUserInfo(context);	
		String item= userInfo.getUserName()==null?"":userInfo.getUserName();
		((EditText) view.findViewById(R.id.etusername)).setText(item);
		
		item= userInfo.getUserPassword()==null?"":userInfo.getUserPassword();
		((EditText) view.findViewById(R.id.etpwd)).setText(item);
		
		
		item= userInfo.getSipServer()==null?"":userInfo.getSipServer();
		((EditText) view.findViewById(R.id.etsipsrv)).setText(item);
		
		
		item= ""+userInfo.getSipPort();
		((EditText) view.findViewById(R.id.etsipport)).setText(item);
		
		
		item= userInfo.getUserdomain()==null?"":userInfo.getUserdomain();
		((EditText) view.findViewById(R.id.etuserdomain)).setText(item);
		
		
		item= userInfo.getAuthName()==null?"":userInfo.getAuthName();
		((EditText) view.findViewById(R.id.etauthName)).setText(item);
		
		item= userInfo.getUserDisplayName()==null?"":userInfo.getUserDisplayName();
		((EditText) view.findViewById(R.id.etdisplayname)).setText(item);

		item= userInfo.getStunServer()==null?"":userInfo.getStunServer();
		((EditText) view.findViewById(R.id.etStunServer)).setText(item);
		
		item= ""+userInfo.getStunPort();
		((EditText) view.findViewById(R.id.etStunPort)).setText(item);
		
		Spinner spTransport = (Spinner) view.findViewById(R.id.spTransport);
		Spinner spSRTP = (Spinner) view.findViewById(R.id.spSRTP);
		
		spTransport.setAdapter(new ArrayAdapter<String>(context,
				R.layout.viewspinneritem, getResources().getStringArray(
						R.array.transpots)));
		spSRTP.setAdapter(new ArrayAdapter<String>(context,
				R.layout.viewspinneritem, getResources().getStringArray(
						R.array.srtp)));

		spSRTP.setOnItemSelectedListener(this);
		spTransport.setOnItemSelectedListener(this);
		
		spTransport.setSelection(userInfo.getTransType());		
		spSRTP.setSelection(SettingConfig.getSrtpType(context));
		
	}
	
	private UserInfo saveUserInfo(View view){
		
		int port;		
		UserInfo userInfo = new UserInfo();		
		
		String item = ((EditText) view.findViewById(R.id.etusername)).getText().toString();
		userInfo.setUserName(item);
		item = ((EditText) view.findViewById(R.id.etpwd)).getText().toString();
		userInfo.setUserPwd(item);
		item = ((EditText) view.findViewById(R.id.etsipsrv)).getText().toString();
		userInfo.setSipServer(item);
		item = ((EditText) view.findViewById(R.id.etsipport)).getText().toString();
		try{
			port = Integer.parseInt(item);
		}catch(NumberFormatException e){
			port = 5060;
		}
		userInfo.setSipPort(port);
		item = ((EditText) view.findViewById(R.id.etuserdomain)).getText().toString();
		userInfo.setUserDomain(item);
		item = ((EditText) view.findViewById(R.id.etauthName)).getText().toString();
		userInfo.setAuthName(item);
		item = ((EditText) view.findViewById(R.id.etdisplayname)).getText().toString();
		userInfo.setUserDisplayName(item);
		item = ((EditText) view.findViewById(R.id.etStunServer)).getText().toString();
		userInfo.setStunServer(item);
		item = ((EditText) view.findViewById(R.id.etStunPort)).getText().toString();
		try{
			port = Integer.parseInt(item);
		}catch(NumberFormatException e){
			port = 5060;
		}
		
		userInfo.setStunPort(port);
		
		
		userInfo.setTranType((int)((Spinner) view.findViewById(R.id.spTransport)).getSelectedItemId());
		
		SettingConfig.setUserInfo(context, userInfo);
		return userInfo;
	}

	private int online() {
		int result = setUserInfo();

		if (result == PortSipErrorcode.ECoreErrorNone) {
			result = mSipSdk.registerServer(90, 3);
			if(result!=PortSipErrorcode.ECoreErrorNone ){
				statusString = "register server failed";
				undateStatus();
			}
		}else {
			undateStatus();
		}
		myApplication.setLoginState(true);
		return result;

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
		myApplication.setLoginState(false);
		undateStatus();
		mSipSdk.unRegisterServer();
		mSipSdk.DeleteCallManager();
	}

	
	int setUserInfo() {
        Environment.getExternalStorageDirectory();
        LogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + '/';

//		String localIP = myApplication.getLocalIP(false);// ipv4
        String localIP = "0.0.0.0";
		int localPort = new Random().nextInt(4940) + 5060;
		UserInfo info = saveUserInfo(getView());
		
		if(info.isAvailable())
		{
			mSipSdk.CreateCallManager(context.getApplicationContext());// step 1

			int result = mSipSdk.initialize(info.getTransType(),
					PortSipEnumDefine.ENUM_LOG_LEVEL_NONE, LogPath,
					Line.MAX_LINES, "PortSIP VoIP SDK for Android",
					0,0);// step 2
			if (result != PortSipErrorcode.ECoreErrorNone) {
				statusString = "init Sdk Failed";
				return result;
			}

            int nSetKeyRet = mSipSdk.setLicenseKey(licenseKey);// step 3
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
                return -1;
            }

			result = mSipSdk.setUser(info.getUserName(),info.getUserDisplayName(),info.getAuthName(),info.getUserPassword(),
					localIP, localPort, info.getUserdomain(),info.getSipServer(),info.getSipPort(),
					info.getStunServer(), info.getStunPort(), null, 5060);// step 4

			if (result != PortSipErrorcode.ECoreErrorNone) {
				statusString = "setUser resource failed";
				return result;
			}
		} else {
			return -1;
		}

		SettingConfig.setAVArguments(context,mSipSdk);
		return PortSipErrorcode.ECoreErrorNone;
	}
}

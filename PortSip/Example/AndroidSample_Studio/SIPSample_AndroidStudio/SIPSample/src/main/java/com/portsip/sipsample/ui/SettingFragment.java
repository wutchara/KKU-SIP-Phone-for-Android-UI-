package com.portsip.sipsample.ui;

import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.sipsample.util.PreferenceFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingFragment extends PreferenceFragment {
	PortSipSdk mSipSdk;
	PreferenceManager mprefmamager;
	SharedPreferences mpreferences;
	boolean changed = true;
	Context context = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.media_set);
		context = getActivity();
		mSipSdk = ((MyApplication) getActivity().getApplicationContext())
				.getPortSIPSDK();

		mprefmamager = getPreferenceManager();
		mpreferences = mprefmamager.getSharedPreferences();

		MyOnChangeListen changeListen = new MyOnChangeListen();
		findPreference(getString(R.string.str_bitrate))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.str_resolution))
				.setOnPreferenceChangeListener(changeListen);
		findPreference(getString(R.string.str_fwtokey))
				.setOnPreferenceChangeListener(changeListen);
		mpreferences
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences sharedPreferences, String key) {
						changed = true;
					}
				});

	}

	@Override
	public void onPause() {
		super.onPause();
		if (!changed)
			return;

		// audio codecs
		mSipSdk.clearAudioCodec();

		if (mpreferences.getBoolean(getString(R.string.MEDIA_G722), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G722);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_G729), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_G729);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_AMR), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMR);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_AMRWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_AMRWB);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_GSM), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_GSM);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMA), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMA);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_PCMU), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_PCMU);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_SPEEX), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEX);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_SPEEXWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_SPEEXWB);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_ILBC), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ILBC);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_ISACWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACWB);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_ISACSWB), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_ISACSWB);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_OPUS), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_OPUS);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_DTMF), false)) {
			mSipSdk.addAudioCodec(PortSipEnumDefine.ENUM_AUDIOCODEC_DTMF);
		}

		mSipSdk.enableVAD(mpreferences.getBoolean(
				getString(R.string.MEDIA_VAD), true));
		mSipSdk.enableAEC(mpreferences.getBoolean(
				getString(R.string.MEDIA_AEC), true)?PortSipEnumDefine.ENUM_EC_DEFAULT:PortSipEnumDefine.ENUM_EC_NONE);
		mSipSdk.enableANS(mpreferences.getBoolean(
				getString(R.string.MEDIA_ANS), false)?PortSipEnumDefine.ENUM_NS_DEFAULT:PortSipEnumDefine.ENUM_NS_NONE);
		mSipSdk.enableAGC(mpreferences.getBoolean(
				getString(R.string.MEDIA_AGC), true)?PortSipEnumDefine.ENUM_AGC_DEFAULT:PortSipEnumDefine.ENUM_AGC_NONE);
		mSipSdk.enableCNG(mpreferences.getBoolean(
				getString(R.string.MEDIA_CNG), true));

		// audio codecs
		mSipSdk.clearVideoCodec();

		if (mpreferences.getBoolean(getString(R.string.MEDIA_H263), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_H26398), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H263_1998);
		}

		if (mpreferences.getBoolean(getString(R.string.MEDIA_H264), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_H264);
		}
		if (mpreferences.getBoolean(getString(R.string.MEDIA_VP8), false)) {
			mSipSdk.addVideoCodec(PortSipEnumDefine.ENUM_VIDEOCODEC_VP8);
		}

		// sdk.setAudioSamples(20,0);

		mSipSdk.setRtpPortRange(2000, 3000, 3002, 4000);

		setForward();

        mSipSdk.enableReliableProvisional(mpreferences.getBoolean(
                getString(R.string.str_pracktitle), false));
	}

	class MyOnChangeListen implements OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			if (arg0.getKey().equals(getString(R.string.str_bitrate))) {
				mSipSdk.setVideoBitrate((Integer) arg1);
			} else if (arg0.getKey().equals(getString(R.string.str_resolution))) {
				String resolution = (String) arg1;
				int width = 352;
				int height = 288;
				if(resolution.equals("1"))//QCIF
				{
					width = 176;
					height = 144;
				}
				else if(resolution.equals("2"))//CIF
				{
					width = 352;
					height = 288;
				}
				else if(resolution.equals("3"))//VGA
				{
					width = 640;
					height = 480;
				}
				else if(resolution.equals("4"))//SVGA
				{
					width = 1024;
					height = 768;
				}
				else if(resolution.equals("5"))//XVGA
				{
					width = 640;
					height = 480;
				}
				else if(resolution.equals("6"))//720P
				{
					width = 1280;
					height = 720;
				}
				else if(resolution.equals("7"))//QVGA
				{
					width = 320;
					height = 240;
				}

				mSipSdk.setVideoResolution(width, height);

			} else if (arg0.getKey().equals(getString(R.string.str_fwtokey))) {
				String forwardTo = (String) arg1;
				if (!forwardTo.matches(MyApplication.SIP_ADDRESS_PATTERN)) {
					Toast.makeText(
							context,
							"The forward address must likes sip:xxxx@sip.portsip.com.",
							Toast.LENGTH_LONG).show();
				}
			}
			return true;
		}
	}

	private int setForward() {
		int ret = PortSipErrorcode.ECoreArgumentNull;
		boolean forwardOpen = mpreferences.getBoolean(
				getString(R.string.str_fwopenkey), false);

		if (!forwardOpen) {
			mSipSdk.disableCallForward();
			return ret;
		}

		String forwardTo = mpreferences.getString(
				getString(R.string.str_fwtokey), "");
		boolean forwardOnBusy = mpreferences.getBoolean(
				getString(R.string.str_fwbusykey), true);

		if (forwardTo.length() <= 0
				|| !forwardTo.matches(MyApplication.SIP_ADDRESS_PATTERN)) {
			// Toast.makeText(context,"The forward address must likes sip:xxxx@sip.portsip.com.",
			// Toast.LENGTH_LONG).show();
			mSipSdk.disableCallForward();
			return ret;
		}

		if (forwardOnBusy) {
			ret = mSipSdk.enableCallForward(true, forwardTo);
		} else {
			ret = mSipSdk.enableCallForward(false, forwardTo);
		}

		return ret;
	}
}

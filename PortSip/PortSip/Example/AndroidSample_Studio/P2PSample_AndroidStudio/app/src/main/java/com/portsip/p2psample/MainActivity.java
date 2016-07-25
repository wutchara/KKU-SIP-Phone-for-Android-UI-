package com.portsip.p2psample;

import com.portsip.PortSipSdk;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import android.content.Context;
import com.portsip.p2psample.R;
import com.portsip.p2psample.util.Line;

public class MainActivity extends FragmentActivity{
	PortSipSdk sdk;
	final int MENU_QUIT = 0;
	Context mContext;
	LoginFragment loginFragment = null;
	NumpadFragment numpadFragment = null;
	VideoCallFragment videoCallFragment = null;
	MessageFragment messageFragment = null;
	SettingFragment settingFragment = null;
	Fragment frontFragment;
	View contentView = null;
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview);
		mContext = this;
		contentView = findViewById(R.id.content);
		loadLoginFragment();
		RadioGroup menuGroup = (RadioGroup) findViewById(R.id.tab_menu);
		menuGroup.check(R.id.tab_login);
		menuGroup.setOnCheckedChangeListener(new MyOnCheckChangeListen());
	}

	class MyOnCheckChangeListen implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.tab_login:
				loadLoginFragment();
				break;
			case R.id.tab_numpad:
				loadNumPadFragment();
				break;
			case R.id.tab_video:
				loadVideoFragment();
				break;
			case R.id.tab_message:
				loadMessageFragment();
				break;
			case R.id.tab_setting:
				loadSettingFragment();
				break;
			default:
				break;
			}

		}

	}

	@Override
	protected void onResume() {
		((P2pApplication) getApplicationContext()).setMainActivity(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		((P2pApplication) getApplicationContext()).setMainActivity(this);
		super.onPause();
	}

	private void loadLoginFragment() {
		if (loginFragment == null) {
			loginFragment = new LoginFragment();
		}
		frontFragment = loginFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, loginFragment).commit();
	}

    public NumpadFragment getNumpadFragment() {
        if (frontFragment == numpadFragment) {
            return numpadFragment;
        }
        return null;
    }

	private void loadNumPadFragment() {
		if (numpadFragment == null) {
			numpadFragment = new NumpadFragment();
		}
		frontFragment = numpadFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, numpadFragment).commit();
	}

    public VideoCallFragment getVideoCallFragment() {
        if (frontFragment == videoCallFragment) {
            return videoCallFragment;
        }
        return null;
    }

	private void loadVideoFragment() {
		if (videoCallFragment == null) {
			videoCallFragment = new VideoCallFragment();
		}
		frontFragment = videoCallFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, videoCallFragment).commit();
	}

	private void loadMessageFragment() {
		if (messageFragment == null) {
			messageFragment = new MessageFragment();
		}
		frontFragment = messageFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, messageFragment).commit();
	}

	private void loadSettingFragment() {
		if (settingFragment == null) {
			settingFragment = new SettingFragment();
		}
		frontFragment = settingFragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, settingFragment).commit();
	}

	LoginFragment getLoginFragment() {
		if (frontFragment == loginFragment) {
			return loginFragment;
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_QUIT, 0, "Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_QUIT:
			quit();
			break;
		default:
		}
		return true;

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			quit();
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	
	private void quit() {
		P2pApplication myApplication = (P2pApplication) getApplicationContext();
		sdk = myApplication.getPortSIPSDK();
		
		if (myApplication.isInitialized()) {
			Line[] mLines = myApplication.getLines();
			for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
				if (mLines[i].getRecvCallState()) {
					sdk.rejectCall(mLines[i].getSessionId(), 486);
				} else if (mLines[i].getSessionState()) {
					sdk.hangUp(mLines[i].getSessionId());
				}

				mLines[i].reset();
			}
			myApplication.setInitilState(false);
			sdk.unRegisterServer();
			sdk.DeleteCallManager();
		}
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
				.cancelAll();
		this.finish();
	}
}

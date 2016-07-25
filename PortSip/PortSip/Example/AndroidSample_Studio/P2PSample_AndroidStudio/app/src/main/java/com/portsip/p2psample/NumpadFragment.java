package com.portsip.p2psample;

import com.portsip.PortSipEnumDefine;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.p2psample.R;
import com.portsip.p2psample.util.Line;
import com.portsip.p2psample.util.Session;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class NumpadFragment extends Fragment {

	PortSipSdk mPortSipSdk;
	P2pApplication myApp;
	private TableLayout dialerPad, functionPad;
	private EditText etSipNum;
	private TextView mtips;
	private Spinner spline;
	private Context context = null;
	CheckBox cbSendVideo, cbRecvVideo, cbConference, cbSendSdp;
	private MyItemClickListener myItemClickListener;
	int _CurrentlyLine = 0;
	Line[] lines = null;
	ArrayAdapter<Session> spinnerAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		context = getActivity();
		myApp = (P2pApplication) context.getApplicationContext();
		mPortSipSdk = myApp.getPortSIPSDK();

		View view = inflater.inflate(R.layout.numpad, null);
		dialerPad = (TableLayout) view.findViewById(R.id.dialer_pad);
		functionPad = (TableLayout) view.findViewById(R.id.function_pad);
		etSipNum = (EditText) view.findViewById(R.id.etsipaddress);
		cbSendSdp = (CheckBox) view.findViewById(R.id.sendSdp);
		cbConference = (CheckBox) view.findViewById(R.id.conference);
		cbSendVideo = (CheckBox) view.findViewById(R.id.sendVideo);
		cbRecvVideo = (CheckBox) view.findViewById(R.id.acceptVideo);

		lines = myApp.getLines();
		spline = (Spinner) view.findViewById(R.id.sp_lines);

		spinnerAdapter = new ArrayAdapter<Session>(context,
				R.layout.viewspinneritem, lines);
		spline.setAdapter(spinnerAdapter);
		spline.setOnItemSelectedListener(new MyItemSelectListener());

		myItemClickListener = new MyItemClickListener();
		Button bt = (Button) view.findViewById(R.id.dial);
		bt.setOnClickListener(myItemClickListener);

		mtips = (TextView) view.findViewById(R.id.txtips);

		ImageButton imgbt = (ImageButton) view.findViewById(R.id.pad);
		imgbt.setOnClickListener(myItemClickListener);

		imgbt = (ImageButton) view.findViewById(R.id.delete);
		imgbt.setOnClickListener(myItemClickListener);

		cbConference.setChecked(myApp.isConference());
		cbConference.setOnCheckedChangeListener(new ConferenceBoxOnChange());

		setTableItemClickListener(dialerPad, myItemClickListener);
		setTableItemClickListener(functionPad, myItemClickListener);
		return view;
	}

	void switchVisibility(View view) {
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.INVISIBLE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	class MyItemSelectListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int index = arg2;
			if (_CurrentlyLine == (index + Line.LINE_BASE)) {
				return;
			}

			if (!myApp.isInitialized()) {
				showTips(R.string.readytips);
				return;
			}

			if (cbConference.isChecked()) {
				_CurrentlyLine = arg2 + Line.LINE_BASE;
				return;
			}

			// To switch the line, must hold currently line first
			Line currentLine = myApp.findSessionByIndex(_CurrentlyLine);
			if (currentLine.getSessionState()
					&& !currentLine.getHoldState()) {
				mPortSipSdk.hold(currentLine.getSessionId());
				currentLine.setHoldState(true);

				showTips(lines[_CurrentlyLine].getLineName() + ": Hold");
			}

			_CurrentlyLine = arg2 + Line.LINE_BASE;
			currentLine = myApp.findSessionByIndex(_CurrentlyLine);// update
																	// current
																	// line
			myApp.setCurrentLine(currentLine);
			// If target line was in hold state, then un-hold it
			if (currentLine.getSessionState()
					&& currentLine.getHoldState()) {
				mPortSipSdk.unHold(currentLine.getSessionId());
				currentLine.setHoldState(false);

				showTips(lines[_CurrentlyLine].getLineName()
						+ ": UnHold - call established");
			}
			spinnerAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	class MyItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if (myApp.isInitialized() == false) {
				showTips(R.string.readytips);
				return;
			}
			switch (v.getId()) {
			case R.id.zero:
			case R.id.one:
			case R.id.two:
			case R.id.three:
			case R.id.four:
			case R.id.five:
			case R.id.six:
			case R.id.seven:
			case R.id.eight:
			case R.id.nine:
			case R.id.star:
			case R.id.sharp: {
				String numberString = ((Button) v).getText().toString();
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				char number = numberString.charAt(0);
				etSipNum.getText().append(number);
				if (myApp.isInitialized()
						&& currentLine.getSessionState()) {
					if (number == '*') {
						mPortSipSdk.sendDtmf(currentLine.getSessionId(),
								PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 10,
								160, true);
						return;
					}
					if (number == '#') {
						mPortSipSdk.sendDtmf(currentLine.getSessionId(),
								PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, 11,
								160, true);
						return;
					}
					int sum = Integer.valueOf(numberString);// 0~9
					mPortSipSdk.sendDtmf(currentLine.getSessionId(),
							PortSipEnumDefine.ENUM_DTMF_MOTHOD_RFC2833, sum,
							160, true);
				}
			}
				break;
			case R.id.delete:
				int cursorpos = etSipNum.getSelectionStart();
				if (cursorpos - 1 >= 0) {
					etSipNum.getText().delete(cursorpos - 1, cursorpos);
				}
				break;
			case R.id.pad:
				switchVisibility(dialerPad);
				break;

			case R.id.dial: {


				String callTo = etSipNum.getText().toString();
				if (callTo == null || callTo.length() <= 0||!callTo.matches(P2pApplication.SIP_ADDRESS_PATTERN)) {
					showTips("The phone number is not available.");
					return;
				}

				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				if (currentLine.getSessionState()
						|| currentLine.getRecvCallState()) {
					showTips("Current line is busy now, please switch a line.");
					return;
				}

				// Ensure that we have been added one audio codec at least
				if (mPortSipSdk.isAudioCodecEmpty()) {
					showTips("Audio Codec Empty,add audio codec at first");
					return;
				}

				// Usually for 3PCC need to make call without SDP
				long sessionId = mPortSipSdk.call(callTo,
						cbSendSdp.isChecked(), cbSendVideo.isChecked());
				if (sessionId <= 0) {
					showTips("Call failure");
					return;
				}

				currentLine.setSessionId(sessionId);
				currentLine.setSessionState(true);
                currentLine.setVideoState(cbSendVideo.isChecked());
				myApp.setCurrentLine(lines[_CurrentlyLine]);
				showTips(lines[_CurrentlyLine].getLineName() + ": Calling...");

                myApp.updateSessionVideo();
			}
				break;
			case R.id.hangup: {

				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);

				if (currentLine.getRecvCallState()) {
					mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
					currentLine.reset();
					showTips(lines[_CurrentlyLine].getLineName()
							+ ": Rejected call");

					return;
				}

				if (currentLine.getSessionState()) {
					mPortSipSdk.hangUp(currentLine.getSessionId());
					currentLine.reset();

					showTips(lines[_CurrentlyLine].getLineName() + ": Hang up");
				}
                myApp.updateSessionVideo();
			}
				break;

			case R.id.answer: {

				Line currentline = myApp.findSessionByIndex(_CurrentlyLine);
				if (currentline.getRecvCallState() == false) {
					showTips("No incoming call on current line, please switch a line.");
					return;
				}

				currentline.setRecvCallState(false);
				currentline.setSessionState(true);

				int rt = mPortSipSdk.answerCall(currentline.getSessionId(),
						cbRecvVideo.isChecked());
				if (rt == 0) {

					showTips(lines[_CurrentlyLine].getLineName()
							+ ": Call established");
					myApp.setCurrentLine(currentline);

					if (cbRecvVideo.isChecked()) {
						mPortSipSdk.sendVideo(currentline.getSessionId(), true);
					}

					if (cbConference.isChecked() == true) {
						mPortSipSdk
								.joinToConference(currentline.getSessionId());
						currentline.setHoldState(false);
					}
				} else {
					currentline.reset();
					showTips(lines[_CurrentlyLine].getLineName()
							+ ": failed to answer call !");
				}
			}
				break;
			case R.id.reject: {

				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				if (currentLine.getRecvCallState()) {
					mPortSipSdk.rejectCall(currentLine.getSessionId(), 486);
					currentLine.reset();

					showTips(lines[_CurrentlyLine].getLineName()
							+ ": Rejected call");
					return;
				}
				break;
			}

			case R.id.hold: {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);


				if (!currentLine.getSessionState()
						|| currentLine.getHoldState()) {
					return;
				}

				int rt = mPortSipSdk.hold(currentLine.getSessionId());
				if (rt != 0) {
					showTips("hold operation failed.");
					return;
				}
				currentLine.setHoldState(true);
			}
				break;
			case R.id.unhold: {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);

				if (!currentLine.getSessionState()
						|| !currentLine.getHoldState()) {
					return;
				}

				int rt = mPortSipSdk.unHold(currentLine.getSessionId());
				if (rt != 0) {
					currentLine.setHoldState(false);
					showTips(lines[_CurrentlyLine].getLineName()
							+ ": Un-Hold Failure.");
					return;
				}

				currentLine.setHoldState(false);
				showTips(lines[_CurrentlyLine].getLineName() + ": Un-Hold");
			}
				break;
			case R.id.attenttransfer: {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);

				if (!currentLine.getSessionState()) {
					showTips("Need to make the call established first");
					return;
				}
				showTransferDialog(R.id.attenttransfer);
			}
				break;
			case R.id.mic:
				if (((Button) v).getText().equals("SpeakOn")) {
					mPortSipSdk.setLoudspeakerStatus(true);
					((Button) v).setText("SpeakOff");
				} else {
					mPortSipSdk.setLoudspeakerStatus(false);
					((Button) v).setText("SpeakOn");
				}
				break;
			case R.id.mute: {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				if (((Button) v).getText().equals("Mute")) {
					mPortSipSdk.muteSession(currentLine.getSessionId(), true,
							true, true, true);
					((Button) v).setText("UnMute");
				} else {
					mPortSipSdk.muteSession(currentLine.getSessionId(), false,
							false, false, false);
					((Button) v).setText("Mute");
				}
			}
				break;
			case R.id.transfer: {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				if (!currentLine.getSessionState()) {
					showTips("Need to make the call established first.");
					return;
				}

				showTransferDialog(R.id.transfer);
			}
				break;
			}
			spinnerAdapter.notifyDataSetChanged();
		}
	}

	private class ConferenceBoxOnChange implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			{
				if (myApp.isInitialized() == false) {
					showTips(R.string.readytips);
					buttonView.setChecked(false);
					myApp.setConferenceMode(false);
					return;
				}
				
				if (isChecked) {
					int rt = mPortSipSdk
							.createConference(myApp.getRemoteSurfaceView(),
									352,288, true);
					if (rt == 0) {
						showTips("Make conference succeeded");
						Line[] sessions = myApp.getLines();
						for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
							if (sessions[i].getSessionState()) {
								if (sessions[i].getHoldState()) {
									mPortSipSdk.unHold(sessions[i]
											.getSessionId());
									mPortSipSdk.sendVideo(sessions[i].getSessionId(),sessions[i].getVideoState());
								}
								mPortSipSdk.joinToConference(sessions[i]
										.getSessionId());
								sessions[i].setHoldState(false);
							}
						}

						myApp.setConferenceMode(true);
					} else {
						showTips("Failed to create conference");
						myApp.setConferenceMode(false);
						buttonView.setChecked(false);
					}
				} else {
					// Stop conference
					// Before stop the conference, MUST place all lines to hold
					// state
					myApp.setConferenceMode(false);
					Line[] sessions = myApp.getLines();
					for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
						if (sessions[i].getSessionState()
								&& !sessions[i].getHoldState()) {
							if (i != _CurrentlyLine) {
								// Hold the line
								mPortSipSdk.hold(sessions[i].getSessionId());
								sessions[i].setHoldState(true);
							}
						}
					}
					mPortSipSdk.destroyConference();
					showTips("Taken off Conference");

				}
			}
		}
	}

	private void setTableItemClickListener(TableLayout table,
			OnClickListener listener) {
		int row = table.getChildCount();
		for (int i = 0; i < row; i++) {
			TableRow tableRow = (TableRow) table.getChildAt(i);
			int line = tableRow.getChildCount();
			for (int index = 0; index < line; index++) {
				tableRow.getChildAt(index).setOnClickListener(
						myItemClickListener);
			}
		}
	}

	void showTips(String text) {
		mtips.setText(text);
		spinnerAdapter.notifyDataSetChanged();
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	void showTips(int resId) {
		mtips.setText(getString(resId));
		spinnerAdapter.notifyDataSetChanged();
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mtips.setText(intent.getStringExtra("description"));
			spinnerAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(P2pApplication.SESSION_CHANG);
		context.registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		context.unregisterReceiver(mReceiver);
	}

	@SuppressWarnings("unused")
	// please call this function in appropriate place
	private void startMediaRecord(String fileName) {
		if (!myApp.isInitialized()) {
			return;
		}

		SharedPreferences mPreferences = context.getSharedPreferences(
				String.format("%s_preferences", context.getPackageName()),
				Context.MODE_PRIVATE);
		String filePath = mPreferences.getString(
				getString(R.string.str_avpathkey), "");
		if (filePath.length() <= 0 || fileName == null
				|| fileName.length() <= 0) {
			return;
		}

		// Start recording
		Session curSession = myApp.getCurrentSession();
		if (curSession != null
				&& curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
			mPortSipSdk.startRecord(curSession.getSessionId(), filePath,
					fileName, true, PortSipEnumDefine.ENUM_VIDEOCODEC_H264,
					PortSipEnumDefine.ENUM_RECORD_MODE_BOTH,
					PortSipEnumDefine.ENUM_VIDEOCODEC_H264,
					PortSipEnumDefine.ENUM_RECORD_MODE_BOTH);
		}
	}

	@SuppressWarnings("unused")
	// please call this function in appropriate place
	private void stopMediaRecord() {
		if (!myApp.isInitialized()) {
			return;
		}

		Session curSession = myApp.getCurrentSession();
		if (curSession != null
				&& curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
			mPortSipSdk.stopRecord(curSession.getSessionId());
		}
	}

	void showTransferDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(
				R.layout.transfer_inputdialog, null);
		builder.setIcon(R.drawable.icon);
		builder.setTitle("Transfer input");
		builder.setView(textEntryView);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Session currentLine = myApp.findSessionByIndex(_CurrentlyLine);
				EditText transferTo = (EditText) textEntryView
						.findViewById(R.id.ettransferto);
				EditText transferLine = (EditText) textEntryView
						.findViewById(R.id.ettransferline);
				String referTo = transferTo.getText().toString();
				if (referTo.length() <= 0) {
					showTips("The transfer number is empty");
					return;
				}
				String lineString = transferLine.getText().toString();
				switch (id) {
				case R.id.transfer: {
					int rt = mPortSipSdk.refer(currentLine.getSessionId(),
							referTo);
					if (rt != 0) {
						showTips(lines[_CurrentlyLine].getLineName()
								+ ": failed to Transfer");
					} else {
						showTips(lines[_CurrentlyLine].getLineName()
								+ " failed to Transfer");
					}
				}
					break;
				case R.id.attenttransfer: {
					int line = Line.LINE_BASE - 1;
					try {
						line = Integer.valueOf(lineString);
					} catch (NumberFormatException e) {
						showTips("The replace line wrong");
					}

					if (line < Line.LINE_BASE || line >= Line.MAX_LINES) {
						showTips("The replace line out of range");
						return;
					}
					Session replaceSession = myApp.findSessionByIndex(line);
					if (replaceSession == null
							|| !replaceSession.getSessionState()) {
						showTips("The replace line does not established yet");
						return;
					}

					int rt = mPortSipSdk.attendedRefer(
							currentLine.getSessionId(),
							replaceSession.getSessionId(), referTo);

					if (rt != 0) {
						showTips(lines[_CurrentlyLine].getLineName()
								+ ": failed to Attend transfer");
					} else {
						showTips(lines[_CurrentlyLine].getLineName()
								+ ": Transferring");
					}
				}
					break;
				}
			}
		});

		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
		builder.create().show();
	}
}

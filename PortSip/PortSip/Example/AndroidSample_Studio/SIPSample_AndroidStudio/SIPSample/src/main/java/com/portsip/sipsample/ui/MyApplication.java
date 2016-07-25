package com.portsip.sipsample.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.portsip.OnPortSIPEvent;
import com.portsip.PortSipErrorcode;
import com.portsip.PortSipSdk;
import com.portsip.R;
import com.portsip.Renderer;
import com.portsip.sipsample.service.PortSipService;
import com.portsip.sipsample.service.PortSipService.MyBinder;
import com.portsip.sipsample.util.Line;
import com.portsip.sipsample.util.Network;
import com.portsip.sipsample.util.Session;
import com.portsip.sipsample.util.SettingConfig;
import com.portsip.sipsample.util.SipContact;
import com.portsip.sipsample.util.UserInfo;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class MyApplication extends Application implements OnPortSIPEvent {
	Intent srvIntent = null;
	PortSipService portSrv = null;
	MyServiceConnection connection = null;
	PortSipSdk sdk;
	boolean conference = false;
	private boolean _SIPLogined = false;// record login status
    private boolean _SIPOnline = false;// record register status
	MainActivity mainActivity;
	private SurfaceView remoteSurfaceView = null;
	private SurfaceView localSurfaceView = null;
	Network netmanager;
	static final private Line[] _CallSessions = new Line[Line.MAX_LINES];// record
																			// all
																			// audio-video
																			// sessions
	static final private ArrayList<SipContact> contacts = new ArrayList<SipContact>();
	private Line _CurrentlyLine = _CallSessions[Line.LINE_BASE];// active line
	static final String SIP_ADDRESS_PATTERN = "^(sip:)(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}(:[0-9]{2,5})?$";
	public static final String SESSION_CHANG = MyApplication.class
			.getCanonicalName() + "Session change";
	public static final String CONTACT_CHANG = MyApplication.class
			.getCanonicalName() + "Contact change";

	public void sendSessionChangeMessage(String message, String action) {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		
		Notification notification = new Notification(R.drawable.icon, "Sip Notify!", System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(this,"Sip Notify",message,pi);
		nm.notify(1, notification);

		Intent broadIntent = new Intent(action);
		broadIntent.putExtra("description", message);
		sendBroadcast(broadIntent);
	}

	Line[] getLines() {
		return _CallSessions;
	}

	List<SipContact> getSipContacts() {
		return contacts;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sdk = new PortSipSdk();
		srvIntent = new Intent(this, PortSipService.class);
		connection = new MyServiceConnection();
		netmanager = new Network(this, new Network.NetWorkChangeListner() {
			@Override
			public void handleNetworkChangeEvent(final boolean wifiConnect,final boolean mobileConnect) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int localPort = new Random().nextInt(4940) + 5060;
                        if (isOnline()) {//network change ,unregist
                            if (mainActivity != null) {
                                LoginFragment fragment;
                                fragment = mainActivity.getLoginFragment();
                                if (fragment != null) {
                                    fragment.onRegisterFailure("", 404);
                                }
                            }
                            sdk.unRegisterServer();
                        } else {
                            if (_SIPLogined && (wifiConnect || mobileConnect)) {//need auto reg
                                UserInfo info = SettingConfig.getUserInfo(MyApplication.this);
                                sdk.setUser(info.getUserName(), info.getUserDisplayName(), info.getAuthName(), info.getUserPassword(),
                                        getLocalIP(false), localPort, info.getUserdomain(), info.getSipServer(), info.getSipPort(),
                                        info.getStunServer(), info.getStunPort(), null, 5060);
                                sdk.registerServer(90, 3);
                            }
                        }
                    }
                }).start();
            }
		});

		sdk.setOnPortSIPEvent(this);
		localSurfaceView = Renderer.CreateLocalRenderer(this);
		remoteSurfaceView = Renderer.CreateRenderer(this, true);

		bindService(srvIntent, connection, BIND_AUTO_CREATE);
		for (int i = 0; i < _CallSessions.length; i++) {
			_CallSessions[i] = new Line(i);
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		unbindService(connection);
		connection = null;
	}

	public SurfaceView getRemoteSurfaceView() {
		return remoteSurfaceView;
	}

	public SurfaceView getLocalSurfaceView() {
		return localSurfaceView;
	}

	public PortSipSdk getPortSIPSDK() {
		return sdk;
	}

	class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;

			portSrv = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			portSrv = null;
		}
	}

	public boolean isOnline() {
		return _SIPOnline;
	}

	void setConferenceMode(boolean state) {
		conference = state;
	}

	public boolean isConference() {
		return conference;
	}

	void setLoginState(boolean state) {
		_SIPLogined = state;
		if(state==false){
			_SIPOnline = false;
		}
	}

    public void showTipMessage(String text){
        if (mainActivity != null) {
            NumpadFragment fragment = mainActivity.getNumpadFragment();
            if (fragment != null) {
                fragment.showTips(text);
            }
        }
    }
    public int answerSessionCall(Line sessionLine, boolean videoCall){
        long sessionId = sessionLine.getSessionId();
        int rt = PortSipErrorcode.INVALID_SESSION_ID;
        if(sessionId != PortSipErrorcode.INVALID_SESSION_ID) {
            rt = sdk.answerCall(sessionLine.getSessionId(), videoCall);
        }
        if(rt == 0){
            sessionLine.setSessionState(true);
            setCurrentLine(sessionLine);
            if(videoCall) {
                sessionLine.setVideoState(true);
            }else{
                sessionLine.setVideoState(false);
            }
            updateSessionVideo();

            if (conference) {
                sdk.joinToConference(sessionLine.getSessionId());
                sdk.sendVideo(sessionLine.getSessionId(),sessionLine.getVideoState());
            }
            showTipMessage(sessionLine.getLineName()
                    + ": Call established");
        }else{
            sessionLine.reset();
            showTipMessage(sessionLine.getLineName()
                    + ": failed to answer call !");
        }

        return rt;
    }

    public void updateSessionVideo(){
        if( mainActivity != null) {
            VideoCallFragment fragment = mainActivity.getVideoCallFragment();
            if (fragment != null) {
                fragment.updateVideo();
            }
        }
    }

	String getLocalIP(boolean ipv6){
		return netmanager.getLocalIP(ipv6);
	}

	// register event listener
	@Override
	public void onRegisterSuccess(String reason, int code) {
        _SIPOnline = true;
		if (mainActivity != null) {
			LoginFragment fragment;
			fragment = mainActivity.getLoginFragment();
			if (fragment != null) {
				fragment.onRegisterSuccess(reason, code);
			}
		}
	}

	@Override
	public void onRegisterFailure(String reason, int code) {
        _SIPOnline = false;

		if (mainActivity != null) {
			LoginFragment fragment;
			fragment = mainActivity.getLoginFragment();
			if (fragment != null) {
				fragment.onRegisterFailure(reason, code);
			}
		}
	}

	// call event listener
	@Override
	public void onInviteIncoming(long sessionId, String callerDisplayName,String caller,
			 String calleeDisplayName,String callee,
			String audioCodecs, String videoCodecs, boolean existsAudio,
			boolean existsVideo) {

		Line tempSession = findIdleLine();
		
		if (tempSession == null)// all sessions busy
		{
			sdk.rejectCall(sessionId, 486);
			return;
		} else {
			tempSession.setRecvCallState(true);
		}

		if (existsVideo) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}

		tempSession.setSessionId(sessionId);
        tempSession.setVideoState(existsVideo);
		String comingCallTips = "Call incoming: "+ callerDisplayName + "<" + caller +">";
		tempSession.setDescriptionString(comingCallTips);
		sendSessionChangeMessage(comingCallTips,SESSION_CHANG);
        setCurrentLine(tempSession);

        if(existsVideo){
            updateSessionVideo();
            final Line curSession = tempSession;
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            alertDialog.setTitle("Incoming Video Call");
            alertDialog.setMessage(comingCallTips);

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Audio",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Answer Audio call
                            answerSessionCall(curSession,false);
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Video",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Answer Video call
                            answerSessionCall(curSession,true);
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Reject",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Reject call
                            if(curSession.getSessionId() != PortSipErrorcode.INVALID_SESSION_ID) {
                                sdk.rejectCall(curSession.getSessionId(), 486);
                            }
                            curSession.reset();

                            showTipMessage("Rejected call");
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        }else{//Audio call
            final Line curSession = tempSession;
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            alertDialog.setTitle("Incoming Audio Call");
            alertDialog.setMessage(comingCallTips);

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Answer",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Answer Audio call
                            answerSessionCall(curSession,false);
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Reject",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Reject call
                            sdk.rejectCall(curSession.getSessionId(), 486);
                            curSession.reset();

                            showTipMessage("Rejected call");
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        }

		bringToFront();
		Toast.makeText(this,comingCallTips, Toast.LENGTH_LONG).show();
		// You should write your own code to play the wav file here for alert
		// the incoming call(incoming tone);
	}

	@Override
	public void onInviteTrying(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Call is trying...");
		sendSessionChangeMessage("Call is trying...", SESSION_CHANG);
	}

	@Override
	public void onInviteSessionProgress(long sessionId, String audioCodecs,
			String videoCodecs, boolean existsEarlyMedia, boolean existsAudio,
			boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		if (existsVideo) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}

		tempSession.setSessionState(true);

		tempSession.setDescriptionString("Call session progress.");
		sendSessionChangeMessage("Call session progress.", SESSION_CHANG);
		tempSession.setEarlyMeida(existsEarlyMedia);

	}

	@Override
	public void onInviteRinging(long sessionId, String statusText,
			int statusCode) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		if (!tempSession.hasEarlyMeida()) {
			// Hasn't the early media, you must play the local WAVE file for
			// ringing tone
		}

		tempSession.setDescriptionString("Ringing...");
		sendSessionChangeMessage("Ringing...", SESSION_CHANG);
	}

	@Override
	public void onInviteAnswered(long sessionId, String callerDisplayName,String caller,
			 String calleeDisplayName, String callee,
			String audioCodecs, String videoCodecs, boolean existsAudio,
			boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		if (existsVideo) {
			sdk.sendVideo(tempSession.getSessionId(), true);
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
        tempSession.setVideoState(existsVideo);
		tempSession.setSessionState(true);
		tempSession.setDescriptionString("call established");
		sendSessionChangeMessage("call established", SESSION_CHANG);

		if (isConference()) {
			sdk.joinToConference(tempSession.getSessionId());
			sdk.sendVideo(tempSession.getSessionId(),tempSession.getVideoState());
			tempSession.setHoldState(false);

		}

		// If this is the refer call then need set it to normal
		if (tempSession.isReferCall()) {
			tempSession.setReferCall(false, 0);
		}

	}

	@Override
	public void onInviteFailure(long sessionId, String reason, int code) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("call failure" + reason);
		sendSessionChangeMessage("call failure" + reason, SESSION_CHANG);
		if (tempSession.isReferCall()) {
			// Take off the origin call from HOLD if the refer call is failure
			Line originSession = findLineBySessionId(tempSession
					.getOriginCallSessionId());
			if (originSession != null) {
				sdk.unHold(originSession.getSessionId());
				originSession.setHoldState(false);

				// Switch the currently line to origin call line
				setCurrentLine(originSession);

				tempSession.setDescriptionString("refer failure:" + reason
						+ "resume orignal call");
				sendSessionChangeMessage("call failure" + reason, SESSION_CHANG);
			}
		}

		tempSession.reset();

	}

	@Override
	public void onInviteUpdated(long sessionId, String audioCodecs,
			String videoCodecs, boolean existsAudio, boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		if (existsVideo) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}
		if (existsAudio) {
			// If more than one codecs using, then they are separated with "#",
			// for example: "g.729#GSM#AMR", "H264#H263", you have to parse them
			// by yourself.
		}

		tempSession.setDescriptionString("Call is updated");
	}

	@Override
	public void onInviteConnected(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Call is connected");
		sendSessionChangeMessage("Call is connected", SESSION_CHANG);

        updateSessionVideo();
	}

	@Override
	public void onInviteBeginingForward(String forwardTo) {
		sendSessionChangeMessage("An incoming call was forwarded to: "
				+ forwardTo, SESSION_CHANG);
	}

	@Override
	public void onInviteClosed(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.reset();
        updateSessionVideo();
		tempSession.setDescriptionString(": Call closed.");
		sendSessionChangeMessage(": Call closed.", SESSION_CHANG);
	}

	@Override
	public void onRemoteHold(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Placed on hold by remote.");
		sendSessionChangeMessage("Placed on hold by remote.", SESSION_CHANG);
	}

	@Override
	public void onRemoteUnHold(long sessionId, String audioCodecs,
			String videoCodecs, boolean existsAudio, boolean existsVideo) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Take off hold by remote.");
		sendSessionChangeMessage("Take off hold by remote.", SESSION_CHANG);
	}

	@Override
	public void onRecvDtmfTone(long sessionId, int tone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceivedRefer(long sessionId, final long referId, String to,
			String from, final String referSipMessage) {
		final Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			sdk.rejectRefer(referId);
			return;
		}

		final Line referSession = findIdleLine();

		if (referSession == null)// all sessions busy
		{
			sdk.rejectRefer(referId);
            return;
		} else {
			referSession.setSessionState(true);
		}

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_NEGATIVE: {

					sdk.rejectRefer(referId);
					referSession.reset();
				}
					break;
				case DialogInterface.BUTTON_POSITIVE: {

					sdk.hold(tempSession.getSessionId());// hold current session
					tempSession.setHoldState(true);

					tempSession
							.setDescriptionString("Place currently call on hold on line: ");

					long referSessionId = sdk.acceptRefer(referId,
							referSipMessage);
					if (referSessionId <= 0) {
						referSession
								.setDescriptionString("Failed to accept REFER on line");

						referSession.reset();

						// Take off hold
						sdk.unHold(tempSession.getSessionId());
						tempSession.setHoldState(false);
					} else {
						referSession.setSessionId(referSessionId);
						referSession.setSessionState(true);
						referSession.setReferCall(true,
								tempSession.getSessionId());

						referSession
								.setDescriptionString("Accepted the refer, new call is trying on line ");

						_CurrentlyLine = referSession;

						tempSession
								.setDescriptionString("Now current line is set to: "
										+ _CurrentlyLine.getLineName());
                        updateSessionVideo();
					}
				}
				}

			}
		};
		showGlobalDialog("Received REFER", "accept", listener, "reject",
				listener);

	}

	@Override
	public void onReferAccepted(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("the REFER was accepted.");
		sendSessionChangeMessage("the REFER was accepted.", SESSION_CHANG);
	}

	@Override
	public void onReferRejected(long sessionId, String reason, int code) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("the REFER was rejected.");
		sendSessionChangeMessage("the REFER was rejected.", SESSION_CHANG);
	}

	@Override
	public void onTransferTrying(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Transfer Trying.");
		sendSessionChangeMessage("Transfer Trying.", SESSION_CHANG);
	}

	@Override
	public void onTransferRinging(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Transfer Ringing.");
		sendSessionChangeMessage("Transfer Ringing.", SESSION_CHANG);
	}

	@Override
	public void onACTVTransferSuccess(long sessionId) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}
		tempSession.setDescriptionString("Transfer succeeded.");
	}

	@Override
	public void onACTVTransferFailure(long sessionId, String reason, int code) {
		Line tempSession = findLineBySessionId(sessionId);
		if (tempSession == null) {
			return;
		}

		tempSession.setDescriptionString("Transfer failure");

		// reason is error reason
		// code is error code

	}

	public Line findLineBySessionId(long sessionId) {
		for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i) {
			if (_CallSessions[i].getSessionId() == sessionId) {
				return _CallSessions[i];
			}
		}

		return null;
	}

	public Line findSessionByIndex(int index) {

		if (Line.LINE_BASE <= index && index < Line.MAX_LINES) {
			return _CallSessions[index];
		}

		return null;
	}

	static Line findIdleLine() {

		for (int i = Line.LINE_BASE; i < Line.MAX_LINES; ++i)// get idle session
		{
			if (!_CallSessions[i].getSessionState()
					&& !_CallSessions[i].getRecvCallState()) {
				return _CallSessions[i];
			}
		}

		return null;
	}

	public void setCurrentLine(Line line) {
		if (line == null) {
			_CurrentlyLine = _CallSessions[Line.LINE_BASE];
		} else {
			_CurrentlyLine = line;
		}

	}

	public Session getCurrentSession() {
		return _CurrentlyLine;
	}

	@Override
	public void onReceivedSignaling(long sessionId, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendingSignaling(long sessionId, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWaitingVoiceMessage(String messageAccount,
			int urgentNewMessageCount, int urgentOldMessageCount,
			int newMessageCount, int oldMessageCount) {
		String text = messageAccount;
		text += " has voice message.";

		showMessage(text);
		// You can use these parameters to check the voice message count

		// urgentNewMessageCount;
		// urgentOldMessageCount;
		// newMessageCount;
		// oldMessageCount;

	}

	@Override
	public void onWaitingFaxMessage(String messageAccount,
			int urgentNewMessageCount, int urgentOldMessageCount,
			int newMessageCount, int oldMessageCount) {
		String text = messageAccount;
		text += " has FAX message.";

		showMessage(text);
		// You can use these parameters to check the FAX message count

		// urgentNewMessageCount;
		// urgentOldMessageCount;
		// newMessageCount;
		// oldMessageCount;

	}

	@Override
	public void onPresenceRecvSubscribe(long subscribeId,
			String fromDisplayName, String from, String subject) {

		String fromSipUri = "sip:" + from;

		final long tempId = subscribeId;
		DialogInterface.OnClickListener onClick;
		SipContact contactReference = null;
		boolean contactExist = false;

		for (int i = 0; i < contacts.size(); ++i) {
			contactReference = contacts.get(i);
			String SipUri = contactReference.getSipAddr();

			if (SipUri.equals(fromSipUri)) {
				contactExist = true;
				if (contactReference.isAccept()) {
					long nOldSubscribeID = contactReference.getSubId();
					sdk.presenceAcceptSubscribe(tempId);
					String status = "Available";
					sdk.presenceOnline(tempId, status);

					if (contactReference.isSubscribed() && nOldSubscribeID >= 0) {
						sdk.presenceSubscribeContact(fromSipUri, subject);
					}
					return;
				} else {
					break;
				}
			}
		}

		//
		if (!contactExist) {
			contactReference = new SipContact();
			contacts.add(contactReference);
			contactReference.setSipAddr(fromSipUri);
		}
		final SipContact contact = contactReference;
		onClick = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					sdk.presenceAcceptSubscribe(tempId);
					contact.setSubId(tempId);
					contact.setAccept(true);
					String status = "Available";
					sdk.presenceOnline(tempId, status);
					contact.setSubstatus(true);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					contact.setAccept(false);// reject subscribe
					contact.setSubId(0);
					contact.setSubstatus(false);// offline

					sdk.presenceRejectSubscribe(tempId);
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		};
		showGlobalDialog(from, "Accept", onClick, "Reject", onClick);

	}

	@Override
	public void onPresenceOnline(String fromDisplayName, String from,
			String stateText) {

		String fromSipUri = "sip:" + from;
		SipContact contactReference;
		for (int i = 0; i < contacts.size(); ++i) {
			contactReference = contacts.get(i);
			String SipUri = contactReference.getSipAddr();
			if (SipUri.endsWith(fromSipUri)) {
				contactReference.setSubDescription(stateText);
				contactReference.setSubstatus(true);// online
			}
		}
		sendSessionChangeMessage("contact status change.", CONTACT_CHANG);
	}

	@Override
	public void onPresenceOffline(String fromDisplayName, String from) {

		String fromSipUri = "sip:" + from;
		SipContact contactReference;
		for (int i = 0; i < contacts.size(); ++i) {
			contactReference = contacts.get(i);
			String SipUri = contactReference.getSipAddr();
			if (SipUri.endsWith(fromSipUri)) {
				contactReference.setSubstatus(false);// "Offline";
				contactReference.setSubId(0);
			}
		}
		sendSessionChangeMessage("contact status change.", CONTACT_CHANG);
	}

	@Override
	public void onRecvOptions(String optionsMessage) {
		// String text = "Received an OPTIONS message: ";
		// text += optionsMessage.toString();
		// showTips(text);
	}

	@Override
	public void onRecvInfo(String infoMessage) {

		// String text = "Received a INFO message: ";
		// text += infoMessage.toString();
		// showTips(text);
	}

	@Override
	public void onRecvMessage(long sessionId, String mimeType,
			String subMimeType, byte[] messageData, int messageDataLength) {

	}

	@Override
	public void onRecvOutOfDialogMessage(String fromDisplayName, String from,
			String toDisplayName, String to, String mimeType,
			String subMimeType, byte[] messageData, int messageDataLength) {
		String text = "Received a " + mimeType + "message(out of dialog) from ";
		text += from;

		if (mimeType.equals("text") && subMimeType.equals("plain")) {
			// String receivedMsg = GetString(messageData);
			showMessage(text);
		} else if (mimeType.equals("application")
				&& subMimeType.equals("vnd.3gpp.sms")) {
			// The messageData is binary data

			showMessage(text);
		} else if (mimeType.equals("application")
				&& subMimeType.equals("vnd.3gpp2.sms")) {
			// The messageData is binary data
			showMessage(text);

		}
	}

	@Override
	public void onPlayAudioFileFinished(long sessionId, String fileName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPlayVideoFileFinished(long sessionId) {
		// TODO Auto-generated method stub
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void onSendMessageSuccess(long sessionId, long messageId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendMessageFailure(long sessionId, long messageId,
			String reason, int code) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendOutOfDialogMessageSuccess(long messageId,
			String fromDisplayName, String from, String toDisplayName, String to) {
	}

	@Override
	public void onSendOutOfDialogMessageFailure(long messageId,
			String fromDisplayName, String from, String toDisplayName,
			String to, String reason, int code) {
	}

	@Override
	public void onReceivedRTPPacket(long sessionId, boolean isAudio,
			byte[] RTPPacket, int packetSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendingRTPPacket(long sessionId, boolean isAudio,
			byte[] RTPPacket, int packetSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAudioRawCallback(long sessionId, int enum_audioCallbackMode,
			byte[] data, int dataLength, int samplingFreqHz) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoRawCallback(long sessionId, int enum_videoCallbackMode,
			int width, int height, byte[] data, int dataLength) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoDecodedInfoCallback(long sessionId,int width, int height,
										   int framerate, int bitrate){
		Line line = findLineBySessionId(sessionId);
		if(width!=0&&height!=0&&(line.getVideoWidth()!=width||line.getVideoHeight()!=height)){
			line.setVideoWidth(width);
			line.setVideoHeight(height);
			line.notifyObservers();
		}
	}

	void showMessage(String message) {
		OnClickListener listener = null;
		showGlobalDialog(message, null, listener, "Cancel",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
	}

	void showGlobalDialog(String message, String strPositive,
			OnClickListener positiveListener, String strNegative,
			OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		if (positiveListener != null) {
			builder.setPositiveButton(strPositive, positiveListener);
		}

		if (negativeListener != null) {
			builder.setNegativeButton(strNegative, negativeListener);
		}

		AlertDialog ad = builder.create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false);
		ad.show();
	}
	
	public void bringToFront(){
	
    	try {    		
    		 Intent startActivity = new Intent();
             startActivity.setClass(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
             PendingIntent pi = PendingIntent.getActivity(this,0,startActivity,0);
             
             pi.send(this,0,null);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

}

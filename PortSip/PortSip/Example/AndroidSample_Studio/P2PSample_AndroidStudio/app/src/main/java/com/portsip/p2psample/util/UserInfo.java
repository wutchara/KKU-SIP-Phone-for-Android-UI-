package com.portsip.p2psample.util;

import com.portsip.PortSipEnumDefine;

public class UserInfo {
	String mUsername = "";
	String mUserpwd= "";
	String mLocalIp= "";	
	String mUserDisName= "";
	
	int mLocalPort= 5060;	
	int mtransType= PortSipEnumDefine.ENUM_TRANSPORT_UDP;	
	
	public void setUserName(String userName){
		mUsername =userName;
	}
	
	public String getUserName() {
		return mUsername;
	}
	
	public void setUserPwd(String password){
		mUserpwd =password;
	}
	
	public String getUserPassword() {
		return mUserpwd;
	}
	
	public void setLocalIp(String localIp){
		mLocalIp =localIp;
	}
	
	public String getLocalIp() {
		return mLocalIp;
	}
	
	public void setUserDisplayName(String dispalyName){
		mUserDisName =dispalyName;
	}
	
	public String getUserDisplayName() {
		return mUserDisName;
	}
	
	
	public void setLocalPort(int port){
		mLocalPort = port;
	}
	
	public int getLocalPort(){
		return mLocalPort;
	}
	
	public void setTranType(int enum_transType){
		mtransType = enum_transType;
	}
	
	public int getTransType(){
		return mtransType;
	}
	
	
	public boolean isAvailable(){
		
		if (mUsername != null && mUsername.length() > 0 && 
				mUserpwd!= null	&& mUserpwd.length() > 0 &&
				mLocalPort>0&&mLocalPort<65535&& 
				mLocalIp!= null&&mLocalIp.length() > 0)// these fields are required
		{
			return true;
		}
		return false;
	}

}

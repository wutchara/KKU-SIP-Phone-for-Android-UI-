package com.portsip.sipsample.util;

public class SipContact {
	String mSipAddr;
	String mSubDescription = "";
	boolean mSubstatus;// online .offline
	boolean mSubscribed;// weigher send my status to remote subscribe
	boolean mAccept; // weigher rev remote subscribe
	long mSubId;

	public String currentStatusToString() {
		String status = "";
		if (mSubstatus) {
			status += "--online" + mSubDescription;
		} else {
			status += "--offline";
		}
		if (mSubscribed) {
			status += "--Subscribed";
		} else {
			status += "--unSubscribed";
		}
		if (mAccept) {
			status += "--accept";
		} else {
			status += "--reject";
		}
		return status;
	}
	
	public String getSipAddr() {
		return mSipAddr;
	}
	public long getSubId() {
		return mSubId;
	}
	public String getSubDescription() {
		return mSubDescription;
	}
	
	public boolean isAccept() {
		return mAccept;
	}
	public boolean isSubscribed() {
		return mSubscribed;
	}
	public boolean isSubstatus() {
		return mSubstatus;
	}
	
	public void setSipAddr(String SipAddr) {
		this.mSipAddr = SipAddr;
	}
	public void setAccept(boolean Accept) {
		this.mAccept = Accept;
	}
	public void setSubDescription(String SubDescription) {
		this.mSubDescription = SubDescription;
	}
	public void setSubscribed(boolean Subscribed) {
		this.mSubscribed = Subscribed;
	}
	public void setSubId(long SubId) {
		this.mSubId = SubId;
	}
	
	public void setSubstatus(boolean Substatus) {
		this.mSubstatus = Substatus;
	}
}

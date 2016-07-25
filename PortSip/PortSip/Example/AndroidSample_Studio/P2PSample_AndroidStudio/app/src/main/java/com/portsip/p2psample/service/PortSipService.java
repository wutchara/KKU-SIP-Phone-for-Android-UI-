package com.portsip.p2psample.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class PortSipService extends Service {
	static PortSipService instance;

	public PortSipService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder {

		public PortSipService getService() {
			return PortSipService.this;
		}
	}

	private MyBinder myBinder = new MyBinder();
}

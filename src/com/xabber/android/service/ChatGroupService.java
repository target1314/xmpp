package com.xabber.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ChatGroupService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String UNIQUE_STRING = "com.xabber.android.service.sendbroadcast";
				Intent intent = new Intent(UNIQUE_STRING);
				sendBroadcast(intent);
			}
		}).start();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	public static Intent createIntent(Context context) {
		return new Intent(context, ChatGroupService.class);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}

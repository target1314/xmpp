package com.xabber.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xabber.android.ui.PushNewMessage;
import com.xabber.androiddevs.R;

public class XMPPService extends Service {

	private static final String TAG = "XMPPService";
	private static final boolean RECIEVE_FLAG = true;
	private NotificationManager manager;
	private int i = 0;
	Thread xmppThread = null;
	private String from,body;

	@Override
	public void onCreate() {
		Log.i(TAG, "service is created");
		super.onCreate();
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		xmppThread = new Thread(new XMPPThread(mHandler));
		xmppThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		Log.i(TAG, "service is started. thread id is "
				+ Thread.currentThread().getName());
		return super.onStartCommand(intent, flag, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "service is destroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				if (RECIEVE_FLAG) {

					final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message) msg.obj;
					 from = mes.getFrom();
					 body =  mes.getBody();
					 showNotification(R.drawable.logo, "指令通知", System.currentTimeMillis() + "");
				  }
				break;
			}
			default:
				break;
			}
		}
	};

	public static Intent createIntent(Context context) {
		return new Intent(context, XMPPService.class);
	}
	
	public void showNotification(int icon, String tickertext, String title) {
		// Notification管理器
		Notification notification = new Notification(icon, tickertext,System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		notification.defaults |= Notification.DEFAULT_SOUND;	
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		// 让声音、振动无限循环，直到用户响应
		notification.flags |= Notification.FLAG_INSISTENT;
		// 通知被点击后，自动消失
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		Intent intent = new Intent(XMPPService.this,PushNewMessage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FILL_IN_DATA);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("Body", body);
		intent.putExtra("From", from);
		PendingIntent pt = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		// 点击通知后的动作，这里是转回main 这个Acticity
		notification.setLatestEventInfo(this, title, body, pt);
		manager.notify(i, notification);
	 }
	
}

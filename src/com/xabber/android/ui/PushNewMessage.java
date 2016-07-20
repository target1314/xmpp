package com.xabber.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.xabber.android.data.intent.EntityIntentBuilder;
import com.xabber.androiddevs.R;

public class PushNewMessage extends Activity {

	private TextView head_name, subject, newmessage;
	private String body, from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_newmessage_item);
		initViews();
	}

	private void initViews() {
		Intent intent = getIntent();
		body = intent.getStringExtra("Body");
		from = intent.getStringExtra("From");
		head_name = (TextView) findViewById(R.id.head_titile);
		subject = (TextView) findViewById(R.id.subject);
		newmessage = (TextView) findViewById(R.id.newmessage);
		subject.setText(from);
		newmessage.setText(body);

	}

	public static Intent createIntent(Context context) {
		return PushNewMessage.createIntent(context, null, null);
	}

	public static Intent createIntent(Context context, String account,
			String room) {
		return new EntityIntentBuilder(context, PushNewMessage.class)
				.setAccount(account).setUser(room).build();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		head_name.setText("新消息");
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
}

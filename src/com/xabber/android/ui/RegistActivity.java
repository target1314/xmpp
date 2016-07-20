package com.xabber.android.ui;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.core.AsyncTask;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xabber.android.data.account.AccountManager;
import com.xabber.android.data.intent.EntityIntentBuilder;
import com.xabber.androiddevs.R;

public class RegistActivity extends FinalActivity implements OnClickListener {
	@ViewInject(id = R.id.head_titile)
	private TextView head_name;
	@ViewInject(id = R.id.username)
	private EditText userName;
	@ViewInject(id = R.id.password)
	private EditText userpassword;
	@ViewInject(id = R.id.nickame)
	private static EditText nickName;
	@ViewInject(id = R.id.email)
	private static EditText email;
	@ViewInject(id = R.id.login)
	private Button login;
	private String state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist);
		initViews();
	}

	private void initViews() {
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.login:
			if (userName.getText().toString().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "账号不能为空",
						Toast.LENGTH_LONG).show();
			} else if (nickName.getText().toString().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "昵称不能为空",
						Toast.LENGTH_LONG).show();
			} else if (userpassword.getText().toString().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "密码不能为空",
						Toast.LENGTH_LONG).show();
			} else if (email.getText().toString().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "邮箱不能为空",
						Toast.LENGTH_LONG).show();
			} else {
				registUser();
			}
			break;
		default:
			break;
		}
	}

	private void registUser() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				Toast.makeText(getApplicationContext(), "正在注册账号",
						Toast.LENGTH_LONG).show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					state = AccountManager.regist(userName.getText().toString()
							.trim(), userpassword.getText().toString().trim(),
							nickName.getText().toString().trim(), email
									.getText().toString().trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				if (state.equals("1")) {
					Toast.makeText(getApplicationContext(), "注册成功",
							Toast.LENGTH_LONG).show();
					finish();
				} else if (state.equals("2")) {
					Toast.makeText(getApplicationContext(), "注册账号已经存在",
							Toast.LENGTH_LONG).show();
				} else if (state.equals("3")) {
					Toast.makeText(getApplicationContext(), "注册失败",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "服务器连接失败，请检查网络",
							Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		head_name.setText("注册账号");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public static Intent createIntent(Context context) {
		return RegistActivity.createIntent(context, null, null);
	}

	public static Intent createIntent(Context context, String account,
			String room) {
		return new EntityIntentBuilder(context, RegistActivity.class)
				.setAccount(account).setUser(room).build();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

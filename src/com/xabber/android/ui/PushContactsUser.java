package com.xabber.android.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xabber.android.data.connection.ConnectionThread;
import com.xabber.android.data.intent.EntityIntentBuilder;
import com.xabber.android.data.roster.RosterContact;
import com.xabber.android.data.roster.RosterManager;
import com.xabber.android.ui.adapter.SortAdapter;
import com.xabber.android.ui.widget.CharacterParser;
import com.xabber.android.ui.widget.PinyinComparator;
import com.xabber.android.ui.widget.SideBar;
import com.xabber.android.ui.widget.SideBar.OnTouchingLetterChangedListener;
import com.xabber.android.ui.widget.SortModel;
import com.xabber.android.utils.Constant;
import com.xabber.androiddevs.R;

public class PushContactsUser extends Activity implements OnClickListener {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private int statusBarHeight;
	private List<SortModel> chatMsgs = new ArrayList<SortModel>();
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private TextView head_name;
	private Button commint;
	private String address;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_message);
		initData();
		initViews();
	}

	private void initData() {
		for (RosterContact check : RosterManager.getInstance().getContacts()) {
			SortModel sortModel = new SortModel();
			sortModel.setName(check.getName());
			sortModel.setUserc(check.getUser());
			sortModel.setImage(check.getAvatarForContactList());
			chatMsgs.add(sortModel);
		}
	}

	private void initViews() {
		sharedPreferences = getSharedPreferences(Constant.location,
				Context.MODE_PRIVATE);
		address = sharedPreferences.getString("address", "");
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		head_name = (TextView) findViewById(R.id.head_titile);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		commint = (Button) findViewById(R.id.commint);
		sideBar.setTextView(dialog);
		statusBarHeight = getStatusBarHeight(this);
		commint.setOnClickListener(this);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		chatMsgs = filledData(chatMsgs);
		// 根据a-z进行排序源数据
		Collections.sort(chatMsgs, pinyinComparator);
		adapter = new SortAdapter(this, chatMsgs, 1);
		sortListView.setAdapter(adapter);

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<SortModel> sortModels) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < sortModels.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(sortModels.get(i).getName());
			sortModel.setUserc(sortModels.get(i).getUserc());
			sortModel.setImage(sortModels.get(i).getImage());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(sortModels.get(i)
					.getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = chatMsgs;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : chatMsgs) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public static Intent createIntent(Context context) {
		return PushContactsUser.createIntent(context, null, null);
	}

	public static Intent createIntent(Context context, String account,
			String room) {
		return new EntityIntentBuilder(context, PushContactsUser.class)
				.setAccount(account).setUser(room).build();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		head_name.setText("选择用户");
		commint.setVisibility(View.VISIBLE);
		super.onResume();
	}

	/**
	 * 发送广播
	 * 
	 * @param conn
	 */
	private void sendPacket(Connection conn, String user) {
		Message newmsg = new Message();
		newmsg.setTo(user); // 这句很重要，是使用broadcast插件向xueyi-pc域下的所有用户发送
		newmsg.setSubject("当前位置");
		newmsg.setBody(address);
		newmsg.setType(Message.Type.headline);// normal支持离线
		conn.sendPacket(newmsg);
		conn.disconnect();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.commint:
			if (SortAdapter.num == 0) {
				sendPacket(ConnectionThread.getXMPPConnection(),
						SortAdapter.userid);
				finish();
			}
			break;

		default:
			break;
		}
	}
}

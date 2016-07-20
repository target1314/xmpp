/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 * 
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 * 
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package com.xabber.android.ui.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xabber.android.data.SettingsManager;
import com.xabber.android.data.SettingsManager.ChatsDivide;
import com.xabber.android.data.account.AccountItem;
import com.xabber.android.data.account.AccountManager;
import com.xabber.android.data.extension.avatar.AvatarManager;
import com.xabber.android.data.extension.muc.MUCManager;
import com.xabber.android.data.extension.muc.RoomContact;
import com.xabber.android.data.message.ChatAction;
import com.xabber.android.data.message.MessageItem;
import com.xabber.android.data.message.MessageManager;
import com.xabber.android.data.roster.AbstractContact;
import com.xabber.android.data.roster.RosterManager;
import com.xabber.android.utils.Constant;
import com.xabber.android.utils.Emoticons;
import com.xabber.android.utils.SmileUtils;
import com.xabber.android.utils.StringUtils;
import com.xabber.androiddevs.R;

/**
 * Adapter for the list of messages in the chat.
 * 
 * @author alexander.ivanov
 * 
 */
public class ChatMessageAdapter extends BaseAdapter implements UpdatableAdapter {

	private static final int TYPE_MESSAGE = 0;
	private static final int TYPE_HINT = 1;
	private static final int TYPE_EMPTY = 2;
	private LayoutInflater mInflater;
	private final Activity activity;
	private String account;
	private String user;
	private boolean isMUC;
	private List<MessageItem> messages;

	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private AnimationDrawable voiceAnimation = null;
	private static boolean playState = false; // 播放状态

	/**
	 * Message font appearance.
	 */
	private final int appearanceStyle;

	/**
	 * Divider between header and body.
	 */
	private final String divider;

	/**
	 * Text with extra information.
	 */
	private String hint;

	private FinalBitmap fb;

	public ChatMessageAdapter(Activity activity) {
		fb = FinalBitmap.create(activity);
		this.activity = activity;
		messages = Collections.emptyList();
		mInflater = LayoutInflater.from(activity);
		account = null;
		user = null;
		hint = null;
		appearanceStyle = 0;
		ChatsDivide chatsDivide = SettingsManager.chatsDivide();
		if (chatsDivide == ChatsDivide.always
				|| (chatsDivide == ChatsDivide.portial && !activity
						.getResources().getBoolean(R.bool.landscape)))
			divider = "\n";
		else
			divider = " ";
	}

	@Override
	public int getCount() {
		return messages.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position < messages.size())
			return messages.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < messages.size())
			return TYPE_MESSAGE;
		else
			return hint == null ? TYPE_EMPTY : TYPE_HINT;
	}

	private void append(SpannableStringBuilder builder, CharSequence text,
			CharacterStyle span) {
		int start = builder.length();
		builder.append(text);
		builder.setSpan(span, start, start + text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MessageItem messageItem = (MessageItem) getItem(position);
		final int type = getItemViewType(position);
		final View view;
		if (convertView == null) {
			final int resources;
			if (type == TYPE_MESSAGE)
				resources = R.layout.chatting_item_msg_text_left;
			else if (type == TYPE_HINT)
				resources = R.layout.chat_viewer_info;
			else if (type == TYPE_EMPTY)
				resources = R.layout.chat_viewer_empty;
			else
				throw new IllegalStateException();
			view = activity.getLayoutInflater().inflate(resources, parent,
					false);
			if (type == TYPE_MESSAGE)
				((TextView) view.findViewById(R.id.text)).setTextAppearance(
						activity, appearanceStyle);

		} else
			view = convertView;

		if (type == TYPE_EMPTY)
			return view;

		if (type == TYPE_HINT) {
			TextView textView = ((TextView) view.findViewById(R.id.info));
			textView.setText(hint);
			textView.setTextAppearance(activity, R.style.ChatInfo_Warning);
			return view;
		}

		final boolean incoming = messageItem.isIncoming();
		final String name;
		final String account = messageItem.getChat().getAccount();
		final String user = messageItem.getChat().getUser();
		final String resource = messageItem.getResource();
		if (isMUC) {
			name = resource;
		} else {
			if (incoming)
				name = RosterManager.getInstance().getName(account, user);
			else
				name = AccountManager.getInstance().getNickName(account);

		}

		TextView textView = (TextView) view.findViewById(R.id.text);
		TextView message = (TextView) view.findViewById(R.id.message);
		TextView message_name = (TextView) view.findViewById(R.id.message_name);
		TextView tv_sendtime = (TextView) view.findViewById(R.id.tv_sendtime);
		LinearLayout row_rec_location = (LinearLayout) view.findViewById(R.id.row_rec_location);
		LinearLayout row_rec_message = (LinearLayout) view.findViewById(R.id.row_rec_message);
		LinearLayout row_rec_image = (LinearLayout) view.findViewById(R.id.row_rec_image);
		LinearLayout row_rec_voice = (LinearLayout) view.findViewById(R.id.row_rec_voice);
		ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
		final ImageView iv_voice = (ImageView) view.findViewById(R.id.iv_voice);
		ImageView tv_chatcontent_fromimage = (ImageView) view
				.findViewById(R.id.tv_chatcontent_fromimage);
		Spannable text = messageItem.getSpannable();
		Spannable span = null;
		if (text.toString().contains(".message")) {
			String msg = new String(text.toString());
			String[] msg_text = msg.split("\\.");
			span = SmileUtils.getSmiledText(activity, msg_text[0]);
		} else if (text.toString().contains(".location")) {
			String msg = new String(text.toString());
			String[] msg_text = msg.split("\\.");
			span = SmileUtils.getSmiledText(activity, msg_text[0]);
		} else if (text.toString().contains(".jpg")
				|| text.toString().contains(".png")
				|| text.toString().contains(".gif")
				|| text.toString().contains(".jpeg")
				|| text.toString().contains(".9.png")) {
			span = SmileUtils.getSmiledText(activity, text);
		} else if (text.toString().contains(".amr")) {
			span = SmileUtils.getSmiledText(activity, text);
		} else {
			span = SmileUtils.getSmiledText(activity, text);
		}

		ChatAction action = messageItem.getAction();
		String time = StringUtils.getSmartTimeText(messageItem.getTimestamp());
		SpannableStringBuilder builder = new SpannableStringBuilder();
		if (action == null) {
			append(builder, " ", new TextAppearanceSpan(activity,
					R.style.ChatHeader));
			append(builder, " ", new TextAppearanceSpan(activity,
					R.style.ChatHeader));
			append(builder, divider, new TextAppearanceSpan(activity,
					R.style.ChatHeader));
			Date timeStamp = messageItem.getDelayTimestamp();
			if (timeStamp != null) {
				String delay = activity.getString(
						incoming ? R.string.chat_delay : R.string.chat_typed,
						StringUtils.getSmartTimeText(timeStamp));
				append(builder, delay, new TextAppearanceSpan(activity,
						R.style.ChatHeader_Delay));
				append(builder, divider, new TextAppearanceSpan(activity,
						R.style.ChatHeader));
			}
			if (messageItem.isUnencypted()) {
				append(builder,
						activity.getString(R.string.otr_unencrypted_message),
						new TextAppearanceSpan(activity,
								R.style.ChatHeader_Delay));
				append(builder, divider, new TextAppearanceSpan(activity,
						R.style.ChatHeader));
			}
			Emoticons.getSmiledText(activity.getApplication(), span);
			if (messageItem.getTag() == null)
				builder.append(span);
			else
				append(builder, span, new TextAppearanceSpan(activity,
						R.style.ChatRead));
		} else {
			append(builder, "", new TextAppearanceSpan(activity,
					R.style.ChatHeader_Time));
			append(builder, " ", new TextAppearanceSpan(activity,
					R.style.ChatHeader));
			span = Emoticons.newSpannable(action.getText(activity, name,
					span.toString()));
			Emoticons.getSmiledText(activity.getApplication(), span);
			append(builder, span, new TextAppearanceSpan(activity,
					R.style.ChatHeader_Delay));
		}

		if (text.toString().contains(".location")) {
			row_rec_message.setVisibility(View.GONE);
			row_rec_voice.setVisibility(View.GONE);
			row_rec_location.setVisibility(View.VISIBLE);
			row_rec_image.setVisibility(View.GONE);
			textView.setText(builder);
			String str = new String(name);
			String[] msg_name = str.split("@");
			message_name.setText(msg_name[0]);
			tv_sendtime.setText(time);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			textView.setBackgroundResource(R.drawable.location_msg);
		} else if (text.toString().contains(".message")) {
			row_rec_message.setVisibility(View.VISIBLE);
			row_rec_image.setVisibility(View.GONE);
			row_rec_voice.setVisibility(View.GONE);
			row_rec_location.setVisibility(View.GONE);
			message.setText(builder);
			String str = new String(name);
			String[] msg_name = str.split("@");
			message_name.setText(msg_name[0]);
			tv_sendtime.setText(time);
			message.setMovementMethod(LinkMovementMethod.getInstance());
		} else if (text.toString().contains(".jpg")
				|| text.toString().contains(".png")
				|| text.toString().contains(".gif")
				|| text.toString().contains(".jpeg")
				|| text.toString().contains(".9.png")) {
			row_rec_image.setVisibility(View.VISIBLE);
			row_rec_message.setVisibility(View.GONE);
			row_rec_location.setVisibility(View.GONE);
			row_rec_voice.setVisibility(View.GONE);
			fb.display(tv_chatcontent_fromimage, messageItem.getText());
			String str = new String(name);
			String[] msg_name = str.split("@");
			message_name.setText(msg_name[0]);
			tv_sendtime.setText(time);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			tv_chatcontent_fromimage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					initPopWindow(messageItem.getText());
				}
			});
		} else if (text.toString().contains(".amr")) {
			row_rec_image.setVisibility(View.GONE);
			row_rec_message.setVisibility(View.GONE);
			row_rec_location.setVisibility(View.GONE);
			row_rec_voice.setVisibility(View.VISIBLE);
			fb.display(tv_chatcontent_fromimage, messageItem.getText());
			String str = new String(name);
			String[] msg_name = str.split("@");
			message_name.setText(msg_name[0]);
			tv_sendtime.setText(time);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			iv_voice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					playMusic(messageItem.getText(), iv_voice);
				}
			});
		} else {
			row_rec_message.setVisibility(View.VISIBLE);
			row_rec_image.setVisibility(View.GONE);
			row_rec_location.setVisibility(View.GONE);
			row_rec_voice.setVisibility(View.GONE);
			message.setText(builder);
			String str = new String(name);
			String[] msg_name = str.split("@");
			message_name.setText(msg_name[0]);
			tv_sendtime.setText(time);
			message.setMovementMethod(LinkMovementMethod.getInstance());
		}
		if (SettingsManager.chatsShowAvatars()) {
			String str = new String(account);
			String[] str_account = str.split("@");
			avatarView.setVisibility(View.VISIBLE);
			if (!incoming
					|| (isMUC && MUCManager.getInstance()
							.getNickname(account, user)
							.equalsIgnoreCase(resource))) {
				avatarView
						.setImageDrawable(AvatarManager.getInstance()
								.getAccountAvatar(
										str_account[0] + Constant.serviceName));
			} else {
				if (isMUC) {
					if ("".equals(resource)) {
						avatarView.setImageDrawable(AvatarManager.getInstance()
								.getRoomAvatar(user));
					} else {
						avatarView
								.setImageDrawable(AvatarManager
										.getInstance()
										.getUserAvatar(
												resource + Constant.serviceName));
					}
				} else {
					avatarView.setImageDrawable(AvatarManager.getInstance()
							.getUserAvatar(user));
				}
			}
		} else {
			avatarView.setVisibility(View.VISIBLE);
			avatarView.setImageDrawable(AvatarManager.getInstance()
					.getUserAvatar(user));
		}
		return view;
	}

	public String getAccount() {
		return account;
	}

	public String getUser() {
		return user;
	}

	private void playMusic(String name, final ImageView voice) {
		if (!playState) {
			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(name);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				showAnimation(voice);
				playState = true;
				// 设置播放结束时监听
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if (playState) {
							playState = false;
							voice.setImageResource(R.drawable.chatfrom_voice_playing);
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				playState = false;
			} else {
				playState = false;
			}
		}
	}

	private void showAnimation(ImageView voice) {
		// play voice, and start animation
		voice.setImageResource(R.anim.voice_from_icon);
		voiceAnimation = (AnimationDrawable) voice.getDrawable();
		voiceAnimation.start();
	}

	private void initPopWindow(String image) {
		// 加载popupWindow的布局文件
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.popup, null);
		// 声明一个弹出框
		final PopupWindow popupWindow = new PopupWindow(view,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		ImageView imageView = (ImageView) view.findViewById(R.id.logo_b);
		// 显示图片
		fb.display(imageView, image);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	/**
	 * Changes managed chat.
	 * 
	 * @param account
	 * @param user
	 */
	public void setChat(String account, String user) {
		this.account = account;
		this.user = user;
		this.isMUC = MUCManager.getInstance().hasRoom(account, user);
		onChange();
	}

	@Override
	public void onChange() {
		messages = new ArrayList<MessageItem>(MessageManager.getInstance()
				.getMessages(account, user));
		hint = getHint();
		notifyDataSetChanged();
	}

	/**
	 * @return New hint.
	 */
	private String getHint() {
		AccountItem accountItem = AccountManager.getInstance().getAccount(
				account);
		boolean online;
		if (accountItem == null)
			online = false;
		else
			online = accountItem.getState().isConnected();
		final AbstractContact abstractContact = RosterManager.getInstance()
				.getBestContact(account, user);
		if (!online) {
			if (abstractContact instanceof RoomContact)
				return activity.getString(R.string.muc_is_unavailable);
			else
				return activity.getString(R.string.account_is_offline);
		} else if (!abstractContact.getStatusMode().isOnline()) {
			if (abstractContact instanceof RoomContact)
				return activity.getString(R.string.muc_is_unavailable);
			else
				return activity.getString(R.string.contact_is_offline,
						abstractContact.getName());
		}
		return null;
	}

	/**
	 * Contact information has been changed. Renews hint and updates data if
	 * necessary.
	 */
	public void updateInfo() {
		String info = getHint();
		if (this.hint == info || (this.hint != null && this.hint.equals(info)))
			return;
		this.hint = info;
		notifyDataSetChanged();
	}
}

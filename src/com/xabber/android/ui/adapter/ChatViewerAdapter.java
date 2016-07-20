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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.xabber.android.data.LogManager;
import com.xabber.android.data.SettingsManager;
import com.xabber.android.data.SettingsManager.SecurityOtrMode;
import com.xabber.android.data.connection.ConnectionThread;
import com.xabber.android.data.entity.BaseEntity;
import com.xabber.android.data.extension.otr.OTRManager;
import com.xabber.android.data.extension.otr.SecurityLevel;
import com.xabber.android.data.message.AbstractChat;
import com.xabber.android.data.message.MessageManager;
import com.xabber.android.data.message.chat.ChatManager;
import com.xabber.android.data.roster.AbstractContact;
import com.xabber.android.data.roster.RosterManager;
import com.xabber.android.ui.ChatViewer.SendFileThread;
import com.xabber.android.ui.helper.AbstractAvatarInflaterHelper;
import com.xabber.android.ui.helper.ContactTitleInflater;
import com.xabber.android.ui.location.Current_LocationActivity;
import com.xabber.android.ui.widget.PageSwitcher;
import com.xabber.android.utils.AudioRecorder;
import com.xabber.android.utils.Constant;
import com.xabber.android.utils.SmileUtils;
import com.xabber.androiddevs.R;
import com.xabber.xmpp.address.Jid;

/**
 * Adapter for the list of chat pages.
 * 
 * @author alexander.ivanov
 * 
 */
public class ChatViewerAdapter extends BaseAdapter implements SaveStateAdapter,
		UpdatableAdapter {

	private final Activity activity;

	/**
	 * Intent sent while opening chat activity.
	 */
	private final AbstractChat intent;

	/**
	 * Position to insert intent.
	 */
	private final int intentPosition;

	private ArrayList<AbstractChat> activeChats;

	/**
	 * Listener for click on title bar and send button.
	 */
	private OnClickListener onClickListener;

	/**
	 * Listener for key press in edit view.
	 */
	private OnKeyListener onKeyListener;

	/**
	 * Listener for actions in edit view.
	 */
	private OnEditorActionListener onEditorActionListener;

	/**
	 * Listener for context menu in message list.
	 */
	private OnCreateContextMenuListener onCreateContextMenuListener;

	/**
	 * Listen for text to be changed.
	 */
	private OnTextChangedListener onTextChangedListener;

	private final AbstractAvatarInflaterHelper avatarInflaterHelper;

	private final Animation shake;

	private List<String> reslist;

	private InputMethodManager manager;

	private Thread recordThread;
	private Dialog dialog;
	private AudioRecorder mr;
	private ImageView dialog_img;
	private TextView recordingHint;
	private LinearLayout del_re;
	private static int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1
	private static int RECORD_NO = 0; // 不在录音
	private static int RECORD_ING = 1; // 正在录音
	private static int RECODE_ED = 2; // 完成录音
	private static int RECODE_STATE = 0; // 录音的状态
	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	private String voiceName;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 4:
				// 录音超过15秒自动停止
				if (RECODE_STATE == RECORD_ING) {
					RECODE_STATE = RECODE_ED;
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					try {
						mr.stop();
						voiceValue = 0.0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (recodeTime < 1.0) {
						Toast.makeText(activity, "时间太短   录音失败",
								Toast.LENGTH_LONG).show();
						RECODE_STATE = RECORD_NO;
					} else {
					}
				}
				break;
			case 5:
				setDialogImage();
				break;

			default:
				break;
			}
		};
	};

	public ChatViewerAdapter(Activity activity, String account, String user) {
		this.activity = activity;
		avatarInflaterHelper = AbstractAvatarInflaterHelper
				.createAbstractContactInflaterHelper();
		activeChats = new ArrayList<AbstractChat>();
		intent = MessageManager.getInstance().getOrCreateChat(account,
				Jid.getBareAddress(user));
		Collection<? extends BaseEntity> activeChats = MessageManager
				.getInstance().getActiveChats();
		if (activeChats.contains(intent))
			intentPosition = -1;
		else
			intentPosition = activeChats.size();
		onClickListener = null;
		onKeyListener = null;
		onEditorActionListener = null;
		onCreateContextMenuListener = null;
		onTextChangedListener = null;
		shake = AnimationUtils.loadAnimation(activity, R.anim.shake);
		onChange();
		manager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public OnKeyListener getOnKeyListener() {
		return onKeyListener;
	}

	public void setOnKeyListener(OnKeyListener onKeyListener) {
		this.onKeyListener = onKeyListener;
	}

	public OnEditorActionListener getOnEditorActionListener() {
		return onEditorActionListener;
	}

	public void setOnEditorActionListener(
			OnEditorActionListener onEditorActionListener) {
		this.onEditorActionListener = onEditorActionListener;
	}

	public OnCreateContextMenuListener getOnCreateContextMenuListener() {
		return onCreateContextMenuListener;
	}

	public void setOnCreateContextMenuListener(
			OnCreateContextMenuListener onCreateContextMenuListener) {
		this.onCreateContextMenuListener = onCreateContextMenuListener;
	}

	public OnTextChangedListener getOnTextChangedListener() {
		return onTextChangedListener;
	}

	public void setOnTextChangedListener(
			OnTextChangedListener onTextChangedListener) {
		this.onTextChangedListener = onTextChangedListener;
	}

	@Override
	public int getCount() {
		return activeChats.size();
	}

	@Override
	public Object getItem(int position) {
		return activeChats.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final AbstractChat chat = (AbstractChat) getItem(position);
		final ChatViewHolder chatViewHolder;
		if (convertView == null) {
			view = activity.getLayoutInflater().inflate(
					R.layout.chat_viewer_item, parent, false);
			ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(
					activity);
			chatViewHolder = new ChatViewHolder(view, chatMessageAdapter);
			chatViewHolder.list.setAdapter(chatViewHolder.chatMessageAdapter);
			chatViewHolder.send.setOnClickListener(onClickListener);
			chatViewHolder.title.setOnClickListener(onClickListener);
			chatViewHolder.input.setOnKeyListener(onKeyListener);
			chatViewHolder.input
					.setOnEditorActionListener(onEditorActionListener);
			chatViewHolder.iv_emoticons_normal
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							chatViewHolder.more.setVisibility(View.VISIBLE);
							chatViewHolder.iv_emoticons_normal
									.setVisibility(View.INVISIBLE);
							chatViewHolder.iv_emoticons_checked
									.setVisibility(View.VISIBLE);
							chatViewHolder.btnContainer
									.setVisibility(View.GONE);
							chatViewHolder.expressionContainer
									.setVisibility(View.VISIBLE);
							hideKeyboard();
						}
					});
			chatViewHolder.iv_emoticons_checked
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							chatViewHolder.iv_emoticons_normal
									.setVisibility(View.VISIBLE);
							chatViewHolder.iv_emoticons_checked
									.setVisibility(View.INVISIBLE);
							chatViewHolder.btnContainer
									.setVisibility(View.VISIBLE);
							chatViewHolder.expressionContainer
									.setVisibility(View.GONE);
							chatViewHolder.more.setVisibility(View.GONE);
						}
					});
			chatViewHolder.buttonSetModeKeyboard
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							chatViewHolder.edittext_layout
									.setVisibility(View.VISIBLE);
							chatViewHolder.more.setVisibility(View.GONE);
							view.setVisibility(View.GONE);
							chatViewHolder.buttonSetModeVoice
									.setVisibility(View.VISIBLE);
							chatViewHolder.input.requestFocus();
							chatViewHolder.buttonPressToSpeak
									.setVisibility(View.GONE);
						}
					});
			chatViewHolder.buttonSetModeVoice
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							chatViewHolder.edittext_layout
									.setVisibility(View.GONE);
							hideKeyboard();
							chatViewHolder.more.setVisibility(View.GONE);
							view.setVisibility(View.GONE);
							chatViewHolder.buttonSetModeKeyboard
									.setVisibility(View.VISIBLE);
							chatViewHolder.buttonPressToSpeak
									.setVisibility(View.VISIBLE);
							chatViewHolder.iv_emoticons_normal
									.setVisibility(View.VISIBLE);
							chatViewHolder.iv_emoticons_checked
									.setVisibility(View.INVISIBLE);
							chatViewHolder.btnContainer
									.setVisibility(View.VISIBLE);
							chatViewHolder.expressionContainer
									.setVisibility(View.GONE);
						}
					});
			chatViewHolder.open.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (chatViewHolder.more.getVisibility() == View.GONE) {
						hideKeyboard();
						chatViewHolder.more.setVisibility(View.VISIBLE);
						chatViewHolder.btnContainer.setVisibility(View.VISIBLE);
						chatViewHolder.expressionContainer
								.setVisibility(View.GONE);
					} else {
						if (chatViewHolder.expressionContainer.getVisibility() == View.VISIBLE) {
							chatViewHolder.expressionContainer
									.setVisibility(View.GONE);
							chatViewHolder.btnContainer
									.setVisibility(View.VISIBLE);
							chatViewHolder.iv_emoticons_normal
									.setVisibility(View.VISIBLE);
							chatViewHolder.iv_emoticons_checked
									.setVisibility(View.INVISIBLE);
						} else {
							chatViewHolder.more.setVisibility(View.GONE);
						}
					}
				}
			});
			chatViewHolder.input.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (count > 0) {
						chatViewHolder.send.setVisibility(View.VISIBLE);
						chatViewHolder.buttonSetModeVoice
								.setVisibility(View.GONE);
					} else {
						chatViewHolder.send.setVisibility(View.GONE);
						chatViewHolder.buttonSetModeVoice
								.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (onTextChangedListener != null)
						onTextChangedListener.onTextChanged(
								chatViewHolder.input, s);
				}

			});

			chatViewHolder.list.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideKeyboard();
					chatViewHolder.more.setVisibility(View.GONE);
					chatViewHolder.iv_emoticons_normal
							.setVisibility(View.VISIBLE);
					chatViewHolder.iv_emoticons_checked
							.setVisibility(View.INVISIBLE);
					chatViewHolder.expressionContainer.setVisibility(View.GONE);
					chatViewHolder.btnContainer.setVisibility(View.GONE);
					return false;
				}
			});
			chatViewHolder.btn_picture
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intents;
							if (Build.VERSION.SDK_INT < 19) {
								intents = new Intent(Intent.ACTION_GET_CONTENT);
								intents.setType("image/*");

							} else {
								intents = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							}
							activity.startActivityForResult(intents,
									Constant.USERPHOTO);
						}
					});
			chatViewHolder.btn_take_picture
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							File dir = new File(Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/Camera/");// 设置临时文件的存放目录
							if (!dir.exists()) {
								dir.mkdir();
							}
							Intent intent = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							File f = new File(dir, Constant.file);
							Uri u = Uri.fromFile(f);
							intent.putExtra(
									MediaStore.Images.Media.ORIENTATION, 0);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
							activity.startActivityForResult(intent,
									Constant.CAMEPHOTO);
						}
					});
			chatViewHolder.btn_location
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							activity.startActivityForResult(new Intent(
									activity, Current_LocationActivity.class),
									Constant.CHATPEOPLE);
						}
					});
			// 录音
			chatViewHolder.send_speak.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						chatViewHolder.speak
								.setText(R.string.button_pushtocanle);
						if (RECODE_STATE != RECORD_ING) {
							long startVoice = SystemClock
									.currentThreadTimeMillis();
							voiceName = String.valueOf(startVoice);
							mr = new AudioRecorder(activity, voiceName);
							RECODE_STATE = RECORD_ING;
							showVoiceDialog();
							try {
								mr.start();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mythread();
						}

						break;

					case MotionEvent.ACTION_MOVE: {
						if (event.getY() < 0) {
							recordingHint.setText(R.string.release_to_cancel);
						} else {
							recordingHint.setText(R.string.move_up_to_cancel);
							recordingHint.setBackgroundColor(Color.TRANSPARENT);
						}
						return true;
					}
					case MotionEvent.ACTION_UP:
						chatViewHolder.speak
								.setText(R.string.button_pushtotalk);
						if (RECODE_STATE == RECORD_ING) {
							RECODE_STATE = RECODE_ED;
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							try {
								mr.stop();
								voiceValue = 0.0;
							} catch (IOException e) {
								e.printStackTrace();
							}

							if (recodeTime < MIX_TIME) {
								Toast.makeText(activity, "时间太短   录音失败",
										Toast.LENGTH_LONG).show();
								RECODE_STATE = RECORD_NO;
							} else if (event.getY() < 0) {
								dialog.dismiss();
							} else {
								SendFileThread sendFileThread = new SendFileThread(
										chatViewHolder.chatMessageAdapter
												.getUser(),
										getAmrPath(activity), ConnectionThread
												.getXMPPConnection(), activity);
								sendFileThread.start();
								chatViewHolder.input.setText("");
							}
						}
						break;
					}
					return false;
				}
			});
			chatViewHolder.list
					.setOnCreateContextMenuListener(onCreateContextMenuListener);
			view.setTag(chatViewHolder);
		} else {
			view = convertView;
			chatViewHolder = (ChatViewHolder) view.getTag();
		}
		final String account = chat.getAccount();
		final String user = chat.getUser();
		final AbstractContact abstractContact = RosterManager.getInstance()
				.getBestContact(account, user);

		if (chat.equals(chatViewHolder.chatMessageAdapter.getAccount(),
				chatViewHolder.chatMessageAdapter.getUser())) {
			chatViewHolder.chatMessageAdapter.updateInfo();
		} else {
			if (chatViewHolder.chatMessageAdapter.getAccount() != null
					&& chatViewHolder.chatMessageAdapter.getUser() != null)
				saveState(view);
			expression(view);
			if (PageSwitcher.LOG)
				LogManager.i(this, "Load " + view + " for "
						+ chatViewHolder.chatMessageAdapter.getUser() + " in "
						+ chatViewHolder.chatMessageAdapter.getAccount());
			OnTextChangedListener temp = onTextChangedListener;
			onTextChangedListener = null;
			chatViewHolder.input.setText(ChatManager.getInstance()
					.getTypedMessage(account, user));
			chatViewHolder.input.setSelection(ChatManager.getInstance()
					.getSelectionStart(account, user), ChatManager
					.getInstance().getSelectionEnd(account, user));
			onTextChangedListener = temp;
			chatViewHolder.chatMessageAdapter.setChat(account, user);
			chatViewHolder.list.setAdapter(chatViewHolder.list.getAdapter());
		}

		chatViewHolder.page.setText(activity.getString(R.string.chat_page,
				position + 1, getCount()));
		ContactTitleInflater.updateTitle(chatViewHolder.title, activity,
				abstractContact);
		avatarInflaterHelper.updateAvatar(chatViewHolder.avatar,
				abstractContact);
		SecurityLevel securityLevel = OTRManager.getInstance()
				.getSecurityLevel(chat.getAccount(), chat.getUser());
		SecurityOtrMode securityOtrMode = SettingsManager.securityOtrMode();
		if (securityLevel == SecurityLevel.plain
				&& (securityOtrMode == SecurityOtrMode.disabled || securityOtrMode == SecurityOtrMode.manual)) {
			chatViewHolder.security.setVisibility(View.GONE);
		} else {
			chatViewHolder.security.setVisibility(View.VISIBLE);
			chatViewHolder.security
					.setImageLevel(securityLevel.getImageLevel());
		}
		return view;
	}

	@Override
	public void saveState(View view) {
		ChatViewHolder chatViewHolder = (ChatViewHolder) view.getTag();
		if (PageSwitcher.LOG)
			LogManager.i(this, "Save " + view + " for "
					+ chatViewHolder.chatMessageAdapter.getUser() + " in "
					+ chatViewHolder.chatMessageAdapter.getAccount());
		ChatManager.getInstance().setTyped(
				chatViewHolder.chatMessageAdapter.getAccount(),
				chatViewHolder.chatMessageAdapter.getUser(),
				chatViewHolder.input.getText().toString(),
				chatViewHolder.input.getSelectionStart(),
				chatViewHolder.input.getSelectionEnd());
	}

	/**
	 * Must be called on changes in chat (message sent, received, etc.).
	 */
	public void onChatChange(View view, boolean incomingMessage) {
		ChatViewHolder holder = (ChatViewHolder) view.getTag();
		if (incomingMessage)
			holder.nameHolder.startAnimation(shake);
		holder.chatMessageAdapter.onChange();
	}

	@Override
	public void onChange() {
		activeChats = new ArrayList<AbstractChat>(MessageManager.getInstance()
				.getActiveChats());
		if (intentPosition != -1) {
			int index = activeChats.indexOf(intent);
			AbstractChat chat;
			if (index == -1)
				chat = intent;
			else
				chat = activeChats.remove(index);
			activeChats.add(Math.min(intentPosition, activeChats.size()), chat);
		}
		notifyDataSetChanged();
	}

	// 录音计时线程
	void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// 录音时显示Dialog
	void showVoiceDialog() {
		dialog = new Dialog(activity, R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.chat_voice_dialog);
		dialog_img = (ImageView) dialog.findViewById(R.id.dialog_img);
		del_re = (LinearLayout) dialog.findViewById(R.id.del_re);
		recordingHint = (TextView) dialog.findViewById(R.id.recording_hint);
		del_re.setVisibility(View.VISIBLE);
		dialog.show();
	}

	// 获取文件手机路径
	private String getAmrPath(Context context) {
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ context.getPackageName() + "/" + "sendVoice" + "/"
				+ voiceName + ".amr");
		return file.getAbsolutePath();
	}

	// 录音线程
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					handler.sendEmptyMessage(4);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue = mr.getAmplitude();
							handler.sendEmptyMessage(5);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.amp1);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.amp2);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.amp3);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.amp4);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.amp5);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.amp6);
		} else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.amp7);
		}
	}

	private static class ChatViewHolder {

		final TextView page;
		final View title;
		final View nameHolder;
		final ImageView avatar;
		final ImageView security;
		final View send;
		final EditText input;
		final ListView list;
		final ImageView iv_emoticons_normal;
		final ImageView iv_emoticons_checked;
		final ImageView btn_take_picture;
		final ImageView btn_picture;
		final ImageView btn_location;
		final LinearLayout expressionContainer;
		final LinearLayout btnContainer;
		final View buttonSetModeVoice;
		final RelativeLayout edittext_layout;
		final View buttonPressToSpeak;
		final ViewPager expressionViewpager;
		final View buttonSetModeKeyboard;
		final View send_speak, open;
		final Button speak;
		final View more;
		final ChatMessageAdapter chatMessageAdapter;

		public ChatViewHolder(View view, ChatMessageAdapter chatMessageAdapter) {
			page = (TextView) view.findViewById(R.id.chat_page);
			title = view.findViewById(R.id.title);
			nameHolder = title.findViewById(R.id.name_holder);
			avatar = (ImageView) title.findViewById(R.id.avatar);
			security = (ImageView) title.findViewById(R.id.security);
			send = view.findViewById(R.id.chat_send);
			input = (EditText) view.findViewById(R.id.chat_input);
			list = (ListView) view.findViewById(android.R.id.list);
			expressionViewpager = (ViewPager) view.findViewById(R.id.vPager);
			expressionContainer = (LinearLayout) view
					.findViewById(R.id.ll_face_container);
			btnContainer = (LinearLayout) view
					.findViewById(R.id.ll_btn_container);
			iv_emoticons_normal = (ImageView) view
					.findViewById(R.id.iv_emoticons_normal);
			iv_emoticons_checked = (ImageView) view
					.findViewById(R.id.iv_emoticons_checked);
			speak = (Button) view.findViewById(R.id.send_speak);
			btn_location = (ImageView) view.findViewById(R.id.btn_location);
			btn_take_picture = (ImageView) view
					.findViewById(R.id.btn_take_picture);
			btn_picture = (ImageView) view.findViewById(R.id.btn_picture);
			send_speak = view.findViewById(R.id.send_speak);
			open = view.findViewById(R.id.open);
			edittext_layout = (RelativeLayout) view
					.findViewById(R.id.edittext_layout);
			buttonSetModeVoice = view.findViewById(R.id.btn_set_mode_voice);
			buttonPressToSpeak = view.findViewById(R.id.btn_press_to_speak);
			buttonSetModeKeyboard = view
					.findViewById(R.id.btn_set_mode_keyboard);
			more = view.findViewById(R.id.more);
			this.chatMessageAdapter = chatMessageAdapter;
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (activity.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void expression(View view) {
		ChatViewHolder chatViewHolder = (ChatViewHolder) view.getTag();
		// 表情list
		reslist = getExpressionRes(105);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1, view);
		View gv2 = getGridChildView(2, view);
		View gv3 = getGridChildView(3, view);
		View gv4 = getGridChildView(4, view);
		View gv5 = getGridChildView(5, view);
		views.add(gv1);
		views.add(gv2);
		views.add(gv3);
		views.add(gv4);
		views.add(gv5);
		chatViewHolder.expressionViewpager
				.setAdapter(new ExpressionPagerAdapter(views));
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i, View views) {
		final ChatViewHolder chatViewHolder = (ChatViewHolder) views.getTag();
		View view = View.inflate(activity, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 21);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(21, 42));
		} else if (i == 3) {
			list.addAll(reslist.subList(42, 63));
		} else if (i == 4) {
			list.addAll(reslist.subList(63, 84));
		} else if (i == 5) {
			list.addAll(reslist.subList(84, reslist.size()));
		} else {
			list.add("delete_expression");
		}
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(
				activity, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					if (filename != "delete_expression") { // 不是删除键，显示表情
						// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
						Class clz = Class
								.forName("com.xabber.android.utils.SmileUtils");
						Field field = clz.getField(filename);
						chatViewHolder.input.append(SmileUtils.getSmiledText(
								activity, (String) field.get(null)));
					} else { // 删除文字或者表情
						if (!TextUtils.isEmpty(chatViewHolder.input.getText())) {

							int selectionStart = chatViewHolder.input
									.getSelectionStart();// 获取光标的位置
							if (selectionStart > 0) {
								String body = chatViewHolder.input.getText()
										.toString();
								String tempStr = body.substring(0,
										selectionStart);
								int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
								if (i != -1) {
									CharSequence cs = tempStr.substring(i,
											selectionStart);
									if (SmileUtils.containsKey(cs.toString()))
										chatViewHolder.input.getEditableText()
												.delete(i, selectionStart);
									else
										chatViewHolder.input.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
								} else {
									chatViewHolder.input.getEditableText()
											.delete(selectionStart - 1,
													selectionStart);
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);
		}
		return reslist;
	}
}

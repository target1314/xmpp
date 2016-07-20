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

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.xabber.android.data.SettingsManager;
import com.xabber.android.data.roster.AbstractContact;
import com.xabber.android.ui.helper.AbstractAvatarInflaterHelper;
import com.xabber.android.utils.SmileUtils;
import com.xabber.androiddevs.R;

/**
 * Provides views and fills them with data for {@link BaseContactAdapter}.
 * 
 * @author alexander.ivanov
 * 
 */
public abstract class BaseContactInflater {

	final Activity activity;

	final LayoutInflater layoutInflater;

	final AbstractAvatarInflaterHelper avatarInflaterHelper;

	/**
	 * Managed adapter.
	 */
	BaseAdapter adapter;

	public BaseContactInflater(Activity activity) {
		this.activity = activity;
		layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		avatarInflaterHelper = AbstractAvatarInflaterHelper
				.createAbstractContactInflaterHelper();
	}

	/**
	 * Sets managed adapter.
	 * 
	 * @param adapter
	 */
	void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Creates new view for specified position.
	 * 
	 * @param position
	 * @param parent
	 * @return
	 */
	abstract View createView(int position, ViewGroup parent);

	/**
	 * Creates new instance of ViewHolder.
	 * 
	 * @param position
	 * @param view
	 * @return
	 */
	abstract ViewHolder createViewHolder(int position, View view);

	/**
	 * Returns status text.
	 * 
	 * @param abstractContact
	 * @return
	 */
	String getStatusText(AbstractContact abstractContact) {
		return abstractContact.getStatusText();
	}

	/**
	 * Fills view for {@link BaseContactAdapter}.
	 * 
	 * @param view
	 *            view to be inflated.
	 * @param abstractContact
	 *            contact to be shown.
	 */
	public void getView(View view, AbstractContact abstractContact) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		if (SettingsManager.contactsShowAvatars()) {
			viewHolder.avatar.setVisibility(View.VISIBLE);
			viewHolder.avatar.setImageDrawable(abstractContact
					.getAvatarForContactList());
			avatarInflaterHelper.updateAvatar(viewHolder.avatar,
					abstractContact);
			((RelativeLayout.LayoutParams) viewHolder.panel.getLayoutParams())
					.addRule(RelativeLayout.RIGHT_OF, R.id.avatar);
		} else {
			viewHolder.avatar.setVisibility(View.GONE);
			((RelativeLayout.LayoutParams) viewHolder.panel.getLayoutParams())
					.addRule(RelativeLayout.RIGHT_OF, R.id.color);
		}
		String name_title = new String(abstractContact.getName());
		String[] titileName = name_title.split("@");
		viewHolder.name.setText(titileName[0]);
		final String statusText = getStatusText(abstractContact);
		if ("".equals(statusText)) {
			viewHolder.name.getLayoutParams().height = activity.getResources()
					.getDimensionPixelSize(
							R.dimen.contact_name_height_hide_status);
			viewHolder.name.setGravity(Gravity.CENTER_VERTICAL);
			viewHolder.status.setVisibility(View.GONE);
		} else {
			viewHolder.name.getLayoutParams().height = activity.getResources()
					.getDimensionPixelSize(
							R.dimen.contact_name_height_show_status);
			viewHolder.name.setGravity(Gravity.BOTTOM);
			if (statusText.contains(".message")) {
				String str = new String(statusText);
				String[] message = str.split("\\.");
				Spannable span = SmileUtils.getSmiledText(activity, message[0]);
				viewHolder.status.setText(span, BufferType.SPANNABLE);
				viewHolder.status.setVisibility(View.VISIBLE);
			} else if (statusText.contains(".location")) {
				viewHolder.status.setText("[位置]");
				viewHolder.status.setVisibility(View.VISIBLE);
			} else if (statusText.contains(".amr")) {
				viewHolder.status.setText("[语音]");
				viewHolder.status.setVisibility(View.VISIBLE);
			} else if (statusText.contains(".jpg")
					|| statusText.contains(".png")
					|| statusText.contains(".gif")
					|| statusText.contains(".jpeg")) {
				viewHolder.status.setText("[图片]");
				viewHolder.status.setVisibility(View.VISIBLE);
			} else {
				Spannable span = SmileUtils.getSmiledText(activity, statusText);
				viewHolder.status.setText(span, BufferType.SPANNABLE);
				viewHolder.status.setVisibility(View.VISIBLE);
			}
		}

	}

	/**
	 * H older for views in contact item.
	 */
	static class ViewHolder {

		final ImageView avatar;
		final RelativeLayout panel;
		final TextView name;
		final TextView status;


		public ViewHolder(View view) {
			avatar = (ImageView) view.findViewById(R.id.avatar);
			panel = (RelativeLayout) view.findViewById(R.id.panel);
			name = (TextView) view.findViewById(R.id.name);
			status = (TextView) view.findViewById(R.id.status);
		}
	}
}

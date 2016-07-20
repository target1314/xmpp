package com.xabber.android.ui.adapter;

import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xabber.android.data.extension.avatar.AvatarManager;
import com.xabber.android.ui.device.Personal_Center;
import com.xabber.android.utils.Constant;
import com.xabber.androiddevs.R;

public class MeAdapter extends BaseAdapter {

	private Context context;
	private List<Personal_Center> personal_Centers;

	public MeAdapter(Context context, List<Personal_Center> personal_Centers) {
		this.context = context;
		this.personal_Centers = personal_Centers;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (personal_Centers != null) {
			return personal_Centers.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return personal_Centers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (arg1 == null) {
			viewHolder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.me_item, null);
			viewHolder.equipment_name = (TextView) arg1
					.findViewById(R.id.equipment_name);
			viewHolder.equipment_location = (TextView) arg1
					.findViewById(R.id.equipment_location);
			viewHolder.user_name = (TextView) arg1.findViewById(R.id.user_name);
			viewHolder.deviceimage = (ImageView) arg1
					.findViewById(R.id.deviceimage);
			viewHolder.user_name.setText(personal_Centers.get(arg0).getUid());
			viewHolder.equipment_name.setText(personal_Centers.get(arg0)
					.getUname());
 			viewHolder.equipment_location.setText(personal_Centers.get(arg0)
					.getDistance() + "米");
			viewHolder.deviceimage.setImageDrawable(AvatarManager.getInstance()
					.getUserAvatarForContactList(
							personal_Centers.get(arg0).getUid()
									+ Constant.serviceName));
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		return arg1;
	}

	   
	final static class ViewHolder {
		TextView equipment_name, equipment_location, user_name;
		ImageView deviceimage;
	}
}

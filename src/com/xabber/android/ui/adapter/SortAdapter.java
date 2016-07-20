package com.xabber.android.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xabber.android.ui.widget.SortModel;
import com.xabber.androiddevs.R;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	
	private List<SortModel> list = null;
	private Context mContext;
	public static int num;
	private int state;
	public static String userid;
	
	public SortAdapter(Context mContext, List<SortModel> list,int state) {
		this.mContext = mContext;
		this.list = list;
		this.state = state;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		final SortModel mContent = list.get(position);
		view = LayoutInflater.from(mContext).inflate(R.layout.contacts_message_item, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.title);
		TextView tvLetter = (TextView) view.findViewById(R.id.catalog);
		ImageView contacts_image=(ImageView)view.findViewById(R.id.contacts_image);
		final CheckBox push_checkBox=(CheckBox)view.findViewById(R.id.push_checkBox);
		String name = new String(mContent.getName());
		String[] str_name = name.split("@");
		tvTitle.setText(str_name[0]);
		contacts_image.setImageDrawable(mContent.getImage());
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			tvLetter.setVisibility(View.VISIBLE);
			tvLetter.setText(mContent.getSortLetters());
		}else{
			tvLetter.setVisibility(View.GONE);
		}
		if (state == 0) {
			push_checkBox.setVisibility(View.GONE);
		}else if (state == 1) {
			push_checkBox.setVisibility(View.VISIBLE);
		}
		push_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
            @Override
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	userid = mContent.getUserc();
                	num = 0; 
                }else{ 
                	num = 1; 
                } 
            } 
        }); 
		return view;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	public Object[] getSections() {
		return null;
	}
}
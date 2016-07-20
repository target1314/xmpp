package com.xabber.android.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xabber.android.ui.adapter.MeAdapter;
import com.xabber.android.ui.device.DeviceHandler;
import com.xabber.android.ui.device.DeviceOverlay;
import com.xabber.android.ui.device.Personal_Center;
import com.xabber.android.ui.helper.ManagedActivity;
import com.xabber.androiddevs.R;

public class Devlice_NearList extends ManagedActivity {

	private ListView devicelistView;
	private List<Personal_Center> personal_Centers = new ArrayList<Personal_Center>();
	private DeviceHandler mStringHandler;
	private String listdata = "";
	private TextView titleName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devlice_near);
		Intent intent = getIntent();
		listdata = intent.getStringExtra("list");
		initDataDeviceList();
		if (!(listdata.equals(""))) {
			getNearDataXml(listdata);
		}else {
			finish();
		}
	}

	/**
	 * 初始化
	 */
	private void initDataDeviceList() {
		devicelistView = (ListView) findViewById(R.id.melist);
		titleName = (TextView)findViewById(R.id.head_titile);
		devicelistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Devlice_NearList.this,
						DeviceOverlay.class);
				intent.putExtra("distance", personal_Centers.get(arg2)
						.getDistance());
				intent.putExtra("uid", personal_Centers.get(arg2).getUid());
				intent.putExtra("uname", personal_Centers.get(arg2).getUname());
				startActivity(intent);
			}
		});
	}

	/**
	 * 解析设备信息
	 * 
	 * @param xml
	 */
	private void getNearDataXml(final String xml) {
		new AsyncTask<Void, Void, List<Personal_Center>>() {

			@Override
			protected void onPreExecute() {

				super.onPreExecute();
			}

			@Override
			protected List<Personal_Center> doInBackground(Void... params) {
				// 创建一个新的字符串
				StringReader read = new StringReader(xml);
				// 创建输入源 使用 InputSource 对象来确定读取XML
				InputSource mInputSource = new InputSource(read);
				SAXParserFactory msSaxParserFactory = SAXParserFactory
						.newInstance();
				SAXParser msSaxParser;
				try {
					msSaxParser = msSaxParserFactory.newSAXParser();
					XMLReader msXmlReader = msSaxParser.getXMLReader();
					mStringHandler = new DeviceHandler();
					msXmlReader.setContentHandler(mStringHandler);
					msXmlReader.parse(mInputSource);
					personal_Centers = mStringHandler.getParsedData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return personal_Centers;
			}

			@Override
			protected void onPostExecute(List<Personal_Center> result) {
				// TODO Auto-generated method stub
				MeAdapter meAdapter = new MeAdapter(Devlice_NearList.this,
						personal_Centers);
				devicelistView.setAdapter(meAdapter);
				meAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		titleName.setText("附近的人");
		super.onResume();
	}
}

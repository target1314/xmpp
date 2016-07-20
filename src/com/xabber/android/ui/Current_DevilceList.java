package com.xabber.android.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.xabber.android.data.intent.EntityIntentBuilder;
import com.xabber.android.ui.device.NoBoringActionBarActivity;
import com.xabber.android.utils.Constant;
import com.xabber.androiddevs.R;

public class Current_DevilceList extends Activity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener, OnInfoWindowClickListener,
		OnMapLoadedListener, InfoWindowAdapter {

	private TextView head_name;
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private UiSettings mUiSettings;
	private String address;
	private double lat, lon;
	private Marker locationMarker;
	private SharedPreferences sharedPreferences;
	private String latitude, longitude, current_address;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_poi_list);
		head_name = (TextView) findViewById(R.id.head_titile);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		intentData();
		init();
	}

	private void intentData() {
		sharedPreferences = getSharedPreferences(Constant.location,
				Context.MODE_PRIVATE);
		latitude = sharedPreferences.getString("lat", "");
		longitude = sharedPreferences.getString("lon", "");
		current_address = sharedPreferences.getString("address", "");
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			setUpMap();
		}
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		mUiSettings.setCompassEnabled(true);
		mUiSettings.setScaleControlsEnabled(true);
		if (latitude.equals("") || longitude.equals("")) {
			latitude = "0.0";
			longitude = "0.0";
		}
		locationMarker = aMap.addMarker(new MarkerOptions()
				.anchor(0.5f, 1)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
				.position(
						new LatLng(Double.valueOf(latitude), Double
								.valueOf(longitude))).title(current_address));
		locationMarker.showInfoWindow();
		addMarkersToMap();// 往地图上添加marker
	}

	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {
		// 22.533392 114.027481
		LatLng ZHENGZHOU0 = new LatLng((Double.valueOf(latitude) + 0.00012),
				(Double.valueOf(longitude) + 0.00014));
		LatLng ZHENGZHOU1 = new LatLng((Double.valueOf(latitude) + 0.00312),
				(Double.valueOf(longitude) + 0.00313));
		LatLng ZHENGZHOU2 = new LatLng((Double.valueOf(latitude) + 0.00211),
				(Double.valueOf(longitude) + 0.00214));
		LatLng ZHENGZHOU3 = new LatLng((Double.valueOf(latitude) + 0.00412),
				(Double.valueOf(longitude) + 0.00514));

		// 动画效果
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(ZHENGZHOU0).title(current_address + "附近")
				.icons(giflist).draggable(true).period(10));
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(ZHENGZHOU1).title(current_address + "附近")
				.icons(giflist).draggable(true).period(10));
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(ZHENGZHOU2).title(current_address + "附近")
				.icons(giflist).draggable(true).period(10));
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(ZHENGZHOU3).title(current_address + "附近")
				.icons(giflist).draggable(true).period(10));
	}

	/**
	 * 监听点击infowindow窗口事件回调
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(Current_DevilceList.this,
				NoBoringActionBarActivity.class);
		startActivity(intent);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		head_name.setText("地图定位");
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			address = aLocation.getAddress();
			lat = aLocation.getLatitude();
			lon = aLocation.getLongitude();
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public static Intent createIntent(Context context) {
		return Current_DevilceList.createIntent(context, null, null);
	}

	public static Intent createIntent(Context context, String account,
			String room) {
		return new EntityIntentBuilder(context, Current_DevilceList.class)
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
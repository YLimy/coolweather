package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.HttpUtil.HttpCallBackListener;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	/** 用于显示城市名 */
	private TextView cityNameText;
	/** 用于显示发布时间 */
	private TextView publishText;
	/** 用于显示天气描述信息 */
	private TextView weatherDespText;
	/** 用于显示气温1 */
	private TextView temp1Text;
	/** 用于显示气温2 */
	private TextView temp2Text;
	/** 用于显示当前日期 */
	private TextView currentDateText;

	/** 切换城市 */
	private Button switchCity;
	/** 更新天气 */
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		currentDateText = (TextView) findViewById(R.id.current_date);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);

		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);

		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);

			queryWeatherCode(countyCode);
		} else {
			showWeather();
		}
	}

	/**
	 * 查询县级代号对应的天气代号
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = getResources().getString(R.string.url_weather_home)
				+ "/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 查询天气代号对应的天气
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = getResources().getString(R.string.url_weather_home)
				+ "/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");

	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * 
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			@Override
			public void onFinished(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代码
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);

						}
					}
				} else if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				publishText.setText("同步失败");

			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(sp.getString("city_name", ""));
		temp1Text.setText(sp.getString("temp1", ""));
		temp2Text.setText(sp.getString("temp2", ""));
		weatherDespText.setText(sp.getString("weather_desp", ""));
		publishText.setText("今天" + sp.getString("publish_time", "") + "发布");
		currentDateText.setText(sp.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = sp.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
}

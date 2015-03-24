package com.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.app.R;
import com.coolweather.app.receiver.AutoUpdateReceivcer;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.HttpUtil.HttpCallBackListener;
import com.coolweather.app.util.Utility;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//启动时，访问一次天气情况
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		
		//添加定时器，延迟发送广播
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 * 60 *60 * 1000;// 8个小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
		Intent startReceiverIntent = new Intent(this, AutoUpdateReceivcer.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, startReceiverIntent, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气信息
	 */
	private void updateWeather() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = sp.getString("weather_code", "");
		String address = getResources().getString(R.string.url_weather_home)
				+ "/data/cityinfo/" + weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinished(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
}

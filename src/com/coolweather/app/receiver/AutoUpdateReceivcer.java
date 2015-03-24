package com.coolweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coolweather.app.service.AutoUpdateService;

public class AutoUpdateReceivcer extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		//接到消息后启动服务
		Intent startServiceIntent = new Intent(context, AutoUpdateService.class);
		context.startActivity(startServiceIntent);
	}
	
}

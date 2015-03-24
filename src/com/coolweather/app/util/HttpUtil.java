package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	/**
	 * 发送网络请求
	 * 
	 * @param path
	 * @param listener
	 */
	public static void sendHttpRequest(final String path,
			final HttpCallBackListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(path);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinished(response.toString());
					}
				} catch (Exception e) {
					
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
				
			}
		}).start();

	}

	/**
	 * 网络访问工具回调接口
	 * 
	 * @author CaiMeng
	 * 
	 */
	public interface HttpCallBackListener {

		/**
		 * 处理服务器端返回的数据
		 * 
		 * @param response
		 *            一般为json/xml
		 */
		void onFinished(String response);

		void onError(Exception e);
	}
}

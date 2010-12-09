package com.toycode.wifistate;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class WiFiStateWidgetProvider extends AppWidgetProvider {

	/**
	 * android.appwidget.action.APPWIDGET_UPDATE で呼び出される
	 * AppWidgetProvider では最低このメソッドを記述しないといけない
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d("WiFiStateWidgetProvider:onUpdate","A");
		for( int appWidgetId : appWidgetIds ){
			Log.d("WiFiStateWidgetProvider:onUpdate","appWidgetId=" + appWidgetId);
			RemoteViews remoteViews 
				= new RemoteViews( context.getPackageName(), R.layout.state_widget);
			remoteViews.setTextViewText(R.id.InfoTextView, getNetInfoText(context));
			appWidgetManager.updateAppWidget( appWidgetId, remoteViews);
		}
		Log.d("WiFiStateWidgetProvider:onUpdate","B");
	}

	/**
	 * ブロードキャストを受信したときに呼ばれる
	 * 今回は NETWORK_STATE_CHANGED_ACTION CONNECTIVITY_ACTION に
	 * 対応して文字列を設定している。
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("WiFiStateWidgetProvider:onReceive:", intent.toString());
		String action = intent.getAction();
		if( action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) ||
				action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			RemoteViews remoteViews 
				= new RemoteViews( context.getPackageName(), R.layout.state_widget);
			remoteViews.setTextViewText(R.id.InfoTextView, getNetInfoText(context));
			ComponentName thisWidget 
				= new ComponentName(context, WiFiStateWidgetProvider.class);
			AppWidgetManager.getInstance(context.getApplicationContext()).updateAppWidget(thisWidget, remoteViews);
				Log.d("WiFiStateWidgetProvider:onReceive:", thisWidget.toShortString());
				
		}else{
			super.onReceive( context, intent);
		}
	}
	
	/**
	 * モバイル(電話回線) ネットワークを表す文字列を返す
	 */
	private String getMbileNetInfoText()
	{
		return "[Mobile]\n" + getInetAddressText();
	}
	
	/**
	 * Wifi ネットワークの情報を表す文字列を返す
	 */
	private String getWifiNetInfoText(Context context)
	{
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if( wifiManager.isWifiEnabled()){
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			return wifiInfo.getSSID() + "\n" 
							+  Formatter.formatIpAddress(wifiInfo.getIpAddress());
		}else{
			return "WiFi\nDisabled";
		}
	}
	
	/**
	 * それ以外のネットワークの情報を表す文字列を返す
	 */
	private String getOtherNetInfoText()
	{
		return "[Other]\n" + getInetAddressText();
	}
	
	/**
	 * アクティブなネットワークの情報を表す文字列を返す
	 */
	private String getNetInfoText(Context context){
		ConnectivityManager cm 
			= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if( info == null){
			Log.d("WiFiStateWidgetProvider:getNetInfoText","No Network");
			return "No Network";		
		}
		String ret = "";
		switch( info.getType()){
		case ConnectivityManager.TYPE_MOBILE:
			ret = getMbileNetInfoText();
			break;
		case ConnectivityManager.TYPE_WIFI:
			ret = getWifiNetInfoText(context);
			break;
		default:
			ret = getOtherNetInfoText();
			break;
		}
		Log.d("WiFiStateWidgetProvider:getNetInfoText",ret);
		return ret;
	}

	/**
	 * localhost 以外の最初に見つかったIPアドレスを文字列にして返す
	 */
	private String getInetAddressText() {
	    try {
	    	Enumeration<NetworkInterface> netifs = NetworkInterface.getNetworkInterfaces();
	        for( NetworkInterface netif : Collections.list(netifs)){
	        	Enumeration<InetAddress> iaddresses = netif.getInetAddresses();
	        	for( InetAddress iaddress : Collections.list(iaddresses)){
	        		if( iaddress.isLoopbackAddress() == false){
	        			return iaddress.getHostAddress();
	        		}
	        	}	        	
	        }
	    } catch (SocketException ex) {
	    }
	    return "";
	}
}

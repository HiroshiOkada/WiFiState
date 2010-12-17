/**
 * Wi-Fi state widget
 *
 * Copyright (c) 2010 Hiroshi Okada <okadahiroshi@miobox.jp>
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *     1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *     2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *     3. This notice may not be removed or altered from any source
 *     distribution.
 */

package com.toycode.wifistate;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.Settings;
import android.text.format.Formatter;
import android.widget.RemoteViews;

	
	/**
	 * The services to update the infomation.
	 */
	public class UpdateService extends Service {
	
		@Override
		public void onStart(Intent intent, int startId) {
			RemoteViews updateViews = new RemoteViews( this.getPackageName(), R.layout.state_widget);
			updateViews.setTextViewText(R.id.InfoTextView, getNetInfoText(this));

			// startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); 
			Intent settingIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, settingIntent, 0);
			updateViews.setOnClickPendingIntent(R.id.InfoTextView, pendingIntent);				
			AppWidgetManager.getInstance(this).updateAppWidget( getThisName(), updateViews);
			super.onStart(intent, startId);
		}
		
		public ComponentName getThisName(){
			return new ComponentName(this, WiFiStateWidgetProvider.class);
		}
		
		
		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

		/**
		 * Retrun a string that represent first IP address except localhost.
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
		
		/**
		 * [Mobile] and ip address.
		 */
		private String getMbileNetInfoText()
		{
			return "[Mobile]\n" + getInetAddressText();
		}
		
		/**
		 * Wi-Fi network ssid and ip address.
		 */
		private String getWifiNetInfoText(Context context)
		{
			final int DIVIDE_LENGTH = 10;
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			if( wifiManager.isWifiEnabled()){
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ssid = wifiInfo.getSSID();
				if( ssid.length() > DIVIDE_LENGTH){
					String head = ssid.substring( 0, DIVIDE_LENGTH);
					String tail = ssid.substring( DIVIDE_LENGTH, ssid.length());
					ssid = head + "\n" + tail;
 				}
				return ssid + "\n" +  Formatter.formatIpAddress(wifiInfo.getIpAddress());
			}else{
				return "WiFi\nDisabled";
			}
		}
		
		/**
		 * [Other] and IP address.
		 */
		private String getOtherNetInfoText()
		{
			return "[Other]\n" + getInetAddressText();
		}
		
		/**
		 * return active network infomation.
		 */
		private String getNetInfoText(Context context){
					
			ConnectivityManager cm 
				= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if( info == null){
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
			return ret;
		}
		
	}

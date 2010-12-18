/*
 * Copyright (C) 2010 Hiroshi Okada <h-okada@toycode.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
public abstract class AbstractUpdateService extends Service {
	/**
	 * @return R.id.Button*
	 */
	protected abstract int getButtonID();

	/**
	 * @return R.layout.state_widget*
	 */
	protected abstract int getLayoutID();

	/**
	 * @return new ComponentName(this, WiFiStateWidgetProvider*.class);
	 */
	protected abstract ComponentName getComponentName();
	
	@Override
	public void onStart(Intent intent, int startId) {
		RemoteViews updateViews = new RemoteViews(this.getPackageName(), getLayoutID());
		updateViews.setTextViewText(getButtonID(), getNetInfoText(this));

		Intent settingIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				settingIntent, 0);
		updateViews.setOnClickPendingIntent(getButtonID(), pendingIntent);
		AppWidgetManager.getInstance(this).updateAppWidget(getComponentName(),
				updateViews);
		super.onStart(intent, startId);
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
			Enumeration<NetworkInterface> netifs = NetworkInterface
					.getNetworkInterfaces();
			for (NetworkInterface netif : Collections.list(netifs)) {
				Enumeration<InetAddress> iaddresses = netif.getInetAddresses();
				for (InetAddress iaddress : Collections.list(iaddresses)) {
					if (iaddress.isLoopbackAddress() == false) {
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
	private String getMbileNetInfoText(NetworkInfo info) {
		if (info.isRoaming()) {
			return "[Roming]\n" + getInetAddressText();
		} else {
			return "[Mobile]\n" + getInetAddressText();
		}
	}

	/**
	 * Wi-Fi network ssid and ip address.
	 */
	private String getWifiNetInfoText(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			return ssid + "\n"
					+ Formatter.formatIpAddress(wifiInfo.getIpAddress());
		} else {
			return "WiFi\nDisabled";
		}
	}

	/**
	 * [Other] and IP address.
	 */
	private String getOtherNetInfoText() {
		return "[Other]\n" + getInetAddressText();
	}

	/**
	 * return active network infomation.
	 */
	private String getNetInfoText(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			return "No Network";
		}
		String ret = "";
		switch (info.getType()) {
		case ConnectivityManager.TYPE_MOBILE:
			ret = getMbileNetInfoText(info);
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


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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

public abstract class WiFiStateWidgetProvider extends AppWidgetProvider {

	/**
	 * @return new Intent( context, UpdateService.class)
	 */
	protected abstract Intent getServiceIntent(Context context);

	/**
	 * Response to "android.appwidget.action.APPWIDGET_UPDATE"
	 * In this widget, all updates are performed in the service.
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		context.startService( getServiceIntent(context));
	}

	/**
	 * When the widget is deleted, the service also be removed.
	 */
	@Override
	public void onDisabled(Context context) {
		context.stopService( getServiceIntent(context));		
		super.onDisabled( context);
	}

	/**
	 * In addition to the regular update, this widget 
	 * is respond to NETWORK_STATE_CHANGED_ACTION & CONNECTIVITY_ACTION.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if( action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) ||
					action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			
			context.startService( getServiceIntent(context));
		}else{
			super.onReceive( context, intent);
		}
	}
	
}

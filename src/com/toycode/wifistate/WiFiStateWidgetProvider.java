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

package com.toycode.wifistate;

import android.content.ComponentName;

public class UpdateService2x1l extends UpdateService {

	@Override
	protected int getButtonID() {
		return R.id.Button2x1l;
	}

	@Override
	protected int getLayoutID() {
		return R.layout.state_widget2x1l;
	}

	@Override
	protected ComponentName getComponentName() {
		return new ComponentName(this, WiFiStateWidgetProvider2x1l.class);
	}
}

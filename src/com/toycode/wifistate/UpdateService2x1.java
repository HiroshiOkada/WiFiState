package com.toycode.wifistate;

import android.content.ComponentName;

public class UpdateService2x1 extends UpdateService {

	@Override
	protected int getButtonID() {
		return R.id.Button2x1;
	}

	@Override
	protected int getLayoutID() {
		return R.layout.state_widget2x1;
	}

	@Override
	protected ComponentName getComponentName() {
		return new ComponentName(this, WiFiStateWidgetProvider2x1.class);
	}
}

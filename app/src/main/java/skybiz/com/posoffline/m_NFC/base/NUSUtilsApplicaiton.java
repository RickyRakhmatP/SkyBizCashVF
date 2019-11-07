package skybiz.com.posoffline.m_NFC.base;

import android.app.Application;

import skybiz.com.posoffline.m_NFC.util.CommonValues;


public class NUSUtilsApplicaiton extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		initializaiton();
	}

	private void initializaiton() {
		CommonValues.Initalization();
	}
}

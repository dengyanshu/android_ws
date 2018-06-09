package cn.yanshu.broadcastreceiver;

import cn.yanshu.R;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class ScreenBroadcastActivity extends Activity {
	private  ScreenBroadcast  screenBroadcast=new ScreenBroadcast();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.screenbroadcast_activity);
		
		IntentFilter  intentFilter=new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		registerReceiver(screenBroadcast, intentFilter);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		unregisterReceiver(screenBroadcast);
	}

}

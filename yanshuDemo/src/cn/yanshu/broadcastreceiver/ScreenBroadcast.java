package cn.yanshu.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * 屏幕广播  
 * google配置过于简单  防止恶意配置
 * 必须用代码开启
 * 
 * **/

public class ScreenBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
            System.out.println("screenoff--broadcastreceiver:"+intent.getAction());
	}

}

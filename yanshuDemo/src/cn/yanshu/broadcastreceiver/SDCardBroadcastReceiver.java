package cn.yanshu.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SDCardBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        //当sd卡挂载或者卸载时候 会发送通知到此  通过intent
		System.out.println(intent.getAction());
	}

}

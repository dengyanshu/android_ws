package cn.yanshu.broadcastreceiver;

import cn.yanshu.intentdemo.IntentActivity1;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BootRestart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
       /* System.out.println("here exec");
        Intent    newIntent=new Intent();
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setComponent(new ComponentName(context,IntentActivity1.class));
        context.startActivity(newIntent);*/
	}

}

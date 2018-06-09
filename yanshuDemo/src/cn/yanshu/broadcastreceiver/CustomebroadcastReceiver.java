package cn.yanshu.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomebroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		   String data=intent.getStringExtra("data");
           Toast.makeText(context, data, 1).show();
          /* abortBroadcast();
                              终止广播*/
	}

}

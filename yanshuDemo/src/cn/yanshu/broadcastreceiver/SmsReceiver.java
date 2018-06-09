package cn.yanshu.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	Bundle  bundle=	intent.getExtras();
	Object[] objs=(Object[]) bundle.get("pdus");
	for(Object obj:objs){
		SmsMessage  sms=SmsMessage.createFromPdu((byte[])obj);
		System.out.println(sms.getMessageBody());
		System.out.println(sms.getOriginatingAddress());
		
	}
	

	}

}

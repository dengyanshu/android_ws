package cn.yanshu.broadcastreceiver;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.yanshu.R;
/**
 * 发送一个无序广播
 * */
public class SendActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendbroadcastreceiver_activity);
	}
    
	
	public void click(View view){
		/**
		 * 发送无序广播
		 * */
		Intent intent=new Intent();
		intent.setAction("cn.yanshu.broadcastreceiver.SendActivity");
		intent.putExtra("data", "新闻联播7点开始啦....");
		sendBroadcast(intent);
		/**
		 * 发送有序广播
		 * 
		 * 这些广播 可以有很多接受者 省长  市长 农民  只要是广播接受者 intent过滤器 能接受这个action
		 * 
		 * <intent-filter android:priority="1000"></intent-filter>
		 * 添加优先级-1000~1000
		 * */
		
		Intent newIntent=new Intent();
		intent.setAction("cm.yanshu.chouchou");
		//通过intent存入的数据必须通过intent取
		
		/*intent  
		 * receiverPermission  接受者权限
		 * resultReceiver  最终的接受者
		 * scheduler    handler对象
		 * initialCode   初始化代码
		 * initialData    初始化数据 （类似电话拨号器案例 直接用getResult()获取电话号码）
		 * initialExtras  其他额外数据
		 * */
		sendOrderedBroadcast(newIntent, null, null, null, 0, "每个农民1000斤大米", null);
	}
}

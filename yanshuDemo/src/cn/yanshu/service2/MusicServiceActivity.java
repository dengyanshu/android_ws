package cn.yanshu.service2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import cn.yanshu.R;
/**
 * 显示混合service的调用
 * 因为音乐需要调用service里面的方法 必须返回binder标示的接口实例 还需要不随着activity的销毁而持续运行 
 * 所以需要以startService的方式运行
 * 
 * 1、二次按钮调用unbindService（会报停止应用） 一般unbindService放在activity的destory中调用
 *   如果不人工调用unbindService  当activity销毁的时候 google会红色字体提示开发者 所以一般在onDestroy中调用 开发者
 * 
 * */
public class MusicServiceActivity extends Activity {
	
	private  ServiceConnection serviceConnection=new MyServiceConnection();
	private  IMusicServiceBinder   iMusicServiceBinder;
	
   @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musicservice_activity);
		Intent  intent=new Intent(this, MusicService.class);
		startService(intent);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
	}
   
   
     private class MyServiceConnection implements  ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			iMusicServiceBinder=(IMusicServiceBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
    	 
     }
     /*
      * 
      * 一般在onDestroy 调用unbindService()方法
      * **/
     @Override
      protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	unbindService(serviceConnection);
    }
     
     
     public  void  click1(View  view){
    	 iMusicServiceBinder.callbeginMusic();
     }
     
     public  void  click2(View  view){
    	 iMusicServiceBinder.calkujinMusic();
     }
     
     public  void  click3(View  view){
    	 iMusicServiceBinder.callhoutuiMusic();
     }
     
     public  void  click4(View  view){
    	 iMusicServiceBinder.callendMusic();
     }
}

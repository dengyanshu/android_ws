package cn.yanshu.service2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}
	
	
	private  void beginMusic(){
		System.out.println("开始播放");
	}
	private  void kujinMusic(){
		System.out.println("音乐按下快进");
	}
	private  void houtuiMusic(){
		System.out.println("音乐按下后退");
	}
	private  void endMusic(){
		System.out.println("停止播放");
	}
	
	/*
	 * 无论是startService方式开启服务 还是bindSerivice的方式开启服务
	 * onCreate 只执行一次
	 * */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("MusicService--onCreate");
	}
	
	/**
	 * 
	 * 每次调用startService 都会调用
	 * */
   
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("MusicService--onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 服务摧毁被调用
	 * */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		System.out.println("MusicService--onDestroy");
	}
	
	/**
	 * 
	 * 如果此类公开 可以在activity中导入这个service包 
	 * 直接创建 myBinder实例 myBinder是service的内部类（把相关的文件放在一个类这种写法非常好）
	 * 
	 * 1、继承自binder 只是标示作用
	 * 2、核心在接口暴露方法 返回给前台的也是service接口实例
	 * 
	 * */
	
	private class MyBinder  extends  Binder  implements  IMusicServiceBinder{

		@Override
		public void callbeginMusic() {
			// TODO Auto-generated method stub
			beginMusic();
		}

		@Override
		public void calkujinMusic() {
			// TODO Auto-generated method stub
			kujinMusic();
		}

		@Override
		public void callhoutuiMusic() {
			// TODO Auto-generated method stub
			houtuiMusic();
		}

		@Override
		public void callendMusic() {
			// TODO Auto-generated method stub
			endMusic();
		}
	}
}

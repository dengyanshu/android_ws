package com.zowee.mes.dialogutil;

import android.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MydismissDialog extends Dialog {

   public  boolean  flag=true;

	public MydismissDialog(Context context,int theme) {
		super(context,theme);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//setContentView(R.layout.mydialog);
	}
	
	 @Override
	 public void show() {
	     super.show();
	     mThread.start();
	 }

	 
	 @Override
	 public void dismiss() {
	     super.dismiss();
	     flag = false;
	 }

	 
	 private Thread mThread = new Thread(){
	     @Override
	     public void run() {
	    	 
	    		 try {
	    			 Thread.sleep(2000);
	    			 Message msg = mHandler.obtainMessage();
	    			 msg.what = 1;
	    			 mHandler.sendMessage(msg);
	    		 } catch (InterruptedException e) {
	    			 e.printStackTrace();
	    		 }
	    	 
	     }
	 };
	     
	     
	     private Handler mHandler = new Handler(){
	    	 @Override
	    	 public void handleMessage(Message msg) {
	    		 super.handleMessage(msg);
	    		 if(msg.what == 1)
	    			 dismiss();
	    	 }

	     };

}

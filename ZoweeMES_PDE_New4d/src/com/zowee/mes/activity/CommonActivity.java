package com.zowee.mes.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.adapter.CommonSpinnerAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
/**
 * @author Administrator
 * @description ͨ�õ�activity
 */
public abstract class CommonActivity extends Activity implements CommonActivityInterface 
{
	protected LaserScanOperator laserScanOperator ;   
	protected int TASKTYPE = TaskType.DEFAULT;
	protected CommonActivity commonActivity = null;
	protected boolean isNull = false;//�����Ա��������ζ����ص������������Ƿ�Ϊ��
	public static final String TAG = "activity";
	protected  ProgressDialog progressDialog;
	public static final int  VERTICAL = 0;//��ʾ������״̬ʱ
	public static final int HORIZONTAL = 1;//��ʾ�ں���״̬ʱ
	//public int a = 2;

	@Override
	public void init() 
	{

		//laserScanOperator.initLaserUserInfor(commonActivity,TASKTYPE);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.taskProDia);

		SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserTicket=sharedPref.getString("pref_user_ticket", "");
		if(TextUtils.isEmpty(savedUserTicket)){
			finish();
		}
	}

	@Override
	public void refresh(Task task) 
	{
		if(null==task||null==task.getTaskResult())//�����ж����񷵻صĽ�������Ƿ�Ϊ��
			isNull = true;
		else
			isNull = false;

		switch(task.getTaskType())
		{
		case TaskType.LASER_INIT_FAIL:
			Toast.makeText(this,task.getTaskData().toString(), 1500).show();
			//SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
			break;
		case TaskType.WEBSERVICE_FAIL:
			Toast.makeText(this ,R.string.ws_req_fail, Toast.LENGTH_LONG).show();
			break;
		case TaskType.NET_CONNECT_FAIL:
			Toast.makeText(this, R.string.NetConFail, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//isNull = false;//
	}


	/*
	 * �Կؼ����зǿ���֤ ��֤ͨ������ true ���� false ע�� EditText extends  TextView
	 */
	protected boolean subView(View view)
	{
		if(null==view ) return false;

		if(view  instanceof EditText )
		{
			if(StringUtils.isEmpty(((EditText)view).getText().toString()))
				return false;
		}

		return true;
	}

	/*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {

    	switch(keyCode)
		{
			case MyApplication.SCAN_KEYCODE:

				laserScan();
				break;
		}

    	return super.onKeyDown(keyCode, event);
    }*/
	@Override 
	public boolean onKeyUp(int keyCode, KeyEvent event) 
	{
		/*
    	 switch(keyCode)
		{
			case MyApplication.SCAN_KEYCODE:				
	    	laserScan();  
				break;
		} 
        return super.onKeyUp(keyCode, event);
		 */
		boolean scanlabel = false;   // change by ybj 20140321
		switch(keyCode)
		{
//		case MyApplication.SCAN_KEYCODE:				
//			laserScan(); 
//			break;
//		case 27:   //  27 Ϊ���ռ�
//			laserScan(); 
//			scanlabel = true;
//			break;
//		case KeyEvent.KEYCODE_SEARCH:   //  27 Ϊ���ռ�
//			Toast.makeText(this,"��������������", 1500).show();
//			laserScan(); 
//			//scanlabel = true;
//			break;
//		case 211:   //  27 Ϊ���ռ�
//			Toast.makeText(this,"��������������", 1500).show();
//			laserScan(); 
//			//scanlabel = true;
//			break;	
//		case 212:   //  27 Ϊ���ռ�
//			Toast.makeText(this,"��������������", 1500).show();
//			laserScan(); 
//			//scanlabel = true;
//			break;	
		} 
		if(scanlabel)
			return true;
		else
			return super.onKeyUp(keyCode, event);
	}
	/**********************************************************************************************************/
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean scanlabel = false;   // 
		switch(keyCode)
		{
		case MyApplication.SCAN_KEYCODE:				
			laserScan(); 
			break;
		case 27:   //  27 Ϊ���ռ�
			//laserScan(); 
			scanlabel = true;
			break;
		} 
		if(scanlabel)
			return true;
		else
			return super.onKeyUp(keyCode, event);
	}
	/**********************************************************************************************************/
	protected void laserScan() 
	{
		if(!laserScanOperator.getLaserEnable()) return;
		if(LaserScanOperator.getCurLaserScanState())
		{
			laserScanOperator.startLaserScan(this);
			return;
		}
		else
		{
			laserScanOperator.laserInitFailNotify();
			laserScanOperator = MyApplication.getLaserScanOperator();
		}

	} 

	@Override
	protected void onResume() 
	{
		super.onResume();
		LaserScanOperator.initLaserUserInfor(commonActivity, TASKTYPE);
		laserScanOperator = LaserScanOperator.getLaserScanOperator();
		if(null!=laserScanOperator)
			laserScanOperator.initLaserUserInfor(commonActivity, TASKTYPE); 

	} 
	protected void logDetails(View view,String logText){
		CharacterStyle ssStyle=null;
		if(logText.contains("Success_Msg:")){
			ssStyle=new ForegroundColorSpan(Color.GREEN);
		}else{
			ssStyle=new ForegroundColorSpan(Color.RED);
		}
		logText =logText.replaceAll("Success_Msg:", "");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog="["+df.format(new Date())+"]"+logText+"\n";		
		SpannableStringBuilder ssBuilder=new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		

		if( view instanceof EditText){

			EditText tempEdtView = (EditText)view;
			ssBuilder.append(tempEdtView.getText());
			tempEdtView.setText(ssBuilder);	
			return;
		} 
	}
	protected void onPause()
	{
		super.onPause();
		LaserScanOperator.closeLaserScanOperator();
	}

	protected void closeProDia()
	{
		if(null!=progressDialog&&progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
	}

	protected void showProDia()
	{
		if(null!=progressDialog&&!progressDialog.isShowing())
		{
			progressDialog.show();
		}
	}

	/**
	 * @param view
	 * @param speicalVal ��պ������ֵ �����Ϊ�յĻ� 
	 * @description �Կؼ����ı�ֵ�������   
	 */
	protected void clearWidget(View view,Object specialVal)
	{
		if(null==view) return;
		if(view  instanceof EditText )
		{
			EditText tempEdtView = (EditText)view;
			if(null==specialVal)
				tempEdtView.setText("");
			else
				tempEdtView.setText(specialVal.toString());
			//Log.i(TAG, "I am in EditText");
			return;
		}
		if(view instanceof Spinner)
		{
			//Log.i(TAG, "I am in Spinner");
			Spinner spinner = (Spinner)view;
			if(null==specialVal)
				spinner.setAdapter(new CommonSpinnerAdapter<String>(new ArrayList<String>(0),this));
			else
			{
				SpinnerAdapter spinnerAdapter = (SpinnerAdapter)specialVal;
				spinner.setAdapter(spinnerAdapter);
			}
			return;
			//spinner.setAdapter(new ArrayAdapter<T>(this, textViewResourceId));
		}

	}



}

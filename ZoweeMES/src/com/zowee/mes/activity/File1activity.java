package com.zowee.mes.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zowee.mes.Anxuzhuangxiang;
import com.zowee.mes.Biaoqianhedui;
import com.zowee.mes.Checkpacking;
import com.zowee.mes.R;
import com.zowee.mes.Tiebiaozhuandsn;
import com.zowee.mes.Xiaojiegouckp;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;

/**
 * @author Administrator
 * @description 该activity主要用于显示file1里面的功能程序
 */
public class File1activity extends Activity implements CommonActivityInterface,
		OnClickListener {
	/************************************ deng tianjia *************************************/
    private  TextView  lab_silou_biaoqianhedui; //1
    private  TextView  lab_silou_caihehedui;//2
    //第3个 只在界面显示 不做点击 跳转
    private   TextView  lab_silou_tiebiaozhuandsn;//4
    private   TextView  lab_silou_xiaojiegouckp;//5
    private   TextView  lab_silou_anxuzhuangxiang;//6
    
    /************************************ deng tianjia *************************************/

	

	@Override
	public void init() {
		

		/************************************ deng tianjia *************************************/
		lab_silou_biaoqianhedui=(TextView) findViewById(R.id.lab_file1_1);
		lab_silou_biaoqianhedui.setOnClickListener(this);
		lab_silou_caihehedui=(TextView) findViewById(R.id.lab_file1_2);
		lab_silou_caihehedui.setOnClickListener(this);
		lab_silou_tiebiaozhuandsn=(TextView) findViewById(R.id.lab_file1_4);
		lab_silou_tiebiaozhuandsn.setOnClickListener(this);
		lab_silou_xiaojiegouckp=(TextView) findViewById(R.id.lab_file1_5);
		lab_silou_xiaojiegouckp.setOnClickListener(this);
		lab_silou_anxuzhuangxiang=(TextView) findViewById(R.id.lab_file1_6);
		lab_silou_anxuzhuangxiang.setOnClickListener(this);
		/************************************ deng tianjia *************************************/


		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_file1);
		init();
		Intent bgSerIntent = new Intent(this, BackgroundService.class);
		startService(bgSerIntent);
		SoundEffectPlayService
				.initSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
	}

	

	@Override
	public void onClick(View v) {
		
		Intent targetActivity = new Intent();
		// 暂时屏蔽物料收料的功能
	
		switch (v.getId()) {
		
		/** deng后续添加 **************************************************************/
		
		case R.id.lab_file1_1:
			targetActivity.setComponent(new ComponentName(File1activity.this,
					Biaoqianhedui.class));
			break;	
		case R.id.lab_file1_2:
			targetActivity.setComponent(new ComponentName(File1activity.this,
					Checkpacking.class));
			break;	
		case R.id.lab_file1_4:
			targetActivity.setComponent(new ComponentName(File1activity.this,
					Tiebiaozhuandsn.class));
			break;	
		case R.id.lab_file1_5:
			targetActivity.setComponent(new ComponentName(File1activity.this,
					Xiaojiegouckp.class));
			break;	
		case R.id.lab_file1_6:
			targetActivity.setComponent(new ComponentName(File1activity.this,
					Anxuzhuangxiang.class));
			break;	
		
		/** deng后续添加 **************************************************************/
		}
	   startActivity(targetActivity);
	}

	

	

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("确定退出应用程序吗?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(File1activity.this,
										BackgroundService.class));
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	@Override
	public void refresh(Task task) {
		// TODO Auto-generated method stub
		
	}
}

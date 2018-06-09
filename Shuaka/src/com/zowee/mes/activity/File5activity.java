package com.zowee.mes.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zowee.mes.Hanjieji;
import com.zowee.mes.HanjiejiTabhost;
import com.zowee.mes.R;
import com.zowee.mes.Shuaka;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;

/**
 * @author Administrator
 * @description 该activity主要用于显示file5里面的功能程序
 * 自动化焊机机
 */
public class File5activity extends Activity implements CommonActivityInterface,
		OnClickListener {
	
	

	/************************************ deng tianjia *************************************/

	
    private  TextView  lab_hanjieji_configtoplc;
    private  TextView  lab_hanjieji_boxingtu;
    private  TextView  lab_hanjieji;
    private  TextView  lab_shuaka;
  
    /************************************ deng tianjia *************************************/

	


	@Override
	public void init() {
		

		/************************************ deng tianjia *************************************/
		lab_hanjieji_configtoplc=(TextView) findViewById(R.id.lab_file5_1);
		lab_hanjieji_configtoplc.setOnClickListener(this);
		lab_hanjieji_boxingtu=(TextView) findViewById(R.id.lab_file5_2);
		//lab_hanjieji_boxingtu.setOnClickListener(this);
		lab_hanjieji_boxingtu.setEnabled(false);
		lab_hanjieji=(TextView) findViewById(R.id.lab_file5_3);
		lab_hanjieji.setOnClickListener(this);
		lab_shuaka=(TextView) findViewById(R.id.lab_file5_4);
		lab_shuaka.setOnClickListener(this);
		
		
		/************************************ deng tianjia *************************************/


		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_file5);
		init();
		Intent bgSerIntent = new Intent(this, BackgroundService.class);
		startService(bgSerIntent);
		SoundEffectPlayService
				.initSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
	}

	

	@Override
	public void onClick(View v) {
		
		Intent targetActivity = new Intent();
		
	
		switch (v.getId()) {
		
		/** deng后续添加 **************************************************************/
		//材料绑定
		case R.id.lab_file5_1:
			targetActivity.setComponent(new ComponentName(File5activity.this,
					HanjiejiTabhost.class));
			targetActivity.putExtra("hanjieji", "1");
		break;	
		//整机绑定
		case R.id.lab_file5_2:
			targetActivity.setComponent(new ComponentName(File5activity.this,
					HanjiejiTabhost.class));
			targetActivity.putExtra("hanjieji", "2");
		case R.id.lab_file5_3:
			targetActivity.setComponent(new ComponentName(File5activity.this,
					Hanjieji.class));
		case R.id.lab_file5_4:
			targetActivity.setComponent(new ComponentName(File5activity.this,
					Shuaka.class));
			
		break;	
		
	
		/** deng后续添加 **************************************************************/
		}
	   startActivity(targetActivity);
	}

	

	

	@Override
//	public void onBackPressed() {
//		killMainProcess();
//	}
//
//	private void killMainProcess() {
//		new AlertDialog.Builder(this)
//				.setIcon(android.R.drawable.ic_dialog_alert)
//				.setTitle("确定退出应用程序吗?")
//				.setPositiveButton(getString(android.R.string.yes),
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								stopService(new Intent(File2activity.this,
//										BackgroundService.class));
//								android.os.Process
//										.killProcess(android.os.Process.myPid());
//							}
//						})
//				.setNegativeButton(getString(android.R.string.no), null).show();
//	}

	
	public void refresh(Task task) {
		// TODO Auto-generated method stub
		
	}
}

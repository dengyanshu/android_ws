package com.zowee.mes.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zowee.mes.Assyqidong;
import com.zowee.mes.Bujianbangding;
import com.zowee.mes.Haiercailiaobangding;
import com.zowee.mes.Haierzhengjisnbangding;
import com.zowee.mes.Liuloufvmi;
import com.zowee.mes.Liuloulaohua;
import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;

/**
 * @author Administrator
 * @description 该activity主要用于显示file4里面的功能程序
 * 卓大海尔平板程序 新增 材料绑定程序，  整机绑定程序
 */
public class File4activity extends Activity implements CommonActivityInterface,
		OnClickListener {
	
	

	/************************************ deng tianjia *************************************/

	
    private  TextView  lab_haier_cailiaobangding;
    private  TextView  lab_haier_zhengjibangding;
  
    /************************************ deng tianjia *************************************/

	


	@Override
	public void init() {
		

		/************************************ deng tianjia *************************************/
		lab_haier_cailiaobangding=(TextView) findViewById(R.id.lab_file4_1);
		lab_haier_cailiaobangding.setOnClickListener(this);
		lab_haier_zhengjibangding=(TextView) findViewById(R.id.lab_file4_2);
		lab_haier_zhengjibangding.setOnClickListener(this);
		
		
		/************************************ deng tianjia *************************************/


		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_file4);
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
		//材料绑定
		case R.id.lab_file4_1:
			targetActivity.setComponent(new ComponentName(File4activity.this,
					Haiercailiaobangding.class));
		break;	
		//整机绑定
		case R.id.lab_file4_2:
			targetActivity.setComponent(new ComponentName(File4activity.this,
					Haierzhengjisnbangding.class));
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

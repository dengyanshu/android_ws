package com.zowee.mes.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zowee.mes.R;
import com.zowee.mes.SNcheckandwritelog;
import com.zowee.mes.Tjbadcount;
import com.zowee.mes.Tjdipstart;
import com.zowee.mes.Tjzicommonpassstation;
import com.zowee.mes.Tjzimicaihebangding;
import com.zowee.mes.Tjzimicaihezhuangxiang;
import com.zowee.mes.Tjzimidianyuantouru;
import com.zowee.mes.Tjzimioqc;
import com.zowee.mes.Tjzimipowertest;
import com.zowee.mes.Tjzimiweixiu;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;

/**
 * @author Administrator
 * @description 该activity主要用于显示file1里面的功能程序
 */
public class File2activity extends Activity implements CommonActivityInterface,
		OnClickListener {
	
	

	/************************************ deng tianjia *************************************/

	
    private  TextView  lab_tianjin_tiaomahedui;
    private  TextView  lab_tianjin_zimicaihebangding;
    private  TextView  lab_tianjin_zimicaihezhuangxiang;
    private  TextView  lab_tianjin_zimioqcchoujian;
    
    
    private  TextView  lab_tianjin_zimidianyuantouru;
    private  TextView  lab_tianjin_zimiweixiu;
    
    private  TextView  lab_tianjin_zimidianyuanceshi;
    private  TextView  lab_tianjin_commonpass;
    private  TextView  lab_tj_dipstart;
    private  TextView  lab_tj_dipstart2;
    private  TextView  lab_tj_badcount;
    /************************************ deng tianjia *************************************/

	


	@Override
	public void init() {
		

		/************************************ deng tianjia *************************************/
		lab_tianjin_tiaomahedui=(TextView) findViewById(R.id.lab_file2_1);
		lab_tianjin_tiaomahedui.setOnClickListener(this);
		lab_tianjin_zimicaihebangding=(TextView) findViewById(R.id.lab_file2_2);
		lab_tianjin_zimicaihebangding.setOnClickListener(this);
		lab_tianjin_zimicaihezhuangxiang=(TextView) findViewById(R.id.lab_file2_3);
		lab_tianjin_zimicaihezhuangxiang.setOnClickListener(this);
		lab_tianjin_zimioqcchoujian=(TextView) findViewById(R.id.lab_file2_4);
		lab_tianjin_zimioqcchoujian.setOnClickListener(this);
		lab_tianjin_zimidianyuantouru=(TextView) findViewById(R.id.lab_file2_5);
		lab_tianjin_zimidianyuantouru.setOnClickListener(this);
		lab_tianjin_zimiweixiu=(TextView) findViewById(R.id.lab_file2_6);
		lab_tianjin_zimiweixiu.setOnClickListener(this);
		lab_tianjin_zimidianyuanceshi=(TextView) findViewById(R.id.lab_file2_7);
		lab_tianjin_zimidianyuanceshi.setOnClickListener(this);
		lab_tianjin_commonpass=(TextView) findViewById(R.id.lab_file2_8);
		lab_tianjin_commonpass.setOnClickListener(this);
		lab_tj_dipstart=(TextView) findViewById(R.id.lab_file2_9);
		lab_tj_dipstart.setOnClickListener(this);
		lab_tj_dipstart2=(TextView) findViewById(R.id.lab_file2_10);
		lab_tj_dipstart2.setOnClickListener(this);
		lab_tj_badcount=(TextView) findViewById(R.id.lab_file2_11);
		lab_tj_badcount.setOnClickListener(this);
		/************************************ deng tianjia *************************************/


		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_file2);
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
		
		case R.id.lab_file2_1:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					SNcheckandwritelog.class));
			break;	
		case R.id.lab_file2_2:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzimicaihebangding.class));
			break;	
		case R.id.lab_file2_3:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzimicaihezhuangxiang.class));
			break;	
		case R.id.lab_file2_4:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzimioqc.class));
			break;	
		case R.id.lab_file2_5:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzimidianyuantouru.class));
			break;	
		case R.id.lab_file2_6:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzimiweixiu.class));
			break;	
		case R.id.lab_file2_7:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzimipowertest.class));
			break;	
		case R.id.lab_file2_8:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjzicommonpassstation.class));
			break;	
		case R.id.lab_file2_9:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjdipstart.class));
			targetActivity.putExtra("diptype", "1");
			break;	
		case R.id.lab_file2_10:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjdipstart.class));
			targetActivity.putExtra("diptype", "2");
			break;
			
		case R.id.lab_file2_11:
			targetActivity.setComponent(new ComponentName(File2activity.this,
					Tjbadcount.class));
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

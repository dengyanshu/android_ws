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
import com.zowee.mes.Bujianbangding2;
import com.zowee.mes.Liuloufvmi;
import com.zowee.mes.Liuloulaohua;
import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;

/**
 * @author Administrator
 * @description 该activity主要用于显示file3里面的功能程序
 */
public class File3activity extends Activity implements CommonActivityInterface,
		OnClickListener {
	
	

	/************************************ deng tianjia *************************************/

	
    private  TextView  lab_liulou_assyqidong;
    private  TextView  lab_liulou_bujianbangding;
    private  TextView  lab_liulou_kaijiceshi;
    private  TextView  lab_liulou_laohuaruku;
    private  TextView  lab_liulou_laohuachuku;
    private  TextView  lab_liulou_gongnengceshi;
    private  TextView  lab_liulou_muqian;
    private  TextView  lab_liulou_perload;//下载出库
    private  TextView  lab_liulou_bujianbangding2;//下载出库
  
    /************************************ deng tianjia *************************************/

	


	@Override
	public void init() {
		

		/************************************ deng tianjia *************************************/
		lab_liulou_assyqidong=(TextView) findViewById(R.id.lab_file3_1);
		lab_liulou_assyqidong.setOnClickListener(this);
		lab_liulou_bujianbangding=(TextView) findViewById(R.id.lab_file3_2);
		lab_liulou_bujianbangding.setOnClickListener(this);
		lab_liulou_kaijiceshi=(TextView) findViewById(R.id.lab_file3_3);
		lab_liulou_kaijiceshi.setOnClickListener(this);
		lab_liulou_laohuaruku=(TextView) findViewById(R.id.lab_file3_4);
		lab_liulou_laohuaruku.setOnClickListener(this);
		lab_liulou_laohuachuku=(TextView) findViewById(R.id.lab_file3_5);
		lab_liulou_laohuachuku.setOnClickListener(this);
		lab_liulou_gongnengceshi=(TextView) findViewById(R.id.lab_file3_6);
		lab_liulou_gongnengceshi.setOnClickListener(this);
		lab_liulou_muqian=(TextView) findViewById(R.id.lab_file3_7);
		lab_liulou_muqian.setOnClickListener(this);
		lab_liulou_perload=(TextView) findViewById(R.id.lab_file3_8);
		lab_liulou_perload.setOnClickListener(this);
		lab_liulou_bujianbangding2=(TextView) findViewById(R.id.lab_file3_9);
		lab_liulou_bujianbangding2.setOnClickListener(this);
		/************************************ deng tianjia *************************************/


		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_file3);
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
		
		case R.id.lab_file3_1:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Assyqidong.class));
			break;	
		case R.id.lab_file3_2:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Bujianbangding.class));
			break;	
		//开机测试
		case R.id.lab_file3_3:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Liuloufvmi.class));
			targetActivity.putExtra("fvmi", "1");
			break;
		//老化入库	
		case R.id.lab_file3_4:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Liuloulaohua.class));
			targetActivity.putExtra("laohua", "1");
			
			break;
		//老化出库
		case R.id.lab_file3_5:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Liuloulaohua.class));
			targetActivity.putExtra("laohua", "2");
			break;
			//功能测试
		case R.id.lab_file3_6:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Liuloufvmi.class));
			targetActivity.putExtra("fvmi", "2");
			
			break;
		case R.id.lab_file3_7:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Liuloufvmi.class));
			targetActivity.putExtra("fvmi", "3");
			//目检
			break;
		case R.id.lab_file3_8:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Liuloufvmi.class));
			targetActivity.putExtra("fvmi", "4");
			//目检
			break;
		case R.id.lab_file3_9:
			targetActivity.setComponent(new ComponentName(File3activity.this,
					Bujianbangding2.class));
			//目检
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

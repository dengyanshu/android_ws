package com.zowee.mes.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.BindPcb;
import com.zowee.mes.BoxLotSNCheckActivity;
import com.zowee.mes.EyeCheckActivity;
import com.zowee.mes.Lenovofvmi;
import com.zowee.mes.Lenovotocar;
import com.zowee.mes.LineQtyselect;
import com.zowee.mes.LineSnInput;
import com.zowee.mes.M8000cp;
import com.zowee.mes.MobileSMTToDIP;
import com.zowee.mes.Printtest;
import com.zowee.mes.R;
import com.zowee.mes.SMTFirstScan;
import com.zowee.mes.SMTFirstScanBB;
import com.zowee.mes.SMTLade;
import com.zowee.mes.SMTScanMaterial;
import com.zowee.mes.SMTStick;
import com.zowee.mes.SMTStick_Old;
import com.zowee.mes.SMTStorage;
import com.zowee.mes.SMTTP_MANUAL;
import com.zowee.mes.SMTTP_New;
import com.zowee.mes.SMTXG;
import com.zowee.mes.Shuaka;
import com.zowee.mes.Signin;
import com.zowee.mes.Smprintting;
import com.zowee.mes.SmtFvmi;
import com.zowee.mes.SmtTP;
import com.zowee.mes.SmtTPforCommon;
import com.zowee.mes.Sntocar;
import com.zowee.mes.StartDip;
import com.zowee.mes.TJOQCCheckActivity;
import com.zowee.mes.Tino;
import com.zowee.mes.Tjzimipower;
import com.zowee.mes.Tjzimiproduct;
import com.zowee.mes.Tjzimirepairin;
import com.zowee.mes.Tjzimirepairput;
import com.zowee.mes.Webservice_test;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;

/**
 * @author Administrator
 * @description 该activity主要用于界面功能的显示
 */
public class MainActivity extends Activity implements CommonActivityInterface,
		OnClickListener {
	private TextView labStorageScan;
	private TextView labFifoScan;
	private TextView labSplitStore;
	private TextView labQuantityAdjust;
	private TextView labBatNumScan;
	private TextView labMaterialRewind;
	private TextView labPrepareOperate;
	private TextView labFeedingOnMateri;
	private TextView labCheMatStaTab;
	private TextView labRemoveMater;
	private TextView labMateriInventory;
	private TextView labFinishWareHouseCard;
	private TextView labFinishWareHouseCatton;
	private TextView labFinishOutCard;
	private TextView labFinishOutCatton;
	private TextView labFinishDeliCard;
	private TextView labFinishDeliCatton;
	private TextView labSMTFirstScan;
	private TextView labSMTFirstScan2;
	private TextView labSMTScanMaterial;
	private TextView labSMTStick;
	private TextView labSMTLade;
	private TextView labSMTStorage;
	private TextView labSMTToDIP;
	private TextView labDIPStartDip;
	private TextView labStartDIPOut;
	private TextView labMobileSMTTODIP;
	private TextView labDip_material_start;
	private TextView labTJeyscheck;
	private TextView labTJOQCCheck;
	private TextView labTjBoxLotSNCheck;
	private TextView labSMTeyeCheck;
	private TextView labSMTStick_Old;
	

	/************************************  tianjia *************************************/

	private TextView labTino;
	private TextView labbindpcb;
	private TextView lablineqtyselect;
	private TextView lablinesninput;
	private TextView labsmtfvmi;
	private TextView labsntocar;
	private TextView labtjzimiproduct;
	private TextView labtjzimipower;
	private TextView labtjzimirepairin;
	private TextView labtjzimirepairout;
    private  TextView  labcheckpacking;
    private  TextView  labfile1;//松岗
    private  TextView  labfile2;//天津
    private  TextView   labprinttest;
    private  TextView  labfile3;//松岗6楼联想平板
    private  TextView  labfile4;//松岗6楼海尔平板
    private  TextView  labfile5;
    private   TextView  lablenovofvmi;//smt联想专用目检  目检扣料不装车
    private   TextView  lablenovotocar;//smt联想专用装车  单板条码与芯片sn绑定 提交
    private   TextView  labm8000cp;//smt联想专用装车  单板条码与芯片sn绑定 提交
    private   TextView  labsignin;//打卡程序
    private   TextView  labsmttp;//新贴片程序
    private   TextView  labsmtlade3;//新增关联主副板装车
    private   TextView  labsmtprintting;//印刷扫描
    private   TextView  labsmtp_new;//新贴片程序 6轴上下多线程
    private   TextView  labsmttp_manual ; //新的人工扫描贴片  
    private   TextView  labsmt_ladetiepian ; //新的贴片装车  
    private    TextView  lab_webservicetest;
    private    TextView  labsmttp_common;
    private TextView labSMTFirstScanBB;
    private TextView labsmtxg;
    /************************************ tianjia *************************************/

	private final int MES_SETTING = Menu.FIRST;
	private final int MES_EXIT = Menu.FIRST + 1;
	// private final int UPDATE = Menu.FIRST+2;
	private final int GROUP_ID = 1;
	private ProgressDialog progressDialog;

	
	
	
	
	@Override
	public void init() {
		labStorageScan = (TextView) this.findViewById(R.id.lab_storageScan);
		labFifoScan = (TextView) this.findViewById(R.id.lab_fifoScan);
		labSplitStore = (TextView) this.findViewById(R.id.lab_splitStore);
		labQuantityAdjust = (TextView) this
				.findViewById(R.id.lab_quantityAdjust);
		labBatNumScan = (TextView) this.findViewById(R.id.lab_batNum_scan);
		labMaterialRewind = (TextView) this
				.findViewById(R.id.lab_materialRewind);
		labPrepareOperate = (TextView) this
				.findViewById(R.id.lab_prepareOperate);
		labFeedingOnMateri = (TextView) this
				.findViewById(R.id.lab_feeding_onMaterial);
		labCheMatStaTab = (TextView) this
				.findViewById(R.id.lab_chemater_statab);
		labRemoveMater = (TextView) this.findViewById(R.id.lab_removeMateri);
		labMateriInventory = (TextView) this
				.findViewById(R.id.lab_materiInvento);
		labFinishWareHouseCard = (TextView) this
				.findViewById(R.id.lab_fini_wareHouCard);
		labFinishWareHouseCatton = (TextView) this
				.findViewById(R.id.lab_fini_warHou_catton);
		labFinishOutCard = (TextView) this
				.findViewById(R.id.lab_finishOut_card);
		labFinishOutCatton = (TextView) this
				.findViewById(R.id.lab_finiOut_catton);
		labFinishDeliCard = (TextView) this
				.findViewById(R.id.lab_finish_delivCard);
		labFinishDeliCatton = (TextView) this
				.findViewById(R.id.lab_finiDeli_catton);
		labSMTFirstScan = (TextView) this.findViewById(R.id.lab_SMTFirstScan);
		labSMTFirstScan2 = (TextView) this.findViewById(R.id.lab_SMTFirstScan2);
		labSMTScanMaterial = (TextView) this
				.findViewById(R.id.lab_SMTScanMaterial);
		labSMTStick = (TextView) this.findViewById(R.id.lab_SMTStick);
		labSMTLade = (TextView) this.findViewById(R.id.lab_SMTLade);
		labSMTStorage = (TextView) this.findViewById(R.id.lab_SMTStorage);
		labSMTToDIP = (TextView) this.findViewById(R.id.lab_SMTToDIP);
		labDIPStartDip = (TextView) this.findViewById(R.id.lab_DIPStartDip);
		labStartDIPOut = (TextView) this.findViewById(R.id.lab_StartDIPOut);
		labMobileSMTTODIP = (TextView) this
				.findViewById(R.id.lab_MobileSMTTODIP);
		labDip_material_start = (TextView) this
				.findViewById(R.id.lab_Dip_material_start);
		labTJeyscheck = (TextView) this.findViewById(R.id.lab_TJeyscheck);
		labTJOQCCheck = (TextView) this.findViewById(R.id.lab_TJOQCCheck);
		labTjBoxLotSNCheck = (TextView) this
				.findViewById(R.id.lab_TjBoxLotSNCheck);
		labSMTeyeCheck = (TextView) this.findViewById(R.id.lab_SMTeyeCheck);
		labSMTStick_Old = (TextView) this.findViewById(R.id.lab_SMTStick_Old);

		/************************************ deng tianjia *************************************/
		labTino = (TextView) findViewById(R.id.lab_tino);
		labTino.setOnClickListener(this);
		labbindpcb = (TextView) findViewById(R.id.lab_bindpcb);
		labbindpcb.setOnClickListener(this);
		lablineqtyselect = (TextView) findViewById(R.id.lab_lineqtyselect);
		lablineqtyselect.setOnClickListener(this);
		lablinesninput = (TextView) findViewById(R.id.lab_linesninput);
		lablinesninput.setOnClickListener(this);
		labsmtfvmi = (TextView) findViewById(R.id.lab_smtfvmi);
		labsmtfvmi.setOnClickListener(this);
		labsntocar = (TextView) findViewById(R.id.lab_sntocar);
		labsntocar.setOnClickListener(this);
		labtjzimiproduct = (TextView) findViewById(R.id.lab_tjzimiproduct);
		labtjzimiproduct.setOnClickListener(this);
		labtjzimipower = (TextView) findViewById(R.id.lab_tjzimipower);
		labtjzimipower.setOnClickListener(this);
		labtjzimirepairin = (TextView) findViewById(R.id.lab_tjzimirepairin);
		labtjzimirepairin.setOnClickListener(this);
		labtjzimirepairout = (TextView) findViewById(R.id.lab_tjzimirepairout);
		labtjzimirepairout.setOnClickListener(this);
		
		labfile1=(TextView) findViewById(R.id.lab_file1);
		labfile1.setOnClickListener(this);
		labfile2=(TextView) findViewById(R.id.lab_file2);
		labfile2.setOnClickListener(this);
		labfile3=(TextView) findViewById(R.id.lab_file3);
		labfile3.setOnClickListener(this);
		labfile4=(TextView) findViewById(R.id.lab_file4);
		labfile4.setOnClickListener(this);
		labfile5=(TextView) findViewById(R.id.lab_file5);
		labfile5.setOnClickListener(this);
		
		labprinttest=(TextView) findViewById(R.id.lab_printtest);
		labprinttest.setOnClickListener(this);
		
		lablenovofvmi=(TextView) findViewById(R.id.lab_lenovofvmi);
		lablenovofvmi.setOnClickListener(this);
		
		lablenovotocar=(TextView) findViewById(R.id.lab_lenovotocar);
		lablenovotocar.setOnClickListener(this);
		
		labm8000cp=(TextView) findViewById(R.id.lab_m8000cp);
		labm8000cp.setOnClickListener(this);
		
		labsignin=(TextView) findViewById(R.id.lab_signin);
		labsignin.setOnClickListener(this);
		
		labsmttp=(TextView) findViewById(R.id.lab_smttp);
		labsmttp.setOnClickListener(this);
		
		labsmtlade3=(TextView) findViewById(R.id.lab_smtlade3);
		labsmtlade3.setOnClickListener(this);
		
		labsmtprintting=(TextView) findViewById(R.id.lab_smtprinting);
		labsmtprintting.setOnClickListener(this);
		
		labsmtp_new=(TextView) findViewById(R.id.lab_smttp_new);
		labsmtp_new.setOnClickListener(this);
		
		labsmttp_manual=(TextView) findViewById(R.id.lab_smttp_manual);
		labsmttp_manual.setOnClickListener(this);
		
		labsmt_ladetiepian=(TextView) findViewById(R.id.lab_smtladecarandtiepian);
		labsmt_ladetiepian.setOnClickListener(this);
		
		lab_webservicetest=(TextView) findViewById(R.id.lab_webservicetest);
		lab_webservicetest.setOnClickListener(this);
		
		 labsmttp_common=(TextView) findViewById(R.id.lab_smttp_common);
		 labsmttp_common.setOnClickListener(this);
		 
		 labSMTFirstScanBB = (TextView) this.findViewById(R.id.lab_SMTFirstScanBB);
		 labSMTFirstScanBB.setOnClickListener(this);
		 
		 
		 labsmtxg=(TextView) findViewById(R.id.lab_smtxg);
		 labsmtxg.setOnClickListener(this);
		 
		/************************************ deng tianjia *************************************/

		labDip_material_start.setOnClickListener(this);
		labStorageScan.setOnClickListener(this);
		labFifoScan.setOnClickListener(this);
		labSplitStore.setOnClickListener(this);
		labQuantityAdjust.setOnClickListener(this);
		labBatNumScan.setOnClickListener(this);
		labMaterialRewind.setOnClickListener(this);
		labPrepareOperate.setOnClickListener(this);
		labFeedingOnMateri.setOnClickListener(this);
		labCheMatStaTab.setOnClickListener(this);
		labRemoveMater.setOnClickListener(this);
		labSMTFirstScan.setOnClickListener(this);
		labSMTFirstScan2.setOnClickListener(this);
		labMateriInventory.setOnClickListener(this);
		labFinishWareHouseCard.setOnClickListener(this);
		labFinishWareHouseCatton.setOnClickListener(this);
		labFinishOutCard.setOnClickListener(this);
		labFinishOutCatton.setOnClickListener(this);
		labFinishDeliCard.setOnClickListener(this);
		labFinishDeliCatton.setOnClickListener(this);
		labSMTScanMaterial.setOnClickListener(this);
		labSMTStick.setOnClickListener(this);
		labSMTLade.setOnClickListener(this);
		labSMTStorage.setOnClickListener(this);
		labSMTToDIP.setOnClickListener(this);
		labDIPStartDip.setOnClickListener(this);
		labStartDIPOut.setOnClickListener(this);
		labMobileSMTTODIP.setOnClickListener(this);
		labTJeyscheck.setOnClickListener(this);
		labTJOQCCheck.setOnClickListener(this);
		labTjBoxLotSNCheck.setOnClickListener(this);
		labSMTeyeCheck.setOnClickListener(this);
		labSMTStick_Old.setOnClickListener(this);

		progressDialog = new ProgressDialog(this);

		progressDialog.setTitle(R.string.mesupdate);
		progressDialog.setMessage(getString(R.string.checkingupdate));

		// labDIPStartDip.setBackgroundColor(Color.argb(15, 155,155, 155));
		// ///背景透明度
		// labDIPStartDip.setDrawingCacheBackgroundColor(Color.argb(15, 155,
		// 155, 155));
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.list_func);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String savedUserName = sharedPref.getString("pref_user_name", "");
		if (TextUtils.isEmpty(savedUserName)) {
//			finish();
//			return;
		} else {
			this.setTitle("当前用户:" + savedUserName);
		}
		init();
		Intent bgSerIntent = new Intent(this, BackgroundService.class);
		startService(bgSerIntent);
		SoundEffectPlayService
				.initSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
		
		startActivity(new Intent(this, Shuaka.class));
		finish();
	}

	@Override
	public void refresh(Task task) {

		// switch(task.getTaskType())
		// {
		// case TaskType.MESUPDATE_CHECK:
		// if(null!=progressDialog&&progressDialog.isShowing())
		// progressDialog.dismiss();
		// Object[] reses = (Object[])task.getTaskResult();
		// boolean existNewEdi = (Boolean)reses[0];
		// MESUpdate mesUpdate = (MESUpdate)reses[1];
		// if(existNewEdi)
		// {
		// Intent updateIntent = new Intent(this, MESUpdateActivity.class);
		// updateIntent.putExtra(MESUpdateModel.EXISTNEWEDI, existNewEdi);
		// updateIntent.putExtra(MESUpdateModel.MESUPDATE, mesUpdate);
		// startActivity(updateIntent);
		// }
		// else
		// Toast.makeText(this, R.string.latest_version, 1500).show();
		// break;
		// case TaskType.MES_CHECKUPDATE_FAIL:
		// if(null!=progressDialog&&progressDialog.isShowing())
		// progressDialog.dismiss();
		// String updateFailMsg = null;
		// if(StringUtils.isEmpty(task.getTaskResult().toString()))
		// updateFailMsg = getString(R.string.mesupdate_fail);
		// else
		// updateFailMsg = task.getTaskResult().toString();
		// Toast.makeText(this, updateFailMsg, 1500).show();
		// break;
		// }

	}

	@Override
	public void onClick(View v) {
		int currentTab = 0;
		Intent targetActivity = new Intent();
		// 暂时屏蔽物料收料的功能
		if (R.id.lab_materialRewind == v.getId()) {
			Toast.makeText(this, R.string.functionShield, 1500).show();
			return;
		}
		switch (v.getId()) {
		case R.id.lab_storageScan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_1.class));
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_fifoScan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_1.class));
			targetActivity.putExtra("currentTab", ++currentTab);
			break;
		case R.id.lab_splitStore:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_1.class));
			currentTab = 2;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_quantityAdjust:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_1.class));
			currentTab = 3;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_batNum_scan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_1.class));
			currentTab = 4;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_materialRewind:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					MaterialRewindActivity.class));

			break;
		case R.id.lab_prepareOperate:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_2.class));
			currentTab = 0;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_feeding_onMaterial:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_3.class));
			currentTab = 0;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_chemater_statab:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_3.class));
			currentTab = 1;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_removeMateri:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TabHost_3.class));
			currentTab = 2;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_fini_wareHouCard:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					FinishWareHouseCardActivity.class));
			break;
		case R.id.lab_materiInvento:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					MaterialInventoryActivity.class));
			break;
		case R.id.lab_fini_warHou_catton:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					FinishWareHouseCattonActivity.class));
			break;
		case R.id.lab_finishOut_card:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					FinishOutCardActivity.class));
			break;
		case R.id.lab_finiOut_catton:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					FinishOutCattonActivity.class));
			break;
		case R.id.lab_finish_delivCard:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					FinishDeliCardActivity.class));
			break;
		case R.id.lab_finiDeli_catton:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					FinishDeliCattonActivity.class));
			break;
		case R.id.lab_SMTFirstScan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTFirstScan.class));
			targetActivity.putExtra("SMTFirstScanType", 1);
			break;
		case R.id.lab_SMTFirstScan2:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTFirstScan.class));
			targetActivity.putExtra("SMTFirstScanType", 2);
			break;
		case R.id.lab_SMTScanMaterial:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTScanMaterial.class));
			break;
		case R.id.lab_SMTStick:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTStick.class));
			break;
		case R.id.lab_SMTLade:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTLade.class));
			targetActivity.putExtra("SMTeyeCheckOrLadeToCar", 1);
			break;
		case R.id.lab_SMTeyeCheck:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTLade.class));
			targetActivity.putExtra("SMTeyeCheckOrLadeToCar", 2);
			break;
		case R.id.lab_SMTStorage:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTStorage.class));
			break;
		case R.id.lab_DIPStartDip:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					StartDip.class));
			targetActivity.putExtra("StartDIPType", 1);
			break;
		case R.id.lab_SMTToDIP:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					StartDip.class));
			targetActivity.putExtra("StartDIPType", 2);
			break;
		case R.id.lab_StartDIPOut:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					StartDip.class));
			targetActivity.putExtra("StartDIPType", 3);
			break;
		case R.id.lab_Dip_material_start:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					StartDip.class));
			targetActivity.putExtra("StartDIPType", 4);
			break;
		case R.id.lab_MobileSMTTODIP:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					MobileSMTToDIP.class));
			break;
		case R.id.lab_TJeyscheck:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					EyeCheckActivity.class));
			break;
		case R.id.lab_TJOQCCheck:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					TJOQCCheckActivity.class));
			break;
		case R.id.lab_TjBoxLotSNCheck:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					BoxLotSNCheckActivity.class));
			break;
		case R.id.lab_SMTStick_Old:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTStick_Old.class));
			break;
		/** deng后续添加 **************************************************************/
		case R.id.lab_tino:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Tino.class));
			break;
		case R.id.lab_bindpcb:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					BindPcb.class));
			break;
		case R.id.lab_lineqtyselect:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					LineQtyselect.class));
			break;
		case R.id.lab_linesninput:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					LineSnInput.class));
			break;
		case R.id.lab_smtfvmi:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SmtFvmi.class));
			break;
		case R.id.lab_sntocar:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Sntocar.class));
			break;
		case R.id.lab_tjzimiproduct:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Tjzimiproduct.class));
			break;
		case R.id.lab_tjzimipower:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Tjzimipower.class));
			break;
		case R.id.lab_tjzimirepairin:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Tjzimirepairin.class));
			break;
		case R.id.lab_tjzimirepairout:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Tjzimirepairput.class));
			break;
		case R.id.lab_file1:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					File1activity.class));
			break;	
		case R.id.lab_file2:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					File2activity.class));
			break;
		case R.id.lab_file3:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					File3activity.class));
			break;	
		case R.id.lab_file4:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					File4activity.class));
			break;	
		case R.id.lab_file5:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					File5activity.class));
			break;
			
			
		case R.id.lab_printtest:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Printtest.class));
			break;
		case R.id.lab_lenovofvmi:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Lenovofvmi.class));
			break;
		case R.id.lab_lenovotocar:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Lenovotocar.class));
			break;
		case R.id.lab_m8000cp:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					M8000cp.class));
			break;
		case R.id.lab_signin:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Signin.class));
			break;
		case R.id.lab_smttp:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SmtTP.class));
			break;
			
		case R.id.lab_smtlade3:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTLade.class));
			targetActivity.putExtra("SMTeyeCheckOrLadeToCar", 3);
			break;
		case R.id.lab_smtprinting:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Smprintting.class));
			break;
		case R.id.lab_smttp_new:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTTP_New.class));
			break;
		case R.id.lab_smttp_manual:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTTP_MANUAL.class));
			break;
		case R.id.lab_smtladecarandtiepian:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTLade.class));
			targetActivity.putExtra("SMTeyeCheckOrLadeToCar", 4);
			break;
		case R.id.lab_webservicetest:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					Webservice_test.class));
			break;
			
		case R.id.lab_SMTFirstScanBB:
			targetActivity.setComponent(new ComponentName(MainActivity.this,
					SMTFirstScanBB.class));
			targetActivity.putExtra("SMTFirstScanType", 3);
			break;
			
			case  R.id.lab_smttp_common:
				targetActivity.setComponent(new ComponentName(MainActivity.this,
						SmtTPforCommon.class));
				break;
				
			case  R.id.lab_smtxg:
				targetActivity.setComponent(new ComponentName(MainActivity.this,
						SMTXG.class));
				break;
		/** deng后续添加 **************************************************************/
			
			
		
	
			
		}
		MainActivity.this.startActivity(targetActivity);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case MES_SETTING:
			final Intent mesSetting = new Intent(this, SettingActivity.class);
			final android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
					this);
			LinearLayout loginForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.passworddialog, null);
			builder2.setView(loginForm);
			builder2.setTitle("进入设定项");
			final EditText password = (EditText) loginForm
					.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (MyApplication.getpasswrod().equals(
									password.getText().toString().trim())) {
								startActivity(mesSetting);
							}
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			break;
		case MES_EXIT:
			killMainProcess();// todo：该功能只是暂时的 日后会继续完善
			break;
		// case UPDATE:
		// Task updateCheck = new Task(this, TaskType.MESUPDATE_CHECK, null);
		// MESUpdateModel updateModel = new MESUpdateModel();
		// if(null!=progressDialog&&!progressDialog.isShowing())
		// progressDialog.show();
		// updateModel.existNewEdition(updateCheck);
		// break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(GROUP_ID, MES_SETTING, 0, getString(R.string.mes_setting));
		menu.add(GROUP_ID, MES_EXIT, 0, getString(R.string.mes_exit));
		// menu.add(GROUP_ID+1,UPDATE, 0, getString(R.string.mesupdate));
		return true;
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
								stopService(new Intent(MainActivity.this,
										BackgroundService.class));
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
}

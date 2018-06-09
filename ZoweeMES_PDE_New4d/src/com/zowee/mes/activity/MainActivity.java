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

import com.zowee.mes.Cangkuchuhuo;
import com.zowee.mes.Cangkuchuhuo2;
import com.zowee.mes.DIPMaterialOnLine;
import com.zowee.mes.DIPMaterialSignboard;
import com.zowee.mes.EquipmentMaintenanceTabhost;
import com.zowee.mes.FdRepari;
import com.zowee.mes.GdOnline;
import com.zowee.mes.GdReplace;
import com.zowee.mes.HuaweiTMisGetWeight;
import com.zowee.mes.Imeicartoonboxcheck;
import com.zowee.mes.InstrumentTabhostactivity;
import com.zowee.mes.JitmzSpilt;
import com.zowee.mes.MaterialReceiveActivity;
import com.zowee.mes.PalletOut;
import com.zowee.mes.PalletOutRoomActivity;
import com.zowee.mes.PhoneOQCBoxBindActivity;
import com.zowee.mes.R;
import com.zowee.mes.SMTFirstScan;
import com.zowee.mes.SMTLade;
import com.zowee.mes.SMTScanMaterial;
import com.zowee.mes.SMTStick;
import com.zowee.mes.SMTStorage;
import com.zowee.mes.SmtChaoling;
import com.zowee.mes.SmtIPQC;
import com.zowee.mes.Smtipqcmz;
import com.zowee.mes.Smtjitchaolingkanban;
import com.zowee.mes.Smtjitshouliao;
import com.zowee.mes.Smtpeoplescan;
import com.zowee.mes.SmtqtyChange;
import com.zowee.mes.Smtreceivematerial;
import com.zowee.mes.Smtshangliaoslot;
import com.zowee.mes.TJchuhuoscan;
import com.zowee.mes.Tjchuhuo_yy;
import com.zowee.mes.XbcTabhost;
import com.zowee.mes.ZdhPandian;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.SoundEffectPlayService;
/**
 * @author Administrator
 * @description 该activity主要用于界面功能的显示
 */

//2014-04-11 version 15 增加物料领料扫描
//2014-04-08 version 14 扫描库位变为仓库号，手动选择仓库号  


public class MainActivity extends Activity implements CommonActivityInterface,OnClickListener 
{
	private TextView labStorageScan ; 
	private TextView labFifoScan;
	private TextView labSplitStore;
	private TextView labQuantityAdjust;
	private TextView labBatNumScan;
	private TextView labMaterialRewind;
	private TextView labPrepareOperate;
	private TextView labFeedingOnMateri;
	private TextView labCheMatStaTab;
	//查料站表
	private TextView labRemoveMater;
	//下料
	private TextView labMateriInventory;
	private TextView labFinishWareHouseCard;
	private TextView labFinishWareHouseCatton;
	private TextView labFinishOutCard;
	private TextView labFinishOutCatton;
	private TextView labFinishDeliCard;
	private TextView labFinishDeliCatton;
	private TextView labSMTFirstScan;
	private TextView labSMTScanMaterial;
	private TextView labHwTMisGetCartonWeitht;
	private TextView labSMTStick;
	private TextView labSMTLade;
	private TextView labSMTStorage;
	private TextView labDipMaterialOnline;
	private TextView labDipMaterSignboard;
	private TextView labphoneOqcboxbind;
	private TextView labPalletOutRoom; 
	private TextView labmaterialReceive;
	/**********************************************************/
	private TextView labimeicartoonboxcheck;
	private TextView labequipmentmaintenance;//设备维护
	private TextView labequipmentselect; //维护情况查询
	private TextView labsmtipqc; //
	private   TextView labtjchuhuoscan; //
	private   TextView labinstrumentlinesetup; //
	private   TextView labinstrumentremovebinding; //
	private   TextView labinstrumentmolinkworkcenter; //
	private   TextView labinstrumentquery; //
	
	
	private  TextView labtjyy;
	
	private  TextView  labsmtreceivematerial;
	
	private  TextView  labsmtjitchaoling;
	
	private  TextView  labsmtshangliaoslot;
	

	private  TextView  labsmtjitshouliao;
	
	
	private  TextView  labhuajipaoliao;
	
	private  TextView  labpalletout;
	
	private  TextView  labsmtpeoplescan;
	
	private  TextView  labjitchaolingkanban;
	
	private  TextView  labjitmzspilt;
	
	private  TextView  labzdhpd;
	
	
	private  TextView  labfdrepari;
	private  TextView  labgdreplace;
	private  TextView  labgdonline;
	
	private  TextView  labsanzhanban;
	private  TextView  labsanzhanban2;
	
	
	private  TextView  labxbcbinding;
	private  TextView  labxbcremovebinding;
	private TextView  labxbcunbindingsingle;
	
	
	private TextView  labsmtipqcmz;
	
	/*********************************************************/
	private final int MES_SETTING = Menu.FIRST;
	private final int MES_EXIT = Menu.FIRST+1;
	//private final int UPDATE = Menu.FIRST+2;
	private final int GROUP_ID = 1;
	private ProgressDialog progressDialog;
	@Override
	public void init()
	{
		labStorageScan = (TextView)this.findViewById(R.id.lab_storageScan);
		labFifoScan = (TextView)this.findViewById(R.id.lab_fifoScan);
		labSplitStore = (TextView)this.findViewById(R.id.lab_splitStore);
		labQuantityAdjust = (TextView)this.findViewById(R.id.lab_quantityAdjust);
		labBatNumScan = (TextView)this.findViewById(R.id.lab_batNum_scan);
		labMaterialRewind = (TextView)this.findViewById(R.id.lab_materialRewind);
		labPrepareOperate = (TextView)this.findViewById(R.id.lab_prepareOperate);
		labFeedingOnMateri = (TextView)this.findViewById(R.id.lab_feeding_onMaterial);
		labCheMatStaTab = (TextView)this.findViewById(R.id.lab_chemater_statab);
		labRemoveMater = (TextView)this.findViewById(R.id.lab_removeMateri);
		labMateriInventory = (TextView)this.findViewById(R.id.lab_materiInvento);
		labFinishWareHouseCard = (TextView)this.findViewById(R.id.lab_fini_wareHouCard);
		labFinishWareHouseCatton = (TextView)this.findViewById(R.id.lab_fini_warHou_catton);
		labFinishOutCard = (TextView)this.findViewById(R.id.lab_finishOut_card);
		labFinishOutCatton = (TextView)this.findViewById(R.id.lab_finiOut_catton);
		labFinishDeliCard = (TextView)this.findViewById(R.id.lab_finish_delivCard);
		labFinishDeliCatton = (TextView)this.findViewById(R.id.lab_finiDeli_catton);
		labSMTFirstScan     = (TextView)this.findViewById(R.id.lab_SMTFirstScan);
		labSMTScanMaterial  = (TextView)this.findViewById(R.id.lab_SMTScanMaterial);
		labSMTStick        = (TextView)this.findViewById(R.id.lab_SMTStick);
		labSMTLade      = (TextView)this.findViewById(R.id.lab_SMTLade);
		labSMTStorage   = (TextView)this.findViewById(R.id.lab_SMTStorage);
		labDipMaterialOnline = (TextView)findViewById(R.id.lab_DipMaterialOnline);
		labDipMaterSignboard = (TextView)findViewById(R.id.lab_DipMaterSignboard);
		labHwTMisGetCartonWeitht = (TextView)findViewById(R.id.lab_HwTMisGetCartonWeitht);
		labphoneOqcboxbind = (TextView)findViewById(R.id.lab_phoneOqcboxbind);
		labPalletOutRoom = (TextView)findViewById(R.id.lab_PalletOutRoom);
		labmaterialReceive = (TextView)findViewById(R.id.lab_materialReceive);
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
		labDipMaterialOnline.setOnClickListener(this);
		labDipMaterSignboard.setOnClickListener(this);
		labphoneOqcboxbind.setOnClickListener(this);
		labHwTMisGetCartonWeitht.setOnClickListener(this);
		labPalletOutRoom.setOnClickListener(this);
		labmaterialReceive.setOnClickListener(this);
		progressDialog = new ProgressDialog(this);
		
		
		/**********************************************************/
		 labimeicartoonboxcheck=(TextView) findViewById(R.id.lab_imeicartoonboxcheeck);
		 labimeicartoonboxcheck.setOnClickListener(this);
		 labequipmentmaintenance=(TextView) findViewById(R.id.lab_equipmentmaintenance);
		 labequipmentmaintenance.setOnClickListener(this);
		 labequipmentselect=(TextView) findViewById(R.id.lab_maintenanceselect);
		 labequipmentselect.setOnClickListener(this);
		 labsmtipqc=(TextView) findViewById(R.id.lab_smtipqc);
		 labsmtipqc.setOnClickListener(this);
		 labtjchuhuoscan=(TextView) findViewById(R.id.lab_tjchuhuoscan);
		 labtjchuhuoscan.setOnClickListener(this);
		 labinstrumentlinesetup=(TextView) findViewById(R.id.lab_instrument_linesetup);
		 labinstrumentlinesetup.setOnClickListener(this);
		 labinstrumentremovebinding=(TextView) findViewById(R.id.lab_instrument_removebinding);
		 labinstrumentremovebinding.setOnClickListener(this);
		 labinstrumentmolinkworkcenter=(TextView) findViewById(R.id.lab_instrument_molinkworkcenter);
		 labinstrumentmolinkworkcenter.setOnClickListener(this);
		 labinstrumentquery=(TextView) findViewById(R.id.lab_instrument_query);
		 labinstrumentquery.setOnClickListener(this);
		 labtjyy=(TextView) findViewById(R.id.lab_tjyy);
		 labtjyy.setOnClickListener(this);
		 labsmtreceivematerial=(TextView) findViewById(R.id.lab_smt_shouliao);
		 labsmtreceivematerial.setOnClickListener(this);
		 labsmtjitchaoling=(TextView) findViewById(R.id.lab_smt_jitchaoling);
		 labsmtjitchaoling.setOnClickListener(this);
		 labsmtshangliaoslot=(TextView) findViewById(R.id.lab_smt_shangliaoslot);
		 labsmtshangliaoslot.setOnClickListener(this);
		 labsmtjitshouliao=(TextView) findViewById(R.id.lab_jitshouliao);
		 labsmtjitshouliao.setOnClickListener(this);
		 labhuajipaoliao=(TextView) findViewById(R.id.lab_huajipaoliao);
		 labhuajipaoliao.setOnClickListener(this);
		 labpalletout=(TextView) findViewById(R.id.lab_palletout);
		 labpalletout.setOnClickListener(this);
		 labsmtpeoplescan=(TextView) findViewById(R.id.lab_smtsckb);
		 labsmtpeoplescan.setOnClickListener(this);
		 labjitchaolingkanban=(TextView) findViewById(R.id.lab_jitchaolingkanban);
		 labjitchaolingkanban.setOnClickListener(this);
		 labjitmzspilt=(TextView) findViewById(R.id.lab_jitmzspilt);
		 labjitmzspilt.setOnClickListener(this);
		 labzdhpd=(TextView) findViewById(R.id.lab_zdhpd);
		 labzdhpd.setOnClickListener(this);
		 labfdrepari=(TextView) findViewById(R.id.lab_fdrepair);
		 labfdrepari.setOnClickListener(this);
		 labgdreplace=(TextView) findViewById(R.id.lab_gdreplace);
		 labgdreplace.setOnClickListener(this);
		 labgdonline=(TextView) findViewById(R.id.lab_gdonline);
		 labgdonline.setOnClickListener(this);
		 
		 labsanzhanban=(TextView) findViewById(R.id.lab_scanzhanban);
		 labsanzhanban.setOnClickListener(this);
		 
		 labsanzhanban2=(TextView) findViewById(R.id.lab_scanzhanban2);
		 labsanzhanban2.setOnClickListener(this);
		 
		 
		 labxbcbinding=(TextView) findViewById(R.id.lab_xbcbinding);
		 labxbcbinding.setOnClickListener(this);
		 labxbcremovebinding=(TextView) findViewById(R.id.lab_xbcremovebinding);
		 labxbcremovebinding.setOnClickListener(this);
		 labxbcunbindingsingle=(TextView) findViewById(R.id.lab_xbcunbindingsingle);
		 labxbcunbindingsingle.setOnClickListener(this);
		 
		 
		 labsmtipqcmz=(TextView) findViewById(R.id.lab_smtipqcmz);
		 labsmtipqcmz.setOnClickListener(this);
		 
		/**********************************************************/
		
		progressDialog.setTitle(R.string.mesupdate);
		progressDialog.setMessage(getString(R.string.checkingupdate));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.list_func);

		SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserName=sharedPref.getString("pref_user_name", "");
		if(TextUtils.isEmpty(savedUserName)){
			finish();
			return;
		}else{
			this.setTitle("当前用户:"+savedUserName);
		}
		init();
		Intent bgSerIntent = new Intent(this,BackgroundService.class);
		startService(bgSerIntent);
		SoundEffectPlayService.initSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
	}

	@Override
	public void refresh(Task task)
	{
          
	}

	@Override
	public void onClick(View v) 
	{
		int currentTab = 0;
		Intent targetActivity = new Intent();
		//暂时屏蔽物料盘点的功能
		if(R.id.lab_materiInvento==v.getId())
		{
			Toast.makeText(this,R.string.functionShield, 1500).show();
			return;
		}
		switch(v.getId())
		{
		case R.id.lab_storageScan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_1.class));  // ybj
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_fifoScan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_1.class));
			targetActivity.putExtra("currentTab", ++currentTab);
			break;
		case R.id.lab_splitStore:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_1.class));
			currentTab = 2;
			targetActivity.putExtra("currentTab",currentTab);
			break;
		case R.id.lab_quantityAdjust:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_1.class));
			currentTab = 3;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_batNum_scan:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_1.class));
			currentTab = 4;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_materialRewind:
			targetActivity.setComponent(new ComponentName(MainActivity.this, MaterialRewindActivity.class));

			break;
		case R.id.lab_prepareOperate:
			targetActivity.setComponent(new ComponentName(MainActivity.this, TabHost_2.class));
			currentTab = 0;
			targetActivity.putExtra("currentTab", currentTab);
			break;
		case R.id.lab_feeding_onMaterial:
			targetActivity.setComponent(new ComponentName(MainActivity.this,FeedOnMateriActivity.class));
			currentTab = 0;
			targetActivity.putExtra("currentTab",currentTab);
			break;
		case R.id.lab_chemater_statab:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_3.class));
			currentTab = 1;
			targetActivity.putExtra("currentTab",currentTab);
			break;
		case R.id.lab_removeMateri:
			targetActivity.setComponent(new ComponentName(MainActivity.this,TabHost_3.class));
			currentTab = 2;
			targetActivity.putExtra("currentTab",currentTab);
			break;
		case R.id.lab_fini_wareHouCard:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FinishWareHouseCardActivity.class));
			break;
		case R.id.lab_materiInvento:
			targetActivity.setComponent(new ComponentName(MainActivity.this, MaterialInventoryActivity.class));
			break;
		case R.id.lab_fini_warHou_catton:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FinishWareHouseCattonActivity.class));
			break;
		case R.id.lab_finishOut_card:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FinishOutCardActivity.class));
			break;
		case R.id.lab_finiOut_catton:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FinishOutCattonActivity.class));
			break;
		case R.id.lab_finish_delivCard:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FinishDeliCardActivity.class));
			break;
		case R.id.lab_finiDeli_catton:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FinishDeliCattonActivity.class));
			break;
		case R.id.lab_SMTFirstScan:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SMTFirstScan.class));		
			break;
		case R.id.lab_SMTScanMaterial:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SMTScanMaterial.class));	
			break;
		case R.id.lab_SMTStick:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SMTStick.class));	
			break;
		case R.id.lab_SMTLade:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SMTLade.class));	
			break;
		case R.id.lab_SMTStorage:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SMTStorage.class));	
			break;
		case R.id.lab_DipMaterialOnline:
			targetActivity.setComponent(new ComponentName(MainActivity.this, DIPMaterialOnLine.class));	
			break;
		case R.id.lab_DipMaterSignboard:
			targetActivity.setComponent(new ComponentName(MainActivity.this, DIPMaterialSignboard.class));	
			break;
		case R.id.lab_HwTMisGetCartonWeitht:
			targetActivity.setComponent(new ComponentName(MainActivity.this, HuaweiTMisGetWeight.class));	
			break;
		case R.id.lab_phoneOqcboxbind:
			targetActivity.setComponent(new ComponentName(MainActivity.this, PhoneOQCBoxBindActivity.class));	
			break;
		case R.id.lab_PalletOutRoom:
			targetActivity.setComponent(new ComponentName(MainActivity.this, PalletOutRoomActivity.class));	
			break;
		case R.id.lab_materialReceive:
			targetActivity.setComponent(new ComponentName(MainActivity.this, MaterialReceiveActivity.class));			
			break;
/**********************************************************************************/
		case R.id.lab_imeicartoonboxcheeck:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Imeicartoonboxcheck.class));			
			break;	
		case R.id.lab_equipmentmaintenance:
			targetActivity.setComponent(new ComponentName(MainActivity.this, EquipmentMaintenanceTabhost.class));
			targetActivity.putExtra("equipment", "1");
			break;	
		case R.id.lab_maintenanceselect:
			targetActivity.setComponent(new ComponentName(MainActivity.this, EquipmentMaintenanceTabhost.class));			
			targetActivity.putExtra("equipment", "2");
			break;	
		case R.id.lab_smtipqc:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SmtIPQC.class));			
			break;
		case R.id.lab_tjchuhuoscan:
			targetActivity.setComponent(new ComponentName(MainActivity.this, TJchuhuoscan.class));			
			break;
		case R.id.lab_instrument_linesetup:
			targetActivity.setComponent(new ComponentName(MainActivity.this, InstrumentTabhostactivity.class));			
			targetActivity.putExtra("instrument", "1");
			break;
		case R.id.lab_instrument_removebinding:
			targetActivity.setComponent(new ComponentName(MainActivity.this, InstrumentTabhostactivity.class));			
			targetActivity.putExtra("instrument", "2");
			break;
		case R.id.lab_instrument_molinkworkcenter:
			targetActivity.setComponent(new ComponentName(MainActivity.this, InstrumentTabhostactivity.class));			
			targetActivity.putExtra("instrument", "3");
			break;
		case R.id.lab_instrument_query:
			targetActivity.setComponent(new ComponentName(MainActivity.this, InstrumentTabhostactivity.class));			
			targetActivity.putExtra("instrument", "4");
			break;
			
		case R.id.lab_tjyy:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Tjchuhuo_yy.class));			
			break;
		
		case R.id.lab_smt_shouliao:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Smtreceivematerial.class));			
		 break;
			
		case R.id.lab_smt_jitchaoling:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SmtChaoling.class));			
		 break;
		 
		case R.id.lab_smt_shangliaoslot:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Smtshangliaoslot.class));			
		 break;
		 
		case R.id.lab_jitshouliao:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Smtjitshouliao.class));			
		 break;
		 
		 
		case R.id.lab_huajipaoliao:
			targetActivity.setComponent(new ComponentName(MainActivity.this, SmtqtyChange.class));			
		 break;
		 
		case R.id.lab_palletout:
			targetActivity.setComponent(new ComponentName(MainActivity.this, PalletOut.class));			
		 break;
		 
		case R.id.lab_smtsckb:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Smtpeoplescan.class));			
			break;
			
		case R.id.lab_jitchaolingkanban:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Smtjitchaolingkanban.class));			
			break;
			
		case R.id.lab_jitmzspilt:
			targetActivity.setComponent(new ComponentName(MainActivity.this, JitmzSpilt.class));			
			break;
			
			
		case R.id.lab_zdhpd:
			targetActivity.setComponent(new ComponentName(MainActivity.this, ZdhPandian.class));			
			break;
			
		case R.id.lab_fdrepair:
			targetActivity.setComponent(new ComponentName(MainActivity.this, FdRepari.class));			
			break;
			
			
		case R.id.lab_gdreplace:
			targetActivity.setComponent(new ComponentName(MainActivity.this, GdReplace.class));			
			break;
			
		case R.id.lab_gdonline:
			targetActivity.setComponent(new ComponentName(MainActivity.this, GdOnline.class));			
			break;
			
		case R.id.lab_scanzhanban:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Cangkuchuhuo.class));			
			break;	
			
		case R.id.lab_scanzhanban2:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Cangkuchuhuo2.class));			
			break;	
			
			
		case R.id.lab_xbcbinding:
			targetActivity.setComponent(new ComponentName(MainActivity.this, XbcTabhost.class));
			targetActivity.putExtra("xbc", "1");
			break;	
			
		case R.id.lab_xbcremovebinding:
			targetActivity.setComponent(new ComponentName(MainActivity.this, XbcTabhost.class));	
			targetActivity.putExtra("xbc", "2");
			break;	
			
			
		case R.id.lab_xbcunbindingsingle:
			targetActivity.setComponent(new ComponentName(MainActivity.this, XbcTabhost.class));	
			targetActivity.putExtra("xbc", "3");
			break;
			
			
		case R.id.lab_smtipqcmz:
			targetActivity.setComponent(new ComponentName(MainActivity.this, Smtipqcmz.class));			
		break;	
			
			
/*************************************************************************************/
			
		}

		MainActivity.this.startActivity(targetActivity);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
	
		switch(item.getItemId())
		{
		case MES_SETTING:
			final Intent mesSetting = new Intent(this, SettingActivity.class);
			final android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			LinearLayout loginForm = (LinearLayout)getLayoutInflater().inflate( R.layout.passworddialog, null);
			builder2.setView(loginForm);
			builder2.setTitle("进入设定项");
			final EditText password = (EditText)loginForm.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("确认", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(MyApplication.getpasswrod().equals(password.getText().toString().trim())){
						startActivity(mesSetting);	
					}					
				}		
			});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();	
			break;
		case MES_EXIT:				
			killMainProcess();//todo：该功能只是暂时的 日后会继续完善
			break;
			//			case UPDATE:
			//				Task updateCheck = new Task(this, TaskType.MESUPDATE_CHECK, null);
			//				MESUpdateModel updateModel = new MESUpdateModel();
			//				if(null!=progressDialog&&!progressDialog.isShowing())
			//					progressDialog.show();
			//				updateModel.existNewEdition(updateCheck);
			//				break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		menu.add(GROUP_ID,MES_SETTING, 0, getString(R.string.mes_setting));
		menu.add(GROUP_ID,MES_EXIT, 0, getString(R.string.mes_exit));
		//menu.add(GROUP_ID+1,UPDATE, 0, getString(R.string.mesupdate));
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
				stopService(new Intent(MainActivity.this,BackgroundService.class));
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}
}

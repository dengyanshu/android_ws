package com.zowee.mes;

import java.util.HashMap;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
//import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.SMTFirstOperationModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class SMTFirstScan extends CommonActivity implements
		View.OnClickListener, OnItemSelectedListener {

	private String MOId;
	private String ProductId;
	private String MOName;
	private String PCBType;
	private String ProductName;
	private String VendorPCBSN;
	private String PCBSN;
	private String LotSNList = "";
	private int onePCBQuantity = 0;
	private int onePCBFactQuantity = 0;
	private int scancount = 0;
	private boolean getMOInfoOk;
	private HashMap<String, String> taskdata;
	private SMTFirstOperationModel smtFirstOperationModel;
	private static String msg = null;

	private EditText EditProductSN;
	private EditText EditWONumber;
	private EditText EditPCBOnePacketRemainQuantity;
	private EditText EditVendorPCBSN;
	private EditText EditPCBSN;
	private EditText EditScanInfo;
	private EditText EditProductNumber;
	private EditText EditOnePCBQuantity;
	private EditText EditWOFinishQuantity;
	private EditText EditCurrentPCBSerialnumber;
	private TextView TextSNLength;
	private Spinner spinnerSNLength;
	// private EditText EditWOSideAQuantity;
	// private EditText EditWOSideBQuantity;
	private CheckBox checkshowAllInfo;
	private Button ButtonGetWOInfo;
	private Button ButtonScanNextPacket;
	private Button ButtonResetScan;
	private Spinner spinnerPCBType;
	private Spinner spinnerPCBFactQuantity;
	// private TextView TextPCBFactQuantity;
	private TextView TextPCBtype;
	private boolean ShowAllInfo = false;
	private int SNLength = 8;
	private int SMTFirstScanType = 1;

	
	//.....deng xiugai........//
	private  EditText  tinosn;
	private  String tinosnstring;
	private  Button  tinobutton;
	//.....deng xiugai........//
	
	private TextView Text_PCBT_QTY;
	private Spinner spinner_PCBTQTY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smtfirst_scan);

		Intent intent = getIntent();
		SMTFirstScanType = intent.getIntExtra("SMTFirstScanType", 1);
		this.setTitleColor(Color.GREEN);
		init();
		if (SMTFirstScanType == 1) // Normal
		{
			this.setTitle("SMT 上线扫描");
			taskdata.put("customer", "Common");
		} else if (SMTFirstScanType == 2) {
			this.setTitle("SMT 上线扫描&扣料&启动  --oppo--");

			taskdata.put("customer", "oppo");
		}

	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.SMT_FirstScan;
		super.init();
		msg = "Smt firt scan";
		super.progressDialog.setMessage(msg);
		smtFirstOperationModel = new SMTFirstOperationModel();
		EditWONumber = (EditText) findViewById(R.id.Edit_WONumber);
		EditPCBOnePacketRemainQuantity = (EditText) findViewById(R.id.Edit_PCBOnePacketRemainQuantity);
		// TextPCBFactQuantity = (TextView)
		// findViewById(R.id.Text_PCBFactQuantity);
		EditVendorPCBSN = (EditText) findViewById(R.id.Edit_VendorPCBSN);
		EditPCBSN = (EditText) findViewById(R.id.Edit_PCBSN);

		EditScanInfo = (EditText) findViewById(R.id.Edit_SMTFirstInfo);
		EditProductNumber = (EditText) findViewById(R.id.Edit_ProductNumber);
		EditOnePCBQuantity = (EditText) findViewById(R.id.Edit_OnePCBQuantity);
		EditWOFinishQuantity = (EditText) findViewById(R.id.Edit_WOFinishQuantity);
		// EditWOSideAQuantity = (EditText)
		// findViewById(R.id.Edit_WOSideAQuantity);
		// EditWOSideBQuantity = (EditText)
		// findViewById(R.id.Edit_WOSideBQuantity);
		ButtonGetWOInfo = (Button) findViewById(R.id.Button_GetWOInfo);
		ButtonScanNextPacket = (Button) findViewById(R.id.Button_ScanNextPacket);
		ButtonResetScan = (Button) findViewById(R.id.Button_ResetScan);
		TextPCBtype = (TextView) findViewById(R.id.Text_PCBtype);
		checkshowAllInfo = (CheckBox) findViewById(R.id.check_showAllInfo);
		checkshowAllInfo.setOnClickListener(this);
		EditCurrentPCBSerialnumber = (EditText) findViewById(R.id.Edit_CurrentPCBSerialnumber);

		TextSNLength = (TextView) findViewById(R.id.Text_SNLength);

		this.EditProductSN = (EditText) findViewById(R.id.Edit_ProductSN);
		EditProductSN.setOnClickListener(this);

		// TextPCBFactQuantity.setOnClickListener(this);
		TextSNLength.setOnClickListener(this);
		EditVendorPCBSN.setOnClickListener(this);
		EditPCBSN.setOnClickListener(this);
		ButtonGetWOInfo.setOnClickListener(this);
		// TextPCBtype.setOnClickListener(this);
		ButtonScanNextPacket.setOnClickListener(this);

		ArrayAdapter<CharSequence> adapterSN_length = ArrayAdapter
				.createFromResource(this, R.array.adapterSNlength,
						android.R.layout.simple_spinner_item);
		adapterSN_length
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSNLength = (Spinner) findViewById(R.id.spinner_SNLength);
		spinnerSNLength.setAdapter(adapterSN_length);
		spinnerSNLength.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapterPCBtype = ArrayAdapter
				.createFromResource(this, R.array.SMTFirstScanPCBType,
						android.R.layout.simple_spinner_item);
		adapterPCBtype
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPCBType = (Spinner) findViewById(R.id.spinner_PCBType);
		spinnerPCBType.setAdapter(adapterPCBtype);
		spinnerPCBType.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapterPCBQuantity = ArrayAdapter
				.createFromResource(this, R.array.SMTFirstScanPCBQuantity,
						android.R.layout.simple_spinner_item);
		adapterPCBQuantity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerPCBFactQuantity = (Spinner) findViewById(R.id.spinner_PCBFactQuantity);
		spinnerPCBFactQuantity.setAdapter(adapterPCBQuantity);
		spinnerPCBFactQuantity.setOnItemSelectedListener(this);

		//2015-4-8 by chenyun
		spinner_PCBTQTY = (Spinner) findViewById(R.id.spinner_PCBT_QTY);
		Text_PCBT_QTY = (TextView) findViewById(R.id.Text_PCBT_QTY);
		spinner_PCBTQTY.setVisibility(View.GONE);
		Text_PCBT_QTY.setVisibility(View.GONE);
		
		taskdata = new HashMap<String, String>();

		final Builder builder = new AlertDialog.Builder(this);

		ButtonResetScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);
				builder.setTitle("重置扫描");
				builder.setMessage("请确认是否要重置扫描");
				builder.setPositiveButton("确定"
				// 为列表项的单击事件设置监听器
						, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								clearScanedInfo();
								EditScanInfo.setText("");
							}
						});
				// 为对话框设置一个“取消”按钮
				builder.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();
			}
		});
		
		
		//.....deng xiugai.....................................................................................................//
		  tinosn=(EditText) findViewById(R.id.smtfirst_scan_tinoedittext);
		  tinosn.setOnKeyListener(myTinosnkeylister);
		 
		  tinobutton=(Button) findViewById(R.id.smtfirst_scan_tinobutton);
		  tinobutton.setOnClickListener(new  View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			tinosn.setEnabled(true);
			tinosn.setText("");
		 }
		});
			
	}
	 OnKeyListener   myTinosnkeylister=new  OnKeyListener  (){

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg1==KeyEvent.KEYCODE_ENTER&&arg2.getAction()==KeyEvent.ACTION_DOWN){
					tinosn.setEnabled(false);
				}
				return false;
			}
			
		};
		//.....deng xiugai..............................................................................................//
	private void clearScanedInfo() {
		scancount = 0;
		LotSNList = "";
		EditCurrentPCBSerialnumber.setText("0");
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("确定退出SMT上线扫描吗?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(SMTFirstScan.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		int Pcbremain = -1; // 每包PCB乘余数量
		switch (task.getTaskType()) {
		case TaskType.SMT_ScanSnGetWO:
			closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (ShowAllInfo)
					super.logDetails(EditScanInfo,
							"Success_Msg:" + getdata.toString());
				if (getdata.get("Error") == null) {

					if (Integer.parseInt(getdata.get("result").toString()) == 0) {
						if (getdata.containsKey("MOName")) {
							getwoinfo(getdata.get("MOName").toString().trim());
							this.EditProductSN.setText("");
						}
					} else {
						this.EditProductSN.setText("");
						if (getdata.containsKey("ReturnMsg") && !ShowAllInfo)
							super.logDetails(EditScanInfo,
									getdata.get("ReturnMsg").toString());
					}
				} else {
					if (!ShowAllInfo)
						super.logDetails(EditScanInfo, getdata.toString());
				}

			}
			break;
		case TaskType.SMT_FirstCancleScan:
			break;
		case TaskType.SMT_FirstScan:
			closeProDia();
			clearScanedInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (ShowAllInfo)
					super.logDetails(EditScanInfo,
							"Success_Msg:" + getdata.toString());
				if (getdata.get("Error") == null) {

					if (Integer.parseInt(getdata.get("result").toString()) == 0) {
						if (getdata.containsKey("PCBQTY")) {
							Pcbremain = Integer.parseInt(getdata.get("PCBQTY")
									.trim().toString());
						}
						this.EditPCBOnePacketRemainQuantity.setText(Pcbremain
								+ "");
						this.EditWOFinishQuantity.setText(getdata
								.get("MOScanCount").trim().toString());
						super.logDetails(EditScanInfo, "Success_Msg:提交成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					} else {
						if (!ShowAllInfo)
							super.logDetails(EditScanInfo,
									getdata.get("ReturnMsg"));
						if (getdata.get("ReturnMsg").toString()
								.contains("该批号无可用数量")) {
							this.EditVendorPCBSN.setEnabled(true);
							this.EditVendorPCBSN.setText("");
							this.EditPCBSN.setText("");
							this.EditVendorPCBSN.requestFocus();
						}
						if(getdata.get("ReturnMsg").contains("不足单位用量")){
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("物料漏扫描，请注意！");
							builder.setMessage("    请核对物料信息？"+getdata.get("ReturnMsg"));
							builder.setPositiveButton("确定",null);
							builder.setNegativeButton("取消", null);
							builder.create().show();
						}
					}
				} else {
					if (!ShowAllInfo)
						super.logDetails(EditScanInfo, getdata.get("Error"));
				}
			}
			if (Pcbremain == 0) {
				this.EditVendorPCBSN.setEnabled(true);
				this.EditVendorPCBSN.setText("");
				this.EditVendorPCBSN.requestFocus();
			}
			break;
		case TaskType.SMT_FirstScan_WOInfo:
			closeProDia();
			String ABSide;
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (ShowAllInfo)

					super.logDetails(EditScanInfo,
							"Success_Msg:" + getdata.toString());
				if (getdata.get("Error") == null) {

					String getMakeupCount;
					MOId = getdata.get("MOId").toUpperCase().trim();
					ProductId = getdata.get("ProductId").toUpperCase().trim();
					MOName = getdata.get("MOName").toUpperCase().trim();
					PCBType = getdata.get("PCBType").toUpperCase().trim();
					ProductName = getdata.get("ProductName").toUpperCase()
							.trim();
					getMakeupCount = getdata.get("MakeUpCount");
					if (getdata.containsKey("ABSide"))
						ABSide = getdata.get("ABSide");
					else
						ABSide = " ";
					try {
						onePCBQuantity = Integer.parseInt(getMakeupCount);
					} catch (Exception e) {
						// "PCB实际连板数量维护错误"
						return;
					}
					EditProductNumber.setText(ProductName);
					EditOnePCBQuantity.setText(getMakeupCount);
					TextPCBtype.setText(PCBType);
					this.spinnerPCBFactQuantity
							.setSelection(onePCBQuantity - 1);
					this.onePCBFactQuantity = onePCBQuantity;
					taskdata.put("MOId", MOId);
					taskdata.put("ProductId", ProductId);
					taskdata.put("MOName", MOName);
					taskdata.put("PCBType", PCBType);
					taskdata.put("ProductName", ProductName);
					taskdata.put("MakeUpCount", onePCBQuantity + "");
					if (MOId.trim().equals("") || ProductId.trim().equals("")
							|| MOName.trim().equals("")
							|| PCBType.trim().equals("")
							|| ProductName.trim().equals("")) {
						Toast.makeText(this, "获取的订单信息不完整", 3).show();
						getMOInfoOk = false;
						onePCBQuantity = 0;
					} else {
						if (PCBType.equals("阴阳板")) {
							taskdata.put("PCBSide", "阴阳板");
							this.spinnerPCBType.setSelection(2);
							spinnerPCBType.setEnabled(false);
							this.EditVendorPCBSN.requestFocus();
						} else {
							if (ABSide.equalsIgnoreCase("A")) {
								taskdata.put("PCBSide", "A");
								this.spinnerPCBType.setSelection(0);
								spinnerPCBType.setEnabled(false);
								this.EditVendorPCBSN.requestFocus();
							} else if (ABSide.equalsIgnoreCase("B")) {
								taskdata.put("PCBSide", "B");
								this.spinnerPCBType.setSelection(1);
								spinnerPCBType.setEnabled(false);
								this.EditVendorPCBSN.requestFocus();
							}
						}

						/*
						 * else if(PCBType.equals("AB面板")) // YBJ {
						 * taskdata.put("PCBSide", "A");
						 * this.spinnerPCBType.setSelection(0);
						 * spinnerPCBType.setEnabled(false);
						 * this.EditVendorPCBSN.requestFocus(); }
						 */
						EditWONumber.setText(MOName);
						this.EditProductSN.setEnabled(false);
						getMOInfoOk = true;
					}
				} else {
					if (!ShowAllInfo)

						super.logDetails(EditScanInfo, getdata.get("Error"));
					getMOInfoOk = false;
					onePCBQuantity = 0;
				}
			}
			break;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		Dialog d = null;
		switch (id) {
		case 10086:
			d = new AlertDialog.Builder(this)
					.setTitle("重新获取工单")
					.setMessage("确定要重新获取工单吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									EditProductSN.setEnabled(true);
									getMOInfoOk = false;
									EditWONumber.setText("");
									EditProductNumber.setText("");
									EditVendorPCBSN.setEnabled(true);
									EditScanInfo.setText("");
									EditVendorPCBSN.setText("");
									EditOnePCBQuantity.setText("");
									spinnerPCBType.setEnabled(true);
									EditProductSN.requestFocus();
									clearScanedInfo();
								}
							}).setNegativeButton("取消", null).show();
			break;
		case 10010:
			d = new AlertDialog.Builder(this)
					.setTitle("扫描下一包PCB")
					.setMessage("确定此包PCB已扫描完成吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									EditVendorPCBSN.setEnabled(true);
									EditVendorPCBSN.requestFocus();
									EditVendorPCBSN.setText("");
									clearScanedInfo();
								}
							}).setNegativeButton("取消", null).show();
			break;

		}
		return d;

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("DefaultLocale")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Text_SNLength:
			Toast.makeText(this, "请选择SN条码长试", 3).show();
			this.spinnerSNLength.setEnabled(true);
			break;
		case R.id.Edit_ProductSN:
			if (EditProductSN.getText().toString().trim().length() >= 8)
				SMT_ScanSnGetWO(EditProductSN.getText().toString().trim());
			this.spinnerSNLength.setSelection(EditProductSN.getText()
					.toString().trim().length() - 8);
			clearScanedInfo();
			break;
		case R.id.check_showAllInfo:
			ShowAllInfo = checkshowAllInfo.isChecked();
			break;
		case R.id.Edit_VendorPCBSN:
			if (!getMOInfoOk) {
				Toast.makeText(this, "请先获取工单信息", 3).show();
				EditVendorPCBSN.setText("");
			} else {
				VendorPCBSN = EditVendorPCBSN.getText().toString().trim();
				if (spinnerPCBType.isEnabled()) {

					Toast.makeText(this, "请先选择PCB面", 3).show();
				} else if (spinnerPCBFactQuantity.isEnabled()) {
					Toast.makeText(this, "请先选择实际连板数量", 3).show();
				} else if (VendorPCBSN.length() >= 8) {
					taskdata.put("VendorPCBSN", VendorPCBSN);
					EditPCBSN.requestFocus();
					EditVendorPCBSN.setEnabled(false);
				} else {
					EditVendorPCBSN.setText("");
				}
			}
			clearScanedInfo();
			break;
         //如果是单面板，扫描一个自动提交不要submit！！！
		case R.id.Edit_PCBSN:
			PCBSN = EditPCBSN.getText().toString().trim().toUpperCase();
			tinosnstring=tinosn.getText().toString().trim().toUpperCase();
			if(onePCBFactQuantity==1){
				if (!getMOInfoOk) {
					Toast.makeText(this, "请先获取工单信息", 3).show();
					EditPCBSN.setText("");
					return;
				}
				if (EditVendorPCBSN.getText().toString().trim().length() <= 8) {
					Toast.makeText(this, "请先扫描PCB包装条码", 3).show();
					EditPCBSN.setText("");
					return;
				}
				if (EditScanInfo.getLineCount() > 50 && scancount == 0)
					this.EditScanInfo.setText("");
				if (SNLength != PCBSN.length()
						&& !PCBSN.equalsIgnoreCase("+Submit+")) {
					this.EditPCBSN.setText("");
					Toast.makeText(this, "扫描条码长度不正确", 3).show();
					return;
				}
				if (LotSNList.contains(PCBSN)) {
					this.EditPCBSN.setText("");
					Toast.makeText(this, "重复扫描", 3).show();
					return;
				}
				if (scancount < onePCBFactQuantity) {
					if (PCBSN.equalsIgnoreCase("+Submit+")) {
						this.EditPCBSN.setText("");
						Toast.makeText(this, "扫描未完成，不能扫描提交条码！", 3).show();
						return;
					}
					scancount++;
					EditCurrentPCBSerialnumber.setText(scancount + "");
					super.logDetails(EditScanInfo, "Success_Msg:已扫描 " + scancount
							+ ":" + PCBSN);
					if (scancount == onePCBFactQuantity)
						LotSNList = LotSNList + PCBSN;
					else
						LotSNList = LotSNList + PCBSN + ",";
				}
				
				if(scancount == onePCBFactQuantity){
					taskdata.put("LotSNList", LotSNList);
					taskdata.put("tinosn", tinosnstring);
					SMTScanLable(taskdata);
				}
				
			}
			
			else{
				if (!getMOInfoOk) {
					Toast.makeText(this, "请先获取工单信息", 3).show();
					EditPCBSN.setText("");
					return;
				}
				if (EditVendorPCBSN.getText().toString().trim().length() <= 8) {
					Toast.makeText(this, "请先扫描PCB包装条码", 3).show();
					EditPCBSN.setText("");
					return;
				}
				if (EditScanInfo.getLineCount() > 50 && scancount == 0)
					this.EditScanInfo.setText("");
				if (SNLength != PCBSN.length()
						&& !PCBSN.equalsIgnoreCase("+Submit+")) {
					this.EditPCBSN.setText("");
					Toast.makeText(this, "扫描条码长度不正确", 3).show();
					return;
				}
				if (LotSNList.contains(PCBSN)) {
					this.EditPCBSN.setText("");
					Toast.makeText(this, "重复扫描", 3).show();
					return;
				}
				if (scancount < onePCBFactQuantity) {
					if (PCBSN.equalsIgnoreCase("+Submit+")) {
						this.EditPCBSN.setText("");
						Toast.makeText(this, "扫描未完成，不能扫描提交条码！", 3).show();
						return;
					}
					scancount++;
					EditCurrentPCBSerialnumber.setText(scancount + "");
					super.logDetails(EditScanInfo, "Success_Msg:已扫描 " + scancount
							+ ":" + PCBSN);
					if (scancount == onePCBFactQuantity)
						LotSNList = LotSNList + PCBSN;
					else
						LotSNList = LotSNList + PCBSN + ",";
				}
				if (scancount == this.onePCBFactQuantity) {
					if (!PCBSN.equalsIgnoreCase("+Submit+")) {
						this.EditPCBSN.setText("");
						Toast.makeText(this, "请扫描提交条码", 3).show();
						return;
					}
				}
				
				if (scancount == onePCBFactQuantity
						&& PCBSN.equalsIgnoreCase("+Submit+")) {
					taskdata.put("LotSNList", LotSNList);
					taskdata.put("tinosn", tinosnstring);
					SMTScanLable(taskdata);
				}
			}
			this.EditPCBSN.setText("");
			break;
			
		case R.id.Button_GetWOInfo:
			showDialog(10086);
			break;
		case R.id.Button_ScanNextPacket:
			showDialog(10010);
			break;
		}

	}

	private void getwoinfo(String wo) {
		Task getWOinfo = new Task(this, TaskType.SMT_FirstScan_WOInfo, wo);
		msg = "Get SMT WO info";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.getWoInfo(getWOinfo);
	}

	private void SMTScanLable(HashMap<String, String> taskdata) {
		Task smtScanLable = new Task(this, TaskType.SMT_FirstScan, taskdata);
		msg = "SMT first scan";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.SMTScanLable2(smtScanLable);
	}

	private void SMT_ScanSnGetWO(String PCBSN) {
		Task GetWOByPCBSN = new Task(this, TaskType.SMT_ScanSnGetWO, PCBSN);
		msg = "SMT Get WO Number by Scan PCBSN";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.SMT_ScanSnGetWO(GetWOByPCBSN);
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		int id = spinner.getId();
		switch (id) {
		case R.id.spinner_PCBFactQuantity:
			onePCBFactQuantity = spinnerPCBFactQuantity
					.getSelectedItemPosition() + 1;
			if (!getMOInfoOk) {
				spinnerPCBFactQuantity.setSelection(onePCBQuantity - 1);
				this.EditProductSN.requestFocus();
				Toast.makeText(this, "请先获取工单信息", 3).show();
			} else {
				if (onePCBFactQuantity > onePCBQuantity
						|| onePCBFactQuantity == 0) {
					Toast.makeText(this, "输入应不大于连板数量或等于0", 3).show();
					spinnerPCBFactQuantity.setSelection(onePCBQuantity - 1);
					onePCBFactQuantity = onePCBQuantity;
				} else {

					taskdata.put("MakeUpCount", onePCBFactQuantity + "");
				}
			}
			spinnerPCBFactQuantity.setEnabled(false);
			spinnerPCBType.setEnabled(false);
			break;
		case R.id.spinner_PCBType:
			if (!getMOInfoOk) {
				this.EditProductSN.requestFocus();
			} else {
				if (this.TextPCBtype.getText().toString().trim().equals("阴阳板")) {
					if (spinnerPCBType.getSelectedItemPosition() != 2) {
						Toast.makeText(this, "应选择阴阳板", 3).show();
						this.spinnerPCBType.setSelection(2);
					}
				} else if (this.TextPCBtype.getText().toString().trim()
						.equals("AB面板")) {
					if (!(spinnerPCBType.getSelectedItemPosition() == 0 || spinnerPCBType
							.getSelectedItemPosition() == 1)) {
						this.spinnerPCBType.setSelection(0);
						Toast.makeText(this, "应选择A或B面", 3).show();
					}

				}

				taskdata.put("PCBSide", spinnerPCBType.getSelectedItem()
						.toString().trim());
				super.logDetails(EditScanInfo, "你选择了"
						+ spinnerPCBType.getSelectedItem().toString().trim());

				spinnerPCBType.setEnabled(false);
			}
			break;
		case R.id.spinner_SNLength:
			SNLength = 8 + spinnerSNLength.getSelectedItemPosition();
			spinnerSNLength.setEnabled(false);
			break;

		}
		clearScanedInfo();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtstick, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_clear:
			EditScanInfo.setText("");
			break;

		case R.id.action_settings:
			final android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
					this);
			LinearLayout loginForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.passworddialog, null);
			builder2.setView(loginForm);
			builder2.setTitle("打开设定项");
			final EditText password = (EditText) loginForm
					.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (MyApplication.getpasswrod().equals(
									password.getText().toString().trim())) {
								spinnerPCBType.setEnabled(true);
								spinnerPCBFactQuantity.setEnabled(true);
							} else {
								spinnerPCBType.setEnabled(false);
								spinnerPCBFactQuantity.setEnabled(false);
							}
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			break;
		}
		return true;
	}

}

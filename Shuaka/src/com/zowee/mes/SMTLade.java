package com.zowee.mes;
/**
 * 1�����ƷĿ��
 * 2����ƷĿ��װ��
 * 3������һ����ƷĿ��װ�������������������
 * 4������һ��װ�����ϳ���
 * 
 * */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SMTBindPCBSN;
import com.zowee.mes.model.SMTFirstOperationModel;
import com.zowee.mes.model.SMTLadeModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.savelog.FileService;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint("ResourceAsColor")
public class SMTLade extends CommonActivity implements View.OnClickListener {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	private  TextView  zibantv;
	private  EditText  zibanedit;
	private  EditText  zibaneditnum;
	private  int num=0;
	private  LinearLayout  zibanlayout;
	private  String  lotsn="";
	
	
	private  TextView  absidetv;
	private  Spinner  absidespinner;
	private  String  abside;
	
	private TextView TextMOName;
	private TextView TextSMTCarLabel;
	private TextView labDIPStartDip;
	private EditText EditMOName;
	private EditText EditSMTCarLabel;
	private EditText EditPCBSN;
	private EditText EditLadequantiyt;
	private EditText EditScanInfo;
	private EditText EditladecarErrorCode;
	private Button ButtonLadeCar;
	private static String msg = null;
	private SMTLadeModel smtLadeModel;
	private SMTFirstOperationModel smtFirstOperationModel;
	private SMTBindPCBSN PCBSNBind;
	private String[] params;
	private String[] BindPar;
	private int OnePanelPCBQty = 0;
	private boolean BindSN = false;
	private String LotSNS = "";
	private String MOId;
	private boolean isCheckEorrCode = false;
	private String MOName;
	private String LastSN;
	private int ScannedQty = 0;
	private CheckBox checkshowAllInfo;
	private boolean getMOInfoOk;
	private boolean ShowAllInfo = false;
	private int SMTeyeCheckOrLadeToCar;
	private  Common4dModel  common4dmodel;
	

	private Button ButtonCloseCar;
	private SntocarModel sntocarmodel; // ��������
	private String makeupcount;//װ������
	private String moid; // ����ѡ�񱣴����
	private Activity sMTLadeActivity;
	// private FileService savelog = new FileService(this);
	// getFileDir() ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtlade);
		this.setTitleColor(Color.GREEN);
		Intent intent = getIntent();
		SMTeyeCheckOrLadeToCar = intent
				.getIntExtra("SMTeyeCheckOrLadeToCar", 1);
		init();
		if (SMTeyeCheckOrLadeToCar == 2) {
			this.setTitle("SMT���ƷĿ�� ");
			this.EditSMTCarLabel.setVisibility(View.GONE);
			this.TextSMTCarLabel.setVisibility(View.GONE);
			absidetv.setVisibility(View.GONE);
			absidespinner.setVisibility(View.GONE);
			zibanlayout.setVisibility(View.GONE);
			//zibantv.setVisibility(View.GONE);
			//zibanedit.setVisibility(View.GONE);
			this.ButtonLadeCar.setText("Ŀ����");
			labDIPStartDip.setText("��Ŀ������");
			
			ButtonCloseCar.setVisibility(View.GONE);
		} else if (SMTeyeCheckOrLadeToCar == 1){

			this.setTitle("SMTĿ��װ�� ");
			zibanlayout.setVisibility(View.GONE);
			absidetv.setVisibility(View.GONE);
			absidespinner.setVisibility(View.GONE);
//			zibantv.setVisibility(View.GONE);
//			zibanedit.setVisibility(View.GONE);
			
			ButtonCloseCar.setVisibility(View.GONE);
		}
		else  if(SMTeyeCheckOrLadeToCar == 3){
			setTitle("---E5��Ʒר�� ��������� Ŀ��װ��--- ");
			absidetv.setVisibility(View.GONE);
			absidespinner.setVisibility(View.GONE);
			
			ButtonCloseCar.setVisibility(View.VISIBLE);
		}
		else  if(SMTeyeCheckOrLadeToCar == 4){
			setTitle("---Ŀ��װ����Ƭ��վ--- ");
			zibanlayout.setVisibility(View.GONE);
		
			ButtonCloseCar.setVisibility(View.GONE);
		}
		
		if(SMTeyeCheckOrLadeToCar == 1||SMTeyeCheckOrLadeToCar == 2||SMTeyeCheckOrLadeToCar == 4){
			logDetails(EditScanInfo, "����������������ɨ���������Ϣ����������");
			logDetails(EditScanInfo, "������ְ�������ʾ���밴˳��ɨ��PCB�����롣");
			logDetails(EditScanInfo, "�������Ʒ����ɨ�������ɨ����Ʒ���뼴��");
			logDetails(EditScanInfo, "���Ϊ����Ʒ������ɨ�費��������ɨPCB����");
			logDetails(EditScanInfo, "���Ϊ����PCB����ɨ��PCB�����һ����");
			if (SMTeyeCheckOrLadeToCar == 1||SMTeyeCheckOrLadeToCar == 4)
				logDetails(EditScanInfo, "���������Ϣ������ճ�����Ϣ");
			logDetails(EditScanInfo, "����������ƿ����嵥����");
			logDetails(EditScanInfo, "��ʾ��");
		}
		if(SMTeyeCheckOrLadeToCar == 3){
			logDetails(EditScanInfo, "����������������ɨ���������Ϣ����������");
			logDetails(EditScanInfo, "���Ϊ��Ʒ������ɨ������������ɨ��Ӧ�ĸ������ţ���ɨ����������ɨ��Ӧ�ĸ������ţ�¼����Ϣ��ɨ\"+submit+\"�ύ");
			logDetails(EditScanInfo, "���Ϊ����Ʒ������ɨ�費��������ɨPCB�����վ");
			logDetails(EditScanInfo, "����������ƿ�����չ��������������Ϣ������ճ�����Ϣ");
			logDetails(EditScanInfo, "��ʾ��");
		}
		
		
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.SMT_LadeToCar;
		super.init();
		msg = "Smt Lade to Car";
		super.progressDialog.setMessage(msg);
		sMTLadeActivity = this;
		
		absidetv=(TextView) findViewById(R.id.Text_Ladeabside);
		absidespinner=(Spinner) findViewById(R.id.SP_SMTLadeabside);
		String[] absides={"A","B"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, absides);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		absidespinner.setAdapter(adapter);
		absidespinner.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				abside=(String) arg0.getItemAtPosition(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		common4dmodel=new Common4dModel();
		zibantv=(TextView) findViewById(R.id.Text_zibanSN);
		zibanedit =(EditText) findViewById(R.id.Edit_zibanSN);
		zibaneditnum =(EditText) findViewById(R.id.Edit_zibanSNnum);
		zibanlayout= (LinearLayout) findViewById(R.id.layout_ziban);
		

		TextMOName = (TextView) findViewById(R.id.Text_LadeMOName);
		TextSMTCarLabel = (TextView) findViewById(R.id.Text_SMTLadeCarLabel);
		EditMOName = (EditText) findViewById(R.id.Edit_SMTLadeMOName);
		EditSMTCarLabel = (EditText) findViewById(R.id.Edit_SMTLadeCarLabel);
		EditPCBSN = (EditText) findViewById(R.id.Edit_LadePCBSN);
		EditScanInfo = (EditText) findViewById(R.id.Edit_LadeMESInfo);
		ButtonLadeCar = (Button) findViewById(R.id.button_SMTLadeCar);
		EditLadequantiyt = (EditText) findViewById(R.id.Edit_ladequantity);
		EditladecarErrorCode = (EditText) findViewById(R.id.Edit_ladecarErrorCode);
		labDIPStartDip = (TextView) findViewById(R.id.lab_DIPStartDip);
		smtLadeModel = new SMTLadeModel();
		smtFirstOperationModel = new SMTFirstOperationModel();
		PCBSNBind = new SMTBindPCBSN();
		checkshowAllInfo = (CheckBox) findViewById(R.id.check_SMTLadeshowAllInfo);
		checkshowAllInfo.setOnClickListener(this);

		this.TextSMTCarLabel.setOnClickListener(this);
		this.EditMOName.setOnClickListener(this);
		this.EditPCBSN.setOnClickListener(this);
		zibanedit.setOnClickListener(this);
		this.EditSMTCarLabel.setOnClickListener(this);
		params = new String[] { "", "", "", "", "", "", "","","","" };
		BindPar = new String[] { "", "", "", "" };
		BindPar[0] = MyApplication.getAppOwner().trim();
		// this.EditScanInfo.setOnClickListener(this);
		// this.ButtonLadeCar.setOnClickListener(this);
		final Builder builder = new AlertDialog.Builder(this);
		 common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
		TextMOName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);
				builder.setTitle("���»񹤵�");
				builder.setMessage("��ȷ���Ƿ�Ҫ���»񹤵�");
				builder.setPositiveButton("ȷ��"
				// Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								EditMOName.setText("");
								EditMOName.setEnabled(true);
								EditMOName.requestFocus();
								getMOInfoOk = false;
								EditScanInfo.setText("");
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder.setNegativeButton("ȡ��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();
			}
		});

		
		ButtonCloseCar = (Button) findViewById(R.id.button_SMTCloseCar);
		this.ButtonCloseCar.setOnClickListener(this);
		sntocarmodel = new SntocarModel();
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("ȷ���˳�SMTĿ��orĿ��װ����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(SMTLade.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		switch (task.getTaskType()) {
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
						params[7]=resourceid;
						params[8]=resourcename;
					}
					String logText = "����������!��⵽��ƽ�����Դ����:[ " + resourcename
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!�����������������������2�֣���";
					logSysDetails(logText, "����");
				} else {
					logSysDetails(
							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ��������õ���Դ�����Ƿ���ȷ", "�ɹ�");
				}
				closeProDia();
			} else {
				params[7]="";
				params[8]=resourcename;
				
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ", "�ɹ�");
			}

			break;
			//оƬ����������������UI!
		case TaskType.smtladeandbinding:
			super.closeProDia();
			lotsn="";
			num=0;
			zibaneditnum.setText("0");
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						if(scantext.contains("ServerMessage:���ݲɼ��ɹ���װ����ϣ���װ��������")){
							this.EditLadequantiyt.setText(scantext.replace("ServerMessage:���ݲɼ��ɹ���װ����ϣ���װ��������",""));
						}
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ������������߹������������������", "�ɹ�");
			}
			break;
			
		case TaskType.SMT_ScanSnGetWO:
			closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (ShowAllInfo)
					logDetails(EditScanInfo, getdata.toString());
				if (getdata.get("Error") == null) {

					if (Integer.parseInt(getdata.get("result").toString()) == 0) {
						if (getdata.containsKey("MOName")) {
							EditMOName.setText(getdata.get("MOName").toString()
									.trim());
							EditMOName.setEnabled(false);
							this.EditSMTCarLabel.requestFocus();
						}
						MOId = getdata.get("MOId").toUpperCase().trim();
						MOName = getdata.get("MOName").toUpperCase().trim();
						params[2] = MOName;
						params[3] = MOId;
						getMOInfoOk = true;
						if (!ShowAllInfo)
							logDetails(EditScanInfo, "Success_Msg:"
									+ getdata.get("ReturnMsg").toString());
						if (SMTeyeCheckOrLadeToCar == 1)
							this.EditSMTCarLabel.requestFocus();
						else
							this.EditPCBSN.requestFocus();
					} else {
						this.EditMOName.setText("");
						if (!ShowAllInfo)
							logDetails(EditScanInfo, getdata.get("ReturnMsg")
									.toString());
						getMOInfoOk = false;
					}
				}

			}
			break;
		case TaskType.SMT_BindPCBSN:
			closeProDia();
			ClearBindSN();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (ShowAllInfo)
					logDetails(EditScanInfo, getdata.toString());
				if (getdata.get("Error") == null) {
					if (Integer.parseInt(getdata.get("result").toString()) == 0) {
						if (!ShowAllInfo)
							logDetails(EditScanInfo, "Success_Msg:"
									+ getdata.get("ReturnMsg").toString());
						if (SMTeyeCheckOrLadeToCar == 1)
							smtLadePCBToCar(params);
						else
							smtEyeCheck(params);
					} else {
						ButtonLadeCar.setText("Fail");
						if (!ShowAllInfo)
							logDetails(EditScanInfo, getdata.get("ReturnMsg")
									.toString());
						logDetails(EditScanInfo, "�����ʧ�ܣ������²���");
					}
				} else {
					if (ShowAllInfo)
						logDetails(EditScanInfo, getdata.get("Error")
								.toString());
				}
			}
			EditPCBSN.setEnabled(true);
			break;
			
		case TaskType.SMT_EyeCheck:
			closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (getdata.get("Error") == null) {
					if (ShowAllInfo)
						logDetails(EditScanInfo, getdata.get("ReturnMsg")
								.toString());
					if (Integer.parseInt(getdata.get("result").toString()) == 0) {
						this.ButtonLadeCar.setTextColor(Color.BLUE);
						this.ButtonLadeCar.setBackgroundColor(Color.GREEN);
						if (isCheckEorrCode) {
							EditladecarErrorCode.setText(getdata
									.get("DefectcodeSn").toString().trim()
									.toUpperCase());
							ButtonLadeCar.setText("�Ϸ���������");
						} else {
							EditladecarErrorCode.setText("");
							if (getdata.get("ReturnMsg").toString()
									.contains("�������Ϊ��00000��")) {
								this.ButtonLadeCar.setText("PASS");
							} else {
								this.ButtonLadeCar.setText("תά��OK");
								this.ButtonLadeCar
										.setBackgroundColor(Color.WHITE);
							}
							String Qty = getdata.get("ReturnMsg").toString();
							Qty = Qty.substring(Qty.indexOf("��Ŀ������") + 5,
									Qty.length());
							this.EditLadequantiyt.setText(Qty);
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
						}
						if (!ShowAllInfo)
							logDetails(EditScanInfo, "Success_Msg:"
									+ getdata.get("ReturnMsg").toString());
					} else if (Integer.parseInt(getdata.get("result")
							.toString()) == 1) // ��վ�������ϣ������°����롣
					{
						if (!ShowAllInfo)
							logDetails(EditScanInfo, getdata.get("ReturnMsg")
									.toString());
						String Qty = getdata.get("ReturnMsg").toString();
						Qty = Qty.substring(Qty.indexOf("����������Ϊ:") + 7,
								Qty.length());
						try {
							ScannedQty = 0;
							OnePanelPCBQty = Integer.parseInt(Qty);
							if (OnePanelPCBQty > 1)
								BindSN = true;
						} catch (NumberFormatException e) {
							BindSN = false;
							logDetails(EditScanInfo, e.toString());
							logDetails(EditScanInfo, "����������������");
						}
						this.ButtonLadeCar.setText("�����");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
					} else {
						if (!ShowAllInfo)
							logDetails(EditScanInfo, getdata.get("ReturnMsg")
									.toString());
						this.ButtonLadeCar.setText("Fail");
						this.ButtonLadeCar.setTextColor(Color.WHITE);
						this.ButtonLadeCar.setBackgroundColor(Color.RED);
						this.EditladecarErrorCode.setText("");
					}
				} else {
					if (ShowAllInfo)
						logDetails(EditScanInfo, getdata.get("Error")
								.toString());
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
			}
			isCheckEorrCode = false;
			this.EditPCBSN.setEnabled(true);
			this.EditPCBSN.selectAll();
			break;
			
		case TaskType.SMT_LadeToCar:
			closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if (getdata.get("Error") == null) {
					if (ShowAllInfo)
						logDetails(EditScanInfo, getdata.get("ReturnMsg")
								.toString());
					if (Integer.parseInt(getdata.get("result").toString()) == 0) {
						this.ButtonLadeCar.setTextColor(Color.BLUE);
						this.ButtonLadeCar.setBackgroundColor(Color.GREEN);
						if (isCheckEorrCode) {
							EditladecarErrorCode.setText(getdata
									.get("DefectcodeSn").toString().trim()
									.toUpperCase());
							ButtonLadeCar.setText("�Ϸ���������");
						} else {
							EditladecarErrorCode.setText("");
							if (params[5].equalsIgnoreCase("+submit+")
									&& getdata.get("ReturnMsg").toString()
											.contains("�����رճɹ�")) {
								logDetails(EditScanInfo, "Success_Msg:"
										+ params[4] + "�����رճɹ�");
								this.ButtonLadeCar.setText("װ�����");
								EditSMTCarLabel.setText("");
								EditSMTCarLabel.setEnabled(true);
							} else if (getdata.get("ReturnMsg").toString()
									.contains("���Ϊά��״̬")) {

								if (!ShowAllInfo)
									logDetails(EditScanInfo, "Success_Msg:"
											+ getdata.get("ReturnMsg")
													.toString());
								this.ButtonLadeCar.setText("תά��OK");
								this.ButtonLadeCar
										.setBackgroundColor(Color.WHITE);
							} else {
								this.ButtonLadeCar.setText("PASS");
								logDetails(EditScanInfo, "Success_Msg:"
										+ params[5] + "װ���ɹ�");
								this.EditLadequantiyt
										.setText(getdata
												.get("ReturnMsg")
												.toString()
												.replace(
														"ServerMessage:���ݲɼ��ɹ���װ����ϣ���װ��������",
														""));
							}
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
						}
					} else if (Integer.parseInt(getdata.get("result")
							.toString()) == 1) // ��վ�������ϣ������°����롣
					{
						if (!ShowAllInfo)
							logDetails(EditScanInfo, getdata.get("ReturnMsg")
									.toString());
						String Qty = getdata.get("ReturnMsg").toString();
						Qty = Qty.substring(Qty.indexOf("����������Ϊ:") + 7,
								Qty.length());
						try {
							ScannedQty = 0;
							OnePanelPCBQty = Integer.parseInt(Qty);
							if (OnePanelPCBQty > 1)
								BindSN = true;
						} catch (NumberFormatException e) {
							BindSN = false;
							logDetails(EditScanInfo, e.toString());
							logDetails(EditScanInfo, "����������������");
						}
						this.ButtonLadeCar.setText("�����");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
					} else {
						if (!ShowAllInfo)
							logDetails(EditScanInfo, getdata.get("ReturnMsg")
									.toString());
						this.ButtonLadeCar.setText("Fail");
						this.ButtonLadeCar.setTextColor(Color.WHITE);
						this.ButtonLadeCar.setBackgroundColor(Color.RED);
						this.EditladecarErrorCode.setText("");
					}
				} else {
					if (ShowAllInfo)
						logDetails(EditScanInfo, getdata.get("Error")
								.toString());
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
			}
			isCheckEorrCode = false;
			this.EditPCBSN.setEnabled(true);
			this.EditPCBSN.selectAll();
			break;
			// ��ȡ����ID
		case TaskType.sntocargetmo:
				String monamemainsn = EditMOName.getText().toString().toUpperCase().trim();
				if (null != task.getTaskResult()) {
							getdata = (HashMap<String, String>) task.getTaskResult();
							Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
							if (getdata.get("Error") == null) {
								
								getMOInfoOk = true;
								//String lot = SN.getText().toString();
								String moname = getdata.get("MOName");
								String moproduct = getdata.get("ProductDescription");
								String momaterial = getdata.get("ProductName");
								if (getdata.containsKey("MakeUpCount")) {

									makeupcount = getdata.get("MakeUpCount");
								}
								//moid = getdata.get("MOId");
								MOId = getdata.get("MOId").toUpperCase().trim();
								MOName = getdata.get("MOName").toUpperCase().trim();
								params[2] = MOName;
								params[3] = MOId;
								EditMOName.setText(moname);
								//editMOMaterial.setText(momaterial);
								//editMOProduct.setText(moproduct);
								EditLadequantiyt.setText(makeupcount);
								EditMOName.setEnabled(false);
								String scantext = "ͨ�����SN��[" + monamemainsn + "]�ɹ��Ļ�ù���:"
										+ moname + ",��Ʒ��Ϣ:" + moproduct + ",��Ʒ�Ϻţ�"
										+ momaterial + "!";
								logSysDetails(scantext, "�ɹ�");
								EditSMTCarLabel.requestFocus();
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

							} else {
								logSysDetails(
										"û�л�ȡ������"
												+ getdata.get("Error"), "�ɹ�");
							}
							closeProDia();
						} else {
							logSysDetails(moid + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
						}

						break;
		case TaskType.sntocargetcar:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");
						EditSMTCarLabel.setFocusable(true);
						EditSMTCarLabel.requestFocus();
						EditSMTCarLabel.setText("");
					} else {
						String str = getdata.get("I_ReturnMessage");
						if (getdata.containsKey("InCarQty")) {
							String carsum = getdata.get("InCarQty");
							EditLadequantiyt.setText(carsum);
						}
						EditSMTCarLabel.setEnabled(false);
						if(EditPCBSN.getText().toString().equals("")){
							EditPCBSN.requestFocus();
						}
						else{
							
							EditPCBSN.requestFocus();
						}
						logSysDetails("����sn[ "
								+ EditSMTCarLabel.getText().toString().toUpperCase()
										.trim() + "]��mesУ��ɹ���" + str, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
				}

				else {
					EditLadequantiyt.setText(EditLadequantiyt.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "��MES������Ϣ", 5).show();
			}

			closeProDia();
			break;
		}

	}

	private void ClearBindSN() {
		BindSN = false;
		OnePanelPCBQty = 0;
		LotSNS = "";
		LastSN = "";
		ScannedQty = 0;
		ButtonLadeCar.setText("����ɨ��");
	}

	@Override
	public void onClick(View v) {
		
		// TODO Auto-generated method stub
		switch (v.getId()) {
		

		case R.id.check_SMTLadeshowAllInfo:
			ShowAllInfo = checkshowAllInfo.isChecked();
			break;
			//deng  �޸�  ��Ҫ���������� �ٽ����ύ����
		case R.id.Edit_zibanSN:
			
			String zibansn=zibanedit.getText().toString().trim().toUpperCase();
			if(!lotsn.contains(zibansn)){
				lotsn=lotsn+zibansn+"|";
				logSysDetails("��ɨ�踱��["+(num+1)+"]:"+zibansn, "ɨ��");
				num++;
				zibanedit.setText("");
				zibaneditnum.setText(num+"");
				EditPCBSN.requestFocus();
			}
			else{
				    logSysDetails("�ظ�ɨ��:"+zibansn, "shibai");
				    zibanedit.setText("");
			}
			
			
			break;
			
/*		case R.id.Edit_SMTLadeMOName:
			if (EditMOName.getText().toString().trim().length() >= 8) {
				SMT_ScanSnGetWO(EditMOName.getText().toString().trim());
			}
			this.EditScanInfo.setText("");
			break;*/
		case R.id.Text_SMTLadeCarLabel:
			this.EditSMTCarLabel.setEnabled(true);
			EditSMTCarLabel.setText("");
			break;
		case R.id.Edit_SMTLadeCarLabel:
			if (!getMOInfoOk) {
				Toast.makeText(this, "���Ȼ�ȡ������Ϣ", 5).show();
				EditSMTCarLabel.setText("");
				EditMOName.requestFocus();
				return;
			} else if (this.EditSMTCarLabel.getText().toString().length() < 8) {
				Toast.makeText(this, "����ĳ�����Ϣ���Ȳ���ȷ", 5).show();
				EditSMTCarLabel.setText("");
				return;
			}
			EditSMTCarLabel.setEnabled(false);
			this.EditPCBSN.requestFocus();
			break;
		case R.id.Edit_LadePCBSN:
			params[4] = EditSMTCarLabel.getText().toString().trim();
			if (!getMOInfoOk) {
				Toast.makeText(this, "���Ȼ�ȡ������Ϣ", 5).show();
				EditMOName.requestFocus();
				EditPCBSN.setText("");
				return;
			} else if (params[4].length() < 8 && (SMTeyeCheckOrLadeToCar == 1||SMTeyeCheckOrLadeToCar == 3||SMTeyeCheckOrLadeToCar == 4)) {
				Toast.makeText(this, "�������복�����", 5).show();
				EditSMTCarLabel.requestFocus();
				EditPCBSN.setText("");
				EditSMTCarLabel.requestFocus();
				return;
				//deng xiugai  �������� ��Ϊ����SMT��������ֻ��5λ����ÿ����
			} else if ( this.EditPCBSN.getText().toString().length() >= 4&&this.EditPCBSN.getText().toString().length() < 8
					|| StringUtils.isScannedSMTLadcardErrorCode(EditPCBSN
							.getText().toString())) {
				params[5] = EditPCBSN.getText().toString().trim();
				isCheckEorrCode = true;
				if (SMTeyeCheckOrLadeToCar == 1||SMTeyeCheckOrLadeToCar == 3||SMTeyeCheckOrLadeToCar == 4)
					smtLadePCBToCar(params); // �����Ϊ����Ʒ����
				else
					smtEyeCheck(params);
			} else if (this.EditPCBSN.getText().toString().length() >= 8) {
				SavelogAndclearEdit();
				String str = EditPCBSN.getText().toString().trim()
						.toUpperCase();
				if (BindSN) {
					if (LastSN.length() != str.length()) {
						EditPCBSN.setText("");
						this.logDetails(EditScanInfo, "���볤�Բ�һ��");
						return;
					}
					if (LotSNS.contains(str)) {
						EditPCBSN.setText("");
						this.logDetails(EditScanInfo, "�ظ�ɨ��");
						return;
					}
					if (ScannedQty < OnePanelPCBQty - 1
							&& LastSN.equalsIgnoreCase(str)) {
						EditPCBSN.setText("");
						this.logDetails(EditScanInfo, "���룺" + str
								+ "ӦΪPCB�����һ������");
						return;
					}
					ScannedQty++;
					if (LotSNS.isEmpty())
						LotSNS = str;
					else
						LotSNS = LotSNS + "," + str;
					this.logDetails(EditScanInfo, "��������Ϣ:" + LotSNS);
					if (ScannedQty == OnePanelPCBQty) {
						if (LastSN.equalsIgnoreCase(str)) {
							BindPar[1] = str;
							BindPar[2] = LotSNS;
							BindPar[3] = OnePanelPCBQty + "";
							BindPCBSN(BindPar);
						} else {
							this.ClearBindSN();
							this.logDetails(EditScanInfo, "��������Ϣ�����");
							this.EditladecarErrorCode.setText("");
							this.logDetails(EditScanInfo,
									"ǰ��ɨ�����벻��ͬһ��PCB�����ɨ��˳�򲻶�,������ɨ��");
						}
					}
				} else {
					params[5] = str;
					if (SMTeyeCheckOrLadeToCar == 1){
						smtLadePCBToCar(params);
					}
					else  if  (SMTeyeCheckOrLadeToCar == 4){
						smtLadePCBToCarnew(params);
					}
					else  if(SMTeyeCheckOrLadeToCar == 3){
						if(EditladecarErrorCode.getText().toString().trim().isEmpty()){
							//��������Ϊ�գ���ʾ��Ҫ�������������ύ
							//�����ɨ���ύ����ʾҪ�رճ���
							if(params[5].equalsIgnoreCase("+submit+")&&lotsn.equalsIgnoreCase("")){
								smtLadePCBToCar(params);
							}
							else{
								//��ʼɨ����������ύ��
								if(str.equalsIgnoreCase("+submit+")){
									params[5]=lotsn;
									smtLadeModel.SMTladeandbinding(new  Task(this,TaskType.smtladeandbinding,params));
									
								}else{
									if(!lotsn.contains(str)){
									lotsn=lotsn+str+",";
									logSysDetails("��ɨ������["+(num+1)+"]:"+str, "ɨ��");
									zibanedit.requestFocus();
									}
									else{
										logSysDetails("�ظ�ɨ��:"+str, "shibai");
										EditPCBSN.requestFocus();
									}
								}
								
								
								
								
							}
						}
						else{
							//����������벻Ϊ�� ��+submit+,���رճ���Ҳ��OK�ģ���
							smtLadePCBToCar(params);
						}
					}
					else
						smtEyeCheck(params);
				}
			}
			EditPCBSN.setText("");
			break;
			
		case R.id.button_SMTCloseCar:
			final Builder builder = new AlertDialog.Builder(this);

			builder.setIcon(R.drawable.check);
			builder.setTitle("�Ƿ�ر�װ����");
			builder.setMessage("��ȷ���Ƿ�ر�װ��?");
			builder.setPositiveButton("ȷ��"
					// Ϊ�б���ĵ����¼����ü�����
							, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String[] params = new String[] {"", "", "", "", "", "","","","",""};
									params[0] = moid;
									params[1] = EditSMTCarLabel.getText().toString().trim();
									params[2] = "";
									params[3] = EditLadequantiyt.getText().toString().trim()
											.toUpperCase();
									params[4] = "";
									params[5] = "3";
									params[6] = resourceid;
									params[7] = resourcename;
									params[8] = useid;
									params[9] = usename;
									Log.i(TAG, "���������ǣ�" + "" + ":::" + params[0] + ":::"
											+ params[2] + ":::" + params[3] + ":::" + params[5]);
									sntocarmodel.sntocar(new Task(sMTLadeActivity, TaskType.sntocartocar, params));
								}
							});
					// Ϊ�Ի�������һ����ȡ������ť
		    builder.setNegativeButton("ȡ��", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder.create().show();

			break;
			// ��ȡ������Ϣ
		case R.id.Edit_SMTLadeMOName:
			if(SMTeyeCheckOrLadeToCar == 3)
			{
				String paras = EditMOName.getText().toString().toUpperCase().trim();
				if (paras.length() < 6) {
						Toast.makeText(this, "���SN���Ȳ���ȷ����ȷ����ȷ��SN",
								Toast.LENGTH_SHORT).show();
				} else {
						sntocarmodel.getMo(new Task(this, TaskType.sntocargetmo,
								paras));
					}
			}
			else
			{
				if (EditMOName.getText().toString().trim().length() >= 8) {
					SMT_ScanSnGetWO(EditMOName.getText().toString().trim());
				}
				this.EditScanInfo.setText("");
			}	
			break;		
		}
	}

	private void SavelogAndclearEdit() {

		if (this.EditScanInfo.getLineCount() < 100)
			return;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat hf = new SimpleDateFormat("HH-mm-ss");
			String logname;
			logname = "data/data/" + MyApplication.getAppOwner() + "/"
					+ df.format(new Date()) + "/" + hf.format(new Date());
			
			EditScanInfo.setText("");

		} catch (Exception e) {
			EditScanInfo.setText("");
			logDetails(EditScanInfo, e.toString());
		}

	}

	private void BindPCBSN(String[] par) {

		String[] p = par.clone();
		msg = "PCB SN �����";
		EditPCBSN.setEnabled(false);
		this.ButtonLadeCar.setText("�����");
		this.ButtonLadeCar.setBackgroundColor(Color.YELLOW);
		Task BindSN = new Task(this, TaskType.SMT_BindPCBSN, p);
		super.progressDialog.setMessage(msg);
		showProDia();
		PCBSNBind.BindPCBSN(BindSN);

	}

	private void smtEyeCheck(String[] params) {

		if (params[5].equalsIgnoreCase(LastSN)) {
			super.logDetails(EditScanInfo, "�ظ�ɨ��");
			return;
		}
		if (EditladecarErrorCode.getText().toString().trim().isEmpty())
			params[6] = "";
		else
			params[6] = EditladecarErrorCode.getText().toString().trim();
		EditPCBSN.setEnabled(false);
		if (isCheckEorrCode) {
			msg = "checking error code";
			this.ButtonLadeCar.setText("ȷ�ϲ�������");
		} else {
			this.ButtonLadeCar.setText("Ŀ���ύ��");
			msg = "Lade PCB to Car";
		}
		this.ButtonLadeCar.setBackgroundColor(Color.YELLOW);
		Task EyeCheck = new Task(this, TaskType.SMT_EyeCheck, params);
		super.progressDialog.setMessage(msg);
		showProDia();
		smtLadeModel.SMTeyeCheck(EyeCheck);
		if (!isCheckEorrCode)
			LastSN = params[5];
	}

	private void smtLadePCBToCar(String[] params) {

		if (params[5].equalsIgnoreCase(LastSN)) {
			super.logDetails(EditScanInfo, "�ظ�ɨ��");
			return;
		}
		if (EditladecarErrorCode.getText().toString().trim().isEmpty())
			params[6] = "";
		else
			params[6] = EditladecarErrorCode.getText().toString().trim();
		EditPCBSN.setEnabled(false);
		if (isCheckEorrCode) {
			msg = "checking error code";
			this.ButtonLadeCar.setText("ȷ�ϲ�������");
		} else {
			this.ButtonLadeCar.setText("װ���ύ��");
			msg = "Lade PCB to Car";
		}
		this.ButtonLadeCar.setBackgroundColor(Color.YELLOW);
		Task LadePCB = new Task(this, TaskType.SMT_LadeToCar, params);
		super.progressDialog.setMessage(msg);
		showProDia();
		smtLadeModel.SMTLadeToCar(LadePCB);
		if (!isCheckEorrCode)
			LastSN = params[5];
	}
	
	private void smtLadePCBToCarnew(String[] params) {
		if (params[5].equalsIgnoreCase(LastSN)) {
			super.logDetails(EditScanInfo, "�ظ�ɨ��");
			return;
		}
		if (EditladecarErrorCode.getText().toString().trim().isEmpty())
			params[6] = "";
		else
			params[6] = EditladecarErrorCode.getText().toString().trim();
		params[9]=abside;
		EditPCBSN.setEnabled(false);
		if (isCheckEorrCode) {
			msg = "checking error code";
			this.ButtonLadeCar.setText("ȷ�ϲ�������");
		} else {
			this.ButtonLadeCar.setText("װ���ύ��");
			msg = "Lade PCB to Car";
		}
		this.ButtonLadeCar.setBackgroundColor(Color.YELLOW);
		Task LadePCB = new Task(this, TaskType.SMT_LadeToCar, params);
		super.progressDialog.setMessage(msg);
		showProDia();
		smtLadeModel.SMTLadeToCarnew(LadePCB);
		if (!isCheckEorrCode)
			LastSN = params[5];
	}

	private void SMT_ScanSnGetWO(String PCBSN) {
		Task GetWOByPCBSN = new Task(this, TaskType.SMT_ScanSnGetWO, PCBSN);
		msg = "SMT Get WO Number by Scan PCBSN";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.SMT_ScanSnGetWO(GetWOByPCBSN);
	}
	private void logSysDetails(String logText, String strflag) {
		CharacterStyle ssStyle = null;
		if (logText.contains(strflag)) {
			ssStyle = new ForegroundColorSpan(Color.BLUE);
		} else {
			ssStyle = new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog = "[" + df.format(new Date()) + "]" + logText + "\n";
		SpannableStringBuilder ssBuilder = new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(EditScanInfo.getText());
		EditScanInfo.setText(ssBuilder);
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
			zibanedit.setText("");
			zibaneditnum.setText("");
			lotsn="";
			num=0;
			ClearBindSN();
			this.EditladecarErrorCode.setText("");
			break;
		}

		return true;
	}

}

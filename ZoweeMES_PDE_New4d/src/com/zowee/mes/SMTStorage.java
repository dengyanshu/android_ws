package com.zowee.mes;

import java.util.HashMap;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.model.SMTFirstOperationModel;
import com.zowee.mes.model.SMTStorageModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMTStorage extends CommonActivity implements View.OnClickListener {

	private EditText EditMOName;
	private EditText EditSMTCarLabel;
	private EditText EditScanInfo;
	private TextView text_MOname;
	private Button button_Storage;
	private String[] params;
	private String UserName;
	private String UserID;
	private String MOId;
	private String MOName;
	private boolean getMOInfoOk;
	private SMTStorageModel storagmodel;
	private SMTFirstOperationModel smtFirstOperationModel;

	private static String msg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtstorage);
		init();
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("ȷ���˳�SMT�����?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(SMTStorage.this,
						BackgroundService.class));
				finish();
				
			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.SMT_LadeStorage;
		super.init();
		msg = "Smt Lade to Car";
		super.progressDialog.setMessage(msg);
		storagmodel = new SMTStorageModel();
		smtFirstOperationModel = new SMTFirstOperationModel();
		EditMOName = (EditText) findViewById(R.id.Edit_MONumber2);
		EditSMTCarLabel = (EditText) findViewById(R.id.Edit_SMTLadeCarLabel2);
		EditScanInfo = (EditText) findViewById(R.id.Edit_StorageMesInfo);
		button_Storage = (Button) findViewById(R.id.button_SMTStorage);
		text_MOname = (TextView) findViewById(R.id.Text_MONumber2);

		params = new String[] { "", "", "", "", "" };
		this.EditMOName.setOnClickListener(this);
		this.EditScanInfo.setOnClickListener(this);
		this.EditSMTCarLabel.setOnClickListener(this);
		final Builder builder = new AlertDialog.Builder(this);	
		text_MOname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);	
				builder.setTitle("���»񹤵�");
				builder.setMessage("��ȷ���Ƿ�Ҫ���»񹤵�");
				builder.setPositiveButton("ȷ��"
						//Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								EditMOName.setText("");
								EditMOName.setEnabled(true);
								EditMOName.requestFocus();
								getMOInfoOk = false;			
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder.setNegativeButton("ȡ��"
						,  new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{							
							}
						});	
				builder.create().show();
			}
		});

		final Builder builder2 = new AlertDialog.Builder(this);
		this.button_Storage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder2.setIcon(R.drawable.check);
				builder2.setTitle("SMT���");
				builder2.setMessage("��ȷ�ϳ�����Ϣ�Ƿ���ȷ��");
				builder2.setPositiveButton("ȷ��"
						// Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								params[4] =  EditSMTCarLabel.getText().toString().trim();
								if(!getMOInfoOk){
									EditScanInfo.append("���Ȼ�ȡ������Ϣ��\n");
									return;
								}else if(params[4].equals("")){								
									EditScanInfo.append("�������복����Ϣ��\n");
									return;
								}else if(params[4].length() < 8){
									EditScanInfo.append("������Ϣ���Ȳ���ȷ��\n");
									return;
								}
								smtLadePCBToCar(params);
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder2.setNegativeButton("ȡ��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder2.create().show();
			}
		});

	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		switch (task.getTaskType()) {
		case TaskType.SMT_LadeStorage:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();			
				if (getdata.get("Error") == null) {
					this.EditScanInfo.setText(EditScanInfo.getText().toString()
							+ "\n" + getdata.toString()+ "\n");
					if( Integer.parseInt(getdata.get("result").toString()) == 0)
					{
						this.EditScanInfo.append(params[4] + "���ɹ�\n");
					}	
					else{
						this.EditScanInfo.append(params[4] + "���ʧ�ܣ�\n");					 
					}
				} else {
					EditScanInfo.setText(EditScanInfo.getText().toString()
							+ "\n" + getdata.get("Error"));	
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();	
				}
				closeProDia();	
			} else{
				Toast.makeText(this, "��MES������Ϣ", 5).show();				
			}
			this.EditSMTCarLabel.setText("");
			this.EditSMTCarLabel.requestFocus();
			closeProDia();
			break;
		case TaskType.SMT_ScanSnGetWO:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();	
				this.EditScanInfo.append(getdata.toString()+"\n");
				if (getdata.get("Error") == null) {

					if( Integer.parseInt(getdata.get("result").toString()) == 0){					
						if(getdata.containsKey("MOName")){
							EditMOName.setText(getdata.get("MOName").toString().trim());
							EditMOName.setEnabled(false);
						}
						MOId = getdata.get("MOId").toUpperCase().trim();
						MOName = getdata.get("MOName").toUpperCase().trim();
						params[2] = MOName;
						params[3] = MOId;
						getMOInfoOk = true;
					}	
					else{
						getMOInfoOk = false;
						this.EditMOName.setText("");
						this.EditScanInfo.append(getdata.get("ReturnMsg").toString()+"\n");
					}
				} else {
					this.EditScanInfo.append(getdata.toString()+"\n");
				}
				closeProDia();
			} else
				closeProDia();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Edit_MONumber2:
			if(EditMOName.getText().toString().trim().length() >= 8){
				SMT_ScanSnGetWO(EditMOName.getText().toString().trim());

			}
			this.EditScanInfo.setText("");	
			break;
		case R.id.Edit_SMTLadeCarLabel2:
			if(!getMOInfoOk){
				Toast.makeText(this, "���Ȼ�ȡ������Ϣ", 5).show();
				EditSMTCarLabel.setText("");
				EditMOName.requestFocus();
				return;
			}else if(this.EditSMTCarLabel.getText().toString().length() < 8)
			{
				Toast.makeText(this, "����ĳ�����Ϣ���Ȳ���ȷ", 5).show();
				EditSMTCarLabel.setText("");
				return;
			}			
			this.button_Storage.requestFocus();
			break; 
		}
	}

	private void smtLadePCBToCar(String[] params) {

		Task LadePCB = new Task(this, TaskType.SMT_LadeStorage, params);
		msg = "SMT Storage";
		super.progressDialog.setMessage(msg);
		showProDia();
		storagmodel.SMTStorage(LadePCB);
	}

	private void SMT_ScanSnGetWO(String PCBSN) {
		Task GetWOByPCBSN = new Task(this, TaskType.SMT_ScanSnGetWO, PCBSN);
		msg = "SMT Get WO Number by Scan PCBSN";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.SMT_ScanSnGetWO(GetWOByPCBSN);
	}

}

package com.zowee.mes;

import java.util.HashMap;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.model.SMTFirstOperationModel;
import com.zowee.mes.model.SMTLadeModel;
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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class SMTLade extends CommonActivity implements
View.OnClickListener {

	private TextView TextMOName; 
	private TextView TextSMTCarLabel;
	private EditText EditMOName;
	private EditText EditSMTCarLabel;
	private EditText EditPCBSN;
	private EditText EditLadequantiyt;
	private EditText EditScanInfo;
	private Button ButtonLadeCar;
	private static String msg = null;
	private SMTLadeModel smtLadeModel;
	private SMTFirstOperationModel smtFirstOperationModel;
	private String[] params;  
	private String UserName;
	private String UserID;
	private String MOId;
	private String MOName;
	private boolean getMOInfoOk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtlade);
		init();
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.SMT_LadeToCar;
		super.init();
		msg = "Smt Lade to Car";
		super.progressDialog.setMessage(msg);

		TextMOName = (TextView) findViewById(R.id.Text_LadeMOName);
		TextSMTCarLabel = (TextView) findViewById(R.id.Text_SMTLadeCarLabel); 		 
		EditMOName = (EditText) findViewById(R.id.Edit_LadeMOName);
		EditSMTCarLabel = (EditText)findViewById(R.id.Edit_SMTLadeCarLabel);
		EditPCBSN  = (EditText)findViewById(R.id.Edit_LadePCBSN);
		EditScanInfo= (EditText)findViewById(R.id.Edit_LadeMESInfo);
		ButtonLadeCar = (Button)findViewById(R.id.button_SMTLadeCar);
		EditLadequantiyt = (EditText)findViewById(R.id.Edit_ladequantity);
		smtLadeModel = new SMTLadeModel();
		smtFirstOperationModel   = new SMTFirstOperationModel();
 
		this.TextSMTCarLabel.setOnClickListener(this);
		this.EditMOName.setOnClickListener(this);
		this.EditPCBSN.setOnClickListener(this);
		this.EditSMTCarLabel.setOnClickListener(this);
		params = new String[]{"","","","","","",""};
		// this.EditScanInfo.setOnClickListener(this);
		//  this.ButtonLadeCar.setOnClickListener(this);		
		final Builder builder = new AlertDialog.Builder(this);

		TextMOName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);	
				builder.setTitle("重新获工单");
				builder.setMessage("请确认是否要重新获工单");
				builder.setPositiveButton("确定"
						//为列表项的单击事件设置监听器
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
				// 为对话框设置一个“取消”按钮
				builder.setNegativeButton("取消"
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
		
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}
	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出SMT装车吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(SMTLade.this,BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}
	@SuppressLint("ResourceAsColor")
	@Override
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		switch (task.getTaskType()) {
		case TaskType.SMT_ScanSnGetWO:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();	
				this.EditScanInfo.append(getdata.toString()+"\n");
				if (getdata.get("Error") == null) {

					if( Integer.parseInt(getdata.get("result").toString()) == 0){					
						if(getdata.containsKey("MOName")){
							EditMOName.setText(getdata.get("MOName").toString().trim());
							EditMOName.setEnabled(false);
							this.EditSMTCarLabel.requestFocus();
						}
						MOId = getdata.get("MOId").toUpperCase().trim();
						MOName = getdata.get("MOName").toUpperCase().trim();
						params[2] = MOName;
						params[3] = MOId;
						getMOInfoOk = true;
					}	
					else{
						this.EditMOName.setText("");
						this.EditScanInfo.append(getdata.get("ReturnMsg").toString()+"\n");
						getMOInfoOk = false;
					}
				} else {
					this.EditScanInfo.append(getdata.toString()+"\n");
				}
				closeProDia();
			} else
				closeProDia();
			break;
		case TaskType.SMT_LadeToCar:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();			
				if (getdata.get("Error") == null) {
					this.EditScanInfo.setText(EditScanInfo.getText().toString()
							+ getdata.toString()+ "\n");
					if( Integer.parseInt(getdata.get("result").toString()) == 0)
					{
						this.EditScanInfo.append(params[5] + "装车成功\n");
						this.ButtonLadeCar.setText("PASS");
						this.ButtonLadeCar.setTextColor(Color.BLUE);
						this.ButtonLadeCar.setBackgroundColor(Color.GREEN);
						this.EditLadequantiyt.setText(getdata.get("ReturnMsg").toString().replace("ServerMessage:数据采集成功！装车完毕，已装车数量：", ""));
					}	
					else{
						this.EditScanInfo.append(params[5] + "装车失败！\n");
						this.ButtonLadeCar.setText("Fail");
						this.EditPCBSN.setText("");
						this.ButtonLadeCar.setTextColor(Color.WHITE);
						this.ButtonLadeCar.setBackgroundColor(Color.RED);

					}
				} else {
					EditScanInfo.setText(EditScanInfo.getText().toString()
							+ "\n" + getdata.get("Error"));	
					Toast.makeText(this, "MES 返回信息发生异常", 5).show();	
				}
				closeProDia();	
			} else{
				Toast.makeText(this, "无MES返回信息", 5).show();				
			}
			this.EditPCBSN.setEnabled(true);
			this.EditPCBSN.selectAll();
			closeProDia();
			break;
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Edit_LadeMOName:
			if(EditMOName.getText().toString().trim().length() >= 8){
				SMT_ScanSnGetWO(EditMOName.getText().toString().trim());								
			}
			this.EditScanInfo.setText("");
			break;
		case R.id.Text_SMTLadeCarLabel: 
			this.EditSMTCarLabel.setEnabled(true);
			EditSMTCarLabel.setText("");
			break;
		case R.id.Edit_SMTLadeCarLabel: 
			if(!getMOInfoOk){
				Toast.makeText(this, "请先获取订单信息", 5).show();
				EditSMTCarLabel.setText("");
				EditMOName.requestFocus();
				return;
			}else if(this.EditSMTCarLabel.getText().toString().length() < 8)
			{
				Toast.makeText(this, "输入的车量信息长度不正确", 5).show();
				EditSMTCarLabel.setText("");
				return;
			}
			EditSMTCarLabel.setEnabled(false);	
			this.EditPCBSN.requestFocus();
			break;
		case R.id.Edit_LadePCBSN:
			params[4] = EditSMTCarLabel.getText().toString().trim();
			if(!getMOInfoOk){
				Toast.makeText(this, "请先获取订单信息", 5).show();
				EditMOName.requestFocus();
				EditPCBSN.setText("");
				return;
			}else if(params[4].length() < 8){
				Toast.makeText(this, "请先输入车量编号", 5).show();
				EditSMTCarLabel.requestFocus();
				EditPCBSN.setText("");
				EditSMTCarLabel.requestFocus();
				return;
			}else if(this.EditPCBSN.getText().toString().length() >= 8){
				params[5] = EditPCBSN.getText().toString().trim();
				smtLadePCBToCar(params);
				this.ButtonLadeCar.setText("装车中");
				this.ButtonLadeCar.setBackgroundColor(Color.YELLOW);
				EditPCBSN.setEnabled(false); 
			}
			EditPCBSN.selectAll();
			break;
		}
	}
	private void smtLadePCBToCar(String[] params) {

		Task LadePCB = new Task(this, TaskType.SMT_LadeToCar, params);
		msg = "Lade PCB to Car";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtLadeModel.SMTLadeToCar(LadePCB);
	}
	private void SMT_ScanSnGetWO(String PCBSN) {
		Task GetWOByPCBSN = new Task(this, TaskType.SMT_ScanSnGetWO, PCBSN);
		msg = "SMT Get WO Number by Scan PCBSN";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.SMT_ScanSnGetWO(GetWOByPCBSN);
	}


}

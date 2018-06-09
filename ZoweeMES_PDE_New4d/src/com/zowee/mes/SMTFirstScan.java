package com.zowee.mes;

import java.util.HashMap;



import com.zowee.mes.activity.CommonActivity;
//import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.SMTFirstOperationModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SMTFirstScan extends CommonActivity implements
View.OnClickListener,OnItemSelectedListener{

	private String MOId;
	private String ProductId;
	private String MOName;
	private String PCBType;
	private String ProductName;
	private String VendorPCBSN;
	private String PCBSN;
	private String HidSN ="";
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
	//private EditText EditWOSideAQuantity;
	//private EditText EditWOSideBQuantity;
	private CheckBox checkshowAllInfo;
	private Button ButtonGetWOInfo;
	private Button ButtonScanNextPacket;
	private Button ButtonResetScan;
	private Spinner spinnerPCBType;
	private Spinner spinnerPCBFactQuantity;
	private TextView TextPCBFactQuantity;
	private TextView TextPCBtype;
	private boolean ShowAllInfo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smtfirst_scan);
		init();
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
		TextPCBFactQuantity = (TextView) findViewById(R.id.Text_PCBFactQuantity);	
		EditVendorPCBSN = (EditText) findViewById(R.id.Edit_VendorPCBSN);
		EditPCBSN = (EditText) findViewById(R.id.Edit_PCBSN);

		EditScanInfo = (EditText) findViewById(R.id.Edit_SMTFirstInfo);
		EditProductNumber = (EditText) findViewById(R.id.Edit_ProductNumber);
		EditOnePCBQuantity = (EditText) findViewById(R.id.Edit_OnePCBQuantity);
		EditWOFinishQuantity = (EditText) findViewById(R.id.Edit_WOFinishQuantity);		
		//EditWOSideAQuantity = (EditText) findViewById(R.id.Edit_WOSideAQuantity);
		//EditWOSideBQuantity = (EditText) findViewById(R.id.Edit_WOSideBQuantity);
		ButtonGetWOInfo = (Button) findViewById(R.id.Button_GetWOInfo);
		ButtonScanNextPacket = (Button) findViewById(R.id.Button_ScanNextPacket);
		ButtonResetScan  = (Button) findViewById(R.id.Button_ResetScan);
		TextPCBtype = (TextView) findViewById(R.id.Text_PCBtype);
		checkshowAllInfo = (CheckBox) findViewById(R.id.check_showAllInfo);
		checkshowAllInfo.setOnClickListener(this);
		EditCurrentPCBSerialnumber = (EditText)findViewById(R.id.Edit_CurrentPCBSerialnumber);

		this.EditProductSN = (EditText)findViewById(R.id.Edit_ProductSN);
		EditProductSN.setOnClickListener(this); 

		TextPCBFactQuantity.setOnClickListener(this);

		EditVendorPCBSN.setOnClickListener(this);
		EditPCBSN.setOnClickListener(this);
		ButtonGetWOInfo.setOnClickListener(this);
		TextPCBtype.setOnClickListener(this);
		ButtonScanNextPacket.setOnClickListener(this);

		ArrayAdapter<CharSequence> adapterPCBtype = ArrayAdapter
				.createFromResource(this, R.array.SMTFirstScanPCBType,
						android.R.layout.simple_spinner_item);
		adapterPCBtype
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPCBType = (Spinner)findViewById(R.id.spinner_PCBType);
		spinnerPCBType.setAdapter(adapterPCBtype);
		spinnerPCBType.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapterPCBQuantity = ArrayAdapter
				.createFromResource(this, R.array.SMTFirstScanPCBQuantity,
						android.R.layout.simple_spinner_item);
		adapterPCBQuantity
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerPCBFactQuantity = (Spinner)findViewById(R.id.spinner_PCBFactQuantity);
		spinnerPCBFactQuantity.setAdapter(adapterPCBQuantity);
		spinnerPCBFactQuantity.setOnItemSelectedListener(this);

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
						//为列表项的单击事件设置监听器
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								HidSN = "";
								scancount = 0;
								EditCurrentPCBSerialnumber.setText("0");
								EditScanInfo.setText("");								
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
		.setTitle("确定退出SMT上线扫描吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(SMTFirstScan.this,BackgroundService.class));
				 finish();		
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		int  Pcbremain  = -1;
		switch (task.getTaskType()) {
		case TaskType.SMT_ScanSnGetWO:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();	
				if(ShowAllInfo)
					this.EditScanInfo.append(getdata.toString()+"\n");
				if (getdata.get("Error") == null) {

					if( Integer.parseInt(getdata.get("result").toString()) == 0){					
						if(getdata.containsKey("MOName")){
							getwoinfo(getdata.get("MOName").toString().trim());
							this.EditProductSN.setText("");
						}
					}	
					else{
						this.EditProductSN.setText("");
						if(getdata.containsKey("ReturnMsg") && !ShowAllInfo)
							this.EditScanInfo.append(getdata.get("ReturnMsg").toString()+"\n");
					}
				} else {
					if(!ShowAllInfo)
						this.EditScanInfo.append(getdata.toString()+"\n");
				}
				closeProDia();
			} else
				closeProDia();
			break;
		case TaskType.SMT_FirstCancleScan:
			break;
		case TaskType.SMT_FirstScan:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();	
				if(ShowAllInfo)
					this.EditScanInfo.append(getdata.toString()+"\n");
				if (getdata.get("Error") == null) {

					if( Integer.parseInt(getdata.get("result").toString()) == 0){
						if(getdata.containsKey("PCBQTY")){
							Pcbremain = Integer.parseInt(getdata.get("PCBQTY").trim().toString());	
						}
						if(getdata.containsKey("ScanCount")){
							scancount = Integer.parseInt(getdata.get("ScanCount").trim().toString());	
							EditCurrentPCBSerialnumber.setText(scancount + "");
						}
						this.EditPCBOnePacketRemainQuantity.setText(Pcbremain + "");
						this.EditWOFinishQuantity.setText(getdata.get("MOScanCount").trim().toString());
						HidSN = getdata.get("HideSN").trim().toString();	
						if( scancount != 0 && !ShowAllInfo){
							EditScanInfo.append( scancount + " : "+ PCBSN +" 扫描OK;\n");
						}else{
							if(!ShowAllInfo)
								EditScanInfo.append( getdata.get("ReturnMsg") +"\n");
						}
					}
					else{
						if(!ShowAllInfo)
							EditScanInfo.append( getdata.get("ReturnMsg") +"\n");
						if(getdata.get("ReturnMsg").toString().contains("该批号无可用数量")){
							this.EditVendorPCBSN.setEnabled(true);
							this.EditVendorPCBSN.setText("");
							this.EditPCBSN.setText("");
							this.EditVendorPCBSN.requestFocus();								
						}
					}
				} else {
					if(!ShowAllInfo)
						EditScanInfo.append( getdata.get("Error")+"\n");
					scancount = 0;
				}
				closeProDia();
			} else
				closeProDia();
			if( Pcbremain == 0){
				this.EditVendorPCBSN.setEnabled(true);
				this.EditVendorPCBSN.setText("");
				this.EditVendorPCBSN.requestFocus();
			}
			break;
		case TaskType.SMT_FirstScan_WOInfo:

			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if(ShowAllInfo)
					EditScanInfo.append(getdata.toString()+"\n");
				if (getdata.get("Error") == null) {

					MOId = getdata.get("MOId").toUpperCase().trim();
					ProductId = getdata.get("ProductId").toUpperCase().trim();
					MOName = getdata.get("MOName").toUpperCase().trim();
					PCBType = getdata.get("PCBType").toUpperCase().trim();
					ProductName = getdata.get("ProductName").toUpperCase()
							.trim();
					EditProductNumber.setText(ProductName);
					EditOnePCBQuantity.setText(getdata.get("MakeUpCount")
							.toUpperCase());
					TextPCBtype.setText(PCBType);
					onePCBQuantity = Integer.parseInt(getdata
							.get("MakeUpCount"));
					this.spinnerPCBFactQuantity.setSelection(onePCBQuantity-1);
					this.onePCBFactQuantity = onePCBQuantity;
					taskdata.put("MOId", MOId);
					taskdata.put("ProductId", ProductId);
					taskdata.put("MOName", MOName);
					taskdata.put("PCBType", PCBType);
					taskdata.put("ProductName", ProductName);
					taskdata.put("MakeUpCount", onePCBQuantity+ "");		
					if (MOId.trim().equals("") || ProductId.trim().equals("")|| MOName.trim().equals("")
							|| PCBType.trim().equals("") || ProductName.trim().equals("")) {
						Toast.makeText(this, "获取的订单信息不完整", 3).show();
						getMOInfoOk = false;
						onePCBQuantity = 0;						
					} else {				 
						if (PCBType.equals("阴阳板")){
							taskdata.put("PCBSide", "阴阳板");
							this.spinnerPCBType.setSelection(2);
							this.EditVendorPCBSN.requestFocus();
						}	
						EditWONumber.setText(MOName);
						this.EditProductSN.setEnabled(false);
						getMOInfoOk = true;
					}
				} else {
					if(!ShowAllInfo)
						EditScanInfo.append( getdata.get("Error")+"\n");	
					getMOInfoOk = false;
					onePCBQuantity = 0;
				}
				closeProDia();
			} else
				closeProDia();
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
			d = new AlertDialog.Builder(this).setTitle("重新获取工单")
			.setMessage("确定要重新获取工单吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {			
					EditProductSN.setEnabled(true);
					getMOInfoOk = false;
					EditWONumber.setText("");
					EditProductNumber.setText("");
					EditOnePCBQuantity.setText("");
					spinnerPCBType.setEnabled(true);	
					EditProductSN.requestFocus();
				}
			}).setNegativeButton("取消", null).show();
			break;
		case 10010:
			d = new AlertDialog.Builder(this).setTitle("扫描下一包PCB")
			.setMessage("确定此包PCB已扫描完成吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {			
					EditVendorPCBSN.setEnabled(true);
					EditVendorPCBSN.requestFocus();	
					EditVendorPCBSN.setText("");
				}
			}).setNegativeButton("取消", null).show();
			break;
		case 1008611:
			d = new AlertDialog.Builder(this).setTitle("更换PCB生产面")
			.setMessage("确定要更换PCB生产面吗?")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {			
					spinnerPCBType.setEnabled(true);
				}
			}).setNegativeButton("取消",  new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {			
					spinnerPCBType.setEnabled(false);
					taskdata.put("PCBSide", spinnerPCBType.getSelectedItem().toString().trim());
				}
			}).show();
			break;

		}
		return d;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Edit_ProductSN:
			if(EditProductSN.getText().toString().trim().length() >= 8)
				SMT_ScanSnGetWO(EditProductSN.getText().toString().trim());
			break;
		case R.id.check_showAllInfo:   
			ShowAllInfo = checkshowAllInfo.isChecked();
			break;
		case R.id.Text_PCBFactQuantity:
			Toast.makeText(this, "请输入实际连板数量", 3).show();	
			this.spinnerPCBFactQuantity.setEnabled(true);
			break;
		case R.id.Text_PCBtype:
			showDialog(1008611);
			break;
		case R.id.Edit_VendorPCBSN:
			if (!getMOInfoOk ) {
				Toast.makeText(this, "请先获取工单信息", 3).show();
				EditVendorPCBSN.setText("");
			}else{
				VendorPCBSN = EditVendorPCBSN.getText().toString().trim();
				if ( this.spinnerPCBType.isEnabled()){

					Toast.makeText(this, "请先选择PCB面", 3).show();	
				}else if (VendorPCBSN.length() >= 8){
					taskdata.put("VendorPCBSN", VendorPCBSN);
					EditPCBSN.requestFocus();	
					EditVendorPCBSN.setEnabled(false);
				}else{
					EditVendorPCBSN.setText("");
				}  
			}
			break;

		case R.id.Edit_PCBSN:
			PCBSN = EditPCBSN.getText().toString().trim();
			if (scancount == this.onePCBFactQuantity){
				if(!PCBSN.equalsIgnoreCase("+Submit+")){
					this.EditPCBSN.setText("");	
					Toast.makeText(this, "请扫描提交条码", 3).show();
					return;
				}
			}
			if(HidSN.contains(PCBSN) && PCBSN.length() >= 8){				
				this.EditPCBSN.setText("");	
				Toast.makeText(this, "重复扫描", 3).show();
				return;				
			} 
			if (PCBSN.length() >= 8 && getMOInfoOk  ){
				if(scancount == 0)
					this.EditScanInfo.setText("");
				taskdata.put("PCBSN", PCBSN);
				taskdata.put("HidSN", HidSN);
				this.EditPCBSN.setText("");
				SMTScanLable(taskdata);				
			}
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
		//msg = "Get SMT WO info";
		//super.progressDialog.setMessage(msg);
		//showProDia();
		smtFirstOperationModel.getWoInfo(getWOinfo);
	}

	private void SMTScanLable(HashMap<String, String> taskdata) {
		Task smtScanLable = new Task(this, TaskType.SMT_FirstScan, taskdata);
		msg = "SMT first scan";
		super.progressDialog.setMessage(msg);
		showProDia();
		smtFirstOperationModel.SMTScanLable(smtScanLable);
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
			onePCBFactQuantity = spinnerPCBFactQuantity.getSelectedItemPosition() + 1 ;
			if (!getMOInfoOk ) {
				spinnerPCBFactQuantity.setSelection(onePCBQuantity -1);
				this.EditProductSN.requestFocus(); 
			}else{ 
				if(onePCBFactQuantity > onePCBQuantity || onePCBFactQuantity ==0 ) {
					Toast.makeText(this, "输入应不大于连板数量或等于0", 3).show();
					spinnerPCBFactQuantity.setSelection(onePCBQuantity -1);	
					onePCBFactQuantity = onePCBQuantity;
				}else {	
					spinnerPCBFactQuantity.setEnabled(false);
					taskdata.put("MakeUpCount", onePCBFactQuantity + "");
				}
			}
			break;
		case R.id.spinner_PCBType:
			if (!getMOInfoOk ) {
				this.EditProductSN.requestFocus();
			}else{ 
				if(this.TextPCBtype.getText().toString().trim().equals("阴阳板") && spinnerPCBType.getSelectedItemPosition() != 2 ){
					this.spinnerPCBType.setSelection(2);
					taskdata.put("PCBSide", "阴阳板");
				}
				if(this.TextPCBtype.getText().toString().trim().equals("AB面板") && spinnerPCBType.getSelectedItemPosition() == 2){
					this.spinnerPCBType.setSelection(0);
					Toast.makeText(this, "应选择A或B面", 3).show();					
				}else{
					taskdata.put("PCBSide", spinnerPCBType.getSelectedItem().toString().trim());					
				}
				spinnerPCBType.setEnabled(false);
			}
			break;
		}

	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}

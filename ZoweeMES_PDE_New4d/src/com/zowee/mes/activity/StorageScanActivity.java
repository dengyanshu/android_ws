package com.zowee.mes.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.adapter.StockCodesAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.StorageScanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;


/**
 * @author Administrator
 * @description ���ɨ��
 */

// 2014-04-08 ��λ��Ϊ�ֿ�ţ��ֶ�ѡ��ֿ�� version 14
public class StorageScanActivity extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener
{

	private EditText edtOutPackageNum;
	private final static String TAG = "activity";
	private StorageScanModel model;
	private GetMOnameModel GetMonamemodel;
	private EditText edtGRNNO;
	private Spinner SpiLocaPosition;
	private Button btnMakeSure;
	private View btnGroups;
	private EditText edtSysDetails;
	private Button btnScan;
	private Button btnCancel;
	private Button btnRefData;
	private String ResourceID;
	private List<HashMap<String,String>> StockCodes;
	private String[] Params;
	private String 	StockCode="";
	@Override
	public void init()
	{
		super.commonActivity = this;
		super.TASKTYPE = TaskType.STORAGE_SCAN;
		super.init();
		model = new StorageScanModel();
		GetMonamemodel  = new GetMOnameModel();
		Params = new String[]{"","","",""};
		edtOutPackageNum = (EditText)this.findViewById(R.id.edt_outbox_packNum);
		edtGRNNO =(EditText)this.findViewById(R.id.edt_reciegood_num);
		SpiLocaPosition = (Spinner)this.findViewById(R.id.Spinner_storage_locaction);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnMakeSure = (Button)btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);
		edtSysDetails = (EditText)findViewById(R.id.edt_sysdetail);

		btnMakeSure.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRefData.setOnClickListener(this);		
		btnMakeSure.setVisibility(View.GONE);
		btnRefData.setVisibility(View.GONE);
		SpiLocaPosition.setOnItemSelectedListener(this);
		edtOutPackageNum.setOnClickListener(this); //add by ybj 20140213
		edtGRNNO.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.storage_scan);
		init();
		GetResourceId();
		getStockCode();
	}

	@Override
	protected void onResume(){
		super.onResume();
		/*	if(TextUtils.isEmpty(edtLocaPosition.getText())){
			edtLocaPosition.requestFocus();
		}*/
	}

	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		int taskType = task.getTaskType();
		switch(taskType)
		{
		case TaskType.StorageScangetStockCode:
			super.closeProDia();
			StockCode = "";
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(SpiLocaPosition, null);
				if(null!=StockCodes)StockCodes.clear();

				return;
			}
			StockCodes = (List<HashMap<String,String>>)task.getTaskResult();
			StockCodesAdapter adapter = new StockCodesAdapter(this, StockCodes);
			SpiLocaPosition.setAdapter(adapter);	 
			break;
		case TaskType.STORAGE_SCAN:
			if(super.isNull) return;
			String lotSN = task.getTaskResult().toString().trim();
			if(applyScanData(lotSN)){
				saveDataToService();
			}
			break;
		case TaskType.GET_GRNNO:
			closeProDia();
			if(super.isNull)
			{
				edtGRNNO.setText("");
				return;
			}
			if(!StringUtils.isEmpty(task.getTaskResult().toString()))
				edtGRNNO.setText(task.getTaskResult().toString());
			break;
		case TaskType.GetResourceId:
			ResourceID = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logDetails(edtSysDetails, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			Params[3] = ResourceID;
			if(ResourceID.isEmpty())	 
				logDetails(edtSysDetails, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			break;
		case TaskType.STORAGE_SCAN_NETWORK:
			HashMap<String, String> getdata;
			super.closeProDia();
			this.edtOutPackageNum.setText("");
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDetails,"��MES������Ϣ��");
				return;
			}
			getdata = (HashMap<String,String>)task.getTaskResult();
			if( Integer.parseInt(getdata.get("result").toString()) == 0)
			{
				logDetails(edtSysDetails,"Success_Msg:"+ getdata.get("ReturnMsg").toString() + "\r\n");

			}else{
				logDetails(edtSysDetails, getdata.get("ReturnMsg").toString());
			}
			break;
		}

	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.Spinner_storage_locaction:
			if(null==StockCodes||0==StockCodes.size()) return;
			StockCode = StockCodes.get(arg2).get("StockCode");
			break;
		}

	}
	private void GetResourceId()
	{
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 

		if(MyApplication.getAppOwner().toString().isEmpty()){

			logDetails(edtSysDetails, "��Դ����Ϊ�գ������趨��������"  );
			return;
		}
		logDetails(edtSysDetails, "��Դ���ƣ�" + MyApplication.getAppOwner().toString() );
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);

	}

	private void logSysDetails(String logText){
		CharacterStyle ssStyle=null;
		if(logText.contains(getString(R.string.storageScan_suc))){
			ssStyle=new ForegroundColorSpan(Color.GREEN);
		}else{
			ssStyle=new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog="["+df.format(new Date())+"]"+logText+"\n";		
		SpannableStringBuilder ssBuilder=new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(edtSysDetails.getText());
		edtSysDetails.setText(ssBuilder);		
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		restoreUIData();
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_cancel:
			restoreUIData();
			break;
		case R.id.edt_reciegood_num:
			if(edtGRNNO.getText().length() >=5)
				edtOutPackageNum.requestFocus();
			else
			{
				Toast.makeText(this,"�ջ����ų���С��5", Toast.LENGTH_SHORT).show();
				edtGRNNO.setText("");
			}
			break;
		case R.id.edt_outbox_packNum: // add by byj 20140213
			if(applyScanData(edtOutPackageNum.getText().toString().trim())){
				saveDataToService();
			}
			break;
		}

	}

	private boolean saveDataToService(){
		boolean isPass = subWidget();
		if(!isPass)	return false;
		Params[0] = StockCode;
		Params[1] = edtGRNNO.getText().toString().trim();
		Params[2] = edtOutPackageNum.getText().toString().trim();
		if(Params[1].length() < 5)
		{
			logDetails(edtSysDetails, "�ջ����ų���С��5��Ϊ��" );
			return false;
		}
		//ybj	String[] paras = {this.edtOutPackageNum.getText().toString().trim(),this.edtLocaPosition.getText().toString().trim(),this.edtGRNNO.getText().toString().trim()};
		Task task = new Task(this,TaskType.STORAGE_SCAN_NETWORK,Params);
		super.progressDialog.setMessage(getString(R.string.TransationData));
		showProDia();
		model.storageScan(task);
		return true;

	}
	private void getStockCode(){
		super.progressDialog.setMessage("��ȡ�ֿ���");
		super.showProDia();	 

		Task task = new Task(this, TaskType.StorageScangetStockCode,  "");	
		model.getStockCode(task);		 
	}
	/*
	 * �������ؼ�������֤ 
	 */
	private boolean subWidget()
	{
		boolean isPass_1 = false;
		boolean isPass_2 = false;
		boolean isPass_3 = false;
		isPass_1 = subView(edtGRNNO);
		//ybj	isPass_2 = subView(edtLocaPosition);
		if(Params[3].length() > 5)
			isPass_2= true;
		isPass_3 = subView(edtOutPackageNum);
		if(!isPass_1)
		{
			Toast.makeText(this, R.string.GRNNO_NOTNULL, Toast.LENGTH_SHORT).show();
			return isPass_1;
		}
		if(!isPass_2)
		{
			Toast.makeText(this, "û�л�ȡ����ԴID", Toast.LENGTH_SHORT).show();
			return isPass_2;
		} 
		if(!isPass_3)
		{
			Toast.makeText(this, R.string.SmallBall_NOTNULL, Toast.LENGTH_SHORT).show();
			return isPass_3;
		}

		return true;
	}

	private void restoreUIData()
	{
		edtOutPackageNum.setText("");
		edtOutPackageNum.requestFocus();
	}


	/**
	 * 
	 * @param lotsn  ��ɨ�赽������Ӧ�õ�UI����  return true = ������ɨ���Ƕ�����Ż�С���Ž��е� ��Ҫ�ͷ�����ͬ������
	 */
	private boolean applyScanData(String lotsn)
	{

		if(edtGRNNO.isFocused()){
			edtGRNNO.setText(lotsn);
			edtOutPackageNum.requestFocus();
		}else if(StockCode.isEmpty())
		{
			logDetails(edtSysDetails, "�ֿ�Ϊ�գ�����ѡ��ֿ�"  );
		}else if(edtOutPackageNum.isFocused()){
			edtOutPackageNum.setText(lotsn);
			return true;
		}

		return false; 
		/*if(StringUtils.isScannedStorageSlot(lotsn))  // changed by ybj 2014-02-18
		{
			edtLocaPosition.setText(lotsn);
			edtOutPackageNum.setText("");
			edtOutPackageNum.requestFocus();
		}
		else
		{
			if(edtGRNNO.isFocused()){
				edtGRNNO.setText(lotsn);
				edtLocaPosition.requestFocus();
			}else if(edtLocaPosition.isFocused()){
				edtLocaPosition.setText(lotsn);
				edtOutPackageNum.requestFocus();
			}else if(edtOutPackageNum.isFocused())
			{
				edtOutPackageNum.setText(lotsn);
				return true;
			}else{
				edtLocaPosition.setText(lotsn);
				edtOutPackageNum.requestFocus();
			}
		}
		return false;*/
	}
	@Override
	public void onBackPressed() {
		killMainProcess();
	}
	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("ȷ���˳����������?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(StorageScanActivity.this,
						BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}



}

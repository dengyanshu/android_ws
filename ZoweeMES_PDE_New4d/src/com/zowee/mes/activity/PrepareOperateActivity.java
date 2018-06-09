package com.zowee.mes.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.SMTStorage;
import com.zowee.mes.adapter.CommonSpinnerAdapter;
import com.zowee.mes.adapter.PreparedOperateMoNamesAdapter;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.ParpareOperationModel;
import com.zowee.mes.model.StorageScanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Administrator
 * @description 备料操作
 */
public class PrepareOperateActivity extends CommonActivity implements View.OnClickListener,View.OnFocusChangeListener
{

	private ParpareOperationModel model;
	private View btnGroups;
	private final static String TAG = "activity";

	private EditText edtPrepMaterialMO;
	private EditText edtPrepMaterialOrderNum;
	private EditText edtLocaPosition;
	private EditText edtMaterialSN;
	private EditText edtSysDetails;

	private Button btnScan;
	private Button btnMakeSure;
	private Button btnCancel;
	private Button btnRefData;


	@Override
	public void init()
	{
		super.commonActivity = this;
		super.TASKTYPE = TaskType.PRAPARE_OPERATION_SCAN;
		super.init();
		model = new ParpareOperationModel();

		btnGroups = this.findViewById(R.id.btnGroups);
		btnMakeSure = (Button)btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);


		edtPrepMaterialMO = (EditText)findViewById(R.id.edt_workorder);
		edtPrepMaterialOrderNum = (EditText)findViewById(R.id.edt_materialOrdernum);
		edtLocaPosition = (EditText)findViewById(R.id.edt_storage_locaction);
		edtMaterialSN =  (EditText)findViewById(R.id.edt_material_barcode);
		edtSysDetails = (EditText)findViewById(R.id.edt_sysdetail);


		edtPrepMaterialMO.setOnClickListener(this);
		edtPrepMaterialOrderNum.setOnClickListener(this);
		edtLocaPosition.setOnClickListener(this);
		edtMaterialSN.setOnClickListener(this);

		btnMakeSure.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRefData.setOnClickListener(this);
		edtLocaPosition.setOnFocusChangeListener(this);
		edtSysDetails.append("点击 清除 可清除当前库位");
		btnMakeSure.setVisibility(View.GONE);
		btnRefData.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.prepare_operation);
		init();
	}


	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		int taskType = task.getTaskType();
		switch(taskType)
		{
		case TaskType.PRAPARE_OPERATION_SCAN:
			if(super.isNull) return;
			String lotSN = task.getTaskResult().toString().trim();
			if(applyScanData(lotSN)){
				PrepareOperation();
			}
			break;
		case TaskType.PRAPARE_OPERATION:
			if (null != task.getTaskResult()) {
				String[] reses = (String[]) task.getTaskResult();
				StringBuffer sbf = new StringBuffer();
				if (reses[0].trim().equals("0")) {
					sbf.append(getString(R.string.prepareSuccess)  );
				} else{
					sbf.append(getString(R.string.prepareFail)  );
				}				 
				sbf.append(reses[1]);
				logSysDetails(sbf.toString());
			}
			closeProDia();
			edtMaterialSN.setText("");
			break;
		}

	}

	private void logSysDetails(String logText){
		CharacterStyle ssStyle=null;
		if(logText.contains(getString(R.string.prepareSuccess))){
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
		edtMaterialSN.selectAll();
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
		case R.id.edt_workorder:
			applyScanData(edtPrepMaterialMO.getText().toString().trim());
			break;
		case R.id.edt_materialOrdernum:
			applyScanData(edtPrepMaterialOrderNum.getText().toString().trim());
			break;
		case R.id.edt_storage_locaction:
			applyScanData(edtLocaPosition.getText().toString().trim());
			break;
		case R.id.edt_material_barcode:
			if(applyScanData(edtMaterialSN.getText().toString().trim())){  // Modify by ybj 20140213
				PrepareOperation();
			}
			break;	 
		}

	}

	private boolean PrepareOperation(){
		boolean isPass = subWidget();
		if(!isPass)	return false;
		String[] paras = {this.edtMaterialSN.getText().toString().trim(),this.edtLocaPosition.getText().toString().trim(),this.edtPrepMaterialMO.getText().toString().trim(),edtPrepMaterialOrderNum.getText().toString().trim()};
		Task task = new Task(this,TaskType.PRAPARE_OPERATION,paras);
		super.progressDialog.setMessage(getString(R.string.TransationData));
		showProDia();
		model.PrepareOperation(task);
		return true;
	}


	/*
	 * 对所属控件进行验证 
	 */
	private boolean subWidget()
	{
		boolean isPass_1 = false;
		boolean isPass_2 = false;
		boolean isPass_3 = false;
		boolean isPass_4 = false;

		isPass_1 = subView(edtPrepMaterialMO);
		isPass_2 = subView(edtPrepMaterialOrderNum);
		isPass_3 = subView(edtLocaPosition);
		isPass_4 = subView(edtMaterialSN);
		if(!isPass_1)
		{
			Toast.makeText(this, R.string.moName_notNull, Toast.LENGTH_SHORT).show();
			return isPass_1;
		}
		if(!isPass_2)
		{
			Toast.makeText(this, "领料单号不能为空", Toast.LENGTH_SHORT).show();
			return isPass_2;
		}
		if(!isPass_3)
		{
			Toast.makeText(this, R.string.StocPosition_NOTNULL, Toast.LENGTH_SHORT).show();
			return isPass_3;
		}
		if(!isPass_4)
		{
			Toast.makeText(this, R.string.lotSN_notNull, Toast.LENGTH_SHORT).show();
			return isPass_4;
		}
		return true;
	}

	private void restoreUIData()
	{
		edtLocaPosition.setText("");
		edtMaterialSN.setText("");
		edtLocaPosition.requestFocus();
	}



	/**
	 * 
	 * @param lotsn  将扫描到的数据应用到UI界面  return true = 表明该扫描是对外箱号或小包号进行的 需要和服务器同步数据
	 */
	private boolean applyScanData(String lotsn)
	{
		if(StringUtils.isScannedStorageSlot(lotsn))  // changed by ybj 2014-02-18
		{
			edtLocaPosition.setText(lotsn);
			edtMaterialSN.requestFocus();
			edtMaterialSN.setText("");
		}
		else
		{
			if(edtPrepMaterialMO.isFocused()){
				edtPrepMaterialMO.setText(lotsn);
				edtPrepMaterialOrderNum.setText("");
				edtLocaPosition.setText("");
				edtMaterialSN.setText("");
				edtPrepMaterialOrderNum.requestFocus();
			}else if(edtPrepMaterialOrderNum.isFocused()){
				edtPrepMaterialOrderNum.setText(lotsn);
				edtLocaPosition.setText("");
				edtMaterialSN.setText("");
				edtLocaPosition.requestFocus();
			}else if(edtLocaPosition.isFocused())
			{
				edtLocaPosition.setText(lotsn);
				edtMaterialSN.requestFocus();
			}else{
				edtMaterialSN.setText(lotsn);			
				return true;
			}
		}
		return false;
	}
	@Override
	public void onBackPressed() {
		killMainProcess();
	}
	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出备料吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(PrepareOperateActivity.this,
						BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.edt_storage_locaction:
			if(edtLocaPosition.isFocused()){
				edtPrepMaterialMO.setText(edtPrepMaterialMO.getText().toString().toUpperCase());
				edtPrepMaterialOrderNum.setText(edtPrepMaterialOrderNum.getText().toString().toUpperCase());
			}
			break;
		}
	}

}

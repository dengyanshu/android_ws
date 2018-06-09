package com.zowee.mes.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.SplitStoreModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

public class SplitStoreActivity extends CommonActivity implements View.OnClickListener,View.OnFocusChangeListener
{
	private EditText edtSplitLot;//拆分批号
	private EditText edtTargetLot;//目标批号
//	private LaserScanOperator laserScanOperator;//激光扫描器
	private EditText edtSplitAmount;
	private View btnGroup;
	private Button btnEnsure;//	确定按钮
	private SplitStoreModel model;
	private EditText edtSysDatail;
	private Double splitLotQty ; //该变量用来存储拆分批号现有的数量
	private Button btnScan;
	private Button btnCancel;//
	private int isManulInput = 0;//默认为激光扫描输入
	private Button btnRefData;
	
	@Override
	public void init()
	{

		super.commonActivity = this;
		super.TASKTYPE = TaskType.STORAGE_SPLITE_SCAN;
		super.init();
		model = new SplitStoreModel();
		
		edtSplitLot = (EditText)this.findViewById(R.id.edt_split_batnum);
		edtSplitLot.setFocusable(true);
		edtTargetLot = (EditText)this.findViewById(R.id.edt_target_batnum);
		edtSplitAmount = (EditText)this.findViewById(R.id.edt_split_amount);
		btnGroup = this.findViewById(R.id.btnGroups);
		btnEnsure = (Button)btnGroup.findViewById(R.id.btn_ensure);
		btnScan = (Button)btnGroup.findViewById(R.id.btn_scan);
		btnCancel = (Button)btnGroup.findViewById(R.id.btn_cancel);
		btnRefData = (Button)btnGroup.findViewById(R.id.btn_refreshData);
		edtSysDatail = (EditText)this.findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		edtSplitAmount.setText("0.0");
		
		this.btnRefData.setOnClickListener(this);
		this.btnEnsure.setOnClickListener(this);
		this.btnCancel.setOnClickListener(this);
		this.btnScan.setOnClickListener(this);
		edtSplitLot.setOnFocusChangeListener(this);
		edtTargetLot.setOnFocusChangeListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.split_store);
		init();
		
	}
	
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
			case TaskType.STORAGE_SPLITE_SCAN://激光扫描
				showScanData(task);
				break;
			case TaskType.SPLIT_STORAGE_WS:
				closeProDia();
				if(super.isNull) return;
				String[] reses = (String[])task.getTaskResult();
				String res = reses[0];
				String logo = reses[1];
				StringBuffer sbf = new StringBuffer();
				if(!StringUtils.isEmpty(res)&&res.trim().equals("0"))
					sbf.append(getString(R.string.storeSplit_success)+"\n");
				else
					sbf.append(getString(R.string.storeSplit_fail)+"\n");
				if(StringUtils.isEmpty(logo))
					logo = "";
				sbf.append(logo+""+"\n\n");
				this.edtSysDatail.setText(this.edtSysDatail.getText().toString()+sbf.toString());
				break;
			case TaskType.STORAGE_SPLITE_GETPARAMS:
				closeProDia();
				if(super.isNull){edtSplitAmount.setText("0.0"); return;};
				String sourceLotQty = task.getTaskResult().toString().trim();
				if(false==StringUtils.isNumberal(sourceLotQty))//盘点该字符串是否 是数字字符串
				{
					edtSplitAmount.setText("0.0"); return;
				}
				sourceLotQty = StringUtils.conDeciStr(sourceLotQty, 1);
				edtSplitAmount.setText(sourceLotQty);
				splitLotQty = Double.valueOf(sourceLotQty);
				break;
		}
		
	}
		
	
	private void showScanData(Task task)
	{
		if(super.isNull)
			return;
		String lotSN = "";
		lotSN = task.getTaskResult().toString().trim();
		if(edtSplitLot.isFocused())
		{
			//lotSN = "Mxxxxxx6";
			edtSplitLot.setText(lotSN);
			refData(lotSN);
//			super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//			showProDia();
//			Task getParams = new Task(this, TaskType.STORAGE_SPLITE_GETPARAMS, lotSN);
//			model.getNecessData(getParams);//获取拆分批号现有的数量
			return;
		}	
		if(edtTargetLot.isFocused())
		{
			edtTargetLot.setText(lotSN);
			return;
		}	
		edtSplitLot.setText(lotSN);
		
	}
	
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_ensure:
				boolean isPass = subStoreSplitData();
				if(!isPass) return;//
				Object[] taskData = {edtSplitLot.getText().toString().trim(),Double.valueOf(edtSplitAmount.getText().toString().trim()),edtTargetLot.getText().toString().trim()};
				super.progressDialog.setMessage(getString(R.string.TransationData));
				super.showProDia();
				Task task = new Task(this,TaskType.SPLIT_STORAGE_WS,taskData);
				model.splitStorage(task);
				break;
			case R.id.btn_scan:
				super.laserScan();
				break;
			case R.id.btn_cancel:
				reStoreUIData();
				break;
			case R.id.btn_refreshData:
				String lotSN = edtSplitLot.getText().toString().trim();
				if(StringUtils.isEmpty(lotSN))
				{
					Toast.makeText(this, R.string.splitlot_notnull, Toast.LENGTH_SHORT).show();
					return;
				}
				refData(lotSN);
				break;
		}
		
	}
	
	/**
	 * 
	 * @description 对拆分出库的参数数据进行有效性的验证
	 */
	private boolean subStoreSplitData()
	{
		boolean isPass = false;
		isPass = subView(edtSplitLot);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.splitlot_notnull), Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtSplitAmount);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.splitamount_notnull), Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if(!StringUtils.isNumberal(edtSplitAmount.getText().toString().trim()))
		{
			isPass = false;
			Toast.makeText(this,getString(R.string.slit_amount)+getString(R.string.inputData_notLegal), Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		if(0>=Double.valueOf(edtSplitAmount.getText().toString().trim()).doubleValue())
		{
			isPass = false;
			Toast.makeText(this,getString(R.string.splitAmount_notLowZero) ,1500).show();
			//edtSplitAmount.selectAll();
			return isPass;
		}
		
		
		isPass = subView(edtTargetLot);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.targetlot_notnull), Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		return isPass;
	}
	
	private void reStoreUIData()
	{
		super.clearWidget(edtSplitLot, null);
		super.clearWidget(edtSplitAmount, "0.0");
		super.clearWidget(edtTargetLot, null);
		
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		EditText tempEdt = (EditText)v;
		switch(v.getId())
		{
			case R.id.edt_split_batnum:
				if(tempEdt.isFocused()&&StringUtils.isEmpty(tempEdt.getText().toString()))
					edtSysDatail.setText(edtSysDatail.getText()+getString(R.string.scan_splitLot)+"\n\n");
				break;
			case R.id.edt_target_batnum:
				if(tempEdt.isFocused()&&StringUtils.isEmpty(tempEdt.getText().toString()))
					edtSysDatail.setText(edtSysDatail.getText()+getString(R.string.scan_targetLot)+"\n\n");
				break;
		}
		
	}
	
//	@Override
//	public void afterTextChanged(Editable s) 
//	{
//		// TODO Auto-generated method stub
//		
//	}
//	
//	
   private void refData(String lotSN)
   {
	   super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
	   showProDia();
	   Task getParams = new Task(this, TaskType.STORAGE_SPLITE_GETPARAMS, lotSN);
	   model.getNecessData(getParams);
	   
   }
	
	
}



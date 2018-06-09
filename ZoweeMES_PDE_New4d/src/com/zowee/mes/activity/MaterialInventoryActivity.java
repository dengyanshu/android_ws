package com.zowee.mes.activity;

import com.zowee.mes.R;
import com.zowee.mes.model.MaterialInventoryModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * @author Administrator
 * @description 物料盘点
 */
public class MaterialInventoryActivity extends CommonActivity implements View.OnClickListener,View.OnFocusChangeListener
{

	private EditText edtLotSN;//物料批号
	private EditText edtStockLocation;//库位
	private EditText edtQTY;//物料数量
	private EditText edtRepeatLotSN;//重复的物料批号
	private EditText edtSysDetails;//操作信息显示
	private MaterialInventoryModel model;//物料盘点的业务逻辑处理类
	private View btnGroups;
	private Button btnEnsure;//确认键
	private static final String CK = "CK";//物料检查标识符
	private static final String SM = "SM";//数据保存标识符 
	private Button btnScan;
	private Button btnCancel;
	private Button btnRefData;
	
	@Override
	public void init()
	{
		model = new MaterialInventoryModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.METERIAL_INVENTORY_SCAN;
		super.init();
		edtLotSN = (EditText)this.findViewById(R.id.edt_material_batnum);
		//edtLotSN.setOnFocusChangeListener(View.OnFocusChangeListener)
		edtStockLocation = (EditText)this.findViewById(R.id.edt_storage_locaction);
		edtQTY = (EditText)this.findViewById(R.id.edt_meterial_amount);
		edtRepeatLotSN = (EditText)this.findViewById(R.id.edt_repeat_batnum);
		edtSysDetails = (EditText)this.findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnEnsure = (Button)btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);
		
		btnEnsure.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRefData.setOnClickListener(this);
		edtLotSN.setOnFocusChangeListener(this);
		edtRepeatLotSN.setOnFocusChangeListener(this);
		edtStockLocation.setOnFocusChangeListener(this);
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.meteri_inventory);
		init();
	}
	
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
			case TaskType.METERIAL_INVENTORY_SCAN:
				if(super.isNull) return;
				String lotSN = task.getTaskResult().toString().trim();
			//	String lotSN = "Mxxxxxx10";
				int focusMark = getFocusWeight();
				if(0==focusMark)
				{
					this.edtLotSN.setText(lotSN);
					
				//	Task GET_PARAMS = new Task(this, TaskType.MET_INV_GETPARAMS, lotSN);
					//model.getNecessaryParams(GET_PARAMS);//开启一个任务 根据物料批号  获取  库位 ,物料数量
					refData(lotSN);
				}
				else if(1==focusMark)
					this.edtRepeatLotSN.setText(lotSN);
				else if(3==focusMark)
					this.edtStockLocation.setText(lotSN);
				break;
			case TaskType.MET_INV_GETPARAMS:
				closeProDia();
				if(super.isNull){this.edtQTY.setText(""); this.edtStockLocation.setText(""); return;};
				String[] params = (String[])task.getTaskResult();
				if(!StringUtils.isEmpty(params[0]))
					this.edtQTY.setText(params[0]);
				if(!StringUtils.isEmpty(params[1]))
					this.edtStockLocation.setText(params[1]);
				//closeProDia();
				break;
			case TaskType.METERIAL_INVENTORY_CK:
				//closeProDia();
				if(super.isNull){closeProDia(); return;};
				String[] reses = (String[])task.getTaskResult();
				String retRes = reses[0];
				String retMsg = reses[1];
				StringBuffer sbf = new StringBuffer(edtSysDetails.getText().toString());
				if(!StringUtils.isEmpty(retRes)&&"0".equals(retRes.trim()))
				{
					sbf.append(getString(R.string.materCheck_succ)+"\n");
					String[] pars  = (String[])task.getTaskData();
					pars[4] = SM;
					//super.progressDialog.setMessage(getString(R.string.TransationData));
					//showProDia();
					Task SMTask = new Task(this, TaskType.METERIAL_INVENTORY_SM,pars);//数据保存
					model.MatInv(SMTask);//物料盘点 == 数据保存
				}
				else
				{
					sbf.append(getString(R.string.materCheck_fail)+"\n");
					closeProDia();
				}
				if(StringUtils.isEmpty(retMsg)) retMsg = "";
				sbf.append(retMsg+"\n\n");
				edtSysDetails.setText(sbf.toString());
				break;
			case TaskType.METERIAL_INVENTORY_SM:
				closeProDia();
				if(super.isNull) return;
				String[] resesSave = (String[])task.getTaskResult();
				String retResSave = resesSave[0];
				String retMsgSave = resesSave[1];
				StringBuffer sbf1 = new StringBuffer(edtSysDetails.getText().toString());
				if(!StringUtils.isEmpty(retResSave)&&"0".equals(retResSave.trim()))			
					sbf1.append(getString(R.string.materInven_succ)+"\n");
				else
					sbf1.append(getString(R.string.materInven_fail)+"\n");
				if(StringUtils.isEmpty(retMsgSave))
					retMsgSave = "";
				sbf1.append(retMsgSave+"\n\n\n");
				edtSysDetails.setText(sbf1.toString());
				break;
		}
		
	}
	
	/** 
	 * @description 判定当前的焦点在哪个输入框  0 == 物料批号输入框    1==重复扫描输入框    2==其他输入框  
	 */
	private int getFocusWeight()
	{
		if(edtLotSN.isFocused())
			return 0;
		else if(edtRepeatLotSN.isFocused())
			return 1;
		else if(edtStockLocation.isFocused()) 
			return 3;
		else 
			return 2;
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_ensure:
				boolean isPass = subWidget();
				if(!isPass) return;
				String[] params = new String[]{edtLotSN.getText().toString().trim(),this.edtStockLocation.getText().toString().trim(),this.edtQTY.getText().toString().trim(),edtRepeatLotSN.getText().toString().trim(),CK};
				Task task = new Task(this, TaskType.METERIAL_INVENTORY_CK, params);
				super.progressDialog.setMessage(getString(R.string.TransationData));
				showProDia();
				model.MatInv(task);//物料检测 
				break;
			case R.id.btn_cancel:
				restoreUIData();
				break;
			case R.id.btn_scan:
				super.laserScan();
				break;
			case R.id.btn_refreshData:
				String lotSN = edtLotSN.getText().toString().trim();
				if(StringUtils.isEmpty(lotSN))
				{
					Toast.makeText(this, R.string.materialNum_notNull, Toast.LENGTH_SHORT).show();
					return;
				}
				refData(lotSN);
				break;
		}
		
	}
	
	/**
	 * @description  对界面控件的数据进行有效性的验证
	 */
	private boolean subWidget()
	{
		boolean isPass = false;
		isPass = super.subView(edtStockLocation);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.stockLocation)+" "+getString(R.string.inputData_notNull),Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = super.subView(edtLotSN);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.materialNum_notNull),Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = super.subView(edtQTY);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.meterial_amount)+" "+getString(R.string.inputData_notNull),Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if(!StringUtils.isNumberal(edtQTY.getText().toString().trim()))
		{
			isPass = false;
			Toast.makeText(this,getString(R.string.meterial_amount)+" "+getString(R.string.inputData_notLegal),Toast.LENGTH_SHORT).show();
			return isPass;
		}
			
		isPass = super.subView(edtRepeatLotSN);
		if(!isPass)
		{
			Toast.makeText(this,getString(R.string.repeatScan)+" "+getString(R.string.inputData_notNull),Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if(!edtLotSN.getText().toString().trim().equals(edtRepeatLotSN.getText().toString().trim()))
		{
			isPass = false;
			Toast.makeText(this,R.string.matInv_dataDisaccord,Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		return isPass;
	}
	
	private void restoreUIData()
	{
		super.clearWidget(edtStockLocation, null);
		super.clearWidget(edtLotSN, null);
		super.clearWidget(edtQTY, null);
		super.clearWidget(edtRepeatLotSN, null);
		
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		switch(v.getId())
		{
			case R.id.edt_material_batnum:
				EditText edtMatBatNum = (EditText)v;
				if(edtMatBatNum.isFocused()&&StringUtils.isEmpty(edtMatBatNum.getText().toString()))
					edtSysDetails.setText(edtSysDetails.getText()+getString(R.string.please_scan)+" "+getString(R.string.meterial_batnum)+"\n\n");
				break;
			case R.id.edt_repeat_batnum:
				EditText edtRepBatNum = (EditText)v;
				if(edtRepBatNum.isFocused()&&StringUtils.isEmpty(edtRepBatNum.getText().toString()))
					edtSysDetails.setText(edtSysDetails.getText()+getString(R.string.please_scan)+" "+getString(R.string.repeatScan)+getString(R.string.meterial_batnum)+"\n\n");
				break;
			case R.id.edt_storage_locaction:
				EditText edtStorageLoc = (EditText)v;
				if(edtStorageLoc.isFocused()&&StringUtils.isEmpty(edtStorageLoc.getText().toString()))
					edtSysDetails.setText(edtSysDetails.getText()+getString(R.string.scan_storage_position)+"\n\n");
				break;
		}
		
	}
	
	private void refData(String lotSN)
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task GET_PARAMS = new Task(this, TaskType.MET_INV_GETPARAMS, lotSN);
		model.getNecessaryParams(GET_PARAMS);//开启一个任务 根据物料批号  获取  库位 ,物料数量
		
	}
	
	
}

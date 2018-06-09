package com.zowee.mes.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FinishWareHouseCartoonAdapter;
import com.zowee.mes.adapter.FinishWareHouseMoAdapter;
import com.zowee.mes.model.FinishWareHouseCattonModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
/**
 * @author Administrator
 * @description 卡通箱成品入仓
 */
public class FinishWareHouseCattonActivity extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,View.OnFocusChangeListener
{
	private Spinner spinFinishNum ;//完工入库单号
	private Spinner spinMOName;//工单名称
	private EditText edtSOName;//订单名称
	private EditText edtCartBoxSN;//卡通箱条码
	private View btnGroups;
	private Button btnInHouse;
	private Button btnCancel;
	private Button btnScan;
	private Button btnRefData;
	private FinishWareHouseCattonModel model;
	private String moId;//工单ID
	private String moName;//工单名
	private String productId;
	private String productCompleteId;//完工入库单ID
	private EditText edtOperDetails;
	private List<HashMap<String,String>> spinLisMoNames = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> spinLis= new ArrayList<HashMap<String,String>>();
	private static final String PRODUCTCOMPLETEID = "ProductCompleteID";
	private static final String COMPLETEDOCNO = "CompleteDocNo";
	private String completeDocNo;//完工的入库单号
	private static final String MONAME = "MOName";
	private static final String MOID = "MOId";
	//private static final String PRODUCTID = "ProductId";
	private static final String SONAME = "SOName";
	private Dialog dialog;
	private View diaView;
	private Button btnCheck;
	private Button btnAbolish;
	private EditText edtCheckStr;
	
	@Override
	public void init() 
	{
		model = new FinishWareHouseCattonModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FINISHWAREHOUSECARTOON_SCAN;
		super.init();
		this.spinFinishNum = (Spinner)this.findViewById(R.id.spin_finished_warehouse_num);
		this.spinMOName = (Spinner)this.findViewById(R.id.spin_workorder_name);
		this.edtCartBoxSN = (EditText)this.findViewById(R.id.edt_cattonnum);
		this.edtSOName = (EditText)this.findViewById(R.id.edt_ordername);
		btnGroups = this.findViewById(R.id.btnGroups);
		this.btnInHouse = (Button)btnGroups.findViewById(R.id.btn_ensure);
		this.btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		this.btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		this.btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);
		btnRefData.setText(R.string.advance);
		edtOperDetails = (EditText)findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		dialog = new Dialog(this, R.style.check_completenos_dialog);
		diaView = View.inflate(this, R.layout.check_completenos, null);
		dialog.setContentView(diaView);
		btnCheck = (Button)diaView.findViewById(R.id.btn_Check);
		btnAbolish = (Button)diaView.findViewById(R.id.btn_abolish);
		edtCheckStr = (EditText)diaView.findViewById(R.id.edt_checkStr);
		
		btnInHouse.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRefData.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		btnAbolish.setOnClickListener(this);
		spinFinishNum.setOnItemSelectedListener(this);
		edtCartBoxSN.setOnFocusChangeListener(this);
		spinMOName.setOnItemSelectedListener(moAdapterLis);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.finish_warehouse_catton);
		init();
		
	}
	
	@Override
	public void refresh(Task task) 
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
			case TaskType.FINISHWAREHOUSECARTOON_SCAN:
				if(super.isNull) return;
				String boxSN = task.getTaskResult().toString();
				//String boxSN = "Cxxxxxx1";
				edtCartBoxSN.setText(boxSN);
//				super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//				showProDia();
//				Task getParamsTask = new Task(this, TaskType.FINISHWAREHOUSECARTOON_GETPARAMS, boxSN);
//				model.getNecesParams(getParamsTask);
			//	refData(boxSN);
				break;
			case TaskType.FINISHWAREHOUSECARTOON_MONAME:
				closeProDia();
				if(super.isNull) return;
				//Object[] params = (Object[])task.getTaskResult();
				//if(null!=params[1]&&!StringUtils.isEmpty(params[1].toString())) 
					//this.edtSOName.setText(params[1].toString());
				//List<HashMap<String,String>> lisMaps = ;
				spinLisMoNames = (List<HashMap<String,String>>)task.getTaskResult();
				if(0==spinLisMoNames.size())
				{
					moId = "";
					moName = "";
					clearWidget(spinMOName, null);
					clearWidget(edtSOName, null);
					return;
				}
				FinishWareHouseMoAdapter adapter = new FinishWareHouseMoAdapter(this, spinLisMoNames);
				spinMOName.setAdapter(adapter);
				break;
			case TaskType.FINISHWAREHOUSECARTOON:
				closeProDia();
				if(super.isNull) return;
				String[] reses = (String[])task.getTaskResult();
				String res = reses[0];
				String logo = reses[1];
				StringBuffer sbf = new StringBuffer(edtOperDetails.getText().toString());
				if(!StringUtils.isEmpty(res)&&res.trim().equals("0"))
					sbf.append(getString(R.string.finishWarehouseCat_succ)+"\n");
				else
					sbf.append(getString(R.string.finishWarehouseCat_fail)+"\n");
				if(StringUtils.isEmpty(logo)) logo = "";
				sbf.append(logo+""+"\n\n");
				edtOperDetails.setText(sbf.toString());
				break;
			case TaskType.FINISHWAREHOUSECARTOON_DOCNOS:
				closeProDia();
				if(dialog.isShowing())dialog.dismiss();
				if(super.isNull) return;
				spinLis = (List<HashMap<String,String>>)task.getTaskResult();
				if(0==spinLis.size())
				{
					productCompleteId = "";
					completeDocNo = "";
					moId = "";
					moName = "";
					if(null!=spinLisMoNames)spinLisMoNames.clear();
					clearWidget(spinMOName, null);
					clearWidget(spinFinishNum,null);
					clearWidget(edtSOName, null);
					return;
				}
				FinishWareHouseCartoonAdapter adapter_1 = new FinishWareHouseCartoonAdapter(this, spinLis);
				spinFinishNum.setAdapter(adapter_1);
				break;
		}
		
	}
	
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_ensure://卡通箱成品入仓
				boolean isPass = subWeigdet();
				if(!isPass) return;
				String[] params = new String[]{"","","","","",""};
				params[0] = moId.trim();
				params[1] = moName.trim();
				params[2] = edtCartBoxSN.getText().toString().trim();
				params[3] = edtSOName.getText().toString().trim();
				params[4] = productCompleteId.trim();
				params[5] = completeDocNo.trim();
				Log.i(TAG, ""+productCompleteId+ " "+completeDocNo+" "+moId+" "+moName+" "+edtSOName.getText().toString());
				Task task = new Task(this, TaskType.FINISHWAREHOUSECARTOON, params);
				super.progressDialog.setMessage(getString(R.string.TransationData));
				showProDia();
				model.finishWareHouseCartoon(task);
			break;	
			case R.id.btn_cancel:
				restoreUIData();
			break;
			case R.id.btn_scan:
				super.laserScan();
			break;
			case R.id.btn_refreshData:
//				String boxSN = edtCartBoxSN.getText().toString().trim();
//				if(StringUtils.isEmpty(boxSN))
//				{
//					Toast.makeText(this, R.string.cartoonBoxSN_notNull, Toast.LENGTH_SHORT).show();
//					return;
//				}
//				refData(boxSN);
				dialog.show();
				break;
			case R.id.btn_abolish:
				dialog.dismiss();
				break;
			case R.id.btn_Check:
				String checkStr = edtCheckStr.getText().toString().trim();
				getCompleteNames(checkStr);
				break;
		}
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
	{
		if(null==spinLis||0==spinLis.size()) return;
		
		HashMap<String,String> selItem = spinLis.get(arg2);
		productCompleteId = selItem.get(PRODUCTCOMPLETEID);
		completeDocNo = selItem.get(COMPLETEDOCNO);
		Log.i(TAG, ""+productCompleteId+"  "+completeDocNo);
		getMoNames(productCompleteId);
		///Toast.makeText(this, ""+productCompleteId+" "+completeDocNo, 1000).show();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	
	private boolean subWeigdet()
	{
		boolean isPass = true;	
		isPass = super.subView(edtCartBoxSN);
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.cartoonBoxSN_notNull), 1500).show();
			return isPass;
		}
		
		if(StringUtils.isEmpty(completeDocNo))
		{
			isPass = false;
			Toast.makeText(this,R.string.completeNo_notnull, 1500).show();//完工入库
			return isPass;
		}
		
		//isPass = !StringUtils.isEmpty(productCompleteId);
		if(StringUtils.isEmpty(moName))
		{
			isPass = false;
			Toast.makeText(this, R.string.moName_isnull, 1500).show();//工单
			return isPass;
		}
		
		isPass = super.subView(edtSOName);
		if(!isPass)
		{
			Toast.makeText(this, R.string.soName_notnull, 1500).show();
			return isPass;
		}
		
		if(StringUtils.isEmpty(productCompleteId)||StringUtils.isEmpty(moId))
		{
			isPass = false;
			Toast.makeText(this, R.string.innerData_error, 1500).show();
			return isPass;
		}
		
		return isPass;
	}
	
	private void restoreUIData()
	{
		moId = "";
		moName = "";
		productCompleteId ="";
		completeDocNo = "";
	//	productId = "";
		super.clearWidget(spinFinishNum, null);
		super.clearWidget(spinMOName, null);
		if(null!=spinLis) spinLis.clear();
		if(null!=spinLisMoNames)spinLisMoNames.clear();
		super.clearWidget(edtSOName, null);
		super.clearWidget(edtCartBoxSN, null);
		
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		switch(v.getId())
		{
			case R.id.edt_cattonnum:
				EditText edtTempCartoonBox = (EditText)v;
				if(edtTempCartoonBox.isFocused()&&StringUtils.isEmpty(edtTempCartoonBox.getText().toString()))
					edtOperDetails.setText(edtOperDetails.getText()+getString(R.string.please_scan)+" "+getString(R.string.catton_num)+"\n");
				break;
		}
	}
	
	AdapterView.OnItemSelectedListener  moAdapterLis =  new AdapterView.OnItemSelectedListener() 
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
		{
			if(null==spinLisMoNames) return;
			HashMap<String,String> selItem = spinLisMoNames.get(arg2);
			moId = selItem.get(MOID);
			moName = selItem.get(MONAME);
			edtSOName.setText(selItem.get(SONAME));
			Log.i(TAG, ""+moId+" "+moName+" "+edtSOName.getText().toString());
			//if(StringUtils.isEmpty(moId)||StringUtils.isEmpty(productId)) return;
			//String[] params = new String[2];
			//params[0] = moId;
			//params[1] = productId;
//			progressDialog.setMessage(getString(R.string.loadAppNecessData));
//			showProDia();
//			Task task = new Task(FinishWareHouseCattonActivity.this,TaskType.FINISHWAREHOUSECARTOON_DOCNOS,params);
//			model.getCompleteDocNos(task);
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
//	private void refData(String boxSN)
//	{
//		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//		showProDia();
//		Task getParamsTask = new Task(this, TaskType.FINISHWAREHOUSECARTOON_GETPARAMS, boxSN);
//		model.getNecesParams(getParamsTask);
//		
//	}
	
	private void getCompleteNames(String checkStr)
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParamsTask = new Task(this, TaskType.FINISHWAREHOUSECARTOON_DOCNOS,checkStr);
		model.getCompleteDocNos(getParamsTask);
		
	}
	
	/**
	 * @param completeId  获取工单名称 ID 以及订单名
	 */
	private void getMoNames(String completeId)
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task task = new Task(this, TaskType.FINISHWAREHOUSECARTOON_MONAME, completeId);
		model.getNecesParams(task);
		
	}
	
	
}

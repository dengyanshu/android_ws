package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.PreparedOperateMoNamesAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 物料备料的业务逻辑处理类 
 */
public class ParpareOperationModel extends CommonModel 
{

	private static final String PRODUCTID = "ProductId";//
	private static  final String StOCLOCAT = "StockLocation";//库位
	private static final  String MONAME = "MOName";//工单名
	private final static String COLUMN_1 = "Column1";
	private final static String COLUMN_2 = "Column2";

	/**
	 * @param task
	 * @description 该任务用于对物料备料的数据初始化   库位初始化
	 */
	public void getNecessaryParams(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String lotSN = task.getTaskData().toString().trim();
					Soap soap = MesWebService.getMesEmptySoap();
					//	soap.addWsProperty("SQLString", "declare @lotSN nvarchar(100) select @lotSN ='"+lotSN+"'   select @lotSN = LotSn from poitemlot where boxsn = '"+lotSN+"' or LotSN = '"+lotSN+"' Select  l.ProductId ,s.StockLocation from Lot l inner join stockLoc s on l.LotSN = s.LotSN and l.LotSN = @lotSN ");
					soap.addWsProperty("SQLString", "select top 1 s.StockLocation from poitemlot pil inner join stockloc s on pil.LotSN = s.LotSN where pil.LotSN = '"+lotSN+"' or pil.BoxSN = '"+lotSN+"'; ");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					Log.i(TAG, envolop.bodyIn.toString());
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					//					if(null==resDataSet)
					//						return task;//后期可能采用通知的方法
					//					else
					//					{
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					//String[] reses = new String[1];
					String stocLoc = null;//库位
					//	String proId = null;//产品ID
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							//								if(tempMap.containsKey(PRODUCTID))
							//									proId = tempMap.get(PRODUCTID);
							if(tempMap.containsKey(StOCLOCAT))
								stocLoc = tempMap.get(StOCLOCAT);
						}
						//	Log.i(TAG, "proId:"+proId +" StocLocat: "+stocLoc);
						task.setTaskResult(stocLoc);
						//	Task initTask2 = new Task(task.getActivity(), TaskType.PRAPARE_OPERATION_INIT2, proId);
						//Task initTask2 = new Task(task.getActivity(), TaskType.PRAPARE_OPERATION_INIT2, lotSN);
						//Log.i(TAG, lotSN+"");
						//	getNecessaryParamsSon(initTask2);
					}

				}
				//} 
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}


	/**
	 * @param task
	 * @description 物料备料任务执行 
	 */
	public void PrepareOperation(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] params = (String[])task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @res int declare @excep nvarchar(100) exec @res = Txn_MaterialsGetReady @I_ReturnMessage=@ReturnMessage output,@I_ExceptionFieldName=@excep output," +
							"@I_OrBitUserId='" + MyApplication.getMseUser().getUserId()+ "',@I_OrBitUserName='" + MyApplication.getMseUser().getUserName()+"',@LotSN='" +params[0] + "', @StockLocation='" + params[1]  + "',@MOName='" + params[2] +"',"  +
							 "@MOSTDNO='"+ params[3] +"' select @res,@ReturnMessage");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					Log.i(TAG, ""+envolop.bodyIn);
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());

					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						//List<String> moNames = new ArrayList<String>(resMapsLis.size());
						String[] reses = new String[2];
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(COLUMN_1))
								reses[0] = tempMap.get(COLUMN_1);
							if(tempMap.containsKey(COLUMN_2))
								reses[1] = tempMap.get(COLUMN_2);
						}
						task.setTaskResult(reses);
					}
				}

				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);	
	}

	/**
	 * @param task
	 * @description
	 */
	public void getMoNames(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String checkStr = (String)task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", " select MOName from MO where MOName like '%"+checkStr+"%'  and MOStatus <> '' and MOStatus <> '6' and MOStatus <> '7' and MOStatus <> '8' and MOStatus <> '9' and MOStatus <> 'P' ;");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					Log.i(TAG, ""+envolop.bodyIn);
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					//					if(null==resDataSet)
					//						return task;//后期可能采用通知的方法
					//					else
					//					{

					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						//List<String> moNames = new ArrayList<String>(resMapsLis.size());
						task.setTaskResult(resMapsLis);
					}
				}
				//	} 
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);	
	}



}

/*  old prepareoperateActivity
public class PrepareOperateActivity extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,View.OnFocusChangeListener
{


	private ParpareOperationModel model;
	private View btnGroups;

	private String moName;//被选择的工单
 

	private Button btnScan;
	private Button btnCancel;
	private Button btnRefData;
	private EditText edtMoName;
	private Dialog dialog;
	private View diaView;
	
	private EditText edtLotSN ;
	private EditText edtLoc;//库位
	private EditText edtCheckStr;
	private EditText edtSysOutImfor;
	private Spinner spinner ;
	
	private Button btnEnsure;
	private Button btnCheck;
	private Button btnFinish;
	private Button btnAbolish;
	
	
	private List<HashMap<String,String>> moNames;
	
	public void init() 
	{
		model = new ParpareOperationModel();
		super.TASKTYPE = TaskType.PRAPARE_OPERATION_SCAN;
		super.commonActivity = this;
		super.init();
		
		dialog = new Dialog(this,R.style.dialog_checkMonames);
		diaView = View.inflate(this, R.layout.check_moname, null);
		dialog.setContentView(diaView);
		edtCheckStr = (EditText)dialog.findViewById(R.id.edt_checkStr);
		spinner = (Spinner)dialog.findViewById(R.id.spin_checkedlist);
		btnAbolish = (Button)dialog.findViewById(R.id.btn_abolish);
		btnFinish = (Button)dialog.findViewById(R.id.btn_finish);
		btnCheck = (Button)dialog.findViewById(R.id.btn_Check);
		edtLotSN = (EditText)this.findViewById(R.id.edt_material_barcode);
		edtLoc  = (EditText)this.findViewById(R.id.edt_storage_locaction);
		edtMoName = (EditText)this.findViewById(R.id.edt_workorder);
		//spinner = (Spinner)this.findViewById(R.id.spiner_workorder);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnEnsure = (Button)btnGroups.findViewById(R.id.btn_ensure);
		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);
		btnRefData.setText(R.string.advance);
		edtSysOutImfor = (EditText)this.findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		
		spinner.setOnItemSelectedListener(this);
		btnEnsure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnRefData.setOnClickListener(this);
		edtLotSN.setOnFocusChangeListener(this);
		edtLoc.setOnFocusChangeListener(this);
 
		btnCheck.setOnClickListener(this);
		btnAbolish.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
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
		switch(task.getTaskType())
		{
			case TaskType.PRAPARE_OPERATION_SCAN:
				if(super.isNull) return;
				String scanSN = task.getTaskResult().toString().trim();
		        boolean isLotSNScan = applyScanData(scanSN);
		        if(isLotSNScan)
		        {
		        	super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		        	showProDia();
		        	Task  getStorageLoc = new Task(this, TaskType.PRAPARE_OPEARTION_GETSTORAGELOCATION,scanSN);
		        	model.getNecessaryParams(getStorageLoc);
		        }
			//	String scanSN = "Bxxxxxx8";
				//edtLotSN.setText(scanSN);
				//refData(scanSN);
				//Task initTask1 = new Task(this, TaskType.PRAPARE_OPEARTION_INIT1, task.getTaskResult().toString());
//				Task initTask1 = new Task(this, TaskType.PRAPARE_OPEARTION_INIT1,scanSN);
//				super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//				showProDia();
//				model.getNecessaryParams(initTask1);
				break;
			case TaskType.PRAPARE_OPEARTION_GETSTORAGELOCATION:
				closeProDia();
				if(super.isNull)
				{
					this.edtLoc.setText("");
					return;
				}
				String stockPosition = task.getTaskResult().toString().trim();//库位
				this.edtLoc.setText(stockPosition);
				break;
			case TaskType.PRAPARE_OPERATION_INIT2:
//				closeProDia();
//				if(super.isNull){ clearSpinner(); return;};
//				 list 元素数量 to-do
//				moNames = (List<String>)task.getTaskResult();
//				if(0>=moNames.size()) {clearSpinner();return;};
//				this.moName = moNames.get(0);
//				CommonSpinnerAdapter<String> moNamesAdapter = new CommonSpinnerAdapter<String>(moNames, this);
//				 Log.i(TAG, ""+spinner+""+moNames);
//				spinner.setAdapter(moNamesAdapter);
				
				break;
			case TaskType.PRAPARE_OPERATION:
				closeProDia();
				if(super.isNull) return;
				String[] reses = (String[])task.getTaskResult();
				String retRes = reses[0];
				String retMsg = reses[1];
				StringBuffer sbf = new StringBuffer(this.edtSysOutImfor.getText().toString());
				if(!StringUtils.isEmpty(retRes)&&"0".equals(retRes.trim()))
					sbf.append(getString(R.string.prepareSuccess)+"\n");
				else
					sbf.append(getString(R.string.prepareFail)+"\n");
				if(StringUtils.isEmpty(retMsg))
					retMsg = "";
				sbf.append(retMsg+"\n\n");
				String resTex = sbf.toString().replace("/r/n", "   ");
				this.edtSysOutImfor.setText(resTex);
				break;
			case TaskType.PREPAREOPERATE_GETMONAMES:
				closeProDia();
				if(super.isNull)
					if(null!=moNames)moNames.clear();
				moNames = (List<HashMap<String,String>>)task.getTaskResult();
				PreparedOperateMoNamesAdapter adapter = new PreparedOperateMoNamesAdapter(this, moNames);
				this.spinner.setAdapter(adapter);
				break;
		}
		
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_ensure:
				boolean isPass = subWeight();
				if(!isPass) return;
				super.progressDialog.setMessage(getString(R.string.TransationData));
				showProDia();
				String[] params = new String[]{this.edtLotSN.getText().toString().trim(),this.edtLoc.getText().toString().trim(),this.moName.trim()};
				Task prepareOper  = new Task(this, TaskType.PRAPARE_OPERATION,params);
				model.PrepareOperation(prepareOper);
				break;
			case R.id.btn_cancel:
				restoreUIData();
				break;
			case R.id.btn_scan:
				super.laserScan();
				break;
			case R.id.btn_refreshData:
				dialog.show();
				break;
			case R.id.edt_workorder:
				
				break;
			case R.id.btn_Check:
				getMONames();
				break;
			case R.id.btn_abolish:
				dialog.dismiss();
				break;
			case R.id.btn_finish:
				edtMoName.setText(moName);
				dialog.dismiss();
				break;
		}		
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
	{
		this.moName = ((TextView)arg1).getText().toString();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
		
	}
	
	private boolean subWeight()
	{
		boolean isPass = subView(edtLotSN);
		if(!isPass)
		{
			Toast.makeText(this, R.string.lotSN_notNull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtLoc);
		if(!isPass)
		{
			Toast.makeText(this, R.string.StocPosition_NOTNULL, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		if(StringUtils.isEmpty(edtMoName.getText().toString()))
		{
			isPass = false;
			Toast.makeText(this, R.string.moName_notNull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		return isPass;
	}
	
	private void restoreUIData()
	{
		clearWidget(edtLoc, null);
		clearWidget(edtLotSN, null);
		clearWidget(edtMoName,null);
//		clearWidget(spinner, null);
//		moName = "";
//		if(null!=moNames) moNames.clear();
		//clearSpinner();
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		switch(v.getId())
		{
			case R.id.edt_material_barcode:
			    EditText edtBarcode = (EditText)v;
			    if(v.isFocused()&&StringUtils.isEmpty(edtBarcode.getText().toString()))
			    {
			    	edtSysOutImfor.setText(edtSysOutImfor.getText().toString()+getString(R.string.please_scan)+" "+getString(R.string.material_barcoade)+"\n\n");
			    	return;
			    }
				break;
			case R.id.edt_storage_locaction:
			    EditText edtStorageLocation = (EditText)v;
			    if(v.isFocused()&&StringUtils.isEmpty(edtStorageLocation.getText().toString()))
			    {
			    	edtSysOutImfor.setText(edtSysOutImfor.getText().toString()+getString(R.string.scan_storage_position)+"\n\n");
			    	return;
			    }
				break;	
		}
		
	}
	
//	private void refData(String scanSN)
//	{
//		Task initTask1 = new Task(this, TaskType.PRAPARE_OPEARTION_INIT1,scanSN);
//		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//		showProDia();
//		model.getNecessaryParams(initTask1);
//		
//	}
	
	private void clearSpinner()
	{
		clearWidget(spinner, null);
		moName = "";
		if(null!=moNames) moNames.clear();
	}
	
	// 
	//   获取查询的工单表
	//  
	private void getMONames()
	{
		Task task = new Task(this, TaskType.PREPAREOPERATE_GETMONAMES, edtCheckStr.getText().toString().trim());
		super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		showProDia();
		model.getMoNames(task);
	}
	
	 
	 // 
	// @param lotsn  将扫描到的数据应用到UI界面 
	  
	private boolean applyScanData(String lotsn)
	{
		if(edtLoc.isFocused())
			edtLoc.setText(lotsn);
		if(edtLotSN.isFocused())
		{
			edtLotSN.setText(lotsn);
			return true;
		}
			
		return false;
	}
	
	
}  */


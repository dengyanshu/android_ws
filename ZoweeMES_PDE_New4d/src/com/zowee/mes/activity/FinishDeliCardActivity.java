package com.zowee.mes.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FinishDeliCardOrdersAdapter;
import com.zowee.mes.adapter.FinishDeliCattonAdapter;
import com.zowee.mes.adapter.FinishOutCardDNNamesAdapter;
import com.zowee.mes.adapter.FinishOutCardOrdersAdapter;
import com.zowee.mes.model.FinishDeliCardModel;
import com.zowee.mes.model.FinishDeliCattonModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.DateUtils;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * @author Administrator
 * @description 卡板发货
 */
public class FinishDeliCardActivity extends CommonActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener,View.OnFocusChangeListener
{

	private Spinner spinDelOrdNum;//出货单号
	private Spinner spinOrderName;
	private EditText edtCusName;
	private EditText edtCusFullName;
	private EditText edtDatetime;
	private EditText edtCardSN;
	private Button btnEnsure;//卡板出货按钮
	private String selOutNum;//被选择的出货单号
	private String soRootId;//主订单ID
	private List<String> finOutNums;//存储从服务器加载的出货单号
//	private String dnName;//出货单号
	private EditText edtOperDetails;
	private EditText edtPier;//码头
	private FinishDeliCardModel model;
	private View btnGroups;
	private List<HashMap<String,String>> orderLisMaps;//订单单号数据集
	private List<HashMap<String,String>> dnLisMaps;//出货单号数据集
	private String soRootName;
//	private static final String SOID = "SOId";//采购订单明细ID 表为 = SO 
	private static final String SOROOTID = "SORootID";
	private static final String SOROOTNAME = "SORootName";
	private static final String DNNAME = "DNName";//出货单号
	private static final String CUSTOMERNAME = "CustomerName";//客户名
	private static final String CUSTOMERDESCRIPTION = "CustomerDescription";//用户全名
	private String customeDescri;
	private String customerName;
	private Button btnCancel;
	private Button btnScan;
	private Button btnRefData;
	private Dialog dialog;
	private View diaView;
	private EditText edtCheckStr;
	private Button btnCheck;
	private Button btnAbolish;
	
	@Override
	public void init()
	{
		model = new FinishDeliCardModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FINISHDELICARD_SCAN;
		super.init();
		
		dialog = new Dialog(this, R.style.check_deliNum_dialog);
		diaView = View.inflate(this, R.layout.check_deli_nums, null);
		dialog.setContentView(diaView);
		edtCheckStr = (EditText)diaView.findViewById(R.id.edt_checkStr);
		btnCheck = (Button)diaView.findViewById(R.id.btn_Check);
		btnAbolish = (Button)diaView.findViewById(R.id.btn_abolish);
		
		spinOrderName = (Spinner)findViewById(R.id.spin_ordername);
		spinDelOrdNum = (Spinner)findViewById(R.id.spin_shipment_num);
		edtCusName = (EditText)findViewById(R.id.edt_custom_name);
		edtCusFullName = (EditText)findViewById(R.id.edt_custom_fullname);
		edtDatetime = (EditText)findViewById(R.id.edt_shipment_date);
		edtCardSN = (EditText)findViewById(R.id.edt_cardnum);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnEnsure= (Button)btnGroups.findViewById(R.id.btn_ensure);
		btnCancel= (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnScan= (Button)btnGroups.findViewById(R.id.btn_scan);
		btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);
		btnRefData.setText(R.string.advance);
		edtOperDetails = (EditText)findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		edtPier = (EditText)findViewById(R.id.edt_pier);
		
		btnAbolish.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		spinDelOrdNum.setOnItemSelectedListener(this);
		spinOrderName.setOnItemSelectedListener(orderAdapterLis);
		btnEnsure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnRefData.setOnClickListener(this);
		edtCardSN.setOnFocusChangeListener(this);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.finish_deli_card);
		init();
	}
	
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
			case TaskType.FINISHDELICARD_SCAN:
				if(super.isNull) return;
				String cardSN = task.getTaskResult().toString().trim();
			//	String cardSN = "Pxxxxxx1";
				edtCardSN.setText(cardSN);
			//	refData(cardSN);
//				super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//				showProDia();
//				Task getParams = new Task(this, TaskType.FINISHDELICARD_GETPARAMS, cardSN);
//				model.getNecessParams(getParams);
				break;
			case TaskType.FINISHDELICARD_GETPARAMS:
				closeProDia();
				if(super.isNull) return;
			//	edtDatetime.setText(DateUtils.conDateToStr(new Date(System.currentTimeMillis()), "yyyy-MM-dd"));
				orderLisMaps = (List<HashMap<String,String>>)task.getTaskResult();
				if(0==orderLisMaps.size())
				{
					soRootName = "";
					clearWidget(spinOrderName, null);
					return;
				}
			//	Log.i(TAG, orderLisMaps.toString());
				FinishDeliCardOrdersAdapter orderAdapter = new FinishDeliCardOrdersAdapter(this, orderLisMaps);
				spinOrderName.setAdapter(orderAdapter);
				break;
			case TaskType.FINISHDELICARD:
				closeProDia();
				if(super.isNull) return;
				String[] reses = (String[])task.getTaskResult();
				String retRes = reses[0];
				String retMsg = reses[1];
				StringBuffer sbf = new StringBuffer(edtOperDetails.getText().toString());
				if(!StringUtils.isEmpty(retRes)&&"0".equals(retRes.trim()))
					sbf.append(getString(R.string.finishDeliCard_succ)+"\n");
				else
					sbf.append(getString(R.string.finishDeliCard_fail)+"\n");
				if(StringUtils.isEmpty(retMsg)) retMsg = "";
				sbf.append(retMsg+"\n\n");	
				edtOperDetails.setText(sbf.toString());
				break;
			case TaskType.FINISHDELICARD_GETDNNAMES:
				closeProDia();
				if(dialog.isShowing())dialog.dismiss();
				if(super.isNull) return;
				dnLisMaps = (List<HashMap<String,String>>)task.getTaskResult();
				if(0==dnLisMaps.size())
				{
					selOutNum = "";
					soRootId = "";
					soRootName ="";
					if(null!=orderLisMaps)orderLisMaps.clear();
					clearWidget(spinDelOrdNum, null);
					clearWidget(spinOrderName, null);
					clearWidget(edtCusName, null);
					clearWidget(edtCusFullName, null);
					clearWidget(edtDatetime, null);
					return;
				}
				FinishOutCardDNNamesAdapter dnAdapter = new FinishOutCardDNNamesAdapter(this, dnLisMaps);
				spinDelOrdNum.setAdapter(dnAdapter);
				break;
		}
		
	}
	
//	/**
//	 * @param params
//	 * @description  把从服务器返回的数据集 应用到UI界面
//	 */
//	private void appUIData(Object[] params)
//	{
//		if(null!=params[0])
//			soRootId = params[0].toString();
////		if(null!=params[1])
////			edtOrderName.setText(params[1].toString());
//		if(null!=params[2])
//		{
//			finOutNums = (List<String>)params[2];
//			FinishDeliCattonAdapter  adapter = new FinishDeliCattonAdapter(this, finOutNums);
//			spinDelOrdNum.setAdapter(adapter);
//		}
//		if(null!=params[3])
//		     edtCusName.setText(params[3].toString());
//		if(null!=params[4])
//			edtCusFullName.setText(params[4].toString());
//		if(null!=params[5])
//			edtDatetime.setText(params[5].toString());
//	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_ensure:
				boolean isPass = subUIData();
				if(!isPass) return;
				String[] params = new String[]{"","","","","",""};
				params[0] = selOutNum;
				params[1] = edtCardSN.getText().toString();
				params[2] = edtPier.getText().toString();
				params[3] = soRootId;
				params[4] = soRootName;
				params[5] = edtDatetime.getText().toString().trim();
				Log.i(TAG, ""+selOutNum+" "+soRootId+" "+soRootName);
				super.progressDialog.setMessage(getString(R.string.TransationData));
				showProDia();
				Task task = new Task(this, TaskType.FINISHDELICARD, params);
				model.FinishDeliCard(task);
				break;
			case R.id.btn_cancel:
				restoreUIData();
				break;
			case R.id.btn_scan:
				super.laserScan();
				break;
			case R.id.btn_refreshData:
//				String cardSN = edtCardSN.getText().toString().trim();
//				if(StringUtils.isEmpty(cardSN))
//				{
//					Toast.makeText(this, R.string.cardSN_notnull, Toast.LENGTH_SHORT).show();
//					return;
//				}
//				refData(cardSN);
				dialog.show();
				break;
			case R.id.btn_Check:
				getDNNames();
				break;
			case R.id.btn_abolish:
				dialog.dismiss();
				break;
				
		}
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
	{
		if(null==dnLisMaps||0==dnLisMaps.size()) return;
		HashMap<String,String> tempItem = dnLisMaps.get(arg2);
		selOutNum = tempItem.get(DNNAME);
		soRootId = tempItem.get(SOROOTID);
		customeDescri = tempItem.get(CUSTOMERDESCRIPTION);
		customerName = tempItem.get(CUSTOMERNAME);
		Log.i(TAG, " "+ " "+selOutNum +" "+customerName +" "+customeDescri+""+soRootId);
		if(StringUtils.isEmpty(customeDescri)) customeDescri = "";
		if(StringUtils.isEmpty(customerName)) customerName = "";
		//if(StringUtils.isEmpty(selOutNum)) selOutNum = "";
		edtCusName.setText(customerName);
		edtCusFullName.setText(customeDescri);
		edtDatetime.setText(DateUtils.conDateToStr(new Date(System.currentTimeMillis()), "yyyy-MM-dd"));
		getSONames(soRootId);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	private AdapterView.OnItemSelectedListener  orderAdapterLis = new AdapterView.OnItemSelectedListener() 
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
		{
			if(null==orderLisMaps||0==orderLisMaps.size()) return;
			HashMap<String,String> tempItem = orderLisMaps.get(arg2);
			//soRootId = tempItem.get(SOROOTID);
			soRootName = tempItem.get(SOROOTNAME);
			//soId = tempItem.get(SOID);
			Log.i(TAG, " "+ " " +" "+soRootName+" ");
			//progressDialog.setMessage(getString(R.string.loadAppNecessData));
			//showProDia();
			//Task task = new Task(FinishDeliCardActivity.this,TaskType.FINISHDELICARD_GETDNNAMES,soRootId);
			//model.getDNNames(task);
		}
		
		public void onNothingSelected(android.widget.AdapterView<?> arg0) 
		{
			
		};
		
	};
	
	/**
	 * 
	 * @description 
	 */
	private boolean subUIData()
	{
		boolean isPass = true;
		
		isPass = !StringUtils.isEmpty(edtCardSN.getText().toString());
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.cardSN_notnull), 1500).show();
			return isPass;
		}
		
		isPass = !StringUtils.isEmpty(selOutNum);
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.dnname_isnull), 1500).show();
			return isPass;
		}
		
		isPass = !StringUtils.isEmpty(soRootName);
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.soRootName_notnull), 1500).show();
			return isPass;
		}
		
		String tempDatetime = edtDatetime.getText().toString().trim();
		isPass = !StringUtils.isEmpty(tempDatetime);
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.datetime_isnull), 1500).show();
			return isPass;
		}
		isPass = DateUtils.subDate(tempDatetime);
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.inputDate_error), 1500).show();
			return isPass;
		}
		else 
		{
			tempDatetime = tempDatetime.replaceAll("/", "-");
			tempDatetime = DateUtils.repairDate(tempDatetime);
			//Log.i(TAG, "补全后的日期 ："+tempDatetime);
		}
		
		isPass = !StringUtils.isEmpty(edtPier.getText().toString());
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.pier_isnull), 1500).show();
			return isPass;
		}
		
		return isPass;
	}
	
	/**
	 * 
	 * @description 
	 */
	private void restoreUIData()
	{
		super.clearWidget(spinDelOrdNum, null);
		selOutNum = "";
		if(null!=dnLisMaps)dnLisMaps.clear();
		super.clearWidget(spinOrderName, null);
		if(null!=orderLisMaps)orderLisMaps.clear();
		soRootId = "";
		soRootName = "";
		//soId = "";
		super.clearWidget(edtCusName, null);
		super.clearWidget(edtCusFullName, null);
		super.clearWidget(edtDatetime, null);
		super.clearWidget(edtCardSN, null);
		super.clearWidget(edtPier, null);
		
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		switch(v.getId())
		{
			case R.id.edt_cardnum:
				EditText edtCard = (EditText)v;
				if(edtCard.isFocused()&&StringUtils.isEmpty(edtCard.getText().toString()))
				{
					edtOperDetails.setText(edtOperDetails.getText().toString()+getString(R.string.please_scan)+" "+getString(R.string.card_num)+"\n\n");
					return;
				}
				break;
		}
	}
	
//	private void refData(String cardSN)
//	{
//		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//		showProDia();
//		Task getParams = new Task(this, TaskType.FINISHDELICARD_GETPARAMS, cardSN);
//		model.getNecessParams(getParams);
//		
//	}
	
	/**
	 * 获得出货单号
	 */
	private void getDNNames()
	{
		String checkStr = edtCheckStr.getText().toString().trim();
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParams = new Task(this, TaskType.FINISHDELICARD_GETDNNAMES, checkStr);//创建一个获取卡板 出货单号的任务
		model.getDNNames(getParams);
	}
	
	/**
	 * 获取SOName
	 */
	private void getSONames(String soRootId)
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParams = new Task(this, TaskType.FINISHDELICARD_GETPARAMS, soRootId);//
		model.getNecessParams(getParams);
		
	}
	
	
}

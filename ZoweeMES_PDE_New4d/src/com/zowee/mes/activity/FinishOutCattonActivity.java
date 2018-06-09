package com.zowee.mes.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FinishOutCattonDNNamesAdapter;
import com.zowee.mes.adapter.FinishOutCattonOrdersAdapter;
import com.zowee.mes.model.FinishOutCattonModel;
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
 * @description 卡通箱成品出库
 */
public class FinishOutCattonActivity extends CommonActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener,View.OnFocusChangeListener
{
	private Spinner spinDelOrdNum;//出货单号
	private Spinner spinOrderName;
	private EditText edtCusName;
	private EditText edtCusFullName;
	private EditText edtDatetime;
	private EditText edtCatBoxSN;
//	private Button btnFinOutCat;//卡通箱扫描出库按钮
	private FinishOutCattonModel model;//卡通箱出库业务逻辑类
	private String selOutNum;//被选择的出货单号
	private String soRootId;//主订单ID
	private String soRootName;//订单名
//	private String datetime;
	//private List<String> finOutNums;//存储从服务器加载的出货单号
	//private String dnName;//出货单号
	private View btnGroups;
	private String customerName;
	private String customeDescri;
	private EditText edtOperDetails;
	private List<HashMap<String,String>> orderLisMaps;//订单单号数据集
	private List<HashMap<String,String>> dnLisMaps;//出货单号数据集
	private static final String SOROOTID = "SORootID";//销售订单ID 
	private static final String SOROOTNAME = "SORootName";//销售订单名
	private static final String DNNAME = "DNName";//出货单号
	private static final String CUSTOMERNAME = "CustomerName";//客户名
	private static final String CUSTOMERDESCRIPTION = "CustomerDescription";//用户全名
	private Button btnEnsure;
	private Button btnCancel;
	private Button btnScan;
	private Button btnRefData;
	private Dialog dialog;
	private View diaView;
	private Button btnCheck;
	private Button btnAbolish;
	private EditText edtCheckStr;
	
	@Override
	public void init()
	{
		model = new FinishOutCattonModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FINISHOUTCARTOON_SCAN;
		super.init();
		dialog = new Dialog(this, R.style.check_deliNum_dialog);
		diaView = View.inflate(this, R.layout.check_deli_nums, null);
		dialog.setContentView(diaView);
		btnAbolish = (Button)diaView.findViewById(R.id.btn_abolish);
		btnCheck = (Button)diaView.findViewById(R.id.btn_Check);
		edtCheckStr = (EditText)diaView.findViewById(R.id.edt_checkStr);
		spinOrderName = (Spinner)findViewById(R.id.spin_ordername);
		spinDelOrdNum = (Spinner)findViewById(R.id.spin_shipment_num);
		edtCusName = (EditText)findViewById(R.id.edt_custom_name);
		edtCusFullName = (EditText)findViewById(R.id.edt_custom_fullname);
		edtDatetime = (EditText)findViewById(R.id.edt_outstore_date);
		edtCatBoxSN = (EditText)findViewById(R.id.edt_cattonnum);
		//btnFinOutCat= (Button)findViewById(R.id.btn_finished_out); 、、\
		btnGroups = findViewById(R.id.btnGroups);
		btnEnsure = (Button)btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		btnRefData = (Button)btnGroups.findViewById(R.id.btn_refreshData);
		btnRefData.setText(R.string.advance);
		edtOperDetails = (EditText)findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		//edtCatBoxSN.setOnFocusChangeListener(l)
		
		btnAbolish.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		spinDelOrdNum.setOnItemSelectedListener(this);
	//	btnFinOutCat.setOnClickListener(this);
		btnEnsure.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRefData.setOnClickListener(this);
		spinOrderName.setOnItemSelectedListener(orderAdapterLis);
		edtCatBoxSN.setOnFocusChangeListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.finish_out_catton);
		init();
	}
	
	@Override
	public void refresh(Task task) 
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
			case TaskType.FINISHOUTCARTOON_SCAN:
				if(super.isNull) return;
				String catBoxSN =  task.getTaskResult().toString();
				//String catBoxSN = "Cxxxxxx1";
				edtCatBoxSN.setText(catBoxSN);
//				super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
//				showProDia();
//				Task getParams = new Task(this, TaskType.FINISHOUTCARTOON_GETPARAMS, catBoxSN);
//				model.getNecesParams(getParams);
			//	refData(catBoxSN);
				break;
			case TaskType.FINISHOUTCARTOON_SONAMES:
				closeProDia();
				if(super.isNull)return;
				//edtDatetime.setText(DateUtils.conDateToStr(new Date(System.currentTimeMillis()), "yyyy-MM-dd"));
				orderLisMaps = (List<HashMap<String,String>>)task.getTaskResult();
				if(0==orderLisMaps.size())
				{
				   //soId = "";
				   soRootName = "";
				   clearWidget(spinOrderName, null);
				   return;
				}
				FinishOutCattonOrdersAdapter orderAdapter = new FinishOutCattonOrdersAdapter(this,orderLisMaps);
				spinOrderName.setAdapter(orderAdapter);
				break;
			case TaskType.FINISHOUTCARTOON:
				closeProDia();
				if(super.isNull) return;
				StringBuffer sbf = new StringBuffer(edtOperDetails.getText().toString());
				String[] reses = (String[])task.getTaskResult();
				String retRes = reses[0];
				String retMsg = reses[1];
				if(!StringUtils.isEmpty(retRes)&&"0".equals(retRes.trim()))
					sbf.append(getString(R.string.finishOutCartoon_succ)+"\n");
				else
					sbf.append(getString(R.string.finishOutCartoon_fail)+"\n");
				if(StringUtils.isEmpty(retMsg)) retMsg = "";
				sbf.append(""+retMsg+"\n\n");
				edtOperDetails.setText(sbf.toString());
				break;
			case TaskType.FINISHOUTCARTOON_GETDNNAMES:
				closeProDia();
				if(dialog.isShowing())dialog.dismiss();
				if(super.isNull) return;
				dnLisMaps = (List<HashMap<String,String>>)task.getTaskResult();
				if(dnLisMaps.size()==0)
				{
					selOutNum = "";
					//soId = "";
					soRootId ="";
					soRootName = "";
					if(null!=orderLisMaps)orderLisMaps.clear();
					clearWidget(spinDelOrdNum, null);
					clearWidget(spinOrderName, null);
					clearWidget(edtDatetime, null);
					clearWidget(edtCusName, null);
					clearWidget(edtCusFullName, null);
					return;
				} 
				FinishOutCattonDNNamesAdapter dnAdapter = new FinishOutCattonDNNamesAdapter(this, dnLisMaps);
				spinDelOrdNum.setAdapter(dnAdapter);
				break;
				
		}
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
	{
		if(null==dnLisMaps||0==dnLisMaps.size()) return;
		HashMap<String,String> tempItem = dnLisMaps.get(arg2);
		selOutNum = tempItem.get(DNNAME);
		customeDescri = tempItem.get(CUSTOMERDESCRIPTION);
		customerName = tempItem.get(CUSTOMERNAME);
		soRootId = tempItem.get(SOROOTID);
		Log.i(TAG, " "+ " "+selOutNum +" "+customerName +" "+customeDescri+" "+soRootId);
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
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_ensure:
				boolean isPass = subUIData();
				if(!isPass) return;
				String[] params = new String[]{"","","","","",""};
				params[0] = edtCatBoxSN.getText().toString().trim();
				params[1] = selOutNum;
				params[2] = soRootId;
				params[3] = soRootName;
				params[4] = "0";// ==  --已装产品总数 可以不管它 存储过程内部自己会加载该数据
				params[5] =edtDatetime.getText().toString().trim();
				
				Log.i(TAG,""+params[0]+" "+params[1]+" "+params[2]+" "+params[3]+" "+params[4]);
				super.progressDialog.setMessage(getString(R.string.TransationData));
				showProDia();
				Task task = new Task(this, TaskType.FINISHOUTCARTOON, params);
				model.finishOutCatton(task);
				break;
			case R.id.btn_cancel:
				restoreUIData();
				break;
			case R.id.btn_scan:
				super.laserScan();//激光扫描
				break;
			case R.id.btn_refreshData:
//				String catBoxSN = edtCatBoxSN.getText().toString().trim();
//				if(StringUtils.isEmpty(catBoxSN))
//				{
//					Toast.makeText(this, R.string.cartoonBoxSN_notNull, Toast.LENGTH_SHORT).show();
//					return;
//				}
//				refData(catBoxSN);
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
	
	
	private AdapterView.OnItemSelectedListener  orderAdapterLis = new AdapterView.OnItemSelectedListener() 
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
		{
			
			if(null==orderLisMaps||0==orderLisMaps.size()) return;
			HashMap<String,String> tempItem = orderLisMaps.get(arg2);
			//so = tempItem.get(SOROOTID);
			soRootName = tempItem.get(SOROOTNAME);
			Log.i(TAG, " "+ " "+" "+soRootName);
//			progressDialog.setMessage(getString(R.string.loadAppNecessData));
//			showProDia();
//			Task task = new Task(FinishOutCattonActivity.this,TaskType.FINISHOUTCARTOON_GETDNNAMES,soRootId);
//			model.getDNNames(task);
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
		
		isPass = !StringUtils.isEmpty(edtCatBoxSN.getText().toString());
		if(!isPass)
		{
			Toast.makeText(this, getString(R.string.cartoonBoxSN_notNull), 1500).show();
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
			Toast.makeText(this, getString(R.string.soRootName_notnull), 1500).show();//订单
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
		
		return isPass;
	}
	
	/**
	 * 
	 * @description 
	 */
	private void restoreUIData()
	{
		super.clearWidget(spinDelOrdNum, null);
		if(null!=orderLisMaps)orderLisMaps.clear();
		selOutNum = "";
		super.clearWidget(spinOrderName, null);
		if(null!=dnLisMaps)dnLisMaps.clear();
		soRootId = "";
		soRootName = "";
		super.clearWidget(edtCusName, null);
		super.clearWidget(edtCusFullName, null);
		super.clearWidget(edtDatetime, null);
		super.clearWidget(edtCatBoxSN, null);
		
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) 
	{
		switch(v.getId())
		{
			case R.id.edt_cattonnum:
				EditText edtCartoonBox = (EditText)v;
				if(edtCartoonBox.isFocused()&&StringUtils.isEmpty(edtCartoonBox.getText().toString()))
				{
					edtOperDetails.setText(edtOperDetails.getText().toString()+getString(R.string.please_scan)+" "+getString(R.string.catton_num)+"\n\n");
					return;
				}
				break;
		
		}
		
	}
	
	private void refData(String catBoxSN)
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParams = new Task(this, TaskType.FINISHOUTCARTOON_SONAMES, catBoxSN);
		model.getNecesParams(getParams);
		
	}
	
	/**
	 * 获得出货单号
	 */
	private void getDNNames()
	{
		String checkStr = edtCheckStr.getText().toString().trim();
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParams = new Task(this, TaskType.FINISHOUTCARTOON_GETDNNAMES, checkStr);//创建一个获取卡板 出货单号的任务
		model.getDNNames(getParams);
	}
	
	/**
	 * 获取SOName
	 */
	private void getSONames(String soRootId)
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParams = new Task(this, TaskType.FINISHOUTCARTOON_SONAMES, soRootId);//
		model.getNecesParams(getParams);
		
	}
	
	
}

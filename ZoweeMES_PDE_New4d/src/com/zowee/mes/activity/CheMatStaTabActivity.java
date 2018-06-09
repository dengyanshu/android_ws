package com.zowee.mes.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.R.color;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.CheMatStaTabModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Administrator
 * @description 查料站表 
 */
public class CheMatStaTabActivity extends CommonActivity implements View.OnClickListener
{

	private EditText edtLineStaNo;//产线机台编辑框
	private EditText edtSlotCount;//用于记录已用的站位条码/总的站位条码数量
	private CheMatStaTabModel model;
	private String docNo;//产线
	private String staNo;//机台
	private View btnGroups;
	private TableLayout TableLayout_table;

	private Button btnScan;
	private Button btnCancel;
	private int slotCount = 0;//特定机台可以的槽位条码
	private int usingSlotCount = 0;//已用的特定机台的槽位条码

	@Override
	public void init() 
	{
		model = new CheMatStaTabModel();
		super.TASKTYPE = TaskType.CHEMATSTATAB_SCAN;
		super.commonActivity = this;
		super.init();


		btnGroups  = this.findViewById(R.id.btnGroups);
		btnGroups.findViewById(R.id.btn_ensure).setVisibility(View.GONE);
		btnGroups.findViewById(R.id.btn_refreshData).setVisibility(View.GONE);

		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
		TableLayout_table = (TableLayout)findViewById(R.id.TableLayout_main); 
		TableLayout_table.setBackgroundColor(Color.GREEN);

		edtLineStaNo = (EditText)this.findViewById(R.id.edt_meter_lineStaNo);
		edtSlotCount = (EditText)this.findViewById(R.id.edt_slotCount);

		edtLineStaNo.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		edtSlotCount.setTextColor(Color.RED);
		AddTitleToTable();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chemat_statab);
		init();
	}

	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
		case TaskType.CHEMATSTATAB_SCAN:
			if(super.isNull) return;
			String staLotSN = task.getTaskResult().toString().trim();
			refreshData(staLotSN);

			break;
		case TaskType.CHEMATSTATAB:
			closeProDia();
			@SuppressWarnings("unchecked")
			List<HashMap<String,String>> parasMapsLis = ( ArrayList<HashMap<String,String>> ) task.getTaskResult();
			HashMap<String,String> tempMap = null;
			AddTitleToTable();
			slotCount =0;
			if(parasMapsLis != null){
				for(int i=0;i<parasMapsLis.size();i++)
				{
					tempMap = parasMapsLis.get(i);
					AddValueToTable(i+1,tempMap);
				}
				slotCount = parasMapsLis.size();
				this.edtLineStaNo.setText(tempMap.get("SMTMountName"));
				getDocStaSlotCount();
			}else
				this.edtLineStaNo.setText("未找到对应料站表");

			break;
		case TaskType.CHEMATSTATAB_GETSLOTCOUNT:
			closeProDia();
			if(super.isNull)
			{
				usingSlotCount = 0;
				return;
			};
			usingSlotCount = Integer.valueOf(task.getTaskResult().toString().trim());
			edtSlotCount.setText(usingSlotCount+" / "+slotCount);
			break;
		}

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
			AddTitleToTable();
			break;
		case R.id.edt_meter_lineStaNo:
			if(StringUtils.isScannedSlotNumber(edtLineStaNo.getText().toString().trim()))
				refreshData(edtLineStaNo.getText().toString().trim());
			else
			{
				edtLineStaNo.setText("");
				Toast.makeText(this, "槽位条码不正确", 5).show();
			}
		}

	}

	private boolean refreshData(String staLotSN)
	{
		if(!StringUtils.isScannedSlotNumber(staLotSN))
		{
			Toast.makeText(this, R.string.error_staLot, Toast.LENGTH_SHORT).show();
			return false;
		}
		staLotSN = staLotSN.trim();
		String lineNo = staLotSN.substring(0,3);
		String stationNo = staLotSN.substring(3, 4);
		docNo = lineNo;
		staNo = stationNo;
		checkMatStaTab();
		return true;
	}

	private void restoreUIData()
	{	
		super.clearWidget(edtLineStaNo, null);
		super.clearWidget(edtSlotCount, null);

	}

	/**
	 * 获取某个机台槽位条码的数量
	 */
	private void getDocStaSlotCount()
	{
		if(MyApplication.getAppMoid().toString().isEmpty())
		{
			Toast.makeText(this, "请选择工单", 5).show();
			return;
		}
		String[] param = new String[]{"",""};
		param[0] = docNo.trim()+staNo.trim();
		param[1] = MyApplication.getAppMoid().toString();
		super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		showProDia();
		Task getSlotCount = new Task(this, TaskType.CHEMATSTATAB_GETSLOTCOUNT,param );
		model.getSlotCount(getSlotCount);
	}

	private void checkMatStaTab()
	{
		if(MyApplication.getAppMoid()==null)
		{
			Toast.makeText(this, "请选择工单", 5).show();
			return;
		}
		if(MyApplication.getAppDeviceTypeId()==null){
			Toast.makeText(this, "请先在上料对料程序中，获取设备类型ID！", 5).show();
			return;
		}
//		if(MyApplication.getAppDeviceTypeId().toString().isEmpty())
//		{
//			Toast.makeText(this, "请选择工单", 5).show();
//			return;
//		}	
		String[] param = new String[]{"","",""};
		param[0] = docNo.trim()+staNo.trim();
		param[1] = MyApplication.getAppMoid().toString();
		param[2] = MyApplication.getAppDeviceTypeId().toString();
		super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		showProDia();
		Task cheMatStaTab = new Task(this, TaskType.CHEMATSTATAB, param);
		model.cheMatStaTab(cheMatStaTab);

	}
	private void AddTitleToTable(){

		TableLayout_table.removeAllViews();
		TableRow row = new TableRow(CheMatStaTabActivity.this);
		row.setPadding(5, 3, 5, 2);
		row.setGravity(Gravity.CENTER);

		TextView Items = new TextView(CheMatStaTabActivity.this);
		Items.setText("序号");
		Items.setGravity(Gravity.CENTER);//文本居中
		Items.setTextSize((float) 18); 
		Items.setTextColor(Color.BLACK);  
		Items.setBackgroundColor(Color.BLUE);
		Items.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(Items); 		

		/*	TextView LineNo = new TextView(CheMatStaTabActivity.this);
		LineNo.setText("线别");
		LineNo.setGravity(Gravity.CENTER);//文本居中
		LineNo.setTextSize((float) 18); 
		LineNo.setTextColor(Color.BLACK);  
		LineNo.setBackgroundColor(Color.BLUE);
		LineNo.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(LineNo); */

		TextView StationNo = new TextView(CheMatStaTabActivity.this);
		StationNo.setText("机台");
		StationNo.setGravity(Gravity.CENTER);//文本居中
		StationNo.setTextSize((float) 18); 
		StationNo.setTextColor(Color.BLACK);  
		StationNo.setBackgroundColor(color.greenyellow);
		StationNo.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(StationNo); 

		TextView SlotNo = new TextView(CheMatStaTabActivity.this);
		SlotNo.setText("槽位");
		SlotNo.setGravity(Gravity.CENTER);//文本居中
		SlotNo.setTextSize((float) 18); 
		SlotNo.setTextColor(Color.BLACK);  
		SlotNo.setBackgroundColor(Color.BLUE);
		SlotNo.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(SlotNo); 

		TextView MaterialNo = new TextView(CheMatStaTabActivity.this);
		MaterialNo.setText("物料编码");
		MaterialNo.setGravity(Gravity.CENTER);//文本居中
		MaterialNo.setTextSize((float) 18); 
		MaterialNo.setTextColor(Color.BLACK);  
		MaterialNo.setBackgroundColor(color.greenyellow);
		MaterialNo.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(MaterialNo); 

		TextView Basequantity = new TextView(CheMatStaTabActivity.this);
		Basequantity.setText("用量数");
		Basequantity.setGravity(Gravity.CENTER);//文本居中
		Basequantity.setTextSize((float) 18); 
		Basequantity.setTextColor(Color.BLACK);  
		Basequantity.setBackgroundColor(Color.BLUE);
		Basequantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(Basequantity); 

		TextView Materialdesc = new TextView(CheMatStaTabActivity.this);
		Materialdesc.setText("物料描述");
		Materialdesc.setGravity(Gravity.CENTER);//文本居中
		Materialdesc.setTextSize((float) 18); 
		Materialdesc.setTextColor(Color.BLACK); 
		Materialdesc.setBackgroundColor(color.greenyellow);
		Materialdesc.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(Materialdesc); 

		TextView Usedquantity = new TextView(CheMatStaTabActivity.this);
		Usedquantity.setText("已上数");
		Usedquantity.setGravity(Gravity.CENTER);//文本居中
		Usedquantity.setTextSize((float) 18); 
		Usedquantity.setBackgroundColor(Color.BLUE);
		Usedquantity.setTextColor(Color.BLACK);  
		Usedquantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(Usedquantity); 

		TextView remainquantity = new TextView(CheMatStaTabActivity.this);
		remainquantity.setText("剩余数");
		remainquantity.setGravity(Gravity.CENTER);//文本居中
		remainquantity.setTextSize((float) 18); 
		remainquantity.setBackgroundColor(color.greenyellow);
		remainquantity.setTextColor(Color.BLACK);  
		remainquantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(remainquantity);  

		TableLayout_table.addView(row);  
	}

	private void AddValueToTable(int row,HashMap<String,String> RowValue ){

		TableRow Value_row = new TableRow(CheMatStaTabActivity.this);
		Value_row.setPadding(5, 1, 5, 1);
		Value_row.setBackgroundColor(Color.GREEN);  
		Value_row.setGravity(Gravity.CENTER);

		TextView Items = new TextView(CheMatStaTabActivity.this);
		Items.setText(row+"");
		Items.setGravity(Gravity.CENTER);//文本居中
		Items.setTextSize((float) 16); 
		Items.setTextColor(Color.WHITE);  
		Items.setBackgroundColor(Color.BLUE);
		Items.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(Items); 	


		/*	TextView LineNo = new TextView(CheMatStaTabActivity.this);
		LineNo.setText(RowValue.get("线别")); 
		LineNo.setGravity(Gravity.CENTER);//文本居中
		LineNo.setTextSize((float) 16); 
		LineNo.setTextColor(Color.WHITE);  
		LineNo.setBackgroundColor(color.greenyellow);
		LineNo.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(LineNo); */

		TextView StationNo = new TextView(CheMatStaTabActivity.this);
		StationNo.setText( RowValue.get("StationNo"));
		StationNo.setGravity(Gravity.CENTER);//文本居中
		StationNo.setTextSize((float) 16); 
		StationNo.setTextColor(Color.WHITE);  
		StationNo.setBackgroundColor(color.greenyellow);
		StationNo.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(StationNo); 

		TextView SlotNo = new TextView(CheMatStaTabActivity.this);
		SlotNo.setText(RowValue.get("SLotNO"));
		SlotNo.setGravity(Gravity.CENTER);//文本居中
		SlotNo.setTextSize((float) 16); 
		SlotNo.setTextColor(Color.WHITE);  
		SlotNo.setBackgroundColor(Color.BLUE);
		SlotNo.setPadding(5, 2, 5, 2);//边框左、上、右、下    
		Value_row.addView(SlotNo); 

		TextView MaterialNo = new TextView(CheMatStaTabActivity.this);
		MaterialNo.setText(RowValue.get("ProductName"));
		MaterialNo.setGravity(Gravity.CENTER);//文本居中
		MaterialNo.setTextSize((float) 16); 
		MaterialNo.setTextColor(Color.WHITE);  
		MaterialNo.setBackgroundColor(color.greenyellow);
		MaterialNo.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(MaterialNo); 

		TextView Basequantity = new TextView(CheMatStaTabActivity.this);
		Basequantity.setText(RowValue.get("BaseQty"));
		Basequantity.setGravity(Gravity.CENTER);//文本居中
		Basequantity.setTextSize((float) 16); 
		Basequantity.setTextColor(Color.WHITE);  
		Basequantity.setBackgroundColor(Color.BLUE);
		Basequantity.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(Basequantity);  

		TextView Materialdesc = new TextView(CheMatStaTabActivity.this);
		Materialdesc.setText(RowValue.get("ProductDescription"));
		Materialdesc.setGravity(Gravity.LEFT);//文本居中
		Materialdesc.setTextSize((float) 16); 
		Materialdesc.setTextColor(Color.WHITE);  
		Materialdesc.setBackgroundColor(color.greenyellow);
		Materialdesc.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(Materialdesc); 

		TextView Usedquantity = new TextView(CheMatStaTabActivity.this);
		Usedquantity.setText(RowValue.get("BeginQty"));
		Usedquantity.setGravity(Gravity.CENTER);//文本居中
		Usedquantity.setTextSize((float) 16); 
		Usedquantity.setTextColor(Color.WHITE);  
		Usedquantity.setBackgroundColor(Color.BLUE);
		Usedquantity.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(Usedquantity); 

		TextView remainquantity = new TextView(CheMatStaTabActivity.this);
		remainquantity.setText(RowValue.get("Qty"));
		remainquantity.setGravity(Gravity.CENTER);//文本居中
		remainquantity.setTextSize((float) 16); 
		remainquantity.setTextColor(Color.WHITE);  
		remainquantity.setBackgroundColor(color.greenyellow);
		remainquantity.setPadding(5, 2, 5, 2);//边框左、上、右、下   
		Value_row.addView(remainquantity);  

		TableLayout_table.addView(Value_row);  

	}
}

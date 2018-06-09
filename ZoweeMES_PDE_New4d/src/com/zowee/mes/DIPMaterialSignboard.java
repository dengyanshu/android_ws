package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R.color;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.SpecificationAdapter;
import com.zowee.mes.adapter.workcenterAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DipMOSpecificationBom;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.GetSpecificationIdAndProductionLine;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class DIPMaterialSignboard  extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

	private  AutoCompleteTextView autoDipMaterMO;
	private Spinner spinDipMaterLine;
	private Spinner spinDipMaterSpeci;
	private TableLayout TableLayout_table; 
	private Button buttDIPMaterialScan;
	private Button buttDIPmaterialSubmit;
	private Button  buttDIPmaterClear;
	private List<HashMap<String,String>> moNames;
	private GetMOnameModel GetMonamemodel;
	private DipMOSpecificationBom GetBomModel;
	private GetSpecificationIdAndProductionLine GetSpeciIdmodel;
	private String moName;
	private String moId;
	private String ResourceID;
	private String SpecificationId="";
	private String WorkcenterId="";
	private List<HashMap<String,String>> SpecificationNames;
	private List<HashMap<String,String>> WorkcenterNames;
	//private List<HashMap<String,String>>  SpecificationBom;
	private List<HashMap<String,String>>  dipmaterialList;
	private static final  String MONAME = "MOName";
	private ArrayAdapter<String> ArrayAdapterMO;
	private String[] aryMO;
	
	private Spinner spinDipMaterRemoved;
	private EditText editdipmaterSN;
	private EditText editdipmaterName;
	private String materialremoved = "";

	@Override
	public void init()
	{
		super.commonActivity = this;
		super.TASKTYPE = TaskType.Dip_matersignboardScan;
		super.init();
		GetMonamemodel = new GetMOnameModel();
		GetSpeciIdmodel = new GetSpecificationIdAndProductionLine();
		GetBomModel = new DipMOSpecificationBom();
		autoDipMaterMO = (AutoCompleteTextView)findViewById(R.id.auto_DipMaterMO);
		spinDipMaterLine = (Spinner)findViewById(R.id.spin_DipMaterLine);
		spinDipMaterSpeci = (Spinner)findViewById(R.id.spin_DipMaterSpeci);
		TableLayout_table = (TableLayout)findViewById(R.id.TableLayout_main);
		TableLayout_table.setBackgroundColor(Color.GREEN);

		spinDipMaterRemoved = (Spinner)findViewById(R.id.spin_DipMaterRemoved);
		editdipmaterSN = (EditText)findViewById(R.id.edit_dipmaterSN);
		editdipmaterName = (EditText)findViewById(R.id.edit_dipmaterName);
		buttDIPMaterialScan = (Button)findViewById(R.id.butt_DipmateScan );
		buttDIPmaterialSubmit =  (Button)findViewById(R.id.butt_DIPmateSubmit);
		buttDIPmaterClear = (Button)findViewById(R.id.butt_DIPmaterClear);
		editdipmaterSN.setOnClickListener(this);
		editdipmaterName.setOnClickListener(this);
		spinDipMaterRemoved.setOnItemSelectedListener(this);
		
		buttDIPMaterialScan.setOnClickListener(this);
		buttDIPmaterialSubmit.setOnClickListener(this);
		buttDIPmaterClear.setOnClickListener(this);
		ArrayAdapter<CharSequence> adapterMaterialremoved = ArrayAdapter
				.createFromResource(this, R.array.MaterialRemovedorNot,
						android.R.layout.simple_dropdown_item_1line);
		spinDipMaterRemoved.setAdapter(adapterMaterialremoved);
		autoDipMaterMO.setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, 
					long arg3) 
			{
				spinDipMaterSpeci.setEnabled(false);
				if(null==moNames||0==moNames.size()) return;
				moName = arg0.getItemAtPosition(arg2).toString().trim();
				moId ="";
				for(int i=0;i<moNames.size();i++){
					if( moName.equalsIgnoreCase( moNames.get(i).get(MONAME))){
						moId =  moNames.get(i).get("MOId");
						break;
					}
				}
				if(moId.isEmpty()) {
					SpecificationId = "";
					return;
				}
				Log.v("Dip",moName );
				getSpecificationId(moName);
			}		 
		});
		spinDipMaterLine.setOnItemSelectedListener(this);
		spinDipMaterSpeci.setOnItemSelectedListener(this);

		this.spinDipMaterSpeci.setEnabled(false);
 	    getDIPMONames();
  		getWorkcenters();
	//	GetResourceId(); 
		AddTitleToTable();
 
	}
	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		int taskType = task.getTaskType();

		switch(taskType)
		{
		case TaskType.Dip_matersignboardScan:
			if(super.isNull) return;
 			applyScanData(task.getTaskResult().toString().trim());
			break;
		case TaskType.GetMoNameByMOSTDType:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(autoDipMaterMO, null);
				if(null!=moNames)moNames.clear();
				moName = "";
				return;
			}
			moId = "";
			moNames = (List<HashMap<String,String>>)task.getTaskResult();
			aryMO = new String[moNames.size()];
			for(int i=0;i<moNames.size();i++)
			{
				aryMO[i] = moNames.get(i).get("MOName").toString().trim();
			}
			ArrayAdapterMO =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,aryMO);
			this.autoDipMaterMO.setAdapter(ArrayAdapterMO);
			autoDipMaterMO.setText("MO");
			break;
		case TaskType.GetResourceId:
			ResourceID = "";
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				Toast.makeText(this, "未能获取到资源ID，请检查资源名是否设置正确", 5).show();
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if(ResourceID.isEmpty())	 
				Toast.makeText(this, "未能获取到资源ID，请检查资源名是否设置正确", 5).show();
			break;
		case TaskType.GetSpecificationId:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(spinDipMaterSpeci, null);
				if(null!=SpecificationNames)SpecificationNames.clear();
				SpecificationId = "";
				return;
			}
			this.spinDipMaterSpeci.setEnabled(true);
			SpecificationNames = (List<HashMap<String,String>>)task.getTaskResult();
			SpecificationAdapter adapter2 = new SpecificationAdapter(this, SpecificationNames);
			spinDipMaterSpeci.setAdapter(adapter2);
			break;
		case TaskType.GetProductionLineName:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(spinDipMaterLine, null);
				if(null!=WorkcenterNames)WorkcenterNames.clear();
				WorkcenterId = "";
				return;
			}
			WorkcenterNames = (List<HashMap<String,String>>)task.getTaskResult();
			HashMap<String,String> showAll = new HashMap<String,String>();
			showAll.put("WorkcenterId", "显示所有产线");
			showAll.put("WorkcenterName", "显示所有产线");
			WorkcenterNames.add(0, showAll);
			workcenterAdapter adapter3 = new workcenterAdapter(this, WorkcenterNames);
			spinDipMaterLine.setAdapter(adapter3);
			break;
		/*case TaskType.Dip_MOSpecificationBom:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{		 
				if(null!=SpecificationBom)SpecificationBom.clear();
				return;
			}
			SpecificationBom = (List<HashMap<String,String>>)task.getTaskResult();
			break;*/
		case TaskType.Dip_GetMaterialList:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{		 
				if(null!=dipmaterialList)dipmaterialList.clear();
				return;
			}
			dipmaterialList = (List<HashMap<String,String>>)task.getTaskResult();
			AddValueToTable(dipmaterialList);
			break;
		}
	}
	/**
	 * 
	 * @param scanlable  将扫描到的数据应用到UI界面  
	 */
	private void applyScanData(String scanlable)
	{
		if(this.autoDipMaterMO.isFocused() ){
			autoDipMaterMO.setText(scanlable);
		}else if(this.editdipmaterSN.isFocused()){
			editdipmaterSN.setText(scanlable);
			editdipmaterName.requestFocus();
		}else if(this.editdipmaterName.isFocused()){
			editdipmaterName.setText(scanlable);
		}
 
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dipmaterial_signboard);
		init();
	}
	private void getDIPMONames()
	{
		super.progressDialog.setMessage("Get DIP MO Names");
		super.showProDia();
		String MOSTDType = "D";

		Task task = new Task(this, TaskType.GetMoNameByMOSTDType, MOSTDType);
		GetMonamemodel.getMoNamesByMOSTdType(task);
	}
	private void getWorkcenters()
	{
		super.progressDialog.setMessage("Get DIP work center");
		super.showProDia();
		Task task = new Task(this, TaskType.GetProductionLineName, "");
		GetSpeciIdmodel.GetWorkcenterId(task);
	}
	private void getSpecificationId(String Moname)
	{
		super.progressDialog.setMessage("Get DIP Specification Id");
		super.showProDia();

		Task task = new Task(this, TaskType.GetSpecificationId, Moname);
		GetSpeciIdmodel.getSpecificationId(task);
	}
	/*private void GetMOSpecificationBom()
	{
		super.progressDialog.setMessage("获取指定订单及规程DIP段BOM清单");
		super.showProDia();
		String[] param= new String[]{"",""};
		param[0] = moId;
		param[1] = SpecificationId;
		if(param[0].isEmpty()){
			
			Toast.makeText(this, "请选择工单", 5).show();
			return;
		}
		if(param[1].isEmpty()){
			Toast.makeText(this, "请选择规程", 5).show();
			return;
		}
			
		Task task = new Task(this, TaskType.Dip_MOSpecificationBom, param);
		GetBomModel.GetSpecificationBom(task);
	} */
	
	private void getDipMaterialList()
	{

		String[] param2= new String[]{"","","","","",""};
		param2[0] = moId;
		param2[1] = SpecificationId;
		param2[2] = WorkcenterId;
		param2[3] = this.editdipmaterSN.getText().toString().trim();
		param2[4] = this.editdipmaterName.getText().toString().trim();;
		param2[5] = materialremoved;
		if(param2[0].isEmpty()){
			
			Toast.makeText(this, "请选择工单", 5).show();
			return;
		}
		if(param2[1].isEmpty()){
			Toast.makeText(this, "请选择规程", 5).show();
			return;
		}
		if(param2[2].isEmpty()){
			Toast.makeText(this, "请选择线别", 5).show();
			return;		
		}
		if(param2[5].isEmpty()){
			Toast.makeText(this, "请选择是否卸料", 5).show();
			return;
		}
		super.progressDialog.setMessage("获取指定订单及规程DIP段BOM清单");
		super.showProDia();
		Task task = new Task(this, TaskType.Dip_GetMaterialList, param2);
		GetBomModel.GetDipMaterialList(task);
	}
	private void GetResourceId()
	{
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 

		if(MyApplication.getAppOwner().toString().isEmpty()){

			Toast.makeText(this, "资源名称为空，请先设定资料名称", 5).show();
			return;
		}

		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dipmaterial_signboard, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){

		case R.id.spin_DipMaterSpeci:
			
			if(null==SpecificationNames||0==SpecificationNames.size()) return;
			SpecificationId = SpecificationNames.get(arg2).get("SpecificationId");		 
			break;
		case R.id.spin_DipMaterLine:
			if(null==WorkcenterNames||0==WorkcenterNames.size()) return;
			WorkcenterId = WorkcenterNames.get(arg2).get("WorkcenterId");
			
			break;
		case R.id.spin_DipMaterRemoved:
			materialremoved = spinDipMaterRemoved.getAdapter().getItem(arg2).toString() ;  // 未卸料，已卸料，全部
			break;

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.butt_DipmateScan:
			super.laserScan();
			break;
		case R.id.butt_DIPmateSubmit:
			
			getDipMaterialList();		
			break;
		case R.id.butt_DIPmaterClear:
			
			editdipmaterName.setText("");
		    editdipmaterSN.setText("");
			editdipmaterSN.requestFocus();
			TableLayout_table.removeAllViews();
			AddTitleToTable();
			break;
		case R.id.edit_dipmaterSN:
			if( editdipmaterSN.getText().toString().trim().length() < 8)
			{
				editdipmaterSN.setText("");
			    return;
			}
			editdipmaterName.requestFocus();
			break;
		case R.id.edit_dipmaterName:
			if( editdipmaterName.getText().toString().trim().length() < 8)
			{
				editdipmaterName.setText("");
			    return;
			}
			break;
			
		}

	}
	private void AddTitleToTable(){

		TableLayout_table.removeAllViews();
		TableRow row = new TableRow(this);
		row.setPadding(5, 3, 5, 2);
		row.setGravity(Gravity.CENTER);
		row.setBackgroundColor(Color.GREEN);
	

		TextView Items = new TextView(this);
		Items.setText("序号");
		Items.setGravity(Gravity.CENTER);//文本居中
		Items.setTextSize((float) 18); 
		Items.setTextColor(Color.RED);  
		Items.setBackgroundColor(Color.WHITE);
		Items.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(Items); 		

		TextView materialLine = new TextView(this);
		materialLine.setText("线别");
		materialLine.setGravity(Gravity.CENTER);//文本居中
		materialLine.setTextSize((float) 18); 
		materialLine.setTextColor(Color.BLUE);  
		materialLine.setBackgroundColor(Color.LTGRAY);
		materialLine.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(materialLine); 
		
		TextView materialSN = new TextView(this);
		materialSN.setText("批号");
		materialSN.setGravity(Gravity.CENTER);//文本居中
		materialSN.setTextSize((float) 18); 
		materialSN.setTextColor(Color.MAGENTA);  
		materialSN.setBackgroundColor(Color.WHITE);
		materialSN.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(materialSN); 

		TextView materialName = new TextView(this);
		materialName.setText("物料");
		materialName.setGravity(Gravity.CENTER);//文本居中
		materialName.setTextSize((float) 18); 
		materialName.setTextColor(Color.BLUE);  
		materialName.setBackgroundColor(Color.LTGRAY);
		materialName.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(materialName); 

		TextView ladequantity = new TextView(this);
		ladequantity.setText("上料数量");
		ladequantity.setGravity(Gravity.CENTER);//文本居中
		ladequantity.setTextSize((float) 18); 
		ladequantity.setTextColor(Color.MAGENTA);  
		ladequantity.setBackgroundColor(Color.WHITE);
		ladequantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(ladequantity); 

		TextView remainquantity = new TextView(this);
		remainquantity.setText("可用数量");
		remainquantity.setGravity(Gravity.CENTER);//文本居中
		remainquantity.setTextSize((float) 18); 
		remainquantity.setTextColor(Color.BLUE);  
		remainquantity.setBackgroundColor(Color.LTGRAY);
		remainquantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(remainquantity); 

		TextView replacematerial = new TextView(this);
		replacematerial.setText("替换物料");
		replacematerial.setGravity(Gravity.CENTER);//文本居中
		replacematerial.setTextSize((float) 18); 
		replacematerial.setTextColor(Color.MAGENTA);  
		replacematerial.setBackgroundColor(Color.WHITE);
		replacematerial.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(replacematerial); 

		TextView RemovedOrNot = new TextView(this);
		RemovedOrNot.setText("是否卸料");
		RemovedOrNot.setGravity(Gravity.CENTER);//文本居中
		RemovedOrNot.setTextSize((float) 18); 
		RemovedOrNot.setTextColor(Color.BLUE); 
		RemovedOrNot.setBackgroundColor(Color.LTGRAY);
		RemovedOrNot.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(RemovedOrNot); 
		
		TextView LockOrNot = new TextView(this);
		LockOrNot.setText("是否锁定");
		LockOrNot.setGravity(Gravity.CENTER);//文本居中
		LockOrNot.setTextSize((float) 18); 
		LockOrNot.setTextColor(Color.MAGENTA);  
		LockOrNot.setBackgroundColor(Color.WHITE);
		LockOrNot.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(LockOrNot); 

		TextView ladeTime = new TextView(this);
		ladeTime.setText("时间");
		ladeTime.setGravity(Gravity.CENTER);//文本居中
		ladeTime.setTextSize((float) 18); 
		ladeTime.setTextColor(Color.BLUE);  
		ladeTime.setBackgroundColor(Color.LTGRAY);
		ladeTime.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		row.addView(ladeTime); 
 
		TableLayout_table.addView(row);  
	}
	private void AddValueToTable(List<HashMap<String,String>> materialList ){
		
		TableLayout_table.removeAllViews();
		AddTitleToTable();
		HashMap<String,String> tempMap = null;
		for(int i=0;i<materialList.size();i++)
		{
			tempMap = materialList.get(i);
			AddValueToRow(i+1,tempMap);
		}
		
	}
	private void AddValueToRow(int row,HashMap<String,String> RowValue ){
		
		TableRow date_row = new TableRow(this);
		date_row.setPadding(5, 3, 5, 2);
		date_row.setGravity(Gravity.CENTER);
		date_row.setBackgroundColor(Color.GREEN);

		TextView Items = new TextView(this);
		Items.setText(row + "");
		Items.setGravity(Gravity.CENTER);//文本居中
		Items.setTextSize((float) 18); 
		Items.setTextColor(Color.RED);  
		Items.setBackgroundColor(Color.WHITE);
		Items.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(Items); 	
		
		TextView materialLine = new TextView(this);
		materialLine.setText(RowValue.get("WorkcenterName"));
		materialLine.setGravity(Gravity.CENTER);//文本居中
		materialLine.setTextSize((float) 18); 
		materialLine.setTextColor(Color.BLUE);  
		materialLine.setBackgroundColor(Color.LTGRAY);
		materialLine.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(materialLine); 
		
		TextView materialSN = new TextView(this);
		materialSN.setText(RowValue.get("LotSN"));
		materialSN.setGravity(Gravity.CENTER);//文本居中
		materialSN.setTextSize((float) 18); 
		materialSN.setTextColor(Color.MAGENTA);  
		materialSN.setBackgroundColor(Color.WHITE);
		materialSN.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(materialSN); 

		TextView materialName = new TextView(this);
		materialName.setText(RowValue.get("MaterialName"));
		materialName.setGravity(Gravity.CENTER);//文本居中
		materialName.setTextSize((float) 18); 
		materialName.setTextColor(Color.BLUE);  
		materialName.setBackgroundColor(Color.LTGRAY);
		materialName.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(materialName); 

		TextView ladequantity = new TextView(this);
		ladequantity.setText(RowValue.get("LoadQty") );
		ladequantity.setGravity(Gravity.CENTER);//文本居中
		ladequantity.setTextSize((float) 18); 
		ladequantity.setTextColor(Color.MAGENTA);  
		ladequantity.setBackgroundColor(Color.WHITE);
		ladequantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(ladequantity); 

		TextView remainquantity = new TextView(this);
		remainquantity.setText(RowValue.get("NowQty") );
		remainquantity.setGravity(Gravity.CENTER);//文本居中
		remainquantity.setTextSize((float) 18); 
		remainquantity.setTextColor(Color.BLUE);  
		remainquantity.setBackgroundColor(Color.LTGRAY);
		remainquantity.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(remainquantity); 

		TextView replacematerial = new TextView(this);
		replacematerial.setText(RowValue.get("ReplaceProductName") );
		replacematerial.setGravity(Gravity.CENTER);//文本居中
		replacematerial.setTextSize((float) 18); 
		replacematerial.setTextColor(Color.MAGENTA);  
		replacematerial.setBackgroundColor(Color.WHITE);
		replacematerial.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(replacematerial); 

		TextView RemovedOrNot = new TextView(this);
		RemovedOrNot.setText(RowValue.get("IsDelete") );
		RemovedOrNot.setGravity(Gravity.CENTER);//文本居中
		RemovedOrNot.setTextSize((float) 18); 
		RemovedOrNot.setTextColor(Color.BLUE); 
		RemovedOrNot.setBackgroundColor(Color.LTGRAY);
		RemovedOrNot.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(RemovedOrNot); 
		
		TextView LockOrNot = new TextView(this);
		LockOrNot.setText(RowValue.get("IsLock") );
		LockOrNot.setGravity(Gravity.CENTER);//文本居中
		LockOrNot.setTextSize((float) 18); 
		LockOrNot.setTextColor(Color.MAGENTA);  
		LockOrNot.setBackgroundColor(Color.WHITE);
		LockOrNot.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(LockOrNot); 

		TextView ladeTime = new TextView(this);
		ladeTime.setText(RowValue.get("CreateDate") );
		ladeTime.setGravity(Gravity.CENTER);//文本居中
		ladeTime.setTextSize((float) 18); 
		ladeTime.setTextColor(Color.BLUE);  
		ladeTime.setBackgroundColor(Color.LTGRAY);
		ladeTime.setPadding(5, 3, 5, 2);//边框左、上、右、下   
		date_row.addView(ladeTime); 
 
		TableLayout_table.addView(date_row);  
	}
}

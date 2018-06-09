package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DipMOSpecificationBom;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.GetSpecificationIdAndProductionLine;
import com.zowee.mes.model.MaterialReceive;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MaterialReceiveActivity  extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

	private Spinner  SpinMaterialReceiveMO ;
	private EditText EditMaterialReceiveMODescription;
	private EditText EditMaterialReceiveSN;
	private GetMOnameModel GetMonamemodel;
    private MaterialReceive MaterialReceivemodel; 
	private EditText edtSysDatails;
	private Button   buttscan ;
	private Button   buttsubmit;
	private Button   buttcancel ;
	private Button   buttrefresh;
	private String  ResourceID;
	private String moName;
	private String moId;
	private List<HashMap<String,String>> moNames;
	String[]Params = new String[]{"","","",""};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_receive);
		init();
		this.setTitle("物料领料扫描");
		this.setTitleColor(Color.GREEN);
	}

	@Override
	public void init()
	{
		super.commonActivity = this;
		super.TASKTYPE = TaskType.MaterialReceiveScan;
		super.init();
		GetMonamemodel = new GetMOnameModel();
		MaterialReceivemodel = new MaterialReceive();
		buttscan =(Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_scan);
		buttsubmit = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_ensure);
		buttcancel = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_cancel);
		buttrefresh = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_refreshData);
		buttsubmit.setVisibility(View.GONE);
		buttrefresh.setVisibility(View.GONE);

		edtSysDatails = (EditText)findViewById(R.id.edt_sysdetail);
		SpinMaterialReceiveMO = (Spinner) findViewById(R.id.Spin_MaterialReceiveMO);
		EditMaterialReceiveMODescription = (EditText)findViewById(R.id.Edit_MaterialReceiveMODescription);
		EditMaterialReceiveSN= (EditText)findViewById(R.id.Edit_MaterialReceiveSN);
		EditMaterialReceiveSN.setOnClickListener(this);
		buttscan.setOnClickListener(this);
		buttcancel.setOnClickListener(this);
		SpinMaterialReceiveMO.setOnItemSelectedListener(this);
		logDetails(edtSysDatails, "在物料批号中输入工单号可实现自动选择工单");		
		logDetails(edtSysDatails, "使用说明：");
		getDIPMONames();		 
		GetResourceId();
		EditMaterialReceiveSN.requestFocus();
		EditMaterialReceiveMODescription.setTextColor(Color.BLUE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.material_receive, menu);
		return true;
	}
	private void getDIPMONames()
	{
		super.progressDialog.setMessage("Get DIP MO Names");
		super.showProDia();
		String MOSTDType = "";

		Task task = new Task(this, TaskType.GetMoNameByMOSTDType, MOSTDType);
		GetMonamemodel.getMoNamesByMOSTdType(task);
	}
	private void GetMOinfo(String str)
	{
		Task getWOinfo = new Task(this, TaskType.GetMOInfo, str);
		super.progressDialog.setMessage("Get MO info");
		showProDia();
		GetMonamemodel.getWoInfo(getWOinfo);

	}
	private void GetResourceId()
	{
		if(MyApplication.getAppOwner().toString().isEmpty()){

			logDetails(edtSysDatails, "资源名称为空，请先设定资料名称"  );
			return;
		}
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		logDetails(edtSysDatails, "资源名称：" + MyApplication.getAppOwner().toString() );
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);

	}
	private void MaterialReceiveScan(String str)
	{
		Params[3] = str;
		if(Params[0].isEmpty())
		{
			Toast.makeText(this, "资源名称为空，请先设定资料名称", 3).show();
			return;
		}	
		if(Params[1].isEmpty() || Params[2].isEmpty())
		{
			Toast.makeText(this, "工单号为空，请选择订单", 3).show();
			return;
		}
		super.progressDialog.setMessage("物料领料提交");
		super.showProDia();	 	 
		Task task = new Task(this, TaskType.MaterialReceiveScanSubmit, Params);	
		MaterialReceivemodel.MaterialReceiveSubmit(task);
	}
	public void refresh(Task task)
	{
		int taskType = task.getTaskType();
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		super.refresh(task);
		switch(taskType)
		{
		case TaskType.MaterialReceiveScanSubmit:
			closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				if( Integer.parseInt(getdata.get("result").toString()) == 0)
				{
					logDetails(edtSysDatails,"Success_Msg:"+ getdata.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");

				}else{
					logDetails(edtSysDatails, getdata.get("ReturnMsg").toString() );
				}
			}  
			EditMaterialReceiveSN.setText("");
			break;
		case TaskType.GetMOInfo:
			closeProDia();
			EditMaterialReceiveMODescription.setText("");
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				super.logDetails(edtSysDatails,"Success_Msg:"+ getdata.toString());
				if (getdata.get("Error") == null) {

					EditMaterialReceiveMODescription.setText(getdata.get("ProductName").toUpperCase().trim());
				}

			}  
			break;
		case TaskType.GetMoNameByMOSTDType:
			super.closeProDia();
			moName = "";
			moId = "";
			EditMaterialReceiveMODescription.setText("");
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(SpinMaterialReceiveMO, null);
				if(null!=moNames)moNames.clear();
				return;
			}
			moNames = (List<HashMap<String,String>>)task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			SpinMaterialReceiveMO.setAdapter(adapter);
			break;
		case TaskType.MaterialReceiveScan:
			if(super.isNull) return;
			String ScanStr = task.getTaskResult().toString().trim();
			applyScanData(ScanStr);
			break;
		case TaskType.GetResourceId:
			ResourceID = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logDetails(edtSysDatails, "未能获取到资源ID，请检查资源名是否设置正确");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			Params[0] = ResourceID;
			if(ResourceID.isEmpty())	 
				logDetails(edtSysDatails, "未能获取到资源ID，请检查资源名是否设置正确");
			break;
		}
	}
	private void applyScanData(String str){
		if(str.isEmpty())
		{
			return;
		}
		if(str.length() <8 ) 
		{
			Toast.makeText(this, "输入的物料批号不正确", 3).show();
			return;
		}
		if(edtSysDatails.getLineCount() >50)
			this.edtSysDatails.setText("");
		if( StringUtils.isScannedMONumber(str))	{
			String  GetItem ;				
			for(int i=0;i< SpinMaterialReceiveMO.getAdapter().getCount();i++)
			{
				GetItem = ((HashMap<String,String>)(this.SpinMaterialReceiveMO.getAdapter().getItem(i))).get("MOName").toString();
				if( GetItem.equals(str))
				{						
					SpinMaterialReceiveMO.setSelection(i);
					break;
				}
				if(i == SpinMaterialReceiveMO.getAdapter().getCount()-1)// 没有找到扫描的工单号。
				{
					Toast.makeText(this, "请确认输入的工单号是否正确", 5).show();						
				}
			}
			EditMaterialReceiveSN.setText("");
		}else{

			MaterialReceiveScan(str);
		}

	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.Spin_MaterialReceiveMO:
			if(null==moNames||0==moNames.size()) return;
			moName = moNames.get(arg2).get("MOName");
			moId =  moNames.get(arg2).get("MOId");		
			Params[1] = moId ;
			Params[2] = moName;
			GetMOinfo( moName);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){

		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.Edit_MaterialReceiveSN:
			applyScanData(EditMaterialReceiveSN.getText().toString().trim().toUpperCase());
			break;
		case R.id.btn_cancel:
			EditMaterialReceiveSN.setText("");
			break;
		}

	}
	@Override
	public void onBackPressed() {
		killMainProcess();
	}
	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出物料扫描吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(MaterialReceiveActivity.this,BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}
}

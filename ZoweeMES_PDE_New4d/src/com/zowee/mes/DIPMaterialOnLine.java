package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.adapter.SpecificationAdapter;
import com.zowee.mes.adapter.workcenterAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DIPLoadpartsModel;
import com.zowee.mes.model.FeedOnMateriModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.GetSpecificationIdAndProductionLine;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DIPMaterialOnLine extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

	private EditText edtSysDatails;

	private Spinner spinDipMaterialMO;
	private Spinner spinDipMaterialLine;
	private Spinner spinDipMaterialSpeci;


	private EditText edtDipMaterialSN;
	private Button buttDIPMaterialScan;
	private Button buttDIPmaterialSubmit;
	private GetMOnameModel GetMonamemodel;
	private GetSpecificationIdAndProductionLine GetSpeciIdmodel;
	private DIPLoadpartsModel DipLoadpartsModel;
	private static final  String MONAME = "MOName";
	private List<HashMap<String,String>> moNames;
	private List<HashMap<String,String>> SpecificationNames;
	private List<HashMap<String,String>> WorkcenterNames;
	private String SpecificationId;
	private String WorkcenterId;
	String[]Params = new String[]{"","","","","",""};
	private String moName;
	private String moId;
	private String ResourceID;
	private String	LotSN;
	@Override
	public void init()
	{
		GetMonamemodel = new GetMOnameModel();
		GetSpeciIdmodel = new GetSpecificationIdAndProductionLine();
		DipLoadpartsModel = new DIPLoadpartsModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.Dip_materialToline;
		super.init();

		edtSysDatails = (EditText)this.findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		spinDipMaterialMO = (Spinner)this.findViewById(R.id.spin_DipMaterialMO);
		spinDipMaterialLine =  (Spinner)this.findViewById(R.id.spin_DipMaterialLine);
		spinDipMaterialSpeci =  (Spinner)this.findViewById(R.id.spin_DipMaterialSpeci);
		edtDipMaterialSN =  (EditText)this.findViewById(R.id.edt_DipMaterialSN);
		buttDIPMaterialScan =  (Button)this.findViewById(R.id.butt_DIPmaterialScan);
		buttDIPmaterialSubmit =  (Button)this.findViewById(R.id.butt_DIPmaterialSubmit);




		buttDIPMaterialScan.setOnClickListener(this);

		this.buttDIPmaterialSubmit.setOnClickListener(this);
		spinDipMaterialMO.setOnItemSelectedListener(this);

		spinDipMaterialLine.setOnItemSelectedListener(this);
		spinDipMaterialSpeci.setOnItemSelectedListener(this);


		logDetails(edtSysDatails, "���������������빤���ſ�ʵ���Զ�ѡ�񹤵�");		
		logDetails(edtSysDatails, "ʹ��˵����");
		getDIPMONames();
		getWorkcenters();
		GetResourceId();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dipmaterial_on_line);
		init();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		int taskType = task.getTaskType();
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		switch(taskType)
		{
		case TaskType.Dip_materialToline:
			if(super.isNull) return;
			LotSN = task.getTaskResult().toString().trim();
			edtDipMaterialSN.setText(LotSN);
			applyScanData(LotSN);
			break;
		case TaskType.GetMoNameByMOSTDType:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(spinDipMaterialMO, null);
				if(null!=moNames)moNames.clear();
				moName = "";
				moId = "";
				return;
			}
			moNames = (List<HashMap<String,String>>)task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			spinDipMaterialMO.setAdapter(adapter);
			break;	
		case TaskType.GetResourceId:
			ResourceID = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logDetails(edtSysDatails, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if(ResourceID.isEmpty())	 
				logDetails(edtSysDatails, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			break;
		case TaskType.GetSpecificationId:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(spinDipMaterialSpeci, null);
				if(null!=SpecificationNames)SpecificationNames.clear();

				SpecificationId = "";
				return;
			}
			SpecificationNames = (List<HashMap<String,String>>)task.getTaskResult();
			SpecificationAdapter adapter2 = new SpecificationAdapter(this, SpecificationNames);
			spinDipMaterialSpeci.setAdapter(adapter2);
			break;
		case TaskType.GetProductionLineName:
			super.closeProDia();
			if(super.isNull||0==((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(spinDipMaterialLine, null);
				if(null!=WorkcenterNames)WorkcenterNames.clear();
				WorkcenterId = "";
				return;
			}
			WorkcenterNames = (List<HashMap<String,String>>)task.getTaskResult();
			workcenterAdapter adapter3 = new workcenterAdapter(this, WorkcenterNames);
			spinDipMaterialLine.setAdapter(adapter3);
			break;
		case TaskType.DIPLoadparts_start:
			super.closeProDia();
			this.edtDipMaterialSN.setText("");
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"��MES������Ϣ��");
				return;
			}
			getdata = (HashMap<String,String>)task.getTaskResult();
			if( Integer.parseInt(getdata.get("result").toString()) == 0)
			{

				logDetails(edtSysDatails,"Success_Msg:"+ getdata.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");

			}else{
				logDetails(edtSysDatails, getdata.get("ReturnMsg").toString() + ";" + getdata.get("exception").toString()+ ": "+LotSN);
			}
			break;
		}

	}
	private void applyScanData(String lotsn)
	{
		if(lotsn.length() <8 ) 
		{
			Toast.makeText(this, "������������Ų���ȷ", 3).show();
			return;
		}
		if( StringUtils.isScannedMONumber(lotsn))	{
			String  GetItem ;				
			for(int i=0;i<this.spinDipMaterialMO.getAdapter().getCount();i++)
			{
				GetItem = ((HashMap<String,String>)(this.spinDipMaterialMO.getAdapter().getItem(i))).get(MONAME).toString();
				if( GetItem.equals(lotsn))
				{						
					spinDipMaterialMO.setSelection(i);
					break;
				}
				if(i == spinDipMaterialMO.getAdapter().getCount()-1)// û���ҵ�ɨ��Ĺ����š�
				{
					Toast.makeText(this, "��ȷ������Ĺ������Ƿ���ȷ", 5).show();						
				}
			}
			this.edtDipMaterialSN.setText("");
		}else{

			DIPLoadparts(lotsn);
		}


	}

	private void DIPLoadparts(String lotsn )
	{


		Params[0] = MyApplication.getAppOwner().toString().trim();
		Params[1] = ResourceID;
		Params[2] = lotsn;
		Params[3] = moId;
		Params[4] = WorkcenterId;
		Params[5] = SpecificationId;

		if( Params[0].isEmpty() ){

			logDetails(edtSysDatails, "û���趨��Դ����");
			Toast.makeText(this, "�����趨��Դ����", 5).show();
			return;
		}
		if( Params[1].isEmpty() ){

			logDetails(edtSysDatails, "û�л�ȡ����ԴID");
			Toast.makeText(this, "�������趨��Դ����", 5).show();
			return;
		}
		if( Params[3].isEmpty() ){

			logDetails(edtSysDatails, " ��������IDΪ��");
			Toast.makeText(this, "����ѡ����������", 5).show();
			return;
		}
		if( Params[4].isEmpty() ){

			logDetails(edtSysDatails, "��������IDΪ��");
			Toast.makeText(this, "����ѡ�������߱�", 5).show();
			return;
		}		
		if( Params[5].isEmpty() ){

			logDetails(edtSysDatails, "���IDΪ��");
			Toast.makeText(this, "����ѡ����", 5).show();
			return;
		}
		super.progressDialog.setMessage("DIP ��������ɨ��");
		super.showProDia();
		Task task = new Task(this, TaskType.DIPLoadparts_start, Params);
		DipLoadpartsModel.DIPLoadparts(task);
	}
	private void getDIPMONames()
	{

		String MOSTDType = "D";
		super.progressDialog.setMessage("Get DIP MO Names");
		super.showProDia();
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
	private void GetResourceId()
	{
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 

		if(MyApplication.getAppOwner().toString().isEmpty()){

			logDetails(edtSysDatails, "��Դ����Ϊ�գ������趨��������"  );
			return;
		}
		logDetails(edtSysDatails, "��Դ���ƣ�" + MyApplication.getAppOwner().toString() );
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dipmaterial_on_line, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.spin_DipMaterialMO:
			if(null==moNames||0==moNames.size()) return;
			moName = moNames.get(arg2).get(MONAME);
			moId =  moNames.get(arg2).get("MOId");
			getSpecificationId(moName);
			break;
		case R.id.spin_DipMaterialSpeci:
			if(null==SpecificationNames||0==SpecificationNames.size()) return;
			SpecificationId = SpecificationNames.get(arg2).get("SpecificationId");
			logDetails(edtSysDatails, "ѡ��Ĺ��Ϊ:" + SpecificationNames.get(arg2).get("SpecificationName") );
			break;
		case R.id.spin_DipMaterialLine:
			if(null==WorkcenterNames||0==WorkcenterNames.size()) return;
			WorkcenterId = WorkcenterNames.get(arg2).get("WorkcenterId");
			logDetails(edtSysDatails, "ѡ����߱�Ϊ:" + WorkcenterNames.get(arg2).get("WorkcenterName") );
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
		case R.id.butt_DIPmaterialScan:
			super.laserScan();
			break;
		case R.id.butt_DIPmaterialSubmit:
			LotSN = this.edtDipMaterialSN.getText().toString().trim().toUpperCase();
			applyScanData(LotSN);
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
		.setTitle("ȷ���˳�DIP������?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(DIPMaterialOnLine.this,BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}

}

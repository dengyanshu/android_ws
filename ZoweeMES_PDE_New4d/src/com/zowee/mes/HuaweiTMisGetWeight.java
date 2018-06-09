package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R.color;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.SpecificationAdapter;
import com.zowee.mes.model.DIPLoadpartsModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.GetSpecificationIdAndProductionLine;
import com.zowee.mes.model.HWTmisGetCartonWeightModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HuaweiTMisGetWeight extends CommonActivity implements View.OnClickListener {

	private EditText edtHwTmisCartonNumber;
	private Button buttHwTmisClear;
	private Button buttHwTmisScan;
	private EditText edtSysDatails;
	private String cartonNumber;
    private HWTmisGetCartonWeightModel getweigthModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_huawei_tmis_get_weight);
		init();
		this.setTitleColor(Color.GREEN);
	 
	}


	@Override
	public void init()
	{
		getweigthModel = new HWTmisGetCartonWeightModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.Hw_GetCartonWeightScan;
		super.init();
       
		edtHwTmisCartonNumber = (EditText)findViewById(R.id.edt_HwTmisCartonNumber);
		buttHwTmisClear = (Button)findViewById(R.id.butt_HwTmisClear);
		buttHwTmisScan = (Button)findViewById(R.id.butt_HwTmisScan);	
		edtSysDatails = (EditText)findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);

		edtHwTmisCartonNumber.setOnClickListener(this);
		buttHwTmisClear.setOnClickListener(this);
		buttHwTmisScan.setOnClickListener(this);


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
		case TaskType.Hw_GetCartonWeightScan:
			if(super.isNull) return;
			cartonNumber = task.getTaskResult().toString().trim().toUpperCase();
			this.edtHwTmisCartonNumber.setText(cartonNumber);
			applyScanData(cartonNumber);
			break;
		case TaskType.Hw_GetCartonWeight:
			super.closeProDia();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
 				return;
			}
			getdata = ( HashMap<String,String>)task.getTaskResult();
		    if(getdata.get("GetWightResult").equalsIgnoreCase("true"))
		    {
		    	logDetails(edtSysDatails, "Success_Msg:" + this.edtHwTmisCartonNumber.getText().toString() + ";重量:" + getdata.get("Wight").toString());
		    	SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
		    }else
		    {
		    	logDetails(edtSysDatails,  this.edtHwTmisCartonNumber.getText() + ";"+ getdata.get("sMessage").toString());
		    	SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
		    }
		    edtHwTmisCartonNumber.selectAll();
			break;
		}
	}
	private void getCartonWeight(String cartonNumber)
	{
		super.progressDialog.setMessage("Get Carton Weight");
		super.showProDia();
		Task task = new Task(this, TaskType.Hw_GetCartonWeight, cartonNumber);
		getweigthModel.GetCartonWeight(task);
	}
	private void applyScanData(String cartonNumber)
	{
		if(cartonNumber.length() <8 ) 
		{
			edtHwTmisCartonNumber.setText("");
			return;
		}
		if(this.edtSysDatails.getLineCount() > 50)
			edtSysDatails.setText("");
		getCartonWeight(cartonNumber);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.butt_HwTmisScan:
			super.laserScan();
			break;
		case R.id.edt_HwTmisCartonNumber:
			applyScanData(edtHwTmisCartonNumber.getText().toString().trim().toUpperCase());
			break;
		case R.id.butt_HwTmisClear:
			edtHwTmisCartonNumber.setText("");
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
		.setTitle("确定退出外箱重量查询吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(HuaweiTMisGetWeight.this,BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtstick, menu);
		return true;
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_clear:
			edtSysDatails.setText("");
			break;
		}

		return true;
	}

}

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Test extends CommonActivity implements View.OnClickListener,OnKeyListener {

	private EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		init();
		this.setTitleColor(Color.GREEN);
	 
	}


	@Override
	public void init()
	{
		super.commonActivity = this;
		super.TASKTYPE = TaskType.Hw_GetCartonWeightScan;
		super.init();
       
		edit = (EditText)findViewById(R.id.test_edittext1);
		
		
		edit.setOnClickListener(this);
		edit.setOnKeyListener(this);

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
	String	cartonNumber = task.getTaskResult().toString().trim().toUpperCase();
//			this.edtHwTmisCartonNumber.setText(cartonNumber);
		applyScanData(cartonNumber);
			break;
		
		}
	}
//	private void getCartonWeight(String cartonNumber)
//	{
//		super.progressDialog.setMessage("Get Carton Weight");
//		super.showProDia();
//		Task task = new Task(this, TaskType.Hw_GetCartonWeight, cartonNumber);
//		getweigthModel.GetCartonWeight(task);
//	}
	private void applyScanData(String cartonNumber)
	{
		if(cartonNumber.length() <8 ) 
		{
			edit.setText("xiaoyu8");
			return;
		}
		else  
			edit.setText(cartonNumber);
		
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.test_edittext1:
			applyScanData(edit.getText().toString().trim().toUpperCase());
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
				stopService(new Intent(Test.this,BackgroundService.class));
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
//			edtSysDatails.setText("");
			break;
		}

		return true;
	}
	
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.test_edittext1:
			String param = edit.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			   
				edit.setText(param+"OK");
				    
					
			   
			}			
		break;
	 }
		return false;
   }
}

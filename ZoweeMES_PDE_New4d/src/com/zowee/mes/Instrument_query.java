package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.InstrumentModel;
import com.zowee.mes.model.TjchuhuoscanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Instrument_query extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
   private  Spinner  workcenterspinner;
   private  String  workcentername;
 
   private  ListView  listview;
   private List<HashMap<String,String>> list;
   
    
	private InstrumentModel instrumentModel; // ��������
	private static final String TAG = "Instrument_query";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_instrument_query);
		init();
	}

	public void onBackPressed() {
		killMainProcess();
	}
   
	// ���˼��ص����activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("ȷ���˳�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(Instrument_query.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.instrumentscan; // ��̨����̬���γ���
		//ɨ������Ҳ�Ǹ���̨����
		super.init();
        
		
		instrumentModel = new InstrumentModel();
		
	    workcenterspinner=(Spinner) findViewById(R.id.instrument_queryspinner);
	    workcenterspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				workcentername=(String) arg0.getItemAtPosition(arg2);
				getqueryresult(workcentername);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    	
		});
	   
	    listview=(ListView) findViewById(R.id.instrument_querylistview);
	    listview.setCacheColorHint(Color.TRANSPARENT);
	    getworkcenterlist();
	}
	
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String>  getdata;
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
		case TaskType.instrumentscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
		break;
		
		case TaskType.instrumentgetworkcenterlist:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				List<String> reslist=new ArrayList<String>();
				List<HashMap<String,String>> listworkcenter = (List<HashMap<String,String>>)task.getTaskResult();
				for(HashMap<String, String> map:listworkcenter){
					String workcenter=map.get("WorkcenterName");
					reslist.add(workcenter);
				}
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,reslist);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				workcenterspinner.setAdapter(adapter);
			}
		break;
		case TaskType.instrumentgetqueryresult:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				list= (List<HashMap<String,String>>)task.getTaskResult();
				
			}
			else{
				if(list!=null){
					list.clear();
				}
				else {
					list=new ArrayList<HashMap<String,String>>();
					list.clear();
				}
			}
			SimpleAdapter adapter=new SimpleAdapter(this, list, R.layout.activity_instrument_queryadapter, 
					new String[] { "InstrumentName","InstrumentType", "InstrumentDescription", "Status","WorkcenterName","IsBinding"}, 
					new int[] {R.id.instrument_queryadapter_1, R.id.instrument_queryadapter_2, R.id.instrument_queryadapter_3, R.id.instrument_queryadapter_4, R.id.instrument_queryadapter_5, R.id.instrument_queryadapter_6});
		   listview.setAdapter(adapter);
			
			
		break;
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    
	}

    private  void  getworkcenterlist(){
    	super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
    	super.showProDia();
    	instrumentModel.getcenterlist(new  Task(this,TaskType.instrumentgetworkcenterlist,""));
    }
    private  void  getqueryresult(String workcentername){
    	super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
    	super.showProDia();
    	instrumentModel.getqueryresult(new  Task(this,TaskType.instrumentgetqueryresult,workcentername));
    }
	
	
	

	
}

package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtIPQCModel;
import com.zowee.mes.model.SmtreceivematerialModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Smtreceivematerial extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	private GetMOnameModel GetMonamemodel; // ��������
	
   private   Spinner spinner;
   private  EditText  usenameedit;
   private  EditText  mzlotsnedit;
   private  ListView listview;
   private  Button button;
   private   ListView listview2;
   
   private  String  workcenterid;
   
   List<HashMap<String, String>> mzs;
   List<HashMap<String, String>> mz;
   
  
	private SmtreceivematerialModel smtreceivematerialModel; // ��������
	private static final String TAG = "Smtreceivematerial";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smt_shouliao);
		init();
	}

	protected void onResume() {
		super.onResume();
		super.progressDialog.setMessage("���ڻ�ȡ������Դ...");
		super.showProDia();
		smtreceivematerialModel.getworkcenter(new Task(this,TaskType.smtreceivematerialgetworkcenter," "));
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
								stopService(new Intent(Smtreceivematerial.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtreceivematerialscan; // ��̨����̬���γ���
		
		super.init();
        
		smtreceivematerialModel = new SmtreceivematerialModel();
		GetMonamemodel=new GetMOnameModel();

		spinner=(Spinner) findViewById(R.id.smtshouliao_spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				String workentername= (String) arg0.getItemAtPosition(arg2);
				smtreceivematerialModel.getworkcenteridbyworkcentername(new Task(Smtreceivematerial.this,TaskType.smtreceivematerialgetworkcenterid,workentername));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		usenameedit=(EditText) findViewById(R.id.smtshouliao_usenameedit);
		mzlotsnedit = (EditText) findViewById(R.id.smtshouliao_mzedit);
		listview=(ListView) findViewById(R.id.smtshouliao_listview);
		listview.setCacheColorHint(Color.TRANSPARENT);
		listview2=(ListView) findViewById(R.id.smtshouliao_listview2);
		listview2.setCacheColorHint(Color.TRANSPARENT);
		
		button = (Button) findViewById(R.id.smtshouliao_button);
		button.setFocusable(false);
		button.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
				builder.setTitle("ȷ�Ͻ��գ���ע�⣡");
				builder.setMessage("    ��˶�������Ϣ��");
				builder.setPositiveButton("ȷ��",new  DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
//						if(mz==null||mz.size()==0){
//							Toast.makeText(Smtreceivematerial.this, "�����ύ����Ϊδ�õ��˶���Ϣ", Toast.LENGTH_LONG).show();
//							return;
//						}
//						for(HashMap<String, String> map:mz){
//						   if(	map.containsValue("NO"))
//						   {
//							   Toast.makeText(Smtreceivematerial.this, "�����ύ����Ϊ�д�����������", Toast.LENGTH_LONG).show();
//							   return;
//						   }
//						}
					
						// TODO Auto-generated method stub
						progressDialog.setMessage("���ڻ�ȡ����������Ϣ...");
						showProDia();
						String[]  paras=new String[7];
						paras[0]=workcenterid;
						paras[1]=usenameedit.getText().toString();
						paras[2]=mzlotsnedit.getText().toString();
						paras[3]=resourceid;
						paras[4]=resourcename;
						paras[5]=useid;
						paras[6]=usename;
						
						smtreceivematerialModel.check(new Task(Smtreceivematerial.this,TaskType.smtreceivematerialcheck,paras));
					}

					
				});
				builder.setNegativeButton("ȡ��", null);
				builder.create().show();
				
			}
		});

		
		
		 GetResourceId();
		
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
		
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				//logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
				Toast.makeText(this, "��Դ���ƻ�ȡʧ�ܣ�����", 0).show();
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				//logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
				Toast.makeText(this, "��Դ���ƻ�ȡʧ�ܣ�����", 0).show();
			else{
				//logSysDetails("�ɹ���ȡ�����豸����ԴID:"+resourceid,"�ɹ�");
				//Toast.makeText(this, "��Դ���ƻ�ȡʧ�ܣ�����", 0).show();
			}
		break;
		
		case TaskType.smtreceivematerialscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
		case TaskType.smtreceivematerialgetworkcenter:
			super.closeProDia();
			if (null != task.getTaskResult()) {
			    List<HashMap<String, String>> workcenters = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + workcenters);
				List<String>  workcenterstrings=new  ArrayList<String>();
				for(HashMap<String, String> map:workcenters){
					workcenterstrings.add(map.get("workcentername"));
				}
				ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, workcenterstrings);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);
			} else {
				
			}
			break;
			
		case TaskType.smtreceivematerialgetworkcenterid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null&&getdata.size()!=0) {
					workcenterid = getdata.get("workcenterid");
					Log.i(TAG,"��ѡ��Ĺ�������id�ǣ�"+workcenterid);
				} else {
					
				}
				closeProDia();
			} else {
				
			}
			break;	
			
//		case TaskType.smtreceivematerialgetmzs:
//			super.closeProDia();
//			if (null != task.getTaskResult()) {
//			    mzs = (List<HashMap<String, String>>) task.getTaskResult();
//			   
//			    Log.d(TAG, "task�Ľ�������ǣ�" + mzs);
//				SimpleAdapter  adapter=new   SimpleAdapter(this,mzs,R.layout.activity_smtshouliao_adapter,
//						new String[]{"lotsn","qty","productname"},new  int[]{R.id.smtshouliao_adapter_1,R.id.smtshouliao_adapter_2,R.id.smtshouliao_adapter_3});
//				listview.setAdapter(adapter);
//			
//				   mz = new  ArrayList<HashMap<String,String>>();
//				   HashMap<String, String>  map=new HashMap<String, String>();
//				   map.put("lotsn", mzlotsnedit.getText().toString().toUpperCase());
//				   map.put("torf", "OK");
//				   mz.add(map);
//					SimpleAdapter  adapter2=new   SimpleAdapter(this,mz,R.layout.activity_smtshouliao_adapter2,
//							new String[]{"lotsn","torf",},new  int[]{R.id.smtshouliao_adapter2_1,R.id.smtshouliao_adapter2_2});
//					listview2.setAdapter(adapter2);
//				
//			} else {
//				Toast.makeText(this, "����MES��ϢΪ�գ�����WEBSERVICE����!", 1).show();
//				usenameedit.requestFocus();
//				mzlotsnedit.requestFocus();
//				mzlotsnedit.setSelectAllOnFocus(true);
//			}
//		break;
			
		
		case TaskType.smtreceivematerialcheck:
			
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				String stockoutname = null;
				if(getdata.containsKey("stockoutname"))
					stockoutname=getdata.get("stockoutname");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
                        Toast.makeText(this, "�˶Գɹ����ⵥ��"+stockoutname, 0).show();
//						SoundEffectPlayService
//								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					
						String[]  paras=new String[8];
						paras[0]=workcenterid;
						paras[1]=usenameedit.getText().toString();
						paras[2]=mzlotsnedit.getText().toString();
						paras[3]=stockoutname;
						
						paras[4]=resourceid;
						paras[5]=resourcename;
						paras[6]=useid;
						paras[7]=usename;
						
						smtreceivematerialModel.submit(new Task(Smtreceivematerial.this,TaskType.smtreceivematerialsubmit,paras));
					
					}
					else{
						super.closeProDia();
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
//						 mzs.clear();
//	                    listview.setAdapter(null);
//	                    mz.clear();
//	                    listview2.setAdapter(null);
		                    
						AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
						builder.setTitle("���Ϻ˶��������ע�⣡");
						builder.setMessage(getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("ȷ��",null) ;
						builder.setNegativeButton("ȡ��", null);
						builder.create().show();
						 
						
						usenameedit.requestFocus();
						mzlotsnedit.requestFocus();
						
//						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
//						logSysDetails(scantext, "�ɹ�");
					}
					

				} else {
					//logSysDetails("��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"+ getdata.get("Error"), "�ɹ�");
				}
				
			} else {
				//logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
		break;
			
		case TaskType.smtreceivematerialsubmit:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
				
						AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
						builder.setTitle("���Ͻ����������ע�⣡");
						builder.setMessage(getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("ȷ��",null) ;
						builder.setNegativeButton("ȡ��", null);
						builder.create().show();
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
						builder.setTitle("���Ϻ˶��������ע�⣡");
						builder.setMessage(getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("ȷ��",null) ;
						builder.setNegativeButton("ȡ��", null);
						builder.create().show();
					}
					usenameedit.requestFocus();
					mzlotsnedit.requestFocus();
					
				} else {
//					logSysDetails(
//							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
//									+ getdata.get("Error"), "�ɹ�");
				}
			} else {
//				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;	
		
//		case TaskType.smtreceivematerialsubmit:
//			super.closeProDia();
//			if (null != task.getTaskResult()) {
//				getdata = (HashMap<String, String>) task.getTaskResult();
//				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
//				if (getdata.get("Error") == null&&getdata.size()!=0) {
//
//                    //task�Ľ�������ǣ�{I_ReturnMessage=ServerMessage:�����ϵ���Ʒ���ճɹ����������ϵ���Ϊ�� L0602150912915, I_ReturnValue=0}				
////                    mzs.clear();
////                    listview.setAdapter(null);
////                    mz.clear();
////                    listview2.setAdapter(null);
//					SoundEffectPlayService
//							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
//					
//					AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
//					builder.setTitle("���Ͻ����������ע�⣡");
//					builder.setMessage(getdata.get("I_ReturnMessage"));
//					builder.setPositiveButton("ȷ��",null) ;
//					builder.setNegativeButton("ȡ��", null);
//					builder.create().show();
//					
//					usenameedit.requestFocus();
//					mzlotsnedit.requestFocus();
//					
//				} else {
//					
//				}
//			} else {
//				
//			}
//
//			break;
			
			
		
			
		
			
		
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(usenameedit.isFocused()){
		    	usenameedit.setText(scanRes);
		    	mzlotsnedit.requestFocus();
		    	
		    }
		    else if(mzlotsnedit.isFocused()){
		    	mzlotsnedit.setText(scanRes);
//		    	    if(mzs==null||mzs.size()==0){
//					
//		    	    	super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
//		    	    	super.showProDia();
//		    	    	smtreceivematerialModel.getmzs(new Task(this,TaskType.smtreceivematerialgetmzs,mzlotsnedit.getText().toString()));
//					}
//					else{
//						
//						HashMap<String, String>  map=new HashMap<String, String>();
//						map.put("lotsn", mzlotsnedit.getText().toString().toUpperCase());
//						String okorno="NO";
//						for(HashMap<String, String>  hashmap:mzs){
//							hashmap.containsValue(mzlotsnedit.getText().toString().toUpperCase());
//							okorno="OK";
//						}
//						map.put("torf", okorno);
//						mz.add(map);
//						SimpleAdapter  adapter2=new   SimpleAdapter(this,mz,R.layout.activity_smtshouliao_adapter2,
//								new String[]{"lotsn","torf",},new  int[]{R.id.smtshouliao_adapter2_1,R.id.smtshouliao_adapter2_2});
//						listview2.setAdapter(adapter2);
//					}
		    }
		    
	}
		

//	@Override
//	public boolean onKey(View v, int keycode, KeyEvent event) {
//		switch (v.getId()) {
//
//		case R.id.smtshouliao_mzedit:
//			if (KeyEvent.KEYCODE_ENTER == keycode
//			&& event.getAction() == KeyEvent.ACTION_DOWN) {
//				
//				
////				if(mzs==null||mzs.size()==0){
////				
////				super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
////				super.showProDia();
////				smtreceivematerialModel.getmzs(new Task(this,TaskType.smtreceivematerialgetmzs,mzlotsnedit.getText().toString()));
////				}
////				else{
////					String okorno="NO";
////					for(HashMap<String, String>  hashmap:mzs){
////						if(hashmap.containsValue(mzlotsnedit.getText().toString().toUpperCase()))
////						  okorno="OK";
////						else{
////							
////						}
////					}
////					HashMap<String, String>  map=new HashMap<String, String>();
////					map.put("lotsn", mzlotsnedit.getText().toString().toUpperCase());
////					map.put("torf", okorno);
////					mz.add(map);
////					SimpleAdapter  adapter2=new   SimpleAdapter(this,mz,R.layout.activity_smtshouliao_adapter2,
////							new String[]{"lotsn","torf",},new  int[]{R.id.smtshouliao_adapter2_1,R.id.smtshouliao_adapter2_2});
////					listview2.setAdapter(adapter2);
////				}
//				
//				
//				//��Ҫ���Ϻ˶Թ����� ֱ������
//				
//			}			
//			break;		
//		}
//		return  false;
//	}
	
	

}

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zowee.mes.Smtshangliaoslot.MOadapter;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtChaolingModel;
import com.zowee.mes.model.SmtshangliaoslotModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class GdOnline extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	
	private  EditText  lotsn1Edit;
	private  EditText  lotsn2Edit;
	
	
	private  String moname;
	private  Spinner  monameSpinner;
	private  String workcenter;
	private  Spinner  workcenterSpinner;
	private  String abside;
	private  Spinner  absideSpinner;
	
	private  EditText  employeeEdit;
	

   
   
   private  EditText  editscan;
   

   private GetMOnameModel GetMonamemodel; // ��������
	private SmtChaolingModel smtChaolingModel; // ��������
	private SmtshangliaoslotModel smtshangliaoslotModel; 
	private static final String TAG = "Smtjitshouliao";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_gdonline);
		init();
	}

	protected void onResume() {
		super.onResume();
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
								stopService(new Intent(GdOnline.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // ��̨����̬���γ���
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		smtChaolingModel = new SmtChaolingModel();
		smtshangliaoslotModel=new SmtshangliaoslotModel();
		
		lotsn1Edit = (EditText) findViewById(R.id.gdonline_lotsn1);
		lotsn2Edit = (EditText) findViewById(R.id.gdonline_lotsn2);
		
		
		
		monameSpinner=(Spinner) findViewById(R.id.gdonline_mo);
		monameSpinner.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 //moname=arg0.getItemAtPosition(arg2).toString();
				HashMap<String, String>  map=(HashMap<String, String>) arg0.getItemAtPosition(arg2);
				moname=map.get("MOName");
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
		final EditText  moedit=(EditText) findViewById(R.id.smtshangliaoslot_moedit);
		moedit.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String paras=moedit.getText().toString().trim();
				if(paras.length()<=2)
					return;
				progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
				showProDia();
				smtshangliaoslotModel.getMOName(new Task(GdOnline.this,100,paras));
			}
		});
		
		workcenterSpinner=(Spinner) findViewById(R.id.gdonline_workcenter);
		workcenterSpinner.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 workcenter=arg0.getItemAtPosition(arg2).toString();
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
	   
		
		absideSpinner=(Spinner) findViewById(R.id.gdonline_side);
		String[] absides={"A","B","A/B"};
		ArrayAdapter<String>  adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, absides);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		absideSpinner.setAdapter(adapter);		
		absideSpinner.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 abside=arg0.getItemAtPosition(arg2).toString();
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
	 
			   
			   
		
		employeeEdit = (EditText) findViewById(R.id.gdonline_employee);
		editscan=(EditText) findViewById(R.id.gdonline_editscan);

	   
		
        GetResourceId();
        getWorkcenter();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
	//��ȡ����
	private void getWorkcenter()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, 200, "");	
		smtChaolingModel.get_workcenter(task);

	}
	
	
	
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		switch (task.getTaskType()) {	
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
			else{
				logSysDetails("�ɹ���ȡ�����豸����ԴID:"+resourceid,"�ɹ�");
			}
		break;
		
		
		
		case 100:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				List<HashMap<String, String>>
				getdatalist = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdatalist);
				monameSpinner.setAdapter(new MOadapter(getdatalist));
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;

		case 200:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				List<HashMap<String,String>>
				workcenterinfo = (List<HashMap<String,String>>)task.getTaskResult();
				//WorkcenterId	WorkcenterName	SiteId
			
				List<String>  workcenters=new ArrayList<String>();
				for(HashMap<String, String>  map :workcenterinfo){
					workcenters.add(map.get("WorkcenterName"));
				}
				ArrayAdapter<String>  adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, workcenters);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				workcenterSpinner.setAdapter(adapter);		
			} 
			
			else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
			
			
		

		/*case 300:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
					

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;*/
			
			
			
		case 400:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
					

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
		}
	}

   
	//����
	public  void  clickme(View  v){
		String[] paras=new String[10];
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=lotsn1Edit.getText().toString();
		paras[5]=lotsn2Edit.getText().toString();
		paras[6]=moname;
		paras[7]=workcenter;
		paras[8]=abside;
		paras[9]=employeeEdit.getText().toString();
		progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
		showProDia();
		smtChaolingModel.gd_online(new Task(GdOnline.this,400,paras));
	}
	
		
	
	public  void  clickme2(View  v){
		String[] paras=new String[10];
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=lotsn1Edit.getText().toString();
		paras[5]=lotsn2Edit.getText().toString();
		paras[6]=moname;
		paras[7]=workcenter;
		paras[8]=abside;
		paras[9]=employeeEdit.getText().toString();
		progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
		showProDia();
		smtChaolingModel.gd_downline(new Task(GdOnline.this,400,paras));
	}
	
   
	
	public  void  clickme3(View  v){
		String[] paras=new String[10];
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=lotsn1Edit.getText().toString();
		paras[5]=lotsn2Edit.getText().toString();
		paras[6]=moname;
		paras[7]=workcenter;
		paras[8]=abside;
		paras[9]=employeeEdit.getText().toString();
		progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
		showProDia();
		smtChaolingModel.gd_moreplace(new Task(GdOnline.this,400,paras));
	}
	
	

	
	
	
	private void logSysDetails(String logText, String strflag) {
		CharacterStyle ssStyle = null;
		if (logText.contains(strflag)) {
			ssStyle = new ForegroundColorSpan(Color.BLUE);
		} else {
			ssStyle = new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog = "[" + df.format(new Date()) + "]" + logText + "\n";
		SpannableStringBuilder ssBuilder = new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(editscan.getText());
		editscan.setText(ssBuilder);
	}
	
	 class  MOadapter extends  BaseAdapter{
		   List<HashMap<String, String>> list;
		   MOadapter(List<HashMap<String, String>> list){
			   this.list=list;
		   }
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}
		
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return list.get(position);
			}
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
		
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				if(convertView==null){
					convertView=getLayoutInflater().inflate(R.layout.smtshangliaoslot_listview, null);
				}
				TextView tv=(TextView) convertView.findViewById(R.id.textView1);
				tv.setText(list.get(position).get("MOName"));
				return convertView;
			}
	   }
		
	
	
}

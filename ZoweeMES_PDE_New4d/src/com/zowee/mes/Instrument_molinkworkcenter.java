package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.Button;
import android.widget.EditText;
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
public class Instrument_molinkworkcenter extends CommonActivity implements
		OnKeyListener,android.view.View.OnClickListener,OnItemSelectedListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
   
   private  EditText  workcenteredit;
   private  String  workcenterid;
   
   private  Spinner  mospinner;
   private  EditText  moedit;
   private  EditText  modescriedit;
   private  EditText  moproductedit;
   private  String mo;
   
   private  Button  okbutton;
   private  Button  removebindingbutton;
   
 
   private  EditText  editscan;
   
    private GetMOnameModel GetMonamemodel; // ��������
	private InstrumentModel instrumentModel; // ��������
	private static final String TAG = "Instrument_molinkworkcenter";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_instrument_molinkworkcenter);
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
								stopService(new Intent(Instrument_molinkworkcenter.this,
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
        
		GetMonamemodel=new GetMOnameModel();
		instrumentModel = new InstrumentModel();
		
		workcenteredit=(EditText) findViewById(R.id.instrument_molinkworkcenter_workcenter);
		
		mospinner=(Spinner) findViewById(R.id.instrument_molinkworkcenter_mospinner);
		moedit=(EditText) findViewById(R.id.instrument_molinkworkcenter_moedit);
		modescriedit=(EditText) findViewById(R.id.instrument_molinkworkcenter_modecri);
		moproductedit=(EditText) findViewById(R.id.instrument_molinkworkcenter_moproductname);
		
		okbutton=(Button) findViewById(R.id.instrument_molinkworkcenter_okbutton);
		removebindingbutton=(Button) findViewById(R.id.instrument_molinkworkcenter_cancelbutton);
	   
		editscan=(EditText) findViewById(R.id.instrument_molinkworkcenter_editscan);
		editscan.setText("ʹ��˵����\n1������õ��Ե����õ���Դ�����Ƿ���ȷ���Ϸ�����ע�ᣡ\n2��ϵͳ���Զ�������Դ���Ƽ�⹤�����ģ����Ȼ�ȡ��ȷ�Ĺ������ģ��ٿ�ʼ�����빤�����İ󶨲�����");
		
        GetResourceId();
        
        moedit.setOnKeyListener(this);
        mospinner.setOnItemSelectedListener(this);
        okbutton.setOnClickListener(this);
        removebindingbutton.setOnClickListener(this);
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
				getworkcenter(resourceid);
			}
		break;
		
		case TaskType.instrumentgetworkcenter:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					workcenterid = getdata.get("WorkcenterId");
					String workcentername = getdata.get("WorkcenterName");
					workcenteredit.setText(workcentername);
					String scantext = "ͨ��������Դ����[" + resourcename + "]�ɹ��Ļ�øù�������:"+ workcentername+ "!";
					logSysDetails(scantext, "�ɹ�");
					moedit.requestFocus();

				} else {
					logSysDetails(
							"��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Ϣʧ�ܣ����飡", "�ɹ�");
			}

			break;
		
		
		
		case TaskType.instrumentgetmo:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				List<HashMap<String,String>> listmo = (List<HashMap<String,String>>)task.getTaskResult();
				SimpleAdapter adapter=new SimpleAdapter(this, listmo, R.layout.activity_instrument_moadapter, 
						new String[] { "MOName",
						"ProductName", "ProductDescription" }, new int[] {
						R.id.instrument_adapter_1, R.id.instrument_adapter_2, R.id.instrument_adapter_3 });
				mospinner.setAdapter(adapter);
			}
			break;
			
		case TaskType.instrumentmolinkworkcenter:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					clean();
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
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    
		    
		    
	}

	
	private  void  clean(){
		if(editscan.getLineCount()>10){
			editscan.setText("");
		}
	}
	
   private void getworkcenter(String resourceid){
	   super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
	   super.showProDia();
	   instrumentModel.getworkcenter(new  Task(this,TaskType.instrumentgetworkcenter,resourceid));
   }
   
   private   void  getmo(String  mo){
	   super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
	   super.showProDia();
	   instrumentModel.getmo(new  Task(this,TaskType.instrumentgetmo,mo));
   }
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.instrument_molinkworkcenter_moedit:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(workcenterid!=null&&!workcenterid.equals("")){
					String mochar=moedit.getText().toString().trim().toUpperCase();
					if(mochar.length()>1){
						getmo(mochar);
					}
				}
			}			
		break;

		
		}
	return  false;
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, String> map=(HashMap<String, String>) arg0.getItemAtPosition(arg2);
		moproductedit.setText(map.get("ProductName"));
		modescriedit.setText(map.get("ProductDescription"));
		mo=map.get("MOName");
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 AlertDialog.Builder builder=null;
		switch(v.getId()){
		case  R.id.instrument_molinkworkcenter_okbutton:
			     builder=new AlertDialog.Builder(this);
			     builder.setIcon(getResources().getDrawable(R.drawable.check));
			     builder.setTitle("�󶨣�");
			     builder.setMessage("    ȷ��Ҫ�Ѹù�����"+mo+"��Ͷ�����������ģ�"+workcenteredit.getText().toString() );
			     builder.setPositiveButton("ȷ��", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						    String[] paras=new String[7];
							paras[0]=resourceid;
							paras[1]=resourcename;
							paras[2]=useid;
							paras[3]=usename;
							paras[4]=workcenterid;
							paras[5]=mo;//moname
							paras[6]="1";//isbinding  ����
						progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					    showProDia();
					    instrumentModel.molinkworkcenter(new  Task(Instrument_molinkworkcenter.this,TaskType.instrumentmolinkworkcenter,paras));
					}
				});
			     builder.setNegativeButton("����", null);
			     builder.create();
			     builder.show();
			break;
       case  R.id.instrument_molinkworkcenter_cancelbutton:
    	   
    	     builder=new AlertDialog.Builder(this);
		     builder.setIcon(getResources().getDrawable(R.drawable.check));
		     builder.setTitle("����󶨣�");
		     builder.setMessage("     ȷ��Ҫ�ѹ�����"+mo+"���ӹ������ģ�"+workcenteredit.getText().toString() +"����󶨹�ϵ��");
		     builder.setPositiveButton("ȷ��", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					    String[] paras=new String[7];
						paras[0]=resourceid;
						paras[1]=resourcename;
						paras[2]=useid;
						paras[3]=usename;
						paras[4]=workcenterid;
						paras[5]=mo;//moname
						paras[6]="0";//isbinding  ����
					progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
				    showProDia();
				    instrumentModel.molinkworkcenter(new  Task(Instrument_molinkworkcenter.this,TaskType.instrumentmolinkworkcenter,paras));
				}
			});
		     builder.setNegativeButton("����", null);
		     builder.create();
		     builder.show();
			
			break;
		}
	}

	
}

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
import android.widget.Spinner;
import android.widget.TextView;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtIPQCModel;
import com.zowee.mes.model.TjchuhuoscanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class TJchuhuoscan extends CommonActivity implements
		OnKeyListener,OnItemSelectedListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	private  Spinner   spinner1;//����
	private  List<HashMap<String, String>>  dingdanlist;
	private ArrayAdapter<List<HashMap<String, String>>>   dingdanadapter;
	private   String  dingdan;
	private  Spinner   spinner2;//������
	private  List<HashMap<String, String>>  chuhuodanlist;
	private ArrayAdapter<List<HashMap<String, String>>>   chuhuodandapter;
	private   String  chuhuodan;
	private   String  chuhuodanid;
	
	
	private  EditText info_decri;
	private  EditText info_productname;
	private  EditText info_chuhuoliang;
	private  EditText info_zhuangxiangshuliang;
	private  EditText info_kehujixing;
	private  EditText info_daima;
	private  EditText info_chuhuodi;
	private  EditText info_dianrongliang;
	
  
   
   private  EditText  snedit;
   private  EditText  infobybox_count;
   private  EditText  infobybox_qtyinbox;
   
   private  EditText  editscan;
   
    private GetMOnameModel GetMonamemodel; // ��������
	private TjchuhuoscanModel tjchuhuoscanModel; // ��������
	private static final String TAG = "TJchuhuoscan";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tj_chuhuo);
		init();
	}

	protected void onResume() {
		super.onResume();
		if(dingdanlist==null)
		getdingdan();
		
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
								stopService(new Intent(TJchuhuoscan.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.tjchuhuoscan; // ��̨����̬���γ���
		//ɨ������Ҳ�Ǹ���̨����
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		tjchuhuoscanModel = new TjchuhuoscanModel();
	   
	   spinner1=(Spinner) findViewById(R.id.tj_chuhuoscan_spinner1);
	   
	   
	   spinner2=(Spinner) findViewById(R.id.tj_chuhuoscan_spinner2);
	   
	   info_decri=(EditText) findViewById(R.id.tj_chuhuoscan_info_descri);
	   info_productname=(EditText) findViewById(R.id.tj_chuhuoscan_info_productname);
	   info_chuhuoliang=(EditText) findViewById(R.id.tj_chuhuoscan_info_chuhuoliang);
	   info_zhuangxiangshuliang=(EditText) findViewById(R.id.tj_chuhuoscan_info_qtyforonebox);
	   info_kehujixing=(EditText) findViewById(R.id.tj_chuhuoscan_info_custormerjixing);
	   info_daima=(EditText) findViewById(R.id.tj_chuhuoscan_info_custormerdaima);
	   info_chuhuodi=(EditText) findViewById(R.id.tj_chuhuoscan_info_chuhuodi);
	   info_dianrongliang=(EditText) findViewById(R.id.tj_chuhuoscan_info_dianrongliang);
	   
	   snedit=(EditText) findViewById(R.id.tj_chuhuoscan_sn);
	   infobybox_count=(EditText) findViewById(R.id.tj_chuhuoscan_sumnumbybox);
	   infobybox_qtyinbox=(EditText) findViewById(R.id.tj_chuhuoscan_onenumbybox);
		  
	   
		editscan=(EditText) findViewById(R.id.tj_chuhuoscan_editscan);

		spinner1.setOnItemSelectedListener(this);
		spinner2.setOnItemSelectedListener(this);
		
		snedit.setOnKeyListener(this);
		
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
		HashMap<String, String>  getdata;
		
		
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
		case TaskType.tjchuhuoscan:
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
			}
		break;
		//����������Ϣ
		case TaskType.tjchuhuoscangetdingdan:
			super.closeProDia();
			dingdanlist= (List<HashMap<String,String>>)task.getTaskResult() ;
			if(dingdanlist.isEmpty()){
				logSysDetails( "��MES��ȡ������Ϣʧ�ܣ����飡", "�ɹ�");
			}else{
				ArrayList<String>  dingdans=new ArrayList<String>();
				for(HashMap<String, String> map :dingdanlist){
					String dingdan=map.get("������");
					dingdans.add(dingdan);
				}
				dingdanadapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, dingdans);
			    dingdanadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner1.setAdapter(dingdanadapter);
			}
			

			break;
			//ͨ����������ȡ����������Ϣ
		case TaskType.tjchuhuoscangetchuhuodan:
			super.closeProDia();
			chuhuodanlist= (List<HashMap<String,String>>)task.getTaskResult() ;
			if(chuhuodanlist.isEmpty()){
				logSysDetails( "��MES��ȡ��������Ϣʧ�ܣ����飡", "�ɹ�");
			}else{
				ArrayList<String>  chuhuodans=new ArrayList<String>();
				for(HashMap<String, String> map :chuhuodanlist){
					String chuhuodan=map.get("������");
					chuhuodans.add(chuhuodan);
				}
				chuhuodandapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, chuhuodans);
				chuhuodandapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner2.setAdapter(chuhuodandapter);
			}
			break;
			
		case TaskType.tjchuhuoscanchuhuoscan:
			super.closeProDia();
			String sn = snedit.getText().toString().toUpperCase().trim();
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
					snedit.requestFocus();
					snedit.setText("");
					if(getdata.containsKey("qty")){
						infobybox_qtyinbox.setText(getdata.get("qty"));
					}
					if(getdata.containsKey("total")){
						infobybox_count.setText(getdata.get("total"));
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

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(snedit.isFocused()){
		    	snedit.setText(scanRes);
		    	String[] paras=new String[6];
				paras[0]=resourceid;
				paras[1]=resourcename;
				paras[2]=useid;
				paras[3]=usename;
				paras[4]=chuhuodanid;
				paras[5]=snedit.getText().toString().trim();
				
				super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
				super.showProDia();
				tjchuhuoscanModel.chuhuoscan(new Task(this,TaskType.tjchuhuoscanchuhuoscan,paras));
				   
		    }
		    
		    
	}
		
   private   void  getdingdan(){
	    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
		super.showProDia();
		tjchuhuoscanModel.getdingdan(new  Task(this,TaskType.tjchuhuoscangetdingdan,""));
   }
  
   
   private   void  getchuhuodan(String  para){
	    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
		super.showProDia();
		tjchuhuoscanModel.getchuhuodan(new  Task(this,TaskType.tjchuhuoscangetchuhuodan,para));
  }
	
	private  void  clean(){
		if(editscan.getLineCount()>10){
			editscan.setText("");
		}
	}
	

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.tj_chuhuoscan_sn:
			String[] paras=new String[6];
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			paras[4]=chuhuodanid;
			paras[5]=snedit.getText().toString().trim();
			
			
			
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
					tjchuhuoscanModel.chuhuoscan(new Task(this,TaskType.tjchuhuoscanchuhuoscan,paras));
			   
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
		switch(arg0.getId()){
			case  R.id.tj_chuhuoscan_spinner1:
			//����ѡ��
				dingdan=spinner1.getSelectedItem().toString();
				getchuhuodan(dingdan);
			break;
			case  R.id.tj_chuhuoscan_spinner2:
				//����ѡ��
				chuhuodan=spinner2.getSelectedItem().toString();
				for(HashMap<String, String> map:chuhuodanlist){
					if(map.get("������").equals(chuhuodan)){
						chuhuodanid=map.get("powpoid");
						info_decri.setText(map.get("����"));
						info_productname.setText(map.get("�Ϻ�"));
						info_chuhuoliang.setText(map.get("������"));
						info_zhuangxiangshuliang.setText(map.get("������"));
						info_kehujixing.setText(map.get("�ͻ�����"));
						info_daima.setText(map.get("����"));
						info_chuhuodi.setText(map.get("������"));
						info_dianrongliang.setText(map.get("������"));
						break;
					}
				}
				break;
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}

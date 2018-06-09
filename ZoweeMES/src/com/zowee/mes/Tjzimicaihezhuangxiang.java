package com.zowee.mes;
import java.text.SimpleDateFormat;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.TjzimicaihezhaungxiangModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//������ײʺа����� �Զ��ͼ�
public class Tjzimicaihezhuangxiang extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener,OnItemSelectedListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	private  String  xianghaoid;
	private  String  songjiandan;
	
	private  List<HashMap<String,String>>  list;
	private  Spinner mospinner;
	private EditText editMO;
	//private  TextView  tvmo;//�����ȡ����
	private EditText editMOProduct;
	private  EditText  editmozhongxiangqty;
	private  EditText  editmooqcqty;
	private String moid; // ����ѡ�񱣴����

	private EditText editxianghaosn;
	private EditText editcaihesn;

	private Button weishuzhuangxiangbutton;
	private Button clearzhongxiangbutton;
	private Button shougongsongjiangbutton;
	

	private EditText editscan;

	

	private TjzimicaihezhaungxiangModel tjzimicaihezhaungxiangModel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Tjzimicaihezhuangxiang";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tianji_zimicaihezhuangxiang);
		init();
	}

	protected void onResume() {
		super.onResume();
		super.progressDialog.show();
		super.progressDialog.setMessage("��ʼ��MES��ȡ������Ϣ��������");
		tjzimicaihezhaungxiangModel.getmo(new Task(this,TaskType.tjzimicaihezhuangxianggetmo,""));
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
								stopService(new Intent(Tjzimicaihezhuangxiang.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
		super.init();

		tjzimicaihezhaungxiangModel = new TjzimicaihezhaungxiangModel();
		common4dmodel=new  Common4dModel();
		
		mospinner=(Spinner) findViewById(R.id.tjzimicaihezhuangxiang_mospinner);
		editMO = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_moedit);
		//tvmo=(TextView) findViewById(R.id.tjzimicaihezhuangxiang_motextview);
		editMOProduct = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_moliaohaoedit);
		editmozhongxiangqty = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_mozhuangxiangqtyedit);
		editmooqcqty= (EditText) findViewById(R.id.tjzimicaihezhuangxiang_mosongjianqtyedit);
		editMO.requestFocus();

		editxianghaosn = (EditText)findViewById(R.id.tjzimicaihezhuangxiang_xianghaoedit);
		editcaihesn = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_picihaoedit);
		
		editscan = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_editscan);
		editscan.setFocusable(false);
		
		weishuzhuangxiangbutton = (Button) findViewById(R.id.tjzimicaihezhuangxiang_weishubutton);
		weishuzhuangxiangbutton.setFocusable(false);
		clearzhongxiangbutton = (Button) findViewById(R.id.tjzimicaihezhuangxiang_qingkongxianghaobutton);
		clearzhongxiangbutton.setFocusable(false);
		shougongsongjiangbutton = (Button) findViewById(R.id.tjzimicaihezhuangxiang_shougongsongjianbutton);
		shougongsongjiangbutton.setFocusable(false);
		
		weishuzhuangxiangbutton.setOnClickListener(this);
		clearzhongxiangbutton.setOnClickListener(this);
		shougongsongjiangbutton.setOnClickListener(this);
		//tvmo.setOnClickListener(this);
		
		editMO.setOnKeyListener(this);
		editcaihesn.setOnKeyListener(this);
		
		
		mospinner.setOnItemSelectedListener(this);
		
       common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));

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
		// ��ȡ����ID
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "����������!��⵽��ƽ�����Դ����:[ " + resourcename
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!";
					logSysDetails(logText, "����");
				} else {
					logSysDetails(
							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ��������õ���Դ�����Ƿ���ȷ", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ", "�ɹ�");
			}

			break;
		
		
		case TaskType.tjzimicaihezhuangxianggetmo:
			super.closeProDia();
			super.closeProDia();
			if (null != task.getTaskResult()) {
				
				list = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG,"task�Ľ�������ǣ�"+list);
				SimpleAdapter  adapter=new  SimpleAdapter(this, list, R.layout.activity_tjzimicaihezhuangxiang_moadapter, new String[]{"moid","moname","product","boxqty","oqcqty"}, new int [] {R.id.tjzimicaihezhuangxiang_adapter_1,R.id.tjzimicaihezhuangxiang_adapter_2,R.id.tjzimicaihezhuangxiang_adapter_3,R.id.tjzimicaihezhuangxiang_adapter_4,R.id.tjzimicaihezhuangxiang_adapter_5});
				mospinner.setAdapter(adapter);
			}
		   else {
			  Toast.makeText(this, "MES ������Ϣ�����쳣,��ȷ�Ϲ�����ȷ", 5).show();	
			}
				
			 
			
			break;	
			
			
		case TaskType.tjzimicaihezhuangxiangzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>)task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				xianghaoid=getdata.get("BoxId");
				String iscomplete=getdata.get("iscomplete");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					editcaihesn.setText("");
					editcaihesn.requestFocus();
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						if(iscomplete.equals("ture")){
							editxianghaosn.setText("");
							editxianghaosn.requestFocus();
						}
						String scantext = "װ��ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception);
						String scantext = "װ��ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ������������߹������������������", "�ɹ�");
			}
			break;
			
			
		case TaskType.tjzimicaihezhuangxiangweishuzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					if(Integer.parseInt(getdata.get("I_ReturnValue"))==0){
						String scantext = "β��װ��ɹ� "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "�ɹ�");
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "β��װ��ʧ�� "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "�ɹ�");
					}
					

				} else {
					logSysDetails(
							 getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Ϣʧ�ܣ����飡", "�ɹ�");
			}

			break;
			
		case TaskType.tjzimicaihezhuangxiangqingkongzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					if(Integer.parseInt(getdata.get("I_ReturnValue"))==0){
						editxianghaosn.setText("");
						editxianghaosn.requestFocus();
						String scantext = "�����ųɹ� "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "�ɹ�");
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "������ʧ�� "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "�ɹ�");
					}
					

				} else {
					logSysDetails(
							 getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Ϣʧ�ܣ����飡", "�ɹ�");
			}

			break;	
			
		case TaskType.tjzimicaihezhuangxianggetoqcbyxianghao:
			super.closeProDia();
			String[]  params=new String[5];
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					  if(getdata.containsKey("SampleInspectionSN")){
						  songjiandan=getdata.get("SampleInspectionSN");
						  String scantext = "�ֹ��ͼ��ȡ�ͼ쵥�ɹ����ͼ쵥Ϊ "+songjiandan;
							logSysDetails(scantext, "�ɹ�");
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = songjiandan;
							
							tjzimicaihezhaungxiangModel.shougongsongjian(new Task(this,TaskType.tjzimicaihezhuangxiangshoudongshoujian,params));
					  }
					  else{
						  String scantext = "�ֹ��ͼ��ȡ�ͼ쵥ʧ�� ";
							logSysDetails(scantext, "�ɹ�");
					  }
				} else {
					logSysDetails(
							 getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Ϣʧ�ܣ����飡", "�ɹ�");
			}

			break;	
			
		case TaskType.tjzimicaihezhuangxiangshoudongshoujian:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(getdata.get("I_ReturnValue"))==0){
						String scantext = "�ֹ��ͼ�ɹ� "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "�ɹ�");
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "�ֶ��ͼ�ʧ�� "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "�ɹ�");
					}
					

				} else {
					logSysDetails(
							 getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Ϣʧ�ܣ����飡", "�ɹ�");
			}

			break;	
			
		}
	}

	@Override
	public void onClick(View v) {
		final String[]  params=new String[9];
		switch (v.getId()) {
		//�����������
//		case R.id.tjzimicaihezhuangxiang_motextview:
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("��������");
//			builder.setMessage("�Ƿ���Ҫ���¸���������");
//			builder.setPositiveButton("ȷ��",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							// TODO Auto-generated method stub
//							editMO.setText("");
//							editMO.setEnabled(true);
//							editMO.requestFocus();
//							editMOProduct.setText("");
//							editmooqcqty.setText("");
//							editmozhongxiangqty.setText("");
//							moid="";
//						}
//					});
//			builder.setNegativeButton("ȡ��", null);
//			builder.create().show();
//			break;
			//���β��װ�䰴ť
		case R.id.tjzimicaihezhuangxiang_weishubutton:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("β��װ��");
			builder2.setMessage("�Ƿ���Ҫβ��װ�䣿");
			builder2.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("β��װ����....");
							showProDia();
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = moid;
							params[5] = editmooqcqty.getText().toString().trim();
							params[6] = xianghaoid;
							tjzimicaihezhaungxiangModel.weishuzhuangxiang(new Task(Tjzimicaihezhuangxiang.this,TaskType.tjzimicaihezhuangxiangweishuzhuangxiang,params));
						}
					});
			builder2.setNegativeButton("ȡ��", null);
			builder2.create().show();
			break;
			//��������Ű�ť
		case R.id.tjzimicaihezhuangxiang_qingkongxianghaobutton:
			AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
			builder3.setTitle("������");
			builder3.setMessage("�Ƿ���Ҫ���װ�䣿");
			builder3.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("��������....");
							showProDia();
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = moid;
							params[5] = editmooqcqty.getText().toString().trim();
							params[6] = xianghaoid;
							tjzimicaihezhaungxiangModel.qingkongxianghao(new Task(Tjzimicaihezhuangxiang.this,TaskType.tjzimicaihezhuangxiangqingkongzhuangxiang,params));
						}
					});
			builder3.setNegativeButton("ȡ��", null);
			builder3.create().show();
			break;
			//�ֶ��ͼ�
		case R.id.tjzimicaihezhuangxiang_shougongsongjianbutton:
			AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
			builder4.setTitle("�ֶ��ͼ�");
			builder4.setMessage("�Ƿ���Ҫ�ֶ��ͼ죿");
			builder4.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("�ֶ��ͼ���....");
							showProDia();
							
							tjzimicaihezhaungxiangModel.getoqcdanhao(new Task(Tjzimicaihezhuangxiang.this,TaskType.tjzimicaihezhuangxianggetoqcbyxianghao,editxianghaosn.getText().toString().trim()));
						}
					});
			builder4.setNegativeButton("ȡ��", null);
			builder4.create().show();
			break;
		}
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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		
//		case R.id.tjzimicaihezhuangxiang_moedit:
//			if (KeyEvent.KEYCODE_ENTER == keycode
//					&& event.getAction() == KeyEvent.ACTION_DOWN) {
//				String param = editMO.getText().toString().toUpperCase().trim();
//			   if(param.length()<8){
//				   Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
//			   }
//			   else{
//				    super.progressDialog.setMessage("�������ݿ��ȡ����");
//					super.showProDia();
//					tjzimicaihezhaungxiangModel.getmo(new Task(this,TaskType.tjzimicaihezhuangxianggetmo,param));
//			   }
//			}
//		break;

		case R.id.tjzimicaihezhuangxiang_picihaoedit:
			String[]  params=new String[9];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(moid.equals("")||moid==null||editxianghaosn.getText().toString().trim().equals("")){
					Toast.makeText(this, "���Ȼ�ȡ�����������Ϣ", Toast.LENGTH_LONG).show();
				}
				else{
					if(editmooqcqty.getText().toString().equals("")||editmozhongxiangqty.getText().toString().equals("")){
						Toast.makeText(this, "��ȷ������ά����װ���������ͼ���������Ϣ��", Toast.LENGTH_LONG).show();
					}
					else{
						super.progressDialog.setMessage("�ʺ�װ����....");
						super.showProDia();
						params[0] = useid;
						params[1] = usename;
						params[2] = resourceid;
						params[3] = resourcename;
						params[4] = moid;
						params[5] = editmozhongxiangqty.getText().toString().trim();
						params[6] = editmooqcqty.getText().toString().trim();
						params[7] = editxianghaosn.getText().toString().trim();
						params[8] = editcaihesn.getText().toString().trim();
						
						tjzimicaihezhaungxiangModel.zhuangxiang(new  Task(this,TaskType.tjzimicaihezhuangxiangzhuangxiang,params));
					}
				}
				
				
				
			}

			break;
		}
		return false;
	}
	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("BoxSN")){
			editxianghaosn.requestFocus();
		}
		if(exception.equalsIgnoreCase("LotSN")){
			editcaihesn.requestFocus();
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	  

		switch (arg0.getId()) {
		case R.id.tjzimicaihezhuangxiang_mospinner:
			 HashMap<String,String> map= (HashMap<String, String>) arg0.getItemAtPosition(arg2);
			   moid=map.get("moid");
			   String  moname=map.get("moname");
			   String  productu=map.get("product");
			   String  boxqty=map.get("boxqty");
			   String  oqcqty=map.get("oqcqty");
		       editMO.setText(moname);  
		       editMOProduct.setText(productu);  
		       editmozhongxiangqty.setText(boxqty);  
		        editmooqcqty.setText(oqcqty);  
			break;

		
		}
	  
      
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
		

}

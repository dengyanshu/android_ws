package com.zowee.mes;
//�������װ�� ��  ����У��  �������У��  ������������У�� �ύУ��
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//�������װ�����������
public class Sntocar extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener,
		OnItemSelectedListener {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	private static String msg = null;

	private String flag;
	// ���ݿ���д���� int�͵ı�־��-װ����ʶ(0��ɨ��У�鳵�� 1��ɨ��У�������� 2��ɨ��У�鵥������ 3��ǿ��װ�� 4�����δ�رյĳ���)

	private EditText editMO;
	private EditText editMOProduct;
	private EditText editMOMaterial;
	private String moid; // ����ѡ�񱣴����

	private EditText editcar;
	private TextView cartextview;
	private String carsn;
	private EditText editsnnum;

	private EditText mainboardSN;
	private TextView mainsntextview;
	private String mainsn;
	private EditText editmainboardnum;
	private String makeupcount;
	private Spinner spinner;

	private EditText SN;
	private EditText snsum;
	private StringBuffer sb;
	private int num = 1;

	private Button okbutton;
	private Button clearbutton;

	private EditText editscan;

	

	private SntocarModel sntocarmodel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Sntocar";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sntocar);
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
								stopService(new Intent(Sntocar.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.sntocar; // ��̨����̬���γ���
		super.init();
		msg = "Tino";
		super.progressDialog.setMessage(msg);

		sntocarmodel = new SntocarModel();
		common4dmodel=new Common4dModel();
		
		editMO = (EditText) findViewById(R.id.sntocar_moname);
		editMOProduct = (EditText) findViewById(R.id.sntocar_moproduct);
		editMOMaterial = (EditText) findViewById(R.id.sntocar_momaterial);
		editMO.requestFocus();

		editcar = (EditText) findViewById(R.id.sntocar_carsn);
		cartextview = (TextView) findViewById(R.id.sntocar_cartextview);
		editsnnum = (EditText) findViewById(R.id.sntocar_snnum);

		mainboardSN = (EditText) findViewById(R.id.sntocar_mainsn);
		mainsntextview = (TextView) findViewById(R.id.sntocar_mainsntextview);
		editmainboardnum = (EditText) findViewById(R.id.sntocar_makecountnum);
		spinner = (Spinner) findViewById(R.id.sntocar_makecountnumspinner);
		String[] str = { "0", "1", "2", "3", "4", "5", "6", "7", "8" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, str);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setFocusable(false);

		SN = (EditText) findViewById(R.id.sntocar_sn);
		snsum = (EditText) findViewById(R.id.sntocar_zibansum);
		snsum.setEnabled(false);
		sb = new StringBuffer();

		okbutton = (Button) findViewById(R.id.sntocar_okbutton);
		okbutton.setFocusable(false);
		clearbutton = (Button) findViewById(R.id.sntocar_clearbutton);
		clearbutton.setFocusable(false);

		editscan = (EditText) findViewById(R.id.sntocar_scan);
		editscan.setFocusable(false);

		okbutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
		cartextview.setOnClickListener(this);
		mainsntextview.setOnClickListener(this);

		SN.setOnKeyListener(this);
		mainboardSN.setOnKeyListener(this);
		editcar.setOnKeyListener(this);
		editMO.setOnKeyListener(this);

		spinner.setOnItemSelectedListener(this);
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
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "����������!��⵽��ƽ�����Դ����:[ " + resourcename
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!����ɨ���������ȡ������Ϣ����ɨ�賵�Ž��г���У�飬����������Ż��߰�������������������򡰰�����롱!";
					logSysDetails(logText, "����");
				} else {
					logSysDetails(
							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ��������õ���Դ�����Ƿ���ȷ!", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ!", "�ɹ�");
			}

			break;
		// ��ȡ����ID
		case TaskType.sntocargetmo:
			String monamemainsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					String lot = SN.getText().toString();
					String moname = getdata.get("MOName");
					String moproduct = getdata.get("ProductDescription");
					String momaterial = getdata.get("ProductName");
					if (getdata.containsKey("MakeUpCount")) {

						makeupcount = getdata.get("MakeUpCount");
					}
					moid = getdata.get("MOId");

					editMO.setText(moname);
					editMOMaterial.setText(momaterial);
					editMOProduct.setText(moproduct);
					editmainboardnum.setText(makeupcount);
					editMO.setEnabled(false);
					String scantext = "ͨ�����SN��[" + monamemainsn + "]�ɹ��Ļ�ù���:"
							+ moname + ",��Ʒ��Ϣ:" + moproduct + ",��Ʒ�Ϻţ�"
							+ momaterial + "!";
					logSysDetails(scantext, "�ɹ�");
					editcar.requestFocus();
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				} else {
					logSysDetails(
							"ͨ��SN��[" + mainsn + "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails(mainsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;
			
		case TaskType.sntocargetmakeupcount:
			
			if (null != task.getTaskResult()) {
				
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					if(getdata.containsKey("MakeUpCount")){
						
						makeupcount = getdata.get("MakeUpCount");
					}
					editmainboardnum.setText(makeupcount);
					String scantext = "ͨ�����SN��[" + mainboardSN.getText().toString() + "]�ɹ��Ļ��������Ŀ:"+makeupcount+"!";
					logSysDetails(scantext, "�ɹ�");
					SN.requestFocus();
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				} else {
					logSysDetails(
							"ͨ��SN��[" + mainsn + "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails(mainsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;
		case TaskType.sntocargetcar:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");
						editcar.setFocusable(true);
						editcar.requestFocus();
						editcar.setText("");
					} else {
						String str = getdata.get("I_ReturnMessage");
						if (getdata.containsKey("InCarQty")) {
							String carsum = getdata.get("InCarQty");
							editsnnum.setText(carsum);
						}
						editcar.setEnabled(false);
						if(mainboardSN.getText().toString().equals("")){
							mainboardSN.requestFocus();
						}
						else{
							
							SN.requestFocus();
						}
						logSysDetails("����sn[ "
								+ editcar.getText().toString().toUpperCase()
										.trim() + "]��mesУ��ɹ���" + str, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
				}

				else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "��MES������Ϣ", 5).show();
			}

			closeProDia();
			break;
			
			
		case TaskType.sntocarchecksingelsn:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");
						SN.requestFocus();
						SN.setText("");
					} else {
						String str = getdata.get("I_ReturnMessage");
						
						
						SN.requestFocus();
						logSysDetails("�ɹ�ɨ�赽�Ӱ�SN[" + num + "]"
								+ SN.getText().toString().trim()+","+str, "�ɹ�");
						snsum.setText(num + "");
						sb.append(SN.getText().toString().trim() + ",");
						num++;
						SN.requestFocus();
						SN.setText("");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
				}

				else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "��MES������Ϣ", 5).show();
			}

			closeProDia();
			break;
			
			
			
		case TaskType.sntocar:
			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");

						num = 1;
						sb.delete(0, sb.length());
						SN.setText("");
						SN.requestFocus();

					} else {
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
						String str = getdata.get("I_ReturnMessage");
						if (getdata.containsKey("InCarQty")) {
							String qty = getdata.get("InCarQty");
							editsnnum.setText(qty);
						}

						logSysDetails("�ɹ�ɨ�裺" + str, "�ɹ�");
						num = 1;
						sb.delete(0, sb.length());
						SN.setText("");
						mainboardSN.setText("");
						mainboardSN.requestFocus();
						spinner.setSelection(0);
					}
				}

				else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "��MES������Ϣ", 5).show();
			}

			closeProDia();
			break;
			
			
		case TaskType.sntocartocar:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");
						editcar.setEnabled(true);
					} else {
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
						String str = getdata.get("I_ReturnMessage");
						editcar.setEnabled(true);
						editcar.setText("");
						editcar.requestFocus();
						logSysDetails("����sn[ "
								+ editcar.getText().toString().toUpperCase()
										.trim() + "]ǿ��װ���ɹ���" + str, "�ɹ�");
					}
				}

				else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "��MES������Ϣ", 5).show();
			}

			closeProDia();
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sntocar_clearbutton:
			editMO.setText("");
			editMO.requestFocus();
			editMO.setEnabled(true);
			editMOMaterial.setText("");
			editMOProduct.setText("");
			editcar.setText("");
			editcar.setEnabled(true);
			editsnnum.setText("");
			mainboardSN.setText("");
			editmainboardnum.setText("");
			spinner.setSelection(0);
			editscan.setText("");
			SN.setText("");
			spinner.setSelection(0);
			sb.delete(0, sb.length());
			snsum.setText("");
			num = 1;
			break;
		// ǿ��װ����ť
		case R.id.sntocar_okbutton:
			String[] params = new String[] {"", "", "", "", "", "","","","",""};
			params[0] = moid;
			params[1] = editcar.getText().toString().trim();
			params[2] = "";
			params[3] = editmainboardnum.getText().toString().trim()
					.toUpperCase();
			params[4] = "";
			params[5] = "3";
			params[6] = resourceid;
			params[7] = resourcename;
			params[8] = useid;
			params[9] = usename;
			Log.i(TAG, "���������ǣ�" + sb.toString() + ":::" + params[0] + ":::"
					+ params[2] + ":::" + params[3] + ":::" + params[5]);
			sntocarmodel.sntocar(new Task(this, TaskType.sntocartocar, params));
			break;

		case R.id.sntocar_cartextview:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��������");
			builder.setMessage("�Ƿ���Ҫ����У�鳵�ţ�");
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							editcar.setText("");
							editcar.setEnabled(true);
							editcar.requestFocus();
							editsnnum.setText("");
						}

					});
			builder.setNegativeButton("ȡ��", null);
			builder.create().show();
			break;
		case R.id.sntocar_mainsntextview:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("�����������");
			builder1.setMessage("�Ƿ���Ҫ���¸���������룿");
			builder1.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							mainboardSN.setText("");
							mainboardSN.requestFocus();
							editmainboardnum.setText("");

							spinner.setSelection(0);
							SN.setText("");
							snsum.setText("");
							spinner.setSelection(0);
							sb.delete(0, sb.length());
							num = 1;
						}

					});
			builder1.setNegativeButton("ȡ��", null);
			builder1.create().show();
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
		String[] params = new String[] { "", "", "", "", "", "","","","","" };
		switch (v.getId()) {
		case R.id.sntocar_sn:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editMO.getText().toString().trim().equals("")
						|| editcar.getText().toString().equals("")
						|| editmainboardnum.getText().toString().equals("")) {
					Toast.makeText(this, "���������š�������������������Ϊ��",
							Toast.LENGTH_SHORT).show();
				} else {

					if (SN.getText().toString().trim()
							.equalsIgnoreCase("+submit+")) {
						if (!(sb.toString().equals(""))) {
							params[0] = moid;
							params[1] = editcar.getText().toString().trim();
							params[2] = mainboardSN.getText().toString().trim()
									.toUpperCase();
							params[3] = editmainboardnum.getText().toString()
									.trim().toUpperCase();
							params[4] = sb.toString().substring(0,
									sb.toString().length() - 1);
							params[5] = "2";
							params[6] = resourceid;
							params[7] = resourcename;
							params[8] = useid;
							params[9] = usename;
							Log.i(TAG, "���������ǣ�" + sb.toString() + ":::"
									+ params[0] + ":::" + params[2] + ":::"
									+ params[3] + ":::" + params[5]);
							sntocarmodel.sntocar(new Task(this,
									TaskType.sntocar, params));
							SN.setText("");
							snsum.setText("");
						} else {
							SN.setText("");
							Toast.makeText(this, "����ɨ���Ӱ�����", Toast.LENGTH_SHORT)
									.show();
						}

					} else {
						if (!(sb.toString().contains(SN.getText().toString()
								.trim()))) {
							params[0] = moid;
							params[1] = editcar.getText().toString().trim();
							params[2] = mainboardSN.getText().toString().trim()
									.toUpperCase();
							params[3] = SN.getText().toString().trim().toUpperCase();
							sntocarmodel.checksinglesn(new Task(this, TaskType.sntocarchecksingelsn,params));
							//....................
							
							//...................
						} else {
							logSysDetails("SN:"
									+ SN.getText().toString().trim() + "�ظ�ɨ�裡",
									"�ɹ�");
							SN.setText("");
							SN.requestFocus();
						}

						
					}
				}
			}
			break;
		// ��ȡ������Ϣ
		case R.id.sntocar_moname:
			String paras = editMO.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (paras.length() < 6) {
					Toast.makeText(this, "���SN���Ȳ���ȷ����ȷ����ȷ��SN",
							Toast.LENGTH_SHORT).show();
				} else {
					sntocarmodel.getMo(new Task(this, TaskType.sntocargetmo,
							paras));
				}
			}
			break;
		//������ȡ�������� 5.29�޸� ����MES	
		case  R.id.sntocar_mainsn:
			String paras1 = mainboardSN.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (paras1.length() < 6) {
					Toast.makeText(this, "���SN���Ȳ���ȷ����ȷ����ȷ��SN",
							Toast.LENGTH_SHORT).show();
				} else {
					sntocarmodel.getMakeupcount(new Task(this, TaskType.sntocargetmakeupcount,
							paras1));
				}
			}
			
			break;

		case R.id.sntocar_carsn:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				carsn = editcar.getText().toString().toUpperCase().trim();
				params[0] = moid;
				params[1] = carsn;
				params[2] = "";
				params[3] = "0";//�����������ݿ���int��
				params[4] = "";
				params[5] = "0";
				params[6] = resourceid;
				params[7] = resourcename;
				params[8] = useid;
				params[9] = usename;
				// int �� У���־
				if (carsn.length() < 6 || moid == null) {
					Toast.makeText(this, "����ȷ�ϳ���SN�ĳ��ȵ���ȷ�����ҹ����Ĳ���Ϊ��",
							Toast.LENGTH_SHORT).show();

				} else {
					sntocarmodel.sntocar(new Task(this, TaskType.sntocargetcar,
							params));
				}
			}

			break;
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		String num = spinner.getSelectedItem().toString();
		if (!(num.equals("0"))) {
			editmainboardnum.setText(num);
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}

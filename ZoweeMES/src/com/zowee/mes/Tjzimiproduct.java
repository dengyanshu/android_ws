package com.zowee.mes;

import java.io.Serializable;
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
import android.text.AlteredCharSequence;
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
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.BindpcbModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.LineqtyModel;
import com.zowee.mes.model.LinesninputModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

@SuppressLint("ShowToast")
public class Tjzimiproduct extends CommonActivity implements OnClickListener,
		OnKeyListener, OnItemSelectedListener {

	private static String msg = null;

	private EditText editmo;
	private String moid;
	private  String moname;//ͨ��lotsn��ȡ�������ƣ�����id����Ʒ�� ����Ʒ���п���ΪNULL!5��22���޸�
	private  String  productma;//ͨ��������ȡ��Ʒ�룬5.14���޸ģ���

	private EditText linename;
	private Button linebutton;
	private String lineid;// �������ѡ��ĵ�2�����淵�ػ��ֵ�������� ����id!

	private EditText sn;

	private Button button;
	private EditText selcetedit1;
	private EditText selcetedit2;
	private Spinner spinner;
	private String selectflag;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private LinesninputModel linesninputmodel; // ��������
	private Common4dModel common4dModel;
	List<HashMap<String, String>> lineresult;// �����list���� ���洢����ΪHashmap
	private static final String TAG = "Tjzimiproduct";

	private static final int REQUESTCODE = 1;// �����¸�����������볣��

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tjzimi_product);
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
								stopService(new Intent(Tjzimiproduct.this,
										BackgroundService.class));
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.linesnstorage; // ��̨����̬���γ���
		super.init();
		msg = "tjzimiproduct";
		super.progressDialog.setMessage(msg);

		editmo = (EditText) findViewById(R.id.tjzimi_product_mo);

		linebutton = (Button) findViewById(R.id.tjzimi_product_linebutton);
		linename = (EditText) findViewById(R.id.tjzimi_product_linesn);
		linename.setEnabled(false);

		sn = (EditText) findViewById(R.id.tjzimi_product_sn);

		button = (Button) findViewById(R.id.tjzimi_product_selectbutton);
		selcetedit1 = (EditText) findViewById(R.id.tjzimi_product_selectedit1);
		selcetedit1.setEnabled(false);
		selcetedit2 = (EditText) findViewById(R.id.tjzimi_product_selectedit2);
		selcetedit2.setEnabled(false);
		spinner = (Spinner) findViewById(R.id.tjzimi_product_spinner);
		String[] str3 = new String[] { "��ѯ����Ͷ��", "��ѯ����ά���ڿ�" };
		// Ͷ�������ݿ� 0,1,2 @Flag INT��
		ArrayAdapter adapter3 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, str3);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter3);

		editscan = (EditText) findViewById(R.id.tjzimi_product_scan);
		editscan.setFocusable(false);

		String logText = "����������!��⵽��ƽ�����Դ����:[  " + I_ResourceName
				+ " ],�û�ID: [ " + userID + " ]!����ɨ�蹤����ȡ��Ʒ�룡";
		logSysDetails(logText, "��");

		button.setOnClickListener(this);
		linebutton.setOnClickListener(this);
		sn.setOnKeyListener(this);
		editmo.setOnKeyListener(this);
		spinner.setOnItemSelectedListener(this);

		linesninputmodel = new LinesninputModel();
		common4dModel = new Common4dModel();
		Task task = new Task(this, TaskType.getline, "getline");
		common4dModel.getLine(task);
	}

	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);

		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
		// ��ȡ����ID

		case TaskType.getline:

			if (null != task.getTaskResult()) {

				lineresult = (List<HashMap<String, String>>) task
						.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + lineresult);
			}
			break;
		case TaskType.linegetmo:

			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				if (getdata.containsKey("MOId")){
					
				

					moname=getdata.get("MOName");
					moid = getdata.get("MOId");
					if(getdata.containsKey("CustPNCode")){
						
						productma=getdata.get("CustPNCode");
					}
					editmo.setText(moname);
					logSysDetails("�ù����ɹ���MES��ѯ���ù�����idΪ" + moid+"����Ʒ��Ϊ"+productma,"�ɹ�");
					Log.d(TAG, "task�Ľ�������ǣ�" + moid);
					sn.requestFocus();
				} else {
					logSysDetails("��MES��������Ľ��ΪError����������Ĺ����Ƿ���ȷ", "�ɹ�");
					sn.requestFocus();
				}
			} else {
				logSysDetails("��MES��������Ľ��Ϊ�գ���������Ĺ����Ƿ���ȷ", "�ɹ�");
			}
			break;
		case TaskType.linesnstorage:
			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");
					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("�ɹ�ִ�У�" + str, "�ɹ�");
					}
					sn.setText("");
					spinner.clearFocus();
					sn.requestFocus();
				} else {
					logSysDetails("��MES��ȡ��Ϣʧ�ܻ���Ϊ�գ�����������Ϊ�գ������������Ϣ�Ƿ���ȷ", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("��MES��������Ľ��Ϊ�գ������������Ϣ�Ƿ���ȷ", "�ɹ�");
			}

			break;
		case TaskType.linesnselect:

			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					if (getdata.containsKey("InputCount")) {
						String linename = getdata.get("WorkcenterName");
						String qty = getdata.get("InputCount");
						selcetedit1.setText(linename);
						selcetedit2.setText(qty);
						logSysDetails("�ɹ���MESϵͳ��ѯ����Ϣ", "�ɹ�");
					} else if (getdata.containsKey("InRepairCount")) {
						String linename = getdata.get("WorkcenterName");
						String qty = getdata.get("InRepairCount");
						selcetedit1.setText(linename);
						selcetedit2.setText(qty);
						logSysDetails("�ɹ���MESϵͳ��ѯ����Ϣ", "�ɹ�");
					}
				} else {
					logSysDetails(getdata.get("Error"), "�ɹ�");
					selcetedit1.setText("");
					selcetedit2.setText("");
				}
				closeProDia();
			} else {
				logSysDetails("��MES��ȡ��Ϣʧ�ܣ�����ѡ��Ĳ�Ʒ���͡����塢��ѯ�����Ƿ���ȷ", "�ɹ�");
			}

			break;

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tjzimi_product_linebutton:

			if (lineresult == null) {
				Toast.makeText(this, "����������ݿ�õ�������Ϊ�գ���ȷ�����������������~��",
						Toast.LENGTH_LONG).show();
			} else {
				Log.d(TAG, "task�Ľ�������ǣ�" + lineresult);
				Intent intent = new Intent(this, Lineselect.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("bundle", (Serializable) lineresult);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE);
			}

			break;

		case R.id.tjzimi_product_selectbutton:
			String[] paras = new String[] { "", "", "", "" };
			paras[0] = moid;
			paras[1] = lineid;
			paras[2] = "PRODUCTSN";
			paras[3] = selectflag;
			linesninputmodel.selectSn(new Task(this, TaskType.linesnselect,
					paras));
			Log.i(TAG, "������ִ��paras==" + moid + "," + moid + "," + lineid
					+ ":::" + selectflag);
			break;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 2) {
			if (requestCode == REQUESTCODE) {
				lineid = data.getStringExtra("WorkcenterId");
				String WorkcenterName = data.getStringExtra("WorkcenterName");
				Log.i(TAG, "��һ�����淵�ص����ݣ���" + lineid + "::" + WorkcenterName);

				linename.setText(WorkcenterName);
				selcetedit1.setText(WorkcenterName);

			}

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

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.tjzimi_product_mo:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {

				linesninputmodel.getproductMo(new Task(this, TaskType.linegetmo,
						editmo.getText().toString().trim()));
				sn.requestFocus();
			}

			break;
		case R.id.tjzimi_product_sn:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editmo.getText().toString().equals("")
						|| linename.getText().toString().equals("")) {
					Toast.makeText(this, "���������岻��Ϊ��", Toast.LENGTH_SHORT)
							.show();
				}
				String[] params = new String[] { "", "", "", "", "", "" };
				params[0] = lineid;
				params[1] = userID;
				params[2] = moid;
				params[3] = sn.getText().toString().toUpperCase();
				params[4] = productma;

				linesninputmodel.storageproductSn(new Task(this,
						TaskType.linesnstorage, params));
				sn.requestFocus();
			}

			break;
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		int id = spinner.getId();
		switch (id) {
		case R.id.tjzimi_product_spinner:
			String str = spinner.getSelectedItem().toString();
			if (str.equals("��ѯ����Ͷ��")) {
				selectflag = "0";
			} else
				selectflag = "1";
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
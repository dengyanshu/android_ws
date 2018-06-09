//version1.0
package com.zowee.mes;
//����Ʒ���ߵ�ԴͶ��
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
public class LineSnInput extends CommonActivity implements OnClickListener,
		OnKeyListener, OnItemSelectedListener {

	private static String msg = null;

	private Spinner spinner1;
	// �������� ����Ʒ�͵�Դ
	private String spin1;
	private Spinner spinner2;
	private String spin2;

	private EditText linename;
	private Button linebutton;
	private String lineid;// �������ѡ��ĵ�2�����淵�ػ��ֵ�������� ����id!

	private EditText sn;

	private EditText selcetedit1;
	private EditText selcetedit2;
	private Spinner spinner3;
	private String spin3;
	private Button button;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private LinesninputModel linesninputmodel; // ��������
	private Common4dModel common4dModel;
	List<HashMap<String, String>> lineresult;// �����list���� ���洢����ΪHashmap
	private static final String TAG = "LineSnInput";

	private static final int REQUESTCODE = 1;// �����¸�����������볣��

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_linesninput);
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
				.setTitle("ȷ���˳��������SNͶ�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(LineSnInput.this,
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
		msg = "Linesninput";
		super.progressDialog.setMessage(msg);

		spinner1 = (Spinner) findViewById(R.id.linesninput_spinner1);
		String[] str1 = new String[] { "��Ʒ", "��Դ" };
		// �������� ����ǵ�ԴSN���̶�ΪPOWERSN ����ǲ�ƷSN���̶�ΪPRODUCTSN,@SNType NVARCHAR(50)=NUL
		ArrayAdapter adapter1 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, str1);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner2 = (Spinner) findViewById(R.id.linesninput_spinner2);
		String[] str2 = new String[] { "Ͷ��", "ά�����", "ά�޳���" };
		// Ͷ�������ݿ� 0,1,2 @Flag INT��
		ArrayAdapter adapter2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, str2);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);

		linebutton = (Button) findViewById(R.id.linesninput_linebutton);
		linename = (EditText) findViewById(R.id.linesninput_edit1);
		linename.setEnabled(false);

		sn = (EditText) findViewById(R.id.linesninput_edit2);

		selcetedit1 = (EditText) findViewById(R.id.linesninput_edit3);
		selcetedit1.setEnabled(false);
		selcetedit2 = (EditText) findViewById(R.id.linesninput_edit4);
		selcetedit2.setEnabled(false);
		spinner3 = (Spinner) findViewById(R.id.linesninput_spinner3);
		String[] str3 = new String[] { "��ѯ����Ͷ��", "��ѯ����ά���ڿ�" };
		// Ͷ�������ݿ� 0,1,2 @Flag INT��
		ArrayAdapter adapter3 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, str3);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner3.setAdapter(adapter3);

		button = (Button) findViewById(R.id.linesninput_button);

		editscan = (EditText) findViewById(R.id.linesninput_editscan);
		editscan.setFocusable(false);

		String logText = "����������!��⵽��ƽ�����Դ����:[  " + I_ResourceName
				+ " ],�û�ID: [ " + userID + " ]!";
		logSysDetails(logText, "��");

		button.setOnClickListener(this);
		linebutton.setOnClickListener(this);
		sn.setOnKeyListener(this);
		spinner1.setOnItemSelectedListener(this);
		spinner2.setOnItemSelectedListener(this);
		spinner3.setOnItemSelectedListener(this);

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
					spinner3.clearFocus();
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
		case R.id.linesninput_linebutton:

			Log.d(TAG, "task�Ľ�������ǣ�" + lineresult);
			Intent intent = new Intent(this, Lineselect.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("bundle", (Serializable) lineresult);
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUESTCODE);
			break;

		case R.id.linesninput_button:
			String[] paras = new String[] { "", "", "" };
			paras[0] = lineid;
			paras[1] = spin1;
			paras[2] = spin3;

			linesninputmodel.selectSn(new Task(this, TaskType.linesnselect,
					paras));
			Log.i(TAG, "������ִ��paras==" + lineid + ":::" + spin1 + ":::" + spin3);
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
		case R.id.linesninput_edit2:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (sn.getText().toString().trim().equals("")
						|| linename.getText().toString().equals("")) {
					Toast.makeText(this, "�������ƣ�����SN����Ϊ�գ���", Toast.LENGTH_SHORT)
							.show();
				} else {
					String[] paras = new String[] { "", "", "", "", "" };
					paras[0] = lineid;
					paras[1] = userID;
					paras[2] = sn.getText().toString().trim().toUpperCase();
					paras[3] = spin1;
					paras[4] = spin2;

					// ����id���û�id����Դidû����sn��sn���ͣ���������
					linesninputmodel.storageSn(new Task(this,
							TaskType.linesnstorage, paras));
					spinner3.clearFocus();
					sn.requestFocus();
				}
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
		case R.id.linesninput_spinner1:
			String str1 = spinner1.getSelectedItem().toString();
			Log.i(TAG, "spinner1ѡ����ǣ�" + str1);
			if (str1.equals("��Ʒ")) {
				spin1 = "PRODUCTSN";
			} else
				spin1 = "POWERSN";
			break;

		case R.id.linesninput_spinner2:
			String str2 = spinner2.getSelectedItem().toString();
			if (str2.equals("Ͷ��")) {
				spin2 = "0";
			} else if (str2.equals("ά�����")) {
				spin2 = "1";
			} else
				spin2 = "2";
			break;
		case R.id.linesninput_spinner3:
			String str3 = spinner3.getSelectedItem().toString();
			if (str3.equals("��ѯ����Ͷ��")) {
				spin3 = "0";
			} else
				spin3 = "1";
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}

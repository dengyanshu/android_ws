//version1.0
package com.zowee.mes;
//�����Ӱ�sn
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.BindpcbModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class BindPcb extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener,
		OnItemSelectedListener {

	private static String msg = null;

	private EditText editMO;
	private EditText editMOProduct;
	private EditText editMOMaterial;

	private EditText editmainboardSN;
	private Button buttonmainboard;
	private EditText editmainboardnum;
	private Spinner spinner;

	private EditText editsmallboardsn;

	private int num = 1;// ���������ı��
	StringBuffer sb = new StringBuffer();

	private Button okButton;
	private Button clearButton;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private BindpcbModel bindpcbmodel; // ��������
	private static final String TAG = "BindPcb";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bindpcb);
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
				.setTitle("ȷ���˳�SMT ��������󶨳���?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(BindPcb.this,
										BackgroundService.class));
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.Bindpcb; // ��̨����̬���γ���
		super.init();
		msg = "Tino";
		super.progressDialog.setMessage(msg);

		bindpcbmodel = new BindpcbModel();

		editMO = (EditText) findViewById(R.id.bindpcb_moname);
		editMOProduct = (EditText) findViewById(R.id.bindpcb_moproduct);
		editMOMaterial = (EditText) findViewById(R.id.bindpcb_momaterial);

		editmainboardSN = (EditText) findViewById(R.id.bindpcb_mubansn);
		editmainboardSN.requestFocus();
		buttonmainboard = (Button) findViewById(R.id.bindpcb_mubanbutton);
		buttonmainboard.setFocusable(false);
		editmainboardnum = (EditText) findViewById(R.id.bindpcb_mubannum);
		editmainboardnum.setFocusable(false);
		spinner = (Spinner) findViewById(R.id.bindpcb_mubanspinner);
		String[] str = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
				"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
				"30", "31", "32" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, str);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setFocusable(false);

		editsmallboardsn = (EditText) findViewById(R.id.bindpcb_zibansn);

		okButton = (Button) findViewById(R.id.bindpcb_okbutton);
		okButton.setFocusable(false);
		clearButton = (Button) findViewById(R.id.bindpcb_clearbutton);
		clearButton.setFocusable(false);

		editscan = (EditText) findViewById(R.id.tino_scan);
		editscan.setFocusable(false);
		String logText = "�����С���������������!��⵽��ƽ�����Դ����:[ " + I_ResourceName
				+ "],�û�ID: [" + userID + "]!";
		logSysDetails(logText, "��");

		// ����adapter

		okButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		buttonmainboard.setOnClickListener(this);

		editmainboardSN.setOnKeyListener(this);
		editsmallboardsn.setOnKeyListener(this);

		spinner.setOnItemSelectedListener(this);
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
		case TaskType.Bindpcbgetmo:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					String lot = editmainboardSN.getText().toString();
					String moname = getdata.get("MOName");
					String moproduct = getdata.get("ProductDescription");
					String momaterial = getdata.get("ProductName");
					String momainboardnum = getdata.get("MakeUpCount");
					editMO.setText(moname);
					editMOMaterial.setText(momaterial);
					editMOProduct.setText(moproduct);
					editmainboardnum.setText(momainboardnum);
					String scantext = "ͨ��SN��[" + lot + "]�ɹ��Ļ�ù���:" + moname
							+ ",��Ʒ��Ϣ:" + moproduct + ",��Ʒ�Ϻţ�" + momaterial
							+ ",��������:" + momainboardnum + "!";
					logSysDetails(scantext, "�ɹ�");
					editMO.setEnabled(false);

					editsmallboardsn.requestFocus();

				} else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣,��ȷ�Ϲ�����ȷ", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(editmainboardSN.getText().toString()
						+ "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;

		case TaskType.Bindpcb:

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
						editmainboardSN.setText("");
						editmainboardSN.requestFocus();
						spinner.setSelection(0);

					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("�ɹ�ɨ�裺" + str, "�ɹ�");
						num = 1;
						sb.delete(0, sb.length());
						editmainboardSN.setText("");
						editmainboardSN.requestFocus();
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

		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.bindpcb_clearbutton:
			editmainboardSN.setEnabled(true);
			editmainboardSN.setText("");
			editmainboardnum.setText("");
			editscan.setText("");
			editsmallboardsn.setText("");
			editscan.setText("");
			editMO.setText("");
			editMOMaterial.setText("");
			editMOProduct.setText("");

			break;
		// ���tv���»�ȡ����
		case R.id.bindpcb_mubanbutton:
			editmainboardSN.setEnabled(true);
			editmainboardSN.setText("");
			editmainboardnum.setText("");

			break;
		// ��ɨ��С��ʱ����Ҫɾ����ʱ�򣡣�ִ�в�������
		case R.id.bindpcb_okbutton:

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

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.bindpcb_mubansn:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editmainboardSN.getText().toString().trim().length() < 6) {
					Toast.makeText(this, "����SN���Ȳ���ȷ����ȷ����ȷ��SN",
							Toast.LENGTH_SHORT).show();
				} else {
					bindpcbmodel.bindpcbgetmo(new Task(this,
							TaskType.Bindpcbgetmo, editmainboardSN.getText()
									.toString().trim()));

				}
			}

			break;
		case R.id.bindpcb_zibansn:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String[] params = new String[] { "", "", "", "" };
				if (editMO.getText().toString().equals("")
						|| editmainboardSN.getText().toString().equals("")
						|| editmainboardnum.getText().toString().equals("")) {
					Toast.makeText(this, "����������SN��������������Ϊ�գ����飡",
							Toast.LENGTH_SHORT).show();
				} else {

					int m = Integer.parseInt(editmainboardnum.getText()
							.toString().trim());

					if (num == m) {
						if (!(sb.toString().contains(editsmallboardsn.getText()
								.toString().trim()))) {
							// editsmallboardscan.append("ɨ�赽�Ӱ�["+num+"]:"
							// +editsmallboardsn.getText().toString().trim());
							logSysDetails("�ɹ�ɨ�赽�Ӱ�["
									+ num
									+ "]SN:"
									+ editsmallboardsn.getText().toString()
											.trim(), "�ɹ�");
							sb.append(editsmallboardsn.getText().toString()
									.trim());
							editsmallboardsn.setText("");
							params[0] = editMO.getText().toString().trim();
							params[1] = editmainboardSN.getText().toString()
									.trim();
							params[2] = editmainboardnum.getText().toString()
									.trim();
							params[3] = sb.toString();
							Log.i(TAG, "���������ǣ�" + sb.toString());
							bindpcbmodel.bindpcb(new Task(this,
									TaskType.Bindpcb, params));
						} else {
							logSysDetails("SN:"
									+ editsmallboardsn.getText().toString()
											.trim() + "�ظ�", "�ɹ�");
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
							editsmallboardsn.setText("");
						}

						// ����num��ֵ��
						// ��������

					} else {
						if (!(sb.toString().contains(editsmallboardsn.getText()
								.toString().trim()))) {
							// editsmallboardscan.append("ɨ�赽�Ӱ�["+num+"]:"
							// +editsmallboardsn.getText().toString().trim()+"\n");
							logSysDetails("�ɹ�ɨ�赽�Ӱ�["
									+ num
									+ "]SN:"
									+ editsmallboardsn.getText().toString()
											.trim(), "�ɹ�");
							sb.append(editsmallboardsn.getText().toString()
									.trim()
									+ ",");
							num++;
						} else {
							logSysDetails("SN:"
									+ editsmallboardsn.getText().toString()
											.trim() + "�ظ�", "�ɹ�");
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						}
						editsmallboardsn.setText("");
						editsmallboardsn.requestFocus();
					}
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

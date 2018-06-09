package com.zowee.mes;
//����  �洢��������д�ģ�
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
import android.text.method.KeyListener;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.TinoModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class Tino extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener {

	private static String msg = null;

	private EditText editMO;
	private EditText editMOProduct;
	private EditText editMOMaterial;
	private TextView tvmo;

	private EditText editTinoSN;
	private EditText editTinojinchang;
	private EditText editTinoyouxiao;
	private EditText editTinodaima;
	private EditText editTinoname;
	private EditText editTinotype;

	private Button okButton;
	private Button clearButton;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private TinoModel tinomodel; // ��������
	private static final String TAG = "Tino";

	private ListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tino);
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
				.setTitle("ȷ���˳�����ɨ�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(Tino.this,
										BackgroundService.class));
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FaceCheck; // ��̨����̬���γ���
		super.init();
		msg = "Tino";
		super.progressDialog.setMessage(msg);

		tinomodel = new TinoModel();

		editMO = (EditText) findViewById(R.id.tino_moname);
		editMOProduct = (EditText) findViewById(R.id.tino_moproduct);
		editMOMaterial = (EditText) findViewById(R.id.tino_momaterial);
		tvmo = (TextView) findViewById(R.id.tino_tvmo);

		editTinoSN = (EditText) findViewById(R.id.tino_tinosn);
		editTinojinchang = (EditText) findViewById(R.id.tino_tinojinchang);
		editTinoyouxiao = (EditText) findViewById(R.id.tino_tinoyouxiaoqi);
		editTinodaima = (EditText) findViewById(R.id.tino_tinodaima);
		editTinoname = (EditText) findViewById(R.id.tino_timoname);
		editTinotype = (EditText) findViewById(R.id.tino_tinotype);

		okButton = (Button) findViewById(R.id.tino_okbutton);
		clearButton = (Button) findViewById(R.id.tino_clearbutton);

		editscan = (EditText) findViewById(R.id.tino_scan);
		String logText = "����ɨ����ӳ���������!��⵽��ƽ�����Դ����:[ " + I_ResourceName
				+ "],�û�ID: [" + userID + "]!";
		logSysDetails(logText, "����");

		// ����adapter

		tvmo.setOnClickListener(this);
		okButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);

		editMO.setOnKeyListener(this);
		editTinoSN.setOnKeyListener(this);

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
		case TaskType.Tinogetmo:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					String lot = editMO.getText().toString();
					String moname = getdata.get("MOName");
					String moproduct = getdata.get("ProductDescription");
					String momaterial = getdata.get("ProdName");
					editMO.setText(moname);
					editMOMaterial.setText(momaterial);
					editMOProduct.setText(moproduct);
					String scantext = "ͨ��lot��[" + lot + "]�ɹ��Ļ�ù���:" + moname
							+ ",��Ʒ��Ϣ:" + moproduct + ",��Ʒ�Ϻţ�" + momaterial
							+ "!";
					logSysDetails(scantext, "�ɹ�");
					editMO.setEnabled(false);
					editTinoSN.requestFocus();

				} else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣,��ȷ�Ϲ�����ȷ", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(editMO.getText().toString()
						+ "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;
		case TaskType.Tinogettino:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();

				if (getdata.get("Error") == null) {

					editTinojinchang.setText(getdata.get("FactoryTime"));
					editTinoyouxiao.setText(getdata.get("EffectDate"));
					editTinodaima.setText(getdata.get("TinolCode"));
					editTinoname.setText(getdata.get("TinolName"));
					editTinotype.setText(getdata.get("TinolType"));

					String scantext = "ͨ������SN:"
							+ editTinoSN.getText().toString().trim()
							+ "��mes�����Ϣ�ɹ�!";
					logSysDetails(scantext, "�ɹ�");
					editTinoSN.setEnabled(false);

				} else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣,��ȷ�Ϲ�����ȷ", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(editTinoSN.getText().toString()
						+ "��MES��ȡ��Ϣʧ�ܣ���ȷ������SN����ȷ�ԣ�", "�ɹ�");
			}

			break;

		case TaskType.Tino:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");

					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("�ɹ�ɨ�裺" + str, "�ɹ�");
						// this.editScan.append( "Ŀ���վ��\r\n");
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

		case R.id.tino_clearbutton:
			editMO.setEnabled(true);
			editscan.setText("");
			editTinoSN.setText("");
			editTinodaima.setText("");
			editTinojinchang.setText("");
			editTinoname.setText("");
			editTinotype.setText("");
			editTinoyouxiao.setText("");
			editMO.setText("");
			editMOMaterial.setText("");
			editMOProduct.setText("");

			break;
		// ���tv���»�ȡ����
		case R.id.tino_tvmo:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("ȷ�����»�ȡ����?");
			builder.setPositiveButton(getString(android.R.string.yes),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							editMO.setEnabled(true);
							editMO.setText("");
							editMOMaterial.setText("");
							editMOProduct.setText("");
							editMO.requestFocus();
						}
					});
			builder.setNegativeButton(getString(android.R.string.no), null);
			builder.create().show();
			break;

		case R.id.tino_okbutton:

			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setIcon(android.R.drawable.ic_dialog_alert);
			builder1.setTitle("ȷ��������ࣿ");
			builder1.setMessage("�ȱ�֤����������SN����Ϊ�գ����ȷ�����������ӣ�");
			builder1.setPositiveButton(getString(android.R.string.yes),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String[] params = new String[] { "", "", "", "" };
							params[0] = editTinoSN.getText().toString().trim();
							params[1] = editMO.getText().toString().trim();
							params[2] = I_ResourceName;
							params[3] = userID;
							tinomodel.tino(new Task(Tino.this, TaskType.Tino,
									params));
						}
					});
			builder1.setNegativeButton(getString(android.R.string.no), null);
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

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.tino_moname:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editMO.getText().toString().trim().length() < 7) {
					Toast.makeText(this, "�������Ȳ���ȷ����ȷ����ȷ�Ĺ�����",
							Toast.LENGTH_SHORT).show();
				} else {
					tinomodel.tinogetmo(new Task(this, TaskType.Tinogetmo,
							editMO.getText().toString().trim()));

				}
			}

			break;

		case R.id.tino_tinosn:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editTinoSN.getText().toString().trim().equals("")) {
					Toast.makeText(this, "����SN����Ϊ��!", Toast.LENGTH_SHORT)
							.show();
				}

				tinomodel.tinogettino(new Task(this, TaskType.Tinogettino,
						editTinoSN.getText().toString().trim().toUpperCase()));

			}

			break;
		}
		return false;
	}

}


package com.zowee.mes;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class SmtFvmi extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener {

	private static String msg = null;

	private EditText editMO;
	private String moid; // ��¼����
	private EditText editMOProduct;
	private EditText editMOMaterial;

	private EditText tverrorcode;// ����������ʾ
	private Button clearerrorcodebutton;
	private String presnid = "";
	private String preflag = "1";// 0���������� 1����SN
	private String buliangString;
	
	private EditText SN;

	private Button clearbutton;

	private ImageView imageview;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private SmtFvmiModel smtfvmimodel; // ��������
	private static final String TAG = "SmtFvmi";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtfvmi);
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
				.setTitle("ȷ���˳��ó���?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(SmtFvmi.this,
										BackgroundService.class));
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtfvmi; // ��̨����̬���γ���
		super.init();
		msg = "Tino";
		super.progressDialog.setMessage(msg);

		smtfvmimodel = new SmtFvmiModel();

		editMO = (EditText) findViewById(R.id.smtfvmi_moname);
		editMOProduct = (EditText) findViewById(R.id.smtfvmi_moproduct);
		editMOMaterial = (EditText) findViewById(R.id.smtfvmi_moproductddescre);

		tverrorcode = (EditText) findViewById(R.id.smtfvmi_buliangtext);
		clearerrorcodebutton = (Button) findViewById(R.id.smtfvmi_clearerrorcodebutton);
		tverrorcode.setText(presnid);

		SN = (EditText) findViewById(R.id.smtfvmi_sn);

		clearbutton = (Button) findViewById(R.id.smtfvmi_button);

		imageview = (ImageView) findViewById(R.id.smtfvmi_imageview);

		editscan = (EditText) findViewById(R.id.smtfvmi_sacnedit);

		String logText = "����������!��⵽��ƽ�����Դ����:[ " + I_ResourceName
				+ " ],�û�ID: [ " + userID + " ]!";
		logSysDetails(logText, "����");

		clearbutton.setOnClickListener(this);
		clearerrorcodebutton.setOnClickListener(this);
		SN.setOnKeyListener(this);
		editMO.setOnKeyListener(this);
		
		editMO.requestFocus(); //2015-3-16 chenyun ���
	}

	/*
	 * ˢ��UI����
	 */
	@SuppressLint("ResourceAsColor")
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
		case TaskType.smtfvmigetmo:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					String lot = SN.getText().toString();
					String moname = getdata.get("MOName");
					String moproduct = getdata.get("ProductDescription");
					String momaterial = getdata.get("ProductName");
					moid = getdata.get("MOId");

					editMO.setText(moname);
					editMOMaterial.setText(momaterial);
					editMOProduct.setText(moproduct);
					String scantext = "ͨ��SN��[" + lot + "]�ɹ��Ļ�ù���:" + moname
							+ ",��Ʒ��Ϣ:" + moproduct + ",��Ʒ�Ϻţ�" + momaterial
							+ "!";
					logSysDetails(scantext, "�ɹ�");
					editMO.setEnabled(false);

					SN.requestFocus();

				} else {
					logSysDetails("ͨ��SN��[" + editMO.getText().toString()
							+ "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails(editMO.getText().toString()
						+ "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;

		case TaskType.smtfvmi:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task�Ľ�������ǣ�"+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "�ɹ�");
						imageview.setBackgroundColor(R.color.red);
						imageview.setImageResource(R.drawable.fail);

					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("�ɹ�ִ�У�" + str, "�ɹ�");
						if (getdata.containsKey("SNId")) {
							presnid = getdata.get("SNId");
							if (presnid != null) {
								//tverrorcode.setText(presnid);
								tverrorcode.setText("");//2015-3-16 chenyun�޸�
							}
						}
						if (getdata.containsKey("SNFlag")) {
							preflag = getdata.get("SNFlag");
							if(preflag.equals("0"))
							{
								tverrorcode.setText(buliangString);
								buliangString = "";
							}
						}

						imageview.setBackgroundColor(R.color.green);
						imageview.setImageResource(R.drawable.sucess);

					}
					clearbutton.clearFocus();
					SN.setText("");
					SN.requestFocus();
				}

				else {
					logSysDetails("ͨ��SN��[" + SN.getText().toString()
							+ "]�ύ��MES����ȡ����ϢΪ�ջ��߽������Ϊ�գ�����SN!", "�ɹ�");
				}
				closeProDia();
			} else {
				Toast.makeText(this, "��MES������Ϣ", 5).show();
			}

			closeProDia();
			break;

		}

	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.smtfvmi_button:
			editMO.setEnabled(true);
			editMO.setText("");
			editMOProduct.setText("");
			editMOMaterial.setText("");
			tverrorcode.setText("");
			SN.setText("");
			imageview.setBackgroundColor(R.color.antiquewhite);
			imageview.setImageResource(R.drawable.wenhao);
			editscan.setText("");
			presnid = "";
			preflag = "1";// 0���������� 1����SN ���ݿ���int����

			break;
		case R.id.smtfvmi_clearerrorcodebutton:
			presnid = "";
			preflag = "1";
			tverrorcode.setText("");
			// 0���������� 1����SN ���ݿ���int����
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
		case R.id.smtfvmi_moname:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editMO.getText().toString().trim().length() < 6) {
					Toast.makeText(this, "SN���Ȳ���ȷ����ȷ����ȷ��SN", Toast.LENGTH_SHORT)
							.show();
				} else {
					smtfvmimodel.getMo(new Task(this, TaskType.smtfvmigetmo,
							editMO.getText().toString().trim()));
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
				}
			}

			break;
		case R.id.smtfvmi_sn:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String[] params = new String[] { "", "", "", "",""};
				if (editMO.getText().toString().equals("")) {
					Toast.makeText(this, "��������Ϊ�գ����飡", Toast.LENGTH_SHORT)
							.show();
				} else {
					params[0] = moid;
					params[1] = SN.getText().toString().trim();
					params[2] = presnid;
					params[3] = preflag;
					params[4] = userID;
					buliangString = params[1];
					Log.i(TAG, "���������ǣ�" + params[0] + "::" + params[1] + "::"
							+ params[2] + "::" + params[3] + "::");
					smtfvmimodel.smtFvmi(new Task(this, TaskType.smtfvmi,
							params));
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
				}

			}

			break;
		}
		return false;
	}

}

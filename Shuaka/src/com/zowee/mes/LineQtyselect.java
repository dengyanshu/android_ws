//version1.0
package com.zowee.mes;
//������ײ�ѯ
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
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.LineqtyModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

@SuppressLint("ShowToast")
public class LineQtyselect extends CommonActivity implements OnClickListener {

	private static String msg = null;

	private Button lineselectbutton;
	private EditText lineedit;
	private String lineid;

	private EditText inputnumedit;
	private Button okbutton;

	private Button selectbutton;

	private EditText selcetedit1;
	private EditText selcetedit2;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private LineqtyModel lineqtymodel; // ��������
	private Common4dModel common4dModel;
	List<HashMap<String, String>> lineresult;
	private static final String TAG = "LineQtyselect";

	private static final int REQUESTCODE = 1;// �����¸�����������볣��

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lineqty);
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
				.setTitle("ȷ���˳�����Ͷ��������ѯ����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(LineQtyselect.this,
										BackgroundService.class));
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.Linestorage; // ��̨����̬���γ���
		super.init();
		msg = "Lineinput";
		super.progressDialog.setMessage(msg);

		lineselectbutton = (Button) findViewById(R.id.line_linebutton);
		lineedit = (EditText) findViewById(R.id.line_lineedittext);
		lineedit.setEnabled(false);

		inputnumedit = (EditText) findViewById(R.id.line_inputnumedittext);
		okbutton = (Button) findViewById(R.id.line_okbutton);

		selectbutton = (Button) findViewById(R.id.line_selectbutton);

		selcetedit1 = (EditText) findViewById(R.id.line_selectedittext1);
		selcetedit1.setEnabled(false);
		selcetedit2 = (EditText) findViewById(R.id.line_selectedittext2);
		selcetedit2.setEnabled(false);

		editscan = (EditText) findViewById(R.id.line_scanedittext);
		editscan.setFocusable(false);

		String logText = "����Ͷ�����������!��⵽��ƽ�����Դ����:[  " + I_ResourceName
				+ " ],�û�ID: [ " + userID + " ]!";
		logSysDetails(logText, "��");

		okbutton.setOnClickListener(this);
		selectbutton.setOnClickListener(this);
		lineselectbutton.setOnClickListener(this);

		lineqtymodel = new LineqtyModel();
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
		case TaskType.Linestorage:

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

				} else {
					editscan.setText(lineedit.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES ������Ϣ�����쳣,��ȷ��������ȷ", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(lineedit.getText().toString()
						+ "��MES��ȡ��Ϣʧ�ܣ�������������������Ƿ���ȷ", "�ɹ�");
			}

			break;

		case TaskType.Lineselectnum:

			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					String linename = getdata.get("WorkcenterName");
					String qty = getdata.get("Qty");
					selcetedit1.setText(linename);
					selcetedit2.setText(qty);
					logSysDetails("�ɹ���MESϵͳ��ѯ����Ϣ", "�ɹ�");

				} else {
					logSysDetails(getdata.get("Error"), "�ɹ�");
					selcetedit1.setText("");
					selcetedit2.setText("");

				}
				closeProDia();
			} else {
				logSysDetails(lineedit.getText().toString()
						+ "��MES��ȡ��Ϣʧ�ܣ�������������������Ƿ���ȷ", "�ɹ�");
			}

			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.line_linebutton:

			Log.d(TAG, "task�Ľ�������ǣ�" + lineresult);
			Intent intent = new Intent(this, Lineselect.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("bundle", (Serializable) lineresult);
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUESTCODE);
			break;

		case R.id.line_okbutton:
			final String[] paras = new String[] { "", "", "" };
			if (lineedit.getText().toString().equals("")
					|| inputnumedit.getText().toString().equals("")) {
				Toast.makeText(this, "Ͷ�����������岻��Ϊ��", Toast.LENGTH_SHORT).show();

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(R.drawable.area);
				builder.setTitle("��ȷ����Ϣ");
				builder.setMessage("����" + lineedit.getText().toString()
						+ "��ҪͶ������" + inputnumedit.getText().toString() + "!!");
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub

								paras[0] = lineid;
								paras[1] = inputnumedit.getText().toString();
								paras[2] = userID;
								lineqtymodel.storage(new Task(
										LineQtyselect.this,
										TaskType.Linestorage, paras));
								inputnumedit.setText("");
							}
						});
				builder.setNegativeButton("ȡ��", null);
				builder.create().show();
			}
			break;

		case R.id.line_selectbutton:
			String para = lineid;
			if (lineedit.getText().toString().equals("")) {
				Toast.makeText(this, "���岻��Ϊ��", Toast.LENGTH_SHORT).show();

			} else {
				lineqtymodel.select(new Task(this, TaskType.Lineselectnum,
						lineid));
			}
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

				lineedit.setText(WorkcenterName);

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
}

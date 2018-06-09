package com.zowee.mes;
//锡膏  存储过程张龙写的！
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

	private TinoModel tinomodel; // 任务处理类
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

	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出锡膏扫描程序?")
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
		super.TASKTYPE = TaskType.FaceCheck; // 后台服务静态整形常量
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
		String logText = "锡膏扫描添加程序已启动!检测到该平板的资源名称:[ " + I_ResourceName
				+ "],用户ID: [" + userID + "]!";
		logSysDetails(logText, "锡膏");

		// 工单adapter

		tvmo.setOnClickListener(this);
		okButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);

		editMO.setOnKeyListener(this);
		editTinoSN.setOnKeyListener(this);

	}

	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID
		case TaskType.Tinogetmo:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					String lot = editMO.getText().toString();
					String moname = getdata.get("MOName");
					String moproduct = getdata.get("ProductDescription");
					String momaterial = getdata.get("ProdName");
					editMO.setText(moname);
					editMOMaterial.setText(momaterial);
					editMOProduct.setText(moproduct);
					String scantext = "通过lot：[" + lot + "]成功的获得工单:" + moname
							+ ",产品信息:" + moproduct + ",产品料号：" + momaterial
							+ "!";
					logSysDetails(scantext, "成功");
					editMO.setEnabled(false);
					editTinoSN.requestFocus();

				} else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES 返回信息发生异常,请确认工单正确", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(editMO.getText().toString()
						+ "在MES获取工单信息失败，请检查输入的条码", "成功");
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

					String scantext = "通过锡膏SN:"
							+ editTinoSN.getText().toString().trim()
							+ "在mes获得信息成功!";
					logSysDetails(scantext, "成功");
					editTinoSN.setEnabled(false);

				} else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES 返回信息发生异常,请确认工单正确", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(editTinoSN.getText().toString()
						+ "在MES获取信息失败，请确认锡膏SN的正确性！", "成功");
			}

			break;

		case TaskType.Tino:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task的结果数据是："+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "成功");

					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("成功扫描：" + str, "成功");
						// this.editScan.append( "目检过站！\r\n");
					}
				}

				else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES 返回信息发生异常", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "无MES返回信息", 5).show();
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
		// 点击tv重新获取工单
		case R.id.tino_tvmo:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("确定重新获取工单?");
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
			builder1.setTitle("确定添加锡膏？");
			builder1.setMessage("先保证工单、锡膏SN不能为空！点击确定完成锡膏添加！");
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
					Toast.makeText(this, "工单长度不正确，请确认正确的工单号",
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
					Toast.makeText(this, "锡膏SN不能为空!", Toast.LENGTH_SHORT)
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

//version1.0
package com.zowee.mes;
//大板绑定子板sn
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

	private int num = 1;// 联板数量的标记
	StringBuffer sb = new StringBuffer();

	private Button okButton;
	private Button clearButton;

	private EditText editscan;

	private String userID = MyApplication.getMseUser().getUserId();
	private String I_ResourceName = MyApplication.getAppOwner().toString()
			.trim();

	private BindpcbModel bindpcbmodel; // 任务处理类
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
     
	
	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出SMT 联板条码绑定程序?")
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
		super.TASKTYPE = TaskType.Bindpcb; // 后台服务静态整形常量
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
		String logText = "联板绑定小板条码程序已启动!检测到该平板的资源名称:[ " + I_ResourceName
				+ "],用户ID: [" + userID + "]!";
		logSysDetails(logText, "联");

		// 工单adapter

		okButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		buttonmainboard.setOnClickListener(this);

		editmainboardSN.setOnKeyListener(this);
		editsmallboardsn.setOnKeyListener(this);

		spinner.setOnItemSelectedListener(this);
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
		case TaskType.Bindpcbgetmo:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
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
					String scantext = "通过SN：[" + lot + "]成功的获得工单:" + moname
							+ ",产品信息:" + moproduct + ",产品料号：" + momaterial
							+ ",联板数量:" + momainboardnum + "!";
					logSysDetails(scantext, "成功");
					editMO.setEnabled(false);

					editsmallboardsn.requestFocus();

				} else {
					editscan.setText(editscan.getText().toString() + "\n"
							+ getdata.get("Error"));
					Toast.makeText(this, "MES 返回信息发生异常,请确认工单正确", 5).show();
				}
				closeProDia();
			} else {
				logSysDetails(editmainboardSN.getText().toString()
						+ "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;

		case TaskType.Bindpcb:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task的结果数据是："+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "成功");
						num = 1;

						sb.delete(0, sb.length());
						editmainboardSN.setText("");
						editmainboardSN.requestFocus();
						spinner.setSelection(0);

					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("成功扫描：" + str, "成功");
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
		// 点击tv重新获取工单
		case R.id.bindpcb_mubanbutton:
			editmainboardSN.setEnabled(true);
			editmainboardSN.setText("");
			editmainboardnum.setText("");

			break;
		// 当扫错小板时候需要删除的时候！！执行操作！！
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
					Toast.makeText(this, "主板SN长度不正确，请确认正确的SN",
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
					Toast.makeText(this, "工单、主板SN、联板数量不能为空，请检查！",
							Toast.LENGTH_SHORT).show();
				} else {

					int m = Integer.parseInt(editmainboardnum.getText()
							.toString().trim());

					if (num == m) {
						if (!(sb.toString().contains(editsmallboardsn.getText()
								.toString().trim()))) {
							// editsmallboardscan.append("扫描到子板["+num+"]:"
							// +editsmallboardsn.getText().toString().trim());
							logSysDetails("成功扫描到子板["
									+ num
									+ "]SN:"
									+ editsmallboardsn.getText().toString()
											.trim(), "成功");
							sb.append(editsmallboardsn.getText().toString()
									.trim());
							editsmallboardsn.setText("");
							params[0] = editMO.getText().toString().trim();
							params[1] = editmainboardSN.getText().toString()
									.trim();
							params[2] = editmainboardnum.getText().toString()
									.trim();
							params[3] = sb.toString();
							Log.i(TAG, "任务数据是：" + sb.toString());
							bindpcbmodel.bindpcb(new Task(this,
									TaskType.Bindpcb, params));
						} else {
							logSysDetails("SN:"
									+ editsmallboardsn.getText().toString()
											.trim() + "重复", "成功");
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
							editsmallboardsn.setText("");
						}

						// 重置num的值，
						// 重置容器

					} else {
						if (!(sb.toString().contains(editsmallboardsn.getText()
								.toString().trim()))) {
							// editsmallboardscan.append("扫描到子板["+num+"]:"
							// +editsmallboardsn.getText().toString().trim()+"\n");
							logSysDetails("成功扫描到子板["
									+ num
									+ "]SN:"
									+ editsmallboardsn.getText().toString()
											.trim(), "成功");
							sb.append(editsmallboardsn.getText().toString()
									.trim()
									+ ",");
							num++;
						} else {
							logSysDetails("SN:"
									+ editsmallboardsn.getText().toString()
											.trim() + "重复", "成功");
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

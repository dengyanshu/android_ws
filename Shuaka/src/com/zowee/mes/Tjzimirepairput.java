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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
public class Tjzimirepairput extends CommonActivity implements OnClickListener,
		OnKeyListener, OnItemSelectedListener {

	private static String msg = null;

	private EditText editmo;
	private String moid;
	private String moname;

	private EditText linename;
	private Button linebutton;
	private String lineid;// 点击线体选择的第2个界面返回会的值变量接受 线体id!

	private RadioGroup radiogroup;
	private RadioButton radiobutton1;
	private RadioButton radiobutton2;
	private String sntype;

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

	private LinesninputModel linesninputmodel; // 任务处理类
	private Common4dModel common4dModel;
	List<HashMap<String, String>> lineresult;// 线体的list集合 ，存储类型为Hashmap
	private static final String TAG = "Tjzimiproduct";

	private static final int REQUESTCODE = 1;// 传入下个界面的请求码常量

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tjzi_repairout);
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
				.setTitle("确定退出程序?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(Tjzimirepairput.this,
										BackgroundService.class));
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.linesnstorage; // 后台服务静态整形常量
		super.init();
		msg = "tjzimiproduct";
		super.progressDialog.setMessage(msg);

		editmo = (EditText) findViewById(R.id.tjzimi_repairout_mo);

		linebutton = (Button) findViewById(R.id.tjzimi_repairout_linebutton);
		linename = (EditText) findViewById(R.id.tjzimi_repairout_line);
		linename.setEnabled(false);

		radiogroup = (RadioGroup) findViewById(R.id.tjzimi_repairout_radiogroup);
		radiobutton1 = (RadioButton) findViewById(R.id.tjzimi_repairout_radio1);
		radiobutton2 = (RadioButton) findViewById(R.id.tjzimi_repairout_radio2);

		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (arg1 == radiobutton1.getId()) {
					sntype = "PRODUCTSN";
				} else
					sntype = "POWERSN";
			}
		});

		sn = (EditText) findViewById(R.id.tjzimi_repairout_sn);

		button = (Button) findViewById(R.id.tjzimi_repairout_selectbutton);
		selcetedit1 = (EditText) findViewById(R.id.tjzimi_repairout_selectedit1);
		selcetedit1.setEnabled(false);
		selcetedit2 = (EditText) findViewById(R.id.tjzimi_repairout_selectedit2);
		selcetedit2.setEnabled(false);
		spinner = (Spinner) findViewById(R.id.tjzimi_repairout_spinner);
		String[] str3 = new String[] { "查询条码投入", "查询条码维修在库" };
		// 投入在数据库 0,1,2 @Flag INT！
		ArrayAdapter adapter3 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, str3);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter3);

		editscan = (EditText) findViewById(R.id.tjzimi_repairout_scan);
		editscan.setFocusable(false);

		String logText = "程序已启动!检测到该平板的资源名称:[  " + I_ResourceName
				+ " ],用户ID: [ " + userID + " ]!";
		logSysDetails(logText, "程");

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
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);

		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID

		case TaskType.getline:

			if (null != task.getTaskResult()) {

				lineresult = (List<HashMap<String, String>>) task
						.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + lineresult);
			}
			break;
		case TaskType.linegetmo:

			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				if (getdata.containsKey("MOId")) {

					moid = getdata.get("MOId");
					moname=getdata.get("MOName");
					editmo.setText(moname);
					logSysDetails("该工单成功在MES查询到该工单id为" + moid, "成功");
					Log.d(TAG, "task的结果数据是：" + moid);
					sn.requestFocus();
				} else {
					logSysDetails("在MES返回任务的结果为Error，请检查输入的工单是否正确", "成功");
					sn.requestFocus();
				}
			} else {
				logSysDetails("在MES返回任务的结果为空，请检查输入的工单是否正确", "成功");
			}
			break;

		case TaskType.linesnstorage:
			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "成功");
					} else {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails("成功执行：" + str, "成功");
					}
					sn.setText("");
					spinner.clearFocus();
					sn.requestFocus();
				} else {
					logSysDetails("在MES获取信息失败或者为空，解析的内容为空！请检查输入的信息是否正确", "成功");
				}
				closeProDia();
			} else {
				logSysDetails("在MES返回任务的结果为空，请检查输入的信息是否正确", "成功");
			}

			break;
		case TaskType.linesnselect:

			if (null != task.getTaskResult()) {

				HashMap<String, String> getdata = (HashMap<String, String>) task
						.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					if (getdata.containsKey("InputCount")) {
						String linename = getdata.get("WorkcenterName");
						String qty = getdata.get("InputCount");
						selcetedit1.setText(linename);
						selcetedit2.setText(qty);
						logSysDetails("成功从MES系统查询到信息", "成功");
					} else if (getdata.containsKey("InRepairCount")) {
						String linename = getdata.get("WorkcenterName");
						String qty = getdata.get("InRepairCount");
						selcetedit1.setText(linename);
						selcetedit2.setText(qty);
						logSysDetails("成功从MES系统查询到信息", "成功");
					}
				} else {
					logSysDetails(getdata.get("Error"), "成功");
					selcetedit1.setText("");
					selcetedit2.setText("");
				}
				closeProDia();
			} else {
				logSysDetails("在MES获取信息失败，请检查选择的产品类型、线体、查询类型是否正确", "成功");
			}

			break;

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tjzimi_repairout_linebutton:

			if (lineresult == null) {
				Toast.makeText(this, "连接天津数据库得到的数据为空，请确认网络连接情况再试~！",
						Toast.LENGTH_LONG).show();
			} else {
				Log.d(TAG, "task的结果数据是：" + lineresult);
				Intent intent = new Intent(this, Lineselect.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("bundle", (Serializable) lineresult);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE);
			}
			break;

		case R.id.tjzimi_repairout_selectbutton:
			String[] paras = new String[] { "", "", "", "" };
			paras[0] = moid;
			paras[1] = lineid;
			paras[2] = sntype;
			paras[3] = selectflag;
			linesninputmodel.selectSn(new Task(this, TaskType.linesnselect,
					paras));
			Log.i(TAG, "这里有执行paras==" + moid + "," + moid + "," + lineid
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
				Log.i(TAG, "另一个界面返回的数据：：" + lineid + "::" + WorkcenterName);

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
		case R.id.tjzimi_repairout_mo:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				linesninputmodel.getMo(new Task(this, TaskType.linegetmo,
						editmo.getText().toString().trim()));
				sn.requestFocus();
			}
			break;
		case R.id.tjzimi_repairout_sn:

			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (editmo.getText().toString().equals("")
						|| linename.getText().toString().equals("")
						|| sntype == null) {
					Toast.makeText(this, "工单、线体、条码类型不能为空", Toast.LENGTH_SHORT)
							.show();
				}
				String[] params = new String[] { "", "", "", "", "", "" };
				params[0] = lineid;
				params[1] = userID;
				params[2] = moid;
				params[3] = sn.getText().toString().toUpperCase();
				params[4] = sntype;
				params[5] = "2";

				linesninputmodel.storageSn(new Task(this,
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
		case R.id.tjzimi_repairout_spinner:
			String str = spinner.getSelectedItem().toString();
			if (str.equals("查询条码投入")) {
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

package com.zowee.mes;
//板边条码装成 绑定  车号校验  板边条码校验  单个单板条码校验 提交校验
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
//板边条码装车联板条码绑定
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
	// 数据库存中存的是 int型的标志，-装车标识(0：扫描校验车号 1：扫描校验板边条码 2：扫描校验单板条码 3：强制装车 4：清空未关闭的车号)

	private EditText editMO;
	private EditText editMOProduct;
	private EditText editMOMaterial;
	private String moid; // 工单选择保存变量

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

	

	private SntocarModel sntocarmodel; // 任务处理类
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

	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出程序?")
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
		super.TASKTYPE = TaskType.sntocar; // 后台服务静态整形常量
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
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "程序已启动!检测到该平板的资源名称:[ " + resourcename
							+ " ],资源ID: [ " + resourceid + " ],用户ID: [ " + useid + " ]!!请先扫描板边条码获取工单信息，再扫描车号进行车号校验，如需更换车号或者板边条码请点击“车辆”或“板边条码”!";
					logSysDetails(logText, "程序");
				} else {
					logSysDetails(
							"通过资源名称获取在MES获取资源ID失败，请检查配置的资源名称是否正确!", "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确!", "成功");
			}

			break;
		// 获取工单ID
		case TaskType.sntocargetmo:
			String monamemainsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
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
					String scantext = "通过板边SN：[" + monamemainsn + "]成功的获得工单:"
							+ moname + ",产品信息:" + moproduct + ",产品料号："
							+ momaterial + "!";
					logSysDetails(scantext, "成功");
					editcar.requestFocus();
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				} else {
					logSysDetails(
							"通过SN：[" + mainsn + "]在MES获取工单信息为空或者解析结果为空，请检查SN!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails(mainsn + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;
			
		case TaskType.sntocargetmakeupcount:
			
			if (null != task.getTaskResult()) {
				
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					if(getdata.containsKey("MakeUpCount")){
						
						makeupcount = getdata.get("MakeUpCount");
					}
					editmainboardnum.setText(makeupcount);
					String scantext = "通过板边SN：[" + mainboardSN.getText().toString() + "]成功的获得联板数目:"+makeupcount+"!";
					logSysDetails(scantext, "成功");
					SN.requestFocus();
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				} else {
					logSysDetails(
							"通过SN：[" + mainsn + "]在MES获取工单信息为空或者解析结果为空，请检查SN!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails(mainsn + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;
		case TaskType.sntocargetcar:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task的结果数据是："+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "成功");
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
						logSysDetails("车号sn[ "
								+ editcar.getText().toString().toUpperCase()
										.trim() + "]在mes校验成功！" + str, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
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
			
			
		case TaskType.sntocarchecksingelsn:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task的结果数据是："+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "成功");
						SN.requestFocus();
						SN.setText("");
					} else {
						String str = getdata.get("I_ReturnMessage");
						
						
						SN.requestFocus();
						logSysDetails("成功扫描到子板SN[" + num + "]"
								+ SN.getText().toString().trim()+","+str, "成功");
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
					Toast.makeText(this, "MES 返回信息发生异常", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "无MES返回信息", 5).show();
			}

			closeProDia();
			break;
			
			
			
		case TaskType.sntocar:
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

						logSysDetails("成功扫描：" + str, "成功");
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
					Toast.makeText(this, "MES 返回信息发生异常", 5).show();
				}
				closeProDia();
			} else {
				Toast.makeText(this, "无MES返回信息", 5).show();
			}

			closeProDia();
			break;
			
			
		case TaskType.sntocartocar:

			if (null != task.getTaskResult()) {

				getdata = (HashMap<String, String>) task.getTaskResult();
				// Log.d(TAG,"task的结果数据是："+getdata);
				if (getdata.get("Error") == null) {
					// editErrorcode.setText(getdata2.get("DefectcodeSn"));
					if (Integer.parseInt(getdata.get("I_ReturnValue")
							.toString()) == -1) {
						String str = getdata.get("I_ReturnMessage");
						logSysDetails(str, "成功");
						editcar.setEnabled(true);
					} else {
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
						String str = getdata.get("I_ReturnMessage");
						editcar.setEnabled(true);
						editcar.setText("");
						editcar.requestFocus();
						logSysDetails("车号sn[ "
								+ editcar.getText().toString().toUpperCase()
										.trim() + "]强制装车成功！" + str, "成功");
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
		// 强制装车按钮
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
			Log.i(TAG, "任务数据是：" + sb.toString() + ":::" + params[0] + ":::"
					+ params[2] + ":::" + params[3] + ":::" + params[5]);
			sntocarmodel.sntocar(new Task(this, TaskType.sntocartocar, params));
			break;

		case R.id.sntocar_cartextview:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("更换车号");
			builder.setMessage("是否需要重新校验车号？");
			builder.setPositiveButton("确定",
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
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
		case R.id.sntocar_mainsntextview:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("更换板边条码");
			builder1.setMessage("是否需要重新更换板边条码？");
			builder1.setPositiveButton("确定",
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
			builder1.setNegativeButton("取消", null);
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
					Toast.makeText(this, "工单、车号、板边条码待绑定数量不能为空",
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
							Log.i(TAG, "任务数据是：" + sb.toString() + ":::"
									+ params[0] + ":::" + params[2] + ":::"
									+ params[3] + ":::" + params[5]);
							sntocarmodel.sntocar(new Task(this,
									TaskType.sntocar, params));
							SN.setText("");
							snsum.setText("");
						} else {
							SN.setText("");
							Toast.makeText(this, "请先扫描子板数量", Toast.LENGTH_SHORT)
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
									+ SN.getText().toString().trim() + "重复扫描！",
									"成功");
							SN.setText("");
							SN.requestFocus();
						}

						
					}
				}
			}
			break;
		// 获取工单信息
		case R.id.sntocar_moname:
			String paras = editMO.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (paras.length() < 6) {
					Toast.makeText(this, "板边SN长度不正确，请确认正确的SN",
							Toast.LENGTH_SHORT).show();
				} else {
					sntocarmodel.getMo(new Task(this, TaskType.sntocargetmo,
							paras));
				}
			}
			break;
		//单纯获取联板数量 5.29修改 刘成MES	
		case  R.id.sntocar_mainsn:
			String paras1 = mainboardSN.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (paras1.length() < 6) {
					Toast.makeText(this, "板边SN长度不正确，请确认正确的SN",
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
				params[3] = "0";//联板数量数据库是int型
				params[4] = "";
				params[5] = "0";
				params[6] = resourceid;
				params[7] = resourcename;
				params[8] = useid;
				params[9] = usename;
				// int 型 校验标志
				if (carsn.length() < 6 || moid == null) {
					Toast.makeText(this, "请先确认车辆SN的长度的正确，并且工单的不能为空",
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

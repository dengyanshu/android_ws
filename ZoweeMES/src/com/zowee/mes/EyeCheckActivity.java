package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.TjeyecheckModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EyeCheckActivity extends CommonActivity implements
		View.OnClickListener, AdapterView.OnItemSelectedListener {

	private Spinner SpinEyecheckMOID;
	private EditText EditEyecheckProductID;
	private EditText EditeyeCheckWorkFlow;
	private EditText EditeyeCheckErrorCode;
	private EditText EditeyeCheckStartSN;
	private EditText Edieyecheckquantity;
	private Button butteyecheckSubmit;
	private Button butteyecheckCancel;

	private EditText EditScanInfo;
	private String msg;
	private GetMOnameModel GetMoModel;
	private TjeyecheckModel eyecheckmodel;
	private String ResourceID;
	private String MOID;
	private String moName;
	private List<HashMap<String, String>> moNames;// 工单下拉框的数据源
	private static final String MONAME = "MOName";
	private String LotSN;
	String[] Params = new String[] { "", "", "", "", "", "", "", "", "" };

	private String PreIsErrCode = "false";
	private String PreErrCodeId = "";
	private String PreErrCodeName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eye_check);
		this.setTitleColor(Color.GREEN);
		this.setTitle("TJ-目检");
		init();

		logDetails(EditScanInfo, "请先确认已扫描不良代码中不良代码正确，再扫描不良产品批号！");
		logDetails(EditScanInfo, "扫描工单号可以自动选择工单信息");
		logDetails(EditScanInfo, "如是是良品，请直接扫描批号");
		logDetails(EditScanInfo, "如果是不良品，请先扫描不良代码，再扫描批号");
		logDetails(EditScanInfo, "提示：");
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.package_eyecheckGetMOinfo;
		super.init();
		msg = "目检-获取工单信息";
		super.progressDialog.setMessage(msg);
		GetMoModel = new GetMOnameModel();
		eyecheckmodel = new TjeyecheckModel();
		SpinEyecheckMOID = (Spinner) findViewById(R.id.Spin_EyecheckMOID);
		EditEyecheckProductID = (EditText) findViewById(R.id.Edit_EyecheckProductID);
		EditeyeCheckWorkFlow = (EditText) findViewById(R.id.Edit_eyeCheckWorkFlow);
		EditeyeCheckErrorCode = (EditText) findViewById(R.id.Edit_eyeCheckErrorCode);
		EditeyeCheckStartSN = (EditText) findViewById(R.id.Edit_eyeCheckStartSN);
		butteyecheckSubmit = (Button) findViewById(R.id.butt_eyecheckSubmit);
		butteyecheckCancel = (Button) findViewById(R.id.butt_eyecheckCancel);
		EditScanInfo = (EditText) findViewById(R.id.edt_sysdetail);
		Edieyecheckquantity = (EditText) findViewById(R.id.Edit_eyecheckquantity);

		SpinEyecheckMOID.setOnItemSelectedListener(this);
		EditeyeCheckStartSN.setOnClickListener(this);
		EditScanInfo.setOnClickListener(this);
		butteyecheckSubmit.setOnClickListener(this);
		butteyecheckCancel.setOnClickListener(this);

		GetResourceId();
		getMONames();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtstick, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_clear:
			EditScanInfo.setText("");
			break;
		}

		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Spin_EyecheckMOID:
			if (null == moNames || 0 == moNames.size())
				return;
			moName = moNames.get(arg2).get(MONAME);
			GetMOinfo(moName);
			ClearErrorCode();
			if (this.EditScanInfo.getLineCount() > 100)
				this.EditScanInfo.setText("");
			break;
		}

	}

	@Override
	public void refresh(Task task) {
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.GetMoNameByMOSTDType:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((List<HashMap<String, String>>) task
							.getTaskResult()).size()) {
				clearWidget(SpinEyecheckMOID, null);
				if (null != moNames)
					moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String, String>>) task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			SpinEyecheckMOID.setAdapter(adapter);
			break;

		case TaskType.GetResourceId:
			ResourceID = "";
			@SuppressWarnings("unchecked")
			List<HashMap<String, String>> getresult = (List<HashMap<String, String>>) task
					.getTaskResult();
			if (super.isNull || 0 == (getresult).size()) {
				logDetails(EditScanInfo, "未能获取到资源ID，请检查资源名是否设置正确");
				return;
			}
			getresult = (List<HashMap<String, String>>) task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if (ResourceID.isEmpty())
				logDetails(EditScanInfo, "未能获取到资源ID，请检查资源名是否设置正确");
			break;
		case TaskType.package_eyecheckGetMOinfo:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				ClearAll();
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			EditeyeCheckStartSN.setText("");
			MOID = getdata.get("MOId");
			Edieyecheckquantity.setText(getdata.get("quantity"));
			EditEyecheckProductID.setText(getdata.get("ProductID"));
			EditeyeCheckWorkFlow.setText(getdata.get("WorkFlow"));
			logDetails(EditScanInfo, "Success_Msg:" + getdata.toString());
			break;
		case TaskType.package_eyecheckscan:
			super.closeProDia();
			EditeyeCheckStartSN.setText("");
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				ClearErrorCode();
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				if (getdata.get("ErrCode").toString().trim()
						.equalsIgnoreCase("true")) {
					PreIsErrCode = "true";
					PreErrCodeId = getdata.get("ErrCodeId").toString().trim();
					PreErrCodeName = getdata.get("ErrCodeNote").toString()
							.trim();
					this.EditeyeCheckErrorCode.setText(LotSN + ":"
							+ PreErrCodeName);
				} else {
					ClearErrorCode();
				}
				logDetails(EditScanInfo,
						"Success_Msg:"
								+ getdata.get("ReturnMsg").toString()
										.replaceFirst("ServerMessage:", "")
								+ "\r\n");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
			} else {
				logDetails(EditScanInfo, getdata.get("ReturnMsg").toString()
						.replaceFirst("ServerMessage:", "")
						+ "\r\n");
			}
			break;

		}
	}

	private void ClearAll() {
		MOID = "";
		EditeyeCheckStartSN.setText("");
		EditEyecheckProductID.setText("");
		EditeyeCheckWorkFlow.setText("");
		Edieyecheckquantity.setText("");
	}

	private void ClearErrorCode() {
		PreIsErrCode = "false";
		PreErrCodeId = "";
		PreErrCodeName = "";
		this.EditeyeCheckErrorCode.setText("");
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void getMONames() {
		super.progressDialog.setMessage("获取工单号");
		super.showProDia();
		String MOSTDType = "";
		Task task = new Task(this, TaskType.GetMoNameByMOSTDType, MOSTDType);
		GetMoModel.getMoNamesByMOSTdType(task);
	}

	private void GetMOinfo(String MOName) {
		super.progressDialog.setMessage("获取订单信息");
		super.showProDia();
		Task task = new Task(this, TaskType.package_eyecheckGetMOinfo, MOName);
		eyecheckmodel.GetMOinfo(task);
	}

	private void GetResourceId() {
		super.progressDialog.setMessage("获取资源ID");
		super.showProDia();
		Task task = new Task(this, TaskType.GetResourceId, MyApplication
				.getAppOwner().toString().trim());
		GetMoModel.GetResourceId(task);
	}

	private void Starteyecheck(String lotSN) {

		Params[0] = MOID;
		Params[1] = lotSN;
		Params[2] = MyApplication.getAppOwner().toString().trim();
		Params[3] = ResourceID;
		Params[4] = PreIsErrCode;
		Params[5] = PreErrCodeId;
		Params[6] = PreErrCodeName;

		if (Params[2].equalsIgnoreCase("system")) {

			logDetails(EditScanInfo, "请先设定资源名称");
			return;
		}
		if (Params[0].trim().isEmpty()) {

			logDetails(EditScanInfo, "请先获取工单信息");
			return;
		}
		if (Params[3].isEmpty()) {

			logDetails(EditScanInfo, "请确认资源名称是否正确");
			return;
		}
		super.progressDialog.setMessage("目检扫描");
		super.showProDia();

		Task task = new Task(this, TaskType.package_eyecheckscan, Params);
		eyecheckmodel.Tjeyecheck(task);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.butt_eyecheckCancel:
			ClearErrorCode();
			break;
		case R.id.butt_eyecheckSubmit:

		case R.id.Edit_eyeCheckStartSN:
			LotSN = EditeyeCheckStartSN.getText().toString().trim()
					.toUpperCase();
			if (LotSN.trim().length() < 5) {
				return;
			}
			if (StringUtils.isScannedMONumber(LotSN)) {
				String GetItem;
				for (int i = 0; i < SpinEyecheckMOID.getAdapter().getCount(); i++) {
					GetItem = ((HashMap<String, String>) (this.SpinEyecheckMOID
							.getAdapter().getItem(i))).get(MONAME).toString();
					if (GetItem.equals(LotSN)) {
						SpinEyecheckMOID.setSelection(i);
						break;
					}
					if (i == SpinEyecheckMOID.getAdapter().getCount() - 1)// 没有找到扫描的工单号。
					{
						Toast.makeText(this, "请确认输入的工单号是否正确", 5).show();
					}
				}
				EditeyeCheckStartSN.setText("");
			} else {
				Starteyecheck(LotSN);
				if (this.EditScanInfo.getLineCount() > 100)
					this.EditScanInfo.setText("");
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("确定退出目检吗?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(EyeCheckActivity.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

}

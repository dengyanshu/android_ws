package com.zowee.mes.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.model.FeedOnMateriModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description 上料对料
 */
public class FeedOnMateriActivity extends CommonActivity implements
		View.OnFocusChangeListener, View.OnClickListener,
		AdapterView.OnItemSelectedListener {

	private EditText edtLotSN;
	private Spinner spinMoNames;// 工单
	private EditText edtLineNo;// 产线
	private EditText edtStationNo;// 机台
	private View btnGroups;
	private Button btnEnsure;// 确认键
	private EditText edtSysDatails;
	private int scanStates = 0;// 扫描状态 0=初始状态 1=站位条码 2==物料批号
	private FeedOnMateriModel model;
	private String moName;// 订单名字
	private String smtMountId;// 工单Id
	private List<HashMap<String, String>> moNames;// 工单下拉框的数据源
	private static final String MONAME = "MOName";
	// private HashMap<String,String> adaSelItem;//工单下拉框被选择的项
	private Button btnClearStalot;
	private Button btnCancel;
	private Button btnScan;
	private Button btnRef;
	private String staLotSN = "";// 站位条码
	private Dialog dialog;
	private View viewDialog;
	private Button dialog_btnEnsure;
	private Button dialog_btnCancel;
	private EditText dialog_edtStaLotSN;
	private EditText dialog_edtLotSN;

	@Override
	public void init() {
		model = new FeedOnMateriModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FeedOnMaterial_SCAN;
		super.init();
		edtLotSN = (EditText) this.findViewById(R.id.edt_operatescan);
		spinMoNames = (Spinner) this.findViewById(R.id.spin_workorder);
		edtLineNo = (EditText) this.findViewById(R.id.edt_productline);
		edtStationNo = (EditText) this.findViewById(R.id.edt_machine);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnEnsure = (Button) btnGroups.findViewById(R.id.btn_ensure);
		btnClearStalot = (Button) this.findViewById(R.id.btn_cancel_stalotsn);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnRef = (Button) btnGroups.findViewById(R.id.btn_refreshData);
		// btnRef.setText(R.string.refMONames);
		edtSysDatails = (EditText) this.findViewById(
				R.id.common_edtView_operateDetails).findViewById(
				R.id.edt_sysdetail);
		dialog = new Dialog(this, R.style.chemat_statab_dialog);
		viewDialog = View.inflate(this, R.layout.input_feed_onmater, null);
		dialog.setContentView(viewDialog);
		dialog_btnEnsure = (Button) viewDialog
				.findViewById(R.id.dialog_btnEnsure);
		dialog_btnCancel = (Button) viewDialog
				.findViewById(R.id.dialog_btnCancel);
		dialog_edtStaLotSN = (EditText) viewDialog
				.findViewById(R.id.dig_edtStaLotSN);
		dialog_edtLotSN = (EditText) viewDialog
				.findViewById(R.id.dig_edtMater_Batnum);
		viewDialog.findViewById(R.id.layout_mater_batnum).setVisibility(
				View.GONE);// 隐藏物料批号的选项

		edtLotSN.setOnFocusChangeListener(this);
		edtLineNo.setOnFocusChangeListener(this);
		btnEnsure.setOnClickListener(this);
		btnClearStalot.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRef.setOnClickListener(this);
		spinMoNames.setOnItemSelectedListener(this);
		dialog_btnCancel.setOnClickListener(this);
		dialog_btnEnsure.setOnClickListener(this);
		// getMONames();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.feeding_onmeteri);
		init();
	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.FeedOnMaterial_SCAN:
			// Log.i(TAG, "COME IN");
			if (super.isNull)
				return;
			// Log.i(TAG, "COME IN");
			String scanRes = task.getTaskResult().toString().trim();
			// super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
			if (1 == scanStates) {
				// scanRes = "S0213-11-R";
				refStaData(scanRes);
			} else if (2 == scanStates) {
				// scanRes = "Mxxxxxx11";
				this.edtLotSN.setText(scanRes);
				// refMoNames(scanRes);

				// moName = "";//每次查询完后 清除原有的工单名
				// Task getParam = new Task(this,
				// TaskType.FeedOnMaterial_GETPARAS, scanRes);
				// model.getNeceParams(getParam);
			}
			break;
		case TaskType.FeedOnMaterial_GETMONames:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((List<HashMap<String, String>>) task
							.getTaskResult()).size()) {
				clearWidget(spinMoNames, null);
				if (null != moNames)
					moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String, String>>) task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			spinMoNames.setAdapter(adapter);
			break;
		case TaskType.FeedOnMaterial:
			closeProDia();
			if (super.isNull)
				return;
			String[] reses = (String[]) task.getTaskResult();
			String retRes = reses[0];
			String retMsg = reses[1];
			StringBuffer sbf = new StringBuffer(this.edtSysDatails.getText()
					.toString());
			if (!StringUtils.isEmpty(retRes) && retRes.trim().equals("0"))
				sbf.append(getString(R.string.feedOnMateriSucces) + "\n");
			else
				sbf.append(getString(R.string.feedOnMateriFail) + "\n");
			if (!StringUtils.isEmpty(retMsg))
				retMsg = retMsg.replaceAll("/r/n", "  ");
			else
				retMsg = "";
			sbf.append(retMsg + "\n\n");
			this.edtSysDatails.setText(sbf.toString());
			break;
		case TaskType.FeedOnMaterial_GETSMTMOUNTID:
			closeProDia();
			if (super.isNull)
				return;
			smtMountId = task.getTaskResult().toString();
			// Log.i(TAG, smtMountId);
			// Toast.makeText(this,smtMountId, Toast.LENGTH_SHORT).show();
			break;

		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		switch (v.getId()) {
		case R.id.edt_productline:
			EditText tempEdt = (EditText) v;
			if (tempEdt.isFocused()) {
				this.addImforToEdtSysDetai(getString(R.string.scanStaSN) + "\n");
				scanStates = 1;
			}
			break;
		case R.id.edt_operatescan:
			EditText tempEdt1 = (EditText) v;
			if (tempEdt1.isFocused()) {
				this.addImforToEdtSysDetai(getString(R.string.scanLotSN) + "\n");
				scanStates = 2;
			}
			break;
		// default:
		// scanStates = 0;
		// break;
		}

	}

	private void addImforToEdtSysDetai(String str) {
		if (!StringUtils.isEmpty(str) && null != edtSysDatails)
			edtSysDatails.setText(edtSysDatails.getText() + str);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ensure:
			boolean isPass = subWidget();
			if (!isPass)
				return;
			String[] paras = new String[] { this.moName, this.smtMountId,
					staLotSN, edtLotSN.getText().toString() };
			Log.i(TAG, this.smtMountId + "" + this.moName + "");
			Task task = new Task(this, TaskType.FeedOnMaterial, paras);
			super.progressDialog.setMessage(getString(R.string.TransationData));
			showProDia();
			model.feedOnMaterial(task);
			break;
		case R.id.btn_cancel_stalotsn:
			staLotSN = "";
			clearWidget(edtLineNo, null);
			clearWidget(edtStationNo, null);
			break;
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_cancel:
			restoreUIData();
			break;
		case R.id.btn_refreshData:
			// String lotSN = edtLotSN.getText().toString().trim();
			// if(StringUtils.isEmpty(lotSN))
			// {
			// Toast.makeText(this, R.string.mater_batnum_notnull,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			// //refMoNames(lotSN);
			getMONames();
			break;
		case R.id.dialog_btnCancel:
			dialog.dismiss();
			break;
		case R.id.dialog_btnEnsure:
			String stationLotSN = dialog_edtStaLotSN.getText().toString()
					.trim();
			if (refStaData(stationLotSN))
				dialog.dismiss();
			dialog_edtStaLotSN.setText("");
			break;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// HashMap<String,String> selItem =
		// (HashMap<String,String>)paraMapLis.get(arg2);
		if (null == moNames || 0 == moNames.size())
			return;
		moName = moNames.get(arg2).get(MONAME);
		Log.i(TAG, "" + moName);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private boolean subWidget() {
		boolean isPass = true;
		if (StringUtils.isEmpty(moName)) {
			isPass = false;
			Toast.makeText(this, getString(R.string.moName_isnull),
					Toast.LENGTH_SHORT).show();// 工单
			return isPass;
		}
		if (StringUtils.isEmpty(edtLineNo.getText().toString()))// 产线为空
		{
			isPass = false;
			Toast.makeText(this, getString(R.string.proLine_isnull),
					Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if (3 != edtLineNo.getText().toString().trim().length()) {
			isPass = false;
			Toast.makeText(this, R.string.lineNo_notLegal, Toast.LENGTH_SHORT)
					.show();
			return isPass;
		}
		if (StringUtils.isEmpty(edtStationNo.getText().toString()))// 站位条码
		{
			isPass = false;
			Toast.makeText(this, getString(R.string.staNO_isnull),
					Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if (1 != edtStationNo.getText().toString().trim().length()) {
			isPass = false;
			Toast.makeText(this, R.string.stationNo_notLegal,
					Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if (StringUtils.isEmpty(edtLotSN.getText().toString()))// 物料批号
		{
			isPass = false;
			Toast.makeText(this, getString(R.string.materialNum_notNull),
					Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if (StringUtils.isEmpty(smtMountId))// 数据错误
		{
			isPass = false;
			Toast.makeText(this, getString(R.string.repScan_stalot),
					Toast.LENGTH_SHORT).show();
			return isPass;
		}

		return isPass;
	}

	// /**
	// * @description 清空产线和机台
	// */
	// private void emptyLineStation()
	// {
	// super.clearWidget(edtLineNo, null);
	// super.clearWidget(edtStationNo, null);
	// }

	/**
	 * @description 重置所有的UI界面数据 被 取消 按钮调用
	 */
	private void restoreUIData() {
		super.clearWidget(spinMoNames, null);
		if (null != moNames)
			moNames.clear();
		staLotSN = "";
		moName = "";
		super.clearWidget(edtLineNo, null);
		super.clearWidget(edtStationNo, null);
		smtMountId = "";
		super.clearWidget(edtLotSN, null);
	}

	private boolean refStaData(String tempStaLotSN) {

		if (StringUtils.isEmpty(tempStaLotSN) || 5 > tempStaLotSN.length()) {
			Toast.makeText(this, R.string.error_staLot, Toast.LENGTH_SHORT)
					.show();
			// super.closeProDia();
			return false;
		}
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		staLotSN = tempStaLotSN.trim();
		this.edtLineNo.setText(tempStaLotSN.substring(0, 3));
		this.edtStationNo.setText(tempStaLotSN.substring(3, 4));
		smtMountId = "";
		Task getSmtMountId = new Task(this,
				TaskType.FeedOnMaterial_GETSMTMOUNTID, tempStaLotSN);
		model.getSMTMountIdParam(getSmtMountId);

		return true;
	}

	// private void refMoNames(String lotSN)
	// {
	// super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
	// showProDia();
	// moName = "";//每次查询完后 清除原有的工单名
	// Task getParam = new Task(this, TaskType.FeedOnMaterial_GETPARAS, lotSN);
	// model.getNeceParams(getParam);
	//
	// }

	// private void manulRefData()
	// {
	//
	// String stationLotSN = dialog_edtStaLotSN.getText().toString().trim();
	// String lotSN = dialog_edtLotSN.getText().toString().trim();
	// //数据非空验证
	// // dialog.dismiss();
	// //super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
	// progressDialog.show();
	// refStaData(stationLotSN);
	// while(progressDialog.isShowing())
	// {
	// try
	// {
	// Thread.sleep(10);
	// }
	// catch (Exception e)
	// {
	// // TODO: handle exception
	// }
	// }
	// progressDialog.show();
	// refMoNames(lotSN);
	//
	// }

	// @Override
	// public void run()
	// {
	// manulRefData();
	//
	// }

	private void getMONames() {
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		super.showProDia();
		Task task = new Task(this, TaskType.FeedOnMaterial_GETMONames, null);
		model.getNeceParams(task);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null == moNames)
			getMONames();
	}

}

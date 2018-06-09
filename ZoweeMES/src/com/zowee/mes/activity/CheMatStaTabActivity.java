package com.zowee.mes.activity;

import com.zowee.mes.R;
import com.zowee.mes.model.CheMatStaTabModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Administrator
 * @description 查料站表
 */
public class CheMatStaTabActivity extends CommonActivity implements
		View.OnFocusChangeListener, View.OnClickListener {

	private EditText edtLineStaNo;// 产线机台编辑框
	private EditText edtSlotCount;// 用于记录已用的站位条码/总的站位条码数量
	private CheMatStaTabModel model;
	private EditText edtSysDetais;
	private String docNo;// 产线
	private String staNo;// 机台
	private View btnGroups;
	private Button btnRef;
	private Button btnScan;
	private Button btnCancel;
	private Dialog dialog;
	private View viewDialog;
	private Button dialogBtnEnsure;
	private Button dialogBtnCancel;
	private EditText diaEdtStationNo;// 站位条码文本框
	private int slotCount = 0;// 特定机台可以的槽位条码
	private int usingSlotCount = 0;// 已用的特定机台的槽位条码

	@Override
	public void init() {
		model = new CheMatStaTabModel();
		super.TASKTYPE = TaskType.CHEMATSTATAB_SCAN;
		super.commonActivity = this;
		super.init();

		dialog = new Dialog(this, R.style.chemat_statab_dialog);
		viewDialog = View.inflate(this, R.layout.input_feed_onmater, null);
		dialog.setContentView(viewDialog);
		viewDialog.findViewById(R.id.layout_mater_batnum).setVisibility(
				View.GONE);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnGroups.findViewById(R.id.btn_ensure).setVisibility(View.GONE);
		btnRef = (Button) btnGroups.findViewById(R.id.btn_refreshData);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);
		dialogBtnEnsure = (Button) viewDialog
				.findViewById(R.id.dialog_btnEnsure);
		dialogBtnCancel = (Button) viewDialog
				.findViewById(R.id.dialog_btnCancel);
		diaEdtStationNo = (EditText) viewDialog
				.findViewById(R.id.dig_edtStaLotSN);

		edtLineStaNo = (EditText) this.findViewById(R.id.edt_meter_lineStaNo);
		edtSlotCount = (EditText) this.findViewById(R.id.edt_slotCount);
		edtSysDetais = (EditText) this.findViewById(
				R.id.common_edtView_operateDetails).findViewById(
				R.id.edt_sysdetail);

		edtLineStaNo.setOnFocusChangeListener(this);
		btnRef.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		dialogBtnCancel.setOnClickListener(this);
		dialogBtnEnsure.setOnClickListener(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chemat_statab);
		init();
	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.CHEMATSTATAB_SCAN:
			if (super.isNull)
				return;
			String staLotSN = task.getTaskResult().toString().trim();
			refreshData(staLotSN);
			// String staLotSN = "S012dgdgd";
			// if(5>staLotSN.length())
			// {
			// Toast.makeText(this, R.string.error_staLot,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			// String lineNo = staLotSN.substring(0,3);
			// String stationNo = staLotSN.substring(3, 4);
			// docNo = lineNo;
			// staNo = stationNo;
			// edtLineNo.setText(lineNo);
			// edtStationNo.setText(stationNo);
			// super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
			// showProDia();
			// Task cheMat = new Task(this, TaskType.CHEMATSTATAB, staLotSN);
			// model.cheMatStaTab(cheMat);
			break;
		case TaskType.CHEMATSTATAB:
			closeProDia();
			String res1 = getString(R.string.CheMatStaTabRes) + "\n\n\n";
			res1 = res1.replaceAll("docNo", docNo);
			res1 = res1.replaceAll("staNo", staNo);//
			if (super.isNull) {
				res1 = res1.replaceAll("\\{\\}", "0");
				usingSlotCount = 0;
				edtSlotCount.setText(usingSlotCount + " / " + slotCount);
				this.edtSysDetais.setText(res1);
				return;
			}
			;
			String[] reses = (String[]) task.getTaskResult();
			usingSlotCount = Integer.valueOf(reses[0].trim());
			edtSlotCount.setText(usingSlotCount + " / " + slotCount);
			res1 = res1.replaceAll("\\{\\}", reses[0]);
			StringBuffer sbf = new StringBuffer(res1);
			CharSequence charSequence = model.hignRetMsg(sbf.toString(),
					reses[1], Color.GREEN);
			// charSequence = model.hignRetMsg(reses[1], Color.GREEN,"上料数量");
			this.edtSysDetais.setText(charSequence);
			// Log.i(TAG, charSequence.toString());
			break;
		case TaskType.CHEMATSTATAB_GETSLOTCOUNT:
			closeProDia();
			checkMatStaTab();
			if (super.isNull) {
				slotCount = 0;
				return;
			}
			;
			slotCount = Integer.valueOf(task.getTaskResult().toString().trim());
			break;
		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.edt_meter_lineStaNo:
			EditText edtDocNo = (EditText) v;
			if (edtDocNo.isFocused()
					&& StringUtils.isEmpty(edtDocNo.getText().toString())) {
				edtSysDetais.setText(getString(R.string.scanStaSN) + "\n\n");
				return;
			}
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		edtLineStaNo.setText("");
		edtSlotCount.setText("");
		edtSysDetais.setText("");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scan:
			super.laserScan();
			break;
		// case R.id.btn_ensure:
		//
		// break;
		case R.id.btn_cancel:
			restoreUIData();
			break;
		case R.id.btn_refreshData:
			dialog.show();
			break;
		case R.id.dialog_btnCancel:
			dialog.dismiss();
			break;
		case R.id.dialog_btnEnsure:
			String stationLotSN = diaEdtStationNo.getText().toString();
			if (!refreshData(stationLotSN))
				return;
			// refreshData(stationLotSN);
			diaEdtStationNo.setText("");
			dialog.dismiss();
			break;
		}

	}

	private boolean refreshData(String staLotSN) {
		if (StringUtils.isEmpty(staLotSN) || 5 > staLotSN.length()) {
			Toast.makeText(this, R.string.error_staLot, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		staLotSN = staLotSN.trim();
		String lineNo = staLotSN.substring(0, 3);
		String stationNo = staLotSN.substring(3, 4);
		docNo = lineNo;
		staNo = stationNo;
		edtLineStaNo.setText(lineNo + staNo);
		// edtSlotCount.setText(stationNo);
		// super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		// showProDia();
		// Task cheMat = new Task(this, TaskType.CHEMATSTATAB, staLotSN);
		// model.cheMatStaTab(cheMat);
		getDocStaSlotCount();

		return true;
	}

	private void restoreUIData() {
		super.clearWidget(edtLineStaNo, null);
		super.clearWidget(edtSlotCount, null);
		super.clearWidget(edtSysDetais, null);

	}

	/**
	 * 获取某个机台槽位条码的数量
	 */
	private void getDocStaSlotCount() {
		super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		showProDia();
		Task getSlotCount = new Task(this, TaskType.CHEMATSTATAB_GETSLOTCOUNT,
				docNo.trim() + staNo.trim());
		model.getSlotCount(getSlotCount);

	}

	private void checkMatStaTab() {
		super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		showProDia();
		Task cheMatStaTab = new Task(this, TaskType.CHEMATSTATAB, docNo.trim()
				+ staNo.trim());
		model.cheMatStaTab(cheMatStaTab);

	}

}

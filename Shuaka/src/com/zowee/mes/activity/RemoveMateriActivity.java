package com.zowee.mes.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.model.RemoveMateriModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description Ð¶»»ÁÏ¾í
 */
public class RemoveMateriActivity extends CommonActivity implements
		View.OnClickListener {
	private EditText edtStaLotSN;
	private EditText edtSysDetails;
	private RemoveMateriModel model;
	private View btnGroups;
	private Button btnScan;
	private Button btnEnsure;
	private Button btnCancel;

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.REMOVEMATERI_SCAN;
		super.init();
		model = new RemoveMateriModel();

		btnGroups = this.findViewById(R.id.btnGroups);
		btnGroups.findViewById(R.id.btn_refreshData).setVisibility(View.GONE);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnEnsure = (Button) btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);

		this.edtStaLotSN = (EditText) this.findViewById(R.id.edt_slot_barcode);
		this.edtSysDetails = (EditText) this.findViewById(
				R.id.common_edtView_operateDetails).findViewById(
				R.id.edt_sysdetail);
		btnScan.setOnClickListener(this);
		btnEnsure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.remove_meter);
		init();

	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.REMOVEMATERI_SCAN:
			if (super.isNull)
				return;
			String staLotSN = task.getTaskResult().toString().trim();
			// String staLotSN = "S012Txxx2";
			this.edtStaLotSN.setText(staLotSN);
			// if(StringUtils.isEmpty(staLotSN)||5>staLotSN.length())
			// {
			// Toast.makeText(this, R.string.ERROR_LOTSN, 1000).show();
			// return;
			// }
			// progressDialog.setMessage(getString(R.string.TransationData));
			// showProDia();
			// Task removeMateri = new
			// Task(this,TaskType.REMOVEMATERI,staLotSN);
			// model.removeMateri(removeMateri);
			refreshData(staLotSN);
			break;
		case TaskType.REMOVEMATERI:
			closeProDia();
			if (super.isNull)
				return;
			String[] reses = (String[]) task.getTaskResult();
			StringBuffer sbf = new StringBuffer(this.edtSysDetails.getText()
					.toString());
			String retRes = reses[0];
			String retMsg = reses[1];
			if (!StringUtils.isEmpty(retRes) && "0".equals(retRes.trim()))
				sbf.append(getString(R.string.removeMateriSucc) + "\n");
			else
				sbf.append(getString(R.string.removeMateriFail) + "\n");
			if (StringUtils.isEmpty(retMsg))
				retMsg = "";
			sbf.append(retMsg + "\n\n");
			this.edtSysDetails.setText(sbf.toString());
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			super.clearWidget(edtStaLotSN, null);
			break;
		case R.id.btn_ensure:
			String staLotSN = edtStaLotSN.getText().toString().trim();
			refreshData(staLotSN);
			break;
		case R.id.btn_scan:
			super.laserScan();
			break;
		}

	}

	private void refreshData(String staLotSN) {
		if (5 > staLotSN.length()) {
			Toast.makeText(this, R.string.error_staLot, 1000).show();
			return;
		}
		progressDialog.setMessage(getString(R.string.TransationData));
		showProDia();
		Task removeMateri = new Task(this, TaskType.REMOVEMATERI, staLotSN);
		model.removeMateri(removeMateri);

	}

}

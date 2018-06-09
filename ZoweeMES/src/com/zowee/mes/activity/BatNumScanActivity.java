package com.zowee.mes.activity;

import gnu.io.SerialPortEventListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.BatNumScanModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator ÅúºÅ²éÑ¯
 * @description
 */
public class BatNumScanActivity extends CommonActivity implements
		View.OnClickListener {

	private EditText edtBatNum;
	// private static final String TAG = "activity";
	private BatNumScanModel model;
	private EditText edtSysDetail;
	private View btnGroups;
	private Button btnScan;
	private Button btnEnsure;
	private Button btnCancel;
	private Button btnRefData;

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.BATNUM_SCAN;
		super.init();
		model = new BatNumScanModel();

		btnGroups = this.findViewById(R.id.btnGroups);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnEnsure = (Button) btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);
		btnRefData = (Button) btnGroups.findViewById(R.id.btn_refreshData);
		edtBatNum = (EditText) this.findViewById(R.id.edt_scaned_batnum);
		edtSysDetail = (EditText) this.findViewById(
				R.id.common_edtView_operateDetails).findViewById(
				R.id.edt_sysdetail);

		btnRefData.setVisibility(View.GONE);
		btnScan.setOnClickListener(this);
		btnEnsure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.batnum_scan);
		init();
	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.BATNUM_SCAN:
			if (super.isNull)
				return;
			String lotSN = task.getTaskResult().toString().trim();
			// String lotSN = "Mxxxxxx10";
			this.edtBatNum.setText(lotSN);
			refData(lotSN);
			// super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
			// showProDia();
			// Task getLotSNImforTask = new Task(this, TaskType.BATNUM_INFOR,
			// lotSN);
			// //Task getLotSNImforTask = new Task(this,
			// TaskType.BATNUM_INFOR,"Mxxxxxx5");
			// model.getLotSNImfor(getLotSNImforTask);
			break;
		case TaskType.BATNUM_INFOR:
			closeProDia();
			if (super.isNull)
				return;
			String[] reses = (String[]) task.getTaskResult();
			String retRes = reses[0];
			String retMsg = reses[1];
			StringBuffer sbf = new StringBuffer(this.edtSysDetail.getText()
					.toString());
			if (!StringUtils.isEmpty(retRes) && retRes.trim().equals("0"))
				sbf.append(getString(R.string.batNumScan_success) + "\n");
			else
				sbf.append(getString(R.string.batNumScan_fail) + "\n");
			if (!StringUtils.isEmpty(retMsg)) {
				retMsg = retMsg.replaceAll("/r/n", "  ");
			} else
				retMsg = "";
			sbf.append(retMsg + "\n\n");
			this.edtSysDetail.setText(sbf.toString());
			break;
		}
	}

	// @Override
	// protected void onPause()
	// {
	// // TODO Auto-generated method stub
	// super.onPause();
	// this.edtBatNum.setText("");
	//
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_cancel:
			super.clearWidget(edtBatNum, null);
			break;
		case R.id.btn_ensure:
			String lotSN = edtBatNum.getText().toString().trim();
			if (StringUtils.isEmpty(lotSN)) {
				Toast.makeText(this, R.string.batNum_notNull,
						Toast.LENGTH_SHORT).show();
				return;
			}
			refData(lotSN);
			break;
		}

	}

	private void refData(String lotSN) {
		super.progressDialog.setMessage(getString(R.string.loadDataFromServer));
		showProDia();
		Task getLotSNImforTask = new Task(this, TaskType.BATNUM_INFOR, lotSN);
		// Task getLotSNImforTask = new Task(this,
		// TaskType.BATNUM_INFOR,"Mxxxxxx5");
		model.getLotSNImfor(getLotSNImforTask);

	}

}

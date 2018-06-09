package com.zowee.mes.activity;

import gnu.io.SerialPortEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FifoAdapter;
import com.zowee.mes.model.FifoModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

public class FifoScanActivity extends CommonActivity implements
		View.OnClickListener {

	private EditText edtSmalPacBarCode;
	private static final String TAG = "activity";
	private SerialPortEventListener serialLis;
	private FifoModel fifoModel;
	private ListView lvOldPacks;
	private TextView labFifoRes;
	private View headView;
	// private ProgressDialog progressDialog;
	private static String msg = null;
	private View btnGroups;
	private Button btnScan;
	private Button btnEnsure;
	private Button btnCancel;
	private Button btnRefresh;

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FIFO_SCAN;
		super.init();
		msg = getString(R.string.loadDataFromServer);
		super.progressDialog.setMessage(msg);

		btnGroups = this.findViewById(R.id.btnGroups);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnEnsure = (Button) btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);
		btnRefresh = (Button) btnGroups.findViewById(R.id.btn_refreshData);
		btnRefresh.setVisibility(View.GONE);
		edtSmalPacBarCode = (EditText) this
				.findViewById(R.id.edt_smalpac_barcode);
		fifoModel = new FifoModel();
		lvOldPacks = (ListView) this.findViewById(R.id.ls_oldpack_details);
		labFifoRes = (TextView) this.findViewById(R.id.lab_fifoRes);
		labFifoRes.setTextColor(Color.RED);
		headView = View.inflate(this, R.layout.fifo_adaper_view, null);
		((TextView) headView.findViewById(R.id.labBoxSn)).setText(this
				.getString(R.string.boxSn));
		((TextView) headView.findViewById(R.id.labLotSn)).setText(this
				.getString(R.string.lotSn));
		((TextView) headView.findViewById(R.id.labStockLocation)).setText(this
				.getString(R.string.stockLocation));
		lvOldPacks.addHeaderView(headView);
		lvOldPacks.setClickable(false);
		lvOldPacks.setFocusable(false);

		btnCancel.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnEnsure.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fifo_scan);
		init();

	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.FIFO_SCAN:
			// if(super.isNull) return;
			String lotSN = task.getTaskResult().toString().trim();
			this.edtSmalPacBarCode.setText(lotSN);
			refData(lotSN);
			// Task fifoScan = new Task(this,TaskType.FIFO_SCAN_DATA,lotSN);
			// msg = getString(R.string.loadDataFromServer);
			// super.progressDialog.setMessage(msg);
			// showProDia();
			// fifoModel.fifoScan(fifoScan);
			break;
		case TaskType.FIFO_SCAN_DATA:
			if (null != task.getTaskResult()) {
				Task viewFifoResTask = new Task(this, TaskType.VIEW_FIFO_RES,
						task.getTaskResult());
				// Log.i(TAG, task.getTaskResult().toString());
				msg = getString(R.string.loadDataForUI);
				progressDialog.setMessage(msg);
				fifoModel.viewFifoRes(viewFifoResTask);
			} else
				closeProDia();
			break;
		case TaskType.VIEW_FIFO_RES:
			closeProDia();
			labFifoRes.setText(this.getString(R.string.fifoRes));
			if (null != task.getTaskResult()) {
				List<HashMap<String, String>> fifoScanRes = (List<HashMap<String, String>>) task
						.getTaskResult();
				labFifoRes.setText(labFifoRes.getText().toString()
						+ fifoScanRes.size());
				FifoAdapter fifoAdapter = new FifoAdapter(fifoScanRes, this);
				lvOldPacks.setAdapter(fifoAdapter);
			} else {
				List<HashMap<String, String>> emptyFifoScanRes = new ArrayList<HashMap<String, String>>();
				lvOldPacks.setAdapter(new FifoAdapter(emptyFifoScanRes, this));
				labFifoRes.setText(labFifoRes.getText()
						+ this.getString(R.string.fifoScanLotSnError));
			}

			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_ensure:
			String lotSN = edtSmalPacBarCode.getText().toString().trim();
			if (StringUtils.isEmpty(lotSN)) {
				Toast.makeText(this, R.string.smallBag_notNull,
						Toast.LENGTH_SHORT).show();
				return;
			}
			refData(lotSN);
			break;
		case R.id.btn_cancel:
			super.clearWidget(edtSmalPacBarCode, null);
			List<HashMap<String, String>> emptyFifoScanRes = new ArrayList<HashMap<String, String>>();
			lvOldPacks.setAdapter(new FifoAdapter(emptyFifoScanRes, this));
			labFifoRes.setText("");
			break;
		}

	}

	private void refData(String lotSN) {
		Task fifoScan = new Task(this, TaskType.FIFO_SCAN_DATA, lotSN);
		msg = getString(R.string.loadDataFromServer);
		super.progressDialog.setMessage(msg);
		showProDia();
		fifoModel.fifoScan(fifoScan);
	}

}

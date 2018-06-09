package com.zowee.mes.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.adapter.FinishWareHouseCartoonAdapter;
import com.zowee.mes.adapter.FinishWareHouseMoAdapter;
import com.zowee.mes.model.FinishWareHouseCardModel;
import com.zowee.mes.model.FinishWareHouseCattonModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Administrator
 * @description 卡板成品入仓
 */
public class FinishWareHouseCardActivity extends CommonActivity implements
		View.OnClickListener, AdapterView.OnItemSelectedListener,
		View.OnFocusChangeListener {

	private Spinner spinFinishNum;// 完工入库单号
	private Spinner spinMOName;// 工单名称
	private EditText edtSOName;// 订单名称
	private EditText edtCartBoxSN;// 卡通箱条码
	private Button btnEnsure;// 卡板成品入仓
	private Button btnCancel;
	private Button btnScan;
	private String moName;
	private String productId;
	private FinishWareHouseCardModel model;
	private String moId;// 工单ID
	private String productCompleteId;// 完工入库单ID
	private String completeDocNo;// 完工入库单名
	private EditText edtOperDetails;
	private View btnGroups;
	private List<HashMap<String, String>> spinLis = new ArrayList<HashMap<String, String>>();
	private static final String PRODUCTCOMPLETEID = "ProductCompleteID";
	private static final String COMPLETEDOCNO = "CompleteDocNo";
	private List<HashMap<String, String>> spinLisMoNames = new ArrayList<HashMap<String, String>>();
	private static final String MONAME = "MOName";
	private static final String MOID = "MOId";
	private static final String PRODUCTID = "ProductId";
	private static final String SONAME = "SOName";
	private Button btnRefreshData;
	private Dialog dialog;
	private View diaView;
	private Button btnCheck;
	private Button btnAbolish;
	private EditText edtCheckStr;

	@Override
	public void init() {
		model = new FinishWareHouseCardModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.FINISHWAREHOUSECARD_SCAN;
		super.init();
		this.spinFinishNum = (Spinner) this
				.findViewById(R.id.spin_finished_warehouse_num);
		this.spinMOName = (Spinner) this.findViewById(R.id.spin_workorder_name);
		this.edtCartBoxSN = (EditText) this.findViewById(R.id.edt_cardnum);
		this.edtSOName = (EditText) this.findViewById(R.id.edt_ordername);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnEnsure = (Button) btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnRefreshData = (Button) btnGroups.findViewById(R.id.btn_refreshData);
		btnRefreshData.setText(R.string.advance);
		edtOperDetails = (EditText) findViewById(
				R.id.common_edtView_operateDetails).findViewById(
				R.id.edt_sysdetail);
		dialog = new Dialog(this, R.style.check_completenos_dialog);
		diaView = View.inflate(this, R.layout.check_completenos, null);
		dialog.setContentView(diaView);
		btnCheck = (Button) diaView.findViewById(R.id.btn_Check);
		btnAbolish = (Button) diaView.findViewById(R.id.btn_abolish);
		edtCheckStr = (EditText) diaView.findViewById(R.id.edt_checkStr);

		btnEnsure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnRefreshData.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		btnAbolish.setOnClickListener(this);
		edtCartBoxSN.setOnFocusChangeListener(this);
		spinFinishNum.setOnItemSelectedListener(this);
		spinMOName.setOnItemSelectedListener(moAdapterLis);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.finish_warehouse_card);
		init();

	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.FINISHWAREHOUSECARD_SCAN:
			if (super.isNull)
				return;
			String boxSN = task.getTaskResult().toString();
			// String boxSN = "Cxxxxxx1";
			edtCartBoxSN.setText(boxSN);
			// refData(boxSN);
			// super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
			// showProDia();
			// Task getParamsTask = new Task(this,
			// TaskType.FINISHWAREHOUSECARD_GETPARAMS, boxSN);
			// model.getNecesParams(getParamsTask);
			break;
		case TaskType.FINISHWAREHOUSECARD_GETCOMPLETENAMES:
			closeProDia();
			if (dialog.isShowing())
				dialog.dismiss();
			if (super.isNull)
				return;
			spinLis = (List<HashMap<String, String>>) task.getTaskResult();
			// if(null!=params[1]&&!StringUtils.isEmpty(params[1].toString()))
			// this.edtSOName.setText(params[1].toString());
			// spinLisMoNames = (List<HashMap<String,String>>)params[0];
			// = completeNames;
			if (0 == spinLis.size()) {
				productCompleteId = "";
				completeDocNo = "";
				moId = "";
				moName = "";
				if (null != spinLisMoNames)
					spinLisMoNames.clear();
				clearWidget(spinMOName, null);
				clearWidget(spinFinishNum, null);
				clearWidget(edtSOName, null);
				return;
			}
			FinishWareHouseCartoonAdapter adapter = new FinishWareHouseCartoonAdapter(
					this, spinLis);
			spinFinishNum.setAdapter(adapter);
			break;
		case TaskType.FINISHWAREHOUSECARD:
			closeProDia();
			if (super.isNull)
				return;
			String[] reses = (String[]) task.getTaskResult();
			String res = reses[0];
			String logo = reses[1];
			StringBuffer sbf = new StringBuffer(edtOperDetails.getText()
					.toString());
			if (!StringUtils.isEmpty(res) && res.trim().equals("0"))
				sbf.append(getString(R.string.finishWarehouseCard_succ) + "\n");
			else
				sbf.append(getString(R.string.finishWarehouseCard_fail) + "\n");
			if (StringUtils.isEmpty(logo))
				logo = "";
			sbf.append(logo + "" + "\n\n");
			edtOperDetails.setText(sbf.toString());
			break;
		case TaskType.FINISHWAREHOUSECARD_MONAME:
			closeProDia();
			if (super.isNull)
				return;
			spinLisMoNames = (List<HashMap<String, String>>) task
					.getTaskResult();
			if (0 == spinLisMoNames.size()) {
				moId = "";
				moName = "";
				clearWidget(spinMOName, null);
				clearWidget(edtSOName, null);
				return;
			}
			FinishWareHouseMoAdapter adapter_1 = new FinishWareHouseMoAdapter(
					this, spinLisMoNames);
			spinMOName.setAdapter(adapter_1);
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ensure:// 卡通箱成品入仓
			boolean isPass = subWeigdet();
			if (!isPass)
				return;
			String[] params = new String[] { "", "", "", "", "", "" };
			params[0] = moId.trim();
			params[1] = moName.trim();
			params[2] = edtCartBoxSN.getText().toString().trim();
			params[3] = edtSOName.getText().toString().trim();
			params[4] = productCompleteId.trim();
			params[5] = completeDocNo.trim();
			Task task = new Task(this, TaskType.FINISHWAREHOUSECARD, params);
			super.progressDialog.setMessage(getString(R.string.TransationData));
			showProDia();
			model.finishWareHouseCard(task);
			break;
		case R.id.btn_cancel:
			restoreUIData();
			break;
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_refreshData:
			// String boxSN = edtCartBoxSN.getText().toString().trim();
			// if(StringUtils.isEmpty(boxSN))
			// {
			// Toast.makeText(this, R.string.cartoonbox_notnull,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			// refData(boxSN);
			dialog.show();
			break;
		case R.id.btn_abolish:
			dialog.dismiss();
			break;
		case R.id.btn_Check:
			String checkStr = edtCheckStr.getText().toString().trim();
			getCompleteNames(checkStr);
			break;
		}

	}

	private boolean subWeigdet() {
		boolean isPass = true;

		isPass = super.subView(edtCartBoxSN);
		if (!isPass) {
			Toast.makeText(this, getString(R.string.cartoonBoxSN_notNull), 1500)
					.show();
			return isPass;
		}

		if (StringUtils.isEmpty(completeDocNo)) {
			isPass = false;
			Toast.makeText(this, R.string.completeNo_notnull, 1500).show();// 完工入库
			return isPass;
		}

		// isPass = !StringUtils.isEmpty(productCompleteId);
		if (StringUtils.isEmpty(moName)) {
			isPass = false;
			Toast.makeText(this, R.string.moName_isnull, 1500).show();// 工单
			return isPass;
		}

		isPass = super.subView(edtSOName);
		if (!isPass) {
			Toast.makeText(this, R.string.soName_notnull, 1500).show();
			return isPass;
		}

		if (StringUtils.isEmpty(productCompleteId) || StringUtils.isEmpty(moId)) {
			isPass = false;
			Toast.makeText(this, R.string.innerData_error, 1500).show();
			return isPass;
		}

		return isPass;
	}

	private void restoreUIData() {
		moId = "";
		moName = "";
		productCompleteId = "";
		completeDocNo = "";
		// productId = "";
		super.clearWidget(spinFinishNum, null);
		super.clearWidget(spinMOName, null);
		if (null != spinLis)
			spinLis.clear();
		if (null != spinLisMoNames)
			spinLisMoNames.clear();
		super.clearWidget(edtSOName, null);
		super.clearWidget(edtCartBoxSN, null);

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (null == spinLis || 0 == spinLis.size())
			return;

		HashMap<String, String> selItem = spinLis.get(arg2);

		productCompleteId = selItem.get(PRODUCTCOMPLETEID);
		completeDocNo = selItem.get(COMPLETEDOCNO);
		getMoNames(productCompleteId);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.edt_cardnum:
			EditText edtTempCartoonBox = (EditText) v;
			if (edtTempCartoonBox.isFocused()
					&& StringUtils.isEmpty(edtTempCartoonBox.getText()
							.toString()))
				edtOperDetails.setText(edtOperDetails.getText()
						+ getString(R.string.please_scan) + " "
						+ getString(R.string.catton_num) + "\n");
			break;
		}

	}

	AdapterView.OnItemSelectedListener moAdapterLis = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (null == spinLisMoNames)
				return;
			HashMap<String, String> selItem = spinLisMoNames.get(arg2);
			moId = selItem.get(MOID);
			moName = selItem.get(MONAME);
			edtSOName.setText(selItem.get(SONAME));
			// = selItem.get(PRODUCTID);
			// if(StringUtils.isEmpty(moId)||StringUtils.isEmpty(productId))
			// return;
			// String[] params = new String[2];
			// params[0] = moId;
			// params[1] = productId;
			// progressDialog.setMessage(getString(R.string.loadAppNecessData));
			// showProDia();
			// Task task = new
			// Task(FinishWareHouseCardActivity.this,TaskType.FINISHWAREHOUSECARD_DOCNOS,params);
			// model.getCompleteDocNos(task);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private void getCompleteNames(String checkStr) {
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task getParamsTask = new Task(this,
				TaskType.FINISHWAREHOUSECARD_GETCOMPLETENAMES, checkStr);
		model.getCompleteDocNos(getParamsTask);

	}

	/**
	 * @param completeId
	 *            获取工单名称 ID 以及订单名
	 */
	private void getMoNames(String completeId) {
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		Task task = new Task(this, TaskType.FINISHWAREHOUSECARD_MONAME,
				completeId);
		model.getNecesParams(task);
	}

}

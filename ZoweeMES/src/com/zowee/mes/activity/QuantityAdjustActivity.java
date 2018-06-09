package com.zowee.mes.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.model.QuantityAdjustModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description 批号数量调整
 */
public class QuantityAdjustActivity extends CommonActivity implements
		View.OnClickListener, View.OnFocusChangeListener {
	private EditText edtLotSN;
	private EditText edtAdjustNum;
	// private RadioGroup radioGroup;
	private QuantityAdjustModel model;
	private Button btnEnsure;
	private Button btnCancel;
	private Button btnScan;
	private View btnGroups;
	private View comEditLay;
	private EditText edtSysDetail;
	private TextView labPlus;// 加
	private TextView labReduce;// 减
	private Button btnRefData;

	@Override
	public void init() {
		model = new QuantityAdjustModel();
		super.TASKTYPE = TaskType.QUANTITY_ADJUST_SCAN;
		super.commonActivity = this;
		super.init();
		edtLotSN = (EditText) this.findViewById(R.id.edt_batnum);
		edtAdjustNum = (EditText) this.findViewById(R.id.edt_quantity_adjust);
		// radioGroup =
		// (RadioGroup)this.findViewById(R.id.radioGroup_plusreduce);
		// radioGroup.setOnCheckedChangeListener(this);
		labPlus = (TextView) this.findViewById(R.id.labPlus);
		labReduce = (TextView) this.findViewById(R.id.labReduce);
		btnGroups = this.findViewById(R.id.btnGroups);
		btnEnsure = (Button) btnGroups.findViewById(R.id.btn_ensure);
		btnCancel = (Button) btnGroups.findViewById(R.id.btn_cancel);
		btnScan = (Button) btnGroups.findViewById(R.id.btn_scan);
		btnRefData = (Button) btnGroups.findViewById(R.id.btn_refreshData);
		comEditLay = this.findViewById(R.id.common_edtView_operateDetails);
		edtSysDetail = (EditText) comEditLay.findViewById(R.id.edt_sysdetail);
		edtAdjustNum.setText("1.0");// 初始化数量

		labPlus.setOnClickListener(this);
		labReduce.setOnClickListener(this);
		btnEnsure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		edtLotSN.setOnFocusChangeListener(this);
		btnRefData.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.quantity_adjust);
		init();

	}

	@Override
	public void refresh(Task task) {
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.QUANTITY_ADJUST_SCAN:
			if (super.isNull)
				return;
			String lotSN = task.getTaskResult().toString().trim();
			// String lotSN = "Mxxxxxx6";
			this.edtLotSN.setText(lotSN);
			refData(lotSN);
			// Task getLotQtyTask = new Task(this,
			// TaskType.GETLOTSNQTY,this.edtLotSN.getText().toString());
			// Task getLotQtyTask = new Task(this, TaskType.GETLOTSNQTY,lotSN);
			// super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
			// showProDia();
			// model.getLotSNOldQuantity(getLotQtyTask);//获取批号数量
			// model.getLotSNOldQuantity(null);
			break;
		case TaskType.GETLOTSNQTY:
			closeProDia();
			if (super.isNull) {
				edtAdjustNum.setText("0.0");
				return;
			}
			String lotQty = task.getTaskResult().toString().trim();
			lotQty = StringUtils.conDeciStr(lotQty, 1);
			this.edtAdjustNum.setText(lotQty);
			break;
		case TaskType.QUANTITYADJUST:
			closeProDia();
			if (super.isNull)
				return;
			String[] reses = (String[]) task.getTaskResult();
			String res = reses[0];
			String retInfor = reses[1];
			// if(StringUtils.isEmpty(res)||StringUtils.isEmpty(retInfor))
			// return;
			StringBuffer sbf = new StringBuffer(this.edtSysDetail.getText()
					.toString());
			if (!StringUtils.isEmpty(res) && res.trim().equals("0"))
				sbf.append(getString(R.string.quantityAdjust_suc) + "\n");
			else
				sbf.append(getString(R.string.quantityAdjust_fail) + "\n");
			if (StringUtils.isEmpty(retInfor))
				retInfor = "";
			sbf.append(retInfor + "\n\n");
			edtSysDetail.setText(sbf.toString());
			break;

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ensure:
			boolean isPass = subQuantityAdjust();
			if (!isPass)
				return;
			super.progressDialog.setMessage(getString(R.string.TransationData));
			showProDia();
			String[] taskDatas = new String[] {
					this.edtLotSN.getText().toString().trim(),
					this.edtAdjustNum.getText().toString().trim() };
			Task task = new Task(this, TaskType.QUANTITYADJUST, taskDatas);
			model.quantityAdjust(task);
			break;
		case R.id.labPlus:
			changeQuantity(v);
			break;
		case R.id.labReduce:
			changeQuantity(v);
			break;
		case R.id.btn_cancel:
			// Task testTask = null;
			restoreUIData();
			break;
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_refreshData:
			String lotSN = edtLotSN.getText().toString().trim();
			if (StringUtils.isEmpty(lotSN)) {
				Toast.makeText(this, R.string.adjustLot_notNull,
						Toast.LENGTH_SHORT).show();
				return;
			}
			refData(lotSN);
			break;
		}

	}

	private void changeQuantity(View v) {
		if (StringUtils.isEmpty(this.edtAdjustNum.getText().toString()))
			this.edtAdjustNum.setText("0.0");
		float qty = Float.valueOf(this.edtAdjustNum.getText().toString());
		if (R.id.labPlus == v.getId())
			qty += 1f;
		else if (R.id.labReduce == v.getId())
			qty -= 1f;
		if (0 >= qty)
			Toast.makeText(this, R.string.MetrialQtyNoLowZero,
					Toast.LENGTH_SHORT).show();
		else
			this.edtAdjustNum.setText("" + qty);
	}

	/**
	 * @description 对拆分出库的界面数据进行有效性验证
	 */
	private boolean subQuantityAdjust() {
		boolean isPass = false;
		isPass = subView(edtLotSN);
		if (!isPass) {
			Toast.makeText(this, R.string.adjustLot_notNull, Toast.LENGTH_SHORT)
					.show();
			return isPass;
		}

		isPass = subView(edtAdjustNum);
		if (!isPass) {
			Toast.makeText(this, R.string.adjustNum_notNull, Toast.LENGTH_SHORT)
					.show();
			return isPass;
		}
		if (!StringUtils.isNumberal(edtAdjustNum.getText().toString().trim())) {
			isPass = false;
			Toast.makeText(
					this,
					getString(R.string.adjust_amount)
							+ getString(R.string.inputData_notLegal),
					Toast.LENGTH_SHORT).show();
			return isPass;
		}
		double adjustNum = Double.valueOf(edtAdjustNum.getText().toString()
				.trim());
		if (1 > adjustNum) {
			isPass = false;
			Toast.makeText(this, R.string.MetrialQtyNoLowZero,
					Toast.LENGTH_SHORT).show();
			return isPass;
		}

		return isPass;
	}

	private void restoreUIData() {
		super.clearWidget(edtLotSN, null);
		super.clearWidget(edtAdjustNum, "1.0");

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.edt_batnum:
			EditText edtBatNum = (EditText) v;
			if (edtBatNum.isFocused()
					&& StringUtils.isEmpty(edtBatNum.getText().toString())) {
				edtSysDetail.setText(edtSysDetail.getText().toString()
						+ getString(R.string.adjust_scanLotSN) + "\n\n");
				return;
			}
			break;
		}

	}

	private void refData(String lotSN) {
		Task getLotQtyTask = new Task(this, TaskType.GETLOTSNQTY, lotSN);
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		model.getLotSNOldQuantity(getLotQtyTask);// 获取批号数量

	}

}

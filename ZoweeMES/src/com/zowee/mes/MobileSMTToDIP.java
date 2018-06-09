package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DIPMobileDipStartModel;
import com.zowee.mes.model.DIPStartModel;
import com.zowee.mes.model.GetMOnameModel;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MobileSMTToDIP extends CommonActivity implements
		View.OnClickListener, AdapterView.OnItemSelectedListener {

	private EditText EditmobileDipKeyword;
	private EditText EditmobileDipProductNumber;
	private EditText EditmobileDipProductdescript;
	private EditText EditmobileDipWorkflowname;
	private EditText EditmobileDipOrderNumber;
	private EditText EditmobileDipCustomerorder;
	private EditText EditmobileDipStartSN;
	private EditText EditScanInfo;
	private EditText EditScannedquality;
	private String snkeyWord = "N";
	private Spinner spinmobileDipMO;
	private Spinner spinmobileDipshift;
	private Spinner spinmobileDipSNlength;
	private static String msg = null;
	private GetMOnameModel GetMoModel;
	private DIPStartModel DIPStartmodel;
	private DIPMobileDipStartModel mobileSMTDipmodel;
	private String moName;
	private String MOID;
	private String ProductId;
	private String WorkFlowId;
	private String LotSN;
	private String WorkShift;
	private String ShiftID = "";
	private int SNLength = 8;

	private static final String MONAME = "MOName";
	String[] Params = new String[] { "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "" };
	private List<HashMap<String, String>> moNames;// ���������������Դ
	private String ResourceID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile_smtto_dip);
		this.setTitleColor(Color.GREEN);
		this.setTitle("�ֻ�SMTת��װ����");
		init();

		logDetails(EditScanInfo, "����SN���Ⱥ�����±༭SNǰ׺�ؽ���");
		logDetails(EditScanInfo, "���������������빤���ſ�ʵ���Զ�ѡ�񹤵�");
		logDetails(EditScanInfo, "ʹ��˵����");
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.DIP_GetMoinfo;
		super.init();
		msg = "Dip Start";
		super.progressDialog.setMessage(msg);
		GetMoModel = new GetMOnameModel();
		DIPStartmodel = new DIPStartModel();
		mobileSMTDipmodel = new DIPMobileDipStartModel();
		spinmobileDipSNlength = (Spinner) findViewById(R.id.spin_mobileDipSNlength);
		EditmobileDipKeyword = (EditText) findViewById(R.id.Edit_mobileDipKeyword);
		EditmobileDipProductNumber = (EditText) findViewById(R.id.Edit_mobileDipProductNumber);
		EditmobileDipProductdescript = (EditText) findViewById(R.id.Edit_mobileDipProductdescript);
		EditmobileDipWorkflowname = (EditText) findViewById(R.id.Edit_mobileDipWorkflowname);
		EditmobileDipOrderNumber = (EditText) findViewById(R.id.Edit_mobileDipOrderNumber);
		EditmobileDipCustomerorder = (EditText) findViewById(R.id.Edit_mobileDipCustomerorder);
		EditmobileDipStartSN = (EditText) findViewById(R.id.Edit_mobileDipStartSN);
		EditScanInfo = (EditText) findViewById(R.id.edt_sysdetail);
		spinmobileDipMO = (Spinner) findViewById(R.id.spin_mobileDipMO);
		spinmobileDipshift = (Spinner) findViewById(R.id.spin_mobileDipshift);
		EditScannedquality = (EditText) findViewById(R.id.Edit_Scannedquality);

		spinmobileDipSNlength.setOnItemSelectedListener(this);
		EditmobileDipKeyword.setOnClickListener(this);
		EditmobileDipStartSN.setOnClickListener(this);
		spinmobileDipMO.setOnItemSelectedListener(this);
		spinmobileDipshift.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapterWorkShift = ArrayAdapter
				.createFromResource(this, R.array.Workshift,
						android.R.layout.simple_spinner_item);
		adapterWorkShift
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinmobileDipshift.setAdapter(adapterWorkShift);

		ArrayAdapter<CharSequence> adapterSNlength = ArrayAdapter
				.createFromResource(this, R.array.adapterSNlength,
						android.R.layout.simple_spinner_item);
		adapterSNlength
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinmobileDipSNlength.setAdapter(adapterSNlength);

		getDIPMONames();
		GetResourceId();

	}

	private void getDIPMONames() {
		super.progressDialog.setMessage("Get DIP MO Names");
		super.showProDia();
		String MOSTDType = "P";
		Task task = new Task(this, TaskType.GetMoNameByMOSTDType, MOSTDType);
		GetMoModel.getMoNamesByMOSTdType(task);
	}

	private void GetStartDIPMOinfo(String MOName) {
		super.progressDialog.setMessage("Get DIP MO info");
		super.showProDia();
		Task task = new Task(this, TaskType.DIP_GetMoinfo, MOName);
		DIPStartmodel.GetStartDIPMOinfo(task);
	}

	private void GetResourceId() {
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();
		Task task = new Task(this, TaskType.GetResourceId, MyApplication
				.getAppOwner().toString().trim());
		GetMoModel.GetResourceId(task);

	}

	@SuppressWarnings("unchecked")
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
				clearWidget(spinmobileDipMO, null);
				if (null != moNames)
					moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String, String>>) task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			spinmobileDipMO.setAdapter(adapter);
			break;
		case TaskType.DIP_GetMoinfo:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				ClearAll();
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			EditmobileDipStartSN.setText("");
			MOID = getdata.get("MOId");
			EditmobileDipProductNumber.setText(getdata.get("ProductName"));
			EditmobileDipProductdescript.setText(getdata
					.get("ProductDescription"));
			ProductId = getdata.get("ProductId");
			WorkFlowId = getdata.get("WorkflowId");
			EditmobileDipWorkflowname.setText(getdata.get("WorkflowName"));
			EditmobileDipOrderNumber.setText(getdata.get("SOName"));
			EditmobileDipCustomerorder.setText(getdata.get("CustMO"));
			logDetails(EditScanInfo, "Success_Msg:" + getdata.toString());

			break;
		case TaskType.GetResourceId:
			ResourceID = "";
			List<HashMap<String, String>> getresult = (List<HashMap<String, String>>) task
					.getTaskResult();
			if (super.isNull || 0 == (getresult).size()) {
				return;
			}
			getresult = (List<HashMap<String, String>>) task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if (ResourceID.isEmpty())
				logDetails(EditScanInfo, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			break;
		case TaskType.Dip_mobileStart:
			super.closeProDia();
			this.EditmobileDipStartSN.setText("");
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				logDetails(EditScanInfo, "��MES������Ϣ��");
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				this.EditScannedquality.setText(getdata.get("ReturnMsg")
						.toString().replaceAll(".{10,}��ǰ��ɨ������: ", ""));
				logDetails(EditScanInfo,
						"Success_Msg:"
								+ getdata.get("ReturnMsg").toString()
										.replaceFirst("ServerMessage:", "")
								+ "\r\n");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
			} else {
				logDetails(EditScanInfo, getdata.get("ReturnMsg").toString()
						+ ";" + getdata.get("exception").toString() + ": "
						+ LotSN);
			}
			break;
		}
	}

	private void ClearAll() {
		EditmobileDipProductdescript.setText("");
		EditmobileDipWorkflowname.setText("");
		EditmobileDipStartSN.setText("");
		EditmobileDipProductNumber.setText("");
		EditmobileDipOrderNumber.setText("");
		EditmobileDipCustomerorder.setText("");
		LotSN = "";
		MOID = "";
		WorkFlowId = "";
		ProductId = "";
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.spin_mobileDipMO:
			if (null == moNames || 0 == moNames.size())
				return;
			moName = moNames.get(arg2).get(MONAME);
			GetStartDIPMOinfo(moName);
			if (this.EditScanInfo.getLineCount() > 5)
				this.EditScanInfo.setText("");

			break;
		case R.id.spin_mobileDipshift:
			WorkShift = this.spinmobileDipshift.getSelectedItem().toString();
			if (WorkShift.contains("�װ�"))
				ShiftID = "SHF10000000C";
			else if (WorkShift.contains("�װ�"))
				ShiftID = "SHF10000000D";
			break;
		case R.id.spin_mobileDipSNlength:
			SNLength = 8 + spinmobileDipSNlength.getSelectedItemPosition();
			EditmobileDipKeyword.setText("");
			EditmobileDipKeyword.setEnabled(true);
			break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Edit_mobileDipStartSN:
			LotSN = EditmobileDipStartSN.getText().toString().trim()
					.toUpperCase();
			if (StringUtils.isScannedMONumber(LotSN)) {
				String GetItem;
				for (int i = 0; i < spinmobileDipMO.getAdapter().getCount(); i++) {
					GetItem = ((HashMap<String, String>) (this.spinmobileDipMO
							.getAdapter().getItem(i))).get(MONAME).toString();
					if (GetItem.equals(LotSN)) {
						spinmobileDipMO.setSelection(i);
						break;
					}
					if (i == spinmobileDipMO.getAdapter().getCount() - 1)// û���ҵ�ɨ��Ĺ����š�
					{
						Toast.makeText(this, "��ȷ������Ĺ������Ƿ���ȷ", 5).show();
					}
				}
				EditmobileDipStartSN.setText("");
			} else {
				StartDIPLotSN(LotSN);
				if (EditScanInfo.getLineCount() > 50)
					EditScanInfo.setText("");
			}
			break;
		case R.id.Edit_mobileDipKeyword:
			if (EditmobileDipKeyword.getText().toString().trim().length() >= 1) {
				snkeyWord = EditmobileDipKeyword.getText().toString().trim();
				EditmobileDipKeyword.setEnabled(false);
			}
			break;
		}
	}

	private void StartDIPLotSN(String lotSN) {
		Params[0] = lotSN;
		Params[1] = MOID;
		Params[2] = WorkFlowId;
		Params[3] = EditmobileDipProductNumber.getText().toString().trim();
		Params[4] = EditmobileDipWorkflowname.getText().toString().trim();
		Params[5] = ProductId;
		Params[6] = MyApplication.getAppOwner().toString().trim();
		Params[7] = ResourceID;
		Params[8] = moName;
		Params[9] = snkeyWord;
		Params[10] = SNLength + "";
		Params[11] = ShiftID;
		Params[12] = WorkShift;
		Params[13] = EditmobileDipOrderNumber.getText().toString().trim();
		Params[14] = EditmobileDipCustomerorder.getText().toString().trim();
		if (lotSN.trim().length() == 0)
			return;

		if (lotSN.trim().length() < 8) {
			Toast.makeText(this, "���������С��8λ", 5).show();
			return;
		}
		if (Params[1].isEmpty() || Params[2].isEmpty() || Params[3].isEmpty()
				|| Params[4].isEmpty() || Params[5].isEmpty()) {

			logDetails(EditScanInfo, "DIP���������������Ϊ��");
			Toast.makeText(this, "������ѡ�񹤵�", 5).show();
			return;
		}
		if (Params[6].isEmpty()) {
			logDetails(EditScanInfo, "�����趨�豸��������");
			Toast.makeText(this, "�����趨�豸��������", 5).show();
			return;
		}
		if (Params[7].isEmpty()) {
			logDetails(EditScanInfo, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			Toast.makeText(this, "��ԴID����Ϊ��", 5).show();
			return;
		}
		if (Params[10].isEmpty()) {
			logDetails(EditScanInfo, "��ѡ��SN���� ");
			Toast.makeText(this, "SN����û��ѡ��", 5).show();
			return;
		}
		if (Params[12].isEmpty()) {
			logDetails(EditScanInfo, "��ѡ���� ");
			Toast.makeText(this, "���û��ѡ��", 5).show();
			return;
		}
		super.progressDialog.setMessage("�ֻ�SMTת��װ����");
		super.showProDia();
		Task task = new Task(this, TaskType.Dip_mobileStart, Params);

		mobileSMTDipmodel.StartMobileDIP(task);

	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("ȷ���˳��ֻ�SMTת��װ������?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(MobileSMTToDIP.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
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

}

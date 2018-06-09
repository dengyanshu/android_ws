package com.zowee.mes;

import java.util.HashMap;
import java.util.List;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.adapter.SNTypeAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DIPStartModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StartDip extends CommonActivity implements View.OnClickListener,
		AdapterView.OnItemSelectedListener {

	private EditText EditProductDescrip;
	private EditText EditWorkFlow;
	private EditText EditDIPStartSN;
	private EditText EditScanInfo;
	private EditText EditDIPProductName;
	private EditText EditScannedQuantity;
	private EditText EditSNKeyWord;
	private Spinner spinStartType;
	private boolean manualChanged = false;
	private int SNLength = 1;
	private String SNType = "";
	private String snkeyWord = "N";
	private Spinner spinSNLength;
	private Spinner SpinSNType;
	private TextView TextSNType;
	private TextView labScannedQuantity;
	private LinearLayout Linearlab;
	private LinearLayout Linearedit;
	private String ResourceID = "";
	private String LotSN;
	private String MOID;
	private String WorkFlowId;
	private String ProductId;
	private Spinner spiDIPMOID;
	private GetMOnameModel GetMoModel;
	private String moName;
	private static final String MONAME = "MOName";
	private List<HashMap<String, String>> moNames;// ���������������Դ
	private static String msg = null;
	private DIPStartModel DIPStartmodel;
	private List<HashMap<String, String>> SNTypes;// SN���������������Դ
	String[] Params = new String[] { "", "", "", "", "", "", "", "", "", "",
			"", "" };
	private int DIPStartType = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_dip);
		Intent intent = getIntent();
		DIPStartType = intent.getIntExtra("StartDIPType", 1);
		this.setTitleColor(Color.GREEN);
		init();
		if (DIPStartType == 1) // SMT to DIP
			this.setTitle("DIP ��������");
		else if (DIPStartType == 2)
			this.setTitle("SMTת DIP ��������");
		else if (DIPStartType == 3) {
			this.setTitle("ί��DIP ��������");
			spinStartType.setSelection(0);
		} else if (DIPStartType == 4)
			this.setTitle("���Ͽ��ϣ����ݲɼ���");

		if (DIPStartType != 3) {
			TextSNType.setVisibility(View.INVISIBLE);
			SpinSNType.setVisibility(View.INVISIBLE);
			Linearlab.setVisibility(View.GONE);
			Linearedit.setVisibility(View.GONE);
		}
		if (DIPStartType == 4) {

			EditScannedQuantity.setVisibility(View.GONE);
			labScannedQuantity.setVisibility(View.GONE);
		}
		if (DIPStartType == 3) {
			logDetails(EditScanInfo, "����SN���ͺ�����±༭SNǰ׺�ؽ���");
			logDetails(EditScanInfo, "����Ҳ�����Ӧ�����������������������");
		}
		logDetails(EditScanInfo, "���������������빤���ſ�ʵ���Զ�ѡ�񹤵�");
		logDetails(EditScanInfo, "ʹ��˵����");
		getDIPMONames();
		GetDipSNType();
		GetResourceId();
		manualChanged = true;

	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.DIP_GetMoinfo;
		super.init();
		msg = "Dip Start";
		super.progressDialog.setMessage(msg);
		DIPStartmodel = new DIPStartModel();
		GetMoModel = new GetMOnameModel();
		EditScannedQuantity = (EditText) findViewById(R.id.Edit_ScannedQuantity);
		EditProductDescrip = (EditText) findViewById(R.id.Edit_ProductDescrip);
		EditWorkFlow = (EditText) findViewById(R.id.Edit_WorkFlow);
		EditDIPStartSN = (EditText) findViewById(R.id.Edit_DIPStartSN);
		EditScanInfo = (EditText) findViewById(R.id.edt_sysdetail);
		spiDIPMOID = (Spinner) findViewById(R.id.Spin_DIPMOID);
		EditDIPProductName = (EditText) findViewById(R.id.Edit_DIPProductID);
		labScannedQuantity = (TextView) findViewById(R.id.lab_StartDIPOut);
		spiDIPMOID.setOnItemSelectedListener(this);
		EditDIPStartSN.setOnClickListener(this);

		spinStartType = (Spinner) findViewById(R.id.spin_StartType);
		EditSNKeyWord = (EditText) findViewById(R.id.Edit_SNKeyWord);
		spinSNLength = (Spinner) findViewById(R.id.spin_SNLength);
		SpinSNType = (Spinner) findViewById(R.id.Spin_SNType);
		TextSNType = (TextView) findViewById(R.id.Text_SNType);
		Linearlab = (LinearLayout) findViewById(R.id.Linear_lab);
		Linearedit = (LinearLayout) findViewById(R.id.Linear_edit);

		SpinSNType.setOnItemSelectedListener(this);
		spinSNLength.setOnItemSelectedListener(this);
		EditSNKeyWord.setOnClickListener(this);
		spinStartType.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> adapterSNlength = ArrayAdapter
				.createFromResource(this, R.array.adapterSNlength,
						android.R.layout.simple_dropdown_item_1line);
		adapterSNlength
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinSNLength.setAdapter(adapterSNlength);

		ArrayAdapter<CharSequence> adapterSellType = ArrayAdapter
				.createFromResource(this, R.array.DIP_StartType,
						android.R.layout.simple_dropdown_item_1line);
		spinStartType.setAdapter(adapterSellType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Edit_DIPStartSN:
			LotSN = EditDIPStartSN.getText().toString().trim().toUpperCase();
			if (StringUtils.isScannedMONumber(LotSN)) {
				String GetItem;
				for (int i = 0; i < spiDIPMOID.getAdapter().getCount(); i++) {
					GetItem = ((HashMap<String, String>) (this.spiDIPMOID
							.getAdapter().getItem(i))).get(MONAME).toString();
					if (GetItem.equals(LotSN)) {
						spiDIPMOID.setSelection(i);
						break;
					}
					if (i == spiDIPMOID.getAdapter().getCount() - 1)// û���ҵ�ɨ��Ĺ����š�
					{
						Toast.makeText(this, "��ȷ������Ĺ������Ƿ���ȷ", 5).show();
					}
				}
				EditDIPStartSN.setText("");
			} else {
				StartDIPLotSN(LotSN);
				if (this.EditScanInfo.getLineCount() > 100)
					this.EditScanInfo.setText("");
			}
			break;
		case R.id.Edit_SNKeyWord:
			if (EditSNKeyWord.getText().toString().trim().length() >= 1) {
				snkeyWord = EditSNKeyWord.getText().toString().trim();
				EditSNKeyWord.setEnabled(false);
			}
			break;
		}
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
				clearWidget(spiDIPMOID, null);
				if (null != moNames)
					moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String, String>>) task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			spiDIPMOID.setAdapter(adapter);
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
			EditDIPStartSN.setText("");
			MOID = getdata.get("MOId");
			EditDIPProductName.setText(getdata.get("ProductName"));
			EditProductDescrip.setText(getdata.get("ProductDescription"));
			ProductId = getdata.get("ProductId");
			WorkFlowId = getdata.get("WorkflowId");
			EditWorkFlow.setText(getdata.get("WorkflowName"));
			logDetails(EditScanInfo, "Success_Msg:" + getdata.toString());
			break;
		case TaskType.Dip_StartDIP:
			super.closeProDia();
			EditDIPStartSN.setText("");
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				logDetails(EditScanInfo, "��MES������Ϣ��");
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				EditScannedQuantity.setText(getdata.get("ReturnMsg").toString()
						.replaceAll(".{10,}��ǰ��ɨ������: ", ""));
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
		case TaskType.GetDipSNType:
			SNType = "";
			if (super.isNull
					|| 0 == ((List<HashMap<String, String>>) task
							.getTaskResult()).size()) {
				clearWidget(SpinSNType, null);
				if (null != SNTypes)
					SNTypes.clear();
				return;
			}
			SNTypes = (List<HashMap<String, String>>) task.getTaskResult();
			SNTypeAdapter adapter2 = new SNTypeAdapter(this, SNTypes);
			SpinSNType.setAdapter(adapter2);
			logDetails(EditScanInfo, "Success_Msg:" + "��ȡSN������Ϣ�ɹ�");
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
		}
	}

	private void getDIPMONames() {
		super.progressDialog.setMessage("Get DIP MO Names");
		super.showProDia();
		String MOSTDType = "D";
		if (DIPStartType == 3) {
			if (spinStartType.getSelectedItemPosition() == 0)
				MOSTDType = "P";
			else
				MOSTDType = "D";
		}
		Task task = new Task(this, TaskType.GetMoNameByMOSTDType, MOSTDType);
		GetMoModel.getMoNamesByMOSTdType(task);
	}

	private void GetDipSNType() {
		if (DIPStartType != 3)
			return;
		super.progressDialog.setMessage("Get DIP SN type");
		super.showProDia();
		Task task = new Task(this, TaskType.GetDipSNType, "");
		GetMoModel.GetDipSNType(task);

	}

	private void GetResourceId() {
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();
		Task task = new Task(this, TaskType.GetResourceId, MyApplication
				.getAppOwner().toString().trim());
		GetMoModel.GetResourceId(task);

	}

	private void GetStartDIPMOinfo(String MOName) {
		super.progressDialog.setMessage("Get DIP MO info");
		super.showProDia();
		Task task = new Task(this, TaskType.DIP_GetMoinfo, MOName);
		DIPStartmodel.GetStartDIPMOinfo(task);
	}

	private void StartDIPLotSN(String lotSN) {
		Params[0] = lotSN;
		Params[1] = MOID;
		Params[2] = WorkFlowId;
		Params[3] = EditDIPProductName.getText().toString().trim();
		Params[4] = EditWorkFlow.getText().toString().trim();
		Params[5] = ProductId;
		Params[6] = MyApplication.getAppOwner().toString().trim();
		Params[7] = ResourceID;
		Params[8] = moName;
		Params[9] = snkeyWord;
		Params[10] = SNLength + "";
		Params[11] = SNType;

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
		if (Params[7].isEmpty()) {
			logDetails(EditScanInfo, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			Toast.makeText(this, "��ԴID����Ϊ��", 5).show();
			return;
		}
		if (Params[6].isEmpty()) {
			logDetails(EditScanInfo, "�����趨�豸��������");
			Toast.makeText(this, "�����趨�豸��������", 5).show();
			return;
		}
		if (DIPStartType == 3 && (Params[10].isEmpty() || Params[11].isEmpty())) {
			logDetails(EditScanInfo, "��ѡ��SN���Ȼ�SN����");
			Toast.makeText(this, "SN���Ȼ�SN����û��ѡ��", 5).show();
			return;
		}
		super.progressDialog.setMessage("DIP ����");
		super.showProDia();
		Task task = new Task(this, TaskType.Dip_StartDIP, Params);
		if (DIPStartType == 1)
			DIPStartmodel.StartDIP(task);
		else if (DIPStartType == 2)
			DIPStartmodel.StartSMTToDIP(task);
		else if (DIPStartType == 3)
			DIPStartmodel.StartDIP_Out(task);
		else if (DIPStartType == 4)
			DIPStartmodel.Dip_material_start(task);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Spin_DIPMOID:
			if (null == moNames || 0 == moNames.size())
				return;
			moName = moNames.get(arg2).get(MONAME);
			GetStartDIPMOinfo(moName);
			if (this.EditScanInfo.getLineCount() > 100)
				this.EditScanInfo.setText("");
			break;
		case R.id.spin_SNLength:

			SNLength = 8 + spinSNLength.getSelectedItemPosition();
			break;
		case R.id.Spin_SNType:
			if (null == SNTypes || 0 == SNTypes.size())
				return;
			SNType = SNTypes.get(arg2).get("�������");
			this.EditSNKeyWord.setEnabled(true);
			EditSNKeyWord.setText("");
			break;
		case R.id.spin_StartType:
			if (manualChanged)
				getDIPMONames();
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void ClearAll() {
		EditProductDescrip.setText("");
		EditWorkFlow.setText("");
		EditDIPStartSN.setText("");
		EditDIPProductName.setText("");
		LotSN = "";
		MOID = "";
		WorkFlowId = "";
		ProductId = "";
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("ȷ���˳�DIP������?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(StartDip.this,
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

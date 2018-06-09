package com.zowee.mes.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.zowee.mes.R;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.FinishOutCardModel;
import com.zowee.mes.model.MaterialRewindModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author Administrator
 * @description �������� 
 */
public class MaterialRewindActivity  extends CommonActivity implements View.OnClickListener 
{	
	private static String msg = null;
	private EditText edtreciegood_num;
	private EditText edtmaterial_barcoade;
	private EditText edtorder_num;
	private EditText edtorder_quantity;	 
	private Button btnreciegood_num;
	private Button btnsubmit;
    private Button btnreciegood_Scan;
	private MaterialRewindModel materialmodel;
	private TextView lab_metrewin_detail;
	private String[] parm={"","",};

	public void init() 
	{
		materialmodel = new MaterialRewindModel();
		super.commonActivity = this;
		super.TASKTYPE = TaskType.MaterialRewindScan;
		super.init();
		msg = "����-����";
		super.progressDialog.setMessage(msg);
		edtreciegood_num = (EditText)findViewById(R.id.edt_reciegood_num);
		edtmaterial_barcoade  = (EditText)findViewById(R.id.edt_material_barcoade);
		edtorder_num  = (EditText)findViewById(R.id.edt_order_num);
		edtorder_quantity =  (EditText)findViewById(R.id.edt_order_quantity);	 
		btnreciegood_num = (Button)findViewById(R.id.btn_reciegood_num); 
		btnsubmit  = (Button)findViewById(R.id.btn_submit); 
		btnreciegood_Scan = (Button)findViewById(R.id.btn_reciegood_Scan);
		lab_metrewin_detail = (TextView)findViewById(R.id.lab_metrewin_detail);
		btnreciegood_Scan.setOnClickListener(this);
		final Builder builder = new AlertDialog.Builder(this);
		final Builder builder_submit = new AlertDialog.Builder(this);
		edtmaterial_barcoade.setOnClickListener(this);
		btnreciegood_num.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);	
				builder.setTitle("�����ջ�����");
				builder.setMessage("��ȷ���Ƿ�Ҫ�����ջ�����");
				builder.setPositiveButton("ȷ��"
						//Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								parm[0] = "";
								generateMaterialPO();
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder.setNegativeButton("ȡ��"
						,  new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{							
							}
						});	
				builder.create().show();
			}
		});
		btnsubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder_submit.setIcon(R.drawable.check);	
				builder_submit.setTitle("�ύɨ������");
				builder_submit.setMessage("��ȷ���Ƿ��ύ");
				builder_submit.setPositiveButton("ȷ��"
						//Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								SubmitRewindMaterial();
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder_submit.setNegativeButton("ȡ��"
						,  new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{							
							}
						});	
				builder_submit.create().show();

			}
		});

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.material_rewind);
		init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		HashMap<String, String> getdata;
		StringBuffer sbf = new StringBuffer();
		getdata = new HashMap<String, String>();
		switch(task.getTaskType())
		{
		case TaskType.MaterialRewindScan://����ɨ��
			String lotSN = task.getTaskResult().toString().trim();
			this.edtmaterial_barcoade.setText(lotSN);
			CheckMaterialInfo(lotSN);
			break;
		case TaskType.MaterialRewindPO:
			getdata = (HashMap<String, String>) task.getTaskResult();	
			this.edtreciegood_num.setText("");
			if (getdata.get("Error") == null) {
				if( Integer.parseInt(getdata.get("Return_value").toString()) == 0)
				{
					if(getdata.containsKey("MESReceiveCode")){
						this.edtreciegood_num.setText(getdata.get("MESReceiveCode").toString());
						parm[0]= getdata.get("MESReceiveCode").toString();
					}
					sbf.append("�����ջ����� ִ�гɹ�\n");
				}else{
					sbf.append("�����ջ�����ʧ��!\n");
				}		 
				sbf.append(getdata.get("I_ReturnMessage").toString()+ "\n");
			} else {
				sbf.append(getdata.get("Error").toString()+ "\n");
				Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();	
			}
			logSysDetails(sbf.toString());
			closeProDia();	
			break;
		case TaskType.MaterialRewindPOInfo:
			getdata = (HashMap<String, String>) task.getTaskResult();	
			this.edtorder_num.setText("");
			this.edtorder_quantity.setText("");
			this.edtmaterial_barcoade.setText("");
			if (getdata.get("Error") == null) {
				if( Integer.parseInt(getdata.get("result").toString()) == 0){

					if(getdata.containsKey("GetSNPO")){
						this.edtorder_num.setText(getdata.get("GetSNPO").toString().trim());
					}
					if(getdata.containsKey("GetSNQty")){
						this.edtorder_quantity.setText(getdata.get("GetSNQty").toString().trim());
					}
					sbf.append("��ȡ���� ִ�гɹ�\n");
				}else{
					sbf.append("��ȡ������Ϣʧ��!\n");
				}
				sbf.append(getdata.get("ReturnMsg").toString()+ "\n");
				
			} else {
				Toast.makeText(this, getdata.get("Error").toString(), 5).show();	
				sbf.append(getdata.get("Error").toString()+ "\n");
			}
			logSysDetails(sbf.toString());
			closeProDia();	
			break;
		case TaskType.MaterialRewindSubmit:
			getdata = (HashMap<String, String>) task.getTaskResult();	
			if (getdata.get("Error") == null) {
				if( Integer.parseInt(getdata.get("result").toString()) == 0){
					this.edtmaterial_barcoade.setText("");
					this.edtorder_num.setText("");
					this.edtorder_quantity.setText("");
					this.edtreciegood_num.setText("");
					sbf.append("�����ύ ִ�гɹ�\n");

				}else{
					sbf.append("����ɨ���ύʧ��!\n");
				}
				sbf.append(getdata.get("ReturnMsg").toString()+ getdata.get("exception").toString()+"\n");
			} else {                   
				sbf.append(getdata.get("Error").toString()+ "\n");
				Toast.makeText(this, "MES ������Ϣ�����쳣", 5).show();	
			}
			logSysDetails(sbf.toString());
			closeProDia();	
			break;
		}

	}
	/**
	 * �����ջ�����
	 */
	private void generateMaterialPO()
	{
		msg ="���ڲ����ջ�����";
		super.progressDialog.setMessage(msg);
		this.edtmaterial_barcoade.setText("");
		this.edtorder_num.setText("");
		this.edtorder_quantity.setText("");
		this.edtreciegood_num.setText("");
		this.lab_metrewin_detail.setText("");
		showProDia();
		Task getMaterial = new Task(this, TaskType.MaterialRewindPO, "");//
		materialmodel.GenerateMaterialRewindPO(getMaterial);

	}
	private void CheckMaterialInfo(String lotSN)
	{
		if(parm[0].equals("")){
			Toast.makeText(this, "���Ȳ����ջ����� ", 5).show();
			return;
		}
		if(lotSN.length() <=5){
			logSysDetails("�������볤��С��6");
			return;		
		}
		parm[1] = lotSN;
		Task GetmaterialInfo = new Task(this,TaskType.MaterialRewindPOInfo,parm);
		msg = "check material info";
		super.progressDialog.setMessage(msg);
		showProDia();
		materialmodel.GetMaterialInfo(GetmaterialInfo);
	}
	private void SubmitRewindMaterial()
	{
		if(parm[0].equals("")){
			Toast.makeText(this, "���Ȳ����ջ����� ", 5).show();
			return;
		}
		parm[1] = this.edtorder_quantity.getText().toString();	    
		Task SubmitModel= new Task(this,TaskType.MaterialRewindSubmit,parm);
		msg = "�����ύɨ������";
		super.progressDialog.setMessage(msg);
		showProDia();
		materialmodel.SubmitRewindMaterial(SubmitModel);
	}
	private void logSysDetails(String logText){
		CharacterStyle ssStyle=null;
		if(logText.contains("ִ�гɹ�")){
			ssStyle=new ForegroundColorSpan(Color.GREEN);
		}else{
			ssStyle=new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog="["+df.format(new Date())+"]"+logText+"\n";		
		SpannableStringBuilder ssBuilder=new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(lab_metrewin_detail.getText());
		lab_metrewin_detail.setText(ssBuilder);	

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.edt_material_barcoade:
			edtmaterial_barcoade.setText(edtmaterial_barcoade.getText().toString().trim().toUpperCase());
			CheckMaterialInfo(edtmaterial_barcoade.getText().toString().trim());  //  for manual debug 
			break;
		case R.id.btn_reciegood_Scan:
			super.laserScan();
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
		.setTitle("ȷ���˳�������?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
					stopService(new Intent(MaterialRewindActivity.this,
							BackgroundService.class));
 
				 finish();				
			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}

}

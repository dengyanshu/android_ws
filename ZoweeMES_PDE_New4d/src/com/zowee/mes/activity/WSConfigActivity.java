package com.zowee.mes.activity;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.ws.MesSoapParser;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * @author Administrator
 * @description WS用户自定义配置界面
 */
public class WSConfigActivity extends CommonActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener
{
	private View btnGroups;
	private EditText edtWSSpaceName;
	private EditText edtWSDL;
	private EditText edtWSEdition;
	private EditText edtWSMethod;
	private EditText edtUserTicket;
	private EditText edtSqlSentenceName;
	private RadioGroup rgIsDotNet;
	//private Button btnEnsure;
	private Button btnModify;
	private Button btnCancel;
	private Button btnRestore;
	private RadioButton rbSupport;//支持DOTNET
	private RadioButton rbUnsupport;//不支持DOTNET
	private boolean isDotNet;
	private String USERTICKET = "UserTicket";
	
	@Override
	public void init()
	{
		btnGroups = this.findViewById(R.id.btnGroups);
		//((Button)btnGroups.findViewById(R.id.btn_scan)).setVisibility(View.GONE);
		edtWSSpaceName = (EditText)this.findViewById(R.id.edt_wsSpaceName);
		edtWSDL = (EditText)this.findViewById(R.id.edt_wsdl);
		edtWSEdition = (EditText)this.findViewById(R.id.edt_wsEdition);
		edtWSMethod = (EditText)this.findViewById(R.id.edt_wsMethod);
		edtUserTicket = (EditText)this.findViewById(R.id.edt_userTicket);
	//	edtSqlSentenceName = (EditText)this.findViewById(R.id.edt_sqlSentence);
		rgIsDotNet = (RadioGroup)this.findViewById(R.id.rg_isDotNet);
		btnModify = (Button)btnGroups.findViewById(R.id.btn_update);
		//btnEnsure.setText(R.string.modify);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnRestore = (Button)btnGroups.findViewById(R.id.btn_restore);
		rbSupport = (RadioButton)rgIsDotNet.findViewById(R.id.rb_support);
		rbUnsupport = (RadioButton)rgIsDotNet.findViewById(R.id.rb_unsupport);
		Soap soap = MesSoapParser.getOfficalSoap();
		initUIData(soap);
		btnModify.setOnClickListener(this);	
		btnCancel.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
		//btnCancel.setVisibility(View.GONE);
		//btnCancel.setText(R.string.abolish);
	}

	/**
	 * @param soap
	 * @description 将soap的信息显示到界面上
	 */
	private void initUIData(Soap soap) 
	{
		if(null==soap) return;
		edtWSSpaceName.setText(soap.getWsNameSpa());
		edtWSDL.setText(soap.getWsWsdl());
		edtWSEdition.setText(soap.getSoapEdi()+"");
		edtWSMethod.setText(soap.getWsMethod());
		rgIsDotNet.setOnCheckedChangeListener(this);
		if(null!=soap.getWsProperty().get(USERTICKET))
			edtUserTicket.setText(soap.getWsProperty().get(USERTICKET).toString());
		if(soap.isDotNet())
		{
			rbSupport.setChecked(true);
			isDotNet = true;
		}
		else
		{
			rbUnsupport.setChecked(true);
			isDotNet = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wsconfig_view);
		init();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_update:
				boolean isPass = subUIData();
				if(!isPass) return;
				String wsSpaceName = edtWSSpaceName.getText().toString().trim();
				String wsWSDL = edtWSDL.getText().toString().trim();
				int wsEditon = Integer.valueOf(edtWSEdition.getText().toString().trim());
				String wsMethod = edtWSMethod.getText().toString().trim();
				//String wsSqlSentence = edtSqlSentenceName.getText().toString().trim();
				String wsUserTicket = edtUserTicket.getText().toString().trim();
				Soap soap = new Soap(wsSpaceName, wsMethod, wsWSDL, wsEditon);
				soap.addWsProperty(USERTICKET,edtUserTicket.getText().toString().trim());
				soap.setDotNet(isDotNet);
				MesSoapParser.mergerSoap(soap);
				soap = MesSoapParser.getOfficalSoap();
				MesWebService.setSoap(soap);
				Toast.makeText(this, R.string.wsconfig_success, Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_cancel:
				this.finish();
				break;
			case R.id.btn_restore:
				Soap  originalSoap = MesSoapParser.getSoap();
				initUIData(originalSoap);
				MesSoapParser.mergerSoap(originalSoap);
				MesWebService.setSoap(MesSoapParser.getOfficalSoap());
				Toast.makeText(this, R.string.wsconfig_restore, 1500).show();
				break;
		}
		
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		switch(checkedId)
		{
			case R.id.rb_support:
				isDotNet = true;
			break;
			case R.id.rb_unsupport:
				isDotNet = false;
				break;
		}
		
	}
	
	private boolean subUIData()
	{
		boolean isPass = false;
		isPass = subView(edtWSSpaceName);
		if(!isPass)
		{
			Toast.makeText(this,R.string.wsSpaceName_isnull , Toast.LENGTH_SHORT).show();
			return isPass;
		}
		isPass = subView(edtWSDL);
		if(!isPass)
		{
			Toast.makeText(this, R.string.wsWsdl_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		isPass = subView(edtWSEdition);
		if(!isPass)
		{
			Toast.makeText(this,R.string.wsEdition_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		isPass = subView(edtWSMethod);
		if(!isPass)
		{
			Toast.makeText(this, R.string.wsMethod_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		return isPass;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode ==MyApplication.SCAN_KEYCODE)
			return true;
		
		return super.onKeyDown(keyCode, event);
	}
	
//	/** 
//	 * @description  还原WS初始化配置
//	 */
//	private void restoreWSConfig()
//	{
//		Soap soap = MesSoapParser.getSoap();
//		edtWSSpaceName.setText(soap.getWsNameSpa());
//		edtWSDL.setText(soap.getWsWsdl());
//		edtWSEdition.setText(soap.getSoapEdi()+"");
//		
//		
//	}
	
	
}

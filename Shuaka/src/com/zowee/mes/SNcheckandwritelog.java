package com.zowee.mes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//天津程序。不连数据库 直接做好条码规章设置进行字符串匹配 ，核对的记录 全部写日志！！
public class SNcheckandwritelog extends Activity {
    protected static final String TAG = "SNcheckandwritelog";
	private   EditText  startwithedit;
    private  String snstart;
    private   EditText  endwithedit;
    private  String snend;
    private   EditText  containedit;
    private   String sncontain;
    private  Button  button;
    private Button  buttoncancel;
    private  EditText  snedit;
    private  TextView  resulttextview;
    
    private   EditText  editscan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tianjin_sncheckwritelog);
		
		startwithedit=(EditText) findViewById(R.id.sncheckwritelog_editstartwith);
		containedit=(EditText) findViewById(R.id.sncheckwritelog_editcontain);
		endwithedit=(EditText) findViewById(R.id.sncheckwritelog_editendwith);
		button=(Button) findViewById(R.id.sncheckwritelog_button);
		button.setOnClickListener(okbuttononclicklistener);
		buttoncancel=(Button) findViewById(R.id.sncheckwritelog_cancelbutton);
		buttoncancel.setOnClickListener(cancelbuttononclicklistener);
		
		snedit=(EditText) findViewById(R.id.sncheckwritelog_sn);
		snedit.setOnKeyListener(snonkeylistener);
		resulttextview=(TextView) findViewById(R.id.sncheckwritelog_textviewresult);
		
		editscan=(EditText) findViewById(R.id.sncheckwritelog_editscan);
		 logSysDetails("程序不需连接网络和MES服务器，请自行设定条码规则进行核对！如不需核对后缀和中间包含字符，可不填包含和后缀规则!规则配置点击按钮确定","程序");
	}
		 

	private View.OnClickListener okbuttononclicklistener=new View.OnClickListener(){
		      public void onClick(View arg0) {
		    	  snstart=startwithedit.getText().toString().toUpperCase().trim();
		    	  sncontain=containedit.getText().toString().toUpperCase().trim();
		    	  Log.i(TAG,"sncontain="+sncontain);
		    	  snend=endwithedit.getText().toString().toUpperCase().trim();
		    	  startwithedit.setEnabled(false);
		    	  containedit.setEnabled(false);
		    	  endwithedit.setEnabled(false);
		    	  logSysDetails("核对规则已设置如下：条码前缀必须是["+snstart+"],中间要包含字符["+sncontain
		    			  +"],结尾是["+snend+"]!!","核对" );
		    	  snedit.requestFocus();
		      }
	};
	
	private View.OnClickListener cancelbuttononclicklistener=new View.OnClickListener(){
	      public void onClick(View arg0) {
	    	  startwithedit.setText("");
	    	  containedit.setText("");
	    	  endwithedit.setText("");
	    	  startwithedit.setEnabled(true);
	    	  containedit.setEnabled(true);
	    	  endwithedit.setEnabled(true);
	      }
   };
   
   private View.OnKeyListener  snonkeylistener=new View.OnKeyListener(){
	   public boolean onKey(View v, int keycode, KeyEvent event) {
		   if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			   
			    String sn=snedit.getText().toString().trim().toUpperCase();
				String  regex=snstart+"\\w{0,}"+sncontain+"\\w{0,}"+snend;
				boolean  result=sn.matches(regex);  
				if(result){
					resulttextview.setText("PASS");
					resulttextview.setTextColor(Color.GREEN);
					logSysDetails(sn+"核对成功","成功");
				}
				else{
					resulttextview.setText("FAIL");
					resulttextview.setTextColor(Color.RED);
					logSysDetails(sn+"核对失败","成功");
				}
			   writeTosdcard(sn+"核对"+result+"!\r\n");
			   snedit.setText("");
			   snedit.requestFocus();
			}
		   return false;
		   
		   
	   };
   };
   
	private void logSysDetails(String logText, String strflag) {
		CharacterStyle ssStyle = null;
		if (logText.contains(strflag)) {
			ssStyle = new ForegroundColorSpan(Color.BLUE);
		} else {
			ssStyle = new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog = "[" + df.format(new Date()) + "]" + logText + "\n";
		SpannableStringBuilder ssBuilder = new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(editscan.getText());
		editscan.setText(ssBuilder);
	}
	 private  boolean  writeTosdcard(String str) {
		 //写入平板文件 在平板的第2存储空间 建立snchecklog的文件夹,不存在该目录就创建
		 //该目录下 按yyyy-MM-dd.txt文件 建立日志  逐条写入
	    	File file=new  File(Environment.getExternalStorageDirectory(),"snchecklog");
	    	
	    	if(!file.exists()){
	    		file.mkdir();
	    		Toast.makeText(this, "安卓平板上名叫snchecklog文件目录创建成功", Toast.LENGTH_LONG).show();
	    	}
	    	SimpleDateFormat  sdf1=new  SimpleDateFormat("yyyy-MM-dd");
	    	SimpleDateFormat  sdf2=new  SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	    	String  txtname=sdf1.format(new  Date());
	    	String  logstartstring=sdf2.format(new  Date());
	    	try {
				FileOutputStream  fos=new FileOutputStream(new  File(file,txtname+".txt"),true);
				fos.write(("["+logstartstring+"]"+str).getBytes());
				fos.flush();
				fos.close();
				logSysDetails("日志写入成功!!","日志" );
				return  true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "要写的文件找不到", Toast.LENGTH_LONG).show();
				return  false;
			} 
	    	
	    	
	    }
	
}

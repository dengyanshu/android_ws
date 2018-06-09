package cn.yanshu.xmldemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;



import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import cn.yanshu.R;

public class XMLSerialActivity extends Activity {
	
	private  TextView  smsTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xmlserial_activity);
		smsTextView=(TextView) findViewById(R.id.xml_tv);
	}
	
	
	/**
	 * click  begin   readxml  to  sdcard
	 * 
	 * */
	public void  click(View view){
		//ģ��һ�����ż���
		List<Sms>  smss=new ArrayList<Sms>();
		smss.add(new Sms("xiaozhang", "today  so happy"));
		smss.add(new Sms("xiaolong", "thank  godness"));
		//��android  �ṩ��api�ĵ� ��ȡ���к��� ���к�
	     XmlSerializer newSerializer = Xml.newSerializer();
	     FileOutputStream  fos=null;
	     try {
	    	 fos=new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"smsback.xml"));
			newSerializer.setOutput(fos, "utf-8");
		    newSerializer.startDocument("utf-8", true);//��2�����������Ƿ���ҪԼ��
		    newSerializer.startTag(null, "smss");
		    //��ʼ�ݹ鼯��
		    for(Sms sms:smss){
		    	newSerializer.startTag(null, "sms");
		    	newSerializer.startTag(null, "name");
		    	newSerializer.text(sms.getName());
		    	newSerializer.endTag(null, "name");
		    	newSerializer.startTag(null, "content");
		    	newSerializer.text(sms.getContent());
		    	newSerializer.endTag(null, "content");
		    	newSerializer.endTag(null, "sms");
		    }
		    newSerializer.endTag(null, "smss");
		    newSerializer.endDocument();
		    fos.close();
	     } catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * �����ȡassert������ļ�
	 * ��android  xml�������ṩ��pull������  ����xml
	 * */
	public  void click2(View view){
		readXml2();
		StringBuffer  sb=new StringBuffer();
	/*	for(Sms s:smss){
			sb.append("[id="+s.getId()+",name="+s.getName()+",content="+s.getContent()+"]");
		}*/
		smsTextView.setText(sb.toString());
	}
	private List<Sms> readXml() {
		XmlPullParser pullparser = Xml.newPullParser();
		List<Sms>  smss=null;
		Sms sms=null;
		try {
			pullparser.setInput(getAssets().open("responsebak.txt"), "utf-8");
			int eventType=0;
			while((eventType=pullparser.getEventType())!=XmlPullParser.END_DOCUMENT){
				switch(eventType){
				case  XmlPullParser.START_TAG:
				    if("smss".equals(pullparser.getName())){
				    	smss=new ArrayList<Sms>();
				    }
				    else  if("sms".equals(pullparser.getName())){
				    	sms=new Sms();
				    	sms.setId(pullparser.getAttributeValue(0));
				    }
				    else  if("name".equals(pullparser.getName())){
				    	sms.setName(pullparser.nextText());
				    }
				    else  if("content".equals(pullparser.getName())){
				    	sms.setContent(pullparser.nextText());
				    }
				   
				    break;
				case  XmlPullParser.END_TAG:
					if("sms".equals(pullparser.getName())){
						smss.add(sms);
					}
					break;
				}
				pullparser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  smss;
	}
	
	private List<Sms> readXml2() {
		XmlPullParser xpp_4d = Xml.newPullParser();
		List<Sms>  smss=null;
		Sms sms=null;
	    try{
	    	xpp_4d.setInput(getAssets().open("responsebak.txt"),"utf-8");
			    int eventType=0;
				int count=1;
				while((eventType=xpp_4d.getEventType())!=XmlPullParser.END_DOCUMENT){
				       switch(eventType){
				       case  XmlPullParser.START_TAG:
				    	   if("smss".equals(xpp_4d.getName())){
						    	smss=new ArrayList<Sms>();
						    }
						    else  if("sms".equals(xpp_4d.getName())){
						    	sms=new Sms();
						    	sms.setId(xpp_4d.getAttributeValue(0));
						    }
						    else  if("name".equals(xpp_4d.getName())){
						    	sms.setName(xpp_4d.nextText());
						    }
						    else  if("content".equals(xpp_4d.getName())){
						    	sms.setContent(xpp_4d.nextText());
						    }
				           break;
				       case  XmlPullParser.END_TAG:
				    	    if("sms".equals(xpp_4d.getName())){
				    	    	smss.add(sms);
							}
				           break;
				       }
				       xpp_4d.next();
				}
		} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  smss;
			

	}
	
	
	
	
	public void click3(View view){
		     XmlPullParser xpp= Xml.newPullParser();
		     try{
			
			
			    xpp.setInput(getAssets().open("responsebak.txt"),"utf-8");
			    
			    int eventtype=0;
				int count=1;
				while((eventtype=xpp.getEventType())!=XmlPullParser.END_DOCUMENT){
					  
				       switch(eventtype){
				       case  XmlPullParser.START_TAG:
				    	   if(xpp.getName().equals("table")){
				    		   System.out.println("1111");
				    		   
				    	   }
				    	   else if (xpp.getName().equals("tr")){
				    		    count=1;
				    		    System.out.println("2222");
				    	   }
				    	  else if (xpp.getName().equals("td")){
				    		   System.out.println("conunt="+count);
				    		   switch (count) {
								case 1://machine order
									String text=xpp.nextText();
									System.out.println("case 1");
								 break;
								case 2://table
									System.out.println("case 2");
								 break;
								case 3://slot
									System.out.println("case 3");
								 break;
								case 4://L/R
									System.out.println("case 4");
								 break;
								default:
									break;
									
								}
				    	   }
				      break;
				    	   
				    	   
				       case  XmlPullParser.END_TAG:
				    	   if(xpp.getName().equals("table")){
				    		   System.out.println("5555");
				    	   }
				    	   else if (xpp.getName().equals("tr")){
				    		   System.out.println("4444");
				    	   }
				    	   else if (xpp.getName().equals("td")){
				    		   System.out.println("3333");
				    		   count++;
				    	   }
				    	   
				    	   break;
				       }
				       xpp.next();
				}
		     }
		     catch(Exception e){
		    	 
		     }
			
		}

}

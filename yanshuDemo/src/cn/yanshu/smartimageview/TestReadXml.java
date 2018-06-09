package cn.yanshu.smartimageview;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.test.AndroidTestCase;
import android.util.Xml;

/**
 *测试xml的解析功能
 * */

public class TestReadXml extends AndroidTestCase {
	private List<Sms>  getSmsFromStream(InputStream  is){
	    XmlPullParser pullparser= Xml.newPullParser();
	    List<Sms> smss=null;
	    Sms sms=null;
	    try {
			pullparser.setInput(is,"utf-8");
			int  eventype=0;
			while((eventype=pullparser.getEventType())!=XmlPullParser.END_DOCUMENT){
				switch (eventype) {
				case XmlPullParser.START_TAG:
					if("smss".equals(pullparser.getName())){
						smss=new ArrayList<Sms>();
					}
					else if("sms".equals(pullparser.getName())){
						sms=new Sms();
					}
					else if("name".equals(pullparser.getName())){
						sms.setName(pullparser.nextText());
					}
					else	if("head".equals(pullparser.getName())){
						sms.setHeadurl(pullparser.nextText());
					}
					else if("content".equals(pullparser.getName())){
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return smss;
	}
	
	
	public  void  test(){
		try {
			List<Sms>  smss=getSmsFromStream(getContext().getAssets().open("smsback.xml"));
			System.out.println(smss);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

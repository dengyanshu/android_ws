package cn.yanshu.smartimageview;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.statements.RunAfters;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.jit.lib.SmartImageView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.yanshu.R;

public class SmartImageViewDemo extends Activity {
	 private  ListView  lv;
	 
	 private  List<Sms>  smss;
	 
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.smartimageview_activity);
		lv=(ListView) findViewById(R.id.smartimageview_lv);
		
	}
	  
	  
	  public void click(View view){
		  new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// TODO Auto-generated method stub
						URLConnection conn = new URL(
								"http://10.1.32.112:8060/home/forandroid.xml")
								.openConnection();
						InputStream  is = conn.getInputStream();
						//ͨ��xmlpurse ��������ȡ  sms���󼯺�
						 smss=getSmsFromStream(is);
						 runOnUiThread(new  Runnable() {
							public void run() {
								lv.setAdapter(new myAdapter());
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}).start();
	  }


	
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
	
	
	
	
	class  myAdapter  extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View  view=null;
			if(convertView==null){
				view=View.inflate(SmartImageViewDemo.this, R.layout.listviewdemo_lvadapter2, null);
			}else{
				view=convertView;
			}
			TextView  tv=(TextView) view.findViewById(R.id.listdemo_adaptertv1);
			TextView  tv2=(TextView) view.findViewById(R.id.listdemo_adaptertv2);
			SmartImageView  smartimageview=(SmartImageView)view.findViewById(R.id.listdemo_adapterimg);
			tv.setText(smss.get(position).getName());
			tv2.setText(smss.get(position).getContent());
			smartimageview.setImageUrl(smss.get(position).getHeadurl());
			return  view;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return smss.size();
		}
	};
	
	
	
}

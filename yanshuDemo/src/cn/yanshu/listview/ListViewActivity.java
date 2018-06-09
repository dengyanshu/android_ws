package cn.yanshu.listview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.yanshu.R;


public class ListViewActivity extends android.app.Activity {
	private  ListView  lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.listviewdemo_activity);
		
		lv=(ListView) findViewById(R.id.listviewdemo_lv);
		
		
		List<Map<String,String>>  list=new ArrayList<Map<String,String>>();
		
		
		Map<String,String>  map=new HashMap<String, String>();
	     map.put("col1", "��һ��title");
	     map.put("col2", "��һ��content");
	     list.add(map);
	     
	     map=new HashMap<String, String>();
	     map.put("col1", "�ڶ��е�һ��");
	     map.put("col2", "�ڶ��еڶ���");
	     list.add(map);
	     
	     map=new HashMap<String, String>();;
	     map.put("col1", "��3�е�һ��");
	     map.put("col2", "��3�еڶ���");
	     list.add(map);
	    
		
		  lv.setAdapter(new MyAdapter(list));
		
		
	}
	
	private class  MyAdapter extends  BaseAdapter{
        private  List<Map<String,String>>  list;
		public MyAdapter() {
			super();
			// TODO Auto-generated constructor stub
		}
		public MyAdapter(List<Map<String, String>> list) {
			super();
			this.list = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
      /*
       * show to view  
       * at position
       * ע�⻺�� �Ͳ��ֵ�һ��Ҫmarch_parent
       * */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
		  	View  view = null;
			if(convertView==null){
				//����д��  ͨ��layout�õ�view����
				view=View.inflate(ListViewActivity.this, R.layout.listviewdemo_lvadapter, null);
				/**
				 * ���滹������2��д��
				  LayoutInflater layoutinflater=(LayoutInflater) getApplicationContext().
						  getSystemService(LAYOUT_INFLATER_SERVICE);
				  layoutinflater.inflate(R.layout.listviewdemo_lvadapter, null);
				  
				  
				  LayoutInflater.from(getApplicationContext()).inflate(R.layout.listviewdemo_lvadapter, null);
				  */
			}else{
				view=convertView;
			}
			TextView  tv=(TextView)view.findViewById(R.id.listdemo_adaptertv1);
			TextView  tv2=(TextView)view.findViewById(R.id.listdemo_adaptertv2);
			tv.setText(list.get(position).get("col1").toString());
			tv2.setText(list.get(position).get("col2").toString());
			
			return view;
		}
		
	}
	
	
	

}

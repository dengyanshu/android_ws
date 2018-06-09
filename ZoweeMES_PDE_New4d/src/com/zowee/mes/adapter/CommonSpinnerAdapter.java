package com.zowee.mes.adapter;

import java.util.List;

import com.zowee.mes.R;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public  class CommonSpinnerAdapter<T> extends BaseAdapter 
{
	protected List<T> lis;
	protected Context context;
	
	public CommonSpinnerAdapter(List<T> lis,Context context)
	{
		 this.lis = lis;
		 this.context = context;
	}
	
	public CommonSpinnerAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.lis.size();
	}

	@Override
	public T getItem(int position)
	{
		// TODO Auto-generated method stub
		return lis.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View view = View.inflate(this.context,R.layout.spinitem_view, null);
		TextView tempView = (TextView)view;
		tempView.setText(lis.get(position).toString());
	//	tempView.setTextSize(12);
		
		return tempView;
	}

	
}

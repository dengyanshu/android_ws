package com.zowee.mes.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zowee.mes.R;
import com.zowee.mes.R.color;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Administrator
 * @description 上料对料 工单下拉框的Adapter
 */
public class SNTypeAdapter extends CommonSpinnerAdapter {

	private List<HashMap<String, String>> SNTypes;
	private static final String sntype = "批号类别";

	public SNTypeAdapter(Context context, List<HashMap<String, String>> snTypes) {
		super(context);
		// if(moNames==null) moNames = new ArrayList<String>();//防止用户输入空数据
		// 导致程序崩溃
		this.SNTypes = snTypes;
	}

	@Override
	public int getCount() {

		return SNTypes.size();
	}

	@Override
	public Object getItem(int position) {
		// if(!isDatasNull())
		return SNTypes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) View.inflate(context,
				R.layout.spinitem_view, null);

		// HashMap<String,String> tempMap = paraMapLis.get(position);
		// for(int i = 0;i<moNames.size();i++)
		view.setText(SNTypes.get(position).get(sntype) + "-"
				+ SNTypes.get(position).get("描述"));
		view.setTextSize(15); // changby ybj 20131005
		view.setGravity(3);
		return view;
	}

}

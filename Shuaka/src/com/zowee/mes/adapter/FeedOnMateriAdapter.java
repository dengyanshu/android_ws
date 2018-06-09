package com.zowee.mes.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zowee.mes.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Administrator
 * @description 上料对料 工单下拉框的Adapter
 */
public class FeedOnMateriAdapter extends CommonSpinnerAdapter {

	private List<HashMap<String, String>> moNames;
	private static final String MONAME = "MOName";

	public FeedOnMateriAdapter(Context context,
			List<HashMap<String, String>> moNames) {
		super(context);
		// if(moNames==null) moNames = new ArrayList<String>();//防止用户输入空数据
		// 导致程序崩溃
		this.moNames = moNames;
	}

	@Override
	public int getCount() {

		return moNames.size();
	}

	@Override
	public Object getItem(int position) {
		// if(!isDatasNull())
		return moNames.get(position);
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
		view.setText(moNames.get(position).get(MONAME));
		view.setTextSize(15); // changby ybj 20131005
		return view;
	}

}

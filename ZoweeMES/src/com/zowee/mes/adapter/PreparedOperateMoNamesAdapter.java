package com.zowee.mes.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zowee.mes.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Administrator
 * @description 卡板发货 - 订单名称的数据适配器
 */
public class PreparedOperateMoNamesAdapter extends
		CommonSpinnerAdapter<HashMap<String, String>> {

	private final static String MONAME = "MOName";

	public PreparedOperateMoNamesAdapter(Context context,
			List<HashMap<String, String>> lisMaps) {
		super(lisMaps, context);
		if (null == lisMaps)
			super.lis = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) View.inflate(this.context,
				R.layout.spinitem_view, null);
		HashMap<String, String> lsItem = super.lis.get(position);
		String viewText = lsItem.get(MONAME);
		view.setText(viewText);

		return view;
	}

}

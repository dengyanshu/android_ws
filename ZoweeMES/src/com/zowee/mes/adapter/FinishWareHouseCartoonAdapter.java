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
 * @description 卡通入库下拉框数据适配器
 */
public class FinishWareHouseCartoonAdapter extends
		CommonSpinnerAdapter<HashMap<String, String>> {

	// private static final String PRODUCTCOMPLETEID = "ProductCompleteID";
	private static final String COMPLETEDOCNO = "CompleteDocNo";

	public FinishWareHouseCartoonAdapter(Context context,
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
		String viewText = lsItem.get(COMPLETEDOCNO);
		view.setText(viewText);

		return view;
	}

}

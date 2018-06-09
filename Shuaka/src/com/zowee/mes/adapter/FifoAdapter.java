package com.zowee.mes.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zowee.mes.R;

/**
 * @author Administrator
 * @description Fifo扫描的数据显示ListView的数据模型
 */
public class FifoAdapter extends BaseAdapter {
	private List<HashMap<String, String>> fifoScanRes;
	private Context context;
	private static final String BOXSN = "BoxSN";
	private static final String LOTSN = "LotSN";
	private static final String STOCKLOCATION = "库位";

	public FifoAdapter(List<HashMap<String, String>> fifoScanRes,
			Context context) {
		this.fifoScanRes = fifoScanRes;
		this.context = context;
	}

	@Override
	public int getCount() {

		return fifoScanRes.size();
	}

	@Override
	public Object getItem(int arg0) {

		return fifoScanRes.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context, R.layout.fifo_adaper_view, null);
		TextView labBoxSn = (TextView) view.findViewById(R.id.labBoxSn);
		TextView labLotSn = (TextView) view.findViewById(R.id.labLotSn);
		TextView labStockLoction = (TextView) view
				.findViewById(R.id.labStockLocation);
		HashMap<String, String> fifoResItem = this.fifoScanRes.get(position);
		labBoxSn.setText(fifoResItem.get(BOXSN) == null ? "" : fifoResItem
				.get(BOXSN));
		labLotSn.setText(fifoResItem.get(LOTSN) == null ? "" : fifoResItem
				.get(LOTSN));
		labStockLoction.setText(fifoResItem.get(STOCKLOCATION) == null ? ""
				: fifoResItem.get(STOCKLOCATION));

		return view;
	}

}

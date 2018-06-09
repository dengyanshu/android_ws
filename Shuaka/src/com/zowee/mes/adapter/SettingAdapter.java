package com.zowee.mes.adapter;

import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.activity.SettingActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Administrator
 * @description 软件设置 的ListView 的数据适配器
 */
public class SettingAdapter extends BaseAdapter {
	private String[] settings;
	private SettingActivity settingActivity;

	public SettingAdapter(String[] settings, SettingActivity settingActivity) {
		this.settings = settings;
		this.settingActivity = settingActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return settings.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return settings[arg0];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View addedView = View.inflate(settingActivity,
				R.layout.setting_lvitem_view, null);
		// addedView.setOnClickListener(settingActivity.labClicLis);
		TextView labView = (TextView) addedView.findViewById(R.id.lab_View);
		labView.setText(settings[position]);
		labView.setOnClickListener(settingActivity.labClicLis);
		return addedView;
	}

}

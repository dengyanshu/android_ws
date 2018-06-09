package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description FifoActivity的业务逻辑处理类
 */
public class FifoModel extends CommonModel {

	/*
	 * Fifo扫描
	 */
	public void fifoScan(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				if (null == task.getTaskData())
					return task;
				String lotSn = task.getTaskData().toString();
				Soap soap = MesWebService.getMesEmptySoap();
				// soap.addWsProperty("SQLString",
				// "if SUBSTRING('"+lotSn+"',1,1) <>'M'  begin select '条码有误,请检查!' as BoxSN,'' LotSN,'' 库位   end else begin "+
				// "SELECT  TOP (100) PERCENT dbo.POItemLot.BoxSN AS BoxSN, dbo.StockLoc.LotSN, dbo.stockLOC.StockLocation AS 库位  FROM  dbo.Lot inner join dbo.stockLOC on dbo.lot.LotSN=dbo.StockLoc.LotSN inner join dbo.POItemLot  on  dbo.stockloc.LotSN=dbo.POItemLot.LotSN  WHERE dbo.Lot.LotSN < '"+lotSn+"' ORDER BY dbo.StockLoc.Createdate end ");
				soap.addWsProperty("SQLString", "exec Txn_WMSRMFIFOQuery '"
						+ lotSn + "';");
				try {
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					task.setTaskResult(envolop);
				} catch (Exception e) {
					MesUtils.netConnFail(task.getActivity());
					Log.i(TAG, e.toString());
				}
				return task;
			}

		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	/*
	 * 获取FIFO扫描的结果
	 */
	public List<HashMap<String, String>> getFifoScanRes(Task task) {
		List<HashMap<String, String>> resArrs = new ArrayList<HashMap<String, String>>();
		SoapSerializationEnvelope envolop = (SoapSerializationEnvelope) task
				.getTaskData();

		if (null == envolop || null == envolop.bodyIn)
			return null;
		// Log.i(TAG, envolop.bodyIn.toString());
		List<String> resDataSet = MesWebService.WsDataParser
				.getResDatSet(envolop.bodyIn.toString());
		// Log.i(TAG, "aa");
		// Log.i(TAG, ""+resDataSet.size());
		if (null == resDataSet || 0 == resDataSet.size())
			return resArrs;
		if (1 == resDataSet.size())// 扫描录入的条码 是不符合规范的
		{
			Log.i(TAG, "come in");
			if (resDataSet.get(0).toString().contains("条码有误"))
				return null;
		}

		// List<HashMap<String,String>> resArr = new
		// ArrayList<HashMap<String,String>>();
		for (int i = 0; i < resDataSet.size(); i++) {
			String regex = "=|;";
			List<int[]> indes = StringUtils.getMatStrIndex(regex, resDataSet
					.get(i).toString());

			if (null != indes) {
				HashMap<String, String> resItem = new HashMap<String, String>();
				String key = "";
				String value = "";
				for (int j = 0; j < indes.size();) {
					if (0 == j)
						key = resDataSet.get(i).substring(0, indes.get(j)[0])
								.trim();
					else
						key = resDataSet
								.get(i)
								.substring(indes.get(j - 1)[1], indes.get(j)[0])
								.trim();
					value = resDataSet.get(i)
							.substring(indes.get(j)[1], indes.get(j + 1)[0])
							.trim();
					resItem.put(key, value);
					j = j + 2;
				}
				resArrs.add(resItem);
			}
		}

		return resArrs;
	}

	/*
	 * 开启任务 用来解析并显示Fifo扫描结果
	 */
	public void viewFifoRes(Task task) {
		// if(null==task||null==task.getTaskData())
		// return;
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				List<HashMap<String, String>> resArrs = getFifoScanRes(task);
				task.setTaskResult(resArrs);
				return task;
			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);

	}

}

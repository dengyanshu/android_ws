package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class SmtFvmiModel extends CommonModel {
	String moid;
	String sn;
	String presnid;
	String preflag;
	String useid;

	public void smtFvmi(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[] { "", "", "", "" };
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					moid = Params[0];
					sn = Params[1];
					presnid = Params[2];
					preflag = Params[3];
					useid = Params[4];

					Log.i(TAG, "任务数据是:" + Params);

					String sql = " declare @res int declare @message nvarchar(max) declare @snid CHAR(12) declare @SNFlag int exec @res= Txn_VisualCheckSMT    "
							+ " @I_OrBitUserId='"+useid+"', @MOId='"+ moid+ "', @SN ='"
							+ sn
							+ "', @PreSNId='"
							+ presnid
							+ "',@PreSNFlag ="
							+ preflag
							+ "  ,"
							+ "   @I_ReturnMessage=@message  out ,@SNId=@snid out,@SNFlag=@SNFlag out  select @res as I_ReturnValue,@message as I_ReturnMessage,@snid as SNId,@SNFlag as SNFlag";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet)
						result.put("Error", "连接MES失败");
					else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
						// String[] reses = new String[1];
						if (null != resMapsLis && 0 != resMapsLis.size()) {
							for (int i = 0; i < resMapsLis.size(); i++) {
								result.putAll(resMapsLis.get(i));
							}
						} else {
							result.put("Error", "解析" + resDataSet.toString()
									+ "回传信息失败");
						}
					}
					task.setTaskResult(result);
				} catch (Exception e) {
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	// 获取工单信息！
	public void getMo(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String Params;// 任务所需数据
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String) task.getTaskData();
					String sql = " SELECT lot.MOId,mo.MOName, dbo.ProductRoot.ProductName,Product.ProductDescription  "
							+ "	FROM dbo.Lot  WITH(NOLOCK)  "
							+ "  LEFT JOIN dbo.MO  WITH(NOLOCK) ON dbo.Lot.MOId = dbo.MO.MOId  "
							+ " LEFT JOIN dbo.Product  WITH(NOLOCK) ON dbo.MO.ProductId = dbo.Product.ProductId  "
							+ "  LEFT JOIN dbo.ProductRoot  WITH(NOLOCK) ON dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId  WHERE Lot.LotSN='"
							+ Params + "'  ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						result.put("Error", "待解析的MES结果LIST为空");
						Log.i(TAG, "这里有执行");
						task.setTaskResult(result);
					} else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "resmaplis" + resMapsLis);
						// List<HashMap<String,String>> parasMapsLis = new
						// ArrayList<HashMap<String,String>>();
						// HashMap<String, String> temphash=new HashMap<String,
						// String>();
						if (null != resMapsLis && 0 != resMapsLis.size()) {
							for (int i = 0; i < resMapsLis.size(); i++) {
								result.putAll(resMapsLis.get(i));
							}
						} else {
							result.put("Error", "解析" + resDataSet.toString()
									+ "回传信息失败");
						}
						task.setTaskResult(result);
					}
				} catch (Exception e) {
					Log.i(TAG, "这里有执行11");
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

}

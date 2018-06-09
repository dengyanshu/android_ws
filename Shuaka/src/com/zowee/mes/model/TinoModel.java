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

public class TinoModel extends CommonModel {
	String tinosn;
	String moname;
	String resource;
	String userid;

	public void tino(Task task) {

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

					tinosn = Params[0];
					moname = Params[1];
					resource = Params[2];
					userid = Params[3];

					Log.i(TAG, "任务数据是:" + Params);

					String sql = " declare @res int declare @message nvarchar(max) exec @res= Txn_TinolAdd   "
							+ "@TinolSN='"
							+ tinosn
							+ "', @MOname='"
							+ moname
							+ "', @I_ResourceName='"
							+ resource
							+ "',@I_OrBitUserId='"
							+ userid
							+ "'  ,"
							+ "@I_ReturnMessage=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage";
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
	public void tinogetmo(Task task) {

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
					String sql = "select lot.MOId,MOName,pr.ProdName,ProductDescription,pd.ProductSpecification  from lot  "
							+ "inner join Product pd on lot.ProductId=pd.Productid "
							+ "inner join mo  on lot.MOId=mo.moid  "
							+ "inner join ProductRoot pr on pd.ProductId=pr.DefaultProductId where lot.LotSN='"
							+ Params + "'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin zhuan hua cheng de list=" + resDataSet);
					if (null == resDataSet) {
						result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行");
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
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	// 获取锡膏条码的信息！！
	public void tinogettino(Task task) {

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
					Log.i(TAG, "任务数据：：：" + Params);
					String sql = " SELECT EffectDate ,FactoryTime,TinolType,TinolName,TinolCode"
							+ " FROM dbo.TinolInfo INNER JOIN dbo.TinolTypeTB ON dbo.TinolInfo.TinolID = dbo.TinolTypeTB.TinolID"
							+ " WHERE TinolSN='" + Params + "'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin zhuan hua cheng de list=" + resDataSet);
					if (null == resDataSet) {
						result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行");
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
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

}
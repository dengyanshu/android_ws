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

public class BindpcbModel extends CommonModel {
	String moname;
	String mainsn;
	String num;
	String zisn;

	public void bindpcb(Task task) {

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

					moname = Params[0];
					mainsn = Params[1];
					num = Params[2];
					zisn = Params[3];

					Log.i(TAG, "任务数据是:" + Params);

					String sql = " declare @res int declare @message nvarchar(max) exec @res= TXN_SMT_SMTSNStart    "
							+ "  @moname='"
							+ moname
							+ "', @MakeUpCount='"
							+ num
							+ "', @SMTSN='"
							+ mainsn
							+ "',@LOTSNS='"
							+ zisn
							+ "'  ,"
							+ "   @I_ReturnMessage=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage";
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
	public void bindpcbgetmo(Task task) {

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
					String sql = "SELECT MOName,ProductName,ProductDescription,dbo.Lot.MakeUpCount,LotSN FROM lot   "
							+ "INNER JOIN  dbo.MO			ON dbo.Lot.MOId = dbo.MO.MOId   "
							+ "INNER JOIN  dbo.Product		ON product.ProductId=mo.ProductId  "
							+ "INNER JOIN  dbo.ProductRoot ON dbo.ProductRoot.ProductRootId=dbo.Product.ProductrootId   "
							+ "WHERE LotSN='" + Params + "'";
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

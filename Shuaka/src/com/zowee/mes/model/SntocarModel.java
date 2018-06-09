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

public class SntocarModel extends CommonModel {

	public void sntocar(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[] {"","","", "", "", "","","","",""};
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					String moid = Params[0];
					String carsn = Params[1];
					String mainsn = Params[2];
					String num = Params[3];
					String sn = Params[4];
					String flag = Params[5];
					String resourceid = Params[6];
					String resourcename = Params[7];
					String useid = Params[8];
					String usename = Params[9];

					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
							+ Params[2] + "," + Params[3] + "," + Params[4]
							+ "," + Params[5]);

					String sql = " declare @res int declare @message nvarchar(max) declare @InCarQty int exec @res= TXN_BatchInCarSMT    "
							+ "  @MOId='"
							+ moid
							+ "', @CarSN='"
							+ carsn
							+ "', @ParentSN ='"
							+ mainsn
							+ "',@MakeUpCount ="
							+ num
							+ "  ,"
							+ "  @SNList ='"
							+ sn
							+ "', @Flag ="
							+ flag
							+ ",@I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', @InCarQty  =@InCarQty out , "
							+ "   @I_ReturnMessage=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage,@InCarQty as InCarQty";
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

	public void checksinglesn(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[] {"", "", "", ""};
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					String moid = Params[0];
					String carsn = Params[1];
					String mainsn = Params[2];
					String sn = Params[3];

					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","+ Params[2] + "," + Params[3] );
					String sql = "declare @res int declare @message nvarchar(max) 	exec @res=Txn_BatchInCarSMT_CheckPcbaSN @I_ReturnMessage=@message  output,"+ 
					"@MOId='"+moid+"',@CarSN='"+carsn+"',@ParentSN ='"+mainsn+"',@SN='"+sn+"' select @res as I_ReturnValue,@message as I_ReturnMessage ";
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
					String sql = " SELECT lot.MOId,mo.MOName, dbo.ProductRoot.ProductName,Product.ProductDescription,product.MakeUpCount  "
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
	public void getMakeupcount(Task task) {

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
					String sql = " SELECT ISNULL(MakeUpCount,0) AS MakeUpCount FROM dbo.Lot  WITH(NOLOCK) WHERE LotSN='"+Params+"'  ";
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

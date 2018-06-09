package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;


public class InstrumentModel extends CommonModel 
{
	public void getworkcenter(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String   Params = (String) task.getTaskData();
					
					String sql = " SELECT dbo.Workcenter.WorkcenterId,WorkcenterName  FROM  dbo.Workcenter  INNER  JOIN  dbo.Resource  ON  dbo.Workcenter.WorkcenterId = dbo.Resource.WorkcenterId WHERE  ResourceId='"+Params+"'";
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
	
	//工单绑定获取工单信息
	public void getmo(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String   Params = (String) task.getTaskData();
					
					String sql = " SELECT  MOName,ProductName,ProductDescription FROM  mo    INNER  JOIN   dbo.Product  ON  dbo.MO.ProductId = dbo.Product.ProductId  "+
							"  INNER  JOIN   dbo.ProductRoot  ON  dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId  WHERE  MOName  LIKE  '%"+Params+"%'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet){
						
					}
					else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
						task.setTaskResult(resMapsLis);
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
	
	public void linesetup(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					    String[]   Params = (String[]) task.getTaskData();

						String resourceid = Params[0];
						String resourcename = Params[1];
						String useid = Params[2];
						String usename = Params[3];
						
						String workcenterid = Params[4];
						String sn = Params[5];
						
						String sql = " declare @res int    declare @message nvarchar(max)  exec @res= [InstrumentSetUpSaveData] "
								+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
								+"@WorkcenterId ='"+workcenterid+"',@InstrumentName ='"+sn+"',"
								+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
    
	
	public void removebinding(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					    String[]   Params = (String[]) task.getTaskData();

						String resourceid = Params[0];
						String resourcename = Params[1];
						String useid = Params[2];
						String usename = Params[3];
						
						String workcenterid = Params[4];
						String sn = Params[5];
						
						String sql = " declare @res int    declare @message nvarchar(max)  exec @res= [InstrumentSetUpDelBindingDoMethod] "
								+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
								+"@WorkcenterId ='"+workcenterid+"',@InstrumentName ='"+sn+"',"
								+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
	
	
	public void molinkworkcenter(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					    String[]   Params = (String[]) task.getTaskData();

						String resourceid = Params[0];
						String resourcename = Params[1];
						String useid = Params[2];
						String usename = Params[3];
						
						String workcenterid = Params[4];
						String sn = Params[5];
						String isbinding = Params[6];
						
						String sql = " declare @res int    declare @message nvarchar(max)  exec @res= [InstrumentToProduceSaveData] "
								+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
								+"@WorkcenterId ='"+workcenterid+"',@MOName ='"+sn+"',@IsBinding="+isbinding+","
								+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
	
	//获取所有工作中心
   public void getcenterlist(Task task) {
			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					try {
						if (null == task.getTaskData())
							return task;
						List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
						
						Soap soap = MesWebService.getMesEmptySoap();
						String sql = " SELECT  WorkcenterName FROM  dbo.Workcenter";
						soap.addWsProperty("SQLString", sql);
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
						List<String> resDataSet = MesWebService.WsDataParser
								.getResDatSet(envolop.bodyIn.toString());
						if (null == resDataSet){
							
						}
						else {
							List<HashMap<String, String>> resMapsLis = MesWebService
									.getResMapsLis(resDataSet);
							Log.i(TAG, "" + resMapsLis);
							task.setTaskResult(resMapsLis);
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
   
   
   //获取查询结果
   public void getqueryresult(Task task) {
			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					try {
						if (null == task.getTaskData())
							return task;
						 String   Params = (String) task.getTaskData();
						
						List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
						
						Soap soap = MesWebService.getMesEmptySoap();
						String sql = " SELECT InstrumentName,InstrumentType,InstrumentDescription,Status,WorkcenterName,IsBinding  FROM  dbo.InstrumentSetUp  INNER  JOIN  dbo.Instrument  ON  dbo.InstrumentSetUp.InstrumentId = dbo.Instrument.InstrumentId"+  
								"   INNER  JOIN   dbo.Workcenter ON  dbo.InstrumentSetUp.WorkcenterId=dbo.Workcenter.WorkcenterId   WHERE  WorkcenterName='"+Params+"'";
						soap.addWsProperty("SQLString", sql);
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
						List<String> resDataSet = MesWebService.WsDataParser
								.getResDatSet(envolop.bodyIn.toString());
						if (null == resDataSet){
							
						}
						else {
							List<HashMap<String, String>> resMapsLis = MesWebService
									.getResMapsLis(resDataSet);
							Log.i(TAG, "" + resMapsLis);
							task.setTaskResult(resMapsLis);
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

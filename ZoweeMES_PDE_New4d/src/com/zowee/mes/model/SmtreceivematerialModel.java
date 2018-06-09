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


public class SmtreceivematerialModel extends CommonModel 
{
	public void getworkcenter(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result;
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT  workcentername  FROM  workcenter WHERE  workcentername  LIKE '1B-2F%'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
						 result = MesWebService.getResMapsLis(resDataSet);
						if (null != result && 0 != result.size()) {
							task.setTaskResult(result);
						} else {
							
						}
						
					}
					
				} catch (Exception e) {
					Log.i(TAG, "这里有执行3");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	public void getworkcenteridbyworkcentername(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String sn= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT  workcenterid  FROM  workcenter  WHERE  workcentername='"+sn+"'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
					Log.i(TAG, "这里有执行ID");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	public void getmzs(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result;
					String sn= (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					
				//以前是直接25服务器查询 现在全部在56 但是表是建在25
//					String sql = "SELECT   lotsn,qty,productname  FROM  materialitemlot  "+
//							" INNER  JOIN  productroot  ON   materialitemlot.productid=productroot.defaultproductid "+
//							" WHERE  stockoutname  in (SELECT stockoutname  FROM materialitemlot WHERE lotsn='"+sn+"' ) AND  moid  in "+
//							" (SELECT moid FROM materialitemlot WHERE lotsn='"+sn+"' )";
					
					String sql="	SELECT   lotsn,qty,productname  FROM   [10.2.0.25].OrBitXE.DBO.MaterialItemLot AS materialitemlot "+
					" INNER  JOIN  [10.2.0.25].OrBitXE.DBO.productroot  ON   materialitemlot.productid=productroot.defaultproductid  " +
					"	WHERE  stockoutname  in (SELECT stockoutname  FROM [10.2.0.25].OrBitXE.DBO.MaterialItemLot WHERE lotsn='"+sn+"' ) AND  moid  in "+
					"  (SELECT moid FROM [10.2.0.25].OrBitXE.DBO.MaterialItemLot WHERE lotsn='"+sn+"')";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
						 result = MesWebService.getResMapsLis(resDataSet);
						if (null != result && 0 != result.size()) {
							task.setTaskResult(result);
						} else {
							
						}
						
					}
					
				} catch (Exception e) {
					Log.i(TAG, "这里有执行MZS");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	public void check(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					String[]   Params = (String[]) task.getTaskData();

					String workcenterid = Params[0];
					String usename = Params[1];
					String lotsn = Params[2];
					
					
					String resourceid = Params[3];
					String resourcename = Params[4];
					String useid = Params[5];
					String oldusename = Params[6];
					
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "declare @res int declare @message nvarchar(max) declare @stockoutname nvarchar(100)  exec @res= [Txn_MaterialIssueToReceiveCheck_Domethod] "
							+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+oldusename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
							+"  @WorkcenterID ='"+workcenterid+"',@UserName ='"+usename+"',@InputMaterialSN ='"+lotsn+"',"
							+ " @I_ReturnMessage=@message out,@I_StockOutName=@stockoutname out select @res as I_ReturnValue,@message as I_ReturnMessage,@stockoutname as stockoutname";
				
				
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
					Log.i(TAG, "这里有执行check");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	public void submit(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					String[]   Params = (String[]) task.getTaskData();

					String workcenterid = Params[0];
					String usename = Params[1];
					String lotsn = Params[2];
					String stockoutname = Params[3];
					
					
					
					String resourceid = Params[4];
					String resourcename = Params[5];
					String useid = Params[6];
					String oldusename = Params[7];
					
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "declare @res int declare @message nvarchar(max)  exec @res= [Txn_MaterialIssueToReceive_Domethod] "
							+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+oldusename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
							+" @WorkcenterID ='"+workcenterid+"',@UserName ='"+usename+"',@InputMaterialSN ='"+lotsn+"',@StockOutName='"+stockoutname+"',"
							+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
					Log.i(TAG, "这里有执行submit");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	public void getpcbanumincar(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String sn= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "select COUNT (lotid) InCarLotSNQty from DataChainLoadcar with(nolock) where CarSN='"+sn +"'and ISNULL(DataChainLoadcar.DefectcodeSn,'')=''";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
					Log.i(TAG, "这里有执行3");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	public void ipqcpcba(Task task) {

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
					
					String moname = Params[4];
					String car = Params[5];
					String lotsn = Params[6];
					String errorcode = Params[7];
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_SMTSpotCheckLotSnDoMethod] @MoSn ='"+moname+"',"
							+"@CarSn ='"+car+"',@LotSn ='"+lotsn+"',@DefectcodeSn ='"+errorcode+"',"
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
    
	
	public void ipqccar(Task task) {

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
					
					String moname = Params[4];
					String car = Params[5];
					String passorfail = Params[6];
					
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_SMTPCBA_IPQC_ALLCheckDoMethod] @MoSn ='"+moname+"',"
							+"@CarSn ='"+car+"',@QCCheckResult ='"+passorfail+"',"
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
	
}

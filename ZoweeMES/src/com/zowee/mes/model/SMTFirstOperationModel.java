package com.zowee.mes.model;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;
import android.widget.Toast;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.SnRuleUtil;
import com.zowee.mes.ws.MesSoapParser;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class SMTFirstOperationModel extends CommonModel {

	/**
	 * @param task
	 * @description 获取订单信息
	 */
	private String Owner = MyApplication.getAppOwner().toString().trim();

	public void SMT_ScanSnGetWO(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String lotSN = task.getTaskData().toString();
					lotSN=SnRuleUtil.getLotsn(MyApplication.getLonsnRule(), lotSN);
					String Sql;
					Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @res int  exec @res = MOName_ViewList_Android @I_ReturnMessage = @ReturnMessage out,@LotSN ='"
							+ lotSN
							+ "',@QueryParameter =S;  select @ReturnMessage as ReturnMsg,@res as result";
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet)
						result.put("Error", "获取订单信息失败");
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
							result.put("Error", "解析订单" + resDataSet.toString()
									+ "信息失败");
						}
					}
					task.setTaskResult(result);
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

	/**
	 * @param task
	 * @description 获取订单信息
	 */

	public void getWoInfo(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String lotWO = task.getTaskData().toString();
					String Sql = "SELECT top 1 MO.MOId ,mo.ProductId,MO.MOName,MO.PCBType,Product.MakeUpCount,ProductRoot.ProductName,(select top 1 * from dbo.SQL_split( Product.firstside,'/'))as ABSide "
							+ "FROM  MO LEFT JOIN ProductRoot on ProductRoot.DefaultProductId = MO.ProductId left join Product on Product.ProductRootId = "
							+ "ProductRoot.ProductRootId Where mo.MOStatus = '3' and mostdtype = 'S' and MOName='"
							+ lotWO + "'";
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet)
						result.put("Error", "获取订单信息失败");
					else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
						// String[] reses = new String[1];
						if (null != resMapsLis && 0 != resMapsLis.size()) {
							result = resMapsLis.get(0);

						} else {
							result.put("Error", "解析订单" + resDataSet.toString()
									+ "信息失败");
						}
					}
					task.setTaskResult(result);
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

	/**
	 * @param task
	 * @description SMT上线条码绑定
	 */

	public void SMTScanLable(Task task) // 旧上线扫描，扫描一个条码提交一交，
	{

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					HashMap<String, String> taskdata = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					String MOId;

					String MOName;
					String PCBType;
					String ProductName;
					String VendorPCBSN;
					String PCBSN;
					String MakeUpCount;
					String PCBSide;
					String HidSN;
					taskdata = (HashMap<String, String>) task.getTaskData();
					MOId = taskdata.get("MOId");
					MOName = taskdata.get("MOName");
					PCBType = taskdata.get("PCBType");
					ProductName = taskdata.get("ProductName");
					VendorPCBSN = taskdata.get("VendorPCBSN");
					PCBSN = taskdata.get("PCBSN");
					MakeUpCount = taskdata.get("MakeUpCount");
					PCBSide = taskdata.get("PCBSide");
					HidSN = taskdata.get("HidSN");
					Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @res int declare @ExceptionFieldName nvarchar(100) exec @res = Txn_SprayingScanAndroid @I_ReturnMessage=@ReturnMessage out,@I_ExceptionFieldName=@ExceptionFieldName  out,@I_OrBitUserId='"
							+ MyApplication.getMseUser().getUserId()
							+ "',@I_OrBitUserName='"
							+ MyApplication.getMseUser().getUserName()
							+ "',@MOId='"
							+ MOId
							+ "',@MOName='"
							+ MOName
							+ "',@MakeupCount='"
							+ MakeUpCount
							+ "',@SNScan ='"
							+ PCBSN
							+ "',@HideSN='"
							+ HidSN
							+ "',@ProductName='"
							+ ProductName
							+ "',@PCBType='"
							+ PCBType
							+ "',@ABSide='"
							+ PCBSide
							+ "',@PCBSN='"
							+ VendorPCBSN
							+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@res as result";
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
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
					Log.i(TAG, e.toString());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	/**
	 * @param task
	 * @description SMT上线条码绑定
	 */

	public void SMTScanLable2(Task task) // 新上线扫描，扫描所有条码再提交，
	{

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					HashMap<String, String> taskdata = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					String MOId;
					String MOName;
					String PCBType;
					String ProductName;
					String VendorPCBSN;
					String MakeUpCount;
					String PCBSide;
					String LotSNList;
					String customer;
					String  tinosn;
					taskdata = (HashMap<String, String>) task.getTaskData();
					MOId = taskdata.get("MOId");
					MOName = taskdata.get("MOName");
					PCBType = taskdata.get("PCBType");
					ProductName = taskdata.get("ProductName");
					VendorPCBSN = taskdata.get("VendorPCBSN");
					LotSNList = taskdata.get("LotSNList");
					MakeUpCount = taskdata.get("MakeUpCount");
					PCBSide = taskdata.get("PCBSide");
					customer = taskdata.get("customer");
					tinosn=taskdata.get("tinosn");
					if (customer.equalsIgnoreCase("oppo")) // for OPPO
						Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @res int declare @ExceptionFieldName nvarchar(100) exec @res = Txn_SprayingScanAndTakeMZ_Batch @I_ReturnMessage=@ReturnMessage out,@I_ExceptionFieldName=@ExceptionFieldName  out,@I_OrBitUserId='"
								+ MyApplication.getMseUser().getUserId()
								+ "',@I_OrBitUserName='"
								+ MyApplication.getMseUser().getUserName()
								+ "',@I_ResourceName='"
								+ Owner
								+ "',@MOId='"
								+ MOId
								+ "',@MOName='"
								+ MOName
								+ "',@MakeupCount='"
								+ MakeUpCount
								+ "',@LotSNList ='"
								+ LotSNList
								+ "',@ProductName='"
								+ ProductName
								+ "',@PCBType='"+ PCBType+ "',@TinolSN='"+tinosn+"', @ABSide='"+ PCBSide+ "',@PCBSN='"
								+ VendorPCBSN
								+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@res as result";
					else
						Sql = "declare @ReturnMessage nvarchar(max) declare @res int declare @ExceptionFieldName nvarchar(100) exec @res = Txn_SprayingScan_Batch @I_ReturnMessage=@ReturnMessage out,@I_ExceptionFieldName=@ExceptionFieldName  out,@I_OrBitUserId='"
								+ MyApplication.getMseUser().getUserId()
								+ "',@I_OrBitUserName='"
								+ MyApplication.getMseUser().getUserName()
								+ "',@I_ResourceName='"
								+ Owner
								+ "',@MOId='"
								+ MOId
								+ "',@MOName='"
								+ MOName
								+ "',@MakeupCount='"
								+ MakeUpCount
								+ "',@LotSNList ='"
								+ LotSNList
								+ "',@ProductName='"
								+ ProductName
								+ "',@PCBType='"+ PCBType+ "',@TinolSN='"+tinosn+"',@ABSide='"+ PCBSide+ "',@PCBSN='"
								+ VendorPCBSN
								+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@res as result";
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
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
					Log.i(TAG, e.toString());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	
	/**
	 * @param task
	 * @description 华为二维码处理
	 */

	public void SMTScanLable3(Task task) // 新上线扫描，扫描所有条码再提交，
	{

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					HashMap<String, String> taskdata = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					String MOId;
					String MOName;
					String PCBType;
					String ProductName;
					String VendorPCBSN;
					String MakeUpCount;
					String PCBSide;
					String LotSNList;
					String customer;
					String  tinosn;
					taskdata = (HashMap<String, String>) task.getTaskData();
					MOId = taskdata.get("MOId");
					MOName = taskdata.get("MOName");
					PCBType = taskdata.get("PCBType");
					ProductName = taskdata.get("ProductName");
					VendorPCBSN = taskdata.get("VendorPCBSN");
					LotSNList = taskdata.get("LotSNList");
					MakeUpCount = taskdata.get("MakeUpCount");
					PCBSide = taskdata.get("PCBSide");
					customer = taskdata.get("customer");
					tinosn=taskdata.get("tinosn");
				
					Sql = "declare @ReturnMessage nvarchar(max) declare @res int declare @ExceptionFieldName nvarchar(100) exec @res = Txn_prepare_SprayingScan_Batch_4d " +
								" @I_ReturnMessage=@ReturnMessage out,@I_ExceptionFieldName=@ExceptionFieldName  out,@LotSNList ='"+LotSNList+"',@moid='"+MOId+"' "   +
	                            " select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@res as result";
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
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
					Log.i(TAG, e.toString());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
}

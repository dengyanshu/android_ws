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
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class DIPStartModel extends CommonModel {

	private String userID;
	private String UserName;
	private String LotSN;
	private String MOID;
	private String WorkFlowId;
	private String ProductName;
	private String WorkflowName;
	private String ProductId;
	private String I_ResourceName;
	private String KeyWord;
	private String Length;
	private String SNType;
	private String I_ResourceId;
	private String MOName;

	String[] Params = new String[] { "", "", "", "", "", "", "", "", "", "",
			"", "" };

	/**
	 * @param task
	 * @description 获得DIP工单信息
	 */
	public void GetStartDIPMOinfo(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {

					String MOName = (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty(
							"SQLString",
							"SELECT top 1 dbo.MO.MOId,MO.MOName, Productroot.productName + '   ' + ISNULL(Product.Productcolor,'') AS ProductName,"
									+ "ISNULL(Product.ProductMobileCode,'') + '   ' + Product.ProductDescription AS ProductDescription,"
									+ "Product.ProductId,MO.WorkflowId,(CASE ISNULL(IsBackMO,'') WHEN '1' THEN WorkflowRoot.WorkflowName+' '+Workflow.WorkflowRevision "
									+ "ELSE wr.WorkflowName+' '+w.WorkflowRevision END) AS WorkflowName,SO.SOID,SO.SOName as SOName, SO.CustMO AS CustMO FROM dbo.MO with(nolock) inner join "
									+ "dbo.Product with(nolock) inner join ProductRoot with(nolock) ON Product.ProductrootID = Productroot.productrootID "
									+ "ON dbo.MO.ProductId = dbo.Product.ProductId inner join Workflow with(nolock) on MO.WorkflowId=Workflow.WorkflowId "
									+ "inner join WorkflowRoot with(nolock) on Workflow.WorkflowRootId=WorkflowRoot.WorkflowRootId "
									+ "inner join Workflow w with(nolock) on Product.WorkflowId=w.WorkflowId "
									+ "inner join WorkflowRoot wr with(nolock) on w.WorkflowRootId=wr.WorkflowRootId "
									+ "Left outer join SO  with(nolock) inner join SORoot with(nolock) On SO.SORootID = SORoot.SORootID "
									+ "ON MO.Soid = SO.SOid WHERE   MO.MOName ='"
									+ MOName + "'");

					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "" + resMapsLis);

					if (null != resMapsLis && 0 != resMapsLis.size()) {
						List<HashMap<String, String>> parasMapsLis = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> tempMap = null;
						// for(int i=0;i<resMapsLis.size();i++)
						for (int i = 0; i < 1; i++) {
							tempMap = resMapsLis.get(i);
							if (tempMap.containsKey("MOName")) {
								parasMapsLis.add(tempMap);
							}

						}
						// task.setTaskResult(parasMapsLis);
						task.setTaskResult(tempMap);
					}
				}

				catch (Exception e) {
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	/**
	 * @param task
	 * @description DIP启动
	 */
	public void StartDIP(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();

					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[]) task.getTaskData();
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();
					LotSN = Params[0];
					MOID = Params[1];
					WorkFlowId = Params[2];
					ProductName = Params[3];
					WorkflowName = Params[4];
					ProductId = Params[5];
					I_ResourceName = Params[6];
					I_ResourceId = Params[7];
					MOName = Params[8];
					soap.addWsProperty(
							"SQLString",
							"declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_DIP_LotStart @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='"
									+ UserName
									+ "',@I_OrBitUserId='"
									+ userID
									+ "',@LotSN='"
									+ LotSN
									+ "',@MOID='"
									+ MOID
									+ "',@WorkFlowId='"
									+ WorkFlowId
									+ "',@ProductName='"
									+ ProductName
									+ "',@WorkflowName='"
									+ WorkflowName
									+ "',@ProductId='"
									+ ProductId
									+ "',@ShiftID='',@I_ResourceName='"
									+ I_ResourceName
									+ "',@I_ResourceId='"
									+ I_ResourceId
									+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result;");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "" + resMapsLis);
					// String[] reses = new String[1];
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						for (int i = 0; i < resMapsLis.size(); i++) {
							result.putAll(resMapsLis.get(i));
						}
						task.setTaskResult(result);
					}
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
	 * @description SMT to DIP启动
	 */
	public void StartSMTToDIP(Task task) // Txn_DIP_TXNLotStart
	{

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[]) task.getTaskData();
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();
					LotSN = Params[0];
					MOID = Params[1];
					WorkFlowId = Params[2];
					ProductName = Params[3];
					WorkflowName = Params[4];
					ProductId = Params[5];
					I_ResourceName = Params[6];
					I_ResourceId = Params[7];
					MOName = Params[8];
					soap.addWsProperty(
							"SQLString",
							"declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_DIP_TXNLotStart @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='"
									+ UserName
									+ "',@I_OrBitUserId='"
									+ userID
									+ "',@LotSN='"
									+ LotSN
									+ "',@MOID='"
									+ MOID
									+ "',@WorkFlowId='"
									+ WorkFlowId
									+ "',@ProductName='"
									+ ProductName
									+ "',@WorkflowName='"
									+ WorkflowName
									+ "',@ProductId='"
									+ ProductId
									+ "',@ShiftID='',@I_ResourceName='"
									+ I_ResourceName
									+ "',@I_ResourceId='"
									+ I_ResourceId
									+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result;");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "" + resMapsLis);
					// String[] reses = new String[1];
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						for (int i = 0; i < resMapsLis.size(); i++) {
							result.putAll(resMapsLis.get(i));
						}
						task.setTaskResult(result);
					}
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
	 * @description 委外DIP启动
	 */
	public void StartDIP_Out(Task task) // Txn_DIP_LotStart_Out
	{

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[]) task.getTaskData();
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();
					LotSN = Params[0];
					MOID = Params[1];
					WorkFlowId = Params[2];
					ProductName = Params[3];
					WorkflowName = Params[4];
					ProductId = Params[5];
					I_ResourceName = Params[6];
					I_ResourceId = Params[7];
					MOName = Params[8];
					KeyWord = Params[9];
					Length = Params[10];
					SNType = Params[11];

					soap.addWsProperty(
							"SQLString",
							"declare @ReturnMessage nvarchar(max)  = ' ' declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_DIP_LotStart_Out @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='"
									+ UserName
									+ "',@I_OrBitUserId='"
									+ userID
									+ "',@LotSN='"
									+ LotSN
									+ "',@MOID='"
									+ MOID
									+ "',@WorkFlowId='"
									+ WorkFlowId
									+ "',@ProductName='"
									+ ProductName
									+ "',@WorkflowName='"
									+ WorkflowName
									+ "',@ProductId='"
									+ ProductId
									+ "',@I_ResourceName='"
									+ I_ResourceName
									+ "',@KeyWord='"
									+ KeyWord
									+ "',@Length='"
									+ Length
									+ "',@SNType='"
									+ SNType
									+ "',@I_ResourceId='"
									+ I_ResourceId
									+ "',@MOName='"
									+ MOName
									+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result;");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "" + resMapsLis);
					// String[] reses = new String[1];
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						for (int i = 0; i < resMapsLis.size(); i++) {
							result.putAll(resMapsLis.get(i));
						}
						task.setTaskResult(result);
					}
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
	 * @description 扣料及DIP启动
	 */
	public void Dip_material_start(Task task) // Txn_LotSNStartBindPartsNew
	{

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[]) task.getTaskData();
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();
					LotSN = Params[0];
					MOID = Params[1];
					WorkFlowId = Params[2];
					ProductName = Params[3];
					WorkflowName = Params[4];
					ProductId = Params[5];
					I_ResourceName = Params[6];
					I_ResourceId = Params[7];
					MOName = Params[8];
					KeyWord = Params[9];
					Length = Params[10];
					SNType = Params[11];

					soap.addWsProperty(
							"SQLString",
							"declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_LotSNStartBindPartsNew @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='"
									+ UserName
									+ "',@I_OrBitUserId='"
									+ userID
									+ "',@I_ResourceName='"
									+ I_ResourceName
									+ "',@I_ResourceId='"
									+ I_ResourceId
									+ "',@LotSN='"
									+ LotSN
									+ "',@MOID='"
									+ MOID
									+ "',@I_PlugInCommand='OTSTD'"
									+ " select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result;");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "" + resMapsLis);
					// String[] reses = new String[1];
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						for (int i = 0; i < resMapsLis.size(); i++) {
							result.putAll(resMapsLis.get(i));
						}
						task.setTaskResult(result);
					}
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

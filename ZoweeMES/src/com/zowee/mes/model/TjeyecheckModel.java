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

public class TjeyecheckModel extends CommonModel {

	private String userID;
	private String UserName;
	private String LotSN;
	private String MOID;
	private String I_ResourceName;
	private String I_ResourceId;
	private String PreIsErrCode;
	private String PreErrCodeId;
	private String PreErrCodeName;
	String[] Params = new String[] { "", "", "", "", "", "", "", "", "" };

	/**
	 * @param task
	 * @description 获得 工单信息
	 */
	public void GetMOinfo(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {

					String MOName = (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql = "SELECT top 1 MO.MOId,MO.MOName AS MoName,dbo.ProductRoot.ProductName+(CASE WHEN ISNULL(Product.ProductDescript,'')='' THEN '' ELSE '/'+Product.ProductDescript END)  AS ProductID,"
							+ " dbo.WorkflowRoot.WorkflowName AS WorkFlow, MO.MOQtyRequired AS quantity FROM dbo.MO WITH(NOLOCK)  LEFT JOIN dbo.Product WITH(NOLOCK)  ON dbo.MO.ProductId = dbo.Product.ProductId"
							+ " LEFT JOIN dbo.ProductRoot WITH(NOLOCK)  ON dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId  LEFT JOIN dbo.Workflow WITH(NOLOCK)  ON MO.WorkflowId=dbo.Workflow.WorkflowId"
							+ " LEFT JOIN dbo.WorkflowRoot WITH(NOLOCK)  ON dbo.Workflow.WorkflowRootId = dbo.WorkflowRoot.WorkflowRootId   WHERE MO.MOName ='"
							+ MOName + "'";
					soap.addWsProperty("SQLString", Sql);
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
	 * @description eye check start
	 */
	public void Tjeyecheck(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String Sql;
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[]) task.getTaskData();
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();

					MOID = Params[0];
					LotSN = Params[1];
					I_ResourceName = Params[2];
					I_ResourceId = Params[3];
					PreIsErrCode = Params[4];
					PreErrCodeId = Params[5];
					PreErrCodeName = Params[6];

					Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100) declare @ErrorCode BIT declare @ErrCodeId CHAR(12)='0' declare @ErrCodeNote NVARCHAR(200)='0' exec @Return_value = Txn_FaceCheck @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_PlugInCommand='FACEC',@I_OrBitUserName='"
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
							+ "',@PreIsErrorCode='"
							+ PreIsErrCode
							+ "',@PreErrorCodeId='"
							+ PreErrCodeId
							+ "',@PreErrorCodeName='"
							+ PreErrCodeName
							+ "',@IsErrorCode=@ErrorCode out,@ErrorCodeId=@ErrCodeId out,"
							+ "@ErrorCodeNote =@ErrCodeNote out select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result,@ErrorCode as ErrCode,@ErrCodeId as ErrCodeId,@ErrCodeNote as ErrCodeNote;";
					soap.addWsProperty("SQLString", Sql);
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

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

public class DIPMobileDipStartModel extends CommonModel {

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
	private String ShiftID;
	private String ShiftName;
	private String SOName;
	private String CustMo;
	private String MOName;
	String[] Params = new String[] { "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "" };

	/**
	 * @param task
	 * @description 手机SMT转DIP启动
	 */
	public void StartMobileDIP(Task task) // Txn_MobilePhoneDIP_LotStart
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
					ShiftID = Params[11];
					ShiftName = Params[12];
					SOName = Params[13];
					CustMo = Params[14];

					soap.addWsProperty(
							"SQLString",
							"declare @ReturnMessage nvarchar(max)  = ' ' declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_MobilePhoneDIP_LotStart @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='"
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
									+ "',@ShiftID='"
									+ ShiftID
									+ "',@ShiftName='"
									+ ShiftName
									+ "',@SOName='"
									+ SOName
									+ "',@CustMo='"
									+ CustMo
									+ "',@KeyWord='"
									+ KeyWord
									+ "',@Length='"
									+ Length
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

}

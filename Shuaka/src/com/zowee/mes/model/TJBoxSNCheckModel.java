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

public class TJBoxSNCheckModel extends CommonModel {

	private String userID;
	private String UserName;
	private String BoxNumber;
	private String LotSN;

	String[] Params = new String[] { "", "" };

	/**
	 * @param task
	 * @description box and SN check
	 */
	public void TjBoxSNCheck(Task task) {

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

					BoxNumber = Params[0];
					LotSN = Params[1];

					Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100)  exec @Return_value = Txn_BoxLotSNCheck @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='"
							+ UserName
							+ "',@I_OrBitUserId='"
							+ userID
							+ "',@BoxSN='"
							+ BoxNumber
							+ "',@LotSN='"
							+ LotSN
							+ "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result ;";
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

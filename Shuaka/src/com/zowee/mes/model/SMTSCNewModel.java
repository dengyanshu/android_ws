package com.zowee.mes.model;

import java.util.ArrayList;
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
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class SMTSCNewModel extends CommonModel {

	/**
	 * @param task
	 * @description SMT 贴片扫描
	 */

	String userID;
	String userName;
	String owner;
	String scanSN;
	String ScanSNEnd;
	String OnepanelQTy;
	String StickType;

	String railName; // 记录哪个轨道传送上来的信息
	String side;
	String[] Params = new String[] { "", "", "", "", "", "", "", "" };

	/**
	 * @param task
	 * @description SMT new 贴片扫描
	 */
	public void SMTSC_New(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				HashMap<String, String> result = new HashMap<String, String>();
				List<String> resDataSet = null;
				synchronized (this) {
					try {
						if (null == task.getTaskData())
							return task;

						Soap soap = MesWebService.getMesEmptySoap();
						Params = (String[]) task.getTaskData();
						userID = Params[0];
						StickType = Params[1];
						userName = MyApplication.getMseUser().getUserName(); 
						owner = Params[2];
						scanSN = Params[3].trim();
						railName = Params[4];
						side = Params[5];
						ScanSNEnd = Params[6];
						OnepanelQTy = Params[7];
						result.put("Rail", railName); // 返回结果中添加轨道信息
						String Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100) declare @Return_FinishQty int='0' exec @Return_value = TXN_SMT_PCBAT @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@Returnint =@Return_FinishQty out,@I_OrBitUserName='"
								+ userName
								+ "',@I_PlugInCommand='',@I_OrBitUserId='"
								+ userID
								+ "',@I_ResourceName='"
								+ owner
								+ "',@StartLotSN='"
								+ scanSN
								+ "',@EndLotSN ='"
								+ ScanSNEnd
								+ "',@MakeUpCount ="
								+ OnepanelQTy
								+ ",@OperatorType ='"
								+ StickType
								+ "',@ABSide='"
								+ side
								+ "' select @ReturnMessage as I_ReturnMessage,@Return_value as I_ReturnValue,@Return_FinishQty as FinishQty";//
						soap.addWsProperty("SQLString", Sql);
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
						resDataSet = MesWebService.WsDataParser
								.getResDatSet(envolop.bodyIn.toString());
						if (null == resDataSet)
							result.put("Error", "连接MES失败_板边");
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
								result.put("Error", resDataSet.toString()
										+ "is Error Message");
							}
						}

					} catch (Exception e) {
						// MesUtils.netConnFail(task.getActivity());
						result.put("Error", "解析" + resDataSet.toString()
								+ "回传信息失败");
						Log.i(TAG, e.toString());
					} finally {
						task.setTaskResult(result);
					}

					return task;
				}
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);

	}

}

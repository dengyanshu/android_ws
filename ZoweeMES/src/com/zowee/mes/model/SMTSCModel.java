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
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class SMTSCModel extends CommonModel {

	/**
	 * @param task
	 * @description SMT 贴片扫描
	 */

	String userID;
	String userName;
	String owner;
	String scanSN;
	String railName; // 记录哪个轨道传送上来的信息
	String side;

	public void SMTSC(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				HashMap<String, String> result = new HashMap<String, String>();
				List<String> resDataSet = null;
				synchronized (this) {
					try {
						if (null == task.getTaskData())
							return task;

						String[] Params = new String[] { "", "", "", "", "", "" };
						Soap soap = MesWebService.getMesEmptySoap();
						Params = (String[]) task.getTaskData();
						userID = Params[0];
						userName = MyApplication.getMseUser().getUserName(); // changed
																				// by
																				// ybj
																				// 20130713
						owner = Params[2];
						scanSN = Params[3].trim();
						scanSN=SnRuleUtil.getLotsn(MyApplication.getLonsnRule(), scanSN);
						
						
						railName = Params[4];
						side = Params[5];
						result.put("Rail", railName); // 返回结果中添加轨道信息
						String Sql = "declare @I_ReturnMessage nvarchar(max)  = ' '  declare @res int declare @I_ExceptionFieldName nvarchar(100) exec @res = Txn_SMTIssueExeNew '', @I_ReturnMessage out,@I_ExceptionFieldName  out,'1','','"
								+ userID
								+ "','"
								+ userName
								+ "','','"
								+ owner
								+ "','','','','','"
								+ scanSN
								+ "','true','"
								+ side
								+ "' select @I_ReturnMessage as ReturnMsg, @I_ExceptionFieldName as exception,@res as result";
						soap.addWsProperty("SQLString", Sql);
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
						resDataSet = MesWebService.WsDataParser
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
	
	public void SMTSC_2(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				HashMap<String, String> result = new HashMap<String, String>();
				List<String> resDataSet = null;
				synchronized (this) {
					try {
						if (null == task.getTaskData())
							return task;

						String[] Params = new String[] { "", "", "", "", "", "" };
						Soap soap = MesWebService.getMesEmptySoap();
						Params = (String[]) task.getTaskData();
						userID = Params[0];
						userName = MyApplication.getMseUser().getUserName(); // changed
																				// by
																				// ybj
																				// 20130713
						owner = Params[2];
						scanSN = Params[3].trim();
						scanSN=SnRuleUtil.getLotsn(MyApplication.getLonsnRule(), scanSN);
						railName = Params[4];
						side = Params[5];
						result.put("Rail", railName); // 返回结果中添加轨道信息---RailA
						String Sql = "declare @I_ReturnMessage nvarchar(max)  = ' '  declare @res int declare @I_ExceptionFieldName nvarchar(100) exec @res = Txn_SMTIssueExeNew4d '', @I_ReturnMessage out,@I_ExceptionFieldName  out,'1','','"
								+ userID
								+ "','"
								+ userName
								+ "','','"
								+ owner
								+ "','','','','','"
								+ scanSN
								+ "','true','"+side+"','"+railName+""
								+ "' select @I_ReturnMessage as ReturnMsg, @I_ExceptionFieldName as exception,@res as result";
						soap.addWsProperty("SQLString", Sql);
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.d(TAG, "bodyIn=" + envolop.bodyIn.toString());
						resDataSet = MesWebService.WsDataParser
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

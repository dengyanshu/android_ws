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

/**
 * @author Administrator
 * @description SMT上料对料的业务逻辑类
 */
public class GetMOnameModel extends CommonModel {

	private final static String MONAME = "MOName";// 工单

	/**
	 * @param task
	 * @description 通过MOSTDType获得有效工单号
	 */
	public void getMoNamesByMOSTdType(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					String MOSTDType = "";
					MOSTDType = (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql = "select MOName from MO where MOStatus = '3' and MOSTDType = '"
							+ MOSTDType + "'  order by mo.PlannedDateFrom desc";
					if (MOSTDType.isEmpty())
						Sql = "select MOName from MO where MOStatus = '3' order by mo.PlannedDateFrom desc";
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
						for (int i = 0; i < resMapsLis.size(); i++) {
							tempMap = resMapsLis.get(i);
							if (tempMap.containsKey(MONAME)) {
								parasMapsLis.add(tempMap);
							}
						}

						task.setTaskResult(parasMapsLis);
					}
				}
				// }
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
	 * @description 获取SN类型
	 */
	public void GetDipSNType(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {

					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "exec StartSNType_ViewList");
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
						for (int i = 0; i < resMapsLis.size(); i++) {
							tempMap = resMapsLis.get(i);
							if (tempMap.containsKey("批号类别")) {
								parasMapsLis.add(tempMap);
							}

						}
						task.setTaskResult(parasMapsLis);
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
	 * @description 通过资源名获取资源ID
	 */
	public void GetResourceId(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					String ResourceName = "";
					ResourceName = (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString",
							"select ResourceId from Resource where ResourceName='"
									+ ResourceName + "'");
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
						for (int i = 0; i < resMapsLis.size(); i++) {
							tempMap = resMapsLis.get(i);
							if (tempMap.containsKey("ResourceId")) {
								parasMapsLis.add(tempMap);
							}

						}
						task.setTaskResult(parasMapsLis);
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

}

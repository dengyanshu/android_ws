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
public class FeedOnMateriModel extends CommonModel {

	private final static String MONAME = "MOName";// 工单
	private final static String SMTMOUNTID = "SMTMountId";
	private final static String COLUMN_1 = "Column1";
	private final static String COLUMN_2 = "Column2";
	private final static String COLUMN_3 = "Column3";

	/**
	 * @param task
	 * @description 获得SMT上料对料 有效工单
	 */
	public void getNeceParams(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {

					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString",
							"select MOName from MO where MOStatus = '3' and MOSTDType = 'S';");
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
	 * @description 物料上料
	 */
	public void feedOnMaterial(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String[] paras = (String[]) task.getTaskData();
					String moName = paras[0];
					String smtMountId = paras[1];
					String staLotSN = paras[2];
					String lotSN = paras[3];
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty(
							"SQLString",
							" declare @ReturnMessage nvarchar(max) = ' '  declare @res int  exec @res = Txn_SMTItemCheck @ReturnMessage output,'','"
									+ MyApplication.getMseUser().getUserId()
									+ "','','"
									+ moName
									+ "','"
									+ smtMountId
									+ "','"
									+ staLotSN
									+ "','"
									+ lotSN
									+ "' select @res,@ReturnMessage;");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());

					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					// Log.i(TAG, ""+resMapsLis);
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						String[] reses = new String[2];
						// Log.i(TAG,"COME IN3");
						for (int i = 0; i < resMapsLis.size(); i++) {
							HashMap<String, String> tempMap = resMapsLis.get(i);
							if (tempMap.containsKey(COLUMN_1))
								reses[0] = tempMap.get(COLUMN_1);
							if (tempMap.containsKey(COLUMN_2))
								reses[1] = tempMap.get(COLUMN_2);
							// Log.i(TAG, tempMap.get(COLUMN_3));
						}
						// Log.i(TAG, ""+reses);
						task.setTaskResult(reses);
					}
					// Log.i(TAG,"COME IN2");
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
	 * 
	 * @description 获得SMTMountId
	 */
	public void getSMTMountIdParam(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {

					if (null == task.getTaskData())
						return task;
					String staLot = (String) task.getTaskData();
					String docNo = staLot.substring(0, 3);
					String staNo = staLot.substring(3, 4);
					String sLotNo = "";
					// if(staLot.length()>=14)
					Log.i(TAG, staLot + " " + docNo + " " + staNo + " "
							+ sLotNo);
					// sLotNo = staLot.substring(4,14);
					// else
					sLotNo = staLot.substring(4);
					Soap soap = MesWebService.getMesEmptySoap();
					// soap.addWsProperty("SQLString",
					// "Select o.MOName,msm.SMTMountId from SMTMount s inner join "+
					// " SMTMountItem si on s.SMTMountId = si.SMTMountId inner join MO_SMTMount msm on si.SMTMountId = msm.SMTMountId inner join MO o on msm.MOId = o.MOId where s.SMTLineNO = '"+proLine+"' and s.StationNo = '"+stationNo+"' and si.SLotNO = '"+SlotNo+"'");
					soap.addWsProperty(
							"SQLString",
							"Select top 1 s.SMTMountId from SMTMount s inner join SMTMountItem sm on s.SMTMountId = sm.SMTMountId  where SMTLineNO = '"
									+ docNo
									+ "' and StationNo = '"
									+ staNo
									+ "' and sm.SLotNo = '" + sLotNo + "';");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					// if(null==resDataSet)
					// return task;//后期可能采用通知的方法
					// else
					// {
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "" + resMapsLis);

					if (null != resMapsLis && 0 != resMapsLis.size()) {
						String smtMountId = "";
						for (int i = 0; i < resMapsLis.size(); i++) {
							HashMap<String, String> tempMap = resMapsLis.get(i);
							// HashMap<String,String> paraItem = new
							// HashMap<String,String>();
							// if(tempMap.containsKey(MONAME)&&tempMap.containsKey(SMTMOUNTID))
							// {
							// paraItem.put(tempMap.get(MONAME),
							// tempMap.get(SMTMOUNTID));
							// parasMapsLis.add(paraItem);
							// }
							if (tempMap.containsKey(SMTMOUNTID))
								smtMountId = tempMap.get(SMTMOUNTID);
							// Log.i(TAG, ""+smtMountId);
						}

						task.setTaskResult(smtMountId);
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

}

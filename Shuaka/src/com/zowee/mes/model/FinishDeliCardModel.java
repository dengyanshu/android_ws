package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.DateUtils;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 卡板出货的业务逻辑类
 */
public class FinishDeliCardModel extends CommonModel {

	private static final String DNNAME = "DNName";// 出货单号
	private static final String CUSTOMERNAME = "CustomerName";// 客户名
	private static final String CUSTOMERDESCRIPTION = "CustomerDescription";// 用户全名
	private static final String SOROOTID = "SORootID";
	private static final String SOROOTNAME = "SORootName";
	private static final String SONAME = "SOName";

	/**
	 * @param task
	 * @description 获取订单数据集 及所关联的信息
	 */
	public void getNecessParams(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String soRootId = task.getTaskData().toString();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString",
							"select sr.SORootName from  SORoot sr where sr.SORootID = '"
									+ soRootId + "';");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					// Log.i(TAG, ""+resMapsLis);
					List<HashMap<String, String>> lisMaps = new ArrayList<HashMap<String, String>>();
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						for (int i = 0; i < resMapsLis.size(); i++) {
							HashMap<String, String> tempMap = resMapsLis.get(i);
							if (tempMap.containsKey(SOROOTNAME)
									&& !StringUtils.isEmpty(tempMap
											.get(SOROOTNAME))) {
								// Log.i(TAG,"COME IN");
								HashMap<String, String> mapItem = new HashMap<String, String>();
								// mapItem.put(SONAME, tempMap.get(SONAME));
								mapItem.put(SOROOTNAME, tempMap.get(SOROOTNAME));
								// mapItem.put(SOID, tempMap.get(SOID));
								lisMaps.add(mapItem);
							}
						}
					}
					task.setTaskResult(lisMaps);
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
	 * @description 卡板出货
	 */
	public void FinishDeliCard(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String[] params = (String[]) task.getTaskData();
					String datetime = params[5];
					String dnName = params[0];
					String cardSN = params[1];
					String pierNum = params[2];
					String soRootId = params[3];
					String soRootName = params[4];
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty(
							"SQLString",
							"declare @retMsg nvarchar(max) declare @excep nvarchar(100) declare @retRes int exec @retRes = Txn_FinishedProductDelivery '',@retMsg output,@excep output,"
									+ "'','','"
									+ MyApplication.getMseUser().getUserId()
									+ "','"
									+ MyApplication.getMseUser().getUserName()
									+ "','','','','','','','"
									+ datetime
									+ "','"
									+ dnName
									+ "','"
									+ cardSN
									+ "','"
									+ pierNum
									+ "','"
									+ soRootId
									+ "','"
									+ soRootName + "'  select @retRes,@retMsg");
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
						String[] reses = new String[] { "", "" };
						for (int i = 0; i < resMapsLis.size(); i++) {
							HashMap<String, String> tempMap = resMapsLis.get(i);
							if (tempMap.containsKey(COLUMN_1))
								reses[0] = tempMap.get(COLUMN_1);
							if (tempMap.containsKey(COLUMN_2))
								reses[1] = tempMap.get(COLUMN_2);
						}
						// Log.i(TAG, "RES:"+reses[0]+" LOG: "+reses[1]);
						task.setTaskResult(reses);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
	 * 
	 * @description 获取出货单号 和与之关联的信息
	 */
	public void getDNNames(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String checkStr = task.getTaskData().toString();//
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty(
							"SQLString",
							"select d.DNName,d.SORootID,c.CustomerName,c.CustomerDescription  from  DN d inner join  Customer c on d.CustomerID = c.CustomerId where d.DNName like '%"
									+ checkStr + "%'  order by d.DNName asc ; ");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					// Log.i(TAG, ""+resMapsLis);
					List<HashMap<String, String>> lisMaps = new ArrayList<HashMap<String, String>>();
					if (null != resMapsLis && 0 != resMapsLis.size()) {
						for (int i = 0; i < resMapsLis.size(); i++) {
							HashMap<String, String> tempMap = resMapsLis.get(i);
							if (tempMap.containsKey(DNNAME)
									&& !StringUtils
											.isEmpty(tempMap.get(DNNAME))
									&& tempMap.containsKey(SOROOTID)
									&& !StringUtils.isEmpty(tempMap
											.get(SOROOTID))) {
								HashMap<String, String> mapItem = new HashMap<String, String>();

								mapItem.put(DNNAME, tempMap.get(DNNAME));
								mapItem.put(SOROOTID, tempMap.get(SOROOTID));
								mapItem.put(CUSTOMERNAME,
										tempMap.get(CUSTOMERNAME));
								mapItem.put(CUSTOMERDESCRIPTION,
										tempMap.get(CUSTOMERDESCRIPTION));
								lisMaps.add(mapItem);
							}
						}
						// task.setTaskResult(params);
					}
					task.setTaskResult(lisMaps);
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

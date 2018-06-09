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


public class EquipmentMaintenancekModel extends CommonModel 
{

	public void getinformation(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String sn= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT  Equipment.EquipmentNumber,Equipment.MaintenancePeriod,dbo.EquipmentMaintenance.LastMaintenanceDate,dbo.EquipmentMaintenance.NextMaintenanceDate  " +
							"FROM  dbo.Equipment  LEFT JOIN  dbo.EquipmentMaintenance ON dbo.Equipment.EquipmentId = dbo.EquipmentMaintenance.EquipmentId    "+
                            "WHERE  dbo.Equipment.EquipmentNumber='"+sn+"' ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
					Log.i(TAG, "这里有执行3");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	public void equipmentmaintenance(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String	Params = (String) task.getTaskData();
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [txn_EquipmentMaintenanceRecord] @EquipmentRepairinfo ='"+Params+"',"
							+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet)
						result.put("Error", "连接MES失败");
					else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
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
				}
				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
}

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


public class MaintenanceSelectModel extends CommonModel 
{

	public void getinformation(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT EquipmentNumber,FixedAssetNumber, EquipmentLocation,MaintenancePeriod,CONVERT(NVARCHAR(20),LastMaintenanceDate,111) LastMaintenanceDate,"+
						"CONVERT(NVARCHAR(20),NextMaintenanceDate,111) NextMaintenanceDate FROM  dbo.Equipment LEFT JOIN  dbo.EquipmentMaintenance "+
						"	ON dbo.Equipment.EquipmentId = dbo.EquipmentMaintenance.EquipmentId";
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
						
					}
					else {
						result = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + result);
						// String[] reses = new String[1];
						
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

package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class testdengModel extends CommonModel {

	public void getlist(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();


				

					//String sql = "SELECT  *  FROM  equfortestdeng  WHERE    (equipmentnumber='AFC01111001208' OR  equipmentnumber='ALC01120624001')";
					String sql = "SELECT  EquipmentNumber,FixedAssetNumber, EquipmentLocation,MaintenancePeriod,LastMaintenanceDate,"+
							"NextMaintenanceDate FROM  dbo.Equipment LEFT JOIN  dbo.EquipmentMaintenance  "+
							"ON dbo.Equipment.EquipmentId = dbo.EquipmentMaintenance.EquipmentId"; 
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet)
						Log.i(TAG,"....");
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

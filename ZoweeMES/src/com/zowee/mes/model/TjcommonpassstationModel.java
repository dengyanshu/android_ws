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

public class TjcommonpassstationModel extends CommonModel {

	public void getstations(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String moname = (String) task.getTaskData();;
					Soap soap = MesWebService.getMesEmptySoap();
					Log.i(TAG, "����������:" + moname);
					String sql = "SELECT dbo.WorkflowStep.WorkflowStepName  FROM  dbo.WorkflowStep  " +
							"INNER  JOIN  dbo.MO  ON dbo.WorkflowStep.WorkflowId=dbo.MO.WorkflowId  "+
                             "WHERE dbo.MO.MOName='"+moname+"'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet){
						//result.put("Error", "����MESʧ��");
					}
					else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
						task.setTaskResult(resMapsLis);
					}
				} catch (Exception e) {
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	public void passstation(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();
				    String	Params = (String) task.getTaskData();
					Log.i(TAG, "����������:" + Params);
					String sql = " declare @res int declare @message nvarchar(max)   exec @res= [TXN_MES_TE]    "
							+ "@I_CMDTYPE=10,@I_CMD='"+Params+"',"
							+ " @MSG=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage";
					//@message as I_ReturnMessage
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					//[I_ReturnValue=1; I_ReturnMessage=OK; ]
					//Log.i(TAG, "resDataSet����" + resDataSet.size()=1);
					/*******************************Ϊ����Ľ�����׼��*************************************/
					String res=resDataSet.get(0);
					resDataSet.remove(0);
					if(res.matches(".+I_ReturnMessage=OK;.+")){
				    	 res=res.replace("OK;", "OK,");
				    	 resDataSet.add(res);
				     }
					else{
						if(res.matches(".+I_ReturnMessage=NG;.+")){
							res=res.replace("NG;", "NG,");
							resDataSet.add(res);
						}
					}
				    	 
					/********************************Ϊ����Ľ�����׼��************************************/ 
					
					if (null == resDataSet)
						result.put("Error", "����MESʧ��");
					else {
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						//����վ���� ��������������  ����ԭ����[I_ReturnValue=1; I_ReturnMessage=OK;COMMAND TYPE IS 10;] ����;û��= ��������
						Log.i(TAG, "" + resMapsLis);
						if (null != resMapsLis && 0 != resMapsLis.size()) {
							for (int i = 0; i < resMapsLis.size(); i++) {
								result.putAll(resMapsLis.get(i));
							}
						} else {
							result.put("Error", "����" + resDataSet.toString()
									+ "�ش���Ϣʧ��");
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

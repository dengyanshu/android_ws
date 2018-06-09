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

public class TjbadcountModel extends CommonModel {
    //下来框数据源获取
	public void getmo(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = " SELECT MOID,MOName,MODescription FROM mo WHERE (MOId LIKE 'cntj%' OR MOId= 'shmoid')AND MOSTDType ='D' ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());
//					String resdatastr=MesWebService.WsDataParser.getUseDataForDataPart(envolop.bodyIn.toString());
//					Log.i(TAG,"resdatastr="+resdatastr);
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
						//result.put("Error", "MES解析出来的结果集为空");
						return  null;
					}
					else
					{
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
						
						task.setTaskResult(resMapsLis);
					}	
				}
				catch (Exception e)
				{
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	public void getline(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = " SELECT WorkcenterId,WorkcenterName,WorkcenterDescription FROM dbo.Workcenter ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());
//					String resdatastr=MesWebService.WsDataParser.getUseDataForDataPart(envolop.bodyIn.toString());
//					Log.i(TAG,"resdatastr="+resdatastr);
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
						//result.put("Error", "MES解析出来的结果集为空");
						return  null;
					}
					else
					{
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);
						
						task.setTaskResult(resMapsLis);
					}	
				}
				catch (Exception e)
				{
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	public void badcount(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[2] ;
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					String moid = Params[0];
					String lineid = Params[1];
					

					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] );
					String  sql = " declare @res int  exec @res= [Proc_StatisticsZM]  "
								+ "@MOId='"+moid+"',@WorkcenterId='"+lineid+"' select @res as I_ReturnValue";
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
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
   
}

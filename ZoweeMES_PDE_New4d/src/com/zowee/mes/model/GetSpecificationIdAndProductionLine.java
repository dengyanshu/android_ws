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
 * @description 获取工作中心和生产线体
 */
public class GetSpecificationIdAndProductionLine  extends CommonModel 
{

	/**
	 * @param task
	 * @description 通过MOName获得工作中心
	 */
	public void getSpecificationId(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					String Moname ="";
					Moname = (String)task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					String SQL ="";
					SQL = "SELECT WorkflowStep.SpecificationId,WorkflowStepName,SpecificationName FROM dbo.WorkflowStep WITH(NOLOCK)" +
							" LEFT JOIN dbo.Specification WITH(NOLOCK) ON dbo.WorkflowStep.SpecificationId = dbo.Specification.SpecificationId" +
							" LEFT JOIN dbo.SpecificationRoot WITH(NOLOCK) ON dbo.Specification.SpecificationRootId = dbo.SpecificationRoot.SpecificationRootId" +
							" WHERE dbo.WorkflowStep.WorkflowId =(SELECT WorkflowId FROM MO WITH(NOLOCK) WHERE MOName='" + Moname +"')";
					soap.addWsProperty("SQLString", SQL);
							SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);

					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						List<HashMap<String,String>> parasMapsLis = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> tempMap = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							tempMap = resMapsLis.get(i);
							if(tempMap.containsKey("SpecificationId"))
							{
								parasMapsLis.add(tempMap);
							}
						}

						task.setTaskResult(parasMapsLis);
					}
				}
				//	} 
				catch (Exception e) 
				{
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
	 * @description  获取产线线体名称
	 */
	public void GetWorkcenterId(Task task)   
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {

					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select WorkcenterName,WorkcenterId from Workcenter ");		
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);

					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						List<HashMap<String,String>> parasMapsLis = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> tempMap = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							tempMap = resMapsLis.get(i);
							if(tempMap.containsKey("WorkcenterName"))
							{
								parasMapsLis.add(tempMap);
							}

						}
						task.setTaskResult(parasMapsLis);						 
					}
				}

				catch (Exception e) 
				{
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

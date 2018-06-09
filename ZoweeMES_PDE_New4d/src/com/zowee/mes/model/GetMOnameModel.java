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
 * @description SMT���϶��ϵ�ҵ���߼���
 */
public class GetMOnameModel extends CommonModel 
{

	private final static String MONAME = "MOName";//����


	/**
	 * @param task
	 * @description ͨ��MOSTDType�����Ч������
	 */
	public void getMoNamesByMOSTdType(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					String MOSTDType ="";
					MOSTDType = (String)task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql = "select MOName,MOId from MO where MOStatus = '3' and MOSTDType = '" + MOSTDType + "' or MOSTDType='p' order by mo.PlannedDateFrom desc;";
					if(MOSTDType.isEmpty())
						Sql = "select MOName,MOId from MO where MOStatus = '3' order by mo.PlannedDateFrom desc;";
					soap.addWsProperty("SQLString",  Sql);
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
							if(tempMap.containsKey(MONAME))
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
	 * @description  ��ȡSN����
	 */
	public void GetDipSNType(Task task)   
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {

					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "exec StartSNType_ViewList");		
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
							if(tempMap.containsKey("�������"))
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
	/**
	 * @param task
	 * @description  ͨ����Դ����ȡ��ԴID
	 */
	public void GetResourceId(Task task)   
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					String ResourceName ="";
					ResourceName = (String)task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select ResourceId from Resource where ResourceName='"+ ResourceName + "'");		
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
							if(tempMap.containsKey("ResourceId"))
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
	
	//��ȡfd  ǰ̨���빤���Ͳ�λ  
	public void GetFD(Task task)   
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					String [] para=new String[5];
					para = (String[])task.getTaskData();
					String moname=para[0];
					String slot=para[1];//������λ����
				
					Soap soap = MesWebService.getMesEmptySoap();
					String  sql="SELECT  DevicePartsNum FROM  [10.2.0.25].OrBitXE.dbo.DevicePartsFD	with(nolock) WHERE DevicePartsFDId=" +
							"  (SELECT  TOP 1 FeederId  FROM  dbo.LotOnSMTHistory WITH(NOLOCK) WHERE MOId=(SELECT  MOID FROM MO WHERE MOName='"+moname+"')  " +
							" AND  StationNO='"+slot.substring(0,4)+"' AND  SlotNO='"+slot.substring(4)+"' ORDER BY   LotOnSMTHistoryId DESC )";
					soap.addWsProperty("SQLString", sql);		
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					task.setTaskResult(resMapsLis);		
					
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
	
	
	/**
	 * @param task
	 * @description ��ȡ������Ϣ 
	 */

	public void getWoInfo(Task task)
	{

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String lotWO = task.getTaskData().toString();
					String Sql= "SELECT top 1 MO.MOId ,mo.ProductId,MO.MOName, Product.ProductDescription,ProductRoot.ProductName FROM  MO LEFT JOIN ProductRoot on ProductRoot.DefaultProductId = MO.ProductId left join Product on Product.ProductRootId =  ProductRoot.ProductRootId Where mo.MOStatus = '3' and mo.MOName='" + lotWO + "'" ;
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet)
						result.put("Error", "��ȡ������Ϣʧ��");
					else
					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						//String[] reses = new String[1];
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
							result = resMapsLis.get(0);

						}
						else{
							result.put("Error", "��������" + resDataSet.toString() + "��Ϣʧ��");
						}
					}	
					task.setTaskResult(result);
				}
				catch (Exception e)
				{
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

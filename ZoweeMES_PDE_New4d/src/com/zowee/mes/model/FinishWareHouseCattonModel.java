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
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 卡通箱入仓业务逻辑类
 */

public class FinishWareHouseCattonModel extends CommonModel
{

	private static final String MONAME = "MOName";
	private static final String MOID = "MOId";
	private static final String SONAME = "SOName";
	private static final String PRODUCTCOMPLETEID = "ProductCompleteID";
	private static final String COMPLETEDOCNO = "CompleteDocNo";
	private static final String PRODUCTID = "ProductId";
	
	/**
	 * @param task
	 * @description 获取卡通箱入仓所需的参数信息 MOId MOName SOName
	 */
	public void getNecesParams(Task task)
	{

		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData())return task;
					String completeId = task.getTaskData().toString().trim();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select m.MOId,m.MOName,s.SOName from ProductComplete p inner join MO m on p.MOId = m.MOId inner join SO s on m.SOId = s.SOId where p.ProductCompleteID = '"+completeId+"';");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					//Log.i(TAG, ""+envolop.bodyIn.toString());	
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
				//	Log.i(TAG, ""+resMapsLis);
					List<HashMap<String,String>> lisMaps = new ArrayList<HashMap<String,String>>();
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						//Object[] params = new Object[2];
						//params[0] = lisMaps;
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(MOID)&&tempMap.containsKey(MONAME)&&tempMap.containsKey(SONAME)&&!StringUtils.isEmpty(tempMap.get(MOID))&&!StringUtils.isEmpty(tempMap.get(MONAME))&&!StringUtils.isEmpty(tempMap.get(SONAME)))
							{
								HashMap<String,String> mapItem = new HashMap<String,String>();
								mapItem.put(MOID,tempMap.get(MOID));
								mapItem.put(MONAME,tempMap.get(MONAME));
								mapItem.put(SONAME,tempMap.get(SONAME));
								lisMaps.add(mapItem);
							}
						
//							if(tempMap.containsKey(SONAME)&&!StringUtils.isEmpty(tempMap.get(SONAME)))
//								   params[1] = tempMap.get(SONAME);
						}
						//Log.i(TAG, ""+lisMaps);
						
					}
					task.setTaskResult(lisMaps);
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
	 * @description 卡通箱成品入仓 
	 */
	public void finishWareHouseCartoon(Task task)
	{
		
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] params = (String[])task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @retMsg nvarchar(max) declare @excep nvarchar(100) declare @retRes int exec @retRes = PDA_ProdEnterLocation_New '',@retMsg output,@excep,'','','"+MyApplication.getMseUser().getUserId()+"','"+MyApplication.getMseUser().getUserName()+"','','','','','','','"+params[0]+"','"+params[1]+"','"+params[2]+"','"+params[3]+"','"+params[4]+"','"+params[5]+"' select @retRes,@retMsg");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					//Log.i(TAG, ""+resMapsLis);
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						String[] reses = new String[]{"",""};
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(COLUMN_1))
							   reses[0] = tempMap.get(COLUMN_1); 
							if(tempMap.containsKey(COLUMN_2))
								   reses[1] = tempMap.get(COLUMN_2);
						}
						
						task.setTaskResult(reses);
					}
				}
				//} 
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
	 * 
	 * @description 获取工单所对应的完工入库单号 
	 */
	public void getCompleteDocNos(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String checkStr = (String)task.getTaskData();
					//String moId = params[0];
					//String productId = params[1];
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select ProductCompleteID,CompleteDocNo from ProductComplete where CompleteDocNo like '%"+checkStr+"%' order by CompleteDocNo asc ;");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					//Log.i(TAG, ""+resMapsLis);
					List<HashMap<String,String>> lisMaps = new ArrayList<HashMap<String,String>>();
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(PRODUCTCOMPLETEID)&&tempMap.containsKey(COMPLETEDOCNO)&&!StringUtils.isEmpty(tempMap.get(PRODUCTCOMPLETEID))&&!StringUtils.isEmpty(tempMap.get(COMPLETEDOCNO)))
							{
								HashMap<String,String> mapItem = new HashMap<String,String>();
								mapItem.put(PRODUCTCOMPLETEID,tempMap.get(PRODUCTCOMPLETEID));
								mapItem.put(COMPLETEDOCNO, tempMap.get(COMPLETEDOCNO));
								lisMaps.add(mapItem);
							}
						}
						
					}
					task.setTaskResult(lisMaps);
				}
			//} 
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

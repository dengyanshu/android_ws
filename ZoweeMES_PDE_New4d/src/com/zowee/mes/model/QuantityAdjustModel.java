package com.zowee.mes.model;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 批号数量调整的业务逻辑处理类
 */

public class QuantityAdjustModel extends CommonModel 
{
	
	private final static String COLUMN_1 = "Column1";
	private final static String QTY = "Qty";
	private final static String COLUMN_2 = "msg";
	
	/**
	 * @param task
	 * @description 获取批号原有的数量 
	 */
	public void getLotSNOldQuantity(Task task)
	{
//		if(null==task||null==task.getTaskData())
//			return;
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					Soap soap = MesWebService.getMesEmptySoap();
					String lotSN = task.getTaskData().toString();
					soap.addWsProperty("SQLString", "select Qty from lot where LotSN = '"+lotSN+"'" );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet)
						task.setTaskResult("0.00");
					else
					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						//String[] reses = new String[1];
						
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
							String qty = null;
							for(int i=0;i<resMapsLis.size();i++)
							{
								HashMap<String,String> tempMap = resMapsLis.get(i);
								if(tempMap.containsKey(QTY))
									qty = tempMap.get(QTY);
							}
							Log.i(TAG, "qty:"+qty);
							task.setTaskResult(qty);
						}
					}						
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
	
	
	/**
	 * @param task
	 * @description 批号数量调整 
	 */
	public void quantityAdjust(Task task)
	{
		if(null==task||null==task.getTaskData())
			return;
		TaskOperator taskOperator = new TaskOperator()
		{	
			@Override
			public Task doTask(Task task)
			{
				try
				{
					String[] taskDatas = (String[])task.getTaskData();
					if(2!=taskDatas.length)
						return task;					
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @res int declare @excep nvarchar(100) exec @res = Txn_WMSLotQtyChange '',@ReturnMessage output,@excep output,'1'" +
				",'','','"+MyApplication.getMseUser().getUserId()+"','"+MyApplication.getMseUser().getUserName()+"','','','','','','','"+taskDatas[0]+"',"+taskDatas[1]+"  select @res,@ReturnMessage " );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet)
						return task;
					else
					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
							String[] reses = new String[2];
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

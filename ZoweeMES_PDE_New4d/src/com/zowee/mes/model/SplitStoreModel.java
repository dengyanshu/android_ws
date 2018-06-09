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
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 拆分出库的业务处理类
 */
public class SplitStoreModel extends CommonModel
{
	private final static String QTY = "Qty";//拆分批号数量值
	
	public void splitStorage(Task task)
	{
//		if(null==task||null==task.getTaskData())
//			return;
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					Object[] taskDatas = (Object[])task.getTaskData();
					String splitLot = taskDatas[0].toString().trim();
					String targetLot = taskDatas[2].toString().trim();
					Soap soap = MesWebService.getMesEmptySoap();
					String splitAmount = StringUtils.conDeciStr(String.valueOf((((Double)taskDatas[1]).doubleValue())),1);
					Log.i(TAG,"splitAmount: "+splitAmount);
					soap.addWsProperty("SQLString", " declare @retVal int declare @outDetail nvarchar(max) declare @excep nvarchar(100) declare @splitQty float select @splitQty = Qty  from lot where LotSN = '"+splitLot+"'; if @splitQty < "+splitAmount+" begin select @retVal = -1, @outDetail=' 拆分数量大于拆分批号现有的数量 ! 现有批号数量为：'+str(@splitQty) select str(@retVal),@outDetail return; end exec @retVal = TXN_LotQTYTransfer '',@outDetail output,@excep output, " +
					" '','','','"+MyApplication.getMseUser().getUserId()+"','"+MyApplication.getMseUser().getUserName()+"','','','','','','"+splitLot+"',"+splitAmount+",'"+targetLot+"' select str(@retVal),@outDetail ");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
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
						Log.i(TAG, "RES:"+reses[0]+" LOG: "+reses[1]);
						task.setTaskResult(reses);
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
	 * 
	 * @description 获取拆分出库所需的参数 主要获取 拆分批号现有的数量
	 */
	public void getNecessData(Task task)
	{
//		if(null==task||null==task.getTaskData())
//			return;
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String lotSN = task.getTaskData().toString().trim();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select Qty  from lot where LotSN = '"+lotSN+"';");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						String qty = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(QTY))
								qty = tempMap.get(QTY);
						}
						
						task.setTaskResult(qty);
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

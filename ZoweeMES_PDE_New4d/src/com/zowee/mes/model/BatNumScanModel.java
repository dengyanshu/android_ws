package com.zowee.mes.model;

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

/**
 * @author Administrator
 * @description 批号信息显示业务逻辑类
 */
public class BatNumScanModel extends CommonModel
{
	
	private final static String RES = "msgint";
	private final static String RESMSG = "msg";
	
	/**
	 * @param task
	 * @description  获取批号的详细信息
	 */
	public void getLotSNImfor(Task task)
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
					String lotSN = task.getTaskData().toString();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @res int declare @excep nvarchar(100) exec @res = Txn_ReturnLotInfo '',@ReturnMessage output,@excep output,'1'" +
				",'','','','','','','','','','"+lotSN+"'" );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					//String[]  reses = new String[2];
//					if(null==resDataSet)
//					{
////						reses[0] = "-1";
////						reses[1] = "网络异常!";
//					}
//					else
//					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						//String[] reses = new String[1];
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
							String[] reses = new String[2];
							for(int i=0;i<resMapsLis.size();i++)
							{
								HashMap<String,String> tempMap = resMapsLis.get(i);
								if(tempMap.containsKey(RES))
									reses[0] = tempMap.get(RES);
								if(tempMap.containsKey(RESMSG))
									reses[1] = tempMap.get(RESMSG);
							}
							task.setTaskResult(reses);
						}
//					}	
					//Log.i(TAG, ""+reses);
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

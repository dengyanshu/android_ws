package com.zowee.mes.model;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 物料盘点的业务逻辑类
 */
public class MaterialInventoryModel extends CommonModel 
{

	private static final String QTY = "QTY";//物料数量
	private static final String STOCKLOCATION = "StockLocation";//库位
	private final static String COLUMN_1 = "Column1";
	private final static String COLUMN_2 = "Column2";
	
	
	/**
	 * @param task
	 * @description 获取必要的参数信息   库位      物料数量  
	 */
	public void getNecessaryParams(Task task)
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
					String lotSN = task.getTaskData().toString();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select QTY,StockLocation from stockLOC where LotSN = '"+lotSN+"'");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
//					if(null==resDataSet)
//						return task;//后期可能采用通知的方法
//					else
//					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						//String[] reses = new String[1];
											
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
							String[] params = new String[2];	
							for(int i=0;i<resMapsLis.size();i++)
							{
								HashMap<String,String> tempMap = resMapsLis.get(i);
								if(tempMap.containsKey(QTY))
									params[0] = tempMap.get(QTY);
								if(tempMap.containsKey(STOCKLOCATION))
									params[1] = tempMap.get(STOCKLOCATION);
							}
							Log.i(TAG, "proId:"+params);
							task.setTaskResult(params);
							
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
	 * @param task
	 * @description 物料盘点  如果传入的 @Labar(查看存储过程 Txn_MaterialsInventory --物料盘点) = CK 物料检查 SM = 数据保存  
	 */
	public void MatInv(Task task)
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
					String[] pas =(String[])task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @res int declare @excep nvarchar(100) exec @res = Txn_MaterialsInventory '',@ReturnMessage output,@excep output,'1'" +
				",'','','','','','','','','','"+pas[0]+"','"+pas[1]+"',"+pas[2]+",'"+pas[3]+"','','"+pas[4]+"' select @res,@ReturnMessage");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
//					if(null==resDataSet)
//						return task;//后期可能采用通知的方法
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
								if(tempMap.containsKey(COLUMN_1))
									reses[0] = tempMap.get(COLUMN_1);
								if(tempMap.containsKey(COLUMN_2))
									reses[1] = tempMap.get(COLUMN_2);
							}
							Log.i(TAG, "proId:"+reses);
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
	
	
	
}

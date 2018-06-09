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
 * @description 卸换料卷的业务逻辑类 
 */
public class RemoveMateriModel extends CommonModel 
{

	/**
	 * @param task
	 * @description 卸换料卷 
	 */
	public void removeMateri(Task task)
	{
		
//		if(null==task||null==task.getTaskData())
//			return;
	//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String staLotSN = (String)task.getTaskData();
					String docNo = staLotSN.substring(0,3);
					String staNo = staLotSN.substring(3,4);
					String sLotNo = "";
					if(staLotSN.length()>=14)
						sLotNo = staLotSN.substring(4,14);
					else
						sLotNo = staLotSN.substring(4);
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @MOName nvarchar(50) declare @returnMsg nvarchar(max) declare @retRes int select top 1 @MOName= isnull(m.MOName,'') from LotOnSMT l inner join MO m on l.MOId = m.MOId where "+
                    " l.SMTLineNO = '"+docNo+"' and l.SLotNO = '"+sLotNo+"'  order by l.LotOnSMTId desc;" +
				   " exec @retRes = Txn_SMTItemUnload @returnMsg output,'"+MyApplication.getMseUser().getUserId()+"','" + MyApplication.getAppOwner() +"',@MOName,'"+staLotSN+"' select @retRes,@returnMsg");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
//					//Log.i(TAG, resDataSet.toString());
//				//	Log.i(TAG,"COME IN1");
//					if(null==resDataSet)
//						return task;//后期可能采用通知的方法
//					else
//					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
							String[] reses = new String[2];
							//Log.i(TAG,"COME IN3");
							for(int i=0;i<resMapsLis.size();i++)
							{
								HashMap<String,String> tempMap = resMapsLis.get(i);
								if(tempMap.containsKey(COLUMN_1))
									reses[0] = tempMap.get(COLUMN_1);
								if(tempMap.containsKey(COLUMN_2))
									reses[1] = tempMap.get(COLUMN_2);
								//Log.i(TAG, tempMap.get(COLUMN_3));
							}
							//Log.i(TAG, ""+reses);
							task.setTaskResult(reses);
						}
						//Log.i(TAG,"COME IN2");
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
	
	
}

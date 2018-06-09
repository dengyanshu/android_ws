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


public class SmtshangliaoslotModel extends CommonModel 
{
   
	public void getMOName(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				    String para=(String) task.getTaskData();
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql="select MOId,MOName from MO where MOStatus = '3' and MOSTDType = 'S'and MOName like '%"+para+"%' order by CreateDate desc;";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					//Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());
//					
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					//Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
						//Log.i(TAG,"testmodel youzhixing2");
					}
					else
					{
						//Log.i(TAG,"testmodel youzhixing3");
						result = MesWebService.getResMapsLis(resDataSet);
						//Log.i(TAG,"testmodel result"+result);
						
					}	
					task.setTaskResult(result);
				}
				catch (Exception e)
				{
					
					Log.i(TAG,"testmodel youzhixing1");
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	public void shouliao(Task task) {
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String[]   Params = (String[]) task.getTaskData();

					String moid = Params[0];
					String line = Params[1];
					String mzlot = Params[2];
					
					
//					String sql = " SELECT  '"+line+"'+SMTMountItem.StationNo+SLotNO as slot FROM  dbo.SMTMountItem  INNER JOIN  dbo.SMTMount  ON SMTMount.SMTMountId = SMTMountItem.SMTMountId "+
//                                  " INNER JOIN dbo.MO_SMTMount  ON MO_SMTMount.SMTMountId = SMTMount.SMTMountId "+
//                                   " WHERE MOId='"+moid+"' AND  ProductId=(SELECT ProductId FROM  lot WITH(NOLOCK) WHERE LotSN='"+mzlot+"')";
					
					
					String sql ="SELECT  Side+'面'+'"+line+"'+SMTMountItem.StationNo+SLotNO+'数量为'+CAST(BaseQty AS NVARCHAR(50))+'*'+CAST(MOQtyRequired AS NVARCHAR(50)) "+
					"  AS slot FROM  dbo.SMTMountItem  INNER JOIN  dbo.SMTMount  ON SMTMount.SMTMountId = SMTMountItem.SMTMountId "+
					"  INNER JOIN dbo.MO_SMTMount  ON MO_SMTMount.SMTMountId = SMTMount.SMTMountId "+
					"  INNER JOIN  dbo.MO  ON MO.MOId = MO_SMTMount.MOId "+
					 " WHERE MO_SMTMount.MOId='"+moid+"' AND  SMTMountItem.ProductId=(SELECT ProductId FROM  lot WITH(NOLOCK) WHERE LotSN='"+mzlot+"')";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					//Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet){
						
					}
					else {
						result = MesWebService.getResMapsLis(resDataSet);
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

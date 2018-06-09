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

public class TjdipstartModel extends CommonModel {
    //下来框数据源获取
	public void getmobymoname(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				    String para=(String) task.getTaskData();
					HashMap<String,String> result = new  HashMap<String, String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "  SELECT MOName,MOId,dbo.Product.ProductDescription,dbo.ProductRoot.ProductName,dbo.WorkflowRoot.WorkflowName FROM  dbo.MO "+ 
							"left JOIN dbo.Product ON dbo.MO.ProductId = dbo.Product.ProductId "+
							"left  JOIN  dbo.ProductRoot ON  dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId "+
							"left JOIN dbo.Workflow ON dbo.MO.WorkflowId=dbo.Workflow.WorkflowId  "+
							"left  JOIN dbo.WorkflowRoot ON  dbo.Workflow.WorkflowRootId = dbo.WorkflowRoot.WorkflowRootId "+
							"WHERE MOName='"+para+"'";
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
						result.put("Error", "MES解析出来的结果集为空");
					}
					else
					{
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
	
	
	
	public void tjdipstart(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[8] ;
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					String useid = Params[0];
					String usename = Params[1];
					String resourceid = Params[2];
					String resourcename = Params[3];
					
					String moid = Params[4];
					String moname = Params[5];
					String sn = Params[6];
					String diptype = Params[7];

					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
							+ Params[2] + "," + Params[3] + "," + Params[4]
							+ "," + Params[5]);
                   //通用的是1=[Txn_DIP_fan]，手环的是2=Txn_DIP_ZMSH_input
					String  sql="";
					if(diptype.equals("1")){
						sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_DIP_fan]  "
								+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
								+ "@MOID='"+moid+"',@LotSN='"+sn+"',"
								+ " @I_ReturnMessage=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage";
					}
					else{
						sql = " declare @res int declare @message nvarchar(max)  exec @res=Txn_DIP_ZMSH_input "
								+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
								+ "@MOID='"+moid+"',@MOName='"+moname+"',@PowerSN='"+sn+"',"
								+ " @I_ReturnMessage=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage";
					}
				 
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

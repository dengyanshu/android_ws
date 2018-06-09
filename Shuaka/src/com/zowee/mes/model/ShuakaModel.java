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

public class ShuakaModel extends CommonModel {
	
	
	public void getlistdata(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				    String[] para=(String[]) task.getTaskData();
				    String linename = para[0];
					String machineid = para[1];
					String moname = para[2];
					String zhicheng = para[3];
					
					String shifname = para[4];
					String shanghuoxia = para[5];
					String empid = para[6];
				    
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "EXEC [Txn_OnlineStaffHistory_ForAndriod] @shift='"+shifname+"',@workcenterName='"+linename+"',@moName='"+moname+"',@WorkprocedureFlowList='"+zhicheng+"'"; 		
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());			
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
					}
					else
					{
						result = MesWebService.getResMapsLis(resDataSet);
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
    
	public void getworkcentername(Task task) {

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
					String sql = "EXEC [WorkprocedureflowMoWorkcenter_ViewList]";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());			
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
					}
					else
					{
						result = MesWebService.getResMapsLis(resDataSet);
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
	//获取工单
	public void getmoname(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				    String[] para=(String[]) task.getTaskData();
				    String line=para[0];
				    String day=para[1];
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "  EXEC  WorkprocedureFlowList_MO_ViewList  @workcenterName='"+line+"',@day="+day;
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());			
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
					}
					else
					{
						result = MesWebService.getResMapsLis(resDataSet);
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
	
	//获取制程
		public void getzhicheng(Task task) {

			TaskOperator taskOperator = new TaskOperator()
			{		
				@Override
				public Task doTask(Task task)
				{
					try 
					{
						if(null==task.getTaskData()) return task;
						String[] Params = new String[3] ;

						Params = (String[]) task.getTaskData();

						String linename = Params[0];
						String moname = Params[1];
						String day = Params[2];
						
						List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
						Soap soap = MesWebService.getMesEmptySoap();
						String sql = " EXEC [dbo].[WorkprocedureMo_Line_ViewList] @workcenterName='"+linename+"',@moname='"+moname+"',@day="+day;
						soap.addWsProperty("SQLString", sql);
						SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
						if(null==envolop||null==envolop.bodyIn)
							return task;
						Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());			
						List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
						Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
						if(null==resDataSet){
						}
						else
						{
							result = MesWebService.getResMapsLis(resDataSet);
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
		
		
		//获取工单
		public void getmachinenumber(Task task) {

			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					try {
						if (null == task.getTaskData())
							return task;
						HashMap<String, String> result = new HashMap<String, String>();
						String Params  ;
						Soap soap = MesWebService.getMesEmptySoap();

						Params = (String) task.getTaskData();

						String sql = " SELECT TOP  1 name FROM [10.2.0.8].[ESD].dbo.[DeviceInfo-GWKJ] WHERE workcenter = '"+Params+"' and record =2 ";
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
		
		
	
	
	public void shuaka(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[7] ;
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					String linename = Params[0];
					String machineid = Params[1];
					String moname = Params[2];
					String zhicheng = Params[3];
					
					String shifname = Params[4];
					String shanghuoxia = Params[5];
					String empid = Params[6];

					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
							+ Params[2] + "," + Params[3] + "," + Params[4]
							+ "," + Params[5]+Params[6]);

					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_Punch_DoMethod]    "
							+" @shift='"+shifname+"',@workcenterName='"+linename+"',@moName='"+moname+"',@WorkprocedureFlowList='"+zhicheng+"',"
							+ "@option="+shanghuoxia+",@MatchAddr='"+machineid+"',@CardNum='"+empid+"',"
							+ " @I_ReturnMessage=@message  out  select @res as I_ReturnValue,@message as I_ReturnMessage";
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
    
	
	
	
	public void getRenli(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					String[] Params = new String[4] ;

					Params = (String[]) task.getTaskData();

					String linename = Params[0];
					String moname = Params[1];
					String zhicheng = Params[2];
					String shift = Params[3];
					
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = " EXEC [dbo].[StaffOnLine_QueryDoMethod] @workcenterName='"+linename+"',@moname='"+moname+"',@WorkprocedureflowName='"+zhicheng+"',@shift='"+shift+"'";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());			
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
					}
					else
					{
						result = MesWebService.getResMapsLis(resDataSet);
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
	
	

}

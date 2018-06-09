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


public class SmtQtyChangeModel extends CommonModel 
{
   
	public void getQty(Task task) {

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
					
					String sql = " select qty  from lot with(nolock) where lotsn='"+para+"'";
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
	
	
	
	
	
	
	public void chaoling(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String[]   Params = (String[]) task.getTaskData();

					String resourceid = Params[0];
					String resourcename = Params[1];
					String useid = Params[2];
					String usename = Params[3];
					
					String moid = Params[4];
					String slot = Params[5];
					String num = Params[6];
					String mz = Params[7];
					String moname = Params[8];
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [PDA_SMTMaterialPicking_Domethod] @MOId ='"+moid+"', "
							+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
							+" @MOName ='"+moname+"',@StationNO ='"+slot+"',@QTY ="+num+",@LotSN ="+mz+", "
							+ "  @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
    
	public void getmo_jit(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				   // String para=(String) task.getTaskData();
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					//测试时候 数据少点  正式不要top100
					String sql = " exec MaterialMOList_ViewList";
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
	
	
	
	public void get_workcenter(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				   // String para=(String) task.getTaskData();
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					//测试时候 数据少点  正式不要top100
					String sql = " exec [Workcenter2Floor_ViewList]";
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
	
	
	public void check_mo_out(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String[]   Params = (String[]) task.getTaskData();

					String resourceid = Params[0];
					String resourcename = Params[1];
					String useid = Params[2];
					String usename = Params[3];
					
					String moid = Params[4];
					String workcenterid = Params[5];
					String mzlot = Params[6];
					
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_NotSmtMountMaterialIssueToMO_DoMethod]  "
							+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
							+" @ToWorkcenterID ='"+workcenterid+"',@InputMaterialSN ='"+mzlot+"',@MOId ='"+moid+"', "
							+ "  @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage ";
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
	
	
	public void submit(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String[]   Params = (String[]) task.getTaskData();

					String resourceid = Params[0];
					String resourcename = Params[1];
					String useid = Params[2];
					String usename = Params[3];
					
					String moid = Params[4];
				
					
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_NOTSMTMountMaterialIssueToMOConfirm_DoMethod]  "
							+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
							+" @MOID ='"+moid+"', "
							+ "  @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage ";
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

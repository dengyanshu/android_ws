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


public class SmtChaolingModel extends CommonModel 
{
   
	public void getmo(Task task) {

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
					String sql = "  	SELECT     dbo.MO.MOId, dbo.MO.MOName,Productroot.productName FROM  dbo.MO with(NOLOCK) inner join "+
					" dbo.Product with(NOLOCK) inner join ProductRoot with(NOLOCK) ON Product.ProductrootID = Productroot.productrootID "+
					" ON dbo.MO.ProductId = dbo.Product.ProductId WHERE  (dbo.MO.MOStatus = '3') and  mo.MOSTDType='S' AND IsJITMode='true'";
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
					String moname = Params[5];
					String WorkcenterName = Params[6];
					String stockid = Params[7];
					String mz = Params[8];
					String num = Params[9];
				
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [PDA_SMTMaterialPickingNew_Domethod] @MOId ='"+moid+"', "
							+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
							+" @MOName ='"+moname+"',@WorkcenterName='"+WorkcenterName+"',@StockID='"+stockid+"',@QTYNew ="+num+",@LotSN ="+mz+", "
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
    
	
	public void oa(Task task) {

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
				
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [PDA_SMTMaterialPickingToOA_Domethod] @MOId ='"+moid+"', "
							+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
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
				   String para=(String) task.getTaskData();
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					//测试时候 数据少点  正式不要top100
					String sql = " exec MaterialMOList_4d_ViewList @QueryParameter="+para;
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
	
	public void getworkcenternamebyjitmoname(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
				    String   Params = (String) task.getTaskData();
					String sql="exec [WorkcenterNameJIT_ViewList] @QueryParameter='"+Params+"'";
					
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)

						return task;
					Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
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
	
	
	
	
	//获取fd故障类型
	public void fd_getType(Task task) {

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
					String sql = " exec  MalfunctionType_ViewList";
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
	
	
	//fd_submit
	public void fd_submit(Task task) {

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
					
					String lotsn = Params[4];
					String type = Params[5];
				
					
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_DevicePartsRecordFDSave_DoMethod]  "
							+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
							+" @DevicePartsNum ='"+lotsn+"',@MalfunctionType ='"+type+"', "
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
	
	
	
	
	//gd_replace
		public void gd_replace(Task task) {

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
						
						String lotsn = Params[4];
						String workcenter = Params[5];
						String employee = Params[6];
					
						
						String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_ScraperReplace]  "
								+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
								+" @DevicePartsNumGD ='"+lotsn+"',@PutOut ='"+employee+"', @Workcenter ='"+workcenter+"',"
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
		
		//gd_online
		public void gd_online(Task task) {

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
						
						String lotsn1 = Params[4];
						String lotsn2 = Params[5];
						String moname = Params[6];
						String workcenter = Params[7];
						String abside = Params[8];
						String employee = Params[9];
					
						
						
						String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_ScraperINLine] "
								+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
								+" @DevicePartsNumGD1 ='"+lotsn1+"',@DevicePartsNumGD2 ='"+lotsn2+"',@MoName ='"+moname+"',@Workcenter ='"+workcenter+"',@ABSide ='"+abside+"',@PutOut ='"+employee+"', "
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
		
		
		
		//gd_downline
				public void gd_downline(Task task) {

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
								
								String lotsn1 = Params[4];
								String lotsn2 = Params[5];
								String moname = Params[6];
								String workcenter = Params[7];
								String abside = Params[8];
								String employee = Params[9];
							
								
								
								String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_ScraperINLine] "
										+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
										+" @DevicePartsNumGD1 ='"+lotsn1+"',@DevicePartsNumGD2 ='"+lotsn2+"',@MoName ='"+moname+"',@Workcenter ='"+workcenter+"',@ABSide ='"+abside+"',@PutOut ='"+employee+"', "
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
				
				
				//gd_downline
				public void gd_moreplace(Task task) {

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
								
								String lotsn1 = Params[4];
								String lotsn2 = Params[5];
								String moname = Params[6];
								String workcenter = Params[7];
								String abside = Params[8];
								String employee = Params[9];
							
								
								
								String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_ScraperMoname] "
										+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
										+" @DevicePartsNumGD1 ='"+lotsn1+"',@DevicePartsNumGD2 ='"+lotsn2+"',@MoName ='"+moname+"',@Workcenter ='"+workcenter+"',@ABSide ='"+abside+"',@PutOut ='"+employee+"', "
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



               /**
                * xbc  库位binding执行业务逻辑
                * 
                * */
				public void binding(Task task) {
					// TODO Auto-generated method stub
					TaskOperator taskOperator = new TaskOperator() {
						@Override
						public Task doTask(Task task) {
							try {
								if (null == task.getTaskData())
									return task;
								List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
								Soap soap = MesWebService.getMesEmptySoap();
							    String[]   Params = (String[]) task.getTaskData();
								String resourceid = Params[0];
								String resourcename = Params[1];
								String useid = Params[2];
								String usename = Params[3];
								
								String mzlotsn = Params[4];
								String kuweilotsn = Params[5];
								
								
								String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_JITStockLocationBindPDE_DoMethod] "
										+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
										+" @LotSN ='"+mzlotsn+"',@StockLocation ='"+kuweilotsn+"', "
										+ "  @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage ";
								soap.addWsProperty("SQLString", sql);
								SoapSerializationEnvelope envolop = MesWebService
										.getWSReqRes(soap);
								if (null == envolop || null == envolop.bodyIn)

									return task;
								Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
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




           
				
             /**
              * 线边仓解绑 获取工单列表信息
              * */
			public void getmolist(Task task) {
				// TODO Auto-generated method stub
				TaskOperator taskOperator = new TaskOperator() {
					@Override
					public Task doTask(Task task) {
						try {
							if (null == task.getTaskData())
								return task;
							List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
							Soap soap = MesWebService.getMesEmptySoap();
							String sql = "EXEC MaterialIssueMOList_ViewList";
							soap.addWsProperty("SQLString", sql);
							SoapSerializationEnvelope envolop = MesWebService
									.getWSReqRes(soap);
							if (null == envolop || null == envolop.bodyIn)

								return task;
							Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
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





            //线别仓解绑物料
			public void xbcunbinding(Task task) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				TaskOperator taskOperator = new TaskOperator() {
					@Override
					public Task doTask(Task task) {
						try {
							if (null == task.getTaskData())
								return task;
							List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
							Soap soap = MesWebService.getMesEmptySoap();
						    String[]   Params = (String[]) task.getTaskData();
							String resourceid = Params[0];
							String resourcename = Params[1];
							String useid = Params[2];
							String usename = Params[3];
							
							String moname = Params[4];
							String line = Params[5];
							String mz = Params[6];
							
							
							String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_JITStockLocationMODelPDE_DoMethod] "
									+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
									+" @LotSN ='"+mz+"',@MOName ='"+moname+"', @WorkcenterName ='"+line+"',"
									+ "  @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage ";
							soap.addWsProperty("SQLString", sql);
							SoapSerializationEnvelope envolop = MesWebService
									.getWSReqRes(soap);
							if (null == envolop || null == envolop.bodyIn)

								return task;
							Log.i(TAG, "bodyIn=" + envolop.bodyIn.toString());
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




             //线别仓 单个物料解绑

			public void unbindingSingle(Task task) {
				// TODO Auto-generated method stub
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
							
							String mz = Params[4];
						
						
							
							
							String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_JITStockLocationSNDel_DoMethod] "
									+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
									+" @Lotsn ='"+mz+"', "
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





        //绑定 单个库位的时候   通过mz给用户提示
			public void binding_mzselectkuwei(Task task) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
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
							
							String mz = Params[4];
						
						
							
							
							String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_JITCheckStockLocationPDE_DoMethod] "
									+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
									+" @Lotsn ='"+mz+"', "
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

package com.zowee.mes.model;

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

/**
 * @author Administrator
 * @description 物料盘点的业务逻辑类
 */
public class MaterialRewindModel extends CommonModel 
{

	String userID = MyApplication.getMseUser().getUserId();
	String UserName = MyApplication.getMseUser().getUserName();
	String PoNumber;
	String LotSN;
	String SNqty;
	
	HashMap<String,String> result = new HashMap<String,String>();
	/**
	 * @param task
	 * @description 产生收货单号
	 */
	public void GenerateMaterialRewindPO(Task task)  
	{

		TaskOperator taskOperator = new TaskOperator() //20130819 ybj
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					//HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
				   	soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_MESReceiveCode @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='" +
					UserName +"',@I_OrBitUserId='" + userID + "'"   );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn="+envolop.bodyIn.toString());
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet)
						result.put("Error", "连接MES失败");
					else
					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
						    for(int i=0;i<resMapsLis.size();i++){
						    	result.putAll(resMapsLis.get(i));	
						    }
						}
						else{
							result.put("Error", "解析" + resDataSet.toString() + "回传信息失败");
						}
					}						
				}
				catch (Exception e)
				{
					MesUtils.netConnFail(task.getActivity());
					result.put("Error", "连接MES失败2");
					Log.i(TAG, e.toString());
				}
				finally{
					task.setTaskResult(result);   // modify by ybj 20130819
				}
				
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	/**
	 * @param task
	 * @description 获取物料信息
	 */
	public void GetMaterialInfo(Task task)   //20130819 ybj
	{
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					String[]Params = new String[]{"",""};
					Params =( String[]) task.getTaskData();
					PoNumber = Params[0];
				    LotSN = Params[1];
				    //HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
				   	soap.addWsProperty("SQLString", " declare @ExceptionFieldName nvarchar(100) declare @ReturnMessage nvarchar(max) declare @Return_value int declare @SNPOName nvarchar(100) declare @SNQty nvarchar(50) exec @Return_value = Txn_MaterialsLotSNScan " +
					" @I_ExceptionFieldName = @ExceptionFieldName out,@LotSNPOName = @SNPOName out,@LotSNQty = @SNQty out,@I_RETURNMessage=@ReturnMessage out,@GRNNo='" + PoNumber +"',@LotSN='" + LotSN +"', @I_OrBitUserName='" + UserName +
					 "',@I_OrBitUserId='"+ userID  +"' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result, @SNPOName as GetSNPO,@SNQty as GetSNQty"    );
				   	
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn="+envolop.bodyIn.toString());
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet)
						result.put("Error", "连接MES失败");
					else
					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
						    for(int i=0;i<resMapsLis.size();i++){
						    	result.putAll(resMapsLis.get(i));	
						    }
						}
						else{
							result.put("Error", "解析" + resDataSet.toString() + "回传信息失败");
						}
					}
					//task.setTaskResult(result);
					
				}
				catch (Exception e)
				{
					MesUtils.netConnFail(task.getActivity());
					result.put("Error", "连接MES失败2");
					Log.i(TAG, e.toString());
				}
				finally{
					task.setTaskResult(result);
				}
				
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	/**
	 * @param task
	 * @description 获取物料信息
	 */
	public void SubmitRewindMaterial(Task task)  //20130819 ybj
	{
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					String[]Params = new String[]{"",""};
					Params = (String[]) task.getTaskData();
					PoNumber = Params[0]; //收货单号
					SNqty = Params[1];
				    //HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
				   	soap.addWsProperty("SQLString", " declare @ExceptionFieldName nvarchar(100) declare @ReturnMessage nvarchar(max) declare @Return_value int  exec @Return_value = Txn_MaterialsRewind  @I_ExceptionFieldName = " +
					"@ExceptionFieldName out,@I_RETURNMessage=@ReturnMessage out,@GRNNo='" + PoNumber +"',@QtyS='" + SNqty +"', @I_OrBitUserName='" + UserName +  "',@I_OrBitUserId='"+ 
					userID  +"' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result"    );						
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn="+envolop.bodyIn.toString());
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet)
						result.put("Error", "连接MES失败");
					else
					{
						List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG, ""+resMapsLis);
						if(null!=resMapsLis&&0!=resMapsLis.size())
						{
						    for(int i=0;i<resMapsLis.size();i++){
						    	result.putAll(resMapsLis.get(i));	
						    }
						}
						else{
							result.put("Error", "解析" + resDataSet.toString() + "回传信息失败");
						}
					}	
					
				}
				catch (Exception e)
				{
					MesUtils.netConnFail(task.getActivity());
					result.put("Error", "连接MES失败2");
					Log.i(TAG, e.toString());					
				}				
				finally{
					task.setTaskResult(result);
				}
				
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
}
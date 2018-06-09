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
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class DIPLoadpartsModel  extends CommonModel  {

	private String userID ;
	private String UserName;
	private String LotSN;
	private String MOID;
	private String WorkcenterId;
	private String SpecificationId;
	private String I_ResourceName;
 	private String I_ResourceId;
 	private String plugInCmd = "DPLPT";
	
	String[]Params = new String[]{"","","","","",""};

	/**
	 * @param task
	 * @description Dip 物料上料扫描
	 */
	public void DIPLoadparts(Task task)   
	{

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[])task.getTaskData(); 					
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();
					
					I_ResourceName = Params[0];
					I_ResourceId = Params[1];
					LotSN = Params[2];
					MOID = Params[3];
					WorkcenterId = Params[4];
					SpecificationId = Params[5];					
				 
				 	soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_DIPLoadPartNew @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_OrBitUserName='" +
							UserName +"',@I_OrBitUserId='" + userID + "',@I_ResourceId='" + I_ResourceId + "',@I_PlugInCommand='" + plugInCmd +"',@I_ResourceName='" + I_ResourceName + "',@LotSN='" + LotSN   + "',@MOID='" + MOID + "',@WorkcenterId='" + WorkcenterId + "',@SpecificationId='" + SpecificationId +  
							 "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result;" );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.d(TAG, "bodyIn="+envolop.bodyIn.toString());
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					//String[] reses = new String[1];
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						for(int i=0;i<resMapsLis.size();i++){
							result.putAll(resMapsLis.get(i));	
						}
						task.setTaskResult(result);
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

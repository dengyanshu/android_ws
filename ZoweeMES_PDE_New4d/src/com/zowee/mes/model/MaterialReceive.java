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

public class MaterialReceive  extends CommonModel  {

	private String userID ;
	private String I_ResourceId;
	private String MOID;
	private String MOName;
	private String LotSN;
	//	private String plugInCmd = "DPLPT";

	String[]Params = new String[]{"","","",""};

	/**
	 * @param task
	 * @description Dip 物料领料扫描
	 */
	public void MaterialReceiveSubmit(Task task)   
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
					I_ResourceId = Params[0];
					MOID = Params[1];
					MOName = Params[2];
					LotSN  = Params[3];	

					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @Return_value int declare @ExceptionFieldName nvarchar(100) exec @Return_value = Txn_WMSOutScanPDA @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@UserId='" +
							userID +"',@ResourceId='" + I_ResourceId + "',@MOId='" + MOID +"',@MoName='" + MOName + "',@LotSNMB='" + LotSN  +  "' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result;" ); 

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

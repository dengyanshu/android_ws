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

public class PalletOutRoomModel  extends CommonModel  {

	private String userID ;
	private String UserName;
	private String ResourceId;	 
	private String ResourceName;
	private String CommandType; 		 
	private String CustomerCode; 	 			 
	private String PalletSN;  
 

	String[]Params = new String[]{"","","","",""};
	/**
	 * @param task
	 * @description �ֻ�������ͼ�
	 */
	public void PalletOutRoomScan (Task task)
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
					String Sql;
					Soap soap = MesWebService.getMesEmptySoap();
					Params = (String[])task.getTaskData(); 					
					userID = MyApplication.getMseUser().getUserId();
					UserName = MyApplication.getMseUser().getUserName();

					ResourceId = Params[0];	 
					ResourceName = Params[1];
					CustomerCode = Params[2];
					CommandType = Params[3];
					PalletSN = Params[4];
 
					Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int declare @ExceptionFieldName nvarchar(100)  exec @Return_value = Txn_PalletOutRoom_Phone @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@I_PlugInCommand='LPORO',@I_OrBitUserName='" +
							UserName + "',@I_OrBitUserId='" + userID + "',@I_ResourceId='" + ResourceId + "',@I_ResourceName='" + ResourceName + "',@CustomerCode='" + CustomerCode + "',@CommandType='" + CommandType + "',@PalletSN='" + PalletSN + 
							"' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@Return_value as result ;" ;
					soap.addWsProperty("SQLString", Sql );
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
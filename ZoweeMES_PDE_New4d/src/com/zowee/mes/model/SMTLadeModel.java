package com.zowee.mes.model;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;
import android.widget.Toast;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class SMTLadeModel extends CommonModel {
		
	/**
	 * @param task
	 * @description SMT 装车 
	 */
	
	String userID;
	String UserName;
	String MOName;
	String MoID;
	String CarInfo;
	String LotSN;
	String DefectcodeSn;
	public void SMTLadeToCar(Task task)
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
					String[]Params = new String[]{"","","","","","",""};
					Soap soap = MesWebService.getMesEmptySoap();
					
					Params = (String[])task.getTaskData(); 
					userID = Params[0];
				    UserName = MyApplication.getMseUser().getUserName();
					MOName  = Params[2];
					MoID  = Params[3];
					CarInfo  = Params[4];
					LotSN  = Params[5];
					DefectcodeSn= Params[6];
					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @res int declare @ExceptionFieldName nvarchar(100) exec @res = Txn_DataChainLoadCar @I_ReturnMessage=@ReturnMessage out,@I_ExceptionFieldName=@ExceptionFieldName out,@I_OrBitUserId='" + userID + "',@I_OrBitUserName='"
					                   + UserName + "',@LotSN='" + LotSN + "',@CarInfo='" + CarInfo + "',@MOID='" + MoID + "',@Moname='" + MOName  + "',@DefectcodeSn='" + DefectcodeSn +"' select @ReturnMessage as ReturnMsg, @ExceptionFieldName as exception,@res as result"   );
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
						//String[] reses = new String[1];
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
					task.setTaskResult(result);
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

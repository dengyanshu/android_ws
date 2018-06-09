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

/**
 * @author Administrator
 * @description SMT上料对料的业务逻辑类
 * 1、输入槽位条码 如S1022-10-L  用S102当做资源名称取获取资源iD和设备类型ID！
 * 2、根据设备类型ID  工单  大机台2 去获取
 */
public class FeedOnMateriModel extends CommonModel 
{

	private final static String MONAME = "MOName";//工单
	private final static String SMTMOUNTID = "SMTMountId";
 
	/**
	 * @param task
	 * @description 获得SMT上料对料 有效工单
	 */
	public void getNeceParams(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "select MOName from MO where MOStatus = '3' and MOSTDType = 'S' AND  CreateDate>DATEADD(DAY,-30,GETDATE()) order by CreateDate desc;");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						List<HashMap<String,String>> parasMapsLis = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> tempMap = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(MONAME))
							{
								parasMapsLis.add(tempMap);
							}
						}					
						task.setTaskResult(parasMapsLis);
					}
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						
				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	public void getNeceParams2(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					Soap soap = MesWebService.getMesEmptySoap();
					String moselectstr=(String) task.getTaskData();
					String sql="select MOName from MO where MOStatus = '3' and MOSTDType = 'S'and MOName like '%"+moselectstr+"%' order by CreateDate desc;";
					soap.addWsProperty("SQLString", sql);
					Log.i(TAG,"sql="+sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						List<HashMap<String,String>> parasMapsLis = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> tempMap = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(MONAME))
							{
								parasMapsLis.add(tempMap);
							}
						}					
						task.setTaskResult(parasMapsLis);
					}
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						
				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	/**
	 * @param task
	 * @description  物料上料
	 */
	public void feedOnMaterial(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] paras = (String[])task.getTaskData();
					HashMap<String,String> result = new HashMap<String,String>();
					String ResourceId = paras[0];
					String MOName = paras[1];
					String staLotSN = paras[2];
					String lotSN = paras[3];
					String feidaSN = paras[4];
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int   exec @Return_value = Txn_SMTLoadPart @I_ReturnMessage=@ReturnMessage out,@I_OrBitUserId='" + MyApplication.getMseUser().getUserId() +
							"',@I_ResourceId='" + ResourceId +  "',@MOName='" + MOName + "',@FeederNO='"+feidaSN+"',@Barcode1='" + staLotSN + "',@Barcode2='" + lotSN + "' select @ReturnMessage as ReturnMsg,@Return_value as result ;" ;
					soap.addWsProperty("SQLString",Sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
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
					//Log.i(TAG,"COME IN2");
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	/**
	 * 物料上料 重置need_alert=false  避免其他有调用这个数据库存储过程报错 
	 * 单独一个后台处理
	 */
	public void feedOnMaterial_reset_needlert(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] paras = (String[])task.getTaskData();
					HashMap<String,String> result = new HashMap<String,String>();
					String ResourceId = paras[0];
					String MOName = paras[1];
					String staLotSN = paras[2];
					String lotSN = paras[3];
					String feidaSN = paras[4];
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int   exec @Return_value = Txn_SMTLoadPart @I_ReturnMessage=@ReturnMessage out,@I_OrBitUserId='" + MyApplication.getMseUser().getUserId() +
							"',@I_ResourceId='" + ResourceId +  "',@MOName='" + MOName + "',@need_alert='false',@FeederNO='"+feidaSN+"',@Barcode1='" + staLotSN + "',@Barcode2='" + lotSN + "' select @ReturnMessage as ReturnMsg,@Return_value as result ;" ;
					soap.addWsProperty("SQLString",Sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
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
					//Log.i(TAG,"COME IN2");
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	
	
	//用于接收前台的变色确认信息
	//锁定条码
	public void feedOnMaterial2(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] paras = (String[])task.getTaskData();
					HashMap<String,String> result = new HashMap<String,String>();
					String ResourceId = paras[0];
					String lotSN = paras[1];
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int   exec @Return_value = Txn_LotSNLock @I_ReturnMessage=@ReturnMessage out,@I_OrBitUserId='" + MyApplication.getMseUser().getUserId() +
							"',@I_ResourceId='" + ResourceId +  "',@LockReason='物料变色',@Lotsn='" + lotSN + "' select @ReturnMessage as ReturnMsg,@Return_value as result ;" ;
					soap.addWsProperty("SQLString",Sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
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
					//Log.i(TAG,"COME IN2");
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	//用于接收前台的变色确认信息 不变色处理
		public void feedOnMaterial3(Task task)
		{
			//		if(null==task||null==task.getTaskData())
			//			return;
			//	Log.i(TAG,"COME IN");
			TaskOperator taskOperator = new TaskOperator() 
			{	
				@Override
				public Task doTask(Task task)
				{
					try {
						if(null==task.getTaskData()) return task;
						String[] paras = (String[])task.getTaskData();
						HashMap<String,String> result = new HashMap<String,String>();
						String ResourceId = paras[0];
						String MOName = paras[1];
						String staLotSN = paras[2];
						String lotSN = paras[3];
						
						
						Soap soap = MesWebService.getMesEmptySoap();
						
						String Sql;
						Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int  exec @Return_value = [Txn_HW_UNCOVER_4d] @I_ReturnMessage=@ReturnMessage out,@I_OrBitUserId='" + MyApplication.getMseUser().getUserId() +
								"',@I_ResourceId='" + ResourceId + "',@lotsn='" +lotSN + "' select @ReturnMessage as ReturnMsg,@Return_value as result" ;
						soap.addWsProperty("SQLString",Sql );
						SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
						if(null==envolop||null==envolop.bodyIn) return task;
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
						//Log.i(TAG,"COME IN2");
					}
					catch (Exception e) 
					{
						Log.i(TAG, e.toString());
						MesUtils.netConnFail(task.getActivity());
					}						

					return task;
				}
			};

			task.setTaskOperator(taskOperator);
			BackgroundService.addTask(task);
		}
	
	public void feedOnMaterial_4d(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] paras = (String[])task.getTaskData();
					HashMap<String,String> result = new HashMap<String,String>();
					String ResourceId = paras[0];
					String MOName = paras[1];
					String staLotSN = paras[2];
					String lotSN = paras[3];
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int declare @isalert int  exec @Return_value = [Txn_Prepare_SMTLoadPart_4d] @I_ReturnMessage=@ReturnMessage out,@isalert=@isalert output,@I_OrBitUserId='" + MyApplication.getMseUser().getUserId() +
							"',@I_ResourceId='" + ResourceId +  "',@MOName='" + MOName + "',@Barcode1='" + staLotSN + "',@Barcode2='" + lotSN + "' select @ReturnMessage as ReturnMsg,@Return_value as result,@isalert as isalert ;" ;
					soap.addWsProperty("SQLString",Sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
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
					//Log.i(TAG,"COME IN2");
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
    //新增通过MZ条码获取到槽位编码
	public void getslot(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String paras = (String)task.getTaskData();
					HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					Sql =  "SELECT dbo.Lot.LotSN,dbo.LotOnSMT.StationNO ,dbo.LotOnSMT.SLotNO FROM  dbo.Lot  INNER  JOIN  dbo.LotOnSMT  ON dbo.Lot.LotId = dbo.LotOnSMT.LotId  WHERE  lotsn='"+paras+"' ;" ;
					soap.addWsProperty("SQLString",Sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
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
					//Log.i(TAG,"COME IN2");
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	/**
	 * 
	 * @description 获得SMTMountId
	 */
	public void getSMTMountId(Task task)
	{

		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try 
				{

					if(null==task.getTaskData()) return task;
					String[] paras = (String[])task.getTaskData();

					String Moname = paras[0];
					String DeviceTypeId =  paras[1];
					String StationNo = paras[2].substring(3, 4);
					
					Soap soap = MesWebService.getMesEmptySoap();
					String sql="SELECT top 1 dbo.SMTMount.SMTMountId FROM dbo.MO INNER JOIN dbo.MO_SMTMount ON dbo.MO.MOId = dbo.MO_SMTMount.MOId INNER JOIN " +  
                            "dbo.SMTMount ON dbo.MO_SMTMount.SMTMountId = dbo.SMTMount.SMTMountId INNER JOIN dbo.SMTMountItem ON dbo.SMTMount.SMTMountId = dbo.SMTMountItem.SMTMountId " +                           
                           " WHERE dbo.MO.MOName = '" + Moname + "' AND dbo.SMTMount.DeviceTypeId = '" + DeviceTypeId + "' AND dbo.SMTMountItem.StationNo = '" +  StationNo + "'";
					soap.addWsProperty("SQLString",sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);

					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						String smtMountId = "";
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(SMTMOUNTID))
								smtMountId = tempMap.get(SMTMOUNTID);
							//Log.i(TAG, ""+smtMountId);
						}

						task.setTaskResult(smtMountId);
					}
					else
					{
						task.setTaskResult("");
					}
				}
				//	} 
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	/**
	 * 
	 * @description 检查设备分类及设备名称
	 */
	public void CheckDeviceTypeNameAndDeviceTypeId(Task task)
	{

		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try 
				{

					if(null==task.getTaskData()) return task;
					String  SourceName = (String)task.getTaskData();
					String[] reses = new String[2];
					Soap soap = MesWebService.getMesEmptySoap();
					String sql= "SELECT Resource.DeviceTypeId, DeviceType.DeviceTypeName FROM dbo.Resource WITH(NOLOCK) LEFT JOIN dbo.DeviceType " +                  
							"WITH(NOLOCK) ON dbo.Resource.DeviceTypeId = dbo.DeviceType.DeviceTypeId WHERE Resource.ResourceName='" + SourceName + "'";
					soap.addWsProperty("SQLString",sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);

					if(null!=resMapsLis&&0!=resMapsLis.size())
					{

						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey("DeviceTypeName"))
								reses[0] = tempMap.get("DeviceTypeName");
							if(tempMap.containsKey("DeviceTypeId"))
								reses[1] = tempMap.get("DeviceTypeId");
						}

						task.setTaskResult(reses);
					}
				}

				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	//bt print
	
	public void feedOnMaterial_bt(Task task)
	{
		//		if(null==task||null==task.getTaskData())
		//			return;
		//	Log.i(TAG,"COME IN");
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] paras = (String[])task.getTaskData();
					HashMap<String,String> result = new HashMap<String,String>();
					String username = paras[0];
					String MOName = paras[1];
					String slotsn = paras[2];
					String mzlotsn = paras[3];
					
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql;
					Sql =  "declare @ReturnMessage nvarchar(max) declare @Return_value int   exec @Return_value = [TXN_smtbt_Print] @I_ReturnMessage=@ReturnMessage out,"+
							"@I_OrBitUserName='" + username +  "',@MOName='" + MOName + "',@LotSN='" + mzlotsn + "',@slotsn='" + slotsn + "' select @ReturnMessage as ReturnMsg,@Return_value as result ;" ;
					soap.addWsProperty("SQLString",Sql );
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn) return task;
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
					//Log.i(TAG,"COME IN2");
				}
				catch (Exception e) 
				{
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}						

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

}

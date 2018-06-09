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
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 入库扫描的业务逻辑层
 */
public class StorageScanModel extends CommonModel 
{

	private final static String COLUMN_1 = "Column1";
	private final static String COLUMN_2 = "Column2";
	/*
	 * 开启任务    获取收货单号
	 */
	public void getGRNNO(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) 
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @poid char(12) declare @DocNo nvarchar(50) declare @temp nvarchar(100) Declare @temp1 nvarchar(100)  select @temp = '"+task.getTaskData().toString().trim()+"' Select @temp1 = @temp if upper(substring(@temp,1,1))='B' begin  select top 1 @temp = isNull(LotSN,@temp1) from lot where  BoxSN = @temp; end "+
							" select top 1 @poid = pi.POId from POItemLot pil inner join POItem pi on pil.POItemId = pi.POItemId where pil.LotSN = @temp or pil.BoxSN = @temp  select  @DocNo = md.DocNo from MaterDoc md inner join MaterReceive mr on md.MaterDocId = mr.materDocID where mr.POID = @poid  select @DocNo");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> dataStrLis = MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resDataSet = MesWebService.getResMapsLis(dataStrLis);
					String GRNNo = "";
					if(resDataSet !=null&&1==resDataSet.size())
					{
						GRNNo = resDataSet.get(0).get(COLUMN_1);
					}
					task.setTaskResult(GRNNo);
				}
				catch (Exception e) 
				{
					MesUtils.netConnFail(task.getActivity());//告知网络连接失败
					Log.i(TAG, e.toString());

				}

				return task;
			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	public void getStockCode (Task task)
	{
		//		if(null==task||StringUtils.isEmpty(task.getTaskData().toString()))
		//			return;
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) 
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					HashMap<String,String> result = new HashMap<String,String>();
					Soap soap = MesWebService.getMesEmptySoap();
					String Sql ="SELECT StockCode,StockName FROM dbo.Stock WHERE ISNULL(StockCode,'')!='' ORDER BY StockCode";
					soap.addWsProperty("SQLString", Sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> dataStrLis = MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(dataStrLis);
					Log.i(TAG, ""+resMapsLis);

					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						List<HashMap<String,String>> parasMapsLis = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> tempMap = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							tempMap = resMapsLis.get(i);
							if(tempMap.containsKey("StockCode"))
							{
								parasMapsLis.add(tempMap);
							}
						}

						task.setTaskResult(parasMapsLis);
					}
				}
				catch (Exception e) 
				{
					MesUtils.netConnFail(task.getActivity());//告知网络连接失败
					Log.i(TAG, e.toString());

				}

				return task;
			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	public void storageScan(Task task)
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
					String[]Params = new String[]{"","","",""};
					Soap soap = MesWebService.getMesEmptySoap();
					String StockCode;
					String DeliverySN;
					String barcode;
					Params = (String[])task.getTaskData(); 
					String userID = MyApplication.getMseUser().getUserId().toString();
					String resourceId;
					StockCode = Params[0];
					DeliverySN = Params[1];
					barcode = Params[2];
					resourceId = Params[3];
                    String sql="declare @ReturnMessage nvarchar(max) declare @res int  exec @res = Txn_MaterialInStock @ReturnMessage=@ReturnMessage out,@userId='" + userID + "',@resourceId='" + resourceId +   
                    		 "',@StockCode='" + StockCode + "',@DeliverySN='" + DeliverySN + "',@barcode='" + barcode + "' select @ReturnMessage as ReturnMsg,@res as result";
					soap.addWsProperty("SQLString",  sql  );
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
	
	/*public void storageScan(Task task)
	{
//		if(null==task)
//			return;
		TaskOperator taskOperator = new TaskOperator() 
		{			
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
					String[] datas = (String[])task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "declare @ReturnMessage nvarchar(max) declare @res int exec @res = Txn_CheckIQCResults  @ReturnMessage output,'"+datas[0]+"','"+MyApplication.getMseUser().getUserId()+"','','"+datas[1]+"','"+datas[2]+"' select ''+STR(@res),@ReturnMessage");
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					Log.i(TAG, ""+resMapsLis);
					String[] reses = new String[2];
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						for(int i=0;i<resMapsLis.size();i++)
						{
							HashMap<String,String> tempMap = resMapsLis.get(i);
							if(tempMap.containsKey(COLUMN_1))
								reses[0] = tempMap.get(COLUMN_1);
							if(tempMap.containsKey(COLUMN_2))
								reses[1] = tempMap.get(COLUMN_2);
						}
						Log.i(TAG, "RES:"+reses[0]+" LOG: "+reses[1]);
						task.setTaskResult(reses);
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					MesUtils.netConnFail(task.getActivity());
					Log.i(TAG, e.toString());
				}		

				return task;
			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	} */







}

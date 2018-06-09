package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 查料站表业务逻辑类 
 */
public class CheMatStaTabModel extends CommonModel 
{

	private static  String SMTMountName = "SMTMountName";
	private static  String SMTLineNO = "SMTLineNO";
	private static  String StationNo = "StationNo";
	private static  String SLotNO = "SLotNO";
	private static  String ProductName = "ProductName";
	private static  String ProductDescription = "ProductDescription";
	private static  String BaseQty = "BaseQty";
	private static String SwitchLine = "\n";
	//	private static final String COLUMN_1 = "Column1";

	/**
	 * @param task
	 * @description 查料站表 
	 */
	public void cheMatStaTab(Task task)
	{
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] param = new String[]{"",""};
					param = (String[]) task.getTaskData();
					String docStaNo = param[0];
					//String docNo = docStaNo.substring(0,3);
					String staNo = docStaNo.substring(3,4);
					String MONum = param[1];
					String DeviceTypeId = param[2]; 
					Soap soap = MesWebService.getMesEmptySoap();
//					String Sql ="SELECT dbo.SMTMount.SMTMountName,dbo.DeviceType.DeviceTypeName,dbo.SMTMountItem.StationNo,dbo.SMTMountItem.SLotNO,dbo.ProductRoot.ProductName,dbo.Product.ProductDescription,dbo.SMTMountItem.BaseQty, SUM(dbo.LotOnSMT.BeginQty) AS MountQTY,"+ 
//							"SUM(dbo.Lot.Qty) AS ResidueQTY FROM dbo.LotOnSMT  WITH(NOLOCK) INNER JOIN dbo.Lot WITH(NOLOCK) ON dbo.Lot.LotId = dbo.LotOnSMT.LotId RIGHT OUTER JOIN dbo.MO  WITH(NOLOCK) INNER JOIN dbo.SMTMount WITH(NOLOCK) "+		  
//							"INNER JOIN dbo.MO_SMTMount WITH(NOLOCK) ON dbo.SMTMount.SMTMountName = dbo.MO_SMTMount.SMTMountName "+ 
//							"INNER JOIN dbo.SMTMountItem WITH(NOLOCK) ON dbo.SMTMount.SMTMountId = dbo.SMTMountItem.SMTMountId " +
//							"INNER JOIN dbo.DeviceType WITH(NOLOCK) ON dbo.SMTMount.DeviceTypeId=DeviceType.DeviceTypeId " +
//							"INNER JOIN dbo.ProductRoot WITH(NOLOCK) INNER JOIN dbo.Product WITH(NOLOCK) ON dbo.ProductRoot.ProductRootId = dbo.Product.ProductRootId " +
//							"ON dbo.SMTMountItem.ProductId = dbo.Product.ProductId ON dbo.MO.MOId = dbo.MO_SMTMount.MOId ON dbo.LotOnSMT.MOId = dbo.MO.MOId WHERE dbo.MO.MOName = '" +  
//							MONum + "' AND dbo.DeviceType.DeviceTypeId =  '"  + DeviceTypeId +  "' AND dbo.SMTMountItem.StationNo = '" +  staNo + "' GROUP BY dbo.SMTMount.SMTMountName," +                         
//							"dbo.DeviceType.DeviceTypeName, dbo.SMTMountItem.StationNo, dbo.SMTMountItem.SLotNO,dbo.ProductRoot.ProductName, dbo.Product.ProductDescription,dbo.SMTMountItem.BaseQty ORDER BY dbo.SMTMountItem.StationNo, dbo.SMTMountItem.SLotNO";		
					
					
					String  Sql="SELECT  dbo.SMTMount.SMTMountName,dbo.SMTMount.side,dbo.DeviceType.DeviceTypeName,dbo.SMTMountItem.StationNo,dbo.SMTMountItem.SLotNO,dbo.ProductRoot.ProductName,dbo.Product.ProductDescription,dbo.SMTMountItem.BaseQty,dbo.LotOnSMT.BeginQty,Lot.Qty FROM dbo.SMTMountItem "+  
							"INNER  JOIN  product ON  dbo.SMTMountItem.ProductId = dbo.Product.ProductId "+
							"INNER JOIN  dbo.ProductRoot  ON  dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId "+
							"INNER  JOIN  dbo.SMTMount  ON  dbo.SMTMountItem.SMTMountId = dbo.SMTMount.SMTMountId "+
							"INNER  JOIN  dbo.DeviceType  ON dbo.SMTMount.DeviceTypeId = dbo.DeviceType.DeviceTypeId "+
							"INNER  JOIN  dbo.MO_SMTMount  ON  dbo.SMTMount.SMTMountName = dbo.MO_SMTMount.SMTMountName "+
							"INNER  JOIN  dbo.MO  ON  dbo.MO_SMTMount.MOId = dbo.MO.MOId "+
							"left JOIN  dbo.LotOnSMT  ON dbo.LotOnSMT.MOId=dbo.MO.MOId  AND  dbo.LotOnSMT.SLotNO=dbo.SMTMountItem.SLotNO  AND  dbo.LotOnSMT.StationNO LIKE '%"+staNo+"' "+
							"left  JOIN  lot  ON  dbo.LotOnSMT.LotId = dbo.Lot.LotId "+
							"WHERE MOName='"+MONum+"' AND  dbo.SMTMountItem.StationNo='"+staNo+"' ";
					soap.addWsProperty("SQLString",Sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);	
					if(null==envolop||null==envolop.bodyIn)
						return task;	
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
					if(null!=resMapsLis&&0!=resMapsLis.size())
					{
						List<HashMap<String,String>> parasMapsLis = new ArrayList<HashMap<String,String>>();
						HashMap<String,String> tempMap = null;
						for(int i=0;i<resMapsLis.size();i++)
						{
							tempMap = resMapsLis.get(i);
							if(tempMap.containsKey("SMTMountName"))
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

	/*private String replaceResStr(String str)
	{
		if(StringUtils.isEmpty(str))
			return null;
		str = str.replaceAll(SwitchLine, "");
		str = str.replaceAll(SMTMountName, "上料名");
		str = str.replaceAll(SMTMountName, "上料名");
		str = str.replaceAll(SLotNO, "站位");
		str = str.replaceAll(SMTLineNO, "产线");
		str = str.replaceAll(StationNo, "机台");
		str = str.replaceAll(ProductName, "产品名称");
		str = str.replaceAll(ProductDescription, "产品描述");
		str =str.replaceAll(BaseQty, "上料数量");

		return str;
	}*/


	public CharSequence  hignRetMsg(CharSequence res1,CharSequence res2,int color)
	{

		SpannableString spanStr = new SpannableString(res1.toString()+res2.toString());
		ForegroundColorSpan spanCondition = new ForegroundColorSpan(color);
		spanStr.setSpan(spanCondition, 0, res1.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spanStr;
	}

	public void getSlotCount(Task task) // 已上料
	{
		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try {
					if(null==task.getTaskData()) return task;
					String[] param = new String[]{"",""};
					param= (String[])task.getTaskData();
					String docStaNo = param[0];
					String docNo = docStaNo.substring(0,3);
					String staNo = docStaNo.substring(3,4);
					Soap soap = MesWebService.getMesEmptySoap();
					String MONum =param[1];
					soap.addWsProperty("SQLString","SELECT COUNT(DISTINCT dbo.SMTMountItem.SLotNO) AS count  FROM dbo.SMTMount INNER JOIN dbo.SMTMountItem ON dbo.SMTMount.SMTMountId = dbo.SMTMountItem.SMTMountId INNER JOIN  dbo.Lot " +
							"INNER JOIN dbo.LotOnSMT ON dbo.Lot.LotId = dbo.LotOnSMT.LotId INNER JOIN  dbo.MO ON dbo.LotOnSMT.MOId = dbo.MO.MOId ON dbo.SMTMountItem.SMTMountItemId = dbo.LotOnSMT.SMTMountItemId WHERE dbo.MO.MOName = '" +         
							MONum + "' AND dbo.SMTMount.SMTLineNO = '" + docNo + "' AND dbo.SMTMount.StationNo = '" + staNo + "' AND dbo.LotOnSMT.BeginQty is not null AND dbo.Lot.Qty > 0");

					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);	
					if(null==envolop||null==envolop.bodyIn)
						return task;	
					List<String> resDataSet = MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					if(null==resDataSet||0==resDataSet.size())
						return task;
					List<HashMap<String,String>> lisMaps = MesWebService.getResMapsLis(resDataSet);
					if(null!=lisMaps&&0!=lisMaps.size())
					{
						String slotCount = lisMaps.get(0).get("count");
						task.setTaskResult(slotCount);
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

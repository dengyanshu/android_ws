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
 */
public class DipMOSpecificationBom extends CommonModel 
{

	/**
	 * @param task
	 * @description 获得指定订单号及规程的BOM信息
	 */
	/*public void GetSpecificationBom(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
                    String [] param =new String[]{"",""};;
                    param = (String[])task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString", "exec Txn_GetMOSpecificationBomNew '" + param[0] + "','" + param[1] + "'"  );
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
							if(tempMap.containsKey("ProductId"))
							{
								parasMapsLis.add(tempMap);
							}
						}

						task.setTaskResult(parasMapsLis);
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
	} */
	/**
	 * @param task
	 * @description 获得指定订单号及规程的物料信息
	 */
	public void GetDipMaterialList(Task task)
	{
		TaskOperator taskOperator = new TaskOperator() 
		{	
			@Override
			public Task doTask(Task task)
			{
				try {
					String [] param2 =new String[]{"","","","","",""};
					param2 = (String[])task.getTaskData();
					String moId = param2[0]; 
					String SpecificationId =param2[1];
					String WorkcenterId = param2[2];
					String materialSn = param2[3];
					String materialName =param2[4];
					String materialremoved = param2[5];

					Soap soap = MesWebService.getMesEmptySoap();
					//String sqlcmd = "SELECT dbo.LotOnDip.LotOnDipId,MO.MOName, dbo.Workcenter.WorkcenterName, dbo.SpecificationRoot.SpecificationName, Lot.LotSN,
					String sqlcmd = "SELECT Lot.LotSN, dbo.Workcenter.WorkcenterName, B.ProductName+(CASE WHEN ISNULL(A.ProductDescription,'')='' THEN '' ELSE '/'+A.ProductDescription END) AS MaterialName," +
							" dbo.LotOnDipHistory.Qty AS LoadQty,  Lot.Qty AS NowQty, (CASE WHEN ISNULL(Product.ProductId,'')=ISNULL(A.ProductId,'')   THEN NULL " +
							" ELSE dbo.ProductRoot.ProductName+(CASE WHEN ISNULL(Product.ProductDescription,'')='' THEN '' ELSE '/'+Product.ProductDescription END)	" +	
							" END) AS ReplaceProductName, (CASE WHEN dbo.LotOnDip.LotOnDipId IS NULL THEN 'true' ELSE 'false' END) AS IsDelete, ISNULL(Lot.IsLock,'false') AS IsLock," +
							" CONVERT(NVARCHAR(20),dbo.LotOnDipHistory.CreateDate,120) AS CreateDate " + 
							" FROM dbo.LotOnDipHistory WITH(NOLOCK)" +
							" LEFT JOIN dbo.Lot WITH(NOLOCK) ON dbo.LotOnDipHistory.LotId = dbo.Lot.LotId" +
							" LEFT JOIN dbo.LotOnDip WITH(NOLOCK) ON dbo.LotOnDip.LotOnDipId=LotOnDipHistory.LotOnDipId" +
							" LEFT JOIN dbo.MO WITH(NOLOCK) ON dbo.LotOnDipHistory.MOId = dbo.MO.MOId" +
							" LEFT JOIN dbo.Product WITH(NOLOCK) ON dbo.LotOnDipHistory.ProductId=Product.ProductId" +
							" LEFT JOIN dbo.ProductRoot WITH(NOLOCK) ON dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId" +
							" LEFT JOIN dbo.Product A WITH(NOLOCK) ON dbo.Lot.ProductId=A.ProductId" +
							" LEFT JOIN dbo.ProductRoot B WITH(NOLOCK) ON A.ProductRootId = B.ProductRootId" +
							" LEFT JOIN dbo.Specification WITH(NOLOCK) ON dbo.LotOnDipHistory.SpecificationId=dbo.Specification.SpecificationId" +
							" LEFT JOIN dbo.SpecificationRoot WITH(NOLOCK) ON dbo.Specification.SpecificationRootId = dbo.SpecificationRoot.SpecificationRootId"+
							" LEFT JOIN dbo.Workcenter WITH(NOLOCK) ON LotOnDipHistory.WorkcenterId=dbo.Workcenter.WorkcenterId" +
							" LEFT JOIN dbo.SysUser WITH(NOLOCK) ON LotOnDipHistory.UserId=dbo.SysUser.UserId WHERE 1=1 " +
							" AND dbo.LotOnDipHistory.MOId='" + moId   + "' AND dbo.LotOnDipHistory.SpecificationId='" +SpecificationId +"'";
				    if(!WorkcenterId.equals("显示所有产线"))
				    	sqlcmd += "' AND dbo.LotOnDipHistory.WorkcenterId='" + WorkcenterId ;				    	
					if(materialremoved.equals("未卸料"))	 
					{
						sqlcmd += " AND dbo.LotOnDip.LotOnDipId IS NOT NULL";
					}else if(materialremoved.equals("已卸料"))
					{
						sqlcmd += " AND dbo.LotOnDip.LotOnDipId IS NULL";
					}
					if(materialSn.trim().length() >=8)
						sqlcmd  += " AND Lot.LotSN='" + materialSn + "'";
					if(materialName.trim().length() >=8)
						sqlcmd  += " AND B.ProductName='" + materialName + "'";
					soap.addWsProperty("SQLString", sqlcmd);
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
							if(tempMap.containsKey("LotSN"))
							{
								parasMapsLis.add(tempMap);
							}
						}

						task.setTaskResult(parasMapsLis);
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

}

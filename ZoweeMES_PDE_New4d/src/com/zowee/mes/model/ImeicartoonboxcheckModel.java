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


public class ImeicartoonboxcheckModel extends CommonModel 
{

	public void getImeilist(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					String cartoonbox= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "select PrimaryIMEI  from CartoonBox inner join lot on boxid=cartoonboxid inner join mo on cartoonbox.moid=mo.moid where CartoonBoxsn='"+cartoonbox+"' ";
					// String
					// sql="SELECT WorkcenterId,WorkcenterName,WorkcenterDescription FROM dbo.Workcenter  WITH(NOLOCK) where WorkcenterDescription <> ''   ORDER BY WorkcenterName ";
					// String
					// sql=" select mo.MOId,pr.ProdName ,ProductDescription ,pd.ProductSpecification  from mo  "+
					// "inner join Product pd on mo.ProductId=pd.Productid  "+
					// "inner join ProductRoot pr on mo.ProductId=pr.DefaultProductId where mo.MOName like'MOR01021308051%' ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "resDATASET=:::" + resDataSet);
					// resDataSet=[1=1;2=2;3=3;,1=1;2=2;3= ,]
					List<HashMap<String, String>>  result = MesWebService
							.getResMapsLis(resDataSet);
					// getResMapsLis 直接进入异常抓取
					if (null == resDataSet) {
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
						Log.i(TAG, "这里有执行2");

						task.setTaskResult(result);
						Log.i(TAG, "result=" + result);

					}
				} catch (Exception e) {
					Log.i(TAG, "这里有执行3");
					Log.i(TAG, e.toString());
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	public void iemicartoonboxcheck(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

					String[] Params = new String[5] ;
					Params = (String[]) task.getTaskData();

					String useid = Params[0];
					String usename = Params[1];
					String resourceid = Params[2];
					String resourcename = Params[3];
					
					String boxsn = Params[4];


					String sql = " declare @res int declare @message nvarchar(max)  exec @res= TxnBase_boxMove @boxSN ='"+boxsn+"',"
							+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
							+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
						// String[] reses = new String[1];
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

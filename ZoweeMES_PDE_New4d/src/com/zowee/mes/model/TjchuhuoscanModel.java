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


public class TjchuhuoscanModel extends CommonModel 
{

	public void getdingdan(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT pow as 订单号 FROM dbo.pow ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						
					}
					else {
						result = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + result);
						// String[] reses = new String[1];
						
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
	
	
	public void getchuhuodan(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
					String para=  (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT  powpoid ,  powpo.ModelCode as 代码,destination as 出货地, powpo.color as 描述,powpo.electric as 电容量,"+
							"QtyRequired as 需求量, QtyInput as 出货量, ProductName as 料号,powpo.BoxQty  as 箱容量   ,CustomerModel as 客户机型 ,zmpo as 出货单  FROM  powpo  "+
							"INNER JOIN ProductRoot ON productid = DefaultProductId  INNER JOIN dbo.Product ON dbo.ProductRoot.DefaultProductId = dbo.Product.ProductId  "+
							"INNER  JOIN  dbo.pow  ON  dbo.powpo.powid=dbo.pow.powid   WHERE pow='"+para+"' ";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					Log.i(TAG, "body.in=:::" + envolop.bodyIn.toString());
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						
					}
					else {
						result = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + result);
						// String[] reses = new String[1];
						
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
	
	public void chuhuoscan(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String[]   Params = (String[]) task.getTaskData();

					String resourceid = Params[0];
					String resourcename = Params[1];
					String useid = Params[2];
					String usename = Params[3];
					
					String chudanid = Params[4];
					String boxsn = Params[5];
					
					String sql = " declare @res int declare  @qty int  declare  @total int  declare @message nvarchar(max)  exec @res= [Txn_pow_po_Box_print_pde] "
							+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
							+"@powpoid ='"+chudanid+"',@BoxSN ='"+boxsn+"',@Qty =@qty  out,@total=@total out,"
							+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage,@Qty as qty, @total as total";
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

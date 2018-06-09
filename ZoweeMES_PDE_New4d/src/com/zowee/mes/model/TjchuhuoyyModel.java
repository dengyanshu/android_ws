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


public class TjchuhuoyyModel extends CommonModel 
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
					String sql = "SELECT  yy  FROM  yy WHERE  ISNULL(yy,'')<>''";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn) {
						return task;
					}
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					if (null == resDataSet) {
						
					}
					else {
						result = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + result);
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
					String sql = "SELECT  yypoid , yypo ,destination ,"+
							" QtyRequired , QtyInput ,(QtyRequired-QtyInput) AS shengyuqty,  ProductName ,yypo.BoxQty  "+
							" ,CustomerModel ,CartoonQty  FROM  yypo "+
							" INNER  JOIN  yy  ON yy.yyid = yypo.yyid "+
							" INNER JOIN ProductRoot ON productid = DefaultProductId "+
							" INNER JOIN dbo.Product ON dbo.ProductRoot.DefaultProductId = dbo.Product.ProductId WHERE yy='"+para+"' ";
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
	
	public void checkzhanban(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					
					Soap soap = MesWebService.getMesEmptySoap();

				    String[]   Params = (String[]) task.getTaskData();

					String yypoid = Params[0];
					String zhanbansn = Params[1];
				
					
					String sql = " declare @res int declare  @qty int  declare @message nvarchar(max)  exec @res= [sp_yy_check_pallent] "
							+"@yypoid ='"+yypoid+"',@pallentsn ='"+zhanbansn+"',@Qty =@qty  out,"
							+ " @MSG=@message out select @res as I_ReturnValue,@message as I_ReturnMessage,@Qty as qty";
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
				    
				   

					String yypoid = Params[0];
					String zhanbansn = Params[1];
					String boxsn = Params[2];
					String qty_shengyu = Params[3];
					String zhanbanqty_standrad = Params[4];
					String zhanbanqty_real = Params[5];
					
				
					String sql = " declare @res int declare @IsComplete char(1) declare @showtoui_zhanban int declare @showtoui_xianghao int  declare @showtoui_countqty int  " +
							"  declare @message nvarchar(max)  exec @res= [Txn_yy_InPallet] "
							+"@yypoid ='"+yypoid+"',@BoxSN ='"+boxsn+"',@pallentsn ='"+zhanbansn+"' ,@resQty ="+qty_shengyu+",@pallentqty ="+zhanbanqty_standrad+",@Maxqty ="+zhanbanqty_real+","
							+ " @I_ReturnMessage=@message out , @IsComplete=@IsComplete out,@preQty=@showtoui_zhanban out,@boxQty=@showtoui_xianghao out,@totail=@showtoui_countqty out  " +
							"select @res as I_ReturnValue,@IsComplete  as IsComplete,@message as I_ReturnMessage,@showtoui_zhanban as showtoui_zhanban, @showtoui_xianghao as showtoui_xianghao,@showtoui_countqty as showtoui_countqty";
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

package com.zowee.mes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;

public class LiuloufvmiModel extends CommonModel {
	
	public void liuloufvmi(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[7] ;
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();

					String useid = Params[0];
					String usename = Params[1];
					String resourceid = Params[2];
					String resourcename = Params[3];
					
					String sn = Params[4];
					String errorcode = Params[5];
					String fvmitype = Params[6];
					
					String execname="";
					if(fvmitype.equals("1")){
						//开机测试
						execname="Txn_LenovoPAD_staTEST_PAD";
					}
					if(fvmitype.equals("2")){
						//功能测试
						execname="Txn_LenovoPAD_TEST_PAD";
					}
					if(fvmitype.equals("3")){
						//目检
						execname="Txn_LenovoPAD_ASSY_PAD";
					}
					if(fvmitype.equals("4")){
						//目检
						execname="Txn_LenovoPAD_ASSY_PAD_PreLoad";
					}
					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
							+ Params[2] + "," + Params[3] + "," + Params[4]
							+ "," + Params[5]);

					String sql = " declare @res int declare @message nvarchar(max) declare @exception nvarchar(100)  exec @res="+execname+"   "
							+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',"
							+ "@USERCODE='"+errorcode+"',@PCBASN='"+sn+"',"
							+ " @I_ReturnMessage=@message  out,@I_ExceptionFieldName=@exception out  select @res as I_ReturnValue,@message as I_ReturnMessage,@exception as I_ExceptionFieldName";
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

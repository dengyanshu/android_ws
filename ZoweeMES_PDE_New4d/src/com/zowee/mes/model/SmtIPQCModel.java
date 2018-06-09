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


public class SmtIPQCModel extends CommonModel 
{

	public void getmonamebylotsn(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String sn= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT MOName FROM  dbo.Lot  INNER JOIN  dbo.MO ON dbo.Lot.MOId = dbo.MO.MOId WHERE LotSN='"+sn+"'";
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
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
	
	public void getpcbanumincar(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String sn= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "select COUNT (lotid) InCarLotSNQty from DataChainLoadcar with(nolock) where CarSN='"+sn +"'and ISNULL(DataChainLoadcar.DefectcodeSn,'')=''";
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
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
	
	public void ipqcpcba(Task task) {

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
					
					String moname = Params[4];
					String car = Params[5];
					String lotsn = Params[6];
					String errorcode = Params[7];
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_SMTSpotCheckLotSnDoMethod] @MoSn ='"+moname+"',"
							+"@CarSn ='"+car+"',@LotSn ='"+lotsn+"',@DefectcodeSn ='"+errorcode+"',"
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
    
	
	public void ipqccar(Task task) {

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
					
					String moname = Params[4];
					String car = Params[5];
					String passorfail = Params[6];
					
					
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_SMTPCBA_IPQC_ALLCheckDoMethod] @MoSn ='"+moname+"',"
							+"@CarSn ='"+car+"',@QCCheckResult ='"+passorfail+"',"
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
   
	//扫描栈板获取PO 通过 sn
	
	public void getPO(Task task) {
		// TODO Auto-generated method stub
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String sn= (String) task.getTaskData();
				    
					Soap soap = MesWebService.getMesEmptySoap();
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_TrayStockOut_GetPO] @PalletBoxSN ='"+sn+"',"
							+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
    
	
	
	//扫描栈板获取PO 通过 sn
	
		public void getPO2(Task task) {
			// TODO Auto-generated method stub
			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					try {
						if (null == task.getTaskData())
							return task;
						HashMap<String, String> result = new HashMap<String, String>();
						String sn= (String) task.getTaskData();
					    
						Soap soap = MesWebService.getMesEmptySoap();
						String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_TrayStockOut_GetPO_NEW] @PalletBoxSN ='"+sn+"',"
								+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
							//task.setTaskResult(result);
						 // result.put("Error", "连接MES失败");
							Log.i(TAG, "这里有执行2a");
						} else {
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
	    
	//扫描栈板
	
	public void canzhanban(Task task) {
		// TODO Auto-generated method stub
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
						
						String po = Params[4];
						String checkpo = Params[5];
						String zhanbansn = Params[6];
						String box1 = Params[7];
						String box2 = Params[8];
						String box3 = Params[9];
						String box4 = Params[10];
				    
					
					
					
				
					String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_TrayStockOut] @Po ='"+po+"',"
							+" @CheckPO='"+checkpo+"' ,@PalletLotSN ='"+zhanbansn+"',@CarToonBoxSN1 ='"+box1+"',@CarToonBoxSN2 ='"+box2+"',@CarToonBoxSN3 ='"+box3+"',@CarToonBoxSN4 ='"+box4+"',"
							+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
						//task.setTaskResult(result);
					 // result.put("Error", "连接MES失败");
						Log.i(TAG, "这里有执行2a");
					} else {
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
	
	
	//扫描栈板
	
		public void canzhanban2(Task task) {
			// TODO Auto-generated method stub
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
							
							String po = Params[4];
							String checkpo = Params[5];
							String zhanbansn = Params[6];
							String box1 = Params[7];
							String box2 = Params[8];
							String box3 = Params[9];
							String box4 = Params[10];
					    
						
						
						
					
						String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_TrayStockOut_NEW] @Po ='"+po+"',"
								+" @CheckPO='"+checkpo+"' ,@PalletLotSN ='"+zhanbansn+"',@CarToonBoxSN1 ='"+box1+"',@CarToonBoxSN2 ='"+box2+"',@CarToonBoxSN3 ='"+box3+"',@CarToonBoxSN4 ='"+box4+"',"
								+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
							//task.setTaskResult(result);
						 // result.put("Error", "连接MES失败");
							Log.i(TAG, "这里有执行2a");
						} else {
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
		
		//smtioqcmz  用  
		
		public void smtipqcmz(Task task) {
			// TODO Auto-generated method stub
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
							
							String mzlotsn = Params[4];
							
						
						
						
					
						String sql = " declare @res int declare @message nvarchar(max)  exec @res= [Txn_smtipqc_mz] @lotsn ='"+mzlotsn+"',"
								+" @I_OrBitUserName='"+usename+"',@I_OrBitUserId='"+useid+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"', "
								+ " @I_ReturnMessage=@message out select @res as I_ReturnValue,@message as I_ReturnMessage";
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
							//task.setTaskResult(result);
						 // result.put("Error", "连接MES失败");
							Log.i(TAG, "这里有执行2a");
						} else {
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
	
}

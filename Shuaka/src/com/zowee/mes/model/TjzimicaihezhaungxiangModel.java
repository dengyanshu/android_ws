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

public class TjzimicaihezhaungxiangModel extends CommonModel {

	public void getmo(Task task) {

		TaskOperator taskOperator = new TaskOperator()
		{		
			@Override
			public Task doTask(Task task)
			{
				try 
				{
					if(null==task.getTaskData()) return task;
				String para=	(String) task.getTaskData();
					List<HashMap<String,String>> result = new ArrayList<HashMap<String, String>>();
					Soap soap = MesWebService.getMesEmptySoap();
					//String  sql=" select *  from  forde";
					String sql = "   SELECT MO.MOId AS moid,MO.MOName AS moname, ProductRoot.ProductName AS product,Product.BoxQty AS boxqty,10 AS oqcqty"
							+" FROM dbo.MO  WITH(NOLOCK) LEFT JOIN dbo.Product  WITH(NOLOCK) ON dbo.MO.ProductId = dbo.Product.ProductId"
					+" LEFT JOIN dbo.ProductRoot  WITH(NOLOCK) ON dbo.Product.ProductRootId = dbo.ProductRoot.ProductRootId ORDER BY MO.MOName";
					soap.addWsProperty("SQLString", sql);
					SoapSerializationEnvelope envolop = MesWebService.getWSReqRes(soap);
					if(null==envolop||null==envolop.bodyIn)
						return task;
					Log.i(TAG, "bodyIn="+envolop.bodyIn.toString());
					String resdatastr=MesWebService.WsDataParser.getUseDataForDataPart(envolop.bodyIn.toString());
					Log.i(TAG,"resdatastr="+resdatastr);
					List<String> resDataSet =  MesWebService.WsDataParser.getResDatSet(envolop.bodyIn.toString());
					Log.i(TAG, "bodyin.tostring解析出来的resdataset="+resDataSet);
					if(null==resDataSet){
						Log.i(TAG,"testmodel youzhixing2");
					}
					else
					{
						Log.i(TAG,"testmodel youzhixing3");
						result = MesWebService.getResMapsLis(resDataSet);
						Log.i(TAG,"testmodel result"+result);
						
					}	
					task.setTaskResult(result);
				}
				catch (Exception e)
				{
					
					Log.i(TAG,"testmodel youzhixing1");
					MesUtils.netConnFail(task.getActivity());
				}
				return task;
			}
		};
		
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	
	
	public void zhuangxiang(Task task) {
  //传了插件命令 (ZMIBX)
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;
					HashMap<String, String> result = new HashMap<String, String>();
					String[] Params = new String[9] ;
					Soap soap = MesWebService.getMesEmptySoap();

					Params = (String[]) task.getTaskData();
					String useid = Params[0];
					String usename = Params[1];
					String resourceid = Params[2];
					String resourcename = Params[3];

					String moid = Params[4];
					String zhuangxiangshu = Params[5];
					String songjianshu = Params[6];
					String zhongxiangsn = Params[7];
					String caihesn = Params[8];

					Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
							+ Params[2] + "," + Params[3] + "," + Params[4]
							+ "," + Params[5]+Params[6]+Params[7]);

					String sql = " declare @res int declare @boxid nvarchar(max) declare @message nvarchar(max) declare @exception nvarchar(100) declare @iscomplete bit  exec @res=Txn_InBoxZM    "
							+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',@I_PlugInCommand='ZMIBX', "
							+ "@MOId='"+moid+"',@BoxQty="+zhuangxiangshu+",@OQCQty="+songjianshu+", @BoxSN ='"+zhongxiangsn+"',@LotSN ='"+caihesn+"',"
							+ " @I_ReturnMessage=@message  out,@BoxId=@boxid out, @I_ExceptionFieldName=@exception out,@IsComplete=@iscomplete out select @res as I_ReturnValue,@boxid as BoxId,@message as I_ReturnMessage,@exception as I_ExceptionFieldName,@iscomplete as iscomplete";
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
	public void weishuzhuangxiang(Task task) {
		  //传了插件命令 (ZMIBX)
				TaskOperator taskOperator = new TaskOperator() {
					@Override
					public Task doTask(Task task) {
						try {
							if (null == task.getTaskData())
								return task;
							HashMap<String, String> result = new HashMap<String, String>();
							String[] Params = new String[9] ;
							Soap soap = MesWebService.getMesEmptySoap();

							Params = (String[]) task.getTaskData();
							String useid = Params[0];
							String usename = Params[1];
							String resourceid = Params[2];
							String resourcename = Params[3];

							String moid = Params[4];
							String songjianshu = Params[5];
							String xianghaoid = Params[6];

							Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
									+ Params[2] + "," + Params[3] + "," + Params[4]
									+ "," + Params[5]+Params[6]);

							String sql = " declare @res int declare @message nvarchar(max) declare @exception nvarchar(100)   exec @res=Txn_InBoxZM_InBox    "
									+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',@I_PlugInCommand='ZMIBX', "
									+ "@MOId='"+moid+"',@OQCQty="+songjianshu+", @BoxId ='"+xianghaoid+"',@Flag=0,"
									+ " @I_ReturnMessage=@message  out, @I_ExceptionFieldName=@exception out select @res as I_ReturnValue,@message as I_ReturnMessage,@exception as I_ExceptionFieldName";
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
	
	     public void qingkongxianghao(Task task) {
		  //传了插件命令 (ZMIBX)
				TaskOperator taskOperator = new TaskOperator() {
					@Override
					public Task doTask(Task task) {
						try {
							if (null == task.getTaskData())
								return task;
							HashMap<String, String> result = new HashMap<String, String>();
							String[] Params = new String[9] ;
							Soap soap = MesWebService.getMesEmptySoap();

							Params = (String[]) task.getTaskData();
							String useid = Params[0];
							String usename = Params[1];
							String resourceid = Params[2];
							String resourcename = Params[3];

							String moid = Params[4];
							String songjianshu = Params[5];
							String xianghaoid = Params[6];

							Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
									+ Params[2] + "," + Params[3] + "," + Params[4]
									+ "," + Params[5]+Params[6]);

							String sql = " declare @res int  declare @message nvarchar(max) declare @exception nvarchar(100)   exec @res=Txn_InBoxZM_InBox    "
									+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',@I_PlugInCommand='ZMIBX', "
									+ "@MOId='"+moid+"',@OQCQty="+songjianshu+", @BoxId ='"+xianghaoid+"',@Flag=1,"
									+ " @I_ReturnMessage=@message  out, @I_ExceptionFieldName=@exception out select @res as I_ReturnValue,@message as I_ReturnMessage,@exception as I_ExceptionFieldName";
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
	     //根据箱号获取到送检单号
	     /**  LotSN         CartoonBoxSN      SampleInspectionSN          WorkcenterId
	      WSDFASDFASDF     PB02Z672212346       OQC46BA5044173Z             WKC10000004A
	    
	     EXEC Txn_MaterialInBoxQuery
	     'WKC10000004A','OQC46DB4631997W',4

	     Txn_MaterialInBoxQuery 
	         @WorkcenterId CHAR(12)=NULL,
	         @SN NVARCHAR(50),
	         @Flag INT  --(0:通过批号查箱号 1：通过批号查送检单  2：通过箱号查送检单  3：通过送检单查箱号  4：通过送检单查批号  5：查询所有未送检的送检单)     */
	    
         
	      public void getoqcdanhao(Task task) {
			  //传了插件命令 (ZMIBX)
					TaskOperator taskOperator = new TaskOperator() {
						@Override
						public Task doTask(Task task) {
							try {
								if (null == task.getTaskData())
									return task;
								HashMap<String, String> result = new HashMap<String, String>();
								
								Soap soap = MesWebService.getMesEmptySoap();

								String Params = (String) task.getTaskData();

								Log.i(TAG, "任务数据是:" + Params);

								String sql = " exec  Txn_MaterialInBoxQuery @SN='"+Params+"',@Flag=2";
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
	     
	      public void shougongsongjian(Task task) {
			  //传了插件命令 (ZMIBX)
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

								String oqcdan = Params[4];
								

								Log.i(TAG, "任务数据是:" + Params[0] + "," + Params[1] + ","
										+ Params[2] + "," + Params[3] + "," + Params[4]
										+ "," );

								Log.i(TAG, "任务数据是:" + Params);

								String sql = " declare @res int declare @message nvarchar(max) declare @exception nvarchar(100)   exec @res=Txn_InBoxZM_SI    "
									+" @I_OrBitUserId='"+useid+"',@I_OrBitUserName='"+usename+"',@I_ResourceId='"+resourceid+"',@I_ResourceName='"+resourcename+"',@I_PlugInCommand='ZMIBX', "
									+ "@SampleInspectionSN ='"+oqcdan+"',"
									+ " @I_ReturnMessage=@message  out, @I_ExceptionFieldName=@exception out select @res as I_ReturnValue,@message as I_ReturnMessage,@exception as I_ExceptionFieldName";
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

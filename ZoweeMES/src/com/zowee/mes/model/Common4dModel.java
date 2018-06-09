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

public class Common4dModel extends CommonModel {
	protected static final String TAG = "Common4dModel";
	//*���MES��������ר��**/
	//deng ͨ����ҵ������ ��ȡ��������list<Hashmap<String,string>>
	public void getLine(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					if (null == task.getTaskData())
						return task;

					Soap soap = MesWebService.getMesEmptySoap();
					String sql = "SELECT WorkcenterId,WorkcenterName,WorkcenterDescription FROM dbo.Workcenter  WITH(NOLOCK) where WorkcenterName like 'ZHUODA%'  ";
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
					List<HashMap<String, String>> result = MesWebService
							.getResMapsLis(resDataSet);
					// getResMapsLis ֱ�ӽ����쳣ץȡ
					if (null == resDataSet) {

						// result.put("Error", "����MESʧ��");
						Log.i(TAG, "������ִ��2a");
					} else {
						Log.i(TAG, "������ִ��2");

						task.setTaskResult(result);
						Log.i(TAG, "result=" + result);

					}
				} catch (Exception e) {
					Log.i(TAG, "������ִ��3");
					MesUtils.netConnFail(task.getActivity());
				}
				return task;

			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	//ͨ����Դ����ȡ��ԴID
	public void getResourceid(Task task) {

		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					String ResourceName = "";
					ResourceName = (String) task.getTaskData();
					Soap soap = MesWebService.getMesEmptySoap();
					soap.addWsProperty("SQLString",
							"select ResourceId from Resource where ResourceName='"
									+ ResourceName + "'");
					SoapSerializationEnvelope envolop = MesWebService
							.getWSReqRes(soap);
					if (null == envolop || null == envolop.bodyIn)
						return task;
					List<String> resDataSet = MesWebService.WsDataParser
							.getResDatSet(envolop.bodyIn.toString());
					List<HashMap<String, String>> resMapsLis = MesWebService
							.getResMapsLis(resDataSet);
					Log.i(TAG, "��ȡ��Դid�����" + resMapsLis);

					if (null != resMapsLis && 0 != resMapsLis.size()) {
						List<HashMap<String, String>> parasMapsLis = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> tempMap = null;
						for (int i = 0; i < resMapsLis.size(); i++) {
							tempMap = resMapsLis.get(i);
							if (tempMap.containsKey("ResourceId")) {
								parasMapsLis.add(tempMap);
							}

						}
						task.setTaskResult(tempMap);
					}
				}

				catch (Exception e) {
					Log.i(TAG,"��ȡ��Դid�쳣"+ e.toString());
					MesUtils.netConnFail(task.getActivity());
				}

				return task;
			}
		};

		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}
	
	//ͨ�����κŵ���xxxxandroid���Ĵ洢���̻�ȡ������������id����Ʒ�Ϻš���������
	/*******************************������չ����*********************************************/
		public void getMObylotsn(Task task) {

			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					try {
						
						String lotsn=(String) task.getTaskData();
						Soap soap = MesWebService.getMesEmptySoap();
						soap.addWsProperty("SQLString",
								"exec  MOName_ViewList_Android  @LotSN='"+lotsn+"'");
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.i(TAG,"envolop.badyin=="+envolop.bodyIn.toString());
						List<String> resDataSet = MesWebService.WsDataParser
								.getResDatSet(envolop.bodyIn.toString());
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);

						if (null != resMapsLis && 0 != resMapsLis.size()) {
							List<HashMap<String, String>> parasMapsLis = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> tempMap = null;
							for (int i = 0; i < resMapsLis.size(); i++) {
								tempMap = resMapsLis.get(i);
								if (tempMap.containsKey("ResourceId")) {
									parasMapsLis.add(tempMap);
								}

							}
							task.setTaskResult(tempMap);
						}
					}

					catch (Exception e) {
						Log.i(TAG, "�쳣��"+e.toString());
						MesUtils.netConnFail(task.getActivity());
					}

					return task;
				}
			};

			task.setTaskOperator(taskOperator);
			BackgroundService.addTask(task);
		}
		
		
		
		//ͨ�����κŵ���xxxxandroid���Ĵ洢���̻�ȡ������������id����Ʒ�Ϻš���������
		/*******************************������չ����*********************************************/
			public void getMObylotsnxm(Task task) {

				TaskOperator taskOperator = new TaskOperator() {
					@Override
					public Task doTask(Task task) {
						try {
							
							String lotsn=(String) task.getTaskData();
							Soap soap = MesWebService.getMesEmptySoap();
							soap.addWsProperty("SQLString",
	"SELECT MO.MOId,MOName FROM dbo.Lot WITH(NOLOCK) INNER JOIN dbo.MO ON MO.SOId = Lot.MOID WHERE LotSN='"+lotsn+"'");
							SoapSerializationEnvelope envolop = MesWebService
									.getWSReqRes(soap);
							if (null == envolop || null == envolop.bodyIn)
								return task;
							Log.i(TAG,"envolop.badyin=="+envolop.bodyIn.toString());
							List<String> resDataSet = MesWebService.WsDataParser
									.getResDatSet(envolop.bodyIn.toString());
							List<HashMap<String, String>> resMapsLis = MesWebService
									.getResMapsLis(resDataSet);
							Log.i(TAG, "" + resMapsLis);

							if (null != resMapsLis && 0 != resMapsLis.size()) {
								List<HashMap<String, String>> parasMapsLis = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> tempMap = null;
								for (int i = 0; i < resMapsLis.size(); i++) {
									tempMap = resMapsLis.get(i);
									if (tempMap.containsKey("ResourceId")) {
										parasMapsLis.add(tempMap);
									}

								}
								task.setTaskResult(tempMap);
							}
						}

						catch (Exception e) {
							Log.i(TAG, "�쳣��"+e.toString());
							MesUtils.netConnFail(task.getActivity());
						}

						return task;
					}
				};

				task.setTaskOperator(taskOperator);
				BackgroundService.addTask(task);
			}
		
		
			
			//ͨ�����κŵ���xxxxandroid���Ĵ洢���̻�ȡ������������id����Ʒ�Ϻš���������
			/*******************************������չ����*********************************************/
				public void getMObylotsnxm2(Task task) {

					TaskOperator taskOperator = new TaskOperator() {
						@Override
						public Task doTask(Task task) {
							try {
								
								String lotsn=(String) task.getTaskData();
								Soap soap = MesWebService.getMesEmptySoap();
								soap.addWsProperty("SQLString",
"SELECT MOB.MOId,MOB.MOName FROM dbo.Lot WITH(NOLOCK) INNER JOIN dbo.MO AS MOA WITH(NOLOCK)  ON MOA.MOId = dbo.Lot.MOId INNER JOIN dbo.MO AS MOB WITH(NOLOCK)  ON MOA.SOId=MOB.MOId WHERE LotSN='"+lotsn+"'");
								SoapSerializationEnvelope envolop = MesWebService
										.getWSReqRes(soap);
								if (null == envolop || null == envolop.bodyIn)
									return task;
								Log.i(TAG,"envolop.badyin=="+envolop.bodyIn.toString());
								List<String> resDataSet = MesWebService.WsDataParser
										.getResDatSet(envolop.bodyIn.toString());
								List<HashMap<String, String>> resMapsLis = MesWebService
										.getResMapsLis(resDataSet);
								Log.i(TAG, "" + resMapsLis);

								if (null != resMapsLis && 0 != resMapsLis.size()) {
									List<HashMap<String, String>> parasMapsLis = new ArrayList<HashMap<String, String>>();
									HashMap<String, String> tempMap = null;
									for (int i = 0; i < resMapsLis.size(); i++) {
										tempMap = resMapsLis.get(i);
										if (tempMap.containsKey("ResourceId")) {
											parasMapsLis.add(tempMap);
										}

									}
									task.setTaskResult(tempMap);
								}
							}

							catch (Exception e) {
								Log.i(TAG, "�쳣��"+e.toString());
								MesUtils.netConnFail(task.getActivity());
							}

							return task;
						}
					};

					task.setTaskOperator(taskOperator);
					BackgroundService.addTask(task);
				}
			
			
		
	
		/*******************************�޸� ���ݴ��������ֻҪ��lot����ڼ���ȡ������Ϣ*********************************************/
		
		public void getMObysn(Task task) {

			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					try {
						
						String lotsn=(String) task.getTaskData();
						Soap soap = MesWebService.getMesEmptySoap();
						soap.addWsProperty("SQLString",
								"exec  MoInformation_ForAndroid  @LotSn='"+lotsn+"'");
						SoapSerializationEnvelope envolop = MesWebService
								.getWSReqRes(soap);
						if (null == envolop || null == envolop.bodyIn)
							return task;
						Log.i(TAG,"envolop.badyin=="+envolop.bodyIn.toString());
						List<String> resDataSet = MesWebService.WsDataParser
								.getResDatSet(envolop.bodyIn.toString());
						List<HashMap<String, String>> resMapsLis = MesWebService
								.getResMapsLis(resDataSet);
						Log.i(TAG, "" + resMapsLis);

						if (null != resMapsLis && 0 != resMapsLis.size()) {
							List<HashMap<String, String>> parasMapsLis = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> tempMap = null;
							for (int i = 0; i < resMapsLis.size(); i++) {
								tempMap = resMapsLis.get(i);
								if (tempMap.containsKey("ResourceId")) {
									parasMapsLis.add(tempMap);
								}

							}
							task.setTaskResult(tempMap);
						}
					}

					catch (Exception e) {
						Log.i(TAG, "�쳣��"+e.toString());
						MesUtils.netConnFail(task.getActivity());
					}

					return task;
				}
			};

			task.setTaskOperator(taskOperator);
			BackgroundService.addTask(task);
		}
}

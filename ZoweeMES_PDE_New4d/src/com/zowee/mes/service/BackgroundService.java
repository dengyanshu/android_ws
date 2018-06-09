package com.zowee.mes.service;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.interfaces.CommonActivityInterface;

/**
 * @author Administrator
 * @description һ����̨���� ��Ҫ����һЩ���� ��ʱ�Ĺ��� 
 */

public class BackgroundService extends Service implements Runnable 
{

	private static final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();//�������� �̰߳�ȫ�� 
	private static boolean isLooping = true;//���������̿�������
	private static final String TAG = "BackgroundService";
//	private static Thread runningThread;
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		
		return null;
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
	    new Thread(this).start();
		//runningThread.start();
	}
	
	
	/*
	 *���߳̽�������BackgroundService�Ĵ���(UI�̴߳���) ����ʵ����   ����UI�߳�ִ�� ��Ҫ�����첽UI������� 
	 */
	private Handler bgHandler = new Handler()
	{
		
		public void handleMessage(android.os.Message msg)
		{
			int what = msg.what;
			Task task = (Task)msg.obj;
			switch(what)
			{
				case TaskType.REFLESHUI://UI�������
					if(null!=task.getActivity()&& task.getActivity() instanceof CommonActivityInterface)
					{
						CommonActivityInterface commonActivityInterface = (CommonActivityInterface)task.getActivity();
						commonActivityInterface.refresh(task);
					}
					break;
				
			}
			
		};
	};
	
	
	public void run() 
	{
		while(isLooping)
		{
			if(0!=taskQueue.size())
			{
				try 
				{
					Task task = taskQueue.poll();
					doTask(task);//����ִ��
					//Log.i(TAG,"is running");
				}
				catch (Exception e)
				{
				//	e.printStackTrace();
					Log.i(TAG,e.toString());//һ�������߳���ִ�й����г��ִ���  ��ô���¿���һ���߳�
					new Thread(this).start();
				}
			}
			else
			{
				try 
				{
					Thread.sleep(10);//���û������Ļ� �ô����߳�˯��0.O1�� �Խ�Լ��Դ
				} 
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
		}
		
	};
	
	/**
	 * @param task
	 * @description ������ ����������е��ȷ��� 
	 */
	private void doTask(Task task)
	{
		Task taskRes = null;//������������
		//if(null==task) return;//����Ϊ�յĻ� ֱ�ӷ���
		if(null!=task.getTaskOperator())
			  taskRes = task.doTask();
		else
			taskRes = task;
		
		if(null==taskRes) return;//���ؽ��Ϊ�յĻ� ֱ�ӷ���
		if(!taskRes.isObtainMsg)//˵����ֻ��һ������������ִ�� �������UI������и���
			return;
		
		Message msg = bgHandler.obtainMessage();
		msg.what = taskRes.what;
		msg.obj = taskRes;
		bgHandler.sendMessage(msg);//������������װ����Ϣ ���ݸ�UI�̴߳���
		
	}
	
	/**
	 * @author Administrator
	 * @description 
	 */
	public static final class Task implements Serializable
	{
		
		private Activity activity;
		private int what = TaskType.REFLESHUI;//��Ҫ����bgHandler����ı�־ 
		private boolean isObtainMsg = true;//�Ƿ�������װ����Ϣ ���ݸ�UI�̴߳���
		private int taskType;
		
		public void setTaskType(int taskType) 
		{
			this.taskType = taskType;
		}
  
		private Object taskData;//������������
		private boolean isHandle = true;//�Ƿ��о���������� ����ִ�����Ƿ����
		private Object taskResult;//��������
		private TaskOperator taskOperator;

		public Task(Activity activity, int taskType, Object taskData) 
		{
			super();
			this.activity = activity;
			this.taskType = taskType;
			this.taskData = taskData;
		}
		
		
		public TaskOperator getTaskOperator() {
			return taskOperator;
		}


		public boolean isObtainMsg() {
			return isObtainMsg;
		}


		public void setObtainMsg(boolean isObtainMsg) {
			this.isObtainMsg = isObtainMsg;
		}

		public int getWhat() {
			return what;
		}

		public void setWhat(int what) {
			this.what = what;
		}

		public boolean isHandle() {
			return isHandle;
		}

		public void setHandle(boolean isHandle) {
			this.isHandle = isHandle;
		}

		public Object getTaskResult() {
			return taskResult;
		}

		public void setTaskResult(Object taskResult) {
			this.taskResult = taskResult;
		}

		public Activity getActivity() {
			return activity;
		}

		public int getTaskType() {
			return taskType;
		}

		public Object getTaskData() {
			return taskData;
		}

		public void setTaskOperator(TaskOperator taskOperator) {
			this.taskOperator = taskOperator;
		}
		
		public Task doTask()
		{
			if(null!=taskOperator)
				return taskOperator.doTask(this);
			
			this.isHandle = false;
			return this;
		}
		
	}
	
	
	/**
	 * @author Administrator
	 * @description ����ִ���� ����������� 
	 */
	public interface TaskOperator 
	{	
		public Task doTask(Task task);
	}
	
	
	/**
	 * @author Administrator
	 * @description Ϊ���������ṩ�˱�־�� 
	 */
	public static class TaskType
	{
		public static final int REFLESHUI = 0;//ˢ��UI����
		public static final int SCAN_BAR = 1;//ɨ���ά��
		public static final int READ_BAR = 2;//��ȡ��ά��
		public static final int RE_INIT_LASER = 3;//���¿�������ɨ�贮��
		public static final int LASER_INIT_FAIL = 4;//���ڳ�ʼ��ʧ��
		public static final int FIFO_SCAN = 5;//FIFO����ɨ��(��������)
		public static final int BATNUM_SCAN = 6;//������Ϣɨ��
		public static final int INIT_LASER = 7;//��ʼ������ɨ����
		public static final int STORAGE_SCAN = 8;//���ɨ��
		public static final int WEBSERVICEREQ = 9;//WEBSERVICE ����
		public static final int WEBSERVICE_FAIL = 10;//WEBSERIVICE ����ʧ��
		public static final int INIT_SOUNDEFFECT = 11;//��ʼ����Ч 
		public static final int FIFO_SCAN_DATA = 12;//fifoɨ���ȡ��������
		public static final int VIEW_FIFO_RES = 13;//��������ʾfifoɨ��Ľ�����ݼ�
		public static final int GET_GRNNO = 14;//��ȡ�ջ����� �������ɨ��
		public static final int STORAGE_SCAN_NETWORK = 15;//���ɨ��
		public static final int STORAGE_SPLITE_SCAN = 16;//��ֳ���ɨ��
		public static final int SPLIT_STORAGE_WS = 17;//��ֳ���WEBSERVICE����
		public static final int QUANTITY_ADJUST_SCAN= 18;//��������ɨ��
		public static final int DEFAULT = -1;//Ĭ��ֵ
		public static final int MaterialRewindScan = 19;//��������
		public static final int GETLOTSNQTY = 20;//��ȡ��������
		public static final int QUANTITYADJUST = 21;//����������������
		public static final int BATNUM_INFOR = 23;//������Ϣ
		public static final int PRAPARE_OPERATION_SCAN = 24;//����ɨ��
		public static final int PRAPARE_OPERATION = 25;//���ϲ���
		public static final int PRAPARE_OPEARTION_GETSTORAGELOCATION = 26;//���ϲ��� ��ȡ��λ
		public static final int PRAPARE_OPERATION_INIT2 = 27;//���ϲ�����ʼ��2
		public static final int NET_CONNECT_FAIL = 28;//��������ʧ�� ����
		public static final int METERIAL_INVENTORY_SCAN = 29;//�����̵�ɨ��
		public static final int MET_INV_GETPARAMS = 30;//��ȡ�����̵�����Ĳ���
		public static final int METERIAL_INVENTORY_CK = 31;//�����̵� = ���ϼ��
		public static final int METERIAL_INVENTORY_SM = 32;//�����̵� =���ݱ���
		public static final int FeedOnMaterial_SCAN = 33;//SMT���϶���ɨ��
		public static final int FeedOnMaterial_GETMONames = 34;//SMT���϶��ϻ������Ĳ�����Ϣ
		public static final int FeedOnMaterial = 35;//SMT���϶���
		public static final int CHEMATSTATAB_SCAN = 36;//����վ����ɨ��
		public static final int CHEMATSTATAB = 37;//����վ��
		public static final int REMOVEMATERI_SCAN = 38;//ж���Ͼ�ɨ��
		public static final int REMOVEMATERI = 39;//ж���Ͼ�
		public static final int FINISHWAREHOUSECARTOON_SCAN = 40;//��ͨ�����ɨ��
		public static final int FINISHWAREHOUSECARTOON_MONAME = 41;//��ͨ����ֱ��ò�����ȡ
		public static final int FINISHWAREHOUSECARTOON = 42;//��ͨ�����
		public static final int FINISHOUTCARTOON_SCAN = 43;//��ͨ�����ɨ��
		public static final int FINISHOUTCARTOON_SONAMES = 44;//��ͨ����� ��Ҫ������ȡ
		public static final int FINISHOUTCARTOON = 45;//��ͨ�����
		public static final int FINISHDELICARTOON_SCAN = 46;//��ͨ�䷢��ɨ��
		public static final int FINISHDELICARTOON_SONAMES = 47;//��ͨ�䷢�� ������ȡ
		public static final int FINISHDELICARTOON = 48;//��ͨ�䷢
		public static final int FINISHOUTCARD_SCAN = 49;//�������ɨ��
		public static final int FINISHOUTCARD_GETPARAMS = 50;//������� ������ȡ
		public static final int FINISHOUTCARD = 51;//�������
		public static final int FINISHDELICARD_SCAN = 52;//���巢��ɨ��
		public static final int FINISHDELICARD_GETPARAMS = 53;//���巢����ȡ����
		public static final int FINISHDELICARD = 54;//���巢��
		public static final int FINISHWAREHOUSECARD_SCAN = 55;//�������ɨ��
		public static final int FINISHWAREHOUSECARD_GETCOMPLETENAMES = 56;//��ÿ��������깤���� 
		public static final int FINISHWAREHOUSECARD = 57;//�������
		public static final int STORAGE_SPLITE_GETPARAMS = 58;//��ֳ���  ������ȡ
		public static final int FeedOnMaterial_GETSMTMOUNTID = 59;//���SMT���϶��� SMTMOUNTID 
		public static final int FINISHWAREHOUSECARTOON_DOCNOS = 60;//��ÿ�ͨ�������깤���� 
		public static final int FINISHWAREHOUSECARD_MONAME = 61;//��ȡ�������Ĺ������ּ�ID�Լ�������
		public static final int FINISHOUTCARTOON_GETDNNAMES = 62;//��ȡ���ⵥ��
		public static final int FINISHDELICARTOON_GETDNNAMES = 63;//��ȡ��ͨ��������
		public static final int FINISHOUTCARD_GETDNNAMES = 64;//������� ��ȡ��������
		public static final int FINISHDELICARD_GETDNNAMES = 65;//���巢�� ��ȡ��������
		public static final int DOWNCONFIG_ERROR = 66;//�������ó���
		public static final int DOWNFILE_LENGTH = 67;//֪ͨ�ϲ������ ���ص��ļ�����
		public static final int DOWN_PROGRESS = 68;//��ǰ�����ؽ���
		public static final int DOWN_FAIL = 69;//����ʧ��
		public static final int DOWNLOADSTOP_USER = 70;//ͨ���û��������������ص�ֹͣ״̬
		public static final int RECYCLE_RESOURCE_ERROR = 71;//����������Դʧ��
		public static final int DOWNLOAD_FINISHED = 72;//����֪ͨ�û� ���������
		public static final int MESUPDATE_CHECK = 73;//MES-PDA���¼��
		public static final int MES_CHECKUPDATE_FAIL = 74;//MES-PDA������ʧ��
		public static final int MESUPDATE_INSTALL = 75;//MES-PDA���°�װ
		public static final int PREPAREOPERATE_GETMONAMES = 76;//���ϲ�����ȡ����
		public static final int CHEMATSTATAB_GETSLOTCOUNT = 77;//����վ�� ��ȡվλ��������
		public static final int SMT_FirstScan_WOInfo = 78;//��SMT ������Ϣ
		public static final int SMT_FirstScan = 79;  // SMT ����ɨ�衣
		public static final int SMT_FirstCancleScan = 80;// SMT ����ɨ������ɨ�衣 
		public static final int SMT_LadeToCar = 81;    // SMT װ����
		public static final int SMT_LadeStorage = 82;    // SMT ��⡣
		public static final int SMT_SC = 83; //��Ƭɨ��
		public static final int SMT_ScanSnGetWO = 84; //ͨ��SN��ȡ����
		public static final int MaterialRewindPO = 85; //���������ջ�����
		public static final int MaterialRewindPOInfo = 86;  //ͨ�����������ȡ�ɹ����š�����
		public static final int MaterialRewindSubmit= 87; //�ύɨ�赽���ϵ�MESϵͳ
		public static final int Dip_materialToline = 88;//DIP����ɨ��
		public static final int GetMoNameByMOSTDType = 89;  //ͨ��MOstdtype��ȡ������
		public static final int GetResourceId =90; // ͨ����Դ���ƻ�ȡ����ID��
		public static final int GetSpecificationId = 91; // ͨ����������ȡ����ID��
		public static final int GetProductionLineName = 92; // ��ȡ�����������ơ�
		public static final int DIPLoadparts_start = 93; // DIP ��������ɨ��
		public static final int Dip_matersignboardScan = 94; //  DIP���Ͽ���ɨ��	
		public static final int Dip_MOSpecificationBom =95;  //��ȡָ�����������DIP��BOM�嵥
		public static final int Dip_GetMaterialList =96;  //��ȡָ�����������DIP������״̬
		public static final int Hw_GetCartonWeightScan =97;   //��ȡ��ΪTMES��������ɨ�� 
		public static final int Hw_GetCartonWeight   =98;   //��ȡ��ΪTMES��������  
		public static final int OQC_PhoneOQCBoxBindScan  =99;   //�ͼ쵥��ɨ��
		public static final int OQC_PhoneOQCBoxBind   = 100;   //�ͼ쵥�� 
		public static final int SMT_GetDeviceTypeId   = 101;   //�ͼ쵥�� 
		public static final int PalletOutRoomScan_phone = 102;// //�ֻ�ջ�����ɨ��
		public static final int PalletOutRoom_phone = 103;// //�ֻ�ջ����� 
		public static final int StorageScangetStockCode = 104;// //ɨ�����
		public static final int  MaterialReceiveScan = 105;   //   ����ɨ��
		public static final int GetMOInfo = 106; //��ȡ������Ϣ
		public static final int  MaterialReceiveScanSubmit = 107; //����ɨ��
		
		/********************************deng xiugai **********************************************/
		public static final int FeedOnMaterial_GETMONames2 = 108;//SMT���϶��ϻ�ȡ�������ɸѡ�ؼ���
		public static final int imeicartoonboxcheck = 109;//imei������˶�
		public static final int imeicartoonboxcheckgetimeilist = 110;//imei������˶�
		public static final int imeicartoonboxcheckscan = 111;//ɨ������
		
		
		
		public static final int equipmentmaintenancescan = 112;//ɨ����
		public static final int equipmentmaintenancegetinformation= 113;//��ȡ�豸ά����ʷ��Ϣ
		public static final int equipmentmaintenance= 114;//��ȡ�豸ά����ʷ��Ϣ
		public static final int maintenanceselectgetdatas= 115;//��ȡ�豸ά����ʷ��Ϣ
		
		
		public static final int smtipqcscan= 116;//smtipqc����ɨ��ͷ����
		public static final int smtipqcgetmoname= 117;//smtipqc����ɨ��ͷ����
		public static final int smtipqcpcba= 118;//smtipqc����ɨ��ͷ����
		public static final int smtipqccar= 119;//smtipqc����ɨ��ͷ����
		public static final int smtipqgetcar= 120;//smtipqc����ɨ��ͷ����
		
		
		public static final int smtfeedgetslot= 121;//smt���϶���ͨ��MZ�����ȡ��λ���
		
		public static final int tjchuhuoscan= 122;//tj����ɨ���ȡ��������
		public static final int tjchuhuoscangetdingdan= 123;//tj����ɨ�� ��ȡ������Ϣ
		public static final int tjchuhuoscangetchuhuodan= 124;//tj����ɨ�� ��ȡ��������Ϣ
		public static final int tjchuhuoscanchuhuoscan= 125;//tj����ɨ�� ��ȡ��������Ϣ
		
		
		public static final int instrumentscan= 126;//
		public static final int instrumentgetworkcenter= 127;//
		public static final int instrumentlinesetup= 128;//
		public static final int instrumentremovebinding= 129;//
		public static final int instrumentgetmo= 130;//
		public static final int instrumentmolinkworkcenter= 131;//
		public static final int instrumentgetworkcenterlist= 132;//
		public static final int instrumentgetqueryresult= 133;//
		
		
		public static final int tj_chuhuoyy_checkzhanban= 134;//
		
		
		
		public static final int smtreceivematerialscan= 135;//
		public static final int smtreceivematerialgetworkcenter= 136;//
		public static final int smtreceivematerialgetworkcenterid= 137;//
		public static final int smtreceivematerialgetmzs= 138;//
		public static final int smtreceivematerialsubmit= 139;//
		public static final int smtreceivematerialcheck= 140;//
		
		
		public static final int smtchaolinggetmo= 141;//
		
		
		
		/********************************deng xiugai **********************************************/
 	}
	
	/**
	 * @param task
	 * @description ������� 
	 */
	public static void addTask(Task task)
	{
		if(null==task) return;
		
		taskQueue.add(task);
	}
	
	
}

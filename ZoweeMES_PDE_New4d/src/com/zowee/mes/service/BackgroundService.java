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
 * @description 一个后台服务 主要处理一些繁重 耗时的工作 
 */

public class BackgroundService extends Service implements Runnable 
{

	private static final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();//任务容器 线程安全的 
	private static boolean isLooping = true;//任务处理流程控制属性
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
	 *该线程将会随着BackgroundService的创建(UI线程创建) 而被实例化   并被UI线程执行 主要用于异步UI界面更新 
	 */
	private Handler bgHandler = new Handler()
	{
		
		public void handleMessage(android.os.Message msg)
		{
			int what = msg.what;
			Task task = (Task)msg.obj;
			switch(what)
			{
				case TaskType.REFLESHUI://UI界面更新
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
					doTask(task);//任务执行
					//Log.i(TAG,"is running");
				}
				catch (Exception e)
				{
				//	e.printStackTrace();
					Log.i(TAG,e.toString());//一旦任务线程在执行过程中出现错误  那么重新开启一条线程
					new Thread(this).start();
				}
			}
			else
			{
				try 
				{
					Thread.sleep(10);//如果没有任务的话 让处理线程睡眠0.O1秒 以节约资源
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
	 * @description 任务处理 并对任务进行调度分配 
	 */
	private void doTask(Task task)
	{
		Task taskRes = null;//任务处理后的数据
		//if(null==task) return;//任务为空的话 直接返回
		if(null!=task.getTaskOperator())
			  taskRes = task.doTask();
		else
			taskRes = task;
		
		if(null==taskRes) return;//返回结果为空的话 直接返回
		if(!taskRes.isObtainMsg)//说明这只是一个单纯的任务执行 并不会对UI界面进行更新
			return;
		
		Message msg = bgHandler.obtainMessage();
		msg.what = taskRes.what;
		msg.obj = taskRes;
		bgHandler.sendMessage(msg);//把任务处理结果封装成消息 传递给UI线程处理
		
	}
	
	/**
	 * @author Administrator
	 * @description 
	 */
	public static final class Task implements Serializable
	{
		
		private Activity activity;
		private int what = TaskType.REFLESHUI;//主要用于bgHandler任务的标志 
		private boolean isObtainMsg = true;//是否把任务封装成消息 传递给UI线程处理
		private int taskType;
		
		public void setTaskType(int taskType) 
		{
			this.taskType = taskType;
		}
  
		private Object taskData;//任务所需数据
		private boolean isHandle = true;//是否有具体的任务处理 任务执行器是否调用
		private Object taskResult;//任务处理结果
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
	 * @description 任务执行器 具体的任务处理 
	 */
	public interface TaskOperator 
	{	
		public Task doTask(Task task);
	}
	
	
	/**
	 * @author Administrator
	 * @description 为任务类型提供了标志量 
	 */
	public static class TaskType
	{
		public static final int REFLESHUI = 0;//刷新UI界面
		public static final int SCAN_BAR = 1;//扫描二维码
		public static final int READ_BAR = 2;//读取二维码
		public static final int RE_INIT_LASER = 3;//重新开启激光扫描串口
		public static final int LASER_INIT_FAIL = 4;//串口初始化失败
		public static final int FIFO_SCAN = 5;//FIFO任务扫描(激光条码)
		public static final int BATNUM_SCAN = 6;//批号信息扫描
		public static final int INIT_LASER = 7;//初始化激光扫描器
		public static final int STORAGE_SCAN = 8;//入库扫描
		public static final int WEBSERVICEREQ = 9;//WEBSERVICE 请求
		public static final int WEBSERVICE_FAIL = 10;//WEBSERIVICE 请求失败
		public static final int INIT_SOUNDEFFECT = 11;//初始化音效 
		public static final int FIFO_SCAN_DATA = 12;//fifo扫描获取数据任务
		public static final int VIEW_FIFO_RES = 13;//解析并显示fifo扫描的结果数据集
		public static final int GET_GRNNO = 14;//获取收货单号 用于入库扫描
		public static final int STORAGE_SCAN_NETWORK = 15;//入库扫描
		public static final int STORAGE_SPLITE_SCAN = 16;//拆分出库扫描
		public static final int SPLIT_STORAGE_WS = 17;//拆分出库WEBSERVICE请求
		public static final int QUANTITY_ADJUST_SCAN= 18;//数量调整扫描
		public static final int DEFAULT = -1;//默认值
		public static final int MaterialRewindScan = 19;//物料收料
		public static final int GETLOTSNQTY = 20;//获取批号数量
		public static final int QUANTITYADJUST = 21;//物料批号数量调整
		public static final int BATNUM_INFOR = 23;//批号信息
		public static final int PRAPARE_OPERATION_SCAN = 24;//备料扫描
		public static final int PRAPARE_OPERATION = 25;//备料操作
		public static final int PRAPARE_OPEARTION_GETSTORAGELOCATION = 26;//备料操作 获取库位
		public static final int PRAPARE_OPERATION_INIT2 = 27;//备料操作初始化2
		public static final int NET_CONNECT_FAIL = 28;//网络连接失败 提醒
		public static final int METERIAL_INVENTORY_SCAN = 29;//物料盘点扫描
		public static final int MET_INV_GETPARAMS = 30;//获取物料盘点所需的参数
		public static final int METERIAL_INVENTORY_CK = 31;//物料盘点 = 物料检查
		public static final int METERIAL_INVENTORY_SM = 32;//物料盘点 =数据保存
		public static final int FeedOnMaterial_SCAN = 33;//SMT上料对料扫描
		public static final int FeedOnMaterial_GETMONames = 34;//SMT上料对料获得所需的参数信息
		public static final int FeedOnMaterial = 35;//SMT上料对料
		public static final int CHEMATSTATAB_SCAN = 36;//查料站表激光扫描
		public static final int CHEMATSTATAB = 37;//查料站表
		public static final int REMOVEMATERI_SCAN = 38;//卸换料卷扫描
		public static final int REMOVEMATERI = 39;//卸换料卷
		public static final int FINISHWAREHOUSECARTOON_SCAN = 40;//卡通箱入仓扫描
		public static final int FINISHWAREHOUSECARTOON_MONAME = 41;//卡通箱入仓必用参数获取
		public static final int FINISHWAREHOUSECARTOON = 42;//卡通箱入仓
		public static final int FINISHOUTCARTOON_SCAN = 43;//卡通箱出库扫描
		public static final int FINISHOUTCARTOON_SONAMES = 44;//卡通箱出库 必要参数获取
		public static final int FINISHOUTCARTOON = 45;//卡通箱出库
		public static final int FINISHDELICARTOON_SCAN = 46;//卡通箱发货扫描
		public static final int FINISHDELICARTOON_SONAMES = 47;//卡通箱发货 参数获取
		public static final int FINISHDELICARTOON = 48;//卡通箱发
		public static final int FINISHOUTCARD_SCAN = 49;//卡板出库扫描
		public static final int FINISHOUTCARD_GETPARAMS = 50;//卡板出库 参数获取
		public static final int FINISHOUTCARD = 51;//卡板出库
		public static final int FINISHDELICARD_SCAN = 52;//卡板发货扫描
		public static final int FINISHDELICARD_GETPARAMS = 53;//卡板发货获取参数
		public static final int FINISHDELICARD = 54;//卡板发货
		public static final int FINISHWAREHOUSECARD_SCAN = 55;//卡板入库扫描
		public static final int FINISHWAREHOUSECARD_GETCOMPLETENAMES = 56;//获得卡板入库的完工单号 
		public static final int FINISHWAREHOUSECARD = 57;//卡板入库
		public static final int STORAGE_SPLITE_GETPARAMS = 58;//拆分出库  参数获取
		public static final int FeedOnMaterial_GETSMTMOUNTID = 59;//获得SMT上料对料 SMTMOUNTID 
		public static final int FINISHWAREHOUSECARTOON_DOCNOS = 60;//获得卡通箱入库的完工单号 
		public static final int FINISHWAREHOUSECARD_MONAME = 61;//获取卡板入库的工单名字及ID以及订单名
		public static final int FINISHOUTCARTOON_GETDNNAMES = 62;//获取出库单号
		public static final int FINISHDELICARTOON_GETDNNAMES = 63;//获取卡通发货单号
		public static final int FINISHOUTCARD_GETDNNAMES = 64;//卡板出库 获取出货单号
		public static final int FINISHDELICARD_GETDNNAMES = 65;//卡板发货 获取出货单号
		public static final int DOWNCONFIG_ERROR = 66;//下载配置出错
		public static final int DOWNFILE_LENGTH = 67;//通知上层调用者 下载的文件长度
		public static final int DOWN_PROGRESS = 68;//当前的下载进度
		public static final int DOWN_FAIL = 69;//下载失败
		public static final int DOWNLOADSTOP_USER = 70;//通过用户交互而导致下载的停止状态
		public static final int RECYCLE_RESOURCE_ERROR = 71;//清理下载资源失败
		public static final int DOWNLOAD_FINISHED = 72;//用来通知用户 下载已完成
		public static final int MESUPDATE_CHECK = 73;//MES-PDA更新检查
		public static final int MES_CHECKUPDATE_FAIL = 74;//MES-PDA检查更新失败
		public static final int MESUPDATE_INSTALL = 75;//MES-PDA更新安装
		public static final int PREPAREOPERATE_GETMONAMES = 76;//备料操作获取工单
		public static final int CHEMATSTATAB_GETSLOTCOUNT = 77;//查料站表 获取站位条码总数
		public static final int SMT_FirstScan_WOInfo = 78;//查SMT 订单信息
		public static final int SMT_FirstScan = 79;  // SMT 上料扫描。
		public static final int SMT_FirstCancleScan = 80;// SMT 上料扫描重置扫描。 
		public static final int SMT_LadeToCar = 81;    // SMT 装车。
		public static final int SMT_LadeStorage = 82;    // SMT 入库。
		public static final int SMT_SC = 83; //贴片扫描
		public static final int SMT_ScanSnGetWO = 84; //通过SN获取订单
		public static final int MaterialRewindPO = 85; //产生物料收货单号
		public static final int MaterialRewindPOInfo = 86;  //通过物料条码获取采购单号、数量
		public static final int MaterialRewindSubmit= 87; //提交扫描到物料到MES系统
		public static final int Dip_materialToline = 88;//DIP物料扫描
		public static final int GetMoNameByMOSTDType = 89;  //通过MOstdtype获取工单号
		public static final int GetResourceId =90; // 通过资源名称获取资料ID。
		public static final int GetSpecificationId = 91; // 通过工单名获取资料ID。
		public static final int GetProductionLineName = 92; // 获取生产线体名称。
		public static final int DIPLoadparts_start = 93; // DIP 物料上料扫描
		public static final int Dip_matersignboardScan = 94; //  DIP物料看板扫描	
		public static final int Dip_MOSpecificationBom =95;  //获取指定订单及规程DIP段BOM清单
		public static final int Dip_GetMaterialList =96;  //获取指定订单及规程DIP段物料状态
		public static final int Hw_GetCartonWeightScan =97;   //获取华为TMES外箱重量扫描 
		public static final int Hw_GetCartonWeight   =98;   //获取华为TMES外箱重量  
		public static final int OQC_PhoneOQCBoxBindScan  =99;   //客检单绑定扫描
		public static final int OQC_PhoneOQCBoxBind   = 100;   //客检单绑定 
		public static final int SMT_GetDeviceTypeId   = 101;   //客检单绑定 
		public static final int PalletOutRoomScan_phone = 102;// //手机栈板出货扫描
		public static final int PalletOutRoom_phone = 103;// //手机栈板出货 
		public static final int StorageScangetStockCode = 104;// //扫描入库
		public static final int  MaterialReceiveScan = 105;   //   物料扫描
		public static final int GetMOInfo = 106; //获取订单信息
		public static final int  MaterialReceiveScanSubmit = 107; //物料扫描
		
		/********************************deng xiugai **********************************************/
		public static final int FeedOnMaterial_GETMONames2 = 108;//SMT上料对料获取工单添加筛选关键字
		public static final int imeicartoonboxcheck = 109;//imei与中箱核对
		public static final int imeicartoonboxcheckgetimeilist = 110;//imei与中箱核对
		public static final int imeicartoonboxcheckscan = 111;//扫描任务
		
		
		
		public static final int equipmentmaintenancescan = 112;//扫描器
		public static final int equipmentmaintenancegetinformation= 113;//获取设备维护历史信息
		public static final int equipmentmaintenance= 114;//获取设备维护历史信息
		public static final int maintenanceselectgetdatas= 115;//获取设备维护历史信息
		
		
		public static final int smtipqcscan= 116;//smtipqc程序扫描头任务
		public static final int smtipqcgetmoname= 117;//smtipqc程序扫描头任务
		public static final int smtipqcpcba= 118;//smtipqc程序扫描头任务
		public static final int smtipqccar= 119;//smtipqc程序扫描头任务
		public static final int smtipqgetcar= 120;//smtipqc程序扫描头任务
		
		
		public static final int smtfeedgetslot= 121;//smt上料对料通过MZ条码获取槽位编号
		
		public static final int tjchuhuoscan= 122;//tj激光扫描获取激光数据
		public static final int tjchuhuoscangetdingdan= 123;//tj出货扫描 获取订单信息
		public static final int tjchuhuoscangetchuhuodan= 124;//tj出货扫描 获取出货单信息
		public static final int tjchuhuoscanchuhuoscan= 125;//tj出货扫描 获取出货单信息
		
		
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
	 * @description 任务添加 
	 */
	public static void addTask(Task task)
	{
		if(null==task) return;
		
		taskQueue.add(task);
	}
	
	
}

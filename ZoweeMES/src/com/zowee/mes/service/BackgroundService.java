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

public class BackgroundService extends Service implements Runnable {

	private static final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();// 任务容器
																									// 线程安全的
	private static boolean isLooping = true;// 任务处理流程控制属性
	private static final String TAG = "BackgroundService";

	// private static Thread runningThread;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		new Thread(this).start();
		// runningThread.start();
	}

	/*
	 * 该线程将会随着BackgroundService的创建(UI线程创建) 而被实例化 并被UI线程执行 主要用于异步UI界面更新
	 */
	private Handler bgHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			Task task = (Task) msg.obj;
			switch (what) {
			case TaskType.REFLESHUI:// UI界面更新
				if (null != task.getActivity()
						&& task.getActivity() instanceof CommonActivityInterface) {
					CommonActivityInterface commonActivityInterface = (CommonActivityInterface) task
							.getActivity();
					commonActivityInterface.refresh(task);
				}
				break;

			}

		};
	};

	public void run() {
		while (isLooping) {
			if (0 != taskQueue.size()) {
				try {
					Task task = taskQueue.poll();
					doTask(task);// 任务执行
					// Log.i(TAG,"is running");
				} catch (Exception e) {
					// e.printStackTrace();
					Log.i(TAG, e.toString());// 一旦任务线程在执行过程中出现错误 那么重新开启一条线程
					new Thread(this).start();
				}
			} else {
				try {
					Thread.sleep(10);// 如果没有任务的话 让处理线程睡眠0.O1秒 以节约资源
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	};

	/**
	 * @param task
	 * @description 任务处理 并对任务进行调度分配
	 */
	private void doTask(Task task) {
		Task taskRes = null;// 任务处理后的数据
		// if(null==task) return;//任务为空的话 直接返回
		if (null != task.getTaskOperator())
			taskRes = task.doTask();
		else
			taskRes = task;

		if (null == taskRes)
			return;// 返回结果为空的话 直接返回
		if (!taskRes.isObtainMsg)// 说明这只是一个单纯的任务执行 并不会对UI界面进行更新
			return;

		Message msg = bgHandler.obtainMessage();
		msg.what = taskRes.what;
		msg.obj = taskRes;
		bgHandler.sendMessage(msg);// 把任务处理结果封装成消息 传递给UI线程处理

	}

	/**
	 * @author Administrator
	 * @description
	 */
	public static final class Task implements Serializable {

		private Activity activity;
		private int what = TaskType.REFLESHUI;// 主要用于bgHandler任务的标志
		private boolean isObtainMsg = true;// 是否把任务封装成消息 传递给UI线程处理
		private int taskType;

		public void setTaskType(int taskType) {
			this.taskType = taskType;
		}

		private Object taskData;// 任务所需数据
		private boolean isHandle = true;// 是否有具体的任务处理 任务执行器是否调用
		private Object taskResult;// 任务处理结果
		private TaskOperator taskOperator;

		public Task(Activity activity, int taskType, Object taskData) {
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

		public Task doTask() {
			if (null != taskOperator)
				return taskOperator.doTask(this);

			this.isHandle = false;
			return this;
		}

	}

	/**
	 * @author Administrator
	 * @description 任务执行器 具体的任务处理
	 */
	public interface TaskOperator {
		public Task doTask(Task task);
	}

	/**
	 * @author Administrator
	 * @description 为任务类型提供了标志量
	 */
	public static class TaskType {
		public static final int REFLESHUI = 0;// 刷新UI界面
		public static final int SCAN_BAR = 1;// 扫描二维码
		public static final int READ_BAR = 2;// 读取二维码
		public static final int RE_INIT_LASER = 3;// 重新开启激光扫描串口
		public static final int LASER_INIT_FAIL = 4;// 串口初始化失败
		public static final int FIFO_SCAN = 5;// FIFO任务扫描(激光条码)
		public static final int BATNUM_SCAN = 6;// 批号信息扫描
		public static final int INIT_LASER = 7;// 初始化激光扫描器
		public static final int STORAGE_SCAN = 8;// 入库扫描
		public static final int WEBSERVICEREQ = 9;// WEBSERVICE 请求
		public static final int WEBSERVICE_FAIL = 10;// WEBSERIVICE 请求失败
		public static final int INIT_SOUNDEFFECT = 11;// 初始化音效
		public static final int FIFO_SCAN_DATA = 12;// fifo扫描获取数据任务
		public static final int VIEW_FIFO_RES = 13;// 解析并显示fifo扫描的结果数据集
		public static final int GET_GRNNO = 14;// 获取收货单号 用于入库扫描
		public static final int STORAGE_SCAN_NETWORK = 15;// 入库扫描
		public static final int STORAGE_SPLITE_SCAN = 16;// 拆分出库扫描
		public static final int SPLIT_STORAGE_WS = 17;// 拆分出库WEBSERVICE请求
		public static final int QUANTITY_ADJUST_SCAN = 18;// 数量调整扫描
		public static final int DEFAULT = -1;// 默认值
		public static final int MaterialRewind = 19;// 物料收料
		public static final int GETLOTSNQTY = 20;// 获取批号数量
		public static final int QUANTITYADJUST = 21;// 物料批号数量调整
		public static final int BATNUM_INFOR = 23;// 批号信息
		public static final int PRAPARE_OPERATION_SCAN = 24;// 备料扫描
		public static final int PRAPARE_OPERATION = 25;// 备料操作
		public static final int PRAPARE_OPEARTION_GETSTORAGELOCATION = 26;// 备料操作
																			// 获取库位
		public static final int PRAPARE_OPERATION_INIT2 = 27;// 备料操作初始化2
		public static final int NET_CONNECT_FAIL = 28;// 网络连接失败 提醒
		public static final int METERIAL_INVENTORY_SCAN = 29;// 物料盘点扫描
		public static final int MET_INV_GETPARAMS = 30;// 获取物料盘点所需的参数
		public static final int METERIAL_INVENTORY_CK = 31;// 物料盘点 = 物料检查
		public static final int METERIAL_INVENTORY_SM = 32;// 物料盘点 =数据保存
		public static final int FeedOnMaterial_SCAN = 33;// SMT上料对料扫描
		public static final int FeedOnMaterial_GETMONames = 34;// SMT上料对料获得所需的参数信息
		public static final int FeedOnMaterial = 35;// SMT上料对料
		public static final int CHEMATSTATAB_SCAN = 36;// 查料站表激光扫描
		public static final int CHEMATSTATAB = 37;// 查料站表
		public static final int REMOVEMATERI_SCAN = 38;// 卸换料卷扫描
		public static final int REMOVEMATERI = 39;// 卸换料卷
		public static final int FINISHWAREHOUSECARTOON_SCAN = 40;// 卡通箱入仓扫描
		public static final int FINISHWAREHOUSECARTOON_MONAME = 41;// 卡通箱入仓必用参数获取
		public static final int FINISHWAREHOUSECARTOON = 42;// 卡通箱入仓
		public static final int FINISHOUTCARTOON_SCAN = 43;// 卡通箱出库扫描
		public static final int FINISHOUTCARTOON_SONAMES = 44;// 卡通箱出库 必要参数获取
		public static final int FINISHOUTCARTOON = 45;// 卡通箱出库
		public static final int FINISHDELICARTOON_SCAN = 46;// 卡通箱发货扫描
		public static final int FINISHDELICARTOON_SONAMES = 47;// 卡通箱发货 参数获取
		public static final int FINISHDELICARTOON = 48;// 卡通箱发
		public static final int FINISHOUTCARD_SCAN = 49;// 卡板出库扫描
		public static final int FINISHOUTCARD_GETPARAMS = 50;// 卡板出库 参数获取
		public static final int FINISHOUTCARD = 51;// 卡板出库
		public static final int FINISHDELICARD_SCAN = 52;// 卡板发货扫描
		public static final int FINISHDELICARD_GETPARAMS = 53;// 卡板发货获取参数
		public static final int FINISHDELICARD = 54;// 卡板发货
		public static final int FINISHWAREHOUSECARD_SCAN = 55;// 卡板入库扫描
		public static final int FINISHWAREHOUSECARD_GETCOMPLETENAMES = 56;// 获得卡板入库的完工单号
		public static final int FINISHWAREHOUSECARD = 57;// 卡板入库
		public static final int STORAGE_SPLITE_GETPARAMS = 58;// 拆分出库 参数获取
		public static final int FeedOnMaterial_GETSMTMOUNTID = 59;// 获得SMT上料对料
																	// SMTMOUNTID
		public static final int FINISHWAREHOUSECARTOON_DOCNOS = 60;// 获得卡通箱入库的完工单号
		public static final int FINISHWAREHOUSECARD_MONAME = 61;// 获取卡板入库的工单名字及ID以及订单名
		public static final int FINISHOUTCARTOON_GETDNNAMES = 62;// 获取出库单号
		public static final int FINISHDELICARTOON_GETDNNAMES = 63;// 获取卡通发货单号
		public static final int FINISHOUTCARD_GETDNNAMES = 64;// 卡板出库 获取出货单号
		public static final int FINISHDELICARD_GETDNNAMES = 65;// 卡板发货 获取出货单号
		public static final int DOWNCONFIG_ERROR = 66;// 下载配置出错
		public static final int DOWNFILE_LENGTH = 67;// 通知上层调用者 下载的文件长度
		public static final int DOWN_PROGRESS = 68;// 当前的下载进度
		public static final int DOWN_FAIL = 69;// 下载失败
		public static final int DOWNLOADSTOP_USER = 70;// 通过用户交互而导致下载的停止状态
		public static final int RECYCLE_RESOURCE_ERROR = 71;// 清理下载资源失败
		public static final int DOWNLOAD_FINISHED = 72;// 用来通知用户 下载已完成
		public static final int MESUPDATE_CHECK = 73;// MES-PDA更新检查
		public static final int MES_CHECKUPDATE_FAIL = 74;// MES-PDA检查更新失败
		public static final int MESUPDATE_INSTALL = 75;// MES-PDA更新安装
		public static final int PREPAREOPERATE_GETMONAMES = 76;// 备料操作获取工单
		public static final int CHEMATSTATAB_GETSLOTCOUNT = 77;// 查料站表 获取站位条码总数
		public static final int SMT_FirstScan_WOInfo = 78;// 查SMT 订单信息
		public static final int SMT_FirstScan = 79; // SMT 上料扫描。
		public static final int SMT_FirstCancleScan = 80;// SMT 上料扫描重置扫描。
		public static final int SMT_LadeToCar = 81; // SMT 装车。
		public static final int SMT_LadeStorage = 82; // SMT 入库。
		public static final int SMT_SC = 83; // 贴片扫描
		public static final int SMT_ScanSnGetWO = 84; // 通过SN获取订单
		public static final int DIP_GetMoinfo = 85; // 通过DIP段工单信息获取该工单信息。
		public static final int GetMoNameByMOSTDType = 86; // 获取DIP段所有工单号
		public static final int Dip_StartDIP = 87; // DIP 启动
		public static final int GetDipSNType = 88; // DIP段SN类型
		public static final int GetResourceId = 89; // 获取资源ID
		public static final int Dip_mobileStart = 90; // 手机SMT转组装
		public static final int package_eyecheckGetMOinfo = 91; // TJ 目检
		public static final int package_eyecheckscan = 92; // TJ 目检
		public static final int package_OQCcheckGetMOinfo = 93; // TJ OQC
		public static final int package_OQCSampling_CheckBoxSN = 94;
		public static final int package_OQCSampling_LotSN = 95;
		public static final int package_OQCSampling_PassFail = 96;
		public static final int package_TjBoxLotSNCheck = 97; // 外箱与SN一致性检查
		public static final int SMT_EyeCheck = 98; // SMT 目检
		public static final int SMT_SC_New = 99; // 新贴片扫描
		public static final int SMT_BindPCBSN = 100; // 手动绑定贴片站抛料标签PCB。
		public static final int StorageScangetStockCode = 101;

		/** DENG 后面添加的 */
		public static final int FaceCheck = 200;
		public static final int FaceCheckgetmoid = 201;
		public static final int FaceCheckgeterrorcode = 202;

		public static final int Tino = 203;
		public static final int Tinogetmo = 204;
		public static final int Tinogettino = 205;
		public static final int Tinogetlistview = 206;

		public static final int Bindpcbgetmo = 207;// 通过sun获取工单信息和联板的数量
		public static final int Bindpcb = 208;// 执行绑定
		public static final int Bindpcbsetnum = 209;// 设置系统母板联板数量

		public static final int getline = 210;// 天津紫米获取线体信息
		public static final int Linestorage = 211;// 线体sn存储
		public static final int Lineselectnum = 212;// 获取线体信息

		public static final int linesnstorage = 213;// 天津紫米sn存储
		public static final int linesnselect = 214;// 天津紫米sn查询
		public static final int linegetmo = 218;// 天津紫米sn查询

		public static final int smtfvmigetmo = 215;// 松岗板边条码目前获取工单
		public static final int smtfvmi = 216;// 松岗板边条码目检结果提交

		public static final int sntocar = 217;// 松岗板边条码装车绑定
		public static final int sntocargetmo = 219;// 松岗板边条码装车获取工单 获取联板数量
		public static final int sntocargetcar = 220;// 松岗板边条码校验车号
		public static final int sntocartocar = 221;// 松岗板边条码强制装车
		public static final int sntocarchecksingelsn = 222;// 松岗板边条码校验单个SN
		public static final int sntocargetmakeupcount = 223;// 松岗板边条码校验单个SN
		
		public static final int biaoqianhedui = 224;// 松岗四楼程序标签核对
		public static final int common4dmodelgetmobylotsn = 225;// common4dmodel 的通用任务类型
		public static final int common4dmodelgetresourceid = 226;// common4dmodel 的通用任务类型
		
		public static final int tiebiaozhuandsn = 227;// 松岗四楼贴标转dsn光模块-无
		public static final int xiaojiegouckp = 228;// 松岗四楼小结构CKP
		public static final int anxuzhuangxiang = 229;// 松岗四楼按序装箱
		public static final int anxuzhuangxiangremovebind = 230;// 松岗四楼按序装箱解除绑定
		
		
		public static final int tjzimicaihebangdingcaihesncheck = 231;// 天津彩盒绑定电源SN 彩盒SN 传moid和彩盒SN 进行校验
		public static final int tjzimicaihebangding = 232;// 天津彩盒绑定电源SN 彩盒SN 电源SN 进行绑定
		
		public static final int tjzimicaihezhuangxianggetmo = 233;// 天津彩盒装箱 获取工单信息 包括装箱数量 送检数量
		public static final int tjzimicaihezhuangxiangzhuangxiang = 234;// 天津彩盒装箱 获取工单信息 包括装箱数量 送检数量
		public static final int tjzimicaihezhuangxiangweishuzhuangxiang = 238;// 天津彩盒装箱 获取工单信息 包括装箱数量 送检数量
		public static final int tjzimicaihezhuangxiangqingkongzhuangxiang = 239;// 天津彩盒装箱 获取工单信息 包括装箱数量 送检数量
		public static final int tjzimicaihezhuangxianggetoqcbyxianghao = 240;// 天津彩盒装箱 获取工单信息 包括装箱数量 送检数量
		public static final int tjzimicaihezhuangxiangshoudongshoujian = 241;// 天津彩盒装箱 获取工单信息 包括装箱数量 送检数量
		
		
		
		
		public static final int tjzimidianyuantourugetmo = 235;// 天津紫米电源投入获取工单通过moname
		public static final int tjzimidianyuantouru = 236;// 天津紫米电源投入
		
		public static final int tjzimiweixiu = 237;// 天津紫米电源维修
		
		
		public static final int liulouassyqidonggetmo = 242;// 松岗6楼获取工单数据源 listmaps
		public static final int liulouassyqidong = 243;// 松岗6楼条码启动
		public static final int liulouassyqidonggetmobymoname = 250;// 松岗6楼条码启动
		
		public static final int liuloubujianbangding = 244;// 松岗6楼组装部件绑定
		
		public static final int liuloufvmi = 245;// 松岗6楼开机测试目检
		
		public static final int liuloulaohua = 246;// 松岗6楼老化
		
		public static final int haierbangdingcailiao = 247;// 松岗6楼海尔平板原材料绑定
		public static final int haierremovebangdingcailiao = 248;// 松岗6楼海尔平板
		
		public static final int haierzhengjisnbangding = 249;// 松岗6楼海尔平板
		
		public static final int lenovotocar = 251;// smt联想装车绑定芯片
		public static final int lenovofvmi = 252;// smt联想目检扣料
		public static final int lenovotocartoclosecar = 253;// smt联想目检扣料
		
		public static final int m8000cpgetpath = 254;// smt联想目检扣料
		
		public static final int common4dmodelgetmobysn = 255;// 后续查询工单信息 不在moitem表
		
		public static final int tjcommonpassstationgetsatations = 256;//天津普通过站 获取数据库的工序表
		public static final int tjcommonpassstation = 257;// 
		
		public static final int tjdipstartgetmoinformation = 258;// 天津dip启动获取工单信息
		public static final int tjdipstart = 259;// 天津dip启动
		
		
		public static final int tjbadcount = 260;// 天津不良品计数
		public static final int tjbadcountgetmo = 261;// 天津不良品计数
		public static final int tjbadcountgetline = 262;// 天津不良品计数
		
		
		public static final int smttiepianrengong = 263;// SMT贴片
		public static final int smttiepianupload = 264;// SMT贴片
		public static final int smttiepiandownload = 265;// SMT贴片
		
		public static final int smtladeandbinding = 266;// SMT装车绑定副板条码
		
		public static final int smtprintting = 267;// 锡膏印刷扫描 人工提交！
		
		public static final int smttp_new_upauto = 268;
		public static final int smttp_new_downauto = 269;
		
		
		public static final int smttp_new_banbian = 270;
		
		public static final int shuaka_getworkcenter = 271;
		public static final int shuaka_getmachinnumber= 272;
		public static final int shuaka_getmoname = 273;
		public static final int shuaka_getzhicheng = 274;
		public static final int shuaka_shuaka = 275;
		public static final int shuaka_getrenli = 279;
		
		
		public static final int smttpcommon_getmo = 276;
		public static final int smttpcommon_tp_raila = 277;
		public static final int smttpcommon_tp_railb = 278;
		
		
		
		

		/** DENG 后面添加的 */
	}

	/**
	 * @param task
	 * @description 任务添加
	 */
	public static void addTask(Task task) {
		if (null == task)
			return;

		taskQueue.add(task);
	}

}

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

public class BackgroundService extends Service implements Runnable {

	private static final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<Task>();// ��������
																									// �̰߳�ȫ��
	private static boolean isLooping = true;// ���������̿�������
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
	 * ���߳̽�������BackgroundService�Ĵ���(UI�̴߳���) ����ʵ���� ����UI�߳�ִ�� ��Ҫ�����첽UI�������
	 */
	private Handler bgHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			Task task = (Task) msg.obj;
			switch (what) {
			case TaskType.REFLESHUI:// UI�������
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
					doTask(task);// ����ִ��
					// Log.i(TAG,"is running");
				} catch (Exception e) {
					// e.printStackTrace();
					Log.i(TAG, e.toString());// һ�������߳���ִ�й����г��ִ��� ��ô���¿���һ���߳�
					new Thread(this).start();
				}
			} else {
				try {
					Thread.sleep(10);// ���û������Ļ� �ô����߳�˯��0.O1�� �Խ�Լ��Դ
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	};

	/**
	 * @param task
	 * @description ������ ����������е��ȷ���
	 */
	private void doTask(Task task) {
		Task taskRes = null;// ������������
		// if(null==task) return;//����Ϊ�յĻ� ֱ�ӷ���
		if (null != task.getTaskOperator())
			taskRes = task.doTask();
		else
			taskRes = task;

		if (null == taskRes)
			return;// ���ؽ��Ϊ�յĻ� ֱ�ӷ���
		if (!taskRes.isObtainMsg)// ˵����ֻ��һ������������ִ�� �������UI������и���
			return;

		Message msg = bgHandler.obtainMessage();
		msg.what = taskRes.what;
		msg.obj = taskRes;
		bgHandler.sendMessage(msg);// ������������װ����Ϣ ���ݸ�UI�̴߳���

	}

	/**
	 * @author Administrator
	 * @description
	 */
	public static final class Task implements Serializable {

		private Activity activity;
		private int what = TaskType.REFLESHUI;// ��Ҫ����bgHandler����ı�־
		private boolean isObtainMsg = true;// �Ƿ�������װ����Ϣ ���ݸ�UI�̴߳���
		private int taskType;

		public void setTaskType(int taskType) {
			this.taskType = taskType;
		}

		private Object taskData;// ������������
		private boolean isHandle = true;// �Ƿ��о���������� ����ִ�����Ƿ����
		private Object taskResult;// ��������
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
	 * @description ����ִ���� �����������
	 */
	public interface TaskOperator {
		public Task doTask(Task task);
	}

	/**
	 * @author Administrator
	 * @description Ϊ���������ṩ�˱�־��
	 */
	public static class TaskType {
		public static final int REFLESHUI = 0;// ˢ��UI����
		public static final int SCAN_BAR = 1;// ɨ���ά��
		public static final int READ_BAR = 2;// ��ȡ��ά��
		public static final int RE_INIT_LASER = 3;// ���¿�������ɨ�贮��
		public static final int LASER_INIT_FAIL = 4;// ���ڳ�ʼ��ʧ��
		public static final int FIFO_SCAN = 5;// FIFO����ɨ��(��������)
		public static final int BATNUM_SCAN = 6;// ������Ϣɨ��
		public static final int INIT_LASER = 7;// ��ʼ������ɨ����
		public static final int STORAGE_SCAN = 8;// ���ɨ��
		public static final int WEBSERVICEREQ = 9;// WEBSERVICE ����
		public static final int WEBSERVICE_FAIL = 10;// WEBSERIVICE ����ʧ��
		public static final int INIT_SOUNDEFFECT = 11;// ��ʼ����Ч
		public static final int FIFO_SCAN_DATA = 12;// fifoɨ���ȡ��������
		public static final int VIEW_FIFO_RES = 13;// ��������ʾfifoɨ��Ľ�����ݼ�
		public static final int GET_GRNNO = 14;// ��ȡ�ջ����� �������ɨ��
		public static final int STORAGE_SCAN_NETWORK = 15;// ���ɨ��
		public static final int STORAGE_SPLITE_SCAN = 16;// ��ֳ���ɨ��
		public static final int SPLIT_STORAGE_WS = 17;// ��ֳ���WEBSERVICE����
		public static final int QUANTITY_ADJUST_SCAN = 18;// ��������ɨ��
		public static final int DEFAULT = -1;// Ĭ��ֵ
		public static final int MaterialRewind = 19;// ��������
		public static final int GETLOTSNQTY = 20;// ��ȡ��������
		public static final int QUANTITYADJUST = 21;// ����������������
		public static final int BATNUM_INFOR = 23;// ������Ϣ
		public static final int PRAPARE_OPERATION_SCAN = 24;// ����ɨ��
		public static final int PRAPARE_OPERATION = 25;// ���ϲ���
		public static final int PRAPARE_OPEARTION_GETSTORAGELOCATION = 26;// ���ϲ���
																			// ��ȡ��λ
		public static final int PRAPARE_OPERATION_INIT2 = 27;// ���ϲ�����ʼ��2
		public static final int NET_CONNECT_FAIL = 28;// ��������ʧ�� ����
		public static final int METERIAL_INVENTORY_SCAN = 29;// �����̵�ɨ��
		public static final int MET_INV_GETPARAMS = 30;// ��ȡ�����̵�����Ĳ���
		public static final int METERIAL_INVENTORY_CK = 31;// �����̵� = ���ϼ��
		public static final int METERIAL_INVENTORY_SM = 32;// �����̵� =���ݱ���
		public static final int FeedOnMaterial_SCAN = 33;// SMT���϶���ɨ��
		public static final int FeedOnMaterial_GETMONames = 34;// SMT���϶��ϻ������Ĳ�����Ϣ
		public static final int FeedOnMaterial = 35;// SMT���϶���
		public static final int CHEMATSTATAB_SCAN = 36;// ����վ����ɨ��
		public static final int CHEMATSTATAB = 37;// ����վ��
		public static final int REMOVEMATERI_SCAN = 38;// ж���Ͼ�ɨ��
		public static final int REMOVEMATERI = 39;// ж���Ͼ�
		public static final int FINISHWAREHOUSECARTOON_SCAN = 40;// ��ͨ�����ɨ��
		public static final int FINISHWAREHOUSECARTOON_MONAME = 41;// ��ͨ����ֱ��ò�����ȡ
		public static final int FINISHWAREHOUSECARTOON = 42;// ��ͨ�����
		public static final int FINISHOUTCARTOON_SCAN = 43;// ��ͨ�����ɨ��
		public static final int FINISHOUTCARTOON_SONAMES = 44;// ��ͨ����� ��Ҫ������ȡ
		public static final int FINISHOUTCARTOON = 45;// ��ͨ�����
		public static final int FINISHDELICARTOON_SCAN = 46;// ��ͨ�䷢��ɨ��
		public static final int FINISHDELICARTOON_SONAMES = 47;// ��ͨ�䷢�� ������ȡ
		public static final int FINISHDELICARTOON = 48;// ��ͨ�䷢
		public static final int FINISHOUTCARD_SCAN = 49;// �������ɨ��
		public static final int FINISHOUTCARD_GETPARAMS = 50;// ������� ������ȡ
		public static final int FINISHOUTCARD = 51;// �������
		public static final int FINISHDELICARD_SCAN = 52;// ���巢��ɨ��
		public static final int FINISHDELICARD_GETPARAMS = 53;// ���巢����ȡ����
		public static final int FINISHDELICARD = 54;// ���巢��
		public static final int FINISHWAREHOUSECARD_SCAN = 55;// �������ɨ��
		public static final int FINISHWAREHOUSECARD_GETCOMPLETENAMES = 56;// ��ÿ��������깤����
		public static final int FINISHWAREHOUSECARD = 57;// �������
		public static final int STORAGE_SPLITE_GETPARAMS = 58;// ��ֳ��� ������ȡ
		public static final int FeedOnMaterial_GETSMTMOUNTID = 59;// ���SMT���϶���
																	// SMTMOUNTID
		public static final int FINISHWAREHOUSECARTOON_DOCNOS = 60;// ��ÿ�ͨ�������깤����
		public static final int FINISHWAREHOUSECARD_MONAME = 61;// ��ȡ�������Ĺ������ּ�ID�Լ�������
		public static final int FINISHOUTCARTOON_GETDNNAMES = 62;// ��ȡ���ⵥ��
		public static final int FINISHDELICARTOON_GETDNNAMES = 63;// ��ȡ��ͨ��������
		public static final int FINISHOUTCARD_GETDNNAMES = 64;// ������� ��ȡ��������
		public static final int FINISHDELICARD_GETDNNAMES = 65;// ���巢�� ��ȡ��������
		public static final int DOWNCONFIG_ERROR = 66;// �������ó���
		public static final int DOWNFILE_LENGTH = 67;// ֪ͨ�ϲ������ ���ص��ļ�����
		public static final int DOWN_PROGRESS = 68;// ��ǰ�����ؽ���
		public static final int DOWN_FAIL = 69;// ����ʧ��
		public static final int DOWNLOADSTOP_USER = 70;// ͨ���û��������������ص�ֹͣ״̬
		public static final int RECYCLE_RESOURCE_ERROR = 71;// ����������Դʧ��
		public static final int DOWNLOAD_FINISHED = 72;// ����֪ͨ�û� ���������
		public static final int MESUPDATE_CHECK = 73;// MES-PDA���¼��
		public static final int MES_CHECKUPDATE_FAIL = 74;// MES-PDA������ʧ��
		public static final int MESUPDATE_INSTALL = 75;// MES-PDA���°�װ
		public static final int PREPAREOPERATE_GETMONAMES = 76;// ���ϲ�����ȡ����
		public static final int CHEMATSTATAB_GETSLOTCOUNT = 77;// ����վ�� ��ȡվλ��������
		public static final int SMT_FirstScan_WOInfo = 78;// ��SMT ������Ϣ
		public static final int SMT_FirstScan = 79; // SMT ����ɨ�衣
		public static final int SMT_FirstCancleScan = 80;// SMT ����ɨ������ɨ�衣
		public static final int SMT_LadeToCar = 81; // SMT װ����
		public static final int SMT_LadeStorage = 82; // SMT ��⡣
		public static final int SMT_SC = 83; // ��Ƭɨ��
		public static final int SMT_ScanSnGetWO = 84; // ͨ��SN��ȡ����
		public static final int DIP_GetMoinfo = 85; // ͨ��DIP�ι�����Ϣ��ȡ�ù�����Ϣ��
		public static final int GetMoNameByMOSTDType = 86; // ��ȡDIP�����й�����
		public static final int Dip_StartDIP = 87; // DIP ����
		public static final int GetDipSNType = 88; // DIP��SN����
		public static final int GetResourceId = 89; // ��ȡ��ԴID
		public static final int Dip_mobileStart = 90; // �ֻ�SMTת��װ
		public static final int package_eyecheckGetMOinfo = 91; // TJ Ŀ��
		public static final int package_eyecheckscan = 92; // TJ Ŀ��
		public static final int package_OQCcheckGetMOinfo = 93; // TJ OQC
		public static final int package_OQCSampling_CheckBoxSN = 94;
		public static final int package_OQCSampling_LotSN = 95;
		public static final int package_OQCSampling_PassFail = 96;
		public static final int package_TjBoxLotSNCheck = 97; // ������SNһ���Լ��
		public static final int SMT_EyeCheck = 98; // SMT Ŀ��
		public static final int SMT_SC_New = 99; // ����Ƭɨ��
		public static final int SMT_BindPCBSN = 100; // �ֶ�����Ƭվ���ϱ�ǩPCB��
		public static final int StorageScangetStockCode = 101;

		/** DENG ������ӵ� */
		public static final int FaceCheck = 200;
		public static final int FaceCheckgetmoid = 201;
		public static final int FaceCheckgeterrorcode = 202;

		public static final int Tino = 203;
		public static final int Tinogetmo = 204;
		public static final int Tinogettino = 205;
		public static final int Tinogetlistview = 206;

		public static final int Bindpcbgetmo = 207;// ͨ��sun��ȡ������Ϣ�����������
		public static final int Bindpcb = 208;// ִ�а�
		public static final int Bindpcbsetnum = 209;// ����ϵͳĸ����������

		public static final int getline = 210;// ������׻�ȡ������Ϣ
		public static final int Linestorage = 211;// ����sn�洢
		public static final int Lineselectnum = 212;// ��ȡ������Ϣ

		public static final int linesnstorage = 213;// �������sn�洢
		public static final int linesnselect = 214;// �������sn��ѯ
		public static final int linegetmo = 218;// �������sn��ѯ

		public static final int smtfvmigetmo = 215;// �ɸڰ������Ŀǰ��ȡ����
		public static final int smtfvmi = 216;// �ɸڰ������Ŀ�����ύ

		public static final int sntocar = 217;// �ɸڰ������װ����
		public static final int sntocargetmo = 219;// �ɸڰ������װ����ȡ���� ��ȡ��������
		public static final int sntocargetcar = 220;// �ɸڰ������У�鳵��
		public static final int sntocartocar = 221;// �ɸڰ������ǿ��װ��
		public static final int sntocarchecksingelsn = 222;// �ɸڰ������У�鵥��SN
		public static final int sntocargetmakeupcount = 223;// �ɸڰ������У�鵥��SN
		
		public static final int biaoqianhedui = 224;// �ɸ���¥�����ǩ�˶�
		public static final int common4dmodelgetmobylotsn = 225;// common4dmodel ��ͨ����������
		public static final int common4dmodelgetresourceid = 226;// common4dmodel ��ͨ����������
		
		public static final int tiebiaozhuandsn = 227;// �ɸ���¥����תdsn��ģ��-��
		public static final int xiaojiegouckp = 228;// �ɸ���¥С�ṹCKP
		public static final int anxuzhuangxiang = 229;// �ɸ���¥����װ��
		public static final int anxuzhuangxiangremovebind = 230;// �ɸ���¥����װ������
		
		
		public static final int tjzimicaihebangdingcaihesncheck = 231;// ���ʺа󶨵�ԴSN �ʺ�SN ��moid�Ͳʺ�SN ����У��
		public static final int tjzimicaihebangding = 232;// ���ʺа󶨵�ԴSN �ʺ�SN ��ԴSN ���а�
		
		public static final int tjzimicaihezhuangxianggetmo = 233;// ���ʺ�װ�� ��ȡ������Ϣ ����װ������ �ͼ�����
		public static final int tjzimicaihezhuangxiangzhuangxiang = 234;// ���ʺ�װ�� ��ȡ������Ϣ ����װ������ �ͼ�����
		public static final int tjzimicaihezhuangxiangweishuzhuangxiang = 238;// ���ʺ�װ�� ��ȡ������Ϣ ����װ������ �ͼ�����
		public static final int tjzimicaihezhuangxiangqingkongzhuangxiang = 239;// ���ʺ�װ�� ��ȡ������Ϣ ����װ������ �ͼ�����
		public static final int tjzimicaihezhuangxianggetoqcbyxianghao = 240;// ���ʺ�װ�� ��ȡ������Ϣ ����װ������ �ͼ�����
		public static final int tjzimicaihezhuangxiangshoudongshoujian = 241;// ���ʺ�װ�� ��ȡ������Ϣ ����װ������ �ͼ�����
		
		
		
		
		public static final int tjzimidianyuantourugetmo = 235;// ������׵�ԴͶ���ȡ����ͨ��moname
		public static final int tjzimidianyuantouru = 236;// ������׵�ԴͶ��
		
		public static final int tjzimiweixiu = 237;// ������׵�Դά��
		
		
		public static final int liulouassyqidonggetmo = 242;// �ɸ�6¥��ȡ��������Դ listmaps
		public static final int liulouassyqidong = 243;// �ɸ�6¥��������
		public static final int liulouassyqidonggetmobymoname = 250;// �ɸ�6¥��������
		
		public static final int liuloubujianbangding = 244;// �ɸ�6¥��װ������
		
		public static final int liuloufvmi = 245;// �ɸ�6¥��������Ŀ��
		
		public static final int liuloulaohua = 246;// �ɸ�6¥�ϻ�
		
		public static final int haierbangdingcailiao = 247;// �ɸ�6¥����ƽ��ԭ���ϰ�
		public static final int haierremovebangdingcailiao = 248;// �ɸ�6¥����ƽ��
		
		public static final int haierzhengjisnbangding = 249;// �ɸ�6¥����ƽ��
		
		public static final int lenovotocar = 251;// smt����װ����оƬ
		public static final int lenovofvmi = 252;// smt����Ŀ�����
		public static final int lenovotocartoclosecar = 253;// smt����Ŀ�����
		
		public static final int m8000cpgetpath = 254;// smt����Ŀ�����
		
		public static final int common4dmodelgetmobysn = 255;// ������ѯ������Ϣ ����moitem��
		
		public static final int tjcommonpassstationgetsatations = 256;//�����ͨ��վ ��ȡ���ݿ�Ĺ����
		public static final int tjcommonpassstation = 257;// 
		
		public static final int tjdipstartgetmoinformation = 258;// ���dip������ȡ������Ϣ
		public static final int tjdipstart = 259;// ���dip����
		
		
		public static final int tjbadcount = 260;// �����Ʒ����
		public static final int tjbadcountgetmo = 261;// �����Ʒ����
		public static final int tjbadcountgetline = 262;// �����Ʒ����
		
		
		public static final int smttiepianrengong = 263;// SMT��Ƭ
		public static final int smttiepianupload = 264;// SMT��Ƭ
		public static final int smttiepiandownload = 265;// SMT��Ƭ
		
		public static final int smtladeandbinding = 266;// SMTװ���󶨸�������
		
		public static final int smtprintting = 267;// ����ӡˢɨ�� �˹��ύ��
		
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
		
		
		
		

		/** DENG ������ӵ� */
	}

	/**
	 * @param task
	 * @description �������
	 */
	public static void addTask(Task task) {
		if (null == task)
			return;

		taskQueue.add(task);
	}

}

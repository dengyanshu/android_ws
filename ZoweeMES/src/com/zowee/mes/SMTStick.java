package com.zowee.mes;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.model.SMTSCNewModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class SMTStick extends CommonActivity implements OnItemSelectedListener,
		View.OnClickListener {

	private static final String TAG = "SMTStick";
	
	private   boolean  isseparate_type=false ; //独立模式标志  
	private   CheckBox checkBox_separate; 
    
	
	
	private static boolean SHOWINFO = false;
	private static final boolean DEBUG = false;
	private Lock lock = new ReentrantLock();
	private Lock Scanlock = new ReentrantLock();
	private Spinner spinner_plc_tty;
	private Spinner spinner_plc_baudrate;
	private Spinner spinner_warntime;
	private Spinner spinner_A1_tty;
	private Spinner spinner_A1_baudrate;
	private Spinner spinner_A2_baudrate;
	private Spinner Spinner_PLC_PARITY;
	private Spinner spinner_B1_tty;
	private Spinner spinner_B1_baudrate;
	private Spinner spinner_B2_baudrate;
	private CheckBox checkBox_manual; // 人工干预
	private CheckBox checkBox_warn;
	private CheckBox checkBox_A;
	private CheckBox checkBox_B;
	private CheckBox checkshowInfo;
	private CheckBox checkBoxPCBPanellabel;

	private String RailAlastSN = "";
	private String RailBlastSN = "";
	private String LastSN = "";
	private int OnePCBQty = 2;
	private int StickType = 0; // 0:A过站B过站, 1:A贴标B过站 ,2:A过站B贴标, 3:A贴标B贴标

	private int continuingPass_A = 0;
	private int continuingPass_B = 0;
	private int SinglePCBcontinuingLabel = 0;
	private String[] params_A;
	private String[] params_B;

	private boolean RailASubmitting = true;
	private boolean RailBSubmitting = true;

	private static EditText scanInfoView;

	private TextView textV_A_rate;
	private TextView textV_B_rate;
	private Button button_test;
	private Button button_ok;

	private CheckBox checkB_labelorder;
	private EditText Edit_A_ScannedQty;
	private EditText Edit_B_ScannedQty;
	private EditText Edit_latelyScannedLabel;
	private Spinner spinner_onePCBQTY;
	private Spinner spinner_ScanType;
	private Spinner spinner_A_PCBSide;
	private Spinner spinner_B_PCBSide;

	private TextView textView_warnTime;

	// save setting
	private String plcTTY; // 记录接驳台使用的串口号
	private String plcBaudrate;
	private boolean plcManual; // 警报的时候是否人工干预
	private String plcWarnTime;
	private String mA1TTY; // 记录A轨使用的串口号
	private String mA1Baudrate;
	private String mA2Baudrate;
	private String mB1TTY; // 记录B轨使用的串口号
	private String mB1Baudrate;
	private String mB2Baudrate;
	private int m_plc_parity;
	private String input_sn; // 手动输入SN

	private boolean canScan = false; // 标记是否确认设置，未确认则不进行扫描等操作
	private boolean manualScan = false; // 标记手动扫描
	private SerialPort SerialPortToPLC; // 保存接驳台使用的串口，与serialPort_TTYSx 进行绑定
	private SerialPort SerialPortToA; // 保存A轨使用的串口
	private SerialPort SerialPortToB; // 保存B轨使用的串口

	private SerialPort serialPort_TTYS1; // 绑定到SerialPortTox，不直接操作
	private SerialPort serialPort_TTYS2;
	private SerialPort serialPort_TTYS3;
	private OutputStream dataOutput;

	private static final String TTYS1_FILE = "/sys/devices/platform/rk_serial.1/uart1_select";
	private static final String TTYS2_FILE = "/sys/devices/platform/rk_serial.2/uart2_select";
	private static final String TTYS3_FILE = "/sys/devices/platform/rk_serial.3/uart3_select";

	private static final String PLC_OPERA_RAIL_A = "1"; // PLC 操作A轨扫描的指令 a
														// "1\r\n"; // 20140119
														// 去掉 \r\n
	private static final String PLC_OPERA_RAIL_B = "2"; // PLC 操作B轨扫描的指令 b
														// "2\r\n"; // 20140119
														// 去掉 \r\n
	private static final String[] RAIL_A_TO_PLC = { "0", "1", "2", "3", "4" }; // A
																				// 轨操作PLC指令集
	private static final String[] RAIL_B_TO_PLC = { "5", "6", "7", "9", "8" }; // B
																				// 轨操作PLC指令集

	private SMTSCNewModel smtScModel;

	private boolean isTestA = false; // 扫描枪测试标记
	private boolean isTestB = false;
	private static int reScanTimesA = 1;
	private static int reScanTimesB = 1;

	private static SimpleDateFormat dataFormat = new SimpleDateFormat(
			"hh:mm:ss"); // time format

	// ============== SharedPreferences default setting ======================
	public static String SCAN_COMMAND = "1B31"; // 扫描枪扫描指令
	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // 扫描中断指令
	public static int SCAN_RESCAN = 2;
	public static String OWNER = "system";
	// ============== SharedPreferences default setting end ==================
    
	
	
	public void  testduli(View v){
		commitToMesNew(params_A);
	}
	// 消息处理，更新UI
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				analysisMessage("COM1", msg.obj.toString().trim()); // 20140119
																	// 去掉\r\n
				break;
			case 2:
				analysisMessage("COM2", msg.obj.toString().trim()); // 20140119
																	// 去掉\r\n
				break;
			case 3:
				analysisMessage("COM3", msg.obj.toString().trim()); // 20140119
																	// 去掉\r\n
				break;
			}
			super.handleMessage(msg);
		}
	};

	// 超时处理
	private Handler handlerTimeOut = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4: // A轨扫描超时
				if (reScanTimesA <= SCAN_RESCAN) {
					logDetails(scanInfoView, "A轨第 " + reScanTimesA + " 次重新扫描");
					startScan(PLC_OPERA_RAIL_A);
					reScanTimesA++;
				} else {
					logDetails(scanInfoView, "A轨扫描失败");
					reScanTimesA = 1;
					if (StickType == 1 || StickType == 3)
						scanResultToPLC(false, "RailA", true);
					else
						scanResultToPLC(false, "RailA", false);
				}
				break;
			case 5: // B轨扫描超时
				if (reScanTimesB <= SCAN_RESCAN) {
					logDetails(scanInfoView, "B轨第 " + reScanTimesA + " 次重新扫描");
					startScan(PLC_OPERA_RAIL_B);
					reScanTimesB++;
				} else {

					logDetails(scanInfoView, "B轨扫描失败");
					reScanTimesB = 1;
					if (StickType == 2 || StickType == 3)
						scanResultToPLC(false, "RailB", true);
					else
						scanResultToPLC(false, "RailB", false);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void analysisMessage(String tty, String msg) {
		if (plcTTY.equals(tty)) { // 读取到的是PLC的信息，操作扫描枪
			if (SHOWINFO)
				logDetails(scanInfoView, "PLC消息: " + msg);
			if (msg.contains(PLC_OPERA_RAIL_A)) { // if(PLC_OPERA_RAIL_A.equals(msg)
													// ){ changed by ybj
				startScan(PLC_OPERA_RAIL_A);
			} else if (msg.contains(PLC_OPERA_RAIL_B)) {
				startScan(PLC_OPERA_RAIL_B);
			} else {
				logDetails(scanInfoView, "PLC指令不正确！");
			}
		} else if (mA1TTY.equals(tty)) { // 读取到的是A轨的信息，扫描结果
			if (isTestA) {
				if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
					logDetails(scanInfoView, "A轨测试: 扫描超时");

				} else {
					logDetails(scanInfoView, "A轨测试: " + msg + ";");

				}
				SystemClock.sleep(100);
				startScan(PLC_OPERA_RAIL_A);
				return;
			}
			if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
				if (SHOWINFO)
					logDetails(scanInfoView, "A轨: 扫描超时。");
				handlerTimeOut.sendEmptyMessage(4);
				return;
			}
			if (SHOWINFO)
				logDetails(scanInfoView, "A轨: " + msg);
			reScanTimesA = 1;
			if (msg.equalsIgnoreCase(RailAlastSN)) // 防止重复提交 20131128 ybj
			{
				super.logDetails(scanInfoView, "A轨重复扫描");
				if (!RailASubmitting)
					scanResultToPLC(false, params_A[4], false);
				return;
			}
			if (!CheckLabelOrder(1, msg)) {
				if (!RailASubmitting)
					scanResultToPLC(false, params_A[4], false);
				return;
			}
			CheckLableStep(params_A[4], msg);
			if ((StickType == 1 || StickType == 3) && OnePCBQty > 1
					&& !checkBoxPCBPanellabel.isChecked()) {
				Scanlock.lock();
				if (LastSN.isEmpty()) {
					RailAlastSN = msg;
					LastSN = msg;
					this.Edit_latelyScannedLabel.setText(LastSN);
					params_A[1] = "3";
					Scanlock.unlock();
					return;
				} else {
					params_A[3] = LastSN; // 第一个条码
					params_A[6] = msg; // 最后一个条码
					LastSN = msg;
					this.Edit_latelyScannedLabel.setText(LastSN);
					RailASubmitting = true;
					commitToMesNew(params_A);
					params_A[1] = "2";
					RailAlastSN = msg;
					Scanlock.unlock();
					return;
				}
			} else {
				params_A[3] = msg; // 第一个条码
				params_A[6] = msg; // 最后一个条码 (过站，连板为一，板边条码，前后条码传的一样)
			}
			RailASubmitting = true;
			commitToMesNew(params_A);
			RailAlastSN = msg;

		} else if (mB1TTY.equals(tty)) { // 读取到的是B轨的信息，扫描结果
			if (isTestB) {
				if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
					logDetails(scanInfoView, "B轨测试: 扫描超时。");

				} else {
					logDetails(scanInfoView, "B轨测试: " + msg + ";");

				}
				SystemClock.sleep(100);
				startScan(PLC_OPERA_RAIL_B);
				return;
			}
			if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
				if (SHOWINFO)
					logDetails(scanInfoView, "B轨: 扫描超时");
				handlerTimeOut.sendEmptyMessage(5);
				return;
			}
			if (SHOWINFO)
				logDetails(scanInfoView, "B轨: " + msg + ";");
			reScanTimesB = 1;
			if (msg.equalsIgnoreCase(RailBlastSN)) // 防止重复提交 20131128 ybj
			{
				super.logDetails(scanInfoView, "B轨重复扫描");
				if (!RailBSubmitting)
					scanResultToPLC(false, params_B[4], false);
				return;
			}
			if (!CheckLabelOrder(2, msg)) {
				if (!RailASubmitting)
					scanResultToPLC(false, params_B[4], false);
				return;
			}
			CheckLableStep(params_B[4], msg);
			if ((StickType == 2 || StickType == 3) && OnePCBQty > 1
					&& !checkBoxPCBPanellabel.isChecked()) {
				Scanlock.lock();
				if (LastSN.isEmpty()) {
					RailBlastSN = msg;
					LastSN = msg;
					this.Edit_latelyScannedLabel.setText(LastSN);
					params_B[1] = "3";
					Scanlock.unlock();
					return;
				} else {
					params_B[3] = LastSN; // 第一个条码
					params_B[6] = msg; // 最后一个条码
					LastSN = msg;
					this.Edit_latelyScannedLabel.setText(LastSN);
					RailBSubmitting = true;
					commitToMesNew(params_B);
					params_B[1] = "2";
					RailBlastSN = msg;
					Scanlock.unlock();
					return;
				}
			} else {
				params_B[3] = msg; // 第一个条码
				params_B[6] = msg; // 最后一个条码 (过站，连板为一，板边条码，前后条码传的一样)
			}
			RailBSubmitting = true;
			commitToMesNew(params_B);
			RailBlastSN = msg;
		}
	}

	private void CheckLableStep(String rail, String msg) {
		if (checkBoxPCBPanellabel.isChecked()) // 板边暂为作业员贴标，暂取消检查。
		{
			SinglePCBcontinuingLabel = 4;
			return;
		}
		if (SinglePCBcontinuingLabel >= 3)
			return;
		if (OnePCBQty == 1 || checkBoxPCBPanellabel.isChecked()) {
			if (StickType == 3) {
				Scanlock.lock(); // 防止A,B轨同时访问 LastSN.
				if (!LastSN.isEmpty()) {
					if (LastSN.trim().compareTo(msg.trim()) == 1
							|| LastSN.trim().compareTo(msg.trim()) == -1)
						SinglePCBcontinuingLabel++;
					else
						SinglePCBcontinuingLabel = 0;
				}
				Scanlock.unlock();
			} else if (StickType == 1 && isRailA(rail)) {
				if (RailAlastSN.trim().compareTo(msg.trim()) == 1
						|| RailAlastSN.trim().compareTo(msg.trim()) == -1)
					SinglePCBcontinuingLabel++;
				else
					SinglePCBcontinuingLabel = 0;
			} else if (StickType == 2 && isRailB(rail)) {
				if (RailBlastSN.trim().compareTo(msg.trim()) == 1
						|| RailBlastSN.trim().compareTo(msg.trim()) == -1)
					SinglePCBcontinuingLabel++;
				else
					SinglePCBcontinuingLabel = 0;
			}
		}

	}

	private boolean CheckLabelOrder(int rail, String Msg) // 1为A轨
	{
		boolean reslut = true;
		if (checkBoxPCBPanellabel.isChecked()) // 板边暂为作业员贴标，暂取消检查。
			return true;
		if (rail == 1) {
			if (RailAlastSN.isEmpty())
				return reslut;
			if ((StickType == 1 || StickType == 3)) {
				if (RailAlastSN.trim().length() != Msg.trim().length()) {
					super.logDetails(scanInfoView, "前后条码长度不一致");
					return false;
				}
				if (this.checkB_labelorder.isChecked()) {
					if (RailAlastSN.compareToIgnoreCase(Msg) > 0) {
						super.logDetails(scanInfoView, "选择了升序条码，实际条码为降序。");
						reslut = false;
					}
				} else {
					if (RailAlastSN.compareToIgnoreCase(Msg) < 0) {
						super.logDetails(scanInfoView, "选择了降序条码，实际条码为升序。");
						reslut = false;
					}
				}
			}
		} else {
			if (RailBlastSN.isEmpty())
				return reslut;
			if ((StickType == 2 || StickType == 3))// 检测前面连续3个条码升降顺序是否正确
			{
				if (RailBlastSN.trim().length() != Msg.trim().length()) {
					super.logDetails(scanInfoView, "前后条码长度不一致");
					return false;
				}
				if (this.checkB_labelorder.isChecked()) {
					if (RailBlastSN.compareToIgnoreCase(Msg) > 0) {
						super.logDetails(scanInfoView, "选择了升序条码，条码实际为降序。");
						reslut = false;
					}
				} else {
					if (RailBlastSN.compareToIgnoreCase(Msg) < 0) {
						super.logDetails(scanInfoView, "选择了降序条码，条码实际为升序。");
						reslut = false;
					}
				}
			}
		}

		return reslut;
	}

	private final class SerialEventsListener implements SerialPortEventListener {
		private InputStream dataInput;
		private String who;

		public SerialEventsListener(InputStream dataInput, String tty) {
			this.who = tty;
			this.dataInput = dataInput;
		}

		@Override
		public void serialEvent(SerialPortEvent ev) {
			int type = ev.getEventType();
			switch (type) {
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.BI:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				if (DEBUG)
					Log.d(TAG, "SerialPortEvent.DATA_AVAILABLE");
				if ("/dev/ttyS1".equals(who)) {
					if (DEBUG)
						Log.d(TAG, "serialEvent: ttyS1 read message.");
					if (canScan || manualScan) {
						if (manualScan)
							manualScan = false;
						Message.obtain(mHandler, 1, readMessage())
								.sendToTarget();
					} else {
						clearBuff();
					}
				} else if ("/dev/ttyS2".equals(who)) {
					if (DEBUG)
						Log.d(TAG, "serialEvent: ttyS2 read message.");
					if (canScan || manualScan) {
						if (manualScan)
							manualScan = false;
						Message.obtain(mHandler, 2, readMessage())
								.sendToTarget();
					} else {
						clearBuff();
					}
				} else if ("/dev/ttyS3".equals(who)) {
					if (DEBUG)
						Log.d(TAG, "serialEvent: ttyS3 read message.");
					if (canScan || manualScan) {
						if (manualScan)
							manualScan = false;
						Message.obtain(mHandler, 3, readMessage())
								.sendToTarget();
					} else {
						clearBuff();
					}
				}
				break;
			}
		}

		private void clearBuff() {
			// 接收消息以清空缓存
			try {
				if (dataInput.available() > 0) {
					dataInput.read(new byte[512]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String readMessage() {
			String sReadBuff = "";
			int i;
			byte[] readBuffer = new byte[512];
			int numBytes;
			try {
				while (dataInput.available() > 0) {
					SystemClock.sleep(50);
					numBytes = dataInput.read(readBuffer);
					if (DEBUG) {
						for (i = 0; i < numBytes; i++) {
							Log.d(TAG, "serialEvent: byte[" + i + "]"
									+ (readBuffer[i]));
						}
					}
					String tmpR = new String(readBuffer, 0, numBytes, "UTF-8");
					sReadBuff += tmpR;
				}
				if (DEBUG)
					Log.d(TAG, "serialEvent: read data:" + sReadBuff);
			} catch (IOException e) {
				Log.e(TAG, "serialEvent: Error Reading from serial port", e);
			}
			return sReadBuff;
		}
	}

	/**
	 * 十六进制字符串转Byte[]
	 * 
	 * @return
	 */
	public static byte[] HexString2Bytes() {
		byte[] ret = new byte[SCAN_COMMAND.length() / 2];
		byte[] tmp = SCAN_COMMAND.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
			if (DEBUG)
				Log.d(TAG, "HexString2Bytes: ret[" + i + "]=" + ret[i]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 打开扫描枪扫描
	 * 
	 * @param c
	 *            1打开A轨扫描枪，2打开B轨扫描枪
	 */
	public void startScan(String c) {
		if (PLC_OPERA_RAIL_A.equals(c)) {
			try {
				dataOutput = SerialPortToA.getOutputStream();
				dataOutput.write(HexString2Bytes());

				dataOutput.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (PLC_OPERA_RAIL_B.equals(c)) {
			try {
				dataOutput = SerialPortToB.getOutputStream();
				dataOutput.write(HexString2Bytes());
				dataOutput.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// Rail A: c = {"0","1","2","3","4"}  0失败  1成功 2滚动   3报警 4复位
	// Rail B: c = {"5","6","7","9","8"}
	public void sendCommandToPLC(boolean resToPlc, String[] cToPlc,boolean stop) {
	    if(SHOWINFO)Log.i(TAG, "sendCommandToPLC");
	    String[] comToPlc = new String[3]; // PLC操作指令数组
	    if (resToPlc) {
	        comToPlc[0] = cToPlc[1];
	        comToPlc[1] = cToPlc[2];
	    } else {
	        if (checkBox_warn.isChecked()) {
	        //  comToPlc[0] = cToPlc[0];
	        	
	           comToPlc[0] = cToPlc[3] + plcWarnTime;
	          // comToPlc[1] = cToPlc[2]; 7yue3ri  test  XIUGAI
	           if ( !(plcManual || stop) ) // 人工干预 或 STOP 为真，报警后不传送，停止等待人员处理  ybj 2014-03-07
	           { 
	               comToPlc[1] = cToPlc[2]; 
	           }
	        } else {
	        //  comToPlc[0] = cToPlc[0];
	           comToPlc[0] = cToPlc[2];
	        }
	    }
	    for (int i = 0; i < comToPlc.length; i++) {
	        if (comToPlc[i] != null) {
	           if (!resToPlc && !plcManual && i > 1) {
	               SystemClock.sleep(Integer.parseInt(plcWarnTime) * 1000); //报警时间
	           } else {
	               SystemClock.sleep(150); // 发送每个指令发送间隔至少100ms
	           }
	           lock.lock();  // add by ybj 防止两个线程同时访问PLC串口。
				dataOutput(SerialPortToPLC, (comToPlc[i] + "\r\n"));
				if (DEBUG)
					Log.d(TAG, "sendCommandToPLC: " + comToPlc[i]);
				lock.unlock();
				comToPlc[i] = "";
			}

		}
	}

	private boolean resultToPlc;

	/**
	 * 根据扫描结果，发送指令给PLC
	 * 
	 * @param result
	 *            扫描结果
	 * @param rail
	 *            需要PLC操作的轨道
	 */
	public void scanResultToPLC(boolean result, String rail, final boolean stop) {
		if (isRailA(rail)) { // A 轨
			Log.d(TAG, "scanResultToPLC: control PLC via Rail A");
			resultToPlc = result;
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendCommandToPLC(resultToPlc, RAIL_A_TO_PLC, stop);
				}
			}).start();

		} else if (isRailB(rail)) { // B轨
			Log.d(TAG, "scanResultToPLC: control PLC via Rail B");
			resultToPlc = result;
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendCommandToPLC(resultToPlc, RAIL_B_TO_PLC, stop);
				}
			}).start();

		} else {
			if (SHOWINFO)
				logDetails(scanInfoView, "手动扫描完成");
			Log.i(TAG, "scanResultToPLC: Manual scan finish.");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtstick);
		this.setTitleColor(Color.GREEN);
		this.setTitle("SMT贴片扫描-连板扣料");
		setDefaultValues();
		initView();
		init();

		registerPorts();

		params_A = new String[] { "", "", "", "", "", "", "", "" };
		params_B = new String[] { "", "", "", "", "", "", "", "" };
		params_A[2] = OWNER;
		params_B[2] = OWNER;
		params_A[4] = "RailA";
		params_B[4] = "RailB";
		params_A[0] = MyApplication.getMseUser().getUserId();
		params_B[0] = MyApplication.getMseUser().getUserId();

		logDetails(scanInfoView, "必须连续三片产品绑定条码成功，设定的非人工预才能生效");
		logDetails(scanInfoView, "Success_Msg:如果要使设定项可以重新设定,请进入选项菜单-设定-输入登陆密码");
		logDetails(
				scanInfoView,
				"如果A轨或B轨有一个为第一次贴标后过贴片扫描的,用扫描头手动扫描当前PCB板第一个条码，再让程序自动扫描该PCB上最后一个条码。如果连板数为一则不需要输入。");
		logDetails(scanInfoView,
				"Success_Msg:执行类别-A贴标B贴标：A/B轨都是第一次过贴片扫描，需绑定PCB连板上条码、扣料、过站。");
		logDetails(scanInfoView,
				"执行类别-A过站B贴标：A轨第二次过贴片扫描仅扣料、过站;B轨第一次过贴片扫描，绑定条码、扣料、过站。");
		logDetails(scanInfoView,
				"Success_Msg:执行类别-A贴标B过站：A轨第一次过贴片扫描，绑定条码、扣料、过站；B轨第二次过贴片扫描仅扣料过站。");
		logDetails(scanInfoView, "执行类别-A过站B过站：A/B轨都不是第一次过贴片扫描，仅扣料、过站。");
		logDetails(scanInfoView, "Success_Msg:连板数量为当前连板实际使用数量");
		logDetails(scanInfoView, "下-波特率为设定扫描当前轨道流水标签朝下的扫描头波特率");
		logDetails(scanInfoView, "Success_Msg:上-波特率为设定扫描当前轨道流水标签朝上的扫描头波特率");
		logDetails(scanInfoView, "上-扫描头为设定当前轨道是扫描方向朝上标签还是方向朝下标签");
		logDetails(scanInfoView, "Success_Msg:升序条码为条码从小到大使用，否则为从大到小。");
		logDetails(scanInfoView, "板边条码：贴片站以板边条码过站的，应钩选此项");
		logDetails(scanInfoView, "当前使用者：" + OWNER);
		logDetails(scanInfoView, "Success_Msg:提示：");

	}

	private void setDefaultValues() {
		SharedPreferences pref = getSharedPreferences("smtstick_setting",
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		if (!pref.contains("setting")) { // 第一次运行，初始化默认值
			editor.putBoolean("setting", false);
			editor.putString("owner", OWNER);
			editor.putString("scan_command", SCAN_COMMAND);
			editor.putString("scan_to_command", SCAN_TIMEOUT_COMMAND);
			editor.putInt("rescan", SCAN_RESCAN);
			editor.commit();
			if (DEBUG)
				Log.i(TAG, "editor commit");
		}

		OWNER = pref.getString("owner", OWNER);
		SCAN_COMMAND = pref.getString("scan_command", "");
		SCAN_TIMEOUT_COMMAND = pref.getString("scan_to_command", "");
		/*
		 * if (pref.getBoolean("addEnter", true)) { SCAN_TIMEOUT_COMMAND =
		 * SCAN_TIMEOUT_COMMAND + "\r\n"; }
		 */
		SCAN_RESCAN = pref.getInt("rescan", 3);
		if (DEBUG)
			Log.i(TAG, "read setting");
	}

	/*
	 * public void listPorts() { Enumeration ports =
	 * CommPortIdentifier.getPortIdentifiers(); while(ports.hasMoreElements()){
	 * CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
	 * Log.e(TAG,"port: "+port.getName()); } }
	 */

	private void registerPorts() {
		serialPort_TTYS3 = registerPort("/dev/ttyS3");
		serialPort_TTYS1 = registerPort("/dev/ttyS1");
		serialPort_TTYS2 = registerPort("/dev/ttyS2");
	}

	/**
	 * 注册串口
	 * 
	 * @param tty
	 *            串口路径
	 * @return
	 */
	private SerialPort registerPort(String tty) {
		SerialPort serialPort = null;
		InputStream mInputStream = null;
		try {
			Log.i(TAG, "registerPort: tty = " + tty);
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(tty);
			serialPort = (SerialPort) portId.open(OWNER, 5000);
			mInputStream = serialPort.getInputStream();
			dataOutput = serialPort.getOutputStream();
			serialPort.addEventListener(new SerialEventsListener(mInputStream,
					tty));
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.notifyOnBreakInterrupt(true);
		} catch (PortInUseException e) {
			Log.e(TAG, "registerPort: " + e.currentOwner);
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}
		return serialPort;
	}

	private void updatePortParams(SerialPort port, int baudrate, int PARITY) {
		try {
			port.setSerialPortParams(baudrate, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, PARITY); // 增加可以选择奇偶校验收 ybj

			Log.d(TAG, "updatePortParams: new baudrate:" + baudrate);
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}

	private void dataOutput(SerialPort portName, String out) {
		try {
			dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			dataOutput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 串口选择，每组串口有2个口，写0选择A串口，写1选择B串口。
	 * @param portPath
	 *            串口名 TTYS0_FILE, TTYS1_FILE,TTYS2_FILE
	 * @param subPort
	 *            子串口编号 0 or 1
	 */
	private void switchPort(String portPath, int subPort) {
		if (subPort == getCurrentSubPort(portPath)) {
			Log.d(TAG, "switchPort: already set, no need to switch.");
			return;
		}
		FileWriter switchOutput = null;
		try {
			switchOutput = new FileWriter(portPath);
			switchOutput.write(subPort + "\n");
			switchOutput.flush();
			Log.d(TAG, (subPort == 0 ? "switchPort: port swith to A"
					: "switchPort: port switch to B"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				switchOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取当前使用的串口，0为A串口，1为B串口，默认为0
	 * 
	 * @param portPath
	 *            串口名 TTYS0_FILE, TTYS1_FILE,TTYS2_FILE
	 */
	private int getCurrentSubPort(String portPath) {
		int subPort = 0;
		FileReader subPortReader = null;
		try {
			subPortReader = new FileReader(portPath);
			subPort = (subPortReader.read() == 48) ? 0 : 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subPort;
	}

	private void unRegisterPort() {
		if (dataOutput != null) {
			try {
				dataOutput.close();
			} catch (IOException e) {
				Log.e(TAG, "unRegisterPort dataOutput", e);
			}
			dataOutput = null;
		}
		if (serialPort_TTYS1 != null) {
			serialPort_TTYS1.removeEventListener();
			serialPort_TTYS1.close();
		}
		serialPort_TTYS1 = null;
		if (serialPort_TTYS2 != null) {
			serialPort_TTYS2.removeEventListener();
			serialPort_TTYS2.close();
		}
		serialPort_TTYS2 = null;
		if (serialPort_TTYS3 != null) {
			serialPort_TTYS3.removeEventListener();
			serialPort_TTYS3.close();
		}
		serialPort_TTYS3 = null;
		if (SerialPortToA != null) {
			SerialPortToA.removeEventListener();
			SerialPortToA.close();
		}
		SerialPortToA = null;
		if (SerialPortToB != null) {
			SerialPortToB.removeEventListener();
			SerialPortToB.close();
		}
		SerialPortToB = null;
		if (SerialPortToPLC != null) {
			SerialPortToPLC.removeEventListener();
			SerialPortToPLC.close();
		}
		SerialPortToPLC = null;
	}

	private void initView() {
		
		
		checkBox_separate=(CheckBox) findViewById(R.id.checkBox_duli);

		textV_A_rate = (TextView) findViewById(R.id.textV_A_rate);
		textV_B_rate = (TextView) findViewById(R.id.textV_B_rate);

		ArrayAdapter<CharSequence> adapterParity = ArrayAdapter
				.createFromResource(this, R.array.SMTStick_Parity,
						android.R.layout.simple_spinner_item);
		adapterParity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<CharSequence> adapterTTY = ArrayAdapter
				.createFromResource(this, R.array.SMTStick_tty,
						android.R.layout.simple_spinner_item);
		adapterTTY
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_plc_tty = (Spinner) findViewById(R.id.spinner_tty);
		spinner_plc_tty.setAdapter(adapterTTY);
		spinner_plc_tty.setSelection(0);
		spinner_plc_tty.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapterBit = ArrayAdapter
				.createFromResource(this, R.array.SMTStick_bit,
						android.R.layout.simple_spinner_item);
		adapterBit
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_plc_baudrate = (Spinner) findViewById(R.id.spinner_baudrate);
		spinner_plc_baudrate.setAdapter(adapterBit);
		spinner_plc_baudrate.setSelection(2);
		spinner_plc_baudrate.setOnItemSelectedListener(this);

		spinner_A1_tty = (Spinner) findViewById(R.id.spinner_A1_tty);
		spinner_A1_tty.setAdapter(adapterTTY);
		spinner_A1_tty.setSelection(1);
		spinner_A1_tty.setOnItemSelectedListener(this);
		spinner_A1_baudrate = (Spinner) findViewById(R.id.spinner_A1_baudrate);
		spinner_A1_baudrate.setAdapter(adapterBit);
		spinner_A1_baudrate.setSelection(2);
		spinner_A1_baudrate.setOnItemSelectedListener(this);

		spinner_A2_baudrate = (Spinner) findViewById(R.id.spinner_A2_baudrate);
		spinner_A2_baudrate.setAdapter(adapterBit);
		spinner_A2_baudrate.setSelection(2);
		spinner_A2_baudrate.setOnItemSelectedListener(this);

		spinner_B1_tty = (Spinner) findViewById(R.id.spinner_B1_tty);
		spinner_B1_tty.setAdapter(adapterTTY);
		spinner_B1_tty.setSelection(2);
		spinner_B1_tty.setOnItemSelectedListener(this);
		spinner_B1_baudrate = (Spinner) findViewById(R.id.spinner_B1_baudrate);
		spinner_B1_baudrate.setAdapter(adapterBit);
		spinner_B1_baudrate.setSelection(2);
		spinner_B1_baudrate.setOnItemSelectedListener(this);

		spinner_B2_baudrate = (Spinner) findViewById(R.id.spinner_B2_baudrate);
		spinner_B2_baudrate.setAdapter(adapterBit);
		spinner_B2_baudrate.setSelection(2);
		spinner_B2_baudrate.setOnItemSelectedListener(this);

		Spinner_PLC_PARITY = (Spinner) findViewById(R.id.Spinner_PLC_PARITY);
		Spinner_PLC_PARITY.setAdapter(adapterParity);
		Spinner_PLC_PARITY.setSelection(0);
		Spinner_PLC_PARITY.setOnItemSelectedListener(this);
		// set warning time adapter
		adapterBit = ArrayAdapter.createFromResource(this,
				R.array.SMTStick_warn_time,
				android.R.layout.simple_spinner_item);
		adapterBit
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_warntime = (Spinner) findViewById(R.id.spinner_warntime);
		spinner_warntime.setAdapter(adapterBit);
		spinner_warntime.setOnItemSelectedListener(this);

		scanInfoView = (EditText) findViewById(R.id.Edit_SMTStickInfo);
		scanInfoView.setText("");

		checkBox_manual = (CheckBox) findViewById(R.id.checkBox_hand);
		checkBox_manual.setOnCheckedChangeListener(checkListener);
		checkBox_warn = (CheckBox) findViewById(R.id.checkBox_warn);
		checkBox_warn.setOnCheckedChangeListener(checkListener);

		checkBox_A = (CheckBox) findViewById(R.id.checkBox_A);
		checkBox_A.setOnCheckedChangeListener(checkListener);

		checkBox_B = (CheckBox) findViewById(R.id.checkBox_B);
		checkBox_B.setOnCheckedChangeListener(checkListener);

		checkBoxPCBPanellabel = (CheckBox) findViewById(R.id.checkBox_PCBPanellabel);
		checkBoxPCBPanellabel.setOnCheckedChangeListener(checkListener);
		textView_warnTime = (TextView) findViewById(R.id.textView_warntime);
		textView_warnTime.setFocusable(true);
		textView_warnTime.setFocusableInTouchMode(true);
		textView_warnTime.requestFocus();

		// control button

		button_ok = (Button) findViewById(R.id.Button_ok);
		button_test = (Button) findViewById(R.id.Button_test);

		button_ok.setOnClickListener(this);
		button_test.setOnClickListener(this);

		checkshowInfo = (CheckBox) findViewById(R.id.check_showInfo);
		checkshowInfo.setOnClickListener(this);
		smtScModel = new SMTSCNewModel();

		checkB_labelorder = (CheckBox) findViewById(R.id.checkB_labelorder);
		// checkB_labelorder.setOnCheckedChangeListener(checkListener);

		Edit_A_ScannedQty = (EditText) findViewById(R.id.Edit_A_ScannedQty);
		Edit_B_ScannedQty = (EditText) findViewById(R.id.Edit_B_ScannedQty);

		ArrayAdapter<CharSequence> OnePCBQtyAdapter = ArrayAdapter
				.createFromResource(this, R.array.SMTFirstScanPCBQuantity,
						android.R.layout.simple_spinner_item);
		OnePCBQtyAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_onePCBQTY = (Spinner) findViewById(R.id.spinner_onePCBQTY);
		spinner_onePCBQTY.setAdapter(OnePCBQtyAdapter);
		spinner_onePCBQTY.setSelection(1);
		spinner_onePCBQTY.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> ScanTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.SMTStickType,
						android.R.layout.simple_spinner_item);
		ScanTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_ScanType = (Spinner) findViewById(R.id.spinner_ScanType);
		spinner_ScanType.setAdapter(ScanTypeAdapter);
		spinner_ScanType.setSelection(0);
		spinner_ScanType.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> SMTStickPCBSide = ArrayAdapter
				.createFromResource(this, R.array.SMTStickPCBSide,
						android.R.layout.simple_spinner_item);
		SMTStickPCBSide
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_A_PCBSide = (Spinner) findViewById(R.id.spinner_A_PCBSide);
		spinner_A_PCBSide.setAdapter(SMTStickPCBSide);
		spinner_A_PCBSide.setSelection(0);
		spinner_A_PCBSide.setOnItemSelectedListener(this);

		spinner_B_PCBSide = (Spinner) findViewById(R.id.spinner_B_PCBSide);
		spinner_B_PCBSide.setAdapter(SMTStickPCBSide);
		spinner_B_PCBSide.setSelection(0);
		spinner_B_PCBSide.setOnItemSelectedListener(this);

		Edit_latelyScannedLabel = (EditText) findViewById(R.id.Edit_latelyScannedLabel);
		Edit_latelyScannedLabel.setOnClickListener(this);

		updateView(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {

		case R.id.Button_ok:
			if (plcTTY == mA1TTY || plcTTY == mB1TTY || mA1TTY == mB1TTY) {
				showDialog(10010);
			} else {
				// 如果没有选择轨道，则不处理条码（手动扫描除外）
				canScan = true;
				updateView(false);
				// 绑定port
				SerialPortToPLC = portSelector(plcTTY);
				SerialPortToA = portSelector(mA1TTY);
				SerialPortToB = portSelector(mB1TTY);

				updatePortParams(SerialPortToPLC,
						Integer.parseInt(plcBaudrate), m_plc_parity);
				switchPort(portPathSelector(plcTTY), 0); // 重置PLC的subPort = 0
				if (checkBox_A.isChecked()) {
					updatePortParams(SerialPortToA,
							Integer.parseInt(mA1Baudrate), 0);
					switchPort(portPathSelector(mA1TTY), 0);
				} else {
					updatePortParams(SerialPortToA,
							Integer.parseInt(mA2Baudrate), 0);
					switchPort(portPathSelector(mA1TTY), 1);
				}
				if (checkBox_B.isChecked()) {
					updatePortParams(SerialPortToB,
							Integer.parseInt(mB1Baudrate), 0);
					switchPort(portPathSelector(mB1TTY), 0);
				} else {
					updatePortParams(SerialPortToB,
							Integer.parseInt(mB2Baudrate), 0);
					switchPort(portPathSelector(mB1TTY), 1);
				}
				if(checkBox_separate.isChecked())
					isseparate_type=true;
				else
					isseparate_type=false;
				if (DEBUG)
					showSetValue();
			}
			break;

		case R.id.Button_test:
			if (this.Edit_latelyScannedLabel.getText().toString().trim()
					.length() >= 8) {
				Toast.makeText(this, "请在贴片前测试", 5).show();
				return;
			}
			String btstr = button_test.getText().toString().trim();
			if ("测试扫描头".equals(btstr)) {
				showDialog(10011);
			} else {

				if (isTestA)
					isTestA = false;
				if (isTestB)
					isTestB = false;
				button_test.setText("测试扫描头");
				logDetails(scanInfoView, "停止测试");

			}
			break;
		case R.id.check_showInfo:
			if (checkshowInfo.isChecked())
				SHOWINFO = true;
			else
				SHOWINFO = false;

			break;
		}
	}

	public void onResume() {
		super.onResume();

	}

	/**
	 * subPort文件选择器
	 * 
	 * @param s
	 * @return
	 */
	public String portPathSelector(String s) {
		if (s.equals("COM1")) {
			return TTYS1_FILE;
		} else if (s.equals("COM2")) {
			return TTYS2_FILE;
		} else {
			return TTYS3_FILE;
		}
	}

	/**
	 * SerialPort 选择器，根据选择的串口号(COM1,COM2,COM3)，绑定serialPort_TTYSx 到SerialPortTox
	 * 
	 * @param str
	 *            UI中选择的串口号
	 * @return
	 */
	public SerialPort portSelector(String str) {
		if (DEBUG)
			Log.d(TAG, "port select: " + str);
		if (str.equals("COM1")) {
			return serialPort_TTYS1;
		} else if ("COM2".equals(str)) {
			return serialPort_TTYS2;
		} else {
			return serialPort_TTYS3;
		}
	}

	public void showSetValue() {

		String str = "接驳台连接 " + plcTTY + ", 波特率："
				+ SerialPortToPLC.getBaudRate();
		if (checkBox_warn.isChecked()) {
			str += ",警报时间：" + plcWarnTime + "秒";
		}
		if (checkBox_A.isChecked()) {
			str += "\nA轨选择上扫描头，连接" + mA1TTY + ", 波特率："
					+ SerialPortToA.getBaudRate();
		} else {
			str += "\nA轨选择下扫描头，连接" + mA1TTY + ", 波特率："
					+ SerialPortToA.getBaudRate();
		}
		if (checkBox_B.isChecked()) {
			str += "\nB轨选择上扫描头，连接" + mB1TTY + ", 波特率："
					+ SerialPortToB.getBaudRate();
		} else {
			str += "\nB轨选择下扫描头，连接" + mB1TTY + ", 波特率："
					+ SerialPortToB.getBaudRate();
		}

		logDetails(scanInfoView, str);
	}

	public void updateView(boolean enable) {
		if (DEBUG)
			Log.d(TAG, "updateView");
		LastSN = "";
		this.Edit_latelyScannedLabel.setText("");
		continuingPass_A = 0;
		continuingPass_B = 0;
		RailAlastSN = "";
		RailBlastSN = "";
		SinglePCBcontinuingLabel = 0;
		setEnable(spinner_plc_tty, enable);
		setEnable(spinner_plc_baudrate, enable);
		setEnable(spinner_warntime, enable);
		setEnable(spinner_A1_baudrate, enable);
		setEnable(spinner_A2_baudrate, enable);
		setEnable(spinner_A1_tty, enable);
		setEnable(spinner_B1_baudrate, enable);
		setEnable(spinner_B2_baudrate, enable);
		setEnable(Spinner_PLC_PARITY, enable);
		setEnable(spinner_B1_tty, enable);
		setEnable(button_ok, enable);
		setEnable(button_test, !enable);
		setEnable(checkBox_A, enable);
		setEnable(checkBox_B, enable);
		// setEnable(checkBox_warn, enable);
		setEnable(checkBox_manual, enable);
		setEnable(checkB_labelorder, enable);
		setEnable(spinner_onePCBQTY, enable);
		setEnable(spinner_ScanType, enable);
		setEnable(spinner_A_PCBSide, enable);
		setEnable(spinner_B_PCBSide, enable);
		setEnable(checkBoxPCBPanellabel, enable);
		
		setEnable(checkBox_separate, enable);

	}

	public OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
			int id = checkBox.getId();
			switch (id) {
			case R.id.checkBox_hand:
				if (isChecked) {
					plcManual = true;
				} else {
					plcManual = false;
				}
				break;
			case R.id.checkBox_warn:
				if (!isChecked) {
					textView_warnTime.setVisibility(View.GONE);
					spinner_warntime.setVisibility(View.GONE);
					checkBox_manual.setChecked(false);
					checkBox_manual.setVisibility(View.GONE);
				} else {
					textView_warnTime.setVisibility(View.VISIBLE);
					spinner_warntime.setVisibility(View.VISIBLE);
					checkBox_manual.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.checkBox_A:
				if (checkBox_A.isChecked()) {

					logDetails(scanInfoView, "A轨选择上扫描头");
					textV_A_rate.setText("上-波特率");
					spinner_A1_baudrate.setVisibility(View.VISIBLE);
					spinner_A2_baudrate.setVisibility(View.GONE);
				} else {
					logDetails(scanInfoView, "A轨选择下扫描头");
					textV_A_rate.setText("下-波特率");
					spinner_A1_baudrate.setVisibility(View.GONE);
					spinner_A2_baudrate.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.checkBox_B:
				if (checkBox_B.isChecked()) {
					textV_B_rate.setText("上-波特率");
					logDetails(scanInfoView, "B轨选择上扫描头");

					spinner_B1_baudrate.setVisibility(View.VISIBLE);
					spinner_B2_baudrate.setVisibility(View.GONE);
				} else {
					textV_B_rate.setText("下-波特率");
					logDetails(scanInfoView, "B轨选择下扫描头");
					spinner_B1_baudrate.setVisibility(View.GONE);
					spinner_B2_baudrate.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.checkBox_PCBPanellabel:
				settingStickType();
				break;
			}
		}
	};

	public void setEnable(View v, boolean isChecked) {
		if (isChecked) {
			v.setEnabled(true);
		} else {
			v.setEnabled(false);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View arg1, int itemId,
			long arg3) {
		int id = spinner.getId();
		switch (id) {
		case R.id.spinner_tty:
			plcTTY = spinner_plc_tty.getSelectedItem().toString();
			break;
		case R.id.spinner_baudrate:
			plcBaudrate = spinner_plc_baudrate.getSelectedItem().toString();
			break;
		case R.id.spinner_warntime:
			plcWarnTime = spinner_warntime.getSelectedItem().toString();
			break;
		case R.id.spinner_A1_baudrate:
			mA1Baudrate = spinner_A1_baudrate.getSelectedItem().toString();
			break;
		case R.id.spinner_A1_tty:
			mA1TTY = spinner_A1_tty.getSelectedItem().toString();
			break;
		case R.id.spinner_A2_baudrate:
			mA2Baudrate = spinner_A2_baudrate.getSelectedItem().toString();
			break;
		case R.id.spinner_B1_baudrate:
			mB1Baudrate = spinner_B1_baudrate.getSelectedItem().toString();
			break;
		case R.id.spinner_B1_tty:
			mB1TTY = spinner_B1_tty.getSelectedItem().toString();
			break;
		case R.id.spinner_B2_baudrate:
			mB2Baudrate = spinner_B2_baudrate.getSelectedItem().toString();
			break;
		case R.id.Spinner_PLC_PARITY:
			m_plc_parity = Spinner_PLC_PARITY.getSelectedItemPosition();
			break;
		case R.id.spinner_onePCBQTY:
			OnePCBQty = spinner_onePCBQTY.getSelectedItemPosition() + 1;

			params_A[7] = OnePCBQty + "";
			params_B[7] = OnePCBQty + "";
			break;
		case R.id.spinner_A_PCBSide:
			if (spinner_ScanType.getSelectedItemPosition() == 3
					&& (spinner_A_PCBSide.getSelectedItemPosition() != spinner_B_PCBSide
							.getSelectedItemPosition())) {
				spinner_B_PCBSide.setSelection(spinner_A_PCBSide
						.getSelectedItemPosition());
				Toast.makeText(this, "执行类别为A、B轨都为贴标时，A、B轨贴片面应该相同", 8).show();
			}
			params_A[5] = spinner_A_PCBSide.getItemAtPosition(
					spinner_A_PCBSide.getSelectedItemPosition()).toString();
			break;
		case R.id.spinner_B_PCBSide:
			if (spinner_ScanType.getSelectedItemPosition() == 3
					&& (spinner_A_PCBSide.getSelectedItemPosition() != spinner_B_PCBSide
							.getSelectedItemPosition())) {
				spinner_A_PCBSide.setSelection(spinner_B_PCBSide
						.getSelectedItemPosition());
				Toast.makeText(this, "执行类别为A、B轨都为贴标时，A、B轨贴片面应该相同", 8).show();
			}
			params_B[5] = spinner_B_PCBSide.getItemAtPosition(
					spinner_B_PCBSide.getSelectedItemPosition()).toString();

			break;
		case R.id.spinner_ScanType:
			if (spinner_ScanType.getSelectedItemPosition() == 3
					&& (spinner_A_PCBSide.getSelectedItemPosition() != spinner_B_PCBSide
							.getSelectedItemPosition())) {
				Toast.makeText(this, "A、B轨贴片面不相同时，不能选择A轨贴标B轨贴标", 8).show();
				spinner_ScanType.setSelection(1);
			}
			StickType = spinner_ScanType.getSelectedItemPosition();
			
			settingStickType();
			break;
		}
	}

	private void settingStickType() {

		if (StickType == 1 || StickType == 3) // A 轨
		{
			if (checkBoxPCBPanellabel.isChecked()) {
				params_A[1] = "4"; // 板边条码
			} else {
				if (OnePCBQty == 1) {
					params_A[1] = "3";
				} else {
					params_A[1] = "2";
				}
			}
		} else
			params_A[1] = "1";

		if (StickType == 2 || StickType == 3) // B 轨
		{
			if (checkBoxPCBPanellabel.isChecked()) {
				params_B[1] = "4"; // 板边条码
			} else {
				if (OnePCBQty == 1) {
					params_B[1] = "3";
				} else {
					params_B[1] = "2"; // 板边条码

				}
			}
		} else
			params_B[1] = "1";
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		Dialog d = null;
		switch (id) {
		case 10010:
			d = new AlertDialog.Builder(this).setTitle("串口选择重复")
					.setMessage("串口选择重复了，请再次确认！").setNegativeButton("确定", null)
					.show();
			break;
		case 10011:
			d = new AlertDialog.Builder(this)
					.setTitle("开始测试扫描枪")
					.setMessage("确定要开始测试扫描枪吗？\n再次点击停止扫描")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									if (checkBox_A.isChecked()) {
										logDetails(scanInfoView, "测试A轨上扫描枪");
									} else {
										logDetails(scanInfoView, "测试A轨下扫描枪");
									}
									isTestA = true;
									startScan(PLC_OPERA_RAIL_A);
									SystemClock.sleep(100);
									if (checkBox_B.isChecked()) {
										logDetails(scanInfoView, "测试B轨上扫描枪");
									} else {
										logDetails(scanInfoView, "测试B轨下扫描枪");
									}
									isTestB = true;
									startScan(PLC_OPERA_RAIL_B);
									button_test.setText("扫描头测试中...");

								}
							}).setNegativeButton("取消", null).show();
			break;
		/*
		 * case 10086: d = new AlertDialog.Builder(this) .setTitle("请确认输入的SN号")
		 * .setMessage("SN：") .setPositiveButton("提交", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface arg0, int arg1) {
		 * manualScan = true; params[3] = input_sn; params[4] = "Manual";
		 * commitToMes(params); } }) .setNegativeButton("取消", null).show();
		 * break;
		 */
		}
		return d;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case 10086:
			((AlertDialog) dialog).setMessage("SN: " + input_sn);
		}
	}

	public static void ClearshowInfo() {
		if (scanInfoView.getLineCount() > 100)
			scanInfoView.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtstick, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_clear:
			scanInfoView.setText("");
			break;
		case R.id.action_settings:
			final android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
					this);
			LinearLayout loginForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.passworddialog, null);
			builder2.setView(loginForm);
			builder2.setTitle("打开设定项");
			final EditText password = (EditText) loginForm
					.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (MyApplication.getpasswrod().equals(
									password.getText().toString().trim())) {
								canScan = false;
								updateView(true);
							}
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("确定退出SMT贴片扫描吗?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								unRegisterPort();
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	/**
	 * 提交扫描码给MES
	 * 
	 * @param params
	 *            包含轨道信息
	 */
	private void commitToMesNew(String[] par) {
		String[] p = par.clone();
		Task mScan = new Task(SMTStick.this, TaskType.SMT_SC_New, p);
		if(!isseparate_type){
			if (isRailA(p[4]))
				progressDialog.setMessage("A轨贴片数据提交中");
			else
				progressDialog.setMessage("B轨贴片数据提交中");
			ClearshowInfo(); // 大于100行清除。
			showProDia();
			smtScModel.SMTSC_New(mScan);
		}
		else{
			if (isRailA(p[4]))
				progressDialog.setMessage("A轨独立贴片数据提交中");
			else
				progressDialog.setMessage("B轨独立贴片数据提交中");
			
			ClearshowInfo(); // 大于100行清除。
			showProDia();
			smtScModel.SMTSC_New2(mScan);
		}
		
		
		
//		if (isRailA(p[4]))
//			progressDialog.setMessage("A轨贴片数据提交中");
//		else
//			progressDialog.setMessage("B轨贴片数据提交中");
//		
//		ClearshowInfo(); // 大于100行清除。
//		showProDia();
//		smtScModel.SMTSC_New(mScan);
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.SMT_SC;
		super.init();
		super.progressDialog.setMessage("SMT贴片扫描");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task) {

		super.refresh(task);
		String rail = "";
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		switch (task.getTaskType()) {
		case TaskType.SMT_SC_New:
			closeProDia();
			if (task.getTaskResult() != null) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				rail = getdata.get("Rail").toString();
				if (isRailA(rail)) {
					// A 轨
					if (SHOWINFO)
						logDetails(scanInfoView, "A轨提交MES结果");
					resultFromMes(getdata, rail);
					RailASubmitting = false;

				}
				if (isRailB(rail)) {
					// B轨
					if (SHOWINFO)
						logDetails(scanInfoView, "B轨提交MES结果");
					resultFromMes(getdata, rail);
					RailBSubmitting = false;
				}
			}
			break;
		}
	}

	public void resultFromMes(HashMap<String, String> data, String rail) {
		boolean isSuccess;

		boolean Stopconveyer = false;
		if (SHOWINFO)
			logDetails(scanInfoView, data.toString());

		if (data.get("Error") == null) {
			if (data.get("I_ReturnValue").equals("0")) {
				// 存储成功
				isSuccess = true;
				if (StickType == 3) {
					continuingPass_A++;
				} else {
					if (isRailA(rail))
						continuingPass_A++;
					if (isRailB(rail))
						continuingPass_B++;
				}
				if (data.containsKey("FinishQty")) {
					if (isRailA(rail))
						this.Edit_A_ScannedQty.setText(data.get("FinishQty")
								.toString().trim());
					if (isRailB(rail))
						this.Edit_B_ScannedQty.setText(data.get("FinishQty")
								.toString().trim());
				}
				if (!SHOWINFO)
					logDetails(scanInfoView, "Success_Msg:[" + rail + "]: "
							+ data.get("I_ReturnMessage"));
			} else if (data.get("I_ReturnValue").equals("1")) {
				isSuccess = true;
				if (StickType == 3) {
					if (continuingPass_A <= 3) { // 必顺连续3个PASS 否则重新开始计算
						continuingPass_A = 0;
						isSuccess = false;
					}
				} else {
					if (isRailA(rail) && continuingPass_A <= 3) { // A轨必顺连续3个PASS
																	// 否则重新开始计算
						continuingPass_A = 0;
						isSuccess = false;
					}
					if (isRailB(rail) && continuingPass_B <= 3) {
						continuingPass_B = 0;
						isSuccess = false;
					}
				}
				if (data.containsKey("FinishQty")) {
					if (isRailA(rail))
						this.Edit_A_ScannedQty.setText(data.get("FinishQty")
								.toString().trim());
					if (isRailB(rail))
						this.Edit_B_ScannedQty.setText(data.get("FinishQty")
								.toString().trim());
				}
				if (!SHOWINFO)
					logDetails(scanInfoView, "Success_Msg:[" + rail + "]: "
							+ data.get("I_ReturnMessage"));
			} else {
				isSuccess = false;
				Stopconveyer = true;
				plcManual=true;
				if (!SHOWINFO && data.containsKey("I_ReturnMessage"))
					logDetails(scanInfoView, "强制扣料:[" + rail + "]: "
							+ data.get("I_ReturnMessage"));
				//***********************************************************
				if(data.get("I_ReturnMessage").contains("不足单位用量")){
					plcManual=true;
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("物料漏扫描，请注意！");
					builder.setMessage("    请核对物料信息？"+data.get("I_ReturnMessage"));
					builder.setPositiveButton("确定",null);
					builder.setNegativeButton("取消", null);
					builder.create().show();
				}
				
				//***********************************************************
				if (StickType == 3) {
					if (continuingPass_A <= 3) // 必顺连续3个PASS 否则重新开始计算
						continuingPass_A = 0;
				} else {
					if (isRailA(rail) && continuingPass_A <= 3) // A轨必顺连续3个PASS
																// 否则重新开始计算
						continuingPass_A = 0;
					if (isRailB(rail) && continuingPass_B <= 3) // B轨必顺连续3个PASS
																// 否则重新开始计算
						continuingPass_B = 0;
				}
				if (!SHOWINFO)
					logDetails(scanInfoView,
							"[" + rail + "]: " + data.get("I_ReturnMessage"));
			}

		} else {
			isSuccess = false;
			if (!SHOWINFO)
				logDetails(scanInfoView, "MES 返回异常: " + data.get("Error"));
		}
		if (OnePCBQty == 1 && StickType != 0) {
			if (SinglePCBcontinuingLabel < 3 && StickType == 3)
				Stopconveyer = true;
			if (SinglePCBcontinuingLabel < 3 && StickType == 1 && isRailA(rail))
				Stopconveyer = true;
			if (SinglePCBcontinuingLabel < 3 && StickType == 2 && isRailB(rail))
				Stopconveyer = true;
		}
		if (checkBoxPCBPanellabel.isChecked() && StickType != 0) {
			if (SinglePCBcontinuingLabel < 3 && StickType == 3)
				Stopconveyer = true;
			if (SinglePCBcontinuingLabel < 3 && StickType == 1 && isRailA(rail))
				Stopconveyer = true;
			if (SinglePCBcontinuingLabel < 3 && StickType == 2 && isRailB(rail))
				Stopconveyer = true;
		}
		if (!isSuccess) {
			if (StickType == 3) {
				if (continuingPass_A <= 3)
					Stopconveyer = true;
			} else {
				if (continuingPass_A <= 3 && isRailA(rail))
					Stopconveyer = true;
				if (continuingPass_B <= 3 && isRailB(rail))
					Stopconveyer = true;
			}
		}
		scanResultToPLC(isSuccess, rail, Stopconveyer);
	}

	public boolean isRailA(String r) {
		if ("RailA".equals(r)) {
			return true;
		}
		return false;
	}

	public boolean isRailB(String r) {
		if ("RailB".equals(r)) {
			return true;
		}
		return false;
	}
}
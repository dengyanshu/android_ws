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
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.zowee.mes.model.SMTSCModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class SMTStick_Old extends CommonActivity implements
		OnItemSelectedListener, View.OnClickListener {
	
	
	private   boolean  isseparate_type=false ; //����ģʽ��־  
	private   CheckBox checkBox_separate; 

	private static final String TAG = "SMTStick";
	private static boolean SHOWINFO = false;
	private static final boolean DEBUG = false;
	private Lock lock = new ReentrantLock();
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
	private CheckBox checkBox_manual; // �˹���Ԥ
	private CheckBox checkBox_warn;
	private CheckBox checkBox_A1;
	private CheckBox checkBox_A2;
	private CheckBox checkBox_B1;
	private CheckBox checkBox_B2;
	private CheckBox checkshowInfo;
	private Spinner spinner_A_Side;
	private Spinner spinner_B_Side;
	private String RailAlastSN = "";
	private String RailBlastSN = "";
	private String LastSN = "";
	private boolean RailASubmitting = true;
	private boolean RailBSubmitting = true;
	
	
	/*******************************************************/
	private  int arescantime=1;  //A���ظ�ɨ�����������
	private  int brescantime=1;
	/*******************************************************/
	
	
	private EditText editText_sn;
	private static EditText scanInfoView;

	private Button button_scan;
	private Button button_test;
	private Button button_ok;
	// private Button button_edit;

	private TextView textView_warnTime;

	// save setting
	private String plcTTY; // ��¼�Ӳ�̨ʹ�õĴ��ں�
	private String plcBaudrate;
	private boolean plcManual; // ������ʱ���Ƿ��˹���Ԥ
	private String plcWarnTime;
	private String mA1TTY; // ��¼A��ʹ�õĴ��ں�
	private String mA1Baudrate;
	private String mA2Baudrate;
	private String mB1TTY; // ��¼B��ʹ�õĴ��ں�
	private String mB1Baudrate;
	private String mB2Baudrate;
	private int m_plc_parity;
	private String input_sn; // �ֶ�����SN

	private boolean canScan = false; // ����Ƿ�ȷ�����ã�δȷ���򲻽���ɨ��Ȳ���
	private boolean manualScan = false; // ����ֶ�ɨ��
	private SerialPort SerialPortToPLC; // ����Ӳ�̨ʹ�õĴ��ڣ���serialPort_TTYSx ���а�
	private SerialPort SerialPortToA; // ����A��ʹ�õĴ���
	private SerialPort SerialPortToB; // ����B��ʹ�õĴ���

	private SerialPort serialPort_TTYS1; // �󶨵�SerialPortTox����ֱ�Ӳ���
	private SerialPort serialPort_TTYS2;
	private SerialPort serialPort_TTYS3;
	private OutputStream dataOutput;

	private static final String TTYS1_FILE = "/sys/devices/platform/rk_serial.1/uart1_select";
	private static final String TTYS2_FILE = "/sys/devices/platform/rk_serial.2/uart2_select";
	private static final String TTYS3_FILE = "/sys/devices/platform/rk_serial.3/uart3_select";

	private static final String PLC_OPERA_RAIL_A = "1"; // PLC ����A��ɨ���ָ�� a
														// "1\r\n"; // 20140119
														// ȥ�� \r\n
	private static final String PLC_OPERA_RAIL_B = "2"; // PLC ����B��ɨ���ָ�� b
														// "2\r\n"; // 20140119
														// ȥ�� \r\n
	private static final String[] RAIL_A_TO_PLC = { "0", "1", "2", "3", "4" }; // A
																				// �����PLCָ�
	private static final String[] RAIL_B_TO_PLC = { "5", "6", "7", "9", "8" }; // B
																				// �����PLCָ�

	private SMTSCModel smtScModel;
	private String[] params_A;
	private String[] params_B;
	private boolean isTestA = false; // ɨ��ǹ���Ա��
	private boolean isTestB = false;
	private static int reScanTimesA = 1;
	private static int reScanTimesB = 1;

	private static SimpleDateFormat dataFormat = new SimpleDateFormat(
			"hh:mm:ss"); // time format

	// ============== SharedPreferences default setting ======================
	public static String SCAN_COMMAND = "1B31"; // ɨ��ǹɨ��ָ��
	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // ɨ���ж�ָ��
	public static int SCAN_RESCAN = 2;
	public static String OWNER = "system";
	// ============== SharedPreferences default setting end ==================

	// ��Ϣ��������UI
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				analysisMessage("COM1", msg.obj.toString().trim()); // 20140119
																	// ȥ��\r\n
				break;
			case 2:
				analysisMessage("COM2", msg.obj.toString().trim()); // 20140119
																	// ȥ��\r\n
				break;
			case 3:
				analysisMessage("COM3", msg.obj.toString().trim()); // 20140119
																	// ȥ��\r\n
				break;
			}
			super.handleMessage(msg);
		}
	};

	// ��ʱ����
	private Handler handlerTimeOut = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4: // A��ɨ�賬ʱ
				if (reScanTimesA <= SCAN_RESCAN) {
					logDetails(scanInfoView, "A��� " + reScanTimesA + " ������ɨ��");
					startScan(PLC_OPERA_RAIL_A);
					reScanTimesA++;
				} else {
					logDetails(scanInfoView, "A��ɨ��ʧ��");
					reScanTimesA = 1;
					scanResultToPLC(false, "RailA", false);
				}
				break;
			case 5: // B��ɨ�賬ʱ
				if (reScanTimesB <= SCAN_RESCAN) {
					logDetails(scanInfoView, "B��� " + reScanTimesA + " ������ɨ��");
					startScan(PLC_OPERA_RAIL_B);
					reScanTimesB++;
				} else {

					logDetails(scanInfoView, "B��ɨ��ʧ��");
					reScanTimesB = 1;
					scanResultToPLC(false, "RailB", false);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void analysisMessage(String tty, String msg) {
		if (plcTTY.equals(tty)) { // ��ȡ������PLC����Ϣ������ɨ��ǹ
			if (SHOWINFO)
				logDetails(scanInfoView, "PLC��Ϣ: " + msg);
			if (msg.contains(PLC_OPERA_RAIL_A)) { // if(PLC_OPERA_RAIL_A.equals(msg)
													// ){ changed by ybj
				startScan(PLC_OPERA_RAIL_A);
			} else if (msg.contains(PLC_OPERA_RAIL_B)) {
				startScan(PLC_OPERA_RAIL_B);
			} else {
				logDetails(scanInfoView, "PLCָ���ȷ��");
			}
		} else if (mA1TTY.equals(tty)) { // ��ȡ������A�����Ϣ��ɨ����
			
			
			
			if (isTestA) {
				
				if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
					logDetails(scanInfoView, "A�����: ɨ�賬ʱ");

				} else {
					logDetails(scanInfoView, "A�����: " + msg + ";");

				}
				SystemClock.sleep(100);
				startScan(PLC_OPERA_RAIL_A);
				return;
			}
			if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
				if (SHOWINFO)
					logDetails(scanInfoView, "A��: ɨ�賬ʱ��");
				handlerTimeOut.sendEmptyMessage(4);
				return;
			}
			if (SHOWINFO)
				logDetails(scanInfoView, "A��: " + msg);
			reScanTimesA = 1;
			params_A[3] = msg;
			params_A[4] = "RailA";
			
			if (msg.equalsIgnoreCase(RailAlastSN)) //
			{   
				//10.24
				//super.logDetails(scanInfoView, "A���ظ�ɨ��");
				if (!RailASubmitting){
					scanResultToPLC(false, params_A[4], false);
				    if (SHOWINFO)
						logDetails(scanInfoView, "A��: �ظ�ɨ�����ύ");
				}
				
				return;
			}
			
			RailAlastSN = msg;
			RailASubmitting = true;
			
			commitToMes(params_A);
			

		} else if (mB1TTY.equals(tty)) { // ��ȡ������B�����Ϣ��ɨ����
			if (isTestB) {
				if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
					logDetails(scanInfoView, "B�����: ɨ�賬ʱ��");

				} else {
					logDetails(scanInfoView, "B�����: " + msg + ";");

				}
				SystemClock.sleep(100);
				startScan(PLC_OPERA_RAIL_B);
				return;
			}
			if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
				if (SHOWINFO)
					logDetails(scanInfoView, "B��: ɨ�賬ʱ");
				handlerTimeOut.sendEmptyMessage(5);
				return;
			}
			if (SHOWINFO)
				logDetails(scanInfoView, "B��: " + msg + ";");
			reScanTimesB = 1;
			params_B[3] = msg;
			params_B[4] = "RailB";
			if (msg.equalsIgnoreCase(RailBlastSN)) // ��ֹ�ظ��ύ 20131128 ybj
			{   //10.24
				//super.logDetails(scanInfoView, "B���ظ�ɨ��");
				if (!RailBSubmitting){
					scanResultToPLC(false, params_B[4], false);
				    if (SHOWINFO)
					logDetails(scanInfoView, "B��: �ظ�ɨ�����ύ");
				}
				return;
			}
			RailBSubmitting = true;
			RailBlastSN = msg;
			
			commitToMes(params_B);
		}
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
			// ������Ϣ����ջ���
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
	 * ʮ�������ַ���תByte[]
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
	 * ��ɨ��ǹɨ��
	 * 
	 * @param c
	 *            1��A��ɨ��ǹ��2��B��ɨ��ǹ
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

	// Rail A: c = {"0","1","2","3","4"}
		// Rail B: c = {"5","6","7","9","8"}
	public void sendCommandToPLC(boolean resToPlc, String[] cToPlc,boolean stop) {
		    String[] comToPlc = new String[3]; // PLC����ָ������
		    if (resToPlc) {
		        comToPlc[0] = cToPlc[1];
		        comToPlc[1] = cToPlc[2];
		    } else {
		        if (checkBox_warn.isChecked()) {
		        //  comToPlc[0] = cToPlc[0];
		           comToPlc[0] = cToPlc[3] + plcWarnTime;
		           if ( !(plcManual || stop) ) // �˹���Ԥ �� STOP Ϊ�棬�����󲻴��ͣ�ֹͣ�ȴ���Ա����  ybj 2014-03-07
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
		               SystemClock.sleep(Integer.parseInt(plcWarnTime) * 1000); //����ʱ��
		           } else {
		               SystemClock.sleep(150); // ����ÿ��ָ��ͼ������100ms
		           }
		           lock.lock();  // add by ybj ��ֹ�����߳�ͬʱ����PLC���ڡ�
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
	 * ����ɨ����������ָ���PLC
	 * 
	 * @param result
	 *            ɨ����
	 * @param rail
	 *            ��ҪPLC�����Ĺ��
	 */
	public void scanResultToPLC(boolean result, String rail, final boolean stop) {
		if (isRailA(rail)) { // A ��
			Log.d(TAG, "scanResultToPLC: control PLC via Rail A");
			if (SHOWINFO)
				logDetails(scanInfoView, "A�췢��ָ�PLCȥ");
			resultToPlc = result;
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendCommandToPLC(resultToPlc, RAIL_A_TO_PLC, stop);
				}
			}).start();

		} else if (isRailB(rail)) { // B��
			Log.d(TAG, "scanResultToPLC: control PLC via Rail B");
			if (SHOWINFO)
				logDetails(scanInfoView, "B�췢��ָ�PLCȥ");
			resultToPlc = result;
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendCommandToPLC(resultToPlc, RAIL_B_TO_PLC, stop);
				}
			}).start();

		} else {
			if (SHOWINFO)
				logDetails(scanInfoView, "�ֶ�ɨ�����");
			Log.i(TAG, "scanResultToPLC: Manual scan finish.");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtstick_old);

		setDefaultValues();
		initView();
		init();

		registerPorts();

		params_A = new String[] { "", "", "", "", "", "" };
		params_B = new String[] { "", "", "", "", "", "" };
		params_A[2] = OWNER;
		params_B[2] = OWNER;
		logDetails(scanInfoView, "��ǰʹ���ߣ�" + OWNER);

	}

	private void setDefaultValues() {
		SharedPreferences pref = getSharedPreferences("smtstick_setting",
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		if (!pref.contains("setting")) { // ��һ�����У���ʼ��Ĭ��ֵ
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
	 * ע�ᴮ��
	 * 
	 * @param tty
	 *            ����·��
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
					SerialPort.STOPBITS_1, PARITY); // ���ӿ���ѡ����żУ���� ybj

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
	 * ����ѡ��ÿ�鴮����2���ڣ�д0ѡ��A���ڣ�д1ѡ��B���ڡ�
	 * 
	 * @param portPath
	 *            ������ TTYS0_FILE, TTYS1_FILE,TTYS2_FILE
	 * @param subPort
	 *            �Ӵ��ڱ�� 0 or 1
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
	 * ��ȡ��ǰʹ�õĴ��ڣ�0ΪA���ڣ�1ΪB���ڣ�Ĭ��Ϊ0
	 * 
	 * @param portPath
	 *            ������ TTYS0_FILE, TTYS1_FILE,TTYS2_FILE
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
		
		
		checkBox_separate=(CheckBox) findViewById(R.id.OldcheckBox_separate);
		

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
		spinner_plc_tty = (Spinner) findViewById(R.id.Oldspinner_tty);
		spinner_plc_tty.setAdapter(adapterTTY);
		spinner_plc_tty.setSelection(0);
		spinner_plc_tty.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapterBit = ArrayAdapter
				.createFromResource(this, R.array.SMTStick_bit,
						android.R.layout.simple_spinner_item);
		adapterBit
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_plc_baudrate = (Spinner) findViewById(R.id.Oldspinner_baudrate);
		spinner_plc_baudrate.setAdapter(adapterBit);
		spinner_plc_baudrate.setSelection(2);
		spinner_plc_baudrate.setOnItemSelectedListener(this);

		spinner_A1_tty = (Spinner) findViewById(R.id.Oldspinner_A1_tty);
		spinner_A1_tty.setAdapter(adapterTTY);
		spinner_A1_tty.setSelection(1);
		spinner_A1_tty.setOnItemSelectedListener(this);
		spinner_A1_baudrate = (Spinner) findViewById(R.id.Oldspinner_A1_baudrate);
		spinner_A1_baudrate.setAdapter(adapterBit);
		spinner_A1_baudrate.setSelection(2);
		spinner_A1_baudrate.setOnItemSelectedListener(this);

		spinner_A2_baudrate = (Spinner) findViewById(R.id.Oldspinner_A2_baudrate);
		spinner_A2_baudrate.setAdapter(adapterBit);
		spinner_A2_baudrate.setSelection(2);
		spinner_A2_baudrate.setOnItemSelectedListener(this);

		spinner_B1_tty = (Spinner) findViewById(R.id.Oldspinner_B1_tty);
		spinner_B1_tty.setAdapter(adapterTTY);
		spinner_B1_tty.setSelection(2);
		spinner_B1_tty.setOnItemSelectedListener(this);
		spinner_B1_baudrate = (Spinner) findViewById(R.id.Oldspinner_B1_baudrate);
		spinner_B1_baudrate.setAdapter(adapterBit);
		spinner_B1_baudrate.setSelection(2);
		spinner_B1_baudrate.setOnItemSelectedListener(this);

		spinner_B2_baudrate = (Spinner) findViewById(R.id.Oldspinner_B2_baudrate);
		spinner_B2_baudrate.setAdapter(adapterBit);
		spinner_B2_baudrate.setSelection(2);
		spinner_B2_baudrate.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> SMTStickPCBType = ArrayAdapter
				.createFromResource(this, R.array.SMTStickPCBType,
						android.R.layout.simple_spinner_item);
		SMTStickPCBType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_A_Side = (Spinner) findViewById(R.id.Oldspinner_A_Side);
		spinner_A_Side.setAdapter(SMTStickPCBType);
		spinner_A_Side.setSelection(0);
		spinner_A_Side.setOnItemSelectedListener(this);

		spinner_B_Side = (Spinner) findViewById(R.id.Oldspinner_B_Side);
		spinner_B_Side.setAdapter(SMTStickPCBType);
		spinner_B_Side.setSelection(0);
		spinner_B_Side.setOnItemSelectedListener(this);

		Spinner_PLC_PARITY = (Spinner) findViewById(R.id.OldSpinner_PLC_PARITY);
		Spinner_PLC_PARITY.setAdapter(adapterParity);
		Spinner_PLC_PARITY.setSelection(0);
		Spinner_PLC_PARITY.setOnItemSelectedListener(this);
		// set warning time adapter
		adapterBit = ArrayAdapter.createFromResource(this,
				R.array.SMTStick_warn_time,
				android.R.layout.simple_spinner_item);
		adapterBit
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_warntime = (Spinner) findViewById(R.id.Oldspinner_warntime);
		spinner_warntime.setAdapter(adapterBit);
		spinner_warntime.setOnItemSelectedListener(this);

		scanInfoView = (EditText) findViewById(R.id.OldEdit_SMTStickInfo);
		scanInfoView.setText("");

		checkBox_manual = (CheckBox) findViewById(R.id.OldcheckBox_hand);
		checkBox_manual.setOnCheckedChangeListener(checkListener);
		checkBox_warn = (CheckBox) findViewById(R.id.OldcheckBox_warn);
		checkBox_warn.setOnCheckedChangeListener(checkListener);

		checkBox_A1 = (CheckBox) findViewById(R.id.OldcheckBox_A1);
		checkBox_A1.setOnCheckedChangeListener(checkListener);

		checkBox_A2 = (CheckBox) findViewById(R.id.OldcheckBox_A2);
		checkBox_A2.setOnCheckedChangeListener(checkListener);
		checkBox_B1 = (CheckBox) findViewById(R.id.OldcheckBox_B1);
		checkBox_B1.setOnCheckedChangeListener(checkListener);
		checkBox_B2 = (CheckBox) findViewById(R.id.OldcheckBox_B2);
		checkBox_B2.setOnCheckedChangeListener(checkListener);
		checkBox_A1.setChecked(true);

		textView_warnTime = (TextView) findViewById(R.id.OldtextView_warntime);
		textView_warnTime.setFocusable(true);
		textView_warnTime.setFocusableInTouchMode(true);
		textView_warnTime.requestFocus();
		// �ֶ�ɨ��
		editText_sn = (EditText) findViewById(R.id.OldeditText_sn);
		editText_sn.clearFocus();
		editText_sn.setHint("����SN");

		button_scan = (Button) findViewById(R.id.Oldbutton_scan);
		button_scan.setOnClickListener(this);
		// control button
		// button_edit = (Button) findViewById(R.id.OldButton_edit);
		button_ok = (Button) findViewById(R.id.OldButton_ok);
		button_test = (Button) findViewById(R.id.OldButton_test);
		// button_edit.setOnClickListener(this);
		button_ok.setOnClickListener(this);
		button_test.setOnClickListener(this);

		checkshowInfo = (CheckBox) findViewById(R.id.Oldcheck_showInfo);
		checkshowInfo.setOnClickListener(this);
		smtScModel = new SMTSCModel();

		updateView(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
		case R.id.OldButton_ok:
			if (plcTTY == mA1TTY || plcTTY == mB1TTY || mA1TTY == mB1TTY) {
				showDialog(10010);
			} else {
				// ���û��ѡ�������򲻴������루�ֶ�ɨ����⣩
				if (checkBox_A1.isChecked() || checkBox_A2.isChecked()
						|| checkBox_A1.isChecked() || checkBox_A2.isChecked()) {
					canScan = true;
				} else {
					canScan = false;
					if (DEBUG)
						Log.d(TAG, "need select a rail");
				}
				updateView(false);
				// ��port
				SerialPortToPLC = portSelector(plcTTY);
				SerialPortToA = portSelector(mA1TTY);
				SerialPortToB = portSelector(mB1TTY);

				updatePortParams(SerialPortToPLC,
						Integer.parseInt(plcBaudrate), m_plc_parity);
				switchPort(portPathSelector(plcTTY), 0); // ����PLC��subPort = 0
				if (checkBox_A1.isChecked()) {
					updatePortParams(SerialPortToA,
							Integer.parseInt(mA1Baudrate), 0);
					switchPort(portPathSelector(mA1TTY), 0);
				} else if (checkBox_A2.isChecked()) {
					updatePortParams(SerialPortToA,
							Integer.parseInt(mA2Baudrate), 0);
					switchPort(portPathSelector(mA1TTY), 1);
				}
				if (checkBox_B1.isChecked()) {
					updatePortParams(SerialPortToB,
							Integer.parseInt(mB1Baudrate), 0);
					switchPort(portPathSelector(mB1TTY), 0);
				} else if (checkBox_B2.isChecked()) {
					updatePortParams(SerialPortToB,
							Integer.parseInt(mB2Baudrate), 0);
					switchPort(portPathSelector(mB1TTY), 1);
				}

				if (DEBUG)
					showSetValue();
				
//				if(spinner_A_Side.getSelectedItem().toString().equals("A")&&spinner_B_Side.getSelectedItem().toString().equals("A")){
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setTitle("����ģʽ����ע�⣡");
//					builder.setMessage("��ѡ��A/B���ͬʱ��ƬA�棬�Ƕ�����Ƭģʽ��"    );
//					builder.setPositiveButton("ȷ��",new  OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							isseparate_type=true;
//						}
//					});
//					builder.setNegativeButton("ȡ��", null);
//					builder.create().show();
//				}
//				isseparate_type=false;
				if(checkBox_separate.isChecked())
					isseparate_type=true;
				else
					isseparate_type=false;
				
			}
			break;
		case R.id.Oldbutton_scan:
			input_sn = editText_sn.getText().toString().trim();
			if (input_sn.length() > 0) {
				if (LastSN.equalsIgnoreCase(input_sn)) {

					super.logDetails(scanInfoView, "�ظ�ɨ��");
					return;
				}
				showDialog(10086);
				LastSN = input_sn;
			}
			break;
		case R.id.OldButton_test:
			if (!checkBox_A1.isChecked() && !checkBox_A2.isChecked()
					&& !checkBox_A1.isChecked() && !checkBox_A2.isChecked()) {

				logDetails(scanInfoView, "û��ѡ����Ե�ɨ��ǹ��");

				return;
			}
			String btstr = button_test.getText().toString().trim();
			if ("����ɨ��ͷ".equals(btstr)) {
				showDialog(10011);
			} else {

				if (isTestA)
					isTestA = false;
				if (isTestB)
					isTestB = false;
				button_test.setText("����ɨ��ͷ");
				// button_edit.setEnabled(true);
				button_scan.setEnabled(true);
				logDetails(scanInfoView, "ֹͣ����");

			}
			break;
		case R.id.Oldcheck_showInfo:
			if (checkshowInfo.isChecked())
				SHOWINFO = true;
			else
				SHOWINFO = false;
			break;
		}
	}

	public void onResume() {
		super.onResume();
		/*
		 * String btstr = button_test.getText().toString().trim();
		 * if(!"����ɨ��ͷ".equals(btstr)){ if(isTestA) isTestA = false; if(isTestB)
		 * isTestB = false; button_test.setText("����ɨ��ͷ");
		 * button_edit.setEnabled(true); button_scan.setEnabled(true);
		 * showInfo("ֹͣ����"); }
		 */
	}

	/**
	 * subPort�ļ�ѡ����
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
	 * SerialPort ѡ����������ѡ��Ĵ��ں�(COM1,COM2,COM3)����serialPort_TTYSx ��SerialPortTox
	 * 
	 * @param str
	 *            UI��ѡ��Ĵ��ں�
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

		String str = "�Ӳ�̨���� " + plcTTY + ", �����ʣ�"
				+ SerialPortToPLC.getBaudRate();
		if (checkBox_warn.isChecked()) {
			str += ",����ʱ�䣺" + plcWarnTime + "��";
		}
		if (checkBox_A1.isChecked()) {
			str += "\nA��ѡ��ɨ����1������" + mA1TTY + ", �����ʣ�"
					+ SerialPortToA.getBaudRate();
		} else if (checkBox_A2.isChecked()) {
			str += "\nA��ѡ��ɨ����2������" + mA1TTY + ", �����ʣ�"
					+ SerialPortToA.getBaudRate();
		}
		if (checkBox_B1.isChecked()) {
			str += "\nB��ѡ��ɨ����1������" + mB1TTY + ", �����ʣ�"
					+ SerialPortToB.getBaudRate();
		} else if (checkBox_B2.isChecked()) {
			str += "\nB��ѡ��ɨ����2������" + mB1TTY + ", �����ʣ�"
					+ SerialPortToB.getBaudRate();
		}

		logDetails(scanInfoView, str);
	}

	public void updateView(boolean enable) {
		if (DEBUG)
			Log.d(TAG, "updateView");
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
		// setEnable(button_edit, !enable);
		setEnable(button_ok, enable);
		setEnable(button_test, !enable);
		setEnable(button_scan, !enable);
		setEnable(editText_sn, !enable);
		setEnable(checkBox_A1, enable);
		setEnable(checkBox_A2, enable);
		setEnable(checkBox_B1, enable);
		setEnable(checkBox_B2, enable);
		// setEnable(checkBox_warn, enable);
		setEnable(checkBox_manual, enable);
		setEnable(spinner_A_Side, enable);
		setEnable(spinner_B_Side, enable);
		setEnable(checkBox_separate, enable);

	}

	public OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
			int id = checkBox.getId();
			switch (id) {
			case R.id.OldcheckBox_hand:
				if (isChecked) {
					plcManual = true;
				} else {
					plcManual = false;
				}
				break;
			case R.id.OldcheckBox_warn:
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
			case R.id.OldcheckBox_A1:
				if (checkBox_A2.isChecked()) {
					checkBox_A2.setChecked(!isChecked);
				}
				checkBox_A1.setChecked(isChecked);
				break;
			case R.id.OldcheckBox_A2:
				if (checkBox_A1.isChecked()) {
					checkBox_A1.setChecked(!isChecked);
				}
				checkBox_A2.setChecked(isChecked);
				break;
			case R.id.OldcheckBox_B1:
				if (checkBox_B2.isChecked()) {
					checkBox_B2.setChecked(!isChecked);
				}
				checkBox_B1.setChecked(isChecked);
				break;
			case R.id.OldcheckBox_B2:
				if (checkBox_B1.isChecked()) {
					checkBox_B1.setChecked(!isChecked);
				}
				checkBox_B2.setChecked(isChecked);
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
		case R.id.Oldspinner_tty:
			plcTTY = spinner_plc_tty.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_baudrate:
			plcBaudrate = spinner_plc_baudrate.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_warntime:
			plcWarnTime = spinner_warntime.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_A1_baudrate:
			mA1Baudrate = spinner_A1_baudrate.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_A1_tty:
			mA1TTY = spinner_A1_tty.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_A2_baudrate:
			mA2Baudrate = spinner_A2_baudrate.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_B1_baudrate:
			mB1Baudrate = spinner_B1_baudrate.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_B1_tty:
			mB1TTY = spinner_B1_tty.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_B2_baudrate:
			mB2Baudrate = spinner_B2_baudrate.getSelectedItem().toString();
			break;
		case R.id.OldSpinner_PLC_PARITY:
			m_plc_parity = Spinner_PLC_PARITY.getSelectedItemPosition();
			break;
		case R.id.Oldspinner_A_Side:
			params_A[5] = spinner_A_Side.getSelectedItem().toString();
			break;
		case R.id.Oldspinner_B_Side:
			params_B[5] = spinner_B_Side.getSelectedItem().toString();
			break;
		}
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
			d = new AlertDialog.Builder(this).setTitle("����ѡ���ظ�")
					.setMessage("����ѡ���ظ��ˣ����ٴ�ȷ�ϣ�").setNegativeButton("ȷ��", null)
					.show();
			break;
		case 10011:
			d = new AlertDialog.Builder(this)
					.setTitle("��ʼ����ɨ��ǹ")
					.setMessage("ȷ��Ҫ��ʼ����ɨ��ǹ��\n�ٴε��ֹͣɨ��")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									if (checkBox_A1.isChecked()
											|| checkBox_A2.isChecked()) {
										isTestA = true;
										logDetails(scanInfoView, "����A��ɨ��ǹ");
										startScan(PLC_OPERA_RAIL_A);
										SystemClock.sleep(100);
									}
									if (checkBox_B1.isChecked()
											|| checkBox_B2.isChecked()) {
										isTestB = true;

										logDetails(scanInfoView, "����B��ɨ��ǹ");
										startScan(PLC_OPERA_RAIL_B);
									}
									button_test.setText("ɨ��ͷ������...");
									// button_edit.setEnabled(false);
									button_scan.setEnabled(false);
								}
							}).setNegativeButton("ȡ��", null).show();
			break;
		case 10086:
			d = new AlertDialog.Builder(this)
					.setTitle("��ȷ�������SN��")
					.setMessage("SN��")
					.setPositiveButton("�ύ",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									manualScan = true;
									params_A[3] = input_sn;
									params_A[4] = "Manual";
									commitToMes(params_A);
								}
							}).setNegativeButton("ȡ��", null).show();
			break;
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
			builder2.setTitle("���趨��");
			final EditText password = (EditText) loginForm
					.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("ȷ��",
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
			builder2.setNegativeButton("ȡ��", null);
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
				.setTitle("ȷ���˳�SMT��Ƭɨ����?")
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
	 * �ύɨ�����MES
	 * 
	 * @param params
	 *            ���������Ϣ
	 */
	private void commitToMes(String[] par) {
		
		String[] p = par.clone();
		Task mScan = new Task(SMTStick_Old.this, TaskType.SMT_SC, p);
		if(!isseparate_type){
			progressDialog.setMessage("SMT��Ƭɨ��");
			ClearshowInfo(); // ����100�������
			showProDia();
			smtScModel.SMTSC(mScan);
		}
		else{
			progressDialog.setMessage("SMT����ģʽ����Ƭɨ�裡��");
			ClearshowInfo(); // ����100�������
			showProDia();
			smtScModel.SMTSC_2(mScan);
		}
	}
    
	
	
	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.SMT_SC;
		super.init();
		super.progressDialog.setMessage("SMT��Ƭɨ��");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task) {

		super.refresh(task);
		String rail = "";
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		closeProDia();
		if (TaskType.SMT_SC == task.getTaskType()) {
			if (task.getTaskResult() != null) {
				
				getdata = (HashMap<String, String>) task.getTaskResult();
				//���޸� �жϵõ��Ľ����ȡ����ERROR����ֵ
				if(getdata.get("ERROR")==null){
					rail = getdata.get("Rail").toString();
					if (isRailA(rail)) {
						// A ��
						if (SHOWINFO)
							logDetails(scanInfoView, "A���ύMES���");
						resultFromMes(getdata, rail);
						RailASubmitting = false;

					} else if (isRailB(rail)) {
						// B��
						if (SHOWINFO)
							logDetails(scanInfoView, "B���ύMES���");
						resultFromMes(getdata, rail);
						RailBSubmitting = false;

					} else {
						// �ֶ�����SN
						if (SHOWINFO)
							logDetails(scanInfoView, "�ֶ��ύMES���");
						resultFromMes(getdata, rail);
					}
				}
				else{
					//getdata.get("ERROR")��ֵ
					if (SHOWINFO)
						logDetails(scanInfoView, "MES�쳣�����"+getdata.get("ERROR"));
				}
				
			}
			/**************************9.27xiugai************************************/
			else{
				if (SHOWINFO)
					logDetails(scanInfoView, "task.getTaskResult()==null");
			}
			/**************************9.27xiugai************************************/
		}
	}

	public void resultFromMes(HashMap<String, String> data, String rail) {
		boolean isSuccess;

		if (SHOWINFO)
			logDetails(scanInfoView, data.toString());

		if (data.get("Error") == null) {
			if (data.get("result").equals("0")) {
				// �洢�ɹ�
				isSuccess = true;
				if (!SHOWINFO && data.containsKey("ReturnMsg"))
					logDetails(scanInfoView, "Success_Msg:[" + rail + "]: "
							+ data.get("ReturnMsg"));
			} 
			//������ؽ��Ϊ-2���ж�
			else if (data.get("result").equals("-2")) {
				// �洢�ɹ�
				isSuccess = false;
				plcManual=true;
				if (!SHOWINFO && data.containsKey("ReturnMsg"))
					logDetails(scanInfoView, "ǿ�ƿ���:[" + rail + "]: "
							+ data.get("ReturnMsg"));
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("����©ɨ�裬��ע�⣡");
				builder.setMessage("    ��˶�������Ϣ��"+data.get("ReturnMsg"));
				builder.setPositiveButton("ȷ��",null);
				builder.setNegativeButton("ȡ��", null);
				builder.create().show();
			} 
			
			else {
				isSuccess = false;
				if (!SHOWINFO && data.containsKey("ReturnMsg"))
					logDetails(scanInfoView,
							"[" + rail + "]: " + data.get("ReturnMsg"));
			}

		} else {
			isSuccess = false;
				logDetails(scanInfoView, "MES �����쳣: " + data.get("Error"));

		}
		scanResultToPLC(isSuccess, rail, false);
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
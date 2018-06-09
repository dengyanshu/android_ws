package com.zowee.mes.eventlistener;

import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class MySerialEventListener implements SerialPortEventListener {
    private  Handler  handler;
    private  SerialPort  sp;
    private  int  handlerwhat;
	public MySerialEventListener( Handler handler,int handlerwhat,SerialPort  sp) {
			this.handler=handler;
			this.sp=sp;
			this.handlerwhat=handlerwhat;
	}
	public void serialEvent(SerialPortEvent ev) {
		int type = ev.getEventType();
		switch (type) {
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.BI:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			Message.obtain(handler, handlerwhat, readMessage()).sendToTarget();
	
		break;
		}
	}
	 
	

		public String readMessage() {
			String sReadBuff = "";
			int i;
			byte[] readBuffer = new byte[512];
			int numBytes;
			try {
				while (sp.getInputStream().available() > 0) {
					SystemClock.sleep(50);
					numBytes = sp.getInputStream().read(readBuffer);
					String tmpR = new String(readBuffer, 0, numBytes, "UTF-8");
					sReadBuff += tmpR;
				}
				
			} catch (IOException e) {
			}
			return sReadBuff;
	  }
}

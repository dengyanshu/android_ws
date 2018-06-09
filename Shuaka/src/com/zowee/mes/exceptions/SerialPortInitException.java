package com.zowee.mes.exceptions;

/**
 * @author Administrator
 * @description 串口初始化异常
 */
public class SerialPortInitException extends Exception {

	public SerialPortInitException() {
		super();
	}

	public SerialPortInitException(String excepMsg) {
		super(excepMsg);
	}

}

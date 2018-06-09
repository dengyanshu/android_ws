package cn.chouchou;

import java.util.List;

public class Keybox {
	
	private  int  keyboxNum;
	private  String deviceId;
	private   List<Key> keys;
	public int getKeyboxNum() {
		return keyboxNum;
	}
	public void setKeyboxNum(int keyboxNum) {
		this.keyboxNum = keyboxNum;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public List<Key> getKeys() {
		return keys;
	}
	public void setKeys(List<Key> keys) {
		this.keys = keys;
	}
	
	

}

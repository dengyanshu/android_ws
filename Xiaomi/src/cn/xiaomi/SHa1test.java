package cn.xiaomi;

import org.apache.commons.codec.digest.DigestUtils;

public class SHa1test {
	

	 //�������ᵽ�Ĳ���appid��data��key�м���/���ӣ�Ȼ����SHA1���ܣ�Ȼ��Ӽ��ܺ���ַ����ĵ���λ��ʼ��ȡ24λ��תΪСд����Ϊtoken��
   ///88fc9bc60f2f84da5ed4fc3779444116ee4266f5 
	//fc9bc60f2f84da5ed4fc3779
	//e8100363b1625be7dffa1d20
   public static void main(String[] args) {
	   String  source_str="MISRM_PRD/eyJMT0dfQ1VSUkVOVF9QQUdFIjoiMSIsIkxPR19QRVJfUEFHRV9DT1VOVCI6IjEwMDAwMCIsIlBSRF9CRUdJTl9EVCI6IjIwMTgwMzIwIiwiUFJEX0VORF9EVCI6IjIwMTgwMzIxIn0=/czx6p1ab0chkjrzc";
	  //MISRM_PRD/eyJMT0dfQ1VSUkVOVF9QQUdFIjoiMSIsIkxPR19QRVJfUEFHRV9DT1VOVCI6IjEwMDAwMCIsIlBSRF9CRUdJTl9EVCI6IjIwMTcxMTIwIiwiUFJEX0VORF9EVCI6IjIwMTcxMTIwIn0=eyJMT0dfQ1VSUkVOVF9QQUdFIjoiMSIsIkxPR19QRVJfUEFHRV9DT1VOVCI6IjEwMDAwMCIsIlBSRF9CRUdJTl9EVCI6IjIwMTcxMTIwIiwiUFJEX0VORF9EVCI6IjIwMTcxMTIwIn0=/czx6p1ab0chkjrzc
	   String res=DigestUtils.shaHex(source_str).substring(2, 26);
	  System.out.println(res);
   }
}




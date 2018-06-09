package cn.mi;

//import com.google.common.base.Charsets;
//import com.squareup.okhttp.*;
//import com.xiaomi.miui.deviceProtect.constant.Constants;
//import com.xiaomi.miui.deviceProtect.model.FactoryDpRequest;
//import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
//import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

public class DeviceProtectServerTest {

    /** 测试工厂加密数据接口 **/
    @Test
    public void testFactoryDeviceProtectApi() {
        // staging
       //String sid = "miFactory1";
      //String salt = "RW8yzD7etkAqh309A";

        // preview
        String sid = "miFactory1";
        String salt = "MetbItDqQICkzOdwyoszDbcseoE5V2Nw";
        long timestamp = System.currentTimeMillis();
        //OkHttpClient client = new OkHttpClient();

        String nonce = UUID.randomUUID().toString();//"a55e3a37-3dac-4443-bd47-d8560325a570";//
       // JSONObject deviceDataObj = new JSONObject();
//        deviceDataObj.put("product", "polaris");
//        deviceDataObj.put("cpuId", "0xd40a022d");
//        deviceDataObj.put("imei1", "867252030043904");
//        deviceDataObj.put("imei2", "867252030043912");
//        deviceDataObj.put("meid", "9900096963121412");
//        deviceDataObj.put("wifiMac", "04B167A77F02");
//        deviceDataObj.put("btMac", "04B1677F7F02");
//        deviceDataObj.put("emmcId", "01234567899");                           // emmc id. adb shell cat /sys/class/block/mmcblk0/device/serial
//        deviceDataObj.put("ufsId", "0123456789");                            // ufs id. adb shell cat /d/ufshcd0/dump_string_desc_serial
//        deviceDataObj.put("fpUid", "00000000-00000000-00000000-00");         // Fingerprint uid.  adb shell getprop persist.sys.fp.uid


        //String encodedFlashData = Base64.encodeBase64String(deviceDataObj.toString().getBytes());

        String joinedStr = new StringBuilder().append("POST\n").append("/factory/encrypt/deviceInfo\n").append("data=").append(encodedFlashData)
                .append("&nonce=").append(nonce).append("&sid=").append(sid).append("&timestamp=").append(timestamp).toString();
        String sign = HmacUtils.hmacSha1Hex(salt, joinedStr);

       // RequestBody formBody = new FormEncodingBuilder().add("sid", sid).add("data", encodedFlashData).
                add("nonce", nonce).add("sign", sign).add("timestamp", String.valueOf(timestamp)).build();
       // Request deviceProtectRequest = new Request.Builder().url("http://protect.dev.sec.miui.com/factory/encrypt/deviceInfo").post(formBody).build();
        try {
            //Response deviceProtectResponse = client.newCall(deviceProtectRequest).execute();
            if (deviceProtectResponse.isSuccessful()) {
                String rawResult = deviceProtectResponse.body().string();
                //JSONObject jsonRresult= JSONObject.fromObject(new String(Base64.decodeBase64(rawResult), Charsets.UTF_8));
                if (jsonRresult != null && (jsonRresult.optInt("code", -1) == 0)) {
                	System.out.println(jsonRresult.optString("result"));
                } else {
                    // Logic error, check parameters.
                }
            } else {
                // Network or server exception. retry.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFactoryDeviceVeriftyPubKeySign() {
    }

    @Test
    public void testDeviceProtectRequest() {
        FactoryDpRequest request = new FactoryDpRequest();

        request.setProduct("msm8992");
        request.setImei1("864141039627945");
        request.setImei2("864141039627946");
        request.setBtMac("123456");
        request.setMeid("0987654321");
        request.setCpuId("0xa8bb262d");
        request.setWifiMac("123456789");

        byte[] retBytes = request.getDeviceInfoStringV1();
        String ret = Hex.encodeHexString(retBytes);
         System.out.println(ret);
    }

    @Test
    public void TestIntToByte() {
        int totalLen = 256;
        byte[] dataHeadBytes = new byte[4];
        int index = 0;
        dataHeadBytes[index++] = 0;
        dataHeadBytes[index++] = Constants.DP_RET_DATA_VERSION;
        dataHeadBytes[index++] = (byte)((totalLen  >> 8) & 0xff);    // 高8位
        dataHeadBytes[index++] = (byte)(totalLen & 0xff);            // 低8为

        String ret = Hex.encodeHexString(dataHeadBytes);
        System.out.println(ret);
    }

    @Test
    public void Base64Decode() {
        String base64Str = "QVFBQUFFQTg0U29lR1JSY3ZtSHZPS2F3SmtFWnJsdDE=";

        byte[] decodeBytes = Base64.decodeBase64(base64Str);

        String HexStr = Hex.encodeHexString(decodeBytes);
         System.out.println(HexStr);
    }
}

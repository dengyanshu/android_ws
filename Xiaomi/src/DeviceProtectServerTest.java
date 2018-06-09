
/*import com.google.common.base.Charsets;
import com.squareup.okhttp.*;
import com.xiaomi.miui.deviceProtect.constant.Constants;
import com.xiaomi.miui.deviceProtect.model.FactoryDpRequest;*/
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class DeviceProtectServerTest {
    //private static Logger logger = LoggerFactory.getLogger(DeviceProtectServerTest.class);
    //sid=miFactory1, data=eyJwcm9kdWN0Ijoic2lyaXVzIiwiY3B1SWQiOiIxMjM0NTY3ODg3NjU0MzIxIiwiaW1laTEiOiI4NjIzMzcwMjEzNDYxMjAiLCJpbWVpMiI6Ijg2MjMzNzAyMTM1NTQwMiIsIm1laWQiOiIxMjM0NTY3ODY1NDMyNCIsIndpZmlNYWMiOiIwRVNEMjNSRjRZM0siLCJidE1hYyI6IjBCNjY3ODVUN0Q1MyIsImVtbWNJZCI6IjAxMjM0NTY3ODk5IiwidWZzSWQiOiIwMTIzNDU2Nzg5IiwiZnBVaWQiOiIwMDAwMDAwMC0wMDAwMDAwMC0wMDAwMDAwMC0wMCJ9, timestamp=1521603199480, sign=d63aa9510927eed318c35587b7d49747ce740f10, nonce=45f33398-7fbd-4f02-b6a4-f8fa967d3f0a
    /** 测试工厂加密数据接口 **/
    @Test
    public void testFactoryDeviceProtectApi() {
        String sid = "miFactory1";
        String salt = "MetbItDqQICkzOdwyoszDbcseoE5V2Nw";
        long timestamp = 1521536575879L;//System.currentTimeMillis()
        String nonce ="0244e823-2872-45df-8439-3b823a36a204";// UUID.randomUUID().toString();
        
        //OkHttpClient client = new OkHttpClient();

        JSONObject deviceDataObj = new JSONObject();
        deviceDataObj.put("product", "cactus");
        deviceDataObj.put("cpuId", "1234567887654321");
        deviceDataObj.put("imei1", "862337021346120");
        deviceDataObj.put("imei2", "862337021355402");
        deviceDataObj.put("meid", "12345678654324");
        deviceDataObj.put("wifiMac", "0ESD23RF4Y3K");
        deviceDataObj.put("btMac", "0B66785T7D53");
        deviceDataObj.put("emmcId", "01234567899");                          // emmc id. adb shell cat /sys/class/block/mmcblk0/device/serial
        deviceDataObj.put("ufsId", "0123456789");                            // ufs id. adb shell cat /d/ufshcd0/dump_string_desc_serial
        deviceDataObj.put("fpUid", "00000000-00000000-00000000-00");         // Fingerprint uid.  adb shell getprop persist.sys.fp.uid
        String encodedFlashData = Base64.encodeBase64String(deviceDataObj.toString().getBytes());
        //logger.debug("deviceDataObjData= {}", deviceDataObj);
        System.out.println("encodedFlashData="+encodedFlashData);

        String joinedStr = new StringBuilder().append("POST\n").append("/factory/encrypt/deviceInfo\n").append("data=").append(encodedFlashData)
                .append("&nonce=").append(nonce).append("&sid=").append(sid).append("&timestamp=").append(timestamp).toString();
        System.out.println("joinedStr="+joinedStr);
      
        String sign = HmacUtils.hmacSha1Hex(salt, joinedStr);
        HmacUtils.hmacSha256(key, valueToDigest) //key  data
        System.out.println(sign);
        
      /*  RequestBody formBody = new FormEncodingBuilder().add("sid", sid).add("data", encodedFlashData).add("nonce", nonce).add("sign", sign).build();
        Request deviceProtectRequest = new Request.Builder().url("https://protect.dev.sec.miui.com/factory/encrypt/deviceInfo").post(formBody).build();
        try {
            Response deviceProtectResponse = client.newCall(deviceProtectRequest).execute();
            if (deviceProtectResponse.isSuccessful()) {
                String rawResult = deviceProtectResponse.body().string();
                System.out.println(new String(Base64.decodeBase64(rawResult));
                //logger.debug("testFactoryDeviceProtectApi:result= {}", , Charsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}

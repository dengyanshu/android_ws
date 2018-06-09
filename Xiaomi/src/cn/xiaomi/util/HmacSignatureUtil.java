package cn.xiaomi.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.net.util.Base64;

public class HmacSignatureUtil {
	// private static final Logger LOGGER = LoggerFactory.getLogger(HmacSignatureUtil.class);

	    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

	    public static String signByHmacSha256(String key, String content) throws Exception {
	        byte[] byteKeys = Base64.decodeBase64(key);
	        return signByHmacSha256(byteKeys, content);
	    }

	    public static String signByHmacSha256(byte[] key, String content) throws Exception {
	        return signByHmacSha256(key, content.getBytes());
	    }

	    public static String signByHmacSha256(byte[] key, byte[] content) throws Exception {
	            SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA256_ALGORITHM);

	            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
	            mac.init(signingKey);

	            byte[] rawHmac = mac.doFinal(content);
	            return Base64.encodeBase64String(rawHmac, false);
	        
	    }

}

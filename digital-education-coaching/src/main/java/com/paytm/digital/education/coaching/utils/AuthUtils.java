package com.paytm.digital.education.coaching.utils;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class AuthUtils {

    private static final Logger log = LoggerFactory.getLogger(AuthUtils.class);

    private static final String UTF8_CHARSET          = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public String getSignature(String message, String apiSecretKey) {
        try {
            byte[] secretyKeyBytes = apiSecretKey.getBytes(UTF8_CHARSET);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] data = message.getBytes(UTF8_CHARSET);
            byte[] rawHmac = mac.doFinal(data);
            return new String(Base64.getEncoder().encode(rawHmac));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException ex) {
            log.error(
                    "Error occurred while generating signature for message : {} with exception : ",
                    ex, message);
        }
        return StringUtils.EMPTY;
    }
}

package redsocial.multimedia.infraestructure.util;

import com.superapp.core.microservice.utils.SALogger;
import com.superapp.redsocial.core.infraestructure.jackson.JsonTransformer;
import com.superapp.redsocial.core.infraestructure.ssm.SecretManager;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {

    public static String encode(String txt , int version) throws Exception {
        try {


            SALogger.info("Utils","encode - txt  - version  ",txt + "  " + version);
            SALogger.info("Utils","encode - cifrado ",(String) JsonTransformer.toMap(SecretManager.getParameter("com/upax/superapp/security/cifrado/seed")).get("seed"));
            SALogger.info("Utils","encode - encrypt ",(String) JsonTransformer.toMap(SecretManager.getParameter("com/upax/superapp/security/encrypt/seed")).get("seed"));

            String seed = "";
            if (version == 1)
                seed = (String) JsonTransformer.toMap(SecretManager.getParameter("com/upax/superapp/security/cifrado/seed")).get("seed");
            else
                seed = (String) JsonTransformer.toMap(SecretManager.getParameter("com/upax/superapp/security/encrypt/seed")).get("seed");

            Base64.Encoder encoder = Base64.getEncoder();

            byte[] clientKeyBytes = seed.getBytes();
            SecretKeySpec clientKey = new SecretKeySpec(clientKeyBytes, 0, clientKeyBytes.length, "AES");
            Cipher encCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encCipher.init(Cipher.ENCRYPT_MODE, clientKey);
            byte[] ivBytes = encCipher.getIV();
            byte[] dataBytes = encCipher.doFinal(txt.getBytes(StandardCharsets.UTF_8));
            byte[] concat = new byte[ivBytes.length + dataBytes.length];
            System.arraycopy(ivBytes, 0, concat, 0, ivBytes.length);
            System.arraycopy(dataBytes, 0, concat, ivBytes.length, dataBytes.length);
            return encoder.encodeToString(concat);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchPaddingException e) {
            throw new Exception(String.format("Fail when try to encode %s \n %s", txt, e.getMessage()));
        }
    }
}

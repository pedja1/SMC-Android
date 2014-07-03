package rs.papltd.smc.common;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt
{
    private static final String ALGO = "AES";
    private static final byte[] keyValue = "s3cr3t_m@ry0_chr".getBytes();

    public static byte[] encrypt(byte[] data) throws Exception
    {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception
    {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);

        return c.doFinal(encryptedData);
    }

    private static Key generateKey() throws Exception
    {
        return new SecretKeySpec(keyValue, ALGO);
    }
}

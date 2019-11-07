package skybiz.com.posoffline.m_Ipay88;

import android.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AeSimpleSHA1 {

    public static String encrypt(String password)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes("UTF-8"));
            byte raw[] = md.digest();
           // String hash = (new BASE64Encoder()).encode(raw);
            String hash = Base64.encodeToString(raw,0);

            return hash;
        }

        catch (NoSuchAlgorithmException e)
        {
        }

        catch (java.io.UnsupportedEncodingException e)
        {
        }

        return null;
    }

    public static void main(String[] args)
    {
        System.out.println(encrypt("my password"));
        // string to hash is here
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        /*MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);*/
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            encodedhash = digest.digest(
                    text.getBytes(StandardCharsets.UTF_8));
        }
        return bytesToHex(encodedhash);

    }

}

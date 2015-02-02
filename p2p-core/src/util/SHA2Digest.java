package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Ethan Petuchowski 2/2/15
 */
public class SHA2Digest {
    /* based in part on
       http://massapi.com/source/resteasy-jaxrs-2.2.1.GA/eagledns/src/main/java/se/unlogic/standardutils/crypto/EncryptionUtils.java.html
     */
    public static String createDigest(File filename) throws IOException {
        InputStream fileInputStream = new FileInputStream(filename);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("sha-256");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        DigestInputStream fileDigester = new DigestInputStream(fileInputStream, messageDigest);
        byte[] buffer = new byte[8192]; // 8 KB (size came from the referenced code, dunno how it was chosen)
        while (fileDigester.read(buffer) != -1);
        fileInputStream.close();
        String digestString = Base64.getEncoder().encodeToString(messageDigest.digest());
        return digestString;
    }

    public static boolean checkFileAgainstDigest(File filename, String digest) throws IOException, NoSuchAlgorithmException {
        return createDigest(filename).equals(digest);
    }
}

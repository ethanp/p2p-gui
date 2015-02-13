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
public class Digester {
    /* based in part on
       http://massapi.com/source/resteasy-jaxrs-2.2.1.GA/eagledns/src/main/java/se/unlogic/standardutils/crypto/EncryptionUtils.java.html
     */
    public static String createDigest(File filename) throws IOException {
        InputStream fileInputStream = new FileInputStream(filename);
        MessageDigest shaDigester = getShaDigester();
        DigestInputStream fileDigester = new DigestInputStream(fileInputStream, shaDigester);
        byte[] buffer = new byte[8192]; // 8 KB (size came from the referenced code, dunno how it was chosen)
        while (fileDigester.read(buffer) != -1);
        fileInputStream.close();
        String digestString = base64OfBytes(shaDigester.digest());
        return digestString;
    }


    public static String createDigest(byte[] byteArray) {
        MessageDigest shaDigester = getShaDigester();
        byte[] digest = shaDigester.digest(byteArray);
        return base64OfBytes(digest);
    }

    public static String createChunkDigest(FileInputStream fileInputStream, int len) throws IOException {
        byte[] fileBytes = new byte[len];
        fileInputStream.read(fileBytes); // read() fills the buffer if there are enough bytes left to read
        return createDigest(fileBytes);
    }

    public static String base64OfBytes(byte[] byteArr) {
        return Base64.getEncoder().encodeToString(byteArr);
    }

    private static MessageDigest getShaDigester() {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("sha-256");
            messageDigest.reset();
            return messageDigest;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("sha is not in at the moment, may I leave a message");
    }

    public static boolean checkFileAgainstDigest(File filename, String digest) throws IOException {
        return createDigest(filename).equals(digest);
    }
}

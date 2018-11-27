package org.deletethis.search.parser.internal.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteArrays {
    private static Base64Provider base64Provider;
    static {
        boolean onJdk = false;
        try {
            // always prefer java.util version - it's available on JDK and newer Androids
            Class.forName( "java.util.Base64");
            onJdk = true;
        } catch( ClassNotFoundException e ) {
            // ignore this one
        }
        if(onJdk) {
            base64Provider = new Base64Jdk();
        } else {
            base64Provider = new Base64Android();
        }
    }

    public static String encodeBase64(byte [] bytes) {
        return base64Provider.encode(bytes);
    }

    public static byte [] decodeBase64(String string) {
        return base64Provider.decode(string);
    }

    public static String sha1Sum(final byte[] bytes) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            byte[] digest = crypt.digest(bytes);
            return String.format("%x", new BigInteger(1, digest));
        } catch(NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

}

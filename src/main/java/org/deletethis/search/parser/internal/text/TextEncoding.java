package org.deletethis.search.parser.internal.text;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TextEncoding {
    private static Base64Impl base64Impl;
    static {
        boolean onJdk = false;
        try {
            Class.forName( "java.util.Base64");
            onJdk = true;
        } catch( ClassNotFoundException e ) {
        }
        if(onJdk) {
            base64Impl = new Base64Jdk();
        } else {
            base64Impl = new Base64Android();
        }
    }

    public static String encodeBase64(byte [] bytes) {
        return base64Impl.encode(bytes);
    }

    public static byte [] decodeBase64(String string) {
        return base64Impl.decode(string);
    }

    public static String sha1Sum(final byte[] bytes) {
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            byte[] digest = crypt.digest(bytes);
            return String.format("%x", new BigInteger(1, digest));
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
    }

}

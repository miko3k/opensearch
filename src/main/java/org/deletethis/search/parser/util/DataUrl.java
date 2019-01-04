package org.deletethis.search.parser.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * This class is currently not used in this project, but it's developed as a part of it nevertheless.
 *
 * https://tools.ietf.org/html/rfc2397
 *
 * Missing feature:
 *    default media type: text/plain;charset=US-ASCII
 */
public class DataUrl {
    private final String mimeType;
    private final byte [] body;
    private final boolean base64;

    private DataUrl(String mimeType, byte[] body, boolean base64) {
        this.mimeType = mimeType;
        this.body = body;
        this.base64 = base64;
    }

    public static DataUrl of(String mimeType, byte[] body) {
        int specialCount = 0;
        for(byte b: body) {
            if(UrlCharacters.isValidChar((char)b))
                specialCount++;
        }
        return new DataUrl(mimeType, body, specialCount > body.length/3);
    }

    public static DataUrl of(String mimeType, byte[] body, boolean base64) {
        return new DataUrl(mimeType, body, base64);

    }

    public static Optional<DataUrl> tryParse(String text) {
        try {
            return Optional.of(parse(text));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private static int getSchemeLength(String text) {
        if(text.length() < 5) {
            return -1;
        } else {
            if(text.substring(0, 5).toLowerCase().equals("data:")) {
                return 5;
            } else {
                return -1;
            }
        }

    }

    public static boolean looksLikeDataUrl(String text) {
        return getSchemeLength(text) >= 0;
    }


    public static DataUrl parse(String text) {
        int pos = getSchemeLength(text);
        if(pos < 0) {
            throw new IllegalArgumentException("wrong scheme");
        }
        UrlCharacters.Decoder charStream = new UrlCharacters.Decoder(text, pos);

        StringBuilder currentParam = new StringBuilder();
        StringBuilder allParams = new StringBuilder();
        boolean base64 = false;

        boolean firstParam = true;
        while(true) {
            int cInt = charStream.read();
            if(cInt < 0) {
                throw new IllegalArgumentException("missing comma character after MIME type");
            }
            char c = (char)(cInt&0xFF);
            if(c == ';' || c == ',') {
                String curr = currentParam.toString();
                currentParam.setLength(0);

                if(curr.equalsIgnoreCase("base64")) {
                    base64 = true;
                } else {
                    if(firstParam) {
                        firstParam = false;
                    } else {
                        allParams.append(";");
                    }
                    allParams.append(curr);
                }
                if(c == ',') {
                    break;
                }
            } else {
                currentParam.append(c);
            }
        }

        String mimeType = allParams.toString();
        byte [] data;
        text = text.substring(charStream.getPosition());

        if(base64) {
            data = ByteArrays.decodeBase64(text);
        } else {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int current;
            while((current=charStream.read()) >= 0) {
                buffer.write(current&0xFF);
            }
            data = buffer.toByteArray();
        }

        return new DataUrl(mimeType, data, base64);
    }

    public byte [] getData() {
        return body;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String encode() {
        StringBuilder out = new StringBuilder();
        out.append("data:");
        out.append(UrlCharacters.encodeInvalid(mimeType, StandardCharsets.UTF_8));
        if(base64) {
            out.append(";base64,");
            out.append(ByteArrays.encodeBase64(body));
        } else {
            out.append(",");
            out.append(UrlCharacters.encodeInvalid(body));
        }
        return out.toString();
    }
}

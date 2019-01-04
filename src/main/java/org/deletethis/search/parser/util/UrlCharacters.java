package org.deletethis.search.parser.util;

import java.nio.charset.Charset;

public class UrlCharacters {
    private static boolean validChars[] = new boolean[128];
    private static boolean reservedChars[] = new boolean[128];

    static {
        for(char c = 'A'; c <= 'Z'; ++c) {
            validChars[c] = true;
        }
        for(char c = 'a'; c <= 'z'; ++c) {
            validChars[c] = true;
        }
        for(char c = '0'; c <= '9'; ++c) {
            validChars[c] = true;
        }
        validChars['-'] = true;
        validChars['.'] = true;
        validChars['_'] = true;
        validChars['~'] = true;
        validChars[':'] = true;
        validChars['/'] = true;
        validChars['?'] = true;
        validChars['#'] = true;
        validChars['['] = true;
        validChars[']'] = true;
        validChars['@'] = true;
        validChars['!'] = true;
        validChars['$'] = true;
        validChars['&'] = true;
        validChars['\''] = true;
        validChars['('] = true;
        validChars[')'] = true;
        validChars['*'] = true;
        validChars['+'] = true;
        validChars[','] = true;
        validChars[';'] = true;
        validChars['='] = true;

        reservedChars[';'] = true;
        reservedChars['/'] = true;
        reservedChars['?'] = true;
        reservedChars[':'] = true;
        reservedChars['@'] = true;
        reservedChars['&'] = true;
        reservedChars['='] = true;
        reservedChars['+'] = true;
        reservedChars['$'] = true;
        reservedChars[','] = true;
    }

    private static boolean isReservedInt(int n) {
        if(n < 0 || n >= reservedChars.length) {
            return false;
        } else {
            return reservedChars[n];
        }
    }

    private static boolean isValidInt(int n) {
        if(n < 0 || n >= validChars.length) {
            return false;
        } else {
            return validChars[n];
        }
    }


    public static boolean isValidChar(char c) {
        return isValidInt(c);
    }

    private static boolean isValidByte(byte c) {
        return isValidInt(c&0xFF);
    }

    private static boolean isReservedByte(byte b) {
        return isReservedInt(b&0xFF);
    }


    private interface ValidByte {
        boolean isValid(byte b);
    }

    private static final String HEX = "0123456789ABCDEF";

    private static void encodeCharacter(StringBuilder out, byte b) {
        out.append('%');
        out.append(HEX.charAt((b >> 4)&0xF));
        out.append(HEX.charAt((b)&0xF));
    }

    private static String doEncode(byte [] bytes, boolean plus, ValidByte validByte) {
        StringBuilder bld = new StringBuilder();
        for(byte b: bytes) {
            if(plus && b == ' ') {
                bld.append('+');
            } else {
                if (validByte.isValid(b)) {
                    bld.append((char) (b & 0xFF));
                } else {
                    encodeCharacter(bld, b);
                }
            }
        }
        return bld.toString();
    }


    public static String encodeInvalid(String url, Charset charset) {
        return encodeInvalid(url.getBytes(charset));
    }

    static String encodeInvalid(byte [] bytes) {
        return doEncode(bytes, false, UrlCharacters::isValidByte);
    }

    public static String encode(String url, Charset charset) {
        byte [] bytes = url.getBytes(charset);
        return doEncode(bytes, false, b -> isValidByte(b) && !isReservedByte(b));
    }

    public static String encodeQuery(String query, Charset charset) {
        byte [] bytes = query.getBytes(charset);
        return doEncode(bytes, true, b -> isValidByte(b) && !isReservedByte(b));
    }

    public static class Decoder {
        private final CharSequence input;
        private int position;

        Decoder(CharSequence input, int position) {
            this.input = input;
            this.position = position;
        }

        static boolean isValidChar(char c) {
            return UrlCharacters.isValidChar(c);
        }

        private int xdigit(char c) {
            if (c >= '0' && c <= '9') return c - '0';
            if (c >= 'A' && c <= 'F') return c - 'A' + 10;
            if (c >= 'a' && c <= 'f') return c - 'a' + 10;
            throw new IllegalArgumentException("invalid hex digit: " + c);
        }

        int getPosition() {
            return position;
        }

        int read() {
            if (position >= input.length()) {
                return -1;
            }
            char c = input.charAt(position);
            if (c == '%') {
                if (position + 2 >= input.length()) {
                    return -1;
                }
                byte result = (byte)((xdigit(input.charAt(position + 1)) << 4) | xdigit(input.charAt(position + 2)));
                position += 3;
                return result&0xFF;
            } else {
                if (!isValidChar(c)) {
                    throw new IllegalStateException("Invalid URL character at position " + position);
                }
                ++position;
                return c&0xFF;
            }
        }
    }
}

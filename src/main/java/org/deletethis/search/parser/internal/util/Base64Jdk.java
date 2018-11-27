package org.deletethis.search.parser.internal.util;

class Base64Jdk implements Base64Provider {
    @Override
    public String encode(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decode(String base64) {
        return java.util.Base64.getDecoder().decode(base64);
    }
}

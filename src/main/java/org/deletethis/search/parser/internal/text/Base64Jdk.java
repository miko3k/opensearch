package org.deletethis.search.parser.internal.text;

class Base64Jdk implements Base64Impl {
    @Override
    public String encode(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decode(String base64) {
        return java.util.Base64.getDecoder().decode(base64);
    }
}

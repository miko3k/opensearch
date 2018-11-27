package org.deletethis.search.parser.internal.text;

interface Base64Impl {
    String encode(byte [] bytes);
    byte[] decode(String base64);
}

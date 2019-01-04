package org.deletethis.search.parser.util;

interface Base64Provider {
    String encode(byte [] bytes);
    byte[] decode(String base64);
}

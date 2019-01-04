package org.deletethis.search.parser.util;

class Base64Android implements Base64Provider {
    @Override
    public String encode(byte[] bytes) {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    @Override
    public byte[] decode(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
    }
}

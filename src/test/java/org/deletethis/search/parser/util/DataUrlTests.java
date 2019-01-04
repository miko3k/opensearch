package org.deletethis.search.parser.util;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DataUrlTests {
    @Test
    public void test1() {
        DataUrl u = DataUrl.parse("data:text/plain;charset=UTF-8;page=21,the%20data:1234,5678");
        Assert.assertEquals("text/plain;charset=UTF-8;page=21", u.getMimeType());
        Assert.assertEquals("the data:1234,5678", new String(u.getData(), StandardCharsets.US_ASCII));
        Assert.assertEquals("data:text/plain;charset=UTF-8;page=21,the%20data:1234,5678", u.encode());

        u = DataUrl.parse("data:;;;;;,");
        Assert.assertEquals(";;;;;", u.getMimeType());
        Assert.assertEquals(0, u.getData().length);
        Assert.assertEquals("data:;;;;;,", u.encode());

        u = DataUrl.parse("data:;;;;;;base64,");
        Assert.assertEquals(";;;;;", u.getMimeType());
        Assert.assertEquals(0, u.getData().length);
        Assert.assertEquals("data:;;;;;;base64,", u.encode());

        u = DataUrl.parse("data:text/plain;base64,SGVsbG8sIHdvcmxkIQ==");
        Assert.assertEquals("Hello, world!", new String(u.getData(), StandardCharsets.US_ASCII));
        Assert.assertEquals("data:text/plain;base64,SGVsbG8sIHdvcmxkIQ==", u.encode());

        byte [] bytes = new byte[] {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF };

        Assert.assertEquals("data:bar;base64,ASNFZ4mrze8=", DataUrl.of("bar", bytes).encode());
        Assert.assertEquals("data:bar,%01#Eg%89%AB%CD%EF", DataUrl.of("bar", bytes, false).encode());
        Assert.assertArrayEquals(bytes, DataUrl.parse("data:bar;base64,ASNFZ4mrze8=").getData());
        Assert.assertArrayEquals(bytes, DataUrl.parse("data:bar,%01#Eg%89%ab%cd%ef").getData());

    }
}

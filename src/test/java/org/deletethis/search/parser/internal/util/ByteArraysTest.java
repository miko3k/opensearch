package org.deletethis.search.parser.internal.util;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class ByteArraysTest {
    private Base64Android a = new Base64Android();
    private Base64Jdk j = new Base64Jdk();

    @Test
    public void first() {
        byte [] input = "hello world".getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(a.encode(input), j.encode(input));
    }

    @Test
    public void manyBytes() {
        //not divisable by 3 so there's padding
        byte input[] = new byte[10000];
        for(int i=0;i<input.length;++i) {
            input[i] = (byte)i;
        }
        String ea = a.encode(input);
        String ej = j.encode(input);

        Assert.assertEquals(ea, ej);
        Assert.assertTrue(ea.endsWith("="));

        byte [] ia = a.decode(ea);
        byte [] ij = a.decode(ej);

        Assert.assertArrayEquals(ia, ij);
    }

    @Test
    public void sha1() {
        byte [] bytes = new byte[] { 0x61, 0x62, 0x63 };

        Assert.assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", ByteArrays.sha1Sum(bytes));

    }
}

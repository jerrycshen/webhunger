package me.shenchao.webhunger.util;

import me.shenchao.webhunger.util.common.MD5Util;
import org.junit.Test;

public class MD5UtilTest {

    @Test
    public void test1() {
        String string = "demo";
        System.out.println(MD5Util.get16bitMD5(string));
        System.out.println(MD5Util.get32bitMD5(string));
    }
}

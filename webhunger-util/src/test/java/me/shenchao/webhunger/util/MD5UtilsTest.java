package me.shenchao.webhunger.util;

import me.shenchao.webhunger.util.common.MD5Utils;
import org.junit.Test;

public class MD5UtilsTest {

    @Test
    public void test1() {
        String string = "demo";
        System.out.println(MD5Utils.get16bitMD5(string));
        System.out.println(MD5Utils.get32bitMD5(string));
    }
}

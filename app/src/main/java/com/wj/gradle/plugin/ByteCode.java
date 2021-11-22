package com.wj.gradle.plugin;

/**
 * Created by wenjing.liu on 2021/11/17 in J1.
 *
 * @author wenjing.liu
 */
public class ByteCode {

    public final static int staticInt = 10;
    private int sumInt;
    private String byteCode = "字节码";

    static {

    }

    private int sumMethod(int aa, int bb) {
        return aa + bb;
    }

}

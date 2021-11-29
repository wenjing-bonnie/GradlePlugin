package com.wj.gradle.plugin;

/**
 * Created by wenjing.liu on 2021/11/17 in J1.
 *
 * @author wenjing.liu
 */
public class ByteCode {

    public static final int staticInt = 10;
    private int sumInt;
    private static String byteCode = "字节码";

    static {

    }

    private int sumMethod(int aa, int bb) {
        return aa + bb + staticInt + sumInt + getSumInt();
    }

    private String stringMethod() {
        return byteCode;
    }

    public void setSumInt(int sum) {
        this.sumInt = sum;
    }

    public int getSumInt() {
        return sumInt;
    }
}

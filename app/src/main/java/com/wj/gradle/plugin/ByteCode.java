package com.wj.gradle.plugin;

import android.util.Log;

/**
 * Created by wenjing.liu on 2021/11/17 in J1.
 *
 * @author wenjing.liu
 */
public class ByteCode {

    public static final int staticInt = 10;
    private int sumInt;
    private static String byteCode = "字节码";

    public int sumMethod(int aa, int bb) {
        long start = System.currentTimeMillis();

//        if (aa > 0 && aa < 100) {
//            end = System.currentTimeMillis();
//            Log.v(getClass().getSimpleName(), String.format("cost time is %ld", (end - start)));
//            return aa;
//        } else if (bb > 0 && bb < 100) {
//            end = System.currentTimeMillis();
//            Log.v(getClass().getSimpleName(), String.format("cost time is %ld", (end - start)));
//            return bb;
//        }

        long end = System.currentTimeMillis() - start;
        Log.v("ExecutionTime", String.format("cost time is %d", end));
        return aa + bb + staticInt + sumInt + getSumInt();
    }

    public String stringMethod() {
        return byteCode;
    }

    public void setSumInt(int sum) {
        long start = System.currentTimeMillis();

        this.sumInt = sum;
        String tag = "13";

        long end = System.currentTimeMillis() - start;
        Log.v("ExecutionTime", String.format("cost time is %d", end));
    }

    public int getSumInt() {
        return sumInt;
    }

    public static void main(String[] args) {
        ByteCode code = new ByteCode();
        code.sumMethod(2, 4);
    }
}

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
        return aa + bb + staticInt + sumInt + getSumInt();
    }

    public String stringMethod() {
        return byteCode;
    }

    public void setSumInt(int sum) {
        long start = System.currentTimeMillis();

        this.sumInt = sum;

        for (int i = 0; i < 400000; i++) {
        }

        long end = System.currentTimeMillis() - start;
        if (end >= 300) {
            Log.v("ExecutionTime", String.format("cost time is %d", end));
        }
    }

    public int getSumInt() {
        return sumInt;
    }
//
//    private void forLabelByteCode() {
//        for (int i = 3; i < 10; i+=2) {
//            System.currentTimeMillis();
//        }
//    }
//
//    private void switchLabelByteCode(int arg) {
//        switch (arg) {
//            case 10: {
//                System.out.println("This is ten");
//                break;
//            }
//            case 9: {
//                System.out.println("This is nine");
//                break;
//            }
//            default: {
//                System.out.println("Not support");
//            }
//
//        }
//    }
//
//    private void tryCatchLabelByteCode() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        ByteCode code = new ByteCode();
        code.sumMethod(2, 4);
    }
}

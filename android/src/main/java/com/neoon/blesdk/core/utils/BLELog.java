package com.neoon.blesdk.core.utils;

import android.util.Log;

import com.neoon.blesdk.util.LogRecorder;


/**
 * 作者:东芝(2017/7/31).
 * 功能:
 */
public class BLELog {

    private static String TAG = "蓝牙框架";
    private static boolean isJavaTest;
    private static boolean isDebug = false;
    public static long beginConnectTime = 0;


    public static void setIsDebug(boolean isDebug) {
        BLELog.isDebug = isDebug;
    }

//    public static void setIsJavaTestModel(boolean isJavaTest) {
//        BLELog.isJavaTest = isJavaTest;
//        isDebug = true;
//    }

    private BLELog() {

    }

//    private static boolean isDebug {
//        return isDebug;
//    }

    private static String createLog(String log) {
        return log;
    }

//    private static void getTAG(StackTraceElement[] sElements) {
//        TAG = "蓝牙框架"/*sElements[1].getFileName()*/;
//    }

    public static void w(String message, Object... args) {
        String log = createLog(String.format(message, args));
        if (isDebug) {

//        getTAG(new Throwable().getStackTrace());
            if (isJavaTest) {
                System.out.println(log);
            } else {
                Log.w(TAG, log);
            }
        }
        printToDisk(TAG + ":" + log);
    }

    public static void stack(String message) {
        StackTraceElement[] sElements  = new Throwable().getStackTrace();
        String methodName = "";
        try {
            if (sElements.length >= 6) {
                methodName = sElements[6].getMethodName() + ">"
                        + sElements[5].getMethodName() + ">"
                        + sElements[4].getMethodName() + ">"
                        + sElements[3].getMethodName() + ">"
                        + sElements[2].getMethodName() + ">"
                        + sElements[1].getMethodName();
            } else {
                methodName = sElements[1].getMethodName();
            }
        } catch (Exception e) {
            methodName = sElements[1].getMethodName();
        }
        Log.w(TAG, message+" "+methodName);
    }
    public static void w(String message) {
        String log = createLog(message);
        if (isDebug) {
//        getTAG(new Throwable().getStackTrace());
            if (isJavaTest) {
                System.out.println(log);
            } else {
                Log.w(TAG, log);
            }
        }
        printToDisk(TAG + ":" + log);
    }



    public static void d(String message, Object... args) {
        String log = createLog(String.format(message, args));
        if (isDebug) {
//        getTAG(new Throwable().getStackTrace());
            if (isJavaTest) {
                System.out.println(log);
            } else {
                Log.d(TAG, log);
            }
        }
        printToDisk(TAG + ":" + log);
    }


    public static void iDebug(String message) {
//        getTAG(new Throwable().getStackTrace());
        String log = createLog(message);
        if (isJavaTest) {
            System.out.println(log);
        } else {
            Log.d(TAG, log);
        }
    }
    private static void printToDisk(String log) {
        if (isDebug)
        {
            LogRecorder.printToDisk(log);
        }
    }

}

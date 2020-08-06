package com.neoon.blesdk.util;

import android.os.Environment;

import java.io.File;

/**
 * 作者:东芝(2017/11/16).
 * 功能:静态常量
 */

public class Constant
{


    public static class Path {

        /**
         * sd卡根目录
         */
        public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        /**
         * 项目根目录
         */
        public static final String ROOT_PATH = mkdirs(SDCARD +  "/SNSDK");
        /**
         * 崩溃日志保存位置
         */
        public static final String CACHE_LOG = mkdirs(ROOT_PATH + "/cache_log");

        private static String mkdirs(String path) {
            File file = new File(path);
            if (file.isFile()) {
                if (!file.getParentFile().exists()) {
                    boolean mkdirs = file.getParentFile().mkdirs();
                    System.out.println("mkdirs1="+mkdirs);
                }
            } else if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                System.out.println("mkdirs2="+mkdirs);
            }
            return path;
        }

    }
}

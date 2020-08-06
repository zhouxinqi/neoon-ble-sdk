package com.neoon.blesdk.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 作者:东芝(2018/4/3).
 * 功能:日志记录器
 */

public class LogRecorder implements Serializable/*实现这个是为了防止混淆,因为该类被各种子依赖库逆向反射*/ {


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat hms = new SimpleDateFormat("MM-dd HH:mm:ss");

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        private        File    file;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (messageList.isEmpty()) {
                        break;
                    }
                    PrintWriter pw = null;
                    try {
                        if (file == null||!file.exists())
                        {
                            file = getFile(System.currentTimeMillis());
                        }
                        pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                        for (String log : messageList) {
                            pw.println(log);
                        }
                        pw.flush();
                        messageList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (pw != null) {
                                pw.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };


    private static File getFile(long time) {
        File file = new File(Constant.Path.CACHE_LOG, String.format( Locale.ENGLISH, "logcat-%s.log",dateFormat.format(time) ));
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    private static List<String> messageList = new CopyOnWriteArrayList<>();

    public static void printToDisk(final String message) {
        messageList.add(hms.format(System.currentTimeMillis()) + ":" + message);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
    }





}

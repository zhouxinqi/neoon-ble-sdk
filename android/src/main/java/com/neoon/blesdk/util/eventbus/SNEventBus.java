package com.neoon.blesdk.util.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * 作者:东芝(2017/12/15).
 * 功能:事件总线
 */

public class SNEventBus {


    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    public static void unregister(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    public static boolean isRegistered(Object subscriber) {
       return EventBus.getDefault().isRegistered(subscriber);
    }

    private static void sendEvent(SNEvent event) {
        EventBus.getDefault().post(event);
    }

    private static void sendStickyEvent(SNEvent event) {
        EventBus.getDefault().postSticky(event);
    }


    public static <T> void sendEvent(int code, T data) {
        sendEvent(new SNEvent<>(code, data));
    }

    public static <T> void sendStickyEvent(int code, T data) {
        sendStickyEvent(new SNEvent<>(code, data));
    }


    public static void sendEvent(int code) {
        sendEvent(new SNEvent(code));
    }

    public static void sendStickyEvent(int code) {
        sendStickyEvent(new SNEvent(code));
    }

}

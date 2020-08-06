package com.neoon.blesdk.interfaces;

import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;

/**
 * 作者:东芝(2018/7/6).
 * 功能:设备(主动)事件
 */

public interface OnDeviceEventListener {

    /**
     * @param type 事件类型,  见常量类 SNBLEEvent.java
     */
    void onDeviceEventChanged(@SNBLEEvent.DeviceEvent int type);
}

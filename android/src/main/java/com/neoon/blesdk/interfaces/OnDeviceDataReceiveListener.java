package com.neoon.blesdk.interfaces;

import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;

/**
 * 作者:东芝(2018/7/6).
 * 功能:接收设备返回的数据
 */

public interface OnDeviceDataReceiveListener {
    /**
     * @param type 数据类型,见常量类 SNBLEEvent.java
     * @param args 数据,需要使用SNDeviceData类进行转换
     */
    void onDeviceDataChanged(@SNBLEEvent.DeviceData int type, Object... args);
}

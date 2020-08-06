package com.neoon.blesdk.encapsulation.entity;


import com.neoon.blesdk.core.entity.BleDevice;

/**
 * 作者:东芝(2017/11/23).
 * 功能:设备对象
 */

public class SNBLEDevice extends BleDevice {

    public SNBLEDevice(BleDevice device) {
        this.mDevice = device.mDevice;
        this.mDeviceAddress = device.mDeviceAddress;
        this.mDeviceName = device.mDeviceName;
        this.mParsedAd = device.mParsedAd;
        this.mRssi = device.mRssi;
        this.mScanRecord = device.mScanRecord;
    }
}

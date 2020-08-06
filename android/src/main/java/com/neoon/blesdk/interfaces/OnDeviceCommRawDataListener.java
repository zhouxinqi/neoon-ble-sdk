package com.neoon.blesdk.interfaces;

/**
 * 作者:东芝(2019/01/10).
 * 功能:
 */

public abstract class OnDeviceCommRawDataListener extends  OnDeviceCommBaseListener {
    /**
     * 原数据
     * @param rawData
     */
    public abstract void onRawDataResponse(byte[] rawData);
}

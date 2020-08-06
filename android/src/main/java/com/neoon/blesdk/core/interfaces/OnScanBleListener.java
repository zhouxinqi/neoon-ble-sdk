package com.neoon.blesdk.core.interfaces;


import com.neoon.blesdk.core.entity.BleDevice;

/**
 * 作者:东芝(2017/11/20).
 * 功能:
 */
public interface OnScanBleListener<T extends BleDevice> {
    /**
     * 扫描开始
     */
    void onScanStart();

    /**
     * 扫描中, 回调扫描结果
     * @param scanResult
     */
    void onScanning(T scanResult);

    /**
     * 扫描停止
     */
    void onScanStop();

    /**
     * 扫描超时, 通常为周围无任何设备
     */
    void onScanTimeout();
}

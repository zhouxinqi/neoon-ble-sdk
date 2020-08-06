package com.neoon.blesdk.core.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙写入 监听
 */

public interface WriteListener extends CommunicationListener {
    void onWriteSuccessful(BluetoothGattCharacteristic characteristic);
}

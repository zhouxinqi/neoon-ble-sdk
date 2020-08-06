package com.neoon.blesdk.encapsulation.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.neoon.blesdk.core.entity.FilePackage;
import com.neoon.blesdk.core.interfaces.OnWallpaperUploadListener;
import com.neoon.blesdk.interfaces.OnDeviceCommBaseListener;
import com.neoon.blesdk.interfaces.OnDeviceConnectListener;
import com.neoon.blesdk.interfaces.OnDeviceDataReceiveListener;
import com.neoon.blesdk.interfaces.OnDeviceEventListener;

import java.util.List;

/**
 * 作者:东芝(2018/7/5).
 * 功能:
 */

public interface ISNBLEManager {

    /**
     * 初始化SDK
     *
     * @param context
     * @param appKey
     */
    void initSDK(Context context, String appKey);

    void restartBLEService(Context context);
    boolean isBLEServiceRunning(Context context);
    /**
     * 调试模式
     *
     * @param debug
     */
    void setDebug(boolean debug);

    /**
     * 是否已经初始化
     */
    boolean isSDKInitialized();

    /**
     * 释放SDK
     */
    void releaseSDK();

    /**
     * 手机是否兼容BLE版本
     */
    boolean isBluetoothSupportVersion();

    /**
     * 手机是否兼容BLE
     */
    boolean isBluetoothSupportBLE();

    /**
     * 手机是否开启蓝牙
     */
    boolean isBluetoothEnable();

    /**
     * 获得原生蓝牙适配器
     *
     * @return
     */
    BluetoothAdapter getBluetoothAdapter();

    /**
     * 获得原生蓝牙管理器
     *
     * @return
     */
    BluetoothManager getBluetoothManager();


    /**
     * 连接设备
     *
     * @param address         mac地址
     * @param connectListener 连接监听
     * @return
     */
    boolean connect(String address, OnDeviceConnectListener connectListener);

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 是否已连接
     *
     * @return
     */
    boolean isConnected();

    /**
     * 是否正在连接
     *
     * @return
     */
    boolean isConnecting();

    /**
     * 是否已断开连接
     *
     * @return
     */
    boolean isDisconnected();

    /**
     * 发送命令到手环上
     *
     * @param data                      命令
     * @param commandSendStatusListener 命令发送是否执行成功的回调
     */
    void sendCMD(byte[] data, OnDeviceCommBaseListener commandSendStatusListener);

    /**
     * 发送命令到手环上
     *
     * @param data 命令
     */
    void sendCMD(byte[] data);

    /**
     * 发送命令到手环上
     *
     * @param datas 命令集合
     */
    void sendCMD(List<byte[]> datas);

    /**
     * 发送命令到手环上
     *
     * @param datas                     命令集合
     * @param commandSendStatusListener 命令发送是否执行成功的回调
     */
    void sendCMD(List<byte[]> datas, OnDeviceCommBaseListener commandSendStatusListener);


    void removeListener(OnDeviceCommBaseListener commandSendStatusListener);


    void removeConnectListener(OnDeviceConnectListener onDeviceConnectListener);

    boolean readRemoteRssi();

    /**
     * 获取原生GATT
     *
     * @return
     */
    BluetoothGatt getBluetoothGatt();

    /**
     * 获取 BluetoothDevice
     *
     * @return
     */
    BluetoothDevice getDevice();

    /**
     * 获取设备名
     *
     * @return
     */
    String getDeviceName();

    /**
     * 获取设备mac地址
     *
     * @return
     */
    String getDeviceMacAddress();

    /**
     * 请求设置连接间隔
     *
     * @param connectionPriority
     * @return
     */
    boolean requestConnectionPriority(int connectionPriority);

    /**
     * 设置事件监听器
     *
     * @param deviceEventListener
     */
    void setOnDeviceEventListener(OnDeviceEventListener deviceEventListener);

    /**
     * 设置数据监听
     *
     * @param deviceDataReceiveListener
     */
    void setOnDeviceDataReceiveListener(OnDeviceDataReceiveListener deviceDataReceiveListener);

    void sendFileCMD(List<FilePackage> filePackage, OnWallpaperUploadListener listener);

    boolean isSupportWallpaper();

    boolean setWallpaperNotifyEnable(boolean enable);
}

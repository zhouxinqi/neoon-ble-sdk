package com.neoon.blesdk.encapsulation.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.neoon.blesdk.core.ble.DZBLESDK;
import com.neoon.blesdk.core.ble.DZBLEScanner;
import com.neoon.blesdk.encapsulation.service.SNBLEService;
import com.neoon.blesdk.encapsulation.storage.SNStorage;

import java.lang.ref.WeakReference;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙SDK基类
 */
public class SNBLESDK {

    private static WeakReference<Context> mBaseContext;
    private static boolean isInitialized;

    public static WeakReference<Context> getBaseContext() {
        return mBaseContext;
    }

    public static void init(Context context) {

        mBaseContext = new WeakReference<>(context.getApplicationContext());
        SNUUID.init();
        SNStorage.init(context, context.getPackageName()+"_storage");
        DZBLESDK.init(mBaseContext.get());
        SNBLEHelper.init(15000L, 10000L, 10000L);
        DZBLEScanner.setScanTimeOut(20000L);
        SNBLEService.startService(mBaseContext.get(), SNBLEService.class);
        isInitialized = true;
    }

    public static void close() {
        isInitialized = false;
        SNBLEService.stopService(mBaseContext.get(), SNBLEService.class);
        DZBLESDK.close(mBaseContext.get());
        SNBLEHelper.close(mBaseContext.get());
        mBaseContext.clear();
    }

    /**
     * 检查是否已经初始化
     *
     * @return
     */
    public static boolean isInitialized() {
        return isInitialized&& DZBLESDK.isInitialized();
    }

    /**
     * 是否在4.3以上
     *
     * @return
     */
    public static boolean isBluetoothSupportVersion() {
        return DZBLESDK.isBluetoothSupportVersion();
    }

    /**
     * 是否支持蓝牙BLE
     *
     * @return
     */
    public static boolean isBluetoothSupportBLE() {
        return DZBLESDK.isBluetoothSupportBLE();
    }

    /**
     * 蓝牙是否开启
     *
     * @return
     */
    public static boolean isBluetoothEnable() {
        return DZBLESDK.isBluetoothEnable();
    }

    /**
     * 取得蓝牙适配器
     *
     * @return
     */
    public static BluetoothAdapter getBluetoothAdapter() {
        return DZBLESDK.getBluetoothAdapter();
    }

    /**
     * 取得蓝牙管理器
     *
     * @return
     */
    public static BluetoothManager getBluetoothManager() {
        return DZBLESDK.getBluetoothManager();
    }


}

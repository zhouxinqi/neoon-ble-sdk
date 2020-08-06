package com.neoon.blesdk.core.ble;

import android.content.Context;
import android.text.TextUtils;

import com.neoon.blesdk.core.entity.BleDevice;
import com.neoon.blesdk.core.interfaces.OnScanBleListener;
import com.neoon.blesdk.core.utils.BLELog;

import java.util.ArrayList;
import java.util.List;


/**
 * 蓝牙扫描
 */
public class DZBLEScanner {
    private static List<String> macAddress = new ArrayList();
    private static OnScanBleListener onScanBleListener;

    public static void setScanTimeOut(long time) {
        BaseBleScanHelper.getInstance().setScanTimeOut(time);
    }

    static void init(Context context) {
        BaseBleScanHelper.getInstance().init(context);
    }

    public static boolean isScanning() {
        return BaseBleScanHelper.getInstance().isScanning();
    }

    public static void startScan(OnScanBleListener scanBleListener) {
        BLELog.d("startScan");
        macAddress.clear();
        onScanBleListener = scanBleListener;
        BaseBleScanHelper.getInstance().scanLeDevice(true);
        BaseBleScanHelper.getInstance().addScanDeviceListener(mScanDeviceListener);
        DZBLEScanner.onScanBleListener.onScanStart();
    }

    public static void stopScan() {
        BLELog.d("stopScan");
        BaseBleScanHelper.getInstance().scanLeDevice(false);
    }

    public static void destroy(){
        BLELog.d("destroy");
        BaseBleScanHelper.getInstance().removeScanDeviceListener(mScanDeviceListener);
    }

    static BaseBleScanHelper.ScanDeviceListener mScanDeviceListener = new BaseBleScanHelper.ScanDeviceListener() {
        @Override
        public void onFind(BleDevice device) {
            if (DZBLEScanner.onScanBleListener != null) {
                String mac = device.mDeviceAddress;
                if (!TextUtils.isEmpty(mac)) {
                    boolean isHas = false;
                    for (int i = 0; i < DZBLEScanner.macAddress.size(); ++i) {
                        String adress = DZBLEScanner.macAddress.get(i);
                        if (adress.equals(mac)) {
                            isHas = true;
                            break;
                        }
                    }
                    if (!isHas) {
                        DZBLEScanner.macAddress.add(mac);
                        DZBLEScanner.onScanBleListener.onScanning(device);
                    }
                }

            }
        }

        @Override
        public void onFinish() {
            DZBLEScanner.onScanBleListener.onScanStop();
        }

        @Override
        public void onTimeout() {
            DZBLEScanner.onScanBleListener.onScanTimeout();
        }
    };


}

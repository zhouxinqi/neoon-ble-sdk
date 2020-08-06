package com.neoon.blesdk.encapsulation.ble;

import com.neoon.blesdk.core.ble.DZBLEScanner;
import com.neoon.blesdk.core.entity.BleDevice;
import com.neoon.blesdk.core.interfaces.OnScanBleListener;
import com.neoon.blesdk.encapsulation.entity.SNBLEDevice;
import com.neoon.blesdk.interfaces.OnDeviceLeScanListener;


/**
 * 作者:东芝(2017/11/20).
 * 功能:蓝牙设备扫描
 */

public class SNBLEScanner {
    private static OnScanBleListener listener;

    /**
     * 检测是否正在扫描中
     *
     * @return
     */
    public static boolean isScanning() {
        return DZBLEScanner.isScanning();
    }

    /**
     * 开始扫描, 并设置回调扫描结果
     *
     * @param listener
     */
    public static void startScan(OnDeviceLeScanListener listener) {
        SNBLEScanner.listener = listener;
        DZBLEScanner.startScan(scanBleListener);
    }

    public static void destroy(){
        DZBLEScanner.destroy();
        SNBLEScanner.listener = null;
    }

    private static OnScanBleListener scanBleListener = new OnScanBleListener<BleDevice>() {
        @Override
        public void onScanStart() {
            if (listener != null) {
                listener.onScanStart();
            }
        }

        @Override
        public void onScanning(BleDevice device) {


//            String name = device.mDeviceName;
//            boolean isUseManufacturerIdGetDeviceInfoSuccess = false;
//            if (device.mParsedAd != null) {//根据厂商定义的广播数据 重新确定设备名称
//
//                //通过厂商ID取出设备信息
//                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(device.mParsedAd.manufacturers);
//                boolean checkManufacturerIsOurDevice = deviceInfo != null;
//                //通过厂商ID判断 是否是我们家的设备
//                if (checkManufacturerIsOurDevice) {
//                    String deviceName = deviceInfo.getDevice_name();
//                    if (!TextUtils.isEmpty(deviceName)) {
//                        name = device.mDeviceName = deviceName;
//                    }
//                    //能取得设备信息
//                    isUseManufacturerIdGetDeviceInfoSuccess = true;
//                }
//            }
//            // 如果上面的逻辑无法通过厂商ID 取出设备信息, 这下子只能粗略地判断设备名了
//            if (!isUseManufacturerIdGetDeviceInfoSuccess) {
//                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(name);
//                boolean checkNameIsOurDevice = deviceInfo != null;
//                //通过设备名判断 是否是我们家的设备
//                if (!checkNameIsOurDevice) {
//                    //同时也不是OTA模式
//                    if (!DeviceType.isDFUModel(name)) {
//                        return;
//                    }
//                }
//            }

            if (listener != null) {
                listener.onScanning(new SNBLEDevice(device));
            }
        }

        @Override
        public void onScanStop() {
            if (listener != null) {
                listener.onScanStop();
            }
        }

        @Override
        public void onScanTimeout() {
            if (listener != null) {
                listener.onScanTimeout();
            }
        }
    };

    /**
     * 停止扫描
     */
    public static void stopScan() {
        DZBLEScanner.stopScan();
    }
}

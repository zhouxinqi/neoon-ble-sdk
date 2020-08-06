package com.neoon.blesdk.encapsulation.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.neoon.blesdk.core.ble.BaseBleDataHelper;
import com.neoon.blesdk.core.ble.BaseUUID;
import com.neoon.blesdk.core.ble.BleControl;
import com.neoon.blesdk.core.ble.BleHelper;
import com.neoon.blesdk.core.entity.FilePackage;
import com.neoon.blesdk.core.interfaces.BluetoothStatusListener;
import com.neoon.blesdk.core.interfaces.CommunicationListener;
import com.neoon.blesdk.core.interfaces.ConnectListener;
import com.neoon.blesdk.core.interfaces.NotifyReceiverRawListener;
import com.neoon.blesdk.core.interfaces.OnWallpaperUploadListener;
import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.core.utils.BLESupport;
import com.neoon.blesdk.encapsulation.entity.SNBLEDevice;
import com.neoon.blesdk.interfaces.OnDeviceLeScanListener;

import java.util.List;
import java.util.UUID;


/**
 * 作者:东芝(2017/11/20).
 * 功能:
 */

public class SNBLEHelper extends BaseBleDataHelper {
    /**
     * 蓝牙被释放过
     */
    private static boolean isBluetoothRecycled = true;


    public static void setIsBluetoothRecycled(boolean isBluetoothRecycled) {
        SNBLEHelper.isBluetoothRecycled = isBluetoothRecycled;
    }

    private static String address;
    private static boolean isAutoReConnect = true;//默认支持重连
    private static int mMaxReConnectCount = Integer.MAX_VALUE;//约等于无穷次重连
    private static boolean isUserDisconnected;//开发者主动断开 不能进行重连

    public static boolean isIsUserDisconnected() {
        return isUserDisconnected;
    }

    public static void setIsUserDisconnected(boolean isUserDisconnected) {
        SNBLEHelper.isUserDisconnected = isUserDisconnected;
    }

    public static boolean isAutoReConnect() {
        return isAutoReConnect;
    }

    public static int getMaxReConnectCount() {
        return mMaxReConnectCount;
    }


    public static void setAutoReConnect(boolean autoReConnect, int mMaxReConnectCount) {
        SNBLEHelper.mMaxReConnectCount = mMaxReConnectCount;
        SNBLEHelper.isAutoReConnect = autoReConnect;
    }

    public static void setAutoReConnect(boolean autoReConnect) {
        SNBLEHelper.isAutoReConnect = autoReConnect;
    }

    static void init(long connectTimeOut, long discoverServicesTimeOut, long notifyEnableTimeOut) {
        BleHelper.getInstance().setDiscoverServicesTimeOut(discoverServicesTimeOut);
        BleHelper.getInstance().setConnectTimeOut(connectTimeOut);
        BleHelper.getInstance().setNotifyEnableTimeOut(notifyEnableTimeOut);
        BleControl.addBluetoothStatusListener(mBluetoothStatusListener);
    }

    static void close(Context context) {
        BleControl.removeBluetoothStatusListener(mBluetoothStatusListener);
        BleControl.close(context);
    }

    static BluetoothStatusListener mBluetoothStatusListener = new BluetoothStatusListener() {
        @Override
        public void onBluetoothStatusChange(int state) {
            if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                BLELog.d("重连:检测到用户正在关闭蓝牙");
                BleHelper.getInstance().disconnect();//关闭蓝牙前, 释放
                isBluetoothRecycled = true;
            } else if (state == BluetoothAdapter.STATE_OFF) {
                isBluetoothRecycled = true;//有些手机STATE_TURNING_OFF是不回调的  于是多重写一个判断,安全保障
            }
        }
    };

    /**
     * 连接设备监听
     * 这个方法使用得非常谨慎,记得不用的时候  调用removeConnectListener
     *
     * @param listener
     */
    public static void addConnectListener(ConnectListener listener) {
        BleHelper.getInstance().addConnectListener(listener);
    }

    /**
     * 记得不用的时候 remove掉
     *
     * @param listener
     */
    public static void removeConnectListener(ConnectListener listener) {
        BleHelper.getInstance().removeConnectListener(listener);
    }


    /**
     * 读写通知 回调通常传入
     * 读取成功回调 {@link com.neoon.blesdk.core.interfaces.ReadListener}
     * 写入成功回调 {@link com.neoon.blesdk.core.interfaces.WriteListener}
     * 通知回调 {@link com.neoon.blesdk.core.interfaces.NotifyReceiverListener}
     * 信号值回调 {@link  com.neoon.blesdk.core.interfaces.RssiListener}
     * 这个方法使用得非常谨慎,记得不用的时候 调用removeCommunicationListener
     *
     * @param listener
     */
    public static void addCommunicationListener(CommunicationListener listener) {
        BleHelper.getInstance().addCommunicationListener(listener);
    }

    /**
     * 记得不用的时候 remove掉
     *
     * @param listener
     */
    public static void removeCommunicationListener(CommunicationListener listener) {
        BleHelper.getInstance().removeCommunicationListener(listener);
    }


    public static boolean connect(String address) {
        BLELog.beginConnectTime = System.currentTimeMillis();

        isUserDisconnected = false;
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        if (SNBLEHelper.address == null || !SNBLEHelper.address.equalsIgnoreCase(address)) {
            if (SNBLEScanner.isScanning()) {
                SNBLEScanner.stopScan();
            }
            BLELog.d("重连:连接到" + address + "(首次直接重连)");
            SNBLEHelper.address = address;
            return BleHelper.getInstance().connect(address);
        }

        if (/*DZBLESDK.isDeviceNeedScan() &&*/ isBluetoothRecycled) {
            BLELog.d("重连:连接到" + address + "(该设备是特殊设备 需要扫描再连接)");
            SNBLEHelper.address = address;
            SNBLEScanner.startScan(scanBleListener);
            return true;
        } else {
            if (SNBLEScanner.isScanning()) {
                SNBLEScanner.stopScan();
            }
            BLELog.d("重连:连接到" + address + "(该设备可以直接连接)");
            SNBLEHelper.address = address;
            return BleHelper.getInstance().connect(address);
        }
    }

    private static OnDeviceLeScanListener scanBleListener = new OnDeviceLeScanListener() {
        @Override
        public void onScanStart() {

        }

        @Override
        public void onScanning(SNBLEDevice scanResult) {
          BLELog.d("重连:扫描:" + scanResult.mDeviceAddress);
            if (scanResult.mDeviceAddress.equalsIgnoreCase(SNBLEHelper.address)) {
                SNBLEScanner.stopScan();

                BLELog.d("重连:扫描到该设备,正在连接到" + SNBLEHelper.address);
                BleHelper.getInstance().connect(SNBLEHelper.address);
                //重置蓝牙释放标记 为什么有这个东西?  因为关闭蓝牙再打开 导致部分手机 清空了蓝牙缓存
                // 导致后面连接设备连接不上, 需要重新扫描一下  系统会把缓存更新回去系统里面 这时 就可以连接了
                //而且这个标记可以防止每次都要扫描 导致重连很慢 影响体验,
                // 于是 我们只需要实现 "关闭再打开蓝牙 后 [第一次连接]需要先扫描一次,然后再连mac,后面如果断开了连接 重新连接只需直接连接mac地址即可 节省时间,直到用户下一次关闭再打开蓝牙 无限循环该逻辑即可"
                isBluetoothRecycled = false;
            }
        }

        @Override
        public void onScanStop() {

            BLELog.d("重连:扫描不到该设备,重新扫描" + address);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                boolean needRestartLeService = !SNBLEHelper.isConnected() && BLESupport.getInternalConnectionState(SNBLEHelper.getDeviceMacAddress()) == BLESupport.CONNECTION_STATE_CONNECTED;
                if (needRestartLeService) {
                    BLELog.d("重连:错误!发现系统残留着上次的连接(此时卸载app也不会断开),尝试重启BLE服务 强制性重连! " + address);
                    BleHelper.getInstance().connect(SNBLEHelper.address);
                }
            }
            if (SNBLEHelper.isDisconnected()) {
                SNBLEScanner.startScan(scanBleListener);
            }
        }

        @Override
        public void onScanTimeout() {

        }
    };

    /**
     * 断开连接
     * 该方法会禁止重连 直到调用connect
     * 注: 重连仅仅发生在正常连接成功后 非 主动断开,  的情况
     */
    public static void disconnect() {
        SNBLEHelper.address = null;
        isUserDisconnected = true;
        BleHelper.getInstance().disconnect();
    }


    public static boolean sendCMD(byte[] data) {
        return BleHelper.getInstance().write(data, BaseUUID.SERVICE, BaseUUID.WRITE);
    }


    public static void sendFileCMD(final List<FilePackage> filePackages, final OnWallpaperUploadListener listener) {
        final boolean[] lock = {false};
        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                long l = 0;
                try {
                    int size = filePackages.size();
                    l = System.currentTimeMillis();
                    for (int i = 0; i < size; i++) {
                        System.out.println("INDEX=" + i + "  (" + (i * 100 / size) + ")");
                        if (listener != null) {
                            listener.onProgress(100, (i * 100 / size), size, i);
                        }
                        FilePackage filePackage = filePackages.get(i);
                        lock[0] = true;
                        List<byte[]> bytes20 = filePackage.getBytes20();
                        for (byte[] bytes : bytes20) {
                            boolean write = BleHelper.getInstance().write(bytes, BaseUUID.SERVICE, BaseUUID.WRITE_WALLPAPER);

                        }
                        while (lock[0]) {
                            //
                        }
                    }
                    if (listener != null) {
                        listener.onProgress(100,100, size, size);
                        listener.onSuccess((System.currentTimeMillis() - l) / 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailed(e);
                    }
                }

            }
        });
        thread.start();
        addCommunicationListener(new NotifyReceiverRawListener() {
            @Override
            public void onReceive(UUID uuid, byte[] buffer) {
                if (uuid.equals(BaseUUID.NOTIFY_WALLPAPER)) {
                    System.out.println("图片接收:" + toByteString(buffer));
                    lock[0] = false;
                }
            }
        });


    }


    //暂时用不到
//    public static boolean read(byte[] data) {
//        return BleHelper.get().read(data,null,null);
//    }


    public static boolean readRemoteRssi() {
        return BleHelper.getInstance().readRemoteRssi();
    }


    public static BluetoothGatt getBluetoothGatt() {
        return BleHelper.getInstance().getBluetoothGatt();
    }

    /**
     * 取得设备 没有取得,可能出现空指针
     *
     * @return
     * @throws NullPointerException
     */
    public static BluetoothDevice getDevice() throws NullPointerException {
        return getBluetoothGatt().getDevice();
    }

    /**
     * 取得设备名 没有取得,可能出现空指针
     *
     * @return
     * @throws NullPointerException
     */
    public static String getDeviceName() throws NullPointerException {
        return getDevice().getName();
    }

    /**
     * 取得设备mac 没有取得,可能出现空指针
     *
     * @return
     * @throws NullPointerException
     */
    public static String getDeviceMacAddress() throws NullPointerException {
        try {
            return getDevice().getAddress();
        } catch (NullPointerException e) {
            return address;
        }
    }

    public static boolean isConnected() {
        return BleHelper.getInstance().isConnected();
    }

    public static boolean requestConnectionPriorityBalanced() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
        }
        return false;
    }

    public static boolean requestConnectionPriorityHigh() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        }
        return false;
    }

    public static boolean requestConnectionPriorityLowPower() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);
        }
        return false;
    }

    public static boolean requestConnectionPriority(int connectionPriority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothGatt bluetoothGatt = getBluetoothGatt();
            if (bluetoothGatt == null) return false;

            return bluetoothGatt.requestConnectionPriority(connectionPriority);

        }
        return false;
    }


    public static boolean isSupportWallpaper() throws IllegalStateException {
        if (isConnected()) {
            List<BluetoothGattService> supportedGattServices = BleHelper.getInstance().getSupportedGattServices();
            if (supportedGattServices != null && !supportedGattServices.isEmpty()) {
                for (BluetoothGattService supportedGattService : supportedGattServices) {
                    if (supportedGattService.getCharacteristic(BaseUUID.NOTIFY_WALLPAPER) != null &&
                            supportedGattService.getCharacteristic(BaseUUID.WRITE_WALLPAPER) != null) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            throw new IllegalStateException("Device is not connected");
        }
    }


    public static boolean isConnecting() {
        return BleHelper.getInstance().isConnecting();
    }


    public static boolean isDisconnected() {
        return BleHelper.getInstance().isDisconnected();
    }


    public static boolean isDisconnectException() {
        return BleHelper.getInstance().isConnected();
    }
}

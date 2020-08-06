package com.neoon.blesdk.core.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.neoon.blesdk.core.comm.GattError;
import com.neoon.blesdk.core.entity.BLEWriteOrRead;
import com.neoon.blesdk.core.entity.BleStatus;
import com.neoon.blesdk.core.interfaces.CommunicationListener;
import com.neoon.blesdk.core.interfaces.ConnectListener;
import com.neoon.blesdk.core.interfaces.IBleHelper;
import com.neoon.blesdk.core.interfaces.LinkedCmdBlockingQueueCallBack;
import com.neoon.blesdk.core.interfaces.NotifyReceiverListener;
import com.neoon.blesdk.core.interfaces.NotifyReceiverRawListener;
import com.neoon.blesdk.core.interfaces.ReadListener;
import com.neoon.blesdk.core.interfaces.RssiListener;
import com.neoon.blesdk.core.interfaces.WriteListener;
import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.core.utils.BLESupport;
import com.neoon.blesdk.core.utils.LinkedCmdBlockingDequeHelper;
import com.neoon.blesdk.core.utils.TimeOutUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 蓝牙帮助类,各种与硬件的数据交互都在此
 *
 * @author 东芝
 */
@SuppressLint("NewApi")
public final class BleHelper implements IBleHelper, LinkedCmdBlockingQueueCallBack {
    private static volatile BleHelper instance = null;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isInit;
    private GattCallback mGattCallback = new GattCallback();
    private long connectTimeOut = 10000;
    private long discoverServicesTimeOut = 10000;
    private long notifyEnableTimeOut = 10000;
    private BluetoothGatt gatt = null;
    private int mConnectionState = BleStatus.DEVICE_DISCONNECTED;
    private int newState;
    private Handler mBLEHandler = new Handler(Looper.getMainLooper());
    private CopyOnWriteArrayList<ConnectListener> mConnectListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<CommunicationListener> communicationListeners = new CopyOnWriteArrayList<>();

    private BleHelper(Context context) {
        mContext = context.getApplicationContext();
        mBLEHandler = new Handler(context.getMainLooper());
        initBleAdapter();
    }

    public static BleHelper getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (BleHelper.class) {
                if (instance == null) {
                    instance = new BleHelper(context);
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------内部函数-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    private void initBleAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = DZBLESDK.getBluetoothAdapter();
        }
    }

    /**
     * 每次切换设备须重新初始化
     *
     * @return true init success
     */
    private boolean initBLE() {
        if (mBluetoothAdapter == null) {
            initBleAdapter();
        }
        if (!isBluetoothAvailable()) {
            return false;
        }
        isInit = true;
        return true;
    }


    private boolean connectDevice(final String address) {

        final BluetoothDevice mRemoteDevice = mBluetoothAdapter
                .getRemoteDevice(address);
        if (mRemoteDevice == null) {
            BLELog.w("mRemoteDevice == null");
            return false;
        }
        // 某些设备需要蓝牙LE交互在UI线程上运行。比如samsung
        mBLEHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //DeadObjectException
                    if (gatt != null) {
                        gatt.close(); //连接前close一下
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                setConnectionState(BleStatus.DEVICE_CONNECTING);
                if (mGattCallback == null) {//有些系统会释放这个
                    mGattCallback = new GattCallback();
                }
                gatt = connectGattCompat(mRemoteDevice);

            }
        }, 500);

        TimeOutUtil.removeTimeOut(mBLEHandler);
        TimeOutUtil.setTimeOut(mBLEHandler, connectTimeOut,
                new TimeOutUtil.OnTimeOutListener() {

                    @Override
                    public void onTimeOut() {
                        disconnect();
                        onFailedOnUIThread(GattError.ERROR_TIMEOUT_CONNECT);
                    }
                });

        return true;
    }

    private BluetoothGatt connectGattCompat(BluetoothDevice mRemoteDevice) {

        //主要解决 status=133问题
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                BLELog.d("connectGattCompat >= M");
                return mRemoteDevice.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BLELog.d("connectGattCompat >= L");
                //参考 https://stackoverflow.com/questions/28018722/android-could-not-connect-to-bluetooth-device-on-lollipop
                Method connectGattMethod = null;
                try {
                    connectGattMethod = mRemoteDevice.getClass().getMethod("connectGatt", Context.class, boolean.class, BluetoothGattCallback.class, int.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                try {
                    return (BluetoothGatt) connectGattMethod.invoke(mRemoteDevice, mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException ignored) {
        }
        BLELog.d("connectGattCompat = DEF");
        return mRemoteDevice.connectGatt(mContext, false, mGattCallback);
    }

    /**
     * 向设备发送指令
     */
    private boolean commit(byte[] data,
                           UUID mGattServiceUUID, UUID mGattCharacteristicUUID, boolean isWrite) {
        if (getConnectionState() != BleStatus.DEVICE_CONNECTED) {
            return false;
        }
        gatt = getBluetoothGatt();
        if (gatt == null) {
            BLELog.w("gatt == null");
            return false;
        }

        BluetoothGattService mGattService = gatt.getService(mGattServiceUUID);
        if (mGattService == null) {
            BLELog.w("mGattService == null");
            return false;
        }

        BluetoothGattCharacteristic mCharacteristic = mGattService.getCharacteristic(mGattCharacteristicUUID);
        if (mCharacteristic == null) {
            BLELog.w("mCharacteristic == null");
            return false;
        }

        mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        boolean setValue = mCharacteristic.setValue(data);
        boolean status;
        try {
            if (isWrite) {
                status = gatt.writeCharacteristic(mCharacteristic);
            } else {
                status = gatt.readCharacteristic(mCharacteristic);
            }
        } catch (SecurityException e) {
            BLELog.d("错误 无法写入 writeCharacteristic " + e);
            //如果是权限被禁止, 默认让它成功吧 不然闪退或会导致无限重试拖垮手机
            status = true;
        }
        //BLELog.d("send status =" + status + ", setValue=" + setValue);
        if (status) {
            BLELog.d("发送:" + BaseBleDataHelper.toHexString(data));
            // BLELog.d("send status =" + true);
        } else {
//            //通过LinkedCmdBlockingQueueHelper 进行重试
        }
        return status;
    }

    /**
     * 刷新蓝牙GATT缓存
     *
     * @param gatt
     */
    private void refreshDeviceCache(final BluetoothGatt gatt) {
        try {
            @SuppressLint("PrivateApi") Method refresh = gatt.getClass().getDeclaredMethod("refresh");
            if (refresh != null) {
                refresh.setAccessible(true);
                refresh.invoke(gatt);
            }
        } catch (Exception e) {
            BLELog.w(e.getMessage());
        }
    }

    /**
     * 检查蓝牙可用
     */
    private boolean isBluetoothAvailable() {
        if (!DZBLESDK.isBluetoothSupportBLE()) {
            BLELog.d("isBluetoothSupportBLE = false");
            return false;
        }
        if (!DZBLESDK.isBluetoothEnable()) {
            BLELog.d("isBluetoothEnable = false");
            return false;
        }
        return true;
    }

    private void setConnectionState(int mConnectionState) {
        this.mConnectionState = mConnectionState;
    }

    private int getConnectionState() {
        return mConnectionState;
    }

    @Override
    public boolean onTake(BLEWriteOrRead obj) {
        return commit(obj.data, obj.mGattServiceUUID, obj.mGattCharacteristicUUID, obj.isWrite);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------内部函数-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------蓝牙回调处理-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    private class GattCallback extends BluetoothGattCallback {
        long startConnectTime = System.nanoTime();

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt,
                                            final int status, int newState) {
            TimeOutUtil.removeTimeOut(mBLEHandler);

            BLELog.w("onConnectionStateChange[" + status + "->" + newState + "]");
            BleHelper.this.newState = newState;

            if (status != BluetoothProfile.STATE_DISCONNECTED && status != BluetoothProfile.STATE_CONNECTED && status != BluetoothProfile.STATE_CONNECTING && status != BluetoothProfile.STATE_DISCONNECTING) {
                disconnect();
                close();
                onFailedOnUIThread(status);
                return;
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                long disconncetTime = System.nanoTime();
                long offset = ((Math.abs(startConnectTime - disconncetTime)) / 1000000);
                BLELog.w("disConnected time offset=" + offset);
                //异常判断,经过测试2~10内则为异常,属于超快速断开(则 连接成功突然马上又断开的那种情况(出现这种情况通常是与 设备硬件+手机系统 有关))
                if (offset <= 10) {
                    BLELog.w("异常判断,经过测试2~10内则为异常,属于超快速断开(则 连接成功突然马上又断开的那种情况(出现这种情况通常是与 设备硬件+手机系统 有关)) ");
                } else {

                }

                disconnect();
                close();

            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                startConnectTime = System.nanoTime();
                setConnectionState(BleStatus.DEVICE_CONNECTED);
                try {
                    //兼容性: 这里延迟下 再'发现服务' 让系统先保存好相关数据, 减少bug (参考了StackoverFlow 和 nordic(蓝牙芯片厂商) 的源码)
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBLEHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (gatt.discoverServices()) {//开启发现服务成功,等待发现服务
                            //超时监听
                            TimeOutUtil.setTimeOut(mBLEHandler, discoverServicesTimeOut,
                                    new TimeOutUtil.OnTimeOutListener() {

                                        @Override
                                        public void onTimeOut() {
                                            disconnect();
                                            close();
                                            onFailedOnUIThread(GattError.ERROR_TIMEOUT_DISCOVERSERVICES);
                                        }
                                    });
                        } else {
                            disconnect();
                            close();
                            onFailedOnUIThread(GattError.ERROR_SERVICES_DISCOVERED_ERROR);

                        }
                    }
                });

            }


        }


        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            BLELog.d("onServicesDiscovered");
            TimeOutUtil.removeTimeOut(mBLEHandler);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                onConnectedOnUIThread();
                boolean enable = setNotifyEnable(BaseUUID.SERVICE, BaseUUID.NOTIFY, BaseUUID.DESC, true);

                if (!enable)//开启通知失败 一般是UUID 错误之类
                {
                    disconnect();
                    close();
                    onFailedOnUIThread(GattError.ERROR_SERVICES_NOTIFY_ERROR);

                } else {
                    //通知超时
                    TimeOutUtil.setTimeOut(mBLEHandler, notifyEnableTimeOut, new TimeOutUtil.OnTimeOutListener() {
                        @Override
                        public void onTimeOut() {
                            disconnect();
                            close();
                            onFailedOnUIThread(GattError.ERROR_TIMEOUT_NOTIFY_ENABLE);
                            BLELog.d("onTimeOut");
                        }
                    });
                }
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                onFailedOnUIThread(GattError.ERROR_SERVICES_DISCOVERED_ERROR);
            }
        }


        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt,
                                          final BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (CommunicationListener listener : communicationListeners) {
                    if (listener instanceof WriteListener) {
                        ((WriteListener) listener).onWriteSuccessful(characteristic);
                    }
                }
            }
        }


        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (CommunicationListener listener : communicationListeners) {
                    if (listener instanceof ReadListener) {
                        ((ReadListener) listener).onReadSuccessful(characteristic);
                    }
                }
            }
        }

        @Override
        public void onDescriptorRead(final BluetoothGatt gatt,
                                     final BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }


//        private LinkedBlockingDequeHelper<byte[]> onCharacteristicLinkedBlockingDequeHelper = new LinkedBlockingDequeHelper<byte[]>() {
//            @Override
//            protected void onProcess(byte[] bytes) {
//                byte[] buffer = fillBytes(bytes);
//                for (CommunicationListener listener : communicationListeners) {
//                    if (listener instanceof NotifyReceiverListener) {
//                        ((NotifyReceiverListener) listener).onReceive(buffer);
//                    }
//                }
//            }
//        };


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS && descriptor.getCharacteristic().getUuid().equals(BaseUUID.NOTIFY)) {
                TimeOutUtil.removeTimeOut(mBLEHandler);
                onNotifyEnabledOnUIThread();
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            onCharacteristicLinkedBlockingDequeHelper.offer(characteristic.getValue());
            byte[] buffer = fillBytes(characteristic.getValue());
            for (CommunicationListener listener : communicationListeners) {
                if (listener instanceof NotifyReceiverListener) {
                    if (characteristic.getUuid().equals(BaseUUID.NOTIFY)) {
                        ((NotifyReceiverListener) listener).onReceive(buffer);
                    }
                }
                if (listener instanceof NotifyReceiverRawListener) {
                    ((NotifyReceiverRawListener) listener).onReceive(characteristic.getUuid(), buffer);
                }
            }
        }

        @Override
        public void onReadRemoteRssi(final BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            for (CommunicationListener listener : communicationListeners) {
                if (listener instanceof RssiListener) {
                    ((RssiListener) listener).onRemoteRssi(rssi);
                }
            }
        }

    }

    private byte[] fillBytes(byte[] value) {
        byte[] buffer = new byte[20];//防止硬件返回的数据不完整导致app闪退 把所有命令调整为20个字节 没有的补0x00
        System.arraycopy(value, 0, buffer, 0, value.length);//超快速数组复制 这里不用担心性能问题
        return buffer;
    }

    private void onConnectedOnUIThread() {
        BLELog.d("onConnected");
        if (mConnectListeners != null && !mConnectListeners.isEmpty()) {
            mBLEHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (ConnectListener mConnectListener : mConnectListeners) {
                        mConnectListener.onConnected();
                    }
                }
            });
        }
    }

    private void onDisconnectedOnUIThread() {
        BLELog.d("onDisconnected");
        if (mConnectListeners != null && !mConnectListeners.isEmpty()) {
            mBLEHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (ConnectListener mConnectListener : mConnectListeners) {
                        mConnectListener.onDisconnected();
                    }
                }
            });
        }
    }

    private void onNotifyEnabledOnUIThread() {
        BLELog.d("onNotifyEnable");
        if (mConnectListeners != null && !mConnectListeners.isEmpty()) {
            mBLEHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (ConnectListener mConnectListener : mConnectListeners) {
                        mConnectListener.onNotifyEnable();
                    }
                }
            });
        }
    }

    private void onFailedOnUIThread(final int error) {
        BLELog.d("onFailed"+error);
        if (mConnectListeners != null && !mConnectListeners.isEmpty()) {
            mBLEHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (ConnectListener mConnectListener : mConnectListeners) {
                        mConnectListener.onFailed(error);
                    }
                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------蓝牙回调处理-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------对外封装-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 连接设备
     *
     * @param address
     * @return
     */
    @Override
    public boolean connect(String address) {
        if (TextUtils.isEmpty(address)) {
            BLELog.w("address is null");
            return false;
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            BLELog.w("address is failed");
            return false;
        }
        if (!isInit) {
            initBLE();
        }
        if (isConnected()) {
            BLELog.w("device connected or connecting..");
            return false;
        }

        return connectDevice(address);
    }

    /**
     * 断开所有蓝牙(该方法是我研究源码看到的私有api 可以断开全部gatt设备 甚至能断开别人智能穿戴app的连接 )
     */
    public void disconnectAll() {
        try {
            @SuppressLint("PrivateApi")
            Method getBluetoothManager = BluetoothAdapter.class.getDeclaredMethod("getBluetoothManager");
            getBluetoothManager.setAccessible(true);
            Object managerService = getBluetoothManager.invoke(BluetoothAdapter.getDefaultAdapter());
            Method getBluetoothGatt = managerService.getClass().getDeclaredMethod("getBluetoothGatt");
            getBluetoothGatt.setAccessible(true);
            Object iGatt = getBluetoothGatt.invoke(managerService);
            Method disconnectAll = iGatt.getClass().getDeclaredMethod("disconnectAll");
            disconnectAll.setAccessible(true);
            disconnectAll.invoke(iGatt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开当前设备
     */
    @Override
    public void disconnect() {

        mBLEHandler.post(new Runnable() {
            @Override
            public void run() {
                setConnectionState(BleStatus.DEVICE_DISCONNECTED);
                try {
                    onDisconnectedOnUIThread();
                    BluetoothGatt gatt = getBluetoothGatt();
                    if (gatt != null) {
                        gatt.disconnect();
                        refreshDeviceCache(gatt);
                    }
//            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.cancelDiscovery();
//            }
                    BLELog.d("disconnect()");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.gc();
            }
        });

    }

    @Override
    public void close() {
        setConnectionState(BleStatus.DEVICE_DISCONNECTED);
        mBLEHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (gatt != null) {
                        gatt.close();
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * 连接设备监听
     *
     * @param listener
     */
    @Override
    public void addConnectListener(ConnectListener listener) {
        if (mConnectListeners != null && !mConnectListeners.contains(listener)) {
            mConnectListeners.add(listener);
        }
    }

    @Override
    public void removeConnectListener(ConnectListener listener) {
        if (mConnectListeners != null && mConnectListeners.contains(listener)) {
            mConnectListeners.remove(listener);
        }
    }


    /**
     * 通常传入
     * {@link ReadListener}
     * {@link WriteListener}
     * {@link NotifyReceiverListener}
     * {@link RssiListener}
     *
     * @param listener
     */
    @Override
    public void addCommunicationListener(CommunicationListener listener) {
        if (communicationListeners != null && !communicationListeners.contains(listener)) {
            communicationListeners.add(listener);
        }
    }

    @Override
    public void removeCommunicationListener(CommunicationListener listener) {
        if (communicationListeners != null && communicationListeners.contains(listener)) {
            communicationListeners.remove(listener);
        }
    }


    /**
     * 设置连接超时时间
     *
     * @param connectTimeOut
     */
    @Override
    public void setConnectTimeOut(long connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    @Override
    public void setNotifyEnableTimeOut(long notifyEnableTimeOut) {
        this.notifyEnableTimeOut = notifyEnableTimeOut;
    }


    /**
     * 设置发现服务超时时间
     *
     * @param discoverServicesTimeOut
     */
    @Override
    public void setDiscoverServicesTimeOut(long discoverServicesTimeOut) {
        this.discoverServicesTimeOut = discoverServicesTimeOut;
    }

    /**
     * 向设备请求通知开启
     *
     * @param mGattServiceUUID
     * @param mGattCharacteristicUUID
     * @param mGattDescriptor
     * @param enable
     * @return
     */
    @Override
    public boolean setNotifyEnable(UUID mGattServiceUUID,
                                   UUID mGattCharacteristicUUID, UUID mGattDescriptor, boolean enable) {
        BluetoothGatt gatt = getBluetoothGatt();
        if (gatt == null) {
            BLELog.w("gatt == null");
            return false;
        }
        BluetoothGattService mUUIDService = gatt.getService(mGattServiceUUID);
        if (mUUIDService == null) {
            BLELog.w("mUUIDService == null");
            return false;
        }

        BluetoothGattCharacteristic mCharacteristic = mUUIDService
                .getCharacteristic(mGattCharacteristicUUID);
        if (mCharacteristic == null) {

            BLELog.w("mCharacteristic == null");
            return false;
        }

        boolean status = gatt.setCharacteristicNotification(mCharacteristic,
                true);

        BLELog.d("boolean status = " + status);
        BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptor(mGattDescriptor);
        if (descriptor != null) {
            descriptor
                    .setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            boolean writeDescriptor = gatt.writeDescriptor(descriptor);
            BLELog.d("boolean writeDescriptor =" + writeDescriptor);
            return writeDescriptor;
        }
        return false;
    }

    private LinkedCmdBlockingDequeHelper blockingQueueHelper = new LinkedCmdBlockingDequeHelper(this);


    /**
     * 向设备发送指令
     */
    @Override
    public boolean write(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID) {
        blockingQueueHelper.commit(data, mGattServiceUUID, mGattCharacteristicUUID, true);
        return true;
//        return commit(data,  mGattServiceUUID, mGattCharacteristicUUID, true);
    }

    /**
     * 向设备发送读取指令
     */
    @Override
    public boolean read(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID) {
        blockingQueueHelper.commit(data, mGattServiceUUID, mGattCharacteristicUUID, false);
        return true;
    }


    /**
     * 读信号
     * 先设置回调{@link RssiListener} 再调用该方法
     *
     * @return
     */
    @Override
    public boolean readRemoteRssi() {
        return getBluetoothGatt().readRemoteRssi();
    }


    /**
     * @return supported services.
     * 得到列表支持 GATT 服务列表 。这方法应该在调用 BluetoothGatt.discoverServices() 之后调用
     */
    @Override
    public List<BluetoothGattService> getSupportedGattServices() {
        BluetoothGatt gatt = getBluetoothGatt();
        if (gatt == null)
            return null;
        return gatt.getServices();
    }

    @Override
    public BluetoothGatt getBluetoothGatt() {
        return gatt;
    }

    @Override
    public boolean isConnected() {

        boolean isConnected = getConnectionState() == BleStatus.DEVICE_CONNECTED;
        try {
            BluetoothGatt bluetoothGatt = getBluetoothGatt();
            if (bluetoothGatt != null) {
                BluetoothDevice device = bluetoothGatt.getDevice();
                if (device != null) {
                    int connectionState = BLESupport.getInternalConnectionState(device.getAddress());
                    if (connectionState == BLESupport.CONNECTION_STATE_UN_SUPPORT) {
                        return isConnected;
                    }
                    //系统内部连接情况
                    boolean isInternalConnected = connectionState == BLESupport.CONNECTION_STATE_CONNECTED;
                    //如果是表面是连接的 但内部断开的
                    if (isConnected && !isInternalConnected) {
                        //强制断开
                        disconnect();
                        return false;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return isConnected;
    }

    @Override
    public boolean isConnecting() {
        return getConnectionState() == BleStatus.DEVICE_CONNECTING;
    }

    @Override
    public boolean isDisconnected() {
        return getConnectionState() == BleStatus.DEVICE_DISCONNECTED;
    }

    @Override
    public boolean isDisconnectException() {
        //不等于他们就是异常断开
        return !(newState == BluetoothProfile.STATE_CONNECTING || newState == BluetoothProfile.STATE_CONNECTED || newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING);
    }


}

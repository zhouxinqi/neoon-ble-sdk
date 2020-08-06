package com.neoon.blesdk.encapsulation.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import com.neoon.blesdk.core.interfaces.NotifyReceiverListener;
import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.decode.DeviceInfoDataDecodeHelper;
import com.neoon.blesdk.decode.HealthDataDecodeHelper;
import com.neoon.blesdk.decode.OtherDataDecodeHelper;
import com.neoon.blesdk.decode.SleepDataDecodeHelper;
import com.neoon.blesdk.decode.SportDataDecodeHelper;
import com.neoon.blesdk.decode.SportModeDecodeHelper;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.interfaces.IDataDecodeHelper;

import java.util.List;

/**
 * 作者:东芝(2017/11/20).
 * 功能:蓝牙后台处理服务
 */
public class SNBLEService extends Service {

    public static boolean isServiceRunning(Context context) {
        boolean isServiceRunning = false;
        ComponentName collectorComponent = new ComponentName(context, SNBLEService.class);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (manager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            if (runningServices != null) {
                for (ActivityManager.RunningServiceInfo service : runningServices) {
                    if (service.service.equals(collectorComponent)) {
                        if (service.pid == Process.myPid()) {
                            isServiceRunning = true;
                        }
                    }
                }
            }
        }
        return isServiceRunning;
    }


    public static void startService(Context context, Class<? extends SNBLEService> service) {
        context.startService(new Intent(context, service));
    }

    public static void stopService(Context context, Class<? extends SNBLEService> service) {
        context.stopService(new Intent(context, service));
    }

    private IDataDecodeHelper mSportDataDecodeHelper;
    private IDataDecodeHelper mSportModeDecodeHelper;
    private IDataDecodeHelper mSleepDataDecodeHelper;
    private IDataDecodeHelper mHealthDataDecodeHelper;
    private IDataDecodeHelper mDeviceInfoDataDecodeHelper;
    private IDataDecodeHelper mOtherDataDecodeHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSportDataDecodeHelper = new SportDataDecodeHelper();
        mSportModeDecodeHelper = new SportModeDecodeHelper();
        mSleepDataDecodeHelper = new SleepDataDecodeHelper();
        mHealthDataDecodeHelper = new HealthDataDecodeHelper();
        mDeviceInfoDataDecodeHelper = new DeviceInfoDataDecodeHelper();
        mOtherDataDecodeHelper = new OtherDataDecodeHelper();

        SNBLEHelper.addCommunicationListener(notifyReceiverListener);
        BLELog.d("蓝牙通讯后台服务已开启");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SNBLEHelper.removeCommunicationListener(notifyReceiverListener);
        BLELog.d("蓝牙通讯后台服务已销毁");
    }

    /**
     * 通知数据回调监听
     */
    private NotifyReceiverListener notifyReceiverListener = new NotifyReceiverListener() {
        @Override
        public void onReceive(byte[] buffer) {
            try {
                BLELog.w("接收:" + SNBLEHelper.toHexString(buffer));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //解码操作...
            mDeviceInfoDataDecodeHelper.decode(buffer);
            mSportDataDecodeHelper.decode(buffer);
            mSportModeDecodeHelper.decode(buffer);
            mSleepDataDecodeHelper.decode(buffer);
            mHealthDataDecodeHelper.decode(buffer);
            mOtherDataDecodeHelper.decode(buffer);
//          mEnvironmentInfoDataDecodeHelper.decode(buffer);
        }
    };
}

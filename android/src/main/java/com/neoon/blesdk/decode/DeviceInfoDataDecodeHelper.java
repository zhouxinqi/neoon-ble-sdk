package com.neoon.blesdk.decode;


import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.decode.entity.device.DeviceInfoBean;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.interfaces.IDataDecodeHelper;
import com.neoon.blesdk.encapsulation.storage.DeviceStorage;
import com.neoon.blesdk.util.DataAnalysisUtil;
import com.neoon.blesdk.util.eventbus.SNEventBus;

/**
 * 作者:东芝(2017/11/24).
 * 功能:设备信息解析
 */
public class DeviceInfoDataDecodeHelper implements IDataDecodeHelper {


    @Override
    public void decode(byte[] buffer) {

        //解析设备基本信息
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0102")) {

            int mDeviceID = SNBLEHelper.subBytesToInt(buffer, 2, 3, 4);
            int mDeviceVersion = buffer[5] & 0xFF;
            int mDeviceBatteryStatus = buffer[6] & 0xFF;
            int mDeviceBatteryLevel = buffer[7] & 0xFF;
            int mCustomerID = SNBLEHelper.subBytesToInt(buffer, 2, 8, 9);
            //设备支持的功能定义位
            int mDeviceFeatures = buffer[10] & 0xFF;
            BLELog.d("mDeviceID=" + mDeviceID);
            BLELog.d("mDeviceVersion=" + mDeviceVersion);
            BLELog.d("mDeviceBatteryStatus=" + mDeviceBatteryStatus);
            BLELog.d("mDeviceBatteryLevel=" + mDeviceBatteryLevel);
            BLELog.d("mCustomerID=" + mCustomerID);
            BLELog.d("mDeviceFeatures=" + mDeviceFeatures);
            boolean isSupportHeartRateHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 0) == 1;
            boolean isSupportBloodOxygenHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 1) == 1;
            boolean isSupportBloodPressureHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 2) == 1;
            boolean isSupportSportModeHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 3) == 1;
            BLELog.d("心率大数据支持=" + isSupportHeartRateHistory);
            BLELog.d("血氧大数据支持=" + isSupportBloodOxygenHistory);
            BLELog.d("血压大数据支持=" + isSupportBloodPressureHistory);
            BLELog.d("运动模式支持=" + isSupportSportModeHistory);
            DeviceStorage.setDeviceCustomerId(mCustomerID);
            DeviceStorage.setDeviceVersion(mDeviceVersion);

            DeviceInfoBean deviceInfo0 = new DeviceInfoBean();
            deviceInfo0.setDeviceID(mDeviceID);
            deviceInfo0.setDeviceVersion(mDeviceVersion);
            deviceInfo0.setDeviceBatteryStatus(mDeviceBatteryStatus);
            deviceInfo0.setDeviceBatteryLevel(mDeviceBatteryLevel);
            deviceInfo0.setCustomerID(mCustomerID);
            deviceInfo0.setSupportHeartRateHistory(isSupportHeartRateHistory);
            deviceInfo0.setSupportBloodOxygenHistory(isSupportBloodOxygenHistory);
            deviceInfo0.setSupportBloodPressureHistory(isSupportBloodPressureHistory);
            deviceInfo0.setSupportSportModeHistory(isSupportSportModeHistory);

            SNEventBus.sendEvent(SNBLEEvent.EVENT_DATA_DEVICE_INFO, deviceInfo0);

        }
    }

}

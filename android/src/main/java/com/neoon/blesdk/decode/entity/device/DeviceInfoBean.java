package com.neoon.blesdk.decode.entity.device;


import com.neoon.blesdk.util.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者:东芝(2018/7/9).
 * 功能:
 */

public class DeviceInfoBean {

    /**
     * 正常使用
     */
    public static final int BATTERY_STATUS_NORMAL = 0x00;
    /**
     * 充电中
     */
    public static final int BATTERY_STATUS_CHARGING = 0x01;
    /**
     * 满电
     */
    public static final int BATTERY_STATUS_FULL = 0x02;
    /**
     * 低电量
     */
    public static final int BATTERY_STATUS_LOW = 0x03;


	@IntDef({
            BATTERY_STATUS_NORMAL,
            BATTERY_STATUS_CHARGING,
            BATTERY_STATUS_FULL,
            BATTERY_STATUS_LOW
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BatteryStatus {
    }



    private int mDeviceID;
    private int mDeviceVersion;
    private int mDeviceBatteryStatus;
    private int mDeviceBatteryLevel;
    private int mCustomerID;
    private boolean isSupportHeartRateHistory;
    private boolean isSupportBloodOxygenHistory;
    private boolean isSupportBloodPressureHistory;
    private boolean isSupportSportModeHistory;

    public int getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(int mDeviceID) {
        this.mDeviceID = mDeviceID;
    }

    public int getDeviceVersion() {
        return mDeviceVersion;
    }

    public void setDeviceVersion(int mDeviceVersion) {
        this.mDeviceVersion = mDeviceVersion;
    }

    public @BatteryStatus int getDeviceBatteryStatus() {
        return mDeviceBatteryStatus;
    }

    public void setDeviceBatteryStatus( int mDeviceBatteryStatus) {
        this.mDeviceBatteryStatus = mDeviceBatteryStatus;
    }

    public int getDeviceBatteryLevel() {
        return mDeviceBatteryLevel;
    }

    public void setDeviceBatteryLevel(int mDeviceBatteryLevel) {
        this.mDeviceBatteryLevel = mDeviceBatteryLevel;
    }

    public int getCustomerID() {
        return mCustomerID;
    }

    public void setCustomerID(int mCustomerID) {
        this.mCustomerID = mCustomerID;
    }

    public boolean isSupportHeartRateHistory() {
        return isSupportHeartRateHistory;
    }

    public void setSupportHeartRateHistory(boolean supportHeartRateHistory) {
        isSupportHeartRateHistory = supportHeartRateHistory;
    }

    public boolean isSupportBloodOxygenHistory() {
        return isSupportBloodOxygenHistory;
    }

    public void setSupportBloodOxygenHistory(boolean supportBloodOxygenHistory) {
        isSupportBloodOxygenHistory = supportBloodOxygenHistory;
    }

    public boolean isSupportBloodPressureHistory() {
        return isSupportBloodPressureHistory;
    }

    public void setSupportBloodPressureHistory(boolean supportBloodPressureHistory) {
        isSupportBloodPressureHistory = supportBloodPressureHistory;
    }

    public void setSupportSportModeHistory(boolean supportSportModeHistory)
    {
        isSupportSportModeHistory=supportSportModeHistory;
    }

    public boolean isSupportSportModeHistory()
    {
        return isSupportSportModeHistory;
    }
}

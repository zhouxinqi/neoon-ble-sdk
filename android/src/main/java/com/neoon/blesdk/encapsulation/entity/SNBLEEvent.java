package com.neoon.blesdk.encapsulation.entity;


import com.neoon.blesdk.util.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者:东芝(2017/12/15).
 * 功能:蓝牙事件 ==> 界面/其他
 */

public class SNBLEEvent {


    @IntDef({
            SNBLEEvent.EVENT_DEVICE_CAMERA_TAKE_PHOTO,
            SNBLEEvent.EVENT_DEVICE_CALL_END_CALL,
            SNBLEEvent.EVENT_DEVICE_CALL_MUTE,
            SNBLEEvent.EVENT_DEVICE_FIND_PHONE,
            SNBLEEvent.EVENT_DEVICE_MUSIC_PLAY_OR_PAUSE,
            SNBLEEvent.EVENT_DEVICE_MUSIC_PREVIOUS,
            SNBLEEvent.EVENT_DEVICE_MUSIC_NEXT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceEvent {
    }

    @IntDef({
            SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_OXYGEN,
            SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_PRESSURE,
            SNBLEEvent.EVENT_DATA_HEALTH_HEART_RATE,
            SNBLEEvent.EVENT_DATA_REAL_TIME_SPORT_DATA,
            SNBLEEvent.EVENT_DATA_DEVICE_INFO,
            SNBLEEvent.EVENT_HISTORY_SPORT_DATA,
            SNBLEEvent.EVENT_HISTORY_SLEEP_DATA,
            SNBLEEvent.EVENT_HISTORY_HEART_RATE_DATA,
            SNBLEEvent.EVENT_HISTORY_SPORT_MODE_DATA,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceData {
    }





    public static final int EVENT_BASE_COMMAND = -1;

    /**
     * 实时步数数据
     */
    public static final int EVENT_DATA_REAL_TIME_SPORT_DATA = 0x100000;
    /**
     * 历史步数数据
     */
    public static final int EVENT_HISTORY_SPORT_DATA = 0x100001;
    /**
     * 历史睡眠数据
     */
    public static final int EVENT_HISTORY_SLEEP_DATA = 0x100002;
    /**
     * 心率历史数据
     */
    public static final int EVENT_HISTORY_HEART_RATE_DATA = 0x100003;


    /**
     *  运动模式数据
     */
    public static final int EVENT_HISTORY_SPORT_MODE_DATA = 0x100008;
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------蓝牙指令接收相关-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 获取设备基本信息0
     */
    public static final int EVENT_DATA_DEVICE_INFO = 0x200008;


    /**
     * 手环点击拍照接收
     */
    public static final int EVENT_DEVICE_CAMERA_TAKE_PHOTO = 0x200000;

    /**
     * 手环电量
     */
    public static final int EVENT_BLE_BAND_ELECTRIC = 0x200001;


    /**
     * 实时体检心率
     */
    public static final int EVENT_DATA_HEALTH_HEART_RATE = 0x200002;

    /**
     * 实时体检血氧
     */
    public static final int EVENT_DATA_HEALTH_BLOOD_OXYGEN = 0x200003;

    /**
     * 实时体检血压
     */
    public static final int EVENT_DATA_HEALTH_BLOOD_PRESSURE = 0x200004;

    /**
     * 挂断电话
     */
    public static final int EVENT_DEVICE_CALL_END_CALL = 0x200005;

    /**
     * 来电静音
     */
    public static final int EVENT_DEVICE_CALL_MUTE = 0x200006;

    /**
     * 手环寻找手机
     */
    public static final int EVENT_DEVICE_FIND_PHONE = 0x200007;

    /**
     * 播放或者暂停音乐
     */
    public static final int EVENT_DEVICE_MUSIC_PLAY_OR_PAUSE = 0x200009;

    /**
     * 下一首音乐
     */
    public static final int EVENT_DEVICE_MUSIC_NEXT = 0x200010;

    /**
     * 上一首音乐
     */
    public static final int EVENT_DEVICE_MUSIC_PREVIOUS = 0x200011;



}

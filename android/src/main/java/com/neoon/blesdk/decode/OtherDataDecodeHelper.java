package com.neoon.blesdk.decode;

import com.neoon.blesdk.core.ble.BaseBleDataHelper;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.interfaces.IDataDecodeHelper;
import com.neoon.blesdk.util.eventbus.SNEventBus;

/**
 * 作者:东芝(2018/7/5).
 * 功能:其他命令
 */

public class OtherDataDecodeHelper implements IDataDecodeHelper {

    @Override
    public void decode(byte[] buffer) {
        SNEventBus.sendEvent(SNBLEEvent.EVENT_BASE_COMMAND,buffer);
        //体检数据返回
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0702")) {
            //心率
            int heartRate = buffer[3] & 0xFF;
            //血氧
            int bloodOxygen = buffer[4] & 0xFF;
            //舒张压
            int diastolic = buffer[5] & 0xFF;
            //收缩压
            int systolic = buffer[6] & 0xFF;
            if (heartRate != 0) {
                SNEventBus.sendEvent(SNBLEEvent.EVENT_DATA_HEALTH_HEART_RATE, heartRate);
            }
            if (bloodOxygen != 0) {
                SNEventBus.sendEvent(SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_OXYGEN, bloodOxygen);
            }
            if (diastolic != 0 && systolic != 0) {
                SNEventBus.sendEvent(SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_PRESSURE, new int[]{diastolic, systolic});
            }
        }

        //手环点击拍照
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060101")) {
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_CAMERA_TAKE_PHOTO);
        }

        //手环找手机
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060201")) {
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_FIND_PHONE);
        }

        //手环震动
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0102")) {
            SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_BAND_ELECTRIC, buffer);
        }

        //来电挂断
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060301")) {
             SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_CALL_END_CALL);
        }

        //来电静音
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060302")) {
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_CALL_MUTE);
        }

        //播放音乐下一首
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060401")){
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_NEXT);
        }

        //播放音乐开始或暂停
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060402")){
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_PLAY_OR_PAUSE);
        }

        //播放音乐上一首
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"060403")){
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_PREVIOUS);
        }
    }
}

package com.neoon.blesdk.decode;


import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.decode.entity.sport.SportModeBean;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.interfaces.IDataDecodeHelper;
import com.neoon.blesdk.util.DateUtil;
import com.neoon.blesdk.util.IF;
import com.neoon.blesdk.util.eventbus.SNEventBus;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 作者:东芝(2019/6/3).
 * 功能:运动模式解码
 */

public class SportModeDecodeHelper implements IDataDecodeHelper
{

    private ArrayList<SportModeBean> sportModeBeans = new ArrayList<>();

    @Override
    public void decode(byte[] buffer) {
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"070B")) {

            int index = buffer[3] & 0xff;
            if(index==0){
                sportModeBeans.clear();
            }

            int type = buffer[4] & 0xff;
            //从2000-01-01 00:00:00至今的秒数
            int startTimeSeconds = SNBLEHelper.subBytesToInt(buffer, 4, 5, 8);
            if(startTimeSeconds==0)return;

            Calendar instance = Calendar.getInstance();
            instance.set(2000,0,1,0,0,0);
            instance.add(Calendar.SECOND,startTimeSeconds);

            String beginDateTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, instance);
            String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, instance);
            int takeMinutes = SNBLEHelper.subBytesToInt(buffer, 2, 9, 10);
            instance.add(Calendar.MINUTE,takeMinutes);
            String endDateTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, instance);

            int step = SNBLEHelper.subBytesToInt(buffer, 2, 11, 12);
            int distance = SNBLEHelper.subBytesToInt(buffer, 2, 13, 14);
            int calories = SNBLEHelper.subBytesToInt(buffer, 2, 15, 16);
            int rateMaxHeart = buffer[17] & 0xFF;
            int rateMinHeart = buffer[18] & 0xFF;
            int rateAvgHeart = buffer[19] & 0xFF;

//            BLELog.d("运动模式数据:类型:%d,开始时间:%s,结束时间:%s,持续时间:%d分钟", type, beginDateTime,endDateTime, takeMinutes);
//            BLELog.d("运动模式数据:步数:%d,距离:%d,卡路里:%d,心率最大:%d,最小:%d,平均:%d", step, distance, calories, rateMaxHeart, rateMinHeart, rateAvgHeart);

            SportModeBean sportModeBean = new SportModeBean();
            sportModeBean.setModeType(type);
            sportModeBean.setBeginDateTime(beginDateTime);
            sportModeBean.setEndDateTime(endDateTime);
            sportModeBean.setTakeMinutes(takeMinutes);
            sportModeBean.setStep(step);
            sportModeBean.setDistance(distance);
            sportModeBean.setCalorie(calories);
            sportModeBean.setHeartRateMax(rateMaxHeart);
            sportModeBean.setHeartRateMin(rateMinHeart);
            sportModeBean.setHeartRateAvg(rateAvgHeart);
            sportModeBean.setDate(date);
            sportModeBeans.add(sportModeBean);

        }
        //运动模式接收完毕
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"07F901")){
            BLELog.w("运动模式数据:解析完成");
            if(IF.isEmpty(sportModeBeans)){
                BLELog.w("运动模式数据:解析有丢失或无数据,不保存");
                return;
            }
            SNEventBus.sendEvent(SNBLEEvent.EVENT_HISTORY_SPORT_MODE_DATA,sportModeBeans);

        }
    }




}

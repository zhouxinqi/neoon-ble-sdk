package com.neoon.blesdk.decode;


import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.decode.entity.health.HeartRateBean;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.interfaces.IDataDecodeHelper;
import com.neoon.blesdk.util.DateUtil;
import com.neoon.blesdk.util.IF;
import com.neoon.blesdk.util.eventbus.SNEventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2017/11/24).
 * 功能:体检数据解析
 * 1.实时心率血氧舒张压收缩压 解析
 * 2.心率大数据解析
 * 3.血压和血氧大数据未写 因为设备还没支持
 */
public class HealthDataDecodeHelper implements IDataDecodeHelper {

    private Calendar mHeartRateDataCalendar;

    private ArrayList<HeartRateBean.HeartRateDetailsBean> heartRates = new ArrayList<>();

    /**
     * @param buffer
     */
    @Override
    public void decode(byte[] buffer) {

        //解析实时心率血氧舒张压收缩压等数据
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0702")) {
            BLELog.w("实时体检数据:解析开始");
            int mHeartRate = buffer[3] & 0xFF;
            int mBloodOxygen = buffer[4] & 0xFF;
            int mBloodDiastolic = buffer[5] & 0xFF;
            int mBloodSystolic = buffer[6] & 0xFF;

            if (IF.isEquals(mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic, 0)) {
                BLELog.w("实时体检数据:数据异常,心率:%d,血氧:%d,舒张压:%d,收缩压:%d", mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic);
                return;
            }
            BLELog.w("实时体检数据:心率:%d,血氧:%d,舒张压:%d,收缩压:%d", mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic);
            BLELog.w("实时体检数据:解析完成");


            if (mHeartRate != 0) {
                if (mHeartRate < 55 || mHeartRate > 200) {
                    BLELog.w("实时体检数据:数据异常,心率:%d,血氧:%d,舒张压:%d,收缩压:%d", mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic);
                    mHeartRate = 0;
                }

            }
            if (mBloodOxygen != 0) {

            }
            if (mBloodDiastolic != 0 && mBloodSystolic != 0) {

            }

        }
        //心率大数据解析
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0707")) {

            //心率数据 00:00~23:54 视为一天,每15分钟一个记录,一共11个序号,两天共96条数据
            //今天00:00-->今天23:54->昨天00:00->昨天23:54
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                BLELog.w("心率大数据:解析开始");
                mHeartRateDataCalendar = DateUtil.getCurrentCalendarBegin();
                heartRates.clear();
            }
            if (mHeartRateDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mHeartRateDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }
            for (int i = 4; i < 20; i++) {

                int heart = buffer[i] & 0xff;
                int timeIndex = DateUtil.convertTimeToIndex(mHeartRateDataCalendar, 1/*1分钟为一个索引,一共1440个,为什么不是15分钟一次?因为我要存实时数据*/);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mHeartRateDataCalendar);
                if (heart != 0) {
                    BLELog.w("心率大数据:%d,index:%d,时间:%s", heart, timeIndex, date);
                    if (heart < 55 || heart > 200) {
                        BLELog.w("心率大数据:数据异常,心率:%d", heart);
                        heart = 0;
                    }

                }

                heartRates.add(new HeartRateBean.HeartRateDetailsBean(timeIndex, date, heart, 1/*(默认0=手动检测,1=自动检测)*/));
                mHeartRateDataCalendar.add(Calendar.MINUTE, +15);
            }

        }

        //心率大数据同步成功
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"07FD")) {
            BLELog.w("心率大数据:解析完成");
            List<HeartRateBean> mHistoryHeartRateBeans = new ArrayList<>();
            //非空判断
            if (IF.isEmpty(heartRates)) {
                BLELog.w("心率大数据:心率大数据有丢失,不保存 数据数量:" + heartRates.size());
                return;
            }
            BLELog.w("心率大数据:本地保存开始");
            //把解析出来的数据 按天分割 分别装
            List<ArrayList<HeartRateBean.HeartRateDetailsBean>> heartRateDetailSet = getList(heartRates);
            //数据量(天数)
            int size = heartRateDetailSet.size();
            //保存两天的数据(如果有的画)
            for (int i = 0; i < size; i++) {
                HeartRateBean heartRateBean = new HeartRateBean();
                try {
                    ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetailsBeans = heartRateDetailSet.get(i);
                    String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, heartRateDetailsBeans.get(0).getDateTime());
                    heartRateBean.setHeartRateDetails(heartRateDetailsBeans);
                    int[] minAvgMaxTotal = getMinAvgMaxTotal(heartRateDetailsBeans);
                    heartRateBean.setMin(minAvgMaxTotal[0]);
                    heartRateBean.setAvg(minAvgMaxTotal[1]);
                    heartRateBean.setMax(minAvgMaxTotal[2]);
                    heartRateBean.setDate(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHistoryHeartRateBeans.add(heartRateBean);
            }
            SNEventBus.sendEvent(SNBLEEvent.EVENT_HISTORY_HEART_RATE_DATA, mHistoryHeartRateBeans);
        }
    }


    /**
     * 把n个数据转成n个96的集合
     *
     * @param list
     * @param <T>
     * @return
     */
    private <T> List<ArrayList<T>> getList(List<T> list) {
        List<ArrayList<T>> beans = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            beans.add(new ArrayList<>(list.subList(i, i += 96)));
        }
        return beans;
    }


    /**
     * 取得大中小
     *
     * @param list
     * @return
     */
    public int[] getMinAvgMaxTotal(List<? extends HeartRateBean.HeartRateDetailsBean> list) {
        int[] min_avg_max = new int[3];
        int total = 0;
        int total_count = 0;
        min_avg_max[0] = Integer.MAX_VALUE;
        min_avg_max[2] = -Integer.MAX_VALUE;
        for (HeartRateBean.HeartRateDetailsBean bean : list) {
            int value = bean.getValue();
            if (value == 0) {
                continue;
            }
            min_avg_max[0] = Math.min(min_avg_max[0], value);
            min_avg_max[2] = Math.max(min_avg_max[2], value);
            total += value;
            total_count++;
        }
        if (total > 0) {
            min_avg_max[1] = Math.round(total / total_count);
        } else {
            min_avg_max[0] = 0;
            min_avg_max[1] = 0;
            min_avg_max[2] = 0;
        }
        return min_avg_max;
    }

}

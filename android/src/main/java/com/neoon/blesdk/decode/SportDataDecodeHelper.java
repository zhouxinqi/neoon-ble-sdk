package com.neoon.blesdk.decode;


import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.decode.entity.sport.SportBean;
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
 * 功能:运动数据解码 实时步数/历史步数/历史距离/历史卡路里
 */
public class SportDataDecodeHelper implements IDataDecodeHelper {
    /**
     * 时间间隔
     */
    public static final int EVERY_MINUTES = 30;
    /**
     * 一天的运动数据最大长度
     */
    public static final int DAY_SPORT_LENGTH = 48;
    private Calendar mDistanceDataCalendar;
    private Calendar mStepDataCalendar;
    private Calendar mCaloriesDataCalendar;

    private ArrayList<SportBean.CalorieBean> calories = new ArrayList<>();
    private ArrayList<SportBean.DistanceBean> distances = new ArrayList<>();
    private ArrayList<SportBean.StepBean> steps = new ArrayList<>();


    @Override
    public void decode(byte[] buffer) {
//
        //解析实时运动数据
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0701")) {
            BLELog.w("实时运动数据:解析开始");
            int real_time_step = SNBLEHelper.subBytesToInt(buffer, 4, 3, 6);
            int real_time_distance = SNBLEHelper.subBytesToInt(buffer, 4, 7, 10);
            int real_time_calorie = SNBLEHelper.subBytesToInt(buffer, 4, 11, 14);
//            int time = SNBLEHelper.subBytesToInt(buffer, 4, 15, 16);//该值是错的 永远是65536
            boolean case1 = real_time_step == 0 && (real_time_distance > 0 || real_time_calorie > 0);//无步数数据 但有距离或卡路里数据!
            boolean case2 = real_time_calorie > 10000;//一万千卡! 错误的数据
            if (case1 || case2) {
                BLELog.w("实时运动数据:数据异常 步数:%d,距离:%d,卡路里:%d", real_time_step, real_time_distance, real_time_calorie);
                return;
            }
            BLELog.w("实时运动数据:步数:%d,距离:%d,卡路里:%d", real_time_step, real_time_distance, real_time_calorie);
            BLELog.w("实时运动数据:解析结束");
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DATA_REAL_TIME_SPORT_DATA,new int[]{real_time_step, real_time_distance, real_time_calorie});
        }
        //解析历史步数数据
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0703")) {
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                BLELog.w("运动大数据:解析开始");
                mStepDataCalendar = DateUtil.getCurrentCalendarBegin();
                steps.clear();//初始化 开始装
            }
            if (mStepDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天

                mStepDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }

            for (int i = 4; i < 20; i += 2) {
                int val = SNBLEHelper.subBytesToInt(buffer, 2, i, i + 1);
                int timeIndex = DateUtil.convertTimeToIndex(mStepDataCalendar, EVERY_MINUTES);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mStepDataCalendar);
                if (val != 0) {
                    BLELog.w("计步大数据:%d,index:%d,时间:%s", val, timeIndex, date);
                }
                steps.add(new SportBean.StepBean(timeIndex, date, val));
                //++
                mStepDataCalendar.add(Calendar.MINUTE, +EVERY_MINUTES/*min*/);
            }

        }
        //解析历史距离数据
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0705")) {
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                mDistanceDataCalendar = DateUtil.getCurrentCalendarBegin();
                distances.clear();
            }
            if (mDistanceDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mDistanceDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }

            for (int i = 4; i < 20; i += 2) {
                int val = SNBLEHelper.subBytesToInt(buffer, 2, i, i + 1);
                int timeIndex = DateUtil.convertTimeToIndex(mDistanceDataCalendar, EVERY_MINUTES);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mDistanceDataCalendar);
                if (val != 0) {
                     BLELog.w("距离大数据:%d,index:%d,时间:%s", val, timeIndex, date);
                }
                distances.add(new SportBean.DistanceBean(timeIndex, date, val));
                mDistanceDataCalendar.add(Calendar.MINUTE, +EVERY_MINUTES/*min*/);
            }

        }
        //解析卡路里距离数据
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0706")) {
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                mCaloriesDataCalendar = DateUtil.getCurrentCalendarBegin();
                calories.clear();
            }
            if (mCaloriesDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mCaloriesDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }

            for (int i = 4; i < 20; i += 2) {
                int val = SNBLEHelper.subBytesToInt(buffer, 2, i, i + 1);
                int timeIndex = DateUtil.convertTimeToIndex(mCaloriesDataCalendar, EVERY_MINUTES);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mCaloriesDataCalendar);
                if (val != 0) {
                    BLELog.w("卡路里大数据:%d,index:%d,时间:%s", val, timeIndex, date);
                    if (val > 10000) {
                        val = 0;
                        BLELog.w("卡路里大数据:数据异常  卡路里:%d", val);
                    }
                }
                calories.add(new SportBean.CalorieBean(timeIndex, date, val));
                mCaloriesDataCalendar.add(Calendar.MINUTE, +EVERY_MINUTES/*min*/);
            }


        }
        //运动大数据同步成功
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"07FF")) {
            BLELog.w("运动大数据:解析完成");
            //他们的长度都全等于吗
            if (!IF.isEquals(steps.size(), distances.size(), calories.size())) {
                BLELog.w("运动大数据:解析有丢失,不保存 步数数据数量:%d,距离数据数量:%d,卡路里数据数量:%d", steps.size(), distances.size(), calories.size());
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BLELog.w("运动大数据:本地保存开始");

                    //把96个数据转成2个48的集合
                    List<ArrayList<SportBean.StepBean>> stepSet = getList(steps);
                    List<ArrayList<SportBean.DistanceBean>> distanceSer = getList(distances);
                    List<ArrayList<SportBean.CalorieBean>> calorieSet = getList(calories);

                    int size = stepSet.size();
                    List<SportBean> sportBeans = new ArrayList<>();
                    try {
                        //倒过来遍历的原因是 为了先保存昨天的 再保存今天的 按顺序存 这样数据库的id 不会乱
                        for (int i = size - 1; i >= 0; i--) {
                            //创建一条一天的对象
                            ArrayList<SportBean.StepBean> steps = stepSet.get(i);
                            ArrayList<SportBean.CalorieBean> calories = calorieSet.get(i);
                            ArrayList<SportBean.DistanceBean> distances = distanceSer.get(i);
                            String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, steps.get(0).getDateTime());
                            //查询这天的数据
                            SportBean sportBean = new SportBean();
                            sportBean.setSteps(steps);
                            sportBean.setCalories(calories);
                            sportBean.setDistances(distances);

                            //设置总数数据 供快速查询
                            sportBean.setStepTotal(getTotal(steps));
                            sportBean.setCalorieTotal(getTotal(calories));
                            sportBean.setDistanceTotal(getTotal(distances));
                            //把该天的起始位置 的时间去掉时分秒 只要日期,然后昨晚该天的数据  这个SportBean.COLUMN_DATE 是唯一的
                            sportBean.setDate(date);////第0条数据的日期作为当天
                            sportBeans.add(sportBean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_HISTORY_SPORT_DATA,sportBeans);
                }
            }).start();
        }


    }


    /**
     * 把n个数据转成n个48的集合
     *
     * @param list
     * @param <T>
     * @return
     */
    private <T> List<ArrayList<T>> getList(List<T> list) {
        List<ArrayList<T>> beans = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            beans.add(new ArrayList<>(list.subList(i, i += DAY_SPORT_LENGTH)));
        }
        return beans;
    }


    /**
     * 取得总数
     *
     * @param list
     * @return
     */
    public   int getTotal(List<? extends SportBean.AbsSportBean> list) {
        int total = 0;
        if (list == null) return total;
        for (SportBean.AbsSportBean bean : list) {
            total += bean.getValue();
        }
        return total;
    }



}

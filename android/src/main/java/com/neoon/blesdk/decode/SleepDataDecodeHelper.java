package com.neoon.blesdk.decode;


import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.decode.entity.sleep.SleepBean;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.interfaces.IDataDecodeHelper;
import com.neoon.blesdk.util.DataAnalysisUtil;
import com.neoon.blesdk.util.DateUtil;
import com.neoon.blesdk.util.IF;
import com.neoon.blesdk.util.eventbus.SNEventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:东芝(2017/11/24).
 * 功能:睡眠数据解码
 */
public class SleepDataDecodeHelper implements IDataDecodeHelper {


    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Calendar startCalendar;
    private Calendar endCalendar;

    private int mDeepSleepMinutes = 0;
    private int mLightSleepMinutes = 0;
    private int mSoberSleepMinutes = 0;
    private List<SleepBean.SleepDetailsBean.SleepData> sleepDataList;
    private ArrayList<SleepBean.SleepDetailsBean> sleepDetailsBeans = new ArrayList<>();
    private Calendar copy;

    @Override
    public void decode(byte[] buffer) {
        //解析睡眠数据
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"0704")) {

            //序号
            int index = buffer[3] & 0xff;

            //  只有一组睡眠数据，则 00 是入睡醒来时间，01 02 03 是三个相应的区间数据
            //出现第二组睡眠数据，则 04 是入睡醒来时间，05 06 07 是三个相应的区间数据
            //出现第三组睡眠数据，则 08 是入睡醒来时间，09 0a 0b 是三个相应的区间数据
            //因此 是每隔4条 则解析开始时间 作为新一段数据

            if (index % 4 == 0)//数据头开始解析
            {
                if (index == 0) {
                    BLELog.w("睡眠大数据:解析开始");
                }

                addData();

                //开始时间解析
                startCalendar = getSleepCalendar(4, buffer);
                //结束时间解析
                endCalendar = getSleepCalendar(9, buffer);
                BLELog.w("睡眠大数据:入睡:%s,醒来:%s", format.format(startCalendar.getTime()), format.format(endCalendar.getTime()));
                clearData(index == 0);
                copy = DateUtil.copy(startCalendar);

            } else { //具体数据
                if (startCalendar == null) return;//错误 无包头

                //解析每条数据,一共3条 每条一共8个数据总共24条 放在 sleepDataList 里装好
                for (int i = 4; i < 20; i += 2) {
                    SleepBean.SleepDetailsBean.SleepData sleepData = new SleepBean.SleepDetailsBean.SleepData();
                    //原始值
                    int src16Bit = DataAnalysisUtil.convertTo16Bit(buffer[i], buffer[i + 1]);
                    //睡眠状态
                    int sleepStatus = DataAnalysisUtil.getSleepStatus(src16Bit);
                    //睡眠分钟数
                    int sleepMinutes = DataAnalysisUtil.getSleepMinutes(src16Bit);

                    //根据睡眠状态来累计 对应的总分钟数
                    switch (sleepStatus) {
                        case 0://浅睡
                            mLightSleepMinutes += sleepMinutes;
                            break;
                        case 1://深睡
                            mDeepSleepMinutes += sleepMinutes;
                            break;
                        case 2://醒着
                            mSoberSleepMinutes += sleepMinutes;
                            break;
                    }
                    sleepData.setMinutes(sleepMinutes);
                    sleepData.setStatus(sleepStatus);
                    sleepData.setValue(src16Bit);
                    sleepDataList.add(sleepData);
                    if (sleepMinutes != 0) {
                        copy.add(Calendar.MINUTE, sleepMinutes);
                        BLELog.w("睡眠大数据:时间%s--%d", format.format(copy.getTime()), sleepMinutes);
                    }
                }

            }

        }
        if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR+"07FE")) {
            addData();
            BLELog.w("睡眠大数据:解析完成");


            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<SleepBean> sleepBeans = new ArrayList<>();

                    if (IF.isEmpty(sleepDetailsBeans)) {
                        BLELog.w("睡眠大数据:睡眠数据有丢失/或无数据,不保存");
                        SNEventBus.sendEvent(SNBLEEvent.EVENT_HISTORY_SLEEP_DATA, sleepBeans);
                        return;
                    }

                    BLELog.w("睡眠大数据:本地保存开始");

                    //把这些数据按日期区分装好
                    LinkedHashMap<String, ArrayList<SleepBean.SleepDetailsBean>> map = new LinkedHashMap<>();
                    //逆序遍历(看不懂可以鼠标放for关键字上 按Alt+Enter 转成正序 查看逻辑)  并添加到LinkedHashMap里
                    for (int i = sleepDetailsBeans.size() - 1; i >= 0; i--) {
                        SleepBean.SleepDetailsBean sleepDetailsBean = sleepDetailsBeans.get(i);
                        //已结束时间为准 结束时间是今天 那就是属于今天的数据
                        String endTime = sleepDetailsBean.getEndDateTime();
                        //去 时分秒 只取日期
                        String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, endTime);
                        if (!map.containsKey(date)) {
                            map.put(date, new ArrayList<SleepBean.SleepDetailsBean>());
                        }
                        map.get(date).add(sleepDetailsBean);
                    }


                    //重新遍历(为什么这里重新遍历一遍 为何不放上面一次性搞定?  因为上面只是区分数据日期分类 这样这边遍历的时候好装,代码也清晰)
                    for (Map.Entry<String, ArrayList<SleepBean.SleepDetailsBean>> entry : map.entrySet()) {
                        String date = entry.getKey();
                        ArrayList<SleepBean.SleepDetailsBean> mDeviceData = entry.getValue();


                        int deepTotal = 0;
                        int lightTotal = 0;
                        int soberTotal = 0;
                        //统计当天总睡眠状态详情
                        for (SleepBean.SleepDetailsBean detailsBean : mDeviceData) {
                            deepTotal += detailsBean.getDeep();
                            lightTotal += detailsBean.getLight();
                            soberTotal += detailsBean.getSober();
                        }
                        //所有数据存放对象
                        SleepBean sleepBean = new SleepBean();

                        sleepBean.setDate(date);
                        sleepBean.setSleepDetailsBeans(mDeviceData);
                        sleepBean.setDeepTotal(deepTotal);
                        sleepBean.setLightTotal(lightTotal);
                        sleepBean.setSoberTotal(soberTotal);
                        int totalTime = deepTotal + lightTotal + soberTotal;
                        if (totalTime > 1440) {
                            BLELog.w("睡眠大数据:数据异常,用户睡眠时长为" + (totalTime / 60) + "小时,已超过24小时,不保存");
                            continue;
                        }
                        sleepBeans.add(sleepBean);
                    }
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_HISTORY_SLEEP_DATA, sleepBeans);
                    clearData(true);
                }
            }).start();

        }

    }


    private void merge(ArrayList<SleepBean.SleepDetailsBean> mLocalData, ArrayList<SleepBean.SleepDetailsBean> mDeviceData) {
        int mLocalSize = mLocalData.size();
        int mDeviceSize = mDeviceData.size();
        if (mLocalSize == 0) {
            //如果本地什么都没有,说明第一次连接,不用合并直接用手环的数据
            return;
        }
        if (mDeviceSize == 0) {
            //如果手环什么都没有,则说明手环复位了,显示本地的
            mDeviceData.clear();
            mDeviceData.addAll(mLocalData);
            return;
        }
        long mLocalBeginTime = Long.MAX_VALUE;
        long mLocalEndTime = 0;
        for (int i = 0; i < mLocalSize; i++) {
            try {
                mLocalBeginTime = Math.min(mLocalBeginTime, DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, mLocalData.get(i).getBeginDateTime()));
                mLocalEndTime = Math.max(mLocalEndTime, DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, mLocalData.get(i).getEndDateTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        long mDeviceBeginTime = Long.MAX_VALUE;
        long mDeviceEndTime = 0;
        for (int i = 0; i < mDeviceSize; i++) {
            try {
                mDeviceBeginTime = Math.min(mDeviceBeginTime, DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, mDeviceData.get(i).getBeginDateTime()));
                mDeviceEndTime = Math.max(mDeviceEndTime, DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, mDeviceData.get(i).getEndDateTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //数据一致,不用处理
        if (mLocalBeginTime == mDeviceBeginTime && mLocalEndTime == mDeviceEndTime) {
            return;
        }
        //开始时间一致,但是结束数据手环的时间更大,说明是新数据.此时直接覆盖
        if (mLocalBeginTime == mDeviceBeginTime && mLocalEndTime < mDeviceEndTime) {
            //return不处理则默认是覆盖
            return;
        }
        //开始时间不一致,但是本地结束数据的时间比手环最新一条的时间 小,  说明手环最早时间的数据居然比本地结束时间仍然要往后,说明手环复位了
        if (mLocalBeginTime != mDeviceBeginTime) {
            if (mLocalEndTime < mDeviceBeginTime) {
                //合并
                for (int i = 0; i < mDeviceSize; i++) {
                    mLocalData.add(mDeviceData.get(i));
                }
                //排序
                Collections.sort(mLocalData, new Comparator<SleepBean.SleepDetailsBean>() {
                    @Override
                    public int compare(SleepBean.SleepDetailsBean o1, SleepBean.SleepDetailsBean o2) {
                        return o1.getBeginDateTime().compareToIgnoreCase(o2.getBeginDateTime());
                    }
                });
            }
            mDeviceData.clear();
            mDeviceData.addAll(mLocalData);
        }

    }

    private void clearData(boolean all) {

        if (all) {  //这个一般是新数据来一次 就清空一次 避免旧数据乱入
            sleepDetailsBeans.clear();
        }
        //清空/初始化数据
        {
            mDeepSleepMinutes = 0;
            mLightSleepMinutes = 0;
            mSoberSleepMinutes = 0;
            sleepDataList = new ArrayList<>();
        }
    }

    private void addData() {
        //存储对象,2天数据目前理论一共4条
        if (!IF.isEmpty(sleepDataList, startCalendar, endCalendar)) {
            SleepBean.SleepDetailsBean sleepDetailsBean = new SleepBean.SleepDetailsBean();

            sleepDetailsBean.setBeginDateTime(DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, startCalendar));
            sleepDetailsBean.setEndDateTime(DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, endCalendar));

            sleepDetailsBean.setSleepData(sleepDataList);
            sleepDetailsBean.setDeep(mDeepSleepMinutes);
            sleepDetailsBean.setLight(mLightSleepMinutes);
            sleepDetailsBean.setSober(mSoberSleepMinutes);
            sleepDetailsBeans.add(sleepDetailsBean);
        }
    }


    private Calendar getSleepCalendar(int startIndex, byte[] buffer) {
        //入睡时间
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.clear();
        startCalendar.set(
                buffer[startIndex] + 2000,
                buffer[startIndex + 1] - 1,
                buffer[startIndex + 2],
                buffer[startIndex + 3],
                buffer[startIndex + 4],
                0
        );
        return startCalendar;
    }


}

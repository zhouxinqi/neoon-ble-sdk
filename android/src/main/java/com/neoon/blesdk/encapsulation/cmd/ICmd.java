package com.neoon.blesdk.encapsulation.cmd;

import android.graphics.Bitmap;

import com.neoon.blesdk.core.entity.FilePackage;

import java.util.List;

/**
 * 作者:东芝(2017/11/22).
 * 描述:设备命令 基类
 */
public interface ICmd {


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------3. 系统命令-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 3.1  请求设备重启  (重启/重置/清除数据)
     *
     * @return
     */
    byte[] setDeviceRestart();

    /**
     * 3.2 请求设备进入空中升级模式
     *
     * @return
     */
    byte[] setDeviceOTAMode();

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------4. 获取命令-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

//    /**
//     * 4.1  获取MAC地址
//     *
//     * @return
//     */
//    byte[] getDeviceMacInfo();

    /**
     * 4.2  获取设备基本信息
     *
     * @return
     */
    byte[] getDeviceInfo();

//    /**
//     * 4.3  获取设备基本信息 1
//     *
//     * @return
//     */
//    byte[] getDeviceInfoCmd1();
//
//
//    /**
//     * 4.4  获取设备闹钟提醒 信息
//     *
//     * @return
//     */
//    byte[] getAlarmReminderInfo();
//
//    /**
//     * 4.5   获取设备日程提醒 信息
//     *
//     * @return
//     */
//    byte[] getScheduleReminderInfo();
//
//    /**
//     * 4.6   获取设备喝水提醒 信息
//     *
//     * @return
//     */
//    byte[] getDrinkReminderInfo();
//
//    /**
//     * 4.7  获取设备久坐提醒 信息
//     *
//     * @return
//     */
//    byte[] getSedentaryReminderInfo();
//
//    /**
//     * 4.8  获取设备当前显示的时间 信息
//     *
//     * @return
//     */
//    byte[] getDeviceTimeInfo();
//
//    /**
//     * 4.9 获取设备 提醒信息 设置， 适配于 IOS  系统
//     * 略
//     */
//
//    /**
//     * 4.10 获取设备其他功能设置
//     */
//    byte[] getDeviceOtherInfo();
//
//
//    /**
//     * 4.11  获取夜间勿扰模式
//     */
//    byte[] getDeviceNightModeInfo();
//
//    /**
//     * 4.12  获取日程提醒 标签内容
//     *
//     * @return
//     */
//    byte[] getScheduleReminderTagInfo();
//
//
//    /**
//     * 4.13  获取设备气压，海拔，温度
//     *
//     * @return
//     */
//    byte[] getAirPressureAltitudeTemperature();


    /**
     * 4.16  设置天气
     *
     * @param weather 天气类型(见附表 天气表)
     * @param curTem  当前温度
     * @param maxTem  最大温度
     * @param minTem  最小温度
     * @return
     */
    byte[] setDeviceWeatherInfo(int weather, int curTem, int maxTem, int minTem);


    /**
     * 4.16  设置天气
     *
     * @param weather 天气类型(见附表 天气表)
     * @param curTem  当前温度
     * @param maxTem  最大温度
     * @param minTem  最小温度
     * @param dayOffset  0x00今天, 0x01明天, 0x02后天.
     * @return
     */
    byte[] setDeviceWeatherInfo(int weather, int curTem, int maxTem, int minTem, int dayOffset);

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------5.Command 0x02 设置命令 -------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 5.1  设置时间
     *
     * @param time 时间戳
     * @return
     */
    byte[] setDeviceTime(long time);

    /**
     * 5.2设置基本信息
     *
     * @param gender          1男, 2女
     * @param age             年龄
     * @param height          单位CM
     * @param weight          单位kg
     * @param handedness      0：左手  1：右手
     * @param timeUnit        0：24小时制, 1：12小时制
     * @param distanceUnit    0：公制  1：英制
     * @param temperatureUnit 0：摄氏度  1：华摄氏度
     * @param clientLanguage  0：中文, 1：英文
     * @param targetSteps     目标步数
     * @return
     */
    byte[] setDeviceUserInfo(int gender, int age, float height, float weight, int handedness, int timeUnit, int distanceUnit, int temperatureUnit, int clientLanguage, int targetSteps);


    /**
     * 5.4 设置 闹钟提醒
     *
     * @param enable      true开启/false关闭
     * @param id          序号(从0开始)
     * @param repeatWeeks 重复周期   则周日周一周二...周六, true时为 重复, false为不重复
     * @param hour        小时
     * @param minute      分钟
     * @param tag         标签 默认0
     * @return
     */
    byte[] setAlarmReminderInfo(boolean enable, int id, boolean[] repeatWeeks, int hour, int minute, int tag);

    /**
     * 5.5 设置日程提醒
     *
     * @param date 日程日期, 格式是: yyyy-MM-dd HH:mm 切记不带秒
     * @param tag  标签(默认0)
     * @return
     */
    byte[] setScheduleReminderInfo(boolean enable, int id, String date, int tag);

    /**
     * 5.6喝水提醒
     *
     * @param enable       true开启/false关闭
     * @param repeatWeeks  重复周期   则周日周一周二...周六, true时为 重复, false为不重复
     * @param beginHour    开始的小时
     * @param beginMinute  开始的分钟
     * @param endHour      结束的小时
     * @param endMinute    结束的分钟
     * @param intervalTime 时间间隔 (分钟)
     * @return
     */
    byte[] setDrinkReminderInfo(boolean enable, boolean[] repeatWeeks, int beginHour, int beginMinute, int endHour, int endMinute, int intervalTime);

    /**
     * 5.7久坐提醒
     *
     * @param enable       true开启/false关闭
     * @param repeatWeeks  重复周期   则周日周一周二...周六, true时为 重复, false为不重复
     * @param beginHour    开始的小时
     * @param beginMinute  开始的分钟
     * @param endHour      结束的小时
     * @param endMinute    结束的分钟
     * @param intervalTime 时间间隔 (分钟)
     * @return
     */
    byte[] setSedentaryReminderInfo(boolean enable, boolean[] repeatWeeks, int beginHour, int beginMinute, int endHour, int endMinute, int intervalTime);


    /**
     * 5.8 定时体检
     * 略。
     */

    /**
     * 5.9 提醒设置 ，适配于 S IOS  系统
     * 略。
     */

    /**
     * 5.10 设备其他功能设置
     * 抬腕亮屏、低电量提醒、防丢开关、心率自动体检测开关、翻腕切屏
     *
     * @param lowBattery                       true开启/false关闭
     * @param lightScreen                      true开启/false关闭
     * @param antiLost                         true开启/false关闭
     * @param heartRateAutoDetect              true开启/false关闭
     * @param cutScreen                        true开启/false关闭
     * @param heartRateAutoDetectIntervalType 心率自动检测间隔时间设置 , 参数传0: 保留,1: 15分钟, 2:30分钟 3:60分钟
     * @return
     */
    byte[] setDeviceConfigInfo(boolean lowBattery, boolean lightScreen, boolean antiLost, boolean heartRateAutoDetect, boolean cutScreen, int heartRateAutoDetectIntervalType);

    /**
     * 5.11 设置 夜间勿扰模式
     *
     * @param enable      true开启/false关闭
     * @param beginHour   开始时
     * @param beginMinute 开始分
     * @param endHour     结束时
     * @param endMinute   结束分
     * @return
     */
    byte[] setDeviceNightModeInfo(boolean enable, int beginHour, int beginMinute, int endHour, int endMinute);


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------6. 绑定命令 略-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------7.提醒命令-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 7.1 发送来电提醒
     *
     * @param title       标题
     * @param phoneNumber 号码(内容)
     * @return
     */
    List<byte[]> setCallMessage(String title, String phoneNumber);

    /**
     * 7.2发送短信提醒
     *
     * @param title   标题
     * @param content 内容
     */
    List<byte[]> setSMSMessage(String title, String content);

    /**
     * 7.3 App消息提醒
     *
     * @param title   标题
     * @param content 内容
     * @return
     */
    List<byte[]> setAppMessage(String title, String content);

//
//    /**
//     * 7.4  设置日程提醒标签内容
//     *
//     * @param id 最大数目为
//     * @param tag 标签内容
//     * @return
//     */
//    byte[] setScheduleReminderTagInfo(int id, String tag);


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------8. command 0x05 APP 端控制命令-------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 8.1 打开或关闭心率开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    byte[] setHeartRateStatus(boolean enable);

    /**
     * 8.2 相机开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    byte[] setCameraModeStatus(boolean enable);

    /**
     * 8.3 找手环
     *
     * @param enable  true开启/false关闭
     * @return
     */
    byte[] setFindDeviceStatus(boolean enable);

    /**
     * 8.4 血氧开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    byte[] setBloodOxygenStatus(boolean enable);

    /**
     * 8.5 血压开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    byte[] setBloodPressureStatus(boolean enable);


    /**
     * 8.6 来电处理状态 ， 适配于Android系统
     *
     * @param status  0:保留，默认状态。
     *                1:来电，手机已经接通，开始通话
     *                2 来电，手机已经挂断。
     * @return
     */
    byte[] setCallStatus(int status);


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------9. command 0x06 固件设备控制命令------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    //属于接收命令 不在此处解析

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------10. command 7 0x07 健康数据命令（运动，心率和睡眠）-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 10.1 取得实时运动数据
     *
     * @return
     */
    byte[] getRealTimeSportData();

//    /**
//     * 10.2  读取设备的实时心率数据 血氧 血压等
//     *
//     * @return
//     */
//    byte[] getRealTimeHeartRateData();


    /**
     * 10.3  取得 计步距离卡路里历史数据
     *
     * @return
     */
    byte[] getHistorySportData();

    /**
     * 10.4 取得 睡眠历史数据
     *
     * @return
     */
    byte[] getHistorySleepData();

    /**
     * 10.5 取得 心率历史数据
     *
     * @return
     */
    byte[] getHistoryHeartRateData();




//    /**
//     * 10.6 血压大数据
//     *
//     * @return
//     */
//    byte[] getHistoryBloodPressureData();
//
//    /**
//     * 10.7 血氧 大数据
//     *
//     * @return
//     */
//    byte[] getHistoryBloodOxygenData();


    /**
     * 5.17 app设置设备是否进入高速传输模式
     * (用完记得关闭)
     * @param enable
     */
    byte[] setHighSpeedTransportStatus(boolean enable);

    List<FilePackage> createWallpaperPackage(Bitmap src, int width, int height);

    List<FilePackage> createFilePackage(byte[] bytes);


    /**
     * 4.20 获取设备是否支持屏保及设备显示分辨率
     */
    byte[] getWallpaperScreenInfo();

    /**
     * 4.21 获取设备是否支持屏保的文字种类,以及对应的分辨率
     */
    byte[] getWallpaperFontInfo();

    /**
     * 5.18   APP  设置设备 是否 开启屏保 功能
     */

    byte[] setWallpaperEnable(boolean enable);


    /**
     * 5.19 APP  设置设备屏保 显示时间的 具体 信息
     * @param enable
     * @param isHorizontal
     * @param fontWidth
     * @param fontHeight
     * @param colorRgb888
     * @param x
     * @param y
     * @return
     * Ox00:保留
     * 0x01:设置成功
     * 0x02:显示方式不支持
     * 0x03:需要显示的字体分辨率不支持
     * 0x04:需要显示的字体颜色不支持
     * 0x05:X 轴，Y 轴起始坐标点异常
     * 0x06:X 轴，Y 轴起始坐标点正常，根据计算，已越界
     * 0x07:与其他屏保效果块重叠
     */
    byte[] setWallpaperTimeInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y);


    /**
     * 5.20 APP  设置设备屏保 显示 记步 的 具体 信息
     */
    byte[] setWallpaperStepInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y);


    /**
     * 5.21 APP 设置设备实时运动数据的上传方式
     */
    byte[] setSyncRealTimeSportDataRealTimeCallback(boolean realTime);

    /**
     * 10.9 运动模式大数据
     * @return
     */
    byte[] getHistorySportModelData();
}

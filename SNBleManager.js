'use strict';
var React = require('react-native');

var SNBleManagerNative = React.NativeModules.SNRNBleManager;

class SNBleManager {

    constructor() {
    }

    initSDK(appkey) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.initSDK(appkey, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    releaseSDK() {
        SNBleManagerNative.releaseSDK();
    }

    setDebug(debug) {
        SNBleManagerNative.setDebug(debug);
    }

    nativeLog(tag,msg)
    {
        SNBleManagerNative.NativeLog(tag,msg);
    }

    nativeLog(msg)
    {
        SNBleManagerNative.NativeLog("===>",msg);
    }

    isSDKInitialized() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isSDKInitialized((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    isBluetoothSupportVersion() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isBluetoothSupportVersion((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    isBluetoothSupportBLE() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isBluetoothSupportBLE((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    isBluetoothEnable() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isBluetoothEnable((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });

    }


    isConnected() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isConnected((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }


    isConnecting() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isConnecting((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }


    isDisconnected() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isDisconnected((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }


    getDeviceName() {
        return new Promise((fulfill) => {
            SNBleManagerNative.getDeviceName((name) => {
                if (name) {
                    fulfill(name);
                } else {
                    fulfill('');
                }
            });
        });
    }


    getDeviceMacAddress() {
        return new Promise((fulfill) => {
            SNBleManagerNative.getDeviceMacAddress((address) => {
                if (address) {
                    fulfill(address);
                } else {
                    fulfill('');
                }
            });
        });
    }

    connect(address) {
        return new Promise((fulfill,reject) => {
            SNBleManagerNative.connect(address,(error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    disconnect() {
        SNBleManagerNative.disconnect();
    }

    sendCMDNoResponse(msg) {
        SNBleManagerNative.sendCMDNoResponse(msg);
    }

    sendCMDByArrayNoResponse(msgArray) {
        SNBleManagerNative.sendCMDByArrayNoResponse(msgArray);
    }

    sendCMDByArray(msgArray) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.sendCMDByArray(msgArray, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });

    }

    sendCMD(msg) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.sendCMD(msg, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });

    }


    restartBLEService() {
        SNBleManagerNative.restartBLEService();
    }

    isBLEServiceRunning() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isBLEServiceRunning((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });

    }

    readRemoteRssi() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.readRemoteRssi((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });

    }


    checkState() {
        SNBleManagerNative.checkState();
    }

///////////////////////////////scan ble begin/////////////////////////
    isScanning() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.isScanning((msg) => {
                if (msg) {
                    reject(msg);//is scanning
                } else {
                    fulfill();
                }
            });
        });

    }

    startScan() {
        SNBleManagerNative.startScan();
    }

    stopScan() {
        SNBleManagerNative.stopScan();
    }


///////////////////////////////sacn ble end ///////////////////////////

///////////////////////////////cmd data begin/////////////////////////

    //5.1  设置时间 @param time 时间戳
    setDeviceTime(time) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setDeviceTime(time, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    setDeviceUserInfo(gender, age, height, weight, handedness, timeUnit, distanceUnit, temperatureUnit, clientLanguage, targetSteps) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setDeviceUserInfo(gender, age, height, weight, handedness, timeUnit, distanceUnit, temperatureUnit, clientLanguage, targetSteps, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    setAlarmReminderInfo(enable, id, repeatWeeks, hour, minute, tag) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setAlarmReminderInfo(enable, id, repeatWeeks, hour, minute, tag, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }


    /**
     * 5.5 设置日程提醒
     *
     * @param date 日程日期, 格式是: yyyy-MM-dd HH:mm 切记不带秒
     * @param tag  标签(默认0)
     * @return
     */
    setScheduleReminderInfo(enable, id, date, tag) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setScheduleReminderInfo(enable, id, date, tag, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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

    setDrinkReminderInfo(enable, repeatWeeks, beginHour, beginMinute, endHour, endMinute, intervalTime) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setDrinkReminderInfo(enable, repeatWeeks, beginHour, beginMinute, endHour, endMinute, intervalTime, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    setSedentaryReminderInfo(enable, repeatWeeks, beginHour, beginMinute, endHour, endMinute, intervalTime) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setSedentaryReminderInfo(enable, repeatWeeks, beginHour, beginMinute, endHour, endMinute, intervalTime, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    setDeviceConfigInfo(lowBattery, lightScreen, antiLost, heartRateAutoDetect, cutScreen, heartRateAutoDetectIntervalType) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setDeviceConfigInfo(lowBattery, lightScreen, antiLost, heartRateAutoDetect, cutScreen, heartRateAutoDetectIntervalType, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    setDeviceNightModeInfo(enable, beginHour, beginMinute, endHour, endMinute) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setDeviceNightModeInfo(enable, beginHour, beginMinute, endHour, endMinute, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 7.1 发送来电提醒
     *
     * @param title       标题
     * @param phoneNumber 号码(内容)
     * @return
     */
    setCallMessage(title, phoneNumber) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setCallMessage(title, phoneNumber, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 7.2发送短信提醒
     *
     * @param title   标题
     * @param content 内容
     */

    setSMSMessage(title, content) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setSMSMessage(title, content, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 7.3 App消息提醒
     *
     * @param title   标题
     * @param content 内容
     * @return
     */
    setAppMessage(title, content) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setAppMessage(title, content, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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

    setHeartRateStatus(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setHeartRateStatus(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 8.2 相机开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    setCameraModeStatus(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setCameraModeStatus(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 8.3 找手环
     *
     * @param enable  true开启/false关闭
     * @return
     */
    setFindDeviceStatus(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setFindDeviceStatus(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 8.4 血氧开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    setBloodOxygenStatus(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setBloodOxygenStatus(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 8.5 血压开关
     *
     * @param enable  true开启/false关闭
     * @return
     */
    setBloodPressureStatus(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setBloodPressureStatus(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 8.6 来电处理状态 ， 适配于Android系统
     *
     * @param status  0:保留，默认状态。
     *                1:来电，手机已经接通，开始通话
     *                2 来电，手机已经挂断。
     * @return
     */
    setCallStatus(status) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setCallStatus(status, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 10.1 取得实时运动数据
     *
     * @return
     */
    getRealTimeSportData() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getRealTimeSportData((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    getHistorySportData() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getHistorySportData((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 10.4 取得 睡眠历史数据
     *
     * @return
     */
    getHistorySleepData() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getHistorySleepData((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 10.5 取得 心率历史数据
     *
     * @return
     */
    getHistoryHeartRateData() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getHistoryHeartRateData((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }


    /**
     * 5.17 app设置设备是否进入高速传输模式
     * (用完记得关闭)
     * @param enable
     */
    setHighSpeedTransportStatus(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setHighSpeedTransportStatus(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

//	List<FilePackage> createWallpaperPackage(Bitmap src, int width, int height);
//
//	List<FilePackage> createFilePackage(byte[] bytes);


    /**
     * 4.20 获取设备是否支持屏保及设备显示分辨率
     */
    getWallpaperScreenInfo() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getWallpaperScreenInfo((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 4.21 获取设备是否支持屏保的文字种类,以及对应的分辨率
     */
    getWallpaperFontInfo() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getWallpaperFontInfo((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 5.18   APP  设置设备 是否 开启屏保 功能
     */
    setWallpaperEnable(enable) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setWallpaperEnable(enable, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

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
    setWallpaperTimeInfo(enable, isHorizontal, fontWidth, fontHeight, colorRgb888, x, y) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setWallpaperTimeInfo(enable, isHorizontal, fontWidth, fontHeight, colorRgb888, x, y, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 5.20 APP  设置设备屏保 显示 记步 的 具体 信息
     */
    setWallpaperStepInfo(enable, isHorizontal, fontWidth, fontHeight, colorRgb888, x, y) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setWallpaperStepInfo(enable, isHorizontal, fontWidth, fontHeight, colorRgb888, x, y, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 5.21 APP 设置设备实时运动数据的上传方式
     */
    setSyncRealTimeSportDataRealTimeCallback(realTime) {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.setSyncRealTimeSportDataRealTimeCallback(realTime, (error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    /**
     * 10.9 运动模式大数据
     * @return
     */
    getHistorySportModelData() {
        return new Promise((fulfill, reject) => {
            SNBleManagerNative.getHistorySportModelData((error) => {
                if (error) {
                    reject(error);
                } else {
                    fulfill();
                }
            });
        });
    }

    toast(msg) {
        SNBleManagerNative.toast(msg);
    }

///////////////////////////////cmd data end ///////////////////////////
}

module.exports = new SNBleManager();

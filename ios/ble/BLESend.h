//
//  BLESend.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/10.
//  Copyright © 2018年 黄建华. All rights reserved.
//


// 蓝牙发送协议
#import <Foundation/Foundation.h>
#import "WeatherModel.h"
#import "KKBLEType.h"

@interface BLESend : NSObject

// 设置时间
- (void)sendSetTimeforDeviceWithUpdate:(KKBLEAcceptBlock)block;
// 获取设备mac地址
- (void)sendGetDeviceMacAddress:(KKBLEAcceptBlock)block;
// 获取设备基本信息
- (void)sendGetDeviceBasicInfoWithUpdate:(KKBLEAcceptBlock)block;
// 设置用户信息
- (void)sendUserInfoSettingWithBlock:(KKBLEAcceptBlock)block;
// 发送低电 抬腕 防丢 心率检测 翻腕切屏 备注:state是总开关  体温检测是否打开 默认开 SN_USER.temAutoCheck
- (void)sendSetLowElecState:(BOOL)state WithUpdateBlock:(KKBLEAcceptBlock)block;
// 消息推送 电话 短信   备注:state是总开关
- (void)sendSetPushNoticeState:(BOOL)state WithUpdateBlock:(KKBLEAcceptBlock)block;
// 实时步数
- (void)sendGetRealStepWithUpdate:(KKBLEAcceptBlock)block;
// 记步大数据
- (void)sendBigStepWithUpdate:(KKBLEAcceptBlock)block;
// 打开 1心率/2血氧/3血压  打开之后检测灯会闪绿灯
- (void)sendHeartStatus:(BOOL)status type:(NSInteger)type WithUpdate:(KKBLEAcceptBlock)block;
// 找手环 开启或者关闭
- (void)sendSetFindBle:(BOOL)Find withUpdateBlock:(KKBLEAcceptBlock)block;
// 睡眠大数据
- (void)sendBigSleepWithUpdate:(KKBLEAcceptBlock)block;
// 心率大数据
- (void)sendBigheartWithUpdate:(KKBLEAcceptBlock)block;
// 免打扰
- (void)sendNotDisturbWithBlock:(KKBLEAcceptBlock)block;
// 喝水提醒
- (void)sendDrinkWithUpdateBlock:(KKBLEAcceptBlock)block;
// 久坐提醒
- (void)sendSedentaryRemindWithUpdateBlock:(KKBLEAcceptBlock)block;
// 闹钟
- (void)sendSetDeviceAlarmClockwithUpdateBlock:(KKBLEAcceptBlock)block;
// 日常提醒
- (void)sendSetDeviceCalendarwithUpdateBlock:(KKBLEAcceptBlock)block;
// 拍照控制
- (void)sendControlTakePhotoState:(BOOL)type WithUpdateBlock:(KKBLEAcceptBlock)block;
//设备重启(数据清零)
- (void)sendDeviceRebootWithUpdate:(KKBLEAcceptBlock)block;
// 设置天气 0x00今天   0x01明天   0x02后天  .....
- (void)sendTemperatureParamWithDays:(NSArray<WeatherModel *>*)arrays withBlock:(KKBLEAcceptBlock)block;
// 获取运动模式数据
- (void)sendSportModeDataWithBlock:(KKBLEAcceptBlock)block;
// 升级指令 针对奉加微平台
- (void)sendUpdateFirmwareWithUpdateBlock:(KKBLEAcceptBlock)block;
// 升级指令 针对NRF芯片平台
- (void)sendNRFUpdateFirmwareWithUpdateBlock:(KKBLEAcceptBlock)block;
// 体温大数据
- (void)sendBigTemWithUpdate:(KKBLEAcceptBlock)block;
@end

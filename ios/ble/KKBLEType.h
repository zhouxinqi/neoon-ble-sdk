//
//  KKBLEType.h
//
//  Created by zorro on 2017/11/13.
//  Copyright © 2017年 zorro. All rights reserved.
//

#ifndef KKBLEType_h
#define KKBLEType_h
#endif

#undef    AS_SINGLETON
#define AS_SINGLETON(__class) \
+ (__class *)sharedInstance;

#undef    DEF_SINGLETON
#define DEF_SINGLETON(__class) \
+ (__class *)sharedInstance \
{ \
static dispatch_once_t once; \
static __class         *__singleton__; \
dispatch_once(&once, ^{ __singleton__ = [[self alloc] init]; }); \
return __singleton__; \
}
#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "BLEDeviceModel.h"

// 最后链接的设备
#define KK_BLELastDevice @"KKBLELastDevice"
#define KK_BLELastChip @"KK_BLELastChip"
#define KK_BLELastDeviceMac @"KK_BLELastDeviceMac"
#define GF_LastUpdateUUID @"AJ_LastUpdateUUID"

// 蓝牙设备服务特征值
#define BLESERVICE [CBUUID UUIDWithString:@"8F400001-CFB4-14A3-F1BA-F61F35CDDBAF"]
#define BLEWRITE   [CBUUID UUIDWithString:@"8F400002-CFB4-14A3-F1BA-F61F35CDDBAF"]
#define BLENOTIFY  [CBUUID UUIDWithString:@"8F400003-CFB4-14A3-F1BA-F61F35CDDBAF"]
#define BLEUPDATESERVICE [CBUUID UUIDWithString:@"00001530-1212-EFDE-1523-785FEABCD123"]
#define BLEUPDATECONTROL [CBUUID UUIDWithString:@"00001531-1212-EFDE-1523-785FEABCD123"]
#define BLEUPDATEPACKET [CBUUID UUIDWithString:@"00001532-1212-EFDE-1523-785FEABCD123"]
#define BLEUPDATEVERSION [CBUUID UUIDWithString:@"00001534-1212-EFDE-1523-785FEABCD123"]

#define SERVICE_OTA_UUID1                                   @"5833ff01-9b8b-5191-6142-22a4536ef123"
#define CHARACTERISTIC_OTA_WRITE_UUID1                        @"5833ff02-9b8b-5191-6142-22a4536ef123"
#define CHARACTERISTIC_OTA_INDICATE_UUID1                     @"5833ff03-9b8b-5191-6142-22a4536ef123"
#define CHARACTERISTIC_OTA_DATA_WRITE_UUID1                   @"5833ff04-9b8b-5191-6142-22a4536ef123"

typedef enum {
    KKBLEUpdateNormal     = 0,           // 正常状态
    KKBLEUpdateConnecting = 1,           // 链接中
    KKBLEUpdateDidConnect = 2,           // 连接
    KKBLEUpdateDisConnect = 3,           // 断开
    KKBLEUpdateRSSI       = 4,           // 更新RSSI
    KKBLEUpdateNotBLE     = 5,           // 没有开启蓝牙
    KKBLEUpdateRemove     = 6,            // 移除设备
    KKBLEUpdatePowerOff   = 7,           // 没有开启蓝牙
    KKBLEUpdatePowerOn    = 8            // 开启蓝牙
} KKBLEUpdateType;

// 蓝牙协议
typedef enum {
    KKBLEProtocolX9 = 0,
    KKBLEProtocolI6 = 1,
} KKBLEProtocol;

// 蓝牙IC方案
typedef enum {
    KKBLEICNORDIC = 0,
    KKBLEICDIALOG = 1,
    KKBLEITI      = 2,
} KKBLEICProject;

typedef enum {                                // 详细的看接口参数。
    KKBLEAcceptTypeUnKnown = 0,               // 无状态
    // 设置类
    KKBLEAcceptTypeDevideMAC,                 // MAC地址
    KKBLEAcceptTypeDevideInfo,                // 设备信息
    KKBLEAcceptTypeUserInfo,                  // 用户信息
    KKBLEAcceptTypeAlarmClock,                // 闹钟
    KKBLEAcceptTypeCalendar,                  // 日历
    KKBLEAcceptTypeDrink,                     // 喝水
    KKBLEAcceptTypeSedentary,                 // 久坐提醒
    KKBLEAcceptTypeTemperature,               // 天气
    KKBLEAcceptTypeTime,                      // 时间信息
    KKBLEAcceptTypeNotify,                    // 通知
    KKBLEAcceptTypeOther,                     // 其他功能
    KKBLEAcceptTypeQuite,                     // 勿扰模式
    KKBLEAcceptTypeHeartSwitch,               // 心率开关
    KKBLEAcceptTypeCameraSwitch,              // 相机开关
    KKBLEAcceptTypeFindBle,                   // 寻找蓝牙
    KKBLEAcceptTypeFindDevice,                // 找设备
    KKBLEAcceptTypeOxygenSwitch,              // 血氧开关
    KKBLEAcceptTypePressureSwitch,            // 血压开关
    KKBLEAcceptTypeCheckType,                 // 检测开关
    KKBLEAcceptTypePhoto,                     // 拍照
    KKBLEAcceptTypeFindPhone,                 // 找手机
    KKBLEAcceptTypeRealtimeSport,             // 实时运动
    KKBLEAcceptTypeRealtimeHeart,             // 实时心率
    KKBLEAcceptTypeRealtimeOX,                // 实时血氧
    KKBLEAcceptTypeRealtimeBP,                // 实时血压
    KKBLEAcceptTypeDataSport,                 // 运动数据
    KKBLEAcceptTypeDataSleep,                 // 睡眠数据
    KKBLEAcceptTypeDataHeart,                 // 心率数据
    KKBLEAcceptTypeDataOxygen,                // 血氧
    KKBLEAcceptTypeDataPressure,              // 血压
    BLTAcceptTypeDataSportEnd,                // 数据请求结束
    BLTAcceptTypeDataSleepEnd,                // 数据请求结束
    BLTAcceptTypeDataHeartEnd,                // 数据请求结束
    BLTAcceptTypeDataOxygenEnd,               // 数据请求结束
    BLTAcceptTypeDataPressureEnd,             // 数据请求结束
    BLTAcceptTypeDataOriginal,
    BLTAcceptTypeDataSportModeDataFinish,     // 运动模式数据
    BLTAcceptTypeDataSportModeDataOnce,       // 表示一次运动模式数据
    KKBLEAcceptTypeDataTem                    // 体温数据
} KKBLEAcceptType;

// 设备状态更新
typedef void (^KKBLEUpdateBlock)(BLEDeviceModel *model, KKBLEUpdateType type);
// 接受的数据类型，对照协议
typedef void (^KKBLEAcceptBlock)(CBPeripheral *Peripher,id object, KKBLEAcceptType type);

typedef void (^KKBLEAcceptData)(NSData *data);

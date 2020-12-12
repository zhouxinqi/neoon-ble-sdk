//
//  BLEDeviceModel.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/6.
//  Copyright © 2018年 黄建华. All rights reserved.
//


// 蓝牙设备模型
#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>

@interface BLEDeviceModel : NSObject
@property (nonatomic, strong) NSString     *bleName;                   // 设备名字
@property (nonatomic, strong) NSString     *bleUUID;                   // 设备UUID
@property (nonatomic, strong) NSString     *bleMAC;                    // 设备MAC
@property (nonatomic, strong) NSString     *bleRSSI;                   // RSSI
@property (nonatomic, assign) NSInteger    bleDeviceID;                // 设备ID
@property (nonatomic, assign) NSInteger    bleFirmwareVersion;         // 固件版本
@property (nonatomic, assign) NSInteger    bleBatteryQuantity;         // 电池电量
@property (nonatomic, strong) CBPeripheral *peripheral;             // 外设中心
@property (nonatomic, assign) BOOL         isConnected;                // 是否连接中
@property (nonatomic, strong) NSData       *bleAdv;                    // 广播
@property (nonatomic, assign) BOOL         heart;                      // 是否支持心率检测
@property (nonatomic, assign) BOOL         airPressure;                // 是否支持气压/体温
@property (nonatomic, assign) BOOL         oxygen;                     // 是否支持血氧检测
@property (nonatomic, assign) BOOL         bistoryPressure;            // 是否支持血压检测
@property (nonatomic, assign) BOOL         ancs;                       // 是否支持消息推送



@property (nonatomic, assign) NSInteger bleAdvID;                   // 广播ID

@end

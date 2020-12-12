//
//  BLEDeviceModel.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/6.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "BLEDeviceModel.h"

@implementation BLEDeviceModel

- (instancetype)init {
    self = [super init];
    if (self) {
        _bleName          = @"";
        _bleUUID          = @"";
        _bleRSSI          = @"";
        _bleMAC           = @"";
        _bleBatteryQuantity  = 0;
        _bleFirmwareVersion = 0;
        _bleDeviceID = 0;
    }
    return self;
}

// 判断当前蓝牙设备是否连接
- (BOOL)isConnected {
    if (_peripheral.state == CBPeripheralStateConnected) {
        return YES;
    }else{
        return NO;
    }
}

@end

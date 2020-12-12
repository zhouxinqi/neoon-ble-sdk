//
//  BLEService.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/10.
//  Copyright © 2018年 黄建华. All rights reserved.
//

// 蓝牙外设控制
#import <Foundation/Foundation.h>
#import "BLEConnect.h"
#import "BLESend.h"
#import "BLEAccept.h"

#define KK_BLEService [BLEService sharedInstance]
#define KK_BLESend    [BLEService sharedInstance].send
#define KK_BLEAccept  [BLEService sharedInstance].accept

@interface BLEService : NSObject<CBPeripheralDelegate>
@property (nonatomic, strong) CBPeripheral *peripheral;             // 外设
@property (nonatomic, strong) BLESend      *send;                   // 发送
@property (nonatomic, strong) BLEAccept    *accept;                 // 接收
@property (nonatomic, strong) KKBLEUpdateBlock updateModelStatus;   // 蓝牙外设更新回调


// 单设备时指定外围设备发数据.
- (void)senderDataToPeripheral:(NSData *)data;
- (void)senderDataToPeripheral64:(NSData *)data;
- (void)senderDataToPeripheralC1:(NSData *)data;
// 多设备时外围设备发数据.
- (void)senderDataToPeripheral:(NSData *)data withPeripheral:(CBPeripheral *)peripheral;
AS_SINGLETON(BLEService)

@end

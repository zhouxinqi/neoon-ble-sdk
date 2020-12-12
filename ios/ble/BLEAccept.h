//
//  BLEAccept.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/10.
//  Copyright © 2018年 黄建华. All rights reserved.
//


// 蓝牙接收类
#import <Foundation/Foundation.h>
#import "BLEAccept.h"
#import "KKBLEType.h"

@interface BLEAccept : NSObject

@property (nonatomic, strong) KKBLEAcceptBlock acceptStatus;
@property (nonatomic, assign) KKBLEAcceptType acceptType;
// 接收到蓝牙数据
- (void)updateData:(NSData *)data updatePeripheral:(CBPeripheral *)peripheral;

@end

//
//  BLEConnect.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/5.
//  Copyright © 2018年 黄建华. All rights reserved.
//


// 蓝牙控制中心
#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "BLEService.h"
#import "KKBLEType.h"
#import "SNPublicClass.h"
#define BLEHEAD 0x05        //产品协议头 默认是0x05
#define BLEFOUNDTIME         10.0                   // 蓝牙搜索的时间
#define KK_BLELastDeviceUuid @"KKBLELastDeviceUuid" // 绑定设备的uuid
#define KK_BLELastDeviceMac  @"KK_BLELastDeviceMac" // 绑定设备的mac地址
#define KK_BLEConnect        [BLEConnect sharedInstance]
#define KK_BLEModel          [BLEConnect sharedInstance].model
#define SN_USER              ([BLEConnect sharedInstance].userModel)
@interface BLEConnect : NSObject<CBCentralManagerDelegate>

@property (nonatomic, strong) CBCentralManager *centralManager;
@property (nonatomic, strong) CBPeripheral     *discoverPeripheral;
@property (nonatomic, strong) BLEDeviceModel *model;                   // 当前连接设备模型
@property (nonatomic, strong) NSMutableArray *devicesArray;            // 设备列表
@property (nonatomic, strong) KKBLEUpdateBlock updateModelStatus;      // 设备连接状态回调1
@property (nonatomic, strong) KKBLEUpdateBlock deviceStateBlock;       // 设备连接状态回调2  1和2是一样的
@property (nonatomic, strong) KKBLEAcceptBlock bleConnectAcceptBlock;  // 设备接收蓝牙数据回调
@property (nonatomic, strong) UserModel *userModel;                    // 用户设置模型
@property (nonatomic, strong) KKBLEAcceptData bleAcceptDtaBlock;       // 蓝牙接收数据回调
@property (nonatomic, assign) UInt8 sendHead;

AS_SINGLETON(BLEConnect)
/********************************************************************/
//注册蓝牙SDK 需要提供APP的Build_Id给我们获取秘钥才可以使用
- (BOOL)registrement:(NSString *)key;
//搜索设备
- (void)foundBle;
//停止搜索
- (void)stopFoundBle;
//断开蓝牙
- (void)disConnect;
//连接指定设备并绑定
- (void)connectPeripheralWithModel:(BLEDeviceModel *)model;
//断开指定设备
- (void)disConnectPeripheralWithModel:(CBPeripheral *)Peripheral;
//移除绑定设备并断开
- (void)removeDevice;

@end

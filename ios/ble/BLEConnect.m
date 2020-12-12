//
//  BLEConnect.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/5.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "BLEConnect.h"
#import<CommonCrypto/CommonDigest.h>
#define KK_BLEServiceX9   [CBUUID UUIDWithString:@"8F400001-CFB4-14A3-F1BA-F61F35CDDBAF"]
#define KK_BLEServiceTW64 [CBUUID UUIDWithString:@"0xFF00"]
@interface BLEConnect ()
@property (nonatomic, strong) NSTimer *scanTimer;
@property (nonatomic, assign) BOOL    blePowerOn;
@end

@implementation BLEConnect
DEF_SINGLETON(BLEConnect)


- (instancetype)init {
    self = [super init];
    if (self) {
        //        _devicesArray   = [[NSMutableArray alloc] initWithCapacity:0];
        //        _centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
        _userModel = [[UserModel alloc] init];
        //        [self startTimerScan];
        //        [self performSelector:@selector(foundBle) withObject:nil afterDelay:0.2];
        _sendHead = BLEHEAD;
    }
    return self;
}

- (BOOL)registrement:(NSString *)key
{
    NSString *getAppBuildId = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleIdentifier"];
    if ([getAppBuildId isEqualToString:@"com.neoonsdk2019"]) {
        NSLog(@"SDK Load Finish.");
        _devicesArray   = [[NSMutableArray alloc] initWithCapacity:0];
        _centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
        [self performSelector:@selector(foundBle) withObject:nil afterDelay:0.2];
        return YES;
    }
    
    NSString *appendString = [NSString stringWithFormat:@"neoon_sdk_%@",getAppBuildId];
    NSString *appKey = [self md5:appendString];
    if (![appKey isEqualToString:key]) {
        NSLog(@"SDK Load Fail.");
        return NO;
    }
    else
    {
        NSLog(@"SDK Load Finish.");
        _devicesArray   = [[NSMutableArray alloc] initWithCapacity:0];
        _centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
        [self performSelector:@selector(foundBle) withObject:nil afterDelay:0.2];
        return  YES;
    }
}

//定时器 10秒
- (void)startTimerScan {
    if (!_scanTimer) {
        _scanTimer = [NSTimer scheduledTimerWithTimeInterval:BLEFOUNDTIME
                                                      target:self
                                                    selector:@selector(timerScanWithoutBreakConnect)
                                                    userInfo:nil
                                                     repeats:YES];
    }
}

- (void)timerScanWithoutBreakConnect {
    if (_discoverPeripheral.state == CBPeripheralStateConnected) {
    } else {
        [self foundBle];
    }
}

- (void)foundBle{
    [_centralManager stopScan];
    if (!_blePowerOn) {
        return;
    }
    [_devicesArray enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop)
     {
         BLEDeviceModel *model = obj;
         switch (model.peripheral.state) {
             case CBPeripheralStateConnected:
                 break;
             default:
             {
                 [self->_devicesArray removeObject:model];
             }
                 break;
         }
     }];
//    [self.centralManager scanForPeripheralsWithServices:[self deviceServices]
//                                                options:nil];
    [self.centralManager scanForPeripheralsWithServices:nil
                                                options:nil];
    
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(stopFoundBle) object:nil];
    [self performSelector:@selector(stopFoundBle) withObject:nil afterDelay:BLEFOUNDTIME];
    
    [self retrieveConnectedPeripherals];       // 老的方法去发现系统蓝牙设置已经连接过的手环
    
    [self retrieveIsConnectedPeripherals];    // 新的查询(连接)
}

// 新的查询(连接)
- (void)retrieveIsConnectedPeripherals {
    NSArray *arrayService = [NSArray array];
    arrayService = [self.centralManager retrieveConnectedPeripheralsWithServices:@[KK_BLEServiceX9]];
    
    for (CBPeripheral *p  in arrayService) {
        [self discoverANCS:p];
    }
}

- (void)discoverANCS:(CBPeripheral *)peripheral {
    NSString *lastConnectMac  = [[KK_BLELastDeviceMac getObjectValue] uppercaseString];
    NSString *lastConnectUuid = [KK_BLELastDevice getObjectValue];
    
    if ([[[[peripheral.identifier UUIDString] getObjectValue]uppercaseString] isEqualToString:lastConnectMac] && [[peripheral.identifier UUIDString]isEqualToString:lastConnectUuid]) {
        NSLog(@"已经连接上的蓝牙Name:%@ uuid>>%@ Mac:>>%@", peripheral.name, [peripheral.identifier UUIDString], [[peripheral.identifier UUIDString] getObjectValue]);
    }
    [self centralManager:_centralManager
   didDiscoverPeripheral:peripheral
       advertisementData:[NSDictionary dictionary]
                    RSSI:@(0)];
}

- (void)stopFoundBle
{
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(stopFoundBle) object:nil];
    [_centralManager stopScan];
}

- (void)retrieveConnectedPeripherals {
    NSString *lastConnectMac  = [[KK_BLELastDeviceMac getObjectValue] uppercaseString];
    NSString *lastConnectUuid = [KK_BLELastDevice getObjectValue];
    NSLog(@"lastConnectMac>>>>>%d %d", lastConnectMac.length, lastConnectUuid.length);
    
    NSArray *arrayService = [NSArray array];
    arrayService = [self.centralManager retrieveConnectedPeripheralsWithServices:@[KK_BLEServiceX9]];
    NSMutableArray *array = [NSMutableArray array];
    for (CBPeripheral *Peripheral in arrayService) {
        if (![array containsObject:Peripheral]) {
            [array addObject:Peripheral];
        }
    }
    
    NSMutableArray *newModelArray = [[NSMutableArray alloc] init];
    for (CBPeripheral *Hp in array) {
        BLEDeviceModel *model = [[BLEDeviceModel alloc] init];
        model.bleName    = Hp.name;
        model.bleUUID    = Hp.identifier.UUIDString;
        model.bleRSSI    = [NSString stringWithFormat:@"%d", Hp.RSSI.intValue];
        model.peripheral = Hp;
        model.bleMAC     = [model.bleUUID getObjectValue];
        
        NSString *bleAdv = [model.bleMAC getObjectValue];
        model.bleAdv = [bleAdv dataUsingEncoding:NSUTF8StringEncoding];
        [self addDeviceBleWithModel:model];
        [newModelArray addObject:model];
        [self updateModel:nil type:KKBLEUpdateNormal];
    }
    
    //    if (lastConnectMac.length == 0 && lastConnectUuid.length == 0) {
    //        return;
    //    }
    //
    //    if (lastConnectMac.length > 0) {
    //        for (BLEDeviceModel *model in newModelArray) {
    //            if ([model.bleMAC isEqualToString:lastConnectMac]) {
    //                [self connectPeripheralWithModel:model];
    //                return;
    //            }
    //        }
    //    }
    //    if (lastConnectUuid.length > 0) {
    //        for (BLEDeviceModel *model in _devicesArray) {
    //            if ([model.bleUUID isEqualToString:lastConnectUuid]) {
    //                [self connectPeripheralWithModel:model];
    //                return;
    //            }
    //        }
    //    }
}

- (void)removeDevice
{
    [KK_BLELastDevice setObjectValue:@""];
    [KK_BLELastDeviceMac setObjectValue:@""];
    [self disConnect];
    [self updateModel:nil type:KKBLEUpdateRemove];
}

- (void)disConnect {
    if (_model && _model.peripheral.state == CBPeripheralStateConnected) {
        [_centralManager cancelPeripheralConnection:_model.peripheral];
    }
    if (_discoverPeripheral) {
        _discoverPeripheral.delegate = nil;
        [_centralManager cancelPeripheralConnection:_discoverPeripheral];
        _discoverPeripheral = nil;
    }
    _model = nil;
}

- (void)disConnectPeripheralWithModel:(CBPeripheral *)peripheral
{
    if (peripheral) {
        if (peripheral.state == CBPeripheralStateConnected) {
            [_centralManager cancelPeripheralConnection:peripheral];
        }
    }
    if (_discoverPeripheral) {
        _discoverPeripheral.delegate = nil;
        [_centralManager cancelPeripheralConnection:_discoverPeripheral];
        _discoverPeripheral = nil;
    }
    _model = nil;
}

- (void)connectPeripheralWithModel:(BLEDeviceModel *)model {
    if (!model &&model.peripheral!=nil) {
        return;
    }
    _model = model;
    [KK_BLEConnect stopFoundBle];
    [self parseDeviceFunction:_model];
    [self connectDevice];
}

// 获取手环功能
- (void)parseDeviceFunction:(BLEDeviceModel *)model
{
    NSData *adv = model.bleAdv;
    NSString *string   = [[NSString alloc] initWithData:adv encoding:NSUTF8StringEncoding];
    NSString *string16 = [NSString stringWithFormat:@"0x%@", string];
    // 转换为16进制
    unsigned long Number = strtoul([string16 UTF8String], 0, 16);
    if (string.length == 4) {
        NSLog(@"🙂🙂🙂%@ 广播包 转换完的数字为：%lx",_model.bleName,Number);
        int num = (int)Number;
        _model.heart           = intConvertBit(num, 0);
        _model.airPressure     = intConvertBit(num, 1);
        _model.oxygen          = intConvertBit(num, 2);
        _model.bistoryPressure = intConvertBit(num, 3);
        _model.ancs            = intConvertBit(num, 4);
    }
    NSLog(@"🙂🙂🙂%@ 广播包>>>>>>%d",_model.bleName,Number);
    NSLog(@"🙂🙂🙂功能 h：%i o:%i bp:%i",_model.heart,_model.oxygen,_model.bistoryPressure);
}

- (void)connectDevice
{   dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, 2.0 * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        if (!_model.isConnected) {
            if (self->_model.peripheral!=nil) {
                [self->_centralManager connectPeripheral:self->_model.peripheral options:nil];
                [self updateModel:self->_model type:KKBLEUpdateNormal];
                [self->_centralManager stopScan];
            }
        }
    });
}

- (NSArray *)deviceServices {
    return @[KK_BLEServiceX9, KK_BLEServiceTW64];
}

- (void)centralManagerDidUpdateState:(CBCentralManager *)central {
    if (central.state != CBCentralManagerStatePoweredOn) {
        _blePowerOn = NO;
        [self updateModel:_model type:KKBLEUpdatePowerOff];
    } else {
        _blePowerOn = YES;
        [self updateModel:_model type:KKBLEUpdatePowerOn];
        [self foundBle];
    }
}

- (void)centralManager:(CBCentralManager *)central
    didDiscoverPeripheral:(CBPeripheral *)peripheral
        advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI {
    int rssi = abs([RSSI intValue]);
    
    if (rssi > 120) {
        return;
    }
    if (_devicesArray.count > 20) {
        BOOL add = YES;
        for (int i = 0; i < _devicesArray.count; i++) {
            BLEDeviceModel *kkModel = _devicesArray[i];
            if (rssi < kkModel.bleRSSI.intValue) {
                add = NO;
                break;
            }
        }
        if (!add) {
            return;
        }
    }
    
    NSString *deviceName = peripheral.name;

    
    NSString *idString   = [peripheral.identifier UUIDString];
    if (!(deviceName && idString)) {
        return;
    }
    NSString *macString ;
    
    NSData *kCData2 = [advertisementData objectForKey:@"kCBAdvDataManufacturerData"];
    if (kCData2) {
            const unsigned *kCData = [kCData2 bytes];
        macString = [NSString stringWithFormat:@"%08x%08x%08x%08x",
        ntohl(kCData[0]), ntohl(kCData[1]),ntohl(kCData[2]),ntohl(kCData[3])];
    }
    
//    if (macString.length < 19) {
//        return;
//    }
    NSString *macAddress    = [macString substringWithRange:NSMakeRange(4, 12)];
    NSString *s1            = [macAddress substringWithRange:NSMakeRange(0, 2)];
    NSString *s2            = [macAddress substringWithRange:NSMakeRange(2, 2)];
    NSString *s3            = [macAddress substringWithRange:NSMakeRange(4, 2)];
    NSString *s4            = [macAddress substringWithRange:NSMakeRange(6, 2)];
    NSString *s5            = [macAddress substringWithRange:NSMakeRange(8, 2)];
    NSString *s6            = [macAddress substringWithRange:NSMakeRange(10, 2)];
    NSString *bltMacAddress = [NSString stringWithFormat:@"%@:%@:%@:%@:%@:%@", s1, s2, s3, s4, s5, s6];
    bltMacAddress = [bltMacAddress uppercaseString];
//    NSLog(@"扫描到蓝牙外设:..%@..%@..%@..~~~~~ %d %@ mac长度", advertisementData, [peripheral.identifier UUIDString], peripheral.name, rssi, bltMacAddress);
    
    BLEDeviceModel *model = [[BLEDeviceModel alloc] init];
    model.bleName    = deviceName;
    model.bleUUID    = idString;
    model.bleRSSI    = [NSString stringWithFormat:@"%d", rssi];
    model.peripheral = peripheral;
    model.bleMAC     = bltMacAddress;
    NSString *bleAdv = [macString substringWithRange:NSMakeRange(0, 4)];
    model.bleAdv = [bleAdv dataUsingEncoding:NSUTF8StringEncoding];
    if (![model.bleUUID getObjectValue]) {
        [model.bleUUID setObjectValue:model.bleMAC];
    }
    if (![model.bleMAC getObjectValue]) {
        [model.bleMAC setObjectValue:bleAdv];
    }
    [self addDeviceBleWithModel:model];
    NSArray *resultArray = [_devicesArray sortedArrayUsingComparator:^NSComparisonResult (id obj1, id obj2) {
        BLEDeviceModel *per1 = obj1;
        BLEDeviceModel *per2 = obj2;
        if (per1.bleRSSI.intValue > per2.bleRSSI.intValue) {
            return NSOrderedDescending;//降序
        } else if (per1.bleRSSI.intValue < per2.bleRSSI.intValue) {
            return NSOrderedAscending;//升序
        } else {
            return NSOrderedSame;//相等
        }
    }];
    [_devicesArray removeAllObjects];
    for (int i = 0; i < resultArray.count; i++) {
        if (i < 20) {
            BLEDeviceModel *addModel = resultArray[i];
            [_devicesArray addObject:addModel];
        }
    }
    
    // 扫描到设备后是否自动连接 //需要绑定设备
    NSString *uuidL = [KK_BLELastDevice getObjectValue];
    NSString *macL  = [KK_BLELastDeviceMac getObjectValue];
    if (macL.length >0) {
        if (([macL isEqualToString:model.bleMAC])) {
            [self connectPeripheralWithModel:model];
            return;
        }
    }
    if (uuidL.length >0) {
        if (([uuidL isEqualToString:model.bleUUID])) {
            [self connectPeripheralWithModel:model];
            return;
        }
    }
    
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, 2.0 * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self updateModel:nil type:KKBLEUpdateNormal];
    });
}

// 添加设备
- (void)addDeviceBleWithModel:(BLEDeviceModel *)model {
//    NSLog(@"🙂🙂🙂添加设备model>>>>%@ mac>>>%@",model.bleName,model.bleMAC);
    
    if (!model) {
        return;
    }
    if (!model.peripheral) {
        return;
    }
    BOOL found = NO;
    for (BLEDeviceModel *mmModel in _devicesArray) {
        if ([mmModel.bleUUID isEqualToString:model.bleUUID]) {
            found = YES;
            break;
        }
    }
    if (!found) {
        [_devicesArray addObject:model];
    }
}

- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral {
    //    NSLog(@"连接到设备, 并去发现服务. peripheral name>>>>%@", peripheral.name);
    _model.peripheral = peripheral;
    _discoverPeripheral          = peripheral;
    _discoverPeripheral.delegate = KK_BLEService;
    KK_BLEService.peripheral     = _discoverPeripheral;
    [self.centralManager stopScan];
    [self updateModel:_model type:KKBLEUpdateDidConnect];
    [_discoverPeripheral discoverServices:@[BLESERVICE, [CBUUID UUIDWithString:@"0xFF00"],[CBUUID UUIDWithString:SERVICE_OTA_UUID1]]];
}

// 连接失败 这个回调很少触发
- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error {
    _model.peripheral = peripheral;
    [self updateModel:_model type:KKBLEUpdateDisConnect];
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error {
    _model.peripheral = peripheral;
    [self updateModel:_model type:KKBLEUpdateDisConnect];
}

// 更新各种蓝牙的情况.
- (void)updateModel:(BLEDeviceModel *)model type:(KKBLEUpdateType)type {
    if (_updateModelStatus) {
        _updateModelStatus(model, type);
    }
    if (_deviceStateBlock) {
        _deviceStateBlock(model, type);
    }
}

//MD5加密
- (NSString *)md5:(NSString *)input {
    const char    *cStr = [input UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cStr, strlen(cStr), digest);   // This is the md5 call
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_MD5_DIGEST_LENGTH; i++) {
        [output appendFormat:@"%02x", digest[i]];
    }
    return output;
}

@end

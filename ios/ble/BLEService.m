//
//  BLEService.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/10.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "BLEService.h"

@interface BLEService ()
@property (nonatomic, strong) NSMutableData                             *acceptData;
@property (nonatomic, assign) CBCharacteristicWriteType                 writeType;
@property (nonatomic, strong) CBCharacteristic                          *readCharac;
@property (nonatomic, strong) NSArray *didConnectDeivces;               // 当前已经连接过的外设
@end

@implementation BLEService
DEF_SINGLETON(BLEService)

- (instancetype)init {
    self = [super init];
    if (self) {
        _acceptData = [[NSMutableData alloc] init];
        _writeType  = CBCharacteristicWriteWithResponse;
    }
    return self;
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error {
    if (error) {
    }
    CBUUID *serviceOTAUUID = [CBUUID UUIDWithString:SERVICE_OTA_UUID1]; //指定服务
    for (CBService *service in peripheral.services) {
        if ([service.UUID isEqual:BLESERVICE]) {
            [_peripheral discoverCharacteristics:@[BLEWRITE,
                                                   BLENOTIFY]
                                      forService:service];
        } else if ([service.UUID isEqual:BLEUPDATESERVICE]) {
            [_peripheral discoverCharacteristics:@[BLEUPDATECONTROL,
                                                   BLEUPDATEPACKET,
                                                   BLEUPDATEVERSION]
                                      forService:service];
        } else if ([service.UUID isEqual:[CBUUID UUIDWithString:@"180A"]]) {
            [_peripheral discoverCharacteristics:@[[CBUUID UUIDWithString:@"2A27"]] forService:service];
        } else if ([service.UUID isEqual:[CBUUID UUIDWithString:@"0xFF00"]]) {
            [_peripheral discoverCharacteristics:@[
                                                   [CBUUID UUIDWithString:@"0xFF01"],
                                                   [CBUUID UUIDWithString:@"0xFF01"]]
                                      forService:service];
        }
        //OTA
        if ([service.UUID isEqual:serviceOTAUUID]) {
            //外围设备查找指定服务中的特征
            [_peripheral discoverCharacteristics:@[[CBUUID UUIDWithString:CHARACTERISTIC_OTA_WRITE_UUID1], [CBUUID UUIDWithString:CHARACTERISTIC_OTA_INDICATE_UUID1], [CBUUID UUIDWithString:CHARACTERISTIC_OTA_DATA_WRITE_UUID1]] forService:service];
            //            [peripheral discoverCharacteristics:@[[CBUUID UUIDWithString:CHARACTERISTIC_OTA_WRITE_UUID], [CBUUID UUIDWithString:CHARACTERISTIC_OTA_INDICATE_UUID], [CBUUID UUIDWithString:CHARACTERISTIC_OTA_DATA_WRITE_UUID]] forService:service];
        }
        NSLog(@"service.UUID. = ...%@。%@", service, service.UUID.description);
    }
}

- (void)updateCurrent
{
    NSMutableArray *array = [NSMutableArray array];
    NSArray *deviceArray = KK_BLEConnect.devicesArray;
    for (BLEDeviceModel *model in deviceArray) {
        if (model.isConnected) {
            [array addObject:model];
        }
    }
    _didConnectDeivces = [NSArray arrayWithArray:array];
}

// 发现该服务所持有的所有特征
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error {
    if (error) {
        NSLog(@"特征错误 发生错误...%@", error);
        // 发现错误时断开并重连接
    }
    for (CBCharacteristic *charac in service.characteristics) {
        if ([charac.UUID isEqual:BLENOTIFY]) {
            [_peripheral setNotifyValue:YES forCharacteristic:charac];
            if (_updateModelStatus) {
                _updateModelStatus(nil, KKBLEUpdateDidConnect);
            }
            self.send = [[BLESend alloc] init];
            self.accept = [[BLEAccept alloc] init];
            [self updateCurrent];
        } else if ([charac.UUID isEqual:BLEWRITE]) {
            _readCharac = charac;
        } else if ([charac.UUID isEqual:BLEUPDATECONTROL]) {
            [_peripheral setNotifyValue:YES forCharacteristic:charac];
        }
        else if ([charac.UUID isEqual:BLEUPDATEPACKET]) {
            
        } else if ([charac.UUID isEqual:BLEUPDATEVERSION]) {
            
        } else if ([charac.UUID isEqual:[CBUUID UUIDWithString:@"0xFF01"]]) {
            [_peripheral setNotifyValue:YES forCharacteristic:charac];
            if (_updateModelStatus) {
                _updateModelStatus(nil, KKBLEUpdateDidConnect);
            }
            [self updateCurrent];
            _readCharac = CBCharacteristicWriteWithResponse;
        }
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic
             error:(NSError *)error {
    if (error) {
        NSLog(@"写数据时发生错误...%@", error);
        peripheral.delegate = self;
        [peripheral discoverServices:@[BLESERVICE, [CBUUID UUIDWithString:@"0xFF00"]]];
    }
}

// 外围设备数据有更新时会触发该方法
- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic
             error:(NSError *)error {
    if (error) {
        peripheral.delegate = self;
        [peripheral discoverServices:@[BLESERVICE, [CBUUID UUIDWithString:@"0xFF00"],[CBUUID UUIDWithString:SERVICE_OTA_UUID1]]];
        //        [KK_BLEConnect disConnect];
    } else {
        if ([characteristic.UUID isEqual:BLENOTIFY])
        {
            [self cleanMutableData:_acceptData];
            [_acceptData appendData:characteristic.value];
            [_accept updateData:_acceptData updatePeripheral:peripheral];
        }
        else if ([characteristic.UUID isEqual:BLEUPDATECONTROL] ||
                   [characteristic.UUID isEqual:BLEUPDATEPACKET])
        {
            
        } else if ([characteristic.UUID isEqual:BLEUPDATEVERSION])
        {
        } else if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"0xFF01"]]) {
            [self cleanMutableData:_acceptData];
            [_acceptData appendData:characteristic.value];
        }
    }
}

- (void)cleanMutableData:(NSMutableData *)data {
    [data resetBytesInRange:NSMakeRange(0, data.length)];
    [data setLength:0];
}

- (void)senderDataToPeripheral:(NSData *)data {
    NSLog(@"向蓝牙发送数据>>>>>%@",data);
    if (_peripheral.state == CBPeripheralStateConnected) {
        CBUUID    *serviceUUID = BLESERVICE;
        CBUUID    *writeUUID   = BLEWRITE;
        CBService *service     = [self searchServiceFromUUID:serviceUUID withPeripheral:_peripheral];
        if (!service) {
            return;
        }
        CBCharacteristic *chara = [self searchCharacteristcFromUUID:writeUUID withService:service];
        if (!chara) {
            return;
        }
        [_peripheral writeValue:data forCharacteristic:chara type:CBCharacteristicWriteWithResponse];
    }
}

- (void)senderDataToPeripheral64:(NSData *)data {
    if (_peripheral.state == CBPeripheralStateConnected) {
        CBUUID *serviceUUID = [CBUUID UUIDWithString:@"0xFF00"];
        CBUUID *charaUUID   = [CBUUID UUIDWithString:@"0xFF01"];
        UInt8  val[20]      = {0};
        [data getBytes:&val length:data.length];
        if (val[0] == 0x08) {
            charaUUID = [CBUUID UUIDWithString:@"00000af1-0000-1000-8000-00805f9b34fb"];
        }
        CBService *service = [self searchServiceFromUUID:serviceUUID withPeripheral:_peripheral];
        if (!service) {
            return;
        }
        CBCharacteristic *chara = [self searchCharacteristcFromUUID:charaUUID withService:service];
        if (!chara) {
            return;
        }
        //NSLog(@"写入数据.>>>>>>>>>>>>.%@..%@", data, [BLTAcceptModel sharedInstance].updateValue);
        [_peripheral writeValue:data forCharacteristic:chara type:_writeType];
    }
}

// 针对多连接
- (void)senderDataToPeripheral:(NSData *)data withPeripheral:(CBPeripheral *)peripheral
{
    if (peripheral.state == CBPeripheralStateConnected)
    {
        CBUUID    *serviceUUID = BLESERVICE;
        CBUUID    *writeUUID   = BLEWRITE;
        CBService *service = [self searchServiceFromUUID:serviceUUID withPeripheral:peripheral];
        if (!service)
        {
            //            NSLog(@"service有错误...");
            return;
            // SHOWMBProgressHUD(BL_Text(@"Error, reconnect"), nil, nil, NO, 3.0);
            return;
        }
        
        CBCharacteristic *chara = [self searchCharacteristcFromUUID:writeUUID withService:service];
        if (!chara)
        {
            //            NSLog(@"chara有错误...");
            return;
            // SHOWMBProgressHUD(BL_Text(@"Error, reconnect"), nil, nil, NO, 3.0);
            return;
        }
        [peripheral writeValue:data forCharacteristic:chara type:_writeType];
        [NSThread sleepForTimeInterval:0.1f];
    }
}

// 匹配相应的服务
- (CBService *)searchServiceFromUUID:(CBUUID *)uuid withPeripheral:(CBPeripheral *)peripheral {
    for (int i = 0; i < peripheral.services.count; i++) {
        CBService *service = peripheral.services[i];
        if ([service.UUID isEqual:uuid]) {
            return service;
        }
    }
    return nil;
}

// 匹配相应的具体特征
- (CBCharacteristic *)searchCharacteristcFromUUID:(CBUUID *)uuid withService:(CBService *)service {
    for (int i = 0; i < service.characteristics.count; i++) {
        CBCharacteristic *chara = service.characteristics[i];
        if ([chara.UUID isEqual:uuid]) {
            return chara;
        }
    }
    return nil;
}

- (void)senderDataToPeripheralC1:(NSData *)data {
    if (_peripheral.state == CBPeripheralStateConnected) {
        NSLog(@"_peripheral.state.>>>%i", _peripheral.state);
        CBUUID    *serviceUUID = [CBUUID UUIDWithString:SERVICE_OTA_UUID1];
        CBUUID    *writeUUID   = [CBUUID UUIDWithString:CHARACTERISTIC_OTA_WRITE_UUID1];
        CBService *service     = [self searchServiceFromUUID:serviceUUID withPeripheral:_peripheral];
        if (!service) {
            NSLog(@"senderDataToPeripheralC1: service>>>错误");
            return;
        }
        CBCharacteristic *chara = [self searchCharacteristcFromUUID:writeUUID withService:service];
        if (!chara) {
            NSLog(@"senderDataToPeripheralC1: chara>>>错误");
            return;
        }
        [_peripheral writeValue:data forCharacteristic:chara type:CBCharacteristicWriteWithResponse];
    }
}

@end

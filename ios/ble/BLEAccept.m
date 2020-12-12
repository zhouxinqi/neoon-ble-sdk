//
//  BLEAccept.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/10.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "BLEAccept.h"
#import "BLEConnect.h"
@implementation BLEAccept

// 高速通道
- (void)updateSpeedData:(NSData *)data updatePeripheral:(CBPeripheral *)peripheral {
    UInt8 val[20] = {0};

    [data getBytes:&val length:data.length];
    NSString *content = [NSString stringWithFormat:@"%@", data];
    int      result   = val[7];
    BOOL     err      = intConvertBit(result, 5);
    BOOL     ack      = intConvertBit(result, 4);
//    NSLog(@"高速通道接收到蓝牙数据>>>>>%@ err>%i ack>%i",content,err,ack);//0001 0000
    if (err == 0 && ack) {
        // 校验成功
        [[NSNotificationCenter defaultCenter] postNotificationName:@"acceptAck" object:@"1"];
    } else {
        // 校验失败
        [[NSNotificationCenter defaultCenter] postNotificationName:@"acceptAck" object:@"0"];
    }
}

- (void)updateData:(NSData *)data updatePeripheral:(CBPeripheral *)peripheral {
    UInt8 val[20] = {0};

    [data getBytes:&val length:data.length];

    NSString *content = [NSString stringWithFormat:@"%@", data];
//    NSLog(@"接收到蓝牙数据>>>>>%@",content);

    if (KK_BLEConnect.bleAcceptDtaBlock) {
        KK_BLEConnect.bleAcceptDtaBlock(data);
    }

    id object = data;
    // 时间
    if (val[1] == 0x02) {
        if (val[2] == 0x01) {
            _acceptType = KKBLEAcceptTypeTime;
            //            NSLog(@"设置时间成功>>>>");
        }
    }

    // MAC地址
    if (val[1] == 0x01) {
        if (val[2] == 0x01) {
            NSString *mac = [NSString stringWithFormat:@"%02x:%02x:%02x:%02x:%02x:%02x", val[3], val[4], val[5], val[6], val[7], val[8]];
            //            NSLog(@"获取Mac地址成功%@", mac);
            if (mac) {
                [KK_BLEModel.bleUUID setObjectValue:[mac uppercaseString]];
            }
            KK_BLEModel.bleMAC = [mac uppercaseString];
            object             = KK_BLEModel.bleMAC;
            _acceptType        = KKBLEAcceptTypeDevideMAC;
        }
    }

    if (val[1] == 0x07) {
        if (val[2] == 0x0B) {
            //            NSLog(@"序号：0x%02x , 运动模式:0x%02x , 开始时间戳：0x%02x%02x%02x%02x, 持续时间:0x%02x%02x, 记步数:0x%02x%02x, 距离：0x%02x%02x, 卡路里:0x%02x%02x, max:0x%02x , min :0x%02x, 平均心率:0x%02x",val[3],val[4],val[5],val[6],val[7],val[8],val[9],val[10],val[11],val[12], val[13],val[14],val[15],val[16],val[17],val[18],val[19]);
            NSLog(@"运动心率数据>>>%d %@", data.length, data);
            SportModeModel *model = [SportModeModel updataSportModeData:data];
            _acceptType = BLTAcceptTypeDataSportModeDataOnce;
            object      = model;
        }
        if (val[2] == 0xf9) {
            NSLog(@"运动模式获取完成");
            _acceptType = BLTAcceptTypeDataSportModeDataFinish;
        }
    }

    // 设备信息
    if (val[1] == 0x01) {
        if (val[2] == 0x02) {
            NSLog(@"获取设备信息>>>>电量%d", val[7]);
            KK_BLEModel.bleFirmwareVersion = val[5];
            KK_BLEModel.bleDeviceID        = val[3] * 256 + val[4];
            KK_BLEModel.bleBatteryQuantity = val[7];
            object                         = KK_BLEModel;
            _acceptType                    = KKBLEAcceptTypeDevideInfo;
        }
    }

    if (val[1] == 0x02) {
        if (val[2] == 0x02) {
            _acceptType = KKBLEAcceptTypeUserInfo;
            //               NSLog(@"用户信息设置成功>>>");
        } else if (val[2] == 0x0a) {
            if (val[3] == 0x01) {
                _acceptType = KKBLEAcceptTypeOther;
                //                NSLog(@"发送低电 抬腕 防丢 心率检测 翻腕切屏设置成功>>>");
            }
        } else if (val[2] == 0x09) {
            _acceptType = KKBLEAcceptTypeNotify;
            //                NSLog(@"消息推送设置成功>>>");
        } else if (val[2] == 0x0b) {
            if (val[3] == 0x01) {
                _acceptType = KKBLEAcceptTypeQuite;
                //                NSLog(@"免打扰设置成功>>>");
            }
        } else if (val[2] == 0x04) {
            _acceptType = KKBLEAcceptTypeAlarmClock;
            //            NSLog(@"闹钟提醒设置成功>>>");
        } else if (val[2] == 0x05) {
            _acceptType = KKBLEAcceptTypeCalendar;
            //            NSLog(@"日程提醒设置成功>>>");
        } else if (val[2] == 0x06) {
            _acceptType = KKBLEAcceptTypeDrink;
            //            NSLog(@"喝水提醒设置成功>>>");
        } else if (val[2] == 0x07) {
            _acceptType = KKBLEAcceptTypeSedentary;
            //            NSLog(@"久坐提醒设置成功>>>");
        } else if (val[2] == 0x0e) {
            _acceptType = KKBLEAcceptTypeTemperature;
//            NSLog(@"天气设置成功>>>");
        }
    }

    if (val [1] == 0x05) {
        if (val[2] == 0x03 && val[3] == 0x01) {
            _acceptType = KKBLEAcceptTypeFindBle;
        }
    }

    if (val[1] == 0x07) {
        if (val[2] == 0x01) {
            if (data.length > 3) {
                NSInteger tmpStep     = 0;
                NSInteger tmpDistance = 0;
                NSInteger tmpCalory   = 0;
                NSInteger tmpDuration = 0;
                tmpStep     = val[3] * 256 * 256 * 256 + val[4] * 256 * 256 + val[5] * 256 + val[6];
                tmpDistance = val[7] * 256 * 256 * 256 + val[8] * 256 * 256 + val[9] * 256 + val[10];
                tmpCalory   = val[11] * 256 * 256 * 256 + val[12] * 256 * 256 + val[13] * 256 + val[14];
                tmpDuration = val[15] * 256 + val[16];
                SportModel *model = SN_PublicClass.todaySport;
                model.step         = tmpStep;
                model.calory       = tmpCalory;
                model.distance     = tmpDistance;
                model.stepDuration = tmpDuration;
                object             = model;
                _acceptType        = KKBLEAcceptTypeRealtimeSport;
            }
        }
    }

    // 运动详情
    if (val[1] == 0x07) {
        if (val[2] == 0x03) {
            //记步大数据
            [self updateSportData:data];
        } else if (val[2] == 0x05) {
            //距离大数据
            [self updateSportData:data];
        } else if (val[2] == 0x06) {
            //卡路里大数据
            [self updateSportData:data];
        } else if (val[2] == 0xFF) {
            // 运动结束
            //                NSLog(@"运动数据同步结束");
            _acceptType = KKBLEAcceptTypeDataSport;
        }
    }

    if (val[1] == 0x07) {
        if (val[2] == 0x0c) {
            NSLog(@"🙂 接收体温大数据[接收中🙂]>>>>%@", content);
            [self updateTemData:data];
        } else if (val[2] == 0xFc) {
            NSLog(@"🙂 接收体温大数据[结束🙂]>>>>%@", content);
            [self temTransEnd];
            _acceptType = KKBLEAcceptTypeDataTem;
        }
    }

    // 睡眠详情
    if (val[1] == 0x07) {
        if (val[2] == 0x04) {
            [self updateSleepData:data];
        } else if (val[2] == 0xFE) {
            // 睡眠结束
            //                NSLog(@"睡眠数据同步结束");
            [self updateSleepEnd];
            _acceptType = KKBLEAcceptTypeDataSleep;
        }
    }

    //其他开关返回 会跟其他指令冲突 所以object = nil;
    if (val[0] == KK_BLEConnect.sendHead && val[1] == 0x05) {
        if (val[2] == 1 || val[2] == 4 || val[2] == 5) {
            object      = nil;
            _acceptType = KKBLEAcceptTypeCheckType;
        }
    }


    //心率详情
    if (val[1] == 0x07) {
        if (val[2] == 0x07) {
            [self updateHeartData:data];
        } else if (val[2] == 0xFD) {
            // 心率
            [self heartTransEnd];
            _acceptType = KKBLEAcceptTypeDataHeart;
        }
    }

    // 接收到的检测类型
    if (val[1] == 0x07) {
        if (val[2] == 0x02) {
            if (SN_PublicClass.checkType == 1 && val[3] > 0) {
                object      = StrByInt(val[3]);
                _acceptType = KKBLEAcceptTypeRealtimeHeart;
                NSLog(@"🙂心率>>>>>>>>>>%@", object);
            }
            if (SN_PublicClass.checkType == 2 && val[4] > 0) {
                object      = StrByInt(val[4]);
                _acceptType = KKBLEAcceptTypeRealtimeOX;
                NSLog(@"🙂血氧>>>>>>>>>>%@", object);
            }
            if (SN_PublicClass.checkType == 3 && val[5] > 0 && val[6] > 0) {
                NSLog(@"🙂接收到血压检测值%d %d", val[5], val[6]);
                if (SN_PublicClass.bpValue.length == 0) {
                    object                 = [NSArray arrayWithObjects:StrByInt(val[5]), StrByInt(val[6]), nil];
                    SN_PublicClass.bpValue = [NSString stringWithFormat:@"%@-%@", StrByInt(val[5]), StrByInt(val[6])];
                    NSLog(@"🙂血压>>>>>>>>>>%@", object);
                    _acceptType = KKBLEAcceptTypeRealtimeBP;
                }
            }
        }
    }

    //拍照 // 查找手机
    if (val[1] == 0x06) {
        if (val[2] == 0x01) {
            _acceptType = KKBLEAcceptTypePhoto;
            //            NSLog(@"接收手环拍照指令");
        }
        if (val[2] == 0x02) {
            _acceptType = KKBLEAcceptTypeFindPhone;
            //            NSLog(@"接收手环找手机指令");
        }
    }


    if (_acceptStatus) {
        _acceptStatus(peripheral, object, _acceptType);
    }

    if (KK_BLEConnect.bleConnectAcceptBlock) {
        KK_BLEConnect.bleConnectAcceptBlock(peripheral, object, _acceptType);
    }
}

// 同步运动数据
- (void)updateSportData:(NSData *)data {
    UInt8 val[20] = {0};

    [data getBytes:&val length:data.length];

    if (val[3] == 0 || val[3] == 6) {
        [SN_PublicClass.stepArray removeAllObjects];
        SN_PublicClass.sportTotal = 0;
        for (int i = 0; i < 48; i++) {
            [SN_PublicClass.stepArray addObject:@"0"];
        }
    }
    //防止手环过来的数据漏发val[3] == 0或val[3] == 6这条导致数组为空
    if (SN_PublicClass.stepArray.count == 0) {
        for (int i = 0; i < 48; i++) {
            [SN_PublicClass.stepArray addObject:@"0"];
        }
    }

    for (int i = 4; i < 19; i += 2) {
        if (val[3] >= 0 && val[3] <= 11) {
            NSInteger number   = val[i] * 256 + val[i + 1];
            int       indexRow = val[3];
            if (val[3] > 5) {
                indexRow = val[3] - 6;
            }
            int index = indexRow * 8 + (i - 4)/2;
            if (number >= 10000) {
                number = 0;
            }
            [SN_PublicClass.stepArray replaceObjectAtIndex:index withObject:StrByInt(number)];
            SN_PublicClass.sportTotal += number;
        }
    }
    if (val[3] == 5) {
        NSLog(@"type>>>%d %d", val[2], SN_PublicClass.sportTotal);
        [self updateSportDataWithType:val[2] with:YES];
    } else if (val[3] == 11) {
        [self updateSportDataWithType:val[2] with:NO];
    }
}

// 运动类型
- (void)updateSportDataWithType:(NSInteger)type with:(BOOL)today {
    SportModel *model;

    if (today) {
        model            = SN_PublicClass.todaySport;
        model.dateString = [[NSDate date]dateToDayString];
    } else {
        model            = SN_PublicClass.yesSport;
        model.dateString = [[[NSDate date] dateAfterDay:-1] dateToDayString];
    }

    if (type == 3) {
        model.step      = SN_PublicClass.sportTotal;
        model.stepArray = [NSArray arrayWithArray:SN_PublicClass.stepArray];
    }
    if (type == 6) {
        model.calory     = SN_PublicClass.sportTotal;
        model.calorArray = [NSArray arrayWithArray:SN_PublicClass.stepArray];
    }
    if (type == 5) {
        model.distance      = SN_PublicClass.sportTotal;
        model.distanceArray = [NSArray arrayWithArray:SN_PublicClass.stepArray];
    }
}

// 睡眠详情
- (void)updateSleepData:(NSData *)sleepData {
    UInt8 val[20] = {0};

    [sleepData getBytes:&val length:sleepData.length];
    BOOL isBagHead = (val[3] % 4) == 0 ? YES : NO;
    if (val[3] == 0) {
        SN_PublicClass.todaySleepArray = [NSMutableArray array];
        SN_PublicClass.yesSleepArray   = [NSMutableArray array];
    }
    if (isBagHead) {
        SN_PublicClass.sleepSubModel          = [[SleepSubModel alloc] init];
        SN_PublicClass.sleepSubModel.subdeep  = 0;
        SN_PublicClass.sleepSubModel.sublight = 0;
        SN_PublicClass.sleepSubModel.subsober = 0;
        SN_PublicClass.sleepDetailArr         = [NSMutableArray array];
        NSString *startTime = [NSString stringWithFormat:@"%04d-%02d-%02d %02d:%02d:00", val[4] + 2000,
                               val[5], val[6], val[7], val[8]];
        NSString *endTime = [NSString stringWithFormat:@"%04d-%02d-%02d %02d:%02d:00", val[9] + 2000,
                             val[10], val[11], val[12], val[13]];
        SN_PublicClass.sleepSubModel.substartTime = startTime;
        SN_PublicClass.sleepSubModel.subendTime   = endTime;
        SN_PublicClass.sleepSubModel.subdate      = [endTime componentsSeparatedByString:@" "][0];
        // 判断这一次睡眠到底是今天或者是昨天
        NSString *dateStrNow = [[NSDate date] dateToDayString];
        NSString *yesdateStr = [[[NSDate date] dateAfterDay:-1] dateToDayString];
        if ([SN_PublicClass.sleepSubModel.subdate isEqualToString:dateStrNow]) {
            [SN_PublicClass.todaySleepArray addObject:SN_PublicClass.sleepSubModel];
        } else if ([SN_PublicClass.sleepSubModel.subdate isEqualToString:yesdateStr]) {
            [SN_PublicClass.yesSleepArray addObject:SN_PublicClass.sleepSubModel];
        }
    } else {
        for (int i = 4; i < 19; i += 2) {
            NSInteger number      = val[i] * 256 + val[i + 1];
            NSInteger sleepStatus = (UInt8)(number >> 14);
            NSInteger sleepTime   = (UInt16)number & 0x3FFF;
            if (sleepStatus == 0) {
                SN_PublicClass.sleepSubModel.sublight += sleepTime;
            } else if (sleepStatus == 1) {
                SN_PublicClass.sleepSubModel.subdeep += sleepTime;
            } else if (sleepStatus == 2) {
                SN_PublicClass.sleepSubModel.subsober += sleepTime;
            }
            [SN_PublicClass.sleepDetailArr addObject:StrByInt(number)];
        }
        SN_PublicClass.sleepSubModel.subsleepData = [NSArray arrayWithArray:SN_PublicClass.sleepDetailArr];
    }
}

// 睡眠同步结束
- (void)updateSleepEnd {
    [self updateSleepToday];
    [self updateSleepYes];
}

- (void)updateSleepToday {
    SleepModel *tmodel = SN_PublicClass.todaySleep;
    int        tDeep   = 0;
    int        tlight  = 0;
    int        tSober  = 0;

    SN_PublicClass.todaySleepArray = [SN_PublicClass.todaySleepArray sortedArrayUsingComparator:^NSComparisonResult (SleepSubModel *obj1, SleepSubModel *obj2) {
                                          return [obj2.subendTime compare:obj1.subendTime];
                                      }].mutableCopy;
    for (int i = 0; i < SN_PublicClass.todaySleepArray.count; i++) {
        SleepSubModel *tModel = [SN_PublicClass.todaySleepArray objectAtIndex:i];
        tDeep  += tModel.subdeep;
        tlight += tModel.sublight;
        tSober += tModel.subsober;
    }
    tmodel.deeps         = tDeep;
    tmodel.lights        = tlight;
    tmodel.sobers        = tSober;
    tmodel.durations     = tDeep + tlight + tSober;
    tmodel.sleepsubArray = [NSMutableArray arrayWithArray:SN_PublicClass.todaySleepArray];
    //    NSLog(@"今日睡眠>>>%d",tmodel.durations);
    SN_PublicClass.todaySleep = tmodel;
}

- (void)updateSleepYes {
    SleepModel *ymodel = SN_PublicClass.yesSleep;
    int        yDeep   = 0;
    int        ylight  = 0;
    int        ySober  = 0;

    SN_PublicClass.yesSleepArray = [SN_PublicClass.yesSleepArray sortedArrayUsingComparator:^NSComparisonResult (SleepSubModel *obj1, SleepSubModel *obj2) {
                                        return [obj2.subendTime compare:obj1.subendTime];
                                    }].mutableCopy;

    for (int i = 0; i < SN_PublicClass.yesSleepArray.count; i++) {
        SleepSubModel *yModel = [SN_PublicClass.yesSleepArray objectAtIndex:i];
        yDeep  += yModel.subdeep;
        ylight += yModel.sublight;
        ySober += yModel.subsober;
    }
    ymodel.deeps            = yDeep;
    ymodel.lights           = ylight;
    ymodel.sobers           = ySober;
    ymodel.durations        = yDeep + ylight + ySober;
    ymodel.sleepsubArray    = [NSMutableArray arrayWithArray:SN_PublicClass.yesSleepArray];
    SN_PublicClass.yesSleep = ymodel;
    //    NSLog(@"昨天睡眠>>>%d",ymodel.durations);
}

- (void)updateHeartData:(NSData *)heartData {
    UInt8 val[20] = {0};

    [heartData getBytes:&val length:heartData.length];
    if (val[3] == 0) {
        SN_PublicClass.todayHeartArray = [NSMutableArray array];
    } else if (val[3] == 6) {
        SN_PublicClass.yesHeartArray = [NSMutableArray array];
    }
    if (val[3] < 6) {
        for (int i = 4; i < 20; i++) {
            NSString *heart = [NSString stringWithFormat:@"%d", val[i]];
            [SN_PublicClass.todayHeartArray addObject:heart];
        }
    } else if (val[3] < 12) {
        for (int i = 4; i < 20; i++) {
            NSString *heart = [NSString stringWithFormat:@"%d", val[i]];
            [SN_PublicClass.yesHeartArray addObject:heart];
        }
    }
}

- (void)heartTransEnd {
}

// 温度大数据
- (void)updateTemData:(NSData *)heartData {
    UInt8 val[20] = {0};

    [heartData getBytes:&val length:heartData.length];
    NSString *content = [NSString stringWithFormat:@"%@", heartData];
    if (val[3] == 0) {
        SN_PublicClass.todayTemArray = [NSMutableArray array];
    } else if (val[3] == 6) {
        SN_PublicClass.yesTemArray = [NSMutableArray array];
    } else {
    }

    if (val[3] < 6) {
        for (int i = 4; i < 20; i += 2) {
            unsigned char by1    = val[i];//高8位
            unsigned char by2    = val[i+1];//低8位
            int           tem    = (by2|(by1<<8));
            NSString      *heart = [NSString stringWithFormat:@"%d", tem];
            [SN_PublicClass.todayTemArray addObject:heart];
        }
    } else if (val[3] < 12) {
        for (int i = 4; i < 20; i += 2) {
            unsigned char by1    = val[i];//高8位
            unsigned char by2    = val[i+1];//低8位
            int           tem    = (by2|(by1<<8));
            NSString      *heart = [NSString stringWithFormat:@"%d", tem];
            [SN_PublicClass.yesTemArray addObject:heart];
        }
    }
}

- (void)temTransEnd {
}

@end

//
//  BLESend.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/10.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "BLESend.h"
#import "BLEConnect.h"

@interface BLESend ()

@property (nonatomic, strong) NSMutableData *sendImageData;
@property (nonatomic, assign) NSInteger ackCount;
@property (nonatomic, assign) NSInteger more;
@property (nonatomic, assign) BOOL isSend;
@property (nonatomic, assign) NSInteger currentSendIndex;

@end

@implementation BLESend

- (instancetype)init {
    self = [super init];
    if (self) {
        if (KK_BLEConnect.model.bleUUID.length > 0) {
            [KK_BLELastDevice setObjectValue:KK_BLEConnect.model.bleUUID];
            [KK_BLELastDeviceMac setObjectValue:KK_BLEConnect.model.bleMAC];
        }
    }
    return self;
}

//设置时间
- (void)sendSetTimeforDeviceWithUpdate:(KKBLEAcceptBlock)block {
    NSDate *date      = [NSDate date];
    int    i          = date.year;
    int    yearHeight = 0;
    int    yearLow    = 0;
    yearHeight = i / 256;
    yearLow    = i - yearHeight *256;
    UInt8 val[10] = {KK_BLEConnect.sendHead, 0x02, 0x01, yearHeight, yearLow, date.month, date.day, date.hour, date.minute, date.second};
    [self sendBytes:val length:10 block:block];
}

//获取设备mac地址
- (void)sendGetDeviceMacAddress:(KKBLEAcceptBlock)block {
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x01, 0x01};
    [self sendBytes:val length:3 block:block];
}

//获取设备基本信息
- (void)sendGetDeviceBasicInfoWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x01, 0x02};
    [self sendBytes:val length:3 block:block];
}

//设置用户信息
- (void)sendUserInfoSettingWithBlock:(KKBLEAcceptBlock)block
{
    int ios = 0x00;  // 00 表示ios  //01 表示安卓 // 02 表示已经配对了
    int step1 = 0;
    int step2 = 0;
    int step3 = 0;
    int step4 = 0;
    NSInteger stepTarget = SN_USER.target_step;
    step4 = stepTarget %256;
    step3 = stepTarget /256;
    step2 = stepTarget /256/256;
    step1 = stepTarget /256/256/256;
    UInt8 val[17] = {KK_BLEConnect.sendHead, 0x02, 0x02, SN_USER.gender, SN_USER.age, SN_USER.height,
        (int)SN_USER.weight, SN_USER.lanuage, SN_USER.is12Time, SN_USER.isMetricSystem, ios, SN_USER.hand, SN_USER.temperatureSet, step1, step2, step3, step4};
    [self sendBytes:val length:17 block:block];
}

// 发送低电 抬腕 防丢 心率检测 翻腕切屏 //体温检测是否打开
- (void)sendSetLowElecState:(BOOL)state WithUpdateBlock:(KKBLEAcceptBlock)block {
    if (state) {
        UInt8 val[10] = {KK_BLEConnect.sendHead, 0x02, 0x0A, SN_USER.lowElecNotice, SN_USER.liftNotice, SN_USER.lostNotice, SN_USER.heartAutoCheck, SN_USER.changeScreen,0,SN_USER.temAutoCheck};
        [self sendBytes:val length:10 block:block];
    }
    else
    {
        UInt8 val[10] = {KK_BLEConnect.sendHead, 0x02, 0x0A, NO, NO, NO, NO, NO,0,SN_USER.temAutoCheck};
        [self sendBytes:val length:10 block:block];
    }
}

// 消息推送 电话 短信
- (void)sendSetPushNoticeState:(BOOL)state WithUpdateBlock:(KKBLEAcceptBlock)block {
    if (state) {
        int MessageNotice = 0;
        if (SN_USER.ancsNotice) {
            MessageNotice = 255;
        } else {
            MessageNotice = 0;
        }
        UInt8 val[6] = {KK_BLEConnect.sendHead, 0x02, 0x09, SN_USER.phoneNotice, SN_USER.smsNotice, MessageNotice};
        [self sendBytes:val length:6 block:block];
    }
    else
    {
        int MessageNotice = 0;
        if (SN_USER.ancsNotice) {
            MessageNotice = 255;
        } else {
            MessageNotice = 0;
        }
        UInt8 val[6] = {KK_BLEConnect.sendHead, 0x02, 0x09, NO, NO, NO};
        [self sendBytes:val length:6 block:block];
    }
}

//实时步数
- (void)sendGetRealStepWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x07, 0x01};
    [self sendBytes:val length:3 block:block];
}

//记步大数据
- (void)sendBigStepWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x07, 0x03};
    [self sendBytes:val length:3 block:block];
}

// 打开 1心率/2血氧/3血压
- (void)sendHeartStatus:(BOOL)status type:(NSInteger)type WithUpdate:(KKBLEAcceptBlock)block
{
    if (type == 1) {
        UInt8 val[4] = {KK_BLEConnect.sendHead, 0x05, 0x01, status};
        [self sendBytes:val length:4 block:block];
        NSLog(@"🙂心率检测设置>>>>");
    }
    if (type == 2) {
        UInt8 val[4] = {KK_BLEConnect.sendHead, 0x05, 0x04, status};
        [self sendBytes:val length:4 block:block];
        NSLog(@"🙂血氧检测设置>>>>");
    }
    if (type == 3) {
        UInt8 val[4] = {KK_BLEConnect.sendHead, 0x05, 0x05, status};
        [self sendBytes:val length:4 block:block];
        NSLog(@"🙂血压检测设置>>>>");
    }
}

// 找手环 开启或者关闭
- (void)sendSetFindBle:(BOOL)Find withUpdateBlock:(KKBLEAcceptBlock)block {
    UInt8 val[4] = {0};
    val[0] = KK_BLEConnect.sendHead;
    val[1] = 0x05;
    val[2] = 0x03;
    val[3] = Find;
    NSData *data = [[NSData alloc] initWithBytes:val length:4];
    [KK_BLEService senderDataToPeripheral:data];
}

//睡眠大数据
- (void)sendBigSleepWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x07, 0x04};
    [self sendBytes:val length:3 block:block];
}

//心率大数据
- (void)sendBigheartWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x07, 0x07};
    [self sendBytes:val length:3 block:block];
}

//免打扰
- (void)sendNotDisturbWithBlock:(KKBLEAcceptBlock)block {
    UInt8 val[8] = {KK_BLEConnect.sendHead, 0x02, 0x0B, SN_USER.quietNotice, SN_USER.quietNoticeStartH, SN_USER.quietNoticeStartM, SN_USER.quietNoticeEndH, SN_USER.quietNoticeEndM};
    NSData *data = [[NSData alloc] initWithBytes:val length:9];
    [KK_BLEService senderDataToPeripheral:data];
}

// 喝水提醒
- (void)sendDrinkWithUpdateBlock:(KKBLEAcceptBlock)block
{
    UInt8 val[18] = {KK_BLEConnect.sendHead, 0x02, 0x06, SN_USER.drinkRemind,0xff,
        SN_USER.drinkNoticeStartH, SN_USER.drinkNoticeStartM, SN_USER.drinkNoticeEndH, SN_USER.drinkNoticeEndM,
        0, 0, 0, 0,
        0, 0, 0, 0, SN_USER.drinkInterval};
    [self sendBytes:val length:18 block:block];
}

// 久坐提醒
- (void)sendSedentaryRemindWithUpdateBlock:(KKBLEAcceptBlock)block
{
    UInt8 val[18] = {KK_BLEConnect.sendHead, 0x02, 0x07, SN_USER.sedentaryRemind,SN_USER.sedentaryWeek,
        SN_USER.sedentaryNoticeStartH, SN_USER.sedentaryNoticeStartM, SN_USER.sedentaryNoticeEndH, SN_USER.sedentaryNoticeEndM,
        0, 0, 0, 0,
        0, 0, 0, 0, SN_USER.sedentaryInterval};
    [self sendBytes:val length:18 block:block];
}

// 闹钟
- (void)sendSetDeviceAlarmClockwithUpdateBlock:(KKBLEAcceptBlock)block
{
    UInt8 val[9] = {KK_BLEConnect.sendHead, 0x02, 0x04, SN_USER.alarmIndex, SN_USER.alarmOpen,SN_USER.alarmWeek,
        SN_USER.alarmHour,SN_USER.alarmMin,0};
    [self sendBytes:val length:9 block:block];
}
// 日常提醒
- (void)sendSetDeviceCalendarwithUpdateBlock:(KKBLEAcceptBlock)block
{
    UInt8 val[11] = {KK_BLEConnect.sendHead, 0x02, 0x05, SN_USER.calendarIndex, SN_USER.calendarOpen, SN_USER.calendarYear - 2000, SN_USER.calendarMonth,SN_USER.calendarDay,
        SN_USER.calendarHour, SN_USER.calendarMin,0};
    [self sendBytes:val length:11 block:block];
}

// 拍照控制
- (void)sendControlTakePhotoState:(BOOL)type WithUpdateBlock:(KKBLEAcceptBlock)block {
    UInt8 val[4] = {KK_BLEConnect.sendHead, 0x05, 0x02, type};
    [self sendBytes:val length:4 block:block];
}
//设备重启
- (void)sendDeviceRebootWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {KK_BLEConnect.sendHead, 0x00, 0x01};
    [self sendBytes:val length:3 block:block];
}

// 设置天气 0x00今天   0x01明天   0x02后天  .....
- (void)sendTemperatureParamWithDays:(NSArray<WeatherModel *>*)arrays withBlock:(KKBLEAcceptBlock)block
{
    for (WeatherModel *model in arrays) {
        [self sendTemperatureParamWithDaysOne:model withBlock:^(CBPeripheral *Peripher, id object, KKBLEAcceptType type) {
            
        }];
        usleep(5000);
    }
}

- (void)sendTemperatureParamWithDaysOne:(WeatherModel *)model withBlock:(KKBLEAcceptBlock)block
{
    NSArray *sunArr      = @[@"100"];
    NSArray *cloudArr    = @[@"101", @"102", @"103", @"101", @"101", @"201", @"202", @"203", @"204"];
    NSArray *snowArr     = @[@"400", @"401", @"402", @"403", @"404", @"405", @"406", @"407", ];
    NSArray *overcastArr = @[@"104", @"200", @"205", @"206", @"207", @"208", @"210", @"500", @"501", @"502", @"503", @"504", @"507", @"508"];
    NSArray *rainArr     = @[@"209", @"211", @"212", @"213", @"300", @"301", @"302", @"304", @"305", @"306", @"307", @"308", @"309", @"310", @"311", @"312", @"313"];
    NSArray *nonArr      = @[@"900", @"901", @"902"];
    int     type         = 0x00;
    
    NSString *typeString = [NSString stringWithFormat:@"%02d",model.weatherType];
    if ([sunArr containsObject:typeString]) {
        type = 0x01;
    } else if ([cloudArr containsObject:typeString]) {
        type = 0x02;
    } else if ([snowArr containsObject:typeString]) {
        type = 0x18;
    } else if ([overcastArr containsObject:typeString]) {
        type = 0x03;
    } else if ([rainArr containsObject:typeString]) {
        type = 0x07;
    } else if ([nonArr containsObject:typeString]) {
        type = 0x00;
    }
    int   maxTem     = model.maxTem;
    int   minTem     = model.minTem;
    int   currentTem = model.currentTem;
    UInt8 val[16]    = {0x05, 0x02, 0x0E, type, 0x00, 0x00, currentTem, 0x00, maxTem, 0x00, minTem,0x00,0x00,0x00,0x00,model.dateString};
    NSData *data = [[NSData alloc] initWithBytes:val length:16];
    [KK_BLEService senderDataToPeripheral:data];
    
    NSLog(@"天气序号:%ld,指令:%@",model.dateString,[NSString stringWithFormat:@"%@", data]);
    NSString *order = [NSString stringWithFormat:@"天气序号:%ld",model.dateString];
}

- (void)sendSportModeDataWithBlock:(KKBLEAcceptBlock)block {
    UInt8 val[3] = {0x05, 0x07, 0x0B};
    [self sendBytes:val length:3 block:block];
}
/**************************************************************************************************************/
// 向外设发送数据
- (void)sendBytes:(UInt8 *)val length:(NSInteger)length block:(KKBLEAcceptBlock)block {
    NSData *data = [[NSData alloc] initWithBytes:val length:length];
    [self sendData:data block:block];
}

- (void)sendData:(NSData *)data block:(KKBLEAcceptBlock)block {
    usleep(10000);
    KK_BLEAccept.acceptStatus = block;
    KK_BLEAccept.acceptType   = KKBLEAcceptTypeUnKnown;
    [KK_BLEService senderDataToPeripheral:data];
}

// 升级
- (void)sendUpdateFirmwareWithUpdateBlock:(KKBLEAcceptBlock)block {
    NSString *commandStr = [self ToHex:0x0102];
    NSData *data = [self hexToBytes:commandStr];
    NSLog(@"发送PHY升级指令>>>>>>>");
    [KK_BLEService senderDataToPeripheralC1:data];
}

- (void)sendNRFUpdateFirmwareWithUpdateBlock:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {0x05, 0x00, 0x02};
    [self sendBytes:val length:3 block:block];
}

//字符串转data 不带0x
- (NSData*)hexToBytes:(NSString *)str
{
    NSMutableData* data = [NSMutableData data];
    int idx;
    for (idx = 0; idx+2 <= str.length; idx+=2) {
        NSRange range = NSMakeRange(idx, 2);
        NSString* hexStr = [str substringWithRange:range];
        NSScanner* scanner = [NSScanner scannerWithString:hexStr];
        unsigned int intValue;
        [scanner scanHexInt:&intValue];
        [data appendBytes:&intValue length:1];
    }
    return data;
}

//将十进制转化为十六进制
- (NSString *)ToHex:(int)tmpid {
    NSString *nLetterValue;
    NSString *str =@"";
    int ttmpig;
    for (int i = 0; i<9; i++) {
        ttmpig=tmpid%16;
        tmpid=tmpid/16;
        switch (ttmpig)
        {
            case 10:
                nLetterValue =@"A";break;
            case 11:
                nLetterValue =@"B";break;
            case 12:
                nLetterValue =@"C";break;
            case 13:
                nLetterValue =@"D";break;
            case 14:
                nLetterValue =@"E";break;
            case 15:
                nLetterValue =@"F";break;
            default:
                nLetterValue = [NSString stringWithFormat:@"%u",ttmpig];
                
        }
        str = [nLetterValue stringByAppendingString:str];
        if (tmpid == 0) {
            break;
        }
    }
    if(str.length == 1 || str.length%2){
        return [NSString stringWithFormat:@"0%@",str];
    }else{
        return str;
    }
}


//温度大数据
- (void)sendBigTemWithUpdate:(KKBLEAcceptBlock)block
{
    UInt8 val[3] = {0x05, 0x07, 0x0c};
    [self sendBytes:val length:3 block:block];
}

@end

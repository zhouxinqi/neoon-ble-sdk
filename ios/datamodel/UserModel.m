//
//  UserModel.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/11.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "UserModel.h"

@implementation UserModel

- (instancetype)init
{
    self = [super init];
    if (self) {
        _height        = 170;
        _weight        = 65;
        _gender = 2;
        _age                = 25;
        _isMetricSystem     = NO;
        _is12Time           = NO;
        _target_step        = 10000;
        _hand               = NO;
        _temperatureSet     = NO;
        _lanuage = 0;
        _lowElecNotice = YES;
        _liftNotice = YES;
        _lostNotice = NO;
        _heartAutoCheck = YES;
        _changeScreen = NO;
        _ancsNotice = YES;
        _phoneNotice = YES;
        _smsNotice = YES;
        _quietNotice = NO;
        _quietNoticeStartH = 0;
        _quietNoticeStartM = 0;
        _quietNoticeEndH = 23;
        _quietNoticeEndM = 59;
        _drinkRemind = NO;
        _drinkInterval = 2;
        _drinkWeek = 255;
        _drinkNoticeStartH = 0;
        _drinkNoticeStartM = 0;
        _drinkNoticeEndH = 23;
        _drinkNoticeEndM = 59;
        _sedentaryRemind = NO;
        _sedentaryInterval = 3;
        _sedentaryWeek = 255;
        _sedentaryNoticeStartH = 0;
        _sedentaryNoticeStartM = 0;
        _sedentaryNoticeEndH = 23;
        _sedentaryNoticeEndM = 59;
        _alarmOpen = NO;
        _alarmWeek = 255;
        _alarmIndex = 0;
        _calendarOpen = NO;
        _calendarIndex = 0;
        _temAutoCheck = YES;
    }
    return self;
}

// 星期赋值的示例 将要开启的星期赋值到对应的bit上面
//UInt8 week = 0;
//week = bitInsertInt(week, 0, model.SUN);
//week = bitInsertInt(week, 1, model.MON);
//week = bitInsertInt(week, 2, model.TUE);
//week = bitInsertInt(week, 3, model.WED);
//week = bitInsertInt(week, 4, model.THU);
//week = bitInsertInt(week, 5, model.FRI);
//week = bitInsertInt(week, 6, model.SAT);
//week = bitInsertInt(week, 7, model.Once);

@end

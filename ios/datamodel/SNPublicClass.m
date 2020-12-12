//
//  SNPublicClass.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/12.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "SNPublicClass.h"

@implementation SNPublicClass

DEF_SINGLETON(SNPublicClass)

- (instancetype)init {
    self = [super init];
    if (self) {
        _stepArray    = [NSMutableArray array];
        _sportTotal = 0;
        _checkType = 1;
        _userModel = [[UserModel alloc] init];
    }
    return self;
}

- (SportModel *)todaySport
{
    if (!_todaySport) {
        _todaySport = [[SportModel alloc] init];
        _todaySport.dateString =  [[NSDate date] dateToDayString];
    }
    return _todaySport;
}

- (SportModel *)yesSport
{
    if (!_yesSport) {
        _yesSport = [[SportModel alloc] init];
        _yesSport.dateString = [[[NSDate date] dateAfterDay:-1] dateToDayString];
    }
    return _yesSport;
}

- (SleepModel *)todaySleep
{
    if (!_todaySleep) {
        _todaySleep = [[SleepModel alloc] init];
        _todaySleep.dateString =  [[NSDate date] dateToDayString];
    }
    return _todaySleep;
}

- (SleepModel *)yesSleep
{
    if (!_yesSleep) {
        _yesSleep = [[SleepModel alloc] init];
        _yesSleep.dateString = [[[NSDate date] dateAfterDay:-1] dateToDayString];
    }
    return _yesSleep;
}

- (NSString*)todayDetailHeart
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _todayHeartArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}

- (NSString*)yesDetailHeart
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _yesHeartArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}

- (NSString *)todayDetailTem
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _todayTemArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}

- (NSString*)yesDetailTem
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _yesTemArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}

@end

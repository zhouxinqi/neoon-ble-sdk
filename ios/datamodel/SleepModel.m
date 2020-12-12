//
//  SleepModel.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/12.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "SleepModel.h"

/***************************************************睡眠模型***********************************************/
@implementation SleepSubModel
- (instancetype)init {
    self = [super init];
    if (self) {
        _subdeep          = 0;
        _sublight         = 0;
        _subsober         = 0;
        _substartTime     = @"";
        _subendTime       = @"";
        _subdate          = [[NSDate date] dateToDayString];
    }
    return self;
}

- (NSArray *)subsleepData {
    return _subsleepData ? _subsleepData : @[];
}

@end

@implementation SleepModel

@end

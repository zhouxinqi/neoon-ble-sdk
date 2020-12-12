//
//  SportModel.m
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/12.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import "SportModel.h"

@implementation SportModel
- (instancetype)init {
    self = [super init];
    if (self) {
        _step         = 0;
        _distance     = 0;
        _calory       = 0;
        NSMutableArray *stepArr     = [NSMutableArray array];
        NSMutableArray *distanceArr = [NSMutableArray array];
        NSMutableArray *calArr      = [NSMutableArray array];
        for (int i = 0; i < 48; i++) {
            [stepArr addObject:StrByInt(0)];
            [distanceArr addObject:StrByInt(0)];
            [calArr addObject:StrByInt(0)];
        }
        _stepArray     = [NSArray arrayWithArray:stepArr];
        _distanceArray = [NSArray arrayWithArray:distanceArr];
        _calorArray    = [NSArray arrayWithArray:calArr];
    }
    return self;
}

- (NSString *)showStepDetail
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _stepArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}

- (NSString *)showCaloryDetail
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _calorArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}

- (NSString *)showDistanceDetail
{
    NSMutableString *string = [NSMutableString string];
    for (NSString *s in _distanceArray) {
        [string appendString:[NSString stringWithFormat:@"%@ ",s]];
    }
    return string;
}
@end

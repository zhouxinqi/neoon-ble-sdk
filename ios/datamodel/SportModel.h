//
//  SportModel.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/12.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SportModel : NSObject
@property (nonatomic, strong) NSString  *dateString;        // 日期
@property (nonatomic, strong) NSArray   *stepArray;         // 步数详情 每30分钟
@property (nonatomic, strong) NSArray   *calorArray;        // 卡路里详情 每30分钟
@property (nonatomic, strong) NSArray   *distanceArray;     // 距离详情 每30分钟
@property (nonatomic, assign) NSInteger step;               // 总步数
@property (nonatomic, assign) NSInteger calory;             // 总消耗卡路里
@property (nonatomic, assign) NSInteger distance;           // 总距离
@property (nonatomic, assign) NSInteger stepDuration;       // 总运动时间
@property (nonatomic, strong) NSString *showStepDetail;
@property (nonatomic, strong) NSString *showCaloryDetail;
@property (nonatomic, strong) NSString *showDistanceDetail;

@end

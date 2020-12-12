//
//  SleepModel.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/12.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import <Foundation/Foundation.h>

// 每次睡眠模型
@interface SleepSubModel : NSObject
@property (nonatomic, strong) NSString  *subdate;        // 每次睡眠日期
@property (nonatomic, strong) NSString  *substartTime;   // 每次睡眠开始时间
@property (nonatomic, strong) NSString  *subendTime;     // 每次睡眠醒来时间
@property (nonatomic, assign) NSInteger subdeep;         // 深睡时长
@property (nonatomic, assign) NSInteger sublight;        // 浅睡时长
@property (nonatomic, assign) NSInteger subsober;        // 清醒时长
@property (nonatomic, strong) NSArray   *subsleepData;   // 每次睡眠详情(睡眠状态 加 每段时长）
@end



@interface SleepModel : NSObject
@property (nonatomic, strong) NSString       *dateString;   // 日期
@property (nonatomic, assign) NSInteger      deeps;         // 深睡
@property (nonatomic, assign) NSInteger      lights;        // 浅睡
@property (nonatomic, assign) NSInteger      sobers;        // 清醒
@property (nonatomic, assign) NSInteger      durations;     // 睡眠总时长
@property (nonatomic, strong) NSMutableArray *sleepsubArray;// 每一次睡眠集合

@end

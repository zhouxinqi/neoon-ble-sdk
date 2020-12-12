//
//  SportModeModel.h
//  project
//
//  Created by 张淋 on 2019/6/1.
//  Copyright © 2019 黄建华. All rights reserved.
//

typedef enum {
    SelectDateDay   = 0,
    SelectDateWeek  = 1,
    SelectDateMonth = 2,
    SelectDateYear  = 3,
    SelectDateThreeMonths  = 4,
} SelectDateType;

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface SportModeModel : NSObject

@property (nonatomic, strong) NSString      *startTime;       // 开始时间
@property (nonatomic, strong) NSString      *weekDate;        // 星期
@property (nonatomic, assign) NSInteger     sportMode;        // 运动模式
@property (nonatomic, assign) NSInteger     timeLength;       // 运动时长
@property (nonatomic, assign) NSInteger     stepNum;          // 记步数
@property (nonatomic, assign) NSInteger     distance;         // 距离
@property (nonatomic, assign) NSInteger     calories;         // 消耗卡路里
@property (nonatomic, assign) NSInteger     heartAve;         // 平均心率
@property (nonatomic, assign) NSInteger     heartMax;         // 最高心率
@property (nonatomic, assign) NSInteger     heartMin;         // 最小心率
@property (nonatomic, assign) NSInteger     sportDataNums;    // 单一类型数据的次数

//@property (nonatomic, strong) NSString *user_id;
//@property (nonatomic, strong) NSString *mac;
//@property (nonatomic, strong) NSString *dateString;

+ (SportModeModel *)updataSportModeData:(NSData *)sportModeData;
//+ (NSArray *)getSportModeDataWithType:(SelectDateType)type;
//+ (NSArray *)getDetailDataWithDay;
//+ (NSDictionary *)getEachSportModeData:(NSInteger)sportModeType slectType:(SelectDateType)slectType;
//+ (int)getTotalSportTime:(SelectDateType)sportModeType;
//+ (NSDictionary *)getWeekSportModeDataWithType:(NSInteger)type;
@end

NS_ASSUME_NONNULL_END

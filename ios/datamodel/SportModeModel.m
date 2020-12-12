//
//  SportModeModel.m
//  project
//
//  Created by 张淋 on 2019/6/1.
//  Copyright © 2019 黄建华. All rights reserved.
//

#import "SportModeModel.h"

@implementation SportModeModel

- (instancetype)init {
    self = [super init];
    if (self) {
//        _user_id      = SN_USER.user_id;
//        _dateString   = [[NSDate date] dateToDayString];
//        _mac = KK_BLEModel.bleMAC;
    }
    return self;
}

// 为模型创建空值的对象
+ (void)creatEmptyDataArrayWithModel:(SportModel *)model {
}

// 表名
+ (NSString *)getTableName {
    return @"SportModeModel";
}

// 表版本
+ (int)getTableVersion {
    return 1;
}

+ (void)initialize {
    [super initialize];
}

// 解析数据
+ (SportModeModel *)updataSportModeData:(NSData *)sportModeData {
    UInt8 val[20] = {0};
    [sportModeData getBytes:&val length:sportModeData.length];
    // 序号
    int num = val[3] & 0xff;
    // 运动模式
    int sportMode = val[4] & 0xff;
    // 持续时间
    int time = ((val[10] & 0xff) | (val[9] << 8));
//    int time = 0;
//    if (num % 2 ==0) {
//        time = 180;
//    } else {
//        time = 1;
//    }
    
    // 记步数
    int step = ((val[12] & 0xff) | (val[11] << 8));
    // 距离
    int distance = ((val[14] & 0xff) | (val[13] << 8));
    // 卡路里
    int calories = ((val[16] & 0xff) | (val[15] << 8));
    // max 心率
    int heartMax = val[17] & 0xff;
    // min 心率
    int heartMin = val[18] & 0xff;
    // 平均心率
    int heartAve = val[19] & 0xff;
    // 开始时间
    int secondSinceNowFrom2000 = (val[8] & 0xff) | ((val[7] & 0xff) << 8 ) | ((val[6] & 0xff) << 16 ) | ((val[5] & 0xff) << 24 );
    NSLog(@"num :%d, sportMode:%d,time:%d,step:%d,distance:%d,calories:%d,heartMax:%d,heardMin:%d,heartAve:%d,secondSinceNowFrom2000:%d",num,sportMode,time,step,distance,calories,heartMax,heartMin,heartAve,secondSinceNowFrom2000);
    
    NSDateComponents *compt = [[NSDateComponents alloc] init];
    [compt setYear:2000];
    [compt setMonth:1];
    [compt setDay:1];
    [compt setHour:0];
    [compt setMinute:0];
    [compt setSecond:0];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDate *date = [calendar dateFromComponents:compt];
    NSInteger NowTimestamp = [SportModeModel getNowTimestamp:date];
    
    double dTimeStamp = NowTimestamp +secondSinceNowFrom2000;
    NSDate *startTimeDat = [NSDate dateWithTimeIntervalSince1970:dTimeStamp];

    NSDateFormatter* dateFormatter = [NSDate dateFormatterTemp];
    [dateFormatter setDateStyle:NSDateFormatterFullStyle];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm"];

    NSString *startTime = [dateFormatter stringFromDate:startTimeDat];

    NSLog(@"startTime:%@",startTime);
    
    SportModeModel *model = [[SportModeModel alloc] init];
    model.startTime = startTime;
    model.sportMode = sportMode;
    model.timeLength =time;
    model.stepNum = step;
    model.distance = distance;
    model.calories = calories;
    model.heartMax = heartMax;
    model.heartMin = heartMin;
    model.heartAve = heartAve;
    model.weekDate = [SportModeModel weekDayStr:startTime isInterNotional:NO];
//        [model saveToDB];
    return model;
}

+ (NSString*)weekDayStr:(NSString*)format isInterNotional:(BOOL)isInterNotional{
    
    NSString *weekDayStr = nil;
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    if(format.length>=10) {
        NSString *nowString = [format substringToIndex:10];
        NSArray *array = [nowString componentsSeparatedByString:@"-"];
        if(array.count==0) {
            array = [nowString componentsSeparatedByString:@"/"];
        }
        
        if(array.count>=3) {
            NSInteger year = [[array objectAtIndex:0] integerValue];
            NSInteger month = [[array objectAtIndex:1] integerValue];
            NSInteger day = [[array objectAtIndex:2] integerValue];
            [comps setYear:year];
            [comps setMonth:month];
            [comps setDay:day];
        }
    }
    //日历
    NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    //获取传入date
    NSDate *_date = [gregorian dateFromComponents:comps];
    
    NSDateComponents *weekdayComponents = [gregorian components:NSCalendarUnitWeekday fromDate:_date];
    NSInteger week = [weekdayComponents weekday];
    if (isInterNotional == YES) {
        switch(week) {
            case 1:
                weekDayStr = @"周日";
                break;
            case 2:
                weekDayStr = @"周一";
            case 3:
                weekDayStr = @"周二";
                break;
            case 4:
                weekDayStr = @"周三";
                break;
            case 5:
                weekDayStr = @"周四";
                break;
            case 6:
                weekDayStr = @"周五";
                break;
            case 7:
                weekDayStr = @"周六";
                break;
            default:
                weekDayStr =@"";
                break;
        }
    } else {
        switch(week) {
            case 1:
                weekDayStr = @"周日";
                break;
            case 2:
                weekDayStr = @"周一";
                break;
            case 3:
                weekDayStr = @"周二";
                break;
            case 4:
                weekDayStr = @"周三";
                break;
            case 5:
                weekDayStr = @"周四";
                break;
            case 6:
                weekDayStr = @"周五";
                break;
            case 7:
                weekDayStr = @"周六";
                break;
            default:
                weekDayStr =@"";
                break;
        }
    }
    
    return weekDayStr;
}


+ (NSDate*)getDateWithTimestamp:(double)timestamp
{
    double dTimeStamp = timestamp;
    NSDate *confromTimesp = [NSDate dateWithTimeIntervalSince1970:dTimeStamp];
    return confromTimesp;
}

+(NSInteger)getNowTimestamp:(NSDate *)date{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"]; // ----------设置你想要的格式,hh与HH的区别:分别表示12小时制,24小时制
    //设置时区,这个对于时间的处理有时很重要
    //    NSTimeZone* timeZone = [NSTimeZone timeZoneWithName:@"Asia/Beijing"];
    NSTimeZone *tz = [NSTimeZone systemTimeZone];
    [formatter setTimeZone:tz];
    NSDate *datenow = date;//现在时间
    //    NSLog(@"设备当前的时间:%@",[formatter stringFromDate:datenow]);
    //时间转时间戳的方法:
    NSInteger timeSp = [[NSNumber numberWithDouble:[datenow timeIntervalSince1970]] integerValue];
    //    NSLog(@"设备当前的时间戳:%ld",(long)timeSp); //时间戳的值
    return timeSp;
}


//// 获取具体天的运动数据
//+ (NSArray *)getDetailDataWithDay{
//    NSString *where = [NSString stringWithFormat:@"startTime >= '%@ %@' and startTime <= '%@ %@' and user_id = '%@'",[SN_PublicClass.selectDate dateToDateWithDate],@"00:00",[SN_PublicClass.selectDate dateToDateWithDate],@"23:59",SN_USER.user_id];
//    NSArray *result = [SportModeModel searchWithWhere:where orderBy:@"startTime desc" offset:0 count:0];
//    return result;
//}
//
//// 获取本周、本月、三个月内的表视图要展示的数据
//+ (NSArray *)getSportModeDataWithType:(SelectDateType)type {
//    NSString *time = [[SNPublicClass sharedInstance] showDateWithType:type];
//    NSArray *array = [time componentsSeparatedByString:@"-"];
//    NSArray *result = [NSArray array];
//    if (type == SelectDateWeek) {
//        NSString *startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//        NSString *endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//        NSString *where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and user_id = '%@'",startTime,endTime,SN_USER.user_id];
//        result = [SportModeModel searchWithWhere:where orderBy:@"startTime desc" offset:0 count:0];
//    } else if(type == SelectDateMonth) {
//        NSString *startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//        NSString *endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//        NSString *where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and user_id = '%@'",startTime,endTime,SN_USER.user_id];
//        result = [SportModeModel searchWithWhere:where orderBy:@"startTime desc" offset:0 count:0];
//    } else if(type == SelectDateThreeMonths) {
//        NSString *startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@"-01 00:00"];
//        NSString *endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@"-31 23:59"];
//        NSString *where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and user_id = '%@'",startTime,endTime,SN_USER.user_id];
//        result = [SportModeModel searchWithWhere:where orderBy:@"startTime desc" offset:0 count:0];
//    } else {
//        result = @[];
//    }
//    return result;
//}
//
//// 获取本周的具体运动类型的数据
//+ (NSDictionary *)getWeekSportModeDataWithType:(NSInteger)type {
//    NSString *time = [[SNPublicClass sharedInstance] showDateWithType:SelectDateWeek];
//    NSArray *array = [time componentsSeparatedByString:@"-"];
//    NSString *startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//    NSString *endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//
//    // 符合条件区间的模式的总数值
//    NSString *totalWhere = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %ld and user_id = '%@'",startTime,endTime,(long)type,SN_USER.user_id];
//    NSArray *totalResult = [SportModeModel searchWithWhere:totalWhere orderBy:@"startTime desc" offset:0 count:0];
//    NSMutableArray *timeLengthArray   = [SportModeModel searchColumn:@"timeLength" where:totalWhere orderBy:nil offset:0 count:0];
//    CGFloat totalTime = [[timeLengthArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//
//    NSMutableArray *eachDateArray = [NSMutableArray array];
//    // 遍历获取本周周一至周五数据进行比总
//
//    for (int i = 0; i < 7; i++) {
//        NSString *date = @"";
//        if (i == 0) {
//            date = @"周日";
//        }
//        else if (i == 1) {
//            date = @"周一";
//        }
//        else if (i == 2) {
//            date = @"周二";
//        }
//        else if (i == 3) {
//            date = @"周三";
//        }
//        else if (i == 4) {
//            date = @"周四";
//        }
//        else if (i == 5) {
//            date = @"周五";
//        }
//        else if (i == 6) {
//            date = @"周六";
//        } else {
//
//        }
//
//        // 符合条件区间的模式的总数值
//        NSArray *result = [SportModeModel searchWithWhere:totalWhere orderBy:nil offset:0 count:0];
//        CGFloat timeSum = 0.0;
//        int aType = (int)type;
//        NSMutableArray *weekDateArray = [NSMutableArray array];
//        [weekDateArray removeAllObjects];
//        NSMutableArray *sumArray = [NSMutableArray array];
//        [sumArray removeAllObjects];
//        for (SportModeModel *model in result) {
//            if ([model.weekDate isEqualToString:date]) {
//                [weekDateArray addObject:model];
//                [sumArray addObject:@(model.timeLength)];
//            } else {
//
//            }
//        }
//        // 有值时
//        if (weekDateArray.count != 0) {
//            timeSum = [[sumArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//            NSString *ratioStr = [NSString stringWithFormat:@"%.4f",timeSum / totalTime];
//            [eachDateArray addObject:@{@"ratio":@([ratioStr doubleValue] * 100),@"mode":@(aType),@"array":weekDateArray, @"time":date}];
//            NSLog(@"date:%@ \n eachDateArray:%@",date,eachDateArray);
//        }
//    }
//    NSDictionary *dic = @{@"totalResult":totalResult,@"array":eachDateArray};
//
//    return dic;
//}
//
//// 获取每一类型运动的总数
//+ (NSDictionary *)getEachSportModeData:(NSInteger)sportModeType slectType:(SelectDateType)selectType {
//    NSString *time = [[SNPublicClass sharedInstance] showDateWithType:selectType];
//    NSArray *array = [time componentsSeparatedByString:@"-"];
//    NSDictionary *dic = @{};
//    NSString *startTime;
//    NSString *endTime;
//    NSString *where;
//    if (selectType == SelectDateWeek) {
//        startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//        endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//        // 符合条件区间的模式的总数值
//        where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %ld and user_id = '%@'",startTime,endTime,(long)sportModeType,SN_USER.user_id];
//    } else if(selectType == SelectDateMonth) {
//        startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//        endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//        where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %ld and user_id = '%@'",startTime,endTime,sportModeType,SN_USER.user_id];
//    } else if(selectType == SelectDateThreeMonths) {
//        startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@"-01 00:00"];
//        endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@"-31 23:59"];
//        where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %ld and user_id = '%@'",startTime,endTime,sportModeType,SN_USER.user_id];
//    } else {
//
//    }
//
//    // 单一项运动的所有符合的数组
//    NSArray *result = [SportModeModel searchWithWhere:where orderBy:@"startTime desc" offset:0 count:0];
//    // 单一项运动时长
//    NSMutableArray *timeLengthArray   = [SportModeModel searchColumn:@"timeLength" where:where orderBy:nil offset:0 count:0];
//    CGFloat timeLength = [[timeLengthArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//    // 单一项运动总距离
//    NSMutableArray *sectionDistanceTotalArray   = [SportModeModel searchColumn:@"distance" where:where orderBy:nil offset:0 count:0];
//    CGFloat sectionDistanceTotal = [[sectionDistanceTotalArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//    // 单一项运动总卡路里
//    NSMutableArray *sectionCaloriesTotalArray   = [SportModeModel searchColumn:@"calories" where:where orderBy:nil offset:0 count:0];
//    CGFloat sectionCaloriesTotal = [[sectionCaloriesTotalArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//    // 单一项运动总平均心率
//    NSMutableArray *sectionHeartAveTotalArray   = [SportModeModel searchColumn:@"heartAve" where:where orderBy:nil offset:0 count:0];
//    CGFloat sectionHeartAveTotal = [[sectionHeartAveTotalArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//    CGFloat aveHeartAve = sectionHeartAveTotal / result.count;
//    // 单一项运动总最大心率
//    NSMutableArray *sectionHeartMaxTotalArray   = [SportModeModel searchColumn:@"heartMax" where:where orderBy:nil offset:0 count:0];
//    CGFloat sectionHeartMaxAveTotal = [[sectionHeartMaxTotalArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//    CGFloat aveHeartMaxAve = sectionHeartMaxAveTotal / result.count;
//
//
//    // 区间的总数值
//    NSString *whereTotal = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and user_id = '%@'",startTime,endTime,SN_USER.user_id];
//    // 总时长
//    NSMutableArray *sectionTimeTotalArray   = [SportModeModel searchColumn:@"timeLength" where:whereTotal orderBy:nil offset:0 count:0];
//    CGFloat sectionTimeTotal = [[sectionTimeTotalArray valueForKeyPath:@"@sum.floatValue"] floatValue];
//
//    if (selectType == SelectDateWeek) {
//
//    } else {
//        if (result.count != 0) {
//            NSMutableArray *array = [NSMutableArray array];
//            [array removeAllObjects];
//            SportModeModel *model = [[SportModeModel alloc] init];
//            model.sportMode = sportModeType;
//            model.calories = sectionCaloriesTotal;
//            model.distance = sectionDistanceTotal;
//            model.heartAve = aveHeartAve;
//            model.heartMax = aveHeartMaxAve;
//            model.timeLength = timeLength;
//            model.sportDataNums = result.count;
//            [array addObject:model];
//            result = array;
//        } else {
//            result = @[];
//        }
//    }
//    NSString *ratioStr = [NSString stringWithFormat:@"%.4f",timeLength / sectionTimeTotal];
//    dic = @{@"ratio":@([ratioStr doubleValue] * 100),@"mode":@(sportModeType),@"array":result};
//
//    return dic;
//}
//
//// 获取本周的运动时长的总值
//+ (int)getTotalSportTime:(SelectDateType)sportModeType {
//    NSString *time = [[SNPublicClass sharedInstance] showDateWithType:sportModeType];
//    NSArray *array = [time componentsSeparatedByString:@"-"];
//    NSString *startTime;
//    NSString *endTime;
//    NSString *where;
//    if (sportModeType == SelectDateWeek) {
//        startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//        endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//        // 符合条件区间的模式的总数值
//        where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %ld and user_id = '%@'",startTime,endTime,(long)sportModeType,SN_USER.user_id];
//    } else if(sportModeType == SelectDateMonth) {
//        startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 00:00"];
//        endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@" 23:59"];
//        where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %u and user_id = '%@'",startTime,endTime,sportModeType,SN_USER.user_id];
//    } else if(sportModeType == SelectDateThreeMonths) {
//        startTime = [[array[0] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@"-01 00:00"];
//        endTime = [[array[1] stringByReplacingOccurrencesOfString:@"/" withString:@"-"] stringByAppendingString:@"-31 23:59"];
//        where = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and sportMode = %u and user_id = '%@'",startTime,endTime,sportModeType,SN_USER.user_id];
//    } else {
//
//    }
//    // 区间的总数值
//    NSString *whereTotal = [NSString stringWithFormat:@"startTime >= '%@' and startTime <= '%@' and user_id = '%@'",startTime,endTime,SN_USER.user_id];
//    // 时长
//    NSMutableArray *sectionTimeTotalArray   = [SportModeModel searchColumn:@"timeLength" where:whereTotal orderBy:nil offset:0 count:0];
//    int sectionTimeTotal = [[sectionTimeTotalArray valueForKeyPath:@"@sum.floatValue"] intValue];
//    return sectionTimeTotal;
//}

@end

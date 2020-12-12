//
//  SNPublicClass.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/12.
//  Copyright © 2018年 黄建华. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SportModel.h"
#import "SleepModel.h"
#import "SportModeModel.h"

#define SN_PublicClass ([SNPublicClass sharedInstance])
#define SN_USER        ([SNPublicClass sharedInstance].userModel)
@interface SNPublicClass : NSObject

    AS_SINGLETON(SNPublicClass)

@property (nonatomic, strong) NSMutableArray *stepArray;       // 步数
@property (nonatomic, assign) NSInteger      sportTotal;       // 总步数临时数组
// 今日与昨天的参数（数据模型)
@property (nonatomic, strong) SportModel *todaySport;          // 今天运动详情
@property (nonatomic, strong) SportModel *yesSport;            // 昨天运动详情
@property (nonatomic, strong) SleepModel *todaySleep;          // 今天睡眠详情
@property (nonatomic, strong) SleepModel *yesSleep;            // 昨天睡眠详情
@property (nonatomic, assign) NSInteger  checkType;            // 检测类型 1心率/2血氧/3血压

@property (nonatomic, strong) NSMutableArray *todaySleepArray; // 今天睡眠数临时数组
@property (nonatomic, strong) NSMutableArray *yesSleepArray;   // 昨天睡眠数临时数组
@property (nonatomic, strong) NSMutableArray *sleepDetailArr;  // 睡眠详情临时数组
@property (nonatomic, strong) SleepSubModel  *sleepSubModel;   // 睡眠单次记录临时数组

@property (nonatomic, strong) NSMutableArray *todayHeartArray; // 今天心率数组
@property (nonatomic, strong) NSMutableArray *yesHeartArray;   // 昨天心率数组
@property (nonatomic, strong) NSString       *todayDetailHeart; // 今天心率详情
@property (nonatomic, strong) NSString       *yesDetailHeart;  // 昨天心率详情
@property (nonatomic, strong) NSString       *bpValue;

@property (nonatomic, strong) UserModel *userModel;  // 用户设置模型

@property (nonatomic, strong) NSMutableArray *todayTemArray; // 今天体温数组
@property (nonatomic, strong) NSMutableArray *yesTemArray;   // 昨天体温数组
@property (nonatomic, strong) NSString       *todayDetailTem; // 今天体温详情
@property (nonatomic, strong) NSString       *yesDetailTem;  // 昨天体温详情


@end

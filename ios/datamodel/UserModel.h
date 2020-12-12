//
//  UserModel.h
//  SDK DEMO
//
//  Created by 黄建华 on 2018/7/11.
//  Copyright © 2018年 黄建华. All rights reserved.
//

// 用户设备模型
#import <Foundation/Foundation.h>

@interface UserModel : NSObject
// 用户设置属性(一般用默认值,需要设置的时候请初始化或者赋值)
@property(nonatomic, assign) NSInteger target_step;          // 步数目标
@property(nonatomic, assign) NSInteger height;               // 身高
@property(nonatomic, assign) NSInteger weight;               // 体重
@property(nonatomic, assign) NSInteger gender;               // 1为男 2为女 默认男属性
@property(nonatomic, assign) NSInteger age;                  // 年龄
@property(nonatomic, assign) NSInteger lanuage;              // 语言 0中文 1英文 其他
@property(nonatomic, assign) BOOL is12Time;                  // 24小时为NO 12小时为YES
@property(nonatomic, assign) BOOL isMetricSystem;            // 公制为NO 英制为YES
@property(nonatomic, assign) BOOL hand;                      // 左右手
@property(nonatomic, assign) BOOL temperatureSet;            // NO 为摄氏度 YES为华氏度
// 提醒开关变量
@property(nonatomic, assign) BOOL lowElecNotice;             // 低电量提醒
@property(nonatomic, assign) BOOL liftNotice;                // 抬腕亮屏 
@property(nonatomic, assign) BOOL lostNotice;                // 防丢提醒
@property(nonatomic, assign) BOOL heartAutoCheck;            // 心率自动检测开关
@property(nonatomic, assign) BOOL changeScreen;              // 翻腕切屏
@property(nonatomic, assign) BOOL ancsNotice;                // 消息推送
@property(nonatomic, assign) BOOL phoneNotice;               // 来电提醒
@property(nonatomic, assign) BOOL smsNotice;                 // 短信提醒
// 免打扰设置(一般为关闭)
@property(nonatomic, assign) BOOL quietNotice;               // 免打扰开关
@property(nonatomic, assign) NSInteger quietNoticeStartH;    // 免打扰开始时
@property(nonatomic, assign) NSInteger quietNoticeStartM;    // 免打扰开始分
@property(nonatomic, assign) NSInteger quietNoticeEndH;      // 免打扰结束时
@property(nonatomic, assign) NSInteger quietNoticeEndM;      // 免打扰结束分

// 喝水 久坐 闹钟 (星期是从星期天开始~星期六 对应这个字节的0~6bit 最后一个bit为一次) 现在默认是每天//
// 喝水提醒设置
@property(nonatomic, assign) BOOL drinkRemind;               // 喝水提醒开关
@property(nonatomic, assign) NSInteger drinkInterval;        // 喝水间隔    测试为5分钟 正常范围是30分钟以上
@property(nonatomic, assign) int drinkWeek;                  // 喝水星期几
@property(nonatomic, assign) NSInteger drinkNoticeStartH;    // 喝水提醒开始时
@property(nonatomic, assign) NSInteger drinkNoticeStartM;    // 喝水提醒开始分
@property(nonatomic, assign) NSInteger drinkNoticeEndH;      // 喝水提醒结束时
@property(nonatomic, assign) NSInteger drinkNoticeEndM;      // 喝水提醒结束分

// 久坐提醒设置
@property(nonatomic, assign) BOOL sedentaryRemind;           // 久坐提醒开关
@property(nonatomic, assign) NSInteger sedentaryInterval;    // 久坐间隔    测试为5分钟 正常范围是30分钟以上
@property(nonatomic, assign) int sedentaryWeek;              // 喝水星期几
@property(nonatomic, assign) NSInteger sedentaryNoticeStartH;// 久坐提醒开始时
@property(nonatomic, assign) NSInteger sedentaryNoticeStartM;// 久坐提醒开始分
@property(nonatomic, assign) NSInteger sedentaryNoticeEndH;  // 久坐提醒结束时
@property(nonatomic, assign) NSInteger sedentaryNoticeEndM;  // 久坐提醒结束分

// 闹钟提醒设置 闹钟最多5个 序号是0 - 4 DEMO里面演示为1个
@property(nonatomic, assign) BOOL alarmOpen;
@property(nonatomic, assign) NSInteger alarmIndex;
@property(nonatomic, assign) int alarmWeek;
@property(nonatomic, assign) NSInteger alarmHour;
@property(nonatomic, assign) NSInteger alarmMin;
// 日程提醒设置 日程最多5个 序号是0 - 4 DEMO里面演示为1个
@property(nonatomic, assign) BOOL calendarOpen;
@property(nonatomic, assign) NSInteger calendarIndex;
@property(nonatomic, assign) NSInteger calendarYear;
@property(nonatomic, assign) NSInteger calendarMonth;
@property(nonatomic, assign) NSInteger calendarDay;
@property(nonatomic, assign) NSInteger calendarHour;
@property(nonatomic, assign) NSInteger calendarMin;

@property (nonatomic, assign)BOOL temAutoCheck;              // 体温自动检测 默认开
@end

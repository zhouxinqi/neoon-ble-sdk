//
//  WeatherModel.h
//  SDK
//
//  Created by 张淋 on 2019/5/25.
//  Copyright © 2019 黄建华. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface WeatherModel : NSObject

@property (nonatomic, assign) int        weatherType;        // 天气类型
@property (nonatomic, assign) int        temperatureUnit;   // 温度制式  0x00为摄氏度  0x01为华摄氏度
@property (nonatomic, assign) int        maxTem;            // 最高温度
@property (nonatomic, assign) int        minTem;            // 最低温度
@property (nonatomic, assign) int        currentTem;        // 当前天气
@property (nonatomic, assign) int        dateString;        // 时间（今0x00 明0x01 后0x02）

@end

NS_ASSUME_NONNULL_END

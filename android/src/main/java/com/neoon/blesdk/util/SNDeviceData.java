package com.neoon.blesdk.util;

import com.neoon.blesdk.decode.entity.device.DeviceInfoBean;
import com.neoon.blesdk.decode.entity.health.BloodOxygenValue;
import com.neoon.blesdk.decode.entity.health.BloodPressureValue;
import com.neoon.blesdk.decode.entity.health.HeartRateBean;
import com.neoon.blesdk.decode.entity.health.HeartRateValue;
import com.neoon.blesdk.decode.entity.sleep.SleepBean;
import com.neoon.blesdk.decode.entity.sport.SportBean;
import com.neoon.blesdk.decode.entity.sport.SportModeBean;
import com.neoon.blesdk.decode.entity.sport.SportValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/7/10).
 * 功能:数据解析
 */

public class SNDeviceData
{

	/**
	 * 获取运动历史数据
	 *
	 * @param args
	 * @return 按天的数据
	 */
	public static List<SportBean> getSportBean(Object... args)
	{
		return (ArrayList<SportBean>) args[0];
	}

	/**
	 * 获取运动模式数据
	 * @param args
	 * @return
	 */
	public static List<SportModeBean> getSportModeBean(Object... args)
	{
		return (ArrayList<SportModeBean>) args[0];
	}

	/**
	 * 获取睡眠历史数据
	 *
	 * @param args
	 * @return 按天的数据
	 */
	public static List<SleepBean> getSleepBean(Object... args)
	{
		return (ArrayList<SleepBean>) args[0];
	}

	/**
	 * 获取心率历史数据
	 *
	 * @param args
	 * @return 按天的数据
	 */
	public static List<HeartRateBean> getHeartRateBean(Object... args)
	{
		return (ArrayList<HeartRateBean>) args[0];
	}

	/**
	 * 获取设备信息
	 *
	 * @param args
	 * @return
	 */
	public static DeviceInfoBean getDeviceInfoBean(Object... args)
	{
		return (DeviceInfoBean) args[0];
	}

	/**
	 * 获取实时血压值
	 *
	 * @param args
	 * @return
	 */
	public static BloodPressureValue getBloodPressureValue(Object... args)
	{
		return new BloodPressureValue((int) args[0], (int) args[1]);
	}

	/**
	 * 获取实时血氧值
	 *
	 * @param args
	 * @return
	 */
	public static BloodOxygenValue getBloodOxygenValue(Object... args)
	{
		return new BloodOxygenValue((int) args[0]);
	}

	/**
	 * 获取实时心率值
	 *
	 * @param args
	 * @return
	 */
	public static HeartRateValue getHeartRateValue(Object... args)
	{
		return new HeartRateValue((int) args[0]);
	}

	/**
	 * 获取实时运动数据值
	 *
	 * @param args
	 * @return
	 */
	public static SportValue getSportValue(Object... args)
	{
		return new SportValue(((int) args[0]), ((int) args[1]), ((int) args[2]));
	}
}

package com.neoon.blesdk.decode.entity.sport;

/**
 * 作者:东芝(2019/8/9).
 * 功能:
 */
public class SportModeBean
{

	public static final int MODE_TYPE_WALKING        = 0x01;//健步 Walking
	public static final int MODE_TYPE_RUNNING        = 0x02;//跑步 Running
	public static final int MODE_TYPE_MOUNTAINEERING = 0x03;//登山 Mountaineering
	public static final int MODE_TYPE_CYCLING        = 0x04;//骑行 Riding
	public static final int MODE_TYPE_TABLE_TENNIS   = 0x05;//乒乓球 Table Tennis
	public static final int MODE_TYPE_BASKETBALL     = 0x06;//篮球 Basketball
	public static final int MODE_TYPE_FOOTBALL       = 0x07;//足球 Football
	public static final int MODE_TYPE_BADMINTON      = 0x08;//羽毛球 Badminton
	public static final int MODE_TYPE_TREADMILL      = 0x09;//跑步机 Treadmill
	public static final int MODE_TYPE_TENNIS         = 0x0A;//网球 Tennis
	public static final int MODE_TYPE_SWIMMING       = 0x0B;//游泳 Swimming

	/**
	 * 该天 yyyy-MM-dd 不带时分秒
	 */
	public String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * 运动模式类型
	 */
	private int modeType;

	/**
	 * 开始时间 "yyyy-MM-dd HH:mm:ss" 精确到秒
	 */
	private String beginDateTime;

	/**
	 * 结束时间 "yyyy-MM-dd HH:mm:ss" 精确到秒
	 */
	private String endDateTime;

	/**
	 * 花费的分钟数
	 */
	private int takeMinutes;

	/**
	 * 步数
	 */
	private int step;

	/**
	 * 距离
	 */
	private int distance;

	/**
	 * 卡路里
	 */
	private int calorie;


	/**
	 * 最大心率
	 */
	private int heartRateMax;

	/**
	 * 最小心率
	 */
	private int heartRateMin;

	/**
	 * 平均心率
	 */
	private int heartRateAvg;


	public int getTakeMinutes()
	{
		return takeMinutes;
	}

	public void setTakeMinutes(int takeMinutes)
	{
		this.takeMinutes = takeMinutes;
	}

	public int getModeType()
	{
		return modeType;
	}

	public void setModeType(int modeType)
	{
		this.modeType = modeType;
	}

	public String getBeginDateTime()
	{
		return beginDateTime;
	}

	public void setBeginDateTime(String beginDateTime)
	{
		this.beginDateTime = beginDateTime;
	}

	public String getEndDateTime()
	{
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime)
	{
		this.endDateTime = endDateTime;
	}

	public int getStep()
	{
		return step;
	}

	public void setStep(int step)
	{
		this.step = step;
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public int getCalorie()
	{
		return calorie;
	}

	public void setCalorie(int calorie)
	{
		this.calorie = calorie;
	}

	public int getHeartRateMax()
	{
		return heartRateMax;
	}

	public void setHeartRateMax(int heartRateMax)
	{
		this.heartRateMax = heartRateMax;
	}

	public int getHeartRateMin()
	{
		return heartRateMin;
	}

	public void setHeartRateMin(int heartRateMin)
	{
		this.heartRateMin = heartRateMin;
	}

	public int getHeartRateAvg()
	{
		return heartRateAvg;
	}

	public void setHeartRateAvg(int heartRateAvg)
	{
		this.heartRateAvg = heartRateAvg;
	}
}

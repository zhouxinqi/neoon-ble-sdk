package com.neoon.blesdk.decode.entity.sport;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2017/11/27).
 * 功能:运动大数据
 */
public class SportBean  {
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
     * 步数详细
     */
    private ArrayList<StepBean> steps;

    /**
     * 卡路里数据详细
     */
    private ArrayList<CalorieBean> calories;

    /**
     * 距离数据详细
     */
    private ArrayList<DistanceBean> distances;

    /**
     * 总步数
     */
    private int stepTotal;

    /**
     * 总距离
     */
    private int distanceTotal;

    /**
     * 总卡路里
     */
    private int calorieTotal;

    public int getStepTotal() {
        return stepTotal;
    }

    public void setStepTotal(int stepTotal) {
        this.stepTotal = stepTotal;
    }

    public int getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(int distanceTotal) {
        this.distanceTotal = distanceTotal;
    }

    public int getCalorieTotal() {
        return calorieTotal;
    }

    public void setCalorieTotal(int calorieTotal) {
        this.calorieTotal = calorieTotal;
    }

    public List<StepBean> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<StepBean> steps) {
        this.steps = steps;
    }

    public List<CalorieBean> getCalories() {
        return calories;
    }

    public void setCalories(ArrayList<CalorieBean> calories) {
        this.calories = calories;
    }

    public List<DistanceBean> getDistances() {
        return distances;
    }

    public void setDistances(ArrayList<DistanceBean> distances) {
        this.distances = distances;
    }

    public static class AbsSportBean implements Serializable {
        public AbsSportBean(int index, String dateTime, int value) {
            this.index = index;
            this.dateTime = dateTime;
            this.value = value;
        }

        /**
         * 时间索引
         */
        private int index;
        /**
         * 日期 带时分秒
         */
        private String dateTime;
        /**
         * 具体值
         */
        private int value;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class StepBean extends AbsSportBean {

        public StepBean(int index, String dateTime, int value) {
            super(index, dateTime, value);
        }
    }

    public static class DistanceBean extends AbsSportBean {

        public DistanceBean(int index, String dateTime, int value) {
            super(index, dateTime, value);
        }
    }

    public static class CalorieBean extends AbsSportBean {

        public CalorieBean(int index, String dateTime, int value) {
            super(index, dateTime, value);
        }
    }
}

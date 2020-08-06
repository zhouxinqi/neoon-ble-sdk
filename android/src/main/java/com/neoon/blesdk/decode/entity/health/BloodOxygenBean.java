package com.neoon.blesdk.decode.entity.health;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者:东芝(2017/12/7).
 * 功能:血氧数据
 */
public class BloodOxygenBean  {


    /**
     * 血氧详细数据
     */
    private ArrayList<BloodOxygenDetailsBean> bloodOxygenDetails;

    /**
     * 最大
     */
    private int max;

    /**
     * 最小
     */
    private int min;

    /**
     * 平均
     */
    private int avg;



    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public ArrayList<BloodOxygenDetailsBean> getBloodOxygenDetails() {
        return bloodOxygenDetails;
    }

    public void setBloodOxygenDetails(ArrayList<BloodOxygenDetailsBean> bloodOxygenDetails) {
        this.bloodOxygenDetails = bloodOxygenDetails;
    }

    public static class BloodOxygenDetailsBean implements Serializable {
        public BloodOxygenDetailsBean(int index, String dateTime, int value, int type) {
            this.index = index;
            this.dateTime = dateTime;
            this.value = value;
            this.type = type;
        }


        /**
         * 时间索引
         */
        private int index;
        /**
         * 日期
         */
        private String dateTime;
        /**
         * 具体值
         */
        private int value;


        /**
         * 是否自动检测 的数据,否则是手动 (默认0=手动检测,1=自动检测)
         */
        private int type;


        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

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
}

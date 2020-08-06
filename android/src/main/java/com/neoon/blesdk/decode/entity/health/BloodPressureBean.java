package com.neoon.blesdk.decode.entity.health;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者:东芝(2017/12/7).
 * 功能:血压数据
 */
public class BloodPressureBean {

    /**
     * 舒张压
     */
    private int bloodDiastolic;


    /**
     * 收缩压
     */
    private int bloodSystolic;


    /**
     * 血压详细数据
     */
    private ArrayList<BloodPressureDetailsBean> bloodPressureDetails;


    /**
     * 舒张压
     */
    public int getBloodDiastolic() {
        return bloodDiastolic;
    }

    /**
     * 舒张压
     *
     * @param bloodDiastolic
     */
    public void setBloodDiastolic(int bloodDiastolic) {
        this.bloodDiastolic = bloodDiastolic;
    }

    /**
     * 收缩压
     */
    public int getBloodSystolic() {
        return bloodSystolic;
    }

    /**
     * 收缩压
     */
    public void setBloodSystolic(int bloodSystolic) {
        this.bloodSystolic = bloodSystolic;
    }

    public ArrayList<BloodPressureDetailsBean> getBloodPressureDetails() {
        return bloodPressureDetails;
    }

    public void setBloodPressureDetails(ArrayList<BloodPressureDetailsBean> heartRateDetails) {
        this.bloodPressureDetails = heartRateDetails;
    }

    public static class BloodPressureDetailsBean implements Serializable {
        public BloodPressureDetailsBean(int index, String dateTime, int bloodDiastolic, int bloodSystolic, int type) {
            this.index = index;
            this.dateTime = dateTime;
            this.bloodDiastolic = bloodDiastolic;
            this.bloodSystolic = bloodSystolic;
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
         * 舒张压
         */
        private int bloodDiastolic;

        /**
         * 收缩压
         */
        private int bloodSystolic;
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

        public int getBloodDiastolic() {
            return bloodDiastolic;
        }

        public void setBloodDiastolic(int bloodDiastolic) {
            this.bloodDiastolic = bloodDiastolic;
        }

        public int getBloodSystolic() {
            return bloodSystolic;
        }

        public void setBloodSystolic(int bloodSystolic) {
            this.bloodSystolic = bloodSystolic;
        }
    }
}

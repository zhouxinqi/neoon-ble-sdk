package com.neoon.blesdk.decode.entity.sleep;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2017/11/27).
 * 功能:睡眠数据
 */
public class SleepBean   {
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
     * 深睡 总数
     */
    private int deepTotal;

    /**
     * 浅睡总数
     */
    private int lightTotal;

    /**
     * 清醒总数
     */
    private int soberTotal;

    /**
     * 睡眠详细数据
     */
    private ArrayList<SleepDetailsBean> sleepDetailsBeans;


    public int getDeepTotal() {
        return deepTotal;
    }

    public void setDeepTotal(int deepTotal) {
        this.deepTotal = deepTotal;
    }

    public int getLightTotal() {
        return lightTotal;
    }

    public void setLightTotal(int lightTotal) {
        this.lightTotal = lightTotal;
    }

    public int getSoberTotal() {
        return soberTotal;
    }

    public void setSoberTotal(int soberTotal) {
        this.soberTotal = soberTotal;
    }

    public ArrayList<SleepDetailsBean> getSleepDetailsBeans() {
        return sleepDetailsBeans;
    }

    public void setSleepDetailsBeans(ArrayList<SleepDetailsBean> sleepDetailsBeans) {
        this.sleepDetailsBeans = sleepDetailsBeans;
    }

    /**
     * 睡眠详情
     */
    public static class SleepDetailsBean implements Serializable {

        /**
         * 深睡
         */
        private int deep;

        /**
         * 浅睡
         */
        private int light;

        /**
         * 清醒
         */
        private int sober;
        /**
         * 开始时间 "yyyy-MM-dd HH:mm:ss" 精确到秒
         */
        private String beginDateTime;

        /**
         * 结束时间 "yyyy-MM-dd HH:mm:ss" 精确到秒
         */
        private String endDateTime;

        /**
         * 具体睡眠数据 集
         */
        private List<SleepData> sleepData;

        public String getBeginDateTime() {
            return beginDateTime;
        }

        public void setBeginDateTime(String beginDateTime) {
            this.beginDateTime = beginDateTime;
        }

        public String getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(String endDateTime) {
            this.endDateTime = endDateTime;
        }

        public List<SleepData> getSleepData() {
            return sleepData;
        }

        public void setSleepData(List<SleepData> sleepData) {
            this.sleepData = sleepData;
        }

        public int getDeep() {
            return deep;
        }

        public void setDeep(int deep) {
            this.deep = deep;
        }

        public int getLight() {
            return light;
        }

        public void setLight(int light) {
            this.light = light;
        }

        public int getSober() {
            return sober;
        }

        public void setSober(int sober) {
            this.sober = sober;
        }

        /**
         * 具体睡眠数据
         */
        public static class SleepData implements Serializable {
            /**
             * 浅睡
             */
            public final static int STATUS_LIGHT = 0;
            /**
             * 深睡
             */
            public final static int STATUS_DEEP = 1;
            /**
             * 醒着
             */
            public final static int STATUS_SOBER = 2;

            /**
             * 具体值
             */
            private int value;

            /**
             * 分钟
             */
            private int minutes;

            /**
             * 状态
             */
            private int status;



            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }

            public int getMinutes() {
                return minutes;
            }

            public void setMinutes(int minutes) {
                this.minutes = minutes;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }

    }
}

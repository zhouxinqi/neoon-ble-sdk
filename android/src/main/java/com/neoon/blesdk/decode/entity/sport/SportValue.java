package com.neoon.blesdk.decode.entity.sport;

/**
 * 作者:东芝(2018/7/10).
 * 功能:
 */

public class SportValue {

    private int step;
    private int distance;
    private int calorie;

    public SportValue(int step, int distance, int calorie) {
        this.step = step;
        this.distance = distance;
        this.calorie = calorie;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }
}

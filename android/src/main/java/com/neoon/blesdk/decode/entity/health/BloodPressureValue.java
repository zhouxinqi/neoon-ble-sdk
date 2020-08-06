package com.neoon.blesdk.decode.entity.health;

/**
 * 作者:东芝(2018/7/10).
 * 功能:
 */

public class BloodPressureValue {
    private int diastolic;
    private int systolic;

    public BloodPressureValue(int diastolic, int systolic) {
        this.diastolic = diastolic;
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public int getSystolic() {
        return systolic;
    }

}

//package com.neoon.blesdk.encapsulation.entity;
//
//import android.text.TextUtils;
//
//import java.io.Serializable;
//
///**
// * 作者:东芝(2018/1/8).
// * 功能:
// */
//
//public class DeviceInfo implements Serializable {
//    /**
//     * 心率
//     */
//    private boolean isSupportHeartRate = false;
//    /**
//     * 气压
//     */
//    private boolean isSupportAirPressure = false;
//    /**
//     * 血氧
//     */
//    private boolean isSupportBloodOxygen = false;
//    /**
//     * 血压
//     */
//    private boolean isSupportBloodPressure = false;
//
//    /**
//     * 消息推送
//     */
//    private boolean isSupportMessagePush = false;
//
//    /**
//     * 是否支持手环自己设置
//     */
//    private boolean isSupportBandSelfSetting = false;
//    /**
//     * 心电
//     */
//    private boolean isSupportECG = false;
//
//    public boolean isSupportHeartRate() {
//        return isSupportHeartRate;
//    }
//
//    public void setSupportHeartRate(boolean supportHeartRate) {
//        isSupportHeartRate = supportHeartRate;
//    }
//
//    public boolean isSupportAirPressure() {
//        return isSupportAirPressure;
//    }
//
//    public void setSupportAirPressure(boolean supportAirPressure) {
//        isSupportAirPressure = supportAirPressure;
//    }
//
//    public boolean isSupportBloodOxygen() {
//        return isSupportBloodOxygen;
//    }
//
//    public void setSupportBloodOxygen(boolean supportBloodOxygen) {
//        isSupportBloodOxygen = supportBloodOxygen;
//    }
//
//    public boolean isSupportBloodPressure() {
//        return isSupportBloodPressure;
//    }
//
//    public void setSupportBloodPressure(boolean supportBloodPressure) {
//        isSupportBloodPressure = supportBloodPressure;
//    }
//
//    public boolean isSupportMessagePush() {
//        return isSupportMessagePush;
//    }
//
//    public void setSupportMessagePush(boolean supportMessagePush) {
//        isSupportMessagePush = supportMessagePush;
//    }
//
//    public boolean isSupportECG() {
//        return isSupportECG;
//    }
//
//    public void setSupportECG(boolean supportECG) {
//        isSupportECG = supportECG;
//    }
//
//
//    public boolean isSupportBandSelfSetting() {
//        switch (getAdv_id()) {
//            case 3089://IT110
//                return true;
//        }
//        return isSupportBandSelfSetting;
//    }
//
//    public boolean isDialog() {
//        return getChip().toLowerCase().startsWith("da") || getChip().startsWith("dialog");
//    }
//
//    public boolean isnRF() {
//        return getChip().toLowerCase().startsWith("nrf") || getChip().toLowerCase().contains("nordic");
//    }
//
//    public boolean isSYD8801() {
//        return getChip().toLowerCase().startsWith("syd") || getChip().toLowerCase().contains("syd8801");
//    }
//
//    public boolean isTi() {
//        return getChip().toLowerCase().startsWith("ti");
//    }
///*
//    public boolean isDFU() {
//        String name = getDevice_name();
//        return isDFU || name.toLowerCase().contains("dfu") || name.toLowerCase().contains("ota");
//    }
//
//    public void setDFU(boolean isDFU) {
//        this.isDFU = isDFU;
//    }
//
//    private boolean isDFU = false;*/
//    private int id;
//    private String type;
//    private int version;
//    private String download_url;
//    private String update_time;
//    private String describe;
//    private int head;
//    private int customid;
//    private int adv_id;
//    private String device_name;
//    private int apptype;
//    private int function;
//    private String chip;
//
//
//    public void setAdv_id(int adv_id) {
//        this.adv_id = adv_id;
//    }
//
//    public int getAdv_id() {
//        return adv_id;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public int getVersion() {
//        return version;
//    }
//
//    public void setVersion(int version) {
//        this.version = version;
//    }
//
//    public String getDownload_url() {
//        return download_url;
//    }
//
//    public void setDownload_url(String download_url) {
//        this.download_url = download_url;
//    }
//
//    public String getUpdate_time() {
//        return update_time;
//    }
//
//    public void setUpdate_time(String update_time) {
//        this.update_time = update_time;
//    }
//
//    public String getDescribe() {
//        return describe;
//    }
//
//    public void setDescribe(String describe) {
//        this.describe = describe;
//    }
//
//    public int getHead() {
//        return head;
//    }
//
//    public void setCommandHead(int head) {
//        this.head = head;
//    }
//
//    public int getCustomid() {
//        return customid;
//    }
//
//    public void setCustomid(int customid) {
//        this.customid = customid;
//    }
//
//    public String getDevice_name() {
//        return device_name;
//    }
//
//    public void setDevice_name(String device_name) {
//        this.device_name = device_name;
//    }
//
//    public int getApptype() {
//        return apptype;
//    }
//
//    public void setApptype(int apptype) {
//        this.apptype = apptype;
//    }
//
//    public int getFunction() {
//        return function;
//    }
//
//    public void setFunction(int function) {
//        this.function = function;
//    }
//
//    public String getChip() {
//        return TextUtils.isEmpty(chip) ? "" : chip;
//    }
//
//    public void setChip(String chip) {
//        this.chip = chip;
//    }
//
//}
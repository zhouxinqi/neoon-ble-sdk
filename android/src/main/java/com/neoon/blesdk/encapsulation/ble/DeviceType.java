//package com.neoon.blesdk.encapsulation.ble;
//
//import android.text.TextUtils;
//
//import com.neoon.blesdk.encapsulation.entity.DeviceInfo;
//import com.neoon.blesdk.encapsulation.storage.DeviceStorage;
//import com.neoon.blesdk.util.JsonUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 作者:东芝(2017/8/8).
// * 描述:
// */
//public class DeviceType {
//
//    private static List<DeviceInfo> list = null;
//
////    public static void addDeviceInfo(String deviceName,int advId){
////        getDeviceInfo();
////        if (list == null) {
////            list = new ArrayList<>();
////        }
////        DeviceInfo deviceInfo = new DeviceInfo();
////        deviceInfo.setDevice_name(deviceName);
////        deviceInfo.setAdv_id(advId);
////        list.add(deviceInfo);
////    }
//
//    private static List<DeviceInfo> getDeviceInfo() {
//        if (list != null && !list.isEmpty()) {//只遍历一次 除非请求了asyncReLoadDeviceInfo
//            return list;
//        }
//        String deviceMessagesJson = DeviceStorage.getDeviceInfoJson();
//        if (!TextUtils.isEmpty(deviceMessagesJson)) {
//            list = JsonUtil.toListBean(deviceMessagesJson, DeviceInfo.class);
//        }
//        if (list == null) {
//            list = new ArrayList<>();
//        }
//        if (!list.isEmpty()) {
//            ArrayList<DeviceInfo> deviceInfos = new ArrayList<>(list);
//            list.clear();
//            for (int i = 0; i < deviceInfos.size(); i++) {
//                DeviceInfo deviceInfo = deviceInfos.get(i);
//                int apptype = deviceInfo.getApptype();
//                //第1位是属于WellGO 位 值
//                boolean isWellGO = ((apptype >> 1) & 0x01) == 1;
//                boolean isGetFit = ((apptype >> 0) & 0x01) == 1;
//                if (isWellGO||isGetFit) {
//                    //把Customid(manufacturer) 解析得到 设备支持的功能
//                    setDeviceSupportInfo(deviceInfo, (short) deviceInfo.getAdv_id());
//                    list.add(deviceInfo);
//                }
//            }
//        }
//        return list;
//    }
//
//    private static void setDeviceSupportInfo(DeviceInfo deviceInfo, int manufacturer) {
//        deviceInfo.setSupportHeartRate(isSupported(manufacturer, 0));
//        deviceInfo.setSupportAirPressure(isSupported(manufacturer, 1));
//        deviceInfo.setSupportBloodOxygen(isSupported(manufacturer, 2));
//        deviceInfo.setSupportBloodPressure(isSupported(manufacturer, 3));
//        deviceInfo.setSupportMessagePush(isSupported(manufacturer, 4));
//        deviceInfo.setSupportECG(isSupported(manufacturer, 5));
//    }
//
//
//    //1010000011101
//    public static DeviceInfo getCurrentDeviceInfo() {
//        int manufacturer = getDeviceAdvId();
//        String name = getDeviceName();
//        DeviceInfo deviceInfo = getDeviceInfo(manufacturer);
//        //得不到厂商ID 就取名称
//        if (deviceInfo == null) {
//            deviceInfo = getDeviceInfo(name);
//        }
//        if (deviceInfo != null) {
//            setDeviceSupportInfo(deviceInfo, manufacturer);
//
//
//            //旧Sone
//            if (isSOne(deviceInfo.getDevice_name()) && !deviceInfo.isSupportHeartRate()) {
//                deviceInfo.setSupportHeartRate(true);
//                deviceInfo.setSupportBloodOxygen(false);
//                deviceInfo.setSupportBloodPressure(true);
//            }
//            //旧X9
//            if (isX9(deviceInfo.getDevice_name()) && !deviceInfo.isSupportHeartRate()) {
//                deviceInfo.setSupportHeartRate(true);
//                deviceInfo.setSupportBloodOxygen(true);
//                deviceInfo.setSupportBloodPressure(false);
//            }
//
//        }
//        return deviceInfo;
//    }
//
//    public static String getDeviceName() {
//        return DeviceStorage.getDeviceName();
//    }
//
//    public static String getDeviceMac() {
//        return DeviceStorage.getDeviceMac();
//    }
//
//    /**
//     * 广播ID
//     *
//     * @return
//     */
//    public static int getDeviceAdvId() {
//        return DeviceStorage.getDeviceAdvId();
//    }
//
//    /**
//     * 客户ID
//     */
//    public static int getDeviceCustomerId() {
//        return DeviceStorage.getDeviceCustomerId();
//    }
//
//    /**
//     * 是我们的设备?
//     *
//     * @param deviceName 设备名
//     * @return
//     */
//    public static boolean isOurDevice(String deviceName) {
//        if (!TextUtils.isEmpty(deviceName)) {
//            List<DeviceInfo> list = getDeviceInfo();
//            for (DeviceInfo deviceMessage : list) {
//                if (deviceMessage.getDevice_name().equalsIgnoreCase(deviceName)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
////
////    /**
////     * 是我们的设备?
////     *
////     * @param manufacturer 厂商(客户)ID
////     * @return
////     */
////    public static boolean isOurDevice(short manufacturer) {
////        return getDeviceInfo(manufacturer) != null;
////    }
//
//    /**
//     * 根据客户id,取得设备信息
//     *
//     * @param manufacturers 厂商(客户)ID 多个
//     * @return
//     */
//    public static DeviceInfo getDeviceInfo(List<Integer> manufacturers) {
//            for (Integer manufacturer : manufacturers) {
//                DeviceInfo deviceInfo = getDeviceInfo(manufacturer);
//                if (deviceInfo !=null) {
//                    return deviceInfo;
//                }
//            }
//        return null;
//    }
//
//
//    /**
//     * 根据客户id,取得设备信息
//     *
//     * @param manufacturer 厂商(客户)ID
//     * @return
//     */
//    public static DeviceInfo getDeviceInfo(int manufacturer) {
//        if (manufacturer != 0) {
//            List<DeviceInfo> list = getDeviceInfo();
//            for (DeviceInfo deviceInfo : list) {
//                if (deviceInfo.getAdv_id() == manufacturer) {
//                    return deviceInfo;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 根据设备名,取得设备信息
//     * 该方法使用请慎重! 不是很准确,通常先判断 厂商(客户)ID 得不到时 再通过该方法做兼容式 获取
//     *
//     * @param deviceName 设备名
//     * @return
//     */
//    public static DeviceInfo getDeviceInfo(String deviceName) {
//        if (!TextUtils.isEmpty(deviceName)) {
//            List<DeviceInfo> list = getDeviceInfo();
//            for (DeviceInfo deviceInfo : list) {
//                if (deviceInfo.getDevice_name().equalsIgnoreCase(deviceName)) {
//                    return deviceInfo;
//                }
//            }
//        }
//        return null;
//    }
//
//
//    private static boolean isSupported(int function, int bit) {
//        return ((function >> bit) & 1) == 1;
//    }
//
//
//    private static boolean isSOne(String name) {
//        if (name == null) {
//            return false;
//        }
//        return name.toLowerCase().contains("ione") ||
//                name.toLowerCase().contains("d one") ||
//                name.toLowerCase().contains("i_one") ||
//                name.toLowerCase().contains("sone") ||
//                name.toLowerCase().contains("s one") ||
//                name.toLowerCase().contains("s_one");
//    }
//
//    private static boolean isX10Pro(String name) {
//        if (name == null) {
//            return false;
//        }
//        return name.toLowerCase().contains("x10pro") ||
//                name.toLowerCase().contains("x10 pro") ||
//                name.toLowerCase().contains("h10pro") ||
//                name.toLowerCase().contains("h10 pro")
//                ;
//    }
//
//    private static boolean isX9(String name) {
//        if (name == null) {
//            return false;
//        }
//        return name.toLowerCase().contains("x9");
//    }
//
//    private static boolean isX1Pro(String name) {
//        if (name == null) {
//            return false;
//        }
//        return name.toLowerCase().contains("x1pro") ||
//                name.toLowerCase().contains("x1 pro");
//    }
//
//
//    public static boolean isDFUModel(String name) {
//        if (name == null||name.length()==0) {
//            return false;
//        }
//        return name.toLowerCase().contains("dfu")|| name.toLowerCase().contains("ota");
//    }
//
//    private static boolean isI6(String name) {
//        if (name == null) {
//            return false;
//        }
//        return name.toLowerCase().contains("i6") || name.toLowerCase().contains("bcdhp");
//    }
//
//
//}

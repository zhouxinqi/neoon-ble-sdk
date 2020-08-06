package com.neoon.blesdk.encapsulation.cmd;

/**
 * 作者:东芝(2017/11/21).
 * 功能:命令工具
 */

public class SNCMD {
    public static byte HEAD = 0x05;
    public static String HEAD_STR = "05";

    private static ICmd mICmd=null;

    public static ICmd getInstance() {

        if (mICmd == null) {
            mICmd = new XWCmd();
        }
        return mICmd;
    }

    /**
     * 设置命令头
     *
     * @param head8Bit
     */
    public static void setCommandHead(int head8Bit) {
        SNCMD.HEAD = (byte) head8Bit;
        SNCMD.HEAD_STR = String.format("%02X",head8Bit);

    }
}

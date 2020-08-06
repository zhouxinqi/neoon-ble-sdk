package com.neoon.blesdk.interfaces;

/**
 * 作者:东芝(2018/7/6).
 * 功能:
 */

public abstract class OnDeviceCommStatusListener extends OnDeviceCommBaseListener{
    public abstract void onResponse(boolean isSuccessful);

}

package com.neoon.blesdk.core.interfaces;

import com.neoon.blesdk.core.entity.BLEWriteOrRead;

/**
 * 作者:东芝(2017/11/25).
 * 功能:线程同步回调
 */

public interface LinkedCmdBlockingQueueCallBack {
    boolean onTake(BLEWriteOrRead obj);
}

package com.neoon.blesdk.core.utils;

import com.neoon.blesdk.core.ble.BleHelper;
import com.neoon.blesdk.core.entity.BLEWriteOrRead;
import com.neoon.blesdk.core.interfaces.LinkedCmdBlockingQueueCallBack;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 作者:东芝(2017/11/25).
 * 功能:多线程命令发送同步工具
 * 为了解决快速发送命令导致失败的问题
 */

public class LinkedCmdBlockingDequeHelper {


    public static   int MILLIS = 2;
    private final LinkedBlockingDeque<BLEWriteOrRead> blockingDeque = new LinkedBlockingDeque<>();
    private Thread thread;
    private LinkedCmdBlockingQueueCallBack callBack;

    public LinkedCmdBlockingDequeHelper(final LinkedCmdBlockingQueueCallBack callBack) {
        this.callBack = callBack;
        thread = createThread();
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private Thread createThread() {
        return new Thread() {
            public void run() {
                while (true) {
                    try {
                        if (callBack == null) {
                            Thread.sleep(1000);
                            continue;
                        }
                        Thread.sleep(MILLIS);
                        BLEWriteOrRead take = blockingDeque.take();
                        if (take.reTryCount < 100000) {
                            if (!callBack.onTake(take)) {
                                Thread.sleep(50);
                                take.reTryCount++;
                                blockingDeque.putFirst(take);
                                BLELog.d("send status = retry");
                                if (BleHelper.getInstance().isDisconnected()) {
                                    blockingDeque.clear();
                                }
                            } else {
                                take.reTryCount = 0;
                            }
                        } else {
                                BLELog.w("send status =" + false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void commit(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID, boolean isWrite) {
        if (thread == null ) {
            thread = createThread();
            thread.start();
        }
        final BLEWriteOrRead obj = new BLEWriteOrRead(data, mGattServiceUUID, mGattCharacteristicUUID, isWrite);
        blockingDeque.offer(obj);
    }

}

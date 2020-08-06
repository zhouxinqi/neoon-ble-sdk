package com.neoon.blesdk.core.interfaces;

import java.util.UUID;



public interface NotifyReceiverRawListener extends CommunicationListener {
    void onReceive(UUID uuid, byte[] buffer);
}

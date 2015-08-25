package com.zing.zalo.zalosdk.connector.connect;

import com.zing.zalo.zalosdk.connector.data.ZaloPacket;

public abstract class ZaloServiceConnectionListener {
	public void onReceiveMessage(ZaloPacket packet) {}
	public void onConnected() {}
	public void onDisconnected() {}
	public void onError(Exception ex) {}
}

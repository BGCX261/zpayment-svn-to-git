package com.zing.zalo.zalosdk.connector.connect;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.zing.zalo.zalosdk.connector.data.ZaloPacket;

public class ZaloServiceConnectionSync extends ZaloServiceConnection {
    
    private Queue<ZaloPacket> mMessageQueue = new LinkedBlockingQueue<ZaloPacket>();
    
    @Override
    protected void haveMessage(ZaloPacket packet) {
    	super.haveMessage(packet);
    	mMessageQueue.offer(packet);
    	mMessageQueue.notifyAll();
    }
    
	public synchronized ZaloPacket receiveMessageNonBlocking() {
		synchronized (mMessageQueue) {
			return mMessageQueue.peek();	
		}		
	}
	
	public synchronized ZaloPacket receiveMessageBlocking() throws IOException {
		try {
			synchronized (mMessageQueue) {
				while (mMessageQueue.size()==0) {
					mMessageQueue.wait();
				}			
				return mMessageQueue.peek();	
			}	
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException();
		}
				
	}
}

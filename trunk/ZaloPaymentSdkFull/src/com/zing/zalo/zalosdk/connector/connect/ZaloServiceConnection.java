package com.zing.zalo.zalosdk.connector.connect;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.zing.zalo.zalosdk.connector.data.ZaloPacket;
import com.zing.zalo.zalosdk.connector.exception.ZaloConnectorException;
import com.zing.zalo.zalosdk.connector.util.Log;

@SuppressLint("HandlerLeak")
public class ZaloServiceConnection {
	
    /** Messenger for communicating with the service. */
    private Messenger mService = null;
    
    /** Flag indicating whether we have called bind on the service. */
    
    private int mState = 0;
    private Object mStateLock = new Object();
    private HashSet<ZaloServiceConnectionListener> mHashListener = new HashSet<ZaloServiceConnectionListener>();
    
    private static final int STATE_NOCONNECT = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_DISCONNECTING = 3;
    
    private static final String ZALO_PACKAGE = "com.zing.zalo";
    private static final String ZALO_SERVICE_CLASS = "com.zing.zalo.service.ZaloPluginService";
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection ;
    
	/**
     * Handler of incoming messages from Zalo service.
     */
    private Handler mZaloServiceIncomingHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) 
        {
        	haveMessage(ZaloPacket.fromMessage(msg));
        }
    };
    
    private Messenger mMyMessenger = new Messenger(mZaloServiceIncomingHandler);
    
    public void addReceiveListener(ZaloServiceConnectionListener listener) {
    	mHashListener.add(listener);
    }
    
    public void removeReceiveListener(ZaloServiceConnectionListener listener) {
    	mHashListener.remove(listener);
    }
    
    public void clearReceiveListener() {
    	mHashListener.clear();
    }
    
	public synchronized void connect(Context context) throws SecurityException {
		synchronized (mStateLock) {
			if (mState!=STATE_NOCONNECT) return;
			mState = STATE_CONNECTING;
		}		
		Intent intent = new Intent();
	    ComponentName cn = new ComponentName(ZALO_PACKAGE, ZALO_SERVICE_CLASS);
	    intent.setComponent(cn);

	    mConnection = new ServiceConnection() 
	    {
	        public void onServiceConnected(ComponentName className, IBinder service) {
	            // This is called when the connection with the service has been
	            // established, giving us the object we can use to
	            // interact with the service.  We are communicating with the
	            // service using a Messenger, so here we get a client-side
	            // representation of that from the raw IBinder object.

	            mService = new Messenger(service);
	            
	            synchronized (mStateLock) {
	            	mState = STATE_CONNECTED;
	            }
	            
	    		for (ZaloServiceConnectionListener listener : mHashListener) {
	    			try {
	    				if (listener!=null) listener.onConnected();	
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}	    			
	    		}
	        }

	        public void onServiceDisconnected(ComponentName className) {
	            // This is called when the connection with the service has been
	            // unexpectedly disconnected -- that is, its process crashed.
	            mService = null;
	            synchronized (mStateLock) {
	            	mState = STATE_NOCONNECT;
	            }
	            
	    		for (ZaloServiceConnectionListener listener : mHashListener) {
	    			try {
		    			if (listener!=null) listener.onDisconnected();	
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
	        }
	    };
	    
	    context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public boolean isConnected() {
		synchronized (mStateLock) {
        	return mState==STATE_CONNECTED;
        }
	}
	
	public boolean isConnecting() {
		synchronized (mStateLock) {
        	return mState==STATE_CONNECTING;
        }
	}
	
	public synchronized void disconnect(Context context) {
		if (context==null) return;
		synchronized (mStateLock) {
			if (mState==STATE_NOCONNECT) return;
			mState = STATE_DISCONNECTING;
        }
		try {
			context.unbindService(mConnection);
		} catch (Exception e) {

		} finally {
			synchronized (mStateLock) {
				mState = STATE_NOCONNECT;
			}
		}
	}
	
	protected void haveMessage(ZaloPacket packet) {
		for (ZaloServiceConnectionListener listener : mHashListener) {
			try {
				if (listener!=null) listener.onReceiveMessage(packet);	
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	public synchronized void sendMessage(ZaloPacket packet){
		Log.d("sendMessage", packet.toString());
		synchronized (mStateLock) {
			if (mState!=STATE_CONNECTED) {
				Exception ex = new ZaloConnectorException(ZaloConnectorException.ERROR_SERVICE_NOT_CONNECTED);
				for (ZaloServiceConnectionListener listener : mHashListener) {
					if (listener!=null) listener.onError(ex);
				}
			}
        }
		
		Message msg = packet.toMessage();
		msg.replyTo = mMyMessenger;
		
		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
			for (ZaloServiceConnectionListener listener : mHashListener) {
				if (listener!=null) listener.onError(e);
			}
		}
	}
}

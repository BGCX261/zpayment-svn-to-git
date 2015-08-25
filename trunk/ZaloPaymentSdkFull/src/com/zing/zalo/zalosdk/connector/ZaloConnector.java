package com.zing.zalo.zalosdk.connector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.SparseArray;

import com.zing.zalo.zalosdk.connector.connect.ZaloServiceConnection;
import com.zing.zalo.zalosdk.connector.connect.ZaloServiceConnectionListener;
import com.zing.zalo.zalosdk.connector.data.AppContants;
import com.zing.zalo.zalosdk.connector.data.ZaloPacket;
import com.zing.zalo.zalosdk.connector.exception.ZaloConnectorException;
import com.zing.zalo.zalosdk.connector.util.ByteArrayUtils;
import com.zing.zalo.zalosdk.connector.util.Log;

public class ZaloConnector {
	private static int TIME_2_CHECK_REQUEST_TIMEOUT = 5000;
	private String mAppId;
	private String mAppApiKey;
	private ZaloServiceConnection serviceConnection = new ZaloServiceConnection();
	//private boolean mAuthenticated = false;
	private ConnectingState mState = ConnectingState.INIT;
	/*private boolean mAutoReconnect = true;*/
	private Context mContext;
	private HashSet<ZaloConnectorListener> mHashListener = new HashSet<ZaloConnectorListener>();
    private volatile SparseArray<Long> mHashRequestIdTime = new SparseArray<Long>();
    private Random random = new Random();
    private long mRequestTimeOut = 10 * 1000;
    private ThreadCheckTimeout threadCheckTimeout = new ThreadCheckTimeout();
//	private Handler mHandlerUI;
	private volatile HashSet<Integer> mHashPermissions = new LinkedHashSet<Integer>();
    

    /**
     * Khởi tạo với appId và apiKey
     * @param context
     * @param appId
     */
	public ZaloConnector(Context context, String appId) {
		if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
			throw new RuntimeException();
		}
//		mHandlerUI = new Handler();
		mAppId = appId;
		mAppApiKey = "abc";
		mContext = context;
		registerSelfListener();		
		threadCheckTimeout.start();
	}
	
	@Override
	protected void finalize() throws Throwable {
		threadCheckTimeout.interrupt();
		super.finalize();
	}
	
	/**
	 * Đăng ký nhận callback khi có sự kiện
	 * @param listener
	 */
    public void addReceiveListener(ZaloConnectorListener listener) {
    	mHashListener.add(listener);
    }
    
    /**
     * Hủy đăng ký nhận callback khi có sự kiện
     * @param listener
     */
    public void removeReceiveListener(ZaloConnectorListener listener) {
    	mHashListener.remove(listener);
    }
    
    /**
     * xóa toàn bộ các đăng ký nhận callback khi có sự kiện
     */
    public void clearReceiveListener() {
    	mHashListener.clear();
    }
    
    /**
     * yêu cầu kết nối đến ứng dụng Zalo
     * @throws ZaloConnectorException
     */
    public void connect(Activity activity) throws  ZaloConnectorException {
    	if (!serviceConnection.isConnected() || !serviceConnection.isConnecting()) {
			try {
				mHashPermissions.clear();
				mContext = activity;
				serviceConnection.connect(mContext.getApplicationContext());
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new ZaloConnectorException(ZaloConnectorException.ERROR_SERVICE_PERMISSION_DENIED);
			}
		}
    }
    
    @Deprecated
    public void reconnect() throws ZaloConnectorException {
    	/*disconnect();
    	if (!mAutoReconnect) {
    		connect();
    	}*/ 
    }
    
    /**
     * Ngắt kết nối đến Zalo
     */
    public void disconnect() {
    	if (mState!=ConnectingState.INIT && mState!=ConnectingState.DISCONNECTED) {
    		ZaloPacket packet = makeRequest();
    		packet.setCmd(AppContants.PluginCommands.COMMAND_DISCONNECT_APP);
    		sendMessageRequestNoRET(packet);
    	}
    	mHashPermissions.clear();
    	mState = ConnectingState.DISCONNECTED;
    	serviceConnection.disconnect(mContext.getApplicationContext());
    }
    
    public void disconnect(Context context) {
    	mContext = context;
    	disconnect();
    }
    
    /**
     * Đã chứng thực và sẵn sàng gửi nhận tin
     * @return
     */
    public boolean isReady() {
    	return mState==ConnectingState.CONFIRMED && serviceConnection.isConnected();
    }
    
    public boolean isConnected() {
    	return serviceConnection.isConnected();
    }
    
    protected boolean isAuthenticated() {
    	return mState==ConnectingState.AUTHENTICATED && serviceConnection.isConnected();
    }
    
    @Deprecated
    public boolean isAutoReconnect() {
    	/*return mAutoReconnect;*/
    	return false;
    }
    
	public long getRequestTimeOut() {
		return mRequestTimeOut;
	}

	/**
	 * gán thời gian mà yêu cầu gửi qua Zalo sẽ bị timeout nếu ko có kết quả trả về (ms)
	 * @param mRequestTimeOut
	 */
	public void setRequestTimeOut(long mRequestTimeOut) {
		this.mRequestTimeOut = mRequestTimeOut;
	}
    
	/**
	 * Có tự động kết nối lại khi bị ngắt kết nối hay không
	 * @param value
	 */
	@Deprecated
    public void setAutoReconnect(boolean value) {
    	/*mAutoReconnect = value;*/
    }
    
    private void registerSelfListener() {
    	serviceConnection.addReceiveListener(new ZaloServiceConnectionListener() {
    		@Override
    		public void onConnected() {
    			mState = ConnectingState.CONNECTED;
    			sendAuthMessage();
    			mState = ConnectingState.AUTHENTICATING;
    		}
    		
    		@Override
    		public void onDisconnected() {
    			mState = ConnectingState.DISCONNECTED;
	    		for (ZaloConnectorListener listener : mHashListener) {
	    			if (listener!=null) listener.onDisconnected();
	    		}
    			/*if (isAutoReconnect()) {
    				try {
						connect();
					} catch (ZaloConnectorException e) {
						e.printStackTrace();
					}
    			}*/
    		}
    		
    		@Override
    		public void onError(Exception ex) {
    			mState = ConnectingState.DISCONNECTED;
	    		for (ZaloConnectorListener listener : mHashListener) {
	    			if (listener!=null) listener.onError(ex);
	    		}
    			if (isConnected() || serviceConnection.isConnecting()) {
    				disconnect();
    			} /*else if (isAutoReconnect()) {
    				try {
						connect();
					} catch (ZaloConnectorException e) {
						e.printStackTrace();
					}
    			}*/
    		}
    		
    		@Override
    		public void onReceiveMessage(ZaloPacket packet) {
    			try {
        			Log.d("onReceiveMessage", packet.toString());
    				if (packet.getMsgType()==AppContants.PluginMessageType.MESSAGE_TYPE_RET && mHashRequestIdTime.get(packet.getRequestId())==null) {
    					return;
    				}
        			if (packet.getRequestId()!=0 && packet.getMsgType()==AppContants.PluginMessageType.MESSAGE_TYPE_RET) {
        				mHashRequestIdTime.remove(packet.getRequestId());
        			}
    				if (packet.getRetCode()!=0 && packet.getCmd()!=AppContants.PluginCommands.COMMAND_AUTHEN) {
//    					if (packet.getCmd()==AppContants.PluginCommands.COMMAND_ZALO_INFO) {
//    						for (ZaloConnectorListener listener : mHashListener) {
//    			    			if (listener!=null) listener.onReceiveErrorZaloInfo(packet.getRequestId(), packet.getRetCode(), ByteArrayUtils.readShort(packet.getParams(), 0));
//    			    		}
//    					} else {
//    						for (ZaloConnectorListener listener : mHashListener) {
//    			    			if (listener!=null) listener.onReceiveError(packet.getRequestId(), packet.getRetCode(), packet.getCmd());
//    			    		}	
//    					}
    					for (ZaloConnectorListener listener : mHashListener) {
			    			if (listener!=null) listener.onReceiveError(packet.getRequestId(), packet.getRetCode(), packet.getCmd());
			    		}
    		    		return;
    				}

        			switch (packet.getCmd()) {
    				case AppContants.PluginCommands.COMMAND_AUTHEN:	{
    					//skip authen return packet (response by comm message)
    					if (packet.getRetCode() == 0 && packet.getMsgType()==AppContants.PluginMessageType.MESSAGE_TYPE_COMM) {
    						byte[] data = packet.getParams();
    						int curr = 0;
    						int nameLen = ByteArrayUtils.readInt(data, curr);
    						curr += 4;
    						String name = new String(data, curr, nameLen);
    						curr += nameLen;
    						byte requireConfirm = data[curr];
    						curr++;
    						int appDesLen = ByteArrayUtils.readInt(data, curr);
    						curr += 4;
    						String appDescription = new String(data, curr, appDesLen);
    						curr += appDesLen;
    						int permissionsLen = ByteArrayUtils.readInt(data, curr);
    						curr += 4;
    						String permissionsString = new String(data, curr, permissionsLen);
    						//parsePermission(permissionsString);
    						if (requireConfirm!=0) {
    							mState = ConnectingState.AUTHENTICATED;
    							//showPrivacyWarningDialog(name, appDescription);	
    							sendAgreeMessage();
    						} else {
								mState = ConnectingState.CONFIRMED;
					    		for (ZaloConnectorListener listener : mHashListener) {
					    			if (listener!=null) listener.onReady(mHashPermissions);
	    				    	}
    						}
    					} else if (packet.getRetCode() != 0) {
    			    		for (ZaloConnectorListener listener : mHashListener) {
    			    			if (listener!=null) listener.onAuthenError(packet.getRequestId(), packet.getRetCode());
    			    		}
    						disconnect();
    					}					
    					break;
    				}
    				
    				case AppContants.PluginCommands.COMMAND_POLICY_AGREE: {
						mState = ConnectingState.CONFIRMED;
			    		for (ZaloConnectorListener listener : mHashListener) {
			    			if (listener!=null) listener.onReady(mHashPermissions);
			    		}
    					break;
    				}

//    				case AppContants.PluginCommands.COMMAND_ZALO_INFO: {
//    					byte[] data = packet.getParams();
//    					ResponseZaloInfo response = new ResponseZaloInfo();
//    					response.setSubCommand(ByteArrayUtils.readShort(data, 0));
//    					byte[] subData = new byte[data.length-2];
//    					System.arraycopy(data, 2, subData, 0, subData.length);
//    					response.setData(subData);
//    		    		for (ZaloConnectorListener listener : mHashListener) {
//    		    			if (listener!=null) listener.onReceiveResponseZaloInfo(response);
//    		    		}
//    					break;
//    				}
    				default:
    		    		for (ZaloConnectorListener listener : mHashListener) {
    		    			if (listener!=null) listener.onReceiveResponse(packet.getRequestId(), packet.getCmd());
    		    		}					
    					break;
    				}
				} catch (Exception e) {
		    		for (ZaloConnectorListener listener : mHashListener) {
		    			if (listener!=null) listener.onError(e);
		    		}
				}
    		}
		});
    }
    
    /*private void parsePermission(String permissionsString) throws JSONException {
    	JSONArray perArr = new JSONArray(permissionsString);
    	List<Integer> listPer = new ArrayList<Integer>();
    	for (int i = 0; i < perArr.length(); i++) {
			JSONObject obj = (JSONObject) perArr.get(i);
			String per = obj.getString("name");
			if (AppContants.ZaloPermissions.PERMISSION_GET_PROFILE_KEY.equals(per)) {
				listPer.add(AppContants.ZaloPermissions.PERMISSION_GET_PROFILE);
			} else if (AppContants.ZaloPermissions.PERMISSION_GET_FRIENDS_KEY.equals(per)) {
				listPer.add(AppContants.ZaloPermissions.PERMISSION_GET_FRIENDS);
			} else if (AppContants.ZaloPermissions.PERMISSION_POST_FEED_KEY.equals(per)) {
				listPer.add(AppContants.ZaloPermissions.PERMISSION_POST_FEED);
			} else if (AppContants.ZaloPermissions.PERMISSION_SEND_CHAT_KEY.equals(per)) {
				listPer.add(AppContants.ZaloPermissions.PERMISSION_SEND_CHAT);
			}
		}
    	mHashPermissions.clear();
    	for (int i = 0; i < listPer.size(); i++) {
    		mHashPermissions.add(listPer.get(i));
		}
    }
    */
    private ZaloPacket makeRequest() {
    	ZaloPacket packet = new ZaloPacket();
    	packet.setAppId(mAppId);
    	packet.setMsgType(AppContants.PluginMessageType.MESSAGE_TYPE_REQ);
    	return packet;
    }
    
    /*private void showPrivacyWarningDialog(final String zaloUsername, final String appDescription) {
    	mHandlerUI.post(new Runnable() {
			@Override
			public void run() {
				ZaloPrivacyWarningDialog.Builder builder = new Builder(mContext);
				List<String> listPer = new ArrayList<String>();
				Iterator<Integer> iterator = mHashPermissions.iterator();
				while (iterator.hasNext()) {
					Integer integer = (Integer) iterator.next();
					listPer.add(mContext.getString(integer));
				}
				builder.setPermissionList(listPer);
				builder.setMessage(appDescription);
				builder.setZaloUser(zaloUsername);
				builder.setAgreeButton(new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						sendAgreeMessage();					
					}
				});
				builder.setCancelButton(new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						sendDisagreeMessage();
    		    		for (ZaloConnectorListener listener : mHashListener) {
    		    			if (listener!=null) listener.onUserDisagree();
    		    		}
						disconnect();
					}
				});
				final PackageManager pm = mContext.getPackageManager();
				ApplicationInfo ai;
				try {
				    ai = pm.getApplicationInfo( mContext.getPackageName(), 0);
				    
				    if (pm.getApplicationLabel(ai)!=null)
				    	builder.setAppname(pm.getApplicationLabel(ai).toString());
				    
				    builder.setAppicon(ai.loadIcon(pm));
				} catch (final NameNotFoundException e) {
				    ai = null;
				}
				
				ZaloPrivacyWarningDialog dialog = builder.create();
				dialog.show();
			}
		});
    }
    */
    
    private int sendAgreeMessage() {
    	ZaloPacket packet = makeRequest();
    	packet.setCmd(AppContants.PluginCommands.COMMAND_POLICY_AGREE);
    	return sendMessageRequest(packet);
    }
    
    /*
    private int sendDisagreeMessage() {
    	ZaloPacket packet = makeRequest();
    	packet.setCmd(AppContants.PluginCommands.COMMAND_POLICY_DISAGREE);
    	return sendMessageRequest(packet);
    }
    */
    
    /**
     * Gửi tin chứng thực cho Zalo (đã được tự động gửi) 
     * @return mã của yêu cầu (requestId)
     */
    public int sendAuthMessage() {
    	ZaloPacket packet = makeRequest();
    	packet.setCmd(AppContants.PluginCommands.COMMAND_AUTHEN);
		try {
			packet.setParams(mAppApiKey.getBytes("UTF-8"));
			return sendMessageRequest(packet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
    }
    
    /**
     * Gửi yêu cầu lấy profile zalo 
     * @return mã của yêu cầu (requestId)
     */
//    public int sendGetProfile() {
//    	ZaloPacket packet = makeRequest();
//    	packet.setCmd(AppContants.PluginCommands.COMMAND_ZALO_INFO);
//    	packet.setParams(ByteArrayUtils.intToTwoByteArray(AppContants.PluginCommands.SUB_COMMAND_GET_ZALO_PROFILE));
//		return sendMessageRequest(packet);
//    }
    
    /**
     * Gửi yêu cầu nhận danh sách bạn 
     * @return mã của yêu cầu (requestId)
     */
//    public int sendGetFriends() {
//    	ZaloPacket packet = makeRequest();
//    	packet.setCmd(AppContants.PluginCommands.COMMAND_ZALO_INFO);
//    	packet.setParams(ByteArrayUtils.intToTwoByteArray(AppContants.PluginCommands.SUB_COMMAND_GET_ZALO_LIST));
//		return sendMessageRequest(packet);
//    }
   
    /**
     * Gửi yêu cần chat với user zalo
     * @param userId - id đối tượng muốn chat
     * @return mã của yêu cầu (requestId)
     */
    public int sendChatRequest(String userId) {
    	ZaloPacket packet = makeRequest();
    	packet.setCmd(AppContants.PluginCommands.COMMAND_CHAT_REQUEST);
    	try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		byte[] data = userId.getBytes("UTF-8");
    		bos.write(ByteArrayUtils.intToByteArray(data.length));
    		bos.write(data);
    		packet.setParams(bos.toByteArray());
			return sendMessageRequest(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
    }
    
    /**
     * Gửi yêu cầu tạo feed mới - hiện chỉ hộ trợ feed text và feed hình
     * @param text
     * @param files
     * @return
     */
    public int sendPostNewFeed(String text, File ... files) {
    	ZaloPacket packet = makeRequest();
    	packet.setCmd(AppContants.PluginCommands.COMMAND_POST_NEW_FEED);
    	try {
    		StringBuilder builder = new StringBuilder();
    		builder.append("{\"text\":");
    		if (text==null) {
    			builder.append("null,\"datas\":[");
    		} else {
    			builder.append("\"").append(text).append("\",\"datas\":[");
    		}
    		for (int i = 0; i < files.length; i++) {
				File f = files[i];
				String filePath = f.getAbsolutePath();
				if (filePath!=null) filePath = filePath.toLowerCase();
				if (f.exists() && f.isFile() && (filePath.endsWith(".jpg")||filePath.endsWith(".png")||filePath.endsWith(".bmp")||filePath.endsWith(".gif"))) {
					if (i>0) builder.append(",");
					builder.append("{\"path\":\"");
					builder.append(f.getAbsolutePath()).append("\",\"type\":\"photo\"}");
				}
			}
    		builder.append("]}");
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		String param = builder.toString();
    		byte[] data = param.getBytes("UTF-8");
    		bos.write(ByteArrayUtils.intToByteArray(data.length));
    		bos.write(data);
    		packet.setParams(bos.toByteArray());
			return sendMessageRequest(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return 0;
    }

    private int sendMessageRequest(ZaloPacket packet) {
    	int requestId = 0;
    	do {
    		requestId = random.nextInt();
    	} while (requestId==0 || mHashRequestIdTime.get(requestId)!=null);
    	mHashRequestIdTime.put(requestId, System.currentTimeMillis());
    	packet.setRequestId(requestId);
    	serviceConnection.sendMessage(packet);
    	return requestId;
    }
    
    private void sendMessageRequestNoRET(ZaloPacket packet) {
    	packet.setRequestId(0);
    	serviceConnection.sendMessage(packet);
    }
    
    private void checkTimeOutRequest() {
    	int key = 0;
    	long currentTime = System.currentTimeMillis();
    	long expiryTime = currentTime - mRequestTimeOut;
    	for(int i = 0; i < mHashRequestIdTime.size(); i++) {
    	   key = mHashRequestIdTime.keyAt(i);
    	   long time = mHashRequestIdTime.get(key);
    	   if (time < expiryTime) {
    		   mHashRequestIdTime.delete(key);
	    		for (ZaloConnectorListener listener : mHashListener) {
	    			if (listener!=null) listener.onRequestTimeOut(key);
	    		}
    	   }
    	}
    }
    
    private long getSleepTime() {
    	return TIME_2_CHECK_REQUEST_TIMEOUT;
    }
    
    private enum ConnectingState {
    	INIT, CONNECTED, AUTHENTICATING, AUTHENTICATED, CONFIRMED, DISCONNECTED
    }
	
	private class ThreadCheckTimeout extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					checkTimeOutRequest();
					Thread.sleep(getSleepTime());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

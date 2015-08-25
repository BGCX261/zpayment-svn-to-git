package com.zing.zalo.zalosdk.connector.data;

import java.io.ByteArrayOutputStream;

import android.os.Bundle;
import android.os.Message;

import com.zing.zalo.zalosdk.connector.util.ByteArrayUtils;

public class ZaloPacket {
	
	public static final String PACKET_DATA = "package_data";

	private byte msgType;
	private int version = AppContants.CONNECTOR_VERSION;
	private String appId;
	private short cmd;
	private int requestId;
	private int retCode;
	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}
	
	public void setMsgType(int msgType) {
		this.msgType = (byte) (msgType & 0xFF);
	}

	public int getVersion() {
		return version;
	}
	
	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	private byte[] params;

	public String getAppId()
	{
		return appId;
	}
	
	public void setAppId(String id)
	{
		appId = id;
	}

	public byte[] getParams() {
		return params;
	}

	public short getCmd() {
		return cmd;
	}

	public void setParams(byte[] params) {
		this.params = params;
	}

	public void setCmd(int cmd) {
		this.cmd = (short) (cmd & 0xFFFF);
	}
	
	public void setCmd(short cmd) {
		this.cmd = cmd;
	}
	
	public String getParamsString()
	{
		if(params != null && params.length > 0)
			return new String(params);
		else
			return "";
	}

	public static ZaloPacket fromMessage(Message msg) {
		ZaloPacket packet = null;
    	try
    	{
        	Bundle bundle = msg.getData();
        	if(bundle != null && bundle.containsKey(PACKET_DATA));
        	{
        		int currLen = 0;
        		byte[] responseData = bundle.getByteArray(PACKET_DATA);
        		packet = new ZaloPacket();
        		packet.msgType = (byte) ByteArrayUtils.readByte(responseData, currLen);
        		currLen += 1;
        		int appIdLen = ByteArrayUtils.readInt(responseData, currLen);
        		currLen += 4;
        		packet.appId = new String(responseData, currLen, appIdLen);
        		currLen += appIdLen;
        		packet.cmd = ByteArrayUtils.readShort(responseData, currLen);
        		currLen += 2;
        		packet.requestId = ByteArrayUtils.readInt(responseData, currLen);
        		currLen += 4;
        		packet.retCode = ByteArrayUtils.readInt(responseData, currLen);
        		currLen += 4;
        		
        		int paramLength = responseData.length - currLen;
        		if(paramLength > 0)
        		{
        			byte[] params = new byte[paramLength];
        			
        			for(int i = 0; i < paramLength; i++)
        			{
        				params[i] = responseData[currLen+i];
        			}
        			
        			packet.params = params;
        		}
        	}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}		
		return packet;
	}
	
	public Message toMessage() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			baos.write(msgType);
			baos.write(ByteArrayUtils.intToTwoByteArray(version));
			byte[] appIdData = appId.getBytes("UTF-8");
			baos.write(ByteArrayUtils.intToByteArray(appIdData.length));
			baos.write(appIdData);		// appId	
			baos.write(ByteArrayUtils.intToTwoByteArray(cmd));		// cmd	
			baos.write(ByteArrayUtils.intToByteArray(requestId));
			baos.write(ByteArrayUtils.intToByteArray(retCode));
			if(params != null && params.length>0)
				baos.write(params);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putByteArray(PACKET_DATA, baos.toByteArray());
		msg.setData(data);
		return msg;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("msgType:").append(msgType).append('\n');
		builder.append("appId:").append(appId).append('\n');
		builder.append("cmd:").append(cmd).append('\n');
		builder.append("requestId:").append(requestId).append('\n');
		builder.append("retCode:").append(retCode).append('\n');
		builder.append("param:").append(getParamsString()).append('\n');
		return builder.toString();
	}


}

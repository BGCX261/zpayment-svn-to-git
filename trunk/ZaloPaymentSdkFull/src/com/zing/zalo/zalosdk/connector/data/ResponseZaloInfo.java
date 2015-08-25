package com.zing.zalo.zalosdk.connector.data;

public class ResponseZaloInfo {
	private short subCommand;
	private byte[] data;
	public short getSubCommand() {
		return subCommand;
	}
	public void setSubCommand(short subCommand) {
		this.subCommand = subCommand;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
}

package com.zing.zalo.zalosdk.connector.util;

import java.io.DataInputStream;
import java.io.IOException;

public class ByteArrayUtils {
	public static int byteToInt(byte[] bIn)
	{
		return (((bIn[0] & 0xFF) << 0) + ((bIn[1] & 0xFF) << 8) + ((bIn[2] & 0xFF) << 16) + ((bIn[3] & 0xFF) << 24));
	}
	
	public static int readInt(byte[] bIn, int offset)
	{
		return (((bIn[offset] & 0xFF) << 0) + ((bIn[offset+1] & 0xFF) << 8) + ((bIn[offset+2] & 0xFF) << 16) + ((bIn[offset+3] & 0xFF) << 24));
	}
	
	public static int readByte(byte[] bIn, int offset)
    {
        return ((bIn[offset] & 0xFF) << 0);
    }
	
	public static int readInt(DataInputStream aIn) throws IOException
	{
		return (((aIn.read() & 0xFF) << 0) + ((aIn.read() & 0xFF) << 8) + ((aIn.read() & 0xFF) << 16) + ((aIn.read() & 0xFF) << 24));
	}

	public static int readShort(DataInputStream aIn) throws IOException
	{
		return (((aIn.read() & 0xFF) << 0) + ((aIn.read() & 0xFF) << 8));
	}
	
	public static short readShort(byte[] bIn, int offset)
	{
		return (short)(((bIn[offset] & 0xFF) << 0) + ((bIn[offset+1] & 0xFF) << 8));
	}
	
	public static long readLong(byte[] bIn, int offset)
	{
		long l = ((bIn[offset + 0] & 0xFFL) << 0) |
				((bIn[offset + 1] & 0xFFL) << 8) |
				((bIn[offset + 2] & 0xFFL) << 16) |
				((bIn[offset + 3] & 0xFFL) << 24) |
				((bIn[offset + 4] & 0xFFL) << 32) |
				((bIn[offset + 5] & 0xFFL) << 40) |
				((bIn[offset + 6] & 0xFFL) << 48) |
				((bIn[offset + 7] & 0xFFL) << 56) ;
		return l;
	}

	public static long readLong(DataInputStream aIn) throws IOException
	{		
		long l = ((aIn.read() & 0xFFL) << 0) |
				((aIn.read() & 0xFFL) << 8) |
				((aIn.read() & 0xFFL) << 16) |
				((aIn.read() & 0xFFL) << 24) |
				((aIn.read() & 0xFFL) << 32) |
				((aIn.read() & 0xFFL) << 40) |
				((aIn.read() & 0xFFL) << 48) |
				((aIn.read() & 0xFFL) << 56) ;
		return l;
	}
	
	//one byte
	public static byte[] intToOneByteArray(int value) {
		return new byte[] {
				(byte)((value >>> 0) & 0xFF) };
	}

	//two byte
	public static byte[] intToTwoByteArray(int value) {
		return new byte[] {
				(byte)((value >>> 0) & 0xFF),
				(byte)((value >>> 8) & 0xFF)};
	}

	//four bytes
	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)((value >>> 0) & 0xFF),
				(byte)((value >>> 8) & 0xFF),
				(byte)((value >>> 16) & 0xFF),
				(byte)((value >>> 24) & 0xFF)};
	}

	//8 bytes
	public static byte[] longToByteArray(long value) {
		return new byte[] {
				(byte)((value >>> 0) & 0xFFL),
				(byte)((value >>> 8) & 0xFFL),
				(byte)((value >>> 16) & 0xFFL),
				(byte)((value >>> 24) & 0xFFL),
				(byte)((value >>> 32) & 0xFFL),
				(byte)((value >>> 40) & 0xFFL),
				(byte)((value >>> 48) & 0xFFL),
				(byte)((value >>> 56) & 0xFFL)
		};
	}
	
}

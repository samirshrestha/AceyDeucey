package com.RotN.acdc.bluetooth;

import java.nio.ByteBuffer;

public class ReceivedDataParser {

	public int MsgType;	
	public byte[] Data;
	
	public ReceivedDataParser(byte[] receivedData){
		byte[] btype = new byte[4];
		System.arraycopy(receivedData, 0, btype, 0, 4);
		MsgType = ByteBuffer.wrap(btype).getInt();
		
		byte[] result = new byte[receivedData.length - 4];
		System.arraycopy(receivedData, 4, result, 0, result.length);
		Data = result;
	}

}

package com.zing.zalo.zalosdk.connector;

import java.util.HashSet;

import com.zing.zalo.zalosdk.connector.data.ResponseZaloInfo;


public class ZaloConnectorListener {

	/**
	 * Được gọi khi nhận được phản hồi các yêu cầu nhận thông tin (khác yêu cầu về gửi nhận tin nhắn) đã thành công.
	 * @param response
	 */
	public void onReceiveResponseZaloInfo(ResponseZaloInfo response) {}
	
	/**
	 * Được gọi khi nhận được trả về lỗi các yêu cầu nhận thông tin
	 * @param requestId mã của gói tin yêu cầu thông tin
	 * @param retCode mã lỗi trả về (#0)
	 * @param subCommand mã lệnh lấy thông tin (profile, friends...)
	 */
	public void onReceiveErrorZaloInfo(int requestId, int retCode, short subCommand) {}
	
	/**
	 * Được gọi khi được trả về thành công với các request không cần dữ liệu trả về (chat..)
	 * @param requestId
	 * @param command
	 */
	public void onReceiveResponse(int requestId, short command) {}
	
	/**
	 *  Được gọi khi nhận được trả về lỗi các yêu cầu gửi nhận tin (ko phải yêu cầu nhận thông tin)
	 * @param requestId mã của gói tin yêu cầu thông tin
	 * @param retCode mã lỗi trả về (#0)
	 * @param command mã lệnh gửi đi
	 */
	public void onReceiveError(int requestId, int retCode, short command) {}
	
	/**
	 *  Được gọi khi nhận được trả về lỗi khi yêu cầu chứng thực
	 * @param requestId - authen request id
	 * @param retCode - mã lỗi trả về
	 */
	public void onAuthenError(int requestId, int retCode) {}
	
	/**
	 * Được gọi khi một yêu cầu bị timeout
	 * @param requestId mã của gói tin yêu cầu
	 */
	public void onRequestTimeOut(int requestId) {}
	
	/**
	 * Được gọi khi dịch vụ đã chứng thực xong sẵn sàng nhận, gửi tin
	 * @param permisions 
	 */
	public void onReady(HashSet<Integer> permissions) {}
	
	/**
	 * Được gọi khi dịch vụ bị ngắt kết nối
	 */
	public void onDisconnected() {}
	
	/**
	 * Được nhận khi có lỗi lúc bind vào dịch vụ hoặc lỗi cấu cúc dữ liệu trả về, lỗi xử lý...
	 * @param ex
	 */
	public void onError(Exception ex) {}

	/**
	 * Được nhận khi người dùng từ chối không cho kết nối vào Zalo
	 */
	public void onUserDisagree() {
	}
}

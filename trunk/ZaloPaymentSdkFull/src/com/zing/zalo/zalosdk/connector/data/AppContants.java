package com.zing.zalo.zalosdk.connector.data;


public class AppContants 
{
	public static final int CONNECTOR_VERSION = 1;
	
	public static final int MESSAGE_HEADER_SIZE = 11;
	
	public interface PluginMessageType
	{
		public static final int MESSAGE_TYPE_RET 		= 1;		
		public static final int MESSAGE_TYPE_COMM 		= 2;
		public static final int MESSAGE_TYPE_REQ 		= 3;
	}
	
	public interface ZaloMessageStatus
	{
		public static final int STATUS_OK 		= 0;
		public static final int STATUS_ERROR 		= 1;
		public static final int STATUS_RECEIVED 		= 2;
	}
	
	/*public interface ZaloPermissions
	{
		public static final int PERMISSION_GET_PROFILE 		= R.string.zalo_privacy_warning_dialog_permission_get_profile;
		public static final String PERMISSION_GET_PROFILE_KEY = "agp";
		public static final int PERMISSION_GET_FRIENDS 		= R.string.zalo_privacy_warning_dialog_permission_get_friends;
		public static final String PERMISSION_GET_FRIENDS_KEY = "agfp";
		public static final int PERMISSION_SEND_CHAT 		= R.string.zalo_privacy_warning_dialog_permission_send_chat;
		public static final String PERMISSION_SEND_CHAT_KEY = "astf";
		public static final int PERMISSION_POST_FEED 		= R.string.zalo_privacy_warning_dialog_permission_post_feed;
		public static final String PERMISSION_POST_FEED_KEY = "aptw";
	}*/
	
	public interface PluginCommands
	{
		public static final int COMMAND_AUTHEN			= 12;
		
//		public static final int COMMAND_ZALO_INFO 		= 13;
//		public static final int SUB_COMMAND_GET_ZALO_PROFILE 	= 1;		
//		public static final int SUB_COMMAND_GET_ZALO_LIST		= 2;
		
		public static final int COMMAND_DISCONNECT_APP 	= 14;
		
		public static final int COMMAND_CHAT_REQUEST = 20;
		public static final int COMMAND_POST_NEW_FEED = 21;

		public static final int COMMAND_POLICY_AGREE = 22;
		public static final int COMMAND_POLICY_DISAGREE = 23;
	}
	
	public interface PluginErrorCode
	{
		public static final int UNKNOWN_ERROR = -1;
		public static final int NOT_REGISTERED = 1001;
		public static final int INVALID_API_KEY = 1002;
	}
}

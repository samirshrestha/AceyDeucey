package com.RotN.acdc.bluetooth;

public class Constants {
	//Received Data Types
    public static final int DATA_STRING = 1;
    public static final int DATA_GAME_DATA = 2;
    public static final int DATA_PLAYER_COLOR = 3;
    
    //Play Mode
    public static final String NEW_GAME_KEY = "NewGame";
    public static final String PLAY_MODE = "PlayMode";
    public static final int EXISTING_GAME = 0;
    public static final int SINGLE_PLAYER = 1;
    public static final int MULTI_PLAYER = 2;
    public static final int MULTI_PLAYER_BT = 3;
    
    //Pairing
    public static final int PAIRED = 1;
    public static final int IS_PAIRING = 2;
    public static final int NOT_PAIRED = 3;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_FAILED = 5;
    public static final int MESSAGE_WRITE_GAME = 6;
    
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int ENABLE_BT_FOR_FIND_GAME = 3;
    
}

package com.android.enjoyalarm;

public class ActivityVariable {

	// get text
	public static final int GET_TEXT_REQUEST_CODE = 0;
	public static final String GET_TEXT_EXTRA_SOURCE = "GET_TEXT_EXTRA_SOURCE";
	public static final String GET_TEXT_EXTRA_RESULT = "GET_TEXT_EXTRA_RESULT";
	public static final String GET_TEXT_EXTRA_TYPE = "GET_TEXT_EXTRA_TYPE";//0.name 1.encourage words

	// music select
	public static final int MUSIC_SELECT_REQUEST_CODE = 10;
	public static final String MUSIC_SELECT_EXTRA_ORIGINAL_MUSIC_TITLE = "MUSIC_SELECT_EXTRA_ORIGINAL_MUSIC_TITLE";
	public static final String MUSIC_SELECT_EXTRA_SELECT_MUSIC_TITLE = "MUSIC_SELECT_EXTRA_SELECT_MUSIC_TITLE";
	public static final String MUSIC_SELECT_EXTRA_SELECT_MUSIC_URI = "MUSIC_SELECT_EXTRA_SELECT_MUSIC_URI";

	//wake up
	public final static String INTENT_EXTRA_ALARM_ID = "ALARM_ID";
	
	//preference
	static final String PREFERENCE_NAME_MAIN_ACTIVITY = "PREFERENCE_MAIN";
	static final String PREFERENCE_BOOLEAN_FIRST_USE = "FIRST_USE";
	static final String PREFERENCE_NAME_WAKE_ACTIVITY = "PREFERENCE_WAKE";
	static final String PREFERENCE_INT_ALARM_NOT_WAKE_NUM = "NOT_WAKE_NUM_";//to append alarm id
	
}

package com.android.enjoyalarm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.Contacts.PhotosColumns;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 
 * problems may occur: 1.in sql "where ? = ?", if ? is varchar type, should I
 * change the second ? into '?'; 2.in ContentValues.put(String,String), should I
 * change the second String into '..';
 *
 */
public class ModelUtil {

	private static boolean debug = false;

	public static class AlarmBasicInfo {
		public int id;
		public String name;
		public int hour;
		public int minute;
		public String days;
		
		
		/**
		 *@param days
		 *				0126, 346 and so on: 0 represents Monday, and 6 represents Sunday
		 */
		public AlarmBasicInfo(int id, String name, int hour, int minute, String days) {
			this.id = id;
			this.name = name;
			this.hour = hour;
			this.minute = minute;
			this.days = days;
		}
		
		@Override
		public String toString() {
			return "id: " + id + "  name: " + name + "  hour: " + hour + "  minute: " + minute
					+ "  days: " + days;
		}
	}

	public static List<AlarmBasicInfo> getAlarmsBasicInfo(Context context) {
		List<AlarmBasicInfo> resultList = new ArrayList<AlarmBasicInfo>();
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(ModelVariable.ALARM_TABLE_NAME, new String[] {
				ModelVariable.ALARM_COLUMN1_ID,
				ModelVariable.ALARM_COLUMN2_NAME,
				ModelVariable.ALARM_COLUMN3_TIME,
				ModelVariable.ALARM_COLUMN4_DAYS, }, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			resultList.add(new AlarmBasicInfo(cursor.getInt(0), cursor
					.getString(1), ModelUtil.getHourFromTime(cursor
					.getString(2)), ModelUtil.getMinuteFromTime(cursor
					.getString(2)), cursor.getString(3)));
			if (debug) {
				Log.i("getAlarmBasicInfo", "id:" + cursor.getInt(0) + ", name:"
						+ cursor.getString(1));
			}
		}
		cursor.close();
		db.close();

		return resultList;
	}

	/**
	 * 
	 * @param context
	 * @param alarmId	pass -1 while delete the temp alarm data
	 */
	public static void deleteAlarm(Context context, int alarmId) {
		String table;
		if (alarmId == -1) {
			table = ModelVariable.TEMP_ALARM_TABLE_NAME;
		} else {
			table = ModelVariable.ALARM_TABLE_NAME;
		}
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete(table,
				ModelVariable.ALARM_COLUMN1_ID + " = ?",
				new String[] { String.valueOf(alarmId) });
		if (debug) {
			Log.i("deleteAlarm", "rows affected number:" + result);
		}
		db.close();
	}
	
	public static void checkTempAlarm(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(ModelVariable.TEMP_ALARM_TABLE_NAME, new String[]{ModelVariable.ALARM_COLUMN1_ID},
				null, null, null, null, null);
		if (cursor!= null && cursor.moveToFirst()) {
			db.delete(ModelVariable.TEMP_ALARM_TABLE_NAME, null, null);
		}
		ContentValues value = new ContentValues();
		value.put(ModelVariable.ALARM_COLUMN1_ID, -1);
		db.insert(ModelVariable.TEMP_ALARM_TABLE_NAME, null, value);
		db.close();
	}

	public static void recordTime(Context context, int hour, String time) {

		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(ModelVariable.TIME_TABLE_NAME,
				new String[] { ModelVariable.TIME_COLUMN1_NOW_HOUR },
				ModelVariable.TIME_COLUMN1_NOW_HOUR + "=?",
				new String[] { String.valueOf(hour) }, null, null, null);

		if (cursor.moveToFirst()) { // update
			ContentValues values = new ContentValues();
			values.put(ModelVariable.TIME_COLUMN1_NOW_HOUR, hour);
			values.put(ModelVariable.TIME_COLUMN2_SET_TIME, time);
			int result = db.update(ModelVariable.TIME_TABLE_NAME, values,
					ModelVariable.TIME_COLUMN1_NOW_HOUR + " = ?",
					new String[] { String.valueOf(hour) });
			if (debug) {
				Log.i("recordTime", "update result:" + result);
			}

		} else { // insert
			ContentValues values = new ContentValues();
			values.put(ModelVariable.TIME_COLUMN1_NOW_HOUR, hour);
			values.put(ModelVariable.TIME_COLUMN2_SET_TIME, time);
			long resultId = db.insert(ModelVariable.TIME_TABLE_NAME, null,
					values);
			if (debug) {
				Log.i("recordTime", "insert result id:" + resultId);
			}
		}

		cursor.close();
		db.close();
	}

	/**
	 * record text,musicUri, photoUri, videoUri and so on according to the type
	 * of Variable.DAIA_TYPE_**
	 */
	public static void recordData(Context context, String data, String type) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(ModelVariable.DATA_TABLE_NAME,
				new String[] { ModelVariable.DATA_COLUMN3_COUNT },
				ModelVariable.DATA_COLUMN1_TYPE + "=? and "
						+ ModelVariable.DATA_COLUMN2_DATA + "=?", new String[] {
						type, data }, null, null, null);

		if (cursor.moveToFirst()) { // update
			int count = cursor.getInt(0);
			count++;
			ContentValues values = new ContentValues();
			values.put(ModelVariable.DATA_COLUMN3_COUNT, count);
			int result = db.update(ModelVariable.DATA_TABLE_NAME, values,
					ModelVariable.DATA_COLUMN1_TYPE + "=? and "
							+ ModelVariable.DATA_COLUMN2_DATA + "=?",
					new String[] { type, data });
			if (debug) {
				Log.i("recordMediaData", "type:" + type + "  update result:"
						+ result);
			}

		} else { // insert
			ContentValues values = new ContentValues();
			values.put(ModelVariable.DATA_COLUMN1_TYPE, type);
			values.put(ModelVariable.DATA_COLUMN2_DATA, data);
			values.put(ModelVariable.DATA_COLUMN3_COUNT, 1);
			long resultId = db.insert(ModelVariable.DATA_TABLE_NAME, null,
					values);
			if (debug) {
				Log.i("recordMediaData", "type:" + type + "  insert result id:"
						+ resultId);
			}
		}

		cursor.close();
		db.close();
	}

	/**
	 * return the suggest time or null
	 */
	public static String getSuggestTime(Context context, int hour) {
		// find records equal to hour
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(ModelVariable.TIME_TABLE_NAME,
				new String[] { ModelVariable.TIME_COLUMN2_SET_TIME },
				ModelVariable.TIME_COLUMN1_NOW_HOUR + "=?",
				new String[] { String.valueOf(hour) }, null, null, null);

		if (cursor.moveToFirst()) {
			String time = cursor.getString(0);
			cursor.close();
			db.close();
			return time;
		}

		// find records equal to hour-1
		int preHour = (hour - 1 + 24) % 24;
		cursor = db.query(ModelVariable.TIME_TABLE_NAME,
				new String[] { ModelVariable.TIME_COLUMN2_SET_TIME },
				ModelVariable.TIME_COLUMN1_NOW_HOUR + "=?",
				new String[] { String.valueOf(preHour) }, null, null, null);
		if (cursor.moveToFirst()) {
			String time = cursor.getString(0);
			cursor.close();
			db.close();
			return time;
		}

		// find records equal to hour+1
		int nextHour = (hour + 1) % 24;
		cursor = db.query(ModelVariable.TIME_TABLE_NAME,
				new String[] { ModelVariable.TIME_COLUMN2_SET_TIME },
				ModelVariable.TIME_COLUMN1_NOW_HOUR + "=?",
				new String[] { String.valueOf(nextHour) }, null, null, null);
		if (cursor.moveToFirst()) {
			String time = cursor.getString(0);
			cursor.close();
			db.close();
			return time;
		}
		
		cursor.close();
		db.close();
		return null;
	}

	/**
	 * return the top three data if possible
	 */
	public static List<String> getSuggestData(Context context, String type) {
		List<String> resultList = new ArrayList<String>();
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(ModelVariable.DATA_TABLE_NAME,
				new String[] { ModelVariable.DATA_COLUMN2_DATA },
				ModelVariable.DATA_COLUMN1_TYPE + "=?", new String[] { type },
				null, null, ModelVariable.DATA_COLUMN3_COUNT + " desc");
		int number = 0;
		while (number++ < 3 && cursor.moveToNext()) {
			resultList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();

		return resultList;
	}
	
	/**
	 * 
	 * @param context
	 * @param type	ModelVariable.DATA_TYPE_WAKE_MUSIC_URI or ModelVariable.DATA_TYPE_MUSIC_URI
	 * @return	the top three data if possible
	 */
	public static List<String> getSuggestMusic(Context context, String type) {
		List<String> resultList = new ArrayList<String>();
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(ModelVariable.DATA_TABLE_NAME,
				new String[] { ModelVariable.DATA_COLUMN2_DATA },
				ModelVariable.DATA_COLUMN1_TYPE + "=?", new String[] { type },
				null, null, ModelVariable.DATA_COLUMN3_COUNT + " desc");
		int number = 0;
		while (number < 3 && cursor.moveToNext()) {
			String uri = cursor.getString(0);
			if (getMusicName(context, uri) != null) {
				resultList.add(uri);
				number++;
			}
		}
		cursor.close();
		db.close();

		return resultList;
	}
	
	/**
	 * return null if uri doesn't match any audio
	 */
	public static String getMusicName(Context context, String uri) {
		Cursor cursor = context.getContentResolver()
				.query(Uri.parse(uri), new String[]{MediaStore.Audio.AudioColumns.TITLE}, null, null, null);
		if (cursor == null) {
			return null;
		} else {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
	}
	
	public static String getCallingMusicName() {
		return null;
	}

	public static List<String> getAlarmNames(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(ModelVariable.ALARM_TABLE_NAME,
				new String[] { ModelVariable.ALARM_COLUMN2_NAME }, null, null,
				null, null, null);
		List<String> result = new ArrayList<String>();
		while (cursor.moveToNext()) {
			result.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return result;
	}

	
	
	public static String getDaysString(List<Integer> days) {
		StringBuilder dayString = new StringBuilder();
		for (Integer day : days) {
			dayString.append(day);
		}
		return dayString.toString();
	}
	
	public static int getHourFromTime(String time) {
		return Integer.parseInt(time.substring(0, time.indexOf(':')));
	}

	public static int getMinuteFromTime(String time) {
		return Integer.parseInt(time.substring(time.indexOf(':') + 1));
	}

	public static String generateTime(int hour, int minute) {
		return hour + ":" + minute;
	}

	public static String getFriendNamesString(List<String> names) {
		StringBuilder result = new StringBuilder();
		for (String name : names) {
			result.append(name).append(',');
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	public static List<String> getFriendNamesList(String names) {
		return Arrays.asList(names.split(","));
	}

	public static String getFriendPhonesString(List<String> phones) {
		StringBuilder result = new StringBuilder();
		for (String phone : phones) {
			result.append(phone).append(',');
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	public static List<String> getFriendPhonesList(String phones) {
		return Arrays.asList(phones.split(","));
	}
}

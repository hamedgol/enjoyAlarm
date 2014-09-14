package com.enjoyalarm.view;

public class ViewUtil {

	/**
	 * get the double-bit String like '15','05'
	 */
	public static String getDoubleBitStringForTime(int hourOrMinute) {
		if (hourOrMinute < 10) {
			return "0" + String.valueOf(hourOrMinute);
		} else {
			return String.valueOf(hourOrMinute);
		}
	}

	public static TimeEntry getRemainTime(int targetWeekDay, int targetHour,
			int targetMinute, int nowWeekDay, int nowHour, int nowMinute) {
		int totalMinute = ((((targetWeekDay + 7) * 24 + targetHour) * 60 + targetMinute) - ((nowWeekDay * 24 + nowHour) * 60 + nowMinute))
				% (7 * 24 * 60);

		return new TimeEntry(totalMinute / (24 * 60),
				(totalMinute % (24 * 60)) / 60, (totalMinute % (24 * 60)) % 60);
	}

	public static class TimeEntry {
		public int day;
		public int hour;
		public int minute;

		public TimeEntry(int day, int hour, int minute) {
			this.day = day;
			this.hour = hour;
			this.minute = minute;
		}
	}

	public static String getMusicName(String uri) {
		return null;
	}
	
	public static String getCallingMusicName() {
		return null;
	}
}

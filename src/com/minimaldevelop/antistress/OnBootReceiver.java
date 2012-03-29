/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
 */

package com.minimaldevelop.antistress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {
	private static final int PERIOD = 300000; // 5 minutes

	@Override
	public void onReceive(Context context, Intent intent) {
//		AlarmManager mgr = (AlarmManager) context
//				.getSystemService(Context.ALARM_SERVICE);
//
//		Calendar now = Calendar.getInstance();
//		int hour = now.get(Calendar.HOUR_OF_DAY);
//		int minute = now.get(Calendar.MINUTE);
//
//		// TODO: For future check SharedPreferences for settings and use that
//		// instead of default
//		
//		int nextAlarmHour = 10;
//		
//		// default settings
//		int addMinutes = 60 - minute;
//		int addHours = 0;
//		if (hour < 10) {
//			addHours = 10 - hour;
//			nextAlarmHour = 15;
//		} else if (hour > 10 && hour < 15) {
//			addHours = 15 - hour;
//			nextAlarmHour = 20;
//		} else if (hour > 15 && hour < 20) {
//			addHours = 20 - hour;
//			nextAlarmHour = 10;
//		} else if (hour == 10 || hour == 15 || hour == 20) {
//			addMinutes = 2;
//			switch(hour) {
//			case 10:
//				nextAlarmHour = 15;
//				break;
//			case 15:
//				nextAlarmHour = 20;
//				break;
//			case 20:
//				nextAlarmHour = 10;
//				break;
//			}
//		}
//
//		// convert hours and minutes to seconds
//		long addSeconds = addHours * 60 * 60;
//		addSeconds += addMinutes * 60;
//		// convert to mseconds
//		long addMiliSeconds = addSeconds * 1000;
//
//		Intent i = new Intent(context, OnAlarmReceiver.class);
//		i.putExtra("NextAlarmHour", nextAlarmHour);
//		i.putExtra("CurrentMSforAlarm", addMiliSeconds);
//		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//		mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, addMiliSeconds, pi);
	}
}
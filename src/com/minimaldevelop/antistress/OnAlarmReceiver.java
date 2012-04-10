package com.minimaldevelop.antistress;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {
	String TAG = "OnAlarmReceiver";
	NotificationManager nm;
	static final int uniqueID = 2206982;
	
  @Override
  public void onReceive(Context context, Intent intent) {
    
	 int nextAlarmHour = intent.getIntExtra("NextAlarmHour", 0);
	 long addMiliSeconds = intent.getLongExtra("CurrentMSforAlarm", 0);
	 int addHours = intent.getIntExtra("CurrentHourforAlarm", 0);
	 int addMinutes = intent.getIntExtra("CurrentMinuteforAlarm", 0);
	 
	 Log.d(TAG, "addHours="+addHours);
	 Log.d(TAG, "addMinutes="+addMinutes);
	 Log.d(TAG, "addMiliSeconds="+addMiliSeconds);
	 Log.d(TAG, "nextAlarmHour="+nextAlarmHour);
	 
	 //Notification
	 Intent i = new Intent(context, AntiStressExerciseActivity.class);
	 nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	 nm.cancel(uniqueID);	
	 
	 PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
	 String title = context.getResources().getString(R.string.notificationTitle);
	 String body = context.getResources().getString(R.string.notificationBody);
	 Notification n = new Notification(R.drawable.icon, body, System.currentTimeMillis());
	 n.setLatestEventInfo(context, title, body, pi);
	 n.defaults = Notification.DEFAULT_ALL;
	 nm.notify(uniqueID, n);
//    context.startActivity(new Intent(context, AntiStressExerciseActivity.class));
  }
}
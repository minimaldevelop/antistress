package com.minimaldevelop.antistress;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class AntiStressExerciseActivity extends Activity {

	public static boolean D = false;
	
	private Handler mHandler = new Handler();
	private TextView mTimeLabel;
	private TextView mActionLabel;
	private TextView mActionPrepareLabel;
	private Button mStartButton;
	private ProgressBar mProgressBar;
	private ToggleButton mToggleSound;
	private int tick = 10;
	private int progress = 0;
	private final int PROGRESSMAX = 1005;
	private final int SPEED = 1000; //need to be 1000, other values only use for testing
	private WakeLock wakeLock;
	private MediaPlayer mPlayer = new MediaPlayer();
	
	private enum ExerciseState {
		Breath3, Keep10, BreathIn3Serie, BreathOut3Serie
	};
	
	private enum ButtonState {
		Start, Resume, Pause
	}
	
	private enum MPlayerState {
		Init, Playing, Pause
	}
	
	private ExerciseState exerciseState = ExerciseState.Breath3;	
	private ButtonState buttonState = ButtonState.Start;
	private MPlayerState mplayerState = MPlayerState.Init;
	
	private int currentExerciseTry = 0;
	private int currentBreath3Serie = 1;
	private final String ACTION_REMAINDER_SETUP = "com.minimaldevelop.antistress.REMAINDER_SETUP";
	private final String TAG = "AntiStressExerciseActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(OnAlarmReceiver.uniqueID);
		
		final Intent intent = getIntent();		
		String action = intent.getAction();
		
		if (D) Log.d(TAG, TAG+" Intent Action=" + action);
		
		if (action != null && action.equals(ACTION_REMAINDER_SETUP)) {
			if (D) Log.d(TAG, TAG+" Intent Action=ACTION_REMAINDER_SETUP, calling finish()");
			finish(); //because OnDestroy call function setupRemainder()
		} 
		
		setContentView(R.layout.main);

		mTimeLabel = (TextView) findViewById(R.id.textView1);
		mActionLabel = (TextView) findViewById(R.id.textView2);
		mStartButton = (Button) findViewById(R.id.button1);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mActionPrepareLabel = (TextView) findViewById(R.id.textView4);
		mToggleSound = (ToggleButton) findViewById(R.id.toggleButton1);
		
		mProgressBar.setMax(PROGRESSMAX);

		mStartButton.setOnClickListener(mStartListener);
		mToggleSound.setOnCheckedChangeListener(mSoundToggle);
		
		//Wake lock
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AntiStress");
		
		//Admob
		AdView adView = (AdView)this.findViewById(R.id.adView);
	    adView.loadAd(new AdRequest());
	    
	    //Play some music
	    toggleMusic(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		wakeLock.acquire();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		wakeLock.release();
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {			

			updateActionText();
			String sTick;
			if (tick < 10) {
				sTick = "0" + tick;
			} else {
				sTick = Integer.toString(tick);
			}
			mTimeLabel.setText(sTick);
//			Log.i("mUpdateTimeTask", "Elapsed time: " + tick);
			tick--;

			if (tick >= 0) {
				mHandler.postDelayed(this, SPEED);
			} else {
				goToNextState();
				doExercise();
			}
		}
	};

	OnClickListener mStartListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (buttonState == ButtonState.Start || buttonState == ButtonState.Resume) {
				if (mProgressBar.getProgress() == PROGRESSMAX) {
					progress = 0;
				}

				mHandler.removeCallbacks(mUpdateTimeTask);
				doExercise();
				buttonState = ButtonState.Pause;
				mStartButton.setText(R.string.pauseButton);
			} else if (buttonState == ButtonState.Pause) {
				mHandler.removeCallbacks(mUpdateTimeTask);
				buttonState = ButtonState.Resume;
				mStartButton.setText(R.string.resumeButton);
				progress -= 15;
			}
		}
	};
	
	OnCheckedChangeListener mSoundToggle = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			
				toggleMusic(isChecked);
		}
	};
	
	private void doExercise() {
		//sve puta 3
		if (D) Log.d("doExcercise", "State=" + exerciseState.toString());
		if (currentExerciseTry < 3) {
			setAndReturnTick();			

			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.postDelayed(mUpdateTimeTask, SPEED);
			progress = progress + 15;
			mProgressBar.setProgress(progress); 
		} else {
			//It is over			
			//Reset 
			exerciseState = ExerciseState.Breath3;
			currentExerciseTry = 0;
			currentBreath3Serie = 1;	
			mActionLabel.setText(R.string.Finish);
			mProgressBar.setProgress(PROGRESSMAX); 
			mActionPrepareLabel.setText("");
			buttonState = ButtonState.Start;
			mStartButton.setText(R.string.startButton);
		}	
	}
	
	private int setAndReturnTick() {
		if (exerciseState == ExerciseState.Keep10) {
			tick = 10;
		} else {
			tick = 3;
		}
		return tick;
	}
	
	private void updateActionText() {
		if (D) Log.d("Update Text", "UpdateText State=" + exerciseState.toString());
		if (exerciseState == ExerciseState.Breath3) {
			mActionLabel.setText(getString(R.string.Breath3, tick));
			mActionPrepareLabel.setText(getString(R.string.Keep10, 10));
		} else if (exerciseState == ExerciseState.Keep10) {
			mActionLabel.setText(getString(R.string.Keep10, tick));
			mActionPrepareLabel.setText(getString(R.string.BreathOut3Serie, 3));
		} else if (exerciseState == ExerciseState.BreathIn3Serie) {
			mActionLabel.setText(getString(R.string.BreathIn3Serie, tick, currentBreath3Serie,10));
			if (currentBreath3Serie < 10) {
				mActionPrepareLabel.setText(getString(R.string.BreathOut3Serie, 3));
			} else {	
				mActionPrepareLabel.setText(getString(R.string.Keep10, 10));
			}
		} else if (exerciseState == ExerciseState.BreathOut3Serie) {
			mActionLabel.setText(getString(R.string.BreathOut3Serie, tick));
			if (D) Log.d("updateActionText()", "currentExerciseTry=" + currentExerciseTry);
			if (currentExerciseTry < 3 ){
				int serie = 1;
				if (currentBreath3Serie > 10) {
					serie = 1;
				} else {
					serie = currentBreath3Serie;
				}
				mActionPrepareLabel.setText(getString(R.string.BreathIn3Serie, 3, serie, 10));
			}
			if (currentExerciseTry == 2 && currentBreath3Serie == 11){
				mActionPrepareLabel.setText(R.string.endOfExercise);
			}
		}
	}
	
	private void goToNextState() {
		if (exerciseState == ExerciseState.Breath3) {
			exerciseState = ExerciseState.Keep10;
		} else if (exerciseState == ExerciseState.Keep10) {
			exerciseState = ExerciseState.BreathOut3Serie;
		} else if (exerciseState == ExerciseState.BreathIn3Serie) {							
			currentBreath3Serie++;
			if (currentBreath3Serie < 11) {
				exerciseState = ExerciseState.BreathOut3Serie;
			} else {
				exerciseState = ExerciseState.Keep10;					
			}

		} else if (exerciseState == ExerciseState.BreathOut3Serie) {
			exerciseState = ExerciseState.BreathIn3Serie;
			if (currentBreath3Serie == 11) {
				currentBreath3Serie = 1;
				currentExerciseTry++;
			}
		}
	}
	
	private long getRemainderMiliseconds(String time) {
		//time format is hh:mm
		Calendar now = Calendar.getInstance();
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		int nowMinute = now.get(Calendar.MINUTE);
		
		int schedHour = Integer.parseInt(time.substring(0, 2));
		int schedMinute = Integer.parseInt(time.substring(3, 5));
		
		int addHours = 0;
		int addMinutes = 0;
		
		if (schedHour > nowHour) {
			addHours = schedHour - nowHour - 1;
		} else {
			addHours = 24 - nowHour + schedHour - 1; //-1 is because we add minutes
		}
		
		if (schedMinute > nowMinute) {
			addMinutes = schedMinute - nowMinute;
		} else {
			addMinutes = 60 - nowMinute + schedMinute;
		}
		
		if (D) {
			Log.d(TAG, "getRemainderMiliseconds schedHour=" + schedHour + " addHours="+addHours);
			Log.d(TAG, "getRemainderMiliseconds schedMinute=" + schedMinute + " addMinutes="+addMinutes);
		}
		
		// convert hours and minutes to seconds
		long addSeconds = addHours * 60 * 60;
		addSeconds += addMinutes * 60;
		// convert to milliseconds
		long addMiliSeconds = addSeconds * 1000;
		
		return addMiliSeconds;
	}
	
	private void setupRemainder() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		boolean remainderOn = settings.getBoolean("remainderOn", false);

		if (remainderOn) {
			Context context = this;
			AlarmManager mgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			long remainder1Miliseconds =  getRemainderMiliseconds(settings.getString("prefRemainder1", getResources()
					.getString(R.string.prefRemainder1DefaultValue)));
			if (D) Log.d(TAG, "Call get for remainder1Miliseconds");
			long remainder2Miliseconds =  getRemainderMiliseconds(settings.getString("prefRemainder2", getResources()
					.getString(R.string.prefRemainder2DefaultValue)));
			if (D) Log.d(TAG, "Call get for remainder2Miliseconds");
			long remainder3Miliseconds =  getRemainderMiliseconds(settings.getString("prefRemainder3", getResources()
					.getString(R.string.prefRemainder3DefaultValue)));
			if (D) Log.d(TAG, "Call get for remainder3Miliseconds");			
			
			//find nextRemainderMiliseconds - It is the closest remainder time to current time 
			long nextRemainderMiliseconds = remainder1Miliseconds;			
			if (nextRemainderMiliseconds > remainder2Miliseconds) {
				nextRemainderMiliseconds = remainder2Miliseconds;
			}			
			if (nextRemainderMiliseconds >  remainder3Miliseconds) {
				nextRemainderMiliseconds = remainder3Miliseconds;
			}
			

			Intent i = new Intent(context, OnAlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
			if (D) Log.d("AntiStressExerciseActivity", "Sent Remainder Broadcast");
			mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + nextRemainderMiliseconds, pi);
		}
	}
	
	private void toggleMusic(boolean enabled) {
		
		if (D) Log.d(TAG, "toggleMusic nabled=" + enabled + " mplayerState=" + mPlayer.toString());
		
		if (mplayerState == MPlayerState.Init) {
			try {
				AssetFileDescriptor afd = getAssets().openFd("antistress_flute_ocean.ogg");
				mPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(), afd.getLength());
				mPlayer.prepare();
				mPlayer.setLooping(true);
				mPlayer.start();
				mplayerState = MPlayerState.Playing;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mplayerState == MPlayerState.Playing) {
			mPlayer.pause();
			mplayerState = MPlayerState.Pause;
		} else if (mplayerState == MPlayerState.Pause) {
			mPlayer.start();
			mplayerState = MPlayerState.Playing;
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    
	        case R.id.item1:
	        	Intent intent = new Intent (AntiStressExerciseActivity.this, Preferences.class);
	        	startActivity(intent);
	        break;
	        
	        
	        case R.id.item2:
	        	Intent helpIntent = new Intent (AntiStressExerciseActivity.this, HelpActivity.class);
	        	startActivity(helpIntent);
	        break;
	        
	        case R.id.item3:
	        	finish();
	        break;
	        
	    }
	    return true; 
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//if is enabled in shared preferences
		if (D) Log.d(TAG, TAG+" OnDestroy()");
		setupRemainder();
		mPlayer.stop();
		mPlayer.release();
	}

}
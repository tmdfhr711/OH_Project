package com.example.widgettutorial;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPoolSetting {

	public static SoundPool sp;
	public static int id;
	
	public static void setSoundPool(Context context){
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		id = sp.load(context, R.raw.siren , 1);
	}
	
	public static void startAlarm(){
			
			sp.play(id, 1, 1, 0, -1, 1);
		}
}

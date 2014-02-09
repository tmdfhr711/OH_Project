package com.example.widgettutorial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneBootCompleteReceiver extends BroadcastReceiver{

	public static boolean wasPhoneBootSucessful = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            wasPhoneBootSucessful = true;
        }
	}


	

}

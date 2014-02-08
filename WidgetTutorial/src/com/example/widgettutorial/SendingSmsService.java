package com.example.widgettutorial;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class SendingSmsService extends Service{

	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sendSMS("01042175589","service test");
	}

	private void sendSMS(String phoneNumber, String message)
    {        
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);        
    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}

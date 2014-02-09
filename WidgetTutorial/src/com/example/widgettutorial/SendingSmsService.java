package com.example.widgettutorial;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SendingSmsService extends Service{

	private AppWidgetManager awm;
	private Context c;
	private int awID;
	
	private LocationManager mgr = null;
	private Location loc = null;
	private boolean isGpsReceived;
	private double getLatitude =0, getLongitude=0;
	private String urlString = "https://maps.google.com/?q=";
	private String smsAddress = null;
	private boolean stateCheck;
	private boolean alarmCheck;
	private NotificationManager nm;
	private Notification mNoti;
	
	private SoundPool sp;
	int id;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("WidgetTutorial", "ServiceOnCreate");
		init();
		SharedPreferences sharedPref = getSharedPreferences("SendingsmsPref", MODE_PRIVATE);
		stateCheck = sharedPref.getBoolean("state", false);
		alarmCheck = sharedPref.getBoolean("alarmState", false);
		Log.d("state", String.valueOf(stateCheck));
		if(stateCheck){
			
			sendSmsfunction();
		}
		else if(alarmCheck){
			startAlarm();
		}
		else{
			Intent intent = new Intent(SendingSmsService.this , WidgetConfig.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(SendingSmsService.this, 0, intent, 0);
			try {
				pendingIntent.send();
			} catch (CanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//startActivity(intent);
		}
		
		stopSelf();
	}

	public void init(){
		isGpsReceived = false;
		mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		SharedPreferences sharedPref = getSharedPreferences("SendingsmsPref", MODE_PRIVATE);
		if(sharedPref.getString("phoneNumber", "").equals("")){
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("name", "me");
	        editor.putString("phoneNumber", getPhoneNumber());
	        editor.commit();
		}
		
	}
	
	
	public void startAlarm(){
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		id = sp.load(c, R.raw.siren , 1);
		sp.play(id, 1, 1, 0, 0, 1);
	}
	public String getPhoneNumber()
	{
	 TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	 return mgr.getLine1Number();
	}

	private String getBestProvider(LocationManager mgr){

		Criteria criteria = new Criteria();

		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		criteria.setPowerRequirement(Criteria.POWER_LOW);

		criteria.setAltitudeRequired(false);

		criteria.setBearingRequired(false);

		criteria.setSpeedRequired(false);

		criteria.setCostAllowed(false);

		
		String provider = mgr.getBestProvider(criteria, true);
		
		return provider;
	}
	
	private void sendSmsfunction(){
		
		if(getBestProvider(mgr).equals("gps")){

			mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new MyGpsLocationListener());
		}
		else if(getBestProvider(mgr).equals("network")){
			
			mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, new MyNetLocationListener());
		}
	}
	
	public void updateWithNewLocation(Location location , String provider){
		if(isGpsReceived){
			if(LocationManager.GPS_PROVIDER.equals(provider)){
				loc = location;
			}
			else{
				long gpsGenTime = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER).getTime();
				long curTime = System.currentTimeMillis();
				if((curTime-gpsGenTime) > 10000){
					loc = location;
					isGpsReceived = false;
				}
			}
		}
		else{
			loc = location;
		}
	}
	
	public class MyGpsLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			isGpsReceived = true;
			updateWithNewLocation(location, "gps");
			getLatitude = loc.getLatitude();
			getLongitude = loc.getLongitude();
			SharedPreferences sharedPref = getSharedPreferences("SendingsmsPref", MODE_PRIVATE);
			sendSMS(sharedPref.getString("phoneNumber", ""), "My Location MapView" + urlString + getLatitude +","+getLongitude);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class MyNetLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			updateWithNewLocation(location, "network");
			getLatitude = loc.getLatitude();
			getLongitude = loc.getLongitude();
			SharedPreferences sharedPref = getSharedPreferences("SendingsmsPref", MODE_PRIVATE);
			sendSMS(sharedPref.getString("phoneNumber", ""), "My Location MapView" + urlString + getLatitude +","+getLongitude);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
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

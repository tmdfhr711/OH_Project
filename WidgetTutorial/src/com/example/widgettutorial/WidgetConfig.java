package com.example.widgettutorial;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.TextView;

public class WidgetConfig extends Activity implements OnClickListener{

	private AppWidgetManager awm;
	private Context c;
	private int awID;
	private Button complete;
	private Button showContact;
	private CheckBox alarmCheck;
	private CheckBox stateCheck;
	private TextView mName = null;
	private TextView mNumber = null;
	private Intent intent = null;
	private Button stopsendsms = null;
	private String getName = null;
	private String getPhoneNumber = null;
	private boolean smsState;
	private SharedPreferences sharedPref;
	private NotificationManager nm;
	private Notification smsNoti;
	private Notification alarmNoti;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widgetconfig);
		Log.d("WidgetTutorial", "ConfigOnCreate");
		c = WidgetConfig.this;
		//Getting info about this widget that Launched this Activity
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if(extras != null){
			awID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}/*else{
			finish();
		}*/
		init();
		
		complete.setOnClickListener(this);
		showContact.setOnClickListener(this);

		awm = AppWidgetManager.getInstance(c);
	}

	public void init(){
		complete = (Button)findViewById(R.id.bwidgetconfig);
		showContact = (Button)findViewById(R.id.showcontact);
		
		mName = (TextView) findViewById(R.id.mName);
		mNumber = (TextView) findViewById(R.id.mNumber);
		stateCheck = (CheckBox)findViewById(R.id.stateCheck);
		alarmCheck = (CheckBox)findViewById(R.id.alarmCheck);
		sharedPref = c.getSharedPreferences("SendingsmsPref", c.MODE_PRIVATE);
		if(sharedPref.getString("phoneNumber", "").equals("")){
			mName.setText("나");
			mNumber.setText(getPhoneNumber());
			stateCheck.setChecked(sharedPref.getBoolean("state", false));
			alarmCheck.setChecked(sharedPref.getBoolean("alarmState", false));
		}else{
			mName.setText(sharedPref.getString("name", ""));
			mNumber.setText(sharedPref.getString("phoneNumber", ""));
			stateCheck.setChecked(sharedPref.getBoolean("state", false));
			alarmCheck.setChecked(sharedPref.getBoolean("alarmState", false));
		}
		
		SoundPoolSetting.setSoundPool(c);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v == complete){
			Log.d("WidgetTutorial", "complete");
			RemoteViews remoteView = new RemoteViews(c.getPackageName() , R.layout.widget);
			//remoteView.setTextViewText(R.id.tvConfigInput, e);
			
			Intent intent = new Intent(c,SendingSmsService.class);
			PendingIntent pendingIntent = PendingIntent.getService(c, 0, intent, 0);
			
			remoteView.setOnClickPendingIntent(R.id.bwidgetOpen, pendingIntent);
			awm.updateAppWidget(awID, remoteView);
			
			Intent result = new Intent();
			result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, awID);
			setResult(RESULT_OK,result);
			
			sharedPref = c.getSharedPreferences("SendingsmsPref", c.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("state", stateCheck.isChecked());
            editor.putBoolean("alarmState", alarmCheck.isChecked());
            editor.commit();
            /*mName.setText(sharedPref.getString("name", ""));
            mNumber.setText(sharedPref.getString("phoneNumber", ""));
            stateCheck.setChecked(stateCheck.isChecked());*/
            boolean smsState = sharedPref.getBoolean("state", false);
            boolean alarmState = sharedPref.getBoolean("alarmState", false);
            boolean locationChange = sharedPref.getBoolean("locationChange", false);
            
            if(smsState)
            	smsNotification("sms");
            else if(alarmState)
            	smsNotification("alarm");
            finish();
		}
		else if(v == showContact){
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		    startActivityForResult(intent, 0);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK)
		{
				Cursor cursor = getContentResolver().query(data.getData(), 
						new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, 
					ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
				cursor.moveToFirst();
	 	        
	            getName = cursor.getString(0);
	            getPhoneNumber = cursor.getString(1);
	            
	           sharedPref = c.getSharedPreferences("SendingsmsPref", c.MODE_PRIVATE);
	            SharedPreferences.Editor editor = sharedPref.edit();
	            editor.putString("name", getName);
	            editor.putString("phoneNumber", getPhoneNumber);
	            editor.commit();
	            
	            //sharedPref.getString("name", "");
	            //sharedPref.getString("phoneNumber", "");
	            mName.setText(sharedPref.getString("name", ""));
	            mNumber.setText(sharedPref.getString("phoneNumber", ""));
	            cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	public void smsNotification (String mode){
		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		PendingIntent mPendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(getApplicationContext(),
						WidgetConfig.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		if(mode.equals("sms")){
		
			smsNoti = new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("위급 모드")
			.setContentText("위급모드를 종료하려면 클릭하세요.")
			.setSmallIcon(R.drawable.alarm)
			.setTicker("WidgetTutorial 알림")
			.setAutoCancel(true)
			.setContentIntent(mPendingIntent)
			.build();
			
			nm.notify(1234, smsNoti);
		}else if(mode.equals("alarm")){
			alarmNoti = new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("경보음 모드")
			.setContentText("모드를 바꾸려면 클릭하세요.")
			.setSmallIcon(R.drawable.alarm)
			.setTicker("WidgetTutorial 알림")
			.setAutoCancel(true)
			.setContentIntent(mPendingIntent)
			.build();
			
			nm.notify(1235, alarmNoti);
		}
		
	}
	public String getPhoneNumber()
	{
	 TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	 return mgr.getLine1Number();
	}

}

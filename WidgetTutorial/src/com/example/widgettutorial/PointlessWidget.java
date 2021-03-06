package com.example.widgettutorial;

import java.util.Random;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PointlessWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d("WidgetTutorial", "ProviderOnUpdate");
		if (PhoneBootCompleteReceiver.wasPhoneBootSucessful) {
	        PhoneBootCompleteReceiver.wasPhoneBootSucessful = false;
			final int N = appWidgetIds.length;
			for(int i = 0; i < N; i++){
				int awID = appWidgetIds[i];
				RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget);
				//v.setTextViewText(R.id.tvwidgetUpdate, rand);
				appWidgetManager.updateAppWidget(awID, v);
			}
		}
	}
	

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		Toast.makeText(context, "bye", Toast.LENGTH_SHORT).show();
	}

	

	

}

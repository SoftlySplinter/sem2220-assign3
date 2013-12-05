package uk.ac.aber.androidcourse.conference.widget;

import java.util.Random;

import uk.ac.aber.androidcourse.conference.R;
import uk.ac.aber.androidcourse.conference.Session;
import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
	private static final String LOG_TAG = "Conferece Widget";
	
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		
		for(int i = 0; i < N; i++) {
			Random r = new Random();
			int appWidgetId = appWidgetIds[i];
			
			Log.d(LOG_TAG, String.format("App Widget ID: %d", appWidgetId));
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

			Session now = getSession(1, context);
			Session next = getSession(2, context);
			
			views.setTextViewText(R.id.widget_now, now.title);
			views.setTextViewText(R.id.widget_now_time, Session.formatTime(now));
			views.setTextViewText(R.id.widget_now_detail, now.type);
			
			views.setTextViewText(R.id.widget_next, next.title);
			views.setTextViewText(R.id.widget_next_time, Session.formatTime(next));
			views.setTextViewText(R.id.widget_next_detail, next.type);
			
			views.setTextViewText(R.id.widget_notification, "Some notification: " + r.nextInt());
			
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		
		Log.d(LOG_TAG, "onUpdate called.");
	}



	private Session getSession(int index, Context context) {
		Cursor cursor = context.getContentResolver().query(ConferenceCP.Sessions.CONTENT_URI, null, null, null, ConferenceCP.Sessions.DAY_ID_NAME + " ASC");
		
		
		for(int i = 0; i < index && i < cursor.getCount(); i++) {
			if(!cursor.moveToNext()) {
				break;
			}
		}
		
		if(cursor.isBeforeFirst() || cursor.isAfterLast()) {
			return null;
		} else {
			return Session.load(cursor);
		}
	}
}

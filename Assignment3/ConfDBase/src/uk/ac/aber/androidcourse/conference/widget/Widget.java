package uk.ac.aber.androidcourse.conference.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import uk.ac.aber.androidcourse.conference.R;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess.DBAccessException;
import uk.ac.aber.androidcourse.conferencelibrary.objects.Session;
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

		for (int i = 0; i < N; i++) {
			Random r = new Random();
			int appWidgetId = appWidgetIds[i];

			Log.d(LOG_TAG, String.format("App Widget ID: %d", appWidgetId));

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget);
			
			DBAccess access = new DBAccess(context.getContentResolver());
			
			Session now = getSession(1, access);
			Session next = getSession(2, access);
			
			if(now != null)
				this.renderNow(now, views);
			if(next != null) 
				this.renderNext(next, views, now);
			
			views.setTextViewText(R.id.widget_notification,
					"Some notification: " + r.nextInt());

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

		Log.d(LOG_TAG, "onUpdate called.");
	}

	private void renderNow(Session session, RemoteViews rViews) {
		String title = session.title;
		if (isNow(session)) {
			title = "Now: " + title;
		}
		rViews.setTextViewText(R.id.widget_now, title);
		rViews.setTextViewText(R.id.widget_now_time, session.formatTime());
		rViews.setTextViewText(R.id.widget_now_detail, session.type);
	}

	private boolean isNow(Session session) {
		Calendar now = Calendar.getInstance();

		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm",
				Locale.getDefault());
		Calendar startEvent = Calendar.getInstance();
		Calendar endEvent = Calendar.getInstance();
		try {
			startEvent.setTime(dateFormatter.parse(session.startTime));
			endEvent.setTime(dateFormatter.parse(session.endTime));
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getLocalizedMessage(), e);
			return false;
		}
		
		Log.d(LOG_TAG,
				String.format("Event starts at %d, time is now: %d",
						startEvent.get(Calendar.HOUR), 9));
		return now.compareTo(startEvent) >= 0 && now.compareTo(endEvent) < 0;
	}

	private void renderNext(Session session, RemoteViews rViews, Session previous) {
		String title = session.title;
		if (isNext(session, previous)) {
			title = "Next: " + title;
		}
		rViews.setTextViewText(R.id.widget_next, title);
		rViews.setTextViewText(R.id.widget_next_time, session.formatTime());
		rViews.setTextViewText(R.id.widget_next_detail, session.type);
	}
	
	public boolean isNext(Session session, Session previous) {
		if(previous == null) return false;
		
		Calendar now = Calendar.getInstance();

		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm",
				Locale.getDefault());
		Calendar startEvent = Calendar.getInstance();
		Calendar prevEnd = Calendar.getInstance();
		try {
			startEvent.setTime(dateFormatter.parse(session.startTime));
			prevEnd.setTime(dateFormatter.parse(previous.endTime));
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getLocalizedMessage(), e);
			return false;
		}

		return now.compareTo(startEvent) > 0 && now.compareTo(prevEnd) <= 0;
	}

	public void previous(View view) {
		Log.i(LOG_TAG, "Previous Day");
	}

	public void next(View view) {
		Log.i(LOG_TAG, "Next Day");
	}

	private Session getSession(int index, DBAccess access) {
		try {
			return Session.load(index, access);
		} catch (DBAccessException e) {
			Log.e(LOG_TAG, "Unable to load session " + index, e);
			return null;
		}
	}
}

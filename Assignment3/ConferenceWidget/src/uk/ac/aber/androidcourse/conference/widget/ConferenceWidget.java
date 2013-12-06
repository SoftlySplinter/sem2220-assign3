package uk.ac.aber.androidcourse.conference.widget;

import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess.DBAccessException;
import uk.ac.aber.androidcourse.conferencelibrary.objects.Session;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ConferenceWidget extends AppWidgetProvider {
	private static final String LOG_TAG = "Conferece Widget";
	public static final String WIDGET_ID = "uk.ac.aber.androidcourse.widget.WIDGET_ID";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(LOG_TAG, "onUpdate");
		for (int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			Intent intent = new Intent(context, ConferenceWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			views.setRemoteAdapter(appWidgetIds[i], R.id.widget_list, intent);
			views.setEmptyView(R.id.widget_list, R.id.widget_empty);
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private final void doRender(int widgetId, Context context,
			AppWidgetManager manager) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		
		/*DBAccess access = new DBAccess(context.getContentResolver());
		Session now = getSession(1, access);
		Session next = getSession(2, access);
		this.renderNow(now, views);
		this.renderNext(next, views, now);*/

		this.bindIntent(widgetId, views, context);
		this.getNotification(widgetId, views, context);

		manager.updateAppWidget(widgetId, views);
	}

	private void getNotification(int widgetId, RemoteViews views,
			Context context) {
		// TODO load notification from Service.
	}

	private void bindIntent(int widgetId, RemoteViews views, Context context) {
	}

//	private void renderNow(Session session, RemoteViews rViews) {
//		String title = session.title;
//		if (isNow(session)) {
//			title = "Now: " + title;
//		}
//		rViews.setTextViewText(R.id.widget_now, title);
//		rViews.setTextViewText(R.id.widget_now_time, session.formatTime());
//		rViews.setTextViewText(R.id.widget_now_detail, session.type);
//	}
//
//	private boolean isNow(Session session) {
//		Calendar now = Calendar.getInstance();
//
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm",
//				Locale.getDefault());
//		Calendar startEvent = Calendar.getInstance();
//		Calendar endEvent = Calendar.getInstance();
//		try {
//			startEvent.setTime(dateFormatter.parse(session.startTime));
//			endEvent.setTime(dateFormatter.parse(session.endTime));
//		} catch (ParseException e) {
//			Log.e(LOG_TAG, e.getLocalizedMessage(), e);
//			return false;
//		}
//
//		Log.d(LOG_TAG,
//				String.format("Event starts at %d, time is now: %d",
//						startEvent.get(Calendar.HOUR), 9));
//		return now.compareTo(startEvent) >= 0 && now.compareTo(endEvent) < 0;
//	}
//
//	private void renderNext(Session session, RemoteViews rViews,
//			Session previous) {
//		String title = session.title;
//		if (isNext(session, previous)) {
//			title = "Next: " + title;
//		}
//		rViews.setTextViewText(R.id.widget_next, title);
//		rViews.setTextViewText(R.id.widget_next_time, session.formatTime());
//		rViews.setTextViewText(R.id.widget_next_detail, session.type);
//	}
//
//	public boolean isNext(Session session, Session previous) {
//		if (previous == null)
//			return false;
//
//		Calendar now = Calendar.getInstance();
//
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm",
//				Locale.getDefault());
//		Calendar startEvent = Calendar.getInstance();
//		Calendar prevEnd = Calendar.getInstance();
//		try {
//			startEvent.setTime(dateFormatter.parse(session.startTime));
//			prevEnd.setTime(dateFormatter.parse(previous.endTime));
//		} catch (ParseException e) {
//			Log.e(LOG_TAG, e.getLocalizedMessage(), e);
//			return false;
//		}
//
//		return now.compareTo(startEvent) > 0 && now.compareTo(prevEnd) <= 0;
//	}

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

package uk.ac.aber.androidcourse.conference.widget;

import java.util.concurrent.ExecutionException;

import uk.ac.aber.androidcourse.conference.widget.list.SessionsListBroadcastReceiver;
import uk.ac.aber.androidcourse.conference.widget.list.SessionsListService;
import uk.ac.aber.androidcourse.conference.widget.notification.NotificationDownloadTask;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Handles the Conference Widget.
 * 
 * @author Alex Brown
 * @version 1.0
 */
public class ConferenceWidget extends AppWidgetProvider {
	private static final String LOG_TAG = "Conferece Widget";
	public static final String NEXT_ACTION = "uk.ac.aber.androidcourse.widget.NEXT_ID";
	public static final String PREV_ACTION = "uk.ac.aber.androidcourse.widget.PREV_ID";
	public static final String CURRENT_DAY = "uk.ac.aber.androidcourse.widget.CURRENT_DAY";
	public static final int DEFAULT_DAY = 0;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget);
			this.setupIntents(context, appWidgetIds[i], views, appWidgetManager);
			this.getNotification(views);

			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * Sets up the intents on the two buttons and the list.
	 * 
	 * @param context
	 * @param appWidgetId
	 * @param views
	 * @param appWidgetManager
	 */
	private void setupIntents(Context context, int appWidgetId,
			RemoteViews views, AppWidgetManager appWidgetManager) {
		Intent listIntent = new Intent(context, SessionsListService.class);
		listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		listIntent.putExtra(ConferenceWidget.CURRENT_DAY,
				ConferenceWidget.DEFAULT_DAY);
		listIntent
				.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
		views.setRemoteAdapter(R.id.widget_list, listIntent);
		views.setEmptyView(R.id.widget_list, R.id.widget_empty);

		Intent prevIntent = new Intent(context,
				SessionsListBroadcastReceiver.class);
		prevIntent.setAction(ConferenceWidget.PREV_ACTION);
		prevIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		prevIntent
				.setData(Uri.parse(prevIntent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(context,
				0, prevIntent, 0);
		views.setOnClickPendingIntent(R.id.widget_left, pendingPrevIntent);

		Intent nextIntent = new Intent(context,
				SessionsListBroadcastReceiver.class);
		nextIntent.setAction(ConferenceWidget.NEXT_ACTION);
		nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		nextIntent
				.setData(Uri.parse(nextIntent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,
				0, nextIntent, 0);
		views.setOnClickPendingIntent(R.id.widget_right, pendingNextIntent);

	}

	/**
	 * Gets the notification from <a
	 * href="http://users.aber.ac.uk/aos/android/message.php"
	 * >http://users.aber.ac.uk/aos/android/message.php</a> and displays it
	 * through the notification text field.
	 * 
	 * @param views
	 *            The RemoteViews object containing the notification text field.
	 */
	private final void getNotification(final RemoteViews views) {
		try {
			final NotificationDownloadTask service = new NotificationDownloadTask();
			service.execute();

			String notification = service.get();
			if (notification != null && !notification.isEmpty()) {
				views.setTextViewText(R.id.widget_notification, notification);
			}
		} catch (InterruptedException e) {
			Log.d(LOG_TAG, "Interrupted", e);
		} catch (ExecutionException e) {
			Log.d(LOG_TAG, "Execution Failed", e);
		}
	}
}

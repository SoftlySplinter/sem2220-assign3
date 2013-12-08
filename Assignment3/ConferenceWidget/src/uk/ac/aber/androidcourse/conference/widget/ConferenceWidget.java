package uk.ac.aber.androidcourse.conference.widget;

import uk.ac.aber.androidcourse.conference.widget.list.SessionsListService;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import android.annotation.TargetApi;
import android.app.PendingIntent;
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
	private static final String NEXT_ACTION = "uk.ac.aber.androidcourse.widget.NEXT_ID";
	private static final String PREV_ACTION = "uk.ac.aber.androidcourse.widget.PREV_ID";
	public static final String CURRENT_DAY = "uk.ac.aber.androidcourse.widget.CURRENT_DAY";
	public static final int DEFAULT_DAY = 0;

	private int currentDay = DEFAULT_DAY;
	private long[] dayIDs;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.dayIDs = new DBAccess(context.getContentResolver()).getDayIds();
		if (intent.getAction().equals(ConferenceWidget.NEXT_ACTION)) {
			this.next(context, intent);
		}
		if (intent.getAction().equals(ConferenceWidget.PREV_ACTION)) {
			this.previous(context, intent);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		this.dayIDs = new DBAccess(context.getContentResolver()).getDayIds();

		Log.i(LOG_TAG, "Updating widget for day: " + this.currentDay);
		for (int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget);

			Intent listIntent = new Intent(context,
					SessionsListService.class);
			listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			listIntent.putExtra(ConferenceWidget.CURRENT_DAY,
					this.dayIDs[this.currentDay]);
			listIntent.setData(Uri.parse(listIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			views.setRemoteAdapter(R.id.widget_list, listIntent);
			views.setEmptyView(R.id.widget_list, R.id.widget_empty);

			// TODO setup notification stuff.

			if (this.atStart(this.currentDay)) {
				views.setViewVisibility(R.id.widget_left, View.INVISIBLE);
			} else {
				views.setViewVisibility(R.id.widget_left, View.VISIBLE);
				Intent prevIntent = new Intent(context, ConferenceWidget.class);
				prevIntent.setAction(ConferenceWidget.PREV_ACTION);
				prevIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetIds[i]);
				prevIntent.putExtra(ConferenceWidget.CURRENT_DAY,
						this.currentDay - 1);
				prevIntent.setData(Uri.parse(prevIntent
						.toUri(Intent.URI_INTENT_SCHEME)));
				PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(
						context, 0, prevIntent, 0);
				views.setOnClickPendingIntent(R.id.widget_left,
						pendingPrevIntent);
			}

			if (this.atEnd(this.currentDay)) {
				views.setViewVisibility(R.id.widget_right, View.INVISIBLE);
			} else {
				views.setViewVisibility(R.id.widget_right, View.VISIBLE);
				Intent nextIntent = new Intent(context, ConferenceWidget.class);
				nextIntent.setAction(ConferenceWidget.NEXT_ACTION);
				nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetIds[i]);
				nextIntent.putExtra(ConferenceWidget.CURRENT_DAY,
						this.currentDay + 1);
				nextIntent.setData(Uri.parse(nextIntent
						.toUri(Intent.URI_INTENT_SCHEME)));
				PendingIntent pendingNextIntent = PendingIntent.getBroadcast(
						context, 0, nextIntent, 0);
				views.setOnClickPendingIntent(R.id.widget_right,
						pendingNextIntent);
			}

			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public void previous(Context context, Intent intent) {
		this.currentDay = intent.getIntExtra(CURRENT_DAY, 0);
		// TODO not sure this is the right way to handle this.
		AppWidgetManager mngr = AppWidgetManager.getInstance(context);
		this.onUpdate(context, mngr, new int[] { intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID) });
	}

	public void next(Context context, Intent intent) {
		this.currentDay = intent.getIntExtra(CURRENT_DAY, 0);
		// TODO not sure this is the right way to handle this.
		AppWidgetManager mngr = AppWidgetManager.getInstance(context);
		this.onUpdate(context, mngr, new int[] { intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID) });
	}

	private final boolean atStart(long id) {
		return id == 0;
	}

	private final boolean atEnd(long id) {
		return id == this.dayIDs.length - 1;
	}
}

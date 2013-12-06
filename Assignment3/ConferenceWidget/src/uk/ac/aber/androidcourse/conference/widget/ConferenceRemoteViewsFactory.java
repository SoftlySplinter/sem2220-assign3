package uk.ac.aber.androidcourse.conference.widget;

import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class ConferenceRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	public final static String LOG_TAG = "Conference Remote Views Factory";

	private final Context context;
	private final DBAccess access;
	private Cursor sessions;
	private final int widgetId;
	
	public ConferenceRemoteViewsFactory(Context context, Intent intent) {
		Log.d(LOG_TAG, "Initialising factory.");
		
		this.context = context;
		this.access = new DBAccess(context.getContentResolver());
		this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	
	@Override
	public int getCount() {
		Log.d(LOG_TAG, "There are " + (this.sessions.getCount() - 1) + " items");
		return this.sessions.getCount() - 1;
	}

	@Override
	public long getItemId(int position) {
		this.sessions.moveToFirst();
		this.sessions.move(position);
		return this.sessions.getLong(ConferenceCP.Sessions.ID_COLUMN);
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		Log.d(LOG_TAG, "getting view at: " + position);
		RemoteViews rv = new RemoteViews(this.context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.widget_notification, Integer.toString(position));
		return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		this.sessions = this.access.getSessionsCursorForDay(1);
	}

	@Override
	public void onDataSetChanged() {
		
	}

	@Override
	public void onDestroy() {
		this.sessions.close();
	}

}

package uk.ac.aber.androidcourse.conference.widget.list;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aber.androidcourse.conference.widget.ConferenceWidget;
import uk.ac.aber.androidcourse.conference.widget.R;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess.DBAccessException;
import uk.ac.aber.androidcourse.conferencelibrary.objects.Session;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Loads the Session list data.
 * 
 * @author Alex Brown
 * @version 1.0
 */
public final class SessionsListFactory implements
		RemoteViewsService.RemoteViewsFactory {

	public final static String LOG_TAG = "Conference Remote Views Factory";

	private final Context context;
	private final DBAccess access;
	private List<Session> sessions;
	private final long[] dayIDs;
	private int currentDay;

	public SessionsListFactory(Context context, Intent intent) {
		Log.d(LOG_TAG, "Initialising factory.");

		this.context = context;
		this.access = new DBAccess(context.getContentResolver());
		this.currentDay = intent.getIntExtra(ConferenceWidget.CURRENT_DAY,
				ConferenceWidget.DEFAULT_DAY);
		this.dayIDs = this.access.getListOfDayIds();
		this.sessions = new ArrayList<Session>(0);

		// TODO this is a bit hacky.
		SessionsListBroadcastReceiver.linker = this;
	}

	@Override
	public int getCount() {
		return this.sessions.size();
	}

	@Override
	public long getItemId(int position) {
		return this.sessions.get(position)._id;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rv = new RemoteViews(this.context.getPackageName(),
				R.layout.row);
		Session session = this.sessions.get(position);
		rv.setTextViewText(R.id.row_title, session.title);
		rv.setTextViewText(R.id.row_detail, session.type);
		rv.setTextViewText(R.id.row_time, session.formatTime());
		return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		// Do nothing, handled by onDataSetChanged
	}

	/**
	 * Loads the sessions into {@link #sessions}
	 * 
	 * @param dayID
	 */
	private final void loadSessions(long dayID) {
		long[] sessionIds = this.access.getSessionsForDayId(dayID);
		this.sessions = new ArrayList<Session>(sessionIds.length);
		for (long _id : sessionIds) {
			try {
				this.sessions.add(Session.load(_id, access));
			} catch (DBAccessException e) {
				Log.e(LOG_TAG, "Unable to load session " + _id, e);
			}
		}
	}

	@Override
	public void onDataSetChanged() {
		Log.d(LOG_TAG, "Data set changed, day: " + this.currentDay);
		this.loadSessions(this.dayIDs[this.currentDay]);
	}

	@Override
	public void onDestroy() {
		this.sessions.clear();
		this.sessions = null;
	}

	/**
	 * Receives broadcasts (from {@link SessionsListBroadcastReceiver}).
	 * 
	 * @param context
	 * @param intent
	 */
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConferenceWidget.NEXT_ACTION)) {
			this.next(context, intent);
		}
		if (intent.getAction().equals(ConferenceWidget.PREV_ACTION)) {
			this.previous(context, intent);
		}
	}

	/**
	 * Go to the next day of sessions, wrapping around to the first day is
	 * necessary.
	 * 
	 * @param context
	 * @param intent
	 */
	private void next(Context context, Intent intent) {
		AppWidgetManager mngr = AppWidgetManager.getInstance(context);

		if (this.currentDay + 1 < this.dayIDs.length) {
			this.currentDay++;
		} else {
			this.currentDay = 0;
		}
		mngr.notifyAppWidgetViewDataChanged(intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID), R.id.widget_list);
	}

	/**
	 * Go to the previous day of sessions, wrapping around to the last day of
	 * sessions if necessary.
	 * 
	 * @param context
	 * @param intent
	 */
	private void previous(Context context, Intent intent) {
		AppWidgetManager mngr = AppWidgetManager.getInstance(context);

		if (this.currentDay - 1 > 0) {
			this.currentDay--;
		} else {
			this.currentDay = this.dayIDs.length - 1;
		}
		mngr.notifyAppWidgetViewDataChanged(intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID), R.id.widget_list);
	}
}

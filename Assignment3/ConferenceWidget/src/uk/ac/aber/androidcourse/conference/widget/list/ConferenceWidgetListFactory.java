package uk.ac.aber.androidcourse.conference.widget.list;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aber.androidcourse.conference.widget.R;
import uk.ac.aber.androidcourse.conference.widget.R.id;
import uk.ac.aber.androidcourse.conference.widget.R.layout;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess.DBAccessException;
import uk.ac.aber.androidcourse.conferencelibrary.objects.Session;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class ConferenceWidgetListFactory implements RemoteViewsService.RemoteViewsFactory {
	public final static String LOG_TAG = "Conference Remote Views Factory";

	private final Context context;
	private final DBAccess access;
	private List<Session> sessions;
	private final long[] dayIDs;
	
	public ConferenceWidgetListFactory(Context context, Intent intent) {
		Log.d(LOG_TAG, "Initialising factory.");
		
		this.context = context;
		this.access = new DBAccess(context.getContentResolver());
		long widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		this.dayIDs = this.access.getListOfDayIds();
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
		RemoteViews rv = new RemoteViews(this.context.getPackageName(), R.layout.row);
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
		this.loadSessions(1);
	}
	
	private final void loadSessions(long dayID) {
		if(!this.validDay(dayID)) {
			Log.e(LOG_TAG, dayID + " is not a valid day");
			return;
		}
		
		long[] sessionIds = this.access.getSessionsForDayId(dayID);
		this.sessions = new ArrayList<Session>(sessionIds.length);
		for(long _id : sessionIds) {
			try {
				this.sessions.add(Session.load(_id, access));
			} catch (DBAccessException e) {
				Log.e(LOG_TAG, "Unable to load session " + _id, e);
			}
		}
	}
	
	private final boolean validDay(long id) {
		// I miss Python :( `return id in this.dayIDs`
		for(long _id : this.dayIDs) {
			if(_id == id) {
				return true;
			}
		}
		return false;
	}
	
	private final boolean atStart(long id) {
		return id == this.dayIDs[0];
	}
	
	private final boolean atEnd(long id) {
		return id == this.dayIDs[this.dayIDs.length - 1];
	}

	@Override
	public void onDataSetChanged() {
		this.sessions.clear();
		
		// TODO change 1 to a variable to select the day.
		this.loadSessions(1);
	}

	@Override
	public void onDestroy() {
		this.sessions.clear();
	}

}

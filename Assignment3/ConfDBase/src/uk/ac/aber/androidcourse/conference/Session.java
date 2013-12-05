package uk.ac.aber.androidcourse.conference;

import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import android.database.Cursor;

public final class Session {
	public static final Session load(Cursor cursor) {
		String _id, title, startTime, endTime, type, dayId;
		
		_id = cursor.getString(ConferenceCP.Sessions.ID_COLUMN);
		title = cursor.getString(ConferenceCP.Sessions.TITLE_COLUMN);
		startTime = cursor.getString(ConferenceCP.Sessions.START_TIME_COLUMN);
		endTime = cursor.getString(ConferenceCP.Sessions.END_TIME_COLUMN);
		type = cursor.getString(ConferenceCP.Sessions.TYPE_COLUMN);
		dayId = cursor.getString(ConferenceCP.Sessions.DAY_ID_COLUMN);
		
		return new Session(_id, title, startTime, endTime, type, dayId);
	}

	public final static String formatTime(Session session) {
		return String.format("%s - %s", session.startTime, session.endTime);
	}

	// Public instance variables because they are final
	public final String _id;
	public final String title;
	public final String startTime;
	public final String endTime;
	public final String type;
	public final String dayId;
	
	private Session(String _id, String title, String startTime, String endTime, String type, String dayId) {
		this._id = _id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type;
		this.dayId = dayId;
	}
}

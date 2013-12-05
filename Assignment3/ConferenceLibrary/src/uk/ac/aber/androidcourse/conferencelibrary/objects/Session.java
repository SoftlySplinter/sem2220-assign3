package uk.ac.aber.androidcourse.conferencelibrary.objects;

import android.database.Cursor;
import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess.DBAccessException;

public final class Session {
	public static final Session load(long id, DBAccess access) throws DBAccessException {
		
		Cursor cursor = access.getSession(id);

		long _id = cursor.getLong(ConferenceCP.Sessions.ID_COLUMN);
		String title = cursor.getString(ConferenceCP.Sessions.TITLE_COLUMN);
		String startTime = cursor.getString(ConferenceCP.Sessions.START_TIME_COLUMN);
		String endTime = cursor.getString(ConferenceCP.Sessions.END_TIME_COLUMN);
		String type = cursor.getString(ConferenceCP.Sessions.TYPE_COLUMN);
		Day day = Day.load(cursor.getLong(ConferenceCP.Sessions.DAY_ID_COLUMN), access);

		return new Session(_id, title, startTime, endTime, type, day);
	}

	public final String formatTime() {
		return String.format("%s - %s (%s)", this.startTime, this.endTime, this.day.date);
	}

	// Public instance variables because they are final
	public final long _id;
	public final String title;
	public final String startTime;
	public final String endTime;
	public final String type;
	public final Day day;

	private Session(long _id, String title, String startTime, String endTime,
			String type, Day day) {
		this._id = _id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type;
		this.day = day;
	}
}

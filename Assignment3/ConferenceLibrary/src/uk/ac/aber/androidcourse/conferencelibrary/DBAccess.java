package uk.ac.aber.androidcourse.conferencelibrary;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Provides a set of utility methods for accessing the ConferenceCP content
 * provider.
 * 
 * Created by Chris Loftus in December 2011. Copyright 2011 Aberystwyth
 * University. All rights reserved.
 * 
 */
public class DBAccess {

	/*
	 * We can't use managed queries in all cases as these only exist for
	 * activities. at times we must instead use unmanaged queries from the more
	 * widespread ContentResolver.
	 */
	private Activity activity;
	private ContentResolver callerContentResolver;

	public DBAccess(Activity caller) {
		activity = caller;
		callerContentResolver = caller.getContentResolver();
	}

	public DBAccess(ContentResolver resolver) {
		activity = null;
		callerContentResolver = resolver;
	}

	public long[] getListOfDayIds() {
		// SELECT _id FROM days ORDER BY _id
		return getArrayOfIdsWithQuery(ConferenceCP.Days.CONTENT_URI,
				BaseColumns._ID, null, null, ConferenceCP.Days._ID + " ASC");
	}

	public String getDayForDayId(long dayId) {
		// SELECT day FROM days WHERE dayID = ?
		return getStringWithQuery(ConferenceCP.Days.CONTENT_URI.buildUpon()
				.appendPath(dayId + "").build(), ConferenceCP.Days.DAY_NAME,
				null, null);
	}

	public String getDateForDayId(long dayId) {
		// SELECT date FROM days WHERE dayID = ?
		return getStringWithQuery(ConferenceCP.Days.CONTENT_URI.buildUpon()
				.appendPath(dayId + "").build(), ConferenceCP.Days.DATE_NAME,
				null, null);
	}

	public long[] getSessionsForDayId(long dayId) {
		// SELECT _id FROM sessions WHERE dayID = ? ORDER BY startTime
		return getArrayOfIdsWithQuery(ConferenceCP.Sessions.CONTENT_URI,
				BaseColumns._ID, ConferenceCP.Sessions.DAY_ID_NAME + " = ?",
				new String[] { "" + dayId },
				ConferenceCP.Sessions.START_TIME_NAME + " ASC");
	}

	public String getSessionTitleForSessionId(long sessionId) {
		// SELECT title FROM sessions WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Sessions.CONTENT_URI.buildUpon()
				.appendPath(sessionId + "").build(),
				ConferenceCP.Sessions.TITLE_NAME, null, null);
	}

	public String getStartTimeForSessionId(long sessionId) {
		// SELECT startTime FROM sessions WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Sessions.CONTENT_URI.buildUpon()
				.appendPath(sessionId + "").build(),
				ConferenceCP.Sessions.START_TIME_NAME, null, null);
	}

	public String getEndTimeForSessionId(long sessionId) {
		// SELECT endTime FROM sessions WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Sessions.CONTENT_URI.buildUpon()
				.appendPath(sessionId + "").build(),
				ConferenceCP.Sessions.END_TIME_NAME, null, null);
	}

	public String getTypeForSessionId(long sessionId) {
		// SELECT type FROM sessions WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Sessions.CONTENT_URI.buildUpon()
				.appendPath(sessionId + "").build(),
				ConferenceCP.Sessions.TYPE_NAME, null, null);
	}

	public long[] getEventsForSessionId(long sessionId) {
		// SELECT _id FROM events WHERE sessionId = ?
		return getArrayOfIdsWithQuery(ConferenceCP.Events.CONTENT_URI,
				BaseColumns._ID, ConferenceCP.Events.SESSION_ID_NAME + " = ?",
				new String[] { "" + sessionId }, ConferenceCP.Events._ID
						+ " ASC");
	}

	public String getEventTitleForEventId(long eventId) {
		// SELECT title FROM events WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Events.CONTENT_URI.buildUpon()
				.appendPath(eventId + "").build(),
				ConferenceCP.Sessions.TITLE_NAME, null, null);
	}

	public long getVenueForEventId(long eventId) {
		// SELECT venueId FROM events WHERE _id = ?
		long result[] = getArrayOfIdsWithQuery(ConferenceCP.Events.CONTENT_URI
				.buildUpon().appendPath(eventId + "").build(),
				ConferenceCP.Events.VENUE_ID_NAME, null, null, null);

		return result != null ? result[0] : -1;
	}

	public long[] getTalksForEventId(long eventId) {
		// SELECT _id FROM talks WHERE eventId = ? ORDER BY eventId
		return getArrayOfIdsWithQuery(ConferenceCP.Talks.CONTENT_URI,
				BaseColumns._ID, ConferenceCP.Talks.EVENT_ID_NAME + " = ?",
				new String[] { "" + eventId }, ConferenceCP.Talks._ID + " ASC");
	}

	public String getTalkTitleForTalkId(long talkId) {
		// SELECT title FROM talks WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Talks.CONTENT_URI.buildUpon()
				.appendPath(talkId + "").build(),
				ConferenceCP.Talks.TITLE_NAME, null, null);
	}

	public String getSpeakerForTalkId(long talkId) {
		// SELECT speaker FROM talks WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Talks.CONTENT_URI.buildUpon()
				.appendPath(talkId + "").build(),
				ConferenceCP.Talks.SPEAKER_NAME, null, null);
	}

	public String getSpeakerPictureFileForTalkId(long talkId) {
		// SELECT image FROM talks WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Talks.CONTENT_URI.buildUpon()
				.appendPath(talkId + "").build(),
				ConferenceCP.Talks.IMAGE_NAME, null, null);
	}

	public String getTalkDescriptionFileForTalkId(long talkId) {
		// SELECT description FROM talks WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Talks.CONTENT_URI.buildUpon()
				.appendPath(talkId + "").build(),
				ConferenceCP.Talks.DESCRIPTION_NAME, null, null);
	}

	public String getNameForVenueId(long venueId) {
		// SELECT name FROM venues WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Venues.CONTENT_URI.buildUpon()
				.appendPath(venueId + "").build(),
				ConferenceCP.Venues.NAME_NAME, null, null);
	}

	public String getLongitudeForVenueId(long venueId) {
		// SELECT longitude FROM venues WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Venues.CONTENT_URI.buildUpon()
				.appendPath(venueId + "").build(),
				ConferenceCP.Venues.LONGITUDE_NAME, null, null);
	}

	public String getLatitudeForVenueId(long venueId) {
		// SELECT latitude FROM venues WHERE _id = ?
		return getStringWithQuery(ConferenceCP.Venues.CONTENT_URI.buildUpon()
				.appendPath(venueId + "").build(),
				ConferenceCP.Venues.LATITUDE_NAME, null, null);
	}

	public Cursor getSessionsCursorForDay(long dayId) {

		String where = ConferenceCP.Sessions.DAY_ID_NAME + " = ?";
		String sortOrder = ConferenceCP.Sessions.START_TIME_NAME + " ASC";
		Cursor cursor = callerContentResolver.query(
				ConferenceCP.Sessions.CONTENT_URI, null, where,
				new String[] { "" + dayId }, sortOrder);
		return cursor;
	}

	public Cursor getSession(long id) throws DBAccessException {
		return this.getDBObject(id, new ConferenceCP.Sessions());
	}
	
	public Cursor getDay(long id) throws DBAccessException {
		return this.getDBObject(id, new ConferenceCP.Days());
	}

	public Cursor getVenue(long id) throws DBAccessException {
		return this.getDBObject(id, new ConferenceCP.Venues());
	}

	public Cursor getEvent(long id) throws DBAccessException {
		return this.getDBObject(id, new ConferenceCP.Events());
	}

	public Cursor getEventVenue(long id) throws DBAccessException {
		return this.getDBObject(id, new ConferenceCP.EventsVenue());
	}

	public Cursor getTalks(long id) throws DBAccessException {
		return this.getDBObject(id, new ConferenceCP.Talks());
	}

	/**
	 * Gets a cursor based on a {@link Descriptor}.
	 * 
	 * @param id
	 *            The ID of the object
	 * @param d
	 *            The Descriptor.
	 * @return A cursor pointing to the one and only object.
	 * @throws DBAccessException
	 *             If there are no objects returned, or more than one.
	 */
	private Cursor getDBObject(long id, Descriptor d) throws DBAccessException {
		String where = BaseColumns._ID + " = ?";
		Cursor cursor = this.callerContentResolver.query(d.contentURI(), null,
				where, new String[] { Long.toString(id) }, null);

		if (cursor.getCount() != 1) {
			throw new DBAccessException(
					"Query resulted in %d results, but expected %d.",
					cursor.getCount(), 1);
		}

		cursor.moveToNext();
		return cursor;
	}

	private long[] getArrayOfIdsWithQuery(Uri uri, String columnName,
			String where, String[] whereArgs, String sortOrder) {
		long[] result = null;
		Cursor cursor = executeQuery(uri, columnName, where, whereArgs,
				sortOrder);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndex(columnName);
			if (columnIndex != -1) {
				result = new long[cursor.getCount()];
				for (int i = 0; cursor.moveToNext(); i++) {
					result[i] = cursor.getLong(columnIndex);
				}
			}
			cursor.close();
		}
		return result;
	}

	private String getStringWithQuery(Uri uri, String columnName, String where,
			String[] whereArgs) {
		String result = null;

		Cursor cursor = executeQuery(uri, columnName, where, whereArgs, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result = cursor.getString(index);
				}
			}
			cursor.close();
		}
		return result;
	}

	/**
	 * execute a query as a managed query if we have an activity and as an
	 * unmageaged query otherwise
	 * 
	 * @param uri
	 * @param columnName
	 * @param where
	 * @param whereArgs
	 * @param sortOrder
	 * @return a Cursor on the result set
	 */
	private Cursor executeQuery(Uri uri, String columnName, String where,
			String[] whereArgs, String sortOrder) {
		if (activity != null) {
			return activity.managedQuery(uri, new String[] { columnName },
					where, whereArgs, sortOrder);
		} else {
			return callerContentResolver.query(uri,
					new String[] { columnName }, where, whereArgs, sortOrder);
		}
	}

	public static final class DBAccessException extends Exception {
		public DBAccessException(String message, Object... args) {
			super(String.format(message, args));
		}
	}

}

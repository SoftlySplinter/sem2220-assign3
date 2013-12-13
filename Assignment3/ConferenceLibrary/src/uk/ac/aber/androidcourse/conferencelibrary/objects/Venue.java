package uk.ac.aber.androidcourse.conferencelibrary.objects;

import android.database.Cursor;
import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;
import uk.ac.aber.androidcourse.conferencelibrary.DBAccess.DBAccessException;

public final class Venue {
	public static final Venue load(long id, DBAccess access) throws DBAccessException {
		Cursor cursor = access.getVenue(id);
		long _id = cursor.getLong(ConferenceCP.Venues.ID_COLUMN);
		String name = cursor.getString(ConferenceCP.Venues.NAME_COLUMN);
		long longitude = cursor.getLong(ConferenceCP.Venues.LONGITUDE_COLUMN);
		long latitude = cursor.getLong(ConferenceCP.Venues.LATITUDE_COLUMN);
		return new Venue(_id, name, longitude, latitude);
	}
	
	public final long _id;
	public final String name;
	public final long longditude;
	public final long latitude;

	private Venue(final long _id, final String name, final long longditude,
			final long latitude) {
		this._id = _id;
		this.name = name;
		this.longditude = longditude;
		this.latitude = latitude;
	}
}

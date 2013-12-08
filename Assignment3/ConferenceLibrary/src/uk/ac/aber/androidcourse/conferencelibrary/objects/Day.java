package uk.ac.aber.androidcourse.conferencelibrary.objects;

import uk.ac.aber.androidcourse.conferencelibrary.DBAccess;

public final class Day {
	public static final Day load(long id, DBAccess access) {
		return new Day(id, access.getDayForDayId(id), access.getDateForDayId(id));
	}
	
	public final long _id;
	public final String day;
	public final String date;
	
	public Day(long _id, String day, String date) {
		this._id = _id;
		this.day = day;
		this.date = date;
	}
}

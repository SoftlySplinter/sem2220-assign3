package uk.ac.aber.androidcourse.conference;

import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class ConfDBaseActivity extends Activity {
	
	private static final String LOG_TAG = "ConfDBaseActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        displayDatabaseTables();
    }

	private void displayDatabaseTables() {
		// Simply writes out database content as a debugging aid and
		// ensure we can access the ConferenceContentProvider
		Cursor cursor = managedQuery(ConferenceCP.Days.CONTENT_URI, null, null, null, null);
		if (cursor != null){
			Log.i(LOG_TAG, "Days (" + cursor.getCount() + " entries):");
			while (cursor.moveToNext()){
				Log.i(LOG_TAG, "ID: " + cursor.getLong(ConferenceCP.Days.ID_COLUMN) + "\n" +
						       "Day: " + cursor.getString(ConferenceCP.Days.DAY_COLUMN) + "\n" +
						       "Date/Time: " + cursor.getString(ConferenceCP.Days.DATE_TIME_COLUMN));
			}
			cursor.close();
		}
		cursor = managedQuery(ConferenceCP.Venues.CONTENT_URI, null, null, null, null);
		
		if (cursor != null){
			Log.i(LOG_TAG, "Venues (" + cursor.getCount() + " entries):");
			while (cursor.moveToNext()){
				Log.i(LOG_TAG, "ID: " + cursor.getLong(ConferenceCP.Venues.ID_COLUMN) + "\n" +
						       "Name: " + cursor.getString(ConferenceCP.Venues.NAME_COLUMN) + "\n" +
						       "Latitude: " + cursor.getString(ConferenceCP.Venues.LATITUDE_COLUMN) + "\n" +
						       "Longitude: " + cursor.getString(ConferenceCP.Venues.LONGITUDE_COLUMN) + "\n");
			}
			cursor.close();
		}
		cursor = managedQuery(ConferenceCP.Sessions.CONTENT_URI, null, null, null, null);
		if (cursor != null){
			Log.i(LOG_TAG, "Sessions (" + cursor.getCount() + " entries):");
			while (cursor.moveToNext()){
				Log.i(LOG_TAG, "ID: " + cursor.getString(ConferenceCP.Sessions.ID_COLUMN) + "\n" +
						       "Title: " + cursor.getString(ConferenceCP.Sessions.TITLE_COLUMN) + "\n" +
						       "Start time: " + cursor.getString(ConferenceCP.Sessions.START_TIME_COLUMN) + "\n" +
						       "End time: " + cursor.getString(ConferenceCP.Sessions.END_TIME_COLUMN) + "\n" +
						       "Type: " + cursor.getString(ConferenceCP.Sessions.TYPE_COLUMN) + "\n" +
						       "Day id: " + cursor.getString(ConferenceCP.Sessions.DAY_ID_COLUMN));
			}
			cursor.close();
		}
		
		cursor = managedQuery(ConferenceCP.Events.CONTENT_URI, null, null, null, null);
		if (cursor != null){
			Log.i(LOG_TAG, "Events (" + cursor.getCount() + " entries):");
			while (cursor.moveToNext()){
				Log.i(LOG_TAG, "ID: " + cursor.getLong(ConferenceCP.Events.ID_COLUMN) + "\n" +
						       "Title: " + cursor.getString(ConferenceCP.Events.TITLE_COLUMN) + "\n" +
						       "Venue id: " + cursor.getLong(ConferenceCP.Events.VENUE_ID_COLUMN) + "\n" +
						       "Session id: " + cursor.getLong(ConferenceCP.Events.SESSION_ID_COLUMN));
			}
			cursor.close();
		}
		cursor = managedQuery(ConferenceCP.Talks.CONTENT_URI, null, null, null, null);
		if (cursor != null){
			Log.i(LOG_TAG, "Talks (" + cursor.getCount() + " entries):");
			while (cursor.moveToNext()){
				Log.i(LOG_TAG, "ID: " + cursor.getLong(ConferenceCP.Talks.ID_COLUMN) + "\n" +
						       "Title: " + cursor.getString(ConferenceCP.Talks.TITLE_COLUMN) + "\n" +
						       "Speaker: " + cursor.getString(ConferenceCP.Talks.SPEAKER_COLUMN) + "\n" +
						       "Image URL: " + cursor.getString(ConferenceCP.Talks.IMAGE_COLUMN) + "\n" +
						       "Description: " + cursor.getString(ConferenceCP.Talks.DESCRIPTION_COLUMN) + "\n" +
						       "Event id: " + cursor.getLong(ConferenceCP.Talks.EVENT_ID_COLUMN));
			}
			cursor.close();	
		}
		
		// Try out the join table
		
		cursor = managedQuery(ConferenceCP.EventsVenue.CONTENT_URI, null, null, null, null);
		if (cursor != null){
			Log.i(LOG_TAG, "Events with venue (" + cursor.getCount() + " entries):");
			while (cursor.moveToNext()){
				Log.i(LOG_TAG, "ID: " + cursor.getLong(cursor.getColumnIndex(ConferenceCP.EventsVenue._ID)));
				Log.i(LOG_TAG, "Title: " + cursor.getString(cursor.getColumnIndex(ConferenceCP.EventsVenue.TITLE_NAME)));
				Log.i(LOG_TAG, "Venue id: " + cursor.getLong(cursor.getColumnIndex(ConferenceCP.EventsVenue.VENUE_ID_NAME)));
				Log.i(LOG_TAG, "Session id: " + cursor.getLong(cursor.getColumnIndex(ConferenceCP.EventsVenue.SESSION_ID_NAME)));
				Log.i(LOG_TAG, "Venue name: " + cursor.getString(cursor.getColumnIndex(ConferenceCP.EventsVenue.VENUE_NAME_NAME)));				
			}
			cursor.close();	
		}
		
		// Try deleting something and then reinserting it
		
	}
}
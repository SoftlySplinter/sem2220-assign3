package uk.ac.aber.androidcourse.conference.provider;

import java.util.HashMap;
import uk.ac.aber.androidcourse.conferencelibrary.ConferenceCP;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content provider implementation for the Conference application. There are six
 * resources managed by this content provider. These are defined in the public
 * ConferenceLibrary project and its ConferenceCP class. In particular the resource URIs are defined
 * and mime types are defined in ConferenceCP.
 * 
 * @author Created by Chris Loftus in December 2011. Copyright 2011 Aberystwyth University. All rights reserved.
 *
 */

public class ConferenceContentProvider extends ContentProvider {
	
	// The Authority is the unique part of any content provider URI. Cf ConferenceCP
	private static final String AUTHORITY = ConferenceCP.AUTHORITY; 
	
	// The name of the SQLite database
	private static final String CONFERENCE_CP = "ConfDBase";
	
	// UriMatcher allows us to map URIs on to integer values. This is useful later
	// when we decide how to react to an incoming URI-based request: e.g. to find the
	// corresponding integer identifier that drives a switch statement 
	private static UriMatcher sUriMatcher;
	
	// The ids used when mapping URIs
	private static final int DAYS = 1;
	private static final int DAYS_ID = 2;
	private static final int SESSIONS = 3;
	private static final int SESSIONS_ID = 4;
	private static final int EVENTS = 5;
	private static final int EVENTS_ID = 6;
	private static final int TALKS = 7;
	private static final int TALKS_ID = 8;
	private static final int VENUES = 9;
	private static final int VENUES_ID = 10;
	private static final int NOTES = 11;
	private static final int NOTES_ID = 12;
	private static final int EVENTS_VENUE = 13;
	private static final int EVENTS_VENUE_ID = 14;

	// By default the projection column names specified by a client when
	// making a query will map to the actual column names used
	// within the underlying database. For security reasons we
	// may wish to map client-provided column names to database
	// specific values. We map also want to qualify columns with
	// table names where table joins are being performed.
	// The following HashMaps can be used for this purpose. One is defined
	// for each result table.
    private static HashMap<String, String> daysProjectionMap;
    private static HashMap<String, String> eventsProjectionMap;
    private static HashMap<String, String> sessionsProjectionMap;
    private static HashMap<String, String> talksProjectionMap;
    private static HashMap<String, String> venuesProjectionMap;
    private static HashMap<String, String> notesProjectionMap;
    private static HashMap<String, String> eventVenuesProjectionMap; // For join table
    
    static final String LOG_TAG = "ConferenceContentProvider";
    
    // The actual table names used in the database. Currently
    // these are the same as the incoming URIs, e.g. .../days/1
    // but for security reasons in a real application it would
    // be wise to use a different set of names.
    private static final String DAYS_TABLE_NAME = "days";
    private static final String EVENTS_TABLE_NAME = "events";
    private static final String SESSIONS_TABLE_NAME = "sessions";
    private static final String TALKS_TABLE_NAME = "talks";
    private static final String NOTES_TABLE_NAME = "notes";
    private static final String VENUES_TABLE_NAME = "venues";

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        sUriMatcher.addURI(AUTHORITY, ConferenceCP.Days.DAYS, DAYS);
        // Use of the hash character indicates matching of an id
        sUriMatcher.addURI(AUTHORITY,
        		ConferenceCP.Days.DAYS + "/#", DAYS_ID);
        
        sUriMatcher.addURI(AUTHORITY, ConferenceCP.Sessions.SESSIONS, SESSIONS);
        sUriMatcher.addURI(AUTHORITY,
        		ConferenceCP.Sessions.SESSIONS + "/#", SESSIONS_ID);
        
        sUriMatcher.addURI(AUTHORITY, ConferenceCP.Events.EVENTS, EVENTS);
        sUriMatcher.addURI(AUTHORITY,
        		ConferenceCP.Events.EVENTS + "/#", EVENTS_ID);
        
        sUriMatcher.addURI(AUTHORITY, ConferenceCP.Talks.TALKS, TALKS);
        sUriMatcher.addURI(AUTHORITY,
        		ConferenceCP.Talks.TALKS + "/#", TALKS_ID);
        
        sUriMatcher.addURI(AUTHORITY, ConferenceCP.Venues.VENUES, VENUES);
        sUriMatcher.addURI(AUTHORITY,
        		ConferenceCP.Venues.VENUES + "/#", VENUES_ID);
        
        sUriMatcher.addURI(AUTHORITY, ConferenceCP.EventsVenue.EVENTS_VENUE, EVENTS_VENUE);
        sUriMatcher.addURI(AUTHORITY,
        		ConferenceCP.EventsVenue.EVENTS_VENUE + "/#", EVENTS_VENUE_ID);

        // Example projection maps.
        // Is used (using a SQLiteQueryBuilder) to allow a different set of table column names from the ones
        // the client application can see. 
        daysProjectionMap = new HashMap<String, String>();
        daysProjectionMap.put(BaseColumns._ID,
                BaseColumns._ID);
        daysProjectionMap.put(ConferenceCP.Days.DAY_NAME,
        		ConferenceCP.Days.DAY_NAME);
        daysProjectionMap.put(ConferenceCP.Days.DATE_NAME,
        		ConferenceCP.Days.DATE_NAME);
        
        eventsProjectionMap = new HashMap<String, String>();
        eventsProjectionMap.put(BaseColumns._ID,
                BaseColumns._ID);
        eventsProjectionMap.put(ConferenceCP.Events.TITLE_NAME,
        		ConferenceCP.Events.TITLE_NAME);
        eventsProjectionMap.put(ConferenceCP.Events.VENUE_ID_NAME,
        		ConferenceCP.Events.VENUE_ID_NAME);
        eventsProjectionMap.put(ConferenceCP.Events.SESSION_ID_NAME,
        		ConferenceCP.Events.SESSION_ID_NAME);
        
        sessionsProjectionMap = new HashMap<String, String>();
        sessionsProjectionMap.put(BaseColumns._ID,
                BaseColumns._ID);
        sessionsProjectionMap.put(ConferenceCP.Sessions.TITLE_NAME,
        		ConferenceCP.Sessions.TITLE_NAME);
        sessionsProjectionMap.put(ConferenceCP.Sessions.START_TIME_NAME,
        		ConferenceCP.Sessions.START_TIME_NAME);
        sessionsProjectionMap.put(ConferenceCP.Sessions.END_TIME_NAME,
        		ConferenceCP.Sessions.END_TIME_NAME);
        sessionsProjectionMap.put(ConferenceCP.Sessions.TYPE_NAME,
        		ConferenceCP.Sessions.TYPE_NAME);
        sessionsProjectionMap.put(ConferenceCP.Sessions.DAY_ID_NAME,
        		ConferenceCP.Sessions.DAY_ID_NAME);
        
        talksProjectionMap = new HashMap<String, String>();
        talksProjectionMap.put(BaseColumns._ID,
                BaseColumns._ID);
        talksProjectionMap.put(ConferenceCP.Talks.TITLE_NAME,
        		ConferenceCP.Talks.TITLE_NAME);
        talksProjectionMap.put(ConferenceCP.Talks.SPEAKER_NAME,
        		ConferenceCP.Talks.SPEAKER_NAME);
        talksProjectionMap.put(ConferenceCP.Talks.IMAGE_NAME,
        		ConferenceCP.Talks.IMAGE_NAME);
        talksProjectionMap.put(ConferenceCP.Talks.DESCRIPTION_NAME,
        		ConferenceCP.Talks.DESCRIPTION_NAME);
        talksProjectionMap.put(ConferenceCP.Talks.EVENT_ID_NAME,
        		ConferenceCP.Talks.EVENT_ID_NAME);
        
        venuesProjectionMap = new HashMap<String, String>();
        venuesProjectionMap.put(BaseColumns._ID,
                BaseColumns._ID);
        venuesProjectionMap.put(ConferenceCP.Venues.NAME_NAME,
        		ConferenceCP.Venues.NAME_NAME);
        venuesProjectionMap.put(ConferenceCP.Venues.LATITUDE_NAME,
        		ConferenceCP.Venues.LATITUDE_NAME);
        venuesProjectionMap.put(ConferenceCP.Venues.LONGITUDE_NAME,
        		ConferenceCP.Venues.LONGITUDE_NAME);
        
        notesProjectionMap = new HashMap<String, String>();
        notesProjectionMap.put(BaseColumns._ID,
                BaseColumns._ID);
       
        
        // The projection map for the join table is more complex. We need to make sure that
        // we map incoming column names to their fully qualified form (table.column) and make sure
        // the result columns have distinct names as defined in EventsVenue. We use the "AS" cluase to
        // achieve this. The _id column is more complex since predefined adapter classes look for the _id 
        // column being present in the result. We only map the events _id and don't handle venues _id.
        eventVenuesProjectionMap = new HashMap<String, String>();
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue._ID,
        		EVENTS_TABLE_NAME + '.' + BaseColumns._ID);
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue.TITLE_NAME,
        		EVENTS_TABLE_NAME + '.' + ConferenceCP.Events.TITLE_NAME + 
        		" AS " + ConferenceCP.EventsVenue.TITLE_NAME);
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue.VENUE_ID_NAME,
        		EVENTS_TABLE_NAME + '.' + ConferenceCP.Events.VENUE_ID_NAME + 
        		" AS " + ConferenceCP.EventsVenue.VENUE_ID_NAME);
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue.SESSION_ID_NAME,
        		EVENTS_TABLE_NAME + '.' + ConferenceCP.Events.SESSION_ID_NAME + 
        		" AS " + ConferenceCP.EventsVenue.SESSION_ID_NAME);
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue.VENUE_NAME_NAME,
        		VENUES_TABLE_NAME + '.' + ConferenceCP.Venues.NAME_NAME + 
        		" AS " + ConferenceCP.EventsVenue.VENUE_NAME_NAME);
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue.VENUE_LATITUDE_NAME,
        		VENUES_TABLE_NAME + '.' + ConferenceCP.Venues.LATITUDE_NAME + 
        		" AS " + ConferenceCP.EventsVenue.VENUE_LATITUDE_NAME);
        eventVenuesProjectionMap.put(ConferenceCP.EventsVenue.VENUE_LONGITUDE_NAME,
        		VENUES_TABLE_NAME + '.' + ConferenceCP.Venues.LONGITUDE_NAME + 
        		" AS " + ConferenceCP.EventsVenue.VENUE_LONGITUDE_NAME);
        
    }
      
    // Sometimes we need to support a resource that derives data from
    // several tables. The ConferenceCP.EventsVenue public class enables
    // clients to request both event and corresponding venue information:
    // i.e. the name of the venue. However to implement this we need to define
    // a join query to create a join table result:
    private static final String EVENTS_VENUE_JOIN_TABLE_NAME = 
    	EVENTS_TABLE_NAME + " JOIN " + VENUES_TABLE_NAME + " ON (" + 
    	ConferenceCP.Events.VENUE_ID_NAME + " = " +
    	VENUES_TABLE_NAME + "._id)";

    // The actual database name also requires the appropriate extension:
    private static final String DATABASE_NAME = CONFERENCE_CP + ".db";
    
    // This is important. When an application is installed on a device, if this version
    // number has changed since last time, then the onUpgrade method is called (see below).
    // Otherwise, if this app is being installed for the first time then onCreate is called.
    // Otherwise, neither onCreate nor onUpgrade are called.
    static int DATABASE_VERSION = 1;
	
    // An SQLiteOpenHelper that makes handling SQL requests easier and supports
    // useful lifecycle methods that we can use or override such as onUpgrade (see below)
    private ConferenceDbHelper mOpenDbHelper;

    // The open database 
    private SQLiteDatabase mDb;
    
    private static class ConferenceDbHelper extends SQLiteOpenHelper {

    	private ConferenceDbHelper(Context context, String name,
                SQLiteDatabase.CursorFactory factory)
    	{
    		super(context, name, factory, DATABASE_VERSION);
		}

    	/**
         * Called when application starts for the first time. 
         */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(LOG_TAG, "ConferenceDbHelper.onCreate");
            createTable(db);
            // Populate with some test data
            populate(db);
		}

		/**
         * Only called when the database version changes and allows one to modify an
         * existing database for the old version. In this case we drop the tables and
         * create a new ones. This is often not a good approach since the user loses their
         * valuable data. A better approach would be to apply a script to modify the
         * existing data to reflect the changes schema.
         */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(LOG_TAG, "ConferenceDbHelper.onUpgrade");
			
			db.execSQL("DROP TABLE IF EXISTS " +
                    TALKS_TABLE_NAME + ";");
			db.execSQL("DROP TABLE IF EXISTS " +
                    EVENTS_TABLE_NAME + ";");
			db.execSQL("DROP TABLE IF EXISTS " +
                    VENUES_TABLE_NAME + ";");
			db.execSQL("DROP TABLE IF EXISTS " +
                    SESSIONS_TABLE_NAME + ";");
			db.execSQL("DROP TABLE IF EXISTS " +
                    DAYS_TABLE_NAME + ";");
			db.execSQL("DROP TABLE IF EXISTS " +
                    NOTES_TABLE_NAME + ";");
            createTable(db);
            // Populate with some test data
            populate(db);
		}
		
		private void createTable(SQLiteDatabase sqLiteDatabase) {
			// Create an SQL query to create a new table. Use  
			// table name and column constants. The one problem with
			// this approach is that it doesn't use any projection mapping
			// names but rather uses the publicly defined column names
			// from ConferenceCP. We could use a SQLiteQueryBuilder (and associated a projection map) 
			// instead to generate the query string if this is a concern.
            String qs = "CREATE TABLE " + DAYS_TABLE_NAME + " (" +
                    BaseColumns._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ConferenceCP.Days.DAY_NAME + " TEXT NOT NULL, " +
                    ConferenceCP.Days.DATE_NAME + " TEXT);";
            sqLiteDatabase.execSQL(qs);
            
            qs = "CREATE TABLE " + TALKS_TABLE_NAME + " (" +
            		BaseColumns._ID +
		            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		            ConferenceCP.Talks.TITLE_NAME + " TEXT NOT NULL, " +
		            ConferenceCP.Talks.SPEAKER_NAME + " TEXT, " +
		            ConferenceCP.Talks.IMAGE_NAME + " TEXT, " +
		            ConferenceCP.Talks.DESCRIPTION_NAME + " TEXT, " +
		            ConferenceCP.Talks.EVENT_ID_NAME + " INTEGER NOT NULL);";
            sqLiteDatabase.execSQL(qs);
            
            qs = "CREATE TABLE " + VENUES_TABLE_NAME + " (" +
		    		BaseColumns._ID +
		            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		            ConferenceCP.Venues.NAME_NAME + " TEXT NOT NULL, " +
		            ConferenceCP.Venues.LATITUDE_NAME + " TEXT, " +
		            ConferenceCP.Venues.LONGITUDE_NAME + " TEXT);";
            sqLiteDatabase.execSQL(qs);
            
            qs = "CREATE TABLE " + SESSIONS_TABLE_NAME + " (" +
		    		BaseColumns._ID +
		            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		            ConferenceCP.Sessions.TITLE_NAME + " TEXT NOT NULL, " +
		            ConferenceCP.Sessions.START_TIME_NAME + " TEXT, " +
		            ConferenceCP.Sessions.END_TIME_NAME + " TEXT, " +
		            ConferenceCP.Sessions.TYPE_NAME + " TEXT, " +
		            ConferenceCP.Sessions.DAY_ID_NAME + " INTEGER NOT NULL);";
            sqLiteDatabase.execSQL(qs);
            
            qs = "CREATE TABLE " + EVENTS_TABLE_NAME + " (" +
		    		BaseColumns._ID +
		            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		            ConferenceCP.Events.TITLE_NAME + " TEXT NOT NULL, " +
		            ConferenceCP.Events.VENUE_ID_NAME + " INTEGER NOT NULL, " +
		            ConferenceCP.Events.SESSION_ID_NAME + " INTEGER NOT NULL);";
            sqLiteDatabase.execSQL(qs);    
            
 
		}
		
		private void populate(SQLiteDatabase sqLiteDatabase){
			
			// In our simple implementation we hard-code the data as sample data. In a real app the
			// data could be provided via an external server and then cached locally within the database.
			String [] daysInsert = {
					"insert into "+ DAYS_TABLE_NAME +" ("+BaseColumns._ID+", day, date) values (1,	'Wednesday',	'8th Sept');",
					"insert into "+ DAYS_TABLE_NAME +" ("+BaseColumns._ID+", day, date) values (2,	'Thursday',	'9th Sept' );",
					"insert into "+ DAYS_TABLE_NAME +" ("+BaseColumns._ID+", day, date) values (3,	'Friday',	'10th Sept' );"
			};
			
			insertRows(daysInsert, sqLiteDatabase);
						
			String [] sessionsInsert = {
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (1,  'OPENING CEREMONY',     '09:00', '09:30', 'Social',        1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (2,  'Keynote Address',      '09:30', '10:30',        'Keynote',       1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (3,  'COFFEE BREAK', '10:30',        '11:00',        'Social',        1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (4,  'TECHNICAL SESSIONS',   '11:00',        '12:30',        'Technical',     1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (5,  'LUNCH',        '12:30',        '14:00',        'Social',        1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (6,  'TECHNICAL SESSIONS',   '14:00',        '15:30',        'Technical',     1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (7,  'TEA BREAK',    '15:30',        '16:00',        'Social',        1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (8,  'TECHNICAL SESSIONS',   '16:00',        '17:30',        'Technical',     1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (9,  'Computers and Thought Award Lecture',  '18:00',        '19:00',        'Technical',     1);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (10, 'Invited Speaker',      '09:00', '10:30',        'Keynote',       2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (11, 'COFFEE BREAK', '10:30',        '11:00',        'Social',        2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (12, 'TECHNICAL SESSIONS',   '11:00',        '12:30',        'Technical',     2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (13, 'LUNCH',        '12:30',        '14:00',        'Social',        2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (14, 'TECHNICAL SESSIONS',   '14:00',        '15:30',        'Technical',     2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (15, 'TEA BREAK',    '15:30',        '16:00',        'Social',        2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (16, 'TECHNICAL SESSIONS',   '16:00',        '17:30',        'Technical',     2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (17, 'CONFERENCE BANQUET',   '19:00',        '22:00',        'Social',        2);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (18, 'Invited Talk', '09:00', '10:30',        'Keynote',       3);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (19, 'COFFEE BREAK', '10:30',        '11:00',        'Social',        3);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (20, 'TECHNICAL SESSIONS',   '11:00',        '12:30',        'Technical',     3);",
				"insert into "+ SESSIONS_TABLE_NAME +" ("+BaseColumns._ID+", title, startTime, endtime, type, dayId) values (21, 'CLOSING CEREMONY',     '12:30',        '13:00',        'Social',        3);"
			};
			
			insertRows(sessionsInsert, sqLiteDatabase);
			
			String [] eventsInsert = {
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (1,    'Computer Mediated Transactions',       1,      2);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (2,    'Cognitive and Philosophical Foundations',      2,      4);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (3,    'Performance and Behavior Modeling in Games',   3,      4);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (4,    'Diagnosis and Testing',        4,      4);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (5,    'Social Choice: Manipulation',  2,      6);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (6,    'Search in Games',      3,      6);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (7,    'Plan Recognition',     4,      6);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (8,    'Online Games', 2,      8);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (9,    'Model-Based Diagnosis and Applications',       3,      8);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (10,   'Word Sense Disambiguation',    4,      8);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (11,   'STAIR: The STanford Artificial Intelligence Robot Project',    1,      9);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (12,   'Embodied Language Games with Autonomous Robots',       1,      10);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (13,   'Robotics: Multirobot Planning',        2,      12);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (14,   'Search And Learning',  3,      12);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (15,   'Multiagent Resource Allocation',       4,      12);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (16,   'HTN Planning', 2,      14);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (17,   'Coalitional Games',    3,      14);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (18,   'Unsupervised Learning',        4,      14);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (19,   'Heuristic Search',     2,      16);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (20,   'Logic Programming',    3,      16);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (21,   'Mechanism Design',     4,      16);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (22,   'Intelligent Tutoring Systems: New Challenges and Directions',  1,      18);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (23,   'Social Choice: Voting',        2,      20);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (24,   'Optimal Planning',     3,      20);",
				"insert into "+ EVENTS_TABLE_NAME +" ("+BaseColumns._ID+", title, venueId, sessionId) values (25,   'Metric Learning',      4,      20);"
			};
			
			insertRows(eventsInsert, sqLiteDatabase);
			
			String [] talksInsert = {
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (1,	'Computer Mediated Transactions',	'Hal R. Varian',	'HalVarian',	'HalVarian', 1);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (2,	'Is It Enough to Get the Behavior Right? ',	'Hector Levesque',	' ',	' ', 	2);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (3,	'A Logic for reasoning about Counterfactual Emotions ',	'Emiliano Lorini',	' ',	' ', 	2);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (4,	'Towards Context Aware Emotional Intelligence in Machines',	'Michal Ptaszynski',	' ',	' ', 	2);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (5,	'Modeling Agents through Bounded Rationality Theories ',	'Avi Rosenfeld',	' ',	' ', 	2);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (6,	'Analysis of a Winning Computational Billiards Player',	'Chris Archibald',	' ',	' ', 	3);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (7,	'Acquiring Agent-Based Models of Conflict from Event Data ',	'Glenn Taylor',	' ',	' ', 	3);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (8,	'Using Reasoning Patterns to Help Humans Solve Complex Games',	'Dimitrios Antos',	' ',	' ', 	3);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (9,	'A New Bayesian Approach to Multiple Intermittent Fault Diagnosis',	'Rui Abreu',	'  ',	' ', 	4);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (10,	'Diagnosing Multiple Persistent and Intermittent Faults',	'Johan de Kleer',	' ',	' ', 	4);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (11,	'FRACTAL: Efficient Fault Isolation Using Active Testing ',	'Alexander Feldman',	' ',	' ', 	4);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (12,	'Complexity of Unweighted Coalitional Manipulation under Some Common Voting Rules',	'Lirong Xia',	' ',	' ', 	5);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (13,	'How Hard Is It to Control Sequential Elections via the Agenda?',	'Vincent Conitzer',	' ',	' ', 	5);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (14,	'Multimode Control Attacks on Elections',	'Piotr Faliszewski',	' ',	' ', 	5);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (15,	'Where Are the Really Hard Manipulation Problems? The Phase Transition in Manipulating the Veto Rule',	'Toby Walsh',	' ',	' ', 	5);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (16,	'Evaluating Strategies for Running from the Cops',	'Carsten Moldenhauer',	' ',	' ', 	6);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (17,	'Improving State Evaluation, Inference, and Search in Trick-Based Card Games',	'Michael Buro',	' ',	' ', 	6);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (18,	'Solving 8x8 Hex',	'Philip Henderson',	' ',	' ', 	6);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (19,	'Probabilistic State Translation in Extensive Games with Large Action Sets',	'David Schnizlein',	' ',	' ', 	6);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (20,	'Abnormal Activity Recognition Based on HDP-HMM',	'Derek Hao Hu',	' ',	' ', 	7);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (21,	'Activity Recognition with Intended Actions',	'Alfredo Gabaldon',	' ',	' ', 	7);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (22,	'Plan Recognition as Planning',	'Miguel Ramirez',	' ',	' ', 	7);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (23,	'Delaying Commitment in Plan Recognition Using Combinatory Categorial Grammars',	'Chris Geib',	' ',	' ', 	7);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (24,	'Wikispeedia: An Online Game for Inferring Semantic Distances between Concepts',	'Robert West',	' ',	' ', 	8);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (25,	'On the Tip of My Thought: Playing the Guillotine Game',	'Giovanni Semeraro',	' ',	' ', 	8);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (26,	'Streamlining Attacks on CAPTCHAs with a Computer Game',	'Jeff Yan',	' ',	' ', 	8);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (27,	'Evaluating Abductive Hypotheses using an EM Algorithm on BDDs',	'Katsumi Inoue',	' ',	' ', 	9);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (28,	'Solving Strong-Fault Diagnostic Models by Model Relaxation ',	'Alex Feldman',	' ',	' ', 	9);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (29,	'Plausible Repairs for Inconsistent Requirements',	'Alex Felfernig',	' ',	' ', 	9);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (30,	'Knowledge-Based WSD and Specific Domains',	'Eneko Agirre',	' ',	' ', 	10);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (31,	'Word Sense Disambiguation for All Words without Hard Labor',	'Zhi Zhong',	' ',	' ', 	10);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (32,	'Web-Scale N-gram Models for Lexical Disambiguation',	'Shane Bergsma',	' ',	' ', 	10);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (33,	'STAIR: The STanford Artificial Intelligence Robot Project',	'Andrew Y. Ng ',	'AndrewNg',	'AndrewNg', 11);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (34,	'Embodied Language Games with Autonomous Robots',	'Luc Steels ',	'LucSteels',	'LucSteels', 12);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (35,	'Adversarial Uncertainty in Multi-Robot Patrol',	'Noa Agmon',	' ',	' ', 	13);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (36,	'Algorithms and Complexity Results for Pursuit-Evasion Problems',	'Richard Borie',	' ',	' ', 	13);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (37,	'Tractable Multi-Agent Path Planning on Grid Maps',	'Ko-Hsin Cindy Wang',	' ',	' ', 	13);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (38,	'A Distributed Control Loop for Autonomous Recovery in a Multi-Agent Plan',	'Roberto Micalizio',	' ',	' ', 	13);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (39,	'Efficient Dominant Point Algorithms for the MLCS Problem',	'Qingguo Wang',	' ',	' ', 	14);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (40,	'Search Strategies for an Anytime Usage of the Branch and Prune Algorithm',	'Raphael Chenouard',	' ',	' ', 	14);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (41,	'Goal-Driven Learning in the GILA Integrated Intelligence Architecture',	'Jainarayan Radhakrishnan',	' ',	' ', 	14);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (42,	'Angluin-Style Learning of NFA',	'Benedikt Bollig',	' ',	' ', 	14);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (43,	'Expressive Power-Based Resource Allocation for Data Centers',	'Benjamin Lubin',	' ',	' ', 	15);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (44,	'Online Stochastic Optimization in the Large: Application to Kidney Exchange',	'Pranjal Awasthi',	' ',	' ', 	15);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (45,	'K-Swaps: Cooperative Negotiation for Solving Task-Allocation Problems',	'Xiaoming Zheng',	' ',	' ', 	15);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (46,	'A Multi-Agent Learning Approach to Online Distributed Resource Allocation',	'Chongjie Zhang',	' ',	' ', 	15);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (47,	'Learning Probabilistic Hierarchical Task Networks to Capture User Preferences',	'Nan Li',	' ',	' ', 	16);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (48,	'Translating HTNs to PDDL',	'Ronald Alford',	' ',	' ', 	16);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (49,	'HTN Planning with Preferences',	'Shirin Sohrabi',	' ',	' ', 	16);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (50,	'Learning HTN Method Preconditions and Action Models from Partial Observations',	'Hankz Hankui Zhuo',	' ',	' ', 	16);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (51,	'On the Complexity of Compact Coalitional Games',	'Gianluigi Greco',	' ',	' ', 	17);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (52,	'Coalitional Affinity Games and the Stability Gap',	'Simina Branzei',	' ',	' ', 	17);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (53,	'Simple Coalitional Games with Beliefs',	'Georgios Chalkiadakis',	' ',	' ', 	17);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (54,	'Coalition Structure Generation in Multi-Agent Systems with Positive and Negative Externalities',	'Talal Rahwan',	' ',	' ', 	17);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (55,	'On the Equivalence between Canonical Correlation Analysis and Orthonormalized Partial Least Squares',	'Liang Sun',	' ',	' ', 	18);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (56,	'Relation Regularized Matrix Factorization ',	'Wu-Jun Li',	' ',	' ', 	18);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (57,	'Spectral Embedded Clustering',	'Feiping Nie',	' ',	' ', 	18);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (58,	'Knowledge Driven Dimension Reduction for Clustering',	'Ian Davidson',	' ',	' ', 	18);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (59,	'Canadian Traveler Problem With Remote Sensing',	'Zahy Bnaya',	' ',	' ', 	19);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (60,	'Minimum Proof Graphs and Fastest-Cut-First Search Heuristic',	'Timothy Furtak',	' ',	' ', 	19);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (61,	'Memory-Based Heuristics for Explicit State Spaces',	'Nathan Sturtevant',	' ',	' ', 	19);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (62,	'Incremental Phi*: Incremental Any-Angle Path Planning on Grids',	'Alex Nash',	' ',	' ', 	19);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (63,	'Local Query Mining in a Probabilistic Prolog',	'Angelika Kimmig',	' ',	' ', 	20);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (64,	'CTPPL: A Continuous Time Probabilistic Programming Language',	'Avi Pfeffer',	' ',	' ', 	20);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (65,	'A Syntax-based Framework for Merging Imprecise Probabilistic Logic Programs',	'Anbu Yue',	' ',	' ', 	20);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (66,	'Next Steps in Propositional Horn Contraction',	'Richard Booth',	' ',	' ', 	20);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (67,	'How Pervasive Is the Myerson-Satterthwaite Impossibility?',	'Abraham Othman',	' ',	' ', 	21);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (68,	'A General Approach to Environment Design with One Agent',	'Haoqi Zhang',	' ',	' ', 	21);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (69,	'Eliciting Honest Reputation Feedback in a Markov Setting',	'Jens Witkowski',	' ',	' ', 	21);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (70,	'Strategyproof Classification with Shared Inputs',	'Reshef Meir',	' ',	' ', 	21);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (71,	'Intelligent Tutoring Systems: New Challenges and Directions',	'Cristina Conati ',	'ChristinaConati',	'ChristinaConati', 22);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (72,	'Finite Local Consistency Characterizes Generalized Scoring Rules',	'Lirong Xia',	' ',	' ', 	23);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (73,	'Nonmanipulable Selections from a Tournament',	'Alon Altman',	' ',	' ', 	23);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (74,	'A Dichotomy Theorem on the Existence of Efficient or Neutral Sequential Voting Correspondences',	'Lirong Xia',	' ',	' ', 	23);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (75,	'A Multivariate Complexity Analysis of Determining Possible Winners Given Incomplete Votes',	'Nadja Betzler',	' ',	' ', 	23);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (76,	'Optimal Symbolic Planning with Action Costs and Preferences',	'Stefan Edelkamp',	' ',	' ', 	24);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (77,	'Planning with Partial Preference Models',	'Tuan A. Nguyen',	' ',	' ', 	24);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (78,	'Completeness and Optimality Preserving Reduction for Planning',	'Yixin Chen',	' ',	' ', 	24);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (79,	'Cost-Optimal Planning with Landmarks',	'Erez Karpas',	' ',	' ', 	24);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (80,	'Robust Distance Metric Learning with Auxiliary Knowledge',	'Zheng-Jun Zha',	' ',	' ', 	25);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (81,	'Non-Metric Label Propagation',	'Yin Zhang',	' ',	' ', 	25);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (82,	'Spectral Kernel Learning for Semi-Supervised Classification',	'Wei Liu',	' ',	' ', 	25);",
				"insert into "+ TALKS_TABLE_NAME +" ("+BaseColumns._ID+", title, speaker, image, description, eventId) values (83,	'Semi-Supervised Metric Learning Using Pairwise Constraints',	'Mahdieh Baghshah',	' ',	' ', 	25);"
			};
			
			insertRows(talksInsert, sqLiteDatabase);
			
			String [] venuesInsert = {
				"insert into "+ VENUES_TABLE_NAME +" ("+BaseColumns._ID+", name, latitude, longitude) values (1,	'Computer Science Reception',	'52.416258', '-4.065607');",
				"insert into "+ VENUES_TABLE_NAME +" ("+BaseColumns._ID+", name, latitude, longitude) values (2,	'Arts Centre',	'52.415574', '-4.063021');",
				"insert into "+ VENUES_TABLE_NAME +" ("+BaseColumns._ID+", name, latitude, longitude) values (3,	'Geography Tower', 	'52.416706', '-4.066612');",
				"insert into "+ VENUES_TABLE_NAME +" ("+BaseColumns._ID+", name, latitude, longitude) values (4,	'Hugh Owen',	'52.415875', '-4.063686');"
			};
			
			insertRows(venuesInsert, sqLiteDatabase);
		}

		private void insertRows(String[] insertStrings,
				SQLiteDatabase sqLiteDatabase) {
			try{
				for (String insertString: insertStrings){
					// Generally avoid using raw SQL, but rather use the insert method etc instead.
					// This is because with "insert" and similar methods we provide the query values
					// as a separate argument from the query syntax, so that the values are checked
					// for SQL meta characters. This is important for incoming requests that may contain
					// malicious SQL injection strings. For setup the raw form is acceptable.
					sqLiteDatabase.execSQL(insertString);
				}	
			}
			catch(SQLException sqle){
				Log.e(LOG_TAG, "Problem inserting setup data: ", sqle);
				throw sqle; // Re-throw the exception so that that application stops
			}
		}
    }
 
    ///////////////////////////////////////////////////////////////
    public ConferenceContentProvider() {
    }

    public ConferenceContentProvider(Context context) {
        init();
    }
    
    /**
     * Called by Android at the start of the content provider's lifecycle. We
     * initialise the database.
     */
    @Override
	public boolean onCreate() {
    	init();
    	
    	// If we get to this point then we assume that the database was created successfully and
    	// so we return true. If there were any problems then an exception will have been thrown.
        return true;
	}
    
    /**
     * Called by Android when there is an incoming delete query
     * @param uri - indicates the resource to delete. This can be
     * either several rows in a resource or a single resource:
     * .../resource enables deletion across a number of rows, with the "where" argument used
     * to narrow the scope of the deletion. .../resource/id identifies a single row in the
     * resource table to delete.
     * @param where - the "where" statement but without the word "where" prepended. Includes
     * if appropriate '?' characters that are substituted by real values from whereArgs
     * @param whereArgs - The values used to substitute into the "where" clause. Must be in same
     * order as '?'s.
     * @return the number of rows deleted.
     */
    @Override
	public int delete(Uri uri, String where, String[] whereArgs) {
    	int affected = 0;
        
    	// Include the URIs for which we want to allow deletion. Here all except the join
    	// table EventsVenue, since that is derived.
		switch (sUriMatcher.match(uri)) {
	        case SESSIONS:          
	        	affected = mDb.delete(SESSIONS_TABLE_NAME, where, whereArgs);
	        	break;
	        case SESSIONS_ID:
	        	// We have some extra work to do before calling mDb.delete so encapsulate
	        	// this in a utility method.
	        	affected = deleteItem(SESSIONS_TABLE_NAME, uri, where, whereArgs);
	        	break;
	        case DAYS:
	        	affected = mDb.delete(DAYS_TABLE_NAME, where, whereArgs);
	        	break;
	        case DAYS_ID:
	        	affected = deleteItem(DAYS_TABLE_NAME, uri, where, whereArgs);
	        	break;
	        case EVENTS:
	        	affected = mDb.delete(EVENTS_TABLE_NAME, where, whereArgs);
	        	break;
	        case EVENTS_ID:
	        	affected = deleteItem(EVENTS_TABLE_NAME, uri, where, whereArgs);
	        	break;
	        case TALKS:
	        	affected = mDb.delete(TALKS_TABLE_NAME, where, whereArgs);
	        	break;
	        case TALKS_ID:
	        	affected = deleteItem(TALKS_TABLE_NAME, uri, where, whereArgs);
	        	break;
	        case NOTES:
	        	affected = mDb.delete(NOTES_TABLE_NAME, where, whereArgs);
	        	break;
	        case NOTES_ID:
	        	affected = deleteItem(NOTES_TABLE_NAME, uri, where, whereArgs);
	        	break;
	        case VENUES:
	        	affected = mDb.delete(VENUES_TABLE_NAME, where, whereArgs);
	        	break;
	        case VENUES_ID:
	        	affected = deleteItem(VENUES_TABLE_NAME, uri, where, whereArgs);
	        	break;
	        default:
	            throw new IllegalArgumentException("Unknown conference URI: " +
	                    uri);
		}
		
		if (affected > 0)
			// The call to notify any observers that the row(s) associated with the URI has been changed (deleted)
	        getContext().getContentResolver().notifyChange(uri, null);
		
		return affected;
	}
    
    // Here we extract the id from the URI and if not empty incorporate
    // the "where" clause into the mDb.delete's "where" clause
	private int deleteItem(String tableName, Uri uri, String where,
			String[] whereArgs) {
		long id = ContentUris.parseId(uri);
        int affected = mDb.delete(tableName,
                BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(where) ?
                        " AND (" + where + ')' : ""),
                whereArgs);
        
        return affected;
	}

	/**
	 * Returns the MIME type for the resource identified by the URI. See ConferenceCP for
	 * an explanation. Can be used by a client to determine the type of data it receives from a
	 * specific URI or indeed sends to a specific URI
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
	        case SESSIONS:
	            return ConferenceCP.Sessions.CONTENT_TYPE;
	        case SESSIONS_ID:
	            return ConferenceCP.Sessions.CONTENT_SESSION_TYPE;
	        
	        case DAYS:
	            return ConferenceCP.Days.CONTENT_TYPE;
	        case DAYS_ID:
	            return ConferenceCP.Days.CONTENT_DAY_TYPE;
	            
	        case EVENTS:
	            return ConferenceCP.Events.CONTENT_TYPE;
	        case EVENTS_ID:
	            return ConferenceCP.Events.CONTENT_EVENT_TYPE;
	            
	        case TALKS:
	            return ConferenceCP.Talks.CONTENT_TYPE;
	        case TALKS_ID:
	            return ConferenceCP.Talks.CONTENT_TALK_TYPE;
	            
	        case VENUES:
	            return ConferenceCP.Venues.CONTENT_TYPE;
	        case VENUES_ID:
	            return ConferenceCP.Venues.CONTENT_VENUE_TYPE;
	            
	        case EVENTS_VENUE:
	            return ConferenceCP.EventsVenue.CONTENT_TYPE;
	        case EVENTS_VENUE_ID:
	        	return ConferenceCP.EventsVenue.CONTENT_EVENT_VENUE_TYPE;
	        
	        default:
	            throw new IllegalArgumentException("Unknown conference URI: " +
	                    uri);
		}
	}
	
	/**
	 * Inserts a new row into the table identified by the URI. 
	 * @param uri - the target resource. It must be a directory request rather than a single
	 * item request, since the unique identifier will be generated automatically.
	 * @param initialValues - the values for the columns as name/value pairs.
	 * @return the URI of the newly created item resource 
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri result = null;
		
		ContentValues values;
        if (initialValues != null) {
        	// Create a duplicate to prevent indirect access to the values during the request.
        	// This is a precaution given that the content provider runs in its own thread.
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
              
        // Only deal with directory style URIs
		switch (sUriMatcher.match(uri)) {
	        case SESSIONS:          
	        	result = insertSession(values);
	        	break;
	        case DAYS:
	        	result = insertDay(values);
	        	break;
	        case EVENTS:
	        	result = insertEvent(values);
	        	break;
	        case TALKS:
	        	result = insertTalk(values);
	        	break;
	        case VENUES:
	        	result = insertVenue(values);
	        	break;
	        default:
	            throw new IllegalArgumentException(uri + " is not support for inserts within the conference content provider");
		}
		
		if (result == null){
			throw new SQLException("Failed to insert row into " + uri);
		}
		return result;
	}
	
	
	private Uri insertVenue(ContentValues values) {
		// Verify values
		if (!values.containsKey(ConferenceCP.Venues.NAME_NAME)) {
        	throw new IllegalArgumentException("Conference venue name not specified: " +
                    values);
        }
		
		// Provide some default values where none provided
		if (!values.containsKey(ConferenceCP.Venues.LATITUDE_NAME)) {
			values.put(ConferenceCP.Venues.LATITUDE_NAME, "");
        }
		if (!values.containsKey(ConferenceCP.Venues.LONGITUDE_NAME)) {
			values.put(ConferenceCP.Venues.LONGITUDE_NAME, "");
        }
		
		// CONTENT_URI will be the same as the URI provided as an argument to insert so we
		// don't need to pass that through as an argument to insertVenue
		return doInsert(VENUES_TABLE_NAME, values, ConferenceCP.Venues.CONTENT_URI);	
	}

	private Uri insertTalk(ContentValues values) {
		// Verify values
		if (!values.containsKey(ConferenceCP.Talks.TITLE_NAME)) {
        	throw new IllegalArgumentException("Conference talk title not specified: " +
                    values);
        }
		if (!values.containsKey(ConferenceCP.Talks.SPEAKER_NAME)) {
			values.put(ConferenceCP.Talks.SPEAKER_NAME, "");
        }
		if (!values.containsKey(ConferenceCP.Talks.IMAGE_NAME)) {
			values.put(ConferenceCP.Talks.IMAGE_NAME, "");
        }
		if (!values.containsKey(ConferenceCP.Talks.DESCRIPTION_NAME)) {
			values.put(ConferenceCP.Talks.DESCRIPTION_NAME, "");
        }
		
		// Check that the event id value exists and if it does whether the event
		// exists
		if (!values.containsKey(ConferenceCP.Talks.EVENT_ID_NAME)) {
        	throw new IllegalArgumentException("Conference talk: no event id specified: " +
                    values);
        }
		Cursor c = mDb.query(EVENTS_TABLE_NAME, null, 
							BaseColumns._ID + " = " + values.getAsLong(ConferenceCP.Talks.EVENT_ID_NAME), 
							null, null, null, null);
		if (c.getCount() == 0){
			throw new IllegalArgumentException("Conference talk: event id specified does not match an event: " +
                    values);
		}
		c.close();
		
		return doInsert(TALKS_TABLE_NAME, values, ConferenceCP.Talks.CONTENT_URI);	
	}

	private Uri insertEvent(ContentValues values) {
		// Verify values
		if (!values.containsKey(ConferenceCP.Events.TITLE_NAME)) {
        	throw new IllegalArgumentException("Conference event title not specified: " +
                    values);
        }
		
		// Check that the venue id and session id values exists and if they do whether the 
		// corresponding venue and session entry exist
		if (!values.containsKey(ConferenceCP.Events.VENUE_ID_NAME)) {
        	throw new IllegalArgumentException("Conference event: no venue id specified: " +
                    values);
        }
		if (!values.containsKey(ConferenceCP.Events.SESSION_ID_NAME)) {
        	throw new IllegalArgumentException("Conference event: no session id specified: " +
                    values);
        }
	
		Cursor c = mDb.query(VENUES_TABLE_NAME, null, 
							BaseColumns._ID + " = " + values.getAsLong(ConferenceCP.Events.VENUE_ID_NAME), 
							null, null, null, null);
		if (c.getCount() == 0){
			throw new IllegalArgumentException("Conference event: venue id specified does not match a venue: " +
                    values);
		}
		c = mDb.query(SESSIONS_TABLE_NAME, null, 
				BaseColumns._ID + " = " + values.getAsLong(ConferenceCP.Events.SESSION_ID_NAME), 
				null, null, null, null);
		if (c.getCount() == 0){
			throw new IllegalArgumentException("Conference event: session id specified does not match a session: " +
					values);
		}
		c.close();
		
		return doInsert(EVENTS_TABLE_NAME, values, ConferenceCP.Events.CONTENT_URI);		
	}

	private Uri insertDay(ContentValues values) {
		// Verify values
		if (!values.containsKey(ConferenceCP.Days.DAY_NAME)) {
        	throw new IllegalArgumentException("Conference day not specified: " +
                    values);
        }
		if (!values.containsKey(ConferenceCP.Days.DATE_NAME)) {
			values.put(ConferenceCP.Days.DATE_NAME, "");
        }
		
		return doInsert(DAYS_TABLE_NAME, values, ConferenceCP.Days.CONTENT_URI);		
	}

	private Uri insertSession(ContentValues values) {
		// Verify values
		if (!values.containsKey(ConferenceCP.Sessions.TITLE_NAME)) {
        	throw new IllegalArgumentException("Conference session title not specified: " +
                    values);
        }
		
		// Check that the day id exists and if so whether the 
		// corresponding day entry exists
		if (!values.containsKey(ConferenceCP.Sessions.DAY_ID_NAME)) {
        	throw new IllegalArgumentException("Conference session: no day id specified: " +
                    values);
        }
		if (!values.containsKey(ConferenceCP.Sessions.START_TIME_NAME)) {
			values.put(ConferenceCP.Sessions.START_TIME_NAME, "");
        }
		if (!values.containsKey(ConferenceCP.Sessions.END_TIME_NAME)) {
			values.put(ConferenceCP.Sessions.END_TIME_NAME, "");
        }
		if (!values.containsKey(ConferenceCP.Sessions.TYPE_NAME)) {
			values.put(ConferenceCP.Sessions.TYPE_NAME, "");
        }
		
		// Does the day entry exist?
		Cursor c = mDb.query(DAYS_TABLE_NAME, null, 
							BaseColumns._ID + " = " + values.getAsLong(ConferenceCP.Sessions.DAY_ID_NAME), 
							null, null, null, null);
		if (c.getCount() == 0){
			throw new IllegalArgumentException("Conference session: day id specified does not match a day: " +
                    values);
		}
		c.close();
		
		return doInsert(SESSIONS_TABLE_NAME, values, ConferenceCP.Sessions.CONTENT_URI);
	}
	
	// Do the actual insert
	private Uri doInsert(String tableName, ContentValues values,
			Uri contentUri) {
		Uri itemUri = null;
		
		// We don't need to use the null column hack (para 2) because we already
		// checked to make sure that data was provided for columns that have a non-null constraint.
		long rowId = mDb.insert(tableName, null, values);
		
		// If a row was inserted then we want to return the URI to
        // the newly created resource
        if (rowId > 0) {
            itemUri = ContentUris.withAppendedId(contentUri, rowId);
            
            // Notify any observers of the change
            getContext().getContentResolver().notifyChange(itemUri, null);
        }

        return itemUri;		
	}

	/**
	 * Handles incoming query calls to a URI.
	 * @param uri - the URI of the target resource
	 * @param projection - the result columns the caller is interested in or null if all columns are of interest
	 * @param where - the "where" clause (without the word "where") or null if there is no "where" clause
	 * @param whereArgs - an array of values that replace '?' place-holders in the "where" clause
	 * @param sortOrder - of the form "column-name [ASC | DESC]{,column-name [ASC | DESC]}"
	 * @return the Cursor created by the query
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String where, String[] whereArgs,
			String sortOrder) {
		        
        int match = sUriMatcher.match(uri);

        Cursor c;

        switch (match) {
            case DAYS:
            	
            	// If no sort order provided use the default one defined in ConferenceCP
            	sortOrder = TextUtils.isEmpty(sortOrder) ? ConferenceCP.Days.DEFAULT_SORT_ORDER : sortOrder;
            	
            	// Queries across multiple items (directory resources) are dealt with differently from those
            	// meant to retrieve a single item (see queryForItem)
            	c = query(DAYS_TABLE_NAME, ConferenceCP.Days.CONTENT_URI, projection, where, whereArgs, sortOrder, daysProjectionMap);
                break;
            case DAYS_ID:
            	c = queryForItem(DAYS_TABLE_NAME, uri, projection, where, whereArgs, daysProjectionMap);
                break;
            case EVENTS:
            	sortOrder = TextUtils.isEmpty(sortOrder) ? ConferenceCP.Events.DEFAULT_SORT_ORDER : sortOrder;
            	c = query(EVENTS_TABLE_NAME, ConferenceCP.Events.CONTENT_URI, projection, where, whereArgs, sortOrder, eventsProjectionMap);
                break;
            case EVENTS_ID:
            	c = queryForItem(EVENTS_TABLE_NAME, uri, projection, where, whereArgs, eventsProjectionMap);
                break;
            case SESSIONS:
            	sortOrder = TextUtils.isEmpty(sortOrder) ? ConferenceCP.Sessions.DEFAULT_SORT_ORDER : sortOrder;
            	c = query(SESSIONS_TABLE_NAME, ConferenceCP.Sessions.CONTENT_URI, projection, where, whereArgs, sortOrder, sessionsProjectionMap);
                break;
            case SESSIONS_ID:
            	c = queryForItem(SESSIONS_TABLE_NAME, uri, projection, where, whereArgs, sessionsProjectionMap);
                break;  
            case TALKS:
            	sortOrder = TextUtils.isEmpty(sortOrder) ? ConferenceCP.Talks.DEFAULT_SORT_ORDER : sortOrder;
            	c = query(TALKS_TABLE_NAME, ConferenceCP.Talks.CONTENT_URI, projection, where, whereArgs, sortOrder, talksProjectionMap);
                break;
            case TALKS_ID:
            	c = queryForItem(TALKS_TABLE_NAME, uri, projection, where, whereArgs, talksProjectionMap);
                break;
            case VENUES:
            	sortOrder = TextUtils.isEmpty(sortOrder) ? ConferenceCP.Venues.DEFAULT_SORT_ORDER : sortOrder;
            	c = query(VENUES_TABLE_NAME, ConferenceCP.Venues.CONTENT_URI, projection, where, whereArgs, sortOrder, venuesProjectionMap);
                break;
            case VENUES_ID:
            	c = queryForItem(VENUES_TABLE_NAME, uri, projection, where, whereArgs, venuesProjectionMap);
                break;
            case NOTES_ID:
            	c = queryForItem(NOTES_TABLE_NAME, uri, projection, where, whereArgs, notesProjectionMap);
                break;
            case EVENTS_VENUE:
            	sortOrder = TextUtils.isEmpty(sortOrder) ? ConferenceCP.Venues.DEFAULT_SORT_ORDER : sortOrder;
            	c = query(EVENTS_VENUE_JOIN_TABLE_NAME, ConferenceCP.EventsVenue.CONTENT_URI, projection, where, whereArgs, sortOrder, eventVenuesProjectionMap);
                break;
            case EVENTS_VENUE_ID:
            	c = queryForItem(EVENTS_VENUE_JOIN_TABLE_NAME, uri, projection, where, whereArgs, eventVenuesProjectionMap);
            	break;
            default:
                throw new IllegalArgumentException("unsupported uri: " + uri);
        }

        return c;
	}

	// Deal with a query for a single item. We allow a where clause to provide further constraints on
	// whether an item is found or not.
	private Cursor queryForItem(String tableName, Uri uri, String[] projection, String where,
			String[] whereArgs, HashMap<String, String> projectionMap) {
		Cursor c;
		
		// A useful helper for building up the query arguments and to automatically
		// apply any project mappings
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName);
		qb.setProjectionMap(projectionMap);
		
		// Extract the id from the URI so that it can be used in the "where" clause
		long id = ContentUris.parseId(uri);
		
		// Append a chunk of "where" clause to the "where" clause provided by the caller. The
		// latter is specified when we call the query method
		qb.appendWhere(BaseColumns._ID + " = " + id);
		
		// Do the query. We don't need to group the rows nor filter by row groups nor define the sort order
		c = qb.query(mDb, projection, where, whereArgs, null, null, null);
		
		// Set the cursor c as an observer on the uri. If it changes the cursor will be notified. Only do this
		// if an item resource based on the uri and where clause was actually found.
		if (c.getCount() > 0)
			c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	// For querying multiple items
	private Cursor query(String tableName, Uri uri, String[] projection, String where, String[] whereArgs,
			String sortOrder, HashMap<String, String> projectionMap) {
		Cursor c;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName);
		qb.setProjectionMap(projectionMap);
		
		// Query the database for all days
		c = qb.query(mDb, projection, where, whereArgs, null, null, sortOrder);
	
		// Set the cursor c as an observer on the uri. If it changes the cursor will be notified. Only do this
		// if a directory resource based on the uri and where clause was actually found.
		if (c.getCount() > 0)
			c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	/**
	 * Handle incoming client update calls to a URI.
	 * @param uri -  the URI of the resource to be updated. This may be a directory or single item resource.
	 * @param values - the values for the columns being updated as name/value pairs.
	 * @param where - the "where" clause (without the word "where") or null if there is no "where" clause
	 * @param whereArgs - an array of values that replace '?' place-holders in the "where" clause
	 * @return the number of rows updated
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int affected = 0;
	        
		switch (sUriMatcher.match(uri)) {
	        case SESSIONS:          
	        	affected = mDb.update(SESSIONS_TABLE_NAME, values,
                        		where, whereArgs);
	        	break;
	        case SESSIONS_ID:
	        	affected = updateItem(SESSIONS_TABLE_NAME, uri, values, where, whereArgs);
	        	break;
	        case DAYS:
	        	affected = mDb.update(DAYS_TABLE_NAME, values,
                		where, whereArgs);
	        	break;
	        case DAYS_ID:
	        	affected = updateItem(DAYS_TABLE_NAME, uri, values, where, whereArgs);
	        	break;
	        case EVENTS:
	        	affected = mDb.update(EVENTS_TABLE_NAME, values,
                		where, whereArgs);
	        	break;
	        case EVENTS_ID:
	        	affected = updateItem(EVENTS_TABLE_NAME, uri, values, where, whereArgs);
	        	break;
	        case TALKS:
	        	affected = mDb.update(TALKS_TABLE_NAME, values,
                		where, whereArgs);
	        	break;
	        case TALKS_ID:
	        	affected = updateItem(TALKS_TABLE_NAME, uri, values, where, whereArgs);
	        	break;
	        case VENUES:
	        	affected = mDb.update(VENUES_TABLE_NAME, values,
                		where, whereArgs);
	        	break;
	        case VENUES_ID:
	        	affected = updateItem(VENUES_TABLE_NAME, uri, values, where, whereArgs);
	        	break;
	        case NOTES:
	        	affected = mDb.update(NOTES_TABLE_NAME, values,
                		where, whereArgs);
	        	break;
	        case NOTES_ID:
	        	affected = updateItem(NOTES_TABLE_NAME, uri, values, where, whereArgs);
	        	break;
	        default:
	            throw new IllegalArgumentException("Unknown conference type: " +
	                    uri);
		}
		
		return affected;
	}
	
	// See insert for comments
	private int updateItem(String tableName, Uri uri, ContentValues values, String where,
						String[] whereArgs) {
		String id = uri.getPathSegments().get(1);
        return mDb.update(tableName, values,
                BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(where) ?
                        " AND (" + where + ')' : ""),
                whereArgs);
	}

	// Allows object initialization to be reused.
    private void init() {
        mOpenDbHelper = new ConferenceDbHelper(getContext(),
                DATABASE_NAME, null);
        
        // Allow for reading and writing to the database
        mDb = mOpenDbHelper.getWritableDatabase();
    }

}

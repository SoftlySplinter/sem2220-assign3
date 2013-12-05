package uk.ac.aber.androidcourse.conferencelibrary;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Public API for the example ConfDBase content provider example.
 *
 * The public API for a content provider should only contain information that
 * should be referenced by content provider clients. Implementation details
 * such as constants only used by a content provider subclass should not appear
 * in the provider API.
 * For each resource maintained by our content provider we provide a public inner class that defines the interface
 * just for that resource.
 * @author Created by Chris Loftus in December 2011. Copyright 2011 Aberystwyth University. All rights reserved.
 */
public class ConferenceCP {
	
	// Every content provider has a unique authority identifier. The recommendation is to use a domain name. This
	// authority forms part of the URI used by clients when contacting the provider
	public static final String AUTHORITY =
        "uk.ac.aber.androidcourse.conference.provider.conferencecontentprovider";
	
    // The scheme part for this provider's URI
    private static final String SCHEME = "content://";

	
	/**
	 * The sessions resource. 
	 */
	public static class Sessions implements BaseColumns {
		// BaseColumns defines the _id and _count column names that are common to all provider tables
		
		// Column positions of results where everything is returned. Not applicable if a projection
		// is used by the client. As an alternative, column positions can also be found based on column names.
		public static final int ID_COLUMN = 5;
		public static final int TITLE_COLUMN = 1;
		public static final int START_TIME_COLUMN = 3;
		public static final int END_TIME_COLUMN = 0;
		public static final int TYPE_COLUMN = 4;
		public static final int DAY_ID_COLUMN = 2;
		
		/**
		 * Directory path part for the sessions resource
		 */
		public static final String SESSIONS = "sessions";

		/**
		 * Sessions URI for the Sessions directory resource
		 */
	    public static final Uri SESSIONS_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + SESSIONS);
	    
	    
	    
	    /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = SESSIONS_URI;
        
        /**
         * The content URI base for a single session. Callers must
         * append a numeric session id to this Uri to retrieve a session
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/'+ SESSIONS + '/');
        
        /**
         * The content URI match pattern for a single session, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + SESSIONS + "/#");
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of sessions.
         * Every resource has its own MIME type. The first part identifies
         * the resource as a directory (vnd.android.cursor.dir). "vnd" means a vendor 
         * (i.e. developer organisation) provided type rather than a standard MIME type.
         * Note the use of the package name to uniquely name the MIME type.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.sessions";

        /**
         * The MIME type of a {@link #CONTENT_URI} of a single
         * session. 
         */
        public static final String CONTENT_SESSION_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.sessions";
	    
	    
        // The column names
	    public static final String TITLE_NAME = "title";
	    public static final String START_TIME_NAME = "starttime";
	    public static final String END_TIME_NAME = "endtime";
	    public static final String TYPE_NAME = "type";
	    public static final String DAY_ID_NAME = "dayid";
	    
	    // The default sort order used for queries
	    public static final String DEFAULT_SORT_ORDER = TITLE_NAME + " DESC";
	}
	
	/**
	 * The events resource. 
	 */
	public static class Events implements BaseColumns {
		
		public static final int ID_COLUMN = 0;
		public static final int TITLE_COLUMN = 1;
		public static final int VENUE_ID_COLUMN = 2;
		public static final int SESSION_ID_COLUMN = 3;
		
		/**
		 * Directory path part for the events resource
		 */
		public static final String EVENTS = "events";
		
		/**
		 * Sessions URI for the Events directory resource
		 */
	    public static final Uri EVENTS_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + EVENTS);
	    
	    
	    /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = EVENTS_URI;
        
        /**
         * The content URI base for a single event. Callers must
         * append a numeric event id to this Uri to retrieve an event
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/' + EVENTS);
        
        /**
         * The content URI match pattern for a single event, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + EVENTS + "/#");
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of events.
         */
        public static final String CONTENT_TYPE =
        	ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.events";

        /**
         * The MIME type of a {@link #CONTENT_URI} a single
         * event.
         */
        public static final String CONTENT_EVENT_TYPE =
        	ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.events";
	    
	    
	    public static final String TITLE_NAME = "title";
	    public static final String VENUE_ID_NAME = "venueid";
	    public static final String SESSION_ID_NAME = "sessionid";
	    
	    public static final String DEFAULT_SORT_ORDER = TITLE_NAME + " DESC";
	}
	
	
	/**
	 * The talks resource. 
	 */
	public static class Talks implements BaseColumns {
		
		public static final int ID_COLUMN = 0;
		public static final int TITLE_COLUMN = 1;
		public static final int SPEAKER_COLUMN = 2;
		public static final int IMAGE_COLUMN = 3;
		public static final int DESCRIPTION_COLUMN = 4;
		public static final int EVENT_ID_COLUMN = 5;
		public static final int NOTES_COLUMN = 6;
		
		/**
		 * Directory path part for the talks resource
		 */
		public static final String TALKS = "talks";
		 
		/**
		 * Sessions URI for the Sessions directory resource
		 */
	    public static final Uri TALKS_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + TALKS);
	    
	    
	    /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = TALKS_URI;
        
        /**
         * The content URI base for a single session. Callers must
         * append a numeric session id to this Uri to retrieve a session
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/' + TALKS + '/');
        
        /**
         * The content URI match pattern for a single talk, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + TALKS + "/#");
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of talks.
         */
        public static final String CONTENT_TYPE =
        	ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.talks";

        /**
         * The MIME type of a {@link #CONTENT_URI} a single
         * talk.
         */
        public static final String CONTENT_TALK_TYPE =
        	ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.talks";
	    
	   
	    public static final String TITLE_NAME = "title";
	    public static final String SPEAKER_NAME = "speaker";
	    public static final String IMAGE_NAME = "image";
	    public static final String DESCRIPTION_NAME = "description";
	    public static final String EVENT_ID_NAME = "eventid";
	    public static final String NOTES_NAME = "notes";
	    
	    public static final String DEFAULT_SORT_ORDER = TITLE_NAME + " DESC";
	}
	
	/**
	 * The venues resource. 
	 */
	public static class Venues implements BaseColumns {
		
		public static final int ID_COLUMN = 0;
		public static final int NAME_COLUMN = 1;
		public static final int LATITUDE_COLUMN = 2;
		public static final int LONGITUDE_COLUMN = 3;
		
		public static final String VENUES = "venues";
		
		/**
		 * Sessions URI for the Venues directory resource
		 */
	    public static final Uri VENUES_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + VENUES);
	    
	    
	    /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = VENUES_URI;
        
        /**
         * The content URI base for a single venue. Callers must
         * append a numeric venue id to this Uri to retrieve a venue
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/' + VENUES + '/');
        
        /**
         * The content URI match pattern for a single venue, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + VENUES + "/#");
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of venues.
         */
        public static final String CONTENT_TYPE =
        	ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.venues";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * venue.
         */
        public static final String CONTENT_VENUE_TYPE =
        	ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.venues";
	    
	    
	    public static final String NAME_NAME = "name";
	    public static final String LATITUDE_NAME = "latitude";
	    public static final String LONGITUDE_NAME = "longitude";
	    
	    public static final String DEFAULT_SORT_ORDER = NAME_NAME + " DESC";
	}
	
	/**
	 * The days resource. 
	 */
	public static class Days implements BaseColumns {
		
		public static final int ID_COLUMN = 0;
		public static final int DAY_COLUMN = 1;
		public static final int DATE_TIME_COLUMN = 2;
		
		/**
		 * Directory path part for the days resource
		 */
		public static final String DAYS = "days";
		
		/**
		 * Days URI for the Days directory resource
		 */
	    public static final Uri DAYS_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + DAYS);
	    
	    
	    /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = DAYS_URI;
        
        /**
         * The content URI base for a single day. Callers must
         * append a numeric day id to this Uri to retrieve a day
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/' + DAYS + '/');
        
        /**
         * The content URI match pattern for a single day, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + DAYS + "/#");
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of days.
         */
        public static final String CONTENT_TYPE =
        	ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.days";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * day.
         */
        public static final String CONTENT_DAY_TYPE =
        	ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.days";
	    
	   
	    public static final String DAY_NAME = "day";
	    public static final String DATE_NAME = "date";
	    
	    public static final String DEFAULT_SORT_ORDER = DAY_NAME + " DESC";
	}
	
	/**
	 * The events-venue resource. 
	 */
	public static class EventsVenue implements BaseColumns {
		
		
		/**
		 * Directory path part for the events-venue resource
		 */
		public static final String EVENTS_VENUE = "events_venue";
		
		/**
		 * Events URI for the Events-venue directory resource
		 */
	    public static final Uri EVENTS_VENUE_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + EVENTS_VENUE);
	    
	    
	    /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = EVENTS_VENUE_URI;
        
        /**
         * The content URI base for a single event-venue. Callers must
         * append a numeric event id to this Uri to retrieve an event-venue
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/' + EVENTS_VENUE + '/');
        
        /**
         * The content URI match pattern for a single event-venue, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + EVENTS_VENUE + "/#");
        

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of event venues.
         */
        public static final String CONTENT_TYPE =
        	ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.events_venue";
        
        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * event venue.
         */
        public static final String CONTENT_EVENT_VENUE_TYPE =
        	ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.events_venue";
	    
        public static final String TITLE_NAME = "eventstitle";
	    public static final String VENUE_ID_NAME = "eventsvenueid";
	    public static final String SESSION_ID_NAME = "eventssessionid";
	    public static final String VENUE_NAME_NAME = "venuesname";
	    public static final String VENUE_LATITUDE_NAME = "venueslatitude";
	    public static final String VENUE_LONGITUDE_NAME = "venueslongitude";
        
	    public static final String DEFAULT_SORT_ORDER = TITLE_NAME + " DESC";
	}
	
	/**
	 * The notes resource. 
	 */
	/*
	public static class Notes implements BaseColumns {
		
		public static final int ID_COLUMN = 0;
		public static final int TITLE_COLUMN = 1;
		public static final int CONTENT_COLUMN = 2;
		public static final int TALK_ID_COLUMN = 3;
		
		
		public static final String NOTES = "notes";
		
		
	    public static final Uri NOTES_URI = Uri.parse(SCHEME +
	            AUTHORITY + '/' + NOTES);
	    
	    
	   
        public static final Uri CONTENT_URI = NOTES_URI;
        
       
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + '/' + NOTES + '/');
        
      
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + '/' + NOTES + "/#");
        
       
        public static final String CONTENT_TYPE =
        	ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.notes";

      
        public static final String CONTENT_NOTE_TYPE =
        	ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.uk.ac.aber.androidcourse.conference.notes";
	    
	   
	    public static final String TITLE_NAME = "title";
	    public static final String CONTENT_NAME = "content";
	    public static final String TALK_ID_NAME = "talkid";
	    
	    public static final String DEFAULT_SORT_ORDER = TITLE_NAME + " DESC";
	}
	*/

}

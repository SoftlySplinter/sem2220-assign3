package uk.ac.aber.androidcourse.conference.widget.list;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Creates the {@link SessionsListFactory}.
 * 
 * @author Alex Brown
 * @version 1.0
 */
public final class SessionsListService extends RemoteViewsService  {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new SessionsListFactory(this.getApplicationContext(), intent);
	}

}

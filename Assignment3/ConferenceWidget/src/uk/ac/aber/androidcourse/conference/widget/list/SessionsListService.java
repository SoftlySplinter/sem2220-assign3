package uk.ac.aber.androidcourse.conference.widget.list;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class SessionsListService extends RemoteViewsService  {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new SessionsListFactory(this.getApplicationContext(), intent);
	}

}

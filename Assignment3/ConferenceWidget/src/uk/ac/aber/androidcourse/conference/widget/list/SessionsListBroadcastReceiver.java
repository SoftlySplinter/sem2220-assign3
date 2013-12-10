package uk.ac.aber.androidcourse.conference.widget.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class SessionsListBroadcastReceiver extends BroadcastReceiver {
	public static SessionsListFactory linker;

	@Override
	public void onReceive(Context context, Intent intent) {
		linker.onReceive(context, intent);
	}

}

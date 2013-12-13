package uk.ac.aber.androidcourse.conference.widget.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Recieves broadcasts for the Sessions list.
 * 
 * This is a bit of a hack as the sessions list has to add itself as the static
 * property {@link #linker}.
 * 
 * @author Alex Brown
 * @version 1.0
 */
public final class SessionsListBroadcastReceiver extends BroadcastReceiver {
	public static SessionsListFactory linker;

	@Override
	public void onReceive(Context context, Intent intent) {
		linker.onReceive(context, intent);
	}

}

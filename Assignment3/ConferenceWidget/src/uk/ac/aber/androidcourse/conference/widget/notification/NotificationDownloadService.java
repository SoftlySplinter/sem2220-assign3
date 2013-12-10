package uk.ac.aber.androidcourse.conference.widget.notification;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public final class NotificationDownloadService extends AsyncTask<Void, Void, String> {
	private static final String NOTIFICATION_URL = "http://users.aber.ac.uk/aos/android/message.php";
	private static final String LOG_TAG = "Notification Downloader";
	
	protected String doInBackground(Void ...v) {
		try {
			return Utils.readConnection(new URL(NOTIFICATION_URL));
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Invalid URL: " + NOTIFICATION_URL, e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to read from URL: " + NOTIFICATION_URL, e);
		}
		return "No notification";
	}
}

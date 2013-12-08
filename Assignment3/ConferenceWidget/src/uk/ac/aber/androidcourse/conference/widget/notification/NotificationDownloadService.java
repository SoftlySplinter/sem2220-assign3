package uk.ac.aber.androidcourse.conference.widget.notification;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public final class NotificationDownloadService extends Service {
	private static final String NOTIFICATION_URL = "http://users.aber.ac.uk/aos/android/message.php";
	private static final String LOG_TAG = "Notification Downloader";
	
	@Override
	public IBinder onBind(Intent intent) {
		return new NotificationBinder();
	}
	
	public String getNotification() {
		try {
			return Utils.readConnection(Utils.doConnect(new URL(NOTIFICATION_URL), "GET"));
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Invalid URL: " + NOTIFICATION_URL, e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to read from URL: " + NOTIFICATION_URL, e);
		}
		return "No notification";
	}
	
	public class NotificationBinder extends Binder {
		public NotificationDownloadService getService() {
			return NotificationDownloadService.this;
		}
	}
}

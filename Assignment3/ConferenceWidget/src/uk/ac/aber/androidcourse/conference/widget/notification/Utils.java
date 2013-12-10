package uk.ac.aber.androidcourse.conference.widget.notification;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;

public final class Utils {
	private final static String LOG_TAG = "URL Utils";
	/**
	 * Helper method to save a lot of playing about with Streams
	 */
	public static final void close(Closeable... toClose) {
		for (Closeable close : toClose) {
			if (close != null) {
				try {
					close.close();
				} catch (IOException e) {
					// Do nothing.
				}
			}
		}
	}

	public static final HttpURLConnection doConnect(URL url, String method)
			throws IOException {
		Log.i(LOG_TAG, String.format("%s %s HTTP/1.1", method, url.getFile()));
		Log.i(LOG_TAG, String.format("Host: %s", url.getHost()));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		for(Entry<String, List<String>> property : conn.getRequestProperties().entrySet()) {
			Log.i(LOG_TAG, String.format("%s: %s", property.getKey(), property.getValue()));
		}
		return conn;
	}

	public static final String readConnection(HttpURLConnection conn)
			throws IOException {
		final StringBuilder s = new StringBuilder();
		BufferedReader bRead = null;
		Reader reader = null;
		try {
			conn.connect();
			Log.i(LOG_TAG, String.format("HTTP/1.1 %d %s", conn.getResponseCode(), conn.getResponseMessage()));
			Log.i(LOG_TAG, String.format("Date: %s", new Date(conn.getDate())));
			for(Entry<String, List<String>> prop: conn.getHeaderFields().entrySet()) {
				Log.i(LOG_TAG, String.format("%s: %s", prop.getKey(), prop.getValue()));
			}
			reader = new InputStreamReader(conn.getInputStream());
			bRead = new BufferedReader(reader);
			while (bRead.ready()) {
				s.append(bRead.readLine());
				s.append(String.format("%n"));
			}
		} finally {
			close(reader, bRead);
		}
		return s.toString();
	}
}

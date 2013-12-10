package uk.ac.aber.androidcourse.conference.widget.notification;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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

	public static final String readConnection(URL url)
			throws IOException {
		Log.i(LOG_TAG, String.format("GET %s HTTP/1.1", url.getFile()));
		Log.i(LOG_TAG, String.format("Host: %s", url.getHost()));
		
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Accept", "*/*");
		
		final StringBuilder s = new StringBuilder();
		final InputStream stream = conn.getInputStream();
		
		try {
			int count = 0;
			int offset = 0;
			byte buffer[] = new byte[256];
			while((count = stream.read(buffer)) != -1) {
				offset = offset + count;
				String temp = new String(buffer);
				s.append(temp.substring(0, count));
			}
		} finally {
			close(stream);
		}
		
		Log.i(LOG_TAG, "Content: " + s.toString());
		
		return s.toString();
	}
}

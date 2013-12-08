package uk.ac.aber.androidcourse.conference.widget.notification;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Utils {
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
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		return conn;
	}

	public static final String readConnection(HttpURLConnection conn)
			throws IOException {
		final StringBuilder s = new StringBuilder();
		BufferedReader bRead = null;
		Reader reader = null;
		try {
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

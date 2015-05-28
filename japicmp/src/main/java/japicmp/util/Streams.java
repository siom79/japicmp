package japicmp.util;

import japicmp.exception.JApiCmpException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Streams {

	private Streams() {

	}

	public static String asString(InputStream inputStream) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = inputStream.read(buffer);
			while (read != -1) {
				byteArrayOutputStream.write(buffer, 0, read);
				read = inputStream.read(buffer);
			}
			return byteArrayOutputStream.toString("UTF-8");
		} catch (IOException e) {
			throw new JApiCmpException(JApiCmpException.Reason.IoException, "Failed to read from input stream: " + e.getMessage(), e);
		}
	}
}

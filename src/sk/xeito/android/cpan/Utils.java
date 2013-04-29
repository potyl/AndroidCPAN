package sk.xeito.android.cpan;

import android.util.Log;

public class Utils {

	private static final String TAG = "cpan";

	public static void printf(String format, Object ... args) {
		String message = String.format(format, args);
		Log.d(TAG, message);
	}
}

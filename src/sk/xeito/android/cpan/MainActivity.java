package sk.xeito.android.cpan;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends BaseActivity {

	private static final String FILE = "01modules.mtime.rss";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RssLoader task = new RssLoader();
		task.execute();
	}

	class RssLoader extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				doTask();
			}
			catch (Exception e) {
				printf("Failed to run async: %s", e.toString());
			}
			return null;
		}
		
		private void doTask() throws Exception {
			AssetManager assets = getAssets();
			InputStream input = assets.open(FILE);
			XmlPullParserFactory parser = XmlPullParserFactory.newInstance();
			XmlPullParser pullParser = parser.newPullParser();
			pullParser.setInput(input, "utf-8");

			Pattern regexp = Pattern.compile("^(.+)-([^-]+) : (\\S+)$");
			boolean doingItems = false;
			LOOP:
			while (true) {
				int type = pullParser.next();
				switch (type) {
					case XmlPullParser.END_DOCUMENT:
						printf("End of document");
					break LOOP;

					case XmlPullParser.START_TAG:
						String name = pullParser.getName();
						if (doingItems) {
							if (name.equals("title")) {
								String text = pullParser.nextText();
								Matcher matcher = regexp.matcher(text);
								if (matcher.find()) {
									String module = matcher.group(1);
									String version = matcher.group(2);
									String user = matcher.group(3);
									printf("Found module: %s; version: %s; user: %s", module, version, user);
								}
							}
						}
						else {
							if (name.equals("item")) {
								doingItems = true;
							}
						}
						//printf("Type: %s; name: %s", type, name);
					break;
				}
			}
		}
	}


	static void printf(String format, Object ... args) {
		String message = String.format(format, args);
		Log.d("app", message);
	}
}

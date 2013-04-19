package sk.xeito.android.cpan;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private static final String FILE = "01modules.mtime.rss";

	private TextView loadingView;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loadingView = (TextView) findViewById(R.id.loading_rss);
		list = (ListView) findViewById(R.id.rss_list);
		list.setVisibility(View.GONE);

		// Load the RSS feed
		RssLoader task = new RssLoader();
		task.execute();
	}

	class RssLoader extends AsyncTask<Void, Void, List<FeedEntry>> {
		@Override
		protected List<FeedEntry> doInBackground(Void... params) {
			List<FeedEntry> entries;

			try {
				entries = doTask();
			}
			catch (Exception e) {
				Utils.printf("Failed to run async: %s", e.toString());
				entries = Collections.emptyList();
			}

			return entries;
		}

		@Override
		protected void onPostExecute(List<FeedEntry> entries) {
			FeedAdapter adapter = new FeedAdapter(MainActivity.this);
			adapter.addAll(entries);
			list.setAdapter(adapter);
			list.setVisibility(View.VISIBLE);
			loadingView.setVisibility(View.GONE);
		}

		private List<FeedEntry> doTask() throws Exception {
			AssetManager assets = getAssets();
			InputStream input = assets.open(FILE);
			XmlPullParserFactory parser = XmlPullParserFactory.newInstance();
			XmlPullParser pullParser = parser.newPullParser();
			pullParser.setInput(input, "utf-8");

			Pattern regexp = Pattern.compile("^(.+)-([^-]+) : (\\S+)$");
			boolean doingItem = false;
			List<FeedEntry> feedEntries = new ArrayList<FeedEntry>();
			FeedEntry feedEntry = null;

			LOOP:
			while (true) {
				int type = pullParser.next();
				switch (type) {
					case XmlPullParser.END_DOCUMENT:
					break LOOP;

					case XmlPullParser.START_TAG:
						String name = pullParser.getName();
						if (doingItem) {
							if (name.equals("title")) {
								// Parse a <item><title>
								String text = pullParser.nextText();
								Matcher matcher = regexp.matcher(text);
								if (matcher.find()) {
									feedEntry.module = matcher.group(1);
									feedEntry.version = matcher.group(2);
									feedEntry.author = matcher.group(3);
								}
							}
							else if (name.equals("comments")) {
								feedEntry.url = pullParser.nextText();
							}

							if (feedEntry.module != null && feedEntry.url != null) {
								feedEntries.add(feedEntry);
								doingItem = false;
							}
						}
						else if (name.equals("item")) {
							// We can start capturing an item
							doingItem = true;
							feedEntry = new FeedEntry();
						}
					break;
				}
			}

			Utils.printf("Found %s entries", feedEntries.size());
			return feedEntries;
		}
	}
}

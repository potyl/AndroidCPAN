package sk.xeito.android.cpan;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FeedListFragment extends Fragment implements OnItemClickListener {
	private static final boolean DO_HTTP = true;
	private static final String FILE = "01modules.mtime.rss";
	private static final String URL = "http://www.cpan.org/modules/01modules.mtime.rss";

	private TextView loadingView;
	private ListView list;
	private FeedAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.printf("fragment onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.feed_list_fragment, container, false);
		Utils.printf("fragment onCreateView");

		loadingView = (TextView) view.findViewById(R.id.loading_rss);
		list = (ListView) view.findViewById(R.id.rss_list);
		list.setVisibility(View.GONE);
		list.setOnItemClickListener(this);

		// Load the RSS feed
		RssLoader task = new RssLoader();
		task.execute();

		return view;
	}


	class RssLoader extends AsyncTask<Void, Void, List<FeedEntry>> {
		@Override
		protected List<FeedEntry> doInBackground(Void... params) {
			List<FeedEntry> entries;

			try {
				entries = doTask();
			}
			catch (Exception e) {
				Utils.eprintf(e, "Failed to run async");
				entries = null;
				loadingView.setText(R.string.rss_load_failure);
			}

			return entries;
		}

		@Override
		protected void onPostExecute(List<FeedEntry> entries) {
			if (entries == null) {
				// Failed to load the entries
				return;
			}

			adapter = new FeedAdapter(getActivity());
			adapter.addAll(entries);
			list.setAdapter(adapter);
			list.setVisibility(View.VISIBLE);
			loadingView.setVisibility(View.GONE);

			String subtitle = getString(R.string.main_subtitle, entries.size());
			getActivity().getActionBar().setSubtitle(subtitle);
		}

		private InputStream getFileStream() throws IOException {
			AssetManager assets = getActivity().getAssets();
			InputStream input = assets.open(FILE);
			return input;
		}

		private InputStream getHttpStream() throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(URL);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			return entity.getContent();
		}

		private List<FeedEntry> doTask() throws Exception {
			XmlPullParserFactory parser = XmlPullParserFactory.newInstance();
			XmlPullParser pullParser = parser.newPullParser();
			InputStream input = DO_HTTP ? getHttpStream() : getFileStream();
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
			input.close();

			Utils.printf("Found %s entries", feedEntries.size());
			return feedEntries;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FeedEntry feedEntry = adapter.getItem(position);
		Intent intent = new Intent(getActivity(), FeedItemActivity.class);
		intent.putExtra(B.args.url, feedEntry.getCpanUrl());
		intent.putExtra(B.args.feed_entry, feedEntry);
		startActivity(intent);
	}

}
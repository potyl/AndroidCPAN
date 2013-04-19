package sk.xeito.android.cpan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FeedAdapter extends ArrayAdapter<FeedEntry> {
	private final LayoutInflater inflater;

	public FeedAdapter(Context context) {
		super(context, R.layout.rss_cell);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.rss_cell, null);
			holder = new ViewHolder();
			holder.moduleView = (TextView) convertView.findViewById(R.id.rss_module);
			holder.versionView = (TextView) convertView.findViewById(R.id.rss_version);
			holder.authorView = (TextView) convertView.findViewById(R.id.rss_author);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		FeedEntry feedEntry = getItem(position);
		Utils.printf("Displaying cell[%s]: %s", position, feedEntry);
		holder.moduleView.setText(feedEntry.module);
		holder.versionView.setText(feedEntry.version);
		holder.authorView.setText(feedEntry.author);

		return convertView;
	}

	static class ViewHolder {
		TextView moduleView;
		TextView versionView;
		TextView authorView;
	}
}

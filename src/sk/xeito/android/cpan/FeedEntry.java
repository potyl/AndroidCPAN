package sk.xeito.android.cpan;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class FeedEntry implements Parcelable {
	private static final String URI_TEMPLATE = "http://search.cpan.org/perldoc?%s";

	public String module;
	public String version;
	public String author;
	public String url;

	public FeedEntry () {
		// Empty
	}

	public FeedEntry (Parcel in) {
		module = in.readString();
		version = in.readString();
		author = in.readString();
		url = in.readString();
	}

	public String getCpanUrl() {
		String cpanPackage = module.replace("-", "::");
		String uriCpanPakacge = Uri.encode(cpanPackage);
		return String.format(URI_TEMPLATE, uriCpanPakacge);
	}

	@Override
	public String toString() {
		return String.format(
			"%s[0x%08x](module: %s; version: %s; author: %s; url: %s)",
			getClass().getSimpleName(),
			System.identityHashCode(this),
			module,
			version,
			author,
			url
		);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(module);
		dest.writeString(version);
		dest.writeString(author);
		dest.writeString(url);
	}

	public static final Creator<FeedEntry> CREATOR = new Creator<FeedEntry>() {
		public FeedEntry createFromParcel(Parcel in) {
			return new FeedEntry(in);
		}

		public FeedEntry[] newArray(int size) {
			return new FeedEntry[size];
		}
	};
}

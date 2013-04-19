package sk.xeito.android.cpan;

public class FeedEntry {
	public String module;
	public String version;
	public String author;
	public String url;

	public FeedEntry () {
		// Empty
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
}

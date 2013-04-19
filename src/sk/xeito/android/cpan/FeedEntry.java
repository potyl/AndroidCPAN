package sk.xeito.android.cpan;

public class FeedEntry {
	final String module;
	final String version;
	final String author;

	public FeedEntry (String module, String version, String author) {
		this.module = module;
		this.version = version;
		this.author = author;
	}

	@Override
	public String toString() {
		return String.format(
			"%s[0x%08x](module: %s; version: %s; author: %s)",
			getClass().getSimpleName(),
			System.identityHashCode(this),
			module,
			version,
			author
		);
	}
}

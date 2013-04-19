package sk.xeito.android.cpan;

import android.net.Uri;

public class FeedEntry {
	private static final String URI_TEMPLATE = "http://search.cpan.org/perldoc?%s";

	public String module;
	public String version;
	public String author;
	public String url;

	public FeedEntry () {
		// Empty
	}

	public String getCpanUrl() {
		String cpanPackage = module.replace("-", "::");
		String uriCpanPakacge = Uri.decode(cpanPackage);
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
}

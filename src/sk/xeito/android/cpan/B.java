package sk.xeito.android.cpan;

public interface B {
	public interface args {
		/**
		 * Pass an URL.
		 */
		String url = "url";

		/**
		 * Pass a feed entry.
		 */
		String feed_entry = "feed_entry";
	}


	public interface fragments {
		String feed_list = "feed_list_fragment";
	}
}

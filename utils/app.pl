#!/usr/bin/env perl
use Dancer;

use FindBin;

config->{port} = 3001;
config->{public} = "$FindBin::Bin";

# Show /index.html when there's a request for '/'
get '/' => sub {
	send_file '/01modules.mtime.rss'
};

dance;

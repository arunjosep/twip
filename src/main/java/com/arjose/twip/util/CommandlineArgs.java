package com.arjose.twip.util;

/**
 * Stores all command line arguments accepted by app. Names start S (Single) if
 * the argument does not expect second value. Names start with D (Double) if the
 * argument expects a second argument. Add processing in
 * <class>CommandParser.java</class>
 * 
 * @author arun
 *
 */
public interface CommandlineArgs {

	/**
	 * Used for passing hash-tag or search key.
	 */
	public static final String D_HASH_TAGS = "-keys";

	/**
	 * Used for passing words to compare in tweets containing search key. If
	 * search key is not passed, will search in all incoming tweets.
	 */
	public static final String D_COMPARE_TAGS = "-compare";

	/**
	 * Time to run in local cluster.
	 */
	public static final String D_TTL_SEC = "-ttl";

	/**
	 * Used to open firehose instead of a filtered hose. If not passed, uses
	 * twitter hose with language filter for "en" applied.
	 */
	public static final String S_OPEN_FIRE = "-fire";

	/**
	 * Run on local cluster. If not passed runs on production cluster.
	 */
	public static final String S_TEST_CLUSTER = "-test";

	/**
	 * Run sentiment analysis for each candidate, if passed.
	 */
	public static final String S_RUN_SENT = "-sentiment";

	/**
	 * Run analysis only on non-retweet original tweets.
	 */
	public static final String S_NO_RT = "-noRT";

	/**
	 * Include key and #key in searches when only either is given.
	 */
	public static final String S_ADD_HASH = "-addHash";

	/**
	 * Proxy host and port. Ex: www.proxy.net:8080
	 */
	public static final String D_PROXY = "-proxy";
}

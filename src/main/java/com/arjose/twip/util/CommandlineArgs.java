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
	public static final String D_HASH_TAG = "-keys";

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
	 * Run on Production cluster. If not passed runs on local cluster.
	 */
	public static final String S_PROD_CLUSTER = "-prod";

}

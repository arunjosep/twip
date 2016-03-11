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
	public static final String D_HASH_TAG = "-h";

	/**
	 * Time to run in local cluster.
	 */
	public static final String D_TTL_SEC = "-t";

	/**
	 * Used to open firehose instead of samplehose. If not passed, uses twitter
	 * sample hose.
	 */
	public static final String S_OPEN_FIRE = "-f";

	/**
	 * Run on Production cluster. If not passed runs on local cluster.
	 */
	public static final String S_PROD_CLUSTER = "-p";

}

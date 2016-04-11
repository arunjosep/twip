package com.arjose.twip.util;

import java.util.regex.Pattern;

/**
 * Class for key operations
 * 
 * @author arun
 *
 */
public class KeyUtils {

	/**
	 * Removes leading hash from comma separated words
	 * 
	 * @param sourceKeys
	 *            Comma separated list of words
	 * @return
	 */
	public static String unHash(String sourceKeys) {
		StringBuffer unhashed = new StringBuffer("");
		String[] keys = sourceKeys.split(Pattern.quote(","));
		for (String key : keys) {
			key = key.trim();
			if (key.startsWith("#")) {
				key = key.substring(1);
			}
			unhashed.append(key);
			unhashed.append(",");
		}
		unhashed.setLength(unhashed.length() - 1); // Remove the extra comma
		return unhashed.toString();
	}

	/**
	 * Unpacks a comma separated list of words and adds a # prepended version of
	 * each word after each word.
	 * 
	 * @param keyString
	 *            Comma separated list of words
	 * @return
	 */
	public static String addHash(String keyString) {
		StringBuffer hashed = new StringBuffer("");
		String[] keys = keyString.split(Pattern.quote(","));
		for (String key : keys) {
			hashed.append(key);
			hashed.append(",#");
			hashed.append(key);
			hashed.append(",");
		}
		hashed.setLength(hashed.length() - 1); // Remove the extra comma

		return hashed.toString();
	}

}

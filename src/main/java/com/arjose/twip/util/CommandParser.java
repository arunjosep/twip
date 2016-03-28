package com.arjose.twip.util;

import java.util.HashMap;

/**
 * Util class to parse command line arguments.
 * 
 * @author arun
 */
public class CommandParser implements CommandlineArgs {

	public CommandParser() {
	}

	/**
	 * Parse command line arguments and pass them in map
	 * 
	 * @param args
	 * @return
	 */
	public static HashMap parseList(String[] args) {
		HashMap argsMap = new HashMap();

		if (args == null)
			return argsMap;

		for (int i = 0; i < args.length; i++) {

			if (D_HASH_TAG.equalsIgnoreCase(args[i])) {
				if (i + 1 < args.length) {
					argsMap.put(D_HASH_TAG, args[i + 1]);
					info(D_HASH_TAG, args[++i]);
				} else {
					warn(D_HASH_TAG, "hash tag or key to search for");
				}

			} else if (D_COMPARE_TAGS.equalsIgnoreCase(args[i])) {
				if (i + 1 < args.length) {
					argsMap.put(D_COMPARE_TAGS, args[i + 1]);
					info(D_COMPARE_TAGS, args[++i]);
				} else {
					warn(D_COMPARE_TAGS, "words to compare");
				}

			} else if (D_TTL_SEC.equalsIgnoreCase(args[i])) {
				if (i + 1 == args.length) {
					warn(D_TTL_SEC, "time to run in local cluster in seconds");
				} else if (!checkIfNum(args[i + 1])) {
					warn(D_TTL_SEC, "time to run in local cluster in seconds. Passed non number " + args[i + 1]);
				} else {
					argsMap.put(D_TTL_SEC, Long.parseLong(args[i + 1]));
					info(D_TTL_SEC, args[++i]);
				}

			} else if (S_OPEN_FIRE.equalsIgnoreCase(args[i])) {
				argsMap.put(S_OPEN_FIRE, true);
				info(S_OPEN_FIRE);

			} else if (S_PROD_CLUSTER.equalsIgnoreCase(args[i])) {
				argsMap.put(S_PROD_CLUSTER, true);
				info(S_PROD_CLUSTER);

			} else if (S_RUN_SENT_W.equalsIgnoreCase(args[i])) {
				argsMap.put(S_RUN_SENT_W, true);
				info(S_RUN_SENT_W);

			} else if (S_RUN_SENT_I.equalsIgnoreCase(args[i])) {
				argsMap.put(S_RUN_SENT_I, true);
				info(S_RUN_SENT_I);

			} else {
				log("Argument unrecognized " + args[i]
						+ "\nUsage:\n  -keys\n  -compare\n  -ttl\n  -prod\n  -sentW\n  -sentI");
			}
		}
		return argsMap;

	}

	/*
	 * Checks if a string can be converted to a number
	 */
	private static boolean checkIfNum(String input) {
		try {
			Long.parseLong(input);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	private static void info(String arg) {
		log("Argument " + arg + " accepted ");
	}

	private static void info(String arg, String param) {
		log("Argument " + arg + " accepted with input " + param + ".");
	}

	private static void warn(String arg, String missing) {
		log("Argument " + arg + " requires " + missing + ". Omitting this argument.");
	}

	private static void log(String message) {
		System.out.println("twipLog: " + message);
	}

}

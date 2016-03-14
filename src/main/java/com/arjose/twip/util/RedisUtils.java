package com.arjose.twip.util;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

/**
 * Creates and maintains a connection to redis.
 * 
 * @author arun
 *
 */
public class RedisUtils {
	private static RedisCommands<String, String> redis = null;
	private static String host = "localhost";
	private static int port = 6379;

	private RedisUtils() {

	}

	/**
	 * Creates and returns a single connection to redis.
	 * 
	 * @return RedisCommands
	 */
	public static RedisCommands<String, String> getRedis() {
		return redis;
	}

	public static void openRedis() {
		try {
			System.out.println("twipLog: Creating connection to " + host + ":" + port);
			RedisClient client = RedisClient.create(RedisURI.Builder.redis(host, port).build());
			StatefulRedisConnection<String, String> redisConn = client.connect();
			redis = redisConn.sync();
		} catch (IllegalArgumentException ex) {
			redis = null;
			System.out.println("twipLog: Couldn't open connection");
		}
	}
	
	/**
	 * Sets connection properties for Redis connection. Need to be called only
	 * once.
	 * 
	 * @param hostname
	 * @param portNumber
	 */
	public static void setUri(String hostname, int portNumber) {
		host = hostname;
		port = portNumber;
	}

}

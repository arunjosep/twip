package com.arjose.twip;

import java.util.HashMap;

import com.arjose.twip.bolts.CompareBolt;
import com.arjose.twip.bolts.HashBolt;
import com.arjose.twip.spouts.TweetSpout;
import com.arjose.twip.util.CommandParser;
import com.arjose.twip.util.CommandlineArgs;
import com.arjose.twip.util.RedisUtils;
import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

public class BasicTopology {

	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();

		/* *** Parse command line args *** */
		HashMap argsMap = CommandParser.parseList(args);

		String searchKeys = (String) ((argsMap.get(CommandlineArgs.D_HASH_TAG) != null)
				? argsMap.get(CommandlineArgs.D_HASH_TAG) : "");
		String candidates = (String) ((argsMap.get(CommandlineArgs.D_COMPARE_TAGS) != null)
				? argsMap.get(CommandlineArgs.D_COMPARE_TAGS) : "");
		Boolean openFire = (Boolean) ((argsMap.get(CommandlineArgs.S_OPEN_FIRE) != null)
				? argsMap.get(CommandlineArgs.S_OPEN_FIRE) : false);
		Boolean runProd = (Boolean) ((argsMap.get(CommandlineArgs.S_PROD_CLUSTER) != null)
				? argsMap.get(CommandlineArgs.S_PROD_CLUSTER) : false);
		Long ttl = (Long) ((argsMap.get(CommandlineArgs.D_TTL_SEC) != null) ? argsMap.get(CommandlineArgs.D_TTL_SEC)
				: 90);

		// Define authentication params
		String customerKey, customerSecret, accessToken, accessSecret;
		customerKey = "";
		customerSecret = "";
		accessToken = "";
		accessSecret = "";

		/*
		 * Add actual keys in strings above. Or place a file
		 * twitter4j.properties in parent folder with the required properties
		 * (with no spaces or quotes): oauth.consumerKey=*************
		 * oauth.consumerSecret=********** oauth.accessToken=*************
		 * oauth.accessTokenSecret=*******
		 */

		// Open redis connection
		RedisUtils.openRedis();

		/* *** Define topology *** */
		System.out.println("twipLog: BasicTopology starting up.");

		BaseRichSpout tweetSpout = new TweetSpout(customerKey, customerSecret, accessToken, accessSecret, searchKeys,
				openFire);

		builder.setSpout("TweetDhaara", tweetSpout);
		builder.setBolt("SortHashTags", new HashBolt(searchKeys), 5).shuffleGrouping("TweetDhaara");
		builder.setBolt("CompareWords", new CompareBolt(candidates), 5).shuffleGrouping("SortHashTags");

		/* *** End of topology *** */

		// Run topology on local or production cluster

		if (!runProd) {
			// Run locally
			// conf.setDebug(true);
			conf.setMaxTaskParallelism(5);
			LocalCluster cluster = new LocalCluster();
			System.out.println("twipLog: Submitting BasicTopology on local cluster");
			try {

				cluster.submitTopology("basic-topology", conf, builder.createTopology());
			} catch (Exception e) {
				System.out.println("twipLog: Topology failed");
				e.printStackTrace();
			}
			try {
				Thread.sleep(ttl * 1000); // ttl is in seconds
			} catch (InterruptedException e) {
				System.out.println("twipLog: Topology was killed");
				e.printStackTrace();
			}
			System.out.println("twipLog: Topology shutting down. It's been a great run folks.");

			cluster.killTopology("basic-topology");
			cluster.shutdown();

		} else {
			// Run on production
			conf.setNumWorkers(4);
			conf.setMaxSpoutPending(4000);
			System.out.println("twipLog: Submitting BasicTopology on production cluster");
			try {
				StormSubmitter.submitTopology("basic-topology", conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				System.out.println("twipLog: Topology is already running. Could not submit");
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				System.out.println("twipLog: Topology is screwed up. Could not submit");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("twipLog: Topology is seriously **** ");
				e.printStackTrace();
			}
		}
	}

}

package com.arjose.twip;

import com.arjose.twip.bolts.HashBolt;
import com.arjose.twip.spouts.TweetSpout;
import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;

public class BasicTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		String customerKey, customerSecret, accessToken, accessSecret;
		String command = (args != null && args.length > 0) ? args[0] : "";

		customerKey = "";
		customerSecret = "";
		accessToken = "";
		accessSecret = "";

		// ------------------------------------------------------------------//
		// Add actual keys above or place a file twitter4j.properties
		// in parent folder with properties (no spaces or quotes):
		// oauth.consumerKey=*************
		// oauth.consumerSecret=**********
		// oauth.accessToken=*************
		// oauth.accessTokenSecret=*******
		// ------------------------------------------------------------------//

		System.out.println("twipLog: In BasicTopology, about to create TweetSpout" + command);
		BaseRichSpout tweetSpout = new TweetSpout(customerKey, customerSecret, accessToken, accessSecret);

		builder.setSpout("tweetdhara", tweetSpout);
		builder.setBolt("anchor", new HashBolt(command), 5).shuffleGrouping("tweetdhara");

		Config conf = new Config();
		conf.setDebug(true);

		conf.setMaxTaskParallelism(3);

		LocalCluster cluster = new LocalCluster();

		try {
			System.out.println("twipLog: In BasicTopology else: cluster.submitTopology");
			cluster.submitTopology("basic-topology", conf, builder.createTopology());
		} catch (Exception e) {
			Log.error("twipLog: Topology failed");
			e.printStackTrace();
		}

		try {
			Thread.sleep(90000);
		} catch (InterruptedException e) {
			Log.error("twipLog: Topology was killed");
			e.printStackTrace();
		}
		cluster.killTopology("basic-topology");
		cluster.shutdown();

	}

}

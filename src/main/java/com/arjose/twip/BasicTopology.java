package com.arjose.twip;

import com.arjose.twip.bolts.HashBolt;
import com.arjose.twip.spouts.TweetSpout;
import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;


public class BasicTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		String customerKey, customerSecret, accessToken, accessSecret;
		
		/* Add actual keys here*/ 
		customerKey    = "";
		customerSecret = "";
		accessToken    = "";
		accessSecret   = "";
		
		IRichSpout tweetSpout = new TweetSpout(customerKey, customerSecret, accessToken, accessSecret);

		builder.setSpout("tweetdhara", tweetSpout);
		builder.setBolt("anchor", new HashBolt("#MallyaEscapes"), 3).shuffleGrouping("tweetdhara");

		Config conf = new Config();
		conf.setDebug(true);

		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				Log.error("twipLog: Topology seems to be running already");
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				Log.error("twipLog: Screwed up topology");
				e.printStackTrace();
			} catch (Exception e){
				Log.error("twipLog: Topology failed");
				e.printStackTrace();
			}

		} else {
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();			
			
			try {
				cluster.submitTopology("basic-topology", conf, builder.createTopology());
			} catch (Exception e){
				Log.error("twipLog: Topology failed");
				e.printStackTrace();
			}

			try {
				Thread.sleep(300000000);
			} catch (InterruptedException e) {
				Log.error("twipLog: Topology was killed");
				e.printStackTrace();
			}
			cluster.killTopology("basic-topology");
			cluster.shutdown();
		}
	}

}

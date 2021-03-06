package com.arjose.twip.spouts;

import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class PanchayathSpout extends BaseRichSpout {
	SpoutOutputCollector collector;
	int num;

	private static final long serialVersionUID = 1L;

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		num=0;
	}

	public void nextTuple() {
	    Utils.sleep(100);
	    collector.emit(new Values("tuple "+num++));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));
	}

}

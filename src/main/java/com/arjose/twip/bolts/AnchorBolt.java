package com.arjose.twip.bolts;

import com.esotericsoftware.minlog.Log;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class AnchorBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	public AnchorBolt() {
		
	}

	public void execute(Tuple input, BasicOutputCollector collector) {
		String out="Processed "+input;
		collector.emit(new Values(out));
		Log.warn(out); 
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));

	}


}

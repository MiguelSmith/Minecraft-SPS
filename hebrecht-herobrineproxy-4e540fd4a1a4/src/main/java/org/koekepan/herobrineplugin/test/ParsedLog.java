package org.koekepan.herobrineplugin.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.koekepan.herobrine.log.Log;

public class ParsedLog {
	
	private Map<Long, Collection<Log> > parsedLog;
	
	
	public ParsedLog() {
		this.parsedLog = new TreeMap<Long, Collection<Log>>();
	}
	
	
	public void addLogAtNewTimeStamp(long timeStamp, Log log) {
		assert(this.parsedLog.containsKey(timeStamp) == false);
		ArrayList<Log> newEntry = new ArrayList<Log>();
		newEntry.add(log);
		this.parsedLog.put(timeStamp, newEntry);
	}
	
	
	public void addLogToExistingTimeStamp(long timeStamp, Log log) {
		assert(this.parsedLog.containsKey(timeStamp) == true);
		this.parsedLog.get(timeStamp).add(log);
	}

	
	public void addLog(long timeStamp, Log log) {
		if (this.parsedLog.containsKey(timeStamp)) {
			this.addLogToExistingTimeStamp(timeStamp, log);
		} else {
			this.addLogAtNewTimeStamp(timeStamp, log);
		}
	}
	
	
	public Collection<Long> getLogTimes() {
		return this.parsedLog.keySet();
	}
	
	
	public Collection<Log> getLogsAtTimeStamp(long timeStamp) {
		return this.parsedLog.get(timeStamp);
	}
}

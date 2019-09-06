package org.koekepan.herobrineplugin.logging.logs;

import java.io.IOException;

import org.koekepan.herobrine.log.Log;
import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public abstract class TimestampLog implements Log {

	private long timestamp;
	
	public TimestampLog() {
		this(-1);
	}
	
	public TimestampLog(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public void write(LogOutput out) throws IOException {
		out.writeVarLong(timestamp);
	}

	@Override
	public void read(LogInput in) throws IOException {
		timestamp = in.readVarLong();
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
}

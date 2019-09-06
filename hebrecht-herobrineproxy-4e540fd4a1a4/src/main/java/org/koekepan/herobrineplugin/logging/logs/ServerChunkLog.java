package org.koekepan.herobrineplugin.logging.logs;

import java.io.IOException;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public class ServerChunkLog extends TimestampLog {
	
	private int numChunksLoaded;
	
	
	public ServerChunkLog() {
		this(0, -1);
	}
	
	public ServerChunkLog(long timestamp, int numChunksLoaded) {
		super(timestamp);
		this.numChunksLoaded = numChunksLoaded;
	}
	

	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarInt(numChunksLoaded);
	}

	
	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		this.numChunksLoaded = in.readVarInt();
	}
	
	
	public int getNumberOfChunksLoaded() {
		return numChunksLoaded;
	}
}

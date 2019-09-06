package org.koekepan.herobrineplugin.logging.logs;

import java.io.IOException;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public class ServerPlayerLog extends TimestampLog {
	
	private int numOnlinePlayers;
	
	
	public ServerPlayerLog() {
		this(0, -1);
	}
	
	public ServerPlayerLog(long timestamp, int numOnlinePlayers) {
		super(timestamp);
		this.numOnlinePlayers = numOnlinePlayers;
	}
	

	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarInt(numOnlinePlayers);
	}

	
	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		this.numOnlinePlayers = in.readVarInt();
	}
	
	
	public int getNumberOfOnlinePlayers() {
		return numOnlinePlayers;
	}
}

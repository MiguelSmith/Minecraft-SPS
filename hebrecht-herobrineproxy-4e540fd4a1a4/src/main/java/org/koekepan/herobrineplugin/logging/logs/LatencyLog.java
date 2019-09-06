package org.koekepan.herobrineplugin.logging.logs;

import java.io.IOException;
import java.util.UUID;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public class LatencyLog extends TimestampLog {
	
	private UUID playerUUID;
	//private long sendTime;
	//private long receiveTime;
	private long roundTripTime;
	
	
	public LatencyLog() {
		this(-1, new UUID(0, 0), -1, -1);
	}
	
	
	public LatencyLog(long timestamp, UUID playerUUID, long sendTime, long receiveTime) {
		super(timestamp);
		this.playerUUID = playerUUID;
		//this.sendTime = sendTime;
		//this.receiveTime = receiveTime;
		this.roundTripTime = receiveTime - sendTime;
	}
	
	
	public LatencyLog(long timestamp, UUID playerUUID, long sendTime, long receiveTime, long roundTripTime) {
		super(timestamp);
		this.playerUUID = playerUUID;
		//this.sendTime = sendTime;
		//this.receiveTime = receiveTime;
		this.roundTripTime = roundTripTime;
	}
	

	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarLong(playerUUID.getMostSignificantBits());
		out.writeVarLong(playerUUID.getLeastSignificantBits());
		//out.writeVarLong(sendTime);
		//out.writeVarLong(receiveTime);
		out.writeVarLong(roundTripTime);
	}

	
	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		long mostSignificantBits = in.readVarLong();
		long leastSignificantBits = in.readVarLong();
		this.playerUUID = new UUID(mostSignificantBits, leastSignificantBits);
		//this.sendTime = in.readVarLong();
		//this.receiveTime = in.readVarLong();
		this.roundTripTime = in.readVarLong();
	}
	
	
	/*public long getSendTime() {
		return sendTime;
	}*/
	
	
	/*public long getReceiveTime() {
		return receiveTime;
	}*/
	
	
	public long getRoundTripTime() {
		return roundTripTime;
	}
	
	
	public UUID getPlayerUUID() {
		return this.playerUUID;
	}
	
}

package org.koekepan.herobrineplugin.logging.logs.packets;

import java.io.IOException;
import java.util.List;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;
import org.koekepan.herobrineplugin.logging.logs.TimestampLog;

import com.comphenix.protocol.events.PacketEvent;

public class ServerUpdateTimePacketLog extends TimestampLog {

	private int entityId;
	private long worldAge;
	private long timeOfDay;
	
	public ServerUpdateTimePacketLog() {
		this(-1, -1, 0, 0);
	}
	
	public ServerUpdateTimePacketLog(long timestamp, int entityId, long worldAge, long timeOfDay) {
		super(timestamp);
		this.entityId = entityId;
		this.worldAge = worldAge;
		this.timeOfDay = timeOfDay;
	}
	
	public ServerUpdateTimePacketLog(long timestamp, PacketEvent event) {
		super(timestamp);
		
		entityId = event.getPlayer().getEntityId();
		
		List<Long> longs = event.getPacket().getLongs().getValues();
		worldAge = longs.get(0);
		timeOfDay = longs.get(1);
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarInt(entityId);
		out.writeVarLong(worldAge);
		out.writeVarLong(timeOfDay);
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		entityId = in.readVarInt();
		worldAge = in.readVarLong();
		timeOfDay = in.readVarLong();
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public long getWorldAge() {
		return worldAge;
	}
	
	public long getTimeOfDay() {
		return timeOfDay;
	}

}

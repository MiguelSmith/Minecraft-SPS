package org.koekepan.herobrineplugin.logging.logs.events;

import java.io.IOException;

import org.bukkit.event.player.PlayerQuitEvent;
import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;
import org.koekepan.herobrineplugin.logging.logs.TimestampLog;

public class PlayerQuitEventLog extends TimestampLog {

	private int entityId;
	
	public PlayerQuitEventLog() {
		this(-1, -1);
	}
	
	public PlayerQuitEventLog(long timestamp, int entityId) {
		super(timestamp);
		this.entityId = entityId;
	}
	
	public PlayerQuitEventLog(long timestamp, PlayerQuitEvent event) {
		super(timestamp);
		this.entityId = event.getPlayer().getEntityId();
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarInt(entityId);
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		entityId = in.readVarInt();
	}
	
	public int getEntityId() {
		return entityId;
	}
	
}

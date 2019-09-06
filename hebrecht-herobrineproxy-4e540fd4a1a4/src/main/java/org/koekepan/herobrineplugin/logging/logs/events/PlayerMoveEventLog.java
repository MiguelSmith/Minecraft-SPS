package org.koekepan.herobrineplugin.logging.logs.events;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;
import org.koekepan.herobrineplugin.logging.logs.TimestampLog;

public class PlayerMoveEventLog extends TimestampLog {

	private UUID playerUUID;
	private double x, y, z;
	private float yaw, pitch;
	
	public PlayerMoveEventLog() {
		this(-1, new UUID(0, 0), 0, 0, 0, 0, 0);
	}
	
	public PlayerMoveEventLog(long timestamp, UUID playerUUID, double x, double y, double z, float yaw, float pitch) {
		super(timestamp);
		this.playerUUID = playerUUID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public PlayerMoveEventLog(long timestamp, PlayerMoveEvent event) {
		super(timestamp);
		playerUUID = event.getPlayer().getUniqueId();
		
		Location to = event.getTo();
		x = to.getX();
		y = to.getY();
		z = to.getZ();
		yaw = to.getYaw();
		pitch = to.getPitch();
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarLong(playerUUID.getMostSignificantBits());
		out.writeVarLong(playerUUID.getLeastSignificantBits());
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		long mostSignificantBits = in.readVarLong();
		long leastSignificantBits = in.readVarLong();
		this.playerUUID = new UUID(mostSignificantBits, leastSignificantBits);
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
	}
	
	public UUID getPlayerUUID() {
		return this.getPlayerUUID();
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
}

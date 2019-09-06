package org.koekepan.herobrineplugin.logging.logs.events;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;
import org.koekepan.herobrineplugin.logging.logs.TimestampLog;

public class PlayerJoinEventLog extends TimestampLog {

	private int entityId;
	private String name;
	private double x, y, z;
	private float yaw, pitch;
	private String host;
	private int port;
	
	public PlayerJoinEventLog() {
		this(-1, -1, "", 0, 0, 0, 0, 0, "", -1);
	}
	
	public PlayerJoinEventLog(long timestamp, int entityId, String name, double x, double y, double z, float yaw, float pitch, String host, int port) {
		super(timestamp);
		this.entityId = entityId;
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.host = host;
		this.port = port;
	}
	
	public PlayerJoinEventLog(long timestamp, PlayerJoinEvent event) {
		super(timestamp);
		
		Player player = event.getPlayer();
		
		entityId = player.getEntityId();
		name = player.getName();
		
		Location location = player.getLocation();
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		yaw = location.getYaw();
		pitch = location.getPitch();
		
		InetSocketAddress address = player.getAddress();
		host = address.getHostString();
		port = address.getPort();
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarInt(entityId);
		out.writeUTF(name);
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		out.writeUTF(host);
		out.writeVarInt(port);
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		entityId = in.readVarInt();
		name = in.readUTF();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
		host = in.readUTF();
		port = in.readVarInt();
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public String getName() {
		return name;
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
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
}

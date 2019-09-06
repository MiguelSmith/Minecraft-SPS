package org.koekepan.herobrineplugin.logging.logs.packets;

import java.io.IOException;
import java.util.List;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;
import org.koekepan.herobrineplugin.logging.logs.TimestampLog;

import com.comphenix.protocol.events.PacketEvent;

public class ClientPlayerPositionAndLookPacketLog extends TimestampLog {

	private int entityId;
	private double x, y, z;
	private float yaw, pitch;

	public ClientPlayerPositionAndLookPacketLog() {
		this(-1, -1, 0, 0, 0, 0, 0);
	}

	public ClientPlayerPositionAndLookPacketLog(long timestamp, int entityId, double x, double y, double z, float yaw, float pitch) {
		super(timestamp);
		this.entityId = entityId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public ClientPlayerPositionAndLookPacketLog(long timestamp, PacketEvent event) {
		super(timestamp);

		entityId = event.getPlayer().getEntityId();
		List<Double> doubles = event.getPacket().getDoubles().getValues();
		x = doubles.get(0);
		y = doubles.get(1);
		z = doubles.get(2);

		List<Float> floats = event.getPacket().getFloat().getValues();
		yaw = floats.get(0);
		pitch =  floats.get(1);
	}

	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeVarInt(entityId);
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		entityId = in.readVarInt();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
	}

	public int getEntityId() {
		return entityId;
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

package org.koekepan.herobrineproxy.session;

import java.util.UUID;

import org.koekepan.herobrineproxy.behaviour.BehaviourHandler;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public interface ISession {
	public void setUsername(String username);
	public String getUsername();
	public String getHost();
	public int getPort();
	
	public boolean isConnected();
	public void disconnect();
	
	public void sendPacket(Packet packet);
	public void packetReceived(Packet packet);
	public void setPacketBehaviours(BehaviourHandler<Packet> behaviours);
	void setPosition(Packet packet);
	void sendWithPosition(Packet packet, double x, double z, double prevX, double prevZ, int radius);
	public void setPositioned(boolean positioned);
	public boolean isPositioned();
	public void setEntityTracker(SPSEntityTracker spsEntityTracker);
	public void setPlayerUsername(int entityID, String username);
	public String getUsername(int entityID);
	public void setUUID(String username, UUID uuid);
	public UUID getUUID(String username);
}

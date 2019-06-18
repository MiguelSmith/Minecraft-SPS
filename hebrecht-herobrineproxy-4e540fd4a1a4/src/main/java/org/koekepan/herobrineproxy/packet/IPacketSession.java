package org.koekepan.herobrineproxy.packet;

import com.github.steveice10.packetlib.packet.Packet;

public interface IPacketSession {
	public void send(Packet packet);
	public void setChannel(String channel);
	public String getUsername();
	public void subscribeSession(String channel);
	public void unsubscribeSession(String channel);
	public void setLogin(boolean login);
	public boolean getLogin();
	public void updatePosition(Packet movementPacket);
}

package org.koekepan.herobrineproxy.packet;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

public class PacketSession implements IPacketSession {
	
	public Session session;
	
	public PacketSession(Session client) {
		this.session = client;
	}
	
	
	@Override
	public void send(Packet packet) {
		session.send(packet);
	}


	@Override
	public void setChannel(String channel) {		
	}
}

package org.koekepan.herobrineproxy.session;

import com.github.steveice10.packetlib.packet.Packet;

public interface IServerSession extends ISession {
	public void connect();	
	public void setJoined(boolean joined);
	void registerClientForChannels();
	public void setPosition(Packet responsePacket);
	
//	public boolean isMigrating();
//	public void setMigrating(boolean migrating);

//	public boolean hasJoined();
	//public PacketSession getPacketSession();
}
	

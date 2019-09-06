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
		//ConsoleIO.println("PacketSession::setChannel => You set the wrong session for the channel");	
	}


	@Override
	public void subscribeSession(String channel) {	
		//ConsoleIO.println("PacketSession::subscribeSession => You set the wrong session");
	}


	@Override
	public void unsubscribeSession(String channel) {		
	}


	@Override
	public void setLogin(boolean login) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean getLogin() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updatePosition(Packet movementPacket) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void moveEntity(Packet packet, int positionType) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void movePosition(Packet movementPacket) {
		// TODO Auto-generated method stub
		
	}
}

package org.koekepan.herobrineproxy.packet;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import com.github.steveice10.packetlib.packet.Packet;

public class SPSPacketSession implements IPacketSession {
	
	private String username;
	private String channel;
	
	private ISPSConnection session;
	
	public SPSPacketSession(ISPSConnection session) {
		this.session = session;
		
		sendSession();
	}
		
	
	public SPSPacketSession(ISPSConnection client, String username, String channel) {
		this.session = client;
		this.username = username;
		this.channel = channel;
		
		sendSession();
	}
	
	@Override
	public void send(Packet packet) {
		SPSPacket spsPacket = new SPSPacket(packet, username, channel);
		session.publish(spsPacket);
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public ISPSConnection getSession() {
		return this.session;
	}

	@Override
	public void subscribeSession(String channel) {
		ConsoleIO.println("Session to subscribe to : "+session.getClass().getSimpleName());
		session.subscribeToChannel(channel);	
	}
	
	private void sendSession () {
		this.session.receivePacketSession(this);
	}


	@Override
	public void unsubscribeSession(String channel) {
		session.unsubscribeFromChannel(channel);		
	}

}

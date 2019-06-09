package org.koekepan.herobrineproxy.packet;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class SPSPacketSession implements IPacketSession {
	
	private String username;
	private String channel;
	
	private int x;
	private int y;
	private int z;
	private int radius;
	
	private boolean login;
	
	private ISPSConnection session;
	
	public SPSPacketSession(ISPSConnection session) {
		this.session = session;
	}
		
	
	public SPSPacketSession(ISPSConnection client, String username, String channel) {
		this.session = client;
		this.username = username;
		this.channel = channel;
		
		this.radius = 80;
	}
	
	@Override
	public void send(Packet packet) {
		SPSPacket spsPacket = new SPSPacket(packet, username, x, z, radius, channel);
		session.publish(spsPacket);
	}
	
	
	public void setUsername(String username) {
		this.username = username;
		
		sendSession();
	}
	
	public String getUsername() {
		return username;
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
		session.subscribeToChannel(channel, username);	
	}
	
	private void sendSession () {
		this.session.receivePacketSession(this, username);
	}


	@Override
	public void unsubscribeSession(String channel) {
		session.unsubscribeFromChannel(channel, username);		
	}


	@Override
	public void setLogin(boolean login) {
		this.login = login;
	}


	@Override
	public boolean getLogin() {
		return this.login;
	}


	public void setPosition(ClientPlayerPositionRotationPacket responsePacket) {
		this.x = (int) responsePacket.getX();
		// currently only use two dimensions
		// this.y = (int) responsePacket.getY();
		this.z = (int) responsePacket.getZ();
		SPSPacket packet = new SPSPacket(responsePacket, username, x, z, radius, channel);
		session.move(packet);
	}

}

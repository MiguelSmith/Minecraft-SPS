package org.koekepan.herobrineproxy.packet;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
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
		
		this.radius = 10;
	}
		
	
	public SPSPacketSession(ISPSConnection client, String username, String channel) {
		this.session = client;
		this.username = username;
		this.channel = channel;
		
		this.radius = 10;
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


	public void setPosition(Packet responsePacket) {
		// TODO make this less rough
		try {
			ClientPlayerPositionRotationPacket packet = (ClientPlayerPositionRotationPacket) responsePacket;
			this.x = (int) packet.getX();
			// currently only use two dimensions
			this.y = (int) packet.getY();
			this.z = (int) packet.getZ();
			//ConsoleIO.println("SPSPacketSession::SetPosition -> Setting position to <" + packet.getX() + "," + packet.getY() + ">");
			SPSPacket spsPacket = new SPSPacket(packet, username, x, z, radius, channel);
			session.move(spsPacket);
		} catch (Exception e) {
			ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket) responsePacket;
			this.x = (int) packet.getX();
			// currently only use two dimensions
			this.y = (int) packet.getY();
			this.z = (int) packet.getZ();
			//ConsoleIO.println("SPSPacketSession::SetPosition -> Setting position to <" + packet.getX() + "," + packet.getY() + ">");
			SPSPacket spsPacket = new SPSPacket(packet, username, x, z, radius, channel);
			session.move(spsPacket);
		}
	}

}

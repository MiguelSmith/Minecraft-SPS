package org.koekepan.herobrineproxy.packet;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;
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
	private boolean positioned;
	
	private ISPSConnection session;
	private SPSEntityTracker entityTracker;
	
	public SPSPacketSession(ISPSConnection session) {
		this.session = session;
		
		this.radius = 65;
	}
		
	
	public SPSPacketSession(ISPSConnection client, String username, String channel) {
		this.session = client;
		this.username = username;
		this.channel = channel;
		
		this.radius = 65;
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
	
	public void setPositioned(boolean positioned) {
		this.positioned = positioned;
	}
	
	public boolean isPositioned() {
		return this.positioned;
	}
	
	public void updatePosition(Packet movementPacket) {
		// TODO make this less rough
		try {
			ClientPlayerPositionRotationPacket packet = (ClientPlayerPositionRotationPacket) movementPacket;
			x = (int) packet.getX();
			// currently only use two dimensions
			y = (int) packet.getY();
			z = (int) packet.getZ();
			//ConsoleIO.println("SPSPacketSession::SetPosition -> Setting position to <" + packet.getX() + "," + packet.getY() + ">");
		} catch (Exception e) {
			ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket) movementPacket;
			x = (int) packet.getX();
			// currently only use two dimensions
			y = (int) packet.getY();
			z = (int) packet.getZ();
			ConsoleIO.println("SPSPacketSession::SetPosition -> Setting position to <" + packet.getX() + "," + packet.getY() + ">");
		}
		this.positioned = true;
	}
	
	public void movePosition(Packet responsePacket) {
		updatePosition(responsePacket);	
		SPSPacket spsPacket = new SPSPacket(responsePacket, this.username, x, z, radius, channel);
		session.move(spsPacket);	
	}

	public void setPositionAndMove(Packet responsePacket) {
		updatePosition(responsePacket);		
		//ConsoleIO.println("SPSPacketSession::setPositionAndMove => sending move to SPS");
		sendMove(x, y, z, radius, channel, responsePacket);
	}
	
	private void sendMove(int x, int y, int z, int radius, String channel, Packet packet) {
		SPSPacket spsPacket = new SPSPacket(packet, this.username, x, z, radius, channel);
		session.move(spsPacket);
		session.publish(spsPacket);
	}


	public void sendWithPosition(Packet packet, double x, double z, double prevX, double prevZ, int radius) {
		//ConsoleIO.println("SPSPacketSession::sendWithPosition => publishing " + packet.getClass().getSimpleName() + " to <" + x + "," + z + "> for user " + this.username);
		SPSPacket spsPacket = new SPSPacket(packet, this.username, (int) x, (int) z, radius, this.channel);
		session.publish(spsPacket);
	}


	@Override
	public void moveEntity(Packet packet, int positionType) {
		entityTracker.move(packet, positionType);
	}


	public void setEntityTracker(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;		
	}
}

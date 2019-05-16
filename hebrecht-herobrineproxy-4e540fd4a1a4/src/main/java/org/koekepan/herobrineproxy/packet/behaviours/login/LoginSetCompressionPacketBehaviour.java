package org.koekepan.herobrineproxy.packet.behaviours.login;

import com.github.steveice10.packetlib.packet.Packet;


import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;

public class LoginSetCompressionPacketBehaviour implements Behaviour<Packet> {
	
	
	private IProxySessionNew proxySession;
	
	public LoginSetCompressionPacketBehaviour() {}
	
	
	public LoginSetCompressionPacketBehaviour(IProxySessionNew proxySession) {
		this.proxySession = proxySession;
	}
	
	
	@Override
	public void process(Packet packet) {
		ConsoleIO.println("LoginSetCompressionPacketBehaviour::process => Received setCompressionPacket");		
		proxySession.sendPacketToClient(packet);
	}
}
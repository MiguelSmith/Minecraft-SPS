package org.koekepan.herobrineproxy.packet.behaviours.client;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;

import com.github.steveice10.packetlib.packet.Packet;

public class ClientPlayerPositionPacketBehaviour implements Behaviour<Packet> {
	
	public IProxySessionNew proxySession;

	@SuppressWarnings("unused")
	private ClientPlayerPositionPacketBehaviour() {}
	
	public ClientPlayerPositionPacketBehaviour(IProxySessionNew proxySession) {
		this.proxySession = proxySession;
	}
	
	
	@Override
	public void process(Packet packet) {
		ConsoleIO.println("ClientPlayerPositionRotationPacketBehaviour::process => processing packet.");
		proxySession.setPosition(packet);
	}

}

package org.koekepan.herobrineproxy.packet.behaviours.client;

import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;

import com.github.steveice10.packetlib.packet.Packet;

public class ClientPlayerPositionPacketBehaviour implements Behaviour<Packet> {
	
	public IProxySessionNew proxySession;

	@SuppressWarnings("unused")
	private ClientPlayerPositionPacketBehaviour() {
		// TODO Auto-generated constructor stub
	}
	
	public ClientPlayerPositionPacketBehaviour(IProxySessionNew proxySession) {
		this.proxySession = proxySession;
	}
	
	
	@Override
	public void process(Packet packet) {
		proxySession.setPosition(packet);
	}

}

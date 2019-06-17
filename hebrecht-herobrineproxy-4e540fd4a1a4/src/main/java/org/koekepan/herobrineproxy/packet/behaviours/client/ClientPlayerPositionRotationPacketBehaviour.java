package org.koekepan.herobrineproxy.packet.behaviours.client;

import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;

import com.github.steveice10.packetlib.packet.Packet;

public class ClientPlayerPositionRotationPacketBehaviour implements Behaviour<Packet>{
	
	private IProxySessionNew proxySession;
	
	@SuppressWarnings("unused")
	public ClientPlayerPositionRotationPacketBehaviour() {}
	
	public ClientPlayerPositionRotationPacketBehaviour(IProxySessionNew proxySession) {
		this.proxySession = proxySession;
	}

	@Override
	public void process(Packet packet) {
		proxySession.setPosition(packet);
	}

}

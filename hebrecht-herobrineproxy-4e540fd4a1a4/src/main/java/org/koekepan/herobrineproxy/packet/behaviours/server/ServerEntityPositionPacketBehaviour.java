package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.IServerSession;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityPositionPacketBehaviour implements Behaviour<Packet> {
	private SPSEntityTracker entityTracker; 
	private IProxySessionNew proxySession;
	private IServerSession serverSession;
	
	@SuppressWarnings("unused")
	public ServerEntityPositionPacketBehaviour() {}
	
	public ServerEntityPositionPacketBehaviour(SPSEntityTracker entityTracker,IProxySessionNew proxySession, IServerSession serverSession) {
		ConsoleIO.println("ServerEntityPositionPacketBehaviour::Constructor -> created behaviour with " + entityTracker.getClass().getSimpleName() + " proxySession: " + proxySession + " serverSession: " + serverSession);
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ConsoleIO.println("ServerEntityPositionPacketBehaviour::process -> received packet. Publishing to position");
		ServerEntityPositionPacket p = (ServerEntityPositionPacket) packet;
		int entityID = p.getEntityId();
		SPSEntity entity = new SPSEntity(entityID, (int) p.getMovementX(), (int) p.getMovementY(), (int) p.getMovementZ());
		entityTracker.updateEntity(entityID, entity, packet);
	}

}

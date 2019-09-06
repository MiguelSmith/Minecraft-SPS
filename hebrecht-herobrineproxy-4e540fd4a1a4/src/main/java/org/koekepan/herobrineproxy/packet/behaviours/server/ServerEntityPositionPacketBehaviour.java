package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySession;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityPositionPacketBehaviour implements Behaviour<Packet> {
	private SPSEntityTracker entityTracker; 
	private IProxySessionNew proxySession;
	
	@SuppressWarnings("unused")
	public ServerEntityPositionPacketBehaviour() {}
	
	public ServerEntityPositionPacketBehaviour(SPSEntityTracker entityTracker, IProxySessionNew session) {
		this.entityTracker = entityTracker;
		this.proxySession = session;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityPositionPacket p = (ServerEntityPositionPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			if (entity != null ) {
				//ConsoleIO.println("Received ServerEntityPositionPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
				entity.move(p.getMovementX(), p.getMovementY(),p.getMovementZ());
				entityTracker.updateEntity(entityID, entity, packet);
			} else {
				entityTracker.forwardPacketWithPosition(packet, 0, 0, 65);
			}
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityPositionPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}

}

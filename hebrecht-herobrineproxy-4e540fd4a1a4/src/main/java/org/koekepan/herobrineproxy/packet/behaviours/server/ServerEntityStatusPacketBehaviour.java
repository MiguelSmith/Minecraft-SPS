package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityStatusPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityStatusPacketBehaviour() {}
	
	public ServerEntityStatusPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityStatusPacket p = (ServerEntityStatusPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			if (entity != null) {
				//ConsoleIO.println("Received ServerEntityVelocityPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
				entityTracker.updateEntity(entityID, entity, packet);
			} else {
				entityTracker.forwardPacketWithPosition(p, 0, 0, 65);
			}
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityStatusPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}
}

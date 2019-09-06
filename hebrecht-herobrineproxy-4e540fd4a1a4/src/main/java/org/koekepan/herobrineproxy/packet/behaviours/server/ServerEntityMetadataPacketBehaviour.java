package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityMetadataPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityMetadataPacketBehaviour() {}
	
	public ServerEntityMetadataPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityMetadataPacket p = (ServerEntityMetadataPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			if (entity != null) {
				//ConsoleIO.println("Received ServerEntityMetadataPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
				entityTracker.updateEntity(entityID, entity, packet);
			} else {
				entityTracker.forwardPacketWithPosition(packet, 0, 0, 65);
			}
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityMetadataPacket::process -> Entity " + entityID + " could not be found.");
		}
	}

}

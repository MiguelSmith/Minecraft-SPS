package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityPositionPacketBehaviour implements Behaviour<Packet> {
	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityPositionPacketBehaviour() {}
	
	public ServerEntityPositionPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityPositionPacket p = (ServerEntityPositionPacket) packet;
		int entityID = p.getEntityId();
		try {
			//SPSEntity entity = entityTracker.getEntity(entityID);
			//ConsoleIO.println("Received ServerEntityPositionPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			//entity.move(p.getMovementX(), p.getMovementY(),p.getMovementZ());
			SPSEntity entity = new SPSEntity(entityID,p.getMovementX(), p.getMovementY(),p.getMovementZ());
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityPositionPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}

}

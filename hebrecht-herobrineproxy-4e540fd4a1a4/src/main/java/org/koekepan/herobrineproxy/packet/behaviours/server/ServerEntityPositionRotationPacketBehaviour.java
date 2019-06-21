package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityPositionRotationPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityPositionRotationPacketBehaviour() {}
	
	public ServerEntityPositionRotationPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityPositionRotationPacket p = (ServerEntityPositionRotationPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			//ConsoleIO.println("Received ServerEntityPositionRotationPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			entity.setPitch(p.getPitch());
			entity.setYaw(p.getYaw());
			entity.move(p.getMovementX(), p.getMovementY(),p.getMovementZ());
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityPositionRotationPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}
}

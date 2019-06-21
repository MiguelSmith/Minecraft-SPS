package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityRotationPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityRotationPacketBehaviour() {}
	
	public ServerEntityRotationPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityRotationPacket p = (ServerEntityRotationPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			//ConsoleIO.println("Received ServerEntityRotationPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			entity.setVelocity(p.getMovementX(), p.getMovementY(), p.getMovementZ());
			entity.setPitch(p.getPitch());
			entity.setYaw(p.getYaw());
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityRotationPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}
}

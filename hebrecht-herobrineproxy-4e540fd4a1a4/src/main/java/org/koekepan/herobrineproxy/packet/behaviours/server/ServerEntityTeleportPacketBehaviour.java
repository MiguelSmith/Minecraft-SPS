package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityTeleportPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityTeleportPacketBehaviour() {}
	
	public ServerEntityTeleportPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityTeleportPacket p = (ServerEntityTeleportPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			entity.setX(p.getX());
			entity.setY(p.getY());
			entity.setZ(p.getZ());
			entity.setPitch(p.getPitch());
			entity.setYaw(p.getYaw());
			//ConsoleIO.println("Received ServerEntityTeleportPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityTeleportPacket::process -> Entity " + entityID + " could not be found.");
		}
	}
}

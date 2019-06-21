package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityHeadLookPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityHeadLookPacketBehaviour() {}
	
	public ServerEntityHeadLookPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityHeadLookPacket p = (ServerEntityHeadLookPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			//ConsoleIO.println("Received ServerEntityHeadLookPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			entity.setHeadYaw(p.getHeadYaw());
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityHeadLookPacket::process -> Entity " + entityID + " could not be found.");
		}
	}
}

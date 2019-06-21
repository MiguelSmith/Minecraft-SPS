package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityEffectPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityEffectPacketBehaviour() {}
	
	public ServerEntityEffectPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityEffectPacket p = (ServerEntityEffectPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			//ConsoleIO.println("Received ServerEntityEffectPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityEffectPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}
}

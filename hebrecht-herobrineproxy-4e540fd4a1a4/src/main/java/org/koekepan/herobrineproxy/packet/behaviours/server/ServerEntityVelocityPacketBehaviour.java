package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityVelocityPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityVelocityPacketBehaviour() {}
	
	public ServerEntityVelocityPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityVelocityPacket p = (ServerEntityVelocityPacket) packet;
		int entityID = p.getEntityId();
		try {
			SPSEntity entity = entityTracker.getEntity(entityID);
			//ConsoleIO.println("Received ServerEntityVelocityPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
			entity.setVelocity(p.getMotionX(), p.getMotionY(), p.getMotionZ());
			entityTracker.updateEntity(entityID, entity, packet);
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityVelocityPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}
}

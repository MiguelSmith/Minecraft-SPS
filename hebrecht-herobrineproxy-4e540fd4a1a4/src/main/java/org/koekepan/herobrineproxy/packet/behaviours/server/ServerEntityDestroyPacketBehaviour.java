package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerEntityDestroyPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerEntityDestroyPacketBehaviour() {}
	
	public ServerEntityDestroyPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerEntityDestroyPacket p = (ServerEntityDestroyPacket) packet;
		int[] entityID = p.getEntityIds();
		try {
			for (int i = 0; i < entityID.length; i++) {
				int j = entityID[i];
				SPSEntity entity = entityTracker.getEntity(j);
				//ConsoleIO.println("Received ServerEntityPositionRotationPacket  <"+ entity.getX()+","+entity.getY()+","+ entity.getZ()+">");
				entityTracker.updateEntity(j, entity, packet);
			}
		} catch (Exception e) {
			ConsoleIO.println("ServerEntityPositionRotationPacketBehaviour::process -> Entity " + entityID + " could not be found.");
		}
	}
}

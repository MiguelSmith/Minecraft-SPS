package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerSpawnMobPacketBehaviour implements Behaviour<Packet> {
	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerSpawnMobPacketBehaviour() {}
	
	public ServerSpawnMobPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerSpawnMobPacket p = (ServerSpawnMobPacket) packet;
		int entityID = p.getEntityId();
		ConsoleIO.println("Received ServerSpawnMobPacket <"+ p.getX() +","+p.getY()+","+ p.getZ()+">");
		SPSEntity entity = new SPSEntity(entityID, p.getUUID(), p.getType(), p.getX(), p.getY(), p.getZ(), p.getYaw(),  p.getPitch(), p.getHeadYaw(), p.getMotionX(), p.getMotionY(), p.getMotionZ());
		entityTracker.updateEntity(entityID, entity, packet);
	}

}

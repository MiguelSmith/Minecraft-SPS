package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.SPSEntity;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerSpawnPlayerPacketBehaviour implements Behaviour<Packet> {
private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerSpawnPlayerPacketBehaviour() {}
	
	public ServerSpawnPlayerPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerSpawnPlayerPacket p = (ServerSpawnPlayerPacket) packet;
		int entityID = p.getEntityId();
		//ConsoleIO.println("ServerSpawnPlayerPacket::process => Spawn " + entityID + " at <"+ p.getX() +","+p.getY()+","+ p.getZ()+">");
		SPSEntity entity = new SPSEntity(entityID, p.getUUID(), null, p.getX(), p.getY(), p.getZ(), p.getYaw(),  p.getPitch(), 0, 0, 0, 0);
		entityTracker.setPlayerID(entityID, entity, packet);
		entityTracker.setUUID(entityTracker.getPlayerUsername(entityID), p.getUUID());
	}
}

package org.koekepan.herobrineproxy.packet.behaviours.server;

import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayBuiltinSoundPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerPlayBuiltinSoundPacketBehaviour implements Behaviour<Packet> {

	private SPSEntityTracker entityTracker; 
	
	@SuppressWarnings("unused")
	public ServerPlayBuiltinSoundPacketBehaviour() {}
	
	public ServerPlayBuiltinSoundPacketBehaviour(SPSEntityTracker entityTracker) {
		this.entityTracker = entityTracker;
	}

	@Override
	public void process(Packet packet) {
		ServerPlayBuiltinSoundPacket p = (ServerPlayBuiltinSoundPacket) packet;
		entityTracker.forwardPacketWithPosition(packet, (int) p.getX(), (int) p.getZ(), 0);
	}

}

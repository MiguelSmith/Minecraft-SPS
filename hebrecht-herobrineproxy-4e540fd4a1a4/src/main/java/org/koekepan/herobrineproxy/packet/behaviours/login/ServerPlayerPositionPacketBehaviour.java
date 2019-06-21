package org.koekepan.herobrineproxy.packet.behaviours.login;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;


import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.behaviour.Behaviour;
import org.koekepan.herobrineproxy.session.IProxySessionNew;

public class ServerPlayerPositionPacketBehaviour implements Behaviour<Packet> {
		
	private IProxySessionNew proxySession;
	
	@SuppressWarnings("unused")
	private ServerPlayerPositionPacketBehaviour() {}
	
	
	public ServerPlayerPositionPacketBehaviour(IProxySessionNew proxySession) {
		//ConsoleIO.println("ServerPlayerPositionPacketBehaviour -> setting proxy session to " + proxySession.getClass().getSimpleName());
		this.proxySession = proxySession;
	}

	
	@Override
	public void process(Packet packet) {
		ServerPlayerPositionRotationPacket p = (ServerPlayerPositionRotationPacket) packet;
		//ConsoleIO.println("ServerPlayerPositionRotationPacket::process => Player \""+proxySession.getUsername()+"\" received location: "+p.toString());		
		ClientPlayerPositionRotationPacket responsePacket = new ClientPlayerPositionRotationPacket(true, p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
		proxySession.sendPacketToServer(responsePacket);
		//proxySession.setPosition(p);
		proxySession.sendPacketToClient(packet);
		proxySession.setPacketSPSForwardingBehaviour();
	}
}
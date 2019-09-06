package org.koekepan.herobrineproxy.session;


import java.util.UUID;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.packet.behaviours.ClientSessionPacketBehaviours;
import org.koekepan.herobrineproxy.packet.behaviours.ServerSessionPacketBehaviours;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSEntityTracker;
import org.koekepan.herobrineproxy.sps.SPSPacket;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ClientToSPSProxy implements IProxySessionNew {

	ISession clientSession;
	IServerSession serverSession;
	ISPSConnection spsConnection;
	
	ClientSessionPacketBehaviours clientPacketBehaviours;
	ServerSessionPacketBehaviours serverPacketBehaviours;
	
	public ClientToSPSProxy(ISession clientSession, ISPSConnection spsConnection) {
		this.spsConnection = spsConnection;
		this.clientSession = clientSession;
		this.serverSession = new SPSSession(spsConnection);
		this.clientPacketBehaviours = new ClientSessionPacketBehaviours(this);
		this.clientPacketBehaviours.registerDefaultBehaviours(clientSession);
		this.clientPacketBehaviours.registerForwardingBehaviour();
	//	ConsoleIO.println("ClientToSPSProxy::setting packet behaviours of client session");		
		this.clientSession.setPacketBehaviours(this.clientPacketBehaviours);
	}
	
		
	@Override
	public String getUsername() {
		return clientSession.getUsername();
	}
	
	
	@Override
	public void setUsername(String username) {
		clientSession.setUsername(username);
		serverSession.setUsername(username);
	}

	@Override
	public void sendPacketToClient(Packet packet) {
	//	ConsoleIO.println("ClientToSPSProxy::sendPacketToClient => Sending packet <"+packet.getClass().getSimpleName()+"> to client <"+clientSession.getHost()+":"+clientSession.getPort()+">");
//		clientSession.packetReceived(packet);
		clientSession.sendPacket(packet);
	}

	
	@Override
	public void sendPacketToServer(Packet packet) {
	//	ConsoleIO.println("ClientToSPSProxy::sendPacketToServer => Sending packet <"+packet.getClass().getSimpleName()+"> to server <"+serverSession.getHost()+":"+serverSession.getPort()+">");		
		serverSession.sendPacket(packet);
	}
	
	

	@Override
	public void setServerHost(String host) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void setServerPort(int port) {
		// TODO Auto-generated method stub
	}


	@Override
	public void connect(String host, int port) {
		ConsoleIO.println("ClientToSPSProxy::connect => Player <"+getUsername()+"> is connecting to server <"+host+":"+port+">");
		this.spsConnection.addListener(this.clientSession);
		this.clientPacketBehaviours.registerForwardingBehaviour();

		this.serverPacketBehaviours = new ServerSessionPacketBehaviours(this, serverSession);
		this.serverPacketBehaviours.registerForwardingBehaviour();
		this.serverSession.setPacketBehaviours(serverPacketBehaviours);	
		serverSession.connect();
	}
	
	
	@Override
	public boolean isConnected() {
		return serverSession.isConnected();
	}
	
	
	@Override
	public void disconnect() {
		disconnectFromServer();
		disconnectFromClient();
	}
	
	
	@Override
	public void disconnectFromServer() {
		//if (isConnected() ) {
			serverSession.disconnect();
		//}
	}
	
	
	private void disconnectFromClient() {
		if (clientSession.isConnected()) {
			clientSession.disconnect();
		}
	}


	@Override
	public String getServerHost() {
		return serverSession.getHost();
	}


	@Override
	public int getServerPort() {
		return serverSession.getPort();
	}


	@Override
	public void migrate(String host, int port) {
		ConsoleIO.println("ClientToSPSProxy::migrate => Migrating player <"+getUsername()+"> to new server <"+host+":"+port+">");
		//newServerSession = new ServerSession(getUsername(), host, port);
		//this.newServerPacketBehaviours = new ServerSessionPacketBehaviours(this, newServerSession);
		//this.newServerPacketBehaviours.registerMigrationBehaviour();
		//this.newServerSession.setPacketBehaviours(newServerPacketBehaviours);	
		//newServerSession.connect();
	}
	
	
	@Override
	public void switchServer() {
		//serverSession = newServerSession;
		//this.serverPacketBehaviours.clearBehaviours();
		//this.serverPacketBehaviours = this.newServerPacketBehaviours;
		//this.newServerPacketBehaviours = null;
	}
	
	
	@Override 
	public void setPacketForwardingBehaviour() {
		this.clientPacketBehaviours.registerForwardingBehaviour();
		this.serverPacketBehaviours.registerForwardingBehaviour();
	}
	
	
	@Override 
	public void registerForPluginChannels() {
		this.serverSession.registerClientForChannels();
	}


	@Override
	public void setPosition(Packet responsePacket) {
		serverSession.setPosition(responsePacket);
	}


	@Override
	public void setPacketSPSForwardingBehaviour() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setPlayerUsername(int entityID) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getPlayerUsername(int entityID) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setUUID(String username, UUID uuid) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public UUID getUUID(String username) {
		// TODO Auto-generated method stub
		return null;
	}
}

package org.koekepan.herobrineproxy.session;


import org.koekepan.herobrineproxy.packet.PacketHandler;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionListener;

public interface IProxySessionOld {
	
	
	public String getUsername();
	public String getHost();
	public int getPort();

	
	public void setUsername(String username);
	public void setHost(String host);
	public void setPort(int port);

	
	public void connect(String host, int port);
	public boolean isConnected();
	public void disconnect();
	
	
	public void migrate(String host, int port); 
	public boolean isMigrating();
	public void setMigrating(boolean migrating);

	public Session getClient();
	public void setClient(Session client);
	
	public Session getServer();
	public void setServer(Session server);


	public SessionListener getClientPacketForwarder();
	public void setClientPacketForwarder(SessionListener forwarder);
	
	public SessionListener getServerPacketForwarder();
	public void setServerPacketForwarder(SessionListener forwarder);
}

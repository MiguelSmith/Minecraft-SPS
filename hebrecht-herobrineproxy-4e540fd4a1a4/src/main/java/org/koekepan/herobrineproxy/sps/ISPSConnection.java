package org.koekepan.herobrineproxy.sps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.koekepan.herobrineproxy.packet.IPacketSession;
import org.koekepan.herobrineproxy.packet.PacketListener;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.ISession;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;


/* SPS javascript server

API	to receive message from SPS
	: "type"
	: "publication"

API to send message to SPS
	: "server connected"
	: "client connected"
	: "disconnect"
	: "publish" // spatial publish
	: "publish packet" // send packet to specific channel
	: "function" : "sub", "usub" // spatial publish subscribe
	: "move"
	*/

public interface ISPSConnection {
	
	public String getHost();
	public int getPort();
	
	public void connect(String type);
	public void disconnect();
	// Receiving functions
	public SPSPacket receivePublication(Object... data);  // "publication"
	public void receiveConnectionID(String connection); // "type"

	
	// Sending functions
	public void subscribeToChannel(String channel, String username); // "subscribe"
	public void subscribeToChannel(String channel, Integer username);
	public void subscribeToArea(String channel, String username, int x, int y, int AoI); // "subscribe"
	public void unsubscribeFromChannel(String channel, String username); // "unsubscribe"
	public void unsubscribeFromArea(String channel);
	public void publish(SPSPacket packet);
	
	// functional functions
	public void addListener(ISession listener);
	public void removeListener(ISession listener);
	public void receivePacketSession(IPacketSession session, String username);
	public void setType(String type);
	public String getType();
	public void checkLobbyConnection();
	public void move(SPSPacket packet);
	public void setPlayerUsername(int entityID, String username);
	public String getPlayerUsername(int entityID);
	public void setUUID(String username, UUID uuid);
	public UUID getUUID(String username);
}

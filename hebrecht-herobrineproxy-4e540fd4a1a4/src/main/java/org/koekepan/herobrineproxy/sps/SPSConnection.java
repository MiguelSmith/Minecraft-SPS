package org.koekepan.herobrineproxy.sps;

import java.io.Console;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.SPSProxyProtocol;
import org.koekepan.herobrineproxy.packet.EstablishConnectionPacket;
import org.koekepan.herobrineproxy.packet.IPacketSession;
import org.koekepan.herobrineproxy.session.IProxySessionConstructor;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.ISession;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetInput;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.gson.Gson;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SPSConnection implements ISPSConnection {
	
	int SPSPort;
	String SPSHost;
	private Socket socket;
	private int connectionID;
	private String type;
	private int numLogins;
	private Map<Integer, String> players;
	private Map<String, UUID> uuids;
	
	private SPSProxyProtocol protocol;
	//private PacketProtocol protocol;
	private Map<String, ISession> listeners = new HashMap<String, ISession>();
	private Map<String,IPacketSession> sessions = new HashMap<String, IPacketSession>();
	private Map<String, Integer> clientIDs = new HashMap<String, Integer>();
	private IProxySessionConstructor sessionConstructor;
	static Logger logger = LogManager.getLogger(SPSConnection.class);
	//private IPacketSession packetSession;
	
	public SPSConnection(String SPSHost, int SPSPort) {
		this.SPSHost = SPSHost;
		this.SPSPort = SPSPort;
		this.protocol = new SPSProxyProtocol();
		numLogins = 0;
		players = new HashMap<>();
		uuids = new HashMap<>();
	}
	
	
	public SPSConnection(String SPSHost, int SPSPort, IProxySessionConstructor sessionConstructor) {
		this(SPSHost, SPSPort);
		this.sessionConstructor = sessionConstructor;
	}


	private boolean initializeConnection() {
		String URL = "http://"+this.SPSHost+":"+this.SPSPort;
		boolean result = false;
		try {
			this.socket = IO.socket(URL);
			result = true;
			if (type == "server") {
				ConsoleIO.println("publish");
				this.socket.emit("type", type);
			}
		} catch (URISyntaxException e) {
				e.printStackTrace();
		}
		return result;
	}

	public void initialiseListeners() {

		socket.on("ID", new Emitter.Listener() {
			@Override
			public void call(Object... data) {
				receiveConnectionID((int) data[0]);
			}
		});
		
		socket.on("type", new Emitter.Listener() {
			@Override
			public void call(Object... data) {
				ConsoleIO.println("type: " + type);
				receiveConnectionID((int) data[0]);
				clientIDs.put(type, connectionID);
				if (type == "server") {
					subscribeToChannel("lobby", type);
				} else {
					subscribeToChannel("lobby", connectionID);					
				}
			}
		});
		
		socket.on("join", new Emitter.Listener() {
			
			@Override
			public void call(Object... data) {
				ConsoleIO.println("Join received with data: " + data);
				clientIDs.put((String) data[0],(Integer) data[1]);
			}
		});

		socket.on("publication", new Emitter.Listener() {
			@Override
			public void call(Object... data) {
				SPSPacket packet = receivePublication(data);
				String username = packet.username;
				//int x = packet.x;
				//int y = packet.y;
				//int radius = packet.radius;
				//ConsoleIO.println("SPSConnection::publication => Received publication from " + packet.username + " on channel " + packet.channel + " of type <" + packet.packet.getClass().getSimpleName() + ">");
				
				// TODO: look at possible polymorphic implementation of this. Create new interface to handle publish-time packet behaviours
				// eg. packet.publish() -> do checks or nothing depending on packet type instead of "instanceof" implementation
				if (packet.packet instanceof EstablishConnectionPacket) {
					EstablishConnectionPacket loginPacket = (EstablishConnectionPacket)packet.packet;
					//LoginStartPacket loginPacket = (LoginStartPacket)packet.packet;
					username = loginPacket.getUsername();
					if (loginPacket.establishConnection()) {
						logger.error(loginPacket.getUsername() + " connected");
						ConsoleIO.println("SPSConnection::publication Must establish new connection for session <"+username+">");
							IProxySessionNew proxySession = sessionConstructor.createProxySession(username);
							String host = proxySession.getServerHost();
							int port = proxySession.getServerPort();
							proxySession.connect(host, port);
					} else {
						logger.debug(loginPacket.getUsername() + " disconnected");
						ConsoleIO.println("SPSConnection::publication Must disconnect session of user <"+username+">");
						IProxySessionNew proxySession = sessionConstructor.getProxySession(username);	
						if (proxySession != null) {
							proxySession.disconnect();
						} else {
							ConsoleIO.println("SPSConnection::publication => Received a packet (" + packet.packet.getClass().getSimpleName() + ") for an unknown session <"+username+">");
						}
					}
				} else if (listeners.containsKey(username)) {					
					//listeners.get(username).packetReceived(packet.packet);
					//ConsoleIO.println("SPSConnection::publication => Sending packet <"+packet.packet.getClass().getSimpleName()+"> for player <"+username+"> at <"+x+":"+y+":"+radius+"> to the client session");
				
					if (!sessions.get(username).getLogin()) {
						ConsoleIO.println("Login: " + sessions.get(username).getLogin());
						if (packet.packet instanceof ServerJoinGamePacket) {
							ConsoleIO.println("SPSConnection::publication Received ServerJoinGame packet. Subscribe to 'ingame'");
							subscribeToChannel(username, username);
							sessions.get(username).setChannel(username);
						} else if (packet.packet instanceof ServerChatPacket) { 
							// the last single "login" packet received by client before chunk data starts flowing in
							numLogins--;
							if (numLogins == 0) {
								ConsoleIO.println("No more logins to process. Unsubscribe from lobby channel");
								unsubscribeFromChannel("lobby", type);
							}
							sessions.get(username).setLogin(true);
						}
					} else if (packet.packet instanceof ClientPlayerPositionRotationPacket || packet.packet instanceof ClientPlayerPositionPacket) {
						sessions.get(username).movePosition(packet.packet);
					}
					
					listeners.get(username).sendPacket(packet.packet);
				} else {
					ConsoleIO.println("SPSConnection::publication => Received a packet (" + packet.packet.getClass().getSimpleName() + ") for an unknown session <"+username+">");
				}
			}
		});
	}


	@Override
	public void connect(String type) {
		setType(type);
		if (initializeConnection()) {
			initialiseListeners();
			socket.connect();
		}
	}

	
	@Override
	public void disconnect() {
		socket.disconnect();
	}

	
	private Packet retrievePacket(String publication) {
		Gson gson = new Gson();
		Packet packet = null;
		try {
			//ConsoleIO.println(data[0].toString());
			byte[] payload = gson.fromJson(publication, byte[].class);
			packet = this.bytesToPacket(payload);
		} catch (Throwable e) {
			ConsoleIO.println(e.toString());
		}
		//ConsoleIO.println("SPSConnection::retrievePacket => Retrieved packet <"+packet.getClass().getSimpleName()+">");
		return packet;
	}
	
	
	@Override
	public SPSPacket receivePublication(Object... data) {
		int connectionID = (int)data[0];
		String username = (String)data[1];
		int x = (int)data[2];
		int y = (int)data[3];
		int radius = (int)data[4];
		String publication = data[5].toString();
		String channel = (String)data[6];
		Packet packet = retrievePacket(publication);
		SPSPacket spsPacket = new SPSPacket(packet, username, x, y, radius, channel);
		return spsPacket;
	}

	
	
	@Override
	public void receiveConnectionID(int connectionID) {
		this.connectionID = connectionID;
		ConsoleIO.println("Received connectionID: <"+connectionID+">");
	}
	
	
	@Override
	public void publish(SPSPacket packet) {		
		//convert to JSON
		Gson gson = new Gson();
		byte[] payload = this.packetToBytes(packet.packet);
		String json = gson.toJson(payload);
		
		try {
			IPacketSession session = this.sessions.get(packet.username);
			
			if (!session.getLogin()) {
				
				switch (type) {
				case "client": {
						// Do we need to do something for client here?
				}
					break;
					
				case "server" : {
					ConsoleIO.println("Login: " + session.getLogin());
					if (packet.packet instanceof ServerPlayerListEntryPacket) {
						ConsoleIO.println("SPSConnection::publish Changing channel to 'ingame' " + session.getClass().getSimpleName());
						session.setChannel(session.getUsername()); // look at changing channel implementation to packet specific channels instead of packet session specific channel 
						subscribeToChannel(session.getUsername(), session.getUsername());
						session.setLogin(true);
					}	
				}
					break;
	
				default:
					ConsoleIO.println("Type has not yet been defined or has been corrupted.");
					break;
				}
			}
		} catch (Exception e) {
			ConsoleIO.println("Packet session has not yet been initialised.");
		} finally {
			if (type == "client") {
				//ConsoleIO.println("Connection <"+clientIDs.get(packet.username)+"> sent packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+"> to player " + packet.username);
			}
			
			socket.emit("publish", clientIDs.get(packet.username), packet.username, packet.x, packet.y, packet.radius, json, packet.channel, packet.packet.getClass().getSimpleName());			
		}
	}
	
	public void move(SPSPacket packet) {
		//ConsoleIO.println("SPSConnection::move -> Moving to <" + packet.x + "," + packet.y + ">");
		//convert to JSON
		Gson gson = new Gson();
		byte[] payload = this.packetToBytes(packet.packet);
		String json = gson.toJson(payload);
		socket.emit("move", clientIDs.get(packet.username), packet.username, packet.x, packet.y, packet.radius, json, packet.channel, packet.packet.getClass().getSimpleName());
	}


	@Override
	public void subscribeToChannel(String channel, String username) {
		ConsoleIO.println("Subscribing to channel " + channel);
		socket.emit("subscribe", clientIDs.get(username), channel, username);
	}
	
	@Override
	public void subscribeToChannel(String channel, Integer connectionID) {
		ConsoleIO.println("Subscribing to channel " + channel);
		socket.emit("subscribe", connectionID, channel, type);
	}

	@Override
	public void subscribeToArea(String channel, String username, int x, int y, int AoI) {
		ConsoleIO.println("SPSConnection::subscribetoArea => Subscribing to <" + x + "," + y + "> with an area of " + AoI + " on channel " + channel);
		socket.emit("subscribe", clientIDs.get(username), channel, username, x, y, AoI);
	}

	@Override
	public void unsubscribeFromChannel(String channel, String username) {
		ConsoleIO.println("SPSConnection::unsubscribeFromChannel => Unsubscribing from channel " + channel);
		
		socket.emit("unsubscribe", connectionID, channel, username);
	}


	@Override
	public void unsubscribeFromArea(String channel) {
		ConsoleIO.println("SPSConnection::unsubscribeFromArea => unsubscribing from " + channel);
		socket.emit("unsubscribe", channel);
	}

		
	private byte[] packetToBytes(Packet packet) {
		ByteBuffer buffer = ByteBuffer.allocate(75000);
		ByteBufferNetOutput output = new ByteBufferNetOutput(buffer);
		
		int packetId = protocol.getOutgoingId(packet.getClass());
		try {
			protocol.getPacketHeader().writePacketId(output, packetId);
			packet.write(output);
		} catch (Exception e) {
			ConsoleIO.println("Exception: "+e.toString());
		}
		byte[] payload = new byte[buffer.position()];
		buffer.flip();
		buffer.get(payload);
		return payload;
	}
	
	
	private Packet bytesToPacket(byte[] payload) {
		ByteBuffer buffer = ByteBuffer.wrap(payload);
		ByteBufferNetInput input = new ByteBufferNetInput(buffer);
		Packet packet = null;
		try {
			int packetId = protocol.getPacketHeader().readPacketId(input);
			//ConsoleIO.println("SPSConnection::byteToPacket => Protocol status <"+protocol.getSubProtocol().toString()+">");
			packet = protocol.createIncomingPacket(packetId);
			packet.read(input);
			
		} catch (Exception e) {
			ConsoleIO.println("Exception: "+e.toString());
		}
		return packet;
	}


	@Override
	public void addListener(ISession listener) {
		String username = listener.getUsername();
		ConsoleIO.println("SPSConnection::addListener => adding listener " + username);
		listeners.put(username, listener);
	}
	
	
	@Override
	public void removeListener(ISession listener) {
		String username = listener.getUsername();
		listeners.remove(username);
	}


	@Override
	public String getHost() {
		return this.SPSHost;
	}


	@Override
	public int getPort() {
		return this.SPSPort;
	}


	@Override
	public void receivePacketSession(IPacketSession session, String username) {
		ConsoleIO.println("SPSConnection::receivePacketSession => Received packet session " + session.getClass().getSimpleName() + " for username " + username);
		this.sessions.put(username, session);
		socket.emit("join", username, type);
	}


	@Override
	public void setType(String type) {
		ConsoleIO.println("SPSConnection::setType => " + type);
		this.type = type;
	}
	
	@Override
	public String getType() {
		return this.type;
	}


	@Override
	public void checkLobbyConnection() {
		if (numLogins == 0) {
			ConsoleIO.println("Not connected to lobby");
			socket.emit("type", type);
		}
		numLogins++;
	}


	@Override
	public void setPlayerUsername(int entityID, String username) {
		//ConsoleIO.println("SPSConnection::setPlayerUsername => setting ID <" + entityID + "> to username " + username);
		players.put(entityID, username);		
	}


	@Override
	public String getPlayerUsername(int entityID) {
		//ConsoleIO.println("SPSConnection::setPlayerUsername => getting ID <" + entityID + "> which returns username " + players.get(entityID));
		return players.get(entityID);
	}


	@Override
	public void setUUID(String username, UUID uuid) {
		uuids.put(username, uuid);
		logger.error("",uuid,username);
	}
	
	@Override
	public UUID getUUID(String username) {
		return uuids.get(username);
	}
}

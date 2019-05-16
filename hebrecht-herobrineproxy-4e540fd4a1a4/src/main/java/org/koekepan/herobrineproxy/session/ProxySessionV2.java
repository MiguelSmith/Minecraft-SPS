package org.koekepan.herobrineproxy.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.koekepan.herobrineproxy.packet.PacketHandler;
import org.koekepan.herobrineproxy.packet.PacketAdapter;
import org.koekepan.herobrineproxy.packet.PacketSession;
import org.koekepan.herobrineproxy.packet.PacketListener;
import org.koekepan.herobrineproxy.packet.behaviours.PacketBehaviours;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.HerobrineProxyProtocol;
import org.koekepan.herobrineproxy.session.IProxySession;

import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;

public class ProxySessionV2 implements IProxySession {
	
	private String username = null;	
	private String host = null;
	private int port = 0;
	
	private PacketBehaviours serverPacketBehaviours;
	private PacketBehaviours clientPacketBehaviours;

	
	private Session clientSession;
	private PacketAdapter clientPacketAdapter;
	private PacketHandler clientPacketHandler;

	private Session server = null;	
	private PacketAdapter serverPacketAdapter;
	private PacketHandler serverPacketHandler;

	List<PacketListener> packetListeners = new ArrayList<PacketListener>();
	
	private ScheduledExecutorService packetExecutor;
	private Future<?> clientPacketFuture;
	private Future<?> serverPacketFuture;
	
	private SessionListener clientForwarder = null;
	private SessionListener serverForwarder = null;
	
	
	
	private Future<?> connectTask;
	private volatile CountDownLatch connectLatch;
	//private volatile CountDownLatch spawnedLatch;
	private volatile CountDownLatch joinedLatch;
	private int connectRetries = 5;
	private int connectTimeout = 30;
	private ScheduledExecutorService connectExecutor;

	//private volatile boolean active;
	//private volatile boolean spawned;
	private volatile boolean joined;
	private volatile boolean migrate;
	
	
	Future<?> migrateTask;
	private ExecutorService migrateExecutor;
	//private volatile CountDownLatch migrateLatch;
	private String migrationHost;
	
	Set<String> channels = new TreeSet<String>();
	
		
	public ProxySessionV2(Session clientSession) {
		this.clientSession = clientSession;
		packetExecutor = Executors.newSingleThreadScheduledExecutor();
		connectExecutor = Executors.newSingleThreadScheduledExecutor();
		migrateExecutor = Executors.newSingleThreadExecutor();
		this.initializeSession();
		
		addChannelRegistration("Koekepan|migrate");
		addChannelRegistration("Koekepan|kick");
		addChannelRegistration("Koekepan|latency");
		connectLatch = new CountDownLatch(connectRetries);
	}
	

	public void initializeSession() {
		serverPacketBehaviours = new PacketBehaviours(this);
		clientPacketBehaviours = new PacketBehaviours(this);
		
		serverPacketHandler = new PacketHandler(serverPacketBehaviours, new PacketSession(clientSession));
		serverPacketAdapter = new PacketAdapter(serverPacketHandler);
		
		clientPacketHandler = new PacketHandler(clientPacketBehaviours, null);
		clientPacketAdapter = new PacketAdapter(clientPacketHandler);
		clientSession.addListener(clientPacketAdapter);
		clientPacketFuture = packetExecutor.scheduleAtFixedRate(clientPacketHandler, 0, 1, TimeUnit.MILLISECONDS);
		//spawnedLatch = new CountDownLatch(1);
		joinedLatch = new CountDownLatch(1);
	}


	@Override
	public String getUsername() {
		return this.username;
	}
	

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	
	
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	
	@Override
	public void setPort(int port) {
		this.port = port;
	}
	
	
	@Override
	public String getHost() {
		return this.host;
	}

	
	@Override
	public int getPort() {
		return this.port;
	}

	
	@Override
	public void connect(String host, int port) {		
		MinecraftProtocol protocol = new MinecraftProtocol(username);
		//MinecraftProtocol protocol = new HerobrineProxyProtocol();
		server = new Client(host, port, protocol, new TcpSessionFactory(null)).getSession();
		ConsoleIO.println("ProxySessionV2::connect => Protocol status <"+protocol.getSubProtocol().name()+">");
		clientPacketHandler.setPacketSession(new PacketSession(server));
		//serverPacketHandler = new PacketHandler(packetBehaviours, new PacketSession(clientSession));
		//serverPacketAdapter = new PacketAdapter(serverPacketHandler);
		server.addListener(serverPacketAdapter);
		serverPacketFuture = packetExecutor.scheduleAtFixedRate(serverPacketHandler, 0, 1, TimeUnit.MILLISECONDS);
		
		//clientPacketBehaviours.clearBehaviours();
		clientPacketBehaviours.registerForwardingBehaviour();
		serverPacketBehaviours.clearBehaviours();
		serverPacketBehaviours.registerForwardingBehaviour();

		/*
		clientSession.removeListener(packetAdapter);
		clientForwarder = new PacketForwarder(server);
		clientSession.addListener(clientForwarder);
		serverForwarder = new PacketForwarder(clientSession);
		server.addListener(serverForwarder);
		*/
			
		ConsoleIO.println("Connecting to server <"+host+":"+port+">");
		this.connect();
		ConsoleIO.println("ProxySessionV2::connect => Protocol status <"+protocol.getSubProtocol().name()+">");

	//	server.connect(true);	
		//ConsoleIO.println("Finished connecting to server <"+host+":"+port+">");
		
	}

	
	private void connect() {	
		ConsoleIO.println("ProxySessionV2::connect => Starting thread to connect ");
		
		connectTask = connectExecutor.submit(new Runnable() {
		MinecraftProtocol protocol = (MinecraftProtocol)server.getPacketProtocol();
			
			@Override
			public void run()  {
				try {
					System.out.println("["+Thread.currentThread().getId()+"] ProxySessionV2::connect => Player <"+getUsername()+">  attempt <"+(connectRetries-connectLatch.getCount()+1)+"> at connecting to "+getHost());
					ConsoleIO.println("ProxySessionV2::connect => Protocol status <"+protocol.getSubProtocol().name()+">");

					server.connect(true);	
					ConsoleIO.println("ProxySessionV2::connect => Protocol status <"+protocol.getSubProtocol().name()+">");

					System.out.println("["+Thread.currentThread().getId()+"] ProxySessionV2::connect => Waiting for player <"+getUsername()+"> to establish connection to "+getHost());
					boolean connected = getJoinedCountDownLatch().await(7, TimeUnit.SECONDS);
					if (connected) {
						System.out.println("["+Thread.currentThread().getId()+"] ProxySessionV2::connect => Player <"+getUsername()+">: connection established to <"+getHost()+">: "+isConnected());
						registerClientForChannels();
						connectLatch = new CountDownLatch(connectRetries);
						connectTask.cancel(false);	
					} else {
						System.out.println("["+Thread.currentThread().getId()+"] ProxySessionV2::connect => Player <"+getUsername()+": Failed to establish connection to <"+getHost()+">");			
						connectLatch.countDown();
						if (connectLatch.getCount() > 0) {
							ConsoleIO.println("["+Thread.currentThread().getId()+"] ProxySessionV2::connect => Player <"+getUsername()+": Attempting to reconnect to <"+getHost()+">");										
							//reconnect();
						} else {
							System.out.println("["+Thread.currentThread().getId()+"] ProxySessionV2::connect => Player <"+getUsername()+": Maximum reconnection attempts reached. Disconnecting...");			
							disconnect();
							setMigrating(false);
							setJoined(false);
							//state.getPlayerState().setSpawned(false);
							//state.getPlayerState().setActive(false);
							connectTask.cancel(false);
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}); 
		ConsoleIO.println("Finished submitting connect task");
	}
	
	
	
	private void disconnectFromServer() {
		if (server != null && server.isConnected()) {		
			ConsoleIO.println("player \"" + username + "\" is disconnecting from <" + host + ":" + port + ">");
			server.disconnect("Finished.", true);
		}
	}
	
	
	private void disconnectFromClient() {
		if (clientSession != null && clientSession.isConnected()) {	
			ConsoleIO.println("Disconnecting from client...");
			clientSession.disconnect("Server connection closed.", true);
		}
	}
	
	
	@Override
	public void disconnect() {
		if (username != null) {
			disconnectFromServer();
			disconnectFromClient();
			
			
		}
		if (clientPacketFuture.isDone()) {
			clientPacketFuture.cancel(false);
		}
		if (serverPacketFuture.isDone()) {
			serverPacketFuture.cancel(false);
		}
	}

	
	@Override
	public void migrate(String host, int port) {
		ConsoleIO.println("Migrating to server <"+host+":"+port+"> from <"+getHost()+":"+getPort()+">: isMigrating <"+isMigrating()+">");
		if (isMigrating() == false) {
			ConsoleIO.println("ProxySessionV2::migrate => Setting host to  <"+host+">");
			migrationHost = host;
			this.setMigrating(true);
			migrate();
		}
	}


	@Override
	public boolean isMigrating() {
		return this.migrate;
	}

	
	@Override
	public void setMigrating(boolean migrating) {
		this.migrate = migrating;
	}

	
	@Override
	public Session getClient() {
		return this.clientSession;
	}

	@Override
	public void setClient(Session client) {
		this.clientSession = client;
	}
	

	@Override
	public Session getServer() {
		return this.server;
	}

	
	@Override
	public void setServer(Session server) {
		this.server = server;	
	}
	

	@Override
	public SessionListener getClientPacketForwarder() {
		return this.clientForwarder;
	}

	
	@Override
	public void setClientPacketForwarder(SessionListener forwarder) {
		this.clientForwarder = forwarder;
	}

	
	@Override
	public SessionListener getServerPacketForwarder() {
		return this.serverForwarder;
	}
	

	@Override
	public void setServerPacketForwarder(SessionListener forwarder) {
		this.serverForwarder = forwarder;
	}


	@Override
	public boolean isConnected() {
		return (server != null && server.isConnected());
	}
	
	
	private void addChannelRegistration(String channel) {
		channels.add(channel);
	}
	
	
	@Override
	public void registerClientForChannels() {
		for (String channel : channels) {
			registerClientForChannel(channel);
		}
	}
	
	
	private void registerClientForChannel(String channel) {
		byte[] payload = writeStringToPluginMessageData(channel);
		String registerMessage = "REGISTER";
		ClientPluginMessagePacket registerPacket = new ClientPluginMessagePacket(registerMessage, payload);
		server.send(registerPacket);
	}


	private byte[] writeStringToPluginMessageData(String message) {
		byte[] data = message.getBytes(Charsets.UTF_8);
		ByteBuf buff = Unpooled.buffer();        
		buff.writeBytes(data);
		return buff.array();
	}
	
	
	@Override
	public CountDownLatch getJoinedCountDownLatch() {
		return this.joinedLatch;
	}
	
	
	public PacketHandler getPacketHandler() {
		return null;
	}
	
	
	@Override
	public void setJoined(boolean joined) {
		this.joined = joined;
		if (!joined) {
			joinedLatch = new CountDownLatch(1);
		}
	}
	

	@Override
	public boolean getJoined() {
		return joined;
	}
	
	
	
	private void migrate() {
		ConsoleIO.println("ProxySessionV2::migrate => Starting new migrate task for player <"+getUsername()+"> to host "+getHost());
		migrateTask = migrateExecutor.submit(new Runnable() {

			@Override
			public void run()  {
					//migrateLatch.await();
					
					System.out.println("ProxySessionV2::migrate => Disconnecting from host "+getHost());
					disconnectFromServer();
					setHost(migrationHost);
					setJoined(false);
					
					//initializeSession();
					System.out.println("ProxySessionV2::migrate => Connecting to host <"+getHost()+">");
					connect(migrationHost, getPort());
					setMigrating(false);
				/*} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			
			}
		});
	}
	
	
	public void initiateMigration(String newHost) {
		if (isMigrating() == false) {
			ConsoleIO.println("ProxySessionV2::initiateMigration => Setting host to  <"+newHost+">");
			migrationHost = newHost;
			this.setMigrating(true);
		}
	}
	
	
	
	public void reconnect() {
		ConsoleIO.println("ProxySession::reconnect => Reconnecting <"+getUsername()+"> to host <"+host+">");
		migrationHost = host;
		setMigrating(true);
		//state.getPlayerState().setSpawned(false); // added but not sure if necessary?
		setJoined(false);
		migrate();
		//migrateLatch.countDown();
	}


	@Override
	public void sendPacketToClient(Packet packet) {
		ConsoleIO.println("ForwardPacketBehaviour::process => Sending packet <"+packet.getClass().getSimpleName()+"> to client");
		serverPacketHandler.sendPacket(packet);
	}


	@Override
	public void sendPacketToServer(Packet packet) {
		ConsoleIO.println("ForwardPacketBehaviour::process => Sending packet <"+packet.getClass().getSimpleName()+"> to server");
		clientPacketHandler.sendPacket(packet);
	}
	

	
	/*
	
	
	private Session client = null;
	private Session server = null;
	
	private String host = null;
	private int port = 0;

	private SessionListener clientForwarder = null;
	private SessionListener serverForwarder = null;

	private String username = null;

	private boolean migrating = false;

	public ProxySessionV2(final Session client) {
		this.client = client;

		// listen for login start packet to get username
		client.addListener(new SessionAdapter() {
			@Override
			public void packetReceived(PacketReceivedEvent event) {
				Packet packet = event.getPacket();
				ConsoleIO.println("ProxySessionV2::constructor => Received a packet <"+packet.getClass().getSimpleName()+">");
				if (packet instanceof LoginStartPacket) {
					LoginStartPacket loginPacket = (LoginStartPacket)packet;
					username = loginPacket.getUsername();
					ConsoleIO.println("player \"" + username + "\" is connecting to <" + host + ":" + port + ">");
					client.removeListener(this);
				} 
			}
		});
	}


	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		server = new Client(host, port, new HerobrineProxyProtocol(), new TcpSessionFactory(null)).getSession();
		clientForwarder = new PacketForwarder(server);
		client.addListener(clientForwarder);
		serverForwarder = new PacketForwarder(client);
		server.addListener(serverForwarder);
		server.addListener(new MigrationListener(this));
		ConsoleIO.println("Connecting to server...");
		server.connect(true);
	}


	public void disconnect() {
		if(username != null) {
			if (server.isConnected()) {
				ConsoleIO.println("player \"" + username + "\" is disconnecting from <" + host + ":" + port + ">");
				server.disconnect("Finished.", true);
			}
			
			if (client.isConnected()) {				
				client.disconnect("Server connection closed.", true);
			}

		}
	}


	// TODO move migration to MigrationListener
	public void migrate(String host, int port) {
		if(!migrating) {
			migrating = true;

			this.host = host;
			this.port = port;

			ConsoleIO.println("player \"" + username + "\" is being migrated from <" + server.getHost() + ":" + server.getPort() + "> to <" + host + ":" + port + ">");

			new Thread(new Runnable() {
				private String host;
				private int port;

				@Override
				public void run() {
					ClientMigrationForwarder clientMigrationForwarder = new ClientMigrationForwarder(ProxySessionV2.this);
					client.addListener(clientMigrationForwarder);
					Session newServer = new Client(host, port, new HerobrineProxyProtocol(), new TcpSessionFactory(null)).getSession();
					newServer.addListener(new MigrationListener(ProxySessionV2.this));
					newServer.addListener(new ServerMigrationForwarder(host, port, ProxySessionV2.this, clientMigrationForwarder));
					newServer.connect(true);
				}

				public Runnable init(String host, int port) {
					this.host = host;
					this.port = port;
					return this;
				}
			}.init(host, port)).start();
		}
	}


	public Session getClient() {
		return client;
	}

	
	public void setClient(Session client) {
		this.client = client;
	}

	
	public Session getServer() {
		return server;
	}

	
	public void setServer(Session server) {
		this.server = server;
	}



	public SessionListener getClientPacketForwarder() {
		return clientForwarder;
	}

	
	public void setClientPacketForwarder(SessionListener forwarder) {
		clientForwarder = forwarder;
	}

	
	public SessionListener getServerPacketForwarder() {
		return serverForwarder;
	}

	
	public void setServerPacketForwarder(SessionListener forwarder) {
		serverForwarder = forwarder;
	}


	public String getUsername() {
		return username;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}


	public boolean isMigrating() {
		return migrating;
	}

	
	public void setMigrating(boolean migrating) {
		this.migrating = migrating;
	}


	public String getHost() {
		return host;
	}
	

	public int getPort() {
		return port;
	}
*/

}

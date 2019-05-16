package org.koekepan.herobrineproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.koekepan.herobrineproxy.session.IProxySessionConstructor;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.session.SPSToServerProxy;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSConnection;

// the main class for the proxy
// this class creates a proxy session for every client session that is connected

public class SPSServerProxy implements IProxySessionConstructor {

	private String spsHost = null;
	private int spsPort = 0;
	private String serverHost = null;
	private int serverPort = 0;
	private ISPSConnection spsConnection;
	
	// private Server server = null;
	private Map<String, IProxySessionNew> sessions = new HashMap<String, IProxySessionNew>();
	

	public SPSServerProxy(final String spsHost, final int spsPort, final String serverHost, final int serverPort) {
		this.spsHost = spsHost;
		this.spsPort = spsPort;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		
		this.spsConnection = new SPSConnection(this.spsHost, this.spsPort, this);

		/*
		SPSClientSession clientSession = new SPSClientSession(this.spsConnection);
		ProxySessionV3 serverProxySession = new ProxySessionV3(clientSession, serverHost, serverPort);
			
		ClientSessionPacketBehaviours clientPacketBehaviours = new ClientSessionPacketBehaviours(serverProxySession);
		clientPacketBehaviours.registerDefaultBehaviours(clientSession);
		clientSession.setPacketBehaviours(clientPacketBehaviours);
		
		ServerSession serverSession = new ServerSession(serverHost, serverPort);	
		serverSession.setUsername("hebrecht");		
	
		clientPacketBehaviours.registerForwardingBehaviour();
		ServerSessionPacketBehaviours serverPacketBehaviours = new ServerSessionPacketBehaviours(serverProxySession, serverSession);
//		this.serverPacketBehaviours.clearBehaviours();
		serverPacketBehaviours.registerForwardingBehaviour();
//		this.serverPacketBehaviours.registerDefaultBehaviours();
		serverSession.setPacketBehaviours(serverPacketBehaviours);	
		serverSession.setUsername("hebrecht");
		serverSession.setJoined(false);
		serverSession.connect();
		*/
		
		
		//serverProxySession.setUsername("hebrecht");
		//serverProxySession.connect(serverHost,  serverPort);
	
		// setup proxy server and add listener to create and store/discard proxy sessions as clients connect/disconnect
		/*server = new Server(Host, proxyPort, HerobrineProxyProtocol.class, new TcpSessionFactory());	
		
		server.addListener(new ServerAdapter() {
			
			@Override
			public void sessionAdded(SessionAddedEvent event) {
				Session session = event.getSession();
				ConsoleIO.println("HerobrineServerProxy::sessionAdded => A SessionAdded event occured from <"+session.getHost()+":"+session.getPort()+"> to server <"+serverHost+":"+serverPort+">");
				IClientSession clientSession = new ClientSession(session);
				//IProxySessionNew proxySession = new ProxySessionV3(clientSession, serverHost, serverPort);
				IProxySessionNew proxySession = new SPSProxySession(clientSession, serverHost, serverPort);
				sessions.put(event.getSession(), proxySession);
			}

			@Override
			public void sessionRemoved(SessionRemovedEvent event) {
				sessions.remove(event.getSession()).disconnect();
			}
		});*/
	}
	
	public void createNewProxySession(String username) {
		
	}

	
	// returns whether the proxy server is currently listening for client connections
	public boolean isListening() {
		return spsConnection != null;
	}


	// initializes the proxy
	public void bind() {
		this.spsConnection.connect();
		// subscribe to the login and status channel immediately
		this.spsConnection.subscribeToChannel("login");
		this.spsConnection.subscribeToChannel("status");
	}

	
	// closes the proxy
	public void close() {
		this.spsConnection.disconnect();
	}


	public String getServerHost() {
		return serverHost;
	}


	public int getServerPort() {
		return serverPort;
	}

	
	public List<IProxySessionNew> getSessions() {
		return new ArrayList<IProxySessionNew>(sessions.values());
	}

	@Override
	public IProxySessionNew createProxySession(String username) {
		SPSToServerProxy newProxySession = new SPSToServerProxy(this.spsConnection, this.serverHost, this.serverPort);
		newProxySession.setUsername(username);
		sessions.put(username, newProxySession);
		return newProxySession;
	}

	@Override
	public IProxySessionNew getProxySession(String username) {
		return sessions.get(username);
	}		
}

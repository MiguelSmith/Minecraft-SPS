package org.koekepan.herobrineproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.koekepan.herobrineproxy.session.ClientSession;
import org.koekepan.herobrineproxy.session.ClientToSPSProxy;
import org.koekepan.herobrineproxy.session.IClientSession;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.herobrineproxy.sps.ISPSConnection;
import org.koekepan.herobrineproxy.sps.SPSConnection;

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;


// the main class for the proxy
// this class creates a proxy session for every client session that is connected

public class SPSClientProxy {


	//private ScheduledExecutorService serverPoll = Executors.newSingleThreadScheduledExecutor();

	private String proxyHost = null;
	private int proxyPort = 0;
	private String spsHost = null;
	private int spsPort = 0;
	private ISPSConnection spsConnection;
	
	private Server server = null;
	private Map<Session, IProxySessionNew> sessions = new HashMap<Session, IProxySessionNew>();
	

	public SPSClientProxy(final String proxyHost, final int proxyPort, final String spsHost, final int spsPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.spsHost = spsHost;
		this.spsPort = spsPort;
		
		
		// setup proxy server and add listener to create and store/discard proxy sessions as clients connect/disconnect
		server = new Server(proxyHost, proxyPort, HerobrineProxyProtocol.class, new TcpSessionFactory());	
		this.spsConnection = new SPSConnection(this.spsHost, this.spsPort );
		this.spsConnection.connect();
		spsConnection.subscribeToChannel("login");
		spsConnection.subscribeToChannel("status");
		
		server.addListener(new ServerAdapter() {
			
			@Override
			public void sessionAdded(SessionAddedEvent event) {
				Session session = event.getSession();
				ConsoleIO.println("SPSClientProxy::sessionAdded => A SessionAdded event occured from <"+session.getHost()+":"+session.getPort()+"> to server <"+spsHost+":"+spsPort+">");
				IClientSession clientSession = new ClientSession(session);
				//IProxySessionNew proxySession = new ProxySessionV3(clientSession, serverHost, serverPort);
				//IProxySessionNew proxySession = new SPSClientProxySession(clientSession, spsConnection);
				IProxySessionNew proxySession = new ClientToSPSProxy(clientSession, spsConnection);
				sessions.put(event.getSession(), proxySession);
			}

			@Override
			public void sessionRemoved(SessionRemovedEvent event) {
				sessions.remove(event.getSession()).disconnect();
			}
		});
	}

	
	// returns whether the proxy server is currently listening for client connections
	public boolean isListening() {
		return server != null && server.isListening();
	}


	// initializes the proxy
	public void bind() {
		server.bind(true);
	}

	
	// closes the proxy
	public void close() {
		server.close(true);
	}

	
	public String getProxyHost() {
		return proxyHost;
	}

	
	public int getProxyPort() {
		return proxyPort;
	}


	/*public String getServerHost() {
		return serverHost;
	}


	public int getServerPort() {
		return serverPort;
	}*/

	
	public List<IProxySessionNew> getSessions() {
		return new ArrayList<IProxySessionNew>(sessions.values());
	}	
}

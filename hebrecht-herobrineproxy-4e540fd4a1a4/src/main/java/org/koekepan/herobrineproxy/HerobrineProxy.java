package org.koekepan.herobrineproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.koekepan.herobrineproxy.session.ProxySession;

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;


// the main class for the proxy
// this class creates a proxy session for every client session that is connected
@SuppressWarnings("unused")
public class HerobrineProxy {

	// interval at which poll thread will check whether the target server is accessible
	private static final int pollInterval = 1000;

	private ScheduledExecutorService serverPoll = Executors.newSingleThreadScheduledExecutor();

	private String proxyHost = null;
	private int proxyPort = 0;
	private String serverHost = null;
	private int serverPort = 0;

	private Server server = null;
	private Map<Session, ProxySession> sessions = new HashMap<Session, ProxySession>();


	public HerobrineProxy(String proxyHost, int proxyPort, String serverHost, int serverPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.serverHost = serverHost;
		this.serverPort = serverPort;

		// setup proxy server and add listener to create and store/discard proxy sessions as clients connect/disconnect
		server = new Server(proxyHost, proxyPort, HerobrineProxyProtocol.class, new TcpSessionFactory());	
		server.addListener(new ServerAdapter() {
			@Override
			public void sessionAdded(SessionAddedEvent event) {			
				ProxySession proxySession = new ProxySession(event.getSession());
				sessions.put(event.getSession(), proxySession);		
				proxySession.connect(HerobrineProxy.this.serverHost, HerobrineProxy.this.serverPort);
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

	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public List<ProxySession> getSessions() {
		return new ArrayList<ProxySession>(sessions.values());
	}

}

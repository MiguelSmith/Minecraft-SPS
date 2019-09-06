package org.koekepan.herobrineproxy.test;

import java.io.IOException;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.SPSServerProxy;
import org.koekepan.herobrineproxy.session.IProxySessionNew;
import org.koekepan.performance.Log;

public class HerobrineServerProxy {
	private static SPSServerProxy proxy = null;
	
	
	public static void main(String[] args) {
		String spsHost = "127.0.0.1";
		int spsPort = 3000;
		String serverHost = "127.0.0.1";
		int serverPort = 25565;
		
		try {
			Log.setup();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// read command line parameters
		try {
			if (args.length == 2) {
				spsHost = args[0];
				spsPort = Integer.parseInt(args[1]);
				proxy = new SPSServerProxy(spsHost, spsPort, serverHost, serverPort);

			} else if (args.length == 4) {
				serverHost = args[0];
				serverPort = Integer.parseInt(args[1]);
				spsHost = args[2];
				spsPort = Integer.parseInt(args[3]);
				proxy = new SPSServerProxy(spsHost, spsPort, serverHost, serverPort);
			} else {
				printUsageMessage();
			}
		} catch (NumberFormatException e) {
			printUsageMessage();
		}
        
		
		// start if proxy is set
		if (proxy != null) {
			proxy.bind();
	
			ConsoleIO.println("Herobrine Server Proxy is running...");
			ConsoleIO.println("Type \"help\" for a list of commands");

			try {
				String input = "";
				do {
					input = ConsoleIO.readLine();
					processInput(input);
				}
				while(!input.equalsIgnoreCase("stop"));
			} catch(IOException e) {
				e.printStackTrace();;
			}

			ConsoleIO.println("Herobrine Server Proxy is stopping...");
			proxy.close();

			// wait for key press to end program
			System.out.println("Press any key to continue...");
			try {
				System.in.read();
			} catch(Exception e) {
				// do nothing
			} 
		}
	}

	
	// invalid command line parameters output
	private static void invalidArguments() {
		ConsoleIO.println("Invalid arguments provided! Valid arguments are:");
		ConsoleIO.println("> ");
		ConsoleIO.println("> [Server Host (String)] [Server Port (int)]");
		ConsoleIO.println("> [Server Host (String)] [Server Port (int)] SPS Host (String)] [SPS Port (int)]");
	}

	
	// process input commands
	private static void processInput(String input) {
		String[] args = input.split(" ");
		String command = args[0];
		if (command.equalsIgnoreCase("help")) {
			synchronized(ConsoleIO.class) {
				ConsoleIO.println("---------------- Commands -----------------");
				ConsoleIO.println("[command] help			- displays a list of commands");
				ConsoleIO.println("[command] ls			- displays player sessions");
				ConsoleIO.println("[command] stop			- shutdowns the proxy");
				ConsoleIO.println("[command] migrate host_ip 	- migrates to host_ip");
			}
		} else if (command.equalsIgnoreCase("ls")) {
			synchronized(ConsoleIO.class) {
				ConsoleIO.println("---------------- Sessions -----------------");
				int count = 0;
				for(IProxySessionNew session : proxy.getSessions()) {
					if(session.getUsername() != null) {
						count++;
						ConsoleIO.println("[session] player \"" + session.getUsername() + "\" connected to <" + session.getServerHost() + ":" + session.getServerPort() + ">");
					}
				}
				if (count == 0) {
					ConsoleIO.println("[No sessions connected]");
				}
			}
		} else if (command.equalsIgnoreCase("stop")) {
			return;
		} else if (command.equalsIgnoreCase("migrate")) {
			if (args.length == 2) {
				String host = args[1];
				ConsoleIO.println("Migrating to host <"+host+">");
				for (IProxySessionNew proxySession : proxy.getSessions() ) {
					proxySession.migrate(host, proxySession.getServerPort());
				}

			} else {
				ConsoleIO.println("[command] migrate host_ip 	- migrates to host_ip");
			}
		} else {
			ConsoleIO.println("Unknown command. Type \"help\" for help");
		}
	}
	
	
	public static void printUsageMessage() {
		ConsoleIO.println("Herobrine Proxy v1.11.2a");
		ConsoleIO.println("-------------------------");
		ConsoleIO.println("To use from command line execute HerobrineProxy class with parameters: ");
	
		ConsoleIO.println("> [Server Host (String)] [Server Port (int)] and default [Proxy Host:Proxy Port] = [127.0.0.1:25570] or ");
		ConsoleIO.println("> [Server Host (String)] [Server Port (int)] [Proxy Host (String)] [Proxy Port (int)]");
	}
}
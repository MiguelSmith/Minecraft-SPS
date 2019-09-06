package org.koekepan.herobrineplugin.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;
import java.util.TreeMap;
import org.koekepan.herobrine.log.Log;
import org.koekepan.herobrine.log.file.LogFileReader;
import org.koekepan.herobrineplugin.logging.HerobrinePluginLogHandler;
import org.koekepan.herobrineplugin.logging.logs.PerformanceLog;
import org.koekepan.herobrineplugin.logging.logs.SystemLog;
import org.koekepan.herobrineplugin.logging.logs.ServerChunkLog;
import org.koekepan.herobrineplugin.logging.logs.ServerPlayerLog;
import org.koekepan.herobrineplugin.logging.logs.LatencyLog;


@SuppressWarnings("unused")
public class Test {
	
	static public class LogEntry {
		long timeStamp;
		public int numPlayers = 0;
		public int numLoadedChunks = 0;
		public double sysCpu = 0; 
		public double procCpu = 0;
		public double sysMem = 0;
		public double procMem = 0;
		public long sysRxBytes = 0;
		public long sysTxBytes = 0;
		public double sysRxBandwidth = 0;
		public double sysTxBandwidth = 0;
		public double RTT = 0;

		
		public void addPerformance(LogEntry entry) {
//			this.numPlayers += entry.numPlayers;
//			this.numLoadedChunks += entry.numLoadedChunks;
			this.sysCpu += entry.sysCpu;
			this.procCpu += entry.procCpu;
			this.sysMem += entry.sysMem;
			this.procMem += entry.procMem;
			this.sysRxBytes += entry.sysRxBytes;
			this.sysTxBytes += entry.sysTxBytes;
			this.sysRxBandwidth += entry.sysRxBandwidth;
			this.sysTxBandwidth += entry.sysTxBandwidth;
//			this.RTT += entry.RTT;
		}
		
		
		public void addPlayers(int numPlayers) {
			this.numPlayers += numPlayers;
		}
		

		public void addLoadedChunks(int loadedChunks) {
			this.numLoadedChunks += loadedChunks;
		}
		

		
		@Override
		public String toString() {
			StringBuilder datasb = new StringBuilder();
			Formatter dataFormatter = new Formatter(datasb);
			Double second = Math.ceil(timeStamp);
			dataFormatter.format("%-22d, %-7d, %-10d, %-12.2f, %-13.2f, %-13.3f, %-13.3f, %-13.3f, %-13.3f, %-9.3f\n", second.intValue(), numPlayers, numLoadedChunks, sysCpu, procCpu, sysMem, procMem, sysRxBandwidth, sysTxBandwidth, RTT);
			String returnValue = dataFormatter.toString();
			dataFormatter.close();
			return returnValue;		
		}
		
	}
	
	static public class CombinedLogEntry {
		Map<Long, LogEntry> totalLogEntry = new TreeMap<Long, LogEntry>();
		
		
		public void addLogEntryAtNewTimeStamp(long timeStamp, LogEntry entry) {
			assert(this.totalLogEntry.containsKey(timeStamp) == false);
			totalLogEntry.put(timeStamp, entry);
		}
	
	
		/*public void addLogEntryToExistingTimeStamp(long timeStamp, LogEntry entry) {
			assert(this.totalLogEntry.containsKey(timeStamp) == true);
			LogEntry existingEntry = totalLogEntry.get(timeStamp);
			//System.out.println("addLogEntryToExistingTimeStamp => "+totalLogEntry.size());
			assert(existingEntry != null);
			assert(entry != null);
			existingEntry.add(entry);
		}

	
		public void addLog(long timeStamp, LogEntry entry) {
			//System.out.println("CombinedLog::addLog => "+timeStamp);
			if (this.totalLogEntry.containsKey(timeStamp)) {
				this.addLogEntryToExistingTimeStamp(timeStamp, entry);
			} else {
				this.addLogEntryAtNewTimeStamp(timeStamp, entry);
			}
		}*/
		
		
		public Collection<Long> getLogEntryTimes() {
			return this.totalLogEntry.keySet();
		}
		
		
		public LogEntry getLogEntryAtTimeStamp(long timeStamp) {
			LogEntry returnValue = null;
			if (this.totalLogEntry.containsKey(timeStamp))
				returnValue = this.totalLogEntry.get(timeStamp);
			else {
				returnValue = new LogEntry();
				returnValue.timeStamp = timeStamp;
				this.addLogEntryAtNewTimeStamp(timeStamp, returnValue);
			}
			assert(returnValue != null);
			return returnValue;
		}		
	}
	
	
	
	
	public static void main(String args[]) {
	
		// get all log files in base directory
		File[] files = new File("./").listFiles(new FilenameFilter() {

			private String prefix = "herobrine"; // all log files are prefixed with "herobrine"
			private String suffix = ".log"; // all log files have extension ".log"

			@Override
			public boolean accept(File directory, String filename) {
				boolean flag = false;
				if(filename.length() >= (prefix + suffix).length()) {
					flag = filename.substring(0, prefix.length()).equals(prefix) && filename.endsWith(suffix);
				}
				return flag;
			}	
		});

		
		CombinedLogEntry combinedServerChunkLog = new CombinedLogEntry();		
		CombinedLogEntry combinedServerPlayerLog = new CombinedLogEntry();		
		CombinedLogEntry combinedPerformanceLog = new CombinedLogEntry();		
		CombinedLogEntry combinedLatencyLog = new CombinedLogEntry();		
		
		for (File file : files) {
			ParsedLog parsedLog = new ParsedLog();
			String filename = file.getName();

			System.out.println("--------------------------------------------------");
			System.out.println("Processing log file: " + filename);
			System.out.println("--------------------------------------------------");

			// process file name

			String data[] = filename.substring(0, filename.lastIndexOf(".")).split("_");

			//Date date = new Date(Long.parseLong(data[4]));
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			String timestamp = dateFormat.format(date);
			String host = "127.0.0.1";
			
			System.out.println("Host   : " + host);
			System.out.println("Socket : 127.0.0.1:25575");
			System.out.println("Date   : " + timestamp);
			System.out.println("UUID   : ");

			// process file contents
			System.out.println("--------------------------------------------------");
			System.out.println("Processing logs...");
			System.out.println("--------------------------------------------------");

			try {
				LogFileReader fileReader = new LogFileReader(new HerobrinePluginLogHandler(), filename);
				Log log = null;
				while((log = fileReader.read()) != null) {
					process(log, parsedLog);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Logfile of "+host+" contains "+parsedLog.getLogTimes().size()+" timestamp entries");
			processParsedLog(parsedLog, host, combinedServerChunkLog, combinedServerPlayerLog, combinedPerformanceLog, combinedLatencyLog);
		}
		
		Test.writeCombinedLatencyLog(combinedLatencyLog, "cluster.latency.csv");
		Test.writeCombinedPerformanceLog(combinedPerformanceLog, "cluster.system.csv");
		Test.writeCombinedServerChunkLog(combinedServerChunkLog, "cluster.chunk.csv");
		Test.writeCombinedServerPlayerLog(combinedServerPlayerLog, "cluster.player.csv");
	}
	
	
	private static void writeCombinedLatencyLog(CombinedLogEntry combinedLog, String filename) {
		try {
			FileWriter file = createDataFile(filename, "%% %-19s, %-9s%n", "ServerTime",  "RoundTripTime");
			
			for (long timestamp : combinedLog.getLogEntryTimes()) {
				LogEntry entry = combinedLog.getLogEntryAtTimeStamp(timestamp);
				writeLatencyEntry(entry, file);
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	private static void writeCombinedServerChunkLog(CombinedLogEntry combinedLog, String filename) {
		try {
			FileWriter file = createDataFile(filename, "%% %-19s, %-10s%n", "ServerTime", "Chunks");
					
			for (long timestamp : combinedLog.getLogEntryTimes()) {
				LogEntry entry = combinedLog.getLogEntryAtTimeStamp(timestamp);
				writeChunkEntry(entry, file);
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	private static void writeCombinedServerPlayerLog(CombinedLogEntry combinedLog, String filename) {
		try {

			FileWriter file = createDataFile(filename, "%% %-19s, %-7s%n", "ServerTime", "Players");
		
			for (long timestamp : combinedLog.getLogEntryTimes()) {
				LogEntry entry = combinedLog.getLogEntryAtTimeStamp(timestamp);
				Test.writePlayerEntry(entry, file);
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	private static void writeCombinedPerformanceLog(CombinedLogEntry combinedLog, String filename) {
		try {
			FileWriter file = createDataFile(filename, "%% %-19s, %-12s, %-13s, %-13s, %-13s, %-13s, %-13s%n", "ServerTime", "tSYS_CPU [%]", "tPROC_CPU [%]", "tSYS_MEM [MB]", "PROC_MEM [MB]", "RX_BW [kb/s]", "TX_BW[kb/s]");
			
			for (long timestamp : combinedLog.getLogEntryTimes()) {
				LogEntry entry = combinedLog.getLogEntryAtTimeStamp(timestamp);
				Test.writePerformanceEntry(entry, file);
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	

	private static void process(Log log, ParsedLog parsedLog) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

		if (log instanceof SystemLog) {
			
			SystemLog currentLog = (SystemLog) log;
			parsedLog.addLog(currentLog.getTimestamp(), currentLog);

		/*	System.out.println("SystemLog:\n" + dateFormat.format(new Date(currentLog.getTimestamp())) + "\n");
			
			System.out.println("Total Memory : " + currentLog.getMemory() + " bytes");
			
			System.out.println("\nListing all CPUs: (" + currentLog.getCpuInfo().length + ")");
			System.out.println("-------------------------------------");
			for(String cpu : currentLog.getCpuInfo()) {
				System.out.println(cpu);
			}
			
			System.out.println("\nListing all Network Interfaces: (" + currentLog.getNetworkInfo().length + ")");
			System.out.println("-------------------------------------");
			for(String net : currentLog.getNetworkInfo()) {
				System.out.println(net);
			}
			System.out.println("--------------------------------------------------");*/
		}

		if (log instanceof PerformanceLog) {
			PerformanceLog currentLog = (PerformanceLog) log;
			parsedLog.addLog(currentLog.getTimestamp(), currentLog);
			
	/*		double sysCpu = (double) Math.round(currentLog.getSystemCpu()*10.0)/10.0;
			double procCpu = (double) Math.round(currentLog.getProcessCpu()*10.0)/10.0;
			long sysMem = currentLog.getSystemMemory();
			long procMem = currentLog.getProcessMemory();
			long sysRxBytes = currentLog.getSystemRxBytes();
			long sysTxBytes = currentLog.getSystemTxBytes();
			long sysRxBandwidth = currentLog.getSystemRxBandwidth();
			long sysTxBandwidth = currentLog.getSystemTxBandwidth();

			System.out.println("PerformanceLog:\n" + dateFormat.format(new Date(currentLog.getTimestamp())) + "\n");
			System.out.println("System CPU  : " + sysCpu + "%");
			System.out.println("Process CPU : " + procCpu + "%");
			System.out.println("System MEM  : " + sysMem + " bytes");
			System.out.println("Process MEM : " + procMem + " bytes");
			System.out.println("RX Bytes    : " + sysRxBytes + " bytes");
			System.out.println("TX Bytes    : " + sysTxBytes + " bytes");
			System.out.println("RX BW       : " + sysRxBandwidth + " bytes/s");
			System.out.println("TX BW       : " + sysTxBandwidth + " bytes/s");
			System.out.println("--------------------------------------------------");*/
		}
		
		if (log instanceof ServerChunkLog) {
			ServerChunkLog currentLog = (ServerChunkLog) log;
			parsedLog.addLog(currentLog.getTimestamp(), currentLog);

		/*	int numOnlinePlayers = currentLog.getNumberOfOnlinePlayers();
			int numChunksLoaded = currentLog.getNumberOfChunksLoaded();

			System.out.println("ServerLog:\n" + dateFormat.format(new Date(currentLog.getTimestamp())) + "\n");
			System.out.println("Server Online Players  : " + numOnlinePlayers);
			System.out.println("Server Loaded Chunks  : " + numChunksLoaded);
			System.out.println("--------------------------------------------------");*/
			
		}
		
		if (log instanceof ServerPlayerLog) {
			ServerPlayerLog currentLog = (ServerPlayerLog) log;
			parsedLog.addLog(currentLog.getTimestamp(), currentLog);
			int numOnlinePlayers = currentLog.getNumberOfOnlinePlayers();
			//System.out.println("ServerPlayerLog:\n" + dateFormat.format(new Date(currentLog.getTimestamp())) + "\n");
			//System.out.println("Server Online Players  : " + numOnlinePlayers);
		}
		
		if (log instanceof LatencyLog) {
			LatencyLog currentLog = (LatencyLog) log;
			parsedLog.addLog(currentLog.getTimestamp(), currentLog);

		/*	UUID playerUUID = currentLog.getPlayerUUID();
			long sendTime = currentLog.getSendTime();			
			long receiveTime = currentLog.getReceiveTime();			
			long roundTripTime = currentLog.getRoundTripTime();		

			System.out.println("LatencyLog:\n" + dateFormat.format(new Date(currentLog.getTimestamp())) + "\n");
			System.out.println("Player <"+playerUUID+"> latency : " + roundTripTime + " ms");			
			System.out.println("--------------------------------------------------");*/
			
		}
	}
	
	
	private static FileWriter createDataFile(String filename, String format, Object...args) throws IOException {
		FileWriter dataFile = new FileWriter(filename);
		StringBuilder sb = new StringBuilder();
		Formatter dataFormatter = new Formatter(sb);
		dataFormatter.format(format, args);
		dataFile.write(dataFormatter.toString());
		dataFile.flush();
		dataFormatter.close();
		return dataFile;
	}
		

	private static void processServerChunkLog(ServerChunkLog log, LogEntry entry, FileWriter chunkDataFile) throws IOException {
		entry.numLoadedChunks = log.getNumberOfChunksLoaded();
		writeChunkEntry(entry, chunkDataFile);
	}
	
	
	private static void writeChunkEntry(LogEntry entry, FileWriter dataFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		Formatter dataFormatter = new Formatter(sb);
		dataFormatter.format("%-22d, %-10d%n", entry.timeStamp, entry.numLoadedChunks);
		dataFile.write(dataFormatter.toString());
		dataFile.flush();
		dataFormatter.close();	
	}
	
	
	private static void processLatencyLog(LatencyLog log, LogEntry entry, FileWriter latencyDataFile) throws IOException {
		entry.RTT = log.getRoundTripTime();
		
		writeLatencyEntry(entry, latencyDataFile);		
	}
	
	
	private static void writeLatencyEntry(LogEntry entry, FileWriter dataFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		Formatter dataFormatter = new Formatter(sb);
		dataFormatter.format("%-22d, %-9.3f%n", entry.timeStamp, entry.RTT);
		dataFile.write(dataFormatter.toString());
		dataFile.flush();
		dataFormatter.close();	
	}
	
	
	private static void processServerPlayerLog(ServerPlayerLog log, LogEntry entry, FileWriter playerDataFile) throws IOException {
		entry.numPlayers = log.getNumberOfOnlinePlayers();
		writePlayerEntry(entry, playerDataFile);		
	}
	
	
	private static void writePlayerEntry(LogEntry entry, FileWriter dataFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		Formatter dataFormatter = new Formatter(sb);
		dataFormatter.format("%-22d, %-10d%n", entry.timeStamp, entry.numPlayers);
		dataFile.write(dataFormatter.toString());
		dataFile.flush();
		dataFormatter.close();	
	}
	
	
	private static void processPerformanceLog(PerformanceLog log, LogEntry entry, FileWriter dataFile) throws IOException {
		
		entry.sysCpu = Math.round(log.getSystemCpu()*10.0)/10.0;
		entry.procCpu = Math.round(log.getProcessCpu()*10.0)/10.0;
		entry.sysMem = (double)(log.getSystemMemory())/1024/1024;
		entry.procMem = (double)(log.getProcessMemory())/1024/1024;
		entry.sysRxBytes = log.getSystemRxBytes();
		entry.sysTxBytes = log.getSystemTxBytes();
		entry.sysRxBandwidth = (double)(log.getSystemRxBandwidth())/1024;
		entry.sysTxBandwidth = (double)(log.getSystemTxBandwidth())/1024;
		
		writePerformanceEntry(entry, dataFile);
	}
	
	
	private static void writePerformanceEntry(LogEntry entry, FileWriter dataFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		Formatter dataFormatter = new Formatter(sb);
		dataFormatter.format("%-22d, %-12.2f, %-13.2f, %-13.3f, %-13.3f, %-13.3f, %-13.3f%n", entry.timeStamp, entry.sysCpu, entry.procCpu, entry.sysMem, entry.procMem, entry.sysRxBandwidth, entry.sysTxBandwidth);
		dataFile.write(dataFormatter.toString());
		dataFile.flush();
		dataFormatter.close();	
	}
	
	
	private static void processParsedLog(ParsedLog parsedLog, String host, 
			CombinedLogEntry combinedServerChunkLog, 
			CombinedLogEntry combinedServerPlayerLog, 
			CombinedLogEntry combinedPerformanceLog, 
			CombinedLogEntry combinedLatencyLog) {

		String chunkDataFilename = host+".chunk.csv";
		String playerDataFilename = host+".player.csv";
		String systemDataFilename = host+".system.csv";
		String latencyDataFilename = host+".latency.csv";


		try {
			
			FileWriter chunkDataFile = createDataFile(chunkDataFilename, "%% %-19s, %-10s%n", "ServerTime", "Chunks");
			FileWriter playerDataFile = createDataFile(playerDataFilename, "%% %-19s, %-7s%n", "ServerTime", "Players");
			FileWriter systemDataFile = createDataFile(systemDataFilename, "%% %-19s, %-12s\n, %-13s, %-13s, %-13s, %-13s, %-13s%n", "ServerTime", "tSYS_CPU [%]", "tPROC_CPU [%]", "tSYS_MEM [MB]", "PROC_MEM [MB]", "RX_BW [kb/s]", "TX_BW[kb/s]");
			FileWriter latencyDataFile = createDataFile(latencyDataFilename, "%% %-19s, %-9s%n", "ServerTime",  "RoundTripTime");

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		
			int serverLogs = 0;
			int performanceLogs = 0; 
			int chunkLogs = 0;
			int playerLogs = 0;
			int latencyLogs = 0;

			for (Long timeStamp : parsedLog.getLogTimes()) {
				LogEntry entry = new LogEntry();
				
				for (Log log : parsedLog.getLogsAtTimeStamp(timeStamp)) {

					entry.timeStamp = timeStamp;		
					if (log instanceof ServerChunkLog) {
						processServerChunkLog((ServerChunkLog)log, entry, chunkDataFile);
						chunkLogs++;
						
						LogEntry combinedLogEntry = combinedServerChunkLog.getLogEntryAtTimeStamp(timeStamp);
						combinedLogEntry.addLoadedChunks(entry.numLoadedChunks);
					} else if (log instanceof ServerPlayerLog) {
						
						processServerPlayerLog((ServerPlayerLog)log, entry, playerDataFile);						
						playerLogs++;
						
						LogEntry combinedLogEntry = combinedServerPlayerLog.getLogEntryAtTimeStamp(timeStamp);
						combinedLogEntry.addPlayers(entry.numPlayers);

					} else if (log instanceof PerformanceLog) {
						processPerformanceLog((PerformanceLog)log, entry, systemDataFile);
						performanceLogs++;

						LogEntry combinedLogEntry = combinedPerformanceLog.getLogEntryAtTimeStamp(timeStamp);
						combinedLogEntry.addPerformance(entry);
					} else if (log instanceof LatencyLog) {
						processLatencyLog((LatencyLog)log, entry, latencyDataFile);
						latencyLogs++;
						combinedLatencyLog.addLogEntryAtNewTimeStamp(entry.timeStamp, entry);
					}
				}

			}
			//System.out.println("Processed "+serverLogs+ " SystemLogs and "+performanceLogs+" PerformanceLogs");
			
			chunkDataFile.close(); 
			playerDataFile.close(); 
			latencyDataFile.close();
			systemDataFile.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

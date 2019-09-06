package org.koekepan.herobrineplugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.koekepan.herobrine.log.Logger;
import org.koekepan.herobrineplugin.logging.logs.PerformanceLog;
import org.koekepan.herobrineplugin.logging.logs.SystemLog;
import org.koekepan.herobrineplugin.logging.logs.ServerChunkLog;
import org.koekepan.herobrineplugin.logging.logs.ServerPlayerLog;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class PerformanceMonitor {

	private Logger logger;
	private Server server;
//	private Plugin plugin;
	
	private int pollInterval = 1000; // polling interval in millisecond
	//private int tickInterval = 20;
	private Future<?> future = null;
	private BukkitTask task = null;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private final Sigar sigar = new Sigar();
	
	private CpuPerc sysCpu;
	private ProcCpu procCpu;
	private Mem sysMem;
	private ProcMem procMem;
	//private volatile int numPlayers;
	
	int cpuCount = 0;
	long sysMemTotal = 0;
	
	// "See note at System Network"
	private Map<String, Long> rxCurrentMap = new HashMap<String, Long>();
	private Map<String, List<Long>> rxChangeMap = new HashMap<String, List<Long>>();
	private Map<String, Long> txCurrentMap = new HashMap<String, Long>();
	private Map<String, List<Long>> txChangeMap = new HashMap<String, List<Long>>();
	
	public PerformanceMonitor(Logger logger, Server server, Plugin plugin, int pollInterval) {
		this.logger = logger;
		this.server = server;
	//	this.plugin = plugin;
		this.pollInterval = pollInterval;
	//	this.tickInterval = pollInterval/50;
		
		try {	
			sysCpu = sigar.getCpuPerc();
			procCpu = sigar.getProcCpu(sigar.getPid());
			sysMem = sigar.getMem();
			procMem = sigar.getProcMem(sigar.getPid());
			
			cpuCount = sigar.getCpuList().length;
			sysMemTotal = sysMem.getRam() * 1024 * 1024;	
		} catch (SigarException e) {
			e.printStackTrace();
		}
	}

	public void intitialize() {
		
		try {
			// log system info
			
			CpuInfo cpu[] = sigar.getCpuInfoList();
			String[] cpuInfo = new String[cpu.length];
			for(int i = 0; i < cpu.length; i++) {
				cpuInfo[i] = cpu[i].getVendor() + " " + cpu[i].getModel() + ", " + cpu[i].getMhz() + " MHz";
			}
			
			String net[] = sigar.getNetInterfaceList();
			String[] netInfo = new String[net.length];
			for (int i = 0; i < net.length; i++) {
				NetInterfaceConfig niConfig = sigar.getNetInterfaceConfig(net[i]);
				NetInterfaceStat niStat = sigar.getNetInterfaceStat(net[i]);
				netInfo[i] = niConfig.getType() + ", " + niConfig.getName() + ", " + niConfig.getDescription() + ", " + niConfig.getAddress() + ", " + niConfig.getHwaddr() + ", " + niStat.getSpeed();
			}
			
			long timestamp = System.currentTimeMillis();
			
			logger.log(new SystemLog(timestamp, sysMemTotal, cpuInfo, netInfo));
			
		} catch (SigarException e) {
			e.printStackTrace();
		}
		
		
		/*task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				numPlayers = server.getOnlinePlayers().size();
			}
		}, 0, tickInterval);*/
		
		// new performance measuring by moving measurement to own function
/*		if (future == null) {
			future = executor.scheduleAtFixedRate(new Runnable(){
				@Override
				public void run() {
					long timestamp = System.currentTimeMillis();
					measure(timestamp);
				}
			}, 0, (long)pollInterval, TimeUnit.MILLISECONDS);
		}*/
		
		/* Old performance measuring
		if (future == null) {
			future = executor.scheduleAtFixedRate(new Runnable(){
				@Override
				public void run() {
					try {
						// log performance
						long timestamp = System.currentTimeMillis();
					//	System.out.println("PerformanceTask::run => Performance measurement at Timestamp: " + timestamp);

						sysCpu = sigar.getCpuPerc();
						double sysCpuUsage = sysCpu.getCombined() * 100.0;
						procCpu.gather(sigar, sigar.getPid()); 
						double procCpuUsage = (procCpu.getPercent() * 100.0)/cpuCount;

						//*	System.out.println("--------------------------------------------------");
						System.out.println("System CPU  : " + sysCpuUsage);
						System.out.println("Process CPU : " + procCpuUsage);*

						sysMem.gather(sigar);
						long sysMemUsage = sysMem.getActualUsed();
						procMem.gather(sigar, sigar.getPid());
						long procMemUsage = procMem.getResident();

						Long[] networkMetrics = getMetric();
						long sysRxBw = networkMetrics[0];
						long sysTxBw = networkMetrics[1];
						long sysRx = networkMetrics[2];
						long sysTx = networkMetrics[3];

						logger.log(new PerformanceLog(timestamp, sysCpuUsage, procCpuUsage, sysMemUsage, procMemUsage, sysRxBw, sysTxBw, sysRx, sysTx));
						
						//System.out.println("PerformanceTask::run => Calculating number of online players at Timestamp: " + timestamp);
						
						//int onlinePlayers = server.getOnlinePlayers().size();;						
						//System.out.println("PerformanceTask::run => "+onlinePlayers+" online players at Timestamp: " + timestamp);

						//System.out.println("PerformanceTask::run => Calculating number of loaded chunks at Timestamp: " + timestamp);
						//int loadedChunks = 0;
						//for (World world : server.getWorlds() ) {
						//	loadedChunks += world.getLoadedChunks().length;
						//}
						//System.out.println("PerformanceTask::run => "+loadedChunks+" chunks loaded at Timestamp: " + timestamp);
						//logger.log(new ServerLog(timestamp, onlinePlayers, loadedChunks));
					//	System.out.println("PerformanceTask::run => Finished measurement at Timestamp: " + timestamp);

					} catch (SigarException e) {
						e.printStackTrace();
					}	
				}
			}, 0, (long)pollInterval, TimeUnit.MILLISECONDS);
		}*/
	}
	
	
	public void measure(long timestamp) {
		//System.out.println("PerformanceTask::run => Calculating number of loaded chunks at Timestamp: " + timestamp);
		int loadedChunks = 0;
		
		// there seems to be a problem with getLoadedChunks causing a null pointer exception
		try {
			loadedChunks = server.getWorlds().get(0).getLoadedChunks().length;
		} catch (NullPointerException e) {
			System.out.println("[ERROR] PerformanceTask::run => NullPointer exception caught!");
			e.printStackTrace();
		}
		//for (World world : server.getWorlds() ) {
//			loadedChunks += world.getLoadedChunks().length;
//		}
		//System.out.println("PerformanceTask::run => "+loadedChunks+" chunks loaded at Timestamp: " + timestamp);
		
		measure(timestamp, loadedChunks);
	}
	
	
	public void measure(long timestamp, int numberOfLoadedChunks) {
		try {
			// log performance
			//System.out.println("PerformanceTask::measure => Performance measurement at Timestamp: " + timestamp);

			sysCpu = sigar.getCpuPerc();
			double sysCpuUsage = sysCpu.getCombined() * 100.0;
			procCpu.gather(sigar, sigar.getPid()); 
			double procCpuUsage = (procCpu.getPercent() * 100.0)/cpuCount;

			/*	System.out.println("--------------------------------------------------");
			System.out.println("System CPU  : " + sysCpuUsage);
			System.out.println("Process CPU : " + procCpuUsage);*/

			sysMem.gather(sigar);
			long sysMemUsage = sysMem.getActualUsed();
			procMem.gather(sigar, sigar.getPid());
			long procMemUsage = procMem.getResident();

			Long[] networkMetrics = getMetric();
			long sysRxBw = networkMetrics[0];
			long sysTxBw = networkMetrics[1];
			long sysRx = networkMetrics[2];
			long sysTx = networkMetrics[3];

			logger.log(new PerformanceLog(timestamp, sysCpuUsage, procCpuUsage, sysMemUsage, procMemUsage, sysRxBw, sysTxBw, sysRx, sysTx));
			
			//System.out.println("PerformanceTask::run => Calculating number of online players at Timestamp: " + timestamp);
			
			int onlinePlayers = server.getOnlinePlayers().size();;			

			logger.log(new ServerPlayerLog(timestamp, onlinePlayers));
			//System.out.println("PerformanceTask::run => "+onlinePlayers+" online players at Timestamp: " + timestamp);

			logger.log(new ServerChunkLog(timestamp, numberOfLoadedChunks));
		//	System.out.println("PerformanceTask::run => Finished measurement at Timestamp: " + timestamp);	
		} catch (SigarException e) {
			e.printStackTrace();
		}	
	}
	

	public void terminate() {
		if (future != null) {
			future.cancel(true);
	//		task.cancel();
			executor.shutdown();
	//		task = null;
			future = null;
		}
	}
	
	
	// **************************************************
	// BEGIN: System Network
	// NOTE: System Network code adapted from "https://gist.github.com/nlinker/3775022". (Retrieved 2016/07/28 12:49 AM)

	private Long[] getMetric() throws SigarException {
		long rxBytes = 0; // EDITED
		long txBytes = 0; // EDITED
		
		for (String ni : sigar.getNetInterfaceList()) {
			NetInterfaceStat netStat = sigar.getNetInterfaceStat(ni);
			NetInterfaceConfig ifConfig = sigar.getNetInterfaceConfig(ni);
			String hwaddr = null;
			if (!NetFlags.NULL_HWADDR.equals(ifConfig.getHwaddr())) {
				hwaddr = ifConfig.getHwaddr();
			}
			if (hwaddr != null) {
				long rxCurrenttmp = netStat.getRxBytes();
				rxBytes += rxCurrenttmp; // EDITED
				saveChange(rxCurrentMap, rxChangeMap, hwaddr, rxCurrenttmp, ni);
				long txCurrenttmp = netStat.getTxBytes();
				txBytes += txCurrenttmp; // EDITED
				saveChange(txCurrentMap, txChangeMap, hwaddr, txCurrenttmp, ni);
			}
		}
		long totalrx = getMetricData(rxChangeMap);
		long totaltx = getMetricData(txChangeMap);
		for (List<Long> l : rxChangeMap.values())
			l.clear();
		for (List<Long> l : txChangeMap.values())
			l.clear();
		//return new Long[] { totalrx, totaltx };
		return new Long[] { totalrx, totaltx, rxBytes, txBytes}; // EDITED
	}

	private long getMetricData(Map<String, List<Long>> rxChangeMap) {
		long total = 0;
		for (Entry<String, List<Long>> entry : rxChangeMap.entrySet()) {
			int average = 0;
			for (Long l : entry.getValue()) {
				average += l;
			}
			total += average / entry.getValue().size();
		}
		return total;
	}

	private void saveChange(Map<String, Long> currentMap, Map<String, List<Long>> changeMap, String hwaddr, long current, String ni) {
		Long oldCurrent = currentMap.get(ni);
		if (oldCurrent != null) {
			List<Long> list = changeMap.get(hwaddr);
			if (list == null) {
				list = new LinkedList<Long>();
				changeMap.put(hwaddr, list);
			}
			list.add((current - oldCurrent));
		}
		currentMap.put(ni, current);
	}
	
	// END: System Network
	// **************************************************
	
}

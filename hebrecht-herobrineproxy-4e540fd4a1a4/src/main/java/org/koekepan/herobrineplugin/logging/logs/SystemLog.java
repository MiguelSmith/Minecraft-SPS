package org.koekepan.herobrineplugin.logging.logs;

import java.io.IOException;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public class SystemLog extends TimestampLog {

	private long memory;
	private String[] cpuInfo;
	private String[] netInfo;
	
	public SystemLog() {
		this(-1, -1, null, null);
	}
	
	public SystemLog(long timestamp, long memory, String[] cpuInfo, String[] netInfo) {
		super(timestamp);
		this.memory = memory;
		this.cpuInfo = cpuInfo;
		this.netInfo = netInfo;
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		
		out.writeVarLong(memory);
		
		out.writeVarInt(cpuInfo.length);
		for(int i = 0; i < cpuInfo.length; i++) {
			out.writeUTF(cpuInfo[i]);
		}
		
		out.writeVarInt(netInfo.length);
		for(int i = 0; i < netInfo.length; i++) {
			out.writeUTF(netInfo[i]);
		}
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);

		memory = in.readVarLong();
		
		int cpuCount = in.readVarInt();
		cpuInfo = new String[cpuCount];
		for(int i = 0; i < cpuCount; i++) {
			cpuInfo[i] = in.readUTF();
		}
		
		int netCount = in.readVarInt();
		netInfo = new String[netCount];
		for(int i = 0; i < netCount; i++) {
			netInfo[i] = in.readUTF();
		}
	}
	
	public long getMemory() {
		return memory;
	}
	
	public String[] getCpuInfo() {
		return cpuInfo;
	}
	
	public String[] getNetworkInfo() {
		return netInfo;
	}
	
}

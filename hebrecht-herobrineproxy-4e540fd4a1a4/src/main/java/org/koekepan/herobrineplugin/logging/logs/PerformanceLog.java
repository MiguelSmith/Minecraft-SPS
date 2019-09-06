package org.koekepan.herobrineplugin.logging.logs;

import java.io.IOException;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public class PerformanceLog extends TimestampLog {

	private double sysCpu;
	private double procCpu;
	private long sysMem;
	private long procMem;
	private long sysRxBw;
	private long sysTxBw;
	private long sysRx;
	private long sysTx;
	
	public PerformanceLog() {
		this(-1, -1, -1, -1, -1, -1, -1, -1, -1);
	}
	
	public PerformanceLog(long timestamp, double sysCpu, double procCpu, long sysMem, long procMem, long sysRxBw, long sysTxBw, long sysRx, long sysTx) {
		super(timestamp);
		this.sysCpu = sysCpu;
		this.procCpu = procCpu;
		this.sysMem = sysMem;
		this.procMem = procMem;
		this.sysRxBw = sysRxBw;
		this.sysTxBw = sysTxBw;
		this.sysRx = sysRx;
		this.sysTx = sysTx;
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		super.write(out);
		out.writeDouble(sysCpu);
		out.writeDouble(procCpu);
		out.writeVarLong(sysMem);
		out.writeVarLong(procMem);
		out.writeVarLong(sysRxBw);
		out.writeVarLong(sysTxBw);
		out.writeVarLong(sysRx);
		out.writeVarLong(sysTx);
	}

	@Override
	public void read(LogInput in) throws IOException {
		super.read(in);
		sysCpu = in.readDouble();
		procCpu = in.readDouble();
		sysMem = in.readVarLong();
		procMem = in.readVarLong();
		sysRxBw = in.readVarLong();
		sysTxBw = in.readVarLong();
		sysRx = in.readVarLong();
		sysTx = in.readVarLong();
	}
	
	public double getSystemCpu() {
		return sysCpu;
	}
	
	public double getProcessCpu() {
		return procCpu;
	}
	
	public long getSystemMemory() {
		return sysMem;
	}
	
	public long getProcessMemory() {
		return procMem;
	}
	
	public long getSystemRxBandwidth() {
		return sysRxBw;
	}
	
	public long getSystemTxBandwidth() {
		return sysTxBw;
	}
	
	public long getSystemRxBytes() {
		return sysRx;
	}
	
	public long getSystemTxBytes() {
		return sysTx;
	}
	
}
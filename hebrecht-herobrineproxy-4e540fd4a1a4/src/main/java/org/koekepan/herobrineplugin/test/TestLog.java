package org.koekepan.herobrineplugin.test;

import java.io.IOException;

import org.koekepan.herobrine.log.Log;
import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public class TestLog implements Log {

	long varLong;
	int varInt;
	
	public TestLog() {
		this(-1, -1);
	}
	
	public TestLog(long varlong, int varInt) {
		this.varLong = varlong;
		this.varInt = varInt;
	}
	
	@Override
	public void write(LogOutput out) throws IOException {
		out.writeVarLong(varLong);
		out.writeVarInt(varInt);
	}

	@Override
	public void read(LogInput in) throws IOException {
		varLong = in.readVarLong();
		varInt = in.readVarInt();
	}
	
	public long getVarLong() {
		return varLong;
	}
	
	public long getVarInt() {
		return varInt;
	}

}

package org.koekepan.herobrine.log.io;

import java.io.DataOutput;
import java.io.IOException;

public interface LogOutput extends DataOutput {
	public void writeVarInt(int i) throws IOException;
	public void writeVarLong(long l) throws IOException;
}

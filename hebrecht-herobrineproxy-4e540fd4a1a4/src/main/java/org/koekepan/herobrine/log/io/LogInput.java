package org.koekepan.herobrine.log.io;

import java.io.DataInput;
import java.io.IOException;

public interface LogInput extends DataInput {
	public int readVarInt() throws IOException;
	public long readVarLong() throws IOException;
}

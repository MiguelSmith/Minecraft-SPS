package org.koekepan.herobrine.log;

import java.io.IOException;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public interface Log {
	public void write(LogOutput out) throws IOException;
	public void read(LogInput in) throws IOException;
}

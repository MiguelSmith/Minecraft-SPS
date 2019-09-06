package org.koekepan.herobrine.log;

import java.io.IOException;

public interface LogWriter {
	public void write(Log log) throws IOException;
	public void flush() throws IOException;
	public void close() throws IOException;
}

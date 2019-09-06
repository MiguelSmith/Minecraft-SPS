package org.koekepan.herobrine.log;

import java.io.IOException;

public interface LogReader {
	public Log read() throws IOException;
	public void close() throws IOException;
}

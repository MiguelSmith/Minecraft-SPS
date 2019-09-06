package org.koekepan.herobrine.log.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.koekepan.herobrine.log.Log;
import org.koekepan.herobrine.log.LogHandler;
import org.koekepan.herobrine.log.LogWriter;
import org.koekepan.herobrine.log.io.stream.LogOutputStream;

public class LogFileWriter implements LogWriter {
	
	private LogHandler logHandler;
	private LogOutputStream out;

	public LogFileWriter(LogHandler logHandler, String filePath) throws FileNotFoundException {
		this.logHandler = logHandler;
		out = new LogOutputStream(new BufferedOutputStream(new FileOutputStream(new File(filePath))));
	}

	@Override
	public void write(Log log) throws IOException {
		logHandler.write(out, log);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}

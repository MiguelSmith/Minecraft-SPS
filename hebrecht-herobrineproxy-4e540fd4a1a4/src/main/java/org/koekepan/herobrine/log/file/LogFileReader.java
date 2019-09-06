package org.koekepan.herobrine.log.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.koekepan.herobrine.log.Log;
import org.koekepan.herobrine.log.LogHandler;
import org.koekepan.herobrine.log.LogReader;
import org.koekepan.herobrine.log.io.stream.LogInputStream;

public class LogFileReader implements LogReader {
	
	private LogHandler logHandler;
	private LogInputStream in;

	public LogFileReader(LogHandler logHandler, String filePath) throws FileNotFoundException {
		this.logHandler = logHandler;
		in = new LogInputStream(new BufferedInputStream(new FileInputStream(new File(filePath))));
	}

	@Override
	public Log read() throws IOException {
		return logHandler.read(in);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
	
}

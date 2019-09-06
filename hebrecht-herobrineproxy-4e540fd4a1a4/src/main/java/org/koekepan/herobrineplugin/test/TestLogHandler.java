package org.koekepan.herobrineplugin.test;

import org.koekepan.herobrine.log.LogHandler;

public class TestLogHandler extends LogHandler {

	public TestLogHandler() {
		register(0, TestLog.class);
	}
}

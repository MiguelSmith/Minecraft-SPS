package org.koekepan.herobrineplugin.logging;

import org.koekepan.herobrine.log.LogHandler;
import org.koekepan.herobrineplugin.logging.logs.LatencyLog;
import org.koekepan.herobrineplugin.logging.logs.PerformanceLog;
import org.koekepan.herobrineplugin.logging.logs.SystemLog;
import org.koekepan.herobrineplugin.logging.logs.ServerChunkLog;
import org.koekepan.herobrineplugin.logging.logs.ServerPlayerLog;
import org.koekepan.herobrineplugin.logging.logs.events.PlayerJoinEventLog;
import org.koekepan.herobrineplugin.logging.logs.events.PlayerMoveEventLog;
import org.koekepan.herobrineplugin.logging.logs.events.PlayerQuitEventLog;
import org.koekepan.herobrineplugin.logging.logs.packets.ClientPlayerPositionAndLookPacketLog;
import org.koekepan.herobrineplugin.logging.logs.packets.ClientPlayerPositionPacketLog;
import org.koekepan.herobrineplugin.logging.logs.packets.ServerPlayerPositionAndLookPacketLog;
import org.koekepan.herobrineplugin.logging.logs.packets.ServerUpdateTimePacketLog;

public class HerobrinePluginLogHandler extends LogHandler {

	public HerobrinePluginLogHandler() {
		register(0, SystemLog.class);
		register(1, PerformanceLog.class);
		register(2, PlayerJoinEventLog.class);
		register(3, PlayerMoveEventLog.class);
		register(4, PlayerQuitEventLog.class);
		register(5, ServerUpdateTimePacketLog.class);
		register(6, ServerPlayerPositionAndLookPacketLog.class);
		register(7, ClientPlayerPositionAndLookPacketLog.class);
		register(8, ClientPlayerPositionPacketLog.class);
		register(9, LatencyLog.class);
		register(10, ServerChunkLog.class);
		register(11, ServerPlayerLog.class);
	}
}

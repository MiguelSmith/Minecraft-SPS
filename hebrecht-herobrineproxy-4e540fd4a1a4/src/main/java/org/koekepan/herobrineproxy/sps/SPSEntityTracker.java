package org.koekepan.herobrineproxy.sps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.packet.behaviours.server.ServerEntityPositionPacketBehaviour;
import org.koekepan.herobrineproxy.session.ISession;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class SPSEntityTracker {
	private Map<Integer, SPSEntity> entities;
	private Map<Integer, String> players;
	private SPSEntity player;
	static Logger logger = LogManager.getLogger(SPSEntityTracker.class);
	
	private ISession session;
	
	public SPSEntityTracker() {}
	
	public SPSEntityTracker(ISession session) {
		ConsoleIO.println("SPSEntityTracker::constructor -> EntityTracker created with session " + session.getClass().getSimpleName());
		entities = new HashMap<>();
		players = new HashMap<>();

		
		this.session = session;
		//session.setEntityTracker(this);
	}
	
	public void addEntity(int entityID, SPSEntity entity) {
		entities.put(entityID, entity);
	}
	
	public SPSEntity getEntity(int entityID) {
		return entities.get(entityID);
	}
	
	public boolean exists(int entityID) {
		return entities.containsKey(entityID);
	}
	
	public void updateEntity(int entityID, SPSEntity entity, Packet packet) {
		entities.put(entityID, entity);
		try {
			if (session.isPositioned()) {
				ConsoleIO.println("SPSEntityTracker::updatEntity => entityID: " + entityID + " player: " + session.getUsername() +" UUID: " + getUUID(getPlayerUsername(entityID)) + "  <" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "," + "> " + packet.getClass().getSimpleName());
				//logger.info("<" + entityID + "> " + getPlayerUsername(entityID) + " moved to <" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + ">");				
				logger.info("", System.currentTimeMillis(),getUUID(getPlayerUsername(entityID)), entity.getX(), entity.getY(), entity.getZ(), entity.getPitch(), entity.getYaw(), session.getUsername(), entityID);
				//logger.debug("entityID: " + entityID + " UUID: " + getUUID(getPlayerUsername(entityID)).toString() + " <" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "," + "> " + packet.getClass().getSimpleName());
				session.sendWithPosition(packet,entity.getX(),entity.getZ(), entity.getPrevX(), entity.getPrevZ(), 0);
			} else {
				session.sendPacket(packet);
			}
		} catch (NullPointerException e)
		{
			// this will happen on client side
		}
	}
	
	public void forwardPacketWithPosition(Packet packet, int x, int y, int radius) {
		session.sendWithPosition(packet, x, y, x, y, radius);
	}

	public void move(Packet packet, int positionType) {
		ServerEntityPositionPacket p = (ServerEntityPositionPacket) packet;
		SPSEntity entity = entities.get(p.getEntityId());
		entity.move(p.getMovementX(), p.getMovementY(), p.getMovementZ());
	}

	//unused
	public void setPlayerID(int entityID, SPSEntity entity, Packet packet) {
		player = entity;
		entities.put(entityID, player);
		ConsoleIO.println("SPSEntityTracker::setPlayerID => Player ID set to " + entityID + " for player " + player.getUUID());
		try {
			if (session.isPositioned()) {
				session.sendWithPosition(packet,entity.getX(),entity.getZ(), entity.getPrevX(), entity.getPrevZ(), 0);
			} else {
				session.sendPacket(packet);
			}
		} catch (NullPointerException e)
		{
			// this will happen on client side
		}
	}

	public void movePlayer(Packet packet) {
		ServerPlayerPositionRotationPacket p = (ServerPlayerPositionRotationPacket) packet;
		session.sendWithPosition(packet,p.getX(),p.getZ(), 0, 0, 0);
	}
	
	public String getPlayerUsername(int entityID) {
		return session.getUsername(entityID);
	}
	
	public void setUUID(String username, UUID uuid) {
		session.setUUID(username, uuid);
	}
	
	public UUID getUUID(String username) {
		return session.getUUID(username);
	}

	public void setPlayerUsername(int entityID, String username) {
		session.setPlayerUsername(entityID, username);
	}
}

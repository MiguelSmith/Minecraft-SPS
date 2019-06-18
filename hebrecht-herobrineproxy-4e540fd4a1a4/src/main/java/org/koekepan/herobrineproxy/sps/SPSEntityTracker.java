package org.koekepan.herobrineproxy.sps;

import java.util.HashMap;
import java.util.Map;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.session.ISession;

import com.github.steveice10.packetlib.packet.Packet;

public class SPSEntityTracker {
	private Map<Integer, SPSEntity> entities;
	
	private ISession spsSession;
	
	public SPSEntityTracker() {}
	
	public SPSEntityTracker(ISession spsSession) {
		ConsoleIO.println("SPSEntityTracker::constructor -> EntityTracker created");
		entities = new HashMap<>();
		
		this.spsSession = spsSession;
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
		ConsoleIO.println("SPSEntityTracker::updateEntity -> stored entity. Sending packet as point publication.");
		try {
		spsSession.sendWithPosition(packet, entity.getX(), entity.getZ(), 0);
		} catch (NullPointerException e)
		{
			// this will happen on client side
		}
	}
}

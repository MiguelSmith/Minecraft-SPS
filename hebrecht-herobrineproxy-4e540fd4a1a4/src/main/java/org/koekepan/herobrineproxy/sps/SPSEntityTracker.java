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
		ConsoleIO.println("SPSEntityTracker::constructor -> EntityTracker created with session " + spsSession.getClass().getSimpleName());
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
		try {
			if (spsSession.isPositioned()) {
				spsSession.sendWithPosition(packet, (int) entity.getX(), (int) entity.getZ(), 0);
			} else {
				spsSession.sendPacket(packet);
			}
		} catch (NullPointerException e)
		{
			// this will happen on client side
		}
	}
	
	public void forwardPacketWithPosition(Packet packet, int x, int y, int radius) {
		spsSession.sendWithPosition(packet, x, y, radius);
	}
}

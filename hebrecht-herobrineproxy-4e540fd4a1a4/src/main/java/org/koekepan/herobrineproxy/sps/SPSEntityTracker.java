package org.koekepan.herobrineproxy.sps;

import java.util.HashMap;
import java.util.Map;

import org.koekepan.herobrineproxy.session.IServerSession;
import org.koekepan.herobrineproxy.session.ISession;

public class SPSEntityTracker {
	private Map<Integer, SPSEntity> entities;
	
	private ISession spsSession;
	
	public SPSEntityTracker(ISession spsSession) {
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
	
	public void updateEntity(int entityID, SPSEntity entity) {
		entities.put(entityID, entity);
		spsSession.
	}
}

package org.koekepan.herobrineproxy.behaviour;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.koekepan.herobrineproxy.ConsoleIO;

public class BehaviourHandler<T> {
	
	private final Map<Class<? extends T>, Behaviour<T>> behaviours = new ConcurrentHashMap<Class<? extends T>, Behaviour<T>>();
	
	public void registerBehaviour(Class<? extends T> type, Behaviour<T> behaviour) {
		if (behaviour != null) {
			behaviours.put(type, behaviour);
		} else {
			behaviours.remove(type);
		}
	}
		
	
	public Behaviour<T> getBehaviour(Class<? extends T> type) {
		return behaviours.get(type);		
	}

	
	public boolean hasBehaviour(Class<? extends T> type) {
		return behaviours.containsKey(type);
	}
	
	
	public void printBehaviours() {
		for (Class<? extends T> clazz : getTypes() ) {
			Behaviour<T> b = behaviours.get(clazz);
			ConsoleIO.println("BehaviourHandler::printBehavious => Key <"+clazz.getSimpleName()+"> has behaviour <"+b.getClass().getSimpleName()+">");
		}
	}
	
//	public Collection< Behaviour<T> > getBehaviours() {
//		return behaviours.values();
//	}

	
	public void clearBehaviours() {
		//ConsoleIO.println("BehaviourHandler::clearBehaviours => Clearing behaviours!");
		behaviours.clear();
	}

	
	public Set<Class<? extends T>> getTypes() {
		return new HashSet<Class<? extends T>>(behaviours.keySet());
	}
	

	public void process(T object) {
		Behaviour<T> behaviour = behaviours.get(object.getClass());
		if (behaviour != null) {
			//ConsoleIO.println("Behaviour <"+behaviour.getClass().getSimpleName()+"> is processing packet <"+object.getClass().getSimpleName()+">");
			behaviour.process(object);
		}	
	}

}

package org.koekepan.herobrine.log;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.koekepan.herobrine.log.io.LogInput;
import org.koekepan.herobrine.log.io.LogOutput;

public abstract class LogHandler {
	
	private Map<Class<? extends Log>, Integer> out = new HashMap<Class<? extends Log>, Integer>();
	private Map<Integer, Class<? extends Log>> in = new HashMap<Integer, Class<? extends Log>>();
	
	public void register(Integer id, Class<? extends Log> type) {
		out.put(type, id);
		in.put(id, type);
	}

	public void write(LogOutput out, Log log) throws IOException {
			out.writeVarInt(getId(log));
			log.write(out);
	}

	public Log read(LogInput in) throws IOException {
		try {
			Log log = getLog(in.readVarInt());
			log.read(in);
			return log;
		} catch(EOFException e) {
			return null;
		}
	}

	private int getId(Log log) {
		return out.get(log.getClass());
	}

	private Log getLog(int id) {
		Class<? extends Log> type = in.get(id);
		//System.out.println("Log id: <"+id+"> with type <"+type.getSimpleName()+">");
		try {
			Constructor<? extends Log> constructor = type.getDeclaredConstructor();
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance();
		} catch(NoSuchMethodError e) {
			throw new IllegalStateException("Log \"" + id + ", " + type + "\" does not have a no-params constructor for instantiation.");
		} catch(Exception e) {
			throw new IllegalStateException("Failed to instantiate log \"" + id + ", " + type + "\".", e);
		}
	}

}

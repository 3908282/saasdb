package org.saasdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.saasdb.raw.DBAccess;
import org.saasdb.raw.pg.PGDBAccess;

public class ObjectPool {



	private static Map<Class<?>, Class<?>> impClasses = new ConcurrentHashMap<Class<?>, Class<?>>();
	private static Map<Class<?>, Object> impInstances = new ConcurrentHashMap<Class<?>, Object>();

	static {
		impClasses.put(DBAccess.Factory.class, PGDBAccess.Factory.class);
	}
	
	public static Object getSingleton(Class<?> interfaze) {
		Object instance = impInstances.get(interfaze);
		if (instance != null)
			return instance;

		Class<?> clazz = impClasses.get(interfaze);

		if (clazz != null) {
			synchronized (impInstances) {
				try {
					instance = clazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("Fatal error, can't initial object of " + clazz);
				}
				impInstances.put(interfaze, instance);
			}
			return instance;
		} else
			throw new RuntimeException("Fatal error, can't initial object of " + interfaze);

	}
}

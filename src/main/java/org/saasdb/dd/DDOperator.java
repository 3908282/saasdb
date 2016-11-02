package org.saasdb.dd;

import java.util.Map;

import org.saasdb.meta.Entity;
import org.saasdb.meta.Field;

public interface DDOperator {
	
	//public Transaction getTransaction();
	public Entity createEntity(String tenantId, Entity entity);
	public boolean deleteEntity(String tenantId, Entity entity);
	public Entity updateEntity(String tenantId, Entity entity);
	
	public Field createFlexField(String tenantId, String entityName, Field field);
	public boolean deleteFlexField(String tenantId, String entityName, String name);
	public Field updateFlexField(String tenantId, String entityName, Field field);
	
	public Entity findEntity(String tenantId, String entityName);
	public Field[] findAllSystemFields(String tenantId, String entityName);
	public Field[] findAllFlexFields(String tenantId, String entityName,boolean excludeDeleted);
	public Field[] findAllFields(String tenantId, String entityName, boolean excludeDeleted);
	public Map<String,Field> findAllFieldAsMap(String tenantId, String entityName, boolean excludeDeleted);
	public Field findFlexField(String tenantId, String entityName, String name);
	public boolean existsFlexField(String tenantId, String entityName, String name);
	public int getFlexFieldCount(String tenantId, String entityName, boolean excludeDeleted);
}

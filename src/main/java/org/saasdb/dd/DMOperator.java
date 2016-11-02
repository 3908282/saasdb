package org.saasdb.dd;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.saasdb.meta.Field;
import org.saasdb.transaction.Transaction;

public interface DMOperator {
	
	//public Transaction getTransaction();
	public DDOperator getDDOperator();
	public void insertData(String tenantId, String entityName, Map<String,Object> valueMap);
	public void updateData(String tenantId, String entityName, String instanceId, Map<String,Object> valueMap);
	public void deleteData(String tenantId, String entityName, String instanceId);
	public void truncateData(String tenantId, String entityName);
	public Iterator<Map<String,Object>> queryData(String tenantId, String entityName, Map<String,Object> whereMap, String[] fields);
	
	public boolean hasData(Transaction trans, String tenantId, String entityName) throws SQLException;
	
	public void deleteIndexData(Transaction trans, String tenantId, String entityName, Field field) throws SQLException;
	//Field从非索引字段变成索引字段
	//public void createIndexData(Transaction trans, String tenantId, String entityName, Field field);
	//从非唯一索引变为唯一索引
	//public void transIndexDataToUnique(Transaction trans, String tenantId, String entityName, Field field);
	//从唯一索引变为非唯一
	//public void transIndexDataToNotUnique(Transaction trans, String tenantId, String entityName, Field field);
}

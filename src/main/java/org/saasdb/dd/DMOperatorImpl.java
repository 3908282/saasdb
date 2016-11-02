package org.saasdb.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.saasdb.DBException;
import org.saasdb.convert.FlexValueConvert;
import org.saasdb.meta.DataType;
import org.saasdb.meta.Entity;
import org.saasdb.meta.Field;
import org.saasdb.raw.Constants;
import org.saasdb.raw.DataTable;
import org.saasdb.raw.IndexTable;
import org.saasdb.raw.RelationshipTable;
import org.saasdb.transaction.Transaction;
import org.saasdb.transaction.TransactionManager;

public class DMOperatorImpl implements DMOperator{

	private DDOperatorImpl ddOperator;
	public DMOperatorImpl()
	{
	
	}
	
	private Transaction getTransaction()
	{
		try {
			return TransactionManager.getOrCreateTransaction(true);
		} catch (SQLException e) {
			throw new DBException("Can't begin transaction.", e);
		}
	}

	@Override
	public DDOperator getDDOperator() {
		return ddOperator;
	}
	
	public void setDDOperator(DDOperatorImpl ddOperator) {
		this.ddOperator = ddOperator;
	}
	
	@Override
	public void insertData(String tenantId, String entityName, Map<String, Object> valueMap) {
		
		Entity entity = getDDOperator().findEntity(tenantId, entityName);
		if(entity==null)
			throw new DBException("Entity " + entityName + " not exists.");
		Map<String,Field> fields = getDDOperator().findAllFieldAsMap(tenantId, entityName, false);
		
		Map<String, Object> rowValues = new HashMap<String,Object>();
		rowValues.put(SystemField.tenantId.getFieldName(), tenantId);
		rowValues.put(SystemField.entityName.getFieldName(), entityName);
		
		IndexTable indexTable = new IndexTable();
		DataTable dataTable = new DataTable();
		
		Transaction trans = getTransaction();
		
		try
		{
			Object id = valueMap.get(entity.getIdFieldName()); //UUIDGenerator.newUUID(tenantId, entityName);
			if(id==null)
				throw new DBException("Object id can't be null.");
			
			String instanceId = id.toString();
			rowValues.put(SystemField.instanceId.getFieldName(), instanceId);
			
			for(Map.Entry<String, Object> entry: valueMap.entrySet())
			{
				String key = entry.getKey();
				if(key.equals(entity.getIdFieldName()))
					continue;
				
				Object value = entry.getValue();
				
				Field field = fields.get(key);
				
				if(field==null)
				{
					throw new DBException("Field " + key + " not exists.");
				}
				
				
							
				if(field.isSystemField())
				{
					rowValues.put(field.getFieldName(), value);
				}
				else
				{
					//lookup can't set defaultValue
					if(value==null)
					{
						if(field.getDefaultValue()!=null)
						{
							value = field.getDefaultValue();
						}
						else if(!field.isNullable())
							throw new DBException("Field " + key + " can't be null.");
						
						rowValues.put(field.getFieldName(), value);
						
						if(value!=null)
							value = FlexValueConvert.fromFlexValue(field.getDataType(), (String)value); //defaultValue is String
						
					}
					else
					{
						int dataType = field.getDataType();
						rowValues.put(field.getFieldName(), FlexValueConvert.toFlexValue(entity, dataType, value));
					}
					
					if(value!=null)
					{
						int dataType = field.getDataType();
						if(dataType==DataType.LOOKUP)
						{
							String targetInstanceId = FlexValueConvert.toFlexValue(entity, dataType, value);
							insertLookupData(trans, tenantId, entityName, instanceId, targetInstanceId, entity, field);
						}
						//index insert earlier than data insert for unique check possibly
						else if(field.isIndex())  //TODO: index or not if value is null?
						{
							insertIndexData(trans, tenantId, entityName, instanceId, value, field, indexTable);
						}
						//TODO: LOOKUP,MASTERDETAIL->relationship
						
						//TODO: CLOB,BLOB
					}
				}
				
			}
			String defaultUser = "";
			if(rowValues.get(SystemField.createdUser.getFieldName())==null)
				rowValues.put(SystemField.createdUser.getFieldName(), defaultUser);
			if(rowValues.get(SystemField.lastModifiedUser.getFieldName())==null)
				rowValues.put(SystemField.lastModifiedUser.getFieldName(), defaultUser);
			
			/*
			Time time = new Time(System.currentTimeMillis());
			if(rowValues.get(SystemField.createdTime.getFieldName())==null)
				rowValues.put(SystemField.createdTime.getFieldName(), time);
			if(rowValues.get(SystemField.lastModifiedTime.getFieldName())==null)
				rowValues.put(SystemField.lastModifiedTime.getFieldName(), time);			
			*/
			DataTable.Row row = new DataTable.Row(rowValues, entity);
			dataTable.insertRow(trans, row);
		}catch(SQLException e)
		{
			throw new DBException("insert data error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//value is String id now, been converted above, value is target instanceid
	private void insertLookupData(Transaction trans, String tenantId, String entityName, String instanceId,
			Object value, Entity entity, Field field) throws SQLException {
		String targetInstanceId = value.toString();
		String targetEntityName = field.getRefEntity();
		
		Map<String,Object> values = new HashMap<String,Object>();

		values.put(SystemField.tenantId.getFieldName(), tenantId);
		values.put(SystemField.entityName.getFieldName(), entityName);
		values.put(SystemField.instanceId.getFieldName(), instanceId);
		values.put(SystemField.fieldNum.getFieldName(), field.getFieldNum());
		values.put(SystemField.targetEntityName.getFieldName(), targetEntityName);
		values.put(SystemField.targetInstanceId.getFieldName(), targetInstanceId);
		
		RelationshipTable.Row row = new RelationshipTable.Row(values, entity, field);
		RelationshipTable table = new RelationshipTable();
		table.insertRow(trans, row);
	}

	
	private void deleteLookupData(Transaction trans, String tenantId, String entityName, String instanceId, Entity entity, Field field) throws SQLException
	{
		Map<String,Object> whereValues = new HashMap<String,Object>();

		whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		
		RelationshipTable.Row row = new RelationshipTable.Row(whereValues, entity, field);
		RelationshipTable table = new RelationshipTable();
		table.deleteRow(trans, row, false);
	}
	
	private void insertIndexData(Transaction trans, String tenantId, String entityName, String instanceId, Object value, Field field, IndexTable indexTable) throws SQLException
	{
		Map<String,Object> indexValues = new HashMap<String,Object>();
		indexValues.put(SystemField.instanceId.getFieldName(), instanceId);
		indexValues.put(SystemField.tenantId.getFieldName(), tenantId);
		indexValues.put(SystemField.entityName.getFieldName(), entityName);
		
		indexValues.put(SystemField.fieldNum.getFieldName(), field.getFieldNum());
		
		switch(field.getDataType())
		{
		case DataType.CHAR:
		case DataType.TEXT:
			indexValues.put(SystemField.stringValue.getFieldName(), value);
			indexValues.put(SystemField.stringValueU.getFieldName(), FlexValueConvert.toUpperCase(value.toString()));
			break;
		case DataType.UUID:
			indexValues.put(SystemField.stringValue.getFieldName(), value);
			break;
		case DataType.INT:
			indexValues.put(SystemField.intValue.getFieldName(), FlexValueConvert.toInt(value));
			break;
		case DataType.NUMERIC:
			indexValues.put(SystemField.numericValue.getFieldName(), FlexValueConvert.toNumber(value));
			break;
		case DataType.DATE:
			indexValues.put(SystemField.dateValue.getFieldName(), FlexValueConvert.toDate(value));
			break;
		case DataType.DATETIME:
		case DataType.TIME:
			indexValues.put(SystemField.timeValue.getFieldName(), FlexValueConvert.toDate(value));
			break;
		}
		
		IndexTable.Row indexRow = new IndexTable.Row(indexValues, field);
		indexTable.insertRow(trans, indexRow);
	}

	private void updateLookupData(Transaction trans, String tenantId, String entityName, String instanceId,
			Object value, Field field, Entity entity) throws SQLException {
		
		Map<String,Object> whereValues = new HashMap<String,Object>();
		Map<String,Object> setValues = new HashMap<String,Object>();
		whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		whereValues.put(SystemField.fieldNum.getFieldName(), field.getFieldNum());
		
		//setValues.put(SystemField.targetEntityName.getFieldName(), field.getRefEntity());
		setValues.put(SystemField.targetInstanceId.getFieldName(), value);
		
		RelationshipTable table = new RelationshipTable();
		table.upateRow(trans, null, whereValues, setValues);
	}
	
	private void updateIndexData(Transaction trans, String tenantId, String entityName, String instanceId, Object value, Field field, IndexTable indexTable) throws SQLException
	{
		Map<String,Object> whereValues = new HashMap<String,Object>();
		Map<String,Object> indexValues = new HashMap<String,Object>();
		whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		
		switch(field.getDataType())
		{
		case DataType.CHAR:
		case DataType.TEXT:
			indexValues.put(SystemField.stringValue.getFieldName(), value);
			indexValues.put(SystemField.stringValueU.getFieldName(), FlexValueConvert.toUpperCase(value.toString()));
			break;
		case DataType.UUID:
			indexValues.put(SystemField.stringValue.getFieldName(), value);
			break;
		case DataType.INT:
			indexValues.put(SystemField.intValue.getFieldName(), FlexValueConvert.toInt(value));
			break;
		case DataType.NUMERIC:
			indexValues.put(SystemField.numericValue.getFieldName(), FlexValueConvert.toNumber(value));
			break;
		case DataType.DATE:
		case DataType.DATETIME:
		case DataType.TIME:
			indexValues.put(SystemField.dateValue.getFieldName(), FlexValueConvert.toDate(value));
			break;
		}
		
		IndexTable.Row indexRow = new IndexTable.Row(indexValues, field);
		indexTable.upateRow(trans, indexRow, whereValues, indexValues);
	}
	
	private void deleteIndexData(Transaction trans, String tenantId, String entityName, String instanceId, Field field, IndexTable indexTable) throws SQLException
	{
		Map<String,Object> whereValues = new HashMap<String,Object>();
		Map<String,Object> indexValues = new HashMap<String,Object>();
		whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		IndexTable.Row indexRow = new IndexTable.Row(indexValues, field);
		indexTable.deleteRow(trans, indexRow, false);
	}
	
	private void truncateIndexData(Transaction trans, String tenantId, String entityName, Field field, IndexTable indexTable) throws SQLException
	{
		Map<String,Object> whereValues = new HashMap<String,Object>();
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		whereValues.put(SystemField.fieldNum.getFieldName(), field.getFieldNum());
		
		IndexTable.Row indexRow = new IndexTable.Row(whereValues, field);
		indexTable.deleteRow(trans, indexRow, false);
	}
	
	@Override
	public void updateData(String tenantId, String entityName, String instanceId, Map<String, Object> valueMap) {
		Entity entity = getDDOperator().findEntity(tenantId, entityName);
		if(entity==null)
			throw new DBException("Entity " + entityName + " not exists.");
		Map<String,Field> fields = getDDOperator().findAllFieldAsMap(tenantId, entityName, false);
		
		Map<String, Object> whereValues = new HashMap<String,Object>();
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		
		Map<String, Object> setValues = new HashMap<String,Object>();
		
		IndexTable indexTable = new IndexTable();
		DataTable dataTable = new DataTable();
		
		Transaction trans = getTransaction();
		
		try
		{

			for(Map.Entry<String, Object> entry: valueMap.entrySet())
			{
				String key = entry.getKey();
				Object value = entry.getValue();
				
				Field field = fields.get(key);
				if(field==null)
				{
					throw new DBException("Field " + key + " not exists.");
				}
							
				if(field.isSystemField())
				{
					setValues.put(field.getFieldName(), value);
				}
				else
				{
					if(value==null)
					{
						if(field.getDefaultValue()!=null)
							value = field.getDefaultValue();
						else if(!field.isNullable())
							throw new DBException("Field " + key + " can't be null.");
					}
					else
					{
						int dataType = field.getDataType();
						value = FlexValueConvert.toFlexValue(entity, dataType, value);
					}
					setValues.put(field.getFieldName(), value);
					
					if(field.getDataType()==DataType.LOOKUP)
					{
						updateLookupData(trans, tenantId, entityName, instanceId, value, field, entity);
					}
					//index insert earlier than data insert for unique check possibly
					else if(field.isIndex())
					{
						updateIndexData(trans, tenantId, entityName, instanceId, value, field, indexTable);
					}
				}
				
				//TODO: LOOKUP,MASTERDETAIL->relationship
				//TODO: CLOB,BLOB
			}
			
			String defaultUser = "";
			if(setValues.get(SystemField.lastModifiedUser.getFieldName())==null)
				setValues.put(SystemField.lastModifiedUser.getFieldName(), defaultUser);
			dataTable.upateRow(trans, null, whereValues, setValues);
		}catch(SQLException e)
		{
			throw new DBException("insert data error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}



	@Override
	public void deleteData(String tenantId, String entityName, String instanceId){
		
		Entity entity = getDDOperator().findEntity(tenantId, entityName);
		if(entity==null)
			throw new DBException("Entity " + entityName + " not exists.");
		Field[] fields = getDDOperator().findAllFlexFields(tenantId, entityName, false);
		
		Map<String, Object> whereValues = new HashMap<String,Object>();
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		
		IndexTable indexTable = new IndexTable();
		DataTable dataTable = new DataTable();
		
		Transaction trans = getTransaction();
		try
		{
			DataTable.Row row = new DataTable.Row(whereValues, entity);
			dataTable.deleteRow(trans, row, false);
			
			for(Field field : fields)
			{
				if(DataType.LOOKUP==field.getDataType())
				{
					deleteLookupData(trans, tenantId, entityName, instanceId, entity, field);
				}
				else if(field.isIndex())
				{
					deleteIndexData(trans, tenantId, entityName, instanceId, field, indexTable);
				}
				//TODO: LOOKUP,MASTERDETAIL->relationship
				//TODO: CLOB,BLOB
			}
		}catch(SQLException e)
		{
			throw new DBException("insert data error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void truncateData(String tenantId, String entityName) {
		
		Entity entity = getDDOperator().findEntity(tenantId, entityName);
		if(entity==null)
			throw new DBException("Entity " + entityName + " not exists.");
		
		
		Field[] fields = getDDOperator().findAllFlexFields(tenantId, entityName, false);
		
		Map<String, Object> whereValues = new HashMap<String,Object>();
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		//whereValues.put(SystemField.instanceId.getFieldName(), instanceId);
		
		IndexTable indexTable = new IndexTable();
		DataTable dataTable = new DataTable();
		
		Transaction trans = getTransaction();
		try
		{
			DataTable.Row row = new DataTable.Row(whereValues, entity);
			dataTable.deleteRow(trans, row, false);
			if(fields!=null)
			{
				for(Field field : fields)
				{
					if(field.isIndex())
					{
						truncateIndexData(trans, tenantId, entityName, field, indexTable);
					}
					//TODO: LOOKUP,MASTERDETAIL->relationship
					//TODO: CLOB,BLOB
				}
			}
		}catch(SQLException e)
		{
			throw new DBException("insert data error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean hasData(Transaction trans, String tenantId, String entityName) throws SQLException {
		DataTable dataTable = new DataTable();
		return dataTable.hasData(trans, tenantId, entityName);
		
	}

	@Override
	public void deleteIndexData(Transaction trans, String tenantId, String entityName, Field field) throws SQLException {
		Map<String,Object> whereValues = new HashMap<String,Object>();

		whereValues.put(SystemField.fieldNum.getFieldName(), field.getFieldNum());
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		IndexTable.Row indexRow = new IndexTable.Row(whereValues, field);
	
		new IndexTable().deleteRow(trans, indexRow, false);

		
	}

	@Override
	public Iterator<Map<String, Object>> queryData(String tenantId, String entityName, Map<String, Object> whereMap,
			String[] selectFields) {
		Entity entity = getDDOperator().findEntity(tenantId, entityName);
		if(entity==null)
			throw new DBException("Entity " + entityName + " not exists.");
		Map<String,Field> fields = getDDOperator().findAllFieldAsMap(tenantId, entityName, false);
		
		Map<String, Object> whereValues = new HashMap<String,Object>();
		whereValues.put(SystemField.tenantId.getFieldName(), tenantId);
		whereValues.put(SystemField.entityName.getFieldName(), entityName);
		
		SqlQuery query = new SqlQuery();
		query.addFromTable(null, Constants.SystemTableNames.DATA, "data");
		
		for(String key: selectFields)
		{
			Field field = fields.get(key);
			if(field==null)
				throw new DBException("Field " + key + " not found.");
			
			query.addSelect(field.getFieldName(), field.getFieldName());
		}
		
		//todo:age=30 and gender=1,  --> ((t_index_int.ffieldnum=1 and fvalue=30) or  (t_index_int.ffieldnum=2 and fvalue=1) )
		Map<String,List<Object[]>> tableInfoMap = new HashMap<String,List<Object[]>>();
		
		ArrayList<Object> values = new ArrayList();
		for(Map.Entry<String, Object> entry:whereMap.entrySet())
		{
			String key = entry.getKey();
			Field field = fields.get(key);
			if(field==null)
				throw new DBException("Field " + key + " not found.");
			if(field.isIndex())
			{
				String table = Constants.getIndexTable(field);
				List<Object[]> list = tableInfoMap.get(table);
				if(list==null)
				{
					list = new ArrayList<Object[]>();
					tableInfoMap.put(table, list);
				}
				list.add(new Object[]{field, entry.getValue()});
				
				//query.addFromTable(null, table, table);
				//query.addWhere("data.finstanceid", "=", table+".finstanceid");
				//query.addWhere(table+".fvalue=?");
				//values.add(entry.getValue());
			}
			else
			{
				query.addWhere("data."+field.getFieldName()+"=?"); 
				
				values.add(FlexValueConvert.toFlexValue(entity, field.getDataType(), entry.getValue()));
			}
			
		}
		
		for(Map.Entry<String, List<Object[]>> entry: tableInfoMap.entrySet())
		{
			String table = entry.getKey();
			
			
			List<Object[]> list = entry.getValue();
			if(list.size()==1)
			{
				query.addFromTable(null, table, table);
				
				Field field = (Field)list.get(0)[0];
				Object value = list.get(0)[1];
				query.addWhere("data.finstanceid", "=", table+".finstanceid");
				query.addWhere(table+".fvalue=? and " + table+".ffieldnum=" + field.getFieldNum());
				values.add(value);
			}
			else
			{
				String where = "(";
				int index = 0;
				for(Object[] pair : list)
				{
					Field field = (Field)pair[0];
					Object value = pair[1];
					if(index++>0)
						where += " and ";
					where += "data.finstanceid in (select finstanceid from "+ table +" where " + table+".fvalue=? and " + table+".ffieldnum=" + field.getFieldNum() + ")";

					values.add(value);
				}
				where += ")";
				query.addWhere(where);
			}
		}
		
		Transaction trans = this.getTransaction();
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ArrayList<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try
		{
			con = trans.getConnection();
			ps = con.prepareStatement(query.toString());
			for(int i=0;i<values.size();i++)
			{
				ps.setObject(i+1, values.get(i));
			}
			
			rs = ps.executeQuery();
			while(rs.next())
			{
				HashMap<String,Object> row = new HashMap<String,Object>();
				for(String key: selectFields)
				{
					Field field = fields.get(key);
					String value = rs.getString(field.getFieldName());
					
					row.put(key, FlexValueConvert.fromFlexValue(field.getDataType(), value));
				}
				
				result.add(row);
			}
			
			return result.iterator();
		}catch(SQLException e)
		{
			throw new DBException("query data error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}

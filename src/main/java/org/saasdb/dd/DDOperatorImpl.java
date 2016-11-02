package org.saasdb.dd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.saasdb.DBException;
import org.saasdb.meta.Entity;
import org.saasdb.meta.Field;
import org.saasdb.raw.EntityTable;
import org.saasdb.raw.FieldTable;
import org.saasdb.transaction.Transaction;
import org.saasdb.transaction.TransactionManager;

public class DDOperatorImpl implements DDOperator{

	private DMOperator dmOperator;
	public DDOperatorImpl()
	{

	}
	
	
	public DMOperator getDmOperator() {
		return dmOperator;
	}



	public void setDmOperator(DMOperator dmOperator) {
		this.dmOperator = dmOperator;
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
	public Entity createEntity(String tenantId, Entity entity) {
		
		Transaction trans = getTransaction();
		EntityTable table = new EntityTable();
		EntityTable.Row row = EntityTable.Row.toRowForInsert(tenantId, entity);
		try
		{
			table.insertRow(trans, row);
		}catch(SQLException e)
		{
			throw new DBException("create entity error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	@Override
	public boolean deleteEntity(String tenantId, Entity entity) {
		Transaction trans = getTransaction();
		EntityTable table = new EntityTable();
		EntityTable.Row row = EntityTable.Row.toRowForDelete(tenantId, entity);
		
		
		try
		{
			Entity entity2 = findEntity(tenantId, entity.getName());
			if(entity2==null)
				return false;
			
			dmOperator.truncateData(tenantId, entity.getName());
			
			Field[] fields = this.findAllFlexFields(tenantId, entity.getName(), false);
			if(fields!=null)
			{
				for(Field field: fields)
				{
					this.deleteFlexField(tenantId, entity.getName(), field.getName());
				}
			}
			
			table.deleteRow(trans, row, true);

		}catch(SQLException e)
		{
			throw new DBException("delete entity error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public Entity updateEntity(String tenantId, Entity entity) {
		Transaction trans = getTransaction();
		EntityTable table = new EntityTable();

		Map<String,Object> where = EntityTable.Row.toMapForWhere(tenantId, entity);
		Map<String,Object> set = EntityTable.Row.toMapForSet(entity);
		
		try
		{
			table.upateRow(trans, entity, where, set);
		}catch(SQLException e)
		{
			throw new DBException("update entity error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	@Override
	public Field createFlexField(String tenantId, String entityName, Field field) {
		
		int fieldNum = this.assignNewFieldNum(tenantId, entityName);
		String fieldName = "fvalue" + fieldNum;
		field.setFieldName(fieldName);
		field.setFieldNum(fieldNum);
		
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		FieldTable.Row row = FieldTable.Row.toFullRow(tenantId, entityName, field);
		try
		{
			table.insertRow(trans, row);
		}catch(SQLException e)
		{
			throw new DBException("create entity error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return field;
	}

	@Override
	public boolean deleteFlexField(String tenantId, String entityName, String name) {
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		FieldTable.Row row = FieldTable.Row.toRowForDelete(tenantId, entityName, name);
		try
		{
			boolean hasData = dmOperator.hasData(trans, tenantId, entityName);
			
			if(hasData)
			{
				Field field = this.findFlexField(tenantId, entityName, name);
				if(field.isIndex())
				{
					dmOperator.deleteIndexData(trans, tenantId, entityName, field);
				}
				table.deleteRow(trans, row, true);
			}
			else
				table.deleteRow(trans, row, true);
			
		}catch(SQLException e)
		{
			throw new DBException("create entity error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public Field updateFlexField(String tenantId, String entityName, Field field) {
		
		return null;
	}

	@Override
	public Entity findEntity(String tenantId, String entityName) {
		Transaction trans = getTransaction();
		EntityTable table = new EntityTable();
		try {
			return table.findEntity(trans, tenantId, entityName);
		} catch(SQLException e)
		{
			throw new DBException("find entity error.", e);
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
	public Field[] findAllSystemFields(String tenantId, String entityName) {
		return SystemField.getAllSystemFields();
	}


	@Override
	public int getFlexFieldCount(String tenantId, String entityName, boolean excludeDeleted) {
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		try {
			return table.getFlexFieldCount(trans, tenantId, entityName, excludeDeleted);
		} catch(SQLException e)
		{
			throw new DBException("find entity error.", e);
		}finally
		{
			try {
				trans.end();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private int assignNewFieldNum(String tenantId, String entityName)
	{
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		try {
			return 1 + table.getMaxFlexFieldNum(trans, tenantId, entityName);
		} catch(SQLException e)
		{
			throw new DBException("find entity error.", e);
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
	public Field[] findAllFlexFields(String tenantId, String entityName,boolean excludeDeleted) {
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		try {
			return table.findAllFlexFields(trans, tenantId, entityName,excludeDeleted);
		} catch(SQLException e)
		{
			throw new DBException("find entity error.", e);
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
	public Field findFlexField(String tenantId, String entityName, String fieldName) {
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		try {
			return table.findFlexField(trans, tenantId, entityName, fieldName);
		} catch(SQLException e)
		{
			throw new DBException("find entity error.", e);
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
	public boolean existsFlexField(String tenantId, String entityName, String fieldName) {
		Transaction trans = getTransaction();
		FieldTable table = new FieldTable();
		try {
			return table.existsFlexFields(trans, tenantId, entityName, fieldName);
		} catch(SQLException e)
		{
			throw new DBException("find entity error.", e);
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
	public Field[] findAllFields(String tenantId, String entityName, boolean excludeDeleted) {
		ArrayList<Field> list = new ArrayList<Field>();
		Field[] fields = this.findAllSystemFields(tenantId, entityName);
		for(Field field: fields)
			list.add(field);
		fields = this.findAllFlexFields(tenantId, entityName, excludeDeleted);
		if(fields!=null)
			for(Field field: fields)
				list.add(field);
		
		return list.toArray(new Field[list.size()]);
	}
	
	@Override
	public Map<String,Field> findAllFieldAsMap(String tenantId, String entityName, boolean excludeDeleted) {
		Map<String,Field> result = new HashMap();
		Field[] fields = this.findAllSystemFields(tenantId, entityName);
		for(Field field: fields)
			result.put(field.getName(), field);
		fields = this.findAllFlexFields(tenantId, entityName, excludeDeleted);
		if(fields!=null)
			for(Field field: fields)
				result.put(field.getName(), field);
		
		return result;
	}
}

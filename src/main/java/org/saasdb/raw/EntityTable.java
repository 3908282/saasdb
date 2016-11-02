package org.saasdb.raw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import org.saasdb.UtilSet;
import org.saasdb.meta.Entity;
import org.saasdb.transaction.Transaction;

public class EntityTable extends ExtendTable<EntityTable.Row,Entity> {

	public static class Row extends ExtendTable.Row {
		public Row(Map<String, Object> values) {
			super(values);
		}
		
		//TODO: CHANG KEY TO FIELD
		public static Row toRowForInsert(String tenantId, Entity entity)
		{
			Map<String, Object> values = new HashMap<String, Object>();
			
			values.put(Constants.SystemFieldNames.TETANTID, tenantId);
			values.put(Constants.SystemFieldNames.NAME, entity.getName());
			values.put(Constants.SystemFieldNames.ALIAS, entity.getAlias());
			values.put(Constants.SystemFieldNames.DESC, entity.getDesc());
			values.put(Constants.SystemFieldNames.CLASSNAME, entity.getClassName());
			values.put(Constants.SystemFieldNames.IDFIELDNAME, entity.getIdFieldName());
			values.put(Constants.SystemFieldNames.IDFIELDDATATYPE, entity.getIdFieldDataType());
			
			values.put(Constants.SystemFieldNames.CREATEDUSER, entity.getCreatedUser());
			values.put(Constants.SystemFieldNames.LASTMODIFIEDUSER, entity.getLastModifiedUser());
			
			long createdTs = entity.getCreatedTime();
			if(createdTs<=0)
				createdTs = System.currentTimeMillis();	
			Time time = new Time(createdTs);
			values.put(Constants.SystemFieldNames.CREATEDTIME, time);
			
			long lastModifiedTs = entity.getLastModifiedTime();
			if(lastModifiedTs<=0)
				lastModifiedTs = System.currentTimeMillis();	
			Time time2 = new Time(lastModifiedTs);
			values.put(Constants.SystemFieldNames.LASTMODIFIEDTIME, time2);
			
			return new Row(values);
		}
		
		public static Map<String, Object> fromResultSet(ResultSet rs) throws SQLException
		{
			Map<String, Object> values = new HashMap();
			values.put(Constants.SystemKeys.TETANTID, rs.getString(Constants.SystemFieldNames.TETANTID));
			//values.put(Constants.SystemKeys.ID, rs.getString(Constants.SystemFieldNames.ID));
			values.put(Constants.SystemKeys.NAME, rs.getString(Constants.SystemFieldNames.NAME));
			values.put(Constants.SystemKeys.ALIAS, rs.getString(Constants.SystemFieldNames.ALIAS));
			values.put(Constants.SystemKeys.DESC, rs.getString(Constants.SystemFieldNames.DESC));
			values.put(Constants.SystemKeys.CLASSNAME, rs.getString(Constants.SystemFieldNames.CLASSNAME));
			values.put(Constants.SystemKeys.IDFIELDNAME, rs.getString(Constants.SystemFieldNames.IDFIELDNAME));
			values.put(Constants.SystemKeys.IDFIELDDATATYPE, rs.getInt(Constants.SystemFieldNames.IDFIELDDATATYPE));
			
			values.put(Constants.SystemKeys.CREATEDUSER, rs.getString(Constants.SystemFieldNames.CREATEDUSER));
			values.put(Constants.SystemKeys.LASTMODIFIEDUSER, rs.getString(Constants.SystemFieldNames.LASTMODIFIEDUSER));
			values.put(Constants.SystemKeys.CREATEDTIME, rs.getTime(Constants.SystemFieldNames.CREATEDTIME));
			values.put(Constants.SystemKeys.LASTMODIFIEDTIME, rs.getTime(Constants.SystemFieldNames.LASTMODIFIEDTIME));
			
			return values;

		}
		
		public static Entity toEntity(Map<String, Object> values) throws SQLException
		{
			Entity entity = new Entity();
			//entity.setId((String)row.values.get(Constants.SystemKeys.ID));
			entity.setName((String)values.get(Constants.SystemKeys.NAME));
			entity.setAlias((String)values.get(Constants.SystemKeys.ALIAS));
			entity.setDesc((String)values.get(Constants.SystemKeys.DESC));
			entity.setClassName((String)values.get(Constants.SystemKeys.CLASSNAME));
			entity.setIdFieldName((String)values.get(Constants.SystemKeys.IDFIELDNAME));
			entity.setIdFieldDataType((Integer)values.get(Constants.SystemKeys.IDFIELDDATATYPE));

			entity.setCreatedUser((String)values.get(Constants.SystemKeys.CREATEDUSER));
			entity.setLastModifiedUser((String)values.get(Constants.SystemKeys.LASTMODIFIEDUSER));
			Time time = (Time)values.get(Constants.SystemKeys.CREATEDTIME);
			entity.setCreatedTime(time.getTime());
			Time time2 = (Time)values.get(Constants.SystemKeys.LASTMODIFIEDTIME);
			entity.setLastModifiedTime(time2.getTime());
			
			return entity;
		}
		
		public static Row toRowForDelete(String tenantId, Entity entity)
		{
			Map<String, Object> values = new HashMap<String,Object>();
			values.put(Constants.SystemFieldNames.TETANTID, tenantId);
			values.put(Constants.SystemFieldNames.NAME, entity.getName());
			
			return new Row(values);
		}
		
		public static Map<String,Object> toMapForWhere(String tenantId, Entity entity)
		{
			Map<String, Object> values = new HashMap<String,Object>();
			values.put(Constants.SystemFieldNames.TETANTID, tenantId);
			values.put(Constants.SystemFieldNames.NAME, entity.getName());
			return values;
		}
		
		public static Map<String,Object> toMapForSet(Entity entity)
		{
			Map<String, Object> values = new HashMap<String,Object>();

			values.put(Constants.SystemFieldNames.NAME, entity.getName());
			values.put(Constants.SystemFieldNames.DESC, entity.getDesc());
			values.put(Constants.SystemFieldNames.IDFIELDNAME, entity.getIdFieldName());
			values.put(Constants.SystemFieldNames.CLASSNAME, entity.getClassName());
			values.put(Constants.SystemFieldNames.IDFIELDDATATYPE, entity.getIdFieldDataType());

			
			values.put(Constants.SystemFieldNames.LASTMODIFIEDUSER, entity.getLastModifiedUser());
			
			long lastModifiedTs = entity.getLastModifiedTime();
			if(lastModifiedTs<=0)
				lastModifiedTs = System.currentTimeMillis();	
			Time time2 = new Time(lastModifiedTs);
			values.put(Constants.SystemFieldNames.LASTMODIFIEDTIME, time2);
			
			return values;
		}
	}

	@Override
	public void insertRow(Transaction trans, Row row) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psInsertEntityRow(con, row.values());

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}

	}

	@Override
	public void deleteRow(Transaction trans, Row row, boolean soft) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psDeleteEntityRow(con, row.values());

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
	}

	@Override
	public void upateRow(Transaction trans, Entity entity, Map<String, Object> where, Map<String, Object> set) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psUpdateEntityRow(con, where, set);

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
	}
	
	public Map<String, Object> findRow(Transaction trans, String tenantId, String entityName) throws SQLException
	{
		DBAccess dba = getDBAccess();
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psFindEntityRow(con, tenantId, entityName);

			rs = ps.executeQuery();
			if(rs.next())
				return Row.fromResultSet(rs);
			else
				return null;
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	public Entity findEntity(Transaction trans, String tenantId, String entityName) throws SQLException {
		Map<String, Object> values = findRow(trans, tenantId, entityName);
		if(values!=null)
			return Row.toEntity(values);
		return null;
	}
}

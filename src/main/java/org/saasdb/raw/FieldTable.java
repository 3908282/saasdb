package org.saasdb.raw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.saasdb.UtilSet;
import org.saasdb.meta.Field;
import org.saasdb.transaction.Transaction;

public class FieldTable extends ExtendTable<FieldTable.Row, Field>{
	
	public static class Row extends ExtendTable.Row
	{
		public Row(Map<String, Object> values) {
			super(values);
		}
		
		public int getFieldNum()
		{
			return (Integer)values.get("fieldNum");
		}
		
		public static Row toFullRow(String tenantId, String entityName, Field field)
		{
			Map<String, Object> values = new HashMap<>();
			values.put(Constants.SystemFieldNames.TETANTID, tenantId);
			values.put(Constants.SystemFieldNames.ENTITYNAME, entityName);
			values.put(Constants.SystemFieldNames.NAME, field.getName());
			values.put(Constants.SystemFieldNames.ALIAS, field.getAlias());
			values.put(Constants.SystemFieldNames.DESC, field.getDesc());
			values.put(Constants.SystemFieldNames.DATATYPE, field.getDataType());
			values.put(Constants.SystemFieldNames.LENGTH, field.getLength());
			values.put(Constants.SystemFieldNames.PRECISE, field.getPrecise());
			values.put(Constants.SystemFieldNames.REFENTITY, field.getRefEntity());
			values.put(Constants.SystemFieldNames.FIELDNAME, field.getFieldName());
			values.put(Constants.SystemFieldNames.FIELDNUM, field.getFieldNum());
			values.put(Constants.SystemFieldNames.DELETED, field.isDeleted());
			values.put(Constants.SystemFieldNames.FIELDNUM, field.getFieldNum());
			values.put(Constants.SystemFieldNames.ISINDEX, field.isIndex());
			values.put(Constants.SystemFieldNames.ISUNIQUE, field.isUnique());
			values.put(Constants.SystemFieldNames.NULLABLE, field.isNullable());
			values.put(Constants.SystemFieldNames.DEFAULTVALUE, field.getDefaultValue());
			values.put(Constants.SystemFieldNames.CREATEDUSER, field.getCreatedUser());
			values.put(Constants.SystemFieldNames.LASTMODIFIEDUSER, field.getLastModifiedUser());
			
			long createdTs = field.getCreatedTime();
			if(createdTs<=0)
				createdTs = System.currentTimeMillis();	
			Time time = new Time(createdTs);
			values.put(Constants.SystemFieldNames.CREATEDTIME, time);
			
			long lastModifiedTs = field.getLastModifiedTime();
			if(lastModifiedTs<=0)
				lastModifiedTs = System.currentTimeMillis();	
			Time time2 = new Time(lastModifiedTs);
			values.put(Constants.SystemFieldNames.LASTMODIFIEDTIME, time2);
			
			return new Row(values);
		}

		public static Row toRowForDelete(String tenantId, String entityName, String name) {
			Map<String, Object> values = new HashMap<>();
			values.put(Constants.SystemFieldNames.TETANTID, tenantId);
			values.put(Constants.SystemFieldNames.ENTITYNAME, entityName);
			values.put(Constants.SystemFieldNames.NAME, name);
			
			return new Row(values);
		}
	}

	@Override
	public void insertRow(Transaction trans, Row row) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psInsertFieldRow(con, row.values());

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
			//if(false)
			//	ps = dba.psSoftDeleteFieldRow(con, row.values());
			//else
			
			ps = dba.psDeleteFieldRow(con, row.values());
			
			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
	}

	@Override
	public void upateRow(Transaction trans, Field field, Map<String, Object> where, Map<String, Object> set) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psUpdateFieldRow(con, where, set);

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
	}

	public int getMaxFlexFieldNum(Transaction trans, String tenantId, String entityName) throws SQLException {
		DBAccess dba = getDBAccess();

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psGetMaxFlexFieldNum(con, tenantId, entityName);

			rs = ps.executeQuery();
			if(rs.next())
				return rs.getInt(1);
			else
				return -1;
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	public int getFlexFieldCount(Transaction trans, String tenantId, String entityName, boolean excludeDeleted) throws SQLException {
		DBAccess dba = getDBAccess();

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psGetFlexFieldCount(con, tenantId, entityName, excludeDeleted);

			rs = ps.executeQuery();
			if(rs.next())
				return rs.getInt(1);
			else
				return 0;
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	public Field findFlexField(Transaction trans, String tenantId, String entityName, String fieldName) throws SQLException {
		DBAccess dba = getDBAccess();

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psFindFlexField(con, tenantId, entityName, fieldName);

			rs = ps.executeQuery();
			if(rs.next())
				return toField(rs);
			else
				return null;
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}


	public boolean existsFlexFields(Transaction trans, String tenantId, String entityName, String fieldName) throws SQLException {
		DBAccess dba = getDBAccess();

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psExistFlexField(con, tenantId, entityName, fieldName);

			rs = ps.executeQuery();
			return rs.next();
			
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	public Field[] findAllFlexFields(Transaction trans, String tenantId, String entityName, boolean excludeDeleted) throws SQLException {
		DBAccess dba = getDBAccess();

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psFindAllFlexFields(con, tenantId, entityName, excludeDeleted);

			rs = ps.executeQuery();
			ArrayList<Field> list = new ArrayList<Field>();
			while(rs.next())
			{
				Field field = toField(rs);
				list.add(field);
			}
			if(list.isEmpty())
				return null;
			else
				return list.toArray(new Field[list.size()]);
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	private Field toField(ResultSet rs) throws SQLException {
		Field field = new Field();
		field.setName((rs.getString(Constants.SystemFieldNames.NAME)));
		field.setAlias(rs.getString(Constants.SystemFieldNames.ALIAS));
		field.setDesc(rs.getString(Constants.SystemFieldNames.DESC));
		field.setDataType(rs.getByte(Constants.SystemFieldNames.DATATYPE));
		field.setLength((rs.getInt(Constants.SystemFieldNames.LENGTH)));
		field.setPrecise((rs.getInt(Constants.SystemFieldNames.PRECISE)));
		field.setRefEntity((rs.getString(Constants.SystemFieldNames.REFENTITY)));
		field.setNullable((rs.getBoolean(Constants.SystemFieldNames.NULLABLE)));
		field.setDefaultValue((rs.getString(Constants.SystemFieldNames.DEFAULTVALUE)));
		field.setDeleted((rs.getBoolean(Constants.SystemFieldNames.DELETED)));
		field.setIndex((rs.getBoolean(Constants.SystemFieldNames.ISINDEX)));
		field.setUnique((rs.getBoolean(Constants.SystemFieldNames.ISUNIQUE)));
		
		field.setFieldNum((rs.getInt(Constants.SystemFieldNames.FIELDNUM)));
		field.setFieldName((rs.getString(Constants.SystemFieldNames.FIELDNAME)));
		field.setFlex(true);
		
		return field;
	}

}

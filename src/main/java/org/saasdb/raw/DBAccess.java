package org.saasdb.raw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.sql.DataSource;

import org.saasdb.UtilSet;
import org.saasdb.meta.Field;

public abstract class DBAccess {

	public static abstract class Factory
	{
		public abstract DBAccess getDBAccess();
	}
	
	private DataSource dataSource;
	protected DBAccess(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
	
	
	protected PreparedStatement psInsertEntityRow(Connection con, Map<String,Object> values) throws SQLException
	{
		String table = Constants.SystemTableNames.ENTITY;
		return psInsertCommon(con, table, values);
	}

	public PreparedStatement psDeleteEntityRow(Connection con, Map<String, Object> values) throws SQLException {
		
		String table = Constants.SystemTableNames.ENTITY;
		return psDeleteCommon(con, table, values);
	}

	public PreparedStatement psUpdateEntityRow(Connection con, Map<String, Object> where, Map<String, Object> set) throws SQLException {
		String table = Constants.SystemTableNames.ENTITY;
		return psUpdateCommon(con, table, where, set);
	}

	public PreparedStatement psInsertFieldRow(Connection con, Map<String, Object> values) throws SQLException {
		String table = Constants.SystemTableNames.FIELD;
		return psInsertCommon(con, table, values);
	}

	
	//软删除
	/*
	public PreparedStatement psSoftDeleteFieldRow(Connection con, Map<String, Object> values) throws SQLException {
			String sql = "update " + Constants.SystemTableNames.FIELD + "set "+Constants.SystemFieldNames.DELETED+"=true where " 
					+ Constants.SystemFieldNames.TETANTID + "=? and " 
					+ Constants.SystemFieldNames.ENTITYNAME + "=? and " 
					+ Constants.SystemFieldNames.NAME + "=?" ;
			
			PreparedStatement ps = con.prepareStatement(sql);
			int index=1;
			
			ps.setString(index++, (String)values.get(Constants.SystemKeys.TETANTID));
			ps.setString(index++, (String)values.get(Constants.SystemKeys.ENTITYNAME));
			ps.setString(index++, (String)values.get(Constants.SystemKeys.NAME));
			return ps;
	}
	*/
	
	public PreparedStatement psDeleteFieldRow(Connection con, Map<String, Object> values) throws SQLException {
		
		String table = Constants.SystemTableNames.FIELD;
		return psDeleteCommon(con, table, values);
	}

	public PreparedStatement psUpdateFieldRow(Connection con, Map<String, Object> where, Map<String, Object> set) throws SQLException {
		
		String table = Constants.SystemTableNames.FIELD;
		return psUpdateCommon(con, table, where, set);
	}

	public PreparedStatement psFindEntityRow(Connection con, String tenantId, String entityName) throws SQLException {
		String sql = "select * from " + Constants.SystemTableNames.ENTITY + " where " 
				+ Constants.SystemFieldNames.TETANTID + "=? and " 
				+ Constants.SystemFieldNames.NAME + "=?";
		
		PreparedStatement ps = con.prepareStatement(sql);
		int index=1;
		
		ps.setString(index++, tenantId);
		ps.setString(index++, entityName);
		return ps;
	}

	public PreparedStatement psGetMaxFlexFieldNum(Connection con, String tenantId, String entityName) throws SQLException {
		String sql = "select max("+Constants.SystemFieldNames.FIELDNUM+") from " + Constants.SystemTableNames.FIELD + " where " 
				+ Constants.SystemFieldNames.TETANTID + "=? and " 
				+ Constants.SystemFieldNames.ENTITYNAME + "=?";
		
		PreparedStatement ps = con.prepareStatement(sql);
		int index=1;
		
		ps.setString(index++, tenantId);
		ps.setString(index++, entityName);
		return ps;
	}

	public PreparedStatement psGetFlexFieldCount(Connection con, String tenantId, String entityName,
			boolean excludeDeleted) throws SQLException {
		String sql = "select count(*) from " + Constants.SystemTableNames.FIELD + " where " 
				+ Constants.SystemFieldNames.TETANTID + "=? and " 
				+ Constants.SystemFieldNames.ENTITYNAME + "=?";
		if(excludeDeleted)
			sql += " and " + Constants.SystemFieldNames.DELETED + "=false";
		
		PreparedStatement ps = con.prepareStatement(sql);
		int index=1;
		
		ps.setString(index++, tenantId);
		ps.setString(index++, entityName);
		return ps;
	}

	public PreparedStatement psFindFlexField(Connection con, String tenantId, String entityName, String fieldName) throws SQLException {
		String sql = "select * from " + Constants.SystemTableNames.FIELD + " where " 
				+ Constants.SystemFieldNames.TETANTID + "=? and " 
				+ Constants.SystemFieldNames.ENTITYNAME + "=? and "
				+ Constants.SystemFieldNames.NAME + "=?";
		
		PreparedStatement ps = con.prepareStatement(sql);
		int index=1;
		
		ps.setString(index++, tenantId);
		ps.setString(index++, entityName);
		ps.setString(index++, fieldName);
		return ps;
	}

	public PreparedStatement psExistFlexField(Connection con, String tenantId, String entityName, String fieldName) throws SQLException {
		String sql = "select 1 from " + Constants.SystemTableNames.FIELD + " where " 
				+ Constants.SystemFieldNames.TETANTID + "=? and " 
				+ Constants.SystemFieldNames.ENTITYNAME + "=? and "
				+ Constants.SystemFieldNames.NAME + "=?";
		
		PreparedStatement ps = con.prepareStatement(sql);
		int index=1;
		
		ps.setString(index++, tenantId);
		ps.setString(index++, entityName);
		ps.setString(index++, fieldName);
		return ps;
	}

	public PreparedStatement psFindAllFlexFields(Connection con, String tenantId, String entityName,
			boolean excludeDeleted) throws SQLException {
		String sql = "select * from " + Constants.SystemTableNames.FIELD + " where " 
				+ Constants.SystemFieldNames.TETANTID + "=? and " 
				+ Constants.SystemFieldNames.ENTITYNAME + "=?";
		if(excludeDeleted)
			sql += " and " + Constants.SystemFieldNames.DELETED + "=false";
		
		PreparedStatement ps = con.prepareStatement(sql);
		int index=1;
		
		ps.setString(index++, tenantId);
		ps.setString(index++, entityName);
		return ps;
	}

	public PreparedStatement psInsertDataRow(Connection con, Map<String, Object> valueMap) throws SQLException {
		String sql = "insert into " + Constants.SystemTableNames.DATA + "(";
		String columns = "";
		
		int count = 0;
		ArrayList<Object> valueList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : valueMap.entrySet())
		{
			if(count++>0)
				columns += ",";
			String fieldName = entry.getKey();
			columns += fieldName;
			valueList.add(entry.getValue());
		}
		columns += "," + Constants.SystemFieldNames.CREATEDTIME + "," + Constants.SystemFieldNames.LASTMODIFIEDTIME;
		sql += columns + ") values(" + UtilSet.Strings.nqm(count) + ", now(), now())";
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<count;i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}

	public PreparedStatement psDeleteDataRow(Connection con, Map<String, Object> where, boolean soft) throws SQLException {
		String sql = "delete from " + Constants.SystemTableNames.DATA;
		int count = 0;
		String whereColumns = "";
		ArrayList<Object> valueList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : where.entrySet())
		{
			if(count++>0)
				whereColumns += " and ";
			String fieldName = entry.getKey();
			whereColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		
		sql +=  " where " + whereColumns;
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<valueList.size();i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}

	public PreparedStatement psUpdateDataRow(Connection con, Map<String, Object> where, Map<String, Object> set) throws SQLException {
		
		String sql = "update " + Constants.SystemTableNames.DATA + " set ";
		String setColumns = "";
		
		int count = 0;
		ArrayList<Object> valueList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : set.entrySet())
		{
			if(count++>0)
				setColumns += ",";
			String fieldName = entry.getKey();
			setColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		setColumns += "," + Constants.SystemFieldNames.LASTMODIFIEDTIME + "=now()";
		
		String whereColumns = "";
		
		count = 0;
		
		for(Map.Entry<String, Object> entry : where.entrySet())
		{
			if(count++>0)
				whereColumns += " and ";
			String fieldName = entry.getKey();
			whereColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		
		sql += setColumns + " where " + whereColumns;
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<valueList.size();i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}
	
	//limit 1
	public PreparedStatement psHasDataRow(Connection con, Map<String, Object> where) throws SQLException {
		
		String sql = "select 1 from " + Constants.SystemTableNames.DATA;

		
		String whereColumns = "";
		
		int count = 0;
		ArrayList<Object> valueList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : where.entrySet())
		{
			if(count++>0)
				whereColumns += " and ";
			String fieldName = entry.getKey();
			whereColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		
		sql +=  " where " + whereColumns + " limit 1";
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<valueList.size();i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}
	
	private String getIndexTable(Field field)
	{
		return Constants.getIndexTable(field);
	}
	public PreparedStatement psInsertIndexRow(Connection con, IndexTable.Row row) throws SQLException {
		String table = getIndexTable(row.getField());
		
		String sql = "insert into " + table + "(";
		String columns = "";
		
		int count = 0;
		ArrayList<Object> valueList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : row.values.entrySet())
		{
			if(count++>0)
				columns += ",";
			String fieldName = entry.getKey();
			columns += fieldName;
			valueList.add(entry.getValue());
		}
		sql += columns + ") values(" + UtilSet.Strings.nqm(count) + ")";
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<count;i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;

	}
	
	private PreparedStatement psDeleteCommon(Connection con, String table, Map<String, Object> where) throws SQLException
	{
		String sql = "delete from " + table;
		int count = 0;
		String whereColumns = "";
		ArrayList valueList = new ArrayList();
		for(Map.Entry<String, Object> entry : where.entrySet())
		{
			if(count++>0)
				whereColumns += " and ";
			String fieldName = entry.getKey();
			whereColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		
		sql +=  " where " + whereColumns;
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<valueList.size();i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}

	public PreparedStatement psDeleteIndexRow(Connection con, IndexTable.Row row, boolean soft) throws SQLException {
		
		String table = Constants.getIndexTable(row.getField());
		return psDeleteCommon(con, table, row.values());

	}
	
	private PreparedStatement psUpdateCommon(Connection con, String table, Map<String, Object> where,
			Map<String, Object> set) throws SQLException {
		String sql = "update " + table + " set ";
		String setColumns = "";
		
		int count = 0;
		ArrayList<Object> valueList = new ArrayList<Object>();
		for(Map.Entry<String, Object> entry : set.entrySet())
		{
			if(count++>0)
				setColumns += ",";
			String fieldName = entry.getKey();
			setColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		
		String whereColumns = "";
		
		count = 0;
		
		for(Map.Entry<String, Object> entry : where.entrySet())
		{
			if(count++>0)
				whereColumns += " and ";
			String fieldName = entry.getKey();
			whereColumns += fieldName + "=?";
			valueList.add(entry.getValue());
		}
		
		sql += setColumns + " where " + whereColumns;
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<valueList.size();i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}

	public PreparedStatement psUpdateIndexRow(Connection con, IndexTable.Row row, Map<String, Object> where,
			Map<String, Object> set) throws SQLException {
		String table = getIndexTable(row.getField());
		return psUpdateCommon(con, table, where, set);
		
	}

	private PreparedStatement psInsertCommon(Connection con, String table, Map<String,Object> values) throws SQLException
	{
		String sql = "insert into " + table + "(";
		String columns = "";
		
		int count = 0;
		ArrayList<Object> valueList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : values.entrySet())
		{
			if(count++>0)
				columns += ",";
			String fieldName = entry.getKey();
			columns += fieldName;
			valueList.add(entry.getValue());
		}
		sql += columns + ") values(" + UtilSet.Strings.nqm(count) + ")";
		PreparedStatement ps = con.prepareStatement(sql);
		
		for(int i=0;i<count;i++)
			ps.setObject(i+1, valueList.get(i));
		return ps;
	}
	
	public PreparedStatement psInsertRelationshipRow(Connection con, RelationshipTable.Row row) throws SQLException {
		String table = Constants.SystemTableNames.RELATIONSHIP;
		
		return psInsertCommon(con, table, row.values());

	}

	public PreparedStatement psDeleteRelationshipRow(Connection con, RelationshipTable.Row row, boolean soft) throws SQLException {
		String table = Constants.SystemTableNames.RELATIONSHIP;
		return psDeleteCommon(con, table, row.values());
	}

	public PreparedStatement psUpdateRelationshipRow(Connection con, RelationshipTable.Row row, Map<String, Object> where,
			Map<String, Object> set) throws SQLException {
		String table = Constants.SystemTableNames.RELATIONSHIP;
		return psUpdateCommon(con, table, where, set);
	}

	
}

package org.saasdb.raw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.saasdb.UtilSet;
import org.saasdb.meta.Entity;
import org.saasdb.meta.Field;
import org.saasdb.transaction.Transaction;

public class RelationshipTable extends ExtendTable<RelationshipTable.Row, RelationshipTable.Row>{
	
	public static class Row extends ExtendTable.Row
	{

		private Entity entity;
		private Field field;
		
		public Row(Map<String, Object> values, Entity entity, Field field) {
			super(values);
			this.entity = entity;
			this.field = field;
		}		
		
		public Field getField()
		{
			return field;
		}
		
		public Entity getEntity()
		{
			return entity;
		}
	}

	@Override
	public void insertRow(Transaction trans, Row row) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psInsertRelationshipRow(con, row);

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
			ps = dba.psDeleteRelationshipRow(con, row, soft);

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
		
	}

	@Override
	public void upateRow(Transaction trans, RelationshipTable.Row row,Map<String, Object> where, Map<String, Object> set) throws SQLException {

		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psUpdateRelationshipRow(con, row, where, set);

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
	}



}

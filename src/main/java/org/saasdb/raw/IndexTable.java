package org.saasdb.raw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.saasdb.UtilSet;
import org.saasdb.meta.Field;
import org.saasdb.transaction.Transaction;

public class IndexTable extends ExtendTable<IndexTable.Row, IndexTable.Row>{
	
	public static class Row extends ExtendTable.Row
	{

		private Field field;
		
		public Row(Map<String, Object> values, Field field) {
			super(values);
			this.field = field;
		}		
		
		public Field getField()
		{
			return field;
		}
	}

	@Override
	public void insertRow(Transaction trans, Row row) throws SQLException {
		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psInsertIndexRow(con, row);

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
			ps = dba.psDeleteIndexRow(con, row, soft);

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
		
	}

	@Override
	public void upateRow(Transaction trans, IndexTable.Row row,Map<String, Object> where, Map<String, Object> set) throws SQLException {

		DBAccess dba = getDBAccess();

		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psUpdateIndexRow(con, row, where, set);

			ps.executeUpdate();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(ps);
		}
	}



}

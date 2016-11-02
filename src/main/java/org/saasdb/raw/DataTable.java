package org.saasdb.raw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.saasdb.UtilSet;
import org.saasdb.dd.SystemField;
import org.saasdb.meta.Entity;
import org.saasdb.transaction.Transaction;

//虚拟表
public class DataTable extends ExtendTable<DataTable.Row,Object> {

	//the key of row values's is database fieldName, not object key
	public static class Row extends ExtendTable.Row {
		
		private Entity entity;
		public Row(Map<String, Object> values, Entity entity) {
			super(values);
			this.entity = entity;
		}
	}

	
	@Override
	public void insertRow(Transaction trans, DataTable.Row row) throws SQLException {
		DBAccess dba = getDBAccess();
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psInsertDataRow(con, row.values());
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	@Override
	public void deleteRow(Transaction trans, Row row, boolean soft) throws SQLException {
		DBAccess dba = getDBAccess();
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psDeleteDataRow(con, row.values(), soft);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}

	@Override
	public void upateRow(Transaction trans, Object none, Map<String, Object> where, Map<String, Object> set) throws SQLException {
		DBAccess dba = getDBAccess();
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			ps = dba.psUpdateDataRow(con, where, set);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
		
	}

	public boolean hasData(Transaction trans, String tenantId, String entityName) throws SQLException {
		DBAccess dba = getDBAccess();
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection con = null;
		try {
			con = trans.getConnection();
			HashMap<String,Object> where = new HashMap();
			where.put(SystemField.tenantId.getFieldName(), tenantId);
			where.put(SystemField.entityName.getFieldName(), entityName);
			
			ps = dba.psHasDataRow(con, where);
			
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			trans.setException(e);
			throw e;
		} finally {
			UtilSet.close(rs, ps);
		}
	}



}

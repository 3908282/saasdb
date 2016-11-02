package org.saasdb.raw;

import java.sql.SQLException;
import java.util.Map;

import org.saasdb.ObjectPool;
import org.saasdb.transaction.Transaction;

public abstract class ExtendTable<R extends ExtendTable.Row, Meta> {
	public static class Row
	{
		protected Map<String,Object> values;
		public Row(Map<String,Object> values)
		{
			this.values = values;
		}
		public Map<String,Object> values()
		{
			return values;
		}
	}
	
	//public abstract List<Row> lookupRowsByPrimaryKey(Connection con, Map<String,Object> where) throws SQLException;
	public abstract void insertRow(Transaction trans, R row) throws SQLException;
	public abstract void deleteRow(Transaction trans, R row, boolean soft) throws SQLException;
	public abstract void upateRow(Transaction trans, Meta meta, Map<String,Object> where,Map<String,Object> set) throws SQLException;
	
	public DBAccess getDBAccess()
	{
		DBAccess.Factory factory = (DBAccess.Factory)ObjectPool.getSingleton(DBAccess.Factory.class);
		return factory.getDBAccess();
	}
	
}

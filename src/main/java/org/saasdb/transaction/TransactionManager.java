package org.saasdb.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import org.saasdb.DBException;
import org.saasdb.ObjectPool;
import org.saasdb.raw.DBAccess;

public class TransactionManager {
	
	private static ThreadLocal<Transaction> local = new ThreadLocal<Transaction>();
	
	public static Transaction getOrCreateTransaction(boolean begin) throws SQLException {
		Transaction t = local.get();
		if(t!=null)
			return t;
		else
		{
			t = newTransaction();
			local.set(t);
			if(begin)
				t.begin();
			return t;
		}
	}

	private static Transaction newTransaction() {
		DBAccess.Factory factory = (DBAccess.Factory) ObjectPool.getSingleton(DBAccess.Factory.class);
		Connection con;
		try {
			con = factory.getDBAccess().getConnection();
			Transaction t = new Transaction();
			t.init(con);
			return t;
		} catch (SQLException e) {
			throw new DBException("Can't get Connection or init transaction", e);
		}

	}
}

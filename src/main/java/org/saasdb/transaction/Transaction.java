package org.saasdb.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

	private Connection con;
	public Transaction()
	{
		
	}
	public Transaction(Connection con)
	{
		this.con = con;
	}
	
	public void init(Connection con)
	{
		this.con = con;
		this.e = null;
	}
	
	public Connection getConnection()
	{
		return con;
	}
	
	public void begin() throws SQLException
	{
		e = null;
		con.setAutoCommit(false);
	}
	
	private Exception e;
	public void setException(Exception e)
	{
		this.e = e;
	}
	
	public void commit() throws SQLException
	{
		if(e!=null)
		{
			rollback();
			
			throw new SQLException("Can't commit for the exception occur." , e);
		}
		else
		{
			con.commit();
		}
		//con.close();
	}
	
	public void rollback() throws SQLException
	{
		con.rollback();
	}
	
	public void end() throws SQLException
	{
		if(e!=null)
		{
			e.printStackTrace();
			rollback();
		}
		else
			commit();
	}
}

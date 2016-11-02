package org.saasdb.raw.pg;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.saasdb.raw.DBAccess;

public class PGDBAccess extends DBAccess{

	protected PGDBAccess(DataSource dataSource) {
		super(dataSource);
	}

	public static class Factory extends DBAccess.Factory
	{

		DBAccess access = innerGetDBAccess();
		
		@Override
		public DBAccess getDBAccess() {
			return access;
		}

		public DBAccess innerGetDBAccess() {
			DataSource dataSource = initDataSource();
			return new PGDBAccess(dataSource);
		}
		
		private DataSource initDataSource()
		{
			String driverClassName = "org.postgresql.Driver";
			String url = "jdbc:postgresql://localhost:5432/oodb";
			Properties props = new Properties();
			props.setProperty("driverClassName", driverClassName);
			props.setProperty("url", url);
			props.setProperty("user","postgres");
			props.setProperty("password","postgres");
			props.setProperty("initialSize", ""+5);
			props.setProperty("maxActive", ""+100);
			props.setProperty("defaultAutoCommit", "false");
			props.setProperty("maxWait", ""+6000);
			
			//props.setProperty("ssl","true");
			//Connection conn = DriverManager.getConnection(url, props);
			
			try
			{
				return BasicDataSourceFactory.createDataSource(props);
			}catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("can't init postgresql datasource.", e);
				
			}
		}
	}
}

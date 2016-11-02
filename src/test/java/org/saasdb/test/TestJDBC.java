package org.saasdb.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class TestJDBC {
	public static void main(String[] args) throws Exception
	{
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost:5432/oodb";
		Properties props = new Properties();
		props.setProperty("user","postgres");
		props.setProperty("password","postgres");
		//props.setProperty("ssl","true");
		Connection conn = DriverManager.getConnection(url, props);
		Statement stmt = conn.createStatement();
		stmt.execute("create table if not exists test1(id numeric, value text)");
		stmt.execute("insert into test1 values(1, 'abcdded')");
		ResultSet rs = stmt.executeQuery("select * from test1");
		rs.next();
		String text = rs.getString(2);
		System.out.println(text);
		rs.close();
		stmt.close();
		conn.close();
	}
}

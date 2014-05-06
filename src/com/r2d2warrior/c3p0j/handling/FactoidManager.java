package com.r2d2warrior.c3p0j.handling;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class FactoidManager
{
	
	private String dbLocation;
	private Connection conn;
	
	public FactoidManager()
	{
		this("factoids.db");
	}
	
	private FactoidManager(String dbLocation)
	{
		this.dbLocation = dbLocation;
		refreshConnection();
		
		File dbFile = new File(dbLocation);
		if (!dbFile.exists())
		{
			try
			{
				dbFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}
	
	public void addFactoid(String name, String data) throws SQLException
	{
		if (factoidExists(name))
			return;
		
		refreshConnection();
		
		String insert = String.format("INSERT INTO factoids VALUES ('%s', '%s');", name, data);
		conn.createStatement().execute(insert);
	}
	
	public void removeFactoid(String name) throws SQLException
	{
		if (!factoidExists(name))
			return;
		
		conn.createStatement().execute("DELETE FROM factoids WHERE name = '" + name + "'");
	}
	
	public String getFactoidData(String name)
	{
		if (!factoidExists(name))
			return "";
		refreshConnection();
		try
		{
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM factoids WHERE name = '" + name + "'");
			rs.next();
			return rs.getString("data");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public LinkedHashMap<String, String> getAllFactoids()
	{
		refreshConnection();
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		try
		{
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM factoids");
			while (rs.next())
				map.put(rs.getString("name"), rs.getString("data"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	public boolean factoidExists(String name)
	{
		refreshConnection();
		try
		{
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM factoids WHERE name = '" + name + "'");
			return rs.next();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private void refreshConnection()
	{
		try
		{
			if (conn != null)
				conn.close();
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
			conn.createStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS factoids ("
					+ "name TEXT PRIMARY KEY,"
					+ "data TEXT"
					+ ");");
		}
		catch (ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}
	}
}

package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionMySQL {
	public static String status = "Not connected";
	
	public ConnectionMySQL(){
		
	}
	
	public static java.sql.Connection getConnectionMySQL() throws SQLException{
		Connection cn = null;
		try{
			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
			String serverName = "localhost";
			String mydatabase = "nml_topology_10000";
			String url = "jdbc:mysql://"+serverName+"/"+mydatabase;
			String userName = "root";
			String password = "";
			cn = DriverManager.getConnection(url, userName,password);
			
			if(cn != null){
				status =("STATUS ---> Connected with success!");
			}else{
				status = ("STATUS ---> Not connected");
			}
			return cn;
		}catch (ClassNotFoundException e){
			System.out.println("The stated driver was not found!");
			return null;
		}
	}
	
	public static String statusConnection(){
		return status;
	}
	
	public static boolean closeConnection(){
		try{
			ConnectionMySQL.getConnectionMySQL().close();
			return true;
		}catch(SQLException e){
			return false;
		}
	}
	
	public static java.sql.Connection resetConnection() throws SQLException{
		closeConnection();
		
		return ConnectionMySQL.getConnectionMySQL();
	}
}

package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.Connection;;

public class Topology {
	private static String NODES_PATH = "" ;
	private static String RELS_PATH = "";
	
	public static void main(String[] args) throws IOException, SQLException {
		HashMap<String, String> lista = new HashMap<String, String>();
		Connection cn = ConnectionMySQL.getConnectionMySQL();
		System.out.println(ConnectionMySQL.statusConnection());
 	    try {
 	    	   long tempoInicio = System.currentTimeMillis();
			   FileInputStream stream = new FileInputStream(NODES_PATH);
			   InputStreamReader reader = new InputStreamReader(stream);
			   BufferedReader br = new BufferedReader(reader);
			   String linha = br.readLine();
			   Integer i = 0;
			   
			   System.out.println("Inserting data...");
			   
			   while(linha != null) {
				   String id = linha.substring(0, linha.indexOf(" "));
				   String x = linha.substring(linha.indexOf(" ") + 1, linha.lastIndexOf(" "));
				   String y= linha.substring(linha.lastIndexOf(" ")+1, linha.length());
				   
				   String sql = "INSERT INTO node VALUES(?)";
				   
				   PreparedStatement preparedStmt = cn.prepareStatement(sql);
				   preparedStmt.setInt(1, Integer.parseInt(id));
				   preparedStmt.execute();
				   
				   sql = "INSERT INTO port(id_port, type, id_node) VALUES (?,?,?)";
				   
				   String portIn  = id+"_IN";
				   String portOut = id+"_OUT";
				   
				   preparedStmt = cn.prepareStatement(sql);
				   preparedStmt.setString(1, portIn);
				   preparedStmt.setString(2,"IN");
				   preparedStmt.setInt(3, Integer.parseInt(id));
				   preparedStmt.execute();
				   
				   sql = "INSERT INTO port(id_port, type, id_node) VALUES (?,?,?)";
				   
				   preparedStmt = cn.prepareStatement(sql);
				   preparedStmt.setString(1, portOut);
				   preparedStmt.setString(2,"OUT");
				   preparedStmt.setInt(3, Integer.parseInt(id));
				   preparedStmt.execute();
				   
				   lista.put(id, id);
				   linha = br.readLine();
			   }
			   
			   stream = new FileInputStream(RELS_PATH);
			   reader = new InputStreamReader(stream);
			   br = new BufferedReader(reader);
			   linha = br.readLine();
			   
			   int cont=0;
			   while(linha != null) {
				   String no1 = linha.substring(0, linha.indexOf(" "));
				   String no2 = linha.substring(linha.indexOf(" ") + 1, linha.length());
				  
				   String sql = "INSERT INTO link(id_link,id_port_in,id_port_out) VALUES(?,?,?)";
				   
				   PreparedStatement preparedStmt = cn.prepareStatement(sql);
				   preparedStmt.setString(1,no1+"_"+no2);
				   preparedStmt.setString(2,no2+"_IN");
				   preparedStmt.setString(3,no1+"_OUT");
				   preparedStmt.execute();
				   
				   preparedStmt = cn.prepareStatement(sql);
				   preparedStmt.setString(1,no2+"_"+no1);
				   preparedStmt.setString(2,no1+"_IN");
				   preparedStmt.setString(3,no2+"_OUT");
				   preparedStmt.execute();
				   
				   linha = br.readLine();
				}
			   
			    System.out.println("Insered data!");

 	    }finally{
 	    	
 	    	
 	    }
	}
}

package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Query {
	
	private static String FILE_PATH = "";
	private static Integer numberQueries = 1001;
	private static Integer limit = 10000;
	private static Connection cn;
	
	public Query(){
			
	}
	
	public static void delete() throws IOException{
		
		try{
			stablishConnection();
			Map<Integer, Integer> randomNodes = new HashMap<Integer, Integer>();
			Random generator = new Random();
			Integer node;
			String text = "delete Node Time \n";
			output(text);
			int i;
			for(i=0;i<limit;i++){
				do{
					node = generator.nextInt(limit)*3;
					if(randomNodes.isEmpty()){
						randomNodes.put(node, node);
					}
				}while(randomNodes.get(node)!= null && randomNodes.size()>1);
				randomNodes.put(node, node);
				long startTime = System.currentTimeMillis();	
				String query = "DELETE link FROM link LEFT JOIN port ON link.id_port_out = port.id_port OR link.id_port_in=port.id_port WHERE port.id_node=?";
				PreparedStatement stmt = cn.prepareStatement(query);
				stmt.setInt(1, node);
				stmt.execute();
				
				query = "DELETE port FROM port WHERE id_node=?";
				stmt = cn.prepareStatement(query);
				stmt.setInt(1, node);
				stmt.execute();
				
				query = "DELETE node FROM node WHERE id_node=?";
				stmt = cn.prepareStatement(query);
				stmt.setInt(1, node);
				stmt.execute();
				
				long endTime = System.currentTimeMillis()-startTime;
				System.out.println("Consulta realizada em "+ endTime+"ms");
				text = "delete " + node + " " + endTime + "\n" ;
				output(text);
				System.out.println("Result: "+text);
			}
			cn.close();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void stablishConnection() throws SQLException{
		cn = ConnectionMySQL.getConnectionMySQL();
		System.out.println(ConnectionMySQL.statusConnection());
	}
	
	public static void countInDegree() throws IOException{
		
		try {
			stablishConnection();
			Random generator = new Random();
			String text = "countInDegree Node Time \n";
			output(text);
			int i;
			for(i=0;i<numberQueries;i++){
				int id = generator.nextInt(limit)*3;
				long startTime = System.currentTimeMillis();	
				String query = "SELECT COUNT(link.id_link) as res FROM link INNER JOIN port ON link.id_port_in=port.id_port WHERE port.id_node = "+id+" and port.type = 'IN'";
				PreparedStatement stmt = cn.prepareStatement(query);
				//stmt.setInt(1,id);
			
				ResultSet rs = stmt.executeQuery(query);
				
				while (rs.next()){
					System.out.println("Result: "+ rs.getString("res"));
				}
				
				long endTime = System.currentTimeMillis()-startTime;
				System.out.println("Consulta realizada em "+ endTime+"ms");
				text = "countInDegree " + id + " " + endTime + "\n" ;
				output(text);
				
			}
			cn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public static void insert() throws IOException{
		try {
			   stablishConnection();
			   Random generator = new Random();
			   Map<Integer, Integer> randomNodes = new HashMap<Integer,Integer> ();
			   String text = "insert Node PortIn PortOut Time \n";
			   output(text);
			   int i;
			   
			   for(i=0;i<numberQueries;i++){
				    int id;
				    do{
				      id = (generator.nextInt(limit)+limit)*3; 
					  if(randomNodes.isEmpty()){
						randomNodes.put(id, id);
					  }
				    }while(randomNodes.get(id)!= null && randomNodes.size()>1);
				    randomNodes.put(id, id);
				   
				    
					long startTime = System.currentTimeMillis();
			   	    String sql = "INSERT INTO node VALUES(?)";
				   
				    PreparedStatement preparedStmt = cn.prepareStatement(sql);
				    preparedStmt.setInt(1, id);
				    preparedStmt.execute();
				   
				    sql = "INSERT INTO port(id_port, type, id_node) VALUES (?,?,?)";
				   
				    String portIn  = id+"_IN";
				    String portOut = id+"_OUT";
				   
				    preparedStmt = cn.prepareStatement(sql);
				    preparedStmt.setString(1, portIn);
				    preparedStmt.setString(2,"IN");
				    preparedStmt.setInt(3, id);
				    preparedStmt.execute();
				   
				   sql = "INSERT INTO port(id_port, type, id_node) VALUES (?,?,?)";
				   
				   preparedStmt = cn.prepareStatement(sql);
				   preparedStmt.setString(1, portOut);
				   preparedStmt.setString(2,"OUT");
				   preparedStmt.setInt(3, id);
				   preparedStmt.execute();
				   
				   long endTime = System.currentTimeMillis() - startTime;
				   text = "\ninsert "+id+" "+portIn+" "+portOut+" "+endTime;
				   output(text);
			   }
			   
			  } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				  
			}

		
	}
	
	public static void output(String text) throws IOException{
		FileOutputStream  arq = new FileOutputStream(FILE_PATH,true);
		arq.write(text.getBytes());
	}
	
	public static void main(String[] args) throws IOException, SQLException {
		//countInDegree();
		//delete();
		insert();
	}	
}

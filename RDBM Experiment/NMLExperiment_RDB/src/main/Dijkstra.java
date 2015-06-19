package main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;
import java.sql.PreparedStatement;


public class Dijkstra {
	private static String FILE_PATH = "/tmp/rmdb/large/--clear-teste.txt";
	private static Integer numberQueries = 1001;
	private static Integer limit = 10000;
	
	public static void computePaths(Vertex source){
		source.minDistance = 0.;
		
		//Visit each vertex u, always existing vertex
		//with smallest minDistance first
		PriorityQueue <Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.clear();
		
		vertexQueue.add(source);
		//System.out.println("Size: "+vertexQueue.size());
		while(!vertexQueue.isEmpty()){
			Vertex u = vertexQueue.poll();
			
			//System.out.println("-----"+ u.name+"-"+u.adjacencies);
			
			for(Edge e : u.adjacencies)
			{
				Vertex v = e.target;
				double weight = e.weight;
				//relax the edge (u,v)
				double distanceThroughU = u.minDistance + weight;
				if(distanceThroughU < v. minDistance){
					//remove v from queue
					vertexQueue.remove(v);
					
					v.minDistance = distanceThroughU;
					v.previous = u;
					
					//re-add v to queue
					vertexQueue.add(v);
				}
			}
			
		}
	}
	
	public static List<Vertex> getShortestPathTo(Vertex target){
		List <Vertex> path = new ArrayList<Vertex>();
		path.clear();
		for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
			path.add(vertex);
			//System.out.print(vertex.toString()+" ");
			System.out.println("loop");
		}
		//System.out.println("Sai loop");
		Collections.reverse(path);
		return path;
		
	}

	public static void main(String[] args) throws SQLException, IOException {
		//computeAPSP();
		computeSSSP();
	}
	
	public static void computeSSSP() throws SQLException, IOException{
		String text;
		Vertex[] vertices = initializeGraph(); 

		//Executing the queries
		Random generator = new Random();
		
		for(int i=0; i<numberQueries;i++){
			int source = generator.nextInt(limit);
			//Compute Shortest Path from a source to each node
			
			vertices = initializeGraph();
			long startTime = System.currentTimeMillis();
			computePaths(vertices[source]);
			long endTime = System.currentTimeMillis() - startTime;
			text = "computeSSSP " + source + " " + endTime + "\n";

			output(text);

			for( Vertex v : vertices){

				List<Vertex> path = getShortestPathTo(v);
				
			}
		
		}  		
	}
	
	public static void computeAPSP() throws SQLException, IOException{
		   /** 
		   * Compute All Shortest Path
		   */
		  String text;
		  Vertex[] vertices = initializeGraph(); 
		  text = "computeAPSP time \n";
		  output(text);
		  for(int i = 0; i<numberQueries;i++){
			  long startTime = System.currentTimeMillis();
			
			  for(int j = 0; j < limit; j++){
				  
				 	computePaths(vertices[j]);
				  
			  }
			  long endTime = System.currentTimeMillis() - startTime;
			  text = "computeAPSP " + " " + endTime + "\n";
			  output(text);
		  }		
    }	  

	
	public static void output(String text) throws IOException{
		FileOutputStream  arq = new FileOutputStream(FILE_PATH,true);
		arq.write(text.getBytes());
	}
		
	public static Vertex[] initializeGraph() throws SQLException, IOException{
		Connection cn = ConnectionMySQL.getConnectionMySQL();
		System.out.println(ConnectionMySQL.statusConnection());
		//LinkdeHashMap maintains a sorted map
		LinkedHashMap<Integer,Vertex> vs = new LinkedHashMap<Integer,Vertex>() ;
		
		long startTime = System.currentTimeMillis();
		
		String sql = "SELECT * FROM node";
		PreparedStatement stmt = cn.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			//Loading all nodes
	
			Integer id = rs.getInt("id_node");
			Vertex v = new Vertex(id.toString());
			vs.put(id, v);
		}		
		
		//rs.first()
		rs.beforeFirst();
		//rs = stmt.executeQuery();
		
		while(rs.next()){
			//Loading all neighbors of each node
			Integer id = rs.getInt("id_node");
			
			sql = "SELECT link.cost, port.id_node FROM link INNER JOIN port ON link.id_port_in = port.id_port "
					+ "WHERE link.id_port_out = '"+id.toString()+"_OUT'";
			PreparedStatement stmt2 = cn.prepareStatement(sql);
			ResultSet rs2 = stmt2.executeQuery();
			
			
			HashMap<Integer, Edge> neighbors = new HashMap<Integer, Edge>();
			
			Vertex v = new Vertex(id.toString());
			
			while(rs2.next()){
				Integer idNeighbor = rs2.getInt("id_node");
				Double cost = (double) rs2.getInt("cost");
				//Default
				cost = 1.0;
				Edge neighbor = new Edge (vs.get(idNeighbor),cost);
				neighbors.put(idNeighbor, neighbor);
			}
		
			//Transforming in an Array of Edge
			Collection<Edge> values = neighbors.values();
			vs.get(id).adjacencies = (Edge[]) values.toArray(new Edge[values.size()]);
			neighbors.clear();
			
		}
		String text = "Load Graph: "+ (System.currentTimeMillis() - startTime)+" ";
		System.out.println(text);
		
		output(text);
		
		Collection <Vertex> valuesV = vs.values();
		
		
		Vertex[] vertices = (Vertex[]) valuesV.toArray(new Vertex[valuesV.size()]);
		
		vs.clear();
		cn.close();
		return vertices;

	}
	
}

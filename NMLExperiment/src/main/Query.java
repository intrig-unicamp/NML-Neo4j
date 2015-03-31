package main;


import java.io.FileOutputStream;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;
import java.util.Random;


import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;


import org.neo4j.graphdb.Transaction;


public class Query {
		private static String FILE_PATH = "/tmp/output.txt";
		private static Integer numberQueries = 1;
		private static Integer limit = 100; 
		
		public void executeCypher(int queryNumber, GraphDatabaseService db) throws IOException{
			String cypher = "";
			String primitive = "";
			
			FileOutputStream  arq = new FileOutputStream(FILE_PATH,true);
			
			switch(queryNumber){	
				case 3: cypher = "MATCH (n:Node)<-[:hasInboundPort]-(p:Port)<-[isSink]-(l:Link) WHERE n.name={nameNode} RETURN COUNT(l) AS CountOutDegree";
						primitive = "countInDegree";
						arq.write("\ncountInDegree Node Time\n".getBytes());
				break;
				case 4: cypher = "MATCH (n:Node)-[:hasOutboundPort]->(p:Port)-[isSource]->(l:Link) WHERE n.name={nameNode} RETURN COUNT(l) AS CountOutDegree";
						primitive = "countOutDegree";
						arq.write("\ncountOutDegree Node Time\n".getBytes());
				break;
				case 5: cypher = "MATCH (n:Node)-[]-(p:Port)-[]-(l:Link)-[]-(p1:Port)-[]-(n2:Node) WHERE n.name={nameNode} RETURN DISTINCT(n2) AS Neighbors";
						primitive = "countNeighbors";
						arq.write("\ncountNeighbors Node Time\n".getBytes());
				break;
				case 6:	cypher = "MATCH p=shortestPath((n:Node)-[*]-(m:Node)) WHERE n.name = {nameNode} AND m.name={nameNode1} RETURN COUNT(p) >0 AS DoesRouteExist";
						primitive = "doesRouteExist";
						arq.write("\ndoesRouteExist Source Destination Time\n".getBytes());
				break;
				case 7: cypher = "MATCH (n:Node),(m:Node), p=shortestPath((n)-[*]->(m)) WHERE n.name={nameNode} RETURN p";
						primitive = "computeSSSP";
						arq.write("\ncomputeSSSP Source Time\n".getBytes());
				break;
				case 8: cypher = "MATCH p=shortestPath((n:Node)-[*]->(m:Node)) RETURN p";
						primitive = "computeAPSP";
						arq.write("\ncomputeAPSP Time\n".getBytes());
				break;
				case 9: cypher = "MATCH (n:Node),(m:Node), p=allShortestPaths((n)-[*]-(m)) WHERE n.name={nameNode} AND m.name={nameNode1} RETURN p LIMIT {valueK}";
						primitive = "computeKSSSP";
						arq.write("\ncomputeKSSSP Source Destination Time\n".getBytes());
				break;
				case 10: cypher = "MATCH (n:Node)-[r1]-(p:Port)-[r2]-(l:Link)-[r3]-(p2:Port) WHERE n.name={nameNode} DELETE n,r1,p,r2,l,r3";
						primitive = "delete";
						arq.write("\ndelete Node Time\n".getBytes());
				break;
				case 11: cypher = "MATCH p=shortestPath((n:Node)-[*]->(m:Node)) WHERE n.name = {nameNode} RETURN p AS MinimumSpanningTree";
						primitive = "computeMST";
						arq.write("\ncomputeMST Source Time\n".getBytes());
				break;
				default: cypher = "";			
			}
			
		s	Random gerador = new Random();
			
			long startTime=0;
			Map<String, Object> params = new HashMap<String, Object>();
			Map<Integer, Integer> randomNodes = new HashMap<Integer,Integer> ();
			
			for(int i=0;i<numberQueries;i++){
				params.clear();
				Integer node1, node2=0;
				int k = gerador.nextInt(10);
				
				do{
					node1 = gerador.nextInt(limit)*3;
					if(randomNodes.isEmpty()){
						randomNodes.put(node1, node1);
					}
				}while(randomNodes.get(node1)!= null && randomNodes.size()>1);
				
				randomNodes.put(node1, node1);
				
				if(queryNumber == 6 || queryNumber ==9){
					do{
						node2 = gerador.nextInt(limit)*3;	
					}while(node1 == node2);		
				}
				String link = node1 + "_" + node2;
									
				params.put("nameNode", node1.toString());
				params.put("nameNode1", node2.toString());
				params.put("nameLink", link);
				params.put("valueK", k);
								
				Transaction tx = db.beginTx();
				ExecutionEngine ex = new ExecutionEngine(db);
				
				startTime = System.currentTimeMillis();
				ExecutionResult result = ex.execute( cypher, params );
		
				long endTime = System.currentTimeMillis()-startTime;
				String texto="";
				switch(queryNumber){
					case 3: 
						texto = primitive + " " + node1 + " " + endTime + "\n";
					break;
					case 4:
						texto = primitive + " " + node1 + " " + endTime + "\n";
					break;		
					case 5:
						texto = primitive + " " + node1 +  " " + endTime + "\n";
					break;
					case 6:
						texto = primitive + " " + node1 + " " + node2 + " " + endTime + "\n";
					break;
					case 7:
						texto = primitive + " " + node1 + " " + endTime + "\n";
					break;
					case 8:
						texto = primitive + " " + endTime + "\n";
					break;
					case 9:
						texto = primitive + " " + node1 + " " + node2 + " " + endTime + "\n";
					break;
					case 10:
						texto = primitive + " " + node1 + " " + endTime + "\n";
					break;
					case 11:
						texto = primitive + " " + node1 + " " + endTime + "\n";
					break;
				}

				arq.write(texto.getBytes());
				tx.success();
			}
			
				
			System.out.println("Consultas de "+primitive+" realizadas. Arquivo Gerado");
			arq.close();
			
		}
		
		public void foundPathsCypher(GraphDatabaseService db ) throws IOException{
			FileOutputStream  arq = new FileOutputStream(FILE_PATH,true);
			
			String text = "\n\ncomputeAPSP Time";
			arq.write(text.getBytes());
			
			String cypher = "MATCH p=shortestPath((n:Node)-[*]-(m:Node)) RETURN p";
			Transaction tx = db.beginTx();
			for(int i=0; i < numberQueries; i++){
					
					ExecutionEngine ex = new ExecutionEngine(db);
			
					long startTime = System.currentTimeMillis();
					ExecutionResult result = ex.execute( cypher);
					long t1 = System.currentTimeMillis()-startTime;
					text = "\ncomputeAPSP "+t1;
					arq.write(text.getBytes());
			}

			tx.success();
			arq.close();
		}
		
		public void edgeWeight(int op, GraphDatabaseService db) throws IOException{
			FileOutputStream  arq = new FileOutputStream(FILE_PATH,true);
			String cypher1 = "MATCH (n:Node)-[]-(p:Port)-[]-(l:Link)-[]-(p1:Port)-[]-(n2:Node) WHERE n.name={nameNode} RETURN DISTINCT(n2) AS NEIGHBORS";
			
			String cypher2 = "";	
			if(op == 1){
				cypher2 = "MATCH (n:Node)-[:hasOutboundPort]-()-[r]-(l:Link) WHERE n.name={nameNode} AND l.name={nameLink} SET l.cost={valueCost}";
				arq.write("\nsetEdgeWeight Node1 Link Cost Time".getBytes());
			}else if(op == 2){
				cypher2 = "MATCH (n:Node)-[:hasOutboundPort]-()-[r]-(l:Link) WHERE n.name={nameNode} AND l.name={nameLink} RETURN l.cost";
				arq.write("\ngetEdgeWeight Node1 Link Time".getBytes());
			}
			
			Map<Integer, Integer> randomNodes = new HashMap<Integer, Integer>();		
			Map<String, String> neighbors = new HashMap<String,String>();
			Map<String, Object> params = new HashMap<String, Object>();
			
			Random gerador = new Random();
			Transaction tx = db.beginTx();
			ExecutionEngine ex;
			ExecutionResult result;
			Integer node1;
			Integer node2;
			
			for(int i=0; i<numberQueries; i++){
				do{
					neighbors.clear();
					do{
						node1 = gerador.nextInt(limit)*3;
						if(randomNodes.isEmpty()){
							randomNodes.put(node1, node1);
						}
					}while(randomNodes.get(node1)!= null && randomNodes.size()>1);
					
					randomNodes.put(node1, node1);
					params.put("nameNode", node1.toString());
					
					ex = new ExecutionEngine( db );
					result = ex.execute( cypher1, params );
					Iterator<Node> resultNeighbors = result.columnAs("NEIGHBORS");
				
					while(resultNeighbors.hasNext()){
						Node neighbor = resultNeighbors.next();
						neighbors.put(neighbor.getProperty("name").toString(), neighbor.getProperty("name").toString());
					}
				}while(neighbors.size()<1);
						
				do{
					node2 = gerador.nextInt(limit)*3;
				}while(neighbors.get(node2.toString())==null);
			
				
				Integer cost = gerador.nextInt(10) ;
				String link=node1.toString()+"_"+node2.toString();
				params.put("nameLink", link);
				params.put("valueCost", cost);
				
				long startTime = System.currentTimeMillis();
				ex.execute(cypher2, params);
				long endTime =System.currentTimeMillis()-startTime; 
				String text="";
				if (op == 1){
					text ="\nsetEdgeWeight "+node1.toString()+" "+link+" "+cost.toString()+" "+endTime;
				}else if(op ==2 ){
					text ="\ngetEdgeWeight "+node1.toString()+" "+link+" "+endTime;
				}
				arq.write(text.getBytes());
			}
			arq.close();
			tx.success();
		}
		
		public void insert(GraphDatabaseService db) throws IOException{
			FileOutputStream  arq = new FileOutputStream(FILE_PATH,true);
			Map<Integer, Integer> randomNodes = new HashMap<Integer,Integer>();
			Map<String, Object> params = new HashMap<String, Object>();
			Random gerador = new Random();
			Transaction tx = db.beginTx();
			ExecutionEngine ex;
			ExecutionResult result;
			Integer newNode;
			
			arq.write("\ninsert Node PortIn PortOut Time".getBytes());
			
			for(int i=0; i < numberQueries; i++){
				do{
					newNode = (gerador.nextInt(limit)+limit)*3;
					if(randomNodes.isEmpty()){
						randomNodes.put(newNode, newNode);
					}
				}while(randomNodes.get(newNode.toString()) != null && randomNodes.size()>1);
				
				randomNodes.put(newNode, newNode);
				
				String portInNewNode = newNode.toString()+"_in";
				String portOutNewNode = newNode.toString()+"_out";
				
				long startTime = System.currentTimeMillis(); 
				
					params.put("nameNewNode", newNode.toString());
				params.put("portInNewNode", portInNewNode);
				params.put("portOutNewNode", portOutNewNode);
			
				String cypher = "CREATE (n1:Node{name:\""+newNode.toString()+"\"}) CREATE (n2:Port{name:\""+portInNewNode.toString()+"\"}) "
						+ "CREATE (n3:Port{name:\""+portOutNewNode.toString()+"\"}) WITH n1, n2, n3 "
						+ "CREATE (n1)<-[r:hasInboundPort]-(n2) CREATE (n1)-[r2:hasOutboundPort]->(n3) RETURN r,r2";
				
				ex = new ExecutionEngine( db );
				ex.execute(cypher);
				long endTime = System.currentTimeMillis() - startTime;
				String text = "\ninsert "+newNode+" "+portInNewNode+" "+portOutNewNode+" "+endTime;
				
				arq.write(text.getBytes());
				
				System.out.println("No "+newNode+" inserido ");
				
				
			}
			
			arq.close();
			
			tx.success();
		}

}

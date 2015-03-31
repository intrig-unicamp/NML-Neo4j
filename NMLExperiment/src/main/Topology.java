package main;

import static main.GraphUtil.registerShutdownHook;
import main.Query;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.DynamicLabel;


public class Topology {
	private static String DB_PATH = "/NmlTopologia100";
	private static String NODES_PATH = "/10nodes.txt" ;
	private static String RELS_PATH = "/connections10.txt" ;
	public enum RelTypes implements RelationshipType {
		hasInboundPort,
		hasOutboundPort,
		isSink,
		isSource
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphDatabaseService graphDb =new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH ); 
		registerShutdownHook(graphDb);
		
		//Index<Node> nodeIndex = graphDb.index().forNodes("nodes");		
		//Index<Node> portIndex = graphDb.index().forNodes("ports");
		//Index<Node> linkIndex = graphDb.index().forNodes("links");
		Transaction tx = graphDb.beginTx();
		HashMap<String, Node> lista = new HashMap<String, Node>();

 	    try {
			   
 	    	   long tempoInicio = System.currentTimeMillis();
			   FileInputStream stream = new FileInputStream(NODES_PATH);
			   InputStreamReader reader = new InputStreamReader(stream);
			   BufferedReader br = new BufferedReader(reader);
			   String linha = br.readLine();
			   Integer i = 0;
			   
			   while(linha != null) {
				   String id = linha.substring(0, linha.indexOf(" "));
				   String x = linha.substring(linha.indexOf(" ") + 1, linha.lastIndexOf(" "));
				   String y= linha.substring(linha.lastIndexOf(" ")+1, linha.length());
				   				   
				   Node noCriar = graphDb.createNode(DynamicLabel.label("Node"));			   
				   noCriar.setProperty("name", id);
				   lista.put(id, noCriar);
			  		
				   //Criando as portas do nó
				   Node port = graphDb.createNode(DynamicLabel.label("Port"));
				   
				   i = (Integer.parseInt(id)+1);
				   
				   
				   port.setProperty("name", id.toString()+"_in");
				   lista.put(i.toString(), port);
				   
				     
				   //Colocando a direção
				   Relationship rel = port.createRelationshipTo(noCriar, RelTypes.hasInboundPort);
				   
				   port = graphDb.createNode(DynamicLabel.label("Port"));
				   i+=1;
				   //System.out.println("-ID: "+ id+"I: "+i);
				   port.setProperty("name", id.toString()+"_out");
				   lista.put(i.toString(), port);
				   
				   rel = noCriar.createRelationshipTo(port, RelTypes.hasOutboundPort);
				   
				   linha = br.readLine();    
			   }
			   
			   br.close();
			   
			   System.out.println("\nTopologia Criada em "+(System.currentTimeMillis()-tempoInicio));
			   
			   tempoInicio = System.currentTimeMillis();
			   stream = new FileInputStream(RELS_PATH);
			   reader = new InputStreamReader(stream);
			   br = new BufferedReader(reader);
			   linha = br.readLine();
			   
			   int cont=0;
			   while(linha != null) {
				   String no1 = linha.substring(0, linha.indexOf(" "));
				   String no2 = linha.substring(linha.indexOf(" ") + 1, linha.length());
				   
				   //Continuando os índices
				   i++;
				   //System.out.println("No1: "+no1+" - No2: "+no2+"Porta out no1:"+(Integer.parseInt(no1)+2)+"-Porta in no2: "+(Integer.parseInt(no2)+1));
				   
				   Node link = graphDb.createNode(DynamicLabel.label("Link"));
				   link.setProperty("name", no1+"_"+no2);
					   
				   
				   lista.put(i.toString(), link);
									   
				   Integer p = (Integer.parseInt(no1)+2);
				   Relationship rel = lista.get(p.toString()).createRelationshipTo(link, RelTypes.isSource);
				   
				   p = (Integer.parseInt(no2)+1);
				   cont++;
				   				   
				   //Colocando a direção 
				   rel = link.createRelationshipTo(lista.get(p.toString()), RelTypes.isSink);
				   
				   i++;
				   
				   link = graphDb.createNode(DynamicLabel.label("Link"));
				   link.setProperty("name", no2+"_"+no1);
				   
				   
				   lista.put(i.toString(), link);
				   
				   p = Integer.parseInt(no2)+2;
				   rel = lista.get(p.toString()).createRelationshipTo(link, RelTypes.isSource);
				   
				   p = Integer.parseInt(no1)+1;
				   
				   rel = link.createRelationshipTo(lista.get(p.toString()), RelTypes.isSink);
				   
				   linha = br.readLine();
			   }
			   
			   br.close();
			   
			   tx.success();
			   
			   System.out.println("\nRelacionamentos Criados em "+(System.currentTimeMillis()-tempoInicio));
			   
			   Query objQuery = new Query();
			  
			   objQuery.edgeWeight(1,graphDb);
			   objQuery.edgeWeight(2,graphDb);
			   objQuery.executeCypher(3, graphDb);
			   objQuery.executeCypher(4, graphDb);
			   objQuery.insert(graphDb);
			   objQuery.executeCypher(5, graphDb);
			   objQuery.foundPathsCypher(graphDb);
			   objQuery.executeCypher(6, graphDb);
			   objQuery.executeCypher(7, graphDb);
			   objQuery.executeCypher(9, graphDb);
			   objQuery.executeCypher(11, graphDb);
			   objQuery.executeCypher(10, graphDb);
			   objQuery.executeCypher(8, graphDb);
		    } catch (IOException e) {
		    	
		    }finally{
		    	tx.finish();
		    }
		
	}	  
}	

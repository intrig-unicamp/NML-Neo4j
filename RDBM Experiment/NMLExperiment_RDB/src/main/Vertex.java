package main;

import java.util.ArrayList;

public class Vertex implements Comparable<Vertex>{
	 public final String name;
	 public Edge[] adjacencies;
	 public double minDistance = Double.POSITIVE_INFINITY;
	 public Vertex previous;
	 public Vertex(String argName) { name = argName; }
	 public String toString() { return name; }
	 
	 public int compareTo(Vertex other) {
		// TODO Auto-generated method stub
		return Double.compare(minDistance, other.minDistance);
	}
}

package algorithms.PPDM.sGA2DT;

import java.util.Set;
import java.util.TreeSet;

public class Node {
	public double fitness;
	public Set<Integer> Squen;//=new TreeSet<Integer>();
	public Node(){
		this.fitness=10;
		this.Squen=new TreeSet<Integer>();
	}
}

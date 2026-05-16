package algorithms.PPDM.pGA2DT;

import java.util.Set;
import java.util.TreeSet;

 public class pgaNode {
	public double fitness;
	public Set<Integer> Squen;//=new TreeSet<Integer>();
	public pgaNode(){
		this.fitness=10;
		this.Squen=new TreeSet<Integer>();
	}
}

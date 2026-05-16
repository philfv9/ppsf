package algorithms.PPDM.cpGA2DT;

import java.util.Set;
import java.util.TreeSet;

public class PNode {
	public double indice;
	public Set<Integer> sequen;
	public PNode(){
		this.indice=100;
		this.sequen=new TreeSet<Integer>();
	}

}
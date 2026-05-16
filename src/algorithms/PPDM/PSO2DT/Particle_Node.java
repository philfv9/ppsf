package algorithms.PPDM.PSO2DT;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Particle_Node {
	List<Integer> psetint=new ArrayList<Integer>();
	List<Integer> vsetint=new ArrayList<Integer>();
	List<Integer> pbest=new ArrayList<Integer>();
	double fitness;
	double pbfit;
	int pitem,item;
	int dbsize;
	int psize;
	
	public Particle_Node(){
		fitness=Double.MAX_VALUE;
		pbfit=Double.MAX_VALUE;
		pitem=Integer.MIN_VALUE;
		item=Integer.MIN_VALUE;
		dbsize=Integer.MIN_VALUE;
		psize=Integer.MIN_VALUE;
	}
}

package algorithms.PPDM.sGA2DT;

import java.util.Set;
import java.util.TreeSet;


public class DBNode {
	public int indice;
	public Set<String> Listr;
	public DBNode(){
		this.indice=-1;
		this.Listr=new TreeSet<String>();
	}

}

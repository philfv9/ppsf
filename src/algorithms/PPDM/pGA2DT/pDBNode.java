package algorithms.PPDM.pGA2DT;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

 public class pDBNode {
	public int indice;
	public Set<String> Listr;
	public pDBNode(){
		this.indice=-1;
		this.Listr=new TreeSet<String>();
	}
}

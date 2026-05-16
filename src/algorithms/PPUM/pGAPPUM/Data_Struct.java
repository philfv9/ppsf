package algorithms.PPUM.pGAPPUM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data_Struct {
	int Tid;
	List<Integer> transaction=new ArrayList<Integer>(); //Each transaction in the database
	Map<Integer,int[]> item_uti=new HashMap<Integer,int[]>(); //The corresponding utility for each item in transaction
	double tu=0; //The entire transaction utility
	double ratio=0;
	
	public int getTid() {
		return Tid;
	}
	public void setTid(int tid) {
		Tid = tid;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	public double getTu() {
		return tu;
	}
	public void setTu(double tu) {
		this.tu = tu;
	}
}

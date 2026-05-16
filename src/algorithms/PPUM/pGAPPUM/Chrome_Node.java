package algorithms.PPUM.pGAPPUM;

import java.util.ArrayList;
import java.util.List;

public class Chrome_Node {
	List<Integer> chromesome=new ArrayList<Integer>();
	double Tuid=0;
	double side_effect;
	
	public Chrome_Node() {
		Tuid=0;
		this.side_effect=Double.MAX_VALUE;
	}
	public List<Integer> getChromesome() {
		return chromesome;
	}
	public void setChromesome(List<Integer> chromesome) {
		this.chromesome = chromesome;
	}
	public double getTuid() {
		return Tuid;
	}
	public void setTuid(double tuid) {
		Tuid = tuid;
	}
	public double getSide_effect() {
		return side_effect;
	}
	public void setSide_effect(double side_effect) {
		this.side_effect = side_effect;
	}
	
}

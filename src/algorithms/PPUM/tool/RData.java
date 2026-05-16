package algorithms.PPUM.tool;

import java.util.ArrayList;
import java.util.List;

public class RData {

	List<String> dus = new ArrayList<String>();
	List<String> ius = new ArrayList<String>();
	List<String> dss =new ArrayList<String>();
	List<String> modifiedTrans = new ArrayList<String>();
	List<String> modifiedNums = new ArrayList<String>();
	List<String> time = new ArrayList<String>();
	List<String> mc = new ArrayList<String>();
	List<String> lost = new ArrayList<String>();
	

	public int getSize() {
		return mc.size();
	}
	
	public void clean() {
		dus.clear();
		ius.clear();
		dss.clear();
		modifiedNums.clear();
		modifiedTrans.clear();
		mc.clear();
		time.clear();
		lost.clear();
		
	}

	public void print(List<String> list, String name) {
		String dus_str = list.toString();
		dus_str = dus_str.replaceAll(",", "\t");
		dus_str = dus_str.substring(1,dus_str.length()-1);
		System.out.println(name);
		System.out.println("HHUIF\tMSICF\tMSU-MAU\tMSU-MIU\tMSU-MSI");
		String[] items = dus_str.split("\t");
	//	System.out.println(items.length);
		for(int i = 1; i <= items.length; ++i) {
			System.out.print(items[i-1]+"\t");
			if(i % 5 == 0 ) {
				System.out.println();
			}
		}
	 	System.out.println();
	}
	
	public void print() {
		print(dus,"DUS");
		print(ius, "IUS");
		print(dss, "DSS");
		print(modifiedNums, "ModfNUMs");
		print(modifiedTrans, "ModiTrans");
		print(time, "Time");
		print(mc, "MC");
		print(lost, "LOST");
		
		
	}

	public void addLine() {
		 
		
	}
}

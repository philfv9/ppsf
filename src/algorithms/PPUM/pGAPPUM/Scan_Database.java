package algorithms.PPUM.pGAPPUM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//import test.Data_Struct;

public class Scan_Database {
	List<Data_Struct> Origin_DB=new ArrayList<Data_Struct>();
	Map<String, Integer> profits=new HashMap<String,Integer>();
	List<Set<Integer>> Sensitive_DB=new ArrayList<Set<Integer>>();
	int Total_Utility=0;
	double upper_min,low_min;
	/**
	 * @ Reading the original database
	 */
	public void Read_Database(String address){
		try{
			FileReader ori_data=new FileReader(address); 
			BufferedReader buf=new BufferedReader(ori_data);
			String line;
			while((line=buf.readLine())!=null){
				if(line.trim().length()>0){
					String str[]=line.split(":");
					Data_Struct ds=new Data_Struct();
					String items[]=str[0].split(" ");
					String utility[]=str[2].split(" ");
					for(int i=0;i<items.length;i++){
						Integer y=Integer.parseInt(items[i]);
						ds.transaction.add(y);
						int x[]=new int[2];
						x[0]=Integer.parseInt(utility[i]);
						ds.item_uti.put(y, x);//x[0] is the utility for each itemset, and x[1] is the TWU
					}
					ds.tu=Integer.parseInt(str[1]);
					Total_Utility+=ds.tu;
					//System.out.println(ds.tu);
					Origin_DB.add(ds);	
				}//end if
			}//end while
		}
		catch(IOException ex) {
	    	System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
	        }
	}
	/*public void Read_Profit(){
		try{
			FileReader pro_data=new FileReader("../pPPUM/dataset/database/mushroom_UM_UtilityTable.txt"); //profit of each item  D:\\new database\\DB_UtilityTable.txt
			BufferedReader buf=new BufferedReader(pro_data);
			String line;
			//¶Áprofits
			while((line=buf.readLine())!=null){
				if(line.trim().length()>0){
					String[] str=line.split(", ");
					int util=Integer.parseInt(str[1]);
					profits.put(str[0],util);
				}//end if
			}//end while
		}
		catch(IOException ex){
			System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
		}
	}*/
	public void Read_Sensitive(String address){
		try{
			FileReader sensitive=new FileReader(address);
			BufferedReader buf=new BufferedReader(sensitive);
			String line;
			Set<Integer> ls;
			while((line=buf.readLine())!=null){
				if(line.trim().length()>0){
					String[] str=line.split(",");
					ls=new TreeSet<Integer>();
					for(String str1:str){
						Integer item=Integer.parseInt(str1);
						ls.add(item);
					}
					Sensitive_DB.add(ls);
				}
			}
		}
		catch(IOException ex){
			System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
		}
	}
	
	/**
	 * @ method Calculate the itemset utility value
	 * @param lds Original database
	 * @param str
	 * @return
	 */
	public int[] Calculate_Utility(List<Data_Struct> lds,Set<Integer> si){
		int itemset_utility[]=new int[2];
		for(Data_Struct ds:lds){
			
			if(ds.transaction.containsAll(si)){
				//next.add(ds);
				//System.out.print(true);
				for(Integer a:si){
					itemset_utility[0]+=ds.item_uti.get(a)[0];
				}
				itemset_utility[1]+=ds.tu;
				
			}
		}
		/*if(itemset_utility[0]>=TUD*upper_min)
			System.out.println(str+"++"+itemset_utility[0]+"**"+itemset_utility[1]);*/
		return itemset_utility;
	}
	

}

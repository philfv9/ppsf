package algorithms.PPDM.Greedy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AlgoGreedy extends Apriori{
	
	/** the current level k in the breadth-first search */
	protected int k; 

	/** total number of candidates */
	protected int totalCandidateCount = 0; 
	
	/** number of candidate generated during last execution */
	protected long startTimestamp; //
	
	/**  start time of last execution */
	protected long endTimestamp; //
	
	/**  end time of last execution */
	private int itemsetCount;  // 
	
	/** itemset found during last execution */
	private int databaseSize;
	
	/** the minimum support set by the user */
	private int minsupRelative;
	
	/** A memory representation of the database. 
	 * Each position in the list represents a transaction */
	private List<int[]> database = null;
	
	/**The  patterns that are found 
	 *  (if the user want to keep them into memory)
	 */
//	protected Itemsetsvoid patterns = null;

	/** object to write the output file (if the user wants to write to a file) */
	BufferedWriter writer = null;

	/** maximum pattern length */
	private int maxPatternLength = 10000; 
	
	/**
	 * Default constructor
	 */
	public AlgoGreedy() {
		
	}
	
	private static double w1=0.98,w2=0.01,w3=0.01;//w=0.9,r1,r2;
	private int Dnum=0;
	int m;
	private int Total_Item=0;
	private List<Set<String>> HSI=new ArrayList<Set<String>>();
	private Map<Set<String>,Integer> hs=new HashMap<Set<String>,Integer>();
	private double fitnesses;
	public int getM() {
		return m;
	}
	public void setM(int m) {
		this.m = m;
	}
	public void ReadHSI(File file) throws IOException{
		try{
			
			FileReader hsi=new FileReader(file);
			BufferedReader br2=new BufferedReader(hsi);
			String line;
			while ((line=br2.readLine())!=null){
				if(line.trim().length()>0){
					String str[]=line.split(",");
					Set<String> ss=new TreeSet<String>();
					int value=Count_Support(Original_DB,str);
					for(String str2:str){
						ss.add(str2);
					}
					hs.put(ss,value);
					Set<String> ss2=new TreeSet<String>();
					ss2.add(line);
					HSI.add(ss2);
				}
			}
		}
		catch(IOException ex) {
	    	System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
	        }
	}
	public int Count_Item(List<Set<String>> db){
		int count=0;
		for(Set<String> ss:db)
			count+=ss.size();
		return count;
	}
	
	/**
	 *@throws IOException 
	 * @method Init
	 */
	public void Init() throws IOException{
		//==============Find all STIDs that contain sensitive items=================
		int max=0;
		Collection<Integer> values=hs.values();
		for(Integer in:values){
			if(in>max)
				max=in;
		}
		//================If ATID is greater than max, only delete transactions in ATID==============
		double num=(max-upper_sup*Original_DB.size())/(1.0-upper_sup);
		int dnum=((int) (num+1))+1;
		Dnum=m*dnum;
		min_sup=upper_sup*(1-((double)Dnum)/Original_DB.size());
//		System.out.println(max+"\t*********** :\t"+Dnum+"\t"+min_sup);
//		writer.write(max+"\t*********** :\t"+Dnum+"\t"+min_sup + "\n");
	}
	
	/**
	 * @method Delete TID contained in C
	 * @param c
	 * @param db
	 * @return
	 */
	public List<Set<String>> Delete(List<Integer> c,List<Set<String>> db){
		List<Set<String>> lss=new ArrayList<Set<String>>();
		Set<Integer> set1=new TreeSet<Integer>();
		for(Integer inte:c)
			set1.add(inte-1);
		for(int i=0;i<db.size();i++){
			if(!set1.contains(i))
				lss.add(db.get(i));
		}
		return lss;
	}
	
	/**
	 * @ Calculate Hiding Failure
	 * @param lss1
	 * @param lss2
	 * @return
	 */
	public List<Set<String>> Intersect(List<Set<String>> lss1,List<Set<String>> lss2){
		List<Set<String>> list = new ArrayList(Arrays.asList(new Object[lss1.size()]));   
        Collections.copy(list, lss1);   
        list.retainAll(lss2);
        return list;
		
	}
	/**
	 * @method Calculate
	 * @param ss1
	 * @param ss2
	 * @return
	 */
	public List<Set<String>> Diff(List<Set<String>> lss1,List<Set<String>> lss2){
		List<Set<String>> list = new ArrayList(Arrays.asList(new Object[lss1.size()])); //<Set<String>>();
		Collections.copy(list,lss1);
        list.removeAll(lss2);
        return list;
	}
	/**
	 * @method Fitness method
	 * @param List of transactions to be deleted
	 * @throws IOException 
	 */
	public void Fitness(List<Set<String>> fs,List<Set<String>> pls, double sup, int number) throws IOException{//,List<Set<String>> ){//,List<PNode> p1){
		int a=0, b=0, c=0;
		List<Set<String>> copyDB=new ArrayList<Set<String>>();
		List<Set<String>> afterlager=new ArrayList<Set<String>>();
		List<Set<String>> artificial=new ArrayList<Set<String>>();
		copyDB.addAll(Original_DB);
		int de_number=0;
		for(Set<String> tran:Original_DB){
			int count=0;
			for(Set<String> sen:hs.keySet()){
				if(tran.containsAll(sen)){
					count++;
					hs.put(sen, hs.get(sen)-1);
				}
			}
			if(count!=0&&de_number<number){
				de_number++;
				copyDB.remove(tran);
			}
		}
		/*List<Set<String>> hsi=new ArrayList<Set<String>>();
		hsi.addAll(hs.keySet());*/
		/*for(Set<String> sstr:hs.keySet()){
			if(hs.get(sstr)>=copyDB.size()*sup){
				a++;
				//hsi.add(sstr);
			}
		}*/
		
		for(Set<String> ls:fs){
			Set<String> ss1=new TreeSet<String>();
			for(String str:ls){
				String str1[]=str.split(",");
				if(Count_Support(copyDB,str1)>=copyDB.size()*sup)
					ss1.add(str);
			}
			if(!ss1.isEmpty())
				afterlager.add(ss1);
			ss1=null;
		}
		for(Set<String> ls:pls){
			Set<String> ss2=new TreeSet<String>();
			for(String str:ls){
				String str2[]=str.split(",");
				if(Count_Support(copyDB,str2)>=copyDB.size()*upper_sup)
					ss2.add(str);
			}
			if(!ss2.isEmpty())
				artificial.add(ss2);
			ss2=null;
		}
		
		//System.out.println(HSI+"\n"+afterlager+"\n"+artificial);
		//System.out.println(ap1.FrequencySet);
		List<Set<String>> HF=new ArrayList<Set<String>>();
		HF=Intersect(HSI,afterlager);
		a=HF.size();
		List<Set<String>> NTH=new ArrayList<Set<String>>();
		NTH=Diff(Diff(fs,HSI),afterlager);
		b=NTH.size();
		c=artificial.size();
//		System.out.println(HF+"\n"+NTH+"\n"+artificial);
		fitnesses=(w1*a+w2*b+w3*c);
//        System.out.println(a+","+b+","+c + "   " + fitnesses);
		writer.write("F-T-H: " + a + "\tN-T-H: " + b + "\tN-T-G: " + c + "\t\tFitness: " + fitnesses);
		writer.newLine();
		Total_Item=Count_Item(copyDB);
	}
	
	public void test(double sup) throws IOException{
//		System.out.println("================================");
		writer.write("================================");
		writer.newLine();
		Init();
		long startTime = System.currentTimeMillis();
		Mining(Original_DB);
		long endTime = System.currentTimeMillis();
//		System.out.println("the time of Ming is : "+(endTime-startTime)/1000+"s");
		writer.write("the time of Ming is : "+(endTime-startTime)/1000+"s");
		writer.newLine();
		List<Set<String>> FS=new ArrayList<Set<String>>();
		for(Set<String> ssa:FrequencySet)
			if(!ssa.isEmpty())
				for(String str1:ssa){
					Set<String> ssb=new TreeSet<String>();
					ssb.add(str1);
					FS.add(ssb);
					ssb=null;
				}
//		System.out.println("**************");
		writer.write("********************************");
		writer.newLine();
		long beginTime = System.currentTimeMillis();
		Fitness(FS,PreLargeSet,sup,Dnum);
		long overTime = System.currentTimeMillis();
//		System.out.println("the time of sanitization is : "+(overTime-beginTime)+"ms");
		writer.write("the time of sanitization is : "+(overTime-beginTime)+"ms");
		writer.newLine();
		writer.write("================================");
		writer.newLine();
	}
	
	
	public void runPPMFAlgorithm(double min_sup, double sen_per, double[] w, String inputDatabaseFile, String inputSensitiveFile, String output) throws IOException {
		
		//BufferedReader reader = new BufferedReader(new FileReader(inputDatabaseFile));
		w1 = w[0];
		w2 = w[1];
		w3 = w[2];
		
		writer = new BufferedWriter(new FileWriter(output)); 
		
		startTimestamp = System.currentTimeMillis();
		

        File file1=new File(inputSensitiveFile);
		setM(1);
		ReadDB(min_sup,inputDatabaseFile);
		ReadHSI(file1);
		//System.out.println(ps.Dnum+"%%%%%%%%%");
		test(upper_sup);
		
		endTimestamp = System.currentTimeMillis();
		
		writer.write("the final run time is :"+(endTimestamp - startTimestamp)/1000+"s");
		writer.newLine();
		writer.newLine();
		
		writer.close();
	}
	
	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("=============  Greedy - STATS =============");
		System.out.println(" The fitness is : " + fitnesses);
		System.out.println(" There are " + (Original_DB.size()-Dnum) + " transactions left in the database.");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println("===================================================");
	}
}

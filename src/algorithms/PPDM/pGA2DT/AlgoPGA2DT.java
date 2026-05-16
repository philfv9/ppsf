package algorithms.PPDM.pGA2DT;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class AlgoPGA2DT {
	private static double upper_sup;
	private static double lower_sup;
	private static double w1,w2,w3;
	private int Psize;
	private int Dnumber;
	private final int gen=10;
	private pgaNode solution =new pgaNode();
	private pgaNode chrom[];
	private List<Set<String>> HS =new ArrayList<Set<String>>();
	private List<Set<String>> Hs =new ArrayList<Set<String>>();
	private List<Set<String>> CandidateSet=new ArrayList<Set<String>>();
	private List<Map<String, Integer>> FrequencySet=new ArrayList<Map<String,Integer>>();
	private Map<String,Integer> PreLargeSet=new HashMap<String,Integer>();
	private List<Set<String>> Origin_DB=new ArrayList<Set<String>>();
	private List<pDBNode> DB=new ArrayList<pDBNode>();
	private long oneTime, twoTime;
	
	
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
	 * @method Constructor
	 */
	public AlgoPGA2DT() {
        
        }
	public void ReadData(String data_path, String hs_path){
		try{
        	FileReader db = new FileReader(data_path);
            FileReader hs = new FileReader(hs_path);
            //FileReader db = new FileReader("D:\\a.txt");
            //FileReader hs = new FileReader("D:\\b.txt");
            BufferedReader br1 = new BufferedReader(db);
            BufferedReader br2 = new BufferedReader(hs);
            String line;
            Set<String> record1;
            Set<String> record2,record3;
            while ((line = br1.readLine()) != null){
            	if(line.trim().length()>0){
            		String str[] = line.split(" ");
                    record1 = new TreeSet<String>();
                    for(String w : str)
                    	record1.add(w);
                    Origin_DB.add(record1);
                    }
            }
            while((line=br2.readLine())!=null){
            	String str[] = line.split(",");
            	record2 = new TreeSet<String>();
                for (String w : str)
                	record2.add(w);
                HS.add(record2);
                record3 = new TreeSet<String>();
                record3.add(line);
                Hs.add(record3);
                }
            }
        catch (IOException ex) {
        	System.out.println("Read transaction records failed."+ ex.getMessage());
            System.exit(1);
            }
	}
	/**
	 * @method Init
	 */
	public void Init(int n, double min_sup, int size){
		upper_sup=min_sup;
		int max=0;
		for(Set<String> sn:HS){
			Set<Integer> si=new TreeSet<Integer>();
			for(int i=0;i<Origin_DB.size();i++)
				if(Origin_DB.get(i).containsAll(sn))
					si.add(i);
			if(si.size()>max)
				max=si.size();
		}
		double num=(max-upper_sup*Origin_DB.size())/(1.0-upper_sup);
		int dnum=((int) (num+1))+1;
		Dnumber = size*dnum;
		Psize=10;
		chrom=new pgaNode[Psize];//DB.size()/2
		double Dnum=(double) Dnumber;
		lower_sup=upper_sup*(1-Dnum/n);
	}
	public void CountItem(List<Set<String>> db){
		Set<String> Candidate=new TreeSet<String>();
		for(Set<String> str:db)
			for(String str1:str){
				Candidate.add(str1);
				
			}
		CandidateSet.add(Candidate);
	}
	/**
	 * @method Calculate support count
	 */
	public int Countsup(List<Set<String>> db,String str[]){
		int number=0;
		List<String> str1=new ArrayList<String>();
		for(int i=0;i<str.length;i++)
			str1.add(str[i]);
		for (Set<String> s1: db)
			if(s1.containsAll(str1))
					number++;
		return number;
	}
	/**
	 *@method Generate candidate item set
	 */
	public void CreatCandidate(int n){
		Iterator ite1 = FrequencySet.get(n-2).keySet().iterator();
		Iterator ite2 = FrequencySet.get(0).keySet().iterator();
		Set<String> test = new TreeSet<String>();
		while (ite1.hasNext()){
		   String s1=(String) ite1.next();
		   String s2[]=s1.split(",");
		   String c1 =s2[s2.length - 1];
		   while (ite2.hasNext()){
			  String s3=(String) ite2.next();
			  String s4[]=s3.split(",");
			  String c2 =s4[0];
			  if(c1.compareTo(c2)>=0)//if(c1>c2)
				 continue;
			  else{
				  String s=s1 +","+ s3;
				  test.add(s);
		    	}
		   }
		   ite2 = FrequencySet.get(0).keySet().iterator();
		 }
		CandidateSet.add(test);
	}

	/**
	 *@method Generate frequent itemsets
	 */
	public void CreatFrequency(List<Set<String>> db,int n){
		Map<String,Integer> test1=new HashMap<String,Integer>();
		Iterator ite=CandidateSet.get(n-1).iterator();
		while(ite.hasNext()){
			String s1=(String) ite.next();
			String s2[]=s1.split(",");
			int a=Countsup(db,s2);
			if(a>=(upper_sup *db.size()))
				test1.put(s1,a);
			else if(a<(upper_sup*db.size())&&a>=(lower_sup*db.size())){
				PreLargeSet.put(s1,a);
			}
		}
		FrequencySet.add(test1);
	}
	/**
	 * @ Determine whether the frequent itemsets are completely generated
	 * @param n
	 * @return
	 */
	public boolean FrequencySetIsEmpty(int n){
		if (FrequencySet.get(n-1).isEmpty())
			return true;
		else
			return false;
		 }
	/**
	 * @method Generate D'
	 */
	public void CtreatDB(List<Set<String>> ab,List<Set<String>> hs){
		for(int i=0;i<ab.size();i++)
			for(int j=0;j<hs.size();j++)
				if(ab.get(i).containsAll(hs.get(j))){
					pDBNode dn=new pDBNode();
					dn.Listr = ab.get(i);
					dn.indice=i;
					DB.add(dn);
					break;
				}
		//Psize=(DB.size()/2);
		//chrom=new Node[Psize];
		
	}
	/**
	 * @
	 */
	public void Generate(int n){
		for(int i=0;i<n;i++){
			chrom[i]=null;
			chrom[i]=new pgaNode();
			while(chrom[i].Squen.size()<Dnumber){
				int a=(int) (Math.random()*(DB.size()));
				chrom[i].Squen.add(DB.get(a).indice);
				}
		}
	}
	/**
	 * @ Calculate the value of HidingFailure
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
	 * @ Calculate the value of Missing and Artificial
	 * @param lss1
	 * @param lss2
	 * @return
	 */
	public List<Set<String>> Diff(List<Set<String>> lss1,List<Set<String>> lss2){
		List<Set<String>> list = new ArrayList(Arrays.asList(new Object[lss1.size()])); //<Set<String>>();
		Collections.copy(list,lss1);
        list.removeAll(lss2);
        return list;//System.out.println(list+"\n"+lss2);
	}
	/**
	 * @method Fitness method
	 * @param List of transaction to be deleted
	 * @throws IOException 
	 */
	public void Fitness(Map<String,Integer> fs,Map<String,Integer> pls,List<Set<String>> frequentpattern) throws IOException{
		for(int i=0;i<chrom.length;i++){
			
			List<Set<String>> afterlager=new ArrayList<Set<String>>();
			List<Set<String>> artificial=new ArrayList<Set<String>>();
			
			List<Set<String>> lls=new ArrayList<Set<String>>();
			lls = Delete(chrom[i].Squen, Origin_DB);
			int santized = Origin_DB.size()-lls.size();
			
			//===============Calculate how many frequent items are missing after deletion (including sensitive items)===============
			Set<String> MS=fs.keySet();
			for(String str:MS){
				Set<String> ss1=new TreeSet<String>();
				//for(String str:ls){*/
				String str1[]=str.split(",");
				if((fs.get(str)-Countsup(lls,str1))>=santized*upper_sup)
					ss1.add(str);
				//}
				if(!ss1.isEmpty())
					afterlager.add(ss1);
				ss1=null;
			}
			
			//===============Calculate increased frequent items================
			Set<String> PS=pls.keySet();
			for(String str:PS){
				Set<String> ss2=new TreeSet<String>();
				String str2[]=str.split(",");
				if((pls.get(str)-Countsup(lls,str2))>=santized*upper_sup)
					ss2.add(str);
				if(!ss2.isEmpty())
					artificial.add(ss2);
				ss2=null;
			}
			
			List<Set<String>> HF=Intersect(Hs,afterlager);
			List<Set<String>> ML=Diff(Diff(frequentpattern,Hs),afterlager);
			int a=HF.size();
			int b=ML.size();
			int c=artificial.size();

			double fit=w1*a+w2*b+w3*c;
			writer.write("Chromosome " + (i+1) + ": \tF-T-H: " + 
					a + "\tN-T-H: " + b + "\tN-T-G: " + c + "\t\tFitness: " + fit);
			writer.newLine();
			chrom[i].fitness=fit;
			if(fit<solution.fitness){
				solution.fitness=fit;
				solution.Squen=chrom[i].Squen;
			}
			afterlager=null;
			artificial=null;
		}
	}
	public List<Set<String>> Delete(Set<Integer> c, List<Set<String>> db){
		List<Set<String>> lss1=new ArrayList<Set<String>>();
		for(Integer inte:c){
				lss1.add(db.get(inte));
			}
		return lss1;
	}
	/**
	 * @method Select
	 */
	public void Selection(){
		int index;pgaNode max;
		for(int i=0;i<chrom.length;i++){
			index=i;
			for(int j=i+1;j<chrom.length;j++)
				if(chrom[j].fitness>chrom[index].fitness)
					index=j;
			if(index!=i){
				max=chrom[index];
				chrom[index]=chrom[i];
				chrom[i]=max;
				}
			}
		Generate(Psize/2);
	}
	/**
	 * @method Crossover
	 */
	public void Crossover(){
		int x,y,z;
		Set<Integer> s1,s2;
		pgaNode s[]=new pgaNode[Psize];
		for(int i=0;i<(Psize/2);i++){
			x=(int) (Math.random()*(Dnumber-1))+1;//intersection
			y=(int) (Math.random()*Psize);//chromosome1
			z=(int) (Math.random()*Psize);//chromosome2
			if(y==z||chrom[y].Squen==null||chrom[z].Squen==null)
				i--;
			else{
				s[y]=new pgaNode();
				s[z]=new pgaNode();
				s1=chrom[y].Squen;
				//System.out.println(s1);
				s2=chrom[z].Squen;
				//System.out.println(s2);
				int k=0;
				for(Integer in1:s1){
					if(k<x)
						s[z].Squen.add(in1);
					else
						s[y].Squen.add(in1);
					k++;
				}
				k=0;
				for(Integer in2:s2){
					if(k<x)
						s[y].Squen.add(in2);
					else
						s[z].Squen.add(in2);
					k++;
				}
				while(s[y].Squen.size()<Dnumber){
					int a=(int) (Math.random()*(DB.size()));
						s[y].Squen.add(DB.get(a).indice);
				}
				while(s[z].Squen.size()<Dnumber){
					int b=(int) (Math.random()*(DB.size()));
						s[z].Squen.add(DB.get(b).indice);
				}
				chrom[y].Squen=null;
				chrom[z].Squen=null;
				}
			}
		chrom=s;
	}
	/**
	 * @method Mutation
	 */
	public void Mutation(){
		int  m;
		Set<Integer> s;
		for(int i=0;i<chrom.length;i++){
			int a=(int) (Math.random()*Dnumber);
			s=chrom[i].Squen;
			m=0;
			for(Integer in1:s){
				if(m==a){
					chrom[i].Squen.remove(in1);
					while(chrom[i].Squen.size()!=Dnumber){
						int b=(int) (Math.random()*(DB.size()));
						chrom[i].Squen.add(DB.get(b).indice);
						}
					break;
					}
				else
					m++;
			}
		}
		}
	/**
	 * @throws IOException 
	 * @method Test method
	 */
	public void test() throws IOException{
		int index=0;
		int gent=1;
		CountItem(Origin_DB);
		index++;
		CreatFrequency(Origin_DB,index);
		do{
			index++;
			CreatCandidate(index);
			CreatFrequency(Origin_DB,index);
		}while(!FrequencySetIsEmpty(index));
		
		Map<String,Integer> FMS=new HashMap<String,Integer>();
		for(Map<String,Integer> msi:FrequencySet)
			if(!msi.isEmpty())
				FMS.putAll(msi);
		List<Set<String>> frequent_pattern=new ArrayList<Set<String>>();
		for(String str:FMS.keySet()){
			Set<String> ss=new TreeSet<String>();
			ss.add(str);
			frequent_pattern.add(ss);
		}
		CtreatDB(Origin_DB,HS);
		Generate(Psize);
		writer.write("=============Initial chromosome=============");
		writer.newLine();
		Fitness(FMS, PreLargeSet, frequent_pattern);
		writer.write("====================================");
		writer.newLine();
		writer.newLine();
		/*for(int i=0;i<chrom.length;i++){
			if(chrom[i].fitness==0){
				System.out.print("the best solution of the transaction squence to delete is : ");
				for(Integer s:chrom[i].Squen)
					System.out.print((s+1)+",");
				System.out.println();
				gent=gen;
				break;
				}
		}*/
		while(gent<gen){
			writer.write("=============Iteration " + gent + "=============");
			writer.newLine();
			gent++;
			Selection();
			Crossover();
			Mutation();
			Fitness(FMS, PreLargeSet, frequent_pattern);
			writer.write("====================================");
			writer.newLine();
			writer.newLine();
		}
		writer.write("the best solution of the transaction squence to delete is : "+solution.Squen);
		writer.newLine();
		writer.write("the best finess is : "+solution.fitness);
		writer.newLine();
	}
	
	public void runAlgorithm(double min_sup, double sen_per, String inputDatabaseFile, String output) throws IOException {
		
	}
	
	/**
	 * Method to run the algorithm
	 * @param minsup  a minimum support value as a percentage
	 * @param input  the path of an input file
	 * @param output the path of an input if the result should be saved to a file.
	 * @throws IOException exception if error while writting or reading the input/output file
	 */
	public void runPPMFAlgorithm(double min_sup, double sen_per, double[] w, String inputDatabaseFile, String inputSensitiveFile, String output) throws IOException {
		int alpha = 4;
		w1 = w[0];
		w2 = w[1];
		w3 = w[2];
		
		// write file object
		writer = new BufferedWriter(new FileWriter(output)); 
		
		startTimestamp=System.currentTimeMillis();
		//double dou=0.8+(i*1.0)/100;
		ReadData(inputDatabaseFile, inputSensitiveFile);
		Init(Origin_DB.size(), min_sup, alpha);
		test();
		endTimestamp=System.currentTimeMillis();
//		System.out.println("the final run time is :"+(endTime-startTime)/1000+"s");
		writer.write("the final run time is :"+(endTimestamp-startTimestamp)/1000+"s");
		writer.newLine();
		
		writer.close();
	}
	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("Algorithm finished.");
		System.out.println("============= pGA2DT - STATS =============");
		System.out.println(" The best solution to delete is : " + solution.Squen);
		System.out.println(" The best fitness is : " + solution.fitness);
		System.out.println(" There are " + (Origin_DB.size()-solution.Squen.size()) + " transactions left in the database.");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp)/1000 + " s");
		System.out.println("===================================================");
	}
}


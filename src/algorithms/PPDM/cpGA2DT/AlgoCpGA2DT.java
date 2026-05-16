package algorithms.PPDM.cpGA2DT;

/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import algorithms.PPDM.cpGA2DT.AlgoCpGA2DT;
import algorithms.PPDM.cpGA2DT.DBP;
import algorithms.PPDM.cpGA2DT.PNode;

import java.util.Set;
import java.util.TreeSet;

import algorithms.PPDM.sGA2DT.Node;

public class AlgoCpGA2DT {

	private static double upper_sup;//=0.4;
	private static double lower_sup;
	private static double w1=0.98,w2=0.01,w3=0.01;
	private final int gen=10;
	private int Dnumber;
	private double rat[];
	private PNode solution =new PNode();
	private List<Set<String>> HSI =new ArrayList<Set<String>>();
	private List<Set<String>> Hs =new ArrayList<Set<String>>();
	private List<Set<String>> CandidateSet=new ArrayList<Set<String>>();
	private List<Map<String,Integer>> FrequencySet=new ArrayList<Map<String,Integer>>();
	private Map<String,Integer> PreLargeSet=new HashMap<String,Integer>();
	private List<Set<String>> Origin_DB=new ArrayList<Set<String>>();
	private List<DBP> DB=new ArrayList<DBP>();
	private List<PNode> p;//=new ArrayList<PNode>();
	private long oneTime;
	private long twoTime;
	
	
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
	public AlgoCpGA2DT() {
		
	}
	
	/**
	 * @method Construction method
	 */
	public void ReadDB(String file1,File file2,double sup) throws IOException {
				upper_sup=sup;
                try {
                    FileReader db=new FileReader(file1);
                    FileReader hs = new FileReader(file2);
                    BufferedReader br1 = new BufferedReader(db);
                    BufferedReader br2 = new BufferedReader(hs);
                        String line;
                        Set<String> record1;
                        Set<String> record2,record3;
                        while ((line = br1.readLine()) != null){
                            if(line.trim().length()>0){
                                String str[] = line.split(" ");
                                record1 = new TreeSet<String>();
                                for (String w : str)
                                    record1.add(w);
                                Origin_DB.add(record1);
                                }
                            }
                        while((line=br2.readLine())!=null){
                        	String str[] = line.split(",");
                            record2 = new TreeSet<String>();
                            for (String w : str)
                                record2.add(w);
                        	HSI.add(record2);
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
	public void Init(int n,int size){
		
		Set<String> Alls=new TreeSet<String>();
		for(Set<String> stn:HSI){
			Alls.addAll(stn);
			//System.out.println(stn+"*&*&*&*&"+Alls);
		}
		Set<Integer> str=new TreeSet<Integer>();
		for(int i=0;i<Origin_DB.size();i++)
			if(Origin_DB.get(i).containsAll(Alls))
				str.add(i);
		//Alls.count=Alls.setint.size();
		//Find all STIDs that contain sensitive items
		int max=0;
		for(Set<String> sn:HSI){
			Set<Integer> si=new TreeSet<Integer>();
			for(int i=0;i<Origin_DB.size();i++)
				if(Origin_DB.get(i).containsAll(sn))
					si.add(i);
			if(si.size()>max)
				max=si.size();
		}
		//System.out.println(str.size()+"  ***********  :"+max);
		//If ATID is greater than max, only delete transactions in ATID
		double num=(max-upper_sup*Origin_DB.size())/(1.0-upper_sup);
		int dnum=((int) (num+1))+1;
		Dnumber=size*dnum;
		double Dnum=(double) Dnumber;
		lower_sup=upper_sup*(1-Dnum/n);
		//System.out.println(str+"\n &&&&&&   "+Dnumber+"****"+lower_sup);
	}
	/**
	 * @method Count the number of elements to generate a candidate 1-item set
	 * @param db
	 */
	public void CountItem(List<Set<String>> db){
		Set<String> Candidate=new TreeSet<String>();
		for(Set<String> str:db)
			for(String str1:str){
				Candidate.add(str1);
				
			}
		CandidateSet.add(Candidate);
	}
	/**
	 * @method Calculation support count
	 * @param db
	 * @param str
	 * @return
	 */
	public int Countsup(List<Set<String>> db,String str[]){
		int number=0;
		Set<String> str1=new TreeSet<String>();
		for(int i=0;i<str.length;i++)
			str1.add(str[i]);
		for (Set<String> s1: db)
			if(s1.containsAll(str1))
					number++;
		return number;
	}
	/**
	 * @method Generate candidate item set
	 * @param n
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
		   if (c1.compareTo(c2)>=0)//if(c1>c2)
			   continue;
		    else {
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
			String s2[]=s1.split(",");//System.out.println(s1);
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
	 * @method Judge whether the frequent itemsets are completely generated
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
	 * @ Generate D'
	 * @param ab
	 * @param hs
	 */
	public void CtreatDB(List<Set<String>> ab,List<Set<String>> hs){
		for(int i=0;i<ab.size();i++)
			for(int j=0;j<hs.size();j++)
				if(ab.get(i).containsAll(hs.get(j))){
					DBP dn=new DBP();
					dn.indice=i;
					DB.add(dn);
					break;
				}
		/*for(DBP dbp:DB)
			System.out.println(dbp.indice+" "+dbp.ratio);*/
	}
	public void Ratio(){
		rat=new double [DB.size()];
		rat[0]=DB.get(0).ratio;
		for(int i=1;i<rat.length;i++){
			rat[i]=rat[i-1]+DB.get(i).ratio;
		}
		/*
		for(int i=0;i<rat.length;i++){
			System.out.println(rat[i]);
		}
		*/
	}
	/**
	 * @
	 */
	public void Selection(int n){
		p=null;
		p=new ArrayList<PNode>();
		for(int i=0;i<n;i++){
			PNode ca=new PNode();
			while(ca.sequen.size()<Dnumber){
				//System.out.println(ca.sequen.size());
				double a=(Math.random()*(DB.size()*5));
				//System.out.println(a);
				if(a<=rat[0])
					ca.sequen.add(DB.get(0).indice);
				else{
					for(int j=1;j<rat.length;j++)
						if(rat[j-1]<a&&a<=rat[j]){
							ca.sequen.add(DB.get(j).indice);
							break;
							}
					}
				}
			p.add(ca);
			ca=null;
		}
		
	}
	/**
	 * @method Crossover
	 */
	public void Crossover(){
		int x,y,z;
		Set<Integer> s1,s2;
		PNode pn1,pn2;
		x=(int) (Math.random()*(Dnumber-1))+1;//Cross bit
		pn1=new PNode();
		pn2=new PNode();
		s1=p.get(0).sequen;
		s2=p.get(1).sequen;
		int k=0;
		for(Integer in1:s1){
			if(k<x)
				pn2.sequen.add(in1);
			else
				pn1.sequen.add(in1);
			k++;
		}
		k=0;
		for(Integer in2:s2){
			if(k<x)
				pn1.sequen.add(in2);
			else
				pn2.sequen.add(in2);
			k++;
		}
		while(pn1.sequen.size()!=Dnumber){
			int a=(int) (Math.random()*(DB.size()));
			pn1.sequen.add(DB.get(a).indice);
		};
		while(pn2.sequen.size()!=Dnumber){
			int b=(int) (Math.random()*(DB.size()));
			pn2.sequen.add(DB.get(b).indice);
		};
	
		p.get(0).sequen=pn1.sequen;
		p.get(1).sequen=pn2.sequen;		
	}
	/**
	 * @method Mutation
	 */
	public void Mutation(){
		//int a;
		int  m;
		Set<Integer> s=new TreeSet<Integer>();
		for(int i=0;i<p.size();i++){
			int a=(int) (Math.random()*Dnumber);
			s=p.get(i).sequen;
			m=0;
			for(Integer in1:s){
				if(m==a){
					p.get(i).sequen.remove(in1);
					while(p.get(i).sequen.size()!=Dnumber){
						int b=(int) (Math.random()*(DB.size()));
						p.get(i).sequen.add(DB.get(b).indice);
					}
					break;
				}
				else
					m++;
			}
		}
	}
	/**
	 * @ Calculate HidingFailure
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
	 * @ Calculate Missing and Artificial
	 * @param lss1
	 * @param lss2
	 * @return
	 */
	public List<Set<String>> Diff(List<Set<String>> lss1,List<Set<String>> lss2){
		List<Set<String>> list = new ArrayList(Arrays.asList(new Object[lss1.size()])); //<Set<String>>();
		Collections.copy(list,lss1);
        list.removeAll(lss2);
        return list;
	}
	/**
	 * @methhod Get the transaction set to delete
	 * @param c TID set to be deleted
	 * @param db Original Database
	 * @return Data to be deleted
	 */
	public List<Set<String>> Delete(Set<Integer> c,List<Set<String>> db){
		List<Set<String>> lss1=new ArrayList<Set<String>>();
		for(Integer inte:c){
				lss1.add(db.get(inte));
			}
		return lss1;
	}
	
	/**
	 * @method Fitness function
	 * @param List of transaction to be deleted
	 * @throws IOException 
	 */
	public void Fitness(Map<String,Integer> fs,Map<String,Integer> pls,List<Set<String>> frequentpattern) throws IOException{//,List<PNode> p1){
		writer.write("" + pls.size());
		writer.newLine();
		for(int i=0;i<p.size();i++){
			List<Set<String>> afterlager=new ArrayList<Set<String>>();
			List<Set<String>> artificial=new ArrayList<Set<String>>();
			
			List<Set<String>> lls=null;
			lls=new ArrayList<Set<String>>();
			lls=Delete(p.get(i).sequen,Origin_DB);
			int santized=Origin_DB.size()-lls.size();
			
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
			//System.out.println("******"+lls.size()+"******");
			List<Set<String>> HF=Intersect(Hs,afterlager);
			List<Set<String>> ML=Diff(Diff(frequentpattern,Hs),afterlager);
			int a=HF.size();
			int b=ML.size();
			int c=artificial.size();
			//System.out.println(HF+"\n"+ML+"\n"+artificial);
						
			double fit=w1*a+w2*b+w3*c;
//			System.out.println(a+","+b+","+c+","+fit);
			writer.write("Chromosome " + (i+1) + ": \tF-T-H: " + 
						a + "\tN-T-H: " + b + "\tN-T-G: " + c + "\t\tFitness: " + fit);
			writer.newLine();
			p.get(i).indice=fit;
			if(fit<solution.indice){
				solution.indice=fit;
				solution.sequen=p.get(i).sequen;
			}
			afterlager=null;
			artificial=null;
		}
		double b=(1.0/gen);
		if(p.get(0).indice<p.get(1).indice){
			for(Integer in:p.get(0).sequen)
				for(DBP dbp:DB)
					if(dbp.indice==in){
						dbp.ratio+=b;
						break;
						}
			for(Integer in:p.get(1).sequen)
				for(DBP dbp:DB)
					if(dbp.indice==in){
						dbp.ratio-=b;
						break;
						}
		}
		else if(p.get(0).indice>p.get(1).indice){
				for(Integer in:p.get(1).sequen)
					for(DBP dbp:DB)
						if(dbp.indice==in){
							dbp.ratio+=b;
							break;
							}
				for(Integer in:p.get(0).sequen)
					for(DBP dbp:DB)
						if(dbp.indice==in){
							dbp.ratio-=b;
							break;
							}
		}
	}
	/**
	 * @throws IOException 
	 * @ Test method
	 */
	public void test() throws IOException{
		int index=0;
		int gent=0;
		long startTime=System.currentTimeMillis();
		CountItem(Origin_DB);
		index++;
		CreatFrequency(Origin_DB,index);
		do{
			index++;
			CreatCandidate(index);
			CreatFrequency(Origin_DB,index);
		}while(!FrequencySetIsEmpty(index));
		long endTime=System.currentTimeMillis();
//		System.out.println("the mining time is :"+(endTime-startTime)/1000+"s");
		writer.write("the mining time is :" + (endTime-startTime)/1000 + "s");
		writer.newLine();
		
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
		
		oneTime=System.currentTimeMillis();
		CtreatDB(Origin_DB,HSI);
		do{
//			System.out.println("generation "+gent);
			writer.write("=============Iteration " + gent + "=============");
			writer.newLine();
			gent++;
			Ratio();
			Selection(2);
			Crossover();
			Mutation();
			Fitness(FMS,PreLargeSet,frequent_pattern);
			writer.write("====================================");
			writer.newLine();
			writer.newLine();
		}while(gent<gen);
		twoTime=System.currentTimeMillis();
//		System.out.println("the final iteration time is :"+(twoTime-oneTime)+"ms");
//		System.out.println("the best solution to delete is : "+
//		solution.sequen+"\n"+"the finess of it is : "+solution.indice+
//		"\n"+"There are " + (Origin_DB.size()-solution.sequen.size()) + "transactions left in the database");
		writer.write("the best solution of the transaction squence to delete is : " + solution.sequen);
		writer.newLine();
		writer.write("the best finess is : "+solution.indice);
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
		int alpha = 4;// The maximum deletion multiple can be set different values according to different data sets
		w1 = w[0];
		w2 = w[1];
		w3 = w[2];
		
		//BufferedReader reader = new BufferedReader(new FileReader(inputDatabaseFile));
		writer = new BufferedWriter(new FileWriter(output)); 
		
		startTimestamp = System.currentTimeMillis();
		

		File file1 = new File(inputSensitiveFile);
		ReadDB(inputDatabaseFile, file1, min_sup);
		Init(Origin_DB.size(),alpha);
		test();
		
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
		System.out.println("=============  cpGA2DT - STATS =============");
		System.out.println(" Final iteration time " + (twoTime - oneTime)/1000 + " s");
		System.out.println(" The best solution to delete is : " + solution.sequen);
		System.out.println(" The best fitness is : " + solution.indice);
		System.out.println(" There are " + (Origin_DB.size()-solution.sequen.size()) + " transactions left in the database.");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp)/1000 + " s");
		System.out.println("===================================================");
	}
}

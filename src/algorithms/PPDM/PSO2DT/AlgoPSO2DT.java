package algorithms.PPDM.PSO2DT;

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
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class AlgoPSO2DT extends Apriori{
	private static double w1=0.98,w2=0.01,w3=0.01;//w=0.9,r1,r2;
	//private int c1=2,c2=2;
	private int DNum;
	private static int psize=5;
	private Particle_Node gbest=new Particle_Node();
	private Particle_Node bestsolution=new Particle_Node();
	private List<Integer> AllSensitive=new ArrayList<Integer>();
	private List<Particle_Node> population=new ArrayList<Particle_Node>();
	private List<Set<String>> DB=new ArrayList<Set<String>>();
	private List<Sensitive_Node> HSI =new ArrayList<Sensitive_Node>();
	private List<Set<String>> hs=new ArrayList<Set<String>>();
	List<Double> fitnesses=new ArrayList<Double>();
	
	
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
	
	/**
	 * Default constructor
	 */
	public AlgoPSO2DT() {
		
	}
	
	
	
	public void ReadHSI(File file) throws IOException{
		try{
			
			FileReader hsi=new FileReader(file);
			BufferedReader br2=new BufferedReader(hsi);
			String line;
			Sensitive_Node record2;
			while ((line=br2.readLine())!=null){
				if(line.trim().length()>0){
					String str[]=line.split(",");
					Set<String> ss=new TreeSet<String>();
					record2=new Sensitive_Node();
					for(String str2:str)
						//Sensitive ss1=new Sensitive();
						//ss1.value=str2;
						ss.add(str2);
					record2.setstr=ss;
					HSI.add(record2);
					Set<String> ss2=new TreeSet<String>();
					ss2.add(line);
					hs.add(ss2);
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
	 *
	 * @param ap
	 * @throws IOException 
	 */
	public void Inti(int alpha) throws IOException{
		//Finding an ATID that contains all sensitive items
		Sensitive_Node Alls=new Sensitive_Node();
		for(Sensitive_Node sn:HSI) {
			Alls.setstr.addAll(sn.setstr);
			//System.out.println(sn.setstr+"*&*&*&*&"+Alls.setstr);
		}
		for(int i=0;i<Origion_DB.size();i++)
			if(Origion_DB.get(i).containsAll(Alls.setstr))
				Alls.setint.add(i+1);//Added position, not a index
		Alls.count=Alls.setint.size();
		//System.out.println(Alls.setint.size());
		Sensitive_Node Asn=new Sensitive_Node();
		for(int i=0;i<Origion_DB.size();i++){
			int count=0;
			for(Sensitive_Node sn:HSI){
				if(Origion_DB.get(i).containsAll(sn.setstr))
					count++;
			}
			if(count>0.5* (HSI.size()))
				Asn.setint.add(i+1);//Added position
		}
		Asn.count=Asn.setint.size();
		
		//Find all STIDs that contain sensitive items
		Set<Integer> si=new TreeSet<Integer>();
		int max=0;
		for(Sensitive_Node sn:HSI){
			for(int i=0;i<Origion_DB.size();i++)
				if(Origion_DB.get(i).containsAll(sn.setstr))
					sn.setint.add(i+1);
			sn.count=sn.setint.size();
			si.addAll(sn.setint);
			if(sn.count>max)
				max=sn.count;
		}
		//System.out.println(Alls.count+"  ***********  :"+max+"&&&"+Asn.setint.size());
		//If ATID is greater than max, only delete transactions in ATID
		double num=(max-upper_sup*Origion_DB.size())/(1.0-upper_sup);
		int dnum=((int) (num+1));
		if(Alls.count>dnum){
			DNum=dnum;
			AllSensitive.addAll(Alls.setint);
			DB=Origion_DB;
		}
		else {
			List<Integer> li=new ArrayList<Integer>();
			li.addAll(Alls.setint);
			DNum=alpha*(dnum-Alls.count);
			if(Asn.setint.size()>DNum){
			AllSensitive.addAll(Asn.setint);
			AllSensitive.removeAll(Alls.setint);
			DB=Delete(li,Origion_DB);
			}
			else{
				AllSensitive.addAll(si);
				DB=Origion_DB;
			}
		}
		AllSensitive.add(0);
		min_sup=upper_sup*(1-((double)(DNum))/Origion_DB.size());
		//dnum=(int)((max-a.min_sup*a.Origion_DB.size())/(1-a.min_sup))+1;
		for(int i=0;i<psize;i++){
			Particle_Node pn=new Particle_Node();
			List<Integer> seti=new ArrayList<Integer>();
			while(seti.size()<DNum){
				/*double x=Math.random();
				if(x<0.2){
					seti.add(0);
				}
				else{*/
					int a=(int) (Math.random()*AllSensitive.size());
					seti.add(AllSensitive.get(a));//int b= (Math.random()*32)-16;
					//pn.vsetint.add(a);pn.psetint.add(b);
				//}
			}
			pn.psetint.addAll(seti);
			seti=null;
			pn.pbest.addAll(pn.psetint);
			pn.pbfit=pn.fitness;
			//min_sup=upper_sup*(Origion_DB.size()-sum)/Origion_DB.size();
			population.add(pn);
			//System.out.println(pn.psetint);
			Set<Integer> asb=new TreeSet<Integer>();
			asb.addAll(pn.psetint);
			//System.out.println(asb+"\n"+"==="+asb.size());
		}
		
	}
	/**
	 * @method Generate next particle swarms generation
	 */
	public void Update(){
		for(Particle_Node pn: population){
			pn.vsetint=null;
			pn.vsetint=new ArrayList<Integer>();
			Set<Integer> setcur=new TreeSet<Integer>();
			setcur.addAll(pn.pbest);
			setcur.addAll(gbest.psetint);
			setcur.removeAll(pn.psetint);
			pn.vsetint.addAll(setcur);
			List<Integer> ll=new ArrayList<Integer>();
			//ll.addAll(pn.vsetint);
			//ll.removeAll(pn.psetint);
			//pn.vsetint=null;
			//pn.vsetint=new TreeSet<Integer>();
			//pn.vsetint.addAll(ll);
			if(pn.vsetint.size()<DNum){
				//System.out.println("+++ old ++++++=================="+pn.psetint.size());
				pn.psetint.add(0);
				//System.out.println("+++++ new +++++++=================="+pn.psetint.size());
				Set<Integer> si=new TreeSet<Integer>();
				si.addAll(pn.psetint);
				pn.psetint=null;
				pn.psetint=new ArrayList<Integer>();
				pn.psetint.addAll(si);
				List<Integer> li=new ArrayList<Integer>();
				while(li.size()<(DNum-pn.vsetint.size())){
					int a=(int) (Math.random()*pn.psetint.size());
					if(pn.psetint.get(a)==0){
						li.add(0);
					}
					else if(!li.contains(pn.psetint.get(a))){
						li.add(pn.psetint.get(a));
					}
				}
				pn.vsetint.addAll(li);
				li=null;
				/*while(pn.vsetint.size()<DNum){
					double x=Math.random();
					if(x<0.5){
						pn.vsetint.add(0);
					}
					else{
						int a=(int) (Math.random()*pn.psetint.size());
						pn.vsetint.add(pn.psetint.get(a));
					}
				}*/
				/*while(pn.vsetint.size()<DNum){
					int a=(int) (Math.random()*AllSensitive.size());
					pn.vsetint.add(AllSensitive.get(a));
				}*/
			}
			else {
				//List<Integer> li=new ArrayList<Integer>();
				//li.addAll(pn.vsetint);
				while(pn.vsetint.size()>DNum){
					int a=(int) (Math.random()*pn.vsetint.size());
					pn.vsetint.remove(a);
					//System.out.println(a+"  *****  "+li.get(a));
				}
			}
			pn.psetint=pn.vsetint;
			//System.out.println("%%%   %%   %%%"+pn.psetint.size());
		}
	}
	/**
	 * @method Delete the Transaction of the ID contained in C;
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
	 * @method Generating new database if ATID<max
	 * @param li
	 * @param db
	 * @return
	 */
	/*public List<Set<String>> Delete2(List<Integer> li,List<Set<String>> db){
		List<Set<String>> lss=new ArrayList<Set<String>>();
		Set<Integer> setrue=new TreeSet<Integer>();
		for(Integer inte:li)
			setrue.add(inte-1);
		for(int i=0;i<db.size();i++){
			if(!setrue.contains(i))
				lss.add(db.get(i));
		}
		return lss;
	}*/
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
	 * @param List of transaction to be deleted
	 * @throws IOException 
	 */
	public void Fitness(List<Set<String>> fs,List<Set<String>> pls) throws IOException{//,List<Set<String>> ){//,List<PNode> p1){
		//Apriori ap=new Apriori();
		
		for(int i=0;i<population.size();i++){
			List<Set<String>> afterlager=new ArrayList<Set<String>>();
			List<Set<String>> artificial=new ArrayList<Set<String>>();
			
			List<Set<String>> lls=null;
			lls=new ArrayList<Set<String>>();
			lls=Delete(population.get(i).psetint,DB);
			population.get(i).dbsize=lls.size();
			for(Set<String> ls:fs){
				Set<String> ss1=new TreeSet<String>();
				for(String str:ls){
					String str1[]=str.split(",");
					if(Count_Support(lls,str1)>=lls.size()*upper_sup)
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
					if(Count_Support(lls,str2)>=lls.size()*upper_sup)
						ss2.add(str);
				}
				if(!ss2.isEmpty())
					artificial.add(ss2);
				ss2=null;
			}
			/*for(Set<String> ls:pls){
				Set<String> ss2=new TreeSet<String>();
				for(String str:ls){
					String str1[]=str.split(",");
					if(Count_Support(lls,str1)>=lls.size()*upper_sup)
						ss2.add(str);
				}
				if(!ss2.isEmpty())
					FrequencySet.add(ss2);
				ss2=null;
			}*/
			//System.out.println(ap1.FrequencySet);//qnm
			int a=Intersect(hs,afterlager).size();
			List<Set<String>> l=new ArrayList<Set<String>>();
			//l=Diff(fs,hs);
			//System.out.println(fs+"\n"+FrequencySet+"\n"+l);
			//System.out.println(fs.size()+"***"+afterlager.size());
			int b=fs.size()-hs.size()-afterlager.size()+a;
			int c=artificial.size();
			//System.out.println(Intersect(hs,afterlager)+"\n"+Diff(Diff(fs,hs),afterlager)+"\n"+artificial);
			double fit=(w1*a+w2*b+w3*c);
			fitnesses.add(fit);
			//if(fit<=population.get(i).fitness){
			population.get(i).item=Count_Item(lls);
			//int y=Count_Item(Delete(population.get(i).pbest,DB));
			if( (fit<population.get(i).pbfit) 
				//||( (fit==population.get(i).pbfit)&&(population.get(i).item>population.get(i).pitem))
					  
				||( (fit==population.get(i).pbfit)&&(population.get(i).dbsize>population.get(i).psize) )
			   ||( (fit==population.get(i).pbfit)&&(population.get(i).dbsize==population.get(i).psize)&&(population.get(i).item>population.get(i).pitem))
			  ){
				population.get(i).pbfit=fit;
				population.get(i).pbest=population.get(i).psetint;
				population.get(i).pitem=population.get(i).item;
				population.get(i).psize=population.get(i).dbsize;
			}
			//System.out.println(population.get(i).pbest);
			population.get(i).fitness=fit;
			
//			System.out.println(population.get(i).psetint+"\n"+a+","+b+","+c+"   "+fit+"\n");
			writer.write("Particle swarm " + (i+1) + ": \tF-T-H: " + a + "\tN-T-H: " + b + "\tN-T-G: " + c + "\t\tFitness: " + fit);
			writer.newLine();
			afterlager=null;
			artificial=null;
			//FrequencySet=new ArrayList<Set<String>>();
		}
		gbest=new Particle_Node();
		for(Particle_Node pn: population){
			if( (pn.fitness<gbest.fitness) //||((pn.fitness==gbest.fitness)&&(pn.item>gbest.item))
				||( (pn.fitness==gbest.fitness)&&(pn.dbsize>gbest.dbsize) )
			    ||((pn.fitness==gbest.fitness)&&(pn.dbsize==gbest.dbsize)&&(pn.item>gbest.item))
			){
				gbest.fitness=pn.fitness;
				gbest.psetint=pn.psetint;
				gbest.item=pn.item;//pn.pitemcount;
				gbest.dbsize=pn.dbsize;
			}
		}
	//	if(gbest.fitness<=bestsolution.fitness){
		if( (gbest.fitness<bestsolution.fitness)//||((gbest.fitness==bestsolution.fitness)&&(gbest.item>bestsolution.item))
				
			||((gbest.fitness==bestsolution.fitness)&&(gbest.dbsize>bestsolution.dbsize))
			||((gbest.fitness==bestsolution.fitness)&&(gbest.dbsize==bestsolution.dbsize)&&(gbest.item>bestsolution.item))
			){
			bestsolution.fitness=gbest.fitness;
			bestsolution.psetint=gbest.psetint;
			bestsolution.dbsize=gbest.dbsize;
			bestsolution.item=gbest.item;
		}
		//System.out.println("now the best solution is :"+"\t"+bestsolution.psetint+"   "+bestsolution.fitness);//bestsolution.psetint+
		//System.out.println("the best solution is :"+bestsolution.psetint+"\t"+bestsolution.fitness);
		//FS=null;
		
	}
	public void test(int alpha) throws IOException{
		Inti(alpha);
		int gent=0;
		long startTime = System.currentTimeMillis();
		Mining(Origion_DB);
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
		//System.out.println("**************");
		long beginTime = System.currentTimeMillis();
		writer.write("===================Initial====================");
		writer.newLine();
		Fitness(FS,PreLargeSet);
		writer.write("===============================================");
		writer.newLine();
		writer.newLine();
		long overTime = System.currentTimeMillis();
		long oneTime = System.currentTimeMillis();
		while(gent<15){
			//long oneTime = System.currentTimeMillis();
			//PreLargeSet
//            System.out.println("================generation : " + gent + "=================");
            writer.write("================generation : " + (gent+1)+ "=================");
            writer.newLine();
			Update();
			Fitness(FS,PreLargeSet);
			gent++;
			writer.write("===============================================");
			writer.newLine();writer.newLine();
		}
		long twoTime = System.currentTimeMillis();
//		System.out.println("the total time is : "+(twoTime-oneTime)/1000+"s");
//		System.out.println("the final best solution is : "+"\t"+bestsolution.psetint+"\nthe fitness is : "+bestsolution.fitness);//bestsolution.psetint+
		writer.write("the total time is : "+(twoTime-oneTime)/1000+"s");
		writer.newLine();
		writer.write("the final best solution is : "+"\t"+bestsolution.psetint);
		writer.newLine();
		writer.write("the fitness is : "+bestsolution.fitness);
		writer.newLine();
	}
	
	public void runPPMFAlgorithm(double min_sup, double sen_per, double[] w, String inputDatabaseFile, String inputSensitiveFile, String output) throws IOException {
		
		//BufferedReader reader = new BufferedReader(new FileReader(inputDatabaseFile));
		w1 = w[0];
		w2 = w[1];
		w3 = w[2];
		
		writer = new BufferedWriter(new FileWriter(output)); 
		
		startTimestamp = System.currentTimeMillis();
		
		
        int alpha = 4;

        File file1=new File(inputSensitiveFile);
        ReadDB(min_sup,inputDatabaseFile);
        ReadHSI(file1);
        test(alpha);
		
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
		System.out.println("=============  PSO2DT - STATS =============");
		System.out.println(" The best solution to delete is : " + bestsolution.psetint);
		System.out.println(" The best fitness is : " + bestsolution.fitness);
		System.out.println(" There are " + bestsolution.dbsize + " transactions left in the database.");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println("===================================================");
	}
	
}

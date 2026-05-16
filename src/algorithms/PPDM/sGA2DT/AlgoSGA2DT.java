package algorithms.PPDM.sGA2DT;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class AlgoSGA2DT {
	private static double upper_sup;
	//private static double lower_sup;
	private static double w1=0.999,w2=0.0005,w3=0.0005;
	private int Psize=10;
	private int Dnumber;
	private final int gen=10;
	private Node solution =new Node();
	private Node chrom[];
	private List<Set<String>> HS =new ArrayList<Set<String>>();
	private List<Set<String>> Hs =new ArrayList<Set<String>>();
	private List<Set<String>> CandidateSet=new ArrayList<Set<String>>();
	private List<Set<String>> FrequencySet=new ArrayList<Set<String>>();
	//private List<Set<String>> PreLargeSet=new ArrayList<Set<String>>();
	private List<Set<String>> Origin_DB=new ArrayList<Set<String>>();
	private List<DBNode> DB=new ArrayList<DBNode>();
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
	 * Default constructor
	 */
	public AlgoSGA2DT() {
		
	}
	
	/**
	 * @throws IOException 
	 * @method Construction method
	 */
	public void ReadDB(String data_path, File file,double sup) throws IOException {
		upper_sup=sup;
        try{
        	FileReader db = new FileReader(data_path);
        	FileReader hs = new FileReader(file);
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
            //System.out.println(HS);
        }
        catch (IOException ex) {
        	System.out.println("Read transaction records failed."+ ex.getMessage());
            System.exit(1);
            }
        }
	/**
	 * @method init
	 */
	public void Init(int n, int alpha){
		
		chrom=new Node[Psize];

		//Alls.count=Alls.setint.size();
		//Find all STIDs that contain sensitive items
		int max=0;
		for(Set<String> sn:HS){
			Set<Integer> si=new TreeSet<Integer>();
			for(int i=0;i<Origin_DB.size();i++)
				if(Origin_DB.get(i).containsAll(sn))
					si.add(i);
			if(si.size()>max)
				max=si.size();
		}
		//System.out.println(str.size()+"  ***********  :"+max);
		//If ATID is greater than max, only delete transaction in ATID
		double num=(max-upper_sup*Origin_DB.size())/(1.0-upper_sup);
		int dnum=((int) (num+1))+1;
		Dnumber=alpha*dnum;
		double Dnum=(double) Dnumber;
		//lower_sup=upper_sup*(1-Dnum/n);
		//System.out.println(str+"\n &&&&&&   "+Dnumber);
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
		Iterator ite1 = FrequencySet.get(n-1).iterator();
		Iterator ite2 = FrequencySet.get(0).iterator();
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
		   ite2 = FrequencySet.get(0).iterator();
		}
		CandidateSet.add(test);
	}

	/**
	 *@method Generate frequent itemsets
	 */
	public void CreatFrequency(List<Set<String>> db,int n){
		TreeSet<String> test1=new TreeSet<String>();
		Iterator ite=CandidateSet.get(n).iterator();
		while(ite.hasNext()){
			String s1=(String) ite.next();
			String s2[]=s1.split(",");
			int a=Countsup(db,s2);
			if(a>=(upper_sup *db.size()))
				test1.add(s1);//}
			/*else if(a<(upper_sup*db.size())&&a>=(lower_sup*db.size())){
				TreeSet<String> test2=new TreeSet<String>();
				test2.add(s1);
				PreLargeSet.add(test2);
				test2=null;
			}*/
		}
		FrequencySet.add(test1);
	}
	/**
	 * @ Judge whether the frequent itemsets are completely generated
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
					DBNode dn=new DBNode();
					dn.Listr=ab.get(i);
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
			chrom[i]=new Node();
			while(chrom[i].Squen.size()<Dnumber){
				int a=(int) (Math.random()*(DB.size()));
				//System.out.println(DB.size());
				chrom[i].Squen.add(DB.get(a).indice);
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
	 * @ Calculate Missing or Artificial value
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
	 * @method Fitness function
	 * @param fs List of transactions to be deleted
	 * @throws IOException 
	 */
	public void Fitness(List<Set<String>> fs) throws IOException{//,List<Set<String>> pls
		for(int i=0;i<chrom.length;i++){
			List<Set<String>> lls=null;
			lls=new ArrayList<Set<String>>();
			//Delete several data sets corresponding to 
			//the chromosome from the original database 
			//and return the deleted database.
			lls=Delete(chrom[i].Squen);
			Mining(lls);
			List<Set<String>> FS=new ArrayList<Set<String>>();
			for(Set<String> ssa:FrequencySet)
				if(!ssa.isEmpty())
					for(String str1:ssa){
						Set<String> ssb=new TreeSet<String>();
						ssb.add(str1);
						FS.add(ssb);
						ssb=null;
					}
		int a=Intersect(Hs,FS).size();
		List<Set<String>> l=new ArrayList<Set<String>>();
		l=Diff(fs,Hs);//There is a problem with the Diff function, 
		//and the difference operation of the set is not implemented.
		
		//System.out.println(fs+"\n"+FS+"\n"+l);
		int b=Diff(l,FS).size();
		int c=Diff(FS,fs).size();
		//System.out.println(Intersect(Hs,FS)+"\n"+Diff(l,FS)+"\n"+Diff(FS,fs));
		double fit=w1*a+w2*b+w3*c;
//		System.out.println(a+","+b+","+c+"  "+fit);
		writer.write("Chromosome " + (i+1) + ": \tF-T-H: " + a + "\tN-T-H: " + b + "\tN-T-G: " + c + "\t\tFitness: " + fit);
		writer.newLine();
		chrom[i].fitness=fit;
		if(fit<solution.fitness){
			solution.fitness=fit;
			solution.Squen=chrom[i].Squen;
		}
		FrequencySet=null;
		FrequencySet=new ArrayList<Set<String>>();
		}
	}
	public List<Set<String>> Delete(Set<Integer> c){
		List<Set<String>> lls1=new ArrayList<Set<String>>();
		for(int i=0;i<Origin_DB.size();i++){
			if(!c.contains(i))
				lls1.add(Origin_DB.get(i));
		}
		return lls1;
	}
	/**
	 * @method Select chromosome
	 */
	public void Selection(){
		int index;Node max;
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
		Node s[]=new Node[Psize];
		for(int i=0;i<(Psize/2);i++){
			x=(int) (Math.random()*(Dnumber-1))+1;//Crossover bit
			y=(int) (Math.random()*Psize);//chromosome1
			z=(int) (Math.random()*Psize);//chromosome2
			if(y==z||chrom[y].Squen==null||chrom[z].Squen==null)
				i--;
			else{
				s[y]=new Node();
				s[z]=new Node();
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
	 * @method Mining loop
	 * @param db
	 */
	public void Mining(List<Set<String>> db){
		int index=0;
		//int gent=0;
		CountItem(db);      //Take out all single items
		//index++;
		CreatFrequency(db,index);   //Select frequent items from all single items
		do{     
			//A single item frequent itemsets make up two item frequent item sets ,
			//form three item frequent item sets ...
			index++;
			CreatCandidate(index);
			CreatFrequency(db,index);
		}while(!FrequencySetIsEmpty(index));
	}
	/**
	 * @throws IOException 
	 * @method Test method
	 */
	public void test() throws IOException{
		
		int gent=1;
		Mining(Origin_DB);
		List<Set<String>> FS=new ArrayList<Set<String>>();
		for(Set<String> ssa:FrequencySet)   //Copy the FrequencySet to FS
			if(!ssa.isEmpty())
				for(String str1:ssa){
					Set<String> ssb=new TreeSet<String>();
					ssb.add(str1);
					FS.add(ssb);
					ssb=null;
					//System.out.print(ssa);
				}
		FrequencySet=null;
		FrequencySet=new ArrayList<Set<String>>();
		oneTime=System.currentTimeMillis();
		CtreatDB(Origin_DB,HS);     //Generate D'
		Generate(Psize);    //Randomly initialize the chromosome
		writer.write("=============Initial chromosome=============");
		writer.newLine();
		Fitness(FS);//PreLargeSet
		writer.write("====================================");
		writer.newLine();writer.newLine();
		while(gent<gen){
//			System.out.println(gent+" ");
			writer.write("=============Iteration " + gent + "=============");
			writer.newLine();
			gent++;
			Selection();
			Crossover();
			Mutation();
			Fitness(FS);//
			writer.write("====================================");
			writer.newLine();writer.newLine();
		}
		twoTime=System.currentTimeMillis();
//		System.out.println("the best solution of the transaction squence to delete is : "+solution.Squen);
//		System.out.println("the best finess is : "+solution.fitness);
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
		// The maximum deletion multiple
		// can be set different values according to different data sets
		int alpha = 4;
		
		w1 = w[0];
		w2 = w[1];
		w3 = w[2];
		
		// write file object
		writer = new BufferedWriter(new FileWriter(output)); 
		
		startTimestamp=System.currentTimeMillis();
		//double dou=0.8+(i*1.0)/100;
		File file1 = new File(inputSensitiveFile);
		ReadDB(inputDatabaseFile, file1, min_sup);
		Init(Origin_DB.size(), alpha);
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
		System.out.println("============= sGA2DT - STATS =============");
		System.out.println(" Final iteration time " + (twoTime - oneTime) + " ms");
		System.out.println(" The best solution to delete is : " + solution.Squen);
		System.out.println(" The best fitness is : " + solution.fitness);
		System.out.println(" There are " + (Origin_DB.size()-solution.Squen.size()) + " transactions left in the database.");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp)/1000 + " s");
		System.out.println("===================================================");
	}
}

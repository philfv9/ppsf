package algorithms.PPDM.PSO2DT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//import New_Test.Sensitive_Node;

public class Apriori {
	static double upper_sup;
	static double min_sup;
	List<Set<String>> Origion_DB=new ArrayList<Set<String>>();
	List<Set<String>> CandidateSet=new ArrayList<Set<String>>();
	List<Set<String>> FrequencySet=new ArrayList<Set<String>>();
	List<Set<String>> PreLargeSet=new ArrayList<Set<String>>();
	public void ReadDB(double sup,String path){
		upper_sup=sup;
		try{
			FileReader origion_db=new FileReader(path);
			BufferedReader br1=new BufferedReader(origion_db);
			String line;
			Set<String> record1;
			//Sensitive_Node record2;
			while ((line=br1.readLine())!=null){
				if(line.trim().length()>0){
	        		String str[] = line.split(" ");
	        		record1=new TreeSet<String>();
	        		for(String str1 :str)
	        			record1.add(str1);
	        		Origion_DB.add(record1);
				}
			}
		}
		catch(IOException ex) {
	    	System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
	        }
		
	}
	/**
	 * @method Initialize candidate sets
	 * @param db
	 */
	public void Init_Cand(List<Set<String>> db){
		Set<String> Candidate=new TreeSet<String>();
		for(Set<String> str:db)
			for(String str1:str){
				Candidate.add(str1);	
			}
		CandidateSet.add(Candidate);
	}
	/**
	 * @method Calculate the support of str
	 * @param lls
	 * @param str
	 * @return
	 */
	public int Count_Support(List<Set<String>> lss, String str[]){
		int count=0;
		//int number=0;
		Set<String> str1=new TreeSet<String>();
		for(int i=0;i<str.length;i++)
			str1.add(str[i]);
		for (Set<String> s1: lss)
			if(s1.containsAll(str1))
					count++;
		//return number;
		return count;
	}
	/**
	 * @method Generate candidate set
	 * @param n
	 */
	public void Creat_Candidate(int n){
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
	 * @method Generate frequent itemsets
	 * @param db
	 * @param n
	 */
	public void Creat_Frequency(List<Set<String>> db, int n){
		Set<String> test1=new TreeSet<String>();
		Iterator ite=CandidateSet.get(n).iterator();
		while(ite.hasNext()){
			String s1=(String) ite.next();
			String s2[]=s1.split(",");
			int a=Count_Support(db,s2);
			if(a>=(upper_sup *db.size()))
				test1.add(s1);//}
			else if(a<(upper_sup*db.size())&&a>=(min_sup*db.size())){
				TreeSet<String> test2=new TreeSet<String>();
				test2.add(s1);
				PreLargeSet.add(test2);
				test2=null;
			}
		}
		FrequencySet.add(test1);
	}
	/**
	 * @method Determine whether the mining is over
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
	 * @method Mining
	 * @param db
	 */
	public void Mining(List<Set<String>> db){
		int index=0;
		//int gent=0;
		Init_Cand(db);
		//index++;
		Creat_Frequency(db,index);
		do{
			index++;
			Creat_Candidate(index);
			Creat_Frequency(db,index);
		}while(!FrequencySetIsEmpty(index));
	}
	

}


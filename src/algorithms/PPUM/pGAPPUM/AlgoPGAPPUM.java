package algorithms.PPUM.pGAPPUM;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.io.File;

public class AlgoPGAPPUM extends Scan_Database{
	double w1=0.99,w2=0.005,w3=0.005;
	double maxMemory = 0;
	long endTime, startTime;
	int psize=10;
	final static int ITER = 50;
	static Chrome_Node solution=new Chrome_Node();
	//List<Set<Integer>> High_Utility_Set=new ArrayList<Set<Integer>>();
	/**
	 * @ Calculate the value of HidingFailure
	 * @param lds HU itemsets
	 * @param lss2 Sensitive itemsets
	 * @return Hiding Failure itemsets
	 */
	public List<Set<Integer>> Intersect(List<Set<Integer>> lss1,List<Set<Integer>> lss2){
		/*List<Set<String>> lss1=new ArrayList<Set<String>>();
		for(Data_Struct ds:lds){
			for(String str:ds.transaction){
				Set<String> ss=new TreeSet<String>();
				ss.add(str);
				lss1.add(ss);
			}
		}*/
		List<Set<Integer>> list = new ArrayList(Arrays.asList(new Object[lss1.size()]));   
        Collections.copy(list, lss1);   
        list.retainAll(lss2);
        return list;
		
	}
	/**
	 * @method Calculate Difference Set
	 * @param lds HUISet
	 * @param lss2  Target itemset
	 * @return
	 */
	public List<Set<Integer>> Diff(List<Set<Integer>> lss1,List<Set<Integer>> lss2){
		/*List<Set<String>> lss1=new ArrayList<Set<String>>();
		for(Data_Struct ds:lds){
			for(String str:ds.transaction){
				Set<String> ss=new TreeSet<String>();
				ss.add(str);
				lss1.add(ss);
			}
		}*/
		List<Set<Integer>> list = new ArrayList(Arrays.asList(new Object[lss1.size()])); //<Set<String>>();
		Collections.copy(list,lss1);
        list.removeAll(lss2);
        return list;
	}
	
	public List<Data_Struct> Delete(List<Integer> li,List<Data_Struct> lds){
		List<Data_Struct> lss2=new ArrayList<Data_Struct>();
		for(int i=0;i<lds.size();i++){
			if(!li.contains(i)||i==0){
				lss2.add(lds.get(i));
			}
		}
		return lss2;
	}
	private void checkMemory() {
		// get the current memory usage
		double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
				/ 1024d / 1024d;
		// if higher than the maximum until now
		if (currentMemory > maxMemory) {
			// replace the maximum with the current memory usage
			maxMemory = currentMemory;
		}
	}
	public void Fiteness(List<Chrome_Node> population,List<Data_Struct> lds,List<Set<Integer>> hus,List<Set<Integer>> prelarge,double maxdutil){
		for(int i=0;i<population.size();i++){
			List<Set<Integer>> afterlager=new ArrayList<Set<Integer>>();//Should be the method body's global variable, remaining HUISet after deletion
			List<Set<Integer>> artificial=new ArrayList<Set<Integer>>();//Should be a method body global variable
			if(population.get(i).Tuid>maxdutil){
				//System.out.println("the deleted utility is larger than max delete utility!!");
				System.out.println("The "+(i+1)+"_th chromosome's utility is larger than maximum deletion utility!!");
				//i--;
			}
			else{
				List<Data_Struct> lls=null;
				lls=new ArrayList<Data_Struct>();
				lls=Delete(population.get(i).chromesome,lds);//delete transactions in the i_th chromosome from original database
				System.out.println("Database 's size after deleting transactions contained in "+(i+1)+" _th chromosome: "+lls.size());
				Total_Utility=0;
				for(Data_Struct ds:lls){
					Total_Utility+=ds.tu;
				}
				//List<Set<Integer>> afterlager=new ArrayList<Set<Integer>>();//Should be the method body's global variable, remaining HUISet after deletion
				for(Set<Integer> hui:hus){
					int [] newuti=Calculate_Utility(lls,hui);
					if(newuti[0]>=Total_Utility*upper_min){
						afterlager.add(hui);
					}
				}
				//List<Set<Integer>> artificial=new ArrayList<Set<Integer>>();//Should be a method body global variable
				for(Set<Integer> pre:prelarge){
					int [] newuti=Calculate_Utility(lls,pre);
					if(newuti[0]>=Total_Utility*upper_min){
						artificial.add(pre);
					}
						
				}
			
				int a=Intersect(Sensitive_DB,afterlager).size();
				//System.out.println(TUD+"***"+TUD*upper_min+"\n"+hus+"\n"+High_Utility_Set);
				//List<Set<Integer>> l=new ArrayList<Set<Integer>>();
				//l=Diff(hus,Sensitive_DB);
				//int b=Diff(l,High_Utility_Set).size();
				//int c=Diff(High_Utility_Set,hus).size();
				int b=Math.abs(hus.size()-Sensitive_DB.size()-afterlager.size()+a);
				int c=artificial.size();
				//System.out.println("\r\nIn Fitness:side effect is:"+Intersect(Sensitive_DB,afterlager)+"\r\nDiff is"+Diff(Diff(hus,Sensitive_DB),afterlager)+"\r\nartificial is"+artificial);
				//System.out.println("Side effects of this chromosome are:\r\nHF:"+a+"\r\nMC:"+b+"\r\nAC :"+c);
				double fit=(w1*a+w2*b+w3*c);
				population.get(i).side_effect=fit;
				if(fit<solution.side_effect||(fit==solution.side_effect&&population.get(i).Tuid<solution.Tuid)){
					solution.Tuid=population.get(i).Tuid;
					solution.side_effect=fit;
					solution.chromesome=population.get(i).chromesome;
				}
				checkMemory();
				//System.out.println("Chromosome: "+population.get(i).chromesome);
				System.out.println("***Side effect of the "+(i+1)+"_th chromosome: "+"a--"+a+" b--"+b+" c--"+c);
				System.out.println("***Fitness of this chromosome: "+fit);
				afterlager=null;
				afterlager=new ArrayList<Set<Integer>>();
				artificial=null;
				artificial=new ArrayList<Set<Integer>>();
			}
		}
	}
	
	public void runAlgorithm(double upper_min, double sen_per, double[] w, String inputDatabaseFile, String inputSensitiveFile, String outputFile) throws IOException {
		w1 = w[0];
		w2 = w[1];
		w3 = w[2];
		double rate = upper_min;
		AlgoPGAPPUM pga=new AlgoPGAPPUM();
		//===================Initialization module=======================
		//String input="../PPUM/src/dataset/foodmart/foodmart_utility.txt";
		//String input="../PPUM/src/dataset/chess/chess_utility.txt";
		//String input="src/dataset/chess/chess_utility.txt";
		pga.Read_Database(inputDatabaseFile);
		pga.upper_min=rate;
		//pga.Read_Sensitive("../PPUM/src/dataset/foodmart/0.0005-0.0009/foodmart_"+rate+"_0.015.txt");
		//pga.Read_Sensitive("src/dataset/chess/0.265-0.285/chess_"+rate+"_0.015.txt");
		pga.Read_Sensitive(inputSensitiveFile);
		double maxutil=0.1*pga.Total_Utility;//Maximum delete utility
		pga.low_min=(1-maxutil/pga.Total_Utility)*pga.upper_min;
		//System.out.println(pga.Total_Utility+"\n"+pga.Total_Utility*pga.upper_min+"\n"+maxutil+"\n"+pga.Total_Utility*pga.low_min);
		PrintStream ps=new PrintStream(new FileOutputStream(outputFile));		
		PrintStream out = System.out;
		System.setOut(ps);
		System.setErr(ps);
		//System.out.println("mining begin:");
		//=================================================
		long oneTime=System.currentTimeMillis();
		//====================Mining module========================
		
		AlgoHUIM ahuim=new AlgoHUIM();
		int min_Utility=(int) (pga.Total_Utility*pga.upper_min);
		int low_Utility=(int) (pga.Total_Utility*pga.low_min);
		//String input="../HUIMiner/database/retail_utility.txt";
		File out_path = new File("temp_file");
		if(!out_path.exists())
			out_path.mkdirs();
		String output="temp_file/resultofretail.txt";
		
		ahuim.runAlgorithm(inputDatabaseFile,output,min_Utility,low_Utility);
		ahuim.printStats();
		//==================================================
		long twoTime=System.currentTimeMillis();
		//System.out.println("the time of mining is :"+(twoTime-oneTime)/1000+"s");
		//=====================Initialize candidates for deletion===================
		Cand_Trans ct=new Cand_Trans();
		List<Integer> lint=ct.getCand_Trans(pga.Origin_DB,pga.Sensitive_DB,maxutil);//Candidate deleted item TID
		List<Data_Struct> lds=ct.Sort_Cand_Trans(ct.cand_trans_delete, pga.Total_Utility);//Candidate deleted items
		int dsize=ct.getDelete_Size(lds, maxutil);//gene number
		System.out.println("Maximum deleted utility: "+maxutil+"\r\n"+"Candidate deleted transactions: "+lint+"\r\n"+"Gene size : "+dsize);
		//===================================================
		startTime=System.currentTimeMillis();
		//======================GA======================
		GA_opr gao= new GA_opr();
		gao.mut_rat=0.1;
		int gent=0;
		System.out.println("iteration "+(gent+1)+":");
		List<Chrome_Node> lcn=gao.Generation(gao.pop,lds,pga.psize,dsize);
		List<Chrome_Node> lcn1=gao.Crossover(lcn,dsize);
		List<Chrome_Node> lcn2=gao.Mutation(lcn1,lds,lint,dsize);
		pga.Fiteness(lcn2,pga.Origin_DB,ahuim.HUISet,ahuim.PrelagerSet,maxutil);
		do{				
			gent++;
			System.out.println("iteration :"+(gent+1)+":");
			List<Chrome_Node> lcn3=gao.Selection(lcn2);
			lcn=gao.Generation(lcn3,lds,pga.psize/2,dsize);
			lcn1=gao.Crossover(lcn,dsize);
			lcn2=gao.Mutation(lcn1,lds,lint,dsize);
			pga.Fiteness(lcn2,pga.Origin_DB,ahuim.HUISet,ahuim.PrelagerSet,maxutil);
			System.out.println("The current maximum memory footprint is:"+pga.maxMemory);
		}while(gent<ITER);
		
		//===================================================
		endTime=System.currentTimeMillis();
		System.out.println("the final run time is :"+(endTime-startTime)/1000+"s");
		System.out.println("solution chrome:"+solution.chromesome+"\r\nTotal utility : "+solution.Tuid+"\r\nfitness is: "+solution.side_effect);
		solution=null;
		solution=new Chrome_Node();
		pga.Sensitive_DB=null;
		pga.Sensitive_DB=new ArrayList<Set<Integer>>();
		System.setOut(out);
		System.setErr(out);	
	}
	public void printStats() {
		System.out.println("Algorithm finished!");
		System.out.println("=============  pGAPPUM - STATS =============");
		System.out.println("the final run time is :"+(endTime-startTime)+"ms");
		System.out.println("===================================================");
	}
}

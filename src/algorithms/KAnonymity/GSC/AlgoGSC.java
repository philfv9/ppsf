package algorithms.KAnonymity.GSC;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithms.KAnonymity.GSC.ReadData.Data_Struct;
import algorithms.KAnonymity.GSC.ReadData.group;

public class AlgoGSC {
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

	public void runPPMFAlgorithm(int ks, int alpha, String inputDatabaseFile, String inputUtilityTableFile, String output) throws IOException {
		
		//BufferedReader reader = new BufferedReader(new FileReader(inputDatabaseFile));
		writer = new BufferedWriter(new FileWriter(output)); 
		startTimestamp = System.currentTimeMillis();

		ReadData RD=new ReadData(writer);
		RD.Read(inputUtilityTableFile, inputDatabaseFile);
		long start=System.currentTimeMillis();
		Collections.sort(RD.Original_Data);
		long end=System.currentTimeMillis();
		
		int Total_Loss=0;
		long Total_Time=0;
		List<Data_Struct> original=new ArrayList<Data_Struct>();
		original.addAll(RD.Original_Data);
		//long seg_T=System.currentTimeMillis();
		writer.write("============= ks: "+ks+"===========Experiment results=======================");
		writer.newLine();
		writer.write("*\tGray code to binary +gray code sorting time:"+(end-start)+"ms");
		writer.newLine();
		Total_Time+=(end-start);
		//==================Generate equivalence classes=====================
		long end_T=System.currentTimeMillis();
		List<group> cluster=RD.get_Groups(ks,alpha,original);
		for(group gr_p:cluster){
			Total_Loss+=gr_p.info_loss;
		}
		long over_T=System.currentTimeMillis();
		Total_Time+=(over_T-end_T);
		writer.write("*\tThe time taken to convert the gray code and form the cluster is: "+(Total_Time)+"ms");
		writer.newLine();
		writer.write("*\tThe total number of information loss caused by anonymization is: "+Total_Loss+"items were changed");
		writer.newLine();
		long over=System.currentTimeMillis();
		writer.write("*\tTotal time: "+(over-start)+"ms");
		writer.newLine();
		writer.write("=====================================================");
		writer.newLine();
		
		
		endTimestamp = System.currentTimeMillis();
		
		writer.write("the final run time is :"+(endTimestamp - startTimestamp)+"ms");
		writer.newLine();
		writer.newLine();
		
		writer.close();
	}
	
	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("=============  Hong GSC - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println("===================================================");
	}
}

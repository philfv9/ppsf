package algorithms.KAnonymity.PTA;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithms.KAnonymity.PTA.Anonymity;
import algorithms.KAnonymity.PTA.ReadData;

public class AlgoPTA {
	
	
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
	
	
	public void runPPMFAlgorithm(int segments, int ks, String inputDatabaseFile, String inputUtilityTableFile, String output) throws IOException {
		
		//BufferedReader reader = new BufferedReader(new FileReader(inputDatabaseFile));
		writer = new BufferedWriter(new FileWriter(output)); 
		
		startTimestamp = System.currentTimeMillis();
		String[] inputDatabaseFileSplited = inputDatabaseFile.split("/");
		String databaseName = inputDatabaseFileSplited[inputDatabaseFileSplited.length - 1];
		String [] database = {"chess", "mushroom", "pumsb", "connect", "accidents", "T10I4D100K"};
		int index = 0;
		for(int i = 0; i < database.length; i++) {
			if(databaseName.contains(database[i])) {
				index = i;
			}
		}
		int rows[] = {3196,8124,49046,67557,340183,100000};
		double average[] = {37.0,23.0,74.0,43.0,33.8,10.1};
		ReadData RD=new ReadData(writer);
		Anonymity Ano=new Anonymity(writer);
		//Item file path and raw data file path
		List<Integer> items=new ArrayList<Integer>();
		items = RD.Read(inputUtilityTableFile, inputDatabaseFile);
		
		long start=System.currentTimeMillis();
		//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(RD.Original_Data);
		int size=RD.Original_Data.size();
		long end=System.currentTimeMillis();

		//long seg_T=System.currentTimeMillis();
//		System.out.println("============="+ks+"===========Experiment results==========="+segments+"============");
//		System.out.println("*\tGray code to binary +gray code sorting time:"+(end-start)+"ms");
		writer.write("===========ks: "+ks+" ==========Experiment results=======segments: "+segments+" ========");
		writer.newLine();
		writer.write("Gray code to binary +gray code sorting time:"+(end-start)+"ms");
		writer.newLine();
		writer.write("===============================================================================");
		writer.newLine();
		RD.Segment(ks,size/(ks*segments),Ano,rows[index],average[index],items);
		long over=System.currentTimeMillis();
		
//		System.out.println("*\tTotal time:"+(over-start)+"ms");
//		System.out.println("=====================================================");
		writer.write("Total time:"+(over-start)+"ms");
		writer.newLine();
		writer.write("=====================================================");
		writer.newLine();
		
		endTimestamp = System.currentTimeMillis();
		
		writer.write("the final run time is :"+ (endTimestamp - startTimestamp) + "ms");
		writer.newLine();
		writer.newLine();
		
		writer.close();
	}
	
	
	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("=============  PTA - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
		System.out.println("===================================================");
	}
	
}

package algorithms.PPDM.SIF_IDF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class AlgoSifIdf {
	int iteration = 1;
	BufferedWriter writer = null;
	double minSupportThreshold;
	double sensitivePercentage;
	int minSupportCount;
	int decreasedCount;
	List<Set<String>> originalDataset;
	double[] transactionsSIF_IDF;
	
	int transactionsNumber; 
	Map<String, Integer> itemsCount;
	Map<String, Double> itemsMRC;
	Map<String, Double> itemsIDF;
	ArrayList<String> oneItemFrequentItemsets;
	int oneItemFrequentItemsetsNumber;
	List<ArrayList<ArrayList<String>>> frequentItemsets;
	int totalFrequentItemsetsNumber = 0;
	int largestFrequentItemsetsSize;
	int sensitiveItemsNumber;
	List<ArrayList<String>> sensitiveItemsets;
	Map<String, Integer> sensitiveItemsCount;
	int[] sensitiveItemsetsOccurCount;
	double[][] sensitiveItemsetsIDF;
	double[][] sensitiveItemsetsSIF;
	long startTime, endTime;
	
	/**
	 * Init this algorithm.
	 * @param minSupportThreshold Minimum support threshold.
	 * @param sensitivePercentage Sensitive itemsets percentage.
	 * @param datasetFilePath The file path of dataset.
	 * @throws IOException 
	 */
//	AlgoSifIdf(double minSupportThreshold, double sensitivePercentage, String datasetFilePath) throws IOException {
//		writer = new FileWriter("datasets/output.txt");
//		
//		this.minSupportThreshold = minSupportThreshold;
//		this.sensitivePercentage = sensitivePercentage;
//		loadDataset(datasetFilePath);
//		minSupportCount = (int)(Math.ceil(minSupportThreshold * transactionsNumber));
//		getItemsCount();
//		getOneItemFrequentItemsets();
//		if(oneItemFrequentItemsetsNumber == 0) {
//			System.out.println("There is no frequent item, algorithm stoped.");
//			System.exit(1);
//		}
//		totalFrequentItemsetsNumber += oneItemFrequentItemsetsNumber;
//		getAllFrequentItemsets();
//		
//		sensitiveItemsNumber = (int)(Math.ceil(oneItemFrequentItemsetsNumber * sensitivePercentage));
//		generateSensitiveItemsets(sensitiveItemsNumber);
//		
//		long startTime = System.currentTimeMillis();
//		while(!checkStop()) {
//			calculateSIF();
//			calculateIDF();
//			calculateSIF_IDF();
//			deleteItem();
//			getItemsCount();
//		}
//		long endTime = System.currentTimeMillis();
//		
//		System.out.println("Finished! Time used: " + ((endTime - startTime)/1000) + "s");
//		writer.close();
//	}
	
	public boolean checkStop() {
		sensitiveItemsetsOccurCount = new int[sensitiveItemsets.size()];
		boolean flag = true;
		for(int i = 0; i < sensitiveItemsets.size(); i++) {
			for(int j = 0; j < originalDataset.size(); j++) {
				if(originalDataset.get(j).containsAll(sensitiveItemsets.get(i))) {
					sensitiveItemsetsOccurCount[i]++;
				}
			}
			if(sensitiveItemsetsOccurCount[i] >= minSupportCount) {
				flag = false;
			}
		}
		
		return flag;
	}
	
	public void deleteItem() throws IOException {
		double maxSIF_IDF = 0;
		int selectedTid = 0;
		for(int i = 0; i < originalDataset.size(); i++) {
			if(maxSIF_IDF < transactionsSIF_IDF[i]) {
				maxSIF_IDF = transactionsSIF_IDF[i];
				selectedTid = i;
			}
		}
		sensitiveItemsCount = new TreeMap<String, Integer>();
		String selectedItem = "";
		for(int i = 0; i < sensitiveItemsets.size(); i++) {
			for(String sensitiveItem :sensitiveItemsets.get(i)) {
				if(sensitiveItemsCount.get(sensitiveItem) == null) {
					sensitiveItemsCount.put(sensitiveItem, 1);
				}
				else {
					int count = sensitiveItemsCount.get(sensitiveItem);
					sensitiveItemsCount.put(sensitiveItem, count + 1);
				}
			}
		}
		List<Map.Entry<String, Integer>> entryArrayList = new ArrayList<>(sensitiveItemsCount.entrySet());
        Collections.sort(entryArrayList, Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(entryArrayList);
        for(int i = 0; i < entryArrayList.size(); i++) {
        	if(originalDataset.get(selectedTid).contains(entryArrayList.get(i).getKey())) {
        		selectedItem = entryArrayList.get(i).getKey();
        	}
        }
        
        writer.write("Iteration:" + (iteration++));
        writer.newLine();
        writer.write("item:" + selectedItem + "\tdeleted "
        		+ "from the " + selectedTid + "th transaction:" + originalDataset.get(selectedTid));
        writer.newLine();writer.newLine();
//        System.out.println("iteration:" + (iteration++) + "\n" + "item:" + selectedItem + "\tdeleted "
//        		+ "from the " + selectedTid + "th transaction:" + originalDataset.get(selectedTid));
        originalDataset.get(selectedTid).remove(selectedItem);
	}
	
	public void calculateSIF_IDF() {
		transactionsSIF_IDF = new double[originalDataset.size()];
		for(int i = 0; i < originalDataset.size(); i++) {
			for(int j = 0; j < sensitiveItemsets.size(); j++) {
				transactionsSIF_IDF[i] += sensitiveItemsetsSIF[i][j] *  sensitiveItemsetsIDF[i][j];
				if(originalDataset.get(i).isEmpty()) transactionsSIF_IDF[i] = 0;
			}
		}
	}
	
	public void calculateIDF() {
		calculateMRC();
		itemsIDF = new TreeMap<String, Double>();
		for(String key :itemsCount.keySet()) {
			double IDF = Math.log(originalDataset.size() * 1.0 / (itemsCount.get(key) - itemsMRC.get(key))) / Math.log(10);
			itemsIDF.put(key, IDF);
		}
		sensitiveItemsetsIDF = new double[originalDataset.size()][sensitiveItemsets.size()];
		for(int i = 0; i < originalDataset.size(); i++) {
			Set<String> transaction = originalDataset.get(i);
			for(int j = 0; j < sensitiveItemsets.size(); j++) {
				double IDF = 0;
				for(String item :sensitiveItemsets.get(j)) {
					if(transaction.contains(item)) {
						IDF += itemsIDF.get(item);
					}
				}
				sensitiveItemsetsIDF[i][j] = IDF;
			}
		}
		
	}
	
	public void calculateMRC() {
		itemsMRC = new TreeMap<String, Double>();
		
		for(String key :itemsCount.keySet()) {
			double MRC = 0;
			for(int j = 0; j < sensitiveItemsets.size(); j++) {
				double RC = 0;
				if(sensitiveItemsets.get(j).contains(key)) {
					RC = sensitiveItemsetsOccurCount[j] - minSupportThreshold * originalDataset.size() + 1;
				}
				if(RC > MRC) MRC = RC;
			}
			itemsMRC.put(key, MRC);
		}
	}
	
	public void calculateSIF() {
		sensitiveItemsetsOccurCount = new int[sensitiveItemsets.size()];
		
		// row_i refer to TID, colum_j refer to Sensitive Itemset Id
		sensitiveItemsetsSIF = new double[originalDataset.size()][sensitiveItemsets.size()];
		for(int i = 0; i < originalDataset.size(); i++) {
			Set<String> transaction = originalDataset.get(i);
			for(int j = 0; j < sensitiveItemsets.size(); j++) {
				if(transaction.containsAll(sensitiveItemsets.get(j))) {
					sensitiveItemsetsOccurCount[j]++;
				}
				int occurrenceNumber = 0;
				for(int k = 0; k < sensitiveItemsets.get(j).size(); k++) {
					if(transaction.contains(sensitiveItemsets.get(j).get(k))) {
						occurrenceNumber++;
					}
				}
				double sif = occurrenceNumber * 1.0 / transaction.size();
				sensitiveItemsetsSIF[i][j] = sif;
			}
		}
	}
	
	public void generateSensitiveItemsets(int sensitiveItemsNumber) {
		sensitiveItemsets = new ArrayList<ArrayList<String>>();
		while(sensitiveItemsets.size() < sensitiveItemsNumber) {
			//random int x: 0 <= x < largestFrequentItemsetsSize  
			int randomItemsetSize = (int)Math.floor(Math.random() * largestFrequentItemsetsSize); 
			ArrayList<ArrayList<String>> randomItemsets = frequentItemsets.get(randomItemsetSize);
			int randomItemsetId = (int)Math.floor(Math.random() * randomItemsets.size());
			ArrayList<String> randomItemset = randomItemsets.get(randomItemsetId);
			if(!sensitiveItemsets.contains(randomItemset)) {
				sensitiveItemsets.add(randomItemset);
			}
		}
		
		
		//test sensitive example
//		ArrayList<String> tmp1 = new ArrayList<String>();
//		tmp1.add("c");
//		tmp1.add("f");
//		tmp1.add("h");
//		sensitiveItemsets.add(tmp1);
//		ArrayList<String> tmp2 = new ArrayList<String>();
//		tmp2.add("a");
//		tmp2.add("f");
//		sensitiveItemsets.add(tmp2);
//		ArrayList<String> tmp3 = new ArrayList<String>();
//		tmp3.add("c");
//		sensitiveItemsets.add(tmp3);
//		sensitiveItemsNumber = 3;
	}
	
	
	public void getAllFrequentItemsets() {
		frequentItemsets = new ArrayList<ArrayList<ArrayList<String>>>();
		
		ArrayList<ArrayList<String>> kItemFrequentItemsets = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < oneItemFrequentItemsets.size(); i++) {
			ArrayList<String> oneItemItemset = new ArrayList<String>();
			String oneItem = oneItemFrequentItemsets.get(i);
			oneItemItemset.add(oneItem);
			kItemFrequentItemsets.add(oneItemItemset);
		}
		frequentItemsets.add(kItemFrequentItemsets);
		
		int k = 0;
		do {
			kItemFrequentItemsets = frequentItemsets.get(k);
			ArrayList<ArrayList<String>> nextItemFrequentItemsets = new ArrayList<ArrayList<String>>();
			for(int i = 0; i < oneItemFrequentItemsets.size(); i++) {
				String oneItem = oneItemFrequentItemsets.get(i);
				for(int j = 0; j < kItemFrequentItemsets.size(); j++) {
					ArrayList<String> itemset = kItemFrequentItemsets.get(j);
					String lastItem = itemset.get(itemset.size() - 1);
					if(oneItem.compareTo(lastItem) > 0) {
						ArrayList<String> newItemset = new ArrayList<String>();
						newItemset.addAll(itemset);
						newItemset.add(oneItem);
						int count = 0;
						for(Set<String> transaction :originalDataset) {
							if(transaction.containsAll(newItemset)) {
								count++;
							}
						}
						if(count >= minSupportCount) {
//							System.out.println("#" + newItemset + "  :  " + count);
							nextItemFrequentItemsets.add(newItemset);
						}
					}
				}
			}
			if(nextItemFrequentItemsets.isEmpty()) break;
			frequentItemsets.add(nextItemFrequentItemsets);
			totalFrequentItemsetsNumber += nextItemFrequentItemsets.size();
			k++;
		}while(k < oneItemFrequentItemsets.size());
		largestFrequentItemsetsSize = frequentItemsets.get(frequentItemsets.size() - 1).get(0).size();
		
	}
	
	
	
	
	/**
	 * Load the dataset to memory.
	 * @param datasetFilePath The file path of dataset. 
	 */
	public void loadDataset(String datasetFilePath) {
		try {
			FileReader datasetFileReader = new FileReader(datasetFilePath);
			BufferedReader datasetReader = new BufferedReader(datasetFileReader);
			String line;
			Set<String> transaction;
			originalDataset = new ArrayList<Set<String>>();
			while((line = datasetReader.readLine()) != null) {
				if(line.trim().length() > 0) {
	        		String[] splitedLine = line.split(" -1 ");
	        		transaction = new TreeSet<String>();
	        		for(int i = 0; i < splitedLine.length - 1; i++) {
	        			transaction.add(splitedLine[i]);
	        		}
	        		originalDataset.add(transaction);
				}
			}
			transactionsNumber = originalDataset.size();
			datasetReader.close();
		}
		catch(IOException ex) {
			System.out.println("Read original dataset failed.\n" + ex.getMessage());
	        System.exit(1);
		}
	}

	/**
	 * Get all items in the dataset along with their total occurrence count.
	 * 
	 */
	public void getItemsCount() {
		itemsCount = new TreeMap<String, Integer>();
		for(Set<String> transaction :originalDataset) {
			for(String item :transaction) {
				if(itemsCount.get(item) == null) {
					itemsCount.put(item, 1);
				}
				else {
					int oldCount = itemsCount.get(item);
					itemsCount.put(item, oldCount + 1);
				}
			}
		}
	}
	
	/**
	 * Get frequency itemsets that only contains one item.
	 */
	public void getOneItemFrequentItemsets() {
		oneItemFrequentItemsets = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry :itemsCount.entrySet()) {
			if(entry.getValue() >= minSupportCount) {
				oneItemFrequentItemsets.add(entry.getKey());
			}
		}
		oneItemFrequentItemsetsNumber = oneItemFrequentItemsets.size();
	}
	
	/**
	 * Method to run the algorithm
	 * @param minsup  a minimum support value as a percentage
	 * @param input  the path of an input file
	 * @param output the path of an input if the result should be saved to a file.
	 * @throws IOException exception if error while writting or reading the input/output file
	 */
	public void runAlgorithm(double min_sup, double sen_per, String inputDatabaseFile, String inputSensitiveFile, String output) throws IOException {
		writer = new BufferedWriter(new FileWriter(output));
		
		this.minSupportThreshold = min_sup;
		this.sensitivePercentage = sen_per;
		loadDataset(inputDatabaseFile);
		minSupportCount = (int)(Math.ceil(minSupportThreshold * transactionsNumber));
		getItemsCount();
		getOneItemFrequentItemsets();
		if(oneItemFrequentItemsetsNumber == 0) {
			System.out.println("There is no frequent item, algorithm stoped.");
			System.exit(1);
		}
		totalFrequentItemsetsNumber += oneItemFrequentItemsetsNumber;
		getAllFrequentItemsets();
		
		sensitiveItemsNumber = (int)(Math.ceil(oneItemFrequentItemsetsNumber * sensitivePercentage));
		generateSensitiveItemsets(sensitiveItemsNumber);
		
		startTime = System.currentTimeMillis();
		while(!checkStop()) {
			calculateSIF();
			calculateIDF();
			calculateSIF_IDF();
			deleteItem();
			getItemsCount();
		}
		endTime = System.currentTimeMillis();
		
		System.out.println("Finished! Time used: " + ((endTime - startTime)/1000) + "s");
		
		writer.close();
	}

	
	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("Algorithm finished.");
		System.out.println("============= SIF_IDF - STATS =============");
		System.out.println("" + (iteration - 1) + " items are deleted.");
		System.out.println(" Total time ~ " + ((endTime - startTime)/1000) + " s");
		System.out.println("===================================================");
	}

}

package algorithms.PPUM;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlgoMSICF extends PPUM {

	 public AlgoMSICF() {

	 }

	int tid = -1;
	String changeItem = null;

	public void hideSI(String dst) {
		Set<String> removed = new HashSet<String>();
		for (String sk : sensitive.keySet()) {
			double diff = sensitive.get(sk) - getMut();
			//System.out.println("\tgetMut()=" + getMut());
			//System.out.println("\tdiff=" + diff);
			double dec = 0;
			//System.out.println(sk + "=" + sensitive.get(sk));
			while (diff > 0) {
				List<String> items = getKeys(sk);

				addressTIDandItem(sk);
				// String tid_changeItem = getTarget(items);
				// String tid_changeItem = getTargetNew(items);

				Map<String, Double> trans = getTransaction(database.get(tid));
				// updatedTrans.add(tid);
				double total = trans.get(changeItem) * getUtility(changeItem);
				//System.out.println("targe value is :" + total);
				//System.out.println("changeItem:" + changeItem);
				if (total < diff) {
					double utilityInTransaction = 0.0;
					for (String i : items) {
						utilityInTransaction += trans.get(i) * getUtility(i);
					}
					// check subset update.
					checkSubsetOfSensitive(trans, sk, changeItem,
							utilityInTransaction, true);
					// update item.
					trans.remove(changeItem);
					// remove index....
					// index.get(item).remove(tid);

					diff = diff - utilityInTransaction;
					//System.out.println("{{{{{{{{{{{" + utilityInTransaction);
				} else {
					dec = Math.ceil(diff / getUtility(changeItem));
					checkSubsetOfSensitive(trans, sk, changeItem, dec
							* getUtility(changeItem), false);
					trans.put(changeItem, trans.get(changeItem) - dec);
					diff = 0;
					//System.out.println(dec * getUtility(changeItem));
				}
				//System.out.println("\t\tdiff=" + diff);
				// //System.out.println("DDDDiff:" + diff);
				// //System.out.println("TID:" + tid);
				//System.out.println("Before:" + database.get(tid));
				database.set(tid, mapToTrans(trans));
				//System.out.println("After:" + database.get(tid));

				updatedTrans.add(tid);
				modifiedTimes++;
			}
			removed.add(sk);

		}
		writeDB2File(database, dst);

	}

	private void addressTIDandItem(String sk) {
		List<String> items = getKeys(sk);
		double maxItemUtility = 0;
		Map<String, Integer> ic = new HashMap<String, Integer>();
		for (String key : sensitive.keySet()) {
			double sv = sensitive.get(key);
			if (sv > mut) {
				for (String item : getKeys(key)) {
					if (ic.get(item) == null) {
						ic.put(item, 1);
					} else {
						ic.put(item, ic.get(item) + 1);
					}
				}
			}
		}
		double maxIC = -1;
		for (String item : ic.keySet()) {
			int iic = ic.get(item);
			if (iic > maxIC && items.contains(item)) {
				maxIC = iic;
				changeItem = item;
			}
		}

		for (int i = 0; i < database.size(); ++i) {
			Map<String, Double> trans = getTransaction(database.get(i));
			if (trans.keySet().containsAll(items)) {

				double iq = trans.get(changeItem);
				double iu = iq * getUtility(changeItem);
				if (iu > maxItemUtility) {
					maxItemUtility = iu;
					tid = i;
				}

			}
		}

	}

	public void runAlgorithm(double min_util, double sen_per, String inputDatabaseFile, String inputUtilityTableFile, String outputFile) {
		// Read utility table
		readUT(inputUtilityTableFile);

		// Read database
		readDatabaseFile(inputDatabaseFile);
		//System.out.println("database is "+database.get(0));
		// Set threshold
		//setMutThreshold(0.05);
		setMutThreshold(min_util);
		setSensitiveThreshold(sen_per);
		// Mine
		File out_path = new File("temp_file");
		if(!out_path.exists())
			out_path.mkdirs();
		String miningResult = "temp_file/hui_mine_result.txt";
		mining(inputDatabaseFile, miningResult);

		// Generate sensitive itemsets
		String siFile = "";
		siFile = generateSensitive(miningResult);
		// Read sensitive itemsets
		//String huiFile = "src/sensitive.txt";
		//readSensitiveHUI(huiFile);
		readSensitiveHUI(siFile);
		//System.out.println("sensitive size is :"+sensitive.size());
		hideSI(outputFile);

		// readDatabaseFile("src/hhuif_result.txt");
		//printParameters();
	}
	public void printStats() {
		
	}
}

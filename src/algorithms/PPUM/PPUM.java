package algorithms.PPUM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import algorithms.PPUM.tool.FileTool;
import algorithms.PPUM.tool.Name;
import algorithms.PPUM.tool.miningalgo.AlgoHUIMiner;

public abstract class PPUM {

	protected Map<String, Double> sensitive = new HashMap<String, Double>();

	protected List<String> database = new ArrayList<String>();
	
	protected Set<Integer> updatedTrans = new HashSet<Integer>();

	protected int mut = 0;

	protected double mutThreshold = 0.0;

	protected double sensitiveThreshold = 0.0;

	protected double dus = 0;
	protected double ius = 0;
	protected double dss = 0;
	protected int modifiedTransNum = 0;
	protected int modifiedTimes = 0;
	protected long time;
	protected double missingCost = 0;

	protected int databaseUtility = 0;

	protected Map<String, Integer> utilityTable = new HashMap<String, Integer>();

	public Map<String, Double> getSensitive() {
		return sensitive;
	}

	public void setSensitive(Map<String, Double> sensitive) {
		this.sensitive = sensitive;
	}

	public List<String> getDatabase() {
		return database;
	}

	public void setDatabase(List<String> database) {
		this.database = database;
	}

	public int getMut() {
		return mut;
	}

	public void setMut(int mut) {
		this.mut = mut;
	}

	public int getDatabaseUtility() {
		return databaseUtility;
	}

	public void setDatabaseUtility(int databaseUtility) {
		this.databaseUtility = databaseUtility;
	}

	public Map<String, Integer> getUtilityTable() {
		return utilityTable;
	}

	public void setUtilityTable(Map<String, Integer> utilityTable) {
		this.utilityTable = utilityTable;
	}

	public void setMutThreshold(double threshold) {
		this.mutThreshold = threshold;
		this.mut = (int) (threshold * databaseUtility);
	}

	public int getUtility(String key) {
		return utilityTable.get(key);
	}

	public double getSensitiveThreshold() {
		return sensitiveThreshold;
	}

	public void setSensitiveThreshold(double sensitiveThreshold) {
		this.sensitiveThreshold = sensitiveThreshold;
	}

	public double getMutThreshold() {
		return mutThreshold;
	}

	public void readUT(String utFile) {
		BufferedReader br = FileTool.getReader(utFile);
		String line = null;
		utilityTable.clear();
		try {
			while ((line = br.readLine()) != null) {
				String[] ut = line.split(", ");
				utilityTable.put(ut[0].trim(), Integer.parseInt(ut[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void readSensitiveHUI(String huiFile) {
		BufferedReader br = FileTool.getReader(huiFile);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] k_v = line.split(" ");
				double v = Double.parseDouble(k_v[1]);
				sensitive.put(k_v[0].trim(), v);
			}
			br.close();
		} catch (Exception e) {

		}
	}

	public void readDatabaseFile(String dbFile) {
		// TODO Auto-generated method stub
		database.clear();
		BufferedReader br = FileTool.getReader(dbFile);
		String line = null;
		try {
			int du = 0;
			while ((line = br.readLine()) != null) {
				database.add(line);
				String[] items = line.split(" ");
				// System.out.println(line);
				for (String item : items) {
					if (item.trim().equals(""))
						continue;
					String[] i_q = item.split(":");
					//System.out.println(i_q[0]);
					int q = Integer.parseInt(i_q[1]);
					du += q * utilityTable.get(i_q[0].trim());
				}
			}
			setDatabaseUtility(du);
			setMut((int) (getDatabaseUtility() * getMutThreshold()));
			br.close();
			System.out.println("DU=" + du);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String generateSensitive(String hui) {
		BufferedReader br = FileTool.getReader(hui);
		BufferedWriter bw = FileTool.getWriter(hui + "_si.txt");
		List<String> huis = new ArrayList<String>();
		String line = null;
		try {
			while (null != (line = br.readLine())) {
				huis.add(line);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int num = (int) (huis.size() * sensitiveThreshold);
		Random r = new Random();
		List<String> choosed = new ArrayList<String>();
		try {
			while (num > 0) {
				int lastIndex = huis.size() - choosed.size() - 1;
				int index = r.nextInt(lastIndex);
				bw.append(huis.get(index));
				bw.newLine();
				String tmp = huis.get(index);

				huis.set(index, huis.get(lastIndex));
				huis.set(lastIndex, tmp);
				choosed.add(tmp);
				num--;
			}
			bw.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hui + "_si.txt";
	}

	protected void writeDB2File(List<String> db, String path) {
		System.out.println("OUT PATH:" + path);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					new File(path), false));
			for (String t : db) {
				bw.append(t);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected String mapToTrans(Map<String, Double> trans) {
		String str = "";
		for (String key : trans.keySet()) {
			String v = (trans.get(key) + "");
			int pos = v.indexOf(".");
			if (pos != -1) {
				v = v.substring(0, pos);
			}
			str = str + key + ":" + v + " ";
		}
		return str;
	}

	protected Map<String, Double> getTransaction(String trans) {

		Map<String, Double> t = new HashMap<String, Double>();
		if (trans == null || trans.equals("")) {
			return t;
		}
		for (String attr : trans.split(" ")) {
			String[] kv = attr.split(":");
			t.put(kv[0], Double.parseDouble(kv[1]));
		}
		return t;
	}

	protected void checkSubsetOfSensitive(Map<String, Double> trans, String sk,
			String item, double dec, boolean del) {
		for (String key : sensitive.keySet()) {
			if (key.equals(sk)) {
				sensitive.put(key, sensitive.get(key) - dec);
				continue;
			}
			// Sensitive key contain the updated item.
			List<String> keys = getKeys(key);
			if (keys.contains(item)) {
				// Current sensitive key is a subset of hiding sensitive.
				if (trans.keySet().containsAll(keys)) {
					// if deleted item.
					if (del) {
//						System.out.println("!!!!REMOVE!!!!");
						dec = 0;
						for (String tk : keys) {
							dec += trans.get(tk) * getUtility(tk);
						}
					}
					// just decrease the value of item.
//					System.out.println("DEC:++++++++++====" + dec
//							+ "-----------" + del);
					sensitive.put(key, (sensitive.get(key) - dec));
				}
			}

		}

	}

	public void mining(String src, String dst) {
		//String huiPath = transDBformat2HUIMiner(src, "temp_file/new_format.txt");
		File out_path = new File("temp_file");
		if(!out_path.exists())
			out_path.mkdirs();
		String huiPath = transDBformat2HUIMiner(src, "temp_file/new_format.txt");
		System.out.println("transfer db formate is ok.");
		System.out.println("begin mining....");
		printParameters();
		AlgoHUIMiner algoHUIMiner = new AlgoHUIMiner();
		try {
			//algoHUIMiner.runAlgorithm(huiPath, dst, getMut());
			algoHUIMiner.runAlgorithm( huiPath, dst, getMut());
			algoHUIMiner.printStats();
			System.out.println("minUtility:"+getMut());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract void hideSI(String dst);

	protected void printParameters() {
		System.out.println("****************************************");
		System.out.println("DU=" + databaseUtility);
		System.out.println("MUT=" + mut);
		System.out.println("mutThreshold=" + mutThreshold);
		System.out.println("sensitiveThreshold=" + sensitiveThreshold);
		System.out.println("****************************************");

	}

	protected String getParameters() {
		StringBuffer sb = new StringBuffer();
//		sb.append(this.getClass().getSimpleName() + "\n");
		sb.append("DBU=" + databaseUtility + "\n");
		sb.append(Name.MUT+"=" + mut + "\n");
		sb.append("SI=" + sensitive.size() + "\n");
		sb.append(Name.MUT_THR+"=" + mutThreshold + "\n");
		sb.append(Name.MOD_TRANS+"=" + updatedTrans.size() + "\n");
		sb.append(Name.MOD_NUMS+"=" + modifiedTimes + "\n");
		sb.append(Name.TIME+"=" + time + "\n");
		sb.append(Name.SI_PRO+"=" + sensitiveThreshold + "\n");
		return sb.toString();

	}

	public String transDBformat2HUIMiner(String src, String dst) {

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {

			FileReader fr = new FileReader(new File(src));
			fw = new FileWriter(new File(dst));
			BufferedReader br = new BufferedReader(fr);

			bw = new BufferedWriter(fw);
			String str = null;
			StringBuffer sb = new StringBuffer();
			while (null != (str = br.readLine())) {
				if (str.length() == 0) {
					continue;
				}
				String items = "";
				String counts = "";
				String[] item_count = str.split(" ");
				double tu = 0.0;
				for (int i = 0; i < item_count.length; ++i) {
					String[] item_value = item_count[i].split(":");
					items += item_value[0] + " ";//get the items
					tu += getUtility(item_value[0])
							* Integer.parseInt(item_value[1]);//get the total utility of items in this line
					counts += Integer.parseInt(item_value[1]) *getUtility(item_value[0])+ " ";//sum utility of every item
				}
				sb.append(items.substring(0, items.length() - 1) + ":"
						+ (int) tu + ":"						
						+ counts.substring(0, counts.length() - 1) + "\r\n");//storing format: items:total utility:utility of every item
						//+ counts.substring(0, counts.length() - 1) + "\r");use "\r" instead of "\r\n"in linux system
			}
			bw.write(sb.toString());
			br.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dst;
	}

	protected List<String> getKeys(String key) {
		List<String> keys = new ArrayList<String>();
		for (String item : key.split(":")) {
			keys.add(item);
		}
		return keys;
	}

	private double getIUS(String bString, String aString) {
		Map<String, Object> hui = getMapFromFile(bString);
		double bu = 0;
		double au = 0;
		for (String key : hui.keySet()) {
			bu += Double.parseDouble((String) hui.get(key));
		}
		hui = getMapFromFile(aString);
		for (String key : hui.keySet()) {
			au += Double.parseDouble((String) hui.get(key));
		}
		return au / bu;
	}

	private double getDUS(String before, String ut) {
		BufferedReader br = null;
		double tu = 0;
		try {

			br = new BufferedReader(new FileReader(new File(before)));

			String line = null;
			Map<String, Object> u = getMapFromFile(ut);
			while (null != (line = br.readLine())) {
				for (String attr : line.split(" ")) {
					if (!attr.contains(":")) {
						continue;
					}
					String[] kv = attr.split(":");
					tu += Integer.parseInt(kv[1])
							* Double.parseDouble((String) u.get(kv[0]));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tu;
	}

	private double getDSS(String before, String after) {
		Map<String, Integer> beforeMap = getBitSetMap(before, " ");
		Map<String, Integer> afterMap = getBitSetMap(after, " ");

		double sumBefore = 0;
		double sumAfter = 0;
		double sumProduct = 0;
		for (String key : beforeMap.keySet()) {
			sumBefore += Math.pow(beforeMap.get(key), 2);
			int tmp = afterMap.get(key) == null ? 0 : afterMap.get(key);
			sumAfter += Math.pow(tmp, 2);
			sumProduct += beforeMap.get(key) * tmp;
		}
		for (String key : afterMap.keySet()) {
			if (!beforeMap.keySet().contains(key)) {
				sumAfter += Math.pow(afterMap.get(key), 2);
			}
		}
		return sumProduct / (Math.sqrt(sumBefore) * Math.sqrt(sumAfter));
	}

	public Map<String, Integer> getBitSetMap(String db, String spliter) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(db)));
			String key = "";
			String line = null;
			while (null != (line = br.readLine())) {
				String[] items = line.split(spliter);
				BitSet bitSet = new BitSet();
				for (String item : items) {
					if (item.equals("")) {
						continue;
					}
					key = item.split(":")[0];
					bitSet.set(Integer.parseInt(key), true);
				}
				key = bitSet.toString();
				if (map.get(key) == null) {
					map.put(key, 1);
				} else {
					map.put(key, map.get(key) + 1);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	private void compare(String before, String after, String out, String time,
			Set<String> sks, String prefix) {
		BufferedReader brb = null;
		BufferedReader bra = null;
		BufferedWriter result = null;
		BufferedWriter writer = null;
		Map<String, Double> b = new HashMap<String, Double>();
		Map<String, Double> a = new HashMap<String, Double>();
		try {
			brb = new BufferedReader(new FileReader(new File(before)));
			bra = new BufferedReader(new FileReader(new File(after)));
			result = new BufferedWriter(new FileWriter(new File(out)));
			writer = new BufferedWriter(new FileWriter(new File(
					"src/data/exp_msicf.txt"), true));
			writer.newLine();
			writer.append("***********************" + prefix
					+ "*********************************\n");
			String line = null;
			while (null != (line = brb.readLine())) {
				String[] kv = line.split(" ");
				b.put(kv[0], Double.parseDouble(kv[1]));
			}
			brb.close();
			while (null != (line = bra.readLine())) {
				String[] kv = line.split(" ");
				a.put(kv[0], Double.parseDouble(kv[1]));
			}
			bra.close();

			result.append(time);
			result.newLine();
			result.append("Total num is =" + b.size());
			result.newLine();

			writer.append(time);
			writer.newLine();
			writer.append("Total num is =" + b.size());
			writer.newLine();
			// missing rule.
			int lost = 0;
			int hf = 0;
			int ghost = 0;
			for (String key : b.keySet()) {
				if (a.get(key) == null) {
					if (sks.contains(key)) {
						System.out.println(key + "=" + b.get(key)
								+ "---->sensitive.");
					} else {
						// System.out
						// .println(key + "=" + b.get(key) + "---->lost");
						result.append(key + "=" + b.get(key) + "---->lost");
						result.newLine();
						lost++;
					}

				} else {
					if (sks.contains(key) && b.get(key) != null) {
						System.out.println(key + "=" + a.get(key)
								+ "---->hiding failure");
						result.append(key + "=" + a.get(key)
								+ "----->hiding failure");
						hf++;
						result.newLine();
					}
				}
			}
			// artifical rule
			for (String key : a.keySet()) {
				if (!b.keySet().contains(key)) {
					System.out.println(key + "=" + a.get(key)
							+ "---->arificial");
					result.append(key + "=" + a.get(key) + "---->arificial");
					ghost++;
					result.newLine();
				} else {
					// System.out.println(key +"="+a.get(key)+"---->a large");
				}
			}
			result.append("Lost=" + lost);
			result.append("\r\nMissingRate=" + lost * 1.0 / b.size());
			result.newLine();
			result.append("Ghost=" + ghost);
			result.newLine();
			result.append("Failure=" + hf);

			writer.append("Lost=" + lost);
			writer.append("\r\nMissingRate=" + lost * 1.0 / b.size());
			writer.newLine();
			writer.append("Ghost=" + ghost);
			writer.newLine();
			writer.append("Failure=" + hf);

			writer.newLine();
			writer.close();
			result.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getMapFromFile(String file) {
		Map<String, Object> map = new HashMap<String, Object>();
		BufferedReader br = null;
		try {
			String line = null;
			String key = null;
			String value = null;
			br = new BufferedReader(new FileReader(new File(file)));
			while (null != (line = br.readLine())) {
				key = line.split(" ")[0];
				value = line.split(" ")[1];
				map.put(key, value);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				} finally {
					br = null;
				}
			}
		}
		return map;
	}
}

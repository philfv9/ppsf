package algorithms.PPUM.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import algorithms.PPUM.tool.miningalgo.AlgoHUIMiner;

public class ResultTool {

	public ResultTool() {
		// TODO Auto-generated constructor stub
	}

	public static String transDBformat2HUIMiner(String src, String dst,
			Map<String, Integer> u) {

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
					items += item_value[0] + " ";
					tu += u.get(item_value[0])
							* Double.parseDouble(item_value[1]);
					counts += item_value[1] + " ";
				}
				sb.append(items.substring(0, items.length() - 1) + ":"
						+ (int) tu + ":"
						+ counts.substring(0, counts.length() - 1) + "\r");
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

	public static String getCompare(String before_db, String after_db,
			Map<String, Double> sensitive, int mut, Map<String, Integer> u) {
		StringBuffer sb = new StringBuffer();
		String b_huiPath = transDBformat2HUIMiner(before_db, "src/b_tmp.txt", u);
		String a_huiPath = transDBformat2HUIMiner(after_db, "src/a_tmp.txt", u);
		AlgoHUIMiner beforeAlgoHUIMiner = new AlgoHUIMiner();
		AlgoHUIMiner afterAlgoHUIMiner = new AlgoHUIMiner();
		try {
			beforeAlgoHUIMiner.runAlgorithm(b_huiPath, b_huiPath + "_hui.txt",
					mut);
			afterAlgoHUIMiner.runAlgorithm(a_huiPath, a_huiPath + "_hui.txt",
					mut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double ius = getIUS(b_huiPath + "_hui.txt", a_huiPath + "_hui.txt");
		double dss = getDSS(before_db, after_db);
		sb.append(Name.IUS + "=" + ius + "\n");
		sb.append(Name.DSS + "=" + dss + "\n");
		sb.append(compare( b_huiPath + "_hui.txt",  a_huiPath + "_hui.txt",sensitive.keySet()));
		 
		return sb.toString();
	}

	public static double getIUS(String bString, String aString) {
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

	public static double getDSS(String before, String after) {
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

	public static Map<String, Integer> getBitSetMap(String db, String spliter) {
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

	public static String compare(String before, String after,
			Set<String> sks) {
		StringBuffer sb = new StringBuffer();
		BufferedReader brb = null;
		BufferedReader bra = null;
		BufferedWriter result = null;
		Map<String, Double> b = new HashMap<String, Double>();
		Map<String, Double> a = new HashMap<String, Double>();
		try {
			brb = new BufferedReader(new FileReader(new File(before)));
			bra = new BufferedReader(new FileReader(new File(after)));
			result = new BufferedWriter(new FileWriter(new File("src/mabi.txt")));
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

		 
			result.newLine();
			result.append("Total num is =" + b.size());
			result.newLine();
			// missing rule.
			int lost = 0;
			int hf = 0;
			int ghost = 0;
			for (String key : b.keySet()) {
				if (a.get(key) == null) {
					if (sks.contains(key)) {
//						System.out.println(key + "=" + b.get(key)
//								+ "---->sensitive.");
					} else {
						// System.out
						// .println(key + "=" + b.get(key) + "---->lost");
						result.append(key + "=" + b.get(key) + "---->lost");
						result.newLine();
						lost++;
					}

				} else {
					if (sks.contains(key) && b.get(key) != null) {
//						System.out.println(key + "=" + a.get(key)
//								+ "---->hiding failure");
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
//					System.out.println(key + "=" + a.get(key)
//							+ "---->arificial");
					result.append(key + "=" + a.get(key) + "---->arificial");
					ghost++;
					result.newLine();
				} else {
					// System.out.println(key +"="+a.get(key)+"---->a large");
				}
			}
			result.append("Lost=" + lost);
			sb.append("LOST="+lost+"\n");
			sb.append("MC=" + lost * 1.0 / b.size()+"\n");
			result.append("\r\nMissingRate=" + lost * 1.0 / b.size());
			result.newLine();
			result.append("Ghost=" + ghost);
			result.newLine();
			result.append("Failure=" + hf);

			result.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

}

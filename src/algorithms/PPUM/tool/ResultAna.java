package algorithms.PPUM.tool;

import java.io.BufferedReader;
import java.io.IOException;

public class ResultAna {

	public ResultAna() {
		// TODO Auto-generated constructor stub
		BufferedReader br = FileTool.getReader("src/result.txt");
		RData data = new RData();
		String line = null;
		try {
			while (null != (line = br.readLine())) {
				if (line.contains("--")) {
					continue;
				}
				String[] kv = line.split("=");
				String v = kv[1];
				switch (kv[0]) {
				case Name.DSS:
					data.dss.add(v);
					break;
				case Name.DUS:
					data.dus.add(v);
					break;
				case Name.IUS:
					data.ius.add(v);
					break;
				case Name.MOD_NUMS:
					data.modifiedNums.add(v);
					break;
				case Name.MOD_TRANS:
					data.modifiedTrans.add(v);
					break;
				case Name.TIME:
					data.time.add(v);
					break;
				case "LOST":
					data.lost.add(v);
					break;
				case "MC":
					data.mc.add(v);
					break;
				default:
					break;
				}
				if(data.getSize() == 5) {
					data.addLine();
				}
				if(data.getSize() == 25){
					data.print();
					data.clean();
				}
				
			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ResultAna();
	}
}

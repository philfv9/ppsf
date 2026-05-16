package algorithms.PPUM.tool.miningalgo;

import java.io.IOException;

public class HUI_Test {

	public HUI_Test() {
		
		 try {
			 AlgoHUIMiner algoHUIMiner = new AlgoHUIMiner();
			algoHUIMiner.runAlgorithm("src/mushroom_hui.txt", "src/r.txt", 60000);
			algoHUIMiner.printStats();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new HUI_Test();
	}
}

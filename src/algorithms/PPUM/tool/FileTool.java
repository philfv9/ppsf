package algorithms.PPUM.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileTool {

	public static BufferedReader getReader(String file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(file)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return br;

	}

	public static BufferedWriter getWriter(String dst) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(dst)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bw;
	}

}

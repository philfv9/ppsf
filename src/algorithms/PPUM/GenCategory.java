package algorithms.PPUM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import algorithms.PPUM.tool.FileTool;

public class GenCategory {

	public GenCategory() {
		 BufferedReader br = FileTool.getReader("src/data/retail_u.txt");
		 BufferedWriter bw = FileTool.getWriter("src/data/category_file.txt");
		 
		 String line = null;
		 Random r = new Random();
		 try {
			while(null != (line = br.readLine())) {
				 String[] items = line.split(" ");
				 int category = r.nextInt(200)+1;
				 bw.append(items[0]+" "+category);
				 bw.newLine();
			 }
			br.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GenCategory();
	}
}

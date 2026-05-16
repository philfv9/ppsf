package algorithms.KAnonymity.GSC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Collections;
//import java.util.HashMap;
import java.util.List;


public class ReadData {
	BufferedWriter writer = null;
	List<Data_Struct>  Original_Data=new ArrayList<Data_Struct>();
	ReadData(BufferedWriter wr) throws IOException {
		writer = wr;
	}
	public class Data_Struct implements Comparable<Data_Struct> {
		public char A_code[];
		public int TID;
		
		@Override
	    public int compareTo(Data_Struct ds) {
	        // First sort in binary
	        if ( Compare_array(BinarytoGray(this.A_code),BinarytoGray(ds.A_code))) {
	            return 1;
	        }
	        else{
	            return -1;
	        }
	    }
	}
	/**
	 * 
	 * @author McQueen
	 *
	 */
	public class group implements Comparable<group>{
		Data_Struct dstruct=new Data_Struct();
		int info_loss;
		List<group> lgr;
		@Override
	    public int compareTo(group gr) {
	        // First sort by distance
	        if (this.info_loss>gr.info_loss) {
	            return 1;
	        }
	        else if(this.info_loss==gr.info_loss){
	        	return 0;
	        }
	        else{
	            return -1;
	        }
	    }
	}
	/**
	 * Read the transaction database and its item data set
	 * @param address1 Item data
	 * @param address2 Transaction data
	 * @throws IOException 
	 */
	public void Read(String address1,String address2) throws IOException{
		try{
			List<List<Integer>> Original_DB=new ArrayList<List<Integer>>();
			FileReader item_num=new FileReader(address1);//The number of items
			BufferedReader buf1=new BufferedReader(item_num);
			
			FileReader ori_data=new FileReader(address2); //Original database  
			BufferedReader buf2=new BufferedReader(ori_data);
			String line; int col, row=0;
			//================Count the number of items================
			List<Integer> items=new ArrayList<Integer>();
			while((line=buf1.readLine())!=null){
				String str[]=line.split(", ");
				items.add(Integer.parseInt(str[0]));
			}//Get all the items and their number
			col=items.size();
			//System.out.println(col);
			//================end=================
			
			while((line=buf2.readLine())!=null){
				if(line.trim().length()>0){
					List<Integer> record=new ArrayList<Integer>();
					String str[]=line.split(" ");
					for(int j=0;j<str.length;j++){
						if(!str[j].equals("")){
							int x=Integer.parseInt(str[j]);
							record.add(x);
						}
					}
					Original_DB.add(record);
				}
				row++;
			}//Get the number of rows in the database
			//===============Generate a binary code data set=================
			for(int i=0;i<Original_DB.size();i++){
				Data_Struct dstr=new Data_Struct();
				dstr.A_code=new char[col];
				for(int j=0;j<col;j++){
					if(Original_DB.get(i).contains(items.get(j)))
						dstr.A_code[j]='1';
					else
						dstr.A_code[j]='0';
				}
				//System.out.println(dstr.code);
				dstr.TID=i+1;
				Original_Data.add(dstr);
			}
			//========================end===========================			
		}
		catch(IOException ex) {
	    	System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
	        }
	}
	/**
	 * Convert gray code to binary code
	 * @param g_data
	 * @return 
	 */
	public char[] BinarytoGray(char[] g_data){
		char Binary_Data[]=new char[g_data.length];
		Binary_Data[0]=g_data[0];
		for(int i=1;i<Binary_Data.length;i++){
			if(g_data[i-1]==g_data[i])
				Binary_Data[i]='0';
			else{
				Binary_Data[i]='1';
			}
		}
		return Binary_Data;
	}
	/**
	 * Compare whether array a is greater than b
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean Compare_array(char[] a, char[] b){
		for(int i=0;i<a.length;i++){
			if(a[i]>b[i]){
				return true;
			}
			else if(a[i]<b[i]){
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param k Anonymity
	 * @param b The number of candidate clustering objects
	 * @param Org Sorted original database
	 * @return
	 * @throws IOException 
	 */
	public List<group> get_Groups(int k,int b,List<Data_Struct> Org) throws IOException{
		//Set<Integer> centers= new TreeSet<Integer>();
		List<group> Groups=new ArrayList<group>();
		while(Org != null && Org.size()!=0){
			if(Org.size()>=k){
				int x=(int)(Math.random()*Org.size());
				group gr_data=new group();//Generate new cluster
				gr_data.dstruct=Org.get(x);//New clustering center point data
				//System.out.println(Org.size()+"\t"+x);
				//===================Find the location of 2αk candidate transactions in the original database==================
				int y=0,z=Org.size();
				if(x-b*k<y)
					z=y+2*b*k;
				else if(x+b*k>z-1)
					y=x-2*b*k;
				//============================Get 2αk candidate transactions========================
				//System.out.println(y+"\t"+z);
				List<group> seg_groups=new ArrayList<group>();
				for(int i=y;i<=z;i++){
					if(i>=0&i<Org.size()&i!=x){
						group above=new group();
						above.dstruct=Org.get(i);
						above.info_loss=getDistance(Org.get(x).A_code,Org.get(i).A_code);
						seg_groups.add(above);
						above=null;
					}
				}
				//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
				Collections.sort(seg_groups);//sort
				List<group> lg=seg_groups.subList(0,k-1);//Get the top k-1 most similar transactions
				seg_groups=null;
				gr_data.lgr=lg;
				for(group gr:lg){//Remove classified transactions from the original data set
					gr_data.info_loss+=gr.info_loss;
					Org.remove(gr.dstruct);
				}
				Org.remove(gr_data.dstruct);
				Groups.add(gr_data);
//				System.out.println(Groups.size()+"***\t"+gr_data.dstruct.TID+"\t"+gr_data.info_loss);
				writer.write(Groups.size()+"***\t"+gr_data.dstruct.TID+"\t"+gr_data.info_loss);
				writer.newLine();
				gr_data=null;
				lg=null;
				
			}
			else{
				for(Data_Struct ds:Org){
					//group re_gr=new group();
					//re_gr.dstruct=ds;
					int min=Integer.MAX_VALUE;
					int index=0;
					for(int j=0;j<Groups.size();j++){
						int loss=getDistance(ds.A_code,Groups.get(j).dstruct.A_code);
						if(loss<min){
							min=loss;
							index=j;
						}
					}
					//System.out.println("******"+Org.size());
					group re_gr=new group();
					re_gr.dstruct=ds;
					re_gr.info_loss=min;
					Groups.get(index).lgr.add(re_gr);
					Groups.get(index).info_loss+=min;
					re_gr=null;
				}
				Org=null;
			}
		}
		return Groups;
	}
	/**
	 * Calculate the distance between two arrays, that is, the difference
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public int getDistance(char[] arr1,char[] arr2){
		int count=0;
		for(int i=0;i<arr1.length;i++){
			if(arr1[i]!=arr2[i])
				count++;
		}
		return count;
	}
	
}

package algorithms.KAnonymity.PTA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import algorithms.KAnonymity.PTA.Anonymity.group;


public class ReadData {
	BufferedWriter writer;
	ReadData(BufferedWriter wr) {
		writer = wr;
	}
	public class Data_Struct implements Comparable<Data_Struct> {
		public char A_code[];
		public int TID;
		
		@Override
	    public int compareTo(Data_Struct ds) {
	        // Compare the size between two transaction records (binary size)
	        if ( Compare_array(GraytoBinary(this.A_code),GraytoBinary(ds.A_code))) {
	            return 1;
	        }
	        else{
	            return -1;
	        }
	    }
	}

	List<List<Integer>> Original_DB=new ArrayList<List<Integer>>();
	List<Data_Struct>  Original_Data=new ArrayList<Data_Struct>();
	/**
	 * Reading transaction data and item list
	 * @param address1 Item list address
	 * @param address2 Transaction data address
	 * @return 
	 * @throws IOException 
	 */
	public List<Integer> Read(String address1,String address2) throws IOException{
		try{
			//
			FileReader item_num=new FileReader(address1);//The number of items
			BufferedReader buf1=new BufferedReader(item_num);
			
			FileReader ori_data=new FileReader(address2); ////Raw data set
			BufferedReader buf2=new BufferedReader(ori_data);
			String line; int col, row=0;
			//================get the number of items================
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
			//===============Save the original data as binary code=================
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
			return items;
			//========================end===========================			
		}
		catch(IOException ex) {
	    	System.out.println("Read transaction records failed."+ ex.getMessage());
	        System.exit(1);
	        }
		return null;
	}
	/**
	 * Convert gray code to binary code
	 * @param g_data
	 * @return 
	 */
	public char[] GraytoBinary(char[] g_data){
		char Binary_Data[]=Arrays.copyOf(g_data,g_data.length);
		//Binary_Data[0]=Gray_Data.get(0).Array;
			for(int i=1;i<Binary_Data.length;i++){
				if(Binary_Data[i]=='0')
					Binary_Data[i]=Binary_Data[i-1];
				else{
					Character ch=Binary_Data[i-1];
					ch.toString();
					String str=""+(Integer.parseInt(ch.toString())+1)%2;
					Binary_Data[i]=str.charAt(0);
					//int s=(int) Binary_Data[i-1];
					//Binary_Data[i]=(""+s).charAt(0);
				}
			//Binary_Data[j]=new String(bin);
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
	 * Segment raw data to reduce complexity
	 * @param k Anonymity
	 * @param groups The number of equivalence classes in each fragment
	 * @param ano Anonymity class object
	 * @throws IOException 
	 */
	public void Segment(int k,int groups,Anonymity ano,int trans,double item_num,List<Integer> items) throws IOException{
		int size=k*groups;//The minimum length of each fragment
		int clus=(Original_Data.size())/size;//Fragment number
		
		int seg_leng; //The length of each fragment
		int last;		//Extra length of a fragment after segmentation
		if(clus==0){
			seg_leng=Original_Data.size();
			last=0;
		}
		else{
			seg_leng=(Original_Data.size())/clus;
			last=(Original_Data.size())%clus;
		}
		//System.out.println("seg_leng:"+seg_leng);
		long Total_Time_1=0;
		long Total_Time_2=0;
		int Total_Loss=0;
		for(int i=0;i<clus;i++){
			List<Data_Struct> seg_data=new ArrayList<Data_Struct>();
			//====================Generate a distance matrix for the last fragment================
			if(i==clus-1|clus==0){
				seg_data.addAll(Original_Data.subList((i*seg_leng),(i*seg_leng+seg_leng+last)));
			}
			else{
				seg_data.addAll(Original_Data.subList((i*seg_leng),(i*seg_leng+seg_leng)));
			}
			
			//System.arraycopy(Original_Data,(i*seg_leng+1),seg_data,0,seg_data.size());
			int dis_mat[][]=new int[seg_data.size()+1][seg_data.size()+1];
			for(int j=0;j<seg_data.size();j++){//Generate distance matrix
				dis_mat[0][j+1]=seg_data.get(j).TID;
				dis_mat[j+1][0]=dis_mat[0][j+1];
				dis_mat[j+1][j+1]=Integer.MAX_VALUE;
				for(int m=j+1;m<seg_data.size();m++){
					int dis=ano.getDistance(seg_data.get(j).A_code,seg_data.get(m).A_code);
					///System.out.println(dis_mat[(j)-i*seg_leng][0]+"and"+dis_mat[(m)-i*seg_leng][0]+"the distance between:"+dis);
					dis_mat[j+1][m+1]=dis;
					dis_mat[m+1][j+1]=dis;
				}
			}//end for
			/*for(int j=0;j<dis_mat.length;j++){
				for(int a=0;a<dis_mat[j].length;a++){
					System.out.print(dis_mat[j][a]+"\t");
				}
				System.out.println();
			}*/
			//==================Finding the shortest loop path======================
			long begin_T=System.currentTimeMillis();
			int loop[]=ano.getLoop(dis_mat);
			/*
			for(int a=0;a<loop.length;a++){
				System.out.print(loop[a]+"\t");
			}
			System.out.println("\n");*/
			long end_T=System.currentTimeMillis();
			Total_Time_1+=(end_T-begin_T);
			
			//==================Grouping of transactions to form a cluster=====================
			group[] pre_cluster=ano.get_Groups(k,loop,seg_data);
			List<group> lg=ano.Cluster(k,pre_cluster,seg_data,items);
			long over_T=System.currentTimeMillis();
			Total_Time_2+=(over_T-end_T);
			for(group gr_p:lg){
				Total_Loss+=gr_p.Info_loss;
			}
		}
//		System.out.println("*\tThe time used by the Prim-based method to find the shortest loop path is:"+Total_Time_1+"ms");
//		System.out.println("*\tThe time it takes to form a cluster based on the shortest loop generated is:"+Total_Time_2+"ms");
//		System.out.println("*\tThe total number of information loss caused by anonymization is:"+Total_Loss+" items changed\n");
//		System.out.println("*\tThe information loss ratio is:"+Total_Loss/(item_num*trans));
		writer.write("*\tThe time used by the Prim-based method to find the shortest loop path is:"+Total_Time_1+"ms");
		writer.newLine();
		writer.write("*\tThe time it takes to form a cluster based on the shortest loop generated is:"+Total_Time_2+"ms");
		writer.newLine();
		writer.write("*\tThe total number of information loss caused by anonymization is:"+Total_Loss+" items changed");
		writer.newLine();
		writer.write("*\tThe information loss ratio is:"+Total_Loss/(item_num*trans));
		writer.newLine();
	}
	/**
	 * Calculate the distance between two transactions
	 * @param str1 transaction1的code
	 * @param str2 transaction2的code
	 * @return 
	 */
	public int getDistance(String str1,String str2){
		int size=str1.length();
		int seg=size/31;
		int count=0;
		for(int i=0;i<seg;i++){
			String segstr1=str1.substring(31*i,31*(i+1));
			String segstr2=str2.substring(31*i,31*(i+1));
			int xleng1=Integer.parseInt(segstr1,2);
			int xleng2=Integer.parseInt(segstr2,2);
			int xleng=xleng1^xleng2;
			count=count+Count(xleng);
		}
		if(size%31!=0){
			int xleng1=Integer.parseInt(str1.substring(seg*31),2);
			int xleng2=Integer.parseInt(str2.substring(seg*31),2);
			count=count+Count(xleng1^xleng2);
		}
		return count;
	}
	
	/**
	 * Calculate the number of 1 in a binary number
	 * @param x
	 * @return
	 */
	public int Count(int x)
	{
	    int n;   
	    n = (x >> 1) & 033333333333;   
	    x = x - n;  
	    n = (n >> 1) & 033333333333;  
	    x = x - n;   
	    x = (x + (x >> 3)) & 030707070707;  
	    x =x%63; 
	    return x;  
	}
	
}

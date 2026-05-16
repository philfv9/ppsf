package algorithms.KAnonymity.PTA;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import algorithms.KAnonymity.PTA.ReadData.Data_Struct;

public class Anonymity {
	BufferedWriter writer;
	Anonymity(BufferedWriter wr) {
		writer = wr;
	}
	class group{
		int center;
		List<Integer> Tids=new ArrayList<Integer>();
		char[] data;
		int Info_loss;
	}
	/**
	 * Calculate the distance between two data records
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
	/**
	 * Generate the shortest loop based on the distance matrix
	 * @param dis
	 * @return
	 */
	public int[] getLoop(int [][] dis){
		int best_loop=0;
		int best_dis=Integer.MAX_VALUE;//The length of the shortest loop
		int loops[][]=new int[dis.length-1][dis.length+1];//All loop collections
		for(int i=1;i<dis.length;i++){
			int fb=loops[i-1].length;
			int flag[]=Arrays.copyOf(dis[0],dis[0].length);//Node access flag
			loops[i-1][0]=dis[i][0];//Determine the first node of a loop
			loops[i-1][fb-2]=dis[i][0];//Determine the tail node
			int last_one=i;//
			int index=i;//Remember current node
			boolean tag=true;
			flag[index]=0;//Index marked as visited
			int min_2=Integer.MAX_VALUE;
			int min_1=Integer.MAX_VALUE;
			int index_1=0;
			int index_2=0;
			int x=1,y=fb-3;
			for(int m=1;m<dis.length-1;m++){
				if(flag[index_1]==0||tag){
					min_1=Integer.MAX_VALUE;
					for(int j=1;j<dis.length;j++){//Find the nearest node to the starting point
						if(flag[j]!=0&dis[index][j]<min_1){
							index_1=j;
							min_1=dis[index][j];
						}
					}
					//System.out.println("index_1:"+index_1+"\tmin_1:"+min_1);
				}
				if(m!=1){
					//System.out.println("***************"+m);
					if(flag[index_2]==0|(!tag)){
						min_2=Integer.MAX_VALUE;
						for(int j=1;j<dis.length;j++){//
							if(flag[j]!=0&dis[last_one][j]<min_2){
								index_2=j;
								min_2=dis[last_one][j];
							}
						}
					}
					//System.out.println("index_2:"+index_2+"\tmin_2:"+min_2);
				}
				
				if(min_2<min_1)
					tag=false;
				else
					tag=true;
				if(tag){
					//System.out.println("index_1"+index_1);
					loops[i-1][fb-1]+=min_1;
					loops[i-1][x]=flag[index_1];
					index=index_1;
					//System.out.print(flag[index_1]);
					x++;
					flag[index_1]=0;
				}
				else{
					//System.out.println("index_2"+index_2);
					loops[i-1][fb-1]+=min_2;
					loops[i-1][y]=flag[index_2];
					//System.out.print(flag[index_2]);
					y--;
					flag[index_2]=0;
					last_one=index_2;
				}		
			}
			//System.out.println();
			loops[i-1][fb-1]+=dis[index][last_one];
			/*for(int k=0;k<loops[i-1].length;k++){
				if(k==loops[i-1].length-1)
					System.out.print("-->");
				System.out.print(loops[i-1][k]);
			}*/
			if(loops[i-1][fb-1]<best_dis){
				best_dis=loops[i-1][fb-1];
				best_loop=i-1;
			}
		}
		//System.out.println();
		return loops[best_loop] ;
	}//end getLoop
	/**
	 * 
	 * @param k
	 * @param loop
	 * @param Org
	 * @return
	 */
	public group[] get_Groups(int k,int [] loop,List<Data_Struct> Org){
		Map<Integer,char[]> Pro_data=new HashMap<Integer,char[]>();
		int leng=loop.length-2;
		group[] Groups=new group[leng];
		for(int i=0;i<Org.size();i++){
			Pro_data.put(Org.get(i).TID,Org.get(i).A_code);
		}
		//System.out.println("&*&*&*&*&*&:"+Org.length);
		for(int j=0;j<leng;j++){
			char[][] pre_vote=new char[k][Org.get(0).A_code.length];
			int pre=(k-1)/2;
			int nex=pre+(k-1)%2;
			//Pro_data.get(loop[j]);
			int n=0;
			Groups[j]=new group();
			Groups[j].center=loop[j];
			for(int i=(j-pre);i<=(j+nex);i++){
				if(i<0){
					pre_vote[n]=Pro_data.get(loop[leng+i]);
					Groups[j].Tids.add(loop[leng+i]);
				}
				else if(i>(leng-1)){
					pre_vote[n]=Pro_data.get(loop[i-leng]);
					Groups[j].Tids.add(loop[i-leng]);
				}
				else{
					pre_vote[n]=Pro_data.get(loop[i]);
					Groups[j].Tids.add(loop[i]);
				}
				n++;
			}
			Groups[j].data=Vote(pre_vote);
			//ReadData rd=new ReadData();
			for(char[] vo:pre_vote){
				/*for(int i=0;i<vo.length;i++)
					System.out.print(vo[i]+"  ");
				System.out.print("\t\t");
				for(int i=0;i<Groups[j].data.length;i++)
					System.out.print(Groups[j].data[i]+"  ");*/
				Groups[j].Info_loss+=getDistance(vo,Groups[j].data);
				//System.out.println("\t"+Groups[j].Info_loss);
			}
			//System.out.println("*************");
		}
		/*for(group gro:Groups)
			System.out.println(gro.Tids+"\t"+gro.Info_loss);*/
		return Groups;
	}
	/**
	 * Voting operation determines new center point
	 * @param vote_data Packet data
	 * @return New group center point
	 */
	public char[] Vote(char[][] vote_data){
		int leng=vote_data[0].length;
		int size=vote_data.length;
		char[] result=new char[leng];
		for(int i=0;i<leng;i++){
			int count=0;
			for(int j=0;j<size;j++){
				//String str[]=vote_data[j].split("");
				if(vote_data[j][i]=='1')
					count++;
			}
			//System.out.println("+++++++:"+count+"+++++++");
			//System.out.println(size+"=======:"+(size/2+size%2)+"======");
			if(count>=(size/2+size%2))
				result[i]='1';
			else
				result[i]='0';
		}
		//String res_str=new String(result);
		return result;
	}
	
	public List<group> Cluster(int k,group[] pre_cluster,List<Data_Struct> Seg_Data,List<Integer> items) throws IOException{
		Map<Integer,char[]> Pro_data=new HashMap<Integer,char[]>();
		for(int i=0;i<Seg_Data.size();i++){
			Pro_data.put(Seg_Data.get(i).TID,Seg_Data.get(i).A_code);
		}
		Set<Integer> clus_ids=new TreeSet<Integer>();
		List<group> clusters=new ArrayList<group>();
		List<group> pro_groups=new ArrayList<group>();
		for(group gr:pre_cluster){
			pro_groups.add(gr);
		}
		
		while(pro_groups.size()!=0){
			int index=0;
			int min=Integer.MAX_VALUE;
			//System.out.println("=========:"+pro_groups.size());
			for(int i=0;i<pro_groups.size();i++){
				//System.out.println(pro_groups.get(i).Tids+"\t"+pro_groups.get(i).Info_loss);
				if(pro_groups.get(i).Info_loss<min){
						min=pro_groups.get(i).Info_loss;
						index=i;
					}
			}//end for
			//System.out.println("*****:"+index);
			clusters.add(pro_groups.get(index));
			clus_ids.addAll(pro_groups.get(index).Tids);
			
			//System.out.println(pro_groups.get(index).Tids+"++++:"+pro_groups.size()+"\t"+pro_groups.get(0).Tids);
			List<group> buff_group=new ArrayList<group>();
			for(group gro:pro_groups){
				List<Integer> buff_data=new ArrayList(Arrays.asList(new Object[pro_groups.get(index).Tids.size()]));
				//buff_data.Tids.retainAll(gro.Tids);
				Collections.copy(buff_data,pro_groups.get(index).Tids);
				buff_data.retainAll(gro.Tids);
				if(buff_data.size()!=0){
					buff_group.add(gro);
				}
			}
			/*for(int i=0;i<pro_groups.size();i++){
				System.out.println(pro_groups.get(i).Tids+"\t"+pro_groups.get(i).Info_loss);
			}*/
			//System.out.println("&&&&&&&&&");
			pro_groups.removeAll(buff_group);
		}//end while
		//==============================
		Set<Integer> keyset=Pro_data.keySet();
		//System.out.println(keyset);
		keyset.removeAll(clus_ids);
		for(Integer last_tid:keyset){
			int min=Integer.MAX_VALUE;
			int index=0;
			//ReadData rd=new ReadData();
			for(int i=0;i<clusters.size();i++){
				int dis=getDistance(Pro_data.get(last_tid),clusters.get(i).data);
				if(dis<min){
					min=dis;
					index=i;
				}
			}
			clusters.get(index).Tids.add(last_tid);
			clusters.get(index).Info_loss+=min;
		}
	
		int cnt = 1;
		for(group gro:clusters){
//			System.out.print("Equivalence class"+cnt+"The details are as follows:"+"\n");
			writer.write("Equivalence class "+cnt);
			writer.newLine();
			writer.write("The details are as follows:");
			writer.newLine();
			cnt++;
//			System.out.print("Corresponding equivalent transaction identifier set:"+gro.Tids+"\n");
//			System.out.print("Corresponding binary code: ");
			writer.write("Corresponding equivalent transaction identifier set:"+gro.Tids);
			writer.newLine();
			writer.write("Corresponding binary code:  ");
			writer.newLine();
			for(char da:gro.data){
//				System.out.print(da+"  ");
				writer.write(da+"  ");
			}
//			System.out.print("\nCorresponding collection of anonymous quasi-mark items: ");
			writer.newLine();
			writer.write("Corresponding collection of anonymous quasi-mark items: ");
			writer.newLine();
			for(int index = 0;index<gro.data.length;index++)
			{
				if( gro.data[index] == '1')
//					System.out.print(items.get(index)+" ");
					writer.write(items.get(index)+" ");
			}
//			System.out.println("\n"+"Corresponding loss (hamming distance):"+gro.Info_loss);
//			System.out.print("--------------------------------------------------------------------------------------------------------------------------------------\n");
			writer.newLine();
			writer.write("Corresponding loss (hamming distance):"+gro.Info_loss);
			writer.newLine();
			writer.write("===============================================================================");
			writer.newLine();
		}
		return clusters;
	}
	
	
	
}

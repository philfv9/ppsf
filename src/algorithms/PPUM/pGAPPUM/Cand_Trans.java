package algorithms.PPUM.pGAPPUM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Cand_Trans {
	List<Data_Struct> cand_trans_delete=new ArrayList<Data_Struct>();
	/**
	 * @method Candidate deleted items
	 * @param lds Original database
	 * @param lls Sensitive item data
	 * @param maxdutil
	 * @return Candidate deleted item's corresponding TID
	 */
	public List<Integer> getCand_Trans(List<Data_Struct> lds,List<Set<Integer>> lls,double maxdutil){
		List<Integer> li=new ArrayList<Integer>();
		Data_Struct ds=new Data_Struct();
		cand_trans_delete.add(0,ds);
		for(int i=0;i<lds.size();i++){
			for(Set<Integer> ls:lls){
				if(lds.get(i).transaction.containsAll(ls))
					if(lds.get(i).tu<maxdutil){
						lds.get(i).Tid=i;
						cand_trans_delete.add(lds.get(i));
						li.add(i);
						break;
					}
			}
		}
		return li;
	}
	/**
	 * @method Sort candidate deletions in ascending order
	 * @param lds Candidate deleted items
	 * @param tud The utility value of the entire database
	 * @return Candidate deleted items after sorting
	 */
	public List<Data_Struct> Sort_Cand_Trans(List<Data_Struct> lds,double tud){
		int index;
		double total=0;
		Data_Struct min;
		for(int i=1;i<lds.size();i++){
			index=i;
			total+=tud/(lds.get(i).tu);
			for(int j=i+1;j<lds.size();j++)
				if(lds.get(j).tu<lds.get(index).tu)
					index=j;
			if(index!=i){
				min=lds.get(index);
				lds.set(index,lds.get(i));
				lds.set(i,min);
			}
		}
		for(int j=1;j<lds.size();j++){
			lds.get(j).ratio=(tud/(lds.get(j).tu))/total;
		}
		return lds;//sortedcand
	}
	/**
	 * @method Calculate the maximum number of transactions to delete
	 * @param lds Candidate deleted items
	 * @param max Maximum delete utility value
	 * @return Maximum number of deletions
	 */
	public int getDelete_Size(List<Data_Struct> lds,double max){
		int count=0; double dele_utility=0;
		for(Data_Struct ds:lds){
			dele_utility+=ds.tu;
			if(dele_utility<max)
				count++;
		}
		return count;
	}
}

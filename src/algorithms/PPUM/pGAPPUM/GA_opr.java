package algorithms.PPUM.pGAPPUM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GA_opr {
	double mut_rat; //Mutation rate
	List<Chrome_Node> pop=new ArrayList<Chrome_Node>(); //÷÷»∫
	/**
	 * @method Generation of individuals in the middle age group
	 * @param lcn population
	 * @parem lds Candidate deleted items
	 * @param n The number of individuals that need to be added to the population
	 * @param m The number of genes in each chromesome
	 */
	public List<Chrome_Node> Generation(List<Chrome_Node> lcn,List<Data_Struct> lds,int n,int m){
		for(int i=0;i<n;i++){
			Chrome_Node cn=new Chrome_Node();
			while(cn.chromesome.size()<m){
				int x=((int) (Math.random()*(lds.size()-1)))+1;
				if(!cn.chromesome.contains(x)){
					cn.chromesome.add(lds.get(x).Tid);
					cn.Tuid+=lds.get(x).tu;
				}
			}
			lcn.add(cn);
		}
		return lcn;
	}
	/**
	 * @method Select the first half of the body with the smallest side_effect 
	 * 			in the population, and generate the other 
	 * 			half of the individual with Generation ()
	 * @param lcn population
	 * @return Half of the population selected
	 */
	public List<Chrome_Node> Selection(List<Chrome_Node> lcn){
		//List<Integer> li=new ArrayList<Integer>();
		int index;
		Chrome_Node min;
		List<Chrome_Node> cand_pop=new ArrayList<Chrome_Node>();
		//System.out.println("@@@@@@&&&&&&"+lcn.size());
		for(int i=0;i<lcn.size()/2;i++){
			//if(i<lcn.size()/2){
				index=i;
				for(int j=i+1;j<lcn.size();j++)
					if(lcn.get(j).side_effect<lcn.get(index).side_effect)
						index=j;
				if(index!=i){
					min=lcn.get(index);
					lcn.set(index,lcn.get(i));
					lcn.set(i, min);
					}
				cand_pop.add(lcn.get(i));
			}
			/*else{
				lcn.remove(i);//remove(i);
			}
		}*///end for
		return cand_pop;
	}
	/**
	 * @method Crossover
	 * @param lcn population
	 * @param n The maximum number of transactions to delete / number of genes
	 * @param lint Candidate delete transactionTID
	 * @return
	 */
	public List<Chrome_Node> Crossover(List<Chrome_Node> lcn,int n){
		int x,y,z;
		Set<Integer> s1,s2;
		//List<Chrome_Node> can_pop=new ArrayList<Chrome_Node>(lcn.size());
		Set<Integer> si=new TreeSet<Integer>();
		for(int i=0;i<(pop.size()/2);i++){
			x=(int) (Math.random()*n);
			y=(int) (Math.random()*pop.size());
			z=(int) (Math.random()*pop.size());
			
			//System.out.println(i+" * "+y+" ** "+z);
			if(y==z||si.contains(y)||si.contains(z)){
				i--;
			}
			else{
				si.add(y);si.add(z);
				//System.out.println(pop.size()+"***"+can_pop.size()+"**"+y+"  "+z);
				Chrome_Node chry=new Chrome_Node();
				Chrome_Node chrz=new Chrome_Node();
				s1=new TreeSet<Integer>();
				s2=new TreeSet<Integer>();
				int k=0;
				for(Integer in1:lcn.get(y).chromesome){
					if(k<x)
						s2.add(in1);
					else
						s1.add(in1);
					k++;
				}
				k=0;
				for(Integer in2:lcn.get(z).chromesome){
					if(k<x)
						s1.add(in2);
					else
						s2.add(in2);
					k++;
				}
				/*while(s1.size()<n){
					int a=(int) (Math.random()*(lint.size()));
						s1.add(lint.get(a));
				}
				while(s2.size()<n){
					int b=(int) (Math.random()*(lint.size()));
						s2.add(lint.get(b));
				}*/
				//lcn.set(y,null);
				//lcn.set(z,null);
				chry.chromesome.addAll(s1);
				lcn.set(y,chry);
				chrz.chromesome.addAll(s2);
				lcn.set(z,chrz);
			}
		}
		//lcn=null;
		return lcn;
	}
	/**
	 * @method Mutation
	 * @param lcn population
	 * @param lds Candidate deleted items
	 * @param lint Candidate deleted items TID
	 * @param n Maximum number of deletions
	 * @return Varied population
	 */
	public List<Chrome_Node> Mutation(List<Chrome_Node> lcn,List<Data_Struct> lds,List<Integer> lint,int n){
		int x=(int) (n*mut_rat+1);
		int i=1;
		for(Chrome_Node cn:lcn){
			int y=0;
			while(y<x){
				int a=(int) (Math.random()*n);
				if(a<cn.chromesome.size()){
					int b=(int) (Math.random()*lint.size());
					if(lint.contains(b)){
						cn.chromesome.set(a,b);
					}
					else{
						cn.chromesome.set(a,0);
					}
				}
				y++;
			}
		}
		for(Chrome_Node cn:lcn){//Calculate the uitility value of each chromesome
			for(Integer in:cn.chromesome){
				for(Data_Struct ds:lds){
					if(ds.Tid==in){
						cn.Tuid+=ds.tu;
						break;
						//System.out.println(in+"******"+ds.tu);
					}
				}	
			}
			System.out.println("Chromosome "+i+": "+cn.chromesome+"\r\n"+"Total utility: "+cn.Tuid);
			i++;
		}
		return lcn;
	}
}

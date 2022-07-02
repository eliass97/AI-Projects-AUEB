//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

import java.util.*;

//This class is consisted of 2 arrays
//One array is called sent and it contains every sentence in the Knowledge Base
//The other array is called proven_things and it contains all the things that we've proven so far,
//plus the assumptions that we knew from the start that existed ( for example Missile("M1"))
public class KnowledgeBase {
	public static int limit = 150;
	public ArrayList<Sentence> sent;
	public ArrayList<Unifiable> proven_things;
	
	public KnowledgeBase() {
		sent = new ArrayList<Sentence>();
		proven_things = new ArrayList<Unifiable>();
	}
	
	public KnowledgeBase(HashSet<Sentence> sent, HashSet<Unifiable> proven) {
		this.sent = new ArrayList<Sentence>(sent);
		this.proven_things = new ArrayList<Unifiable>(proven);
	}
	
	public KnowledgeBase(KnowledgeBase KB) {
		this.sent = new ArrayList<Sentence>(KB.getSent());
		this.proven_things = new ArrayList<Unifiable>(KB.getProvenThings());
	}
	
	public ArrayList<Sentence> getSent() {
		return sent;
	}
	
	public void setSent(ArrayList<Sentence> sent) {
		this.sent = sent;
	}
	
	public ArrayList<Unifiable> getProvenThings(){
		return proven_things;
	}
	
	public void add(Sentence s) {
		sent.add(s);
	}
	
	public void addP(Sentence s) {
		proven_things.add(s.getProve());
	}
	
	//This is a divide and conquer algorithm that returns all the combinations of Unifiable objects of a certain size
	//This had to be implemented in order to be used in the forward chaining algorithm
	private static void getSubsets(ArrayList<Unifiable> superSet, int k, int idx, Sentence current, ArrayList<Sentence> solution) {
	    if(current.size() == k) {
	    	Sentence temp = new Sentence(current);
	        solution.add(temp);
	        return;
	    }
	    if(idx == superSet.size()) {
			return;
		}
	    Unifiable x = superSet.get(idx);
	    current.add(x);
	    getSubsets(superSet,k,idx+1,current,solution);
	    current.remove(x);
	    getSubsets(superSet,k,idx+1,current,solution);
	}
	
	//It iterates the Knowledge base and changes the name in every variable
	//It adds a into the variable's names
	public void new_vars(int a) {
		int i;
		for(i=0;i<sent.size();i++) {
			sent.get(i).appendString(a);
		}
		for(i=0;i<proven_things.size();i++) {
			proven_things.get(i).appendString(a);
		}
	}
	
	public static ArrayList<Sentence> getSubsets(ArrayList<Unifiable> superSet, int k) {
	    ArrayList<Sentence> res = new ArrayList<Sentence>();
	    getSubsets(superSet,k,0,new Sentence(),res);
	    return res;
	}
	
	//Checks if the Unifiable uni is renamed
	//By renamed we mean that uni must not have the same type of variables and the same names(on constants and functions) with every other thing on proven_things
	//Because if this happens, it will mean that the object that we are testing is not unique, so we'll be adding a duplicate in the list with absolutely no purpose
	//If uni is a function or relationship it dives in the function or rel recursively and checks it's variables
	//if something in the list is different then there is no problem
	//After we've checked everything and we haven't found something similar, we can be sure that uni is NOT a rename of something else
	public boolean isRenamed(Unifiable uni) {
		int i;
		for(i=0;i<proven_things.size();i++) {
			if(proven_things.get(i).getType().equals(uni.getType()) && uni.getName().equals(proven_things.get(i).getName())) {
				if(uni.getType().equals("Fun")) {
					if(((Function)proven_things.get(i)).getList().isRenamed(((Function)uni).getList())) {
						return true;
					}
				}
				if(uni.getType().equals("Rel")) {
					if(((Relationship)proven_things.get(i)).getList().isRenamed(((Relationship)uni).getList())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public SubstitutionSet forward_chaining(KnowledgeBase KB, Unifiable uni) {
		KnowledgeBase KB2 = new KnowledgeBase(KB);
		ArrayList<Unifiable> n;
		ArrayList<Sentence> n_sen;
		int count=0;
		int i,j;
		do {
			n = new ArrayList<Unifiable>();
			KB2.new_vars(count);
			for(i=0;i<sent.size();i++) {
				//n_sen contains the powerset of a certain size from the proven_things array
				//We use this powerset and unify it with everything in the sent array of the knowledge base
				n_sen = new ArrayList<Sentence>(getSubsets(KB2.proven_things,sent.get(i).size()));
				System.out.println("Powerset->"+n_sen.size());
				for(j=0;j<n_sen.size();j++) {
				    SubstitutionSet theta = new SubstitutionSet();
					System.out.println("--------------------------------");
					System.out.print("Unifying ");
					sent.get(i).print();
					System.out.print("With ");
					n_sen.get(j).print();
					System.out.print("And we get : ");
					theta = sent.get(i).unify(n_sen.get(j),theta);
					if(theta != null) {
						theta.print();
					} else {
						System.out.println("Fail");
					}
					System.out.println("");
					//If theta isn't null, it means that the unification worked
					//This means that our algorithm has produced something (the prove of a fact in the sent array)
					//Once we enter this, it applies the substitution set that it produced into the result of the clause in the i-th position of the array sent
					if(theta != null) {
						Unifiable b_ = theta.applySubSetOnUnif(sent.get(i).getProve());
						System.out.print("After applying the thera,result is -> ");
						b_.print();
						System.out.println("");
						//If what we produced is unique, then we can advance and add this thing in the n arraylist
						if(!KB2.isRenamed(b_)) {
							System.out.println("Now we got into the isRenamed thingie");
							n.add(b_);
							//After we've added b_ in the n arraylist we unify b_ with uni and save the result in the theta_ SubstitutionSet
							//If the set IS NOT null, then that means that we've found a result
							//We return this and we're ok
							SubstitutionSet theta_ = new SubstitutionSet();
							theta_ = b_.unify(uni,theta_);
							System.out.println("Unifying b' and a gives us : ");
							if(theta_ != null) {
								theta_.print();
							} else {
								System.out.print("Fail");
							}
							System.out.println("");
							if(theta_ != null) {
								return theta_;
							}
						}
					}
				}
			}
			KB2.proven_things.addAll(n);
			System.out.println(n.size());
			System.out.println("-------------------------------------------");
			System.out.println("-----------------NextRound-----------------");
			System.out.println("-------------------------------------------");
			System.out.println("");
			count++;
		} while(!n.isEmpty() && count <= limit);
		//It counts every loop that we do
		//if it goes over the limit that means that we've fallen into an endless loop
		//That means that no result can be found since it may be of the type FatherOf(FatherOf(FatherOf(FatherOf(.......))))
		//But that's rare to happen
		//usually the algorithm stops if nothing new is produced and this means that we can't prove what we wanted to prove
		return null;
	}
	
	public void print() {
		int i;
		for(i=0;i<sent.size();i++) {
			sent.get(i).print();
			System.out.println("");
		}
		for(i=0;i<proven_things.size();i++) {
			proven_things.get(i).print();
			System.out.println();
		}
	}
}
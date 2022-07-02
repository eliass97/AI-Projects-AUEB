//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034


import java.util.*;

//A clause contains all the sub clauses that are given f.e. A^B=>C, C^A=>D, D^B=>E
public class CNFClause {
    public Vector<CNFSubClause> subclauses;
	public HashSet<Literal> proven_literals;
	public ArrayList<Literal> agenda;
	
	public CNFClause() {
		subclauses = new Vector<CNFSubClause>();
		proven_literals = new HashSet<Literal>();
	}
	
    public Vector<CNFSubClause> getSubclauses() {
        return subclauses;
    }
	
	public HashSet<Literal> getProvenLiterals() {
        return proven_literals;
    }
	
    public boolean contains(CNFSubClause SC) {
		int i;
        for(i=0;i<subclauses.size();i++) {
			if(subclauses.get(i).getLiterals().equals(SC.getLiterals())) {
                return true;
            }
        }
        return false;
    }
	
	//This function will iterate through our knowledge base, int i can have 2 values
	//The first one is 1, in which it simply checks the KB in order to find out if there's anything already proven
	//This will mainly be used at the begging of the Forward_Chaining method and it will fill our agenda
	//In this case, the literal lit will be ignored 
	//If i is not 1 then we'll iterate the KB in order to find sub clauses that contain lit and update them
	//Also, we won't add anything to the proven_literals list because this will be done in the Forward_Chaining function
	public void iterate_list(int i, Literal lit) {
		//If the sub clause's literals hash set size is 0, then that means that what we want to prove is proven automatically
		//This means that we can add it to our agenda
		//We wont check if the literal that we'll get is already in the proven_literals table cause we'll only use this if in the 1st line of the code
		//This means that the proven_literals table will be empty
		CNFSubClause SC;
		if(i==1) {
			Iterator<CNFSubClause> it = subclauses.iterator();
			while(it.hasNext()) {
				SC = it.next();
				if(SC.getLiterals().size()==0) {
					SC.setProven();
					agenda.add(SC.getResult());
				}
			}
		} else {
			//On the first iteration it will update any sub clause that contains lit
			//If it contains lit then it will be added in the proven_literals hashSet and then...
			Iterator<CNFSubClause> it = subclauses.iterator();
			while(it.hasNext()) {
				SC = it.next();
				SC.check_if_exists(lit);
			}
			//...on the second iteration we'll check the list in order to see if anything's been proven
			//If it has we'll add it to our agenda, as long as it's not contained in the proven_literals hash set of this class
			it = subclauses.iterator();
			while(it.hasNext()) {
				SC = it.next();
				if(SC.getProven() && (!proven_literals.contains(SC.getResult()))) {
					agenda.add(SC.getResult());
				}
			}
		}
	}
	
	//We search through the CNFClause KB in order to see if literal x is proven
	public boolean Forward_Chaining(CNFClause KB, Literal lit) {
		 agenda = new ArrayList<Literal>();
		 this.iterate_list(1,new Literal());
		 while(agenda.size()!=0) {
			 Literal x = agenda.get(0);
			 agenda.remove(0);
			 if(!proven_literals.contains(x)) {
				 proven_literals.add(x);
				 this.iterate_list(2,x);
			 }
			 if(proven_literals.contains(lit)) return true;
		 }
		 return false;
	}
	
	public void print() {
		int i;
		for(i=0;i<subclauses.size();i++) {
			subclauses.get(i).print();
			if(i<subclauses.size()-1) {
				System.out.print(" / ");
			}
		}
	}
}
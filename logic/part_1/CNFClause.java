//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034


import java.util.Vector;

//Clause constructor f.e. ( NOT P2 ) AND ( P1 OR NOT P2 OR NOT P3 ) AND ( NOT P1 OR P2 ) AND ( P2 OR P3 )
public class CNFClause {
	//A vector that contains all the sub clauses of this clause
    public Vector<CNFSubClause> clauses = new Vector<CNFSubClause>();
	
    public Vector<CNFSubClause> getSubclauses() {
        return clauses;
    }
	
	//Check if it contains a specific sub clause by comparing all of its sub clauses with it
    public boolean contains(CNFSubClause newS) {
		int i;
        for(i=0;i<clauses.size();i++) {
            if(clauses.get(i).getLiterals().equals(newS.getLiterals())) {
                return true;
            }
        }
        return false;
    }
	
	//Print each sub clause of this clause
	public void print() {
		int i;
		for(i=0;i<clauses.size();i++) {
			clauses.get(i).print();
			if(i<clauses.size()-1) {
				System.out.print(" AND ");
			}
		}
	}
}
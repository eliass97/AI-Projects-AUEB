//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034


import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

//Sub Clause constructor f.e. ( P1 OR NOT P3 )
public class CNFSubClause implements Comparable<CNFSubClause> {
	//A Hashset that contains all of its literals
	//The hashset removes the dublicates of the same literal since they make no difference in CNF
    private HashSet<Literal> literals;
	
    public CNFSubClause() {
        literals = new HashSet<Literal>();
    }
	
    public HashSet<Literal> getLiterals() {
        return literals;
    }
	
    public Iterator<Literal> getLiteralsList() {
        return literals.iterator();
    }
	
    public boolean isEmpty() {
        return literals.isEmpty();
    }
	
    public static Vector<CNFSubClause> resolution(CNFSubClause SC1, CNFSubClause SC2) {
        //In the vector we will save all the sub clauses that will be created
		Vector<CNFSubClause> newSubClauses = new Vector<CNFSubClause>();
        Iterator<Literal> iter = SC1.getLiteralsList();
        //The iterator goes through all Literals of the first clause
        while(iter.hasNext()) {            
            Literal lit1 = iter.next(); //Save the literal
            Literal lit2 = new Literal(lit1.getName(), !lit1.getNegation()); //Create it's negation
            //Check if the other sub clause contains that negation
            if(SC2.getLiterals().contains(lit2)) {
				//If yes then we must construct a new temp sub clause that contains all the literals of both sub clauses
                CNFSubClause tempSC = new CNFSubClause();
                //...except the pair of the literals that were a negation of one another
				//So we create new hashsets for both sub clauses that contain all starting literals...
                HashSet<Literal> SC1_lits = new HashSet(SC1.getLiterals());
                HashSet<Literal> SC2_lits = new HashSet(SC2.getLiterals());
				//...and then we remove those 2 that are negation one another
                SC1_lits.remove(lit1);
                SC2_lits.remove(lit2);
				//We remaining literals in the tempSC (hashset will remove dublicates)
                tempSC.getLiterals().addAll(SC1_lits);
                tempSC.getLiterals().addAll(SC2_lits);
				//And we add it to the vector of all the sub clauses we have created so far
                newSubClauses.add(tempSC);
            }
        }
        return newSubClauses;
    }
	
	//Check if this sub clause is equal to another one by comparing the literals they have
    public boolean equals(Object obj) {
        CNFSubClause clause = (CNFSubClause)obj;
        Iterator<Literal> iter = clause.getLiteralsList();
        while(iter.hasNext()) {
            Literal lit = iter.next();
            if(!this.getLiterals().contains(lit)) {
				return false;
			}
        }
        if(clause.getLiterals().size() != this.getLiterals().size()) {
			return false;
		}
        return true;
    }
	
    public int hashCode() {
        Iterator<Literal> iter = this.getLiteralsList();
        int code = 0;
        while(iter.hasNext()) {
            Literal lit = iter.next();
            code = code + lit.hashCode();
        }
        return code;
    }
	
    public int compareTo(CNFSubClause clause) {
        int cmp = 0;
        Iterator<Literal> iter = clause.getLiteralsList();
        while(iter.hasNext()) {
            Literal lit = iter.next();
            Iterator<Literal> iter2 = this.getLiterals().iterator();
            while(iter2.hasNext()) {                
                Literal lit2 = iter2.next();
                cmp = cmp + lit.compareTo(lit2);
            }
        }
        return cmp;
    }
	
	//Print each literal of this sub clause
	public void print() {
        Iterator<Literal> iter = this.getLiteralsList();
		System.out.print("(");
        while(iter.hasNext()) {
            Literal lit = iter.next();
            lit.print();
			if(iter.hasNext()) {
				System.out.print(" OR ");
			}
        }
		System.out.print(")");
    }
}
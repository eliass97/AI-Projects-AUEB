//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034


import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

//They are statements such as A^B=>C
public class CNFSubClause implements Comparable<CNFSubClause> {
    private HashSet<Literal> literals;
	private HashSet<Literal> proven_literals;
	private Literal result;
	boolean proven;
	
    public CNFSubClause() {
        literals = new HashSet<Literal>();
		proven_literals = new HashSet<Literal>();
		result = null;
		proven = false;
    }
	
    public  HashSet<Literal> getLiterals() {
		return literals;
    }
	
	public Literal getResult() {
		return result;
	}
		
	public void setResult(Literal result) {
		this.result = result;
	}
	
	public void setProven() {
		proven = true;
	}
	
	public boolean getProven() {
		return proven;
	}
	
	public void check_if_exists(Literal lit) {
		if(this.getLiterals().contains(lit) && (!this.getProvenLiterals().contains(lit))) {
			this.getProvenLiterals().add(lit);
			proven = check_proven();
		}
	}
	
	public boolean check_proven() {
		if(this.getLiterals().size() == this.getProvenLiterals().size() || this.getLiterals().size() == 0) {
			proven = true;
			return true;
		}
		return false;
	}
	
    public  HashSet<Literal> getProvenLiterals() {
        return proven_literals;
    }
	
    public Iterator<Literal> getLiteralsList() {
        return literals.iterator();
    }
	
    public boolean isEmpty() {
        return literals.isEmpty();
    }
	
    public void print() {
        Iterator<Literal> iter = this.getLiteralsList();
		System.out.print("(");
        while(iter.hasNext()) {
            Literal lit = iter.next();
            lit.print();
			if(iter.hasNext()) {
				System.out.print(" AND ");
			}
        }
		if(result != null) {
			System.out.print(" => ");
			result.print();
		}
		System.out.print(")");
    }
	
    public boolean equals(Object obj) {
        CNFSubClause SC = (CNFSubClause)obj;
        Iterator<Literal> iter = SC.getLiteralsList();
        while(iter.hasNext()) {
            Literal lit = iter.next();
            if(!this.getLiterals().contains(lit)) {
				return false;
			}
        }
        if(SC.getLiterals().size() != this.getLiterals().size()) {
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
	
    public int compareTo(CNFSubClause x) {
        int cmp = 0;
        Iterator<Literal> iter = x.getLiteralsList();
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
}
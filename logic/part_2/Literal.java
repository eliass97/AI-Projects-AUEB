//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034


//Literal constructor f.e. A, NOT B, C
public class Literal implements Comparable<Literal> {
	
    //The name of the literal and its negation (true = negative / false = positive)
	private String name;
    private boolean negation;
    
	public Literal(){} //Dummy constructor
	
    public Literal(String name, boolean negation) {
        this.name = name;
        this.negation = negation;
    }
	
    public void setName(String name) {
        this.name = name;
    }
	
    public String getName() {
        return this.name;
    }
    
    public void setNegation(boolean negation) {
        this.negation = negation;
    }
	
    public boolean getNegation() {
        return this.negation;
    }
	
	//Check if this literal is equal to another one
    public boolean equals(Object obj) {
        Literal lit = (Literal)obj;
        if(lit.getName().compareTo(this.name) == 0 && lit.getNegation() == this.negation) {
            return true;
        } else {
            return false;
        }
    }
	
    public int hashCode() {
        if(this.negation) {
            return this.name.hashCode() + 1;
        } else {
            return this.name.hashCode() + 0;                        
        }
    }
	
    public int compareTo(Literal lit) {
        int a = 0;
        int b = 0;   
        if(lit.getNegation()) {
            a = 1;
		}
        if(this.getNegation()) {
            b = 1;
		}
        return lit.getName().compareTo(name) + a - b;
    }
	
	//Print the literal with it's negation at front
	public void print() {
        if(negation) {
            System.out.print("NOT "+name);
        } else {
            System.out.print(name);
		}
	}
}
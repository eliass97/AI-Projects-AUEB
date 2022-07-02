//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

import java.util.*;

//The sentence is consisted of an arraylist of unifiable objects and a Unifiable object called prove
//The array list contains all the parts of sentence (left of =>)
//The prove is the thing that is right of the =>
//Usually prove starts as a null object, and we change it afterwards as we read from the input txt file, unless we copy another sentence object
public class Sentence {
    public ArrayList<Unifiable> parts;
    public Unifiable prove;
    
    public Sentence(){
		parts = new ArrayList<Unifiable>();
		prove = null;
	}
    
    public Sentence(Sentence sen) {
		parts = new ArrayList<Unifiable>(sen.getParts());
    	Unifiable uni = sen.getProve();
		Unifiable uni2;
		if(uni != null) {
			if(uni.getType().equals("Rel")) {
				uni2 = new Relationship((Relationship)uni);
			} else if(uni.getType().equals("Fun")) {
				uni2 = new Function((Function)uni);
			} else if(uni.getType().equals("Con")) {
				uni2 = new Constant((Constant)uni);
			} else {
				uni2 = new Variable((Variable)uni);
			}
		    prove = uni2;
		} else {
			uni2 = null;
		}
	}
    
    public Sentence(ArrayList<Unifiable> part) {
		parts = new ArrayList<Unifiable>(part);
		prove = null;
	}
    
    public ArrayList<Unifiable> getParts() {
		return parts;
	}
	
	public int size() {
		return parts.size();
	}
    
    public void add(Unifiable uni) {
		parts.add(uni);
	}
	
	//Removes the object uni that's in position index
	//Of course if index = -1 it removes nothing
	public void remove(Unifiable uni) {
    	int index = parts.indexOf(uni);
    	if(index != -1) {
			parts.remove(index);
		}
    }
    
	public Unifiable getProve() {
		return prove;
	}
    
	//Adds the int to the names of all the variables in the Sentence
    //If a part of it has a function, then it applies the int to the function's variables too
	public void appendString(int a) {
		int i;
		for(i=0;i<parts.size();i++) {
			parts.get(i).appendString(a);
		}
		if(prove != null) {
			prove.appendString(a);
		}
	}
	
	public SubstitutionSet unify(Sentence sen, SubstitutionSet sub) {
		if(this.parts.size() != sen.parts.size()) {
			return null;
		}
		boolean checked;
		int i,j;
		for(i=0;i<this.parts.size();i++) {
			checked = false;
			String i_type = this.parts.get(i).getType();
			String i_name = this.parts.get(i).getName();
			for(j=0;j<sen.parts.size();j++) {
				String j_type = sen.parts.get(j).getType();
				String j_name = sen.parts.get(j).getName();
				if(j_type.equals(i_type) && j_name.equals(i_name)) {
					checked = true;
					sub = this.parts.get(i).unify(sen.parts.get(j),sub);
					if(sub == null) {
						return null;
					}
				}
			}
			if(!checked) {
				return null;
			}
		}
		return sub;
	}
	
	//Deep copies uni and assings it to the prove variable
    public void setProve(Unifiable uni) {
		Unifiable temp;
		if(uni.getType().equals("Rel")) {
			temp = new Relationship((Relationship)uni);
		} else if(uni.getType().equals("Fun")) {
			temp = new Function((Function)uni);
		} else if(uni.getType().equals("Con")) {
			temp = new Constant((Constant)uni);
		} else {
			temp = new Variable((Variable)uni);
		}
		prove = temp;
	}
    
    public void print() {
		int i;
    	for(i=0;i<parts.size();i++) {
    		parts.get(i).print();
			if(i != parts.size()-1) {
				System.out.print(" AND ");
			}
    	}
		if(prove != null && !parts.get(0).isSame(prove)) {
			System.out.print(" THEN ");
			prove.print();
		}
    }
	
	//This function applies the subset's bindings to the sentence
    //It itterates the sentence and checks if the object in the i position of the parts array is a variable
	//Then it creates a deep copy of the thing that is bound to it, and assings it to this position
    //Else if it's a function or a relationship it dives into it in order to ensure that all the variables are substituted recursively
	//It ensures that everything in the substitution set is applied
    public void applySubSet(SubstitutionSet sub) {
		int i;
    	for(i=0;i<parts.size();i++) {
    		if(parts.get(i).getType().equals("Var") && sub.isBound((Variable)parts.get(i))) {
    			Unifiable uni = sub.getBinding((Variable)(parts.get(i)));
    			Unifiable temp = null;
    			if(uni.getType().equals("Fun")) {
					temp = new Function((Function)uni);
				}
    			if(uni.getType().equals("Con")) {
					temp = new Constant((Constant)uni);
				}
    			if(uni.getType().equals("Rel")) {
					temp = new Relationship((Relationship)uni);
				}
    			if(uni.getType().equals("Var")) {
					temp = new Variable((Variable)uni);
				}
    			parts.set(i,temp);
    		} else if(parts.get(i).getType().equals("Fun")) {
    			((Function)parts.get(i)).getList().applySubSet(sub);
    		} else if(parts.get(i).getType().equals("Rel")) {
    			((Relationship)parts.get(i)).getList().applySubSet(sub);
    		}
    	}
    }
}
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

import java.util.*;

//This is a class that contains all the binding sets of a certain unification atttempt
//It consists of the bindings array which is the theta result of 2 sentences-Unifiable objects getting unified
public class SubstitutionSet {
	private ArrayList<Binding> bindings;
	
	public SubstitutionSet() {
		bindings = new ArrayList<Binding>();
	}
	
	public SubstitutionSet(SubstitutionSet sub) {
		this.bindings = new ArrayList<Binding>(sub.bindings);
	}
	
	public void clear() {
		bindings.clear();
	}
	
	public ArrayList<Binding> getBindingsArray() {
		return bindings;
	}
	
	public Binding get(int i) {
		return bindings.get(i);
	}
	
	public int size() {
		return bindings.size();
	}
	
	//Checks if the variable of the bin Binding object is contained in the array
	//If it is then it doesn't do anything (on the 1st loop)
	//We can't have 1 variable attached to many Unifiable objects but we can have 1 Unifiable object that is attached to many variables
	public void add(Binding bin) {
		int i;
		for(i=0;i<bindings.size();i++) {
			if(bin.getVar().getName().equals(bindings.get(i).getVar().getName())) {
				return;
			}
		}
		bindings.add(bin);
	}
	
	public void appendString(int count) {
		int i;
		for(i=0;i<bindings.size();i++) {
			if(bindings.get(i).getVar().getType().equals("Var")) {
				bindings.get(i).getVar().appendString(count);
			}
		}
		for(i=0;i<bindings.size();i++) {
			if(bindings.get(i).getUni().getType().equals("Var")) {
				bindings.get(i).getUni().appendString(count);
			} else if(bindings.get(i).getUni().getType().equals("Fun")) {
				((Function)bindings.get(i).getUni()).getList().appendString(count);
			} else if(bindings.get(i).getUni().getType().equals("Rel")) {
				((Relationship)bindings.get(i).getUni()).getList().appendString(count);
			}
		}
	}
	
	public boolean change(Variable var, Unifiable uni) {
		int i;
		for(i=0;i<bindings.size();i++) {
			if(bindings.get(i).getVar().getName().equals(var.getName())) {
				bindings.set(i,new Binding(var,uni));
				return true;
			}
		}
		return false;
	}
	
	public Unifiable getBinding(Variable var) {
		int i;
		boolean found_something=false;
		Unifiable ret=null;
		for(i=0;i<bindings.size();i++) {
			if(bindings.get(i).getVar().getName().equals(var.getName())) {
				found_something=true;
				ret=bindings.get(i).getUni();
				if(bindings.get(i).getUni().getType().equals("Var")) return getBinding(((Variable)bindings.get(i).getUni()));
				else return bindings.get(i).getUni();
			}
		}
		if(!found_something)return null;
		else return ret;
	}
	
	public boolean isBound(Variable var) {
		int i;
		for(i=0;i<bindings.size();i++) {
			if(bindings.get(i).getVar().getName().equals(var.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public int getSize(){
		return bindings.size();
	}
	
	//Applies what is contained on this subset on a Unifiable Object
	//It ensures that it will dive into functions etc in order to change every variable that has to be changed
	public Unifiable applySubSetOnUnif(Unifiable uni) {
    	Unifiable temp = uni;
    	if(uni == null) {
			System.out.println("a null!");
		}
    	if(uni.getType().equals("Var")) {
    		if(this.isBound((Variable)uni)) {
    			Unifiable pre_temp = this.getBinding((Variable)uni);
    			if(pre_temp.getType().equals("Var")) {
    				temp = new Variable((Variable)this.getBinding((Variable)uni));
    			}
				if(pre_temp.getType().equals("Con")) {
    				temp = new Constant((Constant)this.getBinding((Variable)uni));
				}
    			if(pre_temp.getType().equals("Fun")) {
    				temp = new Function((Function)this.getBinding((Variable)uni));
    			}
    			if(pre_temp.getType().equals("Rel")) {
    				temp = new Relationship((Relationship)this.getBinding((Variable)uni));
    			}
    		}
    	}
    	if(uni.getType().equals("Rel")) {
    		temp = new Relationship((Relationship)uni);
    		((Relationship)temp).getList().applySubSet(this);
    	}
    	if(uni.getType().equals("Fun")) {
    		temp = new Function((Function)uni);
    		((Function)temp).getList().applySubSet(this);
    	}
    	if(uni.getType().equals("Con")) {
			return uni;
		}
    	return temp;
    }
	
	public void print() {
		if(bindings.size() == 0) {
			return;
		} else {
			System.out.print("{ ");
			int i;
		    for(i=0;i<bindings.size();i++) {
			    if(i!=0) {
					System.out.print(" , ");
				}
				bindings.get(i).print();
			}
			System.out.print(" }");
			System.out.println();
		}
	}
}
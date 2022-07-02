//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

public class Variable implements Unifiable { //Variable constructor
	public String name;
	public String type;
	public String base_name;
	
	public Variable(String name) {
		this.name = name;
		type = "Var";
		base_name = name;
	}
	
	public Variable(Variable var) {
		this.name = var.name;
		this.type = var.type;
		this.base_name = name;
	}
	
	public boolean isSame(Unifiable uni) {
		//If it has the same type or name then return true else they are not the same
		if(!type.equals(uni.getType())) {
			return false;
		} else if(name.equals(uni.getName())) {
			return true;
		} else {
			return false;
		}
	}
	
	public SubstitutionSet unify_var(Unifiable uni, SubstitutionSet sub) {
		if(sub.isBound(this)) {
			return (sub.getBinding(this)).unify(uni,sub);
		} else if(uni.getType().equals("Var") && sub.isBound((Variable)uni)) {
			return this.unify(sub.getBinding((Variable)uni),sub);
		} else if(uni.getType().equals("Fun") && ((Function)uni).contains(this)) {
			return null;
		} else if(uni.getType().equals("Rel") && ((Relationship)uni).contains(this)) {
			return null;
		} else {
			sub.add(new Binding(this,uni));
			return sub;
		}
	}
	
	public SubstitutionSet unify(Unifiable uni, SubstitutionSet sub) {
		if(sub == null) {
			return null;
		} else if(this.type.equals(uni.getType()) && this.name.equals(uni.getName())) {
			return sub;
		} else {
			return this.unify_var(uni,sub);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void appendString(int a) {
		name = base_name + Integer.toString(a);
	}
	
	public void print() {
		System.out.print(name);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
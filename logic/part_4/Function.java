//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

public class Function implements Unifiable {
	public String name;
	public String type;
	public List list;
	
	public Function(String name, List list) {
		this.name = name;
		type = "Fun";
		this.list = new List(list);
	}
	
	public Function(String name) {
		this.name = name;
		type = "Fun";
		list = new List(name);
	}
	
	public Function(Function f) {
		this.name = f.name;
		this.type = "Fun";
		this.list = new List(f.list);
	}
	
	public SubstitutionSet unify(Unifiable uni, SubstitutionSet sub) {
		if(sub == null) {
			return null;
		} else if(type.equals(uni.getType()) && name.equals(uni.getName()) && this.isSame(uni)) {
			return sub;
		} else if(uni.getType().equals("Var")) {
			return ((Variable)uni).unify_var(this,sub);
		} else if(type.equals(uni.getType()) && type.equals("Fun")) {
			Constant a = new Constant(this.name);
			Constant b = new Constant(uni.getName());
			return list.unify(((Function)uni).getList(),a.unify(b,sub));
		}
		else return null;
	}
	
	//Adds a Unifiable argument on the list, but first it makes sure that this argument is deep-copied
	//We do that by checking it's type first and creating the right object for it's size
	public void add(Unifiable uni) {
		Unifiable temp;
		if(uni.getType().equals("Rel")) temp = new Relationship((Relationship)uni);
		else if(uni.getType().equals("Fun")) temp = new Function((Function)uni);
		else if(uni.getType().equals("Con")) temp = new Constant((Constant)uni);
		else temp = new Variable((Variable)uni);
		list.getList().add(temp);
	}
	
	public boolean isRenamed(Unifiable uni) {
		if(!uni.getName().equals(this.name)) {
			return false;
		}
		if(!uni.getType().equals(this.type)) {
			return false;
		}
		if(this.type.equals("Fun")&&this.name.equals(uni.getName())) {
			if(((Function)uni).getList().isRenamed(this.getList())) return true;
			else return false;
		}
		return true;
	}
	
	public void print() {
		System.out.print(name);
		list.print();
	}
	
	//Appends the int into the function's list
	public void appendString(int a) {
		list.appendString(a);
	}
	
	//This method checks the Unifiable uni's type and name
	//If they're the same it checks the lists of both, after converting uni to a function.
	//If their lists are the same it returns true
	public boolean isSame(Unifiable uni) {
		if(!uni.getType().equals("Fun")) {
			return false;
		}
		if(list.isSame(((Function)uni).getList())) {
			return true;
		} else {
			return false;
		}
	}
	
	//Returns true if it contains the Unifiable uni
	public boolean contains(Unifiable uni) {
		return list.getList().indexOf(uni) != -1;
	}
	
	public String getName() {
		return name;
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
	
	public List getList() {
		return list;
	}
	
	public void setList(List list) {
		this.list = list;
	}
}
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

public class Relationship implements Unifiable{

	public String type;
	public String name;
	public List list;
	
	public Relationship(String name, List list) {
		this.name = name;
		type = "Rel";
		this.list = new List(list);
	}
	
	public Relationship(String name) {
		this.name=name;
		type="Rel";
		this.list=new List(name);
	}
	
	public Relationship(Relationship r) {
		this.name=r.name;
		this.type="Rel";
		this.list=new List(r.list);
	}
	
	public SubstitutionSet unify(Unifiable uni, SubstitutionSet sub) {
		if(sub == null) {
			return null;
		} else if(type.equals(uni.getType()) && name.equals(uni.getName()) && this.isSame(uni)) {
			return sub;
		} else if(uni.getType().equals("Var")) {
			return ((Variable)uni).unify_var(this,sub);
		} else if(type.equals(uni.getType()) && type.equals("Rel")) {
			Constant a = new Constant(this.name);
			Constant b = new Constant(uni.getName());
			return list.unify(((Relationship)uni).getList(),a.unify(b,sub));
		}
		else return null;
	}
	
	public boolean contains(Unifiable uni) {
		return (list.getList().indexOf(uni) != -1);
	}
	
	public boolean isRenamed(Unifiable uni) {
		if(!uni.getName().equals(this.name)) {
			return false;
		}
		if(!uni.getType().equals(this.type)) {
			return false;
		}
		if(this.type.equals("Rel") && this.name.equals(uni.getName())) {
			if(((Relationship)uni).getList().isRenamed(this.getList())) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public void print() {
		System.out.print(name);
		list.print();
	}
	
	public void appendString(int a) {
		list.appendString(a);
	}
	
	//Adds a deep copy of the object uni on the Relationship's list
	public void add(Unifiable uni) {
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
		list.getList().add(temp);
	}
	
	//Checks if the types of this and uni are the same
	//After that if their lists are the same it returns true
	//We check the names before calling this func, so theres no need to check it in there
	public boolean isSame(Unifiable uni) {
		if(!uni.getType().equals("Rel")) {
			return false;
		}
		if(list.isSame(((Relationship)uni).getList())) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List getList() {
		return list;
	}
	
	public void setList(List list) {
		this.list = list;
	}
}
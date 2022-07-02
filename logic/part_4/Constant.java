//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

//This class represents a constant in FOL
//It has 2 arguments, name and type.Name is the variable's name
//We only initialize the name because the type will always be "Con" since this is a Constant object
public class Constant implements Unifiable{
	public String name;
	public String type;
	
	public Constant(String name) {
		this.name = name;
		type = "Con";
	}
	
	public Constant(Constant con) {
		this.name = con.name;
		type = "Con";
	}
	
	//This method checks if the name-type of this variable is equal to another one
	public boolean isSame(Unifiable uni) {
		if(!type.equals(uni.getType())) {
			return false;
		} else if(name.equals(uni.getName())) {
			return true;
		} else {
			return false;
		}
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
	
	//AppendString is used in order to change the variable's name and add a number to it
	//Since this is a constant it won't do anything, it just needed to exist in order to help us avoid errors
	public void appendString(int a) {}
	
	public void print(){
		System.out.print(name);
	}
	
	public SubstitutionSet unify(Unifiable uni, SubstitutionSet sub) {
		if(sub == null) {
			return null;
		}
		if(getType().equals(uni.getType()) && getName().equals(uni.getName())) {
			return sub;
		} else if(uni.getType().equals("Var")) {
			return ((Variable)uni).unify_var(this,sub);
		} else {
			return null;
		}
	}
}
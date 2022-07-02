//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

//This class is used in order to represent a binding instance
//Var is the variable that is bound to something
//Uni is the thing that is bound to var
public class Binding {
	public Variable var;
	public Unifiable uni;
	
	//This is the constructor
	//We make sure that since it's a copy constructor everything will be deep-copied
	//Otherwise it would cause many problems to our code
	public Binding(Variable var, Unifiable uni) {
		this.var = new Variable(var);
		if(uni.getType().equals("Rel")) {
			this.uni = new Relationship((Relationship)uni);
		} else if(uni.getType().equals("Fun")) {
			this.uni = new Function((Function)uni);
		} else if(uni.getType().equals("Con")) {
			this.uni = new Constant((Constant)uni);
		} else if(uni.getType().equals("Var")) {
			this.uni = new Variable((Variable)uni);
		}
	}
	
	//Prints the bindings
	public void print() {
		System.out.print(var.getName() + "/" + uni.getName());
	}
	
	//Getters and setters
	public Variable getVar() {
		return var;
	}
	
	public void setVar(Variable var) {
		this.var = new Variable(var);
	}
	
	public Unifiable getUni() {
		return uni;
	}
}
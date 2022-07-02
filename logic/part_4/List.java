//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

import java.util.ArrayList;

public class List implements Unifiable {
	public ArrayList<Unifiable> list;
	public String name;
	public String type;
	
	public List(String name) {
		this.name = name;
		type = "Lis";
		list = new ArrayList<Unifiable>();
	}
	
	public List(String name,ArrayList<Unifiable> list) {
		this.list = new ArrayList<Unifiable>(list);
		this.name = name;
		this.type = "Lis";
	}
	
	public List(List l) {
		this.list = new ArrayList<Unifiable>(l.list);
		this.name = l.name;
		this.type = "Lis";
	}
	
	public ArrayList<Unifiable> getList() {
		return list;
	}
	
	public void setList(ArrayList<Unifiable> list) {
		this.list = list;
	}
	
	public void set(int i,Unifiable uni) {
		list.set(i,uni);
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
	
	public Unifiable get(int i) {
		return list.get(i);
	}
	
	//Checks the list with object uni
	//Object uni is a list if there is at least one element on the same position of the list that's different
	//If everything is ok it returns true
	public boolean isSame(Unifiable uni) {
		if(!name.equals(uni.getName())) {
			return false;
		}
		if(list.size()!=((List)uni).getList().size()) {
			return false;
		}
		int i;
		for(i=0;i<list.size();i++) {
			if(!list.get(i).isSame(((List)uni).getList().get(i))) {
				return false;
			}
		}
		return true;
	}
	
	//Adds uni on the list, and by checking it's type it creates a new deep-copied object and adds it on the list
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
		list.add(temp);
	}
	
	public void print() {
		System.out.print("(");
		int i;
		for(i=0;i<list.size();i++) {
			if(i!=0)System.out.print(",");
			System.out.print( list.get(i).getName());
			if(list.get(i).getType().equals("Fun")) {
				((Function)list.get(i)).getList().print();
			}
		}
		System.out.print(")");
	}
	
	//Checks the list and adds the number a into every variable that it finds
	//If the element in position i is function or relationship it recursively calls the appendString function till there are no more functions
	//The result is the same list with slightly changed variable names
	public void appendString(int a) {
		String name;
		int i;
		for(i=0;i<list.size();i++) {
			if(list.get(i).getType().equals("Var")) {
				((Variable)list.get(i)).appendString(a);
			} else if(list.get(i).getType().equals("Fun")) {
				((Function)list.get(i)).getList().appendString(a);
			} else if(list.get(i).getType().equals("Rel")) {
				((Relationship)list.get(i)).getList().appendString(a);
			}
		}
	}
	
	//Applies the restrictions of substitution set s into the list
	//What it basically does is that it changes the variables, if they're bound, into something else that is contained on the substitutionset
	public void applySubSet(SubstitutionSet sub) {
		int i;
		for(i=0;i<list.size();i++) {
			if(list.get(i).getType().equals("Var")) {
				if(sub.isBound((Variable)(list.get(i)))) {
					Unifiable uni = sub.getBinding((Variable)list.get(i));
					Unifiable temp;
					if(uni.getType().equals("Rel")) {
						temp = new Relationship((Relationship)uni);
					} else if(uni.getType().equals("Fun")) {
						temp = new Function((Function)uni);
					} else if(uni.getType().equals("Con")) {
						temp = new Constant((Constant)uni);
					}else {
						temp = new Variable((Variable)uni);
					}
					list.set(i,temp);
				}
			} else if(list.get(i).getType().equals("Fun")) {
				((Function)list.get(i)).getList().applySubSet(sub);
			} else if(list.get(i).getType().equals("Rel")) {
				((Relationship)list.get(i)).getList().applySubSet(sub);
			}
		}
	}
	
	//Checks if the List l is renamed
	//By renamed we mean that l must not have the same type of variables and the same names(on constants and functions) with every other thing on the other list
	//Because if this happens, it will mean that the object that we are testing is not unique, so we'll be adding a duplicate in the list with absolutely no purpose
	//If l is a function or relationship it dives in the function or rel recursively and checks it's variables
	//If something in the list is different then there is no problem
	//After we've checked everything and we haven't found something similar, we can be sure that a is NOT a rename of something else
	public boolean isRenamed(List l) {
		if(l.getList().size() != this.list.size()) {
			return false;
		}
		int i;
		for(i=0;i<list.size();i++) {
			if(!l.list.get(i).getType().equals(list.get(i).getType())) {
				return false;
			}
			if((!l.list.get(i).getType().equals("Var")) && (!l.list.get(i).getName().equals(list.get(i).getName()))) {
				return false;
			}
			if(l.list.get(i).getType().equals("Fun")) {
				if(((Function)l.list.get(i)).getList().isRenamed(((Function)list.get(i)).getList())) {
					return true;
				} else {
					return false;
				}
			}
			if(l.list.get(i).getType().equals("Rel")) {
				if(((Relationship)l.list.get(i)).getList().isRenamed(((Relationship)list.get(i)).getList())) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	public SubstitutionSet unify(Unifiable uni, SubstitutionSet sub) {
		if(sub == null) {
			return null;
		} else if(this.type.equals(uni.getType()) && this.isSame(uni)) {
			return sub;
		} else if(uni.getType().equals("Var")) {
			return ((Variable)uni).unify_var(this,sub);
		} else if(this.type.equals(uni.getType()) && type.equals("Lis")) {
			ArrayList<Unifiable> rest1 = new ArrayList<Unifiable>(this.list);
			ArrayList<Unifiable> rest2 = new ArrayList<Unifiable>(((List)uni).getList());
			Unifiable first1= rest1.get(0);
			Unifiable first2= rest2.get(0);
			rest1.remove(0);
			rest2.remove(0);
			List rest_1=new List(name,rest1);
			List rest_2=new List(name,rest2);
			return ((List)rest_1).unify(((List)rest_2),first1.unify(first2,sub));
		} else {
			return null;
		}
	}
}
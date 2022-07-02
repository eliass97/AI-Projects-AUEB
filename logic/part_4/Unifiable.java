//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

/*We created this class because all the objects in FOL had to be named with a single class,
so this class was implemented by every single object.
In order to ensure that everthing has kept it's basic things,we had a variable named Type
in every FOL object,and this represents it's type,we're using this for Casts so that we can avoid errors.
The functions that are in this interface have to be implemented because they are used on any type of object
(constant orr variable etc).Of course things like the append string isn't used in everything,for example
append string is ONLY used on variables,since it can't change constants,so in some classes the implementations
are just nothing ({})*/
public interface Unifiable {
	public SubstitutionSet unify(Unifiable uni, SubstitutionSet sub);
	public String getType();
	public String getName();
	public boolean isSame(Unifiable uni);
	public void print();
	public void appendString(int a);
}
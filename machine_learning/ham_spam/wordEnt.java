//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034

public class wordEnt {
	private String name;
	private double entropy;
	//These 4 double variables represent the chance of the word on certain conditions
	//For example x0c0 is P(C=0|X=0) etc
	public double x0c0;
	public double x0c1;
	public double x1c0;
	public double x1c1;
	
	public wordEnt(String name, double entropy) {
		this.name = name;
		this.entropy = entropy;
	}
	
	public wordEnt() {}
	
	public String getName() {
		return name;
	}
	
	public void setThings(double a, double b, double c, double d) {
		x0c0=a;
		x0c1=b;
		x1c0=c;
		x1c1=d;
	}
	
	public double getNumber(boolean value, boolean type) {
		if(value==true && type==true) return x1c1;
		else if (value==false && type==false) return x0c0;
		else if (value==true && type==false) return x1c0;
		else return x0c1;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getEntropy() {
		return entropy;
	}
	
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}
	
	public void print() {
		System.out.println(name+" -> "+entropy);
	}
}
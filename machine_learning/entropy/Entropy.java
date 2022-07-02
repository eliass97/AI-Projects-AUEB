//Petros Demetrakopoulos 3150034
//Xristos Gkournelos 3140033
//Ilias Settas 3150156

import java.util.ArrayList;

//Data container class representing Entropy entity.
public class Entropy {
	double value = 0;
	double ones = 0;
	double zeros = 0;
	int attr_val = -1;
	ArrayList<Integer> trainDataList;
	
	public Entropy (Attribute attr){
		for (int val : attr.attribute_data){
			if (attr.value(val) == 0)
				zeros++;
			else
				ones++;
		}
		value = calculateEntropy();
	}
	
	public Entropy (double zeros, double ones, int attr_val, ArrayList<Integer> trainDataList){
		this.attr_val = attr_val;
		this.zeros = zeros;
		this.ones = ones;
		this.trainDataList = trainDataList;
		value = calculateEntropy();
	}
	
	private double calculateEntropy() {
		if (ones == 0 || zeros == 0)
			return 0;
		double a = (ones / (ones + zeros));
		double b = (zeros / (ones + zeros));
		return (-a * Math.log( a ) / Math.log( 2 )) - (b * Math.log( b ) / Math.log( 2 ));
	}
}
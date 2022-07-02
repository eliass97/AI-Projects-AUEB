//Petros Demetrakopoulos 3150034
//Xristos Gkournelos 3140033
//Ilias Settas 3150156

import java.util.ArrayList;
import java.util.Arrays;

//Data container class representing Attribute entity.
public class Attribute {
	int index;
	String name;
	int value_count;
	int[] values;
	ArrayList<Integer> attribute_data;

	public Attribute (int index, String name, int val_count)
	{
		this.index = index;
		this.name = name;
		this.value_count = val_count;
		this.values = new int[val_count];
		this.attribute_data = new ArrayList<Integer>();
		Arrays.fill(this.values, -1);
	}

	protected int value(int index){
		return this.values[index];
	}

	protected void add_data(int val){
		this.attribute_data.add(getIndex(val));
	}

	private int getIndex(int val){
		int i = 0;
		while (this.values[i] != -1) {
			if (this.values[i] == val)
				return i;
			i++;
		}
		this.values[ i ] = val;
		return i;
	}
}
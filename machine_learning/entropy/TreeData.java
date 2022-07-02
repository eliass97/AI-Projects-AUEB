//Petros Demetrakopoulos 3150034
//Xristos Gkournelos 3140033
//Ilias Settas 3150156

import java.util.HashMap;

//This class is the node class of our tree.
public class TreeData {
	Attribute attribute = null;
	int level;
	int class_value = -1;
	HashMap<Integer,TreeData> childList = new HashMap<Integer,TreeData>();

	public TreeData (Attribute attribute, int level){
		this.attribute = attribute;
		this.level = level;
	}

	public TreeData (int class_value){
		this.class_value = class_value;
	}
}
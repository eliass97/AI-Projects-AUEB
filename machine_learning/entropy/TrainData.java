//Petros Demetrakopoulos 3150034
//Xristos Gkournelos 3140033
//Ilias Settas 3150156

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import java.util.Iterator;

/*
This class reads and parses the data and then creates the decision tree.
The file read has the following format:
The first line has:
<ATTRIBUTE_1_NAME> <#_OF_ATTRIBUTE_1_DISTINCT_VALUES> <ATTRIBUTE_2_NAME> <#_OF_ATTRIBUTE_2_DISTINCT_VALUES> ....
and so on.
i.e age 49 year 12 nodes 31
which means that in the records that follow attribute "age" has 49 different (discrete) values.
The following lines are the raw data:
Each line represents a record.
*The last number for each records is the class of the record.*
i.e
30	64	1	1 ---> it is a record of class 1
The data are manipulated as follows:
We randomly read and use 10% of the data as test data and we use the rest 90% for training.
*/
public class TrainData {
	ArrayList<Attribute> attribute_list;
	Attribute class_data;
	int n = 0;
	String dataFN = "";
	String[] all_lines = new String[0];
	String[] test_lines = new String[0];
	ArrayList<String> all_lines_array = new ArrayList<String>(all_lines.length);
	
	public TrainData (){
		attribute_list = new ArrayList<Attribute>();
		class_data = new Attribute(-1, "", 2);
	}

	public TrainData (String fileName) throws FileNotFoundException{
		this();
		readDataFile(fileName);
	}

	protected void addAttribute(int index, String name, int val_count){
		this.attribute_list.add(new Attribute(index, name, val_count));
	}

	protected void addTrainData(int i, int val){
		this.attribute_list.get(i).add_data(val);
	}

	protected void addClassTrainData(int val){
		this.class_data.add_data(val);
	}

	private void readDataFile(String fileName) throws FileNotFoundException{
		try {
			dataFN = fileName;
			File train_file = new File(fileName);
			Scanner in = new Scanner(train_file);
			String[] tokens = in.nextLine().trim().split( "\\s+" ); 
			int i = 0;
			int j = 0;
			while (i < tokens.length){
				System.out.println("Added attribute " + tokens[i]);
				this.addAttribute(j++, tokens[i++], Integer.parseInt(tokens[i++]));
			}

			int count = 0;
			while (in.hasNextLine()) {
			    count++;
			    in.nextLine();
			}
			in.close();
			in = null;
			in = new Scanner(train_file);

			int recordsRead = 0;
			in.nextLine();
			in.nextLine();
			int index = 0;
			all_lines = new String[count];
			while (in.hasNextLine()){
				all_lines[index] = in.nextLine();
				index++;
			}
			for (String s : all_lines) {
	    		all_lines_array.add(s);
			}
			int testDataLength = (int)Math.floor(0.1 * count); //10% of data for test
			int trainDataLength = (int)Math.floor(0.9 * count); // rest 90% for train
			test_lines = new String[testDataLength];
			Random randomSelect = new Random();
			for(int k = 0; k < testDataLength; k++){
				n = randomSelect.nextInt(index) + 1;
				if(all_lines_array.get(n) != null){
				test_lines[k] = all_lines_array.get(n);
				all_lines_array.remove(n);
				index--;
			}
			}
			//all_lines_array now contains only the train records

			in = null;
			in = new Scanner(train_file);
			in.nextLine();
			in.nextLine();

			Iterator<String> iterator = all_lines_array.iterator();

			while(iterator.hasNext()){
				String crnLine = iterator.next();
				if(crnLine != null){
				tokens = crnLine.trim().split("\\s+");
				for (j = 0; j < tokens.length - 1; j++){
					this.addTrainData(j, Integer.parseInt(tokens[j]));
				}
				this.addClassTrainData(Integer.parseInt(tokens[tokens.length - 1])); 
				recordsRead++;
			}
		}
			in.close();
		}
		catch (FileNotFoundException e){
			System.out.println("Cannot find data file - " + fileName);
			throw e;
		}
	}

	protected TreeData createTree() {
		System.out.println("Creating the decision tree...");
		ArrayList<Integer> dataList = new ArrayList<Integer>(class_data.attribute_data.size());
		for (int i = 0; i < class_data.attribute_data.size(); i++) {
			dataList.add(i);
		}
		return treeBuilder(new Entropy(class_data), 0, new ArrayList<Integer>(), dataList);
	}

	private TreeData treeBuilder(Entropy entropy, int level, ArrayList<Integer> indexList, ArrayList<Integer> trainDataList) {
		Entropy[] ent = null;
		Entropy[] ent_temp;
		double inform_gain = 0;
		double temp;
		Attribute attrObj;
		Attribute attr = null;
		int index = 0;

		for (int i = 0; i < attribute_list.size(); i++) {
			// We do not check for attributes that alread are on the specific branch
			if (!indexList.contains(i)) {
				attrObj = attribute_list.get( i );
				ent_temp = calculateEntropies(attrObj, trainDataList);
				temp = calculateIG( ent_temp, entropy );
				// Getting the max Info gain attribute
				if (temp >= inform_gain) {
					inform_gain = temp;
					ent = ent_temp;
					index = i;
					attr = attrObj;
				}
			}
		}

		TreeData treeNode = new TreeData(attr, level);
		for (Entropy entObj : ent) {
			// If the entropy is 0 we have to add the attribute in the tree
			if (entObj.value == 0.0) {
				treeNode.childList.put(attr.value(entObj.attr_val), new TreeData((entObj.ones == 0.0) ? 0 : 1 ));
			}else if (indexList.size() == this.attribute_list.size() - 1) {
				treeNode.childList.put(attr.value(entObj.attr_val), new TreeData((entObj.ones > entObj.zeros) ? 1 : 0 ));
			} else {
				indexList.add(index);
				TreeData tmpNode = treeBuilder(entObj, level + 1, indexList, entObj.trainDataList);
				treeNode.childList.put(attr.value(entObj.attr_val), tmpNode);
				indexList.remove((Integer) index);
			}
		}
		return treeNode;
	}

	private Entropy[] calculateEntropies(Attribute attr, ArrayList<Integer> trainDataList) {
		System.out.println("Calculating entropy for attribute " + attr.name);
		Entropy[] ent = new Entropy[attr.value_count];
		double ones;
		double zeros;
		ArrayList<Integer> entropyTrainDataList;

		for (int i = 0; i < attr.value_count; i++){
			ones = 0;
			zeros = 0;
			entropyTrainDataList = new ArrayList<Integer>(trainDataList);

			for (int j = 0; j < attr.attribute_data.size(); j++ ){
				if (entropyTrainDataList.contains(j)){
					int val = attr.attribute_data.get(j);
					if (val == i){
						int temp = this.class_data.attribute_data.get(j);
						if (this.class_data.value(temp) == 0){
							zeros++;
						}else{
							ones++;
						}
					}else{
						entropyTrainDataList.remove((Integer) j);
					}
				}
			}
			ent[i] = new Entropy(zeros, ones, i, entropyTrainDataList);
		}
		return ent;
	}

	private double calculateIG(Entropy[] ent, Entropy entropy){
		System.out.println("Calculating info gain...");
		double ig = entropy.value;
		double total = entropy.ones + entropy.zeros;
		double ones = 0;
		double zeros = 0;

		for (int i = 0; i < ent.length; i++) {
			ones = ent[i].ones;
			zeros = ent[i].zeros;
			ig -= (((ones + zeros) / total) * ent[i].value);
		}
		return ig;
	}

	protected void calculateTrainDataMetrics(TreeData tree) {
		System.out.println("Calculating accuracy of train data...");
		TreeData tempTree = null;
		Attribute attr = null;
		boolean found = false;
		int val, class_val = -1;
		int matched_count = 0;

		for (int i = 0; i < this.class_data.attribute_data.size(); i++){
			tempTree = tree;
			while (tempTree.attribute != null){
				attr = this.attribute_list.get(tempTree.attribute.index);
				val = attr.attribute_data.get(i);
				val = attr.value(val);

				for (Map.Entry<Integer, TreeData> entry : tempTree.childList.entrySet()){
					if (val == entry.getKey()){
						if (entry.getValue().attribute != null){
							tempTree = entry.getValue();
							break;
						} else {
							found = true;
							class_val = entry.getValue().class_value;
							break;
						}
					}
				}
				if (found) {
					found = false;
					break;
				}
			}

			val = this.class_data.attribute_data.get(i);
			val = this.class_data.value(val);
			if (class_val == val)
				matched_count++;
		}
		System.out.println("Accuracy of trained data (" + this.class_data.attribute_data.size() + " instances) = " +  matched_count * 100.00 / this.class_data.attribute_data.size() + "%");
	}

	protected void calculateTestDataMetrics(TreeData tree){
		System.out.println("Calculating accuracy of test data...");
			String[] tokens = new String[0];
			TreeData tempTree = null;
			boolean found = false;
			int val, class_val = -1;
			int matched_count = 0;
			int i = 0;

			ArrayList<String> test_lines_arr_list = new ArrayList<String>(Arrays.asList(test_lines));
			Iterator<String> test_lines_iterator = test_lines_arr_list.iterator();

			while(test_lines_iterator.hasNext()){
				i++;
				tempTree = tree;
				while (tempTree.attribute != null){
					String crnTestLine = test_lines_iterator.next();
					tokens = crnTestLine.trim().split( "\\s+" );
					val = Integer.parseInt(tokens[tempTree.attribute.index]);
					for (Map.Entry<Integer, TreeData> entry: tempTree.childList.entrySet()){
						if (val == entry.getKey()){
							if (entry.getValue().attribute != null){
								tempTree = entry.getValue();
								break;
							} else {
								found = true;
								class_val = entry.getValue().class_value;
								break;
							}
						}
					}
					if (found){
						found = false;
						break;
					}
				}
				val = Integer.parseInt(tokens[tokens.length - 1]);
				if (class_val == val)
					matched_count++;
			}
			System.out.println("Accuracy of test data (" + i + " instances) = " + matched_count * 100.00 / i + "%" );
	}
}
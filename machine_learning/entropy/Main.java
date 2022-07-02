//Petros Demetrakopoulos 3150034
//Xristos Gkournelos 3140033
//Ilias Settas 3150156

import java.io.FileNotFoundException;

//Calls the algorithm functions from TrainData and TreeData classes
public class Main {
	public static void main(String[] args) {
		try{
			long startTime = System.currentTimeMillis();
			TrainData train_data = new TrainData("haberman.dat");	//reads the data from a text file
			TreeData treeData = train_data.createTree();				//creates the decision tree
			train_data.calculateTrainDataMetrics(treeData);	//calculates metrics for train data
			train_data.calculateTestDataMetrics(treeData);	//calculates metrics for test data
			 long stopTime = System.currentTimeMillis();
      		long elapsedTime = stopTime - startTime;
      		System.out.println("program executed in " + elapsedTime + " ms");
		}
		catch (FileNotFoundException e){
			System.out.println("FileNotFoundException");
		}
	}
}
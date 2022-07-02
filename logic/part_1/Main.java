//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
		//Read the 2 clauses needed from the txt file
		CNFClause KB = ReadTXT("input.txt","Clause:");
		CNFClause negProve = ReadTXT("input.txt","negProve:");
		//Check if there is anything wrong with the clauses and if not then start the procedure
		if(KB != null && negProve != null) {
			run(KB,negProve);
		} else {
			System.out.println("Mistakes were spotted in the clauses! Make sure the needed clauses are in CNF and run the program again.");
		}
    }
	
	//Rules:
	//Symbols used in txt: (, ), OR, AND, NOT
	//Before and after those symbols there MUST be an empty space to distinguish them from the rest strings
	//Before and after a literal there MUST be an empty space to distinguish it from the rest strings
	//DO NOT leave double space or the program will use it as a nameless literal
	//The line with the KB clause must start with the string "Clause: "
	//The line with the negation of the clause to prove must start with the string "negProve: "
	//All other lines will be ignored unless they have those specific strings as keywords
	//No empty sub clauses
	//Even sub clauses that contain 1 literal need to be inside ()
	public static CNFClause ReadTXT(String txt, String keyword) {
		CNFClause clause = new CNFClause();
		Scanner scan = null;
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Cannot find input file.");
        }
		int i;
		//The key is the first word of each line and we use it in case we only need 1-2 lines from lots of data
		boolean equalKeys, done = false;
		String currentLine;
		String words[];
		//For all lines and while we havent found the line we are looking for
		while(scan.hasNextLine() && done != true) {
			equalKeys = true;
			currentLine = scan.nextLine();
			//Check if the line key is the only we are looking for
			for(i=0;i<keyword.length();i++) {
				if(currentLine.charAt(i) != keyword.charAt(i)) {
					equalKeys = false;
				}
			}
			//If yes then we need to analyze the line and find the clause
			if(equalKeys == true) {
				//We found the line so once we break out of IF we will also break out of WHILE and return the clause
				done = true;
				//Create strings by splitting the line
				words = currentLine.split(" ");
				i = 1;
				CNFSubClause SC;
				//allowSymbolOR -> It will be true if CNF allows us to use OR in this specific string
				boolean allowSymbolOR, negation;
				while(i<words.length) {
					if(words[i].equals("(")) { //We have entered a new sub clause and we need to check each string to define the form of this sub clause and add it to the clause
						//We cant have OR in front of the sub clause
						allowSymbolOR = false;
						negation = false;
						SC = new CNFSubClause();
						//i++ is for moving to the next word
						i++;
						//Check if we passed the length of the string array
						if(!(i<words.length)) {
							clause = null;
							return clause;
						}
						//If its an empty sub clause then its not CNF so return null clause
						if(words[i].equals(")")) { //If its an empty sub clause then its not CNF
							clause = null;
							return clause;
						}
						//While we havent reached the end of the sub clause
						while(!words[i].equals(")")) {
							//If there is OR in a position that we expected it then move on
							if(words[i].equals("OR") && allowSymbolOR == true) {
								i++;
								allowSymbolOR = false;
								if(!(i<words.length)) {
							        clause = null;
							        return clause;
						        }
							}
							//Set the negation of there is NOT
							if(words[i].equals("NOT")) {
								negation = true;
								i++;
								allowSymbolOR = false;
								if(!(i<words.length)) {
							        clause = null;
							        return clause;
						        }
							}
							//If there is ) after NOT/OR then its not CNF
							if(words[i].equals(")")) {
								clause = null;
								return clause;
							}
							//If there is OR in this position then its not CNF
							if(words[i].equals("OR") && allowSymbolOR == false) {
								clause = null;
								return clause;
							}
							//If we have OR after NOT then its not CNF
							if(words[i].equals("OR") && negation == true) {
								clause = null;
								return clause;
							}
							//If there is AND inside a sub clause then its not CNF
							if(words[i].equals("AND")) {
								clause = null;
								return clause;
							}
							//After all those check points we can say for sure that this string is a literal so we create it and add it to the SC
							SC.getLiterals().add(new Literal(words[i],negation));
							i++;
							//Reset the negation
							negation = false;
							//After the literal we can allow OR
							allowSymbolOR = true;
							if(!(i<words.length)) {
							    clause = null;
							    return clause;
						    }
							//If this is not the end of the sub clause but we also dont have OR then its not CNF
							if(!words[i].equals("OR") && !words[i].equals(")")) {
								clause = null;
								return clause;
							}
						}
						//If we have reached this point then we have read the whole sub clause and now we need to add it to the clause and move on to the next word
						clause.getSubclauses().add(SC);
						i++;
					} else if(words[i].equals("AND")) { //If it's AND then we need to check it's position and then move on to the next word
						//If we have AND at the begging or the end of the clause then it's not CNF
						//We can only have AND between 2 sub clauses
						if(i==1 || i==words.length-1) {
							clause = null;
							return clause;
						} else {
							i++;
						}
					} else { //If we have symbols another than (, ), AND between the sub clauses then its not CNF
						clause = null;
						return clause;
					}
				}
			}
		}
		//If everything went fine then return the clause we found
		return clause;
	}
	
	//This will be called by Main when both needed clauses are inserted correctly
    public static void run(CNFClause KB, CNFClause negProve) {
		//Normally if data were inserted correcly the clause to proove will only contain 1 sub clause
		//If not then we will only pick the first sub clause to proove
		Vector<CNFSubClause> negProveSubClauses = negProve.getSubclauses();
		CNFSubClause PR = negProveSubClauses.get(0);
		//Print the clause inserted from the txt
		System.out.print("Clause: ");
		KB.print();
		System.out.println();
        System.out.println("Starting resolution...");
		//Start the resolution for the clause (KB) and the sub clause (nProve)
        boolean fin = PL_Resolution(KB,PR);
		//Print the results
		System.out.print("Result for ");
        PR.print();
		System.out.print(": ");
        System.out.println(fin);
    }
	
    public static boolean PL_Resolution(CNFClause KB, CNFSubClause negProve) {
		CNFClause clauses = new CNFClause();
		clauses.getSubclauses().addAll(KB.getSubclauses());
		clauses.getSubclauses().add(negProve);
		//The clauses now contain both KB and negProve sub clause
        System.out.print("We want to prove the negation of ");
        negProve.print();
		System.out.println();
        boolean newClauseFound,stop = false;
        int i,j,k,step = 1;
		CNFSubClause Ci,Cj,currentSC; //Will be used to check each new sub clause produced
		Vector<CNFSubClause> NEWsubclauses; //The sub clauses produced each step
		Vector<CNFSubClause> subclauses; //The sub clauses of clauses on each step
		Vector<CNFSubClause> resolution_result; //The result in resolution between 2 sub clauses
        //We will try resolution until we have either reached a contradiction or we cannot produce any more new clauses
        while(!stop) {
			NEWsubclauses = new Vector<CNFSubClause>();
            subclauses = clauses.getSubclauses();
			System.out.println();
            System.out.println("Step:" + step);
            step++;
			//For each compination of sub clauses Ci and Cj
			for(i=0;i<subclauses.size();i++) {
				Ci = subclauses.get(i);
				for(j=i+1;j<subclauses.size();j++) {
					Cj = subclauses.get(j);
					//Try a resolution between those 2 and insert the produced sub clauses to resolution_result
					resolution_result = CNFSubClause.resolution(Ci,Cj);
					//For each produced sub clause
					for(k=0;k<resolution_result.size();k++) {
						 currentSC = resolution_result.get(k); //The current sub clause
						 //If it's empty
						 if(currentSC.isEmpty()) {
                            System.out.println("----------------------------------");
                            System.out.print("Resolution between ");
                            Ci.print();
							System.out.println();
                            System.out.print("and ");
                            Cj.print();
							System.out.println();
                            System.out.print("produced: ");
                            System.out.println("Empty Subclause!");
                            System.out.println("----------------------------------");
                            return true;
                        }
                        //All the produced sub clauses that do not already excist are added
                        if(!NEWsubclauses.contains(currentSC) && !clauses.contains(currentSC)) {
                            System.out.println("----------------------------------");
                            System.out.print("Resolution between ");
                            Ci.print();
							System.out.println();
                            System.out.print("and ");
                            Cj.print();
							System.out.println();
                            System.out.print("produced: ");
                            currentSC.print();
							System.out.println();
							//Add the current SB to the new produced sub clauses
                            NEWsubclauses.add(currentSC);
                            System.out.println("----------------------------------");
                        }
					}
				}
			}
			newClauseFound = false;
            //Check if any new sub clauses were produced in this loop
            for(i=0;i<NEWsubclauses.size();i++) {
                if(!clauses.contains(NEWsubclauses.get(i))) {
                    clauses.getSubclauses().addAll(NEWsubclauses);                    
                    newClauseFound = true;
                }
            }
            //If no new sub clauses were produced then we cant infer logically the sub clause we want to prove
            if(!newClauseFound) {
                System.out.println("No new clauses were found!");
                stop = true;
            }
        }
        return false;
    }
}
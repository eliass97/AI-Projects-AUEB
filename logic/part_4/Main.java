//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034
//Ilias Settas 3150156

import java.util.*;
import java.io.*;

public class Main {
	public static void main(String[] args) {
		//Read the input files and add the data to KB and Prove
		KnowledgeBase KB = ReadTXT("input.txt");
		Relationship Prove = findProve("input.txt");
		//If everything is fine start the forward chaining with those 2
		if(KB != null && Prove != null) {
			KB.print();
			System.out.println();
			SubstitutionSet SS = new SubstitutionSet();
		    SS = KB.forward_chaining(KB,Prove);
			//Print the result
		    if(SS != null) {
			    System.out.println("Result: true");
		    } else {
			    System.out.println("Result: false");
		    }
		} else {
			//Else print which of those 2 have errors (are null) and ask to restart the program with new correct data
			if(KB == null) {
				System.out.println("Error spotted in KB!");
			}
			if(Prove == null) {
				System.out.println("Error spotted in Prove!");
			}
			System.out.println("Please check the data again and restart the program.");
		}
		
	}
	
	//It creates a sentence for each line expect to those who start with "Prove:"
	//A sentence is composed by many relationships that have their own components
	//Each relationship starts with R:
	//Other tags are F: for function V: for variable and C: for constant
	public static KnowledgeBase ReadTXT(String txt) {
		KnowledgeBase KB = new KnowledgeBase();
		Scanner scan = null;
		//Open the file
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Cannot find input file.");
        }
		String currentLine;
		String[] words;
		int i,j,br;
		Relationship rel = null;
		Sentence sen = null;
		Function fun = null;
		boolean done = false;
		boolean FOUND_THEN;
		while(scan.hasNextLine() && done != true) {
			//Break the current line into strings
			currentLine = scan.nextLine();
			words = currentLine.split(" ");
			//If we are not in the prove line then continue
			if(!words[0].equals("Prove:")) {
				//Check if the brackets ( and ) are equal for the whole line
				if(!checkBrackets(0,words.length,words)) {
					return null;
				} else {
					//For each relationship we call a recursion to find it's components and then add it to the sentence
					i = 0;
					sen = new Sentence();
					FOUND_THEN = false;
					//A loop to examine every word in the line
					while(i < words.length) {
						//If the word is the name of a R:
						if(words[i].charAt(0) == 'R' && words[i].charAt(1) == ':') {
							//Remove the tag and add it as the name of the new relationship
							rel = new Relationship(removeTag(words[i]));
							//Move to the next word
							i++;
							j = i;
							//Calculate the position where this R: ends (final bracket of it)
							i = calculateNextPos(i,words);
							//Check if there is no end (brackets are not correct)
							if(i == -1) {
								return null;
							}
							//Since everything is fine so far we can check the inside of the R: which is between the brackets
							while(j < i-1) {
								//If we meet a comma then we simply move on to the next word
								if(words[j].equals("(") || words[j].equals(")") || words[j].equals(",")) {
									j++;
								}
								//If we find a constant then remove the tag and add it to the relationship
								else if(words[j].charAt(0) == 'C' && words[j].charAt(1) == ':') {
									rel.add(new Constant(removeTag(words[j])));
									j++;
								}
								//If we find a variable then remove the tag and add it to the relationship
								else if(words[j].charAt(0) == 'V' && words[j].charAt(1) == ':') {
									rel.add(new Variable(removeTag(words[j])));
									j++;
								}
								//If we find a function then we need to use recursion to find all of it's components
								//And then if the function uses other function inside it we can find it's components too
								else if(words[j].charAt(0) == 'F' && words[j].charAt(1) == ':') {
									//j+1 is the position of the first bracket of the function
									j++;
									//We give as input to the recursion:
									//-The position of the function's '(' which is j
									//The position of the function's ')' which is calculateNextPos-1
									//The String[] which is words
									//The method will return a function to be added in the relationship
									fun = recursion(removeTag(words[j-1]),j,calculateNextPos(j,words)-1,words);
									//If the method returned null then something went wrong else add the function to R:
									if(fun == null) {
										return null;
									} else {
										rel.add(fun);
									}
									//We need to move to the position of the final bracket of the function
									j = calculateNextPos(j,words);
								}
								//If we meet any other symbol then something's wrong so we return null
								else {
									return null;
								}
							}
							//Check if the specific R: is after or before THEN and add it to the sentence correctly
							if(FOUND_THEN == false) {
								sen.add(rel);
							} else {
								sen.setProve(rel);
							}
					    }
						//If we meet those words then we simply move on to the next word in the line
						if(i < words.length) {
							if(words[i].equals("AND")) {
							    i++;
						    } else if(words[i].equals("THEN")) {
							    FOUND_THEN = true;
							    i++;
						    } else {
								return null;
							}
						}
					}
					//If it has no THEN we consider it as a statement so we add it to proven
					//Else we add just add it to KB
					if(sen.getProve() == null) {
						sen.setProve(sen.getParts().get(0));
						KB.addP(sen);
					} else {
						KB.add(sen);
					}
				}
			}
		}
		return KB;
	}
	
	//Method used to find the relationship we want to prove - the line must start with "Prove:"
	public static Relationship findProve(String txt) {
		Relationship prove = null;
		Function fun = null;
		Scanner scan = null;
		//Open the file
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Cannot find input file.");
        }
		String currentLine;
		String[] words;
		boolean done = false;
		int i,j,br;
		//Check all the lines in the txt
		while(scan.hasNextLine() && done != true) {
			//Break the line
			currentLine = scan.nextLine();
			words = currentLine.split(" ");
			//If it starts with Prove: then thats the one
			if(words[0].equals("Prove:")) {
				//Check if the brackets are correct
				if(checkBrackets(0,words.length,words)) {
					done = true;
					//Get the first relationship (if it has more relationships then they won't be counted)
					if(words[1].charAt(0) == 'R' && words[1].charAt(1) == ':') {
						//Remove the tag and use it as name of the relationship
						prove = new Relationship(removeTag(words[1]));
						//Calculate the end of the relationship's brackets
						i = calculateNextPos(2,words);
						//If the brackets are fine
						if(i != -1) {
							//And if there are no more words after this
							if(i != words.length) {
								return null;
							}
							//We start from the position of the first bracket
							j = 2;
							//Till the position of the final bracket
							while(j < i-1) {
								//If we meet a comma then we simply move on to the next word
								if(words[j].equals("(") || words[j].equals(")") || words[j].equals(",")) {
									j++;
								}
								//If we find a constant then remove the tag and add it to the relationship
								else if(words[j].charAt(0) == 'C' && words[j].charAt(1) == ':') {
									prove.add(new Constant(removeTag(words[j])));
									j++;
								}
								//If we find a variable then remove the tag and add it to the relationship
								else if(words[j].charAt(0) == 'V' && words[j].charAt(1) == ':') {
									prove.add(new Variable(removeTag(words[j])));
									j++;
								}
								//If we find a function then we need to use recursion to find all of it's components
								//And then if the function uses other function inside it we can find it's components too
								else if(words[j].charAt(0) == 'F' && words[j].charAt(1) == ':') {
									//j+1 is the position of the first bracket of the function
									j++;
									//We give as input to the recursion:
									//The name of the function that will be created which is removeTag
									//-The position of the function's '(' which is j
									//The position of the function's ')' which is calculateNextPos-1
									//The String[] which is words
									//The method will return a function to be added in the relationship
									fun = recursion(removeTag(words[j-1]),j,calculateNextPos(j,words)-1,words);
									//If the method returned null then something went wrong else add the function to R:
									if(fun == null) {
										return null;
									} else {
										prove.add(fun);
									}
									//We need to move to the position of the final bracket of the function
									j = calculateNextPos(j,words);
								}
								//If we meet any other symbol then something's wrong so we return null
								else {
									return null;
								}
							}
						} else {
							return null;
						}
				    } else {
					    return null;
				    }
				} else {
					return null;
				}
			}
		}
		//If we didn't find it then return null else return the relationship
		if(done == false || prove == null) {
			return null;
		} else {
			return prove;
		}
	}
	
	//Remove the tags (R: F: V: C:) from a string and return it
	public static String removeTag(String tagged) {
		String untagged = tagged.substring(2,tagged.length());
		return untagged;
	}
	
	//Check if the number of ( and ) between x and y are equal
	public static boolean checkBrackets(int x, int y, String[] words) {
		int br = 0;
		int i;
		//Every time we meet ( we add 1 to br
		//Every time we meet ) we subtract 1 from br
		//At the end br must be equal to 0 or else the sentence is not written correctly
		for(i=x;i<y;i++) {
			if(words[i].equals(")")) {
				br--;
			} else if(words[i].equals("(")) {
				br++;
			}
		}
		if(br == 0) {
			return true;
		}
		return false;
	}
	
	//We are given the information that the bracket "(" is at the position i of the words string array
	//We need to find where is the position of the bracket ")" that connects with the previous one
	//We will use almost the same method as checkBrackets (the method above this)
	//It will skip all the between brackets to reach the final one
	//That way we basicly learn where the end of the R: is and then we can move on to the next one
	public static int calculateNextPos(int i, String[] words) {
		//br is used the same way as in the method above
		//Currently we are at the word "(" so we add 1 and move to the next word
		int br = 1;
		i++;
		//So each time we meet a bracket we add or subtract to br and move to the next one
		while(br != 0 && i < words.length) {
			if(words[i].equals(")")) {
				br--;
				i++;
			} else if(words[i].equals("(")) {
				br++;
				i++;
			} else {
				i++;
			}
		}
		//If we didnt find a position where br == 0 then i became equal to words.length
		//So return -1 (used as a way to say 'error')
		//Or else return the position in the string[] right after the final bracket of the R:
		if(br != 0) {
			return -1;
		} else {
			return i;
		}
	}
	
	//This method is used when we meet a function inside a relationship or even inside another function
	//It returns a function with the components that it found inside it
	public static Function recursion(String name, int start, int end, String[] words) {
		//The function we will return
		Function fun = new Function(name);
		//A temporary function
		Function temp = null;
		//A loop that starts from the first bracket of this function till the final
		while(start < end) {
			//When we meet those symbols we just move to the next word
			if(words[start].equals("(") || words[start].equals(")") || words[start].equals(",")) {
				start++;
			} else if(words[start].charAt(0) == 'C' && words[start].charAt(1) == ':') { //Constant
				fun.add(new Constant(removeTag(words[start])));
				start++;
			} else if(words[start].charAt(0) == 'V' && words[start].charAt(1) == ':') { //Variable
				fun.add(new Variable(removeTag(words[start])));
				start++;
			} else if(words[start].charAt(0) == 'F' && words[start].charAt(1) == ':') { //Function
				//In this case we start a new recursion for the function inside this function
				//If it returns null then something went wrong so we return null too
				//Else we add the inner function as a component of this function
				start++;
				temp = recursion(removeTag(words[start-1]),start,calculateNextPos(start,words)-1,words);
				if(temp == null) {
					return null;
				} else {
					fun.add(temp);
				}
			} else { //Other symbols are not allowed and we return null in this case
				return null;
			}
		}
		return fun;
	}
}
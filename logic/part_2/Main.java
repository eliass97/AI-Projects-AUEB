//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034


import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        CNFClause KB = ReadTXT("input.txt");
		if(KB != null) {
			KB.print();
		}
		System.out.println();
		if(KB == null) { //If mistakes happened during the reading process
			System.out.println("Mistakes were spotted in the txt file! Make sure the statements follow the rules and restart the program!");
		} else {
			String prove = findProve("input.txt");
			//If we were not given something to prove then exit
			if(prove == null) {
				System.out.println("Input file does not contain a line with the literal to prove. Check the rules and restart the program!");
			} else {
				//Calls Forward_Chaining to find out if we can logicaly reach the final literal through the statements we already have
		        System.out.print("Result: ");
		        if(KB.Forward_Chaining(KB,new Literal(prove,false))) {
		    	    System.out.println("true");
		        } else {
			        System.out.println("false");
		        }
			}
		}
    }
	
	//Rules:
	//Allowed symbols are ^, =>
	//We decided to use CNF so f.e. the statement "A ^ B => C" should be writen as "NOT A OR NOT B OR C"
	//There must always be a space between symbols and literals
	//The result is always after =>
	//If you want to imply that a literal has been given to be true just type the name of it without spaces in 1 line
	//The final statement that we want to prove MUST be the last literal in the txt in the last line and MUST be positive
	//The rest are before => and between them there is a AND connection with ^
	//Everything other than those symbols will be considered a literal
	//Same aplies to double spaces
	//The literal that we want to prove must be in a line after the tag "Prove: "
	public static CNFClause ReadTXT(String txt) {
		CNFClause clause = new CNFClause();
		Scanner scan = null;
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Cannot find input file.");
        }
		boolean foundResult,negation,statement;
		String currentLine;
		String[] words;
		CNFSubClause SC;
		int i;
		while(scan.hasNextLine()) {
			SC = new CNFSubClause();
			currentLine = scan.nextLine();
			words = currentLine.split(" ");
			//If in this line we are given only 1 literal
			if(words.length == 1) {
				//If the literal has the name "NOT" or "OR" then return null
				if(words[0].equals("NOT") || words[0].equals("OR")) {
					clause = null;
					return clause;
				}
				SC.setResult(new Literal(words[0],false));
				i = 1;
			} else {
				i = 0;
			}
			//If the line starts with "Prove:" then we dont need it
			if(words[0].equals("Prove:")) {
				i = words.length + 1;
			}
			foundResult = false;
			negation = false;
			statement = false;
			while(i<words.length) {
				//If we found NOT then save it and move on to the next word
				if(words[i].equals("NOT")) {
					statement = true;
					negation = true;
					i++;
				}
				//If we found NOT at the end of the statement then return null
				if(i==(words.length-1) && words[i].equals("NOT")) {
					clause = null;
					return clause;
				}
				//If there is OR after NOT then we return null
				if(words[i].equals("OR") && negation == true) {
					clause = null;
					return clause;
				}
				//If there is OR in front or at the end of a statement then return null
				if((i==0 || i==(words.length-1)) && words[i].equals("OR")) {
					clause = null;
					return clause;
				}
				//If we reached this point then we have found a literal
				//If we found a literal without NOT
				if(negation == false) {
					//If we have already found a result then return null (only 1 result allowed)
					if(foundResult == true) {
						clause = null;
						return clause;
					}
					foundResult = true;
		            SC.setResult(new Literal(words[i],false));
					i++;
				} else {
					negation = false;
					SC.getLiterals().add(new Literal(words[i],negation));
					i++;
				}
				if(i<words.length) {
					if(words[i].equals("OR")) {
						i++;
					}
				}
			}
			//If it's a statement but there was no result then return null
			if(statement == true && foundResult == false) {
				clause = null;
				return clause;
			}
			clause.getSubclauses().add(SC);
		}
		//If everything went fine then return the clause
		return clause;
	}
	
	//This is used to define the literal needed to prove
	public static String findProve(String txt) {
		Scanner scan = null;
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Cannot find input file.");
        }
		String currentLine;
		String[] words;
		CNFSubClause SC;
		int i;
		while(scan.hasNextLine()) {
			currentLine = scan.nextLine();
			words = currentLine.split(" ");
			if(words[0].equals("Prove:")) {
				return words[1];
			}
		}
		return null;
	}
}
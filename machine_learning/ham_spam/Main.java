//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034

import java.util.*;
import java.io.*;

public class Main {
	static double percentage = 0.1;
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Data base = new Data(); //This will be the base that contains all the emails (spam and ham)
		File folder; //The folder that contains 2 other folders (spam and ham)
		File[] filesArray;
		System.out.println("Reading the emails...");
		//Open the ham folder
		folder = new File("mails/ham");
        filesArray = folder.listFiles();
        for(File file : filesArray) { //For each file in "mails/ham/" read it and save it in the TD
			base.add(ReadEmail("mails/ham/"+file.getName(),true));
            //System.out.println(file.getName());
        }
		//Open the spam folder
		folder = new File("mails/spam");
        filesArray = folder.listFiles();
        for(File file : filesArray) { //For each file in "mails/spam/" read it and save it in the TD
			base.add(ReadEmail("mails/spam/"+file.getName(),false));
            //System.out.println(file.getName());
        }
		ArrayList<Email> test = new ArrayList<Email>();
        Random randomGenerator = new Random();
        int randomInt;
		int i;
        int test_size = (int)(base.size()*(percentage));//Calculates how many emails it will take from the base
        int spam = 0;//This will be used in order to count how many spam messages are in our test data
        int ham = 0;//This will be used in order to count how many ham messages are in our test data
		//It randomly selects an email from the base
		//The selected emails will be used as check data to test the efficiency of the algorithm
		//The remaining emails in the base will be our traning data
		//Based on the traning data we calculated the chances of a test email to be spam or ham
        for(i=0;i<test_size;i++) {
        	randomInt = randomGenerator.nextInt(base.size());
        	if(base.getEmail(randomInt).getType() == false) {
				spam++;
			} else {
				ham++;
			}
        	test.add(base.getEmail(randomInt));
        	base.getEmails().remove(randomInt);
        }
		System.out.println("---------------------------------------------");
        System.out.println(+test_size +" emails were randomly chosen to be tested.");
        System.out.println(spam +" of them are spam emails.");
        System.out.println(ham +" of them are ham emails.");
        System.out.println("---------------------------------------------");
        base.train();
        int success = 0;//Counts how many times a prediction was correct
		System.out.println("Training finished.");
		System.out.println("Sorting the test emails...");
        for(i=0;i<test.size();i++) {
			//For each test email we compare the result of the algorithm with the actual type of the email
        	if(base.sort(test.get(i)) == test.get(i).getType()) {
				success++;
			}
			if(i%100==0 && i!=0) {
				System.out.println("Progress: "+(Math.round((((double)i/(double)test.size())*100)*100.0)/100.0)+"% of emails have been tested so far.");
			}
        }
        double rate = ((double)success/(double)test.size())*100;
		System.out.println("Email category sorting has finished!");
		System.out.println("Ratio: "+(100-(int)(percentage*100))+"-"+((int)(percentage*100)));
		System.out.println("Important words: "+base.importantThings);
        System.out.println("Results: "+(Math.round(rate*100.0)/100.0)+"% accuracy");
	}
	
	public static Email ReadEmail(String txt, boolean type) { //It opens the text and returns the Email it found in it
		Scanner scan = null;
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Failed to open file: "+txt);
        }
		Email mail = new Email(type);
		String currentLine; //The current line we are reading
		String[] words; //Words in that line
		//For each line in the text
		while(scan.hasNextLine()) {
			currentLine = scan.nextLine(); //Read the line
			words = currentLine.split("\\s+"); //Split it into strings
			//Add all the strings in the hash set after they are set to UpperCase
			for(int i=0;i<words.length;i++) {
				mail.add(words[i].toUpperCase());
			}
		}
		return mail;
	}
}
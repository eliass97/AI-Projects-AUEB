//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Data {
	private ArrayList<Email> emails; //Contains all the emails used during the training
	private HashSet<String> listed_words; //Contains all the words (without duplicates) we found in the emails
	private ArrayList<wordEnt> words_entropy; //Contains the entropy of each word on listed_words
	private HashSet<wordEnt> important_properties; //It has the most important properties of all the words based on their IG
	public int importantThings = 150; //This is the number of how many properties our class will have when it evaluates an email
	
	public Data() {
		emails = new ArrayList<Email>();
		listed_words = new HashSet<String>();
	}
	
	public Email getEmail(int i) {
		if(i<emails.size() && i>-1) {
			return emails.get(i);
		} else {
			return null;
		}
	}
	
	public ArrayList<Email> getEmails() {
		if(emails != null) {
			return emails;
		} else {
			return null;
		}
	}
	
	public void add(Email mail) {
		emails.add(mail);
		listed_words.addAll(mail.getHashSet());
	}
	
	public int size() {
		if(emails != null) {
			return emails.size();
		} else {
			return -1;
		}
	}
	
	/*This is H(C).It counts how many emails are spamm/ham and then divides these by the size of the emails.
	*This gives us the chance of an email to be a spamm or a ham
	*Then we use this chance(let's say x) to make the following calculation P(x)*log(x)
	*We do this for x=C=0 and x=C=1 and we have sum1 and sum2
	*Then we just return (-sum1 -sum2)
	*/
	public double Hc() {
		int sumH = 0;
		int sumS = 0;
		//Counts the amount of spam and ham emails
		for(int i=0;i<emails.size();i++) {
			if(emails.get(i).getType() == true) {
				sumH++;
			} else {
				sumS++;
			}
		}
		//Calculates H(C)
		double sum1 = (((double)sumH)/(double)emails.size())*(Math.log(((double)sumH)/(double)emails.size())/Math.log(2));
		double sum2 = (((double)sumS)/(double)emails.size())*(Math.log(((double)sumS)/(double)emails.size())/Math.log(2));
		double result = ((-1)*sum1)+((-1)*sum2);
		return result;
	}
	
	/*This calculates H(C|X=x)
	*First of all we do this for both classes (spamm and ham)
	*At the beggining, in the first for loop, we calculate how many times we have found X=x^C=0 and X=x^C=1 in our mails
	*After we've found this, we create chance1 and chance2 which is this number that we've found before divided by the chance of the word having this certain value
	*This can be written as P(X=x & C=0)/P(X=x) (the same for C=1) which can also be written as P(C=c|X=x)
	*/
	public double Hc_x(String s, boolean appears, double wordChance3) {
		int sumS = 0;
		int sumH = 0;
		for(int i=0;i<emails.size();i++) {
			if(emails.get(i).getType() == true) {
				if(emails.get(i).contains(s) == appears) {
					sumH++;
				}
			} else {
				if(emails.get(i).contains(s)==appears) {
					sumS++;
				}
			}
		}
		double chance1 = (((double)sumH)/emails.size())/wordChance3;
		double chance2 = (((double)sumS)/emails.size())/wordChance3;
		double part1,part2;
		if(chance1!=0) {
			part1 = (chance1)*((Math.log(chance1))/Math.log(2));
		} else {
			part1 = 0;
		}
		if(chance2!=0) {
			part2 = (chance2)*((Math.log(chance2))/Math.log(2));
		} else {
			part2 = 0;
		}
		part1 = part1*(-1);
		part2 = part2*(-1);
		double result = ((part1+part2));
		return result;
	}
	
	/*This is Sum(P(X=x)*H(C|X=x) (for all values of x)
	*wordChance3 calculates the chance of the word having a certain value.Hc(x) was explained before
	*/
	public double SumPxHc_x(String s) {
		double wc1 = wordChance3(s,false);
		double wc2 = wordChance3(s,true);
		double part1 = wc1*Hc_x(s,false,wc1);
		double part2 = wc2*Hc_x(s,true,wc2);
		double result = part1+part2;
		return result;
	}
	
	//This is the IG function IG=H(C)-Sum(P(X=x)*H(C|X=x) (for all values of x)
	public double IG(String s,double Hc) {
		double result = (Hc - SumPxHc_x(s));
		return result;
	}
	
	/*Training means initialising and filing the important_properties array.
	 * This algorithm has 2 ways of doing this:
	 * 1)by reading from a file all the important properties and their chances
	 * 2)by calculating each word's entropy and then picking out the importantThings words with the biggest entropy
	 * The second one is pretty slow but must be done at least once in order
	 * to get the file output so that the next training can be much faster
	 * Also, apart from just calculating the word's entropy, this algorithm also calculates 4 of the word's chances 
	 * P(X=0|C=0) P(X=1|C=0) P(X=0|C=1) P(X=1|C=1) 
	 * Also if it can't read from the file it just calculates the important_things array by itself*/
	public void train() {
		words_entropy=new ArrayList<wordEnt>();
		double Hc=Hc();
		System.out.print("Training");
		int count=0;
		for (String s : listed_words) {
			words_entropy.add(new wordEnt(s,IG(s,Hc)));
			if(count%3000==0)System.out.print(".");
			count++;
		}
		System.out.println();
		important_properties=new HashSet<wordEnt>();
		for(int i=0;i<(importantThings);i++) {
			double max=-1;
			int position=-1;
			for(int j=0;j<words_entropy.size();j++) {
				if(words_entropy.get(j).getEntropy()>max) {
					max=words_entropy.get(j).getEntropy();
					position=j;
				}
			}
			if(position!=-1) {
				wordEnt asd=new wordEnt(words_entropy.get(position).getName(),words_entropy.get(position).getEntropy());
				String word=words_entropy.get(position).getName();
				asd.setThings(wordChance(word,false,false),wordChance(word,false,true),wordChance(word,true,false),wordChance(word,true,true));
				important_properties.add(asd);
				words_entropy.remove(position);
			}
		}
	}
	
	//Count the amount of emails of this specific type
	public int countEmails(boolean type) {
		int count = 0;
		int i;
		for(i=0;i<emails.size();i++) {
			if(emails.get(i).getType() == type) count++;
		}
		return (count);
	}
	
	public void printRes() {
		for (wordEnt s : important_properties) {
			s.print();
		}
	}
	
	//Calculate the ratio (emails by this type)/(total emails)
	public double typeChance(boolean type) {
		return (double)((double)countEmails(type)/emails.size());
	}
	
	/*Calculates the chance of the word having a certain value by checking all the emails.
	If on an email the word has the same value as appears, the counter is increased.
	This implements P(X=x)*/
	public double wordChance3(String word, boolean appears) {
		int count=0;
		for(int i=0;i<emails.size();i++) {
			if(emails.get(i).contains(word) == appears) {
				count++;
			}
		}
		double result = (double)(((double)count)/(emails.size()));
		return result;
	}
	
	/*This is P(C=c|X=x), which calculates the chance of a class being true of false(ham or spamm)
	 given that the word has a certain value (true - false , appearing or not) */
	public double wordChance(String word, boolean value, boolean type) {
		int counter=0;
		for(int i=0;i<emails.size();i++) {
			if(emails.get(i).getType()==type && emails.get(i).contains(word)==value) counter++;
		}
		double result = (double)(((double)counter+1)/((double)countEmails(type)+2));
		return result;
	}

	/*Calculate the chance this email to be of this specific type using the methods described above
	the difference from findChance is that on the method above,it calculates the chance of each word
	from the begining by making all the calculations,on find chance2 though,that doesn't happen since
	the function uses the already stored and calculated chance values that are stored in the wordEnt object*/
	public double findChance(Email mail, boolean type) {
		double chance = typeChance(type);
		Iterator<wordEnt> iter = important_properties.iterator();
		int counter=0;
		while(iter.hasNext()) {
			wordEnt st = iter.next();
			String str=st.getName();
			if(mail.contains(str)) {
				chance=chance*st.getNumber(true,type);
			} else {
				chance=chance*st.getNumber(false,type);
			}
		}
		return chance;
	}
	
	//If the chance to be a spam is higher than the chance to be a ham then set the type as spam
	//If the chance to be a ham is higher than the chance to be a spam then set the type as ham
	//If the chances are equal then set the type as ham
	public boolean sort(Email mail) {
		double Spam = findChance(mail,false);
		double Ham = findChance(mail,true);
		//System.out.println(Spam +"--" +Ham);
		if(Spam > Ham) {
			return false;
		} else {
			return true;
		}
	}
}
//Xristos Gkournelos 3140033
//Ilias Settas 3150156
//Petros Demetrakopoulos 3150034

import java.util.*;

public class Email {
	private HashSet<String> wordsHash; //Contains all the words in the email
	private boolean type; //The type stands for spam(0) or ham(1)
	
	public Email() {
		wordsHash = new HashSet<String>();
	}
	
	public Email(ArrayList<String> words, boolean type) {
		this.type = type;
		wordsHash = new HashSet<String>();
		for(int i=0;i<words.size();i++) {
			add(words.get(i));
		}
	}
	
	public Email(boolean type) {
		wordsHash = new HashSet<String>();
		this.type = type;
	}
	
	public void setType(boolean type) {
		this.type = type;
	}
	
	public boolean getType() {
		return type;
	}
	
	public int size() {
		if(wordsHash != null) {
			return wordsHash.size();
		} else {
			return -1;
		}
	}
	
	public void add(String word) {
		wordsHash.add(word);
	}
	
	public void clear() {
		if(wordsHash != null) {
			wordsHash.clear();
		}
	}
	
	public boolean contains(String word) {
		if(wordsHash != null) {
			return wordsHash.contains(word);
		}
		else return false;
	}
	
	public void remove(String word) {
		if(wordsHash != null) {
			wordsHash.remove(word);
		}
	}
	
	public boolean isEmpty() {
		return wordsHash.isEmpty();
	}
	
	public Iterator<String> getIterator() {
		return wordsHash.iterator();
	}
	
	public HashSet<String> getHashSet() {
		return wordsHash;
	}
}
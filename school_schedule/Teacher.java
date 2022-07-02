//Ilias Settas 3150156
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034

import java.util.*;

public class Teacher { //Teacher constructor
	
	private int code; //Teacher's code
	private String name; //Teacher's name
	public ArrayList<Integer> lessons; //A list of all the lessons he can teach
	private int hours; //Hours in his disposal
	
    public Teacher(int code, String name, ArrayList<Integer> lessons, int hours) { //Constructor
        this.code = code;
		this.name = name;
		this.hours = hours;
		this.lessons = new ArrayList<Integer>();
		while(lessons.isEmpty() == false) {
			this.lessons.add(lessons.remove(0));
		}
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
    	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public int getHours() {
		return hours;
	}
	
	public void setHours(int hours) {
		this.hours = hours;
	}
	
	public boolean teaches(int lesson) { //Check if he teaches a specific lesson
		return lessons.contains(lesson);
	}
	
	public void print() { //Print teacher's data
		System.out.print(getCode()+" "+getName()+" ");
		for(int i=lessons.size()-1;i>=0;i--) {
			System.out.print(lessons.get(i)+" ");
		}
		System.out.println(getHours());
	}
}
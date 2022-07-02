//Ilias Settas 3150156
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034

import java.util.*;

public class Lesson { //Lesson constructor
	
	private int code; //Lesson's code
	private String name; //Lesson's name
	private char classroom; //Lesson's class (A,B,C)
	private int hours; //Hours per week
	public ArrayList<Integer> teachers; //A list with the teachers that can teach this lesson
	
    public Lesson(int code, String name, char classroom, int hours) { //Constructor
        this.code = code;
		this.name = name;
		this.classroom = classroom;
		this.hours = hours;
		teachers = new ArrayList<Integer>();
    }
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getHours() {
		return hours;
	}
	
	public void setHours(int hours) {
		this.hours = hours;
	}
	
	public char getClassroom() {
		return classroom;
	}
	
	public void setClassroom(char classroom) {
		this.classroom = classroom;
	}
	
	public void print() { //Print lesson's data
		System.out.println(getCode()+" "+getName()+" "+getClassroom()+" "+getHours());
	}
}
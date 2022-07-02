//Ilias Settas 3150156
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034

public class Compo { //Compo constructor
	
	//A compo is a pair consisted of a teacher's code and a lesson's code
	//Each position in the schedule will have 1 of them or will be empty (0,0)
	private int lessonID; //Lesson's code
	private int teacherID; //Teacher's code
	
	public Compo(int lessonID, int teacherID) { //Constructor
		this.lessonID = lessonID;
		this.teacherID = teacherID;
	}
	
	public int getLID() {
		return lessonID;
	}
	
	public int getTID() {
		return teacherID;
	}
	
	public void setLID(int lessonID) {
		this.lessonID = lessonID;
	}
	
	public void setTID(int teacherID) {
		this.teacherID = teacherID;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lessonID;
		result = prime * result + teacherID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Compo other = (Compo) obj;
		if (lessonID != other.lessonID) {
			return false;
		}
		if (teacherID != other.teacherID) {
			return false;
		}
		return true;
	}
	
	public boolean Same(Compo obj) { //Check if this compo is similar to another one
		if(lessonID != obj.getLID() || teacherID != obj.getTID()) {
			return false;
		}
		return true;
	}
	
	public void print(){ //Print compo's data
		System.out.println("Lesson: "+lessonID+" | Teacher: "+teacherID);
	}
}
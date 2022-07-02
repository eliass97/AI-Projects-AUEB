//Ilias Settas 3150156
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034

import java.util.*;

public class State { //State constructor
	
	private Compo[][][] table; //School program
	private int score; //Score - high score means lot's of mistakes in the school program
	private int depth; //Tree depth of this state
	private int F; // F = score + depth (f=h+g)
	//The score is consisted of strong(1,2) and loose(3,4,5) restrictions
	//F is used to add the depth factor in the score
	
	public State(Compo[][][] table) { //Constructor
		this.table = new Compo[5][7][9];
		for(int k=0;k<9;k++) {
			for(int j=0;j<7;j++) {
				for(int i=0;i<5;i++) {
					this.table[i][j][k] = table[i][j][k];
				}
			}
		}
		score = 0;
		depth = 0;
		F = 0;
	}
	
	public void setSchedule(int i, int j, int k, Compo pair) {
		table[i][j][k] = pair;
	}
	
	public Compo getSchedule(int i, int j, int k) {
		if(table == null) {
			return null;
		}
		return table[i][j][k];
	}
	
	public Compo[][][] getScheldule() {
		if(table == null) {
			return null;
		}
		return table;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setF(int F) {
		this.F = F;
	}
	
	public int getF() {
		return F;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public boolean isTerminal() { //Terminal is when all strong restrictions are satisfied
		return getScore() < 4000;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(table);
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
		State other = (State)obj;
		if (!Arrays.deepEquals(table, other.table)) {
			return false;
		}
		return true;
	}
	
	public boolean checkSame(State other) { //Check if it's similar to another state
		int i,j,k;
		//We have to check every single slot in their tables
		for(i=0;i<5;i++) {
			for(j=0;j<7;j++) {
				for(k=0;k<9;k++) {
					if(!table[i][j][k].Same(other.getSchedule(i,j,k))) { //If there is a difference in a slot return false
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public ArrayList<State> getChildren(Teacher[] teachers, Lesson[] lessons) { //Get the children from the specific state
		ArrayList<State> children = new ArrayList<State>(); //An array list for all the children
		int i,j,k,l,t,h,i2,j2;
		State newstate;
		Compo newpair,newpair2;
		//Test all possible teachers for each lesson if they can teach it
	    for(k=0;k<9;k++) { //For each class
			for(l=0;l<lessons.length;l++) { //For every lesson
				if((k<=2 && lessons[l].getClassroom()=='A') || (k>=3 && k<=5 && lessons[l].getClassroom()=='B') || (k>=6 && k<=8 && lessons[l].getClassroom()=='C')) { //If the lesson belongs to this class
				    for(t=0;t<teachers.length;t++) { //For each teacher
					    if(teachers[t].teaches(lessons[l].getCode())) { //If he can teach the lesson
						    newstate = new State(table); //Create a new state
					        newstate.setDepth(getDepth()+1); //Set the depth
							//Find the slots of this class where the lesson is taught and place the specific teacher there
						    for(j=0;j<7;j++) { //For each hour
							    for(i=0;i<5;i++) { //For each day
								    if(table[i][j][k].getLID() == lessons[l].getCode()) { //If this is the lesson
									    newpair = new Compo(table[i][j][k].getLID(),teachers[t].getCode()); //Create a compo consisted of the new teacher and the lesson
									    newstate.setSchedule(i,j,k,newpair); //Set the new pair in the specific slot of the new state's schedule
								    }
							    }
						    }
						    newstate.setScore(newstate.CalculateScore(teachers,lessons)); //Calculate new state's score
					        newstate.setF(newstate.getScore()+(newstate.getDepth()*200)); //Calculate new state's F
					        children.add(newstate); //Add the new state to the children
					    }
				    }
				}
			}
		}
		//Swap all lessons with other lessons on the schedule
	    for(k=0;k<9;k++) { //For each class
			for(j=0;j<7;j++) { //For each hour
				for(i=0;i<5;i++) { //For each day
			        for(j2=0;j2<7;j2++) {
				        for(i2=0;i2<5;i2++) {
							if(table[i][j][k].getLID() != table[i2][j2][k].getLID()) { //If they are not the same lesson
								if((j2==j && i2<=i) || j2<j) { //These are the slots of lessons we have already swapped with every other lesson - we dont need to do anything here
									//Nothing
								} else { //These are the slots of lessons we haven't swapped with this specific lesson yet
								    newstate = new State(table); //Create a new state
						            newpair = new Compo(table[i2][j2][k].getLID(),table[i2][j2][k].getTID()); //Copy LID and TID of lesson 1
							        newpair2 = new Compo(table[i][j][k].getLID(),table[i][j][k].getTID()); //Copy LID and TID of lesson 2
							        newstate.setSchedule(i,j,k,newpair); //Swap 2
							        newstate.setSchedule(i2,j2,k,newpair2); //Swap 1
								    newstate.setDepth(getDepth()+1); //Set new state's depth
							        newstate.setScore(newstate.CalculateScore(teachers,lessons)); //Calculate it's score
								    newstate.setF(newstate.getScore()+(newstate.getDepth()*200)); //Calculate it's F
							        children.add(newstate); //Add the new state to the children
								}
							}
				        }
			        }
				}
			}
		}
		return children;
	}
	
	public int CalculateScore(Teacher[] teachers, Lesson[] lessons) { //Calculate the score of this state based on strong and loose restrictions
		int i,j,k,h,count;
		int score = 0; //Start with score = 0
		int loose_heuretic_weight = 1; //The weight of a loose restriction (if we find an error)
		int strong_heuretic_weight = 4000; //The weight of a strong restriction (if we find an error)
		int low_score = 0 ;
		//First we find the errors caused by empty slots followed later by a lesson in the program
		for(k=0;k<9;k++) {
			for(i=0;i<5;i++) {
				for(j=0;j<7;j++) {
					if(table[i][j][k].getTID() == 0 && j<6) {
						for(h=j+1;h<7;h++) {
							if(table[i][h][k].getTID() != 0) {
								score += strong_heuretic_weight; //Empty slots between lessons
							}
						}
					}
				}
			}
		}
		//Then we find the errors caused by teachers who work more than 2 continuous hours
		for(k=0;k<9;k++) {
			for(i=0;i<5;i++) {
				for(j=0;j<7;j++) {
					if(j<5 && table[i][j][k].getTID() == table[i][j+1][k].getTID() && table[i][j+1][k].getTID() == table[i][j+2][k].getTID() && table[i][j][k].getTID() != 0) {
						score += strong_heuretic_weight; //More than 2 continuous working hours for the specific teacher
					}
				}
			}
		}
		//We create a hashset where we add teachers' code to find if anyone works in multiple classes at the same hour
		Set<Integer> foundTeachers = new HashSet<Integer>();
		for(i=0;i<5;i++) {
			for(j=0;j<7;j++) {
			    for(k=0;k<9;k++) {
					if(table[i][j][k].getTID() != 0) {
					    if(foundTeachers.contains(table[i][j][k].getTID())) {
						    score += strong_heuretic_weight; //Teacher works in more than 1 classes at the same hour
					    } else {
							foundTeachers.add(table[i][j][k].getTID());
						}
					}
				}
				foundTeachers.clear();
			}
		}
		//We calculate mistakes caused by teachers having to work more than they are available during the week
		//We'll also calculate how fairly divided each teachers's teaching hours are.
		//We're gonna find the average hours a teacher teaches each day
		//If the teacher's max teaching hours are equal or higher than the average then we will add the difference to the score.
		int avg_A = 0; //Teacher's average teaching hours
		for(h=0;h<lessons.length;h++) {
			avg_A = avg_A+lessons[h].getHours();
		}
		avg_A = avg_A / teachers.length;
		for(h=0;h<teachers.length;h++) {
			count = 0;
			for(k=0;k<9;k++) {
			    for(i=0;i<5;i++) {
				    for(j=0;j<7;j++) {
						if(teachers[h].getCode() == table[i][j][k].getTID()) {
							count++;
						}
				    }
			    }
			}
			if(count > teachers[h].getHours()) {
				score = score + strong_heuretic_weight * (count - teachers[h].getHours()); //Teacher works more hours in the week than he should
			}
			if(avg_A <= teachers[h].getHours()){ //If the teacher can teach more or the same hours as the average hours
				score = score + loose_heuretic_weight * (Math.abs(count-avg_A)); //Difference of average teaching time and actual teaching time of a teacher is being added on the score
				low_score = low_score + loose_heuretic_weight * (Math.abs(count-avg_A));
			}
		}
		//Then we calculate the average hours of lessons that each class has and then we take this number's integer part.
		//After we've taken it we will count the daily hours on every class.
		//The main concept is that if the actual day's hours are different than the average then we add the difference to the score.
		avg_A = 0;
		int avg_B = 0;
		int avg_C = 0;
		for(i=0;i<lessons.length;i++) { //Finding the average hours for class A/C/B 
			if(lessons[i].getClassroom()=='A') {
				avg_A = avg_A+lessons[i].getHours();
			}
			if(lessons[i].getClassroom()=='B') {
				avg_B = avg_A+lessons[i].getHours();
			}
			if(lessons[i].getClassroom()=='C') {
				avg_C = avg_A+lessons[i].getHours();
			}			
		}
		avg_A = avg_A / 5;
		avg_B = avg_B / 5;
		avg_C = avg_C / 5;
		count = 0;
		for(k=0;k<9;k++) {
			for(i=0;i<5;i++) {
				for(j=0;j<7;j++) {
					if(table[i][j][k].getLID() != 0){ //We're counting how many hours this class has on a given day
						count++;
					}
				}
				if(k>=0 && k<=2) {
					score = score + (loose_heuretic_weight * Math.abs(count-avg_A)); //Adds the difference between the actual teaching hours and the average teaching hours
					low_score = low_score+loose_heuretic_weight * Math.abs(count-avg_A);
					count = 0;
				}
				else if(k>=3 && k<=5) {
					score = score + (loose_heuretic_weight * Math.abs(count-avg_B)); //Each class may have different average hours,so it's better to do it like that
					low_score = low_score+loose_heuretic_weight * Math.abs(count-avg_B);
					count = 0;
				}
				else if(k>=6 && k<=8) {
					score = score + (loose_heuretic_weight * Math.abs(count-avg_C));
					low_score = low_score+loose_heuretic_weight * Math.abs(count-avg_C);
					count = 0;
				}				
			}
		}
		//Each lesson's teaching hours have to be fairly divided.
		//We're going to do this by finding the max and min teaching hours of the lesson.
		//Then we will substract them and add the result to the score.
		int min = 9999;
		int max = -1;
		for(h=0;h<lessons.length;h++) {
			for(k=0;k<9;k++) {
			    for(i=0;i<5;i++) {
					count=0;
				    for(j=0;j<7;j++) {
						if(lessons[h].getCode() == table[i][j][k].getLID()) {
							count++;
						}
					}
					if(count <= min) {
						min = count;
					}
					if(count >= max) {
						max = count;
					}
				}
				score = score + loose_heuretic_weight * (Math.abs(min-max));
				low_score = low_score+loose_heuretic_weight * (Math.abs(min-max));
				min = 9999;
				max = -1;
			}
		}
		return score;
	}
}
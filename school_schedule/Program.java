//Ilias Settas 3150156
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034

import java.io.*;
import java.util.*;

public class Program { //Execute this
	
	public static Lesson[] lessons; //Array for all lessons found in lessons.txt
	public static Teacher[] teachers; //Array for all teachers found in teachers.txt
	
	public static void main(String[] args) { //Main
	    int size,i,j,k;
		Compo[][][] schedule = new Compo[5][7][9]; //School schedule: 5=days, 7=hours, 9=classrooms
		//First we set all positions as empty slots
		for(i=0;i<5;i++) {
			for(j=0;j<7;j++) {
				for(k=0;k<9;k++) {
					if(schedule[i][j][k] == null) {
					    schedule[i][j][k] = new Compo(0,0); //(LID = 0 AND TID = 0) => Empty Slot
					}
				}
			}
		}
		System.out.println("Loading data...");
		String currentLine; //String for each line of the txt
		String words[]; //Strings seperated by " " in each line of the txt
		ArrayList<Integer> list = new ArrayList<Integer>(); //List of lessons for each teacher
		Scanner scan = null; //We will need that to read teachers.txt and lessons.txt
		try { //Open the lessons data file
			scan = new Scanner(new File("lessons.txt"));
		} catch (Exception e) {
            System.err.println("File not found.");
        }
		size = scan.nextInt(); //Get the number of lessons in txt
		lessons = new Lesson[size];
		i = 0;
		scan.nextLine();
		while(scan.hasNextLine()) { //Get the next line, split it, and insert the data in the lessons array
			currentLine = scan.nextLine();
			words = currentLine.split(" "); //Get a string for each word
			lessons[i] = new Lesson(Integer.parseInt(words[0]),words[1],words[2].charAt(0),Integer.parseInt(words[3]));
			i++;
		}
		scan.close();
		try { //Open the teachers data file
			scan = new Scanner(new File("teachers.txt"));
		} catch (Exception e) {
            System.err.println("File not found.");
        }
		size = scan.nextInt(); //Get the number of teachers in the txt
		teachers = new Teacher[size];
		i = 0;
		scan.nextLine();
		while(scan.hasNextLine()) { //Get the next line, split it, recognise all integers except to the last one as lessons and use the last integer as hours
			currentLine = scan.nextLine();
			words = currentLine.split(" "); //Get a string for each word
			for(j=2; j<words.length-1; j++) { //Insert the lessons in the list
				list.add(0,Integer.parseInt(words[j])); //They are between the name and the hours of the teacher
			}
			teachers[i] = new Teacher(Integer.parseInt(words[0]),words[1],list,Integer.parseInt(words[j]));
			i++;
		}
		scan.close();
		//For each lesson find all the teachers and insert them in the teachers array in the constructor
		for(i=0;i<lessons.length;i++) {
			for(j=0;j<teachers.length;j++) {
				if(teachers[j].teaches(lessons[i].getCode())) {
					lessons[i].teachers.add(teachers[j].getCode());
				}
			}
		}
		for(j=0;j<teachers.length;j++){ //Print teachers data
			teachers[j].print();
		}
		for(j=0;j<lessons.length;j++){ //Print lessons data
			lessons[j].print();
		}
		System.out.println("Files teachers.txt and lessons.txt have been loaded.");
		State end_state = null;
		int counter = 1;
		do { //Max 10 trials - if the amount of children is too high then we restart with new randomized schedule (that way it can run with minimum req 4-8GB RAM)
			if(counter!=1) {
				System.out.println("Attempt #"+counter+" failed. Clearing the states list and restarting.");
			}
			System.out.println("Randomizing the schedule...");
			schedule = Randomize(schedule,teachers,lessons); //Randomize the schedule
			System.out.println("Executing AI...");
			State start = new State(schedule); //Create a starting state using the randomized data
			start.setDepth(0); //Starting tree depth is 0
			start.setScore(start.CalculateScore(teachers,lessons)); //Calculate starting state's Score
			start.setF(start.getScore()+start.getDepth()); //Calculate starting state's F
			A_Star proccess = new A_Star(); //Create an A_Star object
			end_state = proccess.Search(start,teachers,lessons); //Use the search method in the A_Star constructor to find a final state
			counter++;
		} while(end_state.getScore() == -100 && counter <= 10);
		//We have tested it a lot of times with diferrent teacher txt and none of them so far has exceeded 10 loops
		schedule = end_state.getScheldule(); //Copy final state's table to schedule
		System.out.println("The proccess is done.");
		System.out.println("Writing data...");
		WriteTXT(schedule); //Write data on txt
		WriteCSV(schedule); //Write data on xls
		System.out.print("Results can be found in schedule.txt and schedule.csv.");
	}
	
	public static void WriteTXT (Compo[][][] schedule) { //A simple txt to keep the compinations of teacher's code and lesson's code for each class
		BufferedWriter writer = null;
		int i,j,k;
		try	{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("schedule.txt"))));
		}
		catch (FileNotFoundException e) {
			System.err.println("Error occured while opening schedule file!");
		}
		for(k=0;k<9;k++) { //For each classroom
			for(j=0;j<7;j++) { //For each hour
				for(i=0;i<5;i++) { //For each day
					try {
                        writer.write(schedule[i][j][k].getLID()+"-"+schedule[i][j][k].getTID()+" "); //Write info in the schedule.txt
		            } catch (IOException e) {
			            System.err.println("Write error!");
		            }
				}
				try {
                    writer.newLine(); //Change line for new hour
		        } catch (IOException e) {
	                System.err.println("Write error!");
                }
			}
			try {
                writer.newLine(); //Change line for new classroom
		    } catch (IOException e) {
	            System.err.println("Write error!");
            }
		}
		try {
			writer.close();
		}
		catch (IOException e) {
			System.err.println("Error closing file.");
		}
	}
	
	public static void WriteCSV(Compo[][][] schedule) { //A better representation of the schedule with names instead of codes
	    FileWriter fileWriter = null;
	    try {
	        fileWriter = new FileWriter("schedule.csv");
			//First write the days on the top
			fileWriter.append("Monday");
			fileWriter.append(";");
			fileWriter.append("Tuesday");
			fileWriter.append(";");
			fileWriter.append("Wednesday");
			fileWriter.append(";");
			fileWriter.append("Thursday");
			fileWriter.append(";");
			fileWriter.append("Friday");
			fileWriter.append(";");
	        fileWriter.append("\n");
			fileWriter.append("\n");
	        int i,j,k,lines,cols,l,t;
	        lines = 0;
	        cols = 0;
	        for(k=0;k<9;k++) { //For each classroom
     	        lines = 0;
			    for(j=0;j<7;j++) { //For each hour
				    cols = 0;
				    for(i=0;i<5;i++) { //For each day
					    String lname = "";
					    String tname = "";
					    for(l=0;l<lessons.length;l++){ //Find the lesson according to the given LID
						    if(lessons[l].getCode() == schedule[i][j][k].getLID()){
							    lname = lessons[l].getName();
							    break;
						    }
					    }
					    for(t=0;t<teachers.length;t++){ //Find the teacher according to the given TID
						    if(teachers[t].getCode() == schedule[i][j][k].getTID()){
							    tname = teachers[t].getName();
							    break;
						    }
					    }
					    fileWriter.append(lname+"-"+tname); //Write the data
					    fileWriter.append(";");
					    cols = cols + 1;
				    }
				    fileWriter.append("\n"); //Change line for next hour
				    lines = lines + 1;
			    }
				fileWriter.append("\n"); //Change line for next class
		    }
		} catch (Exception e){
			System.out.println("Error in CsvFileWriter!");
	        e.printStackTrace();
		} finally {
			try {
	            fileWriter.flush();
	            fileWriter.close();
	        } catch (IOException e) {
	            System.out.println("Error while flushing/closing fileWriter!");
	            e.printStackTrace();
			}
		}
	}
	
	public static Compo[][][] Randomize(Compo[][][] table, Teacher[] teachers, Lesson[] lessons) {
		//Each lesson's total hours are not randimized
		//The lesson positions on the table are randomized
		//Each teacher's total hours are randomized which means that 1 teacher might teach more than he is available as a starting state (restriction)
		//The teacher positions on the table are randomized according to what lesson each can teach
		//On each class (A1,A2...C3) each lesson can be taught ONLY by 1 teacher (restriction)
		//Careful with the data that you use - please use mainly our data based on real school program with well calculated teacher hours for this project
		//If maths-A1 and maths-A2 need 4 hours per week each and we have 2 mathematicians with 3 and 5 hours available then the solution is IMPOSSIBLE just like in reality
		Random rand = new Random();
		int i,j,k,h,n1,n2,i2,j2;
		boolean next,existsLesson,foundTeacher;
		int[] hours = new int[lessons.length]; //An array to keep the lesson hours while we change them at lessons[]
		for(h=0;h<lessons.length;h++) {
				hours[h] = lessons[h].getHours(); //Save hours of each lesson
		}
		for(k=0;k<9;k++) { //For each class
			for(j=0;j<7;j++) { //For each hour
				for(i=0;i<5;i++) { //For each day
					//We set the specific slot as 'Empty hour' - no lesson is taught (LID = 0 AND TID = 0)
					table[i][j][k].setTID(0); 
					table[i][j][k].setLID(0);
					h = 0;
					existsLesson = false;
					//First we check if there is any lesson hours left for this class
					while(existsLesson != true && h < lessons.length) {
						if(k >= 0 && k <= 2 && lessons[h].getClassroom() == 'A' && lessons[h].getHours() > 0) {
							existsLesson = true;
						} else if(k >= 3 && k <= 5 && lessons[h].getClassroom() == 'B' && lessons[h].getHours() > 0) {
							existsLesson = true;
						} else if (k >= 6 && k <= 8 && lessons[h].getClassroom() == 'C' && lessons[h].getHours() > 0) {
							existsLesson = true;
						}
						h++;
					}
					//If there is an available lesson then we must choose one randomly
					if(existsLesson == true) {
						n1 = -1;
						next = false;
					    while(next == false) { //Untill we find a acceptable lesson
						    n1 = rand.nextInt(lessons.length); //Pick a random lesson and check if it belongs to the right class
						    if(k >= 0 && k <= 2 && lessons[n1].getClassroom() == 'A' && lessons[n1].getHours() > 0) {
							    table[i][j][k].setLID(lessons[n1].getCode());
							    lessons[n1].setHours(lessons[n1].getHours() - 1);
							    next = true;
						    } else if(k >= 3 && k <= 5 && lessons[n1].getClassroom() == 'B' && lessons[n1].getHours() > 0) {
							    table[i][j][k].setLID(lessons[n1].getCode());
							    lessons[n1].setHours(lessons[n1].getHours() - 1);
							    next = true;
						    } else if (k >= 6 && k <= 8 && lessons[n1].getClassroom() == 'C' && lessons[n1].getHours() > 0) {
							    table[i][j][k].setLID(lessons[n1].getCode());
							    lessons[n1].setHours(lessons[n1].getHours() - 1);
							    next = true;
						    }
							//If it belongs to the specific class then choose this one
					    }
						next = false;
						foundTeacher = false;
					    //Search if we have already set a teacher for this lesson before and if yes then use him
					    for(j2=0;j2<=j && foundTeacher!=true && next!=true;j2++) {
				            for(i2=0;i2<5 && foundTeacher!=true && next!=true;i2++) {
								if(j2==j && i2>=i) { //If have searched all previous slots then we haven't set any teacher before
									next = true;
								} else { //If we haven't searched all previous slots yet
								    //If he can teach the lesson then pick him
									if(table[i2][j2][k].getLID() == lessons[n1].getCode()) {
							            table[i][j][k].setTID(table[i2][j2][k].getTID());
									    foundTeacher = true;
						            }
								}
					        }
				        }
						next = false;
						//If we haven't set a teacher for this lesson before then we will need to pick one randomly
						if(foundTeacher == false) {
					        while(next != true) {
						        n2 = rand.nextInt(teachers.length); //Pick a random teacher
								//If he can teach the lesson then use him
						        if(teachers[n2].teaches(table[i][j][k].getLID())) {
							        table[i][j][k].setTID(teachers[n2].getCode());
							        foundTeacher = true;
									next = true;
						        }
					        }
						}
				    }
				}
			}
			//Now we need to reload the lesson hours for the next class since we changed them during the randomization proccess
			for(h=0;h<lessons.length;h++) {
				lessons[h].setHours(hours[h]);
			}
		}
		return table;
	}
}
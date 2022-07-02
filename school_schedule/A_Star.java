//Ilias Settas 3150156
//Xristos Gkournelos 3140033
//Petros Demetrakopoulos 3150034

import java.util.*;

public class A_Star {
	
	private ArrayList<State> states; //Contains all the states found as children from another state
	private HashSet<State> closedSet; //A closed set that contains only 1 copy of each found state
	
	public A_Star() {
		this.states = new ArrayList<State>();
		this.closedSet = new HashSet<State>();
	}
	
	public State FindMinScoreState() { //Find the positions of the minimum F state
		int i,min=0;
		if(states.size() == 0) {
			return null;
		}
		else if(states.size()== 1) {
			State st = states.get(0);
			states.remove(0);
			return st;			
		}
		for(i=1;i<states.size();i++) { //Search all states to find the min F and return it after it's removed from the list
			if(states.get(i).getF() < states.get(min).getF()) {
				min = i;
			}
		}
		State st = states.get(min);
		states.remove(min);
		return st;
	}
	
	public State Search(State initialState, Teacher[] teachers, Lesson[] lessons) { //Search for final state
		this.states = new ArrayList<State>();
		this.closedSet = new HashSet<State>();
		this.states.add(initialState); //Add the first state in the list
		while(this.states.size() > 0) {
			State currentState = FindMinScoreState(); //Find the min state according to their F
			System.out.println("State list size : " +states.size());
			System.out.println("Current state's heuretic score: "+currentState.getScore());
			System.out.println("------------------------------------------");
			if(states.size() >= 500000){ //Here we set a MAX amount of found states we allow before we restart (helps for 4-8GB RAM)
				states.clear(); //Clear the states list
				currentState.setScore(-100); //Set score equal to -100 (this will restart the randomization in main)
				closedSet.clear(); //CLear the closed set
				return currentState;
			}
			if(currentState.isTerminal()) { //If it's terminal then return it as final state
				return currentState;
			}
			if(!closedSet.contains(currentState)) { //Check if we can add the state in the closed set and if yes find it's children
				this.closedSet.add(currentState);
				this.states.addAll(currentState.getChildren(teachers,lessons));
				
			}
		}
		return null;
	}
	
	public ArrayList<State> SortChildren(ArrayList<State> children) { //Sort children according to their F
		int i,minPos,minNum;
		ArrayList<State> childrenSorted = new ArrayList<State>(); //Temp list
		while(!children.isEmpty()) { //For each object in the list
			minPos = 0;
			minNum = children.get(0).getF();
			for(i=0;i<children.size();i++) { //Find the min object according to their F
				if(minNum > children.get(i).getF()) {
					minNum = children.get(i).getF();
					minPos = i;
				}
			}
			childrenSorted.add(children.remove(minPos)); //Add it to sorted list and remove it from the temp one
		}
		return childrenSorted;
	}
	
	public void hashEqual(State one, State two) {
		this.closedSet = new HashSet<State>();
		this.closedSet.add(one);
		if(closedSet.contains(two)) {
			System.out.println("It works!");
		}
	}
}
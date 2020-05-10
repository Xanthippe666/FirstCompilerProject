package Thompson;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Nfa {
	
	public enum ANCHOR {
		NONE,
		START,
		END,
		BOTH
	}
	
	public static final int EPSILON = -1; //Epsilon edge
	public static final int CCL = -2; //Set edge (char character list)
	public static final int EMPTY = -3; //no other edges
	private static final int ASCII_COUNT = 127;
	
	private int edge;
	private int edge2;
	
	public int getEdge() {
		return edge;
	}
	
	public void setEdge(int type) {
		edge = type;
	}
	
	public int getEdge2() {
		return edge;
	}
	
	public void setEdge2(int type) {
		edge = type;
	}
	
	public Set<Byte> inputSet;
	public Nfa next;
	public Nfa next2;
	private ANCHOR anchor;
	private int stateNum;
	private boolean visited = false;
	
	public void setVisited() {
		visited = true;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public Nfa() {
		inputSet = new HashSet<Byte>();
		clearState();
	}

	public void setStateNum(int num) {
		stateNum = num;
	}
	public int getStateNum() {
		return stateNum;
	}
	
	
	
	public void clearState() {
		inputSet.clear();
		next = next2 = null;
		anchor = ANCHOR.NONE;
		stateNum = -1; //Epsilon
	}
	
	public void addToSet(Byte b) {
		inputSet.add(b);
	}
	
	public void setComplement() {
		Set<Byte> newSet = new HashSet<Byte>();
		
		for(Byte b = 0; b < ASCII_COUNT; b++) {
			if(inputSet.contains(b) == false) {
				newSet.add(b);
			}
		}
		
		inputSet = null;
		inputSet = newSet;
	}
	
	public void setAnchor(ANCHOR anchor) {
		this.anchor = anchor;
	}
	
	public Nfa.ANCHOR getAnchor(){
		return anchor;
	}
	
	//Move data from another nfa into this current nfa
	public void cloneNfa(Nfa nfa) {
		inputSet.clear();
		Iterator<Byte> it = nfa.inputSet.iterator();
		while (it.hasNext()) {
			inputSet.add(it.next());
		}
		
		anchor = nfa.getAnchor();
		this.next = nfa.next;
		this.next2 = nfa.next2;
		this.edge = nfa.getEdge();
	}
	
}

package Thompson;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import InputSystem.Input;

public class NfaInterpreter {

	Nfa start;
	Input input;
	
	public NfaInterpreter(Nfa startNode, Input input) {
		this.input = input;
		this.start = startNode;
	}

	public void intepretNfa() {
		//从控制台读入要解读的字符串
    	System.out.println("Input regex/string to match: ");
    	input.ii_newFile(null);
    	input.ii_advance();
    	input.ii_pushback(1);
    	
    	//Get the first node
    	Set<Nfa> next = new HashSet<Nfa>();
    	next.add(start);
    	e_closure(next);
    	
    	//Nfa set is a Dfa node
    	Set<Nfa> current = null;
    	char c;
    	String inputStr = "";
    	boolean lastAccepted = false;
    	
    	while((c = (char) input.ii_advance()) != input.EOF){
    		current = move(next, c);
    		//Set the next node
    		next = e_closure(current);
    		
    		if(next != null) {
    			if(hasAcceptedState(next)) {
    				lastAccepted = true;
    			}
    		}
    		else {
    			break;
    		}
    		
    		inputStr += c;
    	}
    	
    	if(lastAccepted) {
    		System.out.println("The NFA can recognize the string: " + inputStr);
    	}
    	else {
    		System.out.println("Unrecognized string");
    	}
    	
	}
	
	//Move from one Nfa set to another
	private Set<Nfa> move(Set<Nfa> next, char c) {
		
		Set<Nfa> nodeSet = new HashSet<Nfa>();
		
		for(Nfa n: next) {
			//int stateNum = n.getStateNum();
			Set<Byte> s = n.inputSet;
			
			byte cb = (byte) c;
			boolean b = s.contains(cb);
			
			boolean ccl = n.getEdge() == Nfa.CCL;
			
			if(n.getEdge() == c //Check if a node has the
					//requested char transition
					|| (ccl && b)) {
				nodeSet.add(n.next);
			}
			
		}
		
		if(nodeSet != null) {
			System.out.print("move ({ " + strFromNfaSet(next) + 
					"},'" + c + "') = ");
			System.out.println("{ " + strFromNfaSet(nodeSet) + " }");
		}
		
		return nodeSet;
	}

	private boolean hasAcceptedState(Set<Nfa> input) {
		boolean isAccepted = false;
		if(input == null || input.isEmpty()) {
			return false;
		}
		
		String acceptedStatement = "Accept state: ";
		for(Nfa p:input) {
			if(p.next == null && p.next2 == null) {
				isAccepted = true;
				acceptedStatement += p.getStateNum() + ' ';
			}
		}
		
		if(isAccepted) {
			System.out.println(acceptedStatement);
		}
		
		
		return isAccepted;
	}

	private Set<Nfa> e_closure(Set<Nfa> nodeInputs){
		System.out.println("ε-Closure( " + strFromNfaSet(nodeInputs) + " ) = ");
		
		Stack<Nfa> nfaStack = new Stack<Nfa>();
		if(nodeInputs == null||nodeInputs.isEmpty()) {
			return null;
		}
		
		Iterator<Nfa> it = nodeInputs.iterator();
		while(it.hasNext()) {
			nfaStack.add(it.next());
		}
		
		while(nfaStack.empty() == false) {
			Nfa p = nfaStack.pop();
			
			if(p.next != null && p.getEdge() == Nfa.EPSILON) {
				if(nodeInputs.contains(p.next) == false ) {
					nfaStack.push(p.next);
					nodeInputs.add(p.next);
				}
			}
			
			if(p.next2 != null && p.getEdge() == Nfa.EPSILON) {
				if(nodeInputs.contains(p.next2) == false ) {
					nfaStack.push(p.next2);
					nodeInputs.add(p.next2);
				}
			}
			
			if(input != null) {
				//System.out.println("here");
				System.out.println("{ " + strFromNfaSet(nodeInputs) + " }");
			}
			
		}
		
		
		//Node inputs with its epsilon-closure
		return nodeInputs;
	}

	private String strFromNfaSet(Set<Nfa> input) {
		
		String s = "";
		Iterator it = input.iterator();
		while(it.hasNext()) {
			s += ((Nfa) it.next()).getStateNum();
			if(it.hasNext()) {
				s += ",";
			}
		}
		
		return s;
	}
	
	
}

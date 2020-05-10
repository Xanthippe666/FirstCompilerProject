package Thompson;

import java.util.Set;

public class nfaPrinter {
	private static final int ASCII_NUM = 128;
	private boolean start = true;
	
	private void printCCL(Set<Byte> set) {
		System.out.print("[");
		for(int i = 0; i < ASCII_NUM; i++) {
			if(set.contains((byte) i)) {
				if (i < ' ') {
					System.out.print("^" + (char)(i + '@'));
				}
				else {
					System.out.print((char) i);
				}
			}
			
		}
		System.out.println("]");
	}
	
	public void printNfa(Nfa startNfa) {
		if(startNfa == null || startNfa.isVisited()) {
			return;
		}
		if(start) {
			System.out.println("--------NFA--------");
		}
		
		startNfa.setVisited();
		printNfaNode(startNfa);
		/*
		if(startNfa.next != null) {
			printNfaNode(startNfa.next);
		}
		if(startNfa.next2 != null) {
			printNfaNode(startNfa.next2);
		}
		*/
		
		if(start) {
			//System.out.print("  (START STATE)");
    		start = false;
		}
		

    	System.out.print("\n");
		
    	//System.out.println("next node");
    	printNfa(startNfa.next);

    	//System.out.println("next node2");
    	printNfa(startNfa.next2);
	}

	public void printNfaNode(Nfa node) {
		if(node.next == null) {
			System.out.println("Terminal");
		}
		else {
			if(node.next != null) {
				System.out.print("NFA state: " + node.getStateNum());
				System.out.print("--> " + node.next.getStateNum());
				System.out.print(" on: ");
				switch(node.getEdge()){
				case Nfa.CCL:
					//System.out.println(node.inputSet);
					printCCL(node.inputSet);
					break;
				case Nfa.EPSILON:
					System.out.print("EPSILON ");
					break;
				default:
					System.out.print((char) node.getEdge());
					break;
				}
				System.out.print("\n");
			}
			if(node.next2 != null) {
				System.out.print("NFA state: " + node.getStateNum());
				System.out.print("--> " + node.next2.getStateNum());
				System.out.print(" on: ");
				switch(node.getEdge2()){
				case Nfa.CCL:
					//System.out.println(node.inputSet);
					printCCL(node.inputSet);
					break;
				case Nfa.EPSILON:
					System.out.print("EPSILON ");
					break;
				default:
					System.out.print((char) node.getEdge());
					break;
				}
				System.out.print("\n");
			}
			
			
		}
	}
	
	
}




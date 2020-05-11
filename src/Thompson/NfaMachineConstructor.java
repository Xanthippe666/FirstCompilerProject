package Thompson;

import java.util.Set;

public class NfaMachineConstructor {
	Lexer lexer;
	NfaManager nfaManager = null;
	
	public NfaMachineConstructor(Lexer lexer) throws Exception{
		this.lexer = lexer;
		nfaManager= new NfaManager();
		
		lexer.advance();
		
		
		while (lexer.MatchToken(Lexer.Token.EOS)) {
			lexer.advance();
		}
	}
	
	//Constructs a two-node NFA for a single character transition
	public boolean constructNfaForSingleCharacter(NfaPair pairOut) throws
	Exception{
		if(lexer.MatchToken(Lexer.Token.L) == false) {
			return false;
		}
		
		Nfa start = nfaManager.newNfa();
		Nfa end = nfaManager.newNfa();
		start.next = end;
		
		pairOut.startNode = start;
		pairOut.endNode = end;
		
		//start = pairOut.startNode = nfaManager.newNfa();
		//pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
		
		start.setEdge(lexer.getLexeme());
		lexer.advance();
		
		return true;
	}
	
	public boolean constructNfaForDot(NfaPair pairOut) throws
	Exception{
		if(lexer.MatchToken(Lexer.Token.ANY) == false) {
			return false;
		}
		
		Nfa start = null;
		start = pairOut.startNode = nfaManager.newNfa();
		pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
		
		start.setEdge(Nfa.CCL);
		start.addToSet((byte) '\n');
		start.addToSet((byte) '\r'); 
		start.setComplement();
		
		lexer.advance();
		
		return true;
	}
	
	public boolean constructNfaForCharacterSetWithoutNegative(NfaPair pairOut) throws
	Exception{
		if(lexer.MatchToken(Lexer.Token.CCL_START) == false) {
			return false;
		}
		
		lexer.advance(); //skips '['
		
		Nfa start = null;
		start = pairOut.startNode = nfaManager.newNfa();
		pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
		start.setEdge(Nfa.CCL);
		
		if(lexer.MatchToken(Lexer.Token.CCL_END) == false) {
			dodash(start.inputSet);
		}
		
		if(lexer.MatchToken(Lexer.Token.CCL_END) == false) {
			ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
			return false;
		}
		
		lexer.advance();//skips ']'
		
		return true;
	}

	public boolean constructNfaForCharacterSet(NfaPair pairOut) throws
	Exception{
		if(lexer.MatchToken(Lexer.Token.CCL_START) == false) {
			return false;
		}
		
		lexer.advance(); //skips '['
		boolean negative = false;
		
		Nfa start = null;
		start = pairOut.startNode = nfaManager.newNfa();
		pairOut.endNode = pairOut.startNode.next = nfaManager.newNfa();
		start.setEdge(Nfa.CCL);
		
		if(lexer.MatchToken(Lexer.Token.AT_BOL) == false) {
			//Is not negative
			negative = false;
			
		}
		else {
			negative = true;
			lexer.advance(); //Skip '^'
		}
		
		if(lexer.MatchToken(Lexer.Token.CCL_END) == false) {
			dodash(start.inputSet);
		}
		
		if(lexer.MatchToken(Lexer.Token.CCL_END) == false) {
			ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
			return false;
		}
		
		if(negative) {
			start.setComplement();
		}	
		
		lexer.advance();//skips ']'
		
		return true;
	}
	
	
	private void dodash(Set<Byte> set) {
		int first = 0;
		
		while(lexer.MatchToken(Lexer.Token.EOS) == false
				&& lexer.MatchToken(Lexer.Token.CCL_END) == false) {
			if(lexer.MatchToken(Lexer.Token.DASH) == false) {
				first = lexer.getLexeme();
				set.add((byte) first);
			}
			else {
				lexer.advance(); //skip '-'
				while(first < lexer.getLexeme()) {
					first++;
					set.add((byte) first);
				}
			}
			lexer.advance();
		}
	}
	
	
	public boolean factor(NfaPair pairOut) throws Exception{
		boolean handled = false;
		handled = constructStarClosure(pairOut);
		if(handled == false) {
			handled = constructPlusClosure(pairOut);
		}
		if(handled == false) {
			handled = constructOptionsClosure(pairOut);
		}
		return handled;
	}

	private boolean constructOptionsClosure(NfaPair pairOut) throws Exception {
Nfa start, end;
		
		boolean aTerm = term(pairOut);
		
		if(lexer.MatchToken(Lexer.Token.OPTIONAL) == false) {
			if(aTerm) {
				return true;
			}
			else {
				return false;
			}
		}
		
		//Initialize two Nfa nodes
		start = nfaManager.newNfa();
		end = nfaManager.newNfa();
		
		//connect the new start node --> start node
		//new end node --> end node
		//:::::Don't need to set EPSILONS because that is the default
		//:::::transitional edge
		start.next = pairOut.startNode;
		pairOut.endNode.next = end;
		
		//connect new start node --> new end node
		//connect out node --> start node
		start.next2 = end;
		//pairOut.endNode.next2 = pairOut.startNode;
		
		//relabel the pairout's nodes
		//to the new start and end nodes
		pairOut.startNode = start;
		pairOut.endNode = end;
		
		lexer.advance(); //Skips the +
		
		return true;
	}

	private boolean constructPlusClosure(NfaPair pairOut) throws Exception{
		Nfa start, end;
		
		boolean aTerm = term(pairOut);
		
		if(lexer.MatchToken(Lexer.Token.PLUS_CLOSE) == false) {
			if(aTerm) {
				return true;
			}
			else {
				return false;
			}
		}
		
		//Initialize two Nfa nodes
		start = nfaManager.newNfa();
		end = nfaManager.newNfa();
		
		//connect the new start node --> start node
		//new end node --> end node
		//:::::Don't need to set EPSILONS because that is the default
		//:::::transitional edge
		start.next = pairOut.startNode;
		pairOut.endNode.next = end;
		
		//connect new start node --> new end node
		//connect out node --> start node
		//start.next2 = end;
		pairOut.endNode.next2 = pairOut.startNode;
		
		//relabel the pairout's nodes
		//to the new start and end nodes
		pairOut.startNode = start;
		pairOut.endNode = end;
		
		lexer.advance(); //Skips the +
		
		return true;
	}

	public boolean constructStarClosure(NfaPair pairOut) throws Exception {
		Nfa start, end;
		
		NfaPair tempPair = new NfaPair();
		boolean aTerm = term(pairOut); 
		
		//term(pairOut);
		/*
		nfaPrinter print = new nfaPrinter();
		print.printNfa(pairOut.startNode);
		*/
		
		//Sets pairout to a NODE1 --> NODE2 structure
		
		if(lexer.MatchToken(Lexer.Token.CLOSURE) == false) {
			if(aTerm) {
				return true;
			}
			else {
				return false;
			}
		}
		
		//Initialize two Nfa nodes
		start = nfaManager.newNfa();
		end = nfaManager.newNfa();
		
		//connect the new start node --> start node
		//new end node --> end node
		//:::::Don't need to set EPSILONS because that is the default
		//:::::transitional edge
		start.next = pairOut.startNode;
		pairOut.endNode.next = pairOut.startNode;
		
		//connect new start node --> new end node
		//connect out node --> start node
		start.next2 = end;
		pairOut.endNode.next2 = end;
		
		//relabel the pairout's nodes
		//to the new start and end nodes
		pairOut.startNode = start;
		pairOut.endNode = end;
		
		
		
		lexer.advance(); //Skip over '*'
		
		return true;
	}

	public boolean term(NfaPair pairOut) throws Exception {
		
		//Check paranenthesis
		while(lexer.MatchToken(Lexer.Token.OPEN_PAREN) == true) {
			lexer.advance();
		}
		
		boolean handled = constructNfaForSingleCharacter(pairOut);
		if(handled==false) {
			handled = constructNfaForDot(pairOut);
			System.out.println("is a dot");
		}
		if(handled == false) {
			handled = constructNfaForCharacterSet(pairOut);
			System.out.println("is a charset");
		}
		else {
			System.out.println("is a char");
		}
		
		while(lexer.MatchToken(Lexer.Token.CLOSE_PAREN) == true) {
			lexer.advance();
		}
		
		return handled;
	}
	
	
	public void cat_expr(NfaPair pairOut) throws Exception {
		Nfa e2_start, e2_end;
		
		//Checks for error symbols
		first_in_cat(lexer.getCurrentToken());
		boolean factorable = factor(pairOut);
		/*
		System.out.println(factorable);
		if(factorable == false) {
			term(pairOut);
		}
		*/
		
		while(lexer.MatchToken(Lexer.Token.EOS) == false
				&& lexer.MatchToken(Lexer.Token.OR) == false) {
			//Checks for error symbols
			first_in_cat(lexer.getCurrentToken());
			NfaPair pairLocal = new NfaPair();
			pairLocal.startNode = nfaManager.newNfa();
			pairLocal.endNode = nfaManager.newNfa();
			factor(pairLocal);
			/*
			factorable = factor(pairLocal);
			if(factorable == false) {
				term(pairLocal);
			}
			*/
			//pairOut.startNode unchanged
			pairOut.endNode.next = pairLocal.startNode;
			pairOut.endNode = pairLocal.endNode;
		}
		
		//Get first character (regex must be some non-empty input!)
		//factor(pairOut);
		
		/*
		while(lexer.MatchToken(Lexer.Token.EOS) != true) {
			NfaPair pairLocal = new NfaPair();
			pairLocal.startNode = nfaManager.newNfa();
			pairLocal.endNode = nfaManager.newNfa();
			
			factor(pairLocal);
			//pairOut.startNode unchanged
			pairOut.endNode.next = pairLocal.startNode;
			pairOut.endNode = pairLocal.endNode;
		}
		*/
		
		/*
		if(first_in_cat(lexer.getCurrentToken())) {
		
			factor(pairOut);
		}
		
		
		while(first_in_cat(lexer.getCurrentToken())) {
			NfaPair pairLocal = new NfaPair();
			factor(pairLocal);
			
			//Concatenate factors
			pairOut.endNode.next = pairLocal.endNode;
			pairOut.endNode = pairLocal.startNode;
		}
		*/
	}
	
	private boolean first_in_cat(Lexer.Token tok) throws Exception{
		switch(tok) {
		case CLOSE_PAREN:
		case AT_EOL:
		case EOS:
			return false;
		case CLOSURE:
    	case PLUS_CLOSE:
    	case OPTIONAL:
    		//*, +, ? 这几个符号应该放在表达式的末尾
    		ErrorHandler.parseErr(ErrorHandler.Error.E_CLOSE);
    		return false;
    	case CCL_END:
    		//表达式不应该以]开头
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BRACKET);
    		return false;
    	case AT_BOL:
    		//^必须在表达式的最开始
    		ErrorHandler.parseErr(ErrorHandler.Error.E_BOL);
    		return false;
    	case OR:
    		//|必须在表达式的中间
    		ErrorHandler.parseErr(ErrorHandler.Error.E_OR);
    		return false;
    	default:
    		return true;
		}
		
	}
	
	public void expr(NfaPair pairOut) throws Exception {
		//System.out.println("get first expression");
		cat_expr(pairOut);
		NfaPair localPair = new NfaPair();
		//System.out.println("first cat_expr");
		
		
		while(lexer.MatchToken(Lexer.Token.OR) == true) {
			lexer.advance(); //skip the or
			//System.out.println("skipped or");
			cat_expr(localPair);
			
			
			Nfa start = nfaManager.newNfa();
			Nfa end = nfaManager.newNfa();
			
			start.next = pairOut.startNode;
			start.next2 = localPair.startNode;
			pairOut.startNode = start;
			
			pairOut.endNode.next = end;
			localPair.endNode.next = end;
			localPair.endNode = end;
			
		}
		
		
	}
	
}

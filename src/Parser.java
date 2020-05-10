
public class Parser {
	private Lexer lexer;
	
	String[] names = {"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"};
	private int nameP = 0;
	
	private String newName() {
		if(nameP>= names.length) {
			System.out.println("Expression too complex, uses more "
					+ "than 8 registers (32-bit system) ...");
			System.exit(1);
		}
		String reg = names[nameP];
		nameP++;
		
		return reg;
	}
	
	private void freeNames(String s) {
		if(nameP > 0) {
			names[nameP] = s;
			nameP--;
		}
		else {
			System.out.println("(Internal error) NAme stack underflow:"
					+ lexer.yylineno);
		}
	}
	
	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}
	
	public void statements() {
		String tempvar = newName();//Get a register
		expression(tempvar);
		
		//while(lexer.match(Lexer.EOI) != false) {
		//	expression(tempvar);
		//	freeNames(tempvar);
			
			if (lexer.match(lexer.SEMI)) {
				lexer.advance();
				freeNames(tempvar);
			}
			else {
				System.out.println("line: " + lexer.yylineno + " Missing semicolon");
	    		return;
			}
			
			if (lexer.match(Lexer.EOI)) {
				statements();
		}
		
	}

	private void expression(String tempvar) {
		String tempVar2;
		term(tempvar);
		while(lexer.match(Lexer.PLUS)) {
			lexer.advance();
			tempVar2 = newName();
			term(tempVar2);
			System.out.println(tempvar + "+=" + tempVar2);
			freeNames(tempVar2);
			
		if(lexer.match(Lexer.UNKNOWN_SYMBOL)) {
			System.out.println("unknow symbol: " + lexer.yytext);
    		return;
		}
		}
	}

	private void term(String tempVar) {
		String tempVar2;
		factor(tempVar);
		
		while(lexer.match(Lexer.TIMES)) {
			lexer.advance();
			tempVar2 = newName();
			factor(tempVar2);
			System.out.println(tempVar + "*=" + tempVar2);
			freeNames(tempVar2);
		
		}
		return;
	}

	private void factor(String tempVar) {
		if(lexer.match(Lexer.NUM_OR_ID)) {
			System.out.println(tempVar + " = " + lexer.yytext);
			lexer.advance();
		}
		else if(lexer.match(Lexer.LP)) {
			lexer.advance();
			expression(tempVar);
			if(lexer.match(Lexer.RP)) {
				lexer.advance();
			}
			else {
    			System.out.println("Mismatched paranthesis: " + lexer.yylineno);
    			return;
			}
			
		}
		else {
    		/*
    		 * 这里不是数字，解析出错
    		 */
			System.out.println("Number or identifier expected: " + lexer.yylineno);
    		return;
    	}
	}
}


public class ParserMultiline {
	private Lexer lexer;
	public boolean isLegalStatement = true;
	
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
	
	public ParserMultiline(Lexer lexer) {
		this.lexer = lexer;
	}
	
	
	public void statements() {
		
		
		while((!lexer.match(lexer.EOI))) {
			String tempvar = newName();
			expression(tempvar);
			freeNames(tempvar);
			
			if (lexer.match(lexer.SEMI)) {
				lexer.advance();
			}
			else {
				isLegalStatement = false;
				System.out.println("line: " + lexer.yylineno + " Missing semicolon");
	    		return;
			}
		
	}
		
		if (isLegalStatement) {
			System.out.println("The statement is legal");
		}
		
	}


	private void expression(String tempvar) {
		String tempvar2;
		term(tempvar);
		while(lexer.match(Lexer.PLUS)) {
			lexer.advance();
			tempvar2 = newName();
			term(tempvar2);
			System.out.println("add " + tempvar + "," + tempvar2);
			freeNames(tempvar2);
		if(lexer.match(Lexer.UNKNOWN_SYMBOL)) {
			isLegalStatement = false;
			System.out.println("unknow symbol: " + lexer.yytext);
    		return;
		}
		}
		
	}


	private void term(String tempvar) {
		String tempvar2;
		
		factor(tempvar);
		while(lexer.match(Lexer.TIMES)) {
			lexer.advance();
			tempvar2 = newName();
			factor(tempvar2);
			System.out.println("mov ax, " + tempvar);
			System.out.println("mul " + tempvar2);
			freeNames(tempvar2);
		
		}
		return;
		
	}



	private void factor(String tempvar) {
		if(lexer.match(Lexer.NUM_OR_ID)) {
			System.out.println("mov " + tempvar + ", " + lexer.yytext);
			lexer.advance();
		}
		else if(lexer.match(Lexer.LP)) {
			lexer.advance();
			expression(tempvar);
			if(lexer.match(Lexer.RP)) {
				lexer.advance();
			}
			else {
				isLegalStatement = false;
    			System.out.println("line: " + lexer.yylineno + " Missing )");
    			return;
			}
			
		}
		else {
    		/*
    		 * 这里不是数字，解析出错
    		 */
    		isLegalStatement = false;
    		System.out.println("illegal statements");
    		return;
    	}
	}
	
	
	
	
}

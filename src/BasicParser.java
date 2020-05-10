
public class BasicParser {
	private Lexer lexer;
	public boolean isLegalStatement = true;
	
	public BasicParser(Lexer lexer) {
		this.lexer = lexer;
	}
	
	
	public void statements() {
		
		
		while((!lexer.match(lexer.EOI))) {
			expression();
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


	private void expression() {
		term();
		while(lexer.match(Lexer.PLUS)) {
			lexer.advance();
			term();
			
		if(lexer.match(Lexer.UNKNOWN_SYMBOL)) {
			isLegalStatement = false;
			System.out.println("unknow symbol: " + lexer.yytext);
    		return;
		}
		}
		
	}


	private void expr_prime() {
		if (lexer.match(Lexer.PLUS)) {
			lexer.advance();
			term();
			expr_prime();
		}
		else if(lexer.match(Lexer.UNKNOWN_SYMBOL)) {
			isLegalStatement = false;
			System.out.println("unknow symbol: " + lexer.yytext);
    		return;
		}
		else {
			return;
		}
	}


	private void term() {
		factor();
		while(lexer.match(Lexer.TIMES)) {
			lexer.advance();
			factor();
		
		}
		return;
		
	}


	private void term_prime() {
		if(lexer.match(Lexer.TIMES)) {
			lexer.advance();
			factor();
			term_prime();
		}
		else {
			return;
		}
	}


	private void factor() {
		if(lexer.match(Lexer.NUM_OR_ID)) {
			lexer.advance();
		}
		else if(lexer.match(Lexer.LP)) {
			lexer.advance();
			expression();
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

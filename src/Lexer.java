import java.util.Scanner;

public class Lexer {
	public static final int EOI = 0;
	public static final int SEMI = 1;
	public static final int PLUS = 2;
	public static final int TIMES = 3;
	public static final int LP = 4;
	public static final int RP = 5;
	public static final int NUM_OR_ID = 6;
    public static final int  UNKNOWN_SYMBOL = 7;
    
	private int lookAhead = -1;
	
	public String yytext = "";
	public int yyleng = 0;
	public int yylineno = 0;
	
	private String input_buffer = "";
	private String current = "";
	
	private int state = 0;
	
	private boolean isAlNum(char c) {
		if(Character.isLetterOrDigit(c)) {
			return true;
		}
		return false;
	}
	
	
	
	private int lex() {
		
		while(true) {
			
			while (current == "") {
				input_buffer = "";
				//System.out.println("Input text to lex:");
				Scanner s = new Scanner(System.in);
				//Keep on getting lines until an end signal
				
				while(s.hasNext()) {
					String line = s.nextLine().toLowerCase();
					if (line.contentEquals("end")) {
						break;
					}
					input_buffer += line;
				}
				
				s.close();
				
				if (input_buffer.length() == 0) {
					current = "";
					//s.close();
					return EOI;
				}
				
				current = input_buffer;
				++yylineno;
				current.trim();
			} //while(current !== "")
			
			if(current.isEmpty()) {
				return EOI;
			}
			
			for (int i = 0; i < current.length(); i++) {
				//System.out.println("here");
				yyleng = 0;
				yytext = current.substring(0,1);
				switch(current.charAt(i)) {
				case ';': current = current.substring(1); return SEMI;
				case '+': current = current.substring(1); return PLUS;
				case '*': current = current.substring(1); return TIMES;
				case '(': current = current.substring(1); return LP;
				case ')': current = current.substring(1); return RP;
				
				case '\n':
				case '\t':
				case ' ':
					current = current.substring(1); break;
				
				default:
					if(!isAlNum(current.charAt(i))) {
						//System.out.println("ignoring illegal input " + current.charAt(i));
						current = current.substring(1);
						return UNKNOWN_SYMBOL;
					}
					else {
						while (i < current.length() && isAlNum(current.charAt(i))) {
							i++;
							yyleng++;
						}
						yytext = current.substring(0, yyleng);
						current = current.substring(yyleng);
						return NUM_OR_ID;
					}
					
					//break;
				}
			}
			
		}
	}

	public boolean match(int token) {
		if (current == "") {
			lookAhead = lex();
		}
		
		return token == lookAhead;
	}

	public void advance() {
		lookAhead = lex();
	}

	public void runLexer() {

		
		while(!match(EOI)) {
			System.out.println("Token : " + token() + ",Symbol: " + yytext);
			
			//System.out.println("Current: " + current);
			//System.out.println("lookAhead: " + lookAhead);
			//System.out.println("Current: " + current);
			
			advance();
		}
		
	}
	
	private String token() {
		String token = "";
		
		switch(lookAhead) {
		case EOI: 
			token = "EOI";
			break;
		case PLUS:
			token = "PLUS";
			break;
		case TIMES:
			token = "TIMES";
			break;
		case NUM_OR_ID:
			token = "NUM_OR_ID";
			break;
		case SEMI:
			token = "SEMI";
			break;
		case LP:
			token = "LP";
			break;
		case RP:
			token = "RP";
			break;
		case UNKNOWN_SYMBOL:
			token = "UNKNOWN???";
			break;
		}
		
		return token;
		
	}
}

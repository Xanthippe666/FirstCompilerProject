package Thompson;

public class Lexer {
	public enum Token{
		EOS, //End of string
		ANY, //.
		AT_BOL, //^
		AT_EOL, //$
		CCL_END, //]
		CCL_START, //[
		CLOSE_CURLY, //}
		CLOSE_PAREN, //)
		CLOSURE, //*
		DASH, //-
		END_OF_INPUT, //End of input stream
		L, //constants, letters
		OPEN_CURLY, //{
		OPEN_PAREN, //(
		OPTIONAL, //?
		OR, //|
		PLUS_CLOSE
	}
	
	private final int ASCII_COUNT = 128;
	private Token[] tokenMap = new Token[ASCII_COUNT];
	private Token currentToken = Token.EOS;
	RegularExpressionHandler exprHandler = null;
	private int exprCount = 0;
	private String curExpr = "";
	private int charIndex = 0;
	private boolean inQuoted = false;
	private boolean sawEsc = false; //Is there an escape character?
	private int lexeme;
	
	public Lexer(RegularExpressionHandler exprHandler) {
		initTokenMap();
		this.exprHandler = exprHandler;
	}

	private void initTokenMap() {
		for(int i = 0; i < ASCII_COUNT; i++) {
			tokenMap[i] = Token.L;
		}
		
        tokenMap['.'] = Token.ANY;
        tokenMap['^'] = Token.AT_BOL;
        tokenMap['$'] = Token.AT_EOL;
        tokenMap[']'] = Token.CCL_END;
        tokenMap['['] = Token.CCL_START;
        tokenMap['}'] = Token.CLOSE_CURLY;
        tokenMap[')'] = Token.CLOSE_PAREN;
        tokenMap['*'] = Token.CLOSURE;
        tokenMap['-'] = Token.DASH;
        tokenMap['{'] = Token.OPEN_CURLY;
        tokenMap['('] = Token.OPEN_PAREN;
        tokenMap['?'] = Token.OPTIONAL;
        tokenMap['|'] = Token.OR;
        tokenMap['+'] = Token.PLUS_CLOSE; 
        
	}
	
	public boolean MatchToken(Token t) {
		return currentToken == t;
	}
	
	public int getLexeme() {
		return lexeme;
	}
	
	public String getCurExpr() {
		return curExpr;
	}
	
	public Token advance() {
		if(currentToken == Token.EOS) {
			if(exprCount >= exprHandler.getRegularExpressionCount()) {
				currentToken = Token.END_OF_INPUT;
				return currentToken;
			}
			
			else {
				curExpr = exprHandler.getRegularExpression(exprCount);
				exprCount++;
			}
		}
		
		if(charIndex >= curExpr.length()) {
			currentToken = Token.EOS;
			charIndex = 0;
			return currentToken;
		}
		
		if(curExpr.charAt(charIndex) == '"') {
			inQuoted = !inQuoted;
			charIndex++;
		}
		
		sawEsc = (curExpr.charAt(charIndex) =='\\');
		if(sawEsc && curExpr.charAt(charIndex + 1) != '"'
				&& inQuoted == false) {
			lexeme = handleEsc();
		}
		else if(sawEsc && curExpr.charAt(charIndex + 1) != '"') {
			charIndex += 2;
			lexeme = '"';
		}
		else {
			lexeme = curExpr.charAt(charIndex);
			charIndex ++;
		}
		
		currentToken = (inQuoted || sawEsc)? Token.L: tokenMap[lexeme];
		return currentToken;
	}

	private int handleEsc() {
		int rval = 0;
		String exprToUpper = curExpr.toUpperCase();
		charIndex++;
		switch(exprToUpper.charAt(charIndex)) {
		case '\0':
			rval = '\\';
			break;
    	case 'B': 
  		  rval = '\b';
  		  break;
	  	case 'F':
	  		  rval = '\f';
	  		  break;
	  	case 'N' :
	  		  rval = '\n';
	  		  break;
	  	case 'R' :
	  		  rval = '\r';
	  		  break;
	  	case 'S':
	  		  rval = ' ';
	  		  break;
	  	case 'T':
	  		  rval = '\t';
	  		  break;
	  	case 'E' :
	  		  rval = '\033';
	  		  break;
	  	case '^' : //when control keys are pressed with a key
	  		charIndex++;
	  		rval = (char) (curExpr.charAt(charIndex) - '@');
	  		break;
	  	case 'X' : //when binaries are inputed
	  		charIndex++;
	  		//Hex (\XABC, \X132
	  		if (isHexDigit(curExpr.charAt(charIndex))) {
    			rval = hex2Bin(curExpr.charAt(charIndex));
    			charIndex++;
    		}
	  		if (isHexDigit(curExpr.charAt(charIndex))) {
    			rval <<= 4;
    			rval |= hex2Bin(curExpr.charAt(charIndex));
    			charIndex++;
    		}
	  		if (isHexDigit(curExpr.charAt(charIndex))) {
    			rval <<= 4;
    			rval |= hex2Bin(curExpr.charAt(charIndex));
    			charIndex++;
    		}
	  		charIndex--;
	  		break;
	  		
	  	default:
	  		//Default case
	  		if (isOctDigit(curExpr.charAt(charIndex)) == false) {
				rval = curExpr.charAt(charIndex);
			}
	  		else { //Octal
	  			charIndex++;
	  			rval = oct2Bin(curExpr.charAt(charIndex));
	  			charIndex++;
	  			if (isOctDigit(curExpr.charAt(charIndex))) {
					rval <<= 3;
					rval |= oct2Bin(curExpr.charAt(charIndex));
					charIndex++;
				}
	  			if (isOctDigit(curExpr.charAt(charIndex))) {
					rval <<= 3;
					rval |= oct2Bin(curExpr.charAt(charIndex));
					charIndex++;
				}
	  			charIndex--;
	  			
	  		}
		}
		
		charIndex++;
		return rval;
	}

	private int oct2Bin(char c) {
		return ((c) - '0') & 0x7;
	}

	private int hex2Bin(char c) {
		return (Character.isDigit(c) ? (c) - '0':
			(Character.toUpperCase(c) - 'A' + 10)) & 0xf;
	}

	private boolean isHexDigit(char c) {
		return (Character.isDigit(c) ||
				('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'));
	}

	private boolean isOctDigit(char c) {
		return ('0' <= c && c <= '7');
	}

	public Token getCurrentToken() {
		// TODO Auto-generated method stub
		return this.currentToken;
	}
	
	
}
	
	

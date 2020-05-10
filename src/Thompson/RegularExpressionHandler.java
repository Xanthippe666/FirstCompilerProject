package Thompson;

import java.util.ArrayList;

import InputSystem.Input;

public class RegularExpressionHandler {
	private Input input = null;
	private MacroHandler macroHandler = null;
	ArrayList<String> regularExprArr = new ArrayList<String>();
	private boolean inquoted = false; //??
	private int preprocessDepth = 1;
	
	public RegularExpressionHandler(Input input, MacroHandler macroHandler) throws Exception {
		this.input = input;
		this.macroHandler = macroHandler;
		
		processRegularExprs(); //Convert regular expression into an NFA?
	}
	
	public int getRegularExpressionCount() {
		return regularExprArr.size();
	}
	
	public String getRegularExpression(int index) {
		if(index < 0 || index >= getRegularExpressionCount()) {
			return null;
		}
		return regularExprArr.get(index);
	}
	
	private void processRegularExprs() throws Exception{
		while(input.ii_lookahead(1) != input.EOF) {
			preProcessExpr();
		}
	}

	
	//Converts D, E, etc. macros
	//Into the regex [0-9], [a-z] etc.
	private void preProcessExpr() throws Exception {
		//Replaces macros
		while(Character.isSpaceChar(input.ii_lookahead(1))
				|| input.ii_lookahead(1) == '\n') {
			input.ii_advance();
		}
		
		String regularExpr = "";
		char c = (char) input.ii_advance();
		while(Character.isSpaceChar(c) == false && c != '\n') {
			//SKips everything that is enclosed in "     "
			if (c == '"') {
				inquoted = !inquoted;
			}
			//Indicates the beginning of a macro
			if(!inquoted && c == '{') {
				String name = extractMacroNameFromInput();
				regularExpr += expandMacro(name);
			}
			else {
				regularExpr += c;
			}
			
			c = (char) input.ii_advance();
		}
		
		regularExprArr.add(regularExpr);
		
	}

	//An recursive process of replacing macros
	private String expandMacro(String macroName) throws Exception{
		String macroContent = macroHandler.expandMacro(macroName);
		String toReturn = "";
		//Loop through every character
		boolean inquoted = false;
		
		for(int i = 0 ; i < macroContent.length(); i++) {
			if(macroContent.charAt(i) == '"') {
				inquoted = !inquoted;
			}
			
			if(!inquoted && macroContent.charAt(i) == '{') {
				int end = macroContent.indexOf('}',i);
				if(end == -1) {
					ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
					return null;
				}
				
				preprocessDepth++;
				if(preprocessDepth > 100) {
					ErrorHandler.parseErr(ErrorHandler.Error.E_MACDEPTH);
					return null;
				}
				
				macroName = macroContent.substring(i+1,end);
				String content = "";
				content += macroContent.substring(0, i);
				content += macroHandler.expandMacro(macroName);
				content += macroContent.substring(end+1,macroContent.length());
				macroContent = content;
				i--; //To take account of 
				//the added parenthesis
				//Of inserting macros
			}
			
			else {
				toReturn += macroContent.substring(i,i+1);
			}
			
		}
		
		preprocessDepth--;
		
		return toReturn;
		/*
		String macroContent = macroHandler.expandMacro(macroName);
		String toReturn = "";
		
		int begin = 0; //macroContent.indexOf('{');
		
		//Will start to loop to check 
		//if there is another { (macro) within a { (macro)
		while (begin != -1) {
			begin = macroContent.indexOf('{');
			
			//
			
			int existsQuote = macroContent.indexOf('"');
			
			//No quotes
			if (existsQuote == -1) {
				int end = macroContent.indexOf('}',begin);
				if(end == -1) {
					ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
					return null;
				}
				
				macroName = macroContent.substring(begin+1,end);
				String content = macroContent.substring(0, begin);
				content += macroHandler.expandMacro(macroName);
				content += macroContent.substring(end+1,macroContent.length());
				macroContent = content;
			}
			//Quote exists
			else {
				int endQuote = macroContent.indexOf('"',existsQuote+1);
				if(endQuote == -1) {
					ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
					return null;
				}
				macroContent = macroContent.substring(endQuote);
			}
		
		}
		
		return macroContent;
		*/
	}

	
	private boolean checkInQuoted(String macroContent, int curlyBracesBegin 
			, int curlyBracesEnd) throws Exception {
		boolean inquoted = false;
		int quoteBegin = macroContent.indexOf('"');
		int quoteEnd = -1;
		
		while(quoteBegin != -1) {
			quoteEnd = macroContent.indexOf('"', quoteBegin + 1);
			if(quoteEnd == -1) {
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			}
			
			if(quoteBegin < curlyBracesBegin &&
					 quoteEnd > curlyBracesEnd) {
				inquoted = true;
			}
			else if(quoteBegin < curlyBracesBegin &&
					 quoteEnd < curlyBracesEnd) {
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			}
			else if(quoteBegin > curlyBracesBegin &&
					 quoteEnd < curlyBracesEnd) {
				ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			}
			
			quoteBegin = macroContent.indexOf('"', quoteEnd + 1);
			
		}
		return inquoted;
	}
	

	private String extractMacroNameFromInput() throws Exception {
		String name = "";
		char c = (char) input.ii_advance();
		while (c != '}' && c != '\n') {
			name += c;
			c = (char) input.ii_advance();
		}
		
		if(c=='}'){
			return name;
		}
		
		else {
			ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
			return null;
		}
	}
	
	
	
	
}

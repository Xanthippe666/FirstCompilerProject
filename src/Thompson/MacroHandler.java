package Thompson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import InputSystem.Input;

public class MacroHandler {
	private HashMap<String, String> macroMap = new HashMap<String,String>();
	private Input input;
	
	public MacroHandler(Input input) {
		this.input = input;
		while(input.ii_lookahead(1) != input.EOF) {
			//System.out.print(input.ii_lookahead(1));
			this.newMacro();
		}
		//System.out.println("finished");
	}
	
	//A macro is defined as a pair of values
	//Etc. A B
	//Etc. Define abc
	private void newMacro() {
		while(Character.isSpaceChar(input.ii_lookahead(1))
				|| input.ii_lookahead(1) == '\n') {
			input.ii_advance();
		}
		
		String macroName = "";
		char c = (char) input.ii_lookahead(1);
		while(Character.isSpaceChar(c) == false && c != '\n') {
			macroName += c;
			input.ii_advance();
			c = (char) input.ii_lookahead(1);
		}
		
		while(Character.isSpaceChar(input.ii_lookahead(1))){
				//|| input.ii_lookahead(1) == '\n') {
			input.ii_advance();
		}
		
		String macroContent = "";
		c = (char) input.ii_lookahead(1);
		while(Character.isSpaceChar(c) == false && c != '\n'
				&& c != 0) {
			//System.out.print(c);
			macroContent += c;
			int line = input.ii_lineno();
			input.ii_advance();
			c = (char) input.ii_lookahead(1);
			if(line < input.ii_lineno()) {
				break;
			}
			
		}
		
		//Skip '\n'
		//input.ii_advance();
		while(Character.isSpaceChar(input.ii_lookahead(1))
				|| input.ii_lookahead(1) == '\n') {
			input.ii_advance();
		}
		
		macroMap.put(macroName, macroContent);
		
		
	}
	
	
	public String expandMacro(String macroName) throws Exception {
		if(macroMap.containsKey(macroName) == false) {
			ErrorHandler.parseErr(ErrorHandler.Error.E_NOMAC);
		}
		else {
			return "(" + macroMap.get(macroName) + ")";
		}
		
		return "ERROR";
	}
	
	
	public void printMacs() {
		if(macroMap.isEmpty()) {
			System.out.println("There are no macros");
			
		}
		
		else {
			Iterator iter = macroMap.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<String, String> entry = 
						(Map.Entry<String, String>) iter.next();
				System.out.println("Macro name: " + entry.getKey()
								+ ", Macro content: " + entry.getValue());
			}
		}
	}
	
	
	
}

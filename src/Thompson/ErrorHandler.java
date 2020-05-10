package Thompson;

public class ErrorHandler {
	public enum Error{
			E_MEM, //not enough internal memory
			E_BADEXPR, //regex is wrong
			E_PAREN, //Paranthesis mismatch
			E_LENGTH, //too much regex to expand
			E_BRACKET, //Missing [ brackets for a expression set
			E_BOL, //^ must be at the start of the regex
			E_CLOSE, //*, ?, + must have an acommpanying regex
			E_NEWLINE, //
			E_BADMAC, //Missing a }
			E_NOMAC, //Missing macro
			E_MACDEPTH, //Too much depth in the macro hash-map
	}
	
	
	private static String[] errMsgs = new String[] {
			"Not enough memory for NFA",
			"Malformed regular expression",
			"Missing closed parenthesis (,)",
			"Too many regular expressions, too long",
			"Missing [ in character class",
			"^ must be the start of a regex or after [",
			"+, ?, * must follow an expression or subexpression",
			"Newline in quoted string, use \\n to get newline into expression",
			"Missing } in macro expansion",
			"Macro doesn't exist (in the hash map)",
			"Macro expansions nested too deeply"
	};
	
	public static void parseErr(Error type) throws Exception {
		throw new Exception(errMsgs[type.ordinal()]);
	}
	
}

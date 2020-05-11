package Thompson;

import InputSystem.Input;

/*
 * Regex + Macros
 * --> Preprocess Macros
 * --> Go through lexer to identify symbols/characters
 */
public class ThompsonConstruction {
	private static Input input = new Input();
	private MacroHandler macroHandler;
	RegularExpressionHandler regularexpr;
	Lexer lexer;
	NfaMachineConstructor nfaMachineConstructor;
	
	public void runMacroExample() {
		//input = new Input();
		System.out.println("Please enter macro definition ");
		//renewInputBuffer();
		renewInputBuffer(".\\src\\Thompson\\Macro.txt");
		macroHandler = new MacroHandler(input);
		macroHandler.printMacs();
	}
	
	public void runMacroExpandExample() throws Exception {
		//input = new Input();
		System.out.println("Enter regular expression ");
		//renewInputBuffer();
		renewInputBuffer(".\\src\\Thompson\\regex.txt");
		
		regularexpr =
				new RegularExpressionHandler(input, macroHandler);
		System.out.println("regular expression after expand");
		for(int i = 0; i < regularexpr.getRegularExpressionCount(); i++) {
			System.out.println(regularexpr.getRegularExpression(i));
		}
	}

	private void renewInputBuffer() {
		input.ii_newFile(null);
		input.ii_advance();
		input.ii_pushback(1);
	}
	
	private void renewInputBuffer(String filename) {
		input.ii_newFile(filename);
		input.ii_advance();
		input.ii_pushback(1);
	}
	
	
	private void runLexerExample() {
		lexer = new Lexer(regularexpr);
		int exprCount = 0;
		System.out.println("当前正则解析的正则表达式: "
				+ regularexpr.getRegularExpression(exprCount));
		//System.out.println(regularexpr.regularExprArr);
		lexer.advance();
		
		
		while (lexer.MatchToken(Lexer.Token.END_OF_INPUT) == false) {
			if(lexer.MatchToken(Lexer.Token.EOS) == true) {
				 System.out.println("解析下一个正则表达式");
				 exprCount++;
				 System.out.println("当前正则解析的正则表达式: " 
				 + regularexpr.getRegularExpression(exprCount));
				 lexer.advance();
			}
			else {
				printLexResult();
			}
		}
		
	}
	
	
	private void printLexResult() {
		while(lexer.MatchToken(Lexer.Token.EOS) == false) {
			System.out.println("当前识别字符是: " + (char)lexer.getLexeme());
			if(lexer.MatchToken(Lexer.Token.L) != true) {
				System.out.println("当前字符具有特殊含义");

    			printMetaCharMeaning(lexer);
			}
			else {
				System.out.println("当前字符是普通字符常量");
			}
			lexer.advance();
		}
	}

	private void printMetaCharMeaning(Lexer lexer) {
    	String s = "";
    	if (lexer.MatchToken(Lexer.Token.ANY)) {
    		s = "当前字符是点通配符";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    		s = "当前字符是开头匹配符";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.AT_EOL)) {
    		s = "当前字符是末尾匹配符";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_END)) {
    		s = "当前字符是字符集类结尾括号";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CCL_START)) {
    		s = "当前字符是字符集类的开始括号";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSE_CURLY)) {
    		s = "当前字符是结尾大括号";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
    		s = "当前字符是结尾圆括号";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.DASH)) {
    		s = "当前字符是横杆";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OPEN_CURLY)) {
    		s = "当前字符是起始大括号";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
    		s = "当前字符是起始圆括号";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OPTIONAL)) {
    		s = "当前字符是单字符匹配符?";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.OR)) {
    		s = "当前字符是或操作符";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
    		s = "当前字符是正闭包操作符";
    	}
    	
    	if (lexer.MatchToken(Lexer.Token.CLOSURE)) {
    		s = "当前字符是闭包操作符";
    	}
    	
    	System.out.println(s);
	}
	
	private void runNfaMachineConstructorExample() throws Exception {
		lexer = new Lexer(regularexpr);
		nfaMachineConstructor = new NfaMachineConstructor(lexer);
		NfaPair pair = new NfaPair();
		//pair.endNode = nfaMachineConstructor.nfaManager.newNfa();
		//pair.startNode = nfaMachineConstructor.nfaManager.newNfa();
		//nfaMachineConstructor.constructNfaForCharacterSet(pair);
		//nfaMachineConstructor.constructNfaForSingleCharacter(pair);
		//nfaMachineConstructor.constructNfaForDot(pair);
		//nfaMachineConstructor.constructNfaForCharacterSetWithoutNegative(pair);
		//nfaMachineConstructor.constructNfaForCharacterSet(pair);
		//nfaMachineConstructor.factor(pair);
		//nfaMachineConstructor.cat_expr(pair);
		nfaMachineConstructor.expr(pair);
		//nfaMachineConstructor.term(pair);
		//nfaMachineConstructor.constructNfaForSingleCharacter(pair);
		//nfaMachineConstructor.constructNfaForSingleCharacter(pair);
		
		//System.out.println(pair.startNode.next2);
		nfaPrinter print = new nfaPrinter();
		print.printNfa(pair.startNode);
		
		//print.printNfaNode(pair.startNode);
		
		System.out.println("nfa简单构造完毕");
	}

	public static void main(String[] args) throws Exception {
		ThompsonConstruction construction = new ThompsonConstruction();
		construction.runMacroExample();
		construction.runMacroExpandExample();
		construction.runLexerExample();
		

    	construction.runNfaMachineConstructorExample();
	}

	
}

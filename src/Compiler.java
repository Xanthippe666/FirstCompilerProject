
public class Compiler {
	public static void main(String[] args) {
		Lexer l = new Lexer();
		//Parser p = new Parser(l);
		ParserMultiline p = new ParserMultiline(l);
		//BasicParser p = new BasicParser(l);
		p.statements();
		
		//Parser?
		//Parser.statements?
		//l.runLexer();
	}
}

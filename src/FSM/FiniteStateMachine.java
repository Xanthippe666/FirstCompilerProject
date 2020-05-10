package FSM;

import InputSystem.Input;

public class FiniteStateMachine {
	private int yystate = 0; //Starting state
	private int yylastaccept = FMS.STATE_FAILURE;
	private int yyprev = FMS.STATE_FAILURE;
	private int yynstate = FMS.STATE_FAILURE;
	private boolean yyanchor = false; //Are we in accepting state?
	private byte yylook = Input.EOF;
	
	private Input input = new Input();
	private TableFMS fms = new TableFMS();
	private boolean endOfReads = false;
	
	public FiniteStateMachine() {
		input.ii_newFile(null);
		input.ii_advance();
		input.ii_pushback(1); //
		input.ii_mark_start();
	}
	
	public void yylex() {
		while(true) {
			while(true) {
				if ((yylook = input.ii_lookahead(1)) != Input.EOF) {
					yynstate = fms.yy_next(yystate, yylook);
					break;
				}
				else {
					endOfReads = true;
					if (yylastaccept != FMS.STATE_FAILURE) {
						yynstate = FMS.STATE_FAILURE;
						break;
					}
					else {
						return;
					}
				}
			}
			
			
			if (yynstate != FMS.STATE_FAILURE) {
				System.out.println("Transition from state " + yystate
						+ " to state " + yynstate + " on input char " + 
						(char) yylook);
				
				input.ii_advance();
				
				if((yyanchor = fms.isAcceptState(yynstate))) {
					yyprev = yystate;
					yylastaccept = yynstate;
					input.ii_mark_end();
				}
				
				yystate = yynstate;
			}
			else {
				if (yylastaccept == FMS.STATE_FAILURE) {
					if(yylook != '\n') {
						System.out.println("Ignoring bad input");
					}
					
					input.ii_advance();
				}
				else {
					input.ii_to_mark(); //Moves Next to the End marker
					System.out.println("Accepting state: " + yylastaccept);
					System.out.println("line: " + input.ii_lineno()
					+ " accept text: " + input.ii_text());
					
					switch(yylastaccept) {
					case 1:
						System.out.println("This is an integer");
						break;
					case 2:
					case 4:
						System.out.println("This is a floating poitn");
						break;
					default:
						System.out.println("Internal error");
					}
				}
				
				yylastaccept = FMS.STATE_FAILURE;
				yystate = 0;
				input.ii_mark_end();
				
				if(endOfReads) {
					return;
				}
			}
			
			
			
			
		}
	}
	
	
	public static void main(String[] args) {
		FiniteStateMachine fms = new FiniteStateMachine();
		fms.yylex();
	}
	
}

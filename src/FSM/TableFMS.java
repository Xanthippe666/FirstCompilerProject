package FSM;

public class TableFMS implements FMS{

	//A specific FMS
	//with six states
	//Used for parsing Floating Point Numbers
	//etc. 123123.123123e39
	//etc. 13.1392e30
	
	private final int ASCII_COUNT = 128;
	private final int STATE_COUNT = 6;
	private int[][] fmsTable = new int[STATE_COUNT][ASCII_COUNT];
	private boolean[] accept = {false, true, true, false, true, false};
	
	public TableFMS() {
		for(int i = 0; i < STATE_COUNT; i++) {
			for (int j = 0; j < ASCII_COUNT; j++) {
				fmsTable[i][j] = FMS.STATE_FAILURE;
			}
			
			//Draws in transition edges for [0-9] input
			initForNumber(0, 1); //For state 0, sets [0-9] to state 1
			initForNumber(1, 1); //For state 1, sets [0-9] to state 1
			initForNumber(2, 2); //For state 2, sets [0-9] to state 2
			initForNumber(4, 4); //For state 4, sets [0-9] to state 4
			initForNumber(5, 4); //For state 5, sets [0-9] to state 4
			initForNumber(3, 2); 
			
			//Draws in transition edges for ., e input
			fmsTable[0]['.'] = 3;
			fmsTable[1]['.'] = 2;
			fmsTable[1]['e'] = 5;
			fmsTable[2]['e'] = 5;
					
			
		}
	}

	private void initForNumber(int state, int val) {
		for (int i = 0; i < 10; i++) {
			fmsTable[state]['0' + i] = val;
		}
		
	}

	public int yy_next(int state, byte c) {
		
		if (state == FMS.STATE_FAILURE || c >= ASCII_COUNT) {
			return FMS.STATE_FAILURE;
		}
		
		return fmsTable[state][c];
	}

	public boolean isAcceptState(int state) {
		
		if(state == FMS.STATE_FAILURE) {
			return false;
		}
		
		return accept[state];
	}

}
